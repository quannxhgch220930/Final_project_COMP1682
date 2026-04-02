package com.ecommerce.backend.service;

import com.ecommerce.backend.common.PageResponse;
import com.ecommerce.backend.dto.request.CategoryRequest;
import com.ecommerce.backend.dto.request.ProductRequest;
import com.ecommerce.backend.dto.response.CategoryResponse;
import com.ecommerce.backend.dto.response.ProductResponse;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    // ── Category ──
    CategoryResponse createCategory(CategoryRequest request);
    List<CategoryResponse> getAllCategories();
    CategoryResponse updateCategory(Long id, CategoryRequest request);
    void deleteCategory(Long id);

    // ── Product ──
    PageResponse<ProductResponse> getProducts(String search, Long categoryId,
                                              BigDecimal minPrice, BigDecimal maxPrice,
                                              BigDecimal minRating, String sort,
                                              int page, int size);
    ProductResponse getProductById(Long id);
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
}