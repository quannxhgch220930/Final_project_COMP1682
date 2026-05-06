package com.ecommerce.backend.dto.request;

import com.ecommerce.backend.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrderRequest {

    @NotBlank(message = "Tên người nhận không được để trống")
    @Size(max = 100)
    private String receiverName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|\\+84)[0-9]{8,9}$",
            message = "Số điện thoại không hợp lệ")
    private String receiverPhone;

    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    private String receiverAddress;

    private String couponCode;

    private String note;

    private PaymentMethod paymentMethod;
}
