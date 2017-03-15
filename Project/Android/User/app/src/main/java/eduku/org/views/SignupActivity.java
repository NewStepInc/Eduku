package eduku.org.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import eduku.org.R;
import eduku.org.utils.Global;
import eduku.org.utils.Models.User;
import eduku.org.utils.ParseClass.ParseUserKeys;
import eduku.org.utils.UiUtil;
import eduku.org.utils.UserDialogs;
import eduku.org.views.Main.MainActivity;

public class SignupActivity extends CommonActivity {

    private  boolean m_bSocial;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ImageView btnBack = (ImageView) findViewById(R.id.btn_back);
        UiUtil.applyImageButtonEffect(btnBack, new Runnable() {
            public void run() {
                finish();
            }
        });

        m_bSocial = getIntent().getBooleanExtra("social", false);
        if (m_bSocial == true){
            findViewById(R.id.txt_password).setVisibility(View.GONE);
            findViewById(R.id.txt_name).setEnabled(false);
            findViewById(R.id.txt_email).setEnabled(false);
            ((EditText)findViewById(R.id.txt_name)).setText(Global.currentUser.fullname);
            ((EditText)findViewById(R.id.txt_email)).setText(Global.currentUser.email);
        }
    }

    public void onSignup(View view){
        EditText txtName = (EditText) findViewById(R.id.txt_name);
        EditText txtEmail = (EditText) findViewById(R.id.txt_email);
        EditText txtPass = (EditText) findViewById(R.id.txt_password);
        EditText txtCity = (EditText) findViewById(R.id.txt_city);
        EditText txtCountry = (EditText) findViewById(R.id.txt_country);

        if (txtName.length() == 0) {
            UiUtil.alert(this, "Signup", "Please input the name!", null);
            txtName.requestFocus();
            return;
        }
        if (txtEmail.length() == 0) {
            UiUtil.alert(this, "Signup", "Please input the email address!", null);
            txtEmail.requestFocus();
            return;
        }
        if (txtPass.length() == 0 && m_bSocial == false) {
            UiUtil.alert(this, "Signup", "Please input the password!", null);
            txtPass.requestFocus();
            return;
        }
        if (txtCity.length() == 0) {
            UiUtil.alert(this, "Signup", "Please input the city!", null);
            txtCity.requestFocus();
            return;
        }
        if (txtCountry.length() == 0) {
            UiUtil.alert(this, "Signup", "Please input the country!", null);
            txtCountry.requestFocus();
            return;
        }
//                if (!UiUtil.checkEmail(txtEmail.getText().toString()))
//                {
//                    UiUtil.alert(NewAccountActivity.this, "Signup", "Please input the email correctly!", null);
//                    txtEmail.requestFocus();
//                    return;
//                }

        if (m_bSocial == true){
            Global.currentUser.city = txtCity.getText().toString();
            Global.currentUser.country = txtCountry.getText().toString();
            Global.currentUser.save(false);
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        final ParseUser user = new ParseUser();
        user.setUsername(txtEmail.getText().toString());
        user.setPassword(txtPass.getText().toString());
        user.setEmail(txtEmail.getText().toString().toLowerCase());

// other fields can be set just like with ParseObject
        user.put(ParseUserKeys.FullName, txtName.getText().toString());
        user.put(ParseUserKeys.City, txtCity.getText().toString());
        user.put(ParseUserKeys.Country, txtCountry.getText().toString());

        showLoading();
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Global.currentUser = new User(user);
                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    hideLoading();
                    if (e.getCode() == 202)
                        UiUtil.alert(SignupActivity.this, "Signup Failed", UserDialogs.UsernameIsTaken, null);
                    else {
                        StringBuilder error = new StringBuilder(e.getMessage());
                        if (Character.isLowerCase(error.charAt(0)))
                            error.setCharAt(0, Character.toUpperCase(error.charAt(0)));
                        UiUtil.alert(SignupActivity.this, "Signup Failed", error.toString() + "!", null);
                    }
                }
            }
        });
    }
}
