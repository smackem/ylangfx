package net.smackem.ylang.lang;

import net.smackem.ylang.runtime.*;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.*;
import java.util.stream.Collectors;

class EmittingVisitor extends BaseVisitor<Program> {

    private static final String endLabel = "@end";
    private final FunctionTable functionTable;
    private final Emitter emitter = new Emitter();
    private final LinkedList<Scope> scopes = new LinkedList<>();
    private final AllocationTable mainAllocationTable;
    private final Map<String, FunctionDef> functions = new HashMap<>();
    private final FunctionDef mainFunction;
    private AllocationTable currentAllocationTable;
    private FunctionDef currentFunction;
    private int labelNumber = 1;

    public EmittingVisitor(CodeMap codeMap, ModuleDecl module, FunctionTable functionTable) {
        super(codeMap);
        Objects.requireNonNull(module);
        this.functionTable = Objects.requireNonNull(functionTable);
        this.mainAllocationTable = new AllocationTable(module.mainBody().localCount());
        for (final FunctionDecl functionDecl : module.functions().values()) {
            this.functions.put(functionDecl.name(), new FunctionDef(functionDecl));
        }
        this.mainFunction = new FunctionDef(module.mainBody());
    }

    @Override
    public Program visitProgram(YLangParser.ProgramContext ctx) {
        this.currentAllocationTable = this.mainAllocationTable;
        this.currentFunction = this.mainFunction;
        for (int i = 0; i < this.currentAllocationTable.slots.length; i++) {
            this.emitter.emit(ctx, OpCode.LD_VAL, NilVal.INSTANCE);
        }
        pushScope();
        super.visitProgram(ctx);
        if (endsWithReturnStmt(ctx) == false) {
            logSemanticError(ctx, "a program must end with a return statement");
            return null;
        }
        this.emitter.emit(ctx, OpCode.LABEL, endLabel);
        popScope();
        return this.emitter.buildProgram();
    }

    private boolean endsWithReturnStmt(YLangParser.ProgramContext ctx) {
        final var stmts = ctx.topLevelStmt();
        if (stmts.isEmpty()) {
            return false;
        }
        final var lastStmt = stmts.get(stmts.size() - 1);
        return lastStmt.statement() != null && lastStmt.statement().returnStmt() != null;
    }

    @Override
    public Program visitOptionStmt(YLangParser.OptionStmtContext ctx) {
        final Value value = parseLiteral(ctx.literal());
        this.emitter.emit(ctx, OpCode.SET_OPT, 0, ctx.Ident().getText(), value);
        return null;
    }

    @Override
    public Program visitFunctionDecl(YLangParser.FunctionDeclContext ctx) {
        final String ident = ctx.Ident().getText();
        final String endLabel = ident + "@end";
        this.currentFunction = this.functions.get(ident);
        this.emitter.emit(ctx, OpCode.BR, endLabel); // hop over function
        // function addresses CALL instructions are fixed up in emitter.buildProgram
        this.emitter.emit(ctx, OpCode.LABEL, ident);
        final int localCount = this.currentFunction.decl.localCount();
        this.currentAllocationTable = new AllocationTable(localCount + this.currentFunction.decl.parameterCount());
        for (int i = 0; i < localCount; i++) {
            this.emitter.emit(ctx, OpCode.LD_VAL, NilVal.INSTANCE);
        }
        pushScope();
        if (ctx.parameters() != null) {
            for (final var parameterIdent : ctx.parameters().Ident()) {
                putIdent(parameterIdent.getText());
            }
        }
        super.visitFunctionDecl(ctx);
        popScope();
        if (ctx.block().statement(ctx.block().statement().size() - 1).returnStmt() == null) {
            // return nil if no return statement present
            this.emitter.emit(ctx, OpCode.LD_VAL, NilVal.INSTANCE);
            this.emitter.emit(ctx, OpCode.RET);
        }
        this.currentFunction = this.mainFunction;
        this.currentAllocationTable = this.mainAllocationTable;
        this.emitter.emit(ctx, OpCode.LABEL, endLabel);
        return null;
    }

