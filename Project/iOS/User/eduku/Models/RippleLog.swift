//
//  RippleLog.swift
//  eduku
//
//  Created by Mickey on 2/13/16.
//  Copyright © 2016 Mickey. All rights reserved.
//

import Foundation

class RippleLog{
    var SenderName: String?
    var SenderCity: String?
    var RecipientName: String?
    var RecipientCity: String?
    var Date : NSDate?
    
    init(_senderName: String, _senderCity: String, _recipientName: String, _recipientCity: String, _date : NSDate) {
        SenderName = _senderName
        SenderCity = _senderCity
        RecipientName = _recipientName
        RecipientCity = _recipientCity
        Date = _date
        
    }
}