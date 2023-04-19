package me.sa1zer.cdrsystem.common.object.enums;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
public enum ActionType {

    RUN("run");

    private final String name;

    public static ActionType getType(String s) {
        for(ActionType t : values()) {
            if(t.name.equalsIgnoreCase(s))
                return t;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("ActionType with name %s not found!", s));
    }
}
