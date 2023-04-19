package me.sa1zer.cdrsystem.crm.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import lombok.Value;

public record CreateUserRequest(
        @Pattern(regexp = "7[0-9]{10}", message = "numberPhone is not correct") String numberPhone,
        @Pattern(regexp = "[0-9]{2}", message = "tariff is not correct") @JsonProperty(value = "tariff_id") String tariff,
        @DecimalMin(value = "0", message = "balance must be >= 0") double balance) {

}
