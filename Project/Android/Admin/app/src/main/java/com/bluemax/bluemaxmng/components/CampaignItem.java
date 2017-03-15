package com.bluemax.bluemaxmng.components;

import java.util.Date;

public class CampaignItem {
    public String id;
    public String name;
    public String description;
    public String destination;
    public Date startAt;
    public String contact;
    public boolean isRun;

    public CampaignItem() {
        id = name = description = destination = contact = "";
        startAt = new Date();
        isRun = false;
    }

    public CampaignItem(CampaignItem anotherCampaignItem) {
        this.id = anotherCampaignItem.id;
        this.name = anotherCampaignItem.name;
        this.description = anotherCampaignItem.description;
        this.destination = anotherCampaignItem.destination;
        this.startAt = new Date(anotherCampaignItem.startAt.getTime());
        this.contact = anotherCampaignItem.contact;
        this.isRun = anotherCampaignItem.isRun;
    }
}
