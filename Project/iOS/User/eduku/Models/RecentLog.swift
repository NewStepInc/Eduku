//
//  RecentLog.swift
//  eduku
//
//  Created by Mickey on 2/17/16.
//  Copyright © 2016 Mickey. All rights reserved.
//

import Foundation

class RecentLog{
    var RippleName: String?
    var RippleCode: String?
    var SenderName: String?
    var SenderCity: String?
    var RecipientName: String?
    var RecipientCity: String?
    
    init(_rippleName: String, _rippleCode: String, _senderName: String, _senderCity: String, _recipientName: String, _recipientCity: String) {
        RippleName = _rippleName
        RippleCode = _rippleCode
        SenderName = _senderName
        SenderCity = _senderCity
        RecipientName = _recipientName
        RecipientCity = _recipientCity
    }
}