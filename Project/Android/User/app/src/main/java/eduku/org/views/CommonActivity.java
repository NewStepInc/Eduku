package eduku.org.views;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import eduku.org.utils.LoadingProgressDialog;
import eduku.org.utils.UiUtil;

public abstract class CommonActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSpForApp();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setSpForApp();
    }

    public void setSpForApp() { UiUtil.setSpUnit(this, getSpForApp());
    }

    public float getSpForApp() {
        return UiUtil.getScreenWidth(this) / 400.0f;
    }

    LoadingProgressDialog dlgLoading;
    /**
     * show loading
     */
    public void showLoading() {
        if (dlgLoading == null) {
            try {
                dlgLoading = new LoadingProgressDialog(this);
                dlgLoading.show();
            } catch (Exception e) {
            }
        }
    }

    public void hideLoading() {
        if (dlgLoading != null) {
            try {
                dlgLoading.dismiss();
            } catch (Exception e) {
            }
            dlgLoading = null;
        }
    }

    public void HideKeyboard(){
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}