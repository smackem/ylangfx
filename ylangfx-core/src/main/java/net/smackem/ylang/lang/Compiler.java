package net.smackem.ylang.lang;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;

public class Compiler {

    public Program compile(String source, FunctionTable functionTable, Collection<String> outErrors) {
        if (source.endsWith("\n") == false) {
            source += "\n";
        }
        final YLangParser.ProgramContext ast = compileToAst(source, outErrors);
        if (ast == null) {
            return null;
        }
        final ModuleVisitor moduleVisitor = new ModuleVisitor();
        final ModuleDecl module = ast.accept(moduleVisitor);
        if (outErrors.addAll(moduleVisitor.semanticErrors())) {
            return null;
        }
        final EmittingVisitor emitter = new EmittingVisitor(module, functionTable);
        final Program program = ast.accept(emitter);
        if (outErrors.addAll(emitter.semanticErrors())) {
            return null;
        }
        return program;
    }

    YLangParser.ProgramContext compileToAst(String source, Collection<String> outErrors) {
        final CharStream input = CharStreams.fromString(source);
        final YLangLexer lexer = new YLangLexer(input);
        final ErrorListener errorListener = new ErrorListener();
        lexer.addErrorListener(errorListener);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final YLangParser parser = new YLangParser(tokens);
        parser.addErrorListener(errorListener);
        final YLangParser.ProgramContext ast = parser.program();
        if (outErrors.addAll(errorListener.errors)) {
            return null;
        }
        return ast;
    }

    private static class ErrorListener implements ANTLRErrorListener {
        private final Collection<String> errors = new ArrayList<>();

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object o, int line, int pos, String s, RecognitionException e) {
            this.errors.add(String.format("line %d:%d: %s", line, pos, s));
        }

        @Override
        public void reportAmbiguity(Parser parser, DFA dfa, int i, int i1, boolean b, BitSet bitSet, ATNConfigSet atnConfigSet) {
        }

        @Override
        public void reportAttemptingFullContext(Parser parser, DFA dfa, int i, int i1, BitSet bitSet, ATNConfigSet atnConfigSet) {
        }

        @Override
        public void reportContextSensitivity(Parser parser, DFA dfa, int i, int i1, int i2, ATNConfigSet atnConfigSet) {
        }
    }
}
