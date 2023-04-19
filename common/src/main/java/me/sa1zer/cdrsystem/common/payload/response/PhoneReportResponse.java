package me.sa1zer.cdrsystem.common.payload.response;

import me.sa1zer.cdrsystem.common.payload.dto.ReportDto;

import java.util.List;

public record PhoneReportResponse(List<ReportDto> reports, double totalCost) {


}
