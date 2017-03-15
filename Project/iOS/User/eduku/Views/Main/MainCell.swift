//
//  MainCell.swift
//  eduku
//
//  Created by Mickey on 2/17/16.
//  Copyright © 2016 Mickey. All rights reserved.
//

import UIKit

class MainCell: UITableViewCell {

    @IBOutlet weak var txtRipple: UILabel!
    @IBOutlet weak var txtSenderName: UILabel!
    @IBOutlet weak var txtSenderCity: UILabel!
    @IBOutlet weak var txtRecipientName: UILabel!
    @IBOutlet weak var txtRecipientCity: UILabel!
    @IBOutlet weak var indicator: UIActivityIndicatorView!
    @IBOutlet weak var viewContent: UIView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    override func prepareForReuse() {
        super.prepareForReuse()
        viewContent.hidden = true
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
}
