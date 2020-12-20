package net.smackem.ylang.runtime;

import net.smackem.ylang.interop.Yln;

public class RuntimeContext {
    private RuntimeContext() {}

    private static final ThreadLocal<RuntimeContext> instance = ThreadLocal.withInitial(RuntimeContext::new);

    private boolean disableYln;

    public static RuntimeContext current() {
        return instance.get();
    }

    public static void reset() {
        instance.remove();
    }

    public void disableYln(boolean disabled) {
        this.disableYln = disabled;
    }

    public boolean isYlnDisabled() {
        return this.disableYln;
    }

    public Yln yln() {
        return this.disableYln ? null : Yln.INSTANCE;
    }
}
