package com.bluemax.bluemaxmng.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bluemax.bluemaxmng.GlobalConstants;
import com.bluemax.bluemaxmng.R;
import com.bluemax.bluemaxmng.components.CampaignItem;
import com.bluemax.bluemaxmng.components.RippleItem;
import com.bluemax.bluemaxmng.components.UserItem;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class RippleDetailActivity extends AppCompatActivity {
    private int position = -1;
    private RippleItem rippleItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ripple_detail);

        // Hide the standard keyboard initially
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);

        // if Add
        if (position == -1) {
            rippleItem = new RippleItem();
            rippleItem.campaignId = intent.getStringExtra("campaignId");

            findViewById(R.id.ripple_detail_remove).setVisibility(View.GONE);
        } else {    // if Edit
            ((TextView)findViewById(R.id.ripple_detail_comment)).setText("Edit a Comment");

            rippleItem = new RippleItem(RippleActivity.rippleDisplayList.get(position));
        }

        ((EditText)findViewById(R.id.ripple_detail_id)).setText(rippleItem.id);
        ((EditText)findViewById(R.id.ripple_detail_title)).setText(rippleItem.rippleName);
        ((TextView)findViewById(R.id.ripple_detail_user_name)).setText("User Name : " + GlobalConstants.getUserName(rippleItem.userId));
        ((EditText)findViewById(R.id.ripple_detail_user_email)).setText(GlobalConstants.getUserEmail(rippleItem.userId));
    }

    public void onSave(View view) {
        rippleItem.id = ((EditText) findViewById(R.id.ripple_detail_id)).getText().toString();
        rippleItem.rippleName = ((EditText) findViewById(R.id.ripple_detail_title)).getText().toString();

        if (position == -1) {
            final ProgressDialog progress = ProgressDialog.show(this, "Saving", "Please wait for a while.", true);
            new Thread(new Runnable() {
                @Override
                public void run()
                {
                    boolean isSuccess = false;
                    try {
                        ParseObject parseObject = new ParseObject("Ripple");
                        parseObject.put("campaignId", rippleItem.campaignId);
                        parseObject.put("rippleId", rippleItem.id);
                        parseObject.put("rippleName", rippleItem.rippleName);
                        parseObject.put("userId", rippleItem.userId);

                        parseObject.save();
                        rippleItem.objectId = parseObject.getObjectId();

                        isSuccess = true;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    final boolean finalIsSuccess = isSuccess;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                            if (finalIsSuccess)
                                RippleActivity.rippleList.add(0, rippleItem);
                            else
                                Toast.makeText(getApplicationContext(), "Saving failed!", Toast.LENGTH_LONG).show();

                            onBackPressed();
                        }
                    });
                }
            }).start();
        } else {
            final ProgressDialog progress = ProgressDialog.show(this, "Saving", "Please wait for a while.", true);
            new Thread(new Runnable() {
                @Override
                public void run()
                {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Ripple");
                    boolean isSuccess = false;
                    try {
                        ParseObject parseObject = query.get(rippleItem.objectId);
                        parseObject.put("campaignId", rippleItem.campaignId);
                        parseObject.put("rippleId", rippleItem.id);
                        parseObject.put("rippleName", rippleItem.rippleName);
                        parseObject.put("userId", rippleItem.userId);
                        parseObject.save();
                        isSuccess = true;
                    } catch (com.parse.ParseException e) {
                        e.printStackTrace();
                    }

                    final boolean finalIsSuccess = isSuccess;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();

                            if (finalIsSuccess) {
                                RippleItem originRippleItem = RippleActivity.rippleDisplayList.get(position);
                                int positionInAll = RippleActivity.rippleList.indexOf(originRippleItem);
                                RippleActivity.rippleList.set(positionInAll, rippleItem);
                            } else
                                Toast.makeText(getApplicationContext(), "Saving failed!", Toast.LENGTH_LONG).show();

                            onBackPressed();
                        }
                    });
                }
            }).start();
        }
    }

    public void onRemove(View view) {
        final ProgressDialog progress = ProgressDialog.show(this, "Removing", "Please wait for a while.", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Ripple");
                boolean isSuccess = false;
                try {
                    ParseObject object = query.get(rippleItem.objectId);
                    object.delete();
                    isSuccess = true;
                } catch (com.parse.ParseException e) {
                    e.printStackTrace();
                }

                final boolean finalIsSuccess = isSuccess;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();

                        if (finalIsSuccess) {

                            RippleItem originRippleItem = RippleActivity.rippleDisplayList.get(position);
                            int positionInAll = RippleActivity.rippleList.indexOf(originRippleItem);
                            RippleActivity.rippleList.remove(positionInAll);
                        } else
                            Toast.makeText(getApplicationContext(), "Removing failed!", Toast.LENGTH_LONG).show();

                        onBackPressed();
                    }
                });
            }
        }).start();
    }

    public void onCheckEmail(View view) {
        final ProgressDialog progress = ProgressDialog.show(this, "Checking", "Please wait for a while.", true);
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                boolean isSuccess = false;
                ParseQuery<ParseUser> quer = ParseUser.getQuery();
                GlobalConstants.userList.clear();
                try {
                    List<ParseUser> objects = quer.find();
                    for (int i = 0; i <objects.size(); i++)
                    {
                        ParseUser user = objects.get(i);
                        UserItem item= new UserItem();
                        item.id = user.getObjectId();
                        item.email = user.getEmail();
                        item.name = user.getString("fullname");
                        GlobalConstants.userList.put(item.id, item);
                    }
                    isSuccess = true;
                } catch (ParseException e) {
                    e.printStackTrace();
                    return;
                }

                final boolean finalIsSuccess = isSuccess;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                        if (finalIsSuccess) {
                            String userId = GlobalConstants.getUserId(((EditText) findViewById(R.id.ripple_detail_user_email)).getText().toString().toLowerCase());
                            if (userId.isEmpty()) {
                                Toast.makeText(getApplicationContext(), "Not registered!!!", Toast.LENGTH_LONG).show();
                                return;
                            }
                            rippleItem.userId = userId;
                            ((TextView)findViewById(R.id.ripple_detail_user_name)).setText("User Name : " + GlobalConstants.getUserName(rippleItem.userId));
                        }
                        else
                            Toast.makeText(getApplicationContext(), "Checking failed!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
