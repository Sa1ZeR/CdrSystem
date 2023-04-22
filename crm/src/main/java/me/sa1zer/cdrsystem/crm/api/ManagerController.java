package me.sa1zer.cdrsystem.crm.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.sa1zer.cdrsystem.common.payload.request.BillingRequest;
import me.sa1zer.cdrsystem.common.payload.response.BillingResponse;
import me.sa1zer.cdrsystem.common.service.HttpService;
import me.sa1zer.cdrsystem.crm.payload.request.ChangeTariffRequest;
import me.sa1zer.cdrsystem.crm.payload.request.CreateUserRequest;
import me.sa1zer.cdrsystem.crm.service.ManagerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/manager/")
@PreAuthorize("hasAuthority('MANAGER')")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;
    private final HttpService httpService;

    @Value("${settings.url.brt-address}")
    private String brtAddress;

    @PatchMapping("changeTariff")
    public ResponseEntity<?> chaneTariff(@Valid @RequestBody ChangeTariffRequest request) {
        return managerService.changeTariff(request);
    }

    @PostMapping("abonent")
    public ResponseEntity<?> createNewUser(@Valid @RequestBody CreateUserRequest request) {
        return managerService.createUser(request);
    }

    @PatchMapping("billing")
    public ResponseEntity<?> billing(@Valid @RequestBody BillingRequest request) {
        return ResponseEntity.ok(httpService.sendPatchRequest(brtAddress + "report/billing", request, BillingResponse.class));
    }
}
