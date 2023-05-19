package com.spotlight.platform.userprofile.api.core.profile;

import com.spotlight.platform.userprofile.api.core.exceptions.EntityNotFoundException;
import com.spotlight.platform.userprofile.api.core.profile.persistence.UserProfileDao;
import com.spotlight.platform.userprofile.api.model.profile.CommandProfile;
import com.spotlight.platform.userprofile.api.model.profile.UserProfile;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserId;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserProfilePropertyName;
import com.spotlight.platform.userprofile.api.model.profile.primitives.UserProfilePropertyValue;

import javax.inject.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserProfileService {
    private final UserProfileDao userProfileDao;

    @Inject
    public UserProfileService(UserProfileDao userProfileDao) {
        this.userProfileDao = userProfileDao;
    }

    public UserProfile get(UserId userId) {
        return userProfileDao.get(userId).orElseThrow(EntityNotFoundException::new);
    }

    public UserProfile put(CommandProfile commandProfile,UserId userId) {
        Map<UserProfilePropertyName, UserProfilePropertyValue> commandProfileUserProperties = commandProfile.properties();
        UserProfile existingUserProfile=null;
        UserProfile userProfile=null;
        switch (commandProfile.type().toString()){
            case "replace":
                userProfile=replaceProperty(userId,commandProfileUserProperties);
                break;
            case "increment":
                existingUserProfile=userProfileDao.get(userId).orElseThrow(EntityNotFoundException::new);
                userProfile=incrementProperty(userId,commandProfileUserProperties,existingUserProfile);
                break;
            case "collect":
                existingUserProfile=userProfileDao.get(userId).orElseThrow(EntityNotFoundException::new);
                userProfile=collectProperty(userId,commandProfileUserProperties,existingUserProfile);
                break;
        }

        userProfileDao.put(userProfile);
        return userProfile;
    }

    private UserProfile replaceProperty(UserId userId,Map<UserProfilePropertyName,UserProfilePropertyValue> commandProfileUserProperties){

        return new UserProfile(userId, Instant.now(), commandProfileUserProperties);
    }
    private UserProfile incrementProperty(UserId userId,Map<UserProfilePropertyName,UserProfilePropertyValue> commandProfileUserProperties,UserProfile existingUserProfile){

        for (UserProfilePropertyName key : commandProfileUserProperties.keySet()) {
            existingUserProfile.userProfileProperties().merge(key, commandProfileUserProperties.get(key),(v1, v2) -> {
                UserProfilePropertyValue userProfilePropertyValue = UserProfilePropertyValue.valueOf(v1.hashCode()+v2.hashCode());
                return userProfilePropertyValue;
            });
        }
        return existingUserProfile;
    }

    private UserProfile collectProperty(UserId userId,Map<UserProfilePropertyName,UserProfilePropertyValue> commandProfileUserProperties,UserProfile existingUserProfile){
        List<UserProfilePropertyValue> values1 = new ArrayList<>();
        List<UserProfilePropertyValue> values2 = new ArrayList<>();
        for (UserProfilePropertyName key : existingUserProfile.userProfileProperties().keySet()) {
             values1.add(existingUserProfile.userProfileProperties().get(key));
             values2.add(commandProfileUserProperties.get(key)) ;
        }
        for (UserProfilePropertyValue value : values2) {
            values1.add(value);
        }
            UserProfilePropertyValue userProfilePropertyValue = UserProfilePropertyValue.valueOf(values1);
        for (UserProfilePropertyName key : existingUserProfile.userProfileProperties().keySet()) {
            existingUserProfile.userProfileProperties().put(key, userProfilePropertyValue);
        }
        return existingUserProfile;
    }
}
