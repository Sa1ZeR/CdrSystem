package me.sa1zer.cdrsystem.crm.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ChangeTariffResponse {

    private Long id;
    private String numberPhone;
    @JsonProperty(value = "tariff_id")
    private String tariffId;
}
