package com.yas.product.service;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.model.ProductImage;
import com.yas.product.model.ProductRelated;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.model.attribute.ProductAttributeValue;
import com.yas.product.repository.BrandRepository;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.repository.ProductCategoryRepository;
import com.yas.product.repository.ProductImageRepository;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductOptionRepository;
import com.yas.product.repository.ProductOptionValueRepository;
import com.yas.product.repository.ProductRelatedRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.product.ProductDetailGetVm;
import com.yas.product.viewmodel.product.ProductEsDetailVm;
import com.yas.product.viewmodel.product.ProductsGetVm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceDetailTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private MediaService mediaService;
    @Mock
    private BrandRepository brandRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductCategoryRepository productCategoryRepository;
    @Mock
    private ProductImageRepository productImageRepository;
    @Mock
    private ProductOptionRepository productOptionRepository;
    @Mock
    private ProductOptionValueRepository productOptionValueRepository;
    @Mock
    private ProductOptionCombinationRepository productOptionCombinationRepository;
    @Mock
    private ProductRelatedRepository productRelatedRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void getProductDetail_shouldMapImagesBrandAndAttributes() {
        Brand brand = new Brand();
        brand.setName("BrandX");

        Category category = new Category();
        category.setId(10L);
        category.setName("CatOne");

        ProductCategory productCategory = ProductCategory.builder()
            .category(category)
            .build();

        ProductImage image = ProductImage.builder().imageId(9L).build();

        ProductAttributeGroup group = new ProductAttributeGroup();
        group.setId(1L);
        group.setName("Specs");

        ProductAttribute attributeWithGroup = ProductAttribute.builder()
            .id(1L)
            .name("Color")
            .productAttributeGroup(group)
            .build();
        ProductAttribute attributeNoGroup = ProductAttribute.builder()
            .id(2L)
            .name("Weight")
            .productAttributeGroup(null)
            .build();

        ProductAttributeValue pav1 = new ProductAttributeValue();
        pav1.setProductAttribute(attributeWithGroup);
        pav1.setValue("Red");

        ProductAttributeValue pav2 = new ProductAttributeValue();
        pav2.setProductAttribute(attributeNoGroup);
        pav2.setValue("1kg");

        Product product = Product.builder()
            .id(1L)
            .name("Prod")
            .slug("prod")
            .price(10.0)
            .brand(brand)
            .thumbnailMediaId(5L)
            .isAllowedToOrder(true)
            .isPublished(true)
            .isFeatured(false)
            .isVisibleIndividually(true)
            .hasOptions(false)
            .build();
        product.setProductImages(List.of(image));
        product.setProductCategories(List.of(productCategory));
        product.setAttributeValues(Arrays.asList(pav1, pav2));

        when(productRepository.findBySlugAndIsPublishedTrue("prod")).thenReturn(Optional.of(product));
        when(mediaService.getMedia(anyLong())).thenReturn(new NoFileMediaVm(5L, "c", "f", "t", "http://img"));

        ProductDetailGetVm vm = productService.getProductDetail("prod");

        assertThat(vm.brandName()).isEqualTo("BrandX");
        assertThat(vm.productCategories()).containsExactly("CatOne");
        assertThat(vm.productImageMediaUrls()).containsExactly("http://img");
        assertThat(vm.productAttributeGroups()).hasSize(2);
        assertThat(vm.productAttributeGroups().stream().map(g -> g.name())).containsExactlyInAnyOrder("Specs", "None group");
    }

    @Test
    void getProductDetail_whenNotFound_shouldThrow() {
        when(productRepository.findBySlugAndIsPublishedTrue("missing")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getProductDetail("missing"));
    }

    @Test
    void getProductEsDetailById_shouldMapFields() {
        Brand brand = new Brand();
        brand.setName("BrandY");
        Category category = new Category();
        category.setName("CatA");
        ProductCategory pc = ProductCategory.builder().category(category).build();

        Product product = Product.builder()
            .id(99L)
            .name("ProdES")
            .slug("prodes")
            .price(20.0)
            .isAllowedToOrder(true)
            .isPublished(true)
            .isFeatured(false)
            .isVisibleIndividually(true)
            .thumbnailMediaId(8L)
            .brand(brand)
            .build();
        product.setProductCategories(List.of(pc));

        ProductAttributeGroup group = new ProductAttributeGroup();
        group.setName("G");
        ProductAttribute attr = ProductAttribute.builder().name("Size").productAttributeGroup(group).build();
        ProductAttributeValue pav = new ProductAttributeValue();
        pav.setProductAttribute(attr);
        pav.setValue("XL");
        product.setAttributeValues(List.of(pav));

        when(productRepository.findById(99L)).thenReturn(Optional.of(product));

        ProductEsDetailVm vm = productService.getProductEsDetailById(99L);

        assertThat(vm.brand()).isEqualTo("BrandY");
        assertThat(vm.thumbnailMediaId()).isEqualTo(8L);
        assertThat(vm.categories()).containsExactly("CatA");
        assertThat(vm.attributes()).containsExactly("Size");
    }

    @Test
    void getRelatedProductsBackoffice_shouldMapParentIds() {
        Product parent = Product.builder().id(500L).build();

        Product child = Product.builder().id(5L).name("Child").slug("child").price(1.0)
            .isAllowedToOrder(true).isPublished(true).isFeatured(false).isVisibleIndividually(true).build();
        child.setParent(parent);

        Product sibling = Product.builder().id(6L).name("Sib").slug("sib").price(2.0)
            .isAllowedToOrder(true).isPublished(true).isFeatured(false).isVisibleIndividually(true).build();

        ProductRelated relWithParent = ProductRelated.builder().product(new Product()).relatedProduct(child).build();
        ProductRelated relNoParent = ProductRelated.builder().product(new Product()).relatedProduct(sibling).build();

        Product product = new Product();
        product.setRelatedProducts(List.of(relWithParent, relNoParent));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        var vms = productService.getRelatedProductsBackoffice(1L);

        assertThat(vms).hasSize(2);
        assertThat(vms.stream().map(vm -> vm.parentId())).containsExactlyInAnyOrder(500L, null);
    }

    @Test
    void getRelatedProductsStorefront_shouldFilterPublished() {
        Product main = Product.builder().id(1L).build();

        Product published = Product.builder().id(2L).name("Pub").slug("pub").price(3.0)
            .thumbnailMediaId(11L).isPublished(true).build();
        Product unpublished = Product.builder().id(3L).name("Unpub").slug("unpub").price(4.0)
            .thumbnailMediaId(12L).isPublished(false).build();

        ProductRelated pr1 = ProductRelated.builder().product(main).relatedProduct(published).build();
        ProductRelated pr2 = ProductRelated.builder().product(main).relatedProduct(unpublished).build();

        Page<ProductRelated> page = new PageImpl<>(List.of(pr1, pr2), PageRequest.of(0, 10), 2);

        when(productRepository.findById(1L)).thenReturn(Optional.of(main));
        when(productRelatedRepository.findAllByProduct(main, PageRequest.of(0, 10))).thenReturn(page);
        when(mediaService.getMedia(eq(11L))).thenReturn(new NoFileMediaVm(11L, "c", "f", "t", "http://thumb"));

        ProductsGetVm vm = productService.getRelatedProductsStorefront(1L, 0, 10);

        assertThat(vm.productContent()).hasSize(1);
        assertThat(vm.productContent().getFirst().slug()).isEqualTo("pub");
        assertThat(vm.totalElements()).isEqualTo(2);
    }
}
