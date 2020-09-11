package net.smackem.ylang.runtime;

public class NilVal extends Value {
    private NilVal() {
        super(ValueType.NIL);
    }

    public static final NilVal INSTANCE = new NilVal();
}
