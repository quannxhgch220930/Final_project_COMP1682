package com.ecommerce.backend.service.Interface;

import com.ecommerce.backend.dto.request.*;
import com.ecommerce.backend.dto.response.AuthResponse;

public interface AuthService {
    void     register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void     verifyEmail(String token);
    void     resendVerifyEmail(String email);
    void forgotPassword(ForgotPasswordRequest request);
    void resetPassword(ResetPasswordRequest request);
    void changePassword(Long userId, ChangePasswordRequest request);
}