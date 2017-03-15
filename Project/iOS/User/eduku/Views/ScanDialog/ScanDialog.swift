//
//  ScanDialog.swift
//  eduku
//
//  Created by Mickey on 2/11/16.
//  Copyright © 2016 Mickey. All rights reserved.
//

import UIKit

protocol ScanDialogDelegate {
    func onTapOk(result: String)
    func onTapScan()
}

class ScanDialog: UIView {
    var delegate: ScanDialogDelegate?
    var parent: UIViewController!
    let btnClose = UIButton()
    let blurEffectView = UIVisualEffectView()

    @IBOutlet weak var txtRippleID: UITextField!
    
    override init(frame: CGRect) {
        delegate = nil
        super.init(frame: frame)
        let customView = NSBundle.mainBundle().loadNibNamed("ScanDialog", owner: self, options: nil).first
        if(CGRectIsEmpty(frame)){
            self.bounds = (customView?.bounds)!
        }
        self.addSubview(customView as! UIView)
        
    }

    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)!
    }
    
    func show(parent: UIViewController!){
        self.parent = parent
        
        let blurEffect = UIBlurEffect(style: UIBlurEffectStyle.Dark)
        blurEffectView.effect = blurEffect
        blurEffectView.frame = parent.view.bounds
        blurEffectView.autoresizingMask = [.FlexibleWidth, .FlexibleHeight] // for supporting device rotation
        parent.view.addSubview(blurEffectView)

        self.center = parent.view.center
        self.frame = CGRect(x: self.frame.origin.x, y: self.frame.origin.y, width: 250, height: 100)
        parent.view.addSubview(self)
        
        btnClose.frame = CGRect(x: self.frame.origin.x + self.frame.width - 15, y: self.frame.origin.y - 15, width: 30, height: 30)
        btnClose.setImage(UIImage(named: "btn_close.png"), forState: UIControlState.Normal)
        btnClose.addTarget(parent, action: "onScanClosed:", forControlEvents: .TouchUpInside)
        parent.view.addSubview(btnClose)
        txtRippleID.becomeFirstResponder()
    }
    
    func hide(){
        blurEffectView.removeFromSuperview()
        btnClose.removeFromSuperview()
        self.removeFromSuperview()
    }
    
    @IBAction func onOkClicked(sender: AnyObject) {
        self.delegate?.onTapOk(self.txtRippleID.text!)
    }
    
    @IBAction func onCancelClicked(sender: AnyObject) {
        hide()
    }

    @IBAction func onScanClicked(sender: AnyObject) {
        self.delegate?.onTapScan()
    }
}
