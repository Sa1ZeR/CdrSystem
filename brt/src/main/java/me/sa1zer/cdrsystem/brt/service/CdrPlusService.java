package me.sa1zer.cdrsystem.brt.service;

import lombok.RequiredArgsConstructor;
import me.sa1zer.cdrsystem.common.payload.dto.CdrDto;
import me.sa1zer.cdrsystem.common.payload.dto.CdrPlusDto;
import me.sa1zer.cdrsystem.common.service.HttpService;
import me.sa1zer.cdrsystem.common.utils.IOUtils;
import me.sa1zer.cdrsystem.common.utils.TimeUtils;
import me.sa1zer.cdrsystem.commondb.entity.User;
import me.sa1zer.cdrsystem.commondb.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CdrPlusService {

    private final Path CDR_PLUS_FILE = Paths.get("cdr+.txt");

    private static final Map<String, List<CdrPlusDto>> CDR_PLUS_CACHE = new HashMap<>();

    private final CdrService cdrService;
    private final UserService userService;
    private final HttpService httpService;

    @Value("${settings.url.cdr-address}")
    private String cdrAddress;

    public void updateCDRPlus() {
        httpService.sendPatchRequest(cdrAddress + "update", null, String.class);

        //read cdr.txt
        cdrService.parseCDRFile();

        Map<String, List<CdrDto>> cdrData = cdrService.getCDRData(); //cdr data grouped by phone number

        //get users with balance > 0 && operator == Ромашка
        List<User> users = userService.findAllWithPositiveBalance(cdrData.keySet(), "Ромашка");

        //create cdr+ file
        createCdrPlus(users, cdrData);
    }

    //create cdr + tariff data
    private void createCdrPlus(List<User> users, Map<String, List<CdrDto>> cdrData) {
        CDR_PLUS_CACHE.clear();

        StringBuilder sb = new StringBuilder();

        for(User u : users) {
            List<CdrDto> cdrDtos = cdrData.get(u.getPhone());

            if(!ObjectUtils.isEmpty(cdrDtos)) {
                for(CdrDto cdrDto : cdrDtos) {
                    //we must create cdr+ dto for caching
                    List<CdrPlusDto> cdrPlusData = CDR_PLUS_CACHE.getOrDefault(u.getPhone(), new ArrayList<>());

                    CdrPlusDto build = CdrPlusDto.builder()
                            .phoneNumber(u.getPhone())
                            .callType(cdrDto.callType())
                            .startTime(cdrDto.startTime())
                            .endTime(cdrDto.endTime())
                            .tariffType(u.getTariff().getType())
                            .operator(u.getOperator().getName())
                            .build();
                    cdrPlusData.add(build);

                    sb.append(getCdrPlusLine(build)); //for writing

                    CDR_PLUS_CACHE.put(u.getPhone(), cdrPlusData);
                }

                IOUtils.createFile(CDR_PLUS_FILE, true);
                IOUtils.writeToFile(CDR_PLUS_FILE, sb.toString());
            }
        }
    }

    private String getCdrPlusLine(CdrPlusDto cdrPlusDto) {
        StringBuilder sb = new StringBuilder();

        sb.append(cdrPlusDto.phoneNumber()).append(", ");
        sb.append(cdrPlusDto.callType().getCode()).append(", ");
        sb.append(TimeUtils.LocalDateTimeToCdr(cdrPlusDto.startTime())).append(", ");
        sb.append(TimeUtils.LocalDateTimeToCdr(cdrPlusDto.endTime())).append(", ");
        sb.append(cdrPlusDto.tariffType().getCode()).append(", ");
        sb.append(cdrPlusDto.operator()).append("\n");

        return sb.toString();
    }
}
