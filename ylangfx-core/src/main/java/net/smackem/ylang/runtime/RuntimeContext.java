package net.smackem.ylang.runtime;

import net.smackem.ylang.interop.Yln;

public class RuntimeContext {
    private RuntimeContext() {}

    private static final ThreadLocal<RuntimeContext> INSTANCE = ThreadLocal.withInitial(RuntimeContext::new);

    private boolean disableYln;

    public static RuntimeContext current() {
        return INSTANCE.get();
    }

    public static void reset() {
        INSTANCE.remove();
    }

    public boolean disableYln(boolean disabled) {
        final boolean old = this.disableYln;
        this.disableYln = disabled;
        return old;
    }

    public boolean isYlnDisabled() {
        return this.disableYln;
    }

    public Yln yln() {
        return this.disableYln ? null : Yln.INSTANCE;
    }
}
