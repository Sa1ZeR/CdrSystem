package me.sa1zer.cdrsystem.cdr.api;

import lombok.RequiredArgsConstructor;
import me.sa1zer.cdrsystem.cdr.service.CDRService;
import me.sa1zer.cdrsystem.common.payload.response.CdrResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cdr/")
public class CdrController {

    private final CDRService cdrService;

    @GetMapping("getAll")
    public ResponseEntity<?> getCdr() {
        return ResponseEntity.ok(new CdrResponse(cdrService.getCDRData()));
    }

    @PatchMapping("update")
    public ResponseEntity<?> update() {
        cdrService.updateCdr();
        return ResponseEntity.ok("Successfully updated");
    }
}