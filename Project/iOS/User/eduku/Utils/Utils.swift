//
//  Utils.swift
//  eduku
//
//  Created by Mickey on 2/10/16.
//  Copyright © 2016 Mickey. All rights reserved.
//

import Foundation
import UIKit

struct Utils {
    static var cell_height = 45
    
    static func UIColorFromRGB(rgbValue: UInt) -> UIColor {
        return UIColor(
            red: CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0,
            green: CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0,
            blue: CGFloat(rgbValue & 0x0000FF) / 255.0,
            alpha: CGFloat(1.0)
        )
    }
    
    static func getJsonFromArray(_array: NSMutableArray) -> String {
        var string:String?
        var array: Array<AnyObject> = Array()
        
        for (var i = 0; i < _array.count; i++) {
            let log = _array[i] as! RippleLog
            let info: [String: AnyObject] = [
                RippleLogKeys.SenderName.rawValue : log.SenderName!,
                RippleLogKeys.SenderCity.rawValue : log.SenderCity!,
                RippleLogKeys.RecipientName.rawValue : log.RecipientName!,
                RippleLogKeys.RecipientCity.rawValue : log.RecipientCity!,
                RippleLogKeys.Date.rawValue : getStringFromNSDate(log.Date!)
            ]
            array.append(info)
        }
        
        do {
            let data = try NSJSONSerialization.dataWithJSONObject(array, options: [])
            string = String(data: data, encoding: NSUTF8StringEncoding)
        } catch let error as NSError {
            print(error.localizedDescription)
        }
        
        return string!
    }
    
    static func getArrayFromJson(_str: String) -> NSMutableArray {
        let array: NSMutableArray = []
        
        let data = _str.dataUsingEncoding(NSUTF8StringEncoding)
        
        do {
            let jsonObj = try NSJSONSerialization.JSONObjectWithData(data!, options: NSJSONReadingOptions(rawValue: 0))
            
            for json in jsonObj as! Array<AnyObject> {
                let log = RippleLog(
                    _senderName: json[RippleLogKeys.SenderName.rawValue] as! String,
                    _senderCity: json[RippleLogKeys.SenderCity.rawValue] as! String,
                    _recipientName: json[RippleLogKeys.RecipientName.rawValue] as! String,
                    _recipientCity: json[RippleLogKeys.RecipientCity.rawValue] as! String,
                    _date: getNSDateFromString(json[RippleLogKeys.Date.rawValue] as! String))
                    
                array.addObject(log)
            }
        } catch let error as NSError {
            print(error.localizedDescription)
        }
        
        return array
    }
    
    static func getJsonFromRecentLog(_array: NSMutableArray) -> String {
        var string:String?
        var array: Array<AnyObject> = Array()
        let length = _array.count > 15 ? 15 : _array.count
        
        for (var i = 0; i < length; i++) {
            let log = _array[i] as! RecentLog
            let info: [String: AnyObject] = [
                RecentLogKeys.RippleName.rawValue : log.RippleName!,
                RecentLogKeys.RippleCode.rawValue : log.RippleCode!,
                RecentLogKeys.SenderName.rawValue : log.SenderName!,
                RecentLogKeys.SenderCity.rawValue : log.SenderCity!,
                RecentLogKeys.RecipientName.rawValue : log.RecipientName!,
                RecentLogKeys.RecipientCity.rawValue : log.RecipientCity!]
            array.append(info)
        }
        
        do {
            let data = try NSJSONSerialization.dataWithJSONObject(array, options: [])
            string = String(data: data, encoding: NSUTF8StringEncoding)
        } catch let error as NSError {
            print(error.localizedDescription)
        }
        
        return string!
    }
    
    static func getRecentLogFromJson(_str: String) -> NSMutableArray{
        let array: NSMutableArray = []
        let data = _str.dataUsingEncoding(NSUTF8StringEncoding)
        
        do {
            let jsonObj = try NSJSONSerialization.JSONObjectWithData(data!, options: NSJSONReadingOptions(rawValue: 0))
            
            for json in jsonObj as! Array<AnyObject> {
                let log = RecentLog(
                    _rippleName: json[RecentLogKeys.RippleName.rawValue] as! String,
                    _rippleCode: json[RecentLogKeys.RippleCode.rawValue] as! String,
                    _senderName: json[RecentLogKeys.SenderName.rawValue] as! String,
                    _senderCity: json[RecentLogKeys.SenderCity.rawValue] as! String,
                    _recipientName: json[RecentLogKeys.RecipientName.rawValue] as! String,
                    _recipientCity: json[RecentLogKeys.RecipientCity.rawValue] as! String)
                array.addObject(log)
            }
        } catch let error as NSError {
            print(error.localizedDescription)
        }
        
        return array
    }
    
    static func getDaysBetweenDates(firstDate: NSDate, secondDate: NSDate) -> Int{
        // Assuming that firstDate and secondDate are defined
        // ...
        
        let calendar: NSCalendar = NSCalendar.currentCalendar()
        
        let flags = NSCalendarUnit.Day
        let components = calendar.components(flags, fromDate: firstDate, toDate: secondDate, options: [])
        
        return components.day  // This will return the number of day(s) between dates
    }
    
    static func getStringFromNSDate(_date: NSDate) -> String{
        let formatter = NSDateFormatter()
        formatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
        return formatter.stringFromDate(_date)
    }
    
    static func getNSDateFromString(_string: String) -> NSDate{
        let dateFormatter = NSDateFormatter()
        // this is imporant - we set our input date format to match our input string
        dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
        // voila!
        return dateFormatter.dateFromString(_string)!
    }
    
    static func getHhMmStringFromNSDate(_date: NSDate) -> String{
        let dateFormatter = NSDateFormatter()
        dateFormatter.dateFormat = "HH:mm"
        return dateFormatter.stringFromDate(_date)
    }
    
    static func getMonthDayStringFromNSDate(_date: NSDate) -> String{
        let calendar = NSCalendar.currentCalendar()
        let components = calendar.components([.Day , .Month , .Year], fromDate: _date)
        
        let month = components.month - 1
        let day = components.day
        
        if (day % 10  == 1){
            return MonthString[month] + "." + String(day) + "st"
        }
        else if (day % 10 == 2){
            return MonthString[month] + "." + String(day) + "nd"
        }
        else if (day % 10 == 3){
            return MonthString[month] + "." + String(day) + "rd"
        }
        else{
            return MonthString[month] + "." + String(day) + "th"
        }
    }
    
    static func delay(delay: CGFloat, closure: () -> ()) {
        dispatch_after(
            dispatch_time(
                DISPATCH_TIME_NOW,
                Int64(Double(delay) * Double(NSEC_PER_SEC))
            ), dispatch_get_main_queue(), closure)
    }
}