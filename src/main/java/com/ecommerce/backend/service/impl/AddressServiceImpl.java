package com.ecommerce.backend.service.impl;

import com.ecommerce.backend.dto.request.AddressRequest;
import com.ecommerce.backend.dto.response.AddressResponse;
import com.ecommerce.backend.entity.Address;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.exception.AppException;
import com.ecommerce.backend.exception.ErrorCode;
import com.ecommerce.backend.repository.AddressRepository;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.service.Interface.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository    userRepository;

    @Override
    public List<AddressResponse> getAll(Long userId) {
        return addressRepository.findByUserIdOrderByIsDefaultDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public AddressResponse create(Long userId, AddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Nếu là địa chỉ mặc định → reset các địa chỉ khác
        if (request.isDefault()) {
            addressRepository.resetDefault(userId);
        }

        // Nếu chưa có địa chỉ nào → tự động set mặc định
        boolean hasAddress = !addressRepository
                .findByUserIdOrderByIsDefaultDesc(userId).isEmpty();

        Address address = Address.builder()
                .user(user)
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .province(request.getProvince())
                .district(request.getDistrict())
                .ward(request.getWard())
                .street(request.getStreet())
                .isDefault(!hasAddress || request.isDefault())
                .build();

        return toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public AddressResponse update(Long userId, Long addressId, AddressRequest request) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        // Kiểm tra địa chỉ có thuộc user không
        if (!address.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        if (request.isDefault()) {
            addressRepository.resetDefault(userId);
        }

        address.setReceiverName(request.getReceiverName());
        address.setReceiverPhone(request.getReceiverPhone());
        address.setProvince(request.getProvince());
        address.setDistrict(request.getDistrict());
        address.setWard(request.getWard());
        address.setStreet(request.getStreet());
        address.setDefault(request.isDefault());

        return toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public void delete(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        if (!address.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        addressRepository.delete(address);
    }

    @Override
    @Transactional
    public AddressResponse setDefault(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

        if (!address.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.FORBIDDEN);
        }

        addressRepository.resetDefault(userId);
        address.setDefault(true);
        return toResponse(addressRepository.save(address));
    }

    // ── Helper ──────────────────────────────────────────
    private AddressResponse toResponse(Address a) {
        String fullAddress = a.getStreet() + ", " + a.getWard()
                + ", " + a.getDistrict() + ", " + a.getProvince();
        return AddressResponse.builder()
                .id(a.getId())
                .receiverName(a.getReceiverName())
                .receiverPhone(a.getReceiverPhone())
                .province(a.getProvince())
                .district(a.getDistrict())
                .ward(a.getWard())
                .street(a.getStreet())
                .fullAddress(fullAddress)
                .isDefault(a.isDefault())
                .build();
    }
}