    @Override
    public Program visitDeclStmt(YLangParser.DeclStmtContext ctx) {
        final String ident = ctx.Ident().getText();
        ctx.expr().accept(this);
        final Address addr = putIdent(ident);
        addr.emitStore(this.emitter);
        return null;
    }

    @Override
    public Program visitAssignStmt(YLangParser.AssignStmtContext ctx) {
        if (ctx.atom() == null) { // assign to ident, not to lvalue-expr
            ctx.expr().accept(this);
            final String ident = ctx.Ident().getText();
            final Address addr = lookupIdent(ident);
            if (addr == null) {
                logSemanticError(ctx, "unknown identifier " + ident);
            } else {
                addr.emitStore(this.emitter);
            }
        } else {
            int index = 0;
            ctx.atom().accept(this);
            for (final var atomSuffix : ctx.atomSuffix()) {
                atomSuffix.accept(index == ctx.atomSuffix().size() - 1
                        ? getLValueAtomVisitor(ctx.expr())
                        : this);
                index++;
            }
        }
        return null;
    }

    private YLangVisitor<Program> getLValueAtomVisitor(YLangParser.ExprContext rvalueExpr) {
        return new YLangBaseVisitor<>() {
            @Override
            public Program visitMemberSuffix(YLangParser.MemberSuffixContext ctx) {
                if (ctx.invocationSuffix() != null) {
                    logSemanticError(ctx, "cannot assign to invocation");
                    return null;
                }
                emitter.emit(ctx, OpCode.LD_VAL, new StringVal(ctx.Ident().getText()));
                rvalueExpr.accept(EmittingVisitor.this);
                emitter.emit(ctx, OpCode.INVOKE, 3, functionTable.indexAssignmentFunction());
                emitter.emit(ctx, OpCode.POP); // like all functions, setAt returns a value -> discard it
                return null;
            }

            @Override
            public Program visitIndexSuffix(YLangParser.IndexSuffixContext ctx) {
                EmittingVisitor.super.visitIndexSuffix(ctx);
                rvalueExpr.accept(EmittingVisitor.this);
                emitter.emit(ctx, OpCode.INVOKE, 3, functionTable.indexAssignmentFunction());
                emitter.emit(ctx, OpCode.POP); // like all functions, setAt returns a value -> discard it
                return null;
            }
        };
    }

    @Override
    public Program visitReturnStmt(YLangParser.ReturnStmtContext ctx) {
        super.visitReturnStmt(ctx);
        if (this.currentFunction.decl.isMain()) {
            this.emitter.emit(ctx, OpCode.BR, endLabel);
        } else {
            this.emitter.emit(ctx, OpCode.RET);
        }
        return null;
    }

    @Override
    public Program visitIfStmt(YLangParser.IfStmtContext ctx) {
        final String ifLabel = nextLabel();
        final String elseLabel = ctx.elseClause() != null ? nextLabel() : null;
        visitConditionalBody(ifLabel, elseLabel, ctx.expr(), ctx.block());
        for (final var elseIf : ctx.elseIfClause()) {
            final String elseIfLabel = nextLabel();
            visitConditionalBody(elseIfLabel, elseLabel, elseIf.expr(), elseIf.block());
        }
        if (ctx.elseClause() != null) {
            ctx.elseClause().accept(this);
            this.emitter.emit(ctx, OpCode.LABEL, elseLabel);
        }
        return null;
    }

    private void visitConditionalBody(String ifLabel, String elseLabel,
                                      YLangParser.ExprContext expr,
                                      YLangParser.BlockContext block) {
        expr.accept(this);
        this.emitter.emit(expr, OpCode.BR_ZERO, ifLabel);
        block.accept(this);
        if (elseLabel != null) {
            this.emitter.emit(expr, OpCode.BR, elseLabel);
        }
        this.emitter.emit(expr, OpCode.LABEL, ifLabel);
    }

    @Override
    public Program visitWhileStmt(YLangParser.WhileStmtContext ctx) {
        final String loopLabel = nextLabel();
        final String breakLabel = nextLabel();
        this.emitter.emit(ctx, OpCode.LABEL, loopLabel);
        ctx.expr().accept(this);
        this.emitter.emit(ctx, OpCode.BR_ZERO, breakLabel);
        ctx.block().accept(this);
        this.emitter.emit(ctx, OpCode.BR, loopLabel);
        this.emitter.emit(ctx, OpCode.LABEL, breakLabel);
        return null;
    }

