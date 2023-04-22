package me.sa1zer.cdrsystem.brt.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.sa1zer.cdrsystem.brt.service.BRTService;
import me.sa1zer.cdrsystem.common.payload.request.BillingRequest;
import me.sa1zer.cdrsystem.common.payload.response.PhoneReportResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/brt/report")
@RequiredArgsConstructor
public class ReportController {

    private final BRTService brtService;

    @GetMapping("getReportByPhone/{numberPhone}")
    public ResponseEntity<?> getReportByPhone(@PathVariable String numberPhone) {
        return ResponseEntity.ok(new PhoneReportResponse(brtService.getReportByPhone(numberPhone),
                brtService.getTotalCost(numberPhone)));
    }

    @PatchMapping("billing")
    public ResponseEntity<?> billing(@Valid @RequestBody BillingRequest request) {
        return ResponseEntity.ok(brtService.billing(request));
    }
}
