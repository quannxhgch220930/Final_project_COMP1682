package com.ecommerce.backend.dto.response;

import com.ecommerce.backend.entity.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private Long    id;
    private String  email;
    private String  fullName;
    private String  avatarUrl;
    private Role    role;
    private String  accessToken;
    private String  tokenType;
}