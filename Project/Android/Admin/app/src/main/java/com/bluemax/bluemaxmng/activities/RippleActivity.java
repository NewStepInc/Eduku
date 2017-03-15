package com.bluemax.bluemaxmng.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bluemax.bluemaxmng.R;
import com.bluemax.bluemaxmng.components.CampaignItem;
import com.bluemax.bluemaxmng.components.CampaignRecyclerViewAdapter;
import com.bluemax.bluemaxmng.components.RippleItem;
import com.bluemax.bluemaxmng.components.RippleRecyclerViewAdapter;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class RippleActivity extends AppCompatActivity {
    public static List<RippleItem> rippleList = new ArrayList<>();
    public static List<RippleItem> rippleDisplayList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private RippleRecyclerViewAdapter adapter;
    private ProgressBar progressBar;
    private boolean isSearching = false;
    private int position;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ripple);

        // Hide the standard keyboard initially
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        ((EditText)findViewById(R.id.ripple_search_text)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))) {
                    onSearch(null);
                    return true;
                } else {
                    return false;
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);

        // Initialize recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.ripple_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = (ProgressBar) findViewById(R.id.ripple_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        isSearching = false;
        new AsyncHttpTask().execute();
    }

    public void onSearch(View view) {
        String string = ((EditText)findViewById(R.id.ripple_search_text)).getText().toString();
        isSearching = true;
        new AsyncHttpTask().execute(string);
    }

    public void onAdd(View view) {
        Intent intent = new Intent(this, RippleDetailActivity.class);
        intent.putExtra("campaignId", CampaignMainActivity.campaignDisplayList.get(position).id);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        onSearch(null);
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

    private class AsyncHttpTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            setProgressBarIndeterminateVisibility(true);
        }

        @Override
        protected Integer doInBackground(String... params) {
            if (isSearching == false) {
                rippleList.clear();
                rippleDisplayList.clear();

                ParseQuery<ParseObject> query = ParseQuery.getQuery("Ripple");
                query.whereEqualTo("campaignId", CampaignMainActivity.campaignDisplayList.get(position).id);
                query.orderByAscending("rippleId");
                try {
                    List<ParseObject> rippleItemList = query.find();
                    for (int i = 0; i < rippleItemList.size(); i++) {
                        ParseObject parseObject = rippleItemList.get(i);

                        RippleItem rippleItem = new RippleItem();
                        rippleItem.objectId = parseObject.getObjectId();
                        rippleItem.id = parseObject.getString("rippleId");
                        rippleItem.campaignId = parseObject.getString("campaignId");
                        rippleItem.userId = parseObject.getString("userId");
                        rippleItem.rippleName = parseObject.getString("rippleName");

                        rippleList.add(rippleItem);
                        rippleDisplayList.add(rippleItem);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0;
                }
            } else {
                rippleDisplayList.clear();
                for (int i = 0; i < rippleList.size(); i++) {
                    RippleItem rippleItem = rippleList.get(i);
                    if (params.length > 0 && !rippleItem.rippleName.contains(params[0]) && !rippleItem.id.contains(params[0]))
                        continue;
                    rippleDisplayList.add(rippleItem);
                }
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            // Download complete. Let us update UI
            progressBar.setVisibility(View.GONE);

            if (result != null && result == 1) {
                adapter = new RippleRecyclerViewAdapter(RippleActivity.this);
                mRecyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(RippleActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
