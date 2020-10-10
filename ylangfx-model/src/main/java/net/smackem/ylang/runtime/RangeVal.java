package net.smackem.ylang.runtime;

import java.util.Iterator;

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
