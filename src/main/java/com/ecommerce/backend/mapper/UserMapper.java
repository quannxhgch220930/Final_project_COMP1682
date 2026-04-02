package com.ecommerce.backend.mapper;

import com.ecommerce.backend.dto.response.AddressResponse;
import com.ecommerce.backend.dto.response.UserResponse;
import com.ecommerce.backend.entity.Address;
import com.ecommerce.backend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toUserResponse(User user) {
        return toUserResponse(user, null);
    }

    public UserResponse toUserResponse(User user, Address defaultAddress) {
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
                .defaultAddress(toAddressResponse(defaultAddress))
                .build();
    }

    private AddressResponse toAddressResponse(Address address) {
        if (address == null) {
            return null;
        }

        String fullAddress = address.getStreet() + ", " + address.getWard()
                + ", " + address.getDistrict() + ", " + address.getProvince();

        return AddressResponse.builder()
                .id(address.getId())
                .receiverName(address.getReceiverName())
                .receiverPhone(address.getReceiverPhone())
                .province(address.getProvince())
                .district(address.getDistrict())
                .ward(address.getWard())
                .street(address.getStreet())
                .fullAddress(fullAddress)
                .isDefault(address.isDefault())
                .build();
    }
}
