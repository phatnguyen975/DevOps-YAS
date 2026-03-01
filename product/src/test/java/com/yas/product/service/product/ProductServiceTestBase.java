package com.yas.product.service.product;

import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.model.ProductImage;
import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductOptionValue;
import com.yas.product.model.enumeration.DimensionUnit;
import com.yas.product.repository.BrandRepository;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.repository.ProductCategoryRepository;
import com.yas.product.repository.ProductImageRepository;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductOptionRepository;
import com.yas.product.repository.ProductOptionValueRepository;
import com.yas.product.repository.ProductRelatedRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.service.MediaService;
import com.yas.product.service.ProductService;
import com.yas.product.viewmodel.NoFileMediaVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
abstract class ProductServiceTestBase {

    @Mock
    protected ProductRepository productRepository;
    @Mock
    protected MediaService mediaService;
    @Mock
    protected BrandRepository brandRepository;
    @Mock
    protected CategoryRepository categoryRepository;
    @Mock
    protected ProductCategoryRepository productCategoryRepository;
    @Mock
    protected ProductImageRepository productImageRepository;
    @Mock
    protected ProductOptionRepository productOptionRepository;
    @Mock
    protected ProductOptionValueRepository productOptionValueRepository;
    @Mock
    protected ProductOptionCombinationRepository productOptionCombinationRepository;
    @Mock
    protected ProductRelatedRepository productRelatedRepository;

    @InjectMocks
    protected ProductService productService;

    protected Product testProduct;
    protected Brand testBrand;
    protected Category testCategory;
    protected ProductImage testProductImage;
    protected ProductCategory testProductCategory;
    protected NoFileMediaVm testMediaVm;
    protected ProductOption testProductOption;
    protected Category secondCategory;

    @BeforeEach
    void setUpBase() {
        testBrand = new Brand();
        testBrand.setId(1L);
        testBrand.setName("Test Brand");
        testBrand.setSlug("test-brand");

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Test Category");
        testCategory.setSlug("test-category");

        testProduct = Product.builder()
            .id(1L)
            .name("Test Product")
            .slug("test-product")
            .sku("TEST-SKU-001")
            .gtin("1234567890123")
            .price(99.99)
            .shortDescription("Short description")
            .description("Full description")
            .specification("Specification")
            .isPublished(true)
            .isFeatured(false)
            .isAllowedToOrder(true)
            .isVisibleIndividually(true)
            .stockTrackingEnabled(true)
            .hasOptions(false)
            .weight(1.5)
            .length(10.0)
            .width(5.0)
            .height(3.0)
            .dimensionUnit(DimensionUnit.CM)
            .brand(testBrand)
            .thumbnailMediaId(1L)
            .taxClassId(1L)
            .stockQuantity(100L)
            .metaTitle("Meta Title")
            .metaKeyword("Meta Keyword")
            .metaDescription("Meta Description")
            .build();

        testProductImage = ProductImage.builder()
            .id(1L)
            .imageId(1L)
            .product(testProduct)
            .build();

        testProductCategory = ProductCategory.builder()
            .id(1L)
            .product(testProduct)
            .category(testCategory)
            .build();

        testMediaVm = new NoFileMediaVm(1L, "Test Image", "image.jpg", "image/jpeg", "http://example.com/image.jpg");
        testProductOption = buildProductOption(10L, "Option");
        secondCategory = buildCategory(2L, "Category 2", "cat-2");
    }

    protected ProductOption buildProductOption(Long id, String name) {
        ProductOption option = new ProductOption();
        option.setId(id);
        option.setName(name);
        return option;
    }

    protected Category buildCategory(Long id, String name, String slug) {
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setSlug(slug);
        return category;
    }

    protected Brand buildBrand(Long id, String name, String slug) {
        Brand brand = new Brand();
        brand.setId(id);
        brand.setName(name);
        brand.setSlug(slug);
        return brand;
    }

    protected ProductOptionValue buildProductOptionValue(Product product, ProductOption option, int displayOrder, String value) {
        return ProductOptionValue.builder()
            .id(1L)
            .product(product)
            .productOption(option)
            .displayType("TEXT")
            .displayOrder(displayOrder)
            .value(value)
            .build();
    }
}
