//
//  UIHelper.swift
//  eduku
//
//  Created by Mickey on 2/10/16.
//  Copyright © 2016 Mickey. All rights reserved.
//

import Foundation
import M13ProgressSuite

struct UIHelper{
    static var hud: AnyObject?
    
    static func showHUD(msg: String) {
        let window = UIApplication.sharedApplication().keyWindow!
        
        if(hud == nil) {
            hud = M13ProgressHUD(progressView: M13ProgressViewRing())
            (hud as! M13ProgressHUD).progressViewSize = CGSizeMake(40.0, 40.0);
            (hud as! M13ProgressHUD).animationPoint =  CGPointMake((UIScreen.mainScreen().bounds.size.width / 2), UIScreen.mainScreen().bounds.size.height / 2)
            (hud as! M13ProgressHUD).backgroundColor = UIColor.whiteColor().colorWithAlphaComponent(0.3)
            (hud as! M13ProgressHUD).indeterminate = true
            
            window.addSubview((hud as! M13ProgressHUD))
        }
        
        (hud as! M13ProgressHUD).status = msg
        window.bringSubviewToFront((hud as! M13ProgressHUD))
        
        (hud as! M13ProgressHUD).show(true)
    }
    
    static func HUDanimationDuration() -> CGFloat{
        if(hud != nil) {
            return (hud as! M13ProgressHUD).animationDuration
        } else {
            return 1.0
        }
    }
    
    static func setHUDStatus(msg: String) {
        (hud as! M13ProgressHUD).status = msg
    }
    
    static func hideHUD() {
        (hud as! M13ProgressHUD).hide(true)
    }
    
}