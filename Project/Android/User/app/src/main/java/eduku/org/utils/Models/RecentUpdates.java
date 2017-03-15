package eduku.org.utils.Models;

import com.parse.ParseException;
import com.parse.ParseObject;

import org.json.JSONArray;

import java.util.ArrayList;

import eduku.org.utils.ParseClass.ParseRecentUpdates;
import eduku.org.utils.Utils;

/**
 * Created by mickey on 2/23/16.
 */
public class RecentUpdates {
    public ArrayList<RecentLog> recentLog = null;
    private ParseObject recentUpdates;

    public RecentUpdates(ParseObject _recentUpdates) {
        recentUpdates = _recentUpdates;
        if (_recentUpdates.getString(ParseRecentUpdates.Log) != null){
            recentLog = Utils.getRecentLogFromJson(_recentUpdates.getString(ParseRecentUpdates.Log));
        }
        else{
            recentLog = new ArrayList<>();
        }
    }

    public void save() {
        recentUpdates.put(ParseRecentUpdates.Log, Utils.getJsonFromRecentLog(recentLog));
        try {
            recentUpdates.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
