//
//  MainController.swift
//  eduku
//
//  Created by Mickey on 2/10/16.
//  Copyright © 2016 Mickey. All rights reserved.
//

import UIKit
import Parse
import QRCodeReader
import AVFoundation

class MainController: UIViewController, ScanDialogDelegate, QRCodeReaderViewControllerDelegate, UICollectionViewDataSource, UICollectionViewDelegate {
    
    let dlgScan = ScanDialog()
    var m_scannedRipple : PFObject?
    var m_campaign : PFObject?
    @IBOutlet weak var tblView: UITableView!
    var imageArray = [UIImage]()
    var bSearch: Bool!, bAppear = false, colCount = 4, tblCount = 4
    @IBOutlet weak var colView: UICollectionView!
    var collectionViewLayout: CustomImageFlowLayout!
    
    lazy var reader: QRCodeReaderViewController = {
        let builder = QRCodeViewControllerBuilder { builder in
            builder.reader          = QRCodeReader(metadataObjectTypes: [AVMetadataObjectTypeQRCode])
            builder.showTorchButton = true
        }
        
        return QRCodeReaderViewController(builder: builder)
    }()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        dlgScan.delegate = self
        // Do any additional setup after loading the view.
        self.view.dodo.topLayoutGuide = self.topLayoutGuide
        self.view.dodo.bottomLayoutGuide = self.bottomLayoutGuide
        self.view.dodo.style.bar.backgroundColor = Utils.UIColorFromRGB(0x000000).colorWithAlphaComponent(0.6)
        
