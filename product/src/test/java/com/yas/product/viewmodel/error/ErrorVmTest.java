package com.yas.product.viewmodel.error;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorVmTest {

    @Test
    void constructorWithoutFieldErrors_shouldInitializeEmptyList() {
        ErrorVm vm = new ErrorVm("400", "Bad Request", "Detail message");

        assertThat(vm.fieldErrors()).isNotNull();
        assertThat(vm.fieldErrors()).isEmpty();
        assertThat(vm.statusCode()).isEqualTo("400");
        assertThat(vm.title()).isEqualTo("Bad Request");
        assertThat(vm.detail()).isEqualTo("Detail message");
    }

    @Test
    void constructorWithFieldErrors_shouldRetainList() {
        List<String> errors = List.of("field1 is required", "field2 invalid");
        ErrorVm vm = new ErrorVm("422", "Validation Failed", "Some fields invalid", errors);

        assertThat(vm.fieldErrors()).containsExactlyElementsOf(errors);
        assertThat(vm.statusCode()).isEqualTo("422");
        assertThat(vm.title()).isEqualTo("Validation Failed");
        assertThat(vm.detail()).isEqualTo("Some fields invalid");
    }
}
