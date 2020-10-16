package net.smackem.ylang.runtime;

import java.util.Iterator;
import java.util.Objects;

public class RangeVal extends Value implements Iterable<Value> {
    private final float lowerInclusive;
    private final float upperExclusive;
    private final float step;

    public RangeVal(float lowerInclusive, float upperExclusive, float step) {
        super(ValueType.RANGE);
        if (step > 0 && upperExclusive < lowerInclusive ||
            step < 0 && upperExclusive > lowerInclusive) {
            throw new IllegalArgumentException("invalid range");
        }
        if (step == 0) {
            throw new IllegalArgumentException("step must be != 0");
        }
        this.lowerInclusive = lowerInclusive;
        this.upperExclusive = upperExclusive;
        this.step = step;
    }

    public boolean contains(float f) {
        return this.step > 0 && f >= this.lowerInclusive && f < this.upperExclusive ||
               this.step < 0 && f <= this.lowerInclusive && f > this.upperExclusive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RangeVal values = (RangeVal) o;
        return Float.compare(values.lowerInclusive, lowerInclusive) == 0 &&
                Float.compare(values.upperExclusive, upperExclusive) == 0 &&
                Float.compare(values.step, step) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lowerInclusive, upperExclusive, step);
    }

    @Override
    public String toString() {
        return "RangeVal{" +
                "lowerInclusive=" + lowerInclusive +
                ", upperExclusive=" + upperExclusive +
                ", step=" + step +
                '}';
    }

    @Override
    public String toLangString() {
        return String.format(RuntimeParameters.LOCALE, "%f..%f..%f", this.lowerInclusive, this.step, this.upperExclusive);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Iterator<Value> iterator() {
        return new RangeIterator();
    }

    private class RangeIterator implements Iterator<Value> {
        private float current;

        RangeIterator() {
            this.current = lowerInclusive;
        }

        @Override
        public boolean hasNext() {
            return step > 0 && this.current < upperExclusive ||
                   step < 0 && this.current > upperExclusive;
        }

        @Override
        public Value next() {
            final float c = this.current;
            this.current += step;
            return new NumberVal(c);
        }
    }
}
