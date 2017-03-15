package eduku.org.views.Individual;

import android.content.Context;
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
import eduku.org.utils.Models.RippleLog;
import eduku.org.utils.ParseClass.ParseClassKeys;
import eduku.org.utils.ParseClass.ParseRipple;
import eduku.org.utils.Utils;
import eduku.org.views.Main.MainActivity;
import eduku.org.views.Popular.PopularActivity;

/**
 * Created by mickey on 3/8/16.
 */
public class IndividualListAdapter extends BaseAdapter {
    private Context mContext;
    private ParseObject m_Ripple;
    private ArrayList<RippleLog> mRippleLogArray;

    public IndividualListAdapter(IndividualActivity mainActivity, ParseObject _ripple){
        mContext = mainActivity;
        m_Ripple = _ripple;
        GetLogArray();
    }

    public void GetLogArray() {
        if (m_Ripple.getString(ParseRipple.RippleLog) == null) {
            mRippleLogArray = new ArrayList<>();
        }else {
            mRippleLogArray = Utils.getArrayFromJson(m_Ripple.getString(ParseRipple.RippleLog));
        }
    }

    @Override
    public int getCount() {
        if (mRippleLogArray == null)
            return 6;
        else
            return mRippleLogArray.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final View cell;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            cell = inflater.inflate(R.layout.activity_individual_listcell, parent, false);
        }else{
            cell = convertView;
        }
        if (mRippleLogArray == null || position > mRippleLogArray.size() - 1){
            cell.findViewById(R.id.progBar).setVisibility(View.VISIBLE);
            cell.findViewById(R.id.individual_content).setVisibility(View.GONE);
            return cell;
        }
        cell.findViewById(R.id.progBar).setVisibility(View.GONE);
        cell.findViewById(R.id.individual_content).setVisibility(View.VISIBLE);

        RippleLog log = mRippleLogArray.get(position);
        ((TextView) cell.findViewById(R.id.txtSenderName)).setText(log.SenderName);
        ((TextView) cell.findViewById(R.id.txtSenderCity)).setText(log.SenderCity);
        ((TextView) cell.findViewById(R.id.txtRecipientName)).setText(log.RecipientName);
        ((TextView) cell.findViewById(R.id.txtRecipientCity)).setText(log.RecipientCity);
        ((TextView) cell.findViewById(R.id.txtDate)).setText(Utils.getMonthDayStringFromNSDate(log.SendDate));
        ((TextView) cell.findViewById(R.id.txtTime)).setText(Utils.getHhMmStringFromDate(log.SendDate));

        return cell;
    }
}