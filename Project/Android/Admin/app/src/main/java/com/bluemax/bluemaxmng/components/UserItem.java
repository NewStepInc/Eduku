package com.bluemax.bluemaxmng.components;

public class UserItem {
    public String id;
    public String name;
    public String email;

    public UserItem() {
        id = name = email = "";
    }

    public UserItem(UserItem anotherUserItem) {
        this.id = anotherUserItem.id;
        this.name = anotherUserItem.name;
        this.email = anotherUserItem.email;
    }
}
