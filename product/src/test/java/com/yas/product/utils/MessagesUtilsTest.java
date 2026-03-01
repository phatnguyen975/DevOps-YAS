package com.yas.product.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessagesUtilsTest {

    @Test
    void getMessage_whenErrorCodeExists_shouldReturnMessage() {
        String message = MessagesUtils.getMessage("PRODUCT_NOT_FOUND");

        assertThat(message).isEqualTo("Product {} is not found");
    }

    @Test
    void getMessage_whenErrorCodeMissing_shouldFallBackToCodeItself() {
        String message = MessagesUtils.getMessage("UNKNOWN_CODE");

        assertThat(message).isEqualTo("UNKNOWN_CODE");
    }

    @Test
    void getMessage_shouldFormatMessageWithParameter() {
        String message = MessagesUtils.getMessage("PRODUCT_NOT_FOUND", "123");

        assertThat(message).isEqualTo("Product 123 is not found");
    }
}
