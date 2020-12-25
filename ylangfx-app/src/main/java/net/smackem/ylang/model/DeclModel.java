package net.smackem.ylang.model;

import net.smackem.ylang.lang.Declaration;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public abstract class DeclModel<T extends Declaration> {
    private final DeclType type;
    private final T decl;
    private final Collection<DeclModel<? extends Declaration>> children;
    private final String docComment;

    DeclModel(DeclType type, T decl, String docComment, Collection<DeclModel<? extends Declaration>> children) {
        this.type = type;
        this.decl = decl;
        this.docComment = docComment;
        this.children = Objects.requireNonNull(children);
    }

    public DeclType type() {
        return this.type;
    }

    public abstract String signature();

    public T decl() {
        return this.decl;
    }

    public String docComment() {
        return this.docComment;
    }

    public Collection<DeclModel<?>> children() {
        return Collections.unmodifiableCollection(this.children);
    }
}
