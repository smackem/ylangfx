package net.smackem.ylang.lang;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.Objects;

public class DebugInfo {
    private final ParserRuleContext ctx;

    private DebugInfo(ParserRuleContext ctx) {
        this.ctx = ctx;
    }

    public static DebugInfo fromRuleContext(ParserRuleContext ctx) {
        return ctx != null ? new DebugInfo(Objects.requireNonNull(ctx)) : null;
    }

    public int lineNumber() {
        return this.ctx.start.getLine();
    }

    public int charPosition() {
        return this.ctx.start.getCharPositionInLine();
    }

    @Override
    public String toString() {
        final Token token = this.ctx.getStart();
        return "@ line %d:%d (near token '%s')".formatted(token.getLine(), token.getCharPositionInLine(), token.getText());
    }
}
