package me.sa1zer.cdrsystem.brt.payload.mapper;

import me.sa1zer.cdrsystem.common.payload.dto.ReportDto;
import me.sa1zer.cdrsystem.common.payload.mapper.Mapper;
import me.sa1zer.cdrsystem.commondb.entity.ReportData;
import org.springframework.stereotype.Component;

@Component
public class ReportDataMapper implements Mapper<ReportDto, ReportData> {
    @Override
    public ReportData map(ReportDto from) {
        return ReportData.builder()
                .callType(from.callType())
                .startTime(from.startTime())
                .endTime(from.endTime())
                .duration(from.duration())
                .cost(from.cost())
                .build();
    }
}
