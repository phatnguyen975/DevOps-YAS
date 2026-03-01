package com.yas.product.service;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.product.model.Brand;
import com.yas.product.model.Product;
import com.yas.product.repository.BrandRepository;
import com.yas.product.viewmodel.brand.BrandPostVm;
import com.yas.product.viewmodel.brand.BrandVm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BrandServiceAdditionalTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    @Test
    void delete_whenBrandHasProducts_shouldThrowBadRequest() {
        Brand brand = new Brand();
        brand.setId(5L);
        brand.setProducts(List.of(new Product()));
        when(brandRepository.findById(5L)).thenReturn(Optional.of(brand));

        assertThrows(BadRequestException.class, () -> brandService.delete(5L));
    }

    @Test
    void delete_whenBrandEmpty_shouldDeleteSuccessfully() {
        Brand brand = new Brand();
        brand.setId(6L);
        brand.setProducts(new ArrayList<>());
        when(brandRepository.findById(6L)).thenReturn(Optional.of(brand));

        brandService.delete(6L);

        verify(brandRepository).deleteById(6L);
    }

    @Test
    void create_whenNameAlreadyExists_shouldThrowDuplicatedException() {
        BrandPostVm vm = new BrandPostVm("Name", "slug", true);
        when(brandRepository.findExistedName("Name", null)).thenReturn(new Brand());

        assertThrows(DuplicatedException.class, () -> brandService.create(vm));
    }

    @Test
    void getBrandsByIds_shouldMapEntitiesToVm() {
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Nike");
        brand.setSlug("nike");
        when(brandRepository.findAllById(List.of(1L))).thenReturn(List.of(brand));

        List<BrandVm> result = brandService.getBrandsByIds(List.of(1L));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().name()).isEqualTo("Nike");
    }
}
