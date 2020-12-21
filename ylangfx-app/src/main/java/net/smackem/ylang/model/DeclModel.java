package net.smackem.ylang.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public abstract class DeclModel<T> {
    private final DeclType type;
    private final String signature;
    private final T decl;
    private final Collection<DeclModel<?>> children;

    DeclModel(DeclType type, String signature, T decl, Collection<DeclModel<?>> children) {
        this.type = type;
        this.signature = signature;
        this.decl = decl;
        this.children = children;
    }

    public DeclType type() {
        return this.type;
    }

    public String signature() {
        return this.signature;
    }

    public T decl() {
        return this.decl;
    }

    public Collection<DeclModel<?>> children() {
        return Collections.unmodifiableCollection(this.children);
    }
}
