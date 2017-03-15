package com.bluemax.bluemaxmng;

import com.bluemax.bluemaxmng.components.UserItem;

import java.util.HashMap;
import java.util.Map;

public class GlobalConstants {
    public static Map<String, UserItem> userList = new HashMap<>();

    public static String getUserName(String userId) {
        UserItem userItem = userList.get(userId);
        if (userItem == null)
            return "";

        return userItem.name;
    }

    public static String getUserEmail(String userId) {
        UserItem userItem = userList.get(userId);
        if (userItem == null)
            return "";

        return userItem.email;
    }

    public static String getUserId(String userEmail) {
        for(Map.Entry<String, UserItem> entry: userList.entrySet()) {
            UserItem item = entry.getValue();
            if (item.email.equals(userEmail)){
                return item.id;
            }
        }
        return "";
    }
}
