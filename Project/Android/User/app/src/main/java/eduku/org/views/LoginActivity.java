package eduku.org.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import eduku.org.R;
import eduku.org.utils.Global;
import eduku.org.utils.Models.User;
import eduku.org.utils.ParseClass.ParseUserKeys;
import eduku.org.utils.UiUtil;
import eduku.org.utils.UserDialogs;
import eduku.org.views.Main.MainActivity;

public class LoginActivity extends CommonActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageView btnSignF = (ImageView) findViewById(R.id.btn_facebook);
        ImageView btnLogin = (ImageView) findViewById(R.id.btn_login);
        ImageView btnSignup = (ImageView) findViewById(R.id.btn_signup);

        UiUtil.applyTextButtonEffect((TextView) findViewById(R.id.btn_forgot), Color.parseColor("#000000"), Color.parseColor("#a9a9a9"), new Runnable() {
            public void run() {
                OnForgot();
            }
        });

        UiUtil.applyImageButtonEffect(btnSignF, new Runnable() {
            public void run() {
                showLoading();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        onFacebookLogin();
                    }
                }).start();
            }
        });
        UiUtil.applyImageButtonEffect(btnLogin, new Runnable() {
            public void run() {
                onLogin();

            }
        });
        UiUtil.applyImageButtonEffect(btnSignup, new Runnable() {
            public void run() {
                onSignup();
            }
        });
    }

    public void onLogin() {
        final EditText txtEmail = (EditText) findViewById(R.id.txt_email);
        final EditText txtPass = (EditText) findViewById(R.id.txt_password);

        if (txtEmail.length() == 0) {
            UiUtil.alert(LoginActivity.this, "Login", "Please input the email address!", null);
            txtEmail.requestFocus();
            return;
        }
        if (txtPass.length() == 0) {
            UiUtil.alert(LoginActivity.this, "Login", "Please input the password!", null);
            txtPass.requestFocus();
            return;
        }
//                if (!UiUtil.checkEmail(txtEmail.getText().toString())) {
//                    UiUtil.alert(LoginActivity.this, "Login", "Please input the email correctly!", null);
//                    txtEmail.requestFocus();
//                    return;
//                }
        showLoading();
        ParseUser.logInInBackground(txtEmail.getText().toString().toLowerCase(), txtPass.getText().toString(), new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    Global.currentUser = new User(ParseUser.getCurrentUser());
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                    hideLoading();
                    if (e.getCode() == 101)
                        UiUtil.alert(LoginActivity.this, "Login Failed", UserDialogs.SigninIncorrect, null);
                    else {
                        StringBuilder error = new StringBuilder(e.getMessage());
                        if (Character.isLowerCase(error.charAt(0)))
                            error.setCharAt(0, Character.toUpperCase(error.charAt(0)));
                        UiUtil.alert(LoginActivity.this, "Login Failed", error.toString() + "!", null);
                    }
                }
            }
        });
    }

    public void onFacebookLogin(){
        List<String> permissions = Arrays.asList("email", "public_profile");
        ParseUser.logOut();
        ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user == null) {
                    hideLoading();
                } else {
                    Global.currentUser = new User(ParseUser.getCurrentUser());

                    if (Global.currentUser.email != null){
                        hideLoading();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    }

                    GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                            try {
                                Global.currentUser.fullname = user.getString("first_name");
                                Global.currentUser.email = user.getString("email");
                                Global.currentUser.save(false);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                            hideLoading();
                            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                            intent.putExtra("social", true);
                            startActivity(intent);
                            finish();
                        }
                    });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id, email, first_name");
                    request.setParameters(parameters);
                    request.executeAsync();
                }
            }
        });
    }
    public void onSignup() {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    public void OnForgot(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        // Create EditText for entry
        final EditText input = new EditText(this);
        input.setHint("Recover password");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        alert.setView(input);

        // Make an "OK" button to save the name
        alert.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.setNegativeButton("Send", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                ParseUser.requestPasswordResetInBackground(input.getText().toString().toLowerCase());
            }
        });

        Dialog d = alert.create();

        d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        d.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}
