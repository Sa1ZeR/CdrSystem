package me.sa1zer.cdrsystem.brt.payload.mapper;

import me.sa1zer.cdrsystem.common.payload.dto.ReportDto;
import me.sa1zer.cdrsystem.common.payload.mapper.Mapper;
import me.sa1zer.cdrsystem.commondb.entity.ReportData;
import org.springframework.stereotype.Component;

@Component
public class ReportDtoMapper implements Mapper<ReportData, ReportDto> {
    @Override
    public ReportDto map(ReportData from) {
        return ReportDto.builder()
                .callType(from.getCallType())
                .cost(from.getCost())
                .duration(from.getDuration())
                .startTime(from.getStartTime())
                .endTime(from.getEndTime())
                .build();
    }
}
