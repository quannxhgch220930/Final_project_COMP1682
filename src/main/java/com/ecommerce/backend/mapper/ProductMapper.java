package com.ecommerce.backend.mapper;

import com.ecommerce.backend.dto.response.ProductImageResponse;
import com.ecommerce.backend.dto.response.ProductResponse;
import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = { CategoryMapper.class })
public interface ProductMapper {

    @Mapping(target = "category", source = "category")
    @Mapping(target = "images",   source = "images")
    ProductResponse toResponse(Product product);

    ProductImageResponse toImageResponse(ProductImage image);
}