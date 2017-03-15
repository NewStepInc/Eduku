//
//  ScannedController.swift
//  eduku
//
//  Created by Mickey on 2/11/16.
//  Copyright © 2016 Mickey. All rights reserved.
//

import UIKit
import Parse

class ScannedController: UIViewController,UINavigationControllerDelegate,  UIImagePickerControllerDelegate {
    var m_scannedRipple : PFObject!
    var m_campaign : PFObject!
    var m_recipientID: String!
    var bCalling: Bool!
    @IBOutlet weak var txtRippleName: UILabel!
    @IBOutlet weak var txtRippleCode: UILabel!
    @IBOutlet weak var txtMessage: UILabel!
    @IBOutlet weak var txtEmail: UITextField!
    @IBOutlet weak var txtName: UITextField!
    @IBOutlet weak var txtCity: UITextField!
    @IBOutlet weak var txtCountry: UITextField!
    @IBOutlet weak var btnUploadPhoto: UIButton!
    @IBOutlet weak var btnSendRipple: UIButton!
    @IBOutlet weak var conUploadVS1: NSLayoutConstraint!
    @IBOutlet weak var conUploadVS2: NSLayoutConstraint!
    
    var m_recipient: PFUser!
    var imagePicker: UIImagePickerController!
    var imagePhoto: UIImage?
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.view.dodo.topLayoutGuide = self.topLayoutGuide
        self.view.dodo.bottomLayoutGuide = self.bottomLayoutGuide
        self.view.dodo.style.bar.backgroundColor = Utils.UIColorFromRGB(0x000000).colorWithAlphaComponent(0.6)

