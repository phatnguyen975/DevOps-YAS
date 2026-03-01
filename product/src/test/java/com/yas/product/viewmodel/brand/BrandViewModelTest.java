package com.yas.product.viewmodel.brand;

import com.yas.product.model.Brand;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BrandViewModelTest {

    @Test
    void brandVm_fromModel_shouldMapFields() {
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Brand 1");
        brand.setSlug("brand-1");
        brand.setPublished(true);

        BrandVm vm = BrandVm.fromModel(brand);

        assertThat(vm.id()).isEqualTo(1L);
        assertThat(vm.name()).isEqualTo("Brand 1");
        assertThat(vm.slug()).isEqualTo("brand-1");
        assertThat(vm.isPublish()).isTrue();
    }

    @Test
    void brandPostVm_toModel_shouldPopulateEntity() {
        BrandPostVm postVm = new BrandPostVm("Brand X", "brand-x", true);

        Brand brand = postVm.toModel();

        assertThat(brand.getName()).isEqualTo("Brand X");
        assertThat(brand.getSlug()).isEqualTo("brand-x");
        assertThat(brand.isPublished()).isTrue();
    }

    @Test
    void brandListGetVm_shouldExposePaginationFields() {
        List<BrandVm> brands = List.of(new BrandVm(1L, "B1", "b1", true));
        BrandListGetVm vm = new BrandListGetVm(brands, 0, 10, 1, 1, true);

        assertThat(vm.brandContent()).containsExactlyElementsOf(brands);
        assertThat(vm.pageNo()).isEqualTo(0);
        assertThat(vm.pageSize()).isEqualTo(10);
        assertThat(vm.totalElements()).isEqualTo(1);
        assertThat(vm.totalPages()).isEqualTo(1);
        assertThat(vm.isLast()).isTrue();
    }
}
