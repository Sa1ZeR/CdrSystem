package me.sa1zer.cdrsystem.hrs.service;

import lombok.extern.slf4j.Slf4j;
import me.sa1zer.cdrsystem.common.object.enums.CallType;
import me.sa1zer.cdrsystem.common.object.enums.TariffType;
import me.sa1zer.cdrsystem.common.payload.dto.CdrDto;
import me.sa1zer.cdrsystem.common.payload.dto.CdrPlusDto;
import me.sa1zer.cdrsystem.common.utils.TimeUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CdrPlusService {

    private static final Map<String, List<CdrPlusDto>> CDR_PLUS_CACHE = new HashMap<>();
    private final Path CDR_PLUS_FILE = Paths.get("cdr+.txt");
    public void parseFile() {
        try(BufferedReader br = Files.newBufferedReader(CDR_PLUS_FILE)) {
            String s;
            while ((s = br.readLine()) != null) {
                try {
                    CdrPlusDto cdr = getCdrPlusFromString(s);
                    if(ObjectUtils.isEmpty(cdr)) continue;

                    List<CdrPlusDto> cdrByPhone = CDR_PLUS_CACHE.getOrDefault(cdr.phoneNumber(), new ArrayList<>());
                    cdrByPhone.add(cdr);

                    CDR_PLUS_CACHE.put(cdr.phoneNumber(), cdrByPhone);
                } catch (ParseException e) {
                    throw new RuntimeException(String.format("Error while parsing cdr line %s", s), e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info("cdr+.txt successfully parsed!");
    }

    private CdrPlusDto getCdrPlusFromString(String s) throws ParseException {
        String[] split = s.split(",");

        if(split.length != 6)
            return null;

        String phone = split[0].trim();
        LocalDateTime start = TimeUtils.parseTimeFromString(split[2].trim());
        LocalDateTime end = TimeUtils.parseTimeFromString(split[3].trim());
        CallType callType = CallType.getType(split[1].trim());
        TariffType tariffType = TariffType.getType(split[4].trim());
        String operator = split[5].trim();

        return new CdrPlusDto(phone, start, end, callType, tariffType, operator);
    }

    public Map<String, List<CdrPlusDto>> getCdrPlusData() {
        return CDR_PLUS_CACHE;
    }
}
