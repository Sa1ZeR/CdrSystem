package me.sa1zer.cdrsystem.cdr.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.sa1zer.cdrsystem.common.payload.dto.CdrDto;
import me.sa1zer.cdrsystem.common.payload.dto.OperatorDto;
import me.sa1zer.cdrsystem.common.payload.dto.UserDto;
import me.sa1zer.cdrsystem.common.object.enums.CallType;
import me.sa1zer.cdrsystem.common.utils.IOUtils;
import me.sa1zer.cdrsystem.common.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Class which generating cdr.txt
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CDRService {

    private static final Path CDR_FILE = Paths.get("files/cdr.txt");
    private static final int CDR_LIMES = 5000;
    private static final Random random = new Random();

    private final RestTemplate restTemplate;

    @Value("${settings.url.brt-address}")
    private String brtAddress;

    /**
     * this method create test cdr data
     */
    public void genCDRFile(boolean isNew) {
        log.info("CDR generation successfully started!");
        IOUtils.createFile(CDR_FILE, isNew);

        StringBuilder stringBuilder = new StringBuilder();

        ResponseEntity<UserDto[]> response = getUsers();

        List<UserDto> users = Arrays.asList(response.getBody());

        System.out.println(users.size());

        LocalDateTime start;
        LocalDateTime end = LocalDateTime.of(2023, 1, 1, 9, 0, 0);
        for (int i = 0; i < CDR_LIMES; i++) {
            CallType callType = CallType.values()[random.nextInt(2)];

            UserDto user = users.get(random.nextInt(users.size() - 1));

            start = end.plusSeconds(45 + random.nextInt(3600));
            end = start.plusSeconds(5 + random.nextInt(7200));

            stringBuilder.append(callType.getCode());
            stringBuilder.append(", ");
            stringBuilder.append(user.phone());
            stringBuilder.append(", ");
            stringBuilder.append(TimeUtils.LocalDateTimeToCdr(start));
            stringBuilder.append(", ");
            stringBuilder.append(TimeUtils.LocalDateTimeToCdr(end));

            if(i < CDR_LIMES - 1)
                stringBuilder.append("\n");
        }

        IOUtils.writeToFile(CDR_FILE, stringBuilder.toString());
        log.info("CDR generation successfully finished!");
    }

    //Для генерации тестовых данных нам приходится обращаться к другим сервисам для получения существующих пользователей
    private ResponseEntity<UserDto[]>getUsers() {
        ResponseEntity<UserDto[]> response = restTemplate.getForEntity(brtAddress + "user/getAll",
                UserDto[].class);
        if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Server Error");

        return response;
    }
}
