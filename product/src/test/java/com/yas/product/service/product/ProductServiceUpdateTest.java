package com.yas.product.service.product;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Product;
import com.yas.product.model.ProductImage;
import com.yas.product.model.enumeration.DimensionUnit;
import com.yas.product.viewmodel.product.ProductOptionValueDisplay;
import com.yas.product.viewmodel.product.ProductPutVm;
import com.yas.product.viewmodel.product.ProductVariationPutVm;
import com.yas.product.viewmodel.productoption.ProductOptionValuePutVm;
import org.junit.jupiter.api.Test;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
class ProductServiceUpdateTest extends ProductServiceTestBase {

    @Test
    void updateProduct_withValidData_updatesSuccessfully() {
        ProductPutVm productPutVm = new ProductPutVm(
            "Updated Product",
            "updated-product",
            199.99,
            true,
            true,
            true,
            true,
            true,
            2L,
            new ArrayList<>(List.of(1L, 2L)),
            "Updated short desc",
            "Updated long description",
            "Updated specification",
            "UPDATED-SKU-001",
            "1234567890125",
            2.5,
            DimensionUnit.CM,
            15.0,
            8.0,
            5.0,
            "Updated Meta Title",
            "Updated Meta Keywords",
            "Updated Meta Description",
            2L,
            List.of(2L, 3L),
            Collections.emptyList(),
            List.of(new ProductOptionValuePutVm(10L, "TEXT", 1, List.of("Value"))),
            List.of(ProductOptionValueDisplay.builder().productOptionId(10L).displayType("TEXT").displayOrder(1).value("Value").build()),
            List.of(2L, 3L),
            2L
        );

        Brand updatedBrand = new Brand();
        updatedBrand.setId(2L);
        updatedBrand.setName("Updated Brand");

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.findBySlugAndIsPublishedTrue("updated-product")).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue("UPDATED-SKU-001")).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue("1234567890125")).thenReturn(Optional.empty());
        when(brandRepository.findById(2L)).thenReturn(Optional.of(updatedBrand));
        when(categoryRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(testCategory, secondCategory));
        when(productCategoryRepository.findAllByProductId(1L)).thenReturn(Collections.emptyList());
        when(productCategoryRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        when(productImageRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        lenient().when(productRelatedRepository.findAllById(anyList())).thenReturn(Collections.emptyList());
        lenient().when(productRepository.findAllById(anyList())).thenReturn(Collections.emptyList());
        when(productOptionRepository.findAllByIdIn(List.of(10L))).thenReturn(List.of(testProductOption));
        lenient().when(productOptionValueRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        doNothing().when(productOptionValueRepository).deleteAllByProductId(1L);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        assertDoesNotThrow(() -> productService.updateProduct(1L, productPutVm));

        verify(productRepository).findById(1L);
        verify(brandRepository).findById(2L);
    }

    @Test
    void updateProduct_withNewVariations_createsOptionCombinations() {
        ProductVariationPutVm newVariation = new ProductVariationPutVm(
            null,
            "Var 1",
            "var-1",
            "SKU-V1",
            "GTIN-V1",
            19.99,
            5L,
            Collections.emptyList(),
            Map.of(10L, "Value")
        );

        ProductPutVm productPutVm = new ProductPutVm(
            "Updated Product", "updated-product", 199.99,
            true, true, true, true, true,
            2L, new ArrayList<>(List.of(1L, 2L)),
            "Updated short desc", "Updated long description", "Updated specification",
            "UPDATED-SKU-001", "1234567890125",
            2.5, DimensionUnit.CM, 15.0, 8.0, 5.0,
            "Updated Meta Title", "Updated Meta Keywords", "Updated Meta Description", 2L,
            List.of(2L, 3L),
            List.of(newVariation),
            List.of(new ProductOptionValuePutVm(10L, "TEXT", 1, List.of("Value"))),
            List.of(ProductOptionValueDisplay.builder().productOptionId(10L).displayType("TEXT").displayOrder(1).value("Value").build()),
            List.of(2L, 3L),
            2L
        );

        testProduct.setProductCategories(List.of(testProductCategory));
        Brand updatedBrand = buildBrand(2L, "Updated Brand", "updated-brand");

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.findBySlugAndIsPublishedTrue("updated-product")).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue("UPDATED-SKU-001")).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue("1234567890125")).thenReturn(Optional.empty());
        when(brandRepository.findById(2L)).thenReturn(Optional.of(updatedBrand));
        when(categoryRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(testCategory, secondCategory));
        when(productCategoryRepository.findAllByProductId(1L)).thenReturn(Collections.emptyList());
        when(productCategoryRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        when(productImageRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        lenient().when(productRelatedRepository.findAllById(anyList())).thenReturn(Collections.emptyList());
        lenient().when(productRepository.findAllById(anyList())).thenReturn(Collections.emptyList());
        when(productOptionRepository.findAllByIdIn(List.of(10L))).thenReturn(List.of(testProductOption));
        when(productOptionValueRepository.saveAll(anyList())).thenReturn(List.of(buildProductOptionValue(testProduct, testProductOption, 1, "Value")));
        when(productRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        // Combination save is not used directly in this flow; keep lenient stub to avoid strictness noise.
        when(productOptionCombinationRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        doNothing().when(productOptionValueRepository).deleteAllByProductId(1L);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        assertDoesNotThrow(() -> productService.updateProduct(1L, productPutVm));

        verify(productOptionValueRepository).saveAll(anyList());
        verify(productOptionCombinationRepository).saveAll(anyList());
    }

    @Test
    void updateProduct_whenVariantHasImages_replacesImages() {
        Product existingVariant = Product.builder().id(10L).slug("var-old").sku("SKU-OLD").gtin("GTIN-OLD").thumbnailMediaId(3L).build();
        existingVariant.setProductImages(List.of(ProductImage.builder().imageId(7L).product(existingVariant).build()));
        testProduct.setProducts(new ArrayList<>(List.of(existingVariant)));

        ProductVariationPutVm variantVm = new ProductVariationPutVm(
            10L, "Var New", "var-new", "SKU-NEW", "GTIN-NEW", 20.0, 4L,
            List.of(8L), Map.of(10L, "V")
        );

        ProductPutVm productPutVm = new ProductPutVm(
            "Updated Product", "updated-product", 199.99,
            true, true, true, true, true,
            2L, List.of(1L),
            "Updated short desc", "Updated long description", "Updated specification",
            "UPDATED-SKU-001", "1234567890125",
            2.5, DimensionUnit.CM, 15.0, 8.0, 5.0,
            "Updated Meta Title", "Updated Meta Keywords", "Updated Meta Description", 2L,
            List.of(2L), List.of(variantVm),
            List.of(new ProductOptionValuePutVm(10L, "TEXT", 1, List.of("V"))),
            List.of(ProductOptionValueDisplay.builder().productOptionId(10L).displayType("TEXT").displayOrder(1).value("V").build()),
            List.of(2L), 2L
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.findBySlugAndIsPublishedTrue("updated-product")).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue("UPDATED-SKU-001")).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue("1234567890125")).thenReturn(Optional.empty());
        when(brandRepository.findById(2L)).thenReturn(Optional.of(testBrand));
        when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(testCategory));
        when(productCategoryRepository.findAllByProductId(1L)).thenReturn(Collections.emptyList());
        when(productCategoryRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        when(productImageRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        lenient().when(productRelatedRepository.findAllById(anyList())).thenReturn(Collections.emptyList());
        lenient().when(productRepository.findAllById(anyList())).thenReturn(Collections.emptyList());
        lenient().when(productOptionRepository.findAllByIdIn(List.of(10L))).thenReturn(List.of(testProductOption));
        lenient().when(productOptionValueRepository.saveAll(anyList())).thenReturn(List.of(buildProductOptionValue(testProduct, testProductOption, 1, "V")));
        doNothing().when(productOptionValueRepository).deleteAllByProductId(1L);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        assertDoesNotThrow(() -> productService.updateProduct(1L, productPutVm));

        verify(productImageRepository).deleteByProductId(10L);
        verify(productImageRepository, atLeast(1)).saveAll(anyList());
    }

    @Test
    void updateProduct_whenCategoryMissing_throwsBadRequest() {
        ProductPutVm productPutVm = new ProductPutVm(
            "Updated Product", "updated-product", 199.99,
            true, true, true, true, true,
            2L, List.of(1L),
            "Updated short desc", "Updated long description", "Updated specification",
            "UPDATED-SKU-001", "1234567890125",
            2.5, DimensionUnit.CM, 15.0, 8.0, 5.0,
            "Updated Meta Title", "Updated Meta Keywords", "Updated Meta Description", 2L,
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList(), Collections.emptyList(), 2L
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.findBySlugAndIsPublishedTrue("updated-product")).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue("UPDATED-SKU-001")).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue("1234567890125")).thenReturn(Optional.empty());
        when(brandRepository.findById(2L)).thenReturn(Optional.of(testBrand));
        when(categoryRepository.findAllById(List.of(1L))).thenReturn(Collections.emptyList());

        assertThrows(BadRequestException.class, () -> productService.updateProduct(1L, productPutVm));
    }

    @Test
    void updateProduct_whenOptionsMissing_throwsBadRequest() {
        ProductPutVm productPutVm = new ProductPutVm(
            "Updated Product", "updated-product", 199.99,
            true, true, true, true, true,
            2L, List.of(1L),
            "Updated short desc", "Updated long description", "Updated specification",
            "UPDATED-SKU-001", "1234567890125",
            2.5, DimensionUnit.CM, 15.0, 8.0, 5.0,
            "Updated Meta Title", "Updated Meta Keywords", "Updated Meta Description", 2L,
            Collections.emptyList(), Collections.emptyList(),
            List.of(new ProductOptionValuePutVm(99L, "TEXT", 1, List.of("V"))),
            Collections.emptyList(), Collections.emptyList(), 2L
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.findBySlugAndIsPublishedTrue("updated-product")).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue("UPDATED-SKU-001")).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue("1234567890125")).thenReturn(Optional.empty());
        when(brandRepository.findById(2L)).thenReturn(Optional.of(testBrand));
        when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(testCategory));
        when(productOptionRepository.findAllByIdIn(List.of(99L))).thenReturn(Collections.emptyList());

        assertThrows(BadRequestException.class, () -> productService.updateProduct(1L, productPutVm));
    }

    @Test
    void updateProduct_whenProductNotFound_throwsNotFound() {
        ProductPutVm productPutVm = new ProductPutVm(
            "Updated Product", "updated-slug", 199.99,
            true, true, true, true, true,
            2L, List.of(1L),
            "Updated short", "Updated long", "Updated spec",
            "UPDATED-SKU", "1234567890125",
            2.5, DimensionUnit.CM, 15.0, 8.0, 5.0,
            "Meta Title", "Meta Keywords", "Meta Description", 2L,
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList(), Collections.emptyList(), 2L
        );

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> productService.updateProduct(999L, productPutVm));

        assertThat(exception.getMessage()).contains("Product 999 is not found");
    }

    @Test
    void updateProduct_whenSlugConflicts_throwsDuplicated() {
        Product existing = Product.builder().id(1L).slug("old").sku("SKU1").gtin("GTIN1").build();
        Product conflict = Product.builder().id(2L).slug("dup").build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(productRepository.findBySlugAndIsPublishedTrue("dup")).thenReturn(Optional.of(conflict));

        ProductPutVm vm = new ProductPutVm(
            "Name", "dup", 10.0, true, true, true, true, true,
            null, Collections.emptyList(), "short", "desc", "spec", "SKU1", "GTIN1",
            1.0, DimensionUnit.CM, 10.0, 5.0, 2.0,
            "metaTitle", "metaKeyword", "metaDesc", null,
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(),
            Collections.emptyList(), null, null
        );

        assertThrows(DuplicatedException.class, () -> productService.updateProduct(1L, vm));
    }
}
