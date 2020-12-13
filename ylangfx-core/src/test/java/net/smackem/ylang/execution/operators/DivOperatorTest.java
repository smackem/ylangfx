package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.runtime.NumberVal;
import net.smackem.ylang.runtime.RgbVal;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class DivOperatorTest {
    @Test
    public void divideNumberByNumber() throws MissingOverloadException {
        assertThat(BinaryOperator.DIV.invoke(new NumberVal(100f), new NumberVal(10f)))
                .isEqualTo(new NumberVal(10f));
    }

    @Test
    public void divideRgbByNumber() throws MissingOverloadException {
        assertThat(BinaryOperator.DIV.invoke(new RgbVal(20f, 30f, 40f, 50f), new NumberVal(10f)))
                .isEqualTo(new RgbVal(2f, 3f, 4f, 50f));
    }

    @Test
    public void divideRgbByRgb() throws MissingOverloadException {
        assertThat(BinaryOperator.DIV.invoke(new RgbVal(20f, 30f, 40f, 50f), new RgbVal(10f, 15f, 20f, 25f)))
                .isEqualTo(new RgbVal(2f, 2f, 2f, 50f));
    }

    @Test
    public void throwsOnMissingOverload() {
        // try to divide number by rgb
        assertThatThrownBy(() -> BinaryOperator.DIV.invoke(NumberVal.ONE, RgbVal.EMPTY))
                .isInstanceOf(MissingOverloadException.class);
    }
}
