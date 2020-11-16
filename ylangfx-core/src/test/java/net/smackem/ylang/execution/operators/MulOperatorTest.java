package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.runtime.NumberVal;
import net.smackem.ylang.runtime.PointVal;
import net.smackem.ylang.runtime.RgbVal;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MulOperatorTest {
    @Test
    public void numberTimesNumber() throws MissingOverloadException {
        assertThat(BinaryOperator.MUL.invoke(new NumberVal(12), new NumberVal(10)))
                .isEqualTo(new NumberVal(120));
    }

    @Test
    public void pointTimesNumber() throws MissingOverloadException {
        assertThat(BinaryOperator.MUL.invoke(new PointVal(12, 13), new NumberVal(10)))
                .isEqualTo(new PointVal(120, 130));
        assertThat(BinaryOperator.MUL.invoke(new NumberVal(10), new PointVal(12, 13)))
                .isEqualTo(new PointVal(120, 130));
    }

    @Test
    public void pointTimesPoint() throws MissingOverloadException {
        assertThat(BinaryOperator.MUL.invoke(new PointVal(12, 13), new PointVal(10, 11)))
                .isEqualTo(new PointVal(120, 143));
    }

    @Test
    public void rgbTimesNumber() throws MissingOverloadException {
        assertThat(BinaryOperator.MUL.invoke(new RgbVal(1, 2, 3, 4), new NumberVal(10)))
                .isEqualTo(new RgbVal(10, 20, 30, 4));
        assertThat(BinaryOperator.MUL.invoke(new NumberVal(10), new RgbVal(1, 2, 3, 4)))
                .isEqualTo(new RgbVal(10, 20, 30, 4));
    }

    @Test
    public void rgbTimesRgb() throws MissingOverloadException {
        assertThat(BinaryOperator.MUL.invoke(new RgbVal(10, 20, 30, 40), new RgbVal(127.5f, 127.5f, 127.5f, 127.5f)))
                .isEqualTo(new RgbVal(5, 10, 15, 40));
        assertThat(BinaryOperator.MUL.invoke(new RgbVal(127.5f, 127.5f, 127.5f, 127.5f), new RgbVal(10, 20, 30, 40)))
                .isEqualTo(new RgbVal(5, 10, 15, 127.5f));
    }
}
