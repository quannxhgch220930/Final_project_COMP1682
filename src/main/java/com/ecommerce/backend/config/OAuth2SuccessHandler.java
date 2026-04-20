package com.ecommerce.backend.config;

import com.ecommerce.backend.common.JwtUtil;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.entity.enums.Provider;
import com.ecommerce.backend.entity.enums.Role;
import com.ecommerce.backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Value("${app.frontend.base-url:http://localhost:5173}")
    private String frontendBaseUrl;

    @Value("${app.frontend.oauth2-callback-path:/oauth2/callback}")
    private String oauth2CallbackPath;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        String email = oauth2User.getAttribute("email");
        String fullName = oauth2User.getAttribute("name");

        User user = userRepository.findByEmail(email).orElseGet(() ->
                userRepository.save(User.builder()
                        .email(email)
                        .fullName(fullName != null ? fullName : "")
                        .passwordHash("")
                        .role(Role.USER)
                        .provider(Provider.GOOGLE)
                        .isVerified(true)
                        .build())
        );

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getFullName(),
                user.getId());

        String redirectUrl = UriComponentsBuilder
                .fromUriString(frontendBaseUrl + oauth2CallbackPath)
                .queryParam("token", token)
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }
}
