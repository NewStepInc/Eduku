//
//  CustomImageFlowLayout.swift
//  eduku
//
//  Created by Mickey on 2/20/16.
//  Copyright © 2016 Mickey. All rights reserved.
//

import UIKit

class CustomImageFlowLayout: UICollectionViewFlowLayout {
    var direction: Bool!
    
    init(_direction: Bool){
        super.init()
        direction = _direction
        setupLayout()
    }
    override init() {
        super.init()
        setupLayout()
    }
    
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setupLayout()
    }
    
    override var itemSize: CGSize {
        set {
            
        }
        get {
            if (direction == false){
                let numberOfColumns: CGFloat = 2
                let itemWidth = (CGRectGetWidth(self.collectionView!.frame) - (numberOfColumns - 1) * 5) / numberOfColumns
                return CGSizeMake(itemWidth, itemWidth)
            }else{
                let numberOfRows: CGFloat = 1
                let itemHeight = (CGRectGetHeight(self.collectionView!.frame) - (numberOfRows - 1) * 5) / numberOfRows
                let itemWidth = (CGRectGetWidth(self.collectionView!.frame) - (4 - 1) * 5) / 4
                return CGSizeMake(itemWidth, itemHeight - 3)
            }
        }
    }
    
    func setupLayout() {
        if (direction == false){
            minimumInteritemSpacing = 0
            minimumLineSpacing = 5
            scrollDirection = .Vertical
        }else{
            minimumInteritemSpacing = 0
            minimumLineSpacing = 5
            scrollDirection = .Horizontal
        }
    }
    
}
