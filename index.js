import {
  Platform,
  NativeModules,
  NativeEventEmitter,
} from 'react-native';

const { RNLaunchDarkly } = NativeModules;


class LaunchDarkly {
  static flags;

  constructor () {
    this.emitter = new NativeEventEmitter(RNLaunchDarkly);
    this.listeners = {};
  }

  configure (apiKey, user) {
    RNLaunchDarkly.configure(apiKey, user);
  }

  boolVariation (featureName, callback) {
    RNLaunchDarkly.boolVariation(featureName, callback);
  }

  stringVariation (featureName, fallback, callback) {
    RNLaunchDarkly.stringVariation(featureName, fallback, callback);
  }

  allFlags(callback){
    if (Platform.OS === 'android') {
      RNLaunchDarkly.allFlags(callback);
      return;
    }

    // The iOS Launch Darkly SDK doesn't have allFlags :(
    let allFlags = {};
    let promises = [];

    LaunchDarkly.flags.forEach(flag => {
      let promise = new Promise((resolve) => {
        RNLaunchDarkly.boolVariation(flag, (value) => {
          allFlags[flag] = value;
          resolve();
        });
      });
      promises.push(promise);
    });

    Promise
      .all(promises)
      .then(() => {
          callback(JSON.stringify(allFlags));
        }
      );
  }

  setFlags(flags) {
    LaunchDarkly.flags = flags;
  }

  identify(user){
    RNLaunchDarkly.identify(user);
  }

  addFeatureFlagChangeListener (featureName, callback) {
    if (Platform.OS === 'android') {
      RNLaunchDarkly.addFeatureFlagChangeListener(featureName);
    }

    if (this.listeners[featureName]) {
      return;
    }

    this.listeners[featureName] = this.emitter.addListener(
      'FeatureFlagChanged',
      ({ flagName }) => {
        if (flagName === featureName) {
          callback(flagName);
        }
      },
    );
  }

  unsubscribe () {
    Object.keys(this.listeners).forEach((featureName) => {
      this.listeners[featureName].remove();
    });

    this.listeners = {};
  }
}

export default new LaunchDarkly();
