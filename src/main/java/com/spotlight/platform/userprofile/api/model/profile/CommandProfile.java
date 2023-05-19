package com.spotlight.platform.userprofile.api.model.profile;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.spotlight.platform.userprofile.api.model.profile.primitives.CommandType;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserId;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserProfilePropertyName;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserProfilePropertyValue;

import java.util.Map;

public record CommandProfile (@JsonProperty UserId userId, @JsonProperty CommandType type,
                              @JsonProperty Map<UserProfilePropertyName, UserProfilePropertyValue> properties) {
}
