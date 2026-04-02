package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.request.AddressRequest;
import com.ecommerce.backend.dto.response.AddressResponse;

import java.util.List;

public interface AddressService {
    List<AddressResponse> getAll(Long userId);
    AddressResponse create(Long userId, AddressRequest request);
    AddressResponse update(Long userId, Long addressId, AddressRequest request);
    void delete(Long userId, Long addressId);
    AddressResponse setDefault(Long userId, Long addressId);
}