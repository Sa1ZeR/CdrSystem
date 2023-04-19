package me.sa1zer.cdrsystem.hrs.service.calculation;

import me.sa1zer.cdrsystem.common.object.enums.TariffType;
import me.sa1zer.cdrsystem.common.payload.dto.CdrPlusDto;
import me.sa1zer.cdrsystem.common.payload.dto.ReportDto;

import java.util.List;

public interface BaseCalculator {

    List<ReportDto> getReportData(List<CdrPlusDto> cdrPlusDto);

    double calculate(List<ReportDto> reportData);

    TariffType getType();
}
