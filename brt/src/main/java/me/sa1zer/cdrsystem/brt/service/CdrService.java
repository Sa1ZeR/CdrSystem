package me.sa1zer.cdrsystem.brt.service;

import me.sa1zer.cdrsystem.common.object.enums.CallType;
import me.sa1zer.cdrsystem.common.payload.dto.CdrDto;
import me.sa1zer.cdrsystem.common.utils.IOUtils;
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
public class CdrService {

    private static final Map<String, List<CdrDto>> CDR_CACHE = new HashMap<>();

    private final Path CDR_FILE = Paths.get("files/cdr.txt");

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
}
