package com.ecommerce.backend.dto.response;

import com.ecommerce.backend.entity.enums.Provider;
import com.ecommerce.backend.entity.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Long          id;
    private String        email;
    private String        fullName;
    private String        avatarUrl;
    private Role          role;
    private boolean       isVerified;
    private boolean       isLocked;
    private Provider      provider;
    private LocalDateTime createdAt;
    private AddressResponse defaultAddress;
}