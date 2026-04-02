package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.dto.request.UpdateProfileRequest;
import com.ecommerce.backend.dto.response.UserResponse;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.exception.AppException;
import com.ecommerce.backend.exception.ErrorCode;
import com.ecommerce.backend.mapper.UserMapper;
import com.ecommerce.backend.repository.AddressRepository;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.service.Interface.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository    userRepository;
    private final AddressRepository addressRepository;
    private final UserMapper        userMapper;

    @Override
    public UserResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(
                user,
                addressRepository.findByUserIdAndIsDefaultTrue(user.getId()).orElse(null));
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

        User savedUser = userRepository.save(user);
        return userMapper.toUserResponse(
                savedUser,
                addressRepository.findByUserIdAndIsDefaultTrue(savedUser.getId()).orElse(null));
    }
}
