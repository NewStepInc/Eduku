package eduku.org.views.Main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import eduku.org.R;
import eduku.org.utils.Global;
import eduku.org.utils.Models.RecentLog;

/**
 * Created by mickey on 3/1/16.
 */
public class MainListAdapter extends BaseAdapter {
    private Context mContext;

    public MainListAdapter(MainActivity mainActivity){
        mContext = mainActivity;
    }
    @Override
    public int getCount() {
        if (Global.recentUpdates == null || Global.recentUpdates.recentLog == null)
            return 4;
        return Global.recentUpdates.recentLog.size();
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
        View cell;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            cell = inflater.inflate(R.layout.activity_main_listitem, parent, false);
        }else{
            cell = convertView;
        }
        if (Global.recentUpdates == null || Global.recentUpdates.recentLog == null || Global.recentUpdates.recentLog.get(position) == null){
            cell.findViewById(R.id.progBar).setVisibility(View.VISIBLE);
            cell.findViewById(R.id.main_content).setVisibility(View.GONE);
            return cell;
        }
        cell.findViewById(R.id.progBar).setVisibility(View.GONE);
        cell.findViewById(R.id.main_content).setVisibility(View.VISIBLE);

        RecentLog log = Global.recentUpdates.recentLog.get(position);
        ((TextView) cell.findViewById(R.id.txtRipple)).setText(log.RippleName + " / " + log.RippleCode);
        ((TextView) cell.findViewById(R.id.txtSenderName)).setText(log.SenderName);
        ((TextView) cell.findViewById(R.id.txtSenderCity)).setText(log.SenderCity);
        ((TextView) cell.findViewById(R.id.txtRecipientName)).setText(log.RecipientName);
        ((TextView) cell.findViewById(R.id.txtRecipientCity)).setText(log.RecipientCity);

        return cell;
    }
}
