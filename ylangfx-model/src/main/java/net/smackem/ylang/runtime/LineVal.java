package net.smackem.ylang.runtime;

import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;

public class LineVal extends GeometryVal {
    private final float x1, y1;
    private final float x2, y2;

    public LineVal(float x1, float y1, float x2, float y2) {
        super(ValueType.LINE);
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public float x1() {
        return x1;
    }

    public float y1() {
        return y1;
    }

    public float x2() {
        return x2;
    }

    public float y2() {
        return y2;
    }

    public float length() {
        return (float) Math.hypot(this.x2 - this.x1, this.y2 - this.y1);
    }

    public PointVal intersect(LineVal other) {
        return null;
    }

    @Override
    public GeometryVal translate(PointVal pt) {
        return new LineVal(this.x1 + pt.x(), this.y1 + pt.y(), this.x2 + pt.x(), this.y2 + pt.y());
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Iterator<Value> iterator() {
        return new PointIterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineVal values = (LineVal) o;
        return Float.compare(values.x1, x1) == 0 &&
                Float.compare(values.y1, y1) == 0 &&
                Float.compare(values.x2, x2) == 0 &&
                Float.compare(values.y2, y2) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x1, y1, x2, y2);
    }

    @Override
    public String toString() {
        return "LineVal{" +
                "x1=" + x1 +
                ", y1=" + y1 +
                ", x2=" + x2 +
                ", y2=" + y2 +
                '}';
    }

    @Override
    public String toLangString() {
        return String.format(Locale.ROOT, "line(%f;%f, %f;%f)", this.x1, this.y1, this.x2, this.y2);
    }

    private class PointIterator implements Iterator<Value> {
        final float stepX, stepY;
        final int steps;
        float x, y;
        int index;

        PointIterator() {
            final float dx = x2 - x1;
            final float dy = y2 - y1;
            this.steps = (int) Math.max(Math.abs(dx), Math.abs(dy));
            this.stepX = dx / (float)steps;
            this.stepY = dy / (float)steps;
            this.x = x1;
            this.y = y1;
        }

        @Override
        public boolean hasNext() {
            return this.index < this.steps;
        }

        @Override
        public Value next() {
            final PointVal pt = new PointVal(this.x, this.y);
            this.index++;
            this.x += this.stepX;
            this.y += this.stepY;
            return pt;
        }
    }
}
