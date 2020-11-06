package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.runtime.ListVal;
import net.smackem.ylang.runtime.NumberVal;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ConcatOperatorImplTest {
    @Test
    public void concatLists() throws MissingOverloadException {
        assertThat(BinaryOperator.CONCAT.invoke(
                new ListVal(List.of(NumberVal.MINUS_ONE, NumberVal.ZERO)),
                new ListVal(List.of(NumberVal.ONE))))
                .isEqualTo(
                        new ListVal(List.of(NumberVal.MINUS_ONE, NumberVal.ZERO, NumberVal.ONE)));
    }
}