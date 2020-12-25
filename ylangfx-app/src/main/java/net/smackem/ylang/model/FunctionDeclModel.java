package net.smackem.ylang.model;

import net.smackem.ylang.lang.FunctionDecl;

import java.util.Collection;
import java.util.Collections;

public class FunctionDeclModel extends DeclModel<FunctionDecl> {
    FunctionDeclModel(FunctionDecl decl) {
        super(DeclType.FUNCTION, decl, decl.docComment(), Collections.emptyList());
    }

    @Override
    public String signature() {
        return "%s(%s)".formatted(decl().ident(), String.join(", ", decl().parameters()));
    }
}
