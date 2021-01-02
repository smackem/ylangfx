package net.smackem.ylang.execution.functions;

import com.google.common.base.Strings;
import net.smackem.ylang.runtime.ValueType;

import java.util.*;
import java.util.stream.Collectors;

class DocMarker {
    private final Map<String, FunctionGroup> functionRepo;
    private final Set<String> excludedFunctionNames;
    private static final String NL = System.lineSeparator();

    public DocMarker(Map<String, FunctionGroup> functionRepo, Set<String> excludedFunctionNames) {
        this.functionRepo = functionRepo;
        this.excludedFunctionNames = excludedFunctionNames != null
                ? excludedFunctionNames
                : Collections.emptySet();
    }

    public String generateDocs() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("<html><head><title>YLang</title></head>").append(NL)
                .append("<body>").append(NL)
                .append("<header><h2>Methods</h2></header>").append(NL);
        final Map<ValueType, Collection<FunctionSignature>> methods = collectMethods();
        for (final var entry : methods.entrySet()) {
            generateTypeMethodsDoc(buffer, entry.getKey(), entry.getValue());
        }
        buffer.append("<header><h2>Functions</h2></header>").append(NL);
        generateFunctionsDoc(buffer);
        buffer.append("</body>").append(NL)
                .append("</html>");
        return buffer.toString();
    }

    private void generateTypeMethodsDoc(StringBuilder buffer, ValueType type, Collection<FunctionSignature> methods) {
        buffer.append("<article>").append(NL)
                .append("<h3>").append(type).append("</h3>").append(NL);
        methods.stream()
            .sorted(Comparator.comparing(sig -> sig.name))
            .forEach(sig -> sig.renderHtml(buffer));
        buffer.append("</article>").append(NL);
    }

    private Map<ValueType, Collection<FunctionSignature>> collectMethods() {
        final Map<ValueType, Collection<FunctionSignature>> repo = new HashMap<>();
        for (final var entry : this.functionRepo.entrySet()) {
            final String name = entry.getKey();
            final FunctionGroup fg = entry.getValue();
            for (final FunctionOverload overload : fg.overloads()) {
                if (overload.isMethod() == false) {
                    continue;
                }
                final ValueType type = overload.parameters().get(0);
                final Collection<FunctionSignature> signatures = repo.computeIfAbsent(type, ignored -> new ArrayList<>());
                signatures.add(new FunctionSignature(name, overload));
            }
        }
        return repo;
    }

    private Collection<FunctionSignature> collectFunctions() {
        final Collection<FunctionSignature> repo = new ArrayList<>();
        this.functionRepo.values().stream()
                .filter(fg -> this.excludedFunctionNames.contains(fg.name()) == false)
                .sorted(Comparator.comparing(FunctionGroup::name))
                .forEach(fg -> {
                    for (final FunctionOverload overload : fg.overloads()) {
                        if (overload.isMethod()) {
                            continue;
                        }
                        repo.add(new FunctionSignature(fg.name(), overload));
                    }
                });
        return repo;
    }

    private void generateFunctionsDoc(StringBuilder buffer) {
        final Collection<FunctionSignature> functions = collectFunctions();
        for (final FunctionSignature sig : functions) {
            sig.renderHtml(buffer);
        }
    }

    private static class FunctionSignature {
        String name;
        FunctionOverload overload;

        FunctionSignature(String name, FunctionOverload overload) {
            this.name = name;
            this.overload = overload;
        }

        void renderHtml(StringBuilder buffer) {
            final Collection<String> parameters = this.overload.parameters()
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
            final String sig = "%s(%s)".formatted(
                    this.name, String.join(", ", parameters));
            final String doc = Strings.nullToEmpty(this.overload.doc())
                    .replaceAll("[\\r\\n]+", "<br />");
            buffer.append("<section>").append(NL)
                    .append("<h4>").append(sig).append("</h4>").append((NL))
                    .append("<p>").append(doc).append("</p>").append(NL);
        }
    }
}
