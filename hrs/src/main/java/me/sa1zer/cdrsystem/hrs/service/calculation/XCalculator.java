package me.sa1zer.cdrsystem.hrs.service.calculation;

import me.sa1zer.cdrsystem.common.object.enums.TariffType;
import me.sa1zer.cdrsystem.common.payload.dto.CdrPlusDto;
import me.sa1zer.cdrsystem.common.payload.dto.ReportDto;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class XCalculator implements BaseCalculator {
    @Override
    public List<ReportDto> getReportData(List<CdrPlusDto> cdrPlusList) {
        List<ReportDto> reportList = new ArrayList<>();

        for(CdrPlusDto cdrPlusDto : cdrPlusList) {
            long duration = cdrPlusDto.startTime().until(cdrPlusDto.endTime(), ChronoUnit.SECONDS);

            double price = 0;

            reportList.add(new ReportDto(cdrPlusDto.phoneNumber(), cdrPlusDto.startTime(),
                    cdrPlusDto.endTime(), cdrPlusDto.callType(), cdrPlusDto.tariffType(), price, duration));
        }
        return reportList;
    }

    @Override
    public double calculate(List<ReportDto> reportData) {
        return reportData.stream().mapToDouble(ReportDto::cost).sum();
    }

    @Override
    public TariffType getType() {
        return TariffType.TARIFF_X;
    }
}
