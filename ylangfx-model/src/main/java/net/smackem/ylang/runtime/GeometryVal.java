package net.smackem.ylang.runtime;

public abstract class GeometryVal extends Value implements Iterable<Value> {
    GeometryVal(ValueType type) {
        super(type);
    }

    public abstract GeometryVal translate(PointVal pt);

    public abstract RectVal bounds();
}
