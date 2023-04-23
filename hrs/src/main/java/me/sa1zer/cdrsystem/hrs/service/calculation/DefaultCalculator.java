package me.sa1zer.cdrsystem.hrs.service.calculation;

import me.sa1zer.cdrsystem.common.object.enums.CallType;
import me.sa1zer.cdrsystem.common.object.enums.TariffType;
import me.sa1zer.cdrsystem.common.payload.dto.CdrPlusDto;
import me.sa1zer.cdrsystem.common.payload.dto.ReportDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultCalculator implements BaseCalculator {

    @Value("${settings.calculation.default.incoming-price}")
    private double incomingPrice;

    @Value("${settings.calculation.default.outgoing-price}")
    private double outgoingPrice; //first 100 min
    @Value("${settings.calculation.per-minute}")
    private double perMinutePrice; //after 100 min
    @Value("${settings.calculation.default.free-minutes}")
    private long freeMinutesAmount;

    @Override
    public List<ReportDto> getReportData(List<CdrPlusDto> cdrPlusList) {
        List<ReportDto> reportList = new ArrayList<>();

        long freeMinutes = freeMinutesAmount;
        for(CdrPlusDto cdrPlusDto : cdrPlusList) {
            double price = 0;

            long usePerMinutePrice = 0;

            long duration = cdrPlusDto.startTime().until(cdrPlusDto.endTime(), ChronoUnit.SECONDS);
            double durationMin = Math.ceil(duration / 60D);

            if(cdrPlusDto.callType() == CallType.OUTGOING) {
                if (freeMinutes - durationMin < 0) { //if freeMins is empty
                    usePerMinutePrice = (long) (freeMinutes - durationMin);

                    if (freeMinutes > 0) {
                        price = (usePerMinutePrice + durationMin) * outgoingPrice +
                                Math.abs(usePerMinutePrice) * perMinutePrice;
                    } else
                        price = Math.abs(usePerMinutePrice) * perMinutePrice;
                } else price = durationMin * outgoingPrice;
            } else price = incomingPrice;

            freeMinutes-=durationMin;

            if(freeMinutes < 0)
                freeMinutes = 0;

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
        return TariffType.DEFAULT;
    }
}
