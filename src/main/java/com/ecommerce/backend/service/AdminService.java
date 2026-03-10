package com.ecommerce.backend.service;

import com.ecommerce.backend.common.PageResponse;
import com.ecommerce.backend.dto.request.UpdateLockRequest;
import com.ecommerce.backend.dto.request.UpdateRoleRequest;
import com.ecommerce.backend.dto.response.UserResponse;

public interface AdminService {
    PageResponse<UserResponse> getAllUsers(int page, int size);
    UserResponse getUserById(Long id);
    UserResponse updateLock(Long id, UpdateLockRequest request);
    UserResponse updateRole(Long id, UpdateRoleRequest request);
    void deleteUser(Long id);
}