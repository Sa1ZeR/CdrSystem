package me.sa1zer.cdrsystem.crm.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreateUserResponse {

    private String numberPhone;
    @JsonProperty(value = "tariff_id")
    private String tariffId;
    private double balance;
    private String password;
}
