//
//  UserMapper.m
//  RNLaunchDarkly
//
//  Created by Jordan Petersen on 9/13/18.
//  Copyright © 2018 Facebook. All rights reserved.
//

#import "UserMapper.h"
#import "LDUserBuilder.h"

@implementation UserMapper

+ (LDUserBuilder*)mapUser:(NSDictionary*)userDictionary{
    NSArray* nonCustomFields  = @[@"key", @"firstName", @"lastName", @"email", @"isAnonymous"];
    
    NSString* key           = userDictionary[@"key"];
    NSString* firstName     = userDictionary[@"firstName"];
    NSString* lastName      = userDictionary[@"lastName"];
    NSString* email         = userDictionary[@"email"];
    NSNumber* isAnonymous   = userDictionary[@"isAnonymous"];
    
    LDUserBuilder *user = [[LDUserBuilder alloc] init];
    user.key = key;
    
    if (firstName) {
        user = [user withFirstName:firstName];
    }
    
    if (lastName) {
        user = [user withLastName:lastName];
    }
    
    if (email) {
        user = [user withEmail:email];
    }
    
    if([isAnonymous isEqualToNumber:[NSNumber numberWithBool:YES]]) {
        user = [user withAnonymous:TRUE];
    }
    
    for (NSString* key in userDictionary) {
        if (![nonCustomFields containsObject:key]) {
            NSLog(@"LaunchDarkly Custom Field key=%@", key);
            user = [user withCustomString:key value:userDictionary[key]];
        }
    }
    
    return user;
}

@end
