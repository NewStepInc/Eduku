package com.bluemax.bluemaxmng.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bluemax.bluemaxmng.R;
import com.bluemax.bluemaxmng.components.CampaignItem;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CampaignDetailActivity extends AppCompatActivity {

    private int position = -1;
    private CampaignItem campaignItem = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_detail);

        // Hide the standard keyboard initially
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);

        // if Add
        if (position == -1) {
            campaignItem = new CampaignItem();

            findViewById(R.id.campaign_detail_remove).setVisibility(View.GONE);
            findViewById(R.id.campaign_detail_buttons).setVisibility(View.GONE);
        } else {    // if Edit
            ((TextView)findViewById(R.id.campaign_detail_comment)).setText("Edit a Comment");

            campaignItem = new CampaignItem(CampaignMainActivity.campaignDisplayList.get(position));
        }
        ((EditText)findViewById(R.id.campaign_detail_name)).setText(campaignItem.name);
        ((EditText)findViewById(R.id.campaign_detail_destination)).setText(campaignItem.destination);
        ((EditText)findViewById(R.id.campaign_detail_description)).setText(campaignItem.description);
        ((EditText)findViewById(R.id.campaign_detail_contact)).setText(campaignItem.contact);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        ((EditText) findViewById(R.id.campaign_detail_startat)).setText(format.format(campaignItem.startAt));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Drawable drawable = ContextCompat.getDrawable(this, campaignItem.isRun ? R.drawable.ic_check_on : R.drawable.ic_check_off);
            ((ImageButton)findViewById(R.id.campaign_detail_run)).setImageDrawable(drawable);
        } else {
            Drawable drawable = getResources().getDrawable(campaignItem.isRun ? R.drawable.ic_check_on : R.drawable.ic_check_off);
            ((ImageButton)findViewById(R.id.campaign_detail_run)).setImageDrawable(drawable);
        }
    }

    public void toggleRun(View view) {
        campaignItem.isRun = !campaignItem.isRun;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Drawable drawable = ContextCompat.getDrawable(this, campaignItem.isRun ? R.drawable.ic_check_on : R.drawable.ic_check_off);
            ((ImageButton)findViewById(R.id.campaign_detail_run)).setImageDrawable(drawable);
        } else {
            Drawable drawable = getResources().getDrawable(campaignItem.isRun ? R.drawable.ic_check_on : R.drawable.ic_check_off);
            ((ImageButton)findViewById(R.id.campaign_detail_run)).setImageDrawable(drawable);
        }
    }

    public void onSave(View view) {
        campaignItem.name = ((EditText)findViewById(R.id.campaign_detail_name)).getText().toString();
        campaignItem.destination = ((EditText)findViewById(R.id.campaign_detail_destination)).getText().toString();
        campaignItem.description = ((EditText)findViewById(R.id.campaign_detail_description)).getText().toString();
        campaignItem.contact = ((EditText)findViewById(R.id.campaign_detail_contact)).getText().toString();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            campaignItem.startAt = format.parse(((EditText) findViewById(R.id.campaign_detail_startat)).getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
            campaignItem.startAt = new Date();
        }

        if (position == -1) {

            final ProgressDialog progress = ProgressDialog.show(this, "Saving", "Please wait for a while.", true);
            new Thread(new Runnable() {
                @Override
                public void run()
                {
                    boolean isSuccess = false;
                    try {
                        ParseObject parseObject = new ParseObject("Campaign");
                        parseObject.put("campaignName", campaignItem.name);
                        parseObject.put("description", campaignItem.description);
                        parseObject.put("destination", campaignItem.destination);
                        parseObject.put("phoneNumber", campaignItem.contact);
                        parseObject.put("startAt", campaignItem.startAt);
                        parseObject.put("isActive", campaignItem.isRun);

                        parseObject.save();
                        campaignItem.id = parseObject.getObjectId();
                        isSuccess = true;
                    } catch (com.parse.ParseException e) {
                        e.printStackTrace();
                    }

                    final boolean finalIsSuccess = isSuccess;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                            if (finalIsSuccess)
                                CampaignMainActivity.campaignList.add(0, campaignItem);
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
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Campaign");
                    boolean isSuccess = false;
                    try {
                        ParseObject parseObject = query.get(campaignItem.id);
                        parseObject.put("campaignName", campaignItem.name);
                        parseObject.put("description", campaignItem.description);
                        parseObject.put("destination", campaignItem.destination);
                        parseObject.put("phoneNumber", campaignItem.contact);
                        parseObject.put("startAt", campaignItem.startAt);
                        parseObject.put("isActive", campaignItem.isRun);
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
                                CampaignItem originCampaignItem = CampaignMainActivity.campaignDisplayList.get(position);
                                int positionInAll = CampaignMainActivity.campaignList.indexOf(originCampaignItem);
                                CampaignMainActivity.campaignList.set(positionInAll, campaignItem);
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
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Campaign");
                boolean isSuccess = false;
                try {
                    ParseObject object = query.get(campaignItem.id);
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
                            CampaignItem originCampaignItem = CampaignMainActivity.campaignDisplayList.get(position);
                            int positionInAll = CampaignMainActivity.campaignList.indexOf(originCampaignItem);
                            CampaignMainActivity.campaignList.remove(positionInAll);
                        } else
                            Toast.makeText(getApplicationContext(), "Removing failed!", Toast.LENGTH_LONG).show();

                        onBackPressed();
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

    public void onRipple(View view) {
        Intent intent = new Intent(this, RippleActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }

    public void onPhoto(View view) {
        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
    }
}
