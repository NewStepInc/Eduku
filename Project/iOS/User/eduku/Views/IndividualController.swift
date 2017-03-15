//
//  IndividualController.swift
//  eduku
//
//  Created by Mickey on 2/13/16.
//  Copyright © 2016 Mickey. All rights reserved.
//

import UIKit
import Parse

class IndividualController: UIViewController, UITableViewDataSource, UITableViewDelegate, UICollectionViewDataSource,UICollectionViewDelegate {
    
    var m_Ripple : PFObject!
    var m_campaign : PFObject!
    var bFollow : Bool?
    var m_logArray: NSMutableArray!
    
    @IBOutlet weak var txtRippleName: UILabel!
    @IBOutlet weak var txtRippleCode: UILabel!
    @IBOutlet weak var txtLeftTime: UILabel!
    @IBOutlet weak var txtDestination: UILabel!
    @IBOutlet weak var txtPhoneNumber: UILabel!
    @IBOutlet weak var btnFollow: UIButton!
    @IBOutlet weak var btnFollowWidthCS: NSLayoutConstraint!
    @IBOutlet weak var btnFollowTrailCS: NSLayoutConstraint!
    @IBOutlet weak var tblView: UITableView!
    @IBOutlet weak var colView: UICollectionView!
    @IBOutlet weak var btnRipple: UIButton!
    @IBOutlet weak var IndFollow: UIActivityIndicatorView!
    
    var collectionViewLayout: CustomImageFlowLayout!
    var imageArray = [UIImage]()
    var bPhoto : Bool!, bAppear: Bool!, colCount = 4, tblCount = 8, bNone = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        IndFollow.hidden = true
        if let log = m_Ripple[ParseRipple.RippleLog.rawValue] as? String{
            m_logArray = Utils.getArrayFromJson(log)
        }
        else{
            m_logArray = NSMutableArray()
        }
        
        txtRippleName.text = m_Ripple[ParseRipple.RippleName.rawValue] as! String + " RIPPLE"
        txtRippleCode.text = m_Ripple[ParseRipple.RippleCode.rawValue] as? String
        txtDestination.text = m_campaign[ParseCampaign.Destination.rawValue] as? String
        txtPhoneNumber.text = m_campaign[ParseCampaign.PhoneNumber.rawValue] as? String
        txtLeftTime.text = String(Utils.getDaysBetweenDates(NSDate(), secondDate: m_campaign[ParseCampaign.StartAt.rawValue] as! NSDate)) + " Days"
        
        if Global.currentUser.followRipples!.containsObject(m_Ripple.objectId!) == true{
            bFollow = false
            setFollow()
        }
        else{
            bFollow = true
            setFollow()
        }
        
        collectionViewLayout = CustomImageFlowLayout(_direction: false)
        colView.collectionViewLayout = collectionViewLayout
        colView.backgroundColor = .whiteColor()
        
        colView.hidden = !bPhoto
        tblView.hidden = bPhoto
        
