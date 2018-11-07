
#import "RNLaunchDarkly.h"
#import "DarklyConstants.h"
#import "UserMapper.h"

@implementation RNLaunchDarkly

- (NSArray<NSString *> *)supportedEvents
{
    return @[@"FeatureFlagChanged"];
}

RCT_EXPORT_METHOD(configure:(NSString*)apiKey options:(NSDictionary*)options) {
    NSLog(@"configure with %@", options);
    
    LDConfigBuilder *config = [[LDConfigBuilder alloc] init];
    [config withMobileKey:apiKey];

    [[NSNotificationCenter defaultCenter]
     addObserver:self
     selector:@selector(handleFeatureFlagChange:)
     name:kLDFlagConfigChangedNotification
     object:nil];
    
    LDUserBuilder * userBuilder = [UserMapper mapUser:options];

    [[LDClient sharedInstance] start:config userBuilder:userBuilder];
}

RCT_EXPORT_METHOD(boolVariation:(NSString*)flagName callback:(RCTResponseSenderBlock)callback) {
    BOOL showFeature = [[LDClient sharedInstance] boolVariation:flagName fallback:NO];
    callback(@[[NSNumber numberWithBool:showFeature]]);
}

RCT_EXPORT_METHOD(stringVariation:(NSString*)flagName fallback:(NSString*)fallback callback:(RCTResponseSenderBlock)callback) {
    NSString* flagValue = [[LDClient sharedInstance] stringVariation:flagName fallback:fallback];
    callback(@[flagValue]);
}

RCT_EXPORT_METHOD(identify:(NSDictionary*)userMap) {
    LDUserBuilder * userBuilder = [UserMapper mapUser:userMap];
    [[LDClient sharedInstance] updateUser:userBuilder];
}

- (void)handleFeatureFlagChange:(NSNotification *)notification
{
    NSString *flagName = notification.userInfo[@"flagkey"];
    [self sendEventWithName:@"FeatureFlagChanged" body:@{@"flagName": flagName}];
}

RCT_EXPORT_MODULE()

@end

