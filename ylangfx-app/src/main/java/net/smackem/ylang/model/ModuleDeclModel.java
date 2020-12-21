package net.smackem.ylang.model;

import net.smackem.ylang.lang.FunctionDecl;
import net.smackem.ylang.lang.ModuleDecl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

public class ModuleDeclModel extends DeclModel<ModuleDecl> {
    public ModuleDeclModel(Path path, ModuleDecl decl) {
        super(DeclType.FILE, path.getFileName().toString(), decl, getChildren(decl));
    }

    private static Collection<DeclModel<?>> getChildren(ModuleDecl module) {
        final Collection<DeclModel<?>> children = new ArrayList<>();
        for (final FunctionDecl function : module.functions().values()) {
            children.add(new FunctionDeclModel(function));
        }
        return children;
    }
}
