package com.yas.product.utils;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductConverterTest {

    @Test
    void toSlug_shouldGenerateLowercaseHyphenatedSlug() {
        String slug = ProductConverter.toSlug("  Spring Boot Product ");

        assertThat(slug).isEqualTo("spring-boot-product");
    }

    @Test
    void toSlug_shouldRemoveLeadingHyphenAfterCleanup() {
        String slug = ProductConverter.toSlug("--Alpha##Beta");

        assertThat(slug).isEqualTo("alpha-beta");
    }

    @Test
    void toSlug_shouldCollapseRepeatedSeparators() {
        String slug = ProductConverter.toSlug("Hello---World");

        assertThat(slug).isEqualTo("hello-world");
    }
}
