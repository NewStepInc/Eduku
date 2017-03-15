//
//  RecentUpdates.swift
//  eduku
//
//  Created by Mickey on 2/17/16.
//  Copyright © 2016 Mickey. All rights reserved.
//

import Foundation
import Parse

class RecentUpdates {
    var recentLog: NSMutableArray
    
    var recentUpdates: PFObject!
    
    init(){
        recentLog = NSMutableArray()
    }
    init(_recentUpdates: PFObject) {
        recentUpdates = _recentUpdates
        if let log = _recentUpdates[ParseRecentUpdates.Log.rawValue] as? String{
            recentLog = Utils.getRecentLogFromJson(log)
        }
        else{
            recentLog = NSMutableArray()
        }
    }
    
    func save() {
        recentUpdates[ParseRecentUpdates.Log.rawValue] = Utils.getJsonFromRecentLog(recentLog)
        do{
            try recentUpdates.save()
        }catch{
        }
    }
}
