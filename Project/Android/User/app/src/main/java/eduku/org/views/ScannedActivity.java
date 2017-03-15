package eduku.org.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import eduku.org.R;
import eduku.org.utils.Global;
import eduku.org.utils.Models.RecentLog;
import eduku.org.utils.Models.Ripple;
import eduku.org.utils.Models.RippleLog;
import eduku.org.utils.Models.User;
import eduku.org.utils.ParseClass.ParseClassKeys;
import eduku.org.utils.ParseClass.ParsePhoto;
import eduku.org.utils.ParseClass.ParseRipple;
import eduku.org.utils.ParseClass.ParseUserKeys;
import eduku.org.utils.RippleScanDialogs;
import eduku.org.utils.UiUtil;
import eduku.org.utils.UserDialogs;
import eduku.org.utils.Utils;
import eduku.org.views.Individual.IndividualActivity;
import eduku.org.views.Main.MainActivity;

public class ScannedActivity extends CommonActivity {
    private static final int CAMERA_REQUEST = 1888;
    public static ParseObject m_Ripple, m_Campaign;
    private EditText txtEmail, txtName, txtCity, txtCountry;
    private ParseUser m_recipient;
    private Bitmap photo;
    private Boolean bSigned = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned);
        txtEmail = (EditText)findViewById(R.id.txt_email);
        txtName = (EditText)findViewById(R.id.txt_name);
        txtCity = (EditText)findViewById(R.id.txt_city);
        txtCountry = (EditText)findViewById(R.id.txt_country);
        txtName.setVisibility(View.GONE);
        txtCity.setVisibility(View.GONE);
        txtCountry.setVisibility(View.GONE);

        ((TextView)findViewById(R.id.txt_RippleName)).setText(m_Ripple.getString(ParseRipple.RippleName) + " RIPPLE");
        ((TextView)findViewById(R.id.txt_RippleCode)).setText(m_Ripple.getString(ParseRipple.RippleCode));
        ((TextView)findViewById(R.id.txt_Message)).setText(RippleScanDialogs.RippleScanMessage + m_Ripple.getString(ParseRipple.RippleCode));

        UiUtil.applyImageButtonEffect((ImageView) findViewById(R.id.btn_back), new Runnable() {
            public void run() {
                finish();
            }
        });
        UiUtil.applyImageButtonEffect((ImageView) findViewById(R.id.btn_go), new Runnable() {
            public void run() {
                FindUser();
            }
        });
        UiUtil.applyImageButtonEffect((ImageView) findViewById(R.id.btn_sendripple), new Runnable() {
            public void run() {
                OnSendRipple();
            }
        });
        UiUtil.applyImageButtonEffect((ImageView) findViewById(R.id.btn_uploadphoto), new Runnable() {
            public void run() {
                UploadPhoto();
            }
        });
        findViewById(R.id.btn_sendripple).setEnabled(false);
        findViewById(R.id.btn_sendripple).setAlpha(0.3f);
    }

    public void FindUser(){
        if (txtEmail.getText().toString().length() < 1){
            UiUtil.alert(this, "Warning", RippleScanDialogs.InputEmailFields, null);
            return;
        }
        if (txtEmail.getText().toString().toLowerCase().equals(Global.currentUser.email)){
            UiUtil.alert(this, "Warning", RippleScanDialogs.NotToYou, null);
            return;
        }
        bSigned = false;
        HideKeyboard();
        showLoading();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("email", txtEmail.getText().toString().toLowerCase());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                hideLoading();
                if (e == null) {
                    findViewById(R.id.btn_sendripple).setEnabled(true);
                    findViewById(R.id.btn_sendripple).setAlpha(1);
                    if (objects.size() > 0) {
                        for (ParseUser user : objects) {
                            m_recipient = user;
                            txtName.setEnabled(false);
                            txtCity.setEnabled(false);
                            txtCountry.setEnabled(false);
                            txtName.setText(user.getString(ParseUserKeys.FullName));
                            txtCity.setText(user.getString(ParseUserKeys.City));
                            txtCountry.setText(user.getString(ParseUserKeys.Country));
                        }
                    } else {
                        txtName.setEnabled(true);
                        txtCity.setEnabled(true);
                        txtCountry.setEnabled(true);
                        Toast.makeText(ScannedActivity.this, RippleScanDialogs.UserSignUP, Toast.LENGTH_LONG).show();
                    }
                } else {
                    UiUtil.alert(ScannedActivity.this, "Warning", RippleScanDialogs.ConnectionBad, null);
                }
            }
        });
        if (txtName.getVisibility() == View.VISIBLE) return;
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(getAnimation(txtName, 0), getAnimation(txtCity, 50), getAnimation(txtCountry, 100));
        animatorSet.start();
    }

    public Animator getAnimation(final View view, long _delay){
        int sWidth = UiUtil.getScreenWidth(this);

        List<Animator> animatorList = new ArrayList<>();
        view.setX(sWidth);
        animatorList.add(ObjectAnimator.ofFloat(view, "x", sWidth, 40 * getSpForApp()).setDuration(1000));
        animatorList.add(ObjectAnimator.ofFloat(view, "alpha", 0, 1).setDuration(2000));

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorList);
        animatorSet.setStartDelay(_delay);

        animatorSet.addListener(new Animator.AnimatorListener() {
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

        return animatorSet;
    }

    public void OnSendRipple(){
        if (txtName.isEnabled() == true && bSigned == false){
            if (txtName.length() < 1 || txtCity.length() < 1|| txtCountry.length() < 1){
                UiUtil.alert(this, "Warning", UserDialogs.CompleteRequireFields, null);
                return;
            }
            showLoading();
            final ParseUser user = new ParseUser();
            user.setUsername(txtEmail.getText().toString());
            user.setPassword("12345678");
            user.setEmail(txtEmail.getText().toString().toLowerCase());

            user.put(ParseUserKeys.FullName, txtName.getText().toString());
            user.put(ParseUserKeys.City, txtCity.getText().toString());
            user.put(ParseUserKeys.Country, txtCountry.getText().toString());

            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        HashMap<String, String> params = new HashMap();
                        params.put("recipient", user.getEmail());
                        params.put("message", "Hi " + txtName.getText() + ",\n\nWe are happy to announce that you are invited by "
                                + Global.currentUser.email + " so that you were signed up \"Ripples of Hope" +
                                "\" automatically.\nYour password: 12345678\nPlease download the app from App Store or Google Play Store.\n\n" +
                                "Thanks,\nRipples of Hope Team");
                        ParseCloud.callFunctionInBackground("sendEmails", params, new FunctionCallback<String>() {
                            @Override
                            public void done(String object, ParseException e) {
                                if (object == "Email sent!") {
                                    m_recipient = user;
                                    SendRipple();
                                } else {
                                    hideLoading();
                                    UiUtil.alert(ScannedActivity.this, "Warning", RippleScanDialogs.ConnectionBad, null);
                                }
                            }
                        });
                        bSigned = true;
                    } else {
                        hideLoading();
                        if (e.getCode() == 202)
                            UiUtil.alert(ScannedActivity.this, "Signup Failed", UserDialogs.UsernameIsTaken, null);
                        else {
                            StringBuilder error = new StringBuilder(e.getMessage());
                            if (Character.isLowerCase(error.charAt(0)))
                                error.setCharAt(0, Character.toUpperCase(error.charAt(0)));
                            UiUtil.alert(ScannedActivity.this, "Signup Failed", error.toString() + "!", null);
                        }
                    }
                }
            });
        }else{
            showLoading();

            new Thread(new Runnable() {
                public void run() {
                    SendRipple();
                }
            }).start();

        }
    }

    public void SendRipple(){
        if (photo != null){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            photo.compress(Bitmap.CompressFormat.PNG, 75, stream);
            ParseFile file = new ParseFile(stream.toByteArray());
            ParseObject object = new ParseObject(ParseClassKeys.Photo);
            object.put(ParsePhoto.IsApproved, true);
            object.put(ParsePhoto.RippleId, m_Ripple.getObjectId());
            object.put(ParsePhoto.Photo, file);
            object.put(ParsePhoto.IsIos, false);
            try {
                object.save();
            } catch (ParseException e) {
                UiUtil.alert(ScannedActivity.this, "Warning", RippleScanDialogs.ConnectionBad, null);
                hideLoading();
                return;
            }
        }
        RippleLog log = new RippleLog(Global.currentUser.fullname, Global.currentUser.city, txtName.getText().toString(),
                txtCity.getText().toString(), new Date());
        ArrayList<RippleLog> logArray;
        if (m_Ripple.getString(ParseRipple.RippleLog) == null){
            logArray = new ArrayList<>();
        }else{
            logArray = Utils.getArrayFromJson(m_Ripple.getString(ParseRipple.RippleLog));
        }
        logArray.add(log);
        m_Ripple.put(ParseRipple.RippleLog, Utils.getJsonFromArray(logArray));
        m_Ripple.put(ParseRipple.UserId, m_recipient.getObjectId());
        try {
            m_Ripple.save();
        } catch (ParseException e) {
            UiUtil.alert(ScannedActivity.this, "Warning", RippleScanDialogs.ConnectionBad, null);
            hideLoading();
            return;
        }

        RecentLog recentLog = new RecentLog(m_Ripple.getString(ParseRipple.RippleName), m_Ripple.getString(ParseRipple.RippleCode),
                Global.currentUser.fullname, Global.currentUser.city, txtName.getText().toString(), txtCity.getText().toString());
        Global.recentUpdates.recentLog.add(0, recentLog);
        Global.recentUpdates.save();

        if (Global.currentUser.followRipples.toString().contains(m_Ripple.getObjectId()) == false){
            Global.currentUser.followRipples.put(m_Ripple.getObjectId());
            Global.currentUser.save(false);
        }

        JSONArray followRipples = m_recipient.getJSONArray(ParseUserKeys.FollowRipples);
        if (followRipples == null){
            followRipples = new JSONArray();
        }
        if (followRipples.toString().contains(m_Ripple.getObjectId()) == false){
            followRipples.put(m_Ripple.getObjectId());
        }

        HashMap<String, String> params = new HashMap();
        params.put("email", txtEmail.getText().toString().toLowerCase());
        params.put("follow", followRipples.toString());
        ParseCloud.callFunctionInBackground("userMigration", params, new FunctionCallback<Object>() {
            @Override
            public void done(Object object, ParseException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoading();
                        Intent i = new Intent(ScannedActivity.this, IndividualActivity.class);
                        IndividualActivity.m_Ripple = m_Ripple;
                        IndividualActivity.m_Campaign = m_Campaign;
                        IndividualActivity.m_photo = false;
                        startActivity(i);
                        finish();
                    }
                });
            }
        });
    }

    public void UploadPhoto(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
        }
    }
}
