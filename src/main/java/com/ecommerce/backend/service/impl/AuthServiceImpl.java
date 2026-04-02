package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.common.JwtUtil;
import com.ecommerce.backend.dto.request.*;
import com.ecommerce.backend.dto.response.AuthResponse;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.exception.AppException;
import com.ecommerce.backend.exception.ErrorCode;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.service.Interface.AuthService;
import com.ecommerce.backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil         jwtUtil;
    private final EmailService    emailService;

    @Value("${app.dev-mode:false}")
    private boolean devMode;

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String verifyToken = devMode ? null : UUID.randomUUID().toString();

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .isVerified(devMode)
                .verifyToken(verifyToken)
                .verifyTokenExpiry(devMode ? null : LocalDateTime.now().plusHours(24))
                .build();

        userRepository.save(user);

        if (!devMode) {
            emailService.sendVerifyEmail(user.getEmail(), user.getFullName(), verifyToken);
        }
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (!user.isVerified()) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_VERIFIED);
        }

        if (user.isLocked()) {
            throw new AppException(ErrorCode.ACCOUNT_LOCKED);
        }

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getFullName(),
                user.getId());

        return AuthResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .accessToken(token)
                .tokenType("Bearer")
                .build();
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByVerifyToken(token)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));

        if (user.getVerifyTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }

        user.setVerified(true);
        user.setVerifyToken(null);
        user.setVerifyTokenExpiry(null);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void resendVerifyEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (user.isVerified()) return;

        String newToken = UUID.randomUUID().toString();
        user.setVerifyToken(newToken);
        user.setVerifyTokenExpiry(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        emailService.sendVerifyEmail(user.getEmail(), user.getFullName(), newToken);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Tạo reset token
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        // Gửi email
        emailService.sendResetPasswordEmail(user.getEmail(), user.getFullName(), token);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(request.getToken())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_TOKEN));

        // Kiểm tra token hết hạn
        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }

        // Đổi mật khẩu
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        // Kiểm tra mật khẩu mới và xác nhận
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        // Kiểm tra mật khẩu mới không trùng mật khẩu cũ
        if (passwordEncoder.matches(request.getNewPassword(), user.getPasswordHash())) {
            throw new AppException(ErrorCode.PASSWORD_SAME_AS_OLD);
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
