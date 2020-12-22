package net.smackem.ylang.model;

import net.smackem.ylang.lang.FunctionDecl;
import net.smackem.ylang.lang.GlobalDecl;
import net.smackem.ylang.lang.ModuleDecl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

public class ModuleDeclModel extends DeclModel<ModuleDecl> {
    private final Path path;

    private ModuleDeclModel(Path path, ModuleDecl decl) {
        super(DeclType.FILE, decl, "", getChildren(decl));
        this.path = path;
    }

    public static ModuleDeclModel extract(Path path) {
        return null;
    }

    public Path path() {
        return this.path;
    }

    private static Collection<DeclModel<?>> getChildren(ModuleDecl module) {
        final Collection<DeclModel<?>> children = new ArrayList<>();
        for (final FunctionDecl function : module.functions().values()) {
            children.add(new FunctionDeclModel(function));
        }
        for (final GlobalDecl global : module.globals().values()) {
            children.add(new GlobalDeclModel(global));
        }
        return children;
    }

    @Override
    public String signature() {
        return this.path.getFileName().toString();
    }
}
