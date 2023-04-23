package me.sa1zer.cdrsystem.hrs.service.calculation;

import me.sa1zer.cdrsystem.common.object.enums.TariffType;
import me.sa1zer.cdrsystem.common.payload.dto.CdrPlusDto;
import me.sa1zer.cdrsystem.common.payload.dto.ReportDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class UnlimitedCalculator implements BaseCalculator {
    @Value("${settings.calculation.unlimited.after-limit-price}")
    private double afterLimitPrice; //after 300 min
    @Value("${settings.calculation.unlimited.before-limit-price}")
    private double beforeLimitPrice = 0.0D; //before 300 min
    @Value("${settings.calculation.unlimited.free-minutes}")
    private int freeMinutesAmount = 300;

    @Value("${settings.calculation.unlimited.free-minutes-price}")
    private static int tariffPrice = 100;

    @Override
    public List<ReportDto> getReportData(List<CdrPlusDto> cdrPlusList) {
        List<ReportDto> reportList = new ArrayList<>();

        long freeMinutes = freeMinutesAmount;
        boolean isExpired = false;

        for(CdrPlusDto cdrPlusDto : cdrPlusList) {
            long duration = cdrPlusDto.startTime().until(cdrPlusDto.endTime(), ChronoUnit.SECONDS);
            double durationMin = Math.ceil(duration / 60D);
            long paidMinutes = 0;

            if(freeMinutes - durationMin < 0) {
                isExpired = true;

                paidMinutes = (long) Math.abs(freeMinutes - durationMin);
            }
            freeMinutes-=durationMin;

            double price = isExpired ? paidMinutes * afterLimitPrice
                    : beforeLimitPrice;

            if(freeMinutes < 0)
                freeMinutes = 0;

            reportList.add(new ReportDto(cdrPlusDto.phoneNumber(), cdrPlusDto.startTime(),
                    cdrPlusDto.endTime(), cdrPlusDto.callType(), cdrPlusDto.tariffType(), price, duration));
        }

        return reportList;
    }

    @Override
    public double calculate(List<ReportDto> reportData) {
        return tariffPrice + reportData.stream().mapToDouble(ReportDto::cost).sum();
    }

    @Override
    public TariffType getType() {
        return TariffType.UNLIMITED;
    }
}
