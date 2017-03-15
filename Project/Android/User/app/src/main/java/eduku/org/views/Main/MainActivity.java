package eduku.org.views.Main;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import eduku.org.R;
import eduku.org.utils.Global;
import eduku.org.utils.Models.RecentUpdates;
import eduku.org.utils.ParseClass.ParseCampaign;
import eduku.org.utils.ParseClass.ParseClassKeys;
import eduku.org.utils.ParseClass.ParsePhoto;
import eduku.org.utils.ParseClass.ParseRipple;
import eduku.org.utils.RippleScanDialogs;
import eduku.org.utils.UiUtil;
import eduku.org.utils.Utils;
import eduku.org.views.CommonActivity;
import eduku.org.views.Popular.PopularActivity;
import eduku.org.views.ScannedActivity;
import mobi.parchment.widget.adapterview.gridview.GridView;

public class MainActivity extends CommonActivity implements ScanDialog.OnCompleteListener {
    MainListAdapter m_listAdapter;
    MainGridAdapter m_gridAdapter;
    ScanDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_listAdapter = new MainListAdapter(this);
        ((ListView)findViewById(R.id.main_list)).setAdapter(m_listAdapter);
        m_gridAdapter = new MainGridAdapter(this);
        ((GridView)findViewById(R.id.main_grid)).setAdapter(m_gridAdapter);
        dialog = new ScanDialog();

        UiUtil.applyImageButtonEffect((ImageView) findViewById(R.id.btn_scanripple), new Runnable() {
            @Override
            public void run() {
                OnScanRipple();
            }
        });
        UiUtil.applyImageButtonEffect((ImageView) findViewById(R.id.btn_search), new Runnable() {
            @Override
            public void run() {
                OnSearchRipple();
            }
        });
        UiUtil.applyImageButtonEffect((ImageView) findViewById(R.id.btn_follow), new Runnable() {
            @Override
            public void run() {
                OnFollowRipple();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetRecentUpdates();
        GetRecentPhotos();
    }

    void OnSearchRipple(){
        Intent intent = new Intent(MainActivity.this, PopularActivity.class);
        intent.putExtra("search", true);
        startActivity(intent);
    }

    void OnFollowRipple(){
        if (Global.currentUser.followRipples == null)
        {
            showLoading();
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    if (Global.currentUser.followRipples == null) {
                        OnFollowRipple();
                        return;
                    }
                    hideLoading();
                    Intent intent = new Intent(MainActivity.this, PopularActivity.class);
                    intent.putExtra("search", false);
                    startActivity(intent);
                    return;
                }
            }, 50);
            return;
        }
        Intent intent = new Intent(MainActivity.this, PopularActivity.class);
        intent.putExtra("search", false);
        startActivity(intent);
    }

    void GetRecentUpdates(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClassKeys.RecentUpdates);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                Global.recentUpdates = new RecentUpdates(object);
                m_listAdapter.notifyDataSetChanged();
            }
        });
    }

    void GetRecentPhotos(){
        m_gridAdapter.Init();
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClassKeys.Photo);
        query.orderByDescending("createdAt");
        query.setLimit(15);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for (int i = 0; i < objects.size(); i++) {
                    if (objects.get(i).getBoolean(ParsePhoto.IsApproved) == false) {
                        continue;
                    }
                    ParseFile file = objects.get(i).getParseFile(ParsePhoto.Photo);
                    m_gridAdapter.AddUrl(file.getUrl(), objects.get(i).getBoolean(ParsePhoto.IsIos));
                }
            }
        });
    }

    public void OnScanRipple(){
        dialog.show(getFragmentManager(), "blur");
    }

    @Override
    public void onComplete(String _rippleId) {
        if (_rippleId.length() < 1){
            UiUtil.alert(this, "Warning", RippleScanDialogs.CompleteRequireFields, null);
            return;
        }
        HideKeyboard();
        showLoading();
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClassKeys.Ripple);
        query.whereEqualTo(ParseRipple.RippleCode, _rippleId);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0){
                        for (final ParseObject ripple : objects){
                            if (Global.currentUser.userId.equals(ripple.getString(ParseRipple.UserId))){
                                ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClassKeys.Campaign);
                                query.getInBackground(ripple.getString(ParseRipple.CampaignId), new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject object, ParseException e) {
                                        hideLoading();
                                        if (e == null && object != null){
                                            if (Utils.getDaysBetweenDates(new Date(), object.getDate(ParseCampaign.StartAt)) > 0 &&
                                                    object.getBoolean(ParseCampaign.isActive) == true){
                                                dialog.dismiss();
                                                Intent i = new Intent(MainActivity.this, ScannedActivity.class);
                                                ScannedActivity.m_Ripple = ripple;
                                                ScannedActivity.m_Campaign = object;
                                                startActivity(i);
                                            }else{
                                                UiUtil.alert(MainActivity.this, "Warning", RippleScanDialogs.NotActiveCampaign, null);
                                            }
                                        }else{
                                            UiUtil.alert(MainActivity.this, "Warning", RippleScanDialogs.ConnectionBad, null);
                                        }
                                    }
                                });
                            }else{
                                hideLoading();
                                UiUtil.alert(MainActivity.this, "Warning", RippleScanDialogs.NotBelongToUser, null);
                            }
                        }
                    }else{
                        hideLoading();
                        UiUtil.alert(MainActivity.this, "Warning", RippleScanDialogs.NotExistRipple, null);
                    }
                } else {
                    hideLoading();
                    UiUtil.alert(MainActivity.this, "Warning", RippleScanDialogs.NotExistRipple, null);
                }
            }
        });
    }
}
