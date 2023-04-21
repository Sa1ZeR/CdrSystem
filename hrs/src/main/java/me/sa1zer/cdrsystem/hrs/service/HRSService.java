package me.sa1zer.cdrsystem.hrs.service;

import me.sa1zer.cdrsystem.common.object.enums.ActionType;
import me.sa1zer.cdrsystem.common.object.enums.TariffType;
import me.sa1zer.cdrsystem.common.payload.dto.BillingDataDto;
import me.sa1zer.cdrsystem.common.payload.dto.CdrPlusDto;
import me.sa1zer.cdrsystem.common.payload.dto.ReportDto;
import me.sa1zer.cdrsystem.common.payload.request.BillingRequest;
import me.sa1zer.cdrsystem.common.payload.request.ReportUpdateDataRequest;
import me.sa1zer.cdrsystem.common.payload.response.BillingResponse;
import me.sa1zer.cdrsystem.common.payload.response.CdrPlusResponse;
import me.sa1zer.cdrsystem.common.service.HttpService;
import me.sa1zer.cdrsystem.hrs.service.calculation.BaseCalculator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HRSService {

    private final static Map<TariffType, BaseCalculator> CALCULATORS = new HashMap<>();
    private final HttpService httpService;

    @Value("${settings.url.brt-address}")
    private String brtAddress;

    public HRSService(List<BaseCalculator> calculators, HttpService httpService) {
        this.httpService = httpService;
        calculators.forEach(c-> CALCULATORS.put(c.getType(), c));
    }

    public ResponseEntity<?> billing(BillingRequest request) {
        ActionType type = ActionType.getType(request.action());
        if(type != ActionType.RUN)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bad action type");
        if(!ObjectUtils.isEmpty(request.clearOld()) && request.clearOld())
            //send request to brt if needed. he must clear old report data
            httpService.sendPatchRequest(brtAddress + "report/update", null, String.class);

        return ResponseEntity.ok(calculateReportData());
    }

    private BillingResponse calculateReportData() {
        CdrPlusResponse body = httpService.sendGetRequest(brtAddress + "/report/getCdrPlus", CdrPlusResponse.class).getBody();

        return calculate(body.cdrData());
    }

    private BillingResponse calculate(Map<String, List<CdrPlusDto>> cdrPlusData) {
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

        ReportUpdateDataRequest request = new ReportUpdateDataRequest(reportsToSave);

        //after calculation need to save billing data in db, that can brt service
        return httpService.sendPatchRequest(brtAddress + "report/updateBillingData", request, BillingResponse.class);
    }
}