    @Override
    public Program visitForStmt(YLangParser.ForStmtContext ctx) {
        final String ident = ctx.Ident().getText();
        pushScope();
        final Address itemAddr = putIdent(ident);
        final Address iteratorAddr = putIdent(AllocVisitor.getIteratorIdent(ident));
        ctx.expr().accept(this);                 // push iterable
        this.emitter.emit(ctx, OpCode.ITER);                 // push iterator
        iteratorAddr.emitStore(this.emitter);           // store iterator
        final String loopLabel = nextLabel();
        final String breakLabel = nextLabel();
        this.emitter.emit(ctx, OpCode.LABEL, loopLabel);
        iteratorAddr.emitLoad(this.emitter);            // iterator.next
        this.emitter.emit(ctx, OpCode.BR_NEXT, breakLabel);
        itemAddr.emitStore(this.emitter);               // store item
        if (ctx.whereClause() != null) {
            ctx.whereClause().accept(this);
            this.emitter.emit(ctx, OpCode.BR_ZERO, loopLabel);
        }
        ctx.block().accept(this);
        this.emitter.emit(ctx, OpCode.BR, loopLabel);
        this.emitter.emit(ctx, OpCode.LABEL, breakLabel);
        popScope();
        return null;
    }

    @Override
    public Program visitInvocationStmt(YLangParser.InvocationStmtContext ctx) {
        if (ctx.Ident() != null) {
            emitInvocation(ctx.Ident().getText(), ctx.invocationSuffix());
        } else {
            ctx.atom().accept(this);
            for (final var atomSuffix : ctx.atomSuffix()) {
                atomSuffix.accept(this);
            }
        }
        this.emitter.emit(ctx, OpCode.POP); // discard result
        return null;
    }

    private void emitInvocation(String ident, YLangParser.InvocationSuffixContext ctx) {
        final int argCount = ctx.arguments() != null
                ? ctx.arguments().expr().size()
                : 0;
        ctx.accept(this);
        final FunctionDef function = this.functions.get(ident);
        if (function != null) {
            // user-defined function
            if (function.decl.parameterCount() != argCount) {
                logSemanticError(ctx, "function %s expects %d arguments, but only %d passed".formatted(
                        ident, function.decl.parameterCount(), argCount));
            }
            this.emitter.emit(ctx, OpCode.CALL, 0, ident, new NumberVal(argCount));
            return;
        }
        // built-in function
        if (this.functionTable.contains(ident) == false) {
            logSemanticError(ctx, "no function with name '" + ident + "' defined!");
            return;
        }
        this.emitter.emit(ctx, OpCode.INVOKE, argCount, ident);
    }

    @Override
    public Program visitSwapStmt(YLangParser.SwapStmtContext ctx) {
        final Address addr1 = lookupIdent(ctx.Ident(0).getText());
        if (addr1 == null) {
            logSemanticError(ctx, "unknown identifier " + ctx.Ident(0));
            return null;
        }
        final Address addr2 = lookupIdent(ctx.Ident(1).getText());
        if (addr2 == null) {
            logSemanticError(ctx, "unknown identifier " + ctx.Ident(1));
            return null;
        }
        addr1.emitLoad(this.emitter);
        addr2.emitLoad(this.emitter);
        addr1.emitStore(this.emitter);
        addr2.emitStore(this.emitter);
        return null;
    }

    @Override
    public Program visitLogStmt(YLangParser.LogStmtContext ctx) {
        super.visitLogStmt(ctx);
        this.emitter.emit(ctx, OpCode.LOG, ctx.arguments().expr().size());
        return null;
    }

    @Override
    public Program visitBlock(YLangParser.BlockContext ctx) {
        pushScope();
        super.visitBlock(ctx);
        popScope();
        return null;
    }

