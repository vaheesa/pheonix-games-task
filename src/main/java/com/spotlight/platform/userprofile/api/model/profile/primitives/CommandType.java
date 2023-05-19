package com.spotlight.platform.userprofile.api.model.profile.primitives;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.spotlight.platform.userprofile.api.model.common.AlphaNumericalStringWithMaxLength;


public class CommandType extends AlphaNumericalStringWithMaxLength {
    @JsonCreator
    protected CommandType(String value) {
        super(value);
    }

    public static CommandType valueOf(String commandType) {
        return new CommandType(commandType);
    }
}