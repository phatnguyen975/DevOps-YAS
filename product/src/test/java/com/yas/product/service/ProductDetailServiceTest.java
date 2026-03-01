package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.model.ProductImage;
import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductOptionCombination;
import com.yas.product.model.attribute.ProductAttributeValue;
import com.yas.product.model.enumeration.DimensionUnit;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.product.ProductDetailInfoVm;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductDetailServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private MediaService mediaService;
    @Mock
    private ProductOptionCombinationRepository productOptionCombinationRepository;

    @InjectMocks
    private ProductDetailService productDetailService;

    private NoFileMediaVm defaultMediaVm;

    @BeforeEach
    void setUp() {
        defaultMediaVm = new NoFileMediaVm(1L, "default-caption", "default-file.jpg", "image/jpeg", "http://test.com/default.jpg");
        lenient().when(mediaService.getMedia(anyLong())).thenReturn(defaultMediaVm);
    }

    // Test getProductDetailById successfully with published product
    @Test
    void test_get_product_detail_by_id_successfully_with_published_product() {
        // Arrange
        Long productId = 1L;
        Product product = createSampleProduct();
        product.setPublished(true);
        
        NoFileMediaVm mediaVm = new NoFileMediaVm(1L, "image.jpg", "image/jpeg", "100KB", "http://test.com/image.jpg");

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(anyLong())).thenReturn(mediaVm);

        // Act
        ProductDetailInfoVm result = productDetailService.getProductDetailById(productId);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("Test Product", result.getName());
        assertEquals("test-product", result.getSlug());
        assertEquals(99.99, result.getPrice());
        assertEquals(1L, result.getBrandId());
        assertEquals("Test Brand", result.getBrandName());
        assertTrue(result.getIsAllowedToOrder());
        assertTrue(result.getIsPublished());
        verify(productRepository).findById(productId);
    }

    // Test getProductDetailById with non-existent product throws exception  
    @Test
    void test_get_product_detail_by_id_with_nonexistent_product_throws_exception() {
        Long productId = 999L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productDetailService.getProductDetailById(productId));
    }

    // Test getProductDetailById with unpublished product throws exception
    @Test
    void test_get_product_detail_by_id_with_unpublished_product_throws_exception() {
        Long productId = 1L;
        Product product = createSampleProduct();
        product.setPublished(false); // Product not published

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(NotFoundException.class, () -> productDetailService.getProductDetailById(productId));
    }

    // Test getProductDetailById with product having variations
    @Test
    void test_get_product_detail_by_id_with_product_having_variations() {
        Long productId = 1L;
        Product product = createSampleProductWithVariations();
        NoFileMediaVm mediaVm = new NoFileMediaVm(1L, "image.jpg", "image/jpeg", "100KB", "http://test.com/image.jpg");
        List<ProductOptionCombination> combinations = createSampleProductOptionCombinations();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(anyLong())).thenReturn(mediaVm);
        when(productOptionCombinationRepository.findAllByProduct(any(Product.class))).thenReturn(combinations);

        ProductDetailInfoVm result = productDetailService.getProductDetailById(productId);

        assertNotNull(result);
        assertEquals(1, result.getVariations().size());
        assertNotNull(result.getVariations().get(0).options());
        verify(productOptionCombinationRepository, times(1)).findAllByProduct(any(Product.class));
        verify(mediaService, atLeastOnce()).getMedia(anyLong());
    }

    // Test getProductDetailById with product having no brand
    @Test
    void test_get_product_detail_by_id_with_product_having_no_brand() {
        Long productId = 1L;
        Product product = createSampleProduct();
        product.setBrand(null); // No brand
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(productId);

        assertNotNull(result);
        assertNull(result.getBrandId());
        assertNull(result.getBrandName());
    }

    // Test getProductDetailById with product having no categories
    @Test
    void test_get_product_detail_by_id_with_product_having_no_categories() {
        Long productId = 1L;
        Product product = createSampleProduct();
        product.setProductCategories(null); // No categories
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(productId);

        assertNotNull(result);
        assertTrue(result.getCategories().isEmpty());
    }

    // Test getProductDetailById with product having no thumbnail
    @Test
    void test_get_product_detail_by_id_with_product_having_no_thumbnail() {
        Long productId = 1L;
        Product product = createSampleProduct();
        product.setThumbnailMediaId(null); // No thumbnail
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(productId);

        assertNotNull(result);
        assertNull(result.getThumbnail());
    }

    // Test getProductDetailById with product having no images
    @Test
    void test_get_product_detail_by_id_with_product_having_no_images() {
        Long productId = 1L;
        Product product = createSampleProduct();
        product.setProductImages(null); // No images
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(productId);

        assertNotNull(result);
        assertTrue(result.getProductImages().isEmpty());
    }

    // Test getProductDetailById with product having attribute values
    @Test 
    void test_get_product_detail_by_id_with_product_having_attribute_values() {
        Long productId = 1L;
        Product product = createSampleProductWithAttributes();
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(productId);

        assertNotNull(result);
        assertEquals(1, result.getAttributeValues().size());
    }

    // Helper methods to create sample objects

    private Product createSampleProduct() {
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Test Brand");

        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");

        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategory(category);

        ProductImage productImage = new ProductImage();
        productImage.setImageId(1L);

        Product product = Product.builder()
                .id(1L)
                .name("Test Product")
                .slug("test-product")
                .shortDescription("Short description")
                .description("Long description")
                .specification("Specifications")
                .sku("SKU123")
                .gtin("GTIN123")
                .price(99.99)
                .hasOptions(false)
                .isAllowedToOrder(true)
                .isPublished(true)
                .isFeatured(false)
                .isVisibleIndividually(true)
                .stockTrackingEnabled(false)
                .weight(1.5)
                .dimensionUnit(DimensionUnit.CM)
                .length(10.0)
                .width(5.0)
                .height(3.0)
                .metaTitle("Meta Title")
                .metaKeyword("Meta Keywords")
                .metaDescription("Meta Description")
                .thumbnailMediaId(1L)
                .taxClassId(1L)
                .build();

        product.setBrand(brand);
        product.setProductCategories(List.of(productCategory));
        product.setProductImages(List.of(productImage));
        product.setAttributeValues(new ArrayList<>());
        product.setProducts(new ArrayList<>());

        return product;
    }

    private Product createSampleProductWithVariations() {
        Product product = createSampleProduct();
        product.setHasOptions(true);

        // Create a variation product
        Product variation = Product.builder()
                .id(2L)
                .name("Test Product - Red")
                .slug("test-product-red")
                .sku("SKU123-RED")
                .gtin("GTIN123-RED")
                .price(109.99)
                .isPublished(true)
                .thumbnailMediaId(2L)
                .build();
        
        // Create ProductImage with proper imageId
        ProductImage productImage = new ProductImage();
        productImage.setImageId(2L);
        variation.setProductImages(List.of(productImage));

        product.setProducts(List.of(variation));
        return product;
    }

    private Product createSampleProductWithAttributes() {
        Product product = createSampleProduct();
        
        ProductAttributeValue attributeValue = new ProductAttributeValue();
        attributeValue.setId(1L);
        attributeValue.setValue("Red");
        
        // Create mock ProductAttribute
        com.yas.product.model.attribute.ProductAttribute productAttribute = 
            new com.yas.product.model.attribute.ProductAttribute();
        productAttribute.setId(1L);
        productAttribute.setName("Color");
        
        attributeValue.setProductAttribute(productAttribute);
        
        product.setAttributeValues(List.of(attributeValue));
        return product;
    }

    private List<ProductOptionCombination> createSampleProductOptionCombinations() {
        ProductOption option = new ProductOption();
        option.setId(1L);
        option.setName("Color");

        ProductOptionCombination combination = new ProductOptionCombination();
        combination.setId(1L);
        combination.setProductOption(option);
        combination.setValue("Red");

        return List.of(combination);
    }
}