        collectionViewLayout = CustomImageFlowLayout(_direction: true)
        colView.collectionViewLayout = collectionViewLayout
        colView.backgroundColor = .whiteColor()
        colView.alwaysBounceHorizontal = true
        
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: "dismissKeyboard")
        view.addGestureRecognizer(tap)

    }
    
    override func viewDidAppear(animated: Bool) {
        super.viewDidAppear(animated)
        if (bAppear == false){
            bAppear = true
        }else{
            return
        }
        
        colCount = 4
        tblCount = 4
        imageArray = [UIImage]()
        
        let query1 = PFQuery(className: ParseClassKeys.RecentUpdates.rawValue)
        query1.getFirstObjectInBackgroundWithBlock({ (object: PFObject?, error: NSError?) -> Void in
            Global.recentUpdates = RecentUpdates(_recentUpdates: object!)
            self.tblCount = Global.recentUpdates.recentLog.count
            self.tblView.reloadData()
        })
        
        let query2 = PFQuery(className: ParseClassKeys.Photo.rawValue)
        query2.orderByDescending("createdAt")
        query2.limit = 15
        query2.findObjectsInBackgroundWithBlock { (objects: [PFObject]?, error: NSError?) -> Void in
            self.colCount = objects!.count
            self.colView.reloadData()
            if (objects!.count > 0){
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
    

    @IBAction func onScanRippleClicked(sender: AnyObject) {
        dlgScan.show(self)
    }
    
    func onScanClosed(sender: UIButton!){
        dlgScan.hide()
    }
    
    func onTapOk(result: String) {
        if (result.characters.count < 1){
            self.view.dodo.error(RippleScanDialogs.CompleteRequireFields.rawValue)
            return
        }
        
        self.view.endEditing(true)
        
        let query = PFQuery(className:ParseClassKeys.Ripple.rawValue)
        query.whereKey(ParseRipple.RippleCode.rawValue, equalTo:result)
        
        UIHelper.showHUD("Scan")
        query.findObjectsInBackgroundWithBlock {
            (objects: [PFObject]?, error: NSError?) -> Void in
            
            if error == nil {
                // The find succeeded.
                // Do something with the found objects
                if (objects?.count > 0) {
                    for ripple in objects! {
                        if ((Global.currentUser.userId)! == ripple[ParseRipple.UserId.rawValue] as! String){
                            let query = PFQuery(className:ParseClassKeys.Campaign.rawValue)
                            query.getObjectInBackgroundWithId(ripple[ParseRipple.CampaignId.rawValue] as! String) {
                                (campaign: PFObject?, error: NSError?) -> Void in
                                UIHelper.hideHUD()
                                if error == nil && campaign != nil {
                                    if (Utils.getDaysBetweenDates(NSDate(), secondDate: campaign![ParseCampaign.StartAt.rawValue] as! NSDate) > 0 && campaign![ParseCampaign.isActive.rawValue] as! Bool == true){
                                        self.m_scannedRipple = ripple
                                        self.m_campaign = campaign
                                        self.dlgScan.hide()
                                        self.performSegueWithIdentifier("MainScannedSegue", sender: self)
                                    }
                                    else{
                                        self.view.dodo.error(RippleScanDialogs.NotActiveCampaign.rawValue)
                                    }
                                } else {
                                    self.view.dodo.error(RippleScanDialogs.ConnectionBad.rawValue)
                                }
                            }
                        }
                        else{
                            UIHelper.hideHUD()
                            self.view.dodo.error(RippleScanDialogs.NotBelongToUser.rawValue)
                        }
                    }
                }
                else{
                    UIHelper.hideHUD()
                    self.view.dodo.error(RippleScanDialogs.NotExistRipple.rawValue)
                }
            } else {
                UIHelper.hideHUD()
                // Log details of the failure
                self.view.dodo.error(RippleScanDialogs.NotExistRipple.rawValue)
            }
        }
    }
    
    func onTapScan() {
        if QRCodeReader.supportsMetadataObjectTypes() {
            reader.modalPresentationStyle = .FormSheet
            reader.delegate               = self
            
            reader.completionBlock = { (result: QRCodeReaderResult?) in
                if let result = result {
                    print("Completion with result: \(result.value) of type \(result.metadataType)")
                }
            }
            
            presentViewController(reader, animated: true, completion: nil)
        }
        else {
            let alert = UIAlertController(title: "Error", message: "Reader not supported by the current device", preferredStyle: .Alert)
            alert.addAction(UIAlertAction(title: "OK", style: .Cancel, handler: nil))
            
            presentViewController(alert, animated: true, completion: nil)
        }
    }
    
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        if (segue.identifier == "MainScannedSegue"){
            let detailVC = segue.destinationViewController as! ScannedController
            detailVC.m_scannedRipple = m_scannedRipple
            detailVC.m_campaign = m_campaign
        }
        if (segue.identifier == "MainPopularSegue"){
            let detailVC = segue.destinationViewController as! PopularController
            detailVC.bSearch = bSearch
        }
        bAppear = false
    }
    
    // MARK: - QRCodeReader Delegate Methods
    
    func reader(reader: QRCodeReaderViewController, didScanResult result: QRCodeReaderResult) {
        self.dismissViewControllerAnimated(true, completion: {
            self.dlgScan.txtRippleID.text = result.value
            })
    }
    
    func readerDidCancel(reader: QRCodeReaderViewController) {
        self.dismissViewControllerAnimated(true, completion: nil)
    }
    
    func dismissKeyboard() {
        //Causes the view (or one of its embedded text fields) to resign the first responder status.
        view.endEditing(true)
    }

    @IBAction func onFollowedClicked(sender: AnyObject) {
        bSearch = false
        self.performSegueWithIdentifier("MainPopularSegue", sender: self)
    }

    @IBAction func onSearchRipplesClicked(sender: AnyObject) {
        bSearch = true
        self.performSegueWithIdentifier("MainPopularSegue", sender: self)
    }
    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return tblCount
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        
        let cell = tblView.dequeueReusableCellWithIdentifier("recentlog", forIndexPath: indexPath) as! MainCell
        
        if (Global.recentUpdates.recentLog.count - 1 < indexPath.row){
            cell.showIndicator()
        }else{
            cell.hideIndicator()
            let log = Global.recentUpdates.recentLog[indexPath.row] as! RecentLog
            cell.txtRipple.text = log.RippleName! + " / " + log.RippleCode!
            cell.txtSenderName.text = log.SenderName
            cell.txtSenderCity.text = log.SenderCity
            cell.txtRecipientName.text = log.RecipientName
            cell.txtRecipientCity.text = log.RecipientCity
        }
        cell.selectionStyle = .None;
        
        return cell
    }
    
    func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return colCount
    }
    
    func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCellWithReuseIdentifier("cell", forIndexPath: indexPath) as! ImageMainCollectionCell
        
        if (imageArray.count - 1 < indexPath.row){
            cell.showIndicator()
        }
        else{
            cell.hideIndicator()
            cell.imageView.image = imageArray[indexPath.row]
        }
        cell.layoutIfNeeded()
        return cell
    }
}
