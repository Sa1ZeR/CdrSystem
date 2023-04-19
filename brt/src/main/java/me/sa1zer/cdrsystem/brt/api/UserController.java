package me.sa1zer.cdrsystem.brt.api;

import lombok.RequiredArgsConstructor;
import me.sa1zer.cdrsystem.brt.service.BRTService;
import me.sa1zer.cdrsystem.commondb.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/brt/user/")
public class UserController {

    private final BRTService brtService;

    @GetMapping("getAll")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(brtService.getAllUsers());
    }
}
