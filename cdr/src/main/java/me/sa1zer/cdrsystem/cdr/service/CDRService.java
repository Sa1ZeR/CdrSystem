package me.sa1zer.cdrsystem.cdr.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.sa1zer.cdrsystem.common.payload.dto.CdrDto;
import me.sa1zer.cdrsystem.common.payload.dto.UserDto;
import me.sa1zer.cdrsystem.common.object.enums.CallType;
import me.sa1zer.cdrsystem.common.utils.IOUtils;
import me.sa1zer.cdrsystem.common.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CDRService {

    private static final Path CDR_FILE = Paths.get("cdr.txt");
    private static final int CDR_LIMES = 5000;
    private static final Random random = new Random();

    private static final Map<String, List<CdrDto>> CDR_CACHE = new HashMap<>();

    private final RestTemplate restTemplate;

    @Value("${settings.url.brt-address}")
    private String brtAddress;

    @PostConstruct
    public void init() {
        genCDRFile(false);
        parseCDRFile();
    }

    public void parseCDRFile() {
        CDR_CACHE.clear();

        try(BufferedReader bis = Files.newBufferedReader(CDR_FILE)) {
            String s;
            while ((s = bis.readLine()) != null) {
                try {
                    CdrDto cdr = getCdrFromString(s);
                    if(ObjectUtils.isEmpty(cdr)) continue;

                    List<CdrDto> cdrByPhone = CDR_CACHE.getOrDefault(cdr.phoneNumber(), new ArrayList<>());
                    cdrByPhone.add(cdr);

                    CDR_CACHE.put(cdr.phoneNumber(), cdrByPhone);
                } catch (ParseException e) {
                    throw new RuntimeException(String.format("Error while parsing cdr line %s", s), e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private CdrDto getCdrFromString(String s) throws ParseException {
        String[] split = s.split(",");

        if(split.length != 4)
            return null;

        String phone = split[1].trim();
        LocalDateTime start = TimeUtils.parseTimeFromString(split[2].trim());
        LocalDateTime end = TimeUtils.parseTimeFromString(split[3].trim());
        CallType callType = CallType.getType(split[0].trim());

        return new CdrDto(phone, start, end, callType);
    }

    public Map<String, List<CdrDto>> getCDRData() {
        return CDR_CACHE;
    }

    /**
     * if cdr file not exist, this method create test cdr data
     */
    @SneakyThrows
    public void genCDRFile(boolean isNew) {
        if(isNew && Files.exists(CDR_FILE))
            Files.delete(CDR_FILE);

        log.info("CDR generation successfully started!");
        IOUtils.createFile(CDR_FILE);

        StringBuilder stringBuilder = new StringBuilder();

        ResponseEntity<UserDto[]> response = restTemplate.getForEntity(brtAddress + "user/getAll",
                UserDto[].class);
        if(!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            log.error(String.format("Can't get user's list. Response code: %s, message: %s",
                    response.getStatusCode(), response.toString()));
            return;
        }
        List<UserDto> users = Arrays.asList(response.getBody());

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
//            stringBuilder.append(", ");
//            stringBuilder.append(user.getTariff().getCode());
            if(i < CDR_LIMES - 1)
                stringBuilder.append("\n");
        }

        IOUtils.writeToFile(CDR_FILE, stringBuilder.toString());
        log.info("CDR generation successfully finished!");
    }

    public void updateCdr() {
        genCDRFile(true);
        parseCDRFile();
    }
}
