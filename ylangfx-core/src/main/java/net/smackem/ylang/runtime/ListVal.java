package net.smackem.ylang.runtime;

import java.util.*;
import java.util.stream.Collectors;

public class ListVal extends Value implements Iterable<Value> {
    private final List<Value> values;

    private ListVal(List<Value> values, boolean copyValues) {
        super(ValueType.LIST);
        this.values = copyValues ? new ArrayList<>(values) : values;
    }

    public ListVal(List<Value> values) {
        this(values, true);
    }

    public ListVal(ListVal original) {
        this(original.values, true);
    }

    public int size() {
        return this.values.size();
    }

    public ListVal concat(ListVal other) {
        final List<Value> values = new ArrayList<>(this.values);
        values.addAll(other.values);
        return new ListVal(values, false);
    }

    public Value get(int index) {
        return this.values.get(index);
    }

    public void set(int index, Value v) {
        this.values.set(index, v);
    }

    public boolean contains(Value v) {
        return this.values.contains(v);
    }

    public void add(Value v) {
        this.values.add(v);
    }

    public Value removeLast() {
        return this.values.remove(this.values.size() - 1);
    }

    public Value removeAt(int index) {
        return this.values.remove(index);
    }

    public ListVal reversed() {
        final List<Value> copies = new ArrayList<>(this.values);
        Collections.reverse(copies);
        return new ListVal(copies);
    }

    public void sort(Comparator<Value> comparator) {
        this.values.sort(comparator);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Iterator<Value> iterator() {
        return this.values.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ListVal values1 = (ListVal) o;
        return Objects.equals(values, values1.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }

    @Override
    public String toString() {
        return "ListVal{" +
               "values=" + values +
               '}';
    }

    @Override
    public String toLangString() {
        return this.values.stream()
                .map(Value::toLangString)
                .collect(Collectors.joining(",", "[", "]"));
    }
}
