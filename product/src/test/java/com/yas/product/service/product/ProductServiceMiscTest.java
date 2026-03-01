package com.yas.product.service.product;

import com.yas.product.model.Product;
import com.yas.product.model.ProductImage;
import com.yas.product.model.enumeration.FilterExistInWhSelection;
import com.yas.product.viewmodel.product.ProductInfoVm;
import com.yas.product.viewmodel.product.ProductListVm;
import com.yas.product.viewmodel.product.ProductSlugGetVm;
import com.yas.product.viewmodel.product.ProductVariationGetVm;
import com.yas.product.viewmodel.product.ProductsGetVm;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class ProductServiceMiscTest extends ProductServiceTestBase {

    @Test
    void getProductByIds_withValidIds_returnsProducts() {
        List<Long> productIds = List.of(1L, 2L);
        when(productRepository.findAllByIdIn(productIds)).thenReturn(List.of(testProduct));

        List<ProductListVm> result = productService.getProductByIds(productIds);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().name()).isEqualTo("Test Product");
    }

    @Test
    void getProductVariationsByParentId_whenNoOptions_returnsEmptyList() {
        testProduct.setHasOptions(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        List<ProductVariationGetVm> result = productService.getProductVariationsByParentId(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void getProductsByMultiQuery_returnsPagedThumbnails() {
        Product prod = Product.builder().id(1L).name("P").slug("p").price(10.0).thumbnailMediaId(9L).build();
        Page<Product> page = new PageImpl<>(List.of(prod), PageRequest.of(0, 2), 1);

        when(productRepository.findByProductNameAndCategorySlugAndPriceBetween("p", "c", 1.0, 20.0, PageRequest.of(0, 2))).thenReturn(page);
        when(mediaService.getMedia(9L)).thenReturn(testMediaVm);

        ProductsGetVm result = productService.getProductsByMultiQuery(0, 2, "p", "c", 1.0, 20.0);

        assertThat(result.productContent()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    void getProductsForWarehouse_returnsProducts() {
        List<Long> productIds = List.of(1L);
        when(productRepository.findProductForWarehouse("test", "sku", productIds, "ALL")).thenReturn(List.of(testProduct));

        List<ProductInfoVm> result = productService.getProductsForWarehouse("test", "sku", productIds, FilterExistInWhSelection.ALL);

        assertThat(result).hasSize(1);
    }

    @Test
    void getProductSlug_whenProductExists_returnsSlug() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        ProductSlugGetVm result = productService.getProductSlug(1L);

        assertThat(result).isNotNull();
        assertThat(result.slug()).isEqualTo("test-product");
    }

    @Test
    void getProductSlug_whenVariation_returnsParentSlug() {
        Product parentProduct = Product.builder().id(2L).slug("parent-product").build();
        testProduct.setParent(parentProduct);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        ProductSlugGetVm result = productService.getProductSlug(1L);

        assertThat(result).isNotNull();
        assertThat(result.slug()).isEqualTo("parent-product");
        assertThat(result.productVariantId()).isEqualTo(1L);
    }

    @Test
    void setProductImages_whenNoneExisting_addsNewImages() {
        Product product = Product.builder().id(12L).build();
        product.setProductImages(null);

        List<ProductImage> result = productService.setProductImages(List.of(5L, 6L), product);

        assertThat(result).hasSize(2);
        assertThat(result.stream().map(ProductImage::getImageId)).containsExactlyInAnyOrder(5L, 6L);
    }
}
