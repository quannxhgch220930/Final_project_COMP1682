package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.dto.request.UpdateProfileRequest;
import com.ecommerce.backend.dto.response.AddressResponse;
import com.ecommerce.backend.dto.response.UserResponse;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.exception.AppException;
import com.ecommerce.backend.exception.ErrorCode;
import com.ecommerce.backend.repository.AddressRepository;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Override
    public UserResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setFullName(request.getFullName());
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        return toResponse(userRepository.save(user));
    }

    // ── Helper ──────────────────────────────────────────
    private UserResponse toResponse(User user) {
        AddressResponse defaultAddress = addressRepository
                .findByUserIdAndIsDefaultTrue(user.getId())
                .map(a -> AddressResponse.builder()
                        .id(a.getId())
                        .receiverName(a.getReceiverName())
                        .receiverPhone(a.getReceiverPhone())
                        .province(a.getProvince())
                        .district(a.getDistrict())
                        .ward(a.getWard())
                        .street(a.getStreet())
                        .fullAddress(a.getStreet() + ", " + a.getWard()
                                + ", " + a.getDistrict() + ", " + a.getProvince())
                        .isDefault(true)
                        .build())
                .orElse(null);

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
                .defaultAddress(defaultAddress)
                .build();
    }
}