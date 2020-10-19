package net.smackem.ylang.util;

import net.smackem.ylang.runtime.ListVal;
import net.smackem.ylang.runtime.MapVal;
import net.smackem.ylang.runtime.Value;

public class Values {
    private Values() {}

    public static String prettyPrint(Value value) {
        final StringBuilder buffer = new StringBuilder();
        prettyPrintRecurse(value, buffer, "");
        return buffer.toString();
    }

    private static void prettyPrintRecurse(Value value, StringBuilder buffer, String indent) {
        if (value instanceof MapVal) {
            final MapVal map = (MapVal) value;
            buffer.append("{\n");
            final String innerIndent = indent + "    ";
            for (final var entry : map.entries().entrySet()) {
                buffer.append(innerIndent);
                prettyPrintRecurse(entry.getKey(), buffer, innerIndent);
                buffer.append(": ");
                prettyPrintRecurse(entry.getValue(), buffer, innerIndent);
                buffer.append(",\n");
            }
            buffer.append(indent);
            buffer.append("}");
        } else if (value instanceof ListVal) {
            final ListVal list = (ListVal) value;
            buffer.append("[\n");
            final String innerIndent = indent + "    ";
            for (final Value element : list) {
                buffer.append(innerIndent);
                prettyPrintRecurse(element, buffer, innerIndent);
                buffer.append(",\n");
            }
            buffer.append(indent);
            buffer.append("]");
        } else {
            buffer.append(value.toLangString());
        }
    }
}
