package net.smackem.ylang.runtime;

import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.execution.operators.BinaryOperator;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RectValTest {
    @Test
    public void iterator() throws MissingOverloadException {
        final int width = 12;
        final int height = 15;
        final RectVal rect = new RectVal(10, 10, width, height);
        int count = 0;
        for (final var pt : rect) {
            assertThat(BinaryOperator.IN.invoke(pt, rect)).as("%s in %s", pt, rect)
                    .isEqualTo(BoolVal.TRUE);
            count++;
        }
        assertThat(count).isEqualTo(width * height);
    }
}
