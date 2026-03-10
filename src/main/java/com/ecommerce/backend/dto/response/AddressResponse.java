package com.ecommerce.backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AddressResponse {
    private Long    id;
    private String  receiverName;
    private String  receiverPhone;
    private String  province;
    private String  district;
    private String  ward;
    private String  street;
    private String  fullAddress;
    private boolean isDefault;
}