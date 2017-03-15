//
//  IndividualCell.swift
//  eduku
//
//  Created by Mickey on 2/15/16.
//  Copyright © 2016 Mickey. All rights reserved.
//

import UIKit

class IndividualCell: UITableViewCell {

    @IBOutlet weak var txtSenderName: UILabel!
    @IBOutlet weak var txtSenderCity: UILabel!
    @IBOutlet weak var txtRecipientName: UILabel!
    @IBOutlet weak var txtRecipientCity: UILabel!
    @IBOutlet weak var txtDate: UILabel!
    @IBOutlet weak var txtTime: UILabel!
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
    }
}
