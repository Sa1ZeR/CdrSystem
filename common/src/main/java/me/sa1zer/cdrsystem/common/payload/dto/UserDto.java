package me.sa1zer.cdrsystem.common.payload.dto;

import lombok.Builder;

@Builder
public record UserDto(Long id, String phone, double balance) {
}
