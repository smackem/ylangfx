package net.smackem.ylang.lang;

import net.smackem.ylang.runtime.*;
import org.antlr.v4.runtime.ParserRuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

class EmittingVisitor extends YLangBaseVisitor<Void> {

    private static final Logger log = LoggerFactory.getLogger(EmittingVisitor.class);
    private static final String endLabel = "@end";
    private final Emitter emitter = new Emitter();
    private final List<String> semanticErrors = new ArrayList<>();
    private final Map<String, Integer> globals = new HashMap<>();
    private int labelNumber = 1;

    public EmittingVisitor(Collection<String> globals) {
        int index = 0;
        for (final String ident : globals) {
            this.globals.put(ident, index);
            index++;
        }
    }

    public List<String> semanticErrors() {
        return Collections.unmodifiableList(this.semanticErrors);
    }

    @Override
    public Void visitProgram(YLangParser.ProgramContext ctx) {
        this.globals.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .forEach(entry -> this.emitter.emit(OpCode.LD_VAL, NilVal.INSTANCE));
        super.visitProgram(ctx);
        this.emitter.emit(OpCode.LABEL, endLabel);
        return null;
    }

    @Override
    public Void visitAssignStmt(YLangParser.AssignStmtContext ctx) {
        super.visitAssignStmt(ctx);
        if (ctx.atom() == null) { // assign to ident, not to lvalue-expr
            final String ident = ctx.Ident().getText();
            final Integer addr = this.globals.get(ident);
            if (addr == null) {
                logSemanticError(ctx, "unknown identifier " + ident);
            } else {
                this.emitter.emit(OpCode.ST_GLB, addr);
            }
        }
        return null;
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
        final int itemAddr = this.globals.get(ident);
        final int iteratorAddr = this.globals.get(DeclExtractingVisitor.getIteratorIdent(ident));
        ctx.expr().accept(this);                 // push iterable
        this.emitter.emit(OpCode.ITER);                 // push iterator
        this.emitter.emit(OpCode.ST_GLB, iteratorAddr); // store iterator
        final String loopLabel = nextLabel();
        final String breakLabel = nextLabel();
        this.emitter.emit(OpCode.LABEL, loopLabel);
        this.emitter.emit(OpCode.LD_GLB, iteratorAddr); // iterator.next
        this.emitter.emit(OpCode.BR_NEXT, breakLabel);
        this.emitter.emit(OpCode.ST_GLB, itemAddr);     // store item
        ctx.block().accept(this);
        this.emitter.emit(OpCode.BR, loopLabel);
        this.emitter.emit(OpCode.LABEL, breakLabel);
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
    public Void visitTuple(YLangParser.TupleContext ctx) {
        super.visitTuple(ctx);
        if (ctx.Pair() != null) {
            this.emitter.emit(OpCode.MK_POINT);
        }
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
                throw new UnsupportedOperationException();
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
        if (ctx.invocationSuffix() != null) {
            final int argCount = ctx.invocationSuffix().arguments() != null
                    ? ctx.invocationSuffix().arguments().expr().size()
                    : 0;
            ctx.invocationSuffix().accept(this);
            // method receiver is first argument -> argCount + 1
            this.emitter.emit(OpCode.INVOKE, argCount + 1, ctx.Ident().getText());
        } else {
            this.emitter.emit(OpCode.INVOKE_P, ctx.Ident().getText());
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
            final Integer addr = this.globals.get(ctx.Ident().getText());
            if (addr == null) {
                logSemanticError(ctx, "unknown identifier " + ctx.Ident());
            } else {
                this.emitter.emit(OpCode.LD_GLB, addr);
            }
        } else if (ctx.String() != null) {
            this.emitter.emit(OpCode.LD_VAL, new StringVal(ctx.number().getText()));
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
            this.emitter.emit(OpCode.MK_LIST, ctx.list().arguments().expr().size());
        } else if (ctx.map() != null) {
            throw new UnsupportedOperationException();
        } else if (ctx.expr() != null) {
            ctx.expr().accept(this);
        } else if (ctx.functionInvocation() != null) {
            ctx.functionInvocation().accept(this);
        }
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

    private static NumberVal parseNumber(String s) {
        return new NumberVal(Float.parseFloat(s));
    }

    private static RgbVal parseColor(String s) {
        final String[] tokens = s.substring(1).split(":");
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
}
