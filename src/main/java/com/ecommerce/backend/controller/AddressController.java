package com.ecommerce.backend.controller;

import com.ecommerce.backend.common.ApiResponse;
import com.ecommerce.backend.common.UserHelper;
import com.ecommerce.backend.dto.request.AddressRequest;
import com.ecommerce.backend.dto.response.AddressResponse;
import com.ecommerce.backend.service.Interface.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;
    private final UserHelper userHelper;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                addressService.getAll(userHelper.getUserId(userDetails))));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AddressRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Them dia chi thanh cong",
                        addressService.create(userHelper.getUserId(userDetails), request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> update(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Cap nhat dia chi thanh cong",
                addressService.update(userHelper.getUserId(userDetails), id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        addressService.delete(userHelper.getUserId(userDetails), id);
        return ResponseEntity.ok(ApiResponse.success("Xoa dia chi thanh cong", null));
    }
}
