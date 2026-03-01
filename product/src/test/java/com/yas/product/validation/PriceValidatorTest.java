package com.yas.product.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PriceValidatorTest {

    private final PriceValidator priceValidator = new PriceValidator();

    @Test
    void isValid_shouldReturnTrueForNonNegativeValues() {
        assertThat(priceValidator.isValid(0.0, null)).isTrue();
        assertThat(priceValidator.isValid(25.5, null)).isTrue();
    }

    @Test
    void isValid_shouldReturnFalseForNegativeValues() {
        assertThat(priceValidator.isValid(-0.01, null)).isFalse();
    }

    @Test
    void isValid_shouldThrowExceptionForNullValue() {
        assertThatThrownBy(() -> priceValidator.isValid(null, null))
                .isInstanceOf(NullPointerException.class);
    }
}
