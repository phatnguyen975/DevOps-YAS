package com.yas.product.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AbstractCircuitBreakFallbackHandlerTest {

    private static class TestCircuitBreakHandler extends AbstractCircuitBreakFallbackHandler {
        void invokeBodilessFallback(Throwable throwable) throws Throwable {
            handleBodilessFallback(throwable);
        }

        <T> T invokeTypedFallback(Throwable throwable) throws Throwable {
            return handleTypedFallback(throwable);
        }
    }

    @Test
    void handleBodilessFallback_shouldRethrowOriginalException() {
        TestCircuitBreakHandler handler = new TestCircuitBreakHandler();
        RuntimeException exception = new RuntimeException("Test exception");

        assertThatThrownBy(() -> handler.invokeBodilessFallback(exception))
                .isSameAs(exception);
    }

    @Test
    void handleTypedFallback_shouldRethrowOriginalException() {
        TestCircuitBreakHandler handler = new TestCircuitBreakHandler();
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        assertThatThrownBy(() -> handler.invokeTypedFallback(exception))
                .isSameAs(exception);
    }

    @Test
    void handleTypedFallback_shouldPropagateCheckedException() {
        TestCircuitBreakHandler handler = new TestCircuitBreakHandler();
        Exception checked = new Exception("Checked failure");

        assertThatThrownBy(() -> handler.invokeTypedFallback(checked))
                .isSameAs(checked);
    }
}
