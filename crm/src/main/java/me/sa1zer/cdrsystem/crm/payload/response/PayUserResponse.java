package me.sa1zer.cdrsystem.crm.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PayUserResponse {
    private Long id;
    private Long userId;
    private String numberPhone;
    private double money;
    private double balance;
}
