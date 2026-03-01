package com.yas.product.service.product;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Product;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.model.attribute.ProductAttributeValue;
import com.yas.product.viewmodel.product.ProductDetailVm;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductServiceGetByIdTest extends ProductServiceTestBase {

    @Test
    void getProductById_whenProductExists_returnsProductDetail() {
        testProduct.setProductImages(List.of(testProductImage));
        testProduct.setProductCategories(List.of(testProductCategory));

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(mediaService.getMedia(1L)).thenReturn(testMediaVm);

        ProductDetailVm result = productService.getProductById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Test Product");
        assertThat(result.slug()).isEqualTo("test-product");
        assertThat(result.sku()).isEqualTo("TEST-SKU-001");
        assertThat(result.price()).isEqualTo(99.99);
        assertThat(result.brandId()).isEqualTo(1L);
        assertThat(result.categories()).hasSize(1);
        assertThat(result.productImageMedias()).hasSize(1);
        assertThat(result.thumbnailMedia()).isNotNull();
        assertThat(result.thumbnailMedia().url()).isEqualTo("http://example.com/image.jpg");

        verify(productRepository).findById(1L);
        verify(mediaService, times(2)).getMedia(1L);
    }

    @Test
    void getProductById_whenBrandIsNull_returnsDetailWithNullBrand() {
        testProduct.setBrand(null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(mediaService.getMedia(1L)).thenReturn(testMediaVm);

        ProductDetailVm result = productService.getProductById(1L);

        assertThat(result).isNotNull();
        assertThat(result.brandId()).isNull();
    }

    @Test
    void getProductById_whenImagesMissing_handlesGracefully() {
        testProduct.setProductImages(null);
        testProduct.setProductCategories(null);
        testProduct.setThumbnailMediaId(null);
        testProduct.setBrand(null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        ProductDetailVm result = productService.getProductById(1L);

        assertThat(result).isNotNull();
        assertThat(result.productImageMedias()).isEmpty();
        assertThat(result.categories()).isEmpty();
        assertThat(result.thumbnailMedia()).isNull();
        assertThat(result.brandId()).isNull();
    }

    @Test
    void getProductById_whenAttributesPresent_includesAttributeValues() {
        ProductAttributeGroup group = new ProductAttributeGroup();
        group.setId(1L);
        group.setName("Group");

        ProductAttribute attribute = ProductAttribute.builder()
            .id(2L)
            .name("Attr")
            .productAttributeGroup(group)
            .build();
        ProductAttributeValue pav = new ProductAttributeValue();
        pav.setProductAttribute(attribute);
        pav.setProduct(testProduct);
        pav.setValue("Red");

        testProduct.setAttributeValues(List.of(pav));
        testProduct.setProductImages(List.of(testProductImage));
        testProduct.setProductCategories(List.of(testProductCategory));

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(mediaService.getMedia(1L)).thenReturn(testMediaVm);

        ProductDetailVm result = productService.getProductById(1L);

        assertThat(result.categories()).hasSize(1);
        assertThat(result.thumbnailMedia()).isNotNull();
    }

    @Test
    void getProductById_whenMissing_throwsNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> productService.getProductById(1L));

        assertThat(exception.getMessage()).contains("Product 1 is not found");
    }

    @Test
    void getProductById_whenProductHasParent_includesParentId() {
        Product parentProduct = Product.builder().id(2L).name("Parent Product").build();
        testProduct.setParent(parentProduct);
        testProduct.setProductImages(List.of(testProductImage));
        testProduct.setProductCategories(List.of(testProductCategory));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(mediaService.getMedia(1L)).thenReturn(testMediaVm);

        ProductDetailVm result = productService.getProductById(1L);

        assertThat(result.parentId()).isEqualTo(2L);
    }
}
