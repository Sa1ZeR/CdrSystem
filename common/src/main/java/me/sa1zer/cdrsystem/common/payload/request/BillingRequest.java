package me.sa1zer.cdrsystem.common.payload.request;

import jakarta.validation.constraints.NotBlank;

public record BillingRequest(@NotBlank(message = "Action must be not empty") String action) {

}
