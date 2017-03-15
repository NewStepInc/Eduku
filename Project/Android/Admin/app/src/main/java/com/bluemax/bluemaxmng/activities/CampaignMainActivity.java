package com.bluemax.bluemaxmng.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bluemax.bluemaxmng.GlobalConstants;
import com.bluemax.bluemaxmng.R;
import com.bluemax.bluemaxmng.components.CampaignItem;
import com.bluemax.bluemaxmng.components.CampaignRecyclerViewAdapter;
import com.bluemax.bluemaxmng.components.UserItem;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class CampaignMainActivity extends AppCompatActivity {

    public static List<CampaignItem> campaignList = new ArrayList<>();
    public static List<CampaignItem> campaignDisplayList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private CampaignRecyclerViewAdapter adapter;
    private ProgressBar progressBar;
    private boolean isSearching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_main);

        getSupportActionBar().setTitle("Campaigns");

        // Hide the standard keyboard initially
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        ((EditText)findViewById(R.id.campaign_search_text)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ( (actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN ))) {
                    onSearch(null);
                    return true;
                }
                else{
                    return false;
                }
            }
        });

        // Initialize recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.campaign_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = (ProgressBar) findViewById(R.id.campaign_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        isSearching = false;
        new AsyncHttpTask().execute();
    }

    public void onSearch(View view) {
        String string = ((EditText)findViewById(R.id.campaign_search_text)).getText().toString();
        isSearching = true;
        new AsyncHttpTask().execute(string);
    }

    public void onAdd(View view) {
        Intent intent = new Intent(this, CampaignDetailActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        onSearch(null);
    }

    private class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Integer doInBackground(String... params) {

            if (isSearching == false) {
                campaignList.clear();
                campaignDisplayList.clear();

                ParseQuery<ParseObject> query = ParseQuery.getQuery("Campaign");
                query.orderByDescending("createdAt");

                ParseQuery<ParseUser> quer = ParseUser.getQuery();
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

                    List<ParseObject> campaignItemList = query.find();
                    for (int i = 0; i < campaignItemList.size(); i++) {
                        ParseObject parseObject = campaignItemList.get(i);

                        CampaignItem campaignItem = new CampaignItem();
                        campaignItem.id = parseObject.getObjectId();
                        campaignItem.name = parseObject.getString("campaignName");
                        campaignItem.description = parseObject.getString("description");
                        campaignItem.destination = parseObject.getString("destination");
                        campaignItem.contact = parseObject.getString("phoneNumber");
                        campaignItem.startAt = parseObject.getDate("startAt");
                        campaignItem.isRun = parseObject.getBoolean("isActive");

                        campaignList.add(campaignItem);
                        campaignDisplayList.add(campaignItem);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            } else {
                campaignDisplayList.clear();
                for (int i = 0; i < campaignList.size(); i++) {
                    CampaignItem campaignItem = campaignList.get(i);
                    if (params.length > 0 && !campaignItem.name.contains(params[0]))
                        continue;
                    campaignDisplayList.add(campaignItem);
                }
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Let us update UI
            progressBar.setVisibility(View.GONE);

            if (result != null && result == 1) {
                adapter = new CampaignRecyclerViewAdapter(CampaignMainActivity.this);
                mRecyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(CampaignMainActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
