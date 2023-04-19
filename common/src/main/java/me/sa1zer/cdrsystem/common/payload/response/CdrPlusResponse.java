package me.sa1zer.cdrsystem.common.payload.response;

import me.sa1zer.cdrsystem.common.payload.dto.CdrPlusDto;

import java.util.List;
import java.util.Map;

public record CdrPlusResponse(Map<String, List<CdrPlusDto>> cdrData) {

}
