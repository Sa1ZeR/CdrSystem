package me.sa1zer.cdrsystem.hrs.service.calculation;

import me.sa1zer.cdrsystem.common.object.enums.TariffType;
import me.sa1zer.cdrsystem.common.payload.dto.CdrPlusDto;
import me.sa1zer.cdrsystem.common.payload.dto.ReportDto;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class UnlimitedCalculator implements BaseCalculator {
    private static final double AFTER_LIMIT_PRICE = 1.0D; //after 300 min
    private static final double BEFORE_LIMIT_PRICE = 0.0D; //before 300 min
    private static final int FREE_MINUTES = 300;

    private static int TARIFF_PRICE = 100;

    @Override
    public List<ReportDto> getReportData(List<CdrPlusDto> cdrPlusList) {
        List<ReportDto> reportList = new ArrayList<>();

        long freeMinutes = FREE_MINUTES;
        boolean isExpired = false;

        long totalMins = 0;
        for(CdrPlusDto cdrPlusDto : cdrPlusList) {
            long duration = cdrPlusDto.startTime().until(cdrPlusDto.endTime(), ChronoUnit.SECONDS);
            double durationMin = Math.ceil(duration / 60D);
            long paidMinutes = 0;

            if(freeMinutes - durationMin < 0) {
                isExpired = true;

                paidMinutes = (long) Math.abs(freeMinutes - durationMin);
            }
            freeMinutes-=durationMin;

            double price = isExpired ? paidMinutes * AFTER_LIMIT_PRICE
                    : BEFORE_LIMIT_PRICE;

            if(freeMinutes < 0)
                freeMinutes = 0;

            reportList.add(new ReportDto(cdrPlusDto.phoneNumber(), cdrPlusDto.startTime(),
                    cdrPlusDto.endTime(), cdrPlusDto.callType(), cdrPlusDto.tariffType(), price, duration));
        }

        return reportList;
    }

    @Override
    public double calculate(List<ReportDto> reportData) {
        return TARIFF_PRICE + reportData.stream().mapToDouble(ReportDto::cost).sum();
    }

    @Override
    public TariffType getType() {
        return TariffType.UNLIMITED;
    }
}
