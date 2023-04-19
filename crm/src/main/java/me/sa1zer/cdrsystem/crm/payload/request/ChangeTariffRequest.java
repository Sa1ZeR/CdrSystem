package me.sa1zer.cdrsystem.crm.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import lombok.Value;

public record ChangeTariffRequest(
        @Pattern(regexp = "7[0-9]{10}", message = "numberPhone is not correct") String numberPhone,
        @JsonProperty(value = "tariff_id") String tariffId) {

}
