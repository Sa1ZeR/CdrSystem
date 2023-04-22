package me.sa1zer.cdrsystem.common.payload.response;

import me.sa1zer.cdrsystem.common.payload.dto.BillingDataDto;

import java.util.List;

public record ReportUpdateDataResponse(List<BillingDataDto> data) {
}
