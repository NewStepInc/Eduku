package com.bluemax.bluemaxmng.components;

public class RippleItem {
    public String objectId;
    public String id;
    public String campaignId;
    public String userId;
    public String rippleName;

    public RippleItem() {
        objectId = id = campaignId = userId = rippleName = "";
    }

    public RippleItem(RippleItem anotherRippleItem) {
        this.objectId = anotherRippleItem.objectId;
        this.id = anotherRippleItem.id;
        this.campaignId = anotherRippleItem.campaignId;
        this.userId = anotherRippleItem.userId;
        this.rippleName = anotherRippleItem.rippleName;
    }
}
