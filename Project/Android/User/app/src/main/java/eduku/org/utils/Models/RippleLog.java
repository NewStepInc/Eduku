package eduku.org.utils.Models;

import java.util.Date;

/**
 * Created by mickey on 2/23/16.
 */
public class RippleLog {
    public String SenderName;
    public String SenderCity;
    public String RecipientName;
    public String RecipientCity;
    public Date SendDate;

    public RippleLog(String _senderName, String _senderCity, String _recipientName, String _recipientCity, Date _date) {
        SenderName = _senderName;
        SenderCity = _senderCity;
        RecipientName = _recipientName;
        RecipientCity = _recipientCity;
        SendDate = _date;
    }
}
