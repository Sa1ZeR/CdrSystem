package me.sa1zer.cdrsystem.brt.payload.mapper;

import me.sa1zer.cdrsystem.common.payload.dto.UserDto;
import me.sa1zer.cdrsystem.common.payload.mapper.Mapper;
import me.sa1zer.cdrsystem.commondb.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements Mapper<User, UserDto> {
    @Override
    public UserDto map(User from) {
        return UserDto.builder()
                .id(from.getId())
                .phone(from.getPhone())
                .balance(from.getBalance())
                .build();
    }
}
