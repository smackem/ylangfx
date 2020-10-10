package net.smackem.ylang.runtime;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class RangeValTest {
    @Test
    public void forwardRange() {
        final RangeVal range = new RangeVal(0, 10, 1);
        float n = 0;
        for (final Value v : range) {
            assertThat(v).isInstanceOf(NumberVal.class);
            n += ((NumberVal) v).value();
        }
        assertThat(n).isEqualTo(1+2+3+4+5+6+7+8+9);
    }

    @Test
    public void reverseRange() {
        final RangeVal range = new RangeVal(10, 0, -1);
        float n = 0;
        for (final Value v : range) {
            assertThat(v).isInstanceOf(NumberVal.class);
            n += ((NumberVal) v).value();
        }
        assertThat(n).isEqualTo(1+2+3+4+5+6+7+8+9+10);
    }

    @Test
    public void throwsOnIllegalRange() {
        assertThatThrownBy(() -> new RangeVal(10, -5, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new RangeVal(0, 100, -1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new RangeVal(0, 10, 0)).isInstanceOf(IllegalArgumentException.class);
    }
}