        if (bPhoto == false){
            btnRipple.setBackgroundImage(UIImage(named: "btn_seephotos.png"), forState: .Normal)
        }else{
            btnRipple.setBackgroundImage(UIImage(named: "btn_where.png"), forState: .Normal)
        }
        // Do any additional setup after loading the view.
    }
    
    override func viewWillAppear(animated: Bool) {
        self.navigationController!.navigationBarHidden = true
        super.viewWillAppear(animated)
    }
    
    override func viewDidAppear(animated: Bool) {
        super.viewDidAppear(animated)
        if (bAppear == nil){
            bAppear = true
        }else{
            return
        }
        
        let query = PFQuery(className: ParseClassKeys.Photo.rawValue)
        query.whereKey(ParsePhoto.RippleId.rawValue, equalTo: self.m_Ripple.objectId!)
        query.orderByDescending("createdAt")
        query.findObjectsInBackgroundWithBlock { (objects: [PFObject]?, error: NSError?) -> Void in
            if (objects!.count < 1){
                self.colCount = 1
                self.bNone = true
                self.colView.reloadData()
            }else{
                self.colCount = objects!.count
                self.colView.reloadData()
                self.GetPhotoFromFile(objects!, _index: 0)
            }
        }
    }
    
    func GetPhotoFromFile(_objects: [PFObject], _index: Int){
        if (_objects[_index][ParsePhoto.IsApproved.rawValue] as! Bool == false){
            self.colCount--
            self.colView.reloadData()
            if (_index != _objects.count - 1){
                self.GetPhotoFromFile(_objects, _index: _index + 1)
            }else{
                self.colCount = 1
                self.bNone = true
                self.colView.reloadData()
            }
            return
        }
        let file = _objects[_index][ParsePhoto.Photo.rawValue] as! PFFile
        
        file.getDataInBackgroundWithBlock({
            (imageData: NSData?, error: NSError?) -> Void in
            if (error == nil){
                let image = UIImage(data:imageData!)
                self.imageArray.append(image!)
                self.colView.reloadData()
                if (_index != _objects.count - 1){
                    self.GetPhotoFromFile(_objects, _index: _index + 1)
                }
            }
        })

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return colCount
    }
    
    func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCellWithReuseIdentifier("cell", forIndexPath: indexPath) as! ImageCollectionViewCell
        
        if (bNone == true){
            cell.imageView.image = UIImage(named: "img_none.png")
        }
        else if (imageArray.count - 1 < indexPath.row){
            cell.showIndicator()
        }
        else{
            cell.hideIndicator()
            cell.imageView.image = imageArray[indexPath.row]
        }

        cell.layoutIfNeeded()
        return cell
    }
    
    @IBAction func onFollowClicked(sender: AnyObject) {
        btnFollow.hidden = true
        IndFollow.hidden = false
        IndFollow.startAnimating()
        if (bFollow == false){
            bFollow = true
            setFollow()
            Global.currentUser.followRipples!.removeObject(m_Ripple.objectId!)
        }
        else{
            bFollow = false
            setFollow()
            Global.currentUser.followRipples!.addObject(m_Ripple.objectId!)
        }
        Global.currentUser.user[ParseUserKeys.FollowRipples.rawValue] = Global.currentUser.followRipples
        Global.currentUser.user.saveInBackgroundWithBlock({ (r: Bool, er: NSError?) -> Void in
            self.IndFollow.stopAnimating()
            self.IndFollow.hidden = true
            self.btnFollow.hidden = false
        })
    }
    
    func setFollow(){
        if (bFollow == true){
            btnFollow.setBackgroundImage(UIImage(named: "btn_followbutton.png"), forState: .Normal)
            btnFollowWidthCS.constant = 105
            btnFollowTrailCS.constant = 36
        }
        else{
            btnFollow.setBackgroundImage(UIImage(named: "btn_unfollowbutton.png"), forState: .Normal)
            btnFollowWidthCS.constant = 129
            btnFollowTrailCS.constant = 22
        }
    }
    
    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return m_logArray.count
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        
        let cell = tblView.dequeueReusableCellWithIdentifier("ripplelog", forIndexPath: indexPath) as! IndividualCell
        
        let log = m_logArray[indexPath.row] as! RippleLog

        cell.selectionStyle = UITableViewCellSelectionStyle.None
        cell.txtSenderName.text = log.SenderName
        cell.txtSenderCity.text = log.SenderCity
        cell.txtRecipientName.text = log.RecipientName
        cell.txtRecipientCity.text = log.RecipientCity
        cell.txtDate.text = Utils.getMonthDayStringFromNSDate(log.Date!)
        cell.txtTime.text = Utils.getHhMmStringFromNSDate(log.Date!)
        return cell
    }
    
    @IBAction func onBackClicked(sender: AnyObject) {
        let n: Int! = self.navigationController!.viewControllers.count
        let myUIViewController = self.navigationController!.viewControllers[n-2] as? ScannedController
        if (myUIViewController == nil){
            self.navigationController!.popViewControllerAnimated(true)
        }else{
            self.navigationController!.popToViewController(self.navigationController!.viewControllers[n-3], animated: true)
        }
    }
    @IBAction func onBtnRipplePhotosClicked(sender: AnyObject) {
        self.bPhoto = !self.bPhoto
        if (self.bPhoto == false){
            btnRipple.setBackgroundImage(UIImage(named: "btn_seephotos.png"), forState: .Normal)
            colView.alpha = 1
            tblView.alpha = 0
        }else{
            colView.alpha = 0
            tblView.alpha = 1
            btnRipple.setBackgroundImage(UIImage(named: "btn_where.png"), forState: .Normal)
        }
        self.tblView.hidden = self.bPhoto
        self.colView.hidden = !self.bPhoto
        UIView.animateWithDuration(1, delay: 0, options: UIViewAnimationOptions.TransitionCrossDissolve, animations: {
            if (self.colView.alpha == 0){
                self.colView.alpha = 1
                self.tblView.alpha = 0
            }else{
                self.colView.alpha = 0
                self.tblView.alpha = 1
            }
            }, completion: nil)
    }
}
