package me.sa1zer.cdrsystem.common.payload.dto;

import me.sa1zer.cdrsystem.common.object.enums.CallType;

import java.time.LocalDateTime;

public record CdrDto(String phoneNumber, LocalDateTime startTime, LocalDateTime endTime, CallType callType) {

}
