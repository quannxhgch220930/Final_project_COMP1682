package com.ecommerce.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AddressRequest {

    @NotBlank(message = "Ten nguoi nhan khong duoc de trong")
    private String receiverName;

    @NotBlank(message = "So dien thoai khong duoc de trong")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "So dien thoai khong hop le")
    private String receiverPhone;

    @NotBlank(message = "Tinh/Thanh pho khong duoc de trong")
    private String province;

    @NotBlank(message = "Quan/Huyen khong duoc de trong")
    private String district;

    @NotBlank(message = "Phuong/Xa khong duoc de trong")
    private String ward;

    @NotBlank(message = "Dia chi cu the khong duoc de trong")
    private String street;
}