    @Override
    public Program visitExpr(YLangParser.ExprContext ctx) {
        ctx.condition().accept(this);
        if (ctx.term() != null) {
            final String elseLabel = nextLabel();
            final String endLabel = nextLabel();
            emitter.emit(ctx, OpCode.BR_ZERO, elseLabel);
            ctx.term().accept(this);
            emitter.emit(ctx, OpCode.BR, endLabel);
            emitter.emit(ctx, OpCode.LABEL, elseLabel);
            ctx.expr().accept(this);
            emitter.emit(ctx, OpCode.LABEL, endLabel);
        }
        return null;
    }

    @Override
    public Program visitCondition(YLangParser.ConditionContext ctx) {
        super.visitCondition(ctx);
        if (ctx.conditionOp() != null) {
            if (ctx.conditionOp().Or() != null) {
                this.emitter.emit(ctx, OpCode.OR);
            } else if (ctx.conditionOp().And() != null) {
                this.emitter.emit(ctx, OpCode.AND);
            }
        }
        return null;
    }

    @Override
    public Program visitComparison(YLangParser.ComparisonContext ctx) {
        ctx.tuple(0).accept(this);
        final var comparator = ctx.comparator();
        if (comparator != null) {
            ctx.tuple(1).accept(this);
            if (comparator.Eq() != null) {
                this.emitter.emit(ctx, OpCode.EQ);
            } else if (comparator.Lt() != null) {
                this.emitter.emit(ctx, OpCode.LT);
            } else if (comparator.Le() != null) {
                this.emitter.emit(ctx, OpCode.LE);
            } else if (comparator.Gt() != null) {
                this.emitter.emit(ctx, OpCode.GT);
            } else if (comparator.Ge() != null) {
                this.emitter.emit(ctx, OpCode.GE);
            } else if (comparator.Ne() != null) {
                this.emitter.emit(ctx, OpCode.NEQ);
            } else if (comparator.In() != null) {
                this.emitter.emit(ctx, OpCode.IN);
            }
        }
        return null;
    }

    @Override
    public Program visitPoint(YLangParser.PointContext ctx) {
        super.visitPoint(ctx);
        this.emitter.emit(ctx, OpCode.MK_POINT);
        return null;
    }

    @Override
    public Program visitRange(YLangParser.RangeContext ctx) {
        if (ctx.term().size() == 2) {
            ctx.term(0).accept(this);
            this.emitter.emit(ctx, OpCode.LD_VAL, NumberVal.ONE);
            ctx.term(1).accept(this);
        } else {
            // visit all three terms
            super.visitRange(ctx);
        }
        this.emitter.emit(ctx, OpCode.MK_RANGE);
        return null;
    }

    @Override
    public Program visitTerm(YLangParser.TermContext ctx) {
        ctx.product(0).accept(this);
        int index = 1;
        for (final var op : ctx.termOp()) {
            ctx.product(index).accept(this);
            if (op.Plus() != null) {
                this.emitter.emit(ctx, OpCode.ADD);
            } else if (op.Minus() != null) {
                this.emitter.emit(ctx, OpCode.SUB);
            } else if (op.Cmp() != null) {
                this.emitter.emit(ctx, OpCode.CMP);
            } else if (op.Concat() != null) {
                this.emitter.emit(ctx, OpCode.CONCAT);
            }
            index++;
        }
        return null;
    }

    @Override
    public Program visitProduct(YLangParser.ProductContext ctx) {
        ctx.molecule(0).accept(this);
        int index = 1;
        for (final var op : ctx.productOp()) {
            ctx.molecule(index).accept(this);
            if (op.Times() != null) {
                this.emitter.emit(ctx, OpCode.MUL);
            } else if (op.Div() != null) {
                this.emitter.emit(ctx, OpCode.DIV);
            } else if (op.Mod() != null) {
                this.emitter.emit(ctx, OpCode.MOD);
            }
            index++;
        }
        return null;
    }

    @Override
    public Program visitMolecule(YLangParser.MoleculeContext ctx) {
        ctx.atom().accept(this);
        for (final var atomSuffix : ctx.atomSuffix()) {
            atomSuffix.accept(this);
        }
        if (ctx.atomPrefix() != null) {
            ctx.atomPrefix().accept(this);
        }
        return null;
    }

    @Override
    public Program visitIndexSuffix(YLangParser.IndexSuffixContext ctx) {
        super.visitIndexSuffix(ctx);
        this.emitter.emit(ctx, OpCode.IDX);
        return null;
    }

