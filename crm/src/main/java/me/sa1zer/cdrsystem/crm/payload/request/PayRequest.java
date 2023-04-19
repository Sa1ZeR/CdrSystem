package me.sa1zer.cdrsystem.crm.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Value;

public record PayRequest(@Pattern(regexp = "7[0-9]{10}", message = "numberPhone is not correct") String numberPhone,
                         @Min(value = 1, message = "Money param must be > 0") Long money) {

}
