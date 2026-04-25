package com.ecommerce.backend.service.Interface;

import com.ecommerce.backend.dto.response.ProductImageResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProductImageService {
    ProductImageResponse uploadProductImage(Long productId, MultipartFile file, boolean isPrimary, Integer sortOrder);
    void deleteProductImage(Long imageId);
}
