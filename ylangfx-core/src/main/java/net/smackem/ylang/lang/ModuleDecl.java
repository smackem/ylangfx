package net.smackem.ylang.lang;

import java.util.*;

class ModuleDecl {
    private final Map<String, FunctionDecl> functions = new HashMap<>();
    private final FunctionDecl mainBody;

    ModuleDecl(FunctionDecl mainBody, Collection<FunctionDecl> functions) {
        this.mainBody = Objects.requireNonNull(mainBody);
        for (final FunctionDecl function : Objects.requireNonNull(functions)) {
            this.functions.put(function.name(), function);
        }
    }

    public Map<String, FunctionDecl> functions() {
        return Collections.unmodifiableMap(this.functions);
    }

    public FunctionDecl mainBody() {
        return this.mainBody;
    }
}
