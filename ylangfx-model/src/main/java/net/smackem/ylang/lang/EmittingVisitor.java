package net.smackem.ylang.lang;

import net.smackem.ylang.runtime.BoolVal;
import net.smackem.ylang.runtime.NilVal;
import net.smackem.ylang.runtime.NumberVal;
import net.smackem.ylang.runtime.StringVal;
import org.antlr.v4.runtime.ParserRuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

class EmittingVisitor extends YLangBaseVisitor<Void> {

    private static final Logger log = LoggerFactory.getLogger(EmittingVisitor.class);
    private final List<Instruction> instructions = new ArrayList<>();
    private final List<String> semanticErrors = new ArrayList<>();
    private final Map<String, Integer> globals = new HashMap<>();

    public EmittingVisitor(List<String> globals) {
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
                .forEach(entry -> this.instructions.add(new Instruction(OpCode.LD_VAL, NilVal.INSTANCE)));
        return super.visitProgram(ctx);
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
                this.instructions.add(new Instruction(OpCode.ST_GLB, addr));
            }
        }
        return null;
    }

    @Override
    public Void visitReturnStmt(YLangParser.ReturnStmtContext ctx) {
        super.visitReturnStmt(ctx);
        // TODO: replace with branch to end
        this.instructions.add(new Instruction(OpCode.EXIT));
        return null;
    }

    @Override
    public Void visitCondition(YLangParser.ConditionContext ctx) {
        super.visitCondition(ctx);
        if (ctx.conditionOp() != null) {
            if (ctx.conditionOp().Or() != null) {
                this.instructions.add(new Instruction(OpCode.OR));
            } else if (ctx.conditionOp().And() != null) {
                this.instructions.add(new Instruction(OpCode.AND));
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
                this.instructions.add(new Instruction(OpCode.EQ));
            } else if (comparator.Lt() != null) {
                this.instructions.add(new Instruction(OpCode.LT));
            } else if (comparator.Le() != null) {
                this.instructions.add(new Instruction(OpCode.LE));
            } else if (comparator.Gt() != null) {
                this.instructions.add(new Instruction(OpCode.GT));
            } else if (comparator.Ge() != null) {
                this.instructions.add(new Instruction(OpCode.GE));
            } else if (comparator.Ne() != null) {
                this.instructions.add(new Instruction(OpCode.NEQ));
            } else if (comparator.In() != null) {
                this.instructions.add(new Instruction(OpCode.IN));
            }
        }
        return null;
    }

    @Override
    public Void visitTuple(YLangParser.TupleContext ctx) {
        super.visitTuple(ctx);
        if (ctx.Pair() != null) {
            this.instructions.add(new Instruction(OpCode.MK_POINT));
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
                this.instructions.add(new Instruction(OpCode.ADD));
            } else if (op.Minus() != null) {
                this.instructions.add(new Instruction(OpCode.SUB));
            } else if (op.Cmp() != null) {
                this.instructions.add(new Instruction(OpCode.CMP));
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
                this.instructions.add(new Instruction(OpCode.MUL));
            } else if (op.Div() != null) {
                this.instructions.add(new Instruction(OpCode.DIV));
            } else if (op.Mod() != null) {
                this.instructions.add(new Instruction(OpCode.MOD));
            }
            index++;
        }
        return null;
    }

    @Override
    public Void visitMolecule(YLangParser.MoleculeContext ctx) {
        ctx.atom().accept(this);
        if (ctx.atomSuffix().isEmpty() == false) {
            throw new UnsupportedOperationException();
        }
        if (ctx.atomPrefix() != null) {
            ctx.atomPrefix().accept(this);
        }
        return null;
    }

    @Override
    public Void visitAtomPrefix(YLangParser.AtomPrefixContext ctx) {
        if (ctx.Not() != null) {
            this.instructions.add(new Instruction(OpCode.MOD));
        } else if (ctx.Minus() != null) {
            this.instructions.add(new Instruction(OpCode.NEG));
        } else if (ctx.At() != null) {
            throw new UnsupportedOperationException();
        }
        return null;
    }

    @Override
    public Void visitAtom(YLangParser.AtomContext ctx) {
        if (ctx.Nil() != null) {
            this.instructions.add(new Instruction(OpCode.LD_VAL, NilVal.INSTANCE));
        } else if (ctx.number() != null) {
            this.instructions.add(new Instruction(OpCode.LD_VAL, new NumberVal(Float.parseFloat(ctx.number().getText()))));
        } else if (ctx.String() != null) {
            this.instructions.add(new Instruction(OpCode.LD_VAL, new StringVal(ctx.number().getText())));
        } else if (ctx.Ident() != null) {
            final Integer addr = this.globals.get(ctx.Ident().getText());
            if (addr == null) {
                logSemanticError(ctx, "unknown identifier " + ctx.Ident());
            } else {
                this.instructions.add(new Instruction(OpCode.LD_GLB, addr));
            }
        } else if (ctx.True() != null) {
            this.instructions.add(new Instruction(OpCode.LD_VAL, BoolVal.TRUE));
        } else if (ctx.False() != null) {
            this.instructions.add(new Instruction(OpCode.LD_VAL, BoolVal.FALSE));
        } else if (ctx.expr() != null) {
            ctx.expr().accept(this);
        }
        return null;
    }

    public Program buildProgram() {
        return new Program(this.instructions);
    }

    private void logSemanticError(ParserRuleContext ctx, String message) {
        final String text = String.format("line %d, char %d: %s",
                ctx.start.getLine(), ctx.start.getCharPositionInLine(), message);
        log.info(text);
        this.semanticErrors.add(text);
    }
}