        conUploadVS2.active = false
        txtRippleName.text = (m_scannedRipple[ParseRipple.RippleName.rawValue] as? String)! + " RIPPLE"
        txtRippleCode.text = m_scannedRipple[ParseRipple.RippleCode.rawValue] as? String
        txtMessage.text = RippleScanDialogs.RippleScanMessage.rawValue + txtRippleCode.text!
        
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: "dismissKeyboard")
        view.addGestureRecognizer(tap)
        
        bCalling = false
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func onUploadPhotoClicked(sender: AnyObject) {
        imagePicker =  UIImagePickerController()
        imagePicker.delegate = self
        imagePicker.sourceType = .Camera
        
        presentViewController(imagePicker, animated: true, completion: nil)
    }
    
    func imagePickerController(picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : AnyObject]) {
        imagePicker.dismissViewControllerAnimated(true, completion: nil)
        
        imagePhoto = info[UIImagePickerControllerOriginalImage] as? UIImage
    }
    
    @IBAction func onSendRippleClicked(sender: AnyObject) {
        if (bCalling == true) {
            return
        }
        bCalling = true
        if (txtName.userInteractionEnabled == true){
            if (txtName.text?.characters.count < 1 || txtCity.text?.characters.count < 1 || txtCountry.text?.characters.count < 1){
                self.view.dodo.error(UserDialogs.CompleteRequireFields.rawValue)
                bCalling = false
                return
            }
            
            UIHelper.showHUD("Sign up")
            let user = PFUser()
            user.username = self.txtEmail!.text
            user.email = self.txtEmail!.text
            user.password = "12345678"
            user[ParseUserKeys.FullName.rawValue] = self.txtName!.text
            user[ParseUserKeys.City.rawValue] = self.txtCity!.text
            user[ParseUserKeys.Country.rawValue] = self.txtCountry!.text
            
            user.signUpInBackgroundWithBlock {
                (succeeded: Bool, error: NSError?) -> Void in
                UIHelper.hideHUD()
                if error == nil {
                    UIHelper.showHUD("Sending")
                    Utils.delay(UIHelper.HUDanimationDuration(), closure: {
                        PFCloud.callFunctionInBackground("sendEmails", withParameters:
                            ["recipient" : user.email!,
                                "message" : "Hi " + self.txtName!.text! + ",\n\nWe are happy to announce that you are invited by " + Global.currentUser.email! + " so that you were signed up \"Ripples of Hope\" automatically.\nYour password: 12345678\nPlease download the app from App Store or Google Play Store.\n\nThanks,\nRipples of Hope Team"]) {
                                    (response: AnyObject?, error: NSError?) -> Void in
                                    if (response as! String == "Email sent!"){
                                        self.m_recipientID = user.objectId
                                        self.m_recipient = user
                                        self.SendRipple()
                                    }
                                    else{
                                        UIHelper.hideHUD()
                                        self.bCalling = false
                                        self.view.dodo.error(RippleScanDialogs.ConnectionBad.rawValue)
                                    }
                        }
                    })
                } else {
                    self.bCalling = false
                    if(error?.code == 202) {
                        self.view.dodo.error(UserDialogs.UsernameIsTaken.rawValue)
                    } else {
                        self.view.dodo.error(UserDialogs.CompleteRequireFields.rawValue)
                    }
                }
            }
        }
        else{
            UIHelper.showHUD("Sending")
            Utils.delay(UIHelper.HUDanimationDuration(), closure: {
                self.SendRipple()
            })
        }
    }
    
    func SendRipple(){
        if (imagePhoto != nil){
            let imageData = UIImageJPEGRepresentation(imagePhoto!, 0.75)!
            let file = PFFile(data: imageData)!
            
            let object = PFObject(className: ParseClassKeys.Photo.rawValue)
            object[ParsePhoto.Photo.rawValue] = file
            object[ParsePhoto.RippleId.rawValue] = m_scannedRipple.objectId
            object[ParsePhoto.IsApproved.rawValue] = true
            object[ParsePhoto.IsIos.rawValue] = true
            do{
                try object.save()
            }catch{
                self.view.dodo.error(RippleScanDialogs.ConnectionBad.rawValue)
                UIHelper.hideHUD()
                bCalling = false
                return
            }
        }
        let log = RippleLog(_senderName: Global.currentUser.fullname!, _senderCity: Global.currentUser.city!, _recipientName: self.txtName.text!, _recipientCity: self.txtCity.text!, _date: NSDate())
        let logArray: NSMutableArray!
        if let _logJson = self.m_scannedRipple[ParseRipple.RippleLog.rawValue] as? String{
            logArray = Utils.getArrayFromJson(_logJson)
            logArray.addObject(log)
        }
        else{
            logArray = NSMutableArray(array: [log])
        }
        self.m_scannedRipple[ParseRipple.RippleLog.rawValue] = Utils.getJsonFromArray(logArray)
        self.m_scannedRipple[ParseRipple.UserId.rawValue] = self.m_recipientID
        do{
            try self.m_scannedRipple.save()
        }catch{
            self.view.dodo.error(RippleScanDialogs.ConnectionBad.rawValue)
            UIHelper.hideHUD()
            bCalling = false
            return
        }
            
        let recentLog = RecentLog(_rippleName: self.m_scannedRipple[ParseRipple.RippleName.rawValue] as!String, _rippleCode: self.m_scannedRipple[ParseRipple.RippleCode.rawValue] as! String, _senderName: Global.currentUser.fullname!, _senderCity: Global.currentUser.city!, _recipientName: self.txtName.text!, _recipientCity: self.txtCity.text!)
        Global.recentUpdates.recentLog.insertObject(recentLog, atIndex: 0)
        Global.recentUpdates.save()
            
        if (Global.currentUser.followRipples!.containsObject(self.m_scannedRipple.objectId!) == false){
            Global.currentUser.followRipples!.addObject(self.m_scannedRipple.objectId!)
            Global.currentUser.save(false)
        }
        
        var followRipples = self.m_recipient[ParseUserKeys.FollowRipples.rawValue] as? NSMutableArray
        if (followRipples == nil){
            followRipples = NSMutableArray()
        }
        
        if followRipples!.containsObject(self.m_scannedRipple.objectId!) == false{
            followRipples!.addObject(self.m_scannedRipple.objectId!)
        }
        
        PFCloud.callFunctionInBackground("userMigration", withParameters:
            ["email" : self.txtEmail.text!.lowercaseString, "follow" : followRipples!]) {
                (response: AnyObject?, error: NSError?) -> Void in
                UIHelper.hideHUD()
                self.performSegueWithIdentifier("ScannedIndividualSegue", sender: self)
                self.bCalling = false
        }
    }
    
    @IBAction func onGoClicked(sender: AnyObject) {
        if (txtEmail.text?.characters.count < 1){
            self.view.dodo.error(RippleScanDialogs.InputEmailFields.rawValue)
            return
        }
        if (txtEmail.text?.lowercaseString == Global.currentUser.email){
            self.view.dodo.error(RippleScanDialogs.NotToYou.rawValue)
            return
        }
        
        view.endEditing(true)
        
        let query = PFUser.query()!
        query.whereKey("email", equalTo:txtEmail.text!.lowercaseString)
        UIHelper.showHUD("Find User")
        
        query.findObjectsInBackgroundWithBlock {
            (objects: [PFObject]?, error: NSError?) -> Void in
            
            if error == nil {
                // The find succeeded.
                // Do something with the found objects
                UIHelper.hideHUD()
                self.btnSendRipple.alpha = 1
                self.btnSendRipple.userInteractionEnabled = true
                
                if (objects?.count > 0) {
                    for user in objects! {
                        self.m_recipient = user as! PFUser
                        self.m_recipientID = user.objectId
                        self.txtName.userInteractionEnabled = false
                        self.txtCity.userInteractionEnabled = false
                        self.txtCountry.userInteractionEnabled = false
                        self.txtName.text = user[ParseUserKeys.FullName.rawValue] as? String
                        self.txtCity.text = user[ParseUserKeys.City.rawValue] as? String
                        self.txtCountry.text = user[ParseUserKeys.Country.rawValue] as? String
                    }
                }
                else{
                    self.txtName.userInteractionEnabled = true
                    self.txtCity.userInteractionEnabled = true
                    self.txtCountry.userInteractionEnabled = true
                    self.txtName.text = nil
                    self.txtCountry.text = nil
                    self.txtCity.text = nil
                    self.view.dodo.error(RippleScanDialogs.UserSignUP.rawValue)
                }
            } else {
                UIHelper.hideHUD()
                // Log details of the failure
                self.view.dodo.error(RippleScanDialogs.ConnectionBad.rawValue)
            }
        }

        if (self.conUploadVS1.active == false){
            return
        }
        self.view.layoutIfNeeded()

        UIView.animateWithDuration(1.5, delay: 0, options: UIViewAnimationOptions.CurveEaseOut, animations: {
            self.conUploadVS1.active = false
            self.conUploadVS2.active = true

            self.view.layoutIfNeeded()
            }, completion: nil)

        txtName.hidden = false
        txtCity.hidden = false
        txtCountry.hidden = false
        txtName.frame.origin.x = view.frame.width
        txtCity.frame.origin.x = view.frame.width
        txtCountry.frame.origin.x = view.frame.width
        
        UIView.animateWithDuration(0.3, delay: 0, options: UIViewAnimationOptions.CurveEaseOut, animations: {
            self.txtName.frame.origin.x = 40
            self.txtCity.frame.origin.x = 40
            self.txtCountry.frame.origin.x = 40
        }, completion: nil)
        
    }
    @IBAction func onBackClicked(sender: AnyObject) {
        self.navigationController!.popViewControllerAnimated(true)
    }
    
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        if (segue.identifier == "ScannedIndividualSegue"){
            let detailVC = segue.destinationViewController as! IndividualController;
            detailVC.m_Ripple = m_scannedRipple
            detailVC.m_campaign = m_campaign
            detailVC.bPhoto = false
        }
    }
    
    func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        view.endEditing(true)
    }
    
}
