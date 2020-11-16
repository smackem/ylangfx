package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.runtime.NumberVal;
import net.smackem.ylang.runtime.PointVal;
import net.smackem.ylang.runtime.RgbVal;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NegOperatorTest {
    @Test
    public void negNumber() throws MissingOverloadException {
        assertThat(UnaryOperator.NEG.invoke(NumberVal.ONE))
                .isEqualTo(NumberVal.MINUS_ONE);
    }

    @Test
    public void negPoint() throws MissingOverloadException {
        assertThat(UnaryOperator.NEG.invoke(new PointVal(10, 11)))
                .isEqualTo(new PointVal(-10, -11));
    }

    @Test
    public void negRgb() throws MissingOverloadException {
        assertThat(UnaryOperator.NEG.invoke(new RgbVal(1, 2, 3, 4)))
                .isEqualTo(new RgbVal(254, 253, 252, 4));
    }
}
