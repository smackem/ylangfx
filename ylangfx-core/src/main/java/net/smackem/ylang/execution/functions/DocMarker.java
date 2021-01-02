package net.smackem.ylang.execution.functions;

import com.google.common.base.Strings;
import net.smackem.ylang.runtime.ValueType;

import java.util.*;
import java.util.stream.Collectors;

class DocMarker {
    private final Map<String, FunctionGroup> functionRepo;
    private final Set<String> excludedFunctionNames;
    private static final String NL = System.lineSeparator();
    private static final String HTML_PREFIX = """
            <html>
                <head>
                    <title>YLang</title>
                    <style>
                        article:nth-child(odd) {
                            background: #ddd
                        }
                        h2 {
                            border: 1px solid #888;
                            padding: 8px;
                            background-color: #001060;
                            color: #fff;
                        }
                        body {
                            font-family: 'Droid Sans', Calibri, Helvetica, Arial, sans-serif;
                        }
                        article {
                            padding: 2px 2px 2px 6px;
                        }
                        section > h4 {
                            font-family: 'Fira Code', Consolas, monospace;
                            padding-bottom: 0px;
                            margin-bottom: 4px;
                        }
                        section > p {
                            margin-top: 1px;
                        }
                    </style>
                </head>
                <body>
            """;
    private static final String HTML_SUFFIX = """
                </body>
            </html>
            """;

    public DocMarker(Map<String, FunctionGroup> functionRepo, Set<String> excludedFunctionNames) {
        this.functionRepo = functionRepo;
        this.excludedFunctionNames = excludedFunctionNames != null
                ? excludedFunctionNames
                : Collections.emptySet();
    }

    public String generateDocs() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(HTML_PREFIX)
                .append("<header><h2>YLang Methods</h2></header>").append(NL);
        renderNavBar(buffer);
        final Map<ValueType, Collection<FunctionSignature>> methods = collectMethods();
        methods.entrySet().stream()
                .sorted(Comparator.comparing(entry -> entry.getKey().name()))
                .forEach(entry -> generateTypeMethodsDoc(buffer, entry.getKey(), entry.getValue()));
        buffer.append("<header><h2>YLang Functions</h2></header>").append(NL);
        generateFunctionsDoc(buffer);
        buffer.append(HTML_SUFFIX);
        return buffer.toString();
    }

    private static void renderNavBar(StringBuilder buffer) {
        buffer.append("<nav>");
        boolean[] first = { true };
        ValueType.publicValues().stream()
                .sorted(Comparator.comparing(Enum::name))
                .forEach(type -> {
                    final String name = type.name();
                    if (first[0] == false) {
                        buffer.append("&nbsp;|&nbsp;");
                    }
                    buffer.append("<a href=\"#").append(name).append("\">").append(name).append("</a>");
                    first[0] = false;
                });
        buffer.append("</nav>");
    }

    private void generateTypeMethodsDoc(StringBuilder buffer, ValueType type, Collection<FunctionSignature> methods) {
        buffer.append("<article id=\"").append(type).append("\">").append(NL)
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
                    .replaceAll("[\\r\\n]+", "<br />")
                    .replaceAll("`(\\w+)`", "<em>$1</em>");
            buffer.append("<section>").append(NL)
                    .append("<h4>").append(sig).append("</h4>").append((NL))
                    .append("<p>").append(doc).append("</p>").append(NL)
                    .append("</section>").append(NL);
        }
    }
}
