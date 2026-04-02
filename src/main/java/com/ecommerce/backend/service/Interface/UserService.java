package com.ecommerce.backend.service.Interface;

import com.ecommerce.backend.dto.request.UpdateProfileRequest;
import com.ecommerce.backend.dto.response.UserResponse;

public interface UserService {
    UserResponse getProfile(Long userId);
    UserResponse updateProfile(Long userId, UpdateProfileRequest request);
}