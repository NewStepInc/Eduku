package com.bluemax.bluemaxmng.components;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluemax.bluemaxmng.GlobalConstants;
import com.bluemax.bluemaxmng.R;
import com.bluemax.bluemaxmng.activities.RippleActivity;
import com.bluemax.bluemaxmng.activities.RippleDetailActivity;

import java.util.List;

public class RippleRecyclerViewAdapter extends RecyclerView.Adapter<RippleRecyclerViewAdapter.CustomViewHolder> {
    private List<RippleItem> rippleList;
    private Context mContext;

    public RippleRecyclerViewAdapter(Context context) {
        this.rippleList = RippleActivity.rippleDisplayList;
        mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ripple_row, null);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int position) {
        RippleItem rippleItem = rippleList.get(position);

        customViewHolder.textViewNo.setText(String.valueOf(position + 1));
        customViewHolder.textViewId.setText(rippleItem.id);
        customViewHolder.textViewTitle.setText(rippleItem.rippleName);
        customViewHolder.textViewUser.setText(GlobalConstants.getUserName(rippleItem.userId));

        customViewHolder.viewContainer.setTag(customViewHolder);
        customViewHolder.viewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomViewHolder holder = (CustomViewHolder) v.getTag();
                int position = holder.getAdapterPosition();

                Intent intent = new Intent(mContext, RippleDetailActivity.class);
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != rippleList ? rippleList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected View viewContainer;
        protected TextView textViewNo;
        protected TextView textViewId;
        protected TextView textViewTitle;
        protected TextView textViewUser;

        public CustomViewHolder(View view) {
            super(view);
            this.viewContainer = view.findViewById(R.id.ripple_row);
            this.textViewNo = (TextView) view.findViewById(R.id.ripple_row_no);
            this.textViewId = (TextView) view.findViewById(R.id.ripple_row_id);
            this.textViewTitle = (TextView) view.findViewById(R.id.ripple_row_title);
            this.textViewUser = (TextView) view.findViewById(R.id.ripple_row_user);
        }
    }
}
