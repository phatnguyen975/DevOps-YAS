package com.yas.product.service.product;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Product;
import com.yas.product.model.ProductOptionCombination;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductServiceDeleteTest extends ProductServiceTestBase {

    @Test
    void deleteProduct_whenProductExists_marksUnpublished() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        productService.deleteProduct(1L);

        verify(productRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void deleteProduct_whenNotFound_throwsException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> productService.deleteProduct(999L));

        assertThat(exception.getMessage()).contains("Product 999 is not found");
    }

    @Test
    void deleteProduct_whenVariation_deletesOptionCombinations() {
        Product parentProduct = Product.builder().id(2L).name("Parent").build();
        testProduct.setParent(parentProduct);

        ProductOptionCombination combination = new ProductOptionCombination();
        List<ProductOptionCombination> combinations = List.of(combination);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productOptionCombinationRepository.findAllByProduct(testProduct)).thenReturn(combinations);
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        productService.deleteProduct(1L);

        verify(productOptionCombinationRepository).findAllByProduct(testProduct);
        verify(productOptionCombinationRepository).deleteAll(combinations);
        verify(productRepository).save(any(Product.class));
    }
}
