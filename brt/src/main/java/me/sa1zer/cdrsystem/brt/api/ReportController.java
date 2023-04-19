package me.sa1zer.cdrsystem.brt.api;

import lombok.RequiredArgsConstructor;
import me.sa1zer.cdrsystem.brt.service.BRTService;
import me.sa1zer.cdrsystem.common.payload.request.ReportUpdateDataRequest;
import me.sa1zer.cdrsystem.common.payload.response.CdrPlusResponse;
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

    @GetMapping("getCdrPlus")
    public ResponseEntity<?> getCdrPlus() {
        return ResponseEntity.ok(new CdrPlusResponse(brtService.getCdrPlusData()));
    }

    @PatchMapping("updateBillingData")
    public ResponseEntity<?> updateReportData(@RequestBody ReportUpdateDataRequest request) {
        return ResponseEntity.ok(brtService.updateReportData(request));
    }

    @PatchMapping("update")
    public ResponseEntity<?> updateReports() {
        brtService.updateReports();
        return ResponseEntity.ok("Reports successfully updated");
    }
}
