package com.yas.product.service;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.product.model.Category;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.category.CategoryGetVm;
import com.yas.product.viewmodel.category.CategoryPostVm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceAdditionalTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private MediaService mediaService;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void create_whenParentMissing_shouldThrowBadRequest() {
        CategoryPostVm vm = new CategoryPostVm("Name", "slug", "desc", 9L,
            "metaK", "metaD", (short) 1, true, 1L);
        when(categoryRepository.findExistedName("Name", null)).thenReturn(null);
        when(categoryRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> categoryService.create(vm));
    }

    @Test
    void create_withParent_shouldPersistParent() {
        Category parent = new Category();
        parent.setId(2L);
        CategoryPostVm vm = new CategoryPostVm("Name", "slug", "desc", 2L,
            "metaK", "metaD", (short) 1, true, 1L);
        when(categoryRepository.findExistedName("Name", null)).thenReturn(null);
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(parent));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Category saved = categoryService.create(vm);

        assertThat(saved.getParent()).isEqualTo(parent);
        assertThat(saved.getName()).isEqualTo("Name");
    }

    @Test
    void update_whenParentIsItself_shouldThrowBadRequest() {
        Category existing = new Category();
        existing.setId(1L);
        CategoryPostVm vm = new CategoryPostVm("Name", "slug", "desc", 1L,
            "metaK", "metaD", (short) 1, true, null);
        when(categoryRepository.findExistedName("Name", 1L)).thenReturn(null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThrows(BadRequestException.class, () -> categoryService.update(vm, 1L));
    }

    @Test
    void update_whenParentNotFound_shouldThrowBadRequest() {
        Category existing = new Category();
        existing.setId(1L);
        CategoryPostVm vm = new CategoryPostVm("Name", "slug", "desc", 5L,
            "metaK", "metaD", (short) 1, true, null);
        when(categoryRepository.findExistedName("Name", 1L)).thenReturn(null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> categoryService.update(vm, 1L));
    }

    @Test
    void update_whenClearingParent_shouldRemoveReference() {
        Category existing = new Category();
        existing.setId(1L);
        Category parent = new Category();
        parent.setId(10L);
        existing.setParent(parent);
        CategoryPostVm vm = new CategoryPostVm("Name", "slug", "desc", null,
            "metaK", "metaD", (short) 1, true, null);
        when(categoryRepository.findExistedName("Name", 1L)).thenReturn(null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));

        categoryService.update(vm, 1L);

        assertThat(existing.getParent()).isNull();
    }

    @Test
    void getCategoryByIds_shouldMapEntities() {
        Category category = new Category();
        category.setId(3L);
        category.setName("Cat");
        when(categoryRepository.findAllById(List.of(3L))).thenReturn(List.of(category));

        List<CategoryGetVm> vms = categoryService.getCategoryByIds(List.of(3L));

        assertThat(vms).hasSize(1);
        assertThat(vms.getFirst().name()).isEqualTo("Cat");
    }

    @Test
    void getTopNthCategories_shouldDelegateToRepository() {
        Pageable pageable = PageRequest.of(0, 2);
        when(categoryRepository.findCategoriesOrderedByProductCount(pageable)).thenReturn(List.of("A", "B"));

        List<String> result = categoryService.getTopNthCategories(2);

        assertThat(result).containsExactly("A", "B");
    }

    @Test
    void getPageableCategories_shouldBuildVm() {
        Category c1 = new Category();
        c1.setId(1L);
        c1.setName("C1");
        Page<Category> page = new PageImpl<>(List.of(c1));
        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(page);

        assertThat(categoryService.getPageableCategories(0, 1).categoryContent())
            .extracting(CategoryGetVm::name)
            .containsExactly("C1");
    }

    @Test
    void getCategories_shouldPopulateImageUrlWhenPresent() {
        Category c1 = new Category();
        c1.setId(1L);
        c1.setName("Cat One");
        c1.setSlug("cat-one");
        c1.setImageId(9L);
        when(categoryRepository.findByNameContainingIgnoreCase("cat"))
            .thenReturn(List.of(c1));
        when(mediaService.getMedia(9L)).thenReturn(new NoFileMediaVm(9L, "c", "f", "type", "http://img"));

        List<CategoryGetVm> vms = categoryService.getCategories("cat");

        assertThat(vms).hasSize(1);
        assertThat(vms.getFirst().categoryImage().url()).isEqualTo("http://img");
    }

    @Test
    void create_whenNameDuplicated_shouldThrow() {
        CategoryPostVm vm = new CategoryPostVm("Dup", "slug", "desc", null,
            "metaK", "metaD", (short) 1, true, null);
        when(categoryRepository.findExistedName(eq("Dup"), eq(null))).thenReturn(new Category());

        assertThrows(DuplicatedException.class, () -> categoryService.create(vm));
    }
}
