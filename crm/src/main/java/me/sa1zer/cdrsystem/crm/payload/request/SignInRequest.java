package me.sa1zer.cdrsystem.crm.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Value;

public record SignInRequest(@Pattern(regexp = "7[0-9]{10}", message = "numberPhone is not correct") String numberPhone,
                            @NotBlank(message = "Password can't be empty") String password) {

}
