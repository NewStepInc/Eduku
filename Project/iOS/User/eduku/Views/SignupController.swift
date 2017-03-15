//
//  SignupController.swift
//  eduku
//
//  Created by Mickey on 2/10/16.
//  Copyright © 2016 Mickey. All rights reserved.
//

import UIKit
import Parse

class SignupController: UIViewController, UITextFieldDelegate {

    @IBOutlet weak var txtName: UITextField!
    @IBOutlet weak var txtCity: UITextField!
    @IBOutlet weak var txtCountry: UITextField!
    @IBOutlet weak var txtEmail: UITextField!
    @IBOutlet weak var txtPassword: UITextField!
    @IBOutlet weak var cityVSCons2: NSLayoutConstraint!
    var bLogSocial: Bool!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.view.dodo.topLayoutGuide = self.topLayoutGuide
        self.view.dodo.bottomLayoutGuide = self.bottomLayoutGuide
        self.view.dodo.style.bar.backgroundColor = Utils.UIColorFromRGB(0x000000).colorWithAlphaComponent(0.6)
        
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: "dismissKeyboard")
        view.addGestureRecognizer(tap)
    }

    override func viewWillAppear(animated: Bool) {
        if (bLogSocial == true){
            cityVSCons2.constant = 1
            txtName.text = Global.currentUser.fullname
            txtEmail.text = Global.currentUser.email
            txtName.userInteractionEnabled = false
            txtEmail.userInteractionEnabled = false
            txtPassword.hidden = true
        }else{
            cityVSCons2.constant = 32
        }
    }
    
    func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        view.endEditing(true)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func onSignupClicked(sender: AnyObject) {
        if (txtName.text?.characters.count < 1 || txtEmail.text?.characters.count < 1 || txtCity.text?.characters.count < 1 || txtCountry.text?.characters.count < 1){
            self.view.dodo.error(UserDialogs.CompleteRequireFields.rawValue)
            return
        }
        if (bLogSocial == false && txtPassword.text?.characters.count < 1){
            self.view.dodo.error(UserDialogs.CompleteRequireFields.rawValue)
            return
        }

        if (bLogSocial == true){
            Global.currentUser.city = txtCity.text
            Global.currentUser.country = txtCountry.text
            Global.currentUser.save(false)
            
            let transition = CATransition()
            transition.duration = 0.3
            transition.type = "alignedFlip"
            transition.subtype = kCATransitionFromLeft
            transition.delegate = self
            self.view.layer.addAnimation(transition, forKey: kCATransition)
            
            let query = PFQuery(className: ParseClassKeys.RecentUpdates.rawValue)
            do{
                Global.recentUpdates = RecentUpdates(_recentUpdates: try query.getFirstObject())
                self.performSegueWithIdentifier("SignupMainSegue", sender: self)
                return
            }catch{
            }
        }
        
        view.endEditing(true)
        txtEmail.text = txtEmail.text?.lowercaseString
        
        let user = PFUser()
        user.username = txtEmail!.text
        user.email = txtEmail!.text
        user.password = txtPassword!.text
        user[ParseUserKeys.FullName.rawValue] = txtName!.text
        user[ParseUserKeys.City.rawValue] = txtCity!.text
        user[ParseUserKeys.Country.rawValue] = txtCountry!.text
        
        
        UIHelper.showHUD("Sign up")
        
        user.signUpInBackgroundWithBlock {
            (succeeded: Bool, error: NSError?) -> Void in
            if error == nil {
                Global.currentUser = User(_user: PFUser.currentUser()!)
                
                let transition = CATransition()
                transition.duration = 0.3
                transition.type = "alignedFlip"
                transition.subtype = kCATransitionFromLeft
                transition.delegate = self
                self.view.layer.addAnimation(transition, forKey: kCATransition)
                let query = PFQuery(className: ParseClassKeys.RecentUpdates.rawValue)
                do{
                    Global.recentUpdates = RecentUpdates(_recentUpdates: try query.getFirstObject())
                    UIHelper.hideHUD()
                    self.performSegueWithIdentifier("SignupMainSegue", sender: self)
                }catch{
                }
            } else {
                var message = error!.localizedDescription
                message.replaceRange(message.startIndex...message.startIndex, with: String(message[message.startIndex]).capitalizedString)
                if(error?.code == 202) {
                    self.view.dodo.error(UserDialogs.UsernameIsTaken.rawValue)
                } else {
                    self.view.dodo.error(message)
                }
                
                UIHelper.hideHUD()
            }
        }

    }

    func textFieldShouldReturn(textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
    @IBAction func onBackClicked(sender: AnyObject) {
        self.navigationController!.popViewControllerAnimated(true)
    }
}
