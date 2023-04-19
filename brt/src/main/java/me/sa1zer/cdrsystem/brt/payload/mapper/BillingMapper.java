package me.sa1zer.cdrsystem.brt.payload.mapper;

import me.sa1zer.cdrsystem.common.payload.dto.BillingDto;
import me.sa1zer.cdrsystem.common.payload.mapper.Mapper;
import me.sa1zer.cdrsystem.commondb.entity.User;
import org.springframework.stereotype.Component;

@Component
public class BillingMapper implements Mapper<User, BillingDto> {
    @Override
    public BillingDto map(User from) {
        return BillingDto.builder()
                .phoneNumber(from.getPhone())
                .balance(from.getBalance())
                .build();
    }
}
