package me.sa1zer.cdrsystem.hrs.api;

import lombok.RequiredArgsConstructor;
import me.sa1zer.cdrsystem.common.payload.request.BillingRequest;
import me.sa1zer.cdrsystem.hrs.service.HRSService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hrs/")
@RequiredArgsConstructor
public class HRSController {

    private final HRSService hrsService;

    @PatchMapping("billing")
    public ResponseEntity<?> billing(@RequestBody BillingRequest request) {
        return hrsService.handleBilling(request);
    }
}
