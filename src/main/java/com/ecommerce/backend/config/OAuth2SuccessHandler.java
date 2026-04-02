package com.ecommerce.backend.config;

import com.ecommerce.backend.common.JwtUtil;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.entity.enums.Provider;
import com.ecommerce.backend.entity.enums.Role;
import com.ecommerce.backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        String email    = oauth2User.getAttribute("email");
        String fullName = oauth2User.getAttribute("name");

        // Tìm hoặc tạo user
        User user = userRepository.findByEmail(email).orElseGet(() ->
                userRepository.save(User.builder()
                        .email(email)
                        .fullName(fullName != null ? fullName : "")
                        .passwordHash("")
                        .role(Role.USER)
                        .provider(Provider.GOOGLE)
                        .isVerified(true)   // Google đã xác thực rồi
                        .build())
        );

        // Tạo JWT token
        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getFullName(),
                user.getId());

        // Redirect về frontend kèm token
        response.sendRedirect("http://localhost:5173/oauth2/callback?token=" + token);
    }
}
