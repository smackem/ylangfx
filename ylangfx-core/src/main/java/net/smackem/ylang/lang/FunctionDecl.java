package net.smackem.ylang.lang;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public final class FunctionDecl extends Declaration {
    private final int localCount;
    private final Collection<String> parameters;

    private FunctionDecl(String ident, String docComment, int lineNumber,
                         Collection<String> parameters, int localCount) {
        super(ident, docComment, lineNumber);
        this.localCount = localCount;
        this.parameters = Collections.unmodifiableCollection(Objects.requireNonNull(parameters));
    }

    static FunctionDecl function(String ident, String docComment, int lineNumber,
                                 Collection<String> parameters, int localCount) {
        return new FunctionDecl(ident, docComment, lineNumber, parameters, localCount);
    }

    static FunctionDecl main(int localCount) {
        return new FunctionDecl(null, "", -1, Collections.emptyList(), localCount);
    }

    public boolean isMain() {
        return ident() == null;
    }

    public int localCount() {
        return this.localCount;
    }

    public int parameterCount() {
        return this.parameters.size();
    }

    public Collection<String> parameters() {
        return this.parameters;
    }
}
