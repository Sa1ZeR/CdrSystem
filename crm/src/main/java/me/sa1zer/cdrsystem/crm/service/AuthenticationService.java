package me.sa1zer.cdrsystem.crm.service;

import lombok.RequiredArgsConstructor;
import me.sa1zer.cdrsystem.crm.payload.request.SignInRequest;
import me.sa1zer.cdrsystem.crm.payload.response.SignInResponse;
import me.sa1zer.cdrsystem.crm.security.jwt.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    //authentication with token generation
    public ResponseEntity<?> signIn(SignInRequest request) {
        var authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.numberPhone(), request.password()));
        String token = tokenProvider.generateToken(authenticate);

        return ResponseEntity.ok(new SignInResponse(token));
    }
}
