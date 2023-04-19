package me.sa1zer.cdrsystem.common.payload.request;

import lombok.Builder;
import me.sa1zer.cdrsystem.common.payload.dto.BillingDataDto;
import me.sa1zer.cdrsystem.common.payload.dto.ReportDto;

import java.util.List;

public record ReportUpdateDataRequest(List<BillingDataDto> data) {
}
