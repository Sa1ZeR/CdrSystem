package me.sa1zer.cdrsystem.crm.payload.response;

import lombok.Builder;
import me.sa1zer.cdrsystem.common.payload.dto.ReportPayloadDto;

import java.util.List;

@Builder
public record AbonentReportResponse(Long id, String numberPhone, String tariffIndex, List<ReportPayloadDto> payload,
                                    double totalCost, double currentBalance, String monetaryUnit) {

}
