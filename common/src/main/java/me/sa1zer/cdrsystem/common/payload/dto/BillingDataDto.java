package me.sa1zer.cdrsystem.common.payload.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record BillingDataDto(String phone, double totalPrice, List<ReportDto> reports) {
}
