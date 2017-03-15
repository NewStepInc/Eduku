package com.bluemax.bluemaxmng.components;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bluemax.bluemaxmng.activities.CampaignDetailActivity;
import com.bluemax.bluemaxmng.activities.CampaignMainActivity;
import com.bluemax.bluemaxmng.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class CampaignRecyclerViewAdapter extends RecyclerView.Adapter<CampaignRecyclerViewAdapter.CustomViewHolder> {
    private List<CampaignItem> campaignList;
    private Context mContext;

    public CampaignRecyclerViewAdapter(Context context) {
        this.campaignList = CampaignMainActivity.campaignDisplayList;
        this.mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.campaign_row, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {
        CampaignItem campaign = campaignList.get(i);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            Drawable drawable = ContextCompat.getDrawable(mContext, campaign.isRun ? R.drawable.ic_check_on : R.drawable.ic_check_off);
            customViewHolder.imageViewRun.setImageDrawable(drawable);
        } else {
            Drawable drawable = mContext.getResources().getDrawable(campaign.isRun ? R.drawable.ic_check_on : R.drawable.ic_check_off);
            customViewHolder.imageViewRun.setImageDrawable(drawable);
        }

        customViewHolder.textViewName.setText(campaign.name);

        SimpleDateFormat format = new SimpleDateFormat("MMM dd, yyyy");
        customViewHolder.textViewStartDate.setText(format.format(campaign.startAt));

        customViewHolder.viewContainer.setTag(customViewHolder);
        customViewHolder.viewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomViewHolder holder = (CustomViewHolder) v.getTag();
                int position = holder.getAdapterPosition();

//                CampaignItem campaignItem = campaignList.get(position);
                Intent intent = new Intent(mContext, CampaignDetailActivity.class);
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != campaignList ? campaignList.size() : 0);
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected View viewContainer;
        protected ImageView imageViewRun;
        protected TextView textViewName;
        protected TextView textViewStartDate;

        public CustomViewHolder(View view) {
            super(view);
            this.viewContainer = view.findViewById(R.id.campaign_row);
            this.imageViewRun = (ImageView) view.findViewById(R.id.campaign_row_run);
            this.textViewName = (TextView) view.findViewById(R.id.campaign_row_name);
            this.textViewStartDate = (TextView) view.findViewById(R.id.campaign_row_startdate);
        }
    }
}
