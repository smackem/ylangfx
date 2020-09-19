package net.smackem.ylang.runtime;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class KernelValTest {

    @Test
    public void construction() {
        final KernelVal kernel = new KernelVal(10, 5, 1f);
        kernel.iterator().forEachRemaining(n ->
                assertThat(n).isEqualTo(new NumberVal(1)));
        final List<NumberVal> list = List.of(
                new NumberVal(1),
                new NumberVal(2),
                new NumberVal(3),
                new NumberVal(4));
        final KernelVal kernel2 = new KernelVal(list);
        assertThat(kernel2.width()).isEqualTo(2);
        assertThat(kernel2.height()).isEqualTo(2);
        assertThat(kernel2.size()).isEqualTo(4);
        final List<NumberVal> nonQuadraticList = List.of(
                new NumberVal(1),
                new NumberVal(2),
                new NumberVal(3));
        assertThatThrownBy(() -> new KernelVal(nonQuadraticList)).isInstanceOf(IllegalArgumentException.class);
    }
}