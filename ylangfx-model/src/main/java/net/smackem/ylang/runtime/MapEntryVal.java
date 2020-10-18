package net.smackem.ylang.runtime;

import java.util.Objects;

public class MapEntryVal extends Value {

    private final String key;
    private final Value value;

    public MapEntryVal(String key, Value value) {
        super(ValueType.MAP_ENTRY);
        this.key = Objects.requireNonNull(key);
        this.value = Objects.requireNonNull(value);
    }

    public String key() {
        return this.key;
    }

    public Value value() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final MapEntryVal that = (MapEntryVal) o;
        return Objects.equals(key, that.key) &&
               Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        return "MapEntryVal{" +
               "key='" + key + '\'' +
               ", value=" + value +
               '}';
    }

    @Override
    public String toLangString() {
        return String.format(RuntimeParameters.LOCALE, "%s:%s", this.key, this.value.toLangString());
    }
}
