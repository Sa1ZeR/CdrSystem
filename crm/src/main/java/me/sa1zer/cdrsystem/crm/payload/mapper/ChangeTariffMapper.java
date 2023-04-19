package me.sa1zer.cdrsystem.crm.payload.mapper;

import me.sa1zer.cdrsystem.common.payload.mapper.Mapper;
import me.sa1zer.cdrsystem.commondb.entity.User;
import org.springframework.stereotype.Component;
import me.sa1zer.cdrsystem.crm.payload.response.ChangeTariffResponse;
@Component
public class ChangeTariffMapper implements Mapper<User, ChangeTariffResponse> {
    @Override
    public ChangeTariffResponse map(User from) {
        return ChangeTariffResponse.builder()
                .id(from.getId())
                .numberPhone(from.getPhone())
                .tariffId(from.getTariff().getCode())
                .build();
    }
}
