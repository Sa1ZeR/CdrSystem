package me.sa1zer.cdrsystem.crm.api;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/crm/")
public class HealthController {

    @GetMapping("healthCheck")
    public ResponseEntity<?> healthCheck() {
        return new ResponseEntity<>(HttpEntity.EMPTY, HttpStatus.OK);
    }
}
