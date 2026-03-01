package com.yas.product.service.product;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.product.ProductFeatureGetVm;
import com.yas.product.viewmodel.product.ProductListGetFromCategoryVm;
import com.yas.product.viewmodel.product.ProductListGetVm;
import com.yas.product.viewmodel.product.ProductListVm;
import com.yas.product.viewmodel.product.ProductThumbnailGetVm;
import com.yas.product.viewmodel.product.ProductThumbnailVm;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ProductServiceListingTest extends ProductServiceTestBase {

    @Test
    void getProductsWithFilter_returnsProducts() {
        Page<Product> productPage = new PageImpl<>(List.of(testProduct), PageRequest.of(0, 10), 1);
        when(productRepository.getProductsWithFilter("test", "brand", PageRequest.of(0, 10))).thenReturn(productPage);

        ProductListGetVm result = productService.getProductsWithFilter(0, 10, "test", "brand");

        assertThat(result).isNotNull();
        assertThat(result.productContent()).hasSize(1);
        assertThat(result.pageNo()).isEqualTo(0);
        assertThat(result.pageSize()).isEqualTo(10);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(1);
        assertThat(result.isLast()).isTrue();
    }

    @Test
    void getProductsWithFilter_whenNoResults_returnsEmptyList() {
        Page<Product> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(productRepository.getProductsWithFilter("nonexistent", "brand", PageRequest.of(0, 10))).thenReturn(emptyPage);

        ProductListGetVm result = productService.getProductsWithFilter(0, 10, "nonexistent", "brand");

        assertThat(result).isNotNull();
        assertThat(result.productContent()).isEmpty();
        assertThat(result.totalElements()).isEqualTo(0);
    }

    @Test
    void getLatestProducts_withPositiveCount_returnsLatestProducts() {
        List<Product> products = List.of(testProduct);
        when(productRepository.getLatestProducts(any())).thenReturn(products);

        List<ProductListVm> result = productService.getLatestProducts(5);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().name()).isEqualTo("Test Product");
    }

    @Test
    void getLatestProducts_withZeroOrNegativeCount_returnsEmpty() {
        assertThat(productService.getLatestProducts(0)).isEmpty();
        assertThat(productService.getLatestProducts(-1)).isEmpty();
    }

    @Test
    void getLatestProducts_whenNoProducts_returnsEmpty() {
        when(productRepository.getLatestProducts(any())).thenReturn(Collections.emptyList());

        List<ProductListVm> result = productService.getLatestProducts(5);

        assertThat(result).isEmpty();
    }

    @Test
    void getProductsByBrand_whenBrandExists_returnsProducts() {
        testProduct.setThumbnailMediaId(1L);
        List<Product> products = List.of(testProduct);

        when(brandRepository.findBySlug("test-brand")).thenReturn(Optional.of(testBrand));
        when(productRepository.findAllByBrandAndIsPublishedTrueOrderByIdAsc(testBrand)).thenReturn(products);
        when(mediaService.getMedia(1L)).thenReturn(testMediaVm);

        List<ProductThumbnailVm> result = productService.getProductsByBrand("test-brand");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(1L);
    }

    @Test
    void getProductsByBrand_whenBrandMissing_throwsNotFound() {
        when(brandRepository.findBySlug("nonexistent")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> productService.getProductsByBrand("nonexistent"));

        assertThat(exception.getMessage()).contains("Brand nonexistent is not found");
    }

    @Test
    void getProductsFromCategory_whenCategoryExists_returnsProducts() {
        testProduct.setThumbnailMediaId(1L);
        ProductCategory categoryLink = ProductCategory.builder().product(testProduct).category(testCategory).build();
        Page<ProductCategory> page = new PageImpl<>(List.of(categoryLink), PageRequest.of(0, 10), 1);

        when(categoryRepository.findBySlug("test-category")).thenReturn(Optional.of(testCategory));
        when(productCategoryRepository.findAllByCategory(PageRequest.of(0, 10), testCategory)).thenReturn(page);
        when(mediaService.getMedia(1L)).thenReturn(testMediaVm);

        ProductListGetFromCategoryVm result = productService.getProductsFromCategory(0, 10, "test-category");

        assertThat(result.productContent()).hasSize(1);
        assertThat(result.productContent().getFirst().id()).isEqualTo(1L);
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    void getProductsFromCategory_whenCategoryMissing_throwsNotFound() {
        when(categoryRepository.findBySlug("missing-category")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getProductsFromCategory(0, 10, "missing-category"));
    }

    @Test
    void getFeaturedProductsById_whenChildThumbnailMissing_usesParent() {
        Product parent = Product.builder().id(2L).name("Parent").slug("parent").price(30.0).thumbnailMediaId(20L).build();
        Product child = Product.builder().id(1L).name("Child").slug("child").price(10.0).thumbnailMediaId(10L).parent(parent).build();

        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(child));
        when(mediaService.getMedia(10L)).thenReturn(new NoFileMediaVm(10L, "c", "c", "image/png", ""));
        when(productRepository.findById(2L)).thenReturn(Optional.of(parent));
        when(mediaService.getMedia(20L)).thenReturn(new NoFileMediaVm(20L, "p", "p", "image/png", "parent-url"));

        List<ProductThumbnailGetVm> result = productService.getFeaturedProductsById(List.of(1L));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().thumbnailUrl()).isEqualTo("parent-url");
    }

    @Test
    void getListFeaturedProducts_returnsThumbnails() {
        Product featured = Product.builder().id(5L).name("Featured").slug("featured").price(50.0).thumbnailMediaId(5L).build();
        Page<Product> page = new PageImpl<>(List.of(featured), PageRequest.of(0, 2), 1);

        when(productRepository.getFeaturedProduct(PageRequest.of(0, 2))).thenReturn(page);
        when(mediaService.getMedia(5L)).thenReturn(new NoFileMediaVm(5L, "f", "f", "image/png", "f-url"));

        ProductFeatureGetVm result = productService.getListFeaturedProducts(0, 2);

        assertThat(result.productList()).hasSize(1);
        assertThat(result.productList().getFirst().thumbnailUrl()).isEqualTo("f-url");
        assertThat(result.totalPage()).isEqualTo(1);
    }
}
