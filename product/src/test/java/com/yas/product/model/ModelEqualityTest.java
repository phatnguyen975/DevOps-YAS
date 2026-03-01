package com.yas.product.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ModelEqualityTest {

    @Test
    void brandEqualsAndHashCode_shouldUseId() {
        Brand a = new Brand();
        a.setId(1L);
        a.setName("Brand A");
        Brand b = new Brand();
        b.setId(1L);
        b.setName("Brand B");

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());

        b.setId(2L);
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void categoryEquals_shouldReturnTrueWhenIdsMatch() {
        Category parent = new Category();
        parent.setId(9L);
        Category child = new Category();
        child.setId(9L);

        assertThat(parent).isEqualTo(child);
        assertThat(parent.getCategories()).isNotNull();
        assertThat(parent.getProductCategories()).isNotNull();
    }

    @Test
    void productEquals_shouldReturnTrueWhenIdsMatch() {
        Product p1 = Product.builder().id(5L).name("P1").build();
        Product p2 = Product.builder().id(5L).name("P2").build();
        Product p3 = Product.builder().id(6L).name("P3").build();

        assertThat(p1).isEqualTo(p2);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
        assertThat(p1).isNotEqualTo(p3);
    }

    @Test
    void productOptionEquals_shouldUseId() {
        ProductOption opt1 = new ProductOption();
        opt1.setId(7L);
        ProductOption opt2 = new ProductOption();
        opt2.setId(7L);

        assertThat(opt1).isEqualTo(opt2);
    }

    @Test
    void productOptionValueBuilder_shouldPopulateFields() {
        Product product = Product.builder().id(1L).name("Main").build();
        ProductOption option = new ProductOption();
        option.setId(2L);
        option.setName("Size");

        ProductOptionValue value = ProductOptionValue.builder()
            .id(3L)
            .product(product)
            .productOption(option)
            .displayOrder(1)
            .displayType("TEXT")
            .value("M")
            .build();

        assertThat(value.getProduct()).isEqualTo(product);
        assertThat(value.getProductOption()).isEqualTo(option);
        assertThat(value.getValue()).isEqualTo("M");
        assertThat(value.getDisplayOrder()).isEqualTo(1);
    }

    @Test
    void productCategory_shouldLinkProductAndCategory() {
        Product product = Product.builder().id(11L).build();
        Category category = new Category();
        category.setId(22L);
        ProductCategory productCategory = ProductCategory.builder()
            .id(33L)
            .product(product)
            .category(category)
            .build();

        assertThat(productCategory.getProduct()).isEqualTo(product);
        assertThat(productCategory.getCategory()).isEqualTo(category);
    }

    @Test
    void productImage_shouldStoreImageAndProduct() {
        Product product = Product.builder().id(44L).build();
        ProductImage image = ProductImage.builder()
            .id(55L)
            .imageId(66L)
            .product(product)
            .build();

        assertThat(image.getImageId()).isEqualTo(66L);
        assertThat(image.getProduct()).isEqualTo(product);
    }

    @Test
    void productRelated_shouldLinkProducts() {
        Product main = Product.builder().id(77L).build();
        Product related = Product.builder().id(88L).build();
        ProductRelated pr = ProductRelated.builder()
            .id(99L)
            .product(main)
            .relatedProduct(related)
            .build();

        assertThat(pr.getProduct()).isEqualTo(main);
        assertThat(pr.getRelatedProduct()).isEqualTo(related);
    }

    @Test
    void productOptionValueEquals_shouldUseId() {
        ProductOptionValue v1 = ProductOptionValue.builder().id(50L).build();
        ProductOptionValue v2 = ProductOptionValue.builder().id(50L).build();
        ProductOptionValue v3 = ProductOptionValue.builder().id(51L).build();

        assertThat(v1).isEqualTo(v2);
        assertThat(v1.hashCode()).isEqualTo(v2.hashCode());
        assertThat(v1).isNotEqualTo(v3);
    }

    @Test
    void productRelatedEquals_shouldReturnFalseWhenIdNull() {
        ProductRelated pr1 = new ProductRelated();
        ProductRelated pr2 = ProductRelated.builder().id(10L).build();

        assertThat(pr1).isNotEqualTo(pr2);
    }

    @Test
    void productEquals_shouldReturnFalseWhenIdNull() {
        Product p1 = Product.builder().id(null).name("P1").build();
        Product p2 = Product.builder().id(1L).name("P2").build();

        assertThat(p1).isNotEqualTo(p2);
    }

    @Test
    void categoryEquals_shouldReturnFalseWhenIdNull() {
        Category c1 = new Category();
        Category c2 = new Category();
        c2.setId(1L);

        assertThat(c1).isNotEqualTo(c2);
    }
}
