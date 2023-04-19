package me.sa1zer.cdrsystem.common.object.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TariffType {

    DEFAULT("11"), PER_MINUTE("03"), UNLIMITED("06"), TARIFF_X("82");

    private final String code;

    public static TariffType getType(String s) {
        for(TariffType t : values()) {
            if(t.code.equals(s))
                return t;
        }
        throw new RuntimeException(String.format("TariffType with code %s not found!", s));
    }
}
