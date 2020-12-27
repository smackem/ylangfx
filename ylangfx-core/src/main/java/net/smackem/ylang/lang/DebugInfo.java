package net.smackem.ylang.lang;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.Objects;

public class DebugInfo {
    private final CodeMap.Location location;
    private final int charPos;
    private final String tokenText;

    private DebugInfo(CodeMap.Location location, int charPos, String tokenText) {
        this.location = location;
        this.charPos = charPos;
        this.tokenText = tokenText;
    }

    public static DebugInfo fromRuleContext(ParserRuleContext ctx, CodeMap codeMap) {
        if (ctx == null) {
            return null;
        }
        final Token token = ctx.getStart();
        return new DebugInfo(codeMap.translate(token.getLine()), token.getCharPositionInLine(), token.getText());
    }

    @Override
    public String toString() {
        return "@ %s %d:%d (near token '%s')".formatted(
                this.location.fileName(),
                this.location.lineNumber(),
                this.charPos,
                this.tokenText);
    }
}