    @Override
    public Program visitMemberSuffix(YLangParser.MemberSuffixContext ctx) {
        final String ident = ctx.Ident() != null ? ctx.Ident().getText() : null;
        if (ctx.invocationSuffix() != null) {
            final int argCount = ctx.invocationSuffix().arguments() != null
                    ? ctx.invocationSuffix().arguments().expr().size()
                    : 0;
            ctx.invocationSuffix().accept(this);
            if (ident == null) {
                // invoke function object
                this.emitter.emit(ctx, OpCode.CALL_FUNC, argCount);
                return null;
            }
            // method receiver is first argument -> argCount + 1
            this.emitter.emit(ctx, OpCode.INVOKE, argCount + 1, ident);
        } else {
            if (ident == null) {
                // invoke function object
                this.emitter.emit(ctx, OpCode.CALL_FUNC, 0);
                return null;
            }
            if (this.functionTable.contains(ident)) {
                this.emitter.emit(ctx, OpCode.INVOKE, 1, ident);
                return null;
            }
            this.emitter.emit(ctx, OpCode.LD_VAL, new StringVal(ident)); // emit x.property as x["property"]
            this.emitter.emit(ctx, OpCode.IDX);
        }
        return null;
    }

    @Override
    public Program visitAtomPrefix(YLangParser.AtomPrefixContext ctx) {
        if (ctx.Not() != null) {
            this.emitter.emit(ctx, OpCode.NOT);
        } else if (ctx.Minus() != null) {
            this.emitter.emit(ctx, OpCode.NEG);
        }
        return null;
    }

    @Override
    public Program visitAtom(YLangParser.AtomContext ctx) {
        if (ctx.literal() != null) {
            final Value value = parseLiteral(ctx.literal());
            if (value == null) {
                logSemanticError(ctx, "unknown literal");
            }
            this.emitter.emit(ctx, OpCode.LD_VAL, value);
        } else if (ctx.Ident() != null) {
            final Address addr = lookupIdent(ctx.Ident().getText());
            if (addr == null) {
                logSemanticError(ctx, "unknown identifier " + ctx.Ident());
            } else {
                addr.emitLoad(this.emitter);
            }
        } else if (ctx.list() != null) {
            ctx.list().accept(this);
            this.emitter.emit(ctx, OpCode.MK_LIST, ctx.list().arguments() != null ? ctx.list().arguments().expr().size() : 0);
        } else if (ctx.map() != null) {
            ctx.map().accept(this);
            this.emitter.emit(ctx, OpCode.MK_MAP, ctx.map().mapEntries() != null ? ctx.map().mapEntries().mapEntry().size() : 0);
        } else if (ctx.expr() != null) {
            ctx.expr().accept(this);
        } else if (ctx.functionInvocation() != null) {
            ctx.functionInvocation().accept(this);
        } else if (ctx.EnvironmentArg() != null) {
            this.emitter.emit(ctx, OpCode.LD_ENV, ctx.EnvironmentArg().getText().substring(1));
        } else if (ctx.functionRef() != null) {
            ctx.functionRef().accept(this);
        }
        return null;
    }

    private static Value parseLiteral(YLangParser.LiteralContext ctx) {
        if (ctx.Nil() != null) {
            return NilVal.INSTANCE;
        }
        if (ctx.number() != null) {
            return parseNumber(ctx.number().getText());
        }
        if (ctx.String() != null) {
            return getStringLiteralValue(ctx.String());
        }
        if (ctx.True() != null) {
            return BoolVal.TRUE;
        }
        if (ctx.False() != null) {
            return BoolVal.FALSE;
        }
        if (ctx.kernel() != null) {
            final List<NumberVal> numbers = ctx.kernel().number().stream()
                    .map(n -> parseNumber(n.getText()))
                    .collect(Collectors.toList());
            return new KernelVal(numbers);
        }
        if (ctx.Color() != null) {
            return parseColor(ctx.Color().getText());
        }
        return null;
    }

