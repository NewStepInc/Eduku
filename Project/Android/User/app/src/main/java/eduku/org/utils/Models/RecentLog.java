package eduku.org.utils.Models;

/**
 * Created by mickey on 2/23/16.
 */
public class RecentLog {
    public String RippleName;
    public String RippleCode;
    public String SenderName;
    public String SenderCity;
    public String RecipientName;
    public String RecipientCity;

    public RecentLog(String _rippleName, String _rippleCode, String _senderName, String _senderCity, String _recipientName, String _recipientCity) {
        RippleName = _rippleName;
        RippleCode = _rippleCode;
        SenderName = _senderName;
        SenderCity = _senderCity;
        RecipientName = _recipientName;
        RecipientCity = _recipientCity;
    }

}
