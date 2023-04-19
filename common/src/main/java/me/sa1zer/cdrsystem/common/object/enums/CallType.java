package me.sa1zer.cdrsystem.common.object.enums;

public enum CallType {

    OUTGOING("01"), INCOMING("02");

    private final String code;

    CallType(String code) {
        this.code = code;
    }

    public static CallType getType(String s) {
        for(CallType c : values()) {
            if(c.code.equals(s))
                return c;
        }
        throw new RuntimeException(String.format("CallType with code %s not found!", s));
    }

    public String getCode() {
        return code;
    }
}
