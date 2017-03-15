//
//  User.swift
//  eduku
//
//  Created by Mickey on 2/10/16.
//  Copyright © 2016 Mickey. All rights reserved.
//

import Foundation
import Parse

class User {
    var userId: String?
    var username: String?
    var email: String?
    var fullname: String?
    var city: String?
    var country: String?
    var followRipples: NSMutableArray?
    
    var user:PFUser
    
    init(_user: PFUser) {
        user = _user
        
        userId = _user.objectId
        username = _user.username
        email = _user.email
        fullname = _user[ParseUserKeys.FullName.rawValue] as? String
    
        city = _user[ParseUserKeys.City.rawValue] as? String
        country = _user[ParseUserKeys.Country.rawValue] as? String
        followRipples = _user[ParseUserKeys.FollowRipples.rawValue] as? NSMutableArray
        if (followRipples == nil){
            followRipples = NSMutableArray()
        }
    }
    
    func logout() {
        PFUser.logOut()
    }
    
    func save(bBackground: Bool) {
        user[ParseUserKeys.FullName.rawValue] = fullname
        if (city == nil){
            city = ""
        }
        if (country == nil) {
            country = ""
        }
        if (followRipples == nil){
            followRipples = NSMutableArray()
        }
        user[ParseUserKeys.City.rawValue] = city
        user[ParseUserKeys.Country.rawValue] = country
        user[ParseUserKeys.FollowRipples.rawValue] = followRipples
        user[ParseUserKeys.Email.rawValue] = email
        user.username = email
        if (bBackground == true){
            user.saveInBackground()
        }else{
            do{
                try user.save()
            }catch{
            }
        }
    }
}
