package eduku.org.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import eduku.org.utils.Models.RecentLog;
import eduku.org.utils.Models.RippleLog;

public class Utils {

    public static String getJsonFromArray(ArrayList<RippleLog> _array) {
        JSONArray jsonArray = new JSONArray();

        for (int i = 0; i < _array.size(); i++) {
            RippleLog log = _array.get(i);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(RippleLogKeys.SenderName, log.SenderName);
                jsonObject.put(RippleLogKeys.SenderCity, log.SenderCity);
                jsonObject.put(RippleLogKeys.RecipientName, log.RecipientName);
                jsonObject.put(RippleLogKeys.RecipientCity, log.RecipientCity);
                jsonObject.put(RippleLogKeys.Date, getStringFromDate(log.SendDate));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObject);
        }

        return jsonArray.toString();
    }

    public static ArrayList<RippleLog> getArrayFromJson(String _str){
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray = new JSONArray(_str);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        ArrayList<RippleLog> logArray = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject rip = jsonArray.getJSONObject(i);
                RippleLog log = new RippleLog(
                        rip.getString(RippleLogKeys.SenderName),
                        rip.getString(RippleLogKeys.SenderCity),
                        rip.getString(RippleLogKeys.RecipientName),
                        rip.getString(RippleLogKeys.RecipientCity),
                        Utils.getDateFromString(rip.getString(RippleLogKeys.Date)));
                logArray.add(log);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  logArray;
    }

    public static String getJsonFromRecentLog(ArrayList<RecentLog> _array) {
        JSONArray jsonArray = new JSONArray();
        int length = _array.size() > 15 ? 15 : _array.size();

        for (int i = 0; i < length; i++) {
            RecentLog log = _array.get(i);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(RecentLogKeys.RippleName, log.RippleName);
                jsonObject.put(RecentLogKeys.RippleCode, log.RippleCode);
                jsonObject.put(RecentLogKeys.SenderName, log.SenderName);
                jsonObject.put(RecentLogKeys.SenderCity, log.SenderCity);
                jsonObject.put(RecentLogKeys.RecipientName, log.RecipientName);
                jsonObject.put(RecentLogKeys.RecipientCity, log.RecipientCity);
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray.toString();
    }

    public static ArrayList<RecentLog> getRecentLogFromJson(String _str) {
        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray = new JSONArray(_str);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        ArrayList<RecentLog> logArray = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject rip = jsonArray.getJSONObject(i);
                RecentLog log = new RecentLog(
                        rip.getString(RecentLogKeys.RippleName),
                        rip.getString(RecentLogKeys.RippleCode),
                        rip.getString(RecentLogKeys.SenderName),
                        rip.getString(RecentLogKeys.SenderCity),
                        rip.getString(RecentLogKeys.RecipientName),
                        rip.getString(RecentLogKeys.RecipientCity));
                logArray.add(log);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return logArray;
    }

    public static int getDaysBetweenDates(Date firstDate, Date secondDate){
        long diff = secondDate.getTime() - firstDate.getTime();
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static String getStringFromDate(Date _date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(_date);
    }

    public static Date getDateFromString(String _string){
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(_string);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getHhMmStringFromDate(Date _date){
        return new SimpleDateFormat("HH:mm").format((_date));
    }

    public static String getMonthDayStringFromNSDate(Date _date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(_date);
        int month = cal.get(Calendar.MONTH) - 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        if (day % 10  == 1){
            return Global.MonthString[month] + "." + day + "st";
        }
        else if (day % 10 == 2){
            return Global.MonthString[month] + "." + day + "nd";
        }
        else if (day % 10 == 3){
            return Global.MonthString[month] + "." + day + "rd";
        }
        else{
            return Global.MonthString[month] + "." + day + "th";
        }
    }
}
