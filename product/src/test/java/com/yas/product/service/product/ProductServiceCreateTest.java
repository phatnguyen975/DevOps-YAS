package com.yas.product.service.product;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.InternalServerErrorException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Product;
import com.yas.product.model.enumeration.DimensionUnit;
import com.yas.product.utils.Constants;
import com.yas.product.viewmodel.product.ProductGetDetailVm;
import com.yas.product.viewmodel.product.ProductOptionValueDisplay;
import com.yas.product.viewmodel.product.ProductPostVm;
import com.yas.product.viewmodel.product.ProductVariationPostVm;
import com.yas.product.viewmodel.productoption.ProductOptionValuePostVm;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductServiceCreateTest extends ProductServiceTestBase {

    @Test
    void createProduct_withMinimalData_createsSuccessfully() {
        ProductPostVm productPostVm = new ProductPostVm(
            "New Product",
            "new-product",
            1L,
            List.of(1L),
            "Short desc",
            "Long description",
            "Specification",
            "NEW-SKU-001",
            "1234567890124",
            1.5,
            DimensionUnit.CM,
            10.0,
            5.0,
            3.0,
            99.99,
            true,
            true,
            false,
            true,
            true,
            "Meta Title",
            "Meta Keywords",
            "Meta Description",
            1L,
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList(),
            1L
        );

        Product savedProduct = Product.builder().id(2L).name("New Product").build();

        when(productRepository.findBySlugAndIsPublishedTrue("new-product")).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue("NEW-SKU-001")).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue("1234567890124")).thenReturn(Optional.empty());
        when(brandRepository.findById(1L)).thenReturn(Optional.of(testBrand));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(testCategory));
        when(productCategoryRepository.saveAll(anyList())).thenReturn(List.of(testProductCategory));
        when(productImageRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        ProductGetDetailVm result = productService.createProduct(productPostVm);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("New Product");
        verify(productRepository).save(any(Product.class));
        verify(categoryRepository).findAllById(List.of(1L));
        verify(productCategoryRepository).saveAll(anyList());
    }

    @Test
    void createProduct_whenSlugExists_throwsDuplicatedException() {
        ProductPostVm productPostVm = new ProductPostVm(
            "New Product", "existing-slug", 1L, List.of(1L),
            "Short desc", "Long desc", "Spec", "NEW-SKU", "1234567890124",
            1.5, DimensionUnit.CM, 10.0, 5.0, 3.0, 99.99,
            true, true, false, true, true,
            "Meta Title", "Meta Keywords", "Meta Description", 1L,
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList(), Collections.emptyList(), 1L
        );

        when(productRepository.findBySlugAndIsPublishedTrue("existing-slug")).thenReturn(Optional.of(testProduct));

        DuplicatedException exception = assertThrows(DuplicatedException.class, () -> productService.createProduct(productPostVm));

        assertThat(exception.getMessage()).contains("already existed or is duplicated");
    }

    @Test
    void createProduct_whenSkuExists_throwsDuplicatedException() {
        ProductPostVm productPostVm = new ProductPostVm(
            "New Product", "new-slug", 1L, List.of(1L),
            "Short desc", "Long desc", "Spec", "EXISTING-SKU", "1234567890124",
            1.5, DimensionUnit.CM, 10.0, 5.0, 3.0, 99.99,
            true, true, false, true, true,
            "Meta Title", "Meta Keywords", "Meta Description", 1L,
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList(), Collections.emptyList(), 1L
        );

        when(productRepository.findBySlugAndIsPublishedTrue("new-slug")).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue("EXISTING-SKU")).thenReturn(Optional.of(testProduct));

        DuplicatedException exception = assertThrows(DuplicatedException.class, () -> productService.createProduct(productPostVm));

        assertThat(exception.getMessage()).contains("already existed or is duplicated");
    }

    @Test
    void createProduct_whenLengthLessThanWidth_throwsBadRequest() {
        ProductPostVm productPostVm = new ProductPostVm(
            "New Product", "new-slug", 1L, List.of(1L),
            "Short desc", "Long desc", "Spec", "NEW-SKU", "1234567890124",
            1.5, DimensionUnit.CM, 5.0, 10.0, 3.0, 99.99,
            true, true, false, true, true,
            "Meta Title", "Meta Keywords", "Meta Description", 1L,
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList(), Collections.emptyList(), 1L
        );

        BadRequestException exception = assertThrows(BadRequestException.class, () -> productService.createProduct(productPostVm));

        assertThat(exception.getMessage()).contains("length greater than width");
    }

    @Test
    void createProduct_whenBrandNotFound_throwsNotFoundException() {
        ProductPostVm productPostVm = new ProductPostVm(
            "New Product", "new-slug", 999L, List.of(1L),
            "Short desc", "Long desc", "Spec", "NEW-SKU", "1234567890124",
            1.5, DimensionUnit.CM, 10.0, 5.0, 3.0, 99.99,
            true, true, false, true, true,
            "Meta Title", "Meta Keywords", "Meta Description", 1L,
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList(), Collections.emptyList(), 1L
        );

        when(productRepository.findBySlugAndIsPublishedTrue("new-slug")).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue("NEW-SKU")).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue("1234567890124")).thenReturn(Optional.empty());
        when(brandRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> productService.createProduct(productPostVm));

        assertThat(exception.getMessage()).contains("Brand 999 is not found");
    }

    @Test
    void createProduct_whenVariationDuplicates_throwsDuplicatedException() {
        ProductVariationPostVm var1 = new ProductVariationPostVm("Var", "dup-slug", "SKU1", "GTIN1", 10.0, 1L, Collections.emptyList(), Map.of());
        ProductVariationPostVm var2 = new ProductVariationPostVm("Var 2", "dup-slug", "SKU2", "GTIN2", 11.0, 1L, Collections.emptyList(), Map.of());

        ProductPostVm productPostVm = new ProductPostVm(
            "Prod", "prod-slug", 1L, List.of(1L),
            "Short", "Desc", "Spec", "SKU-MAIN", "GTIN-MAIN",
            1.0, DimensionUnit.CM, 5.0, 4.0, 3.0, 20.0,
            true, true, false, true, true,
            "Meta", "MetaK", "MetaD", 1L,
            Collections.emptyList(), List.of(var1, var2), Collections.emptyList(),
            Collections.emptyList(), Collections.emptyList(), 1L
        );

        when(productRepository.findBySlugAndIsPublishedTrue("prod-slug")).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue("SKU-MAIN")).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue("GTIN-MAIN")).thenReturn(Optional.empty());

        assertThrows(DuplicatedException.class, () -> productService.createProduct(productPostVm));
    }

    @Test
    void createProduct_whenOptionIdNotFound_throwsBadRequest() {
        ProductOptionValueDisplay optionDisplay = ProductOptionValueDisplay.builder()
            .productOptionId(99L)
            .displayType("TEXT")
            .displayOrder(1)
            .value("V1")
            .build();

        ProductVariationPostVm variation = new ProductVariationPostVm(
            "Var", "prod2-var", "SKU2-V", "GTIN2-V", 9.0, 1L, Collections.emptyList(), Map.of(99L, "V1"));

        ProductOptionValuePostVm optionValuePostVm = new ProductOptionValuePostVm(99L, "TEXT", 1, List.of("V1"));

        ProductPostVm productPostVm = new ProductPostVm(
            "Prod", "prod2", 1L, List.of(1L),
            "Short", "Desc", "Spec", "SKU2", "GTIN2",
            1.0, DimensionUnit.CM, 5.0, 4.0, 3.0, 20.0,
            true, true, false, true, true,
            "Meta", "MetaK", "MetaD", 1L,
            Collections.emptyList(), List.of(variation), List.of(optionValuePostVm),
            List.of(optionDisplay), Collections.emptyList(), 1L
        );

        when(productRepository.findBySlugAndIsPublishedTrue("prod2")).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue("SKU2")).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue("GTIN2")).thenReturn(Optional.empty());
        when(brandRepository.findById(1L)).thenReturn(Optional.of(testBrand));
        when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(testCategory));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(productCategoryRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        when(productImageRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        when(productOptionRepository.findAllByIdIn(List.of(99L))).thenReturn(Collections.emptyList());

        assertThrows(BadRequestException.class, () -> productService.createProduct(productPostVm));
    }

    @Test
    void createProduct_whenSavedVariationMissing_throwsInternalServerError() {
        ProductVariationPostVm variationVm = new ProductVariationPostVm(
            "Var", "var-slug", "SKU-V", "GTIN-V", 10.0, 1L, Collections.emptyList(), Map.of(10L, "V1"));
        ProductOptionValuePostVm optionValuePostVm = new ProductOptionValuePostVm(10L, "TEXT", 1, List.of("V1"));
        ProductOptionValueDisplay optionDisplay = ProductOptionValueDisplay.builder()
            .productOptionId(10L)
            .displayType("TEXT")
            .displayOrder(1)
            .value("V1")
            .build();

        ProductPostVm productPostVm = new ProductPostVm(
            "Prod", "prod-combo", 1L, List.of(1L),
            "Short", "Desc", "Spec", "SKU2", "GTIN2",
            1.0, DimensionUnit.CM, 5.0, 4.0, 3.0, 20.0,
            true, true, false, true, true,
            "Meta", "MetaK", "MetaD", 1L,
            Collections.emptyList(), List.of(variationVm), List.of(optionValuePostVm),
            List.of(optionDisplay), Collections.emptyList(), 1L
        );

        when(productRepository.findBySlugAndIsPublishedTrue("prod-combo")).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue("SKU2")).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue("GTIN2")).thenReturn(Optional.empty());
        when(brandRepository.findById(1L)).thenReturn(Optional.of(testBrand));
        when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(testCategory));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productCategoryRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        when(productImageRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        when(productOptionRepository.findAllByIdIn(List.of(10L))).thenReturn(List.of(testProductOption));
        when(productOptionValueRepository.saveAll(anyList())).thenReturn(List.of(buildProductOptionValue(testProduct, testProductOption, 1, "V1")));
        when(productRepository.saveAll(anyList())).thenReturn(List.of(Product.builder().slug("different-slug").build()));

        assertThrows(InternalServerErrorException.class, () -> productService.createProduct(productPostVm));
    }

    @Test
    void createProduct_whenOptionValueMissing_throwsBadRequest() {
        ProductVariationPostVm variationVm = new ProductVariationPostVm(
            "Var", "var2", "SKU-V2", "GTIN-V2", 10.0, 1L, Collections.emptyList(), Map.of(10L, "V1"));
        ProductOptionValuePostVm optionValuePostVm = new ProductOptionValuePostVm(10L, "TEXT", 1, List.of("V1"));
        ProductOptionValueDisplay optionDisplay = ProductOptionValueDisplay.builder()
            .productOptionId(10L)
            .displayType("TEXT")
            .displayOrder(1)
            .value("V1")
            .build();

        ProductPostVm productPostVm = new ProductPostVm(
            "Prod", "prod-option", 1L, List.of(1L),
            "Short", "Desc", "Spec", "SKU3", "GTIN3",
            1.0, DimensionUnit.CM, 5.0, 4.0, 3.0, 20.0,
            true, true, false, true, true,
            "Meta", "MetaK", "MetaD", 1L,
            Collections.emptyList(), List.of(variationVm), List.of(optionValuePostVm),
            List.of(optionDisplay), Collections.emptyList(), 1L
        );

        when(productRepository.findBySlugAndIsPublishedTrue("prod-option")).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue("SKU3")).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue("GTIN3")).thenReturn(Optional.empty());
        when(brandRepository.findById(1L)).thenReturn(Optional.of(testBrand));
        when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(testCategory));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productCategoryRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        when(productImageRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        when(productOptionRepository.findAllByIdIn(List.of(10L))).thenReturn(List.of(testProductOption));
        when(productOptionValueRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        when(productRepository.saveAll(anyList())).thenReturn(List.of(Product.builder().slug("var2").build()));

        assertThrows(BadRequestException.class, () -> productService.createProduct(productPostVm));
    }

    @Test
    void createProduct_withRelatedIds_createsRelations() {
        ProductPostVm productPostVm = new ProductPostVm(
            "Prod", "prod-rel", 1L, List.of(1L),
            "Short", "Desc", "Spec", "SKU-REL", "GTIN-REL",
            1.0, DimensionUnit.CM, 5.0, 4.0, 3.0, 20.0,
            true, true, false, true, true,
            "Meta", "MetaK", "MetaD", 1L,
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList(), List.of(2L, 3L), 1L
        );

        Product related1 = Product.builder().id(2L).build();
        Product related2 = Product.builder().id(3L).build();

        when(productRepository.findBySlugAndIsPublishedTrue("prod-rel")).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue("SKU-REL")).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue("GTIN-REL")).thenReturn(Optional.empty());
        when(brandRepository.findById(1L)).thenReturn(Optional.of(testBrand));
        when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(testCategory));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);
        when(productCategoryRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        when(productImageRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        when(productRepository.findAllById(anyList())).thenReturn(Collections.emptyList());
        when(productRepository.findAllById(List.of(2L, 3L))).thenReturn(List.of(related1, related2));
        when(productRelatedRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        productService.createProduct(productPostVm);

        verify(productRelatedRepository).saveAll(anyList());
    }
}
