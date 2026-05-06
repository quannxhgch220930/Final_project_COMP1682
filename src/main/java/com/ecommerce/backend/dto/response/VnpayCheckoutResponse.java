package com.ecommerce.backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VnpayCheckoutResponse {
    private OrderResponse order;
    private String paymentUrl;
}
