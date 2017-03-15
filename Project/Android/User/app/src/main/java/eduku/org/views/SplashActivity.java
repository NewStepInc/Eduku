package eduku.org.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.parse.ParseUser;

import eduku.org.R;
import eduku.org.utils.Global;
import eduku.org.utils.ParseClass.ParseUserKeys;

public class SplashActivity extends CommonActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
//                    if (ParseUser.getCurrentUser() != null && ParseUser.getCurrentUser().getObjectId() != null)
//                    {
//                        Global.currentUser = ParseUser.getCurrentUser();
//                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                        startActivity(intent);
//                    }
                    //else {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    //}
                }
            }
        };
        timerThread.start();
    }
}
