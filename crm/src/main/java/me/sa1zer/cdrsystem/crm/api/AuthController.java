package me.sa1zer.cdrsystem.crm.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.sa1zer.cdrsystem.crm.payload.request.SignInRequest;
import me.sa1zer.cdrsystem.crm.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("signin")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest request) {
        return authenticationService.signIn(request);
    }
}
