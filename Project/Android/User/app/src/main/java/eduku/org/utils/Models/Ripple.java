package eduku.org.utils.Models;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import eduku.org.utils.ParseClass.ParseClassKeys;
import eduku.org.utils.ParseClass.ParsePhoto;
import eduku.org.utils.ParseClass.ParseRipple;

/**
 * Created by mickey on 3/5/16.
 */

public class Ripple {
    public interface MyCallbackInterface {
        public void OnDownloadFinished();
    }

    public String name;
    public String code;
    public String id;
    public ParseObject object;
    public ParseObject campaign;
    public String photoUrl;
    public boolean isIos;
    public Ripple(ParseObject _object, final MyCallbackInterface _callback){
        name = _object.getString(ParseRipple.RippleName);
        code = _object.getString(ParseRipple.RippleCode);
        id = _object.getObjectId();
        object = _object;
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClassKeys.Campaign);
        query.getInBackground(_object.getString(ParseRipple.CampaignId), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                campaign = object;
                _callback.OnDownloadFinished();
            }
        });

        query = ParseQuery.getQuery(ParseClassKeys.Photo);
        query.whereEqualTo(ParsePhoto.RippleId, id);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> photos, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < photos.size(); i++) {
                        final ParseObject photo = photos.get(i);
                        if (photo.getBoolean(ParsePhoto.IsApproved) == false)
                            continue;
                        isIos = photo.getBoolean(ParsePhoto.IsIos);
                        ParseFile file = photo.getParseFile(ParsePhoto.Photo);
                        photoUrl = file.getUrl();
                        _callback.OnDownloadFinished();
                        return;
                    }
                    photoUrl = "";
                    _callback.OnDownloadFinished();
                }
            }
        });
    }
}
