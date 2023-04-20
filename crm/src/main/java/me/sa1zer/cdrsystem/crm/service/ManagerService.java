package me.sa1zer.cdrsystem.crm.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.sa1zer.cdrsystem.common.object.enums.UserRole;
import me.sa1zer.cdrsystem.common.service.KafkaSender;
import me.sa1zer.cdrsystem.commondb.entity.Tariff;
import me.sa1zer.cdrsystem.commondb.entity.User;
import me.sa1zer.cdrsystem.commondb.service.TariffService;
import me.sa1zer.cdrsystem.commondb.service.UserService;
import me.sa1zer.cdrsystem.crm.payload.mapper.ChangeTariffMapper;
import me.sa1zer.cdrsystem.crm.payload.mapper.CreateUserMapper;
import me.sa1zer.cdrsystem.crm.payload.request.ChangeTariffRequest;
import me.sa1zer.cdrsystem.crm.payload.request.CreateUserRequest;
import me.sa1zer.cdrsystem.crm.payload.response.CreateUserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ManagerService {

    private final UserService userService;
    private final TariffService tariffService;
    private final KafkaSender kafkaSender;
    private final ChangeTariffMapper changeTariffMapper;
    private final CreateUserMapper createUserMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Value("${settings.broker.topic.user-update-topic}")
    private String userUpdateTopic;

    @Transactional
    public ResponseEntity<?> changeTariff(ChangeTariffRequest request) {
        User user = userService.getUserByPhone(request.numberPhone());

        Tariff tariff = tariffService.findByCode(request.tariffId());
        user.setTariff(tariff);

        userService.save(user);

        kafkaSender.sendMessage(userUpdateTopic, user.getPhone());

        return ResponseEntity.ok(changeTariffMapper.map(user));
    }

    @Transactional
    public ResponseEntity<?> createUser(CreateUserRequest request) {
        if(userService.isUserExist(request.numberPhone()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists");
        String password = UUID.randomUUID().toString().replaceAll("-", "")
                .substring(0, 16);

        User build = User.builder()
                .phone(request.numberPhone())
                .roles(new HashSet<>(Collections.singleton(UserRole.SUBSCRIBER)))
                .balance(request.balance())
                .password(bCryptPasswordEncoder.encode(password))
                .tariff(tariffService.findByCode(request.tariff()))
                .build();

        userService.save(build);

        CreateUserResponse userDto = createUserMapper.map(build);
        userDto.setPassword(password); //да, это небезопасно, но нам нужен пароль для авторизации в некоторых контроллерах
        //будем считать, что пароль на самом деле отправляется пользователю на почту, а он в свою очередь может сменить пароль в личном кабинете на любой желающий

        kafkaSender.sendMessage(userUpdateTopic, build.getPhone());

        return ResponseEntity.ok(userDto);
    }

    @PostConstruct
    public void init() {
        if(userService.findAll().size() == 0) {
            createManager();
            createUsers();
        }
    }

    /**
     * this method creating users with test data
     */
    public void createUsers() {
        List<Tariff> tariffs = tariffService.findAll();
        if(userService.findAll().size() <= 1) {
            for(int i = 0; i < 500; i++) {
                User testUser = User.builder()
                        .balance((int)(Math.random() * 1000) - 115)
                        .roles(new HashSet<>(Collections.singleton(UserRole.SUBSCRIBER)))
                        .password(bCryptPasswordEncoder.encode("test"))
                        .tariff(tariffs.get((int) (Math.random() * tariffs.size())))
                        .phone(genTestPhoneNumber())
                        .build();

                userService.save(testUser);
            }
        }

        log.info("Test users successfully created");
    }

    //create default admin user (when the app is first launched)
    public void createManager() {
        String adminPhone = "79024333333";
        if(!userService.isUserExist(adminPhone)) {
            User user = User.builder()
                    .balance(99999)
                    .phone(adminPhone)
                    .tariff(tariffService.findByCode("06"))
                    .roles(new HashSet<>(List.of(UserRole.MANAGER, UserRole.SUBSCRIBER)))
                    .password(bCryptPasswordEncoder.encode("admin")).build();

            userService.save(user);
        }
    }

    private String genTestPhoneNumber() {
        StringBuilder sb = new StringBuilder("79");

        for (int i = 0; i < 9; i++) {
            sb.append((int) (Math.random() * 9));
        }

        return sb.toString();
    }
}
