package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.dto.response.ProductImageResponse;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.ProductImage;
import com.ecommerce.backend.exception.AppException;
import com.ecommerce.backend.exception.ErrorCode;
import com.ecommerce.backend.repository.ProductImageRepository;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.service.Interface.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CloudinaryMediaService cloudinaryMediaService;

    @Override
    @Transactional
    public ProductImageResponse uploadProductImage(Long productId, MultipartFile file, boolean isPrimary, Integer sortOrder) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Map result = cloudinaryMediaService.uploadImage(file, "ecommerce/products/" + productId);

        if (isPrimary) {
            productImageRepository.clearPrimaryByProductId(productId);
        }

        ProductImage productImage = ProductImage.builder()
                .product(product)
                .url((String) result.get("secure_url"))
                .publicId((String) result.get("public_id"))
                .isPrimary(isPrimary)
                .sortOrder(sortOrder != null ? sortOrder : 0)
                .build();

        ProductImage savedImage = productImageRepository.save(productImage);
        return ProductImageResponse.builder()
                .id(savedImage.getId())
                .url(savedImage.getUrl())
                .isPrimary(savedImage.isPrimary())
                .sortOrder(savedImage.getSortOrder())
                .build();
    }

    @Override
    @Transactional
    public void deleteProductImage(Long imageId) {
        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_IMAGE_NOT_FOUND));

        cloudinaryMediaService.deleteImage(productImage.getPublicId());
        productImageRepository.delete(productImage);
    }
}
