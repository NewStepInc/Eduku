//
//  ImageCollectionViewCell.swift
//  eduku
//
//  Created by Mickey on 2/20/16.
//  Copyright © 2016 Mickey. All rights reserved.
//

import UIKit

class ImageCollectionViewCell: UICollectionViewCell {
    @IBOutlet weak var imageView: UIImageView!
    @IBOutlet weak var indicator: UIActivityIndicatorView!
    
    override func prepareForReuse() {
        super.prepareForReuse()
        imageView.image = nil
    }
    
    func showIndicator(){
        indicator.hidden = false
        indicator.startAnimating()
    }
    
    func hideIndicator(){
        indicator.hidden = true
        indicator.stopAnimating()
    }
}