    @Override
    public Program visitMapEntry(YLangParser.MapEntryContext ctx) {
        if (ctx.Ident() != null) {
            this.emitter.emit(ctx, OpCode.LD_VAL, new StringVal(ctx.Ident().getText()));
        } else {
            emitStringLiteral(ctx.String());
        }
        ctx.expr().accept(this);
        this.emitter.emit(ctx, OpCode.MK_ENTRY);
        return null;
    }

    @Override
    public Program visitFunctionInvocation(YLangParser.FunctionInvocationContext ctx) {
        emitInvocation(ctx.Ident().getText(), ctx.invocationSuffix());
        return null;
    }

    @Override
    public Program visitFunctionRef(YLangParser.FunctionRefContext ctx) {
        final String ident = ctx.Ident().getText();
        final FunctionDef function = this.functions.get(ident);
        if (function == null) {
            logSemanticError(ctx, "unknown function '" + ident + "'");
            return null;
        }
        this.emitter.emit(ctx, OpCode.LD_FUNC, 0, ident, new NumberVal(function.decl.parameterCount()));
        return null;
    }

    private void emitStringLiteral(TerminalNode str) {
        this.emitter.emit(null, OpCode.LD_VAL, getStringLiteralValue(str));
    }

    private static Value getStringLiteralValue(TerminalNode str) {
        final String s = str.getText();
        return new StringVal(s.substring(1, s.length() - 1));
    }

    private static NumberVal parseNumber(String s) {
        return new NumberVal(Float.parseFloat(s.replace("_", "")));
    }

    private static RgbVal parseColor(String s) {
        final String[] tokens = s.substring(1).split("@");
        final int rgb = Integer.parseInt(tokens[0], 16);
        final int alpha = tokens.length > 1
                ? Integer.parseInt(tokens[1], 16)
                : 255;
        return new RgbVal(rgb >> 16 & 0xff, rgb >> 8 & 0xff, rgb & 0xff, alpha);
    }

    private String nextLabel() {
        final String label = "@lbl" + this.labelNumber;
        this.labelNumber++;
        return label;
    }

    private Scope currentScope() {
        return this.scopes.peek();
    }

    private void pushScope() {
        final Scope scope = new Scope(this.scopes.isEmpty());
        this.scopes.addFirst(scope);
    }

    private void popScope() {
        final Scope poppedScope = this.scopes.removeFirst();
        this.currentAllocationTable.free(poppedScope.variableAddresses.size());
    }

    private Address putIdent(String ident) {
        final int offset = this.currentAllocationTable.alloc(ident);
        currentScope().variableAddresses.put(ident, offset);
        return new Address(offset, currentScope().global);
    }

    private Address lookupIdent(String ident) {
        for (final Scope scope : this.scopes) {
            final Integer addr = scope.variableAddresses.get(ident);
            if (addr != null) {
                return new Address(addr, scope.global);
            }
        }
        return null;
    }

    private static class AllocationTable {
        final String[] slots;
        int tail = 0;

        AllocationTable(int slots) {
            this.slots = new String[slots];
        }

        int alloc(String ident) {
            if (this.tail >= this.slots.length) {
                throw new IllegalStateException("cannot allocate: allocation table is full");
            }
            this.slots[this.tail] = ident;
            return this.tail++;
        }

        void free(int slots) {
            if (this.tail <= 0 && slots > 0) {
                throw new IllegalStateException("cannot free: allocation table is empty");
            }
            this.tail -= slots;
        }
    }

    private static class Scope {
        final Map<String, Integer> variableAddresses = new HashMap<>();
        final boolean global;

        Scope(boolean global) {
            this.global = global;
        }
    }

    private static class FunctionDef {
        final FunctionDecl decl;

        FunctionDef(FunctionDecl decl) {
            this.decl = decl;
        }
    }

    private static class Address {
        final int offset;
        final boolean global;

        Address(int offset, boolean global) {
            this.offset = offset;
            this.global = global;
        }

        void emitLoad(Emitter emitter) {
            emitter.emit(null, this.global ? OpCode.LD_GLB : OpCode.LD_LOC, this.offset);
        }

        void emitStore(Emitter emitter) {
            emitter.emit(null, this.global ? OpCode.ST_GLB : OpCode.ST_LOC, this.offset);
        }
    }
}
