package net.smackem.ylang.model;

import net.smackem.ylang.lang.GlobalDecl;

import java.util.Collections;

public class GlobalDeclModel extends DeclModel<GlobalDecl> {
    GlobalDeclModel(GlobalDecl decl) {
        super(DeclType.GLOBAL, decl, decl.docComment(), Collections.emptyList());
    }

    @Override
    public String signature() {
        return decl().name();
    }
}
