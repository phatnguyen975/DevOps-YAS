package com.yas.product.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductOptionCombinationTest {

    @Test
    void equality_shouldDependOnId() {
        ProductOptionCombination c1 = ProductOptionCombination.builder().id(1L).build();
        ProductOptionCombination c2 = ProductOptionCombination.builder().id(1L).build();
        ProductOptionCombination c3 = ProductOptionCombination.builder().id(2L).build();

        assertThat(c1).isEqualTo(c2);
        assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
        assertThat(c1).isNotEqualTo(c3);
    }

    @Test
    void fields_shouldBeSetViaBuilder() {
        Product product = Product.builder().id(10L).build();
        ProductOption option = new ProductOption();
        option.setId(20L);

        ProductOptionCombination c = ProductOptionCombination.builder()
            .id(30L)
            .product(product)
            .productOption(option)
            .value("Red")
            .displayOrder(2)
            .build();

        assertThat(c.getProduct()).isEqualTo(product);
        assertThat(c.getProductOption()).isEqualTo(option);
        assertThat(c.getValue()).isEqualTo("Red");
        assertThat(c.getDisplayOrder()).isEqualTo(2);
    }
}
