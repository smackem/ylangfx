package net.smackem.ylang.lang;

import net.smackem.ylang.runtime.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

class EmittingVisitor extends YLangBaseVisitor<Void> {

    private static final Logger log = LoggerFactory.getLogger(EmittingVisitor.class);
    private static final String endLabel = "@end";
    private final FunctionTable functionTable;
    private final Emitter emitter = new Emitter();
    private final List<String> semanticErrors = new ArrayList<>();
    private final AllocationTable allocationTable;
    private final LinkedList<Scope> scopes = new LinkedList<>();
    private int labelNumber = 1;

    public EmittingVisitor(int allocationSlots, FunctionTable functionTable) {
        this.functionTable = functionTable;
        this.allocationTable = new AllocationTable(allocationSlots);
    }

    public List<String> semanticErrors() {
        return Collections.unmodifiableList(this.semanticErrors);
    }

    @Override
    public Void visitProgram(YLangParser.ProgramContext ctx) {
        for (int i = 0; i < this.allocationTable.slots.length; i++) {
            this.emitter.emit(OpCode.LD_VAL, NilVal.INSTANCE);
        }
        pushScope();
        super.visitProgram(ctx);
        this.emitter.emit(OpCode.LABEL, endLabel);
        popScope();
        return null;
    }

    @Override
    public Void visitAssignStmt(YLangParser.AssignStmtContext ctx) {
        if (ctx.atom() == null) { // assign to ident, not to lvalue-expr
            ctx.expr().accept(this);
            final String ident = ctx.Ident().getText();
            final Integer addr = ctx.Decleq() != null
                    ? putIdent(ident)
                    : lookupIdent(ident);
            if (addr == null) {
                logSemanticError(ctx, "unknown identifier " + ident);
            } else {
                this.emitter.emit(OpCode.ST_GLB, addr);
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

    private YLangVisitor<Void> getLValueAtomVisitor(YLangParser.ExprContext rvalueExpr) {
        return new YLangBaseVisitor<>() {
            @Override
            public Void visitMemberSuffix(YLangParser.MemberSuffixContext ctx) {
                if (ctx.invocationSuffix() != null) {
                    semanticErrors.add("cannot assign to invocation");
                    return null;
                }
                emitter.emit(OpCode.LD_VAL, new StringVal(ctx.Ident().getText()));
                rvalueExpr.accept(EmittingVisitor.this);
                emitter.emit(OpCode.INVOKE, 3, functionTable.indexAssignmentFunction());
                emitter.emit(OpCode.POP); // like all functions, setAt returns a value -> discard it
                return null;
            }

            @Override
            public Void visitIndexSuffix(YLangParser.IndexSuffixContext ctx) {
                EmittingVisitor.super.visitIndexSuffix(ctx);
                rvalueExpr.accept(EmittingVisitor.this);
                emitter.emit(OpCode.INVOKE, 3, functionTable.indexAssignmentFunction());
                emitter.emit(OpCode.POP); // like all functions, setAt returns a value -> discard it
                return null;
            }
        };
    }

    @Override
    public Void visitReturnStmt(YLangParser.ReturnStmtContext ctx) {
        super.visitReturnStmt(ctx);
        this.emitter.emit(OpCode.BR, endLabel);
        return null;
    }

    @Override
    public Void visitIfStmt(YLangParser.IfStmtContext ctx) {
        final String ifLabel = nextLabel();
        final String elseLabel = ctx.elseClause() != null ? nextLabel() : null;
        visitConditionalBody(ifLabel, elseLabel, ctx.expr(), ctx.block());
        for (final var elseIf : ctx.elseIfClause()) {
            final String elseIfLabel = nextLabel();
            visitConditionalBody(elseIfLabel, elseLabel, elseIf.expr(), elseIf.block());
        }
        if (ctx.elseClause() != null) {
            ctx.elseClause().accept(this);
            this.emitter.emit(OpCode.LABEL, elseLabel);
        }
        return null;
    }

    private void visitConditionalBody(String ifLabel, String elseLabel,
                                      YLangParser.ExprContext expr,
                                      YLangParser.BlockContext block) {
        expr.accept(this);
        this.emitter.emit(OpCode.BR_ZERO, ifLabel);
        block.accept(this);
        if (elseLabel != null) {
            this.emitter.emit(OpCode.BR, elseLabel);
        }
        this.emitter.emit(OpCode.LABEL, ifLabel);
    }

    @Override
    public Void visitWhileStmt(YLangParser.WhileStmtContext ctx) {
        final String loopLabel = nextLabel();
        final String breakLabel = nextLabel();
        this.emitter.emit(OpCode.LABEL, loopLabel);
        ctx.expr().accept(this);
        this.emitter.emit(OpCode.BR_ZERO, breakLabel);
        ctx.block().accept(this);
        this.emitter.emit(OpCode.BR, loopLabel);
        this.emitter.emit(OpCode.LABEL, breakLabel);
        return null;
    }

    @Override
    public Void visitForStmt(YLangParser.ForStmtContext ctx) {
        final String ident = ctx.Ident().getText();
        pushScope();
        final int itemAddr = putIdent(ident);
        final int iteratorAddr = putIdent(DeclExtractingVisitor.getIteratorIdent(ident));
        ctx.expr().accept(this);                 // push iterable
        this.emitter.emit(OpCode.ITER);                 // push iterator
        this.emitter.emit(OpCode.ST_GLB, iteratorAddr); // store iterator
        final String loopLabel = nextLabel();
        final String breakLabel = nextLabel();
        this.emitter.emit(OpCode.LABEL, loopLabel);
        this.emitter.emit(OpCode.LD_GLB, iteratorAddr); // iterator.next
        this.emitter.emit(OpCode.BR_NEXT, breakLabel);
        this.emitter.emit(OpCode.ST_GLB, itemAddr);     // store item
        if (ctx.whereClause() != null) {
            ctx.whereClause().accept(this);
            this.emitter.emit(OpCode.BR_ZERO, loopLabel);
        }
        ctx.block().accept(this);
        this.emitter.emit(OpCode.BR, loopLabel);
        this.emitter.emit(OpCode.LABEL, breakLabel);
        popScope();
        return null;
    }

    @Override
    public Void visitInvocationStmt(YLangParser.InvocationStmtContext ctx) {
        if (ctx.Ident() != null) {
            final int argCount = ctx.invocationSuffix().arguments() != null
                    ? ctx.invocationSuffix().arguments().expr().size()
                    : 0;
            ctx.invocationSuffix().accept(this);
            this.emitter.emit(OpCode.INVOKE, argCount, ctx.Ident().getText());
        } else {
            ctx.atom().accept(this);
            for (final var atomSuffix : ctx.atomSuffix()) {
                atomSuffix.accept(this);
            }
        }
        this.emitter.emit(OpCode.POP); // discard result
        return null;
    }

    @Override
    public Void visitSwapStmt(YLangParser.SwapStmtContext ctx) {
        final Integer addr1 = lookupIdent(ctx.Ident(0).getText());
        if (addr1 == null) {
            logSemanticError(ctx, "unknown identifier " + ctx.Ident(0));
            return null;
        }
        final Integer addr2 = lookupIdent(ctx.Ident(1).getText());
        if (addr2 == null) {
            logSemanticError(ctx, "unknown identifier " + ctx.Ident(1));
            return null;
        }
        this.emitter.emit(OpCode.LD_GLB, addr1);
        this.emitter.emit(OpCode.LD_GLB, addr2);
        this.emitter.emit(OpCode.ST_GLB, addr1);
        this.emitter.emit(OpCode.ST_GLB, addr2);
        return null;
    }

    @Override
    public Void visitLogStmt(YLangParser.LogStmtContext ctx) {
        super.visitLogStmt(ctx);
        this.emitter.emit(OpCode.LOG, ctx.arguments().expr().size());
        return null;
    }

    @Override
    public Void visitBlock(YLangParser.BlockContext ctx) {
        pushScope();
        super.visitBlock(ctx);
        popScope();
        return null;
    }

    @Override
    public Void visitExpr(YLangParser.ExprContext ctx) {
        ctx.condition().accept(this);
        if (ctx.term() != null) {
            final String elseLabel = nextLabel();
            final String endLabel = nextLabel();
            emitter.emit(OpCode.BR_ZERO, elseLabel);
            ctx.term().accept(this);
            emitter.emit(OpCode.BR, endLabel);
            emitter.emit(OpCode.LABEL, elseLabel);
            ctx.expr().accept(this);
            emitter.emit(OpCode.LABEL, endLabel);
        }
        return null;
    }

    @Override
    public Void visitCondition(YLangParser.ConditionContext ctx) {
        super.visitCondition(ctx);
        if (ctx.conditionOp() != null) {
            if (ctx.conditionOp().Or() != null) {
                this.emitter.emit(OpCode.OR);
            } else if (ctx.conditionOp().And() != null) {
                this.emitter.emit(OpCode.AND);
            }
        }
        return null;
    }

    @Override
    public Void visitComparison(YLangParser.ComparisonContext ctx) {
        ctx.tuple(0).accept(this);
        final var comparator = ctx.comparator();
        if (comparator != null) {
            ctx.tuple(1).accept(this);
            if (comparator.Eq() != null) {
                this.emitter.emit(OpCode.EQ);
            } else if (comparator.Lt() != null) {
                this.emitter.emit(OpCode.LT);
            } else if (comparator.Le() != null) {
                this.emitter.emit(OpCode.LE);
            } else if (comparator.Gt() != null) {
                this.emitter.emit(OpCode.GT);
            } else if (comparator.Ge() != null) {
                this.emitter.emit(OpCode.GE);
            } else if (comparator.Ne() != null) {
                this.emitter.emit(OpCode.NEQ);
            } else if (comparator.In() != null) {
                this.emitter.emit(OpCode.IN);
            }
        }
        return null;
    }

    @Override
    public Void visitPoint(YLangParser.PointContext ctx) {
        super.visitPoint(ctx);
        this.emitter.emit(OpCode.MK_POINT);
        return null;
    }

    @Override
    public Void visitRange(YLangParser.RangeContext ctx) {
        if (ctx.term().size() == 2) {
            ctx.term(0).accept(this);
            this.emitter.emit(OpCode.LD_VAL, NumberVal.ONE);
            ctx.term(1).accept(this);
        } else {
            // visit all three terms
            super.visitRange(ctx);
        }
        this.emitter.emit(OpCode.MK_RANGE);
        return null;
    }

    @Override
    public Void visitTerm(YLangParser.TermContext ctx) {
        ctx.product(0).accept(this);
        int index = 1;
        for (final var op : ctx.termOp()) {
            ctx.product(index).accept(this);
            if (op.Plus() != null) {
                this.emitter.emit(OpCode.ADD);
            } else if (op.Minus() != null) {
                this.emitter.emit(OpCode.SUB);
            } else if (op.Cmp() != null) {
                this.emitter.emit(OpCode.CMP);
            } else if (op.Concat() != null) {
                this.emitter.emit(OpCode.CONCAT);
            }
            index++;
        }
        return null;
    }

    @Override
    public Void visitProduct(YLangParser.ProductContext ctx) {
        ctx.molecule(0).accept(this);
        int index = 1;
        for (final var op : ctx.productOp()) {
            ctx.molecule(index).accept(this);
            if (op.Times() != null) {
                this.emitter.emit(OpCode.MUL);
            } else if (op.Div() != null) {
                this.emitter.emit(OpCode.DIV);
            } else if (op.Mod() != null) {
                this.emitter.emit(OpCode.MOD);
            }
            index++;
        }
        return null;
    }

    @Override
    public Void visitMolecule(YLangParser.MoleculeContext ctx) {
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
    public Void visitIndexSuffix(YLangParser.IndexSuffixContext ctx) {
        super.visitIndexSuffix(ctx);
        this.emitter.emit(OpCode.IDX);
        return null;
    }

    @Override
    public Void visitMemberSuffix(YLangParser.MemberSuffixContext ctx) {
        final String ident = ctx.Ident().getText();
        if (ctx.invocationSuffix() != null) {
            final int argCount = ctx.invocationSuffix().arguments() != null
                    ? ctx.invocationSuffix().arguments().expr().size()
                    : 0;
            ctx.invocationSuffix().accept(this);
            // method receiver is first argument -> argCount + 1
            this.emitter.emit(OpCode.INVOKE, argCount + 1, ident);
        } else {
            if (this.functionTable.contains(ident)) {
                this.emitter.emit(OpCode.INVOKE, 1, ident);
            } else {
                this.emitter.emit(OpCode.LD_VAL, new StringVal(ident)); // emit x.property as x["property"]
                this.emitter.emit(OpCode.IDX);
            }
        }
        return null;
    }

    @Override
    public Void visitAtomPrefix(YLangParser.AtomPrefixContext ctx) {
        if (ctx.Not() != null) {
            this.emitter.emit(OpCode.NOT);
        } else if (ctx.Minus() != null) {
            this.emitter.emit(OpCode.NEG);
        } else if (ctx.At() != null) {
            throw new UnsupportedOperationException();
        }
        return null;
    }

    @Override
    public Void visitAtom(YLangParser.AtomContext ctx) {
        if (ctx.Nil() != null) {
            this.emitter.emit(OpCode.LD_VAL, NilVal.INSTANCE);
        } else if (ctx.number() != null) {
            this.emitter.emit(OpCode.LD_VAL, parseNumber(ctx.number().getText()));
        } else if (ctx.Ident() != null) {
            final Integer addr = lookupIdent(ctx.Ident().getText());
            if (addr == null) {
                logSemanticError(ctx, "unknown identifier " + ctx.Ident());
            } else {
                this.emitter.emit(OpCode.LD_GLB, addr);
            }
        } else if (ctx.String() != null) {
            emitStringLiteral(ctx.String());
        } else if (ctx.True() != null) {
            this.emitter.emit(OpCode.LD_VAL, BoolVal.TRUE);
        } else if (ctx.False() != null) {
            this.emitter.emit(OpCode.LD_VAL, BoolVal.FALSE);
        } else if (ctx.kernel() != null) {
            final List<NumberVal> numbers = ctx.kernel().number().stream()
                    .map(n -> parseNumber(n.getText()))
                    .collect(Collectors.toList());
            this.emitter.emit(OpCode.LD_VAL, new KernelVal(numbers));
        } else if (ctx.Color() != null) {
            this.emitter.emit(OpCode.LD_VAL, parseColor(ctx.Color().getText()));
        } else if (ctx.list() != null) {
            ctx.list().accept(this);
            this.emitter.emit(OpCode.MK_LIST, ctx.list().arguments() != null ? ctx.list().arguments().expr().size() : 0);
        } else if (ctx.map() != null) {
            ctx.map().accept(this);
            this.emitter.emit(OpCode.MK_MAP, ctx.map().mapEntries() != null ? ctx.map().mapEntries().mapEntry().size() : 0);
        } else if (ctx.expr() != null) {
            ctx.expr().accept(this);
        } else if (ctx.functionInvocation() != null) {
            ctx.functionInvocation().accept(this);
        } else if (ctx.EnvironmentArg() != null) {
            this.emitter.emit(OpCode.LD_ENV, ctx.EnvironmentArg().getText().substring(1));
        }
        return null;
    }

    @Override
    public Void visitMapEntry(YLangParser.MapEntryContext ctx) {
        if (ctx.Ident() != null) {
            this.emitter.emit(OpCode.LD_VAL, new StringVal(ctx.Ident().getText()));
        } else {
            emitStringLiteral(ctx.String());
        }
        ctx.expr().accept(this);
        this.emitter.emit(OpCode.MK_ENTRY);
        return null;
    }

    @Override
    public Void visitFunctionInvocation(YLangParser.FunctionInvocationContext ctx) {
        final int argCount = ctx.invocationSuffix().arguments() != null
                ? ctx.invocationSuffix().arguments().expr().size()
                : 0;
        ctx.invocationSuffix().accept(this);
        this.emitter.emit(OpCode.INVOKE, argCount, ctx.Ident().getText());
        return null;
    }

    public Program buildProgram() {
        return this.emitter.buildProgram();
    }

    private void logSemanticError(ParserRuleContext ctx, String message) {
        final String text = String.format("line %d, char %d: %s",
                ctx.start.getLine(), ctx.start.getCharPositionInLine(), message);
        log.info(text);
        this.semanticErrors.add(text);
    }

    private void emitStringLiteral(TerminalNode str) {
        final String s = str.getText();
        this.emitter.emit(OpCode.LD_VAL, new StringVal(s.substring(1, s.length() - 1)));
    }

    private static NumberVal parseNumber(String s) {
        return new NumberVal(Float.parseFloat(s));
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
        this.scopes.addFirst(new Scope());
    }

    private void popScope() {
        final Scope poppedScope = this.scopes.removeFirst();
        this.allocationTable.free(poppedScope.variableAddresses.size());
    }

    private Integer putIdent(String ident) {
        final int addr = this.allocationTable.alloc(ident);
        currentScope().variableAddresses.put(ident, addr);
        return addr;
    }

    private Integer lookupIdent(String ident) {
        for (final Scope scope : this.scopes) {
            final Integer addr = scope.variableAddresses.get(ident);
            if (addr != null) {
                return addr;
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
    }
}
