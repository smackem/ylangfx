package net.smackem.ylang.model;

import net.smackem.ylang.lang.FunctionDecl;

import java.util.Collection;
import java.util.Collections;

public class FunctionDeclModel extends DeclModel<FunctionDecl> {
    FunctionDeclModel(FunctionDecl decl) {
        super(DeclType.FUNCTION, decl.name(), decl, Collections.emptyList());
    }
}
