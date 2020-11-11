package net.smackem.ylang.lang;

import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Objects;

class DebugInfo {
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
}
