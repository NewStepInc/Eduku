//
//  PopularCell.swift
//  eduku
//
//  Created by Mickey on 2/15/16.
//  Copyright © 2016 Mickey. All rights reserved.
//

import UIKit
import Parse

class PopularCell: UITableViewCell {

    @IBOutlet weak var txtRippleName: UILabel!
    @IBOutlet weak var txtRippleCode: UILabel!
    @IBOutlet weak var btnPin: UIButton!
    @IBOutlet weak var btnPhoto: UIButton!
    @IBOutlet weak var indicator: UIActivityIndicatorView!
    @IBOutlet weak var viewContent: UIView!
    @IBOutlet weak var imgIndicator: UIActivityIndicatorView!
    var m_Ripple: PFObject!, m_Campaign: PFObject!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        btnPhoto.layer.cornerRadius = 0.5 * btnPhoto.bounds.size.width
        btnPhoto.clipsToBounds = true
    }

    override func setSelected(selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func showIndicator(){
        indicator.hidden = false
        indicator.startAnimating()
    }
    
    func hideIndicator(){
        viewContent.hidden = false
        indicator.hidden = true
        indicator.stopAnimating()
    }
    
    func showImageIndicator(){
        imgIndicator.hidden = false
        imgIndicator.startAnimating()
    }
    
    func hideImageIndicator(){
        btnPhoto.hidden = false
        imgIndicator.hidden = true
        imgIndicator.stopAnimating()
    }
}
