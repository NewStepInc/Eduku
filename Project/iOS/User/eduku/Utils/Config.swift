//
//  Config.swift
//  eduku
//
//  Created by Mickey on 2/10/16.
//  Copyright © 2016 Mickey. All rights reserved.
//

import Foundation

enum ParseUserKeys: String {
    case UserName           = "username"
    case Email              = "email"
    case FullName           = "fullname"
    case City               = "city"
    case Country            = "country"
    case FollowRipples      = "followRipples"
}

enum ParseCampaign: String{
    case CampaignName       = "campaignName"
    case Description        = "description"
    case Destination        = "destination"
    case isActive           = "isActive"
    case PhoneNumber        = "phoneNumber"
    case StartAt            = "startAt"
}

enum ParseRipple: String{
    case CampaignId         = "campaignId"
    case RippleCode         = "rippleId"
    case RippleName         = "rippleName"
    case UserId             = "userId"
    case RippleLog          = "rippleLog"
}

enum ParseRecentUpdates: String{
    case Log                = "log"
}

enum ParseClassKeys: String{
    case User               = "User"
    case Campaign           = "Campaign"
    case Ripple             = "Ripple"
    case RecentUpdates      = "RecentUpdates"
    case Photo              = "Photo"
}

enum ParsePhoto: String{
    case Photo              = "photo"
    case RippleId           = "rippleId"
    case IsApproved         = "isApproved"
    case IsIos              = "isIos"
}

enum UserDialogs: String {
    case SigninIncorrect        = "Your login information is incorrect."
    case UsernameIsTaken        = "That email address is already taken."
    case CompleteRequireFields  = "Please complete all required fields."
    case RequireEmailAddress    = "Email address should be required."
    case SaveDataFailed         = "Saving data is failed. Please try again!"
    case ReportsFromDateError   = "This date should not greater than last date."
    case ReportsToDateError     = "This date should not greater than today date."
    case PasswordCurrentDiff    = "Current password is not same."
    case PasswordConfirmDiff    = "New password and confirm password is not same."
}

enum RippleScanDialogs: String{
    case CompleteRequireFields  = "Please input Ripple ID."
    case NotExistRipple         = "Your Ripple ID is incorrect."
    case NotBelongToUser        = "This is not your Ripple."
    case NotActiveCampaign      = "The Campaign which this Ripple belongs to is not active now."
    case RippleScanMessage      = "PLEASE ENTER RECIPIENT'S INFORMATION BELOW BEFORE HANDING OVER RIPPLE "
    case InputEmailFields       = "Please input email address to send."
    case NotToYou               = "You can't be recipient."
    case ConnectionBad          = "Network connection is bad now."
    case UserSignUP             = "The ricipient needs to sign up."
}

enum RippleLogKeys: String{
    case SenderName             = "SenderName"
    case SenderCity             = "SenderCity"
    case RecipientName          = "RecipientName"
    case RecipientCity          = "RecipientCity"
    case Date                   = "Date"
}

enum RecentLogKeys: String{
    case RippleName             = "RippleName"
    case RippleCode             = "RippleCode"
    case SenderName             = "SenderName"
    case SenderCity             = "SenderCity"
    case RecipientName          = "RecipientName"
    case RecipientCity          = "RecipientCity"
}
var MonthString = ["JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"]