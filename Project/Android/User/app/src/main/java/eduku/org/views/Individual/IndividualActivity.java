package eduku.org.views.Individual;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import eduku.org.R;
import eduku.org.utils.Global;
import eduku.org.utils.ParseClass.ParseCampaign;
import eduku.org.utils.ParseClass.ParseRipple;
import eduku.org.utils.ParseClass.ParseUserKeys;
import eduku.org.utils.UiUtil;
import eduku.org.utils.Utils;

public class IndividualActivity extends Activity {
    public static ParseObject m_Ripple, m_Campaign;
    public static boolean m_photo;
    private boolean m_bFollow;
    private ProgressBar progBar;
    private IndividualGridAdapter m_gridAdapter;
    private IndividualListAdapter m_listAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual);
        ((TextView)findViewById(R.id.txt_RippleName)).setText(m_Ripple.getString(ParseRipple.RippleName) + " RIPPLE");
        ((TextView)findViewById(R.id.txt_RippleCode)).setText(m_Ripple.getString(ParseRipple.RippleCode));
        ((TextView)findViewById(R.id.txt_Destination)).setText(m_Campaign.getString(ParseCampaign.Destination));
        ((TextView)findViewById(R.id.txt_PhoneNumber)).setText(m_Campaign.getString(ParseCampaign.PhoneNumber));
        ((TextView)findViewById(R.id.txt_LeftTime)).setText("TIME LEFT: " + Utils.getDaysBetweenDates(new Date(), m_Campaign.getDate(ParseCampaign.StartAt)) + " Days");
        progBar = (ProgressBar)findViewById(R.id.progBar);
        progBar.setVisibility(View.GONE);
        if (Global.currentUser.followRipples.toString().contains(m_Ripple.getObjectId()) == true){
            m_bFollow = false;
            SetFollow();
        }else{
            m_bFollow = true;
            SetFollow();
        }
        m_listAdapter = new IndividualListAdapter(this, m_Ripple);
        m_gridAdapter = new IndividualGridAdapter(this, m_Ripple);
        ((ListView)findViewById(R.id.individual_list)).setAdapter(m_listAdapter);
        ((GridView)findViewById(R.id.individual_grid)).setAdapter(m_gridAdapter);
        if (m_photo == false){
            ((ImageView)findViewById(R.id.btn_ripple)).setImageResource(R.drawable.btn_seephotos);
            findViewById(R.id.individual_grid).setVisibility(View.GONE);
            findViewById(R.id.individual_list).setVisibility(View.VISIBLE);
        }else{
            ((ImageView)findViewById(R.id.btn_ripple)).setImageResource(R.drawable.btn_where);
            findViewById(R.id.individual_grid).setVisibility(View.VISIBLE);
            findViewById(R.id.individual_list).setVisibility(View.GONE);
        }

        UiUtil.applyImageButtonEffect((ImageView)findViewById(R.id.btn_back), new Runnable() {
            public void run() {
                finish();
            }
        });
        UiUtil.applyImageButtonEffect((ImageView)findViewById(R.id.btn_ripple), new Runnable() {
            public void run() {
                OnBtnRippleClicked();
            }
        });
        UiUtil.applyImageButtonEffect((ImageView)findViewById(R.id.btn_follow), new Runnable() {
            public void run() {
                OnBtnFollowClicked();
            }
        });
    }

    public void SetFollow(){
        if (m_bFollow == true){
            ((ImageView)findViewById(R.id.btn_follow)).setImageResource(R.drawable.btn_followbutton);
        }else{
            ((ImageView)findViewById(R.id.btn_follow)).setImageResource(R.drawable.btn_unfollowbutton);
        }
    }

    public void OnBtnFollowClicked() {
        findViewById(R.id.btn_follow).setVisibility(View.INVISIBLE);
        findViewById(R.id.progBar).setVisibility(View.VISIBLE);
        if (m_bFollow == false){
            m_bFollow = true;
            SetFollow();
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < Global.currentUser.followRipples.length(); i++){
                try {
                    if (Global.currentUser.followRipples.getString(i).equals(m_Ripple.getObjectId()) == false){
                        jsonArray.put(Global.currentUser.followRipples.get(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Global.currentUser.followRipples = jsonArray;
        }else{
            m_bFollow = false;
            SetFollow();
            Global.currentUser.followRipples.put(m_Ripple.getObjectId());
        }
        Global.currentUser.user.put(ParseUserKeys.FollowRipples, Global.currentUser.followRipples);
        Global.currentUser.user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                findViewById(R.id.progBar).setVisibility(View.GONE);
                findViewById(R.id.btn_follow).setVisibility(View.VISIBLE);
            }
        });
    }


    public void OnBtnRippleClicked(){
        m_photo = !m_photo;
        if (m_photo == false){
            ((ImageView)findViewById(R.id.btn_ripple)).setImageResource(R.drawable.btn_seephotos);
            findViewById(R.id.individual_grid).setVisibility(View.GONE);
            getAnimation(findViewById(R.id.individual_list)).start();
        }else{
            ((ImageView)findViewById(R.id.btn_ripple)).setImageResource(R.drawable.btn_where);
            findViewById(R.id.individual_list).setVisibility(View.GONE);
            getAnimation(findViewById(R.id.individual_grid)).start();
        }
    }

    public Animator getAnimation(final View view){
        Animator animator = ObjectAnimator.ofFloat(view, "alpha", 0, 1).setDuration(1000);

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        return animator;
    }
}
