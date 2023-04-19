package me.sa1zer.cdrsystem.crm.payload.mapper;

import me.sa1zer.cdrsystem.common.payload.mapper.Mapper;
import me.sa1zer.cdrsystem.commondb.entity.User;
import me.sa1zer.cdrsystem.crm.payload.response.CreateUserResponse;
import org.springframework.stereotype.Component;

@Component
public class CreateUserMapper implements Mapper<User, CreateUserResponse> {
    @Override
    public CreateUserResponse map(User from) {
        return CreateUserResponse.builder()
                .numberPhone(from.getPhone())
                .balance(from.getBalance())
                .tariffId(from.getTariff().getCode())
                .build();
    }
}
