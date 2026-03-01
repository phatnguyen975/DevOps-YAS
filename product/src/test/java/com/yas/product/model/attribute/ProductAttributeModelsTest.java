package com.yas.product.model.attribute;

import com.yas.product.model.Product;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductAttributeModelsTest {

    @Test
    void productAttributeEquality_shouldUseId() {
        ProductAttributeGroup group = new ProductAttributeGroup();
        group.setId(1L);

        ProductAttribute a = ProductAttribute.builder()
            .id(10L)
            .name("Color")
            .productAttributeGroup(group)
            .build();
        ProductAttribute b = ProductAttribute.builder()
            .id(10L)
            .name("Size")
            .productAttributeGroup(group)
            .build();

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void productAttributeValue_shouldLinkProductAndAttribute() {
        Product product = Product.builder().id(2L).build();
        ProductAttribute attribute = ProductAttribute.builder().id(3L).build();
        ProductAttributeValue pav = new ProductAttributeValue();
        pav.setId(4L);
        pav.setProduct(product);
        pav.setProductAttribute(attribute);
        pav.setValue("Red");

        assertThat(pav.getProduct()).isEqualTo(product);
        assertThat(pav.getProductAttribute()).isEqualTo(attribute);
        assertThat(pav.getValue()).isEqualTo("Red");
    }

    @Test
    void productAttributeTemplate_shouldReferenceTemplateAndAttribute() {
        ProductAttribute attribute = ProductAttribute.builder().id(5L).build();
        ProductTemplate template = ProductTemplate.builder().id(6L).name("Default").build();
        ProductAttributeTemplate pat = ProductAttributeTemplate.builder()
            .id(7L)
            .productAttribute(attribute)
            .productTemplate(template)
            .displayOrder(1)
            .build();

        assertThat(pat.getProductAttribute()).isEqualTo(attribute);
        assertThat(pat.getProductTemplate()).isEqualTo(template);
        assertThat(pat.getDisplayOrder()).isEqualTo(1);
    }

    @Test
    void productTemplateEquality_shouldUseId() {
        ProductTemplate t1 = ProductTemplate.builder().id(8L).name("Template A").build();
        ProductTemplate t2 = ProductTemplate.builder().id(8L).name("Template B").build();
        ProductTemplate t3 = ProductTemplate.builder().id(9L).name("Template C").build();

        assertThat(t1).isEqualTo(t2);
        assertThat(t1.hashCode()).isEqualTo(t2.hashCode());
        assertThat(t1).isNotEqualTo(t3);
    }

    @Test
    void productAttributeGroupEquality_shouldUseId() {
        ProductAttributeGroup g1 = new ProductAttributeGroup();
        g1.setId(20L);
        ProductAttributeGroup g2 = new ProductAttributeGroup();
        g2.setId(20L);
        ProductAttributeGroup g3 = new ProductAttributeGroup();
        g3.setId(21L);

        assertThat(g1).isEqualTo(g2);
        assertThat(g1.hashCode()).isEqualTo(g2.hashCode());
        assertThat(g1).isNotEqualTo(g3);
    }

    @Test
    void productAttributeGroupEquality_shouldReturnFalseWhenIdNull() {
        ProductAttributeGroup g1 = new ProductAttributeGroup();
        ProductAttributeGroup g2 = new ProductAttributeGroup();
        g2.setId(30L);

        assertThat(g1).isNotEqualTo(g2);
    }
}
