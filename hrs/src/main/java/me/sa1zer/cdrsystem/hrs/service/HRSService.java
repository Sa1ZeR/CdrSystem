package me.sa1zer.cdrsystem.hrs.service;

import me.sa1zer.cdrsystem.common.object.enums.ActionType;
import me.sa1zer.cdrsystem.common.object.enums.TariffType;
import me.sa1zer.cdrsystem.common.payload.dto.BillingDataDto;
import me.sa1zer.cdrsystem.common.payload.dto.CdrPlusDto;
import me.sa1zer.cdrsystem.common.payload.dto.ReportDto;
import me.sa1zer.cdrsystem.common.payload.request.BillingRequest;
import me.sa1zer.cdrsystem.common.payload.response.ReportUpdateDataResponse;
import me.sa1zer.cdrsystem.common.service.HttpService;
import me.sa1zer.cdrsystem.hrs.service.calculation.BaseCalculator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HRSService {

    private final static Map<TariffType, BaseCalculator> CALCULATORS = new HashMap<>();
    private final HttpService httpService;
    private final CdrPlusService cdrPlusService;

    @Value("${settings.url.brt-address}")
    private String brtAddress;

    public HRSService(List<BaseCalculator> calculators, HttpService httpService, CdrPlusService cdrPlusService) {
        this.httpService = httpService;
        this.cdrPlusService = cdrPlusService;
        calculators.forEach(c-> CALCULATORS.put(c.getType(), c));
    }

    public ResponseEntity<?> billing(BillingRequest request) {
        ActionType type = ActionType.getType(request.action());
        if(type != ActionType.RUN) return null;

        cdrPlusService.parseFile();
        Map<String, List<CdrPlusDto>> cdrPlusData = cdrPlusService.getCdrPlusData();

        //start calculation
        return ResponseEntity.ok(calculate(cdrPlusData));
    }

    private ReportUpdateDataResponse calculate(Map<String, List<CdrPlusDto>> cdrPlusData) {
        List<BillingDataDto> reportsToSave = new ArrayList<>();

        //calculating total price and send to brt service (save user's balance changes)
        for(Map.Entry<String, List<CdrPlusDto>> entry : cdrPlusData.entrySet()) {
            List<CdrPlusDto> cdrData = entry.getValue();
            if(cdrData.size() > 0) {
                BaseCalculator calculator = CALCULATORS.get(cdrData.get(0).tariffType());
                List<ReportDto> reportList = calculator.getReportData(cdrData);
                double totalPrice = calculator.calculate(reportList);

                BillingDataDto dto = BillingDataDto.builder()
                        .phone(entry.getKey())
                        .totalPrice(totalPrice)
                        .reports(reportList)
                        .build();

                reportsToSave.add(dto);
            }
        }

        return new ReportUpdateDataResponse(reportsToSave);
    }
}
