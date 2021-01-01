package net.smackem.ylang.lang;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Objects;

public class Compiler {
    private final static Logger log = LoggerFactory.getLogger(Compiler.class);

    public Program compile(String source, FunctionTable functionTable, FileProvider fileProvider, Collection<String> outErrors) {
        final Preprocessor preprocessor = new Preprocessor(source, Objects.requireNonNull(fileProvider));
        final CodeMap codeMap;
        try {
            codeMap = preprocessor.preprocess();
        } catch (IOException e) {
            outErrors.add("%s: %s".formatted(e.getClass().getName(), e.getMessage()));
            return null;
        }
        return internalCompile(codeMap, functionTable, outErrors);
    }

    public ModuleDecl extractDeclarations(String source, Collection<String> outErrors) {
        final CodeMap codeMap = Preprocessor.stripDirectives(source);
        return internalCompileToModule(codeMap, outErrors, null);
    }

    @SuppressWarnings("SameParameterValue")
    Program compileWithoutPreprocessing(String source, FunctionTable functionTable, Collection<String> outErrors) {
        return internalCompile(CodeMap.oneToOne(source), functionTable, outErrors);
    }

    private ModuleDecl internalCompileToModule(CodeMap codeMap, Collection<String> outErrors, YLangParser.ProgramContext[] outAst) {
        final YLangParser.ProgramContext ast = compileToAst(codeMap, outErrors);
        if (ast == null) {
            return null;
        }
        final ModuleVisitor moduleVisitor = new ModuleVisitor(codeMap);
        final ModuleDecl module = ast.accept(moduleVisitor);
        if (outErrors.addAll(moduleVisitor.semanticErrors())) {
            return null;
        }
        if (outAst != null) {
            outAst[0] = ast;
        }
        return module;
    }

    private Program internalCompile(CodeMap codeMap, FunctionTable functionTable, Collection<String> outErrors) {
        YLangParser.ProgramContext[] ast = new YLangParser.ProgramContext[1];
        final ModuleDecl module = internalCompileToModule(codeMap, outErrors, ast);
        if (outErrors.isEmpty() == false) {
            return null;
        }
        final EmittingVisitor emitter = new EmittingVisitor(codeMap, module, functionTable);
        final Program program = ast[0].accept(emitter);
        if (outErrors.addAll(emitter.semanticErrors())) {
            return null;
        } else {
            log.info("program compiled to {} instructions", program.instructions().size());
        }
        return program;
    }

    YLangParser.ProgramContext compileToAst(CodeMap codeMap, Collection<String> outErrors) {
        String source = codeMap.source();
        if (source.endsWith("\n") == false) {
            source += "\n";
        }
        final CharStream input = CharStreams.fromString(source);
        final YLangLexer lexer = new YLangLexer(input);
        final ErrorListener errorListener = new ErrorListener(codeMap);
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
        private final CodeMap codeMap;

        ErrorListener(CodeMap codeMap) {
            this.codeMap = codeMap;
        }

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object o, int line, int pos, String s, RecognitionException e) {
            final CodeMap.Location loc = this.codeMap.translate(line);
            this.errors.add(String.format("file %s line %d:%d: %s", loc.fileName(), loc.lineNumber(), pos, s));
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
