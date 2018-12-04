//
//  UserMapper.h
//  RNLaunchDarkly
//
//  Created by Jordan Petersen on 9/13/18.
//  Copyright © 2018 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LDUserBuilder.h"

@interface UserMapper : NSObject
+ (LDUserBuilder*)mapUser:(NSDictionary*)userDictionary;
@end
