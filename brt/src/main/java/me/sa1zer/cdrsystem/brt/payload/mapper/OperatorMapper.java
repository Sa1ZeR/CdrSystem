package me.sa1zer.cdrsystem.brt.payload.mapper;

import me.sa1zer.cdrsystem.common.payload.dto.OperatorDto;
import me.sa1zer.cdrsystem.common.payload.mapper.Mapper;
import me.sa1zer.cdrsystem.commondb.entity.Operator;
import org.springframework.stereotype.Component;

@Component
public class OperatorMapper implements Mapper<Operator, OperatorDto> {
    @Override
    public OperatorDto map(Operator from) {
        return OperatorDto.builder()
                .name(from.getName())
                .build();
    }
}
