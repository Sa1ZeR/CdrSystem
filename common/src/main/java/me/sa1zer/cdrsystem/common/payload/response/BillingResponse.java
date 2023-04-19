package me.sa1zer.cdrsystem.common.payload.response;

import me.sa1zer.cdrsystem.common.payload.dto.BillingDto;

import java.util.List;

public record BillingResponse(List<BillingDto> numbers) {

}
