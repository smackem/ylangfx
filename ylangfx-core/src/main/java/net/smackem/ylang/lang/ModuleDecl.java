package net.smackem.ylang.lang;

import java.util.*;

public final class ModuleDecl extends Declaration {
    private final Map<String, FunctionDecl> functions = new HashMap<>();
    private final FunctionDecl mainBody;
    private final Map<String, GlobalDecl> globals = new HashMap<>();

    ModuleDecl(String ident, FunctionDecl mainBody, Collection<FunctionDecl> functions, Collection<GlobalDecl> globals) {
        super(ident, "", 0);
        this.mainBody = Objects.requireNonNull(mainBody);
        for (final FunctionDecl function : Objects.requireNonNull(functions)) {
            this.functions.put(function.ident(), function);
        }
        for (final GlobalDecl global : Objects.requireNonNull(globals)) {
            this.globals.put(global.ident(), global);
        }
    }

    public Map<String, FunctionDecl> functions() {
        return Collections.unmodifiableMap(this.functions);
    }

    public FunctionDecl mainBody() {
        return this.mainBody;
    }

    public Map<String, GlobalDecl> globals() {
        return Collections.unmodifiableMap(this.globals);
    }
}
