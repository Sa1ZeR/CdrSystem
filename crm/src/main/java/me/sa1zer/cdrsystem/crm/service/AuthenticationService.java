package me.sa1zer.cdrsystem.crm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.sa1zer.cdrsystem.commondb.entity.User;
import me.sa1zer.cdrsystem.commondb.service.UserService;
import me.sa1zer.cdrsystem.crm.payload.request.SignInRequest;
import me.sa1zer.cdrsystem.crm.payload.response.SignInResponse;
import me.sa1zer.cdrsystem.crm.security.jwt.JwtTokenProvider;
import me.sa1zer.cdrsystem.crm.security.jwt.JwtUserFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    //authentication with token generation
    public ResponseEntity<?> signIn(SignInRequest request) {
        //var authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.numberPhone(), request.password()));
        User user = userService.findUserByPhone(request.numberPhone());
        bCryptPasswordEncoder.matches(request.password(), user.getPassword());

        String token = tokenProvider.generateToken(JwtUserFactory.createJwtUser(user));

        return ResponseEntity.ok(new SignInResponse(token));
    }
}
