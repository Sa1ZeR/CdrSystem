package me.sa1zer.cdrsystem.common.payload.mapper;

import me.sa1zer.cdrsystem.common.payload.dto.ReportDto;
import me.sa1zer.cdrsystem.common.payload.dto.ReportPayloadDto;
import me.sa1zer.cdrsystem.common.utils.TimeUtils;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper implements Mapper<ReportDto, ReportPayloadDto> {
    @Override
    public ReportPayloadDto map(ReportDto from) {
        return ReportPayloadDto.builder()
                .callType(from.callType().getCode())
                .startTime(from.startTime())
                .endTime(from.endTime())
                .duration(TimeUtils.getDurationString(from.duration()))
                .cost(from.cost())
                .build();
    }
}
