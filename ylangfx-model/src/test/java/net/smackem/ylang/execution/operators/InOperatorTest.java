package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.Context;
import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.runtime.BoolVal;
import net.smackem.ylang.runtime.PointVal;
import net.smackem.ylang.runtime.RectVal;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InOperatorTest {
    @Test
    public void pointInRect() throws MissingOverloadException {
        final RectVal rect = new RectVal(0, 0, 20, 20);
        assertThat(BinaryOperator.IN.invoke(Context.EMPTY,
                new PointVal(10f, 10f), rect))
                .isEqualTo(BoolVal.TRUE);
        assertThat(BinaryOperator.IN.invoke(Context.EMPTY,
                new PointVal(19, 19), rect))
                .isEqualTo(BoolVal.TRUE);
        assertThat(BinaryOperator.IN.invoke(Context.EMPTY,
                new PointVal(20, 19), rect))
                .isEqualTo(BoolVal.FALSE);
        assertThat(BinaryOperator.IN.invoke(Context.EMPTY,
                new PointVal(19, 20), rect))
                .isEqualTo(BoolVal.FALSE);
    }
}
