package eduku.org.views.Main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import eduku.org.R;
import eduku.org.utils.UiUtil;
import mobi.parchment.widget.adapterview.gridview.GridView;

/**
 * Created by mickey on 3/1/16.
 */
public class MainGridAdapter extends BaseAdapter{
    private Context mContext;
    public static class PhotoUrl{
        public String url;
        public boolean isIos;
        public PhotoUrl(String _url, boolean _isIos){
            url = _url;
            isIos = _isIos;
        }
    }
    private ArrayList<PhotoUrl> mUrlArray = null;

    public MainGridAdapter(MainActivity mainActivity){
        mContext = mainActivity;
    }

    public void AddUrl(String _url, boolean _isIos){
        if (mUrlArray == null){
            mUrlArray = new ArrayList<>();
        }
        mUrlArray.add(new PhotoUrl(_url, _isIos));
        notifyDataSetChanged();
    }

    public void Init(){
        mUrlArray = null;
    }

    @Override
    public int getCount() {
        if (mUrlArray == null){
            return 4;
        }
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

            int sWidth = UiUtil.getScreenWidth((MainActivity)mContext);
            cell.setLayoutParams(new GridView.LayoutParams((sWidth - 6) / 4, ViewGroup.LayoutParams.MATCH_PARENT));
        } else {
            cell = convertView;
        }
        if (mUrlArray == null || position > mUrlArray.size() - 1){
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
                }else {
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
