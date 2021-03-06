package net.smackem.ylang.runtime;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MapVal extends Value {

    private final Map<Value, Value> entries = new HashMap<>();

    public MapVal(Collection<MapEntryVal> entries) {
        super(ValueType.MAP);
        for (final MapEntryVal entry : entries) {
            this.entries.put(entry.key(), entry.value());
        }
    }

    public Map<Value, Value> entries() {
        return this.entries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final MapVal mapVal = (MapVal) o;
        return Objects.equals(entries, mapVal.entries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entries);
    }

    @Override
    public String toString() {
        return "MapVal{" +
               "entries=" + entries +
               '}';
    }

    @Override
    public String toLangString() {
        final StringBuilder sb = new StringBuilder();
        for (final var entry : this.entries.entrySet()) {
            sb.append(String.format(RuntimeParameters.LOCALE, "{ %s: %s }, ", entry.getKey().toLangString(), entry.getValue().toLangString()));
        }
        return sb.toString();
    }
}
