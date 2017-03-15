//
//  LoginController.swift
//  eduku
//
//  Created by Mickey on 2/10/16.
//  Copyright © 2016 Mickey. All rights reserved.
//

import UIKit
import ParseFacebookUtilsV4
import FBSDKLoginKit
import Parse

class LoginController: UIViewController, UITextFieldDelegate {

    @IBOutlet weak var txtEmail: UITextField!
    @IBOutlet weak var txtPassword: UITextField!
    var bLoginSocial : Bool!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.view.dodo.topLayoutGuide = self.topLayoutGuide
        self.view.dodo.bottomLayoutGuide = self.bottomLayoutGuide
        self.view.dodo.style.bar.backgroundColor = Utils.UIColorFromRGB(0x000000).colorWithAlphaComponent(0.6)
        
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: "dismissKeyboard")
        view.addGestureRecognizer(tap)
    }
    
    override func viewWillAppear(animated: Bool) {
        self.navigationController!.navigationBarHidden = true
        super.viewWillAppear(animated)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        view.endEditing(true)
    }
    
    @IBAction func onLoginClicked(sender: AnyObject) {
        if (txtEmail.text?.characters.count < 1 || txtPassword.text?.characters.count < 1) {
            self.view.dodo.error(UserDialogs.CompleteRequireFields.rawValue)
            return
        }
        
        view.endEditing(true)
        txtEmail.text = txtEmail.text?.lowercaseString
        
        UIHelper.showHUD("Log in")
        
        PFUser.logInWithUsernameInBackground(txtEmail!.text!, password: txtPassword!.text!) {
            (user: PFUser?, error: NSError?) -> Void in
            if (user != nil) {
                Global.currentUser = User(_user: user!)
                UIHelper.hideHUD()
                self.performSegueWithIdentifier("LoginMainSegue", sender: self)
            } else {
                UIHelper.hideHUD()
                self.view.dodo.error(UserDialogs.SigninIncorrect.rawValue)
            }
        }

    }
    
    @IBAction func onFacebookClicked(sender: AnyObject) {
        let permissionsArray = ["email", "public_profile"]
        
        PFUser.logOut()
        PFFacebookUtils.logInInBackgroundWithReadPermissions(permissionsArray) {
            (user: PFUser?, error: NSError?) -> Void in
            if let user = user {
                UIHelper.showHUD("Log in")
                Utils.delay(UIHelper.HUDanimationDuration(), closure: {
                    if user.isNew {
                        print("User signed up and logged in through Facebook!")
                    
                        Global.currentUser = User(_user: PFUser.currentUser()!)
                    } else {
                        print("User logged in through Facebook!")
                        Global.currentUser = User(_user: user)
                    }
                
                    let requestParameters = ["fields": "id, email, first_name, last_name"]
                
                    let userDetails = FBSDKGraphRequest(graphPath: "me", parameters: requestParameters)
                
                    if (Global.currentUser.email != nil){
                        UIHelper.hideHUD()
                        self.performSegueWithIdentifier("LoginMainSegue", sender: self)
                        return
                    }
                
                    userDetails.startWithCompletionHandler {
                        (connection, result, error:NSError!) -> Void in
                    
                        if(error != nil)
                        {
                            print("\(error.localizedDescription)")
                            return
                        }
                        
                        if(result != nil)
                        {
                            Global.currentUser.fullname = result["first_name"] as? String
                            Global.currentUser.email = result["email"] as? String
                            Global.currentUser.save(false)
                        }
                    
                        self.bLoginSocial = true
                        UIHelper.hideHUD()
                        self.performSegueWithIdentifier("LoginSignupSegue", sender: self)
                    }
                })
            } else {
                print("Uh oh. The user cancelled the Facebook login.")
            }
        }
    }
    
    @IBAction func onSignupClicked(sender: AnyObject) {
        bLoginSocial = false
        self.performSegueWithIdentifier("LoginSignupSegue", sender: self)
    }
    
    @IBAction func onForgotPasswordClicked(sender: AnyObject) {
        let alert: UIAlertView = UIAlertView()
        
        alert.delegate = self
        
        alert.title = "Recover password"
        alert.alertViewStyle = UIAlertViewStyle.PlainTextInput
        let textfield = alert.textFieldAtIndex(0)
        textfield?.placeholder = "Type your email address here"
        alert.addButtonWithTitle("Cancel")
        alert.addButtonWithTitle("Send")
        
        alert.show();
    }
    
    func textFieldShouldReturn(textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
    
    func alertView(View: UIAlertView!, clickedButtonAtIndex buttonIndex: Int){
        
        switch buttonIndex{
            
        case 1:
            let email = View.textFieldAtIndex(0)!.text
            PFUser.requestPasswordResetForEmailInBackground(email!)
            NSLog("Send");
            
            break;
        default:
            NSLog("Default");
            break;
            //Some code here..
            
        }
    }
    
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        if (segue.identifier == "LoginSignupSegue"){
            let detailVC = segue.destinationViewController as! SignupController
            detailVC.bLogSocial = bLoginSocial
        }
    }
}
