package net.smackem.ylang.runtime;

import net.smackem.ylang.execution.Context;
import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.execution.operators.BinaryOperator;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RectValTest {
    @Test
    public void iterator() throws MissingOverloadException {
        final RectVal rect = new RectVal(0, 0, 10, 10);
        int count = 0;
        for (final var pt : rect) {
            assertThat(BinaryOperator.IN.invoke(Context.EMPTY, pt, rect))
                    .isEqualTo(BoolVal.TRUE);
            count++;
        }
        assertThat(count).isEqualTo(100);
    }
}
