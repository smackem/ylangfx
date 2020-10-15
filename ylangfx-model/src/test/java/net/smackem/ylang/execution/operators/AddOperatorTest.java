package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.Context;
import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.runtime.*;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class AddOperatorTest {
    @Test
    public void numberPlusNumber() throws MissingOverloadException {
        assertThat(BinaryOperator.ADD.invoke(new NumberVal(100f), new NumberVal(50f)))
                .isEqualTo(new NumberVal(150f));
    }

    @Test
    public void pointPlusNumber() throws MissingOverloadException {
        assertThat(BinaryOperator.ADD.invoke(new PointVal(100f, 200f), new NumberVal(50f)))
                .isEqualTo(new PointVal(150f, 250f));
        assertThat(BinaryOperator.ADD.invoke(new NumberVal(50f), new PointVal(100f, 200f)))
                .isEqualTo(new PointVal(150f, 250f));
    }

    @Test
    public void pointPlusPoint() throws MissingOverloadException {
        assertThat(BinaryOperator.ADD.invoke(new PointVal(100f, 200f), new PointVal(50f, 20f)))
                .isEqualTo(new PointVal(150f, 220f));
    }

    @Test
    public void rgbPlusNumber() throws MissingOverloadException {
        assertThat(BinaryOperator.ADD.invoke(new RgbVal(10f, 20f, 30f, 40f), new NumberVal(50f)))
                .isEqualTo(new RgbVal(60f, 70f, 80f, 40f));
        assertThat(BinaryOperator.ADD.invoke(new NumberVal(50f), new RgbVal(10f, 20f, 30f, 40f)))
                .isEqualTo(new RgbVal(60f, 70f, 80f, 40f));
    }

    @Test
    public void throwsOnMissingOverload() {
        assertThatThrownBy(() -> BinaryOperator.ADD.invoke(NumberVal.ONE, RectVal.EMPTY))
                .isInstanceOf(MissingOverloadException.class);
        assertThatThrownBy(() -> BinaryOperator.ADD.invoke(NilVal.INSTANCE, NumberVal.ONE))
                .isInstanceOf(MissingOverloadException.class);
    }
}
