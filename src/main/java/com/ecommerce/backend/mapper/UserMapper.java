package com.ecommerce.backend.mapper;

import com.ecommerce.backend.dto.response.UserResponse;
import com.ecommerce.backend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .isVerified(user.isVerified())
                .isLocked(user.isLocked())
                .provider(user.getProvider())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
