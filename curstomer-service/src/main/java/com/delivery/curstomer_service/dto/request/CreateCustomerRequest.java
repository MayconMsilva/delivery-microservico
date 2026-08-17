package com.delivery.curstomer_service.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateCustomerRequest(

        @NotBlank
        String name,

        @NotBlank
        String phone,

        @NotBlank
        String address
) {
}
