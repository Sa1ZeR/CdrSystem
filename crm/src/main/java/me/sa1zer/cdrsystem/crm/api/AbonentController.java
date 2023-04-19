package me.sa1zer.cdrsystem.crm.api;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import me.sa1zer.cdrsystem.crm.payload.request.PayRequest;
import me.sa1zer.cdrsystem.crm.service.AbonentService;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/abonent/")
@RequiredArgsConstructor
public class AbonentController {

    private final AbonentService abonentService;

    @PatchMapping("pay")
    public ResponseEntity<?> pay(@Valid @RequestBody PayRequest request) {
        return abonentService.payMoney(request);
    }

    @GetMapping("report/{numberPhone}")
    public ResponseEntity<?> getReport(@PathVariable(name = "numberPhone") @Valid @Pattern(regexp = "7[0-9]{10}",
            message = "numberPhone is not correct") String numberPhone, Principal principal) {
        return abonentService.getReport(numberPhone, principal);
    }
}
