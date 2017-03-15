package eduku.org.views.Popular;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import eduku.org.R;
import eduku.org.utils.Global;
import eduku.org.utils.Models.RecentLog;
import eduku.org.utils.Models.Ripple;
import eduku.org.utils.ParseClass.ParseClassKeys;
import eduku.org.utils.UiUtil;
import eduku.org.views.Individual.IndividualActivity;

public class PopularListAdapter extends BaseAdapter{
    private Context mContext;
    private boolean m_bSearch;
    private int m_nCount = 6;
    public ArrayList<Ripple> mRippleArray, mTempArray;

    public PopularListAdapter(PopularActivity mainActivity, boolean _bSearch){
        mContext = mainActivity;
        m_bSearch = _bSearch;
        if (m_bSearch == false) {
            SetCount(Global.currentUser.followRipples.length());
            for (int i = 0; i < Global.currentUser.followRipples.length(); i++){
                try {
                    AddRipple(Global.currentUser.followRipples.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            MakeRippleArray();
        }
    }

    public void SetCount(int _count){
        m_nCount = _count;
        notifyDataSetChanged();
    }


    public void AddRipple(String _id){
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClassKeys.Ripple);
        query.getInBackground(_id, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (mRippleArray == null)
                    mRippleArray = new ArrayList<>();
                mRippleArray.add(new Ripple(object, new Ripple.MyCallbackInterface() {
                    @Override
                    public void OnDownloadFinished() {
                        notifyDataSetChanged();
                    }
                }));
            }
        });
    }

    public void MakeRippleArray(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClassKeys.Ripple);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    SetCount(objects.size());
                    if (mTempArray == null)
                        mTempArray = new ArrayList<>();
                    for (int i = 0; i < objects.size(); i++) {
                        mTempArray.add(new Ripple(objects.get(i), new Ripple.MyCallbackInterface() {
                            @Override
                            public void OnDownloadFinished() {
                                mRippleArray = mTempArray;
                                notifyDataSetChanged();
                            }
                        }));
                    }
                }
            }
        });
    }

    public void Filter(String _search){
        if (_search == ""){
            mRippleArray = mTempArray;
            SetCount(mRippleArray.size());
            return;
        }
        mRippleArray = new ArrayList<>();
        for (Ripple rip: mTempArray){
            if (rip.name.toLowerCase().contains(_search) || rip.code.toLowerCase().contains(_search)) {
                mRippleArray.add(rip);
            }
        }
        SetCount(mRippleArray.size());
    }

    @Override
    public int getCount() {
        return m_nCount;
    }

    @Override
    public boolean isEnabled(int position) {
        if (mRippleArray == null || position > mRippleArray.size() - 1 || mRippleArray.get(position).object == null || mRippleArray.get(position).campaign == null){
            return false;
        }
        return super.isEnabled(position);
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View cell;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            cell = inflater.inflate(R.layout.activity_popular_cell, parent, false);
        }else{
            cell = convertView;
        }
        if (mRippleArray == null || position > mRippleArray.size() - 1){
            cell.findViewById(R.id.progBar).setVisibility(View.VISIBLE);
            cell.findViewById(R.id.popular_content).setVisibility(View.GONE);
            return cell;
        }
        cell.findViewById(R.id.progBar).setVisibility(View.GONE);
        cell.findViewById(R.id.popular_content).setVisibility(View.VISIBLE);

        final Ripple ripple = mRippleArray.get(position);
        ((TextView) cell.findViewById(R.id.txtRippleName)).setText(ripple.name);
        ((TextView) cell.findViewById(R.id.txtRippleCode)).setText(ripple.code);

        if (mRippleArray.get(position).photoUrl == null) {
            cell.findViewById(R.id.progBarCamera).setVisibility(View.VISIBLE);
            cell.findViewById(R.id.btn_camera).setVisibility(View.GONE);
            cell.findViewById(R.id.btn_photo).setVisibility(View.GONE);
            return cell;
        }

        ImageView imgView = (ImageView) cell.findViewById(R.id.btn_photo);
        if (mRippleArray.get(position).photoUrl == ""){
            imgView.setImageResource(R.drawable.img_none);
            cell.findViewById(R.id.progBarCamera).setVisibility(View.GONE);
            cell.findViewById(R.id.btn_camera).setVisibility(View.VISIBLE);
            cell.findViewById(R.id.btn_photo).setVisibility(View.VISIBLE);
            return cell;
        }

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(mRippleArray.get(position).photoUrl, imgView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (mRippleArray.get(position).isIos == true) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);

                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(loadedImage, 180, (int) ((180.0 * loadedImage.getWidth()) / loadedImage.getHeight()), true);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

                    ((ImageView) view).setImageBitmap(rotatedBitmap);
                } else {
                    ((ImageView) view).setImageBitmap(loadedImage);
                }
                cell.findViewById(R.id.progBarCamera).setVisibility(View.GONE);
                cell.findViewById(R.id.btn_camera).setVisibility(View.VISIBLE);
                cell.findViewById(R.id.btn_photo).setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

        if (ripple.object == null || ripple.campaign == null)
            return cell;

        UiUtil.applyButtonEffect(cell.findViewById(R.id.view_photo), new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(mContext, IndividualActivity.class);
                IndividualActivity.m_Ripple = ripple.object;
                IndividualActivity.m_Campaign = ripple.campaign;
                IndividualActivity.m_photo = true;
                mContext.startActivity(i);
            }
        });

        UiUtil.applyImageButtonEffect((ImageView) cell.findViewById(R.id.btn_pin), new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(mContext, IndividualActivity.class);
                IndividualActivity.m_Ripple = ripple.object;
                IndividualActivity.m_Campaign = ripple.campaign;
                IndividualActivity.m_photo = false;
                mContext.startActivity(i);
            }
        });

        return cell;
    }
}
