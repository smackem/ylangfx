package net.smackem.ylang.model;

import net.smackem.ylang.lang.Compiler;
import net.smackem.ylang.lang.FunctionDecl;
import net.smackem.ylang.lang.GlobalDecl;
import net.smackem.ylang.lang.ModuleDecl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class ModuleDeclModel extends DeclModel<ModuleDecl> {
    private final Path path;

    private ModuleDeclModel(Path path, ModuleDecl decl) {
        super(DeclType.FILE, decl, "", decl != null ? getChildren(decl) : Collections.emptyList());
        this.path = path;
    }

    public static ModuleDeclModel extract(Path path) throws IOException {
        final Compiler compiler = new Compiler();
        final Collection<String> errors = new ArrayList<>();
        final ModuleDecl module = compiler.extractDeclarations(Files.readString(path), errors);
        return new ModuleDeclModel(path, module);
    }

    public Path path() {
        return this.path;
    }

    private static Collection<DeclModel<?>> getChildren(ModuleDecl module) {
        final Collection<DeclModel<?>> children = new ArrayList<>();
        module.functions().values().stream()
                .sorted(Comparator.comparing(FunctionDecl::name))
                .forEach(f -> children.add(new FunctionDeclModel(f)));
        module.globals().values().stream()
                .sorted(Comparator.comparing(GlobalDecl::name))
                .forEach(g -> children.add(new GlobalDeclModel(g)));
        return children;
    }

    @Override
    public String signature() {
        return this.path.getFileName().toString();
    }
}
