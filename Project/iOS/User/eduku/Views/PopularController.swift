//
//  PopularController.swift
//  eduku
//
//  Created by Mickey on 2/15/16.
//  Copyright © 2016 Mickey. All rights reserved.
//

import UIKit
import Parse

extension PopularController: UISearchResultsUpdating {
    func updateSearchResultsForSearchController(searchController: UISearchController) {
        filterContentForSearchText(searchController.searchBar.text!)
    }
}

class Ripple {
    var name : String?
    var code : String?
    var id: String?
    var object: PFObject?
    var campaign: PFObject?
    var photo: UIImage?
    
    init(_object: PFObject, _callback: ()->()){
        name = _object[ParseRipple.RippleName.rawValue] as? String
        code = _object[ParseRipple.RippleCode.rawValue] as? String
        id = _object.objectId
        object = _object
        
        let query1 = PFQuery(className: ParseClassKeys.Campaign.rawValue)
        query1.getObjectInBackgroundWithId(_object[ParseRipple.CampaignId.rawValue] as! String, block: { (object: PFObject?, error: NSError?) -> Void in
            self.campaign = object
            _callback()
        })
        
        let query = PFQuery(className: ParseClassKeys.Photo.rawValue)
        query.whereKey(ParsePhoto.RippleId.rawValue, equalTo: id!)
        query.orderByDescending("createdAt")
        query.findObjectsInBackgroundWithBlock { (photos: [PFObject]?, er: NSError?) -> Void in
            if (er == nil){
                for photo in photos!{
                    if (photo[ParsePhoto.IsApproved.rawValue] as! Bool == false){
                        continue
                    }
                    let file = photo[ParsePhoto.Photo.rawValue] as! PFFile
                    file.getDataInBackgroundWithBlock({
                        (imageData: NSData?, error: NSError?) -> Void in
                        if (error == nil) {
                            self.photo = UIImage(data:imageData!)
                            _callback()
                        }
                    })
                    return
                }
                self.photo = UIImage(named: "img_none.png")
                _callback()
                return
            }
        }
    }
}

class PopularController: UIViewController, UITableViewDataSource, UITableViewDelegate {

    var m_followArray: NSMutableArray!
    @IBOutlet weak var tblView: UITableView!
    
    var m_selectedRipple: PFObject!
    var m_selectedCampaign: PFObject!
    var bSearch: Bool!
    let searchController = UISearchController(searchResultsController: nil)
    var filteredRipples = [Ripple]()
    var filterTemple = [Ripple]()
    var bPhoto:Bool!, bAppear: Bool!, tblCount = 6, bBack = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
        self.view.dodo.topLayoutGuide = self.topLayoutGuide
        self.view.dodo.bottomLayoutGuide = self.bottomLayoutGuide
        self.view.dodo.style.bar.backgroundColor = Utils.UIColorFromRGB(0x000000).colorWithAlphaComponent(0.6)
    }
    
    override func viewDidAppear(animated: Bool) {
        super.viewDidAppear(animated)
        if (bAppear == nil){
            bAppear = true
        }else{
            return
        }
        
        if (self.bSearch == false){
            self.m_followArray = Global.currentUser.followRipples
            tblCount = m_followArray.count
            for ripID in self.m_followArray{
                let query = PFQuery(className:ParseClassKeys.Ripple.rawValue)
                query.getObjectInBackgroundWithId(ripID as! String, block: { (ripple: PFObject?, error: NSError?) -> Void in
                    self.filteredRipples.append(Ripple(_object: ripple!,_callback: { self.tblView.reloadData()}))
                    self.tblView.reloadData()
                })
            }
        }
        else{
            self.searchController.searchResultsUpdater = self
            self.searchController.dimsBackgroundDuringPresentation = false
            self.definesPresentationContext = true
            self.tblView.tableHeaderView = self.searchController.searchBar
            
            let query = PFQuery(className: ParseClassKeys.Ripple.rawValue)
            query.findObjectsInBackgroundWithBlock({ (ripples: [PFObject]?, error: NSError?) -> Void in
                for ripple in ripples!{
                    self.filterTemple.append(Ripple(_object: ripple, _callback: {self.tblView.reloadData()}))
                }
                self.filteredRipples = self.filterTemple
                self.tblCount = self.filteredRipples.count
                self.tblView.reloadData()
            })
        }
        self.tblView.reloadData()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func filterContentForSearchText(searchText: String, scope: String = "All") {
        if (searchText != ""){
            filteredRipples = filterTemple.filter { ripple in
                return ripple.name!.lowercaseString.containsString(searchText.lowercaseString) || ripple.code!.lowercaseString.containsString(searchText.lowercaseString)
            }
        }
        else{
            filteredRipples = filterTemple
        }
        
        tblCount = filteredRipples.count
        tblView.reloadData()
    }
    
    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return tblCount
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        
        let cell = tblView.dequeueReusableCellWithIdentifier("ripplefollow", forIndexPath: indexPath) as! PopularCell
        
        if (filteredRipples.count - 1 < indexPath.row){
            cell.showIndicator()
            cell.userInteractionEnabled = false
        }else {
            cell.txtRippleName.text = filteredRipples[indexPath.row].name
            cell.txtRippleCode.text = filteredRipples[indexPath.row].code
            cell.layoutMargins = UIEdgeInsetsZero
            cell.m_Ripple = filteredRipples[indexPath.row].object
            
            if (filteredRipples[indexPath.row].campaign != nil){
                cell.m_Campaign = filteredRipples[indexPath.row].campaign
                cell.userInteractionEnabled = true
            }
        
            if (filteredRipples[indexPath.row].photo != nil){
                cell.hideImageIndicator()
                cell.btnPhoto.setBackgroundImage(filteredRipples[indexPath.row].photo, forState: .Normal)
            }else{
                cell.showImageIndicator()
            }
    
            cell.hideIndicator()
        }
        return cell
    }
    
    @IBAction func onBtnPinClicked(sender: UIButton) {
        let cell = sender.superview!.superview!.superview as! PopularCell
        m_selectedRipple = cell.m_Ripple
        m_selectedCampaign = cell.m_Campaign
        self.bPhoto = false
        self.performSegueWithIdentifier("PopularIndividualSegue", sender: self)
    }
    
    @IBAction func onBtnPhotoClicked(sender: UIButton) {
        let cell = sender.superview!.superview!.superview as! PopularCell
        m_selectedRipple = cell.m_Ripple
        m_selectedCampaign = cell.m_Campaign
        self.bPhoto = true
        self.performSegueWithIdentifier("PopularIndividualSegue", sender: self)
    }
    
    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        let cell = tblView.cellForRowAtIndexPath(indexPath) as! PopularCell
        m_selectedRipple = cell.m_Ripple
        m_selectedCampaign = cell.m_Campaign
        tblView.deselectRowAtIndexPath(indexPath, animated: true)
        self.bPhoto = false
        self.performSegueWithIdentifier("PopularIndividualSegue", sender: self)
    }
    
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        if (segue.identifier == "PopularIndividualSegue"){
            let detailVC = segue.destinationViewController as! IndividualController;
            detailVC.m_Ripple = m_selectedRipple
            detailVC.m_campaign = m_selectedCampaign
            detailVC.bPhoto = bPhoto
        }
    }
    @IBAction func onBackClicked(sender: AnyObject) {
        if (bBack == false){
            bBack = true
            self.navigationController!.popViewControllerAnimated(true)
        }
    }
}
