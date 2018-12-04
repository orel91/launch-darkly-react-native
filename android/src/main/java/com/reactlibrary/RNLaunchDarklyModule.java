
package com.reactlibrary;

import android.app.Application;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.gson.Gson;
import com.launchdarkly.android.FeatureFlagChangeListener;
import com.launchdarkly.android.LDClient;
import com.launchdarkly.android.LDConfig;
import com.launchdarkly.android.LDUser;
import com.launchdarkly.android.LaunchDarklyException;

public class RNLaunchDarklyModule extends ReactContextBaseJavaModule {

    private LDClient ldClient;
    private Gson gson;

    public RNLaunchDarklyModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.gson = new Gson();
    }

    @Override
    public String getName() {
        return "RNLaunchDarkly";
    }

    @ReactMethod
    public void configure(String apiKey, ReadableMap userMap) {
        LDConfig ldConfig = new LDConfig.Builder()
                .setMobileKey(apiKey)
                .build();

        LDUser user = UserMapper.getUserFromMap(userMap);
        Application application = (Application) getReactApplicationContext().getApplicationContext();

        if (application != null) {
            ldClient = LDClient.init(application, ldConfig, user, 0);
        } else {
            Log.d("RNLaunchDarklyModule", "Couldn't init RNLaunchDarklyModule cause application was null");
        }
    }

    @ReactMethod
    public void addFeatureFlagChangeListener(String flagName) {
        FeatureFlagChangeListener listener = new FeatureFlagChangeListener() {
            @Override
            public void onFeatureFlagChange(String flagKey) {
                WritableMap result = Arguments.createMap();
                result.putString("flagName", flagKey);

                getReactApplicationContext()
                        .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                        .emit("FeatureFlagChanged", result);
            }
        };

        try {
            LDClient.get().registerFeatureFlagListener(flagName, listener);
        } catch (LaunchDarklyException e) {
            Log.d("RNLaunchDarklyModule", e.getMessage());
            e.printStackTrace();
        }
    }

    @ReactMethod
    public void boolVariation(String flagName, Callback callback) {
        Boolean variationResult = ldClient.boolVariation(flagName, false);
        Log.d("RNLauchDarklyModule", "flag " + flagName + ": " + variationResult);
        callback.invoke(variationResult);
    }

    @ReactMethod
    public void stringVariation(String flagName, String fallback, Callback callback) {
        String variationResult = ldClient.stringVariation(flagName, fallback);
        callback.invoke(variationResult);
    }

    @ReactMethod
    public void allFlags(Callback callback) {
        String jsonFlags = gson.toJson(ldClient.allFlags());
        callback.invoke(jsonFlags);
    }

    @ReactMethod
    public void identify(ReadableMap userMap){
        LDUser user = UserMapper.getUserFromMap(userMap);
        ldClient.identify(user);
    }

}
