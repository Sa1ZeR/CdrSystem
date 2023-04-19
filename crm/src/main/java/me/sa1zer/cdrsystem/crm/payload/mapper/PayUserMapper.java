package me.sa1zer.cdrsystem.crm.payload.mapper;

import me.sa1zer.cdrsystem.common.payload.mapper.Mapper;
import me.sa1zer.cdrsystem.commondb.entity.User;
import org.springframework.stereotype.Component;
import me.sa1zer.cdrsystem.crm.payload.response.PayUserResponse;

@Component
public class PayUserMapper implements Mapper<User, PayUserResponse> {
    @Override
    public PayUserResponse map(User from) {
        return PayUserResponse.builder()
                .userId(from.getId())
                .numberPhone(from.getPhone())
                .money(from.getBalance())
                .build();
    }
}
