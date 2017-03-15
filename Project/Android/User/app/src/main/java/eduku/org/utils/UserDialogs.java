package eduku.org.utils;

/**
 * Created by mickey on 2/23/16.
 */
public interface UserDialogs {
    String SigninIncorrect        = "Your login information is incorrect.";
    String UsernameIsTaken        = "That email address is already taken.";
    String CompleteRequireFields  = "Please complete all required fields.";
    String RequireEmailAddress    = "Email address should be required.";
    String SaveDataFailed         = "Saving data is failed. Please try again!";
    String ReportsFromDateError   = "This date should not greater than last date.";
    String ReportsToDateError     = "This date should not greater than today date.";
    String PasswordCurrentDiff    = "Current password is not same.";
    String PasswordConfirmDiff    = "New password and confirm password is not same.";
}
