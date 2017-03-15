package eduku.org.utils.Models;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.ArrayList;

import eduku.org.utils.ParseClass.ParseUserKeys;

/**
 * Created by mickey on 2/23/16.
 */
public class User {
    public String userId;
    public String username;
    public String email;
    public String fullname;
    public String city;
    public String country;
    public JSONArray followRipples;
    public ParseUser user;

    public User(ParseUser _user){
        user = _user;

        userId = _user.getObjectId();
        username = _user.getUsername();
        email = _user.getEmail();
        fullname = _user.getString(ParseUserKeys.FullName);

        city = _user.getString(ParseUserKeys.City);
        country = _user.getString(ParseUserKeys.Country);
        followRipples = _user.getJSONArray(ParseUserKeys.FollowRipples);
        if (followRipples == null){
            followRipples = new JSONArray();
        }
    }

    public void save(boolean bBackground) {
        user.put(ParseUserKeys.FullName, fullname);
        if (city == null){
            city = "";
        }
        if (country == null) {
            country = "";
        }
        if (followRipples == null){
            followRipples = new JSONArray();
        }
        user.put(ParseUserKeys.City, city);
        user.put(ParseUserKeys.Country, country);
        user.put(ParseUserKeys.FollowRipples, followRipples);
        user.setEmail(email);
        user.setUsername(email);
        if (bBackground == true)
            user.saveInBackground();
        else
        {
            try {
                user.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
