package eduku.org.views.Individual;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import eduku.org.R;
import eduku.org.utils.ParseClass.ParseClassKeys;
import eduku.org.utils.ParseClass.ParsePhoto;
import eduku.org.utils.UiUtil;
import eduku.org.views.Main.MainGridAdapter;

/**
 * Created by mickey on 3/8/16.
 */
public class IndividualGridAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<MainGridAdapter.PhotoUrl> mUrlArray = null;

    public IndividualGridAdapter(IndividualActivity mainActivity, ParseObject _ripple){
        mContext = mainActivity;
        GetPhotos(_ripple.getObjectId());
    }

    public void GetPhotos(String _id) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClassKeys.Photo);
        query.orderByDescending("createdAt");
        query.whereEqualTo(ParsePhoto.RippleId, _id);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                mUrlArray = new ArrayList<>();
                for (int i = 0; i < objects.size(); i++) {
                    if (objects.get(i).getBoolean(ParsePhoto.IsApproved) == false) {
                        continue;
                    }
                    ParseFile file = objects.get(i).getParseFile(ParsePhoto.Photo);
                    mUrlArray.add(new MainGridAdapter.PhotoUrl(file.getUrl(), objects.get(i).getBoolean(ParsePhoto.IsIos)));
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getCount() {
        if (mUrlArray == null){
            return 6;
        }
        if (mUrlArray.size() == 0)
            return 1;
        return mUrlArray.size();
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
            // if it's not recycled, initialize some attributes
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            cell = inflater.inflate(R.layout.activity_main_griditem, null);

            int sWidth = UiUtil.getScreenWidth((IndividualActivity) mContext);
            cell.setLayoutParams(new GridView.LayoutParams((sWidth - 15) / 2, (sWidth - 15) / 2));
        } else {
            cell = convertView;
        }

        if (mUrlArray == null || position > mUrlArray.size() - 1){
            if (mUrlArray.size() == 0)
            {
                cell.findViewById(R.id.progBar).setVisibility(View.GONE);
                cell.findViewById(R.id.imgView).setVisibility(View.VISIBLE);
                ((ImageView) cell.findViewById(R.id.imgView)).setImageResource(R.drawable.img_none);
                return cell;
            }
            cell.findViewById(R.id.progBar).setVisibility(View.VISIBLE);
            cell.findViewById(R.id.imgView).setVisibility(View.GONE);
            return cell;
        }
        ImageView imgView = (ImageView) cell.findViewById(R.id.imgView);

        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(mUrlArray.get(position).url, imgView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (mUrlArray.get(position).isIos == true) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);

                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(loadedImage, 180, (int) ((180.0 * loadedImage.getWidth()) / loadedImage.getHeight()), true);
                    Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);

                    ((ImageView) view).setImageBitmap(rotatedBitmap);
                }else{
                    ((ImageView) view).setImageBitmap(loadedImage);
                }
                cell.findViewById(R.id.progBar).setVisibility(View.GONE);
                cell.findViewById(R.id.imgView).setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

        return cell;
    }
}