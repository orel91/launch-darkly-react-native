package com.reactlibrary;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.launchdarkly.android.LDUser;

import java.util.Iterator;

public class UserMapper {
    private UserMapper() {
    }

    public static LDUser getUserFromMap(ReadableMap userMap) {
        LDUser.Builder userBuilder = new LDUser.Builder(userMap.getString("key"));

        if (userMap.hasKey("email")) {
            userBuilder = userBuilder.email(userMap.getString("email"));
        }

        if (userMap.hasKey("firstName")) {
            userBuilder = userBuilder.firstName(userMap.getString("firstName"));
        }

        if (userMap.hasKey("lastName")) {
            userBuilder = userBuilder.lastName(userMap.getString("lastName"));
        }

        if (userMap.hasKey("isAnonymous")) {
            userBuilder = userBuilder.anonymous(userMap.getBoolean("isAnonymous"));
        }

        if (userMap.hasKey("custom")) {
            ReadableMap a = userMap.getMap("custom");
            ReadableMapKeySetIterator iterator = a.keySetIterator();
            while (iterator.hasNextKey()) {
                String key = iterator.nextKey();
                userBuilder = userBuilder.custom(key, a.getString(key));
            }
        }
        return userBuilder.build();
    }
}
