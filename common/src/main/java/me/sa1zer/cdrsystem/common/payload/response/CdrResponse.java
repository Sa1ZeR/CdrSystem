package me.sa1zer.cdrsystem.common.payload.response;

import lombok.Value;
import me.sa1zer.cdrsystem.common.payload.dto.CdrDto;

import java.util.List;
import java.util.Map;

public record CdrResponse(Map<String, List<CdrDto>> cdrData) {

}
