package com.yas.product.service.product;

import com.yas.product.viewmodel.product.ProductQuantityPostVm;
import com.yas.product.viewmodel.product.ProductQuantityPutVm;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductServiceInventoryTest extends ProductServiceTestBase {

    @Test
    void updateProductQuantity_withValidData_updatesSuccessfully() {
        List<ProductQuantityPostVm> quantityUpdates = List.of(new ProductQuantityPostVm(1L, 150L));
        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(testProduct));
        when(productRepository.saveAll(anyList())).thenReturn(List.of(testProduct));

        assertDoesNotThrow(() -> productService.updateProductQuantity(quantityUpdates));

        verify(productRepository).findAllByIdIn(List.of(1L));
        verify(productRepository).saveAll(anyList());
    }

    @Test
    void subtractStockQuantity_withValidData_subtractsSuccessfully() {
        testProduct.setStockQuantity(100L);
        testProduct.setStockTrackingEnabled(true);
        List<ProductQuantityPutVm> quantityUpdates = List.of(new ProductQuantityPutVm(1L, 10L));

        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(testProduct));
        when(productRepository.saveAll(anyList())).thenReturn(List.of(testProduct));

        assertDoesNotThrow(() -> productService.subtractStockQuantity(quantityUpdates));

        verify(productRepository).findAllByIdIn(List.of(1L));
        verify(productRepository).saveAll(anyList());
    }

    @Test
    void restoreStockQuantity_withValidData_restoresSuccessfully() {
        testProduct.setStockQuantity(90L);
        testProduct.setStockTrackingEnabled(true);
        List<ProductQuantityPutVm> quantityUpdates = List.of(new ProductQuantityPutVm(1L, 10L));

        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(testProduct));
        when(productRepository.saveAll(anyList())).thenReturn(List.of(testProduct));

        assertDoesNotThrow(() -> productService.restoreStockQuantity(quantityUpdates));

        verify(productRepository).findAllByIdIn(List.of(1L));
        verify(productRepository).saveAll(anyList());
    }
}
