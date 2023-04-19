package me.sa1zer.cdrsystem.common.payload.dto;

import lombok.Builder;

@Builder
public record BillingDto(String phoneNumber, double balance) {

}
