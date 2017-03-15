package eduku.org.views.Main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import eduku.org.R;
import eduku.org.utils.UiUtil;
import eduku.org.views.CommonActivity;
import fr.tvbarthel.lib.blurdialogfragment.BlurDialogFragment;

/**
 * Created by mickey on 3/2/16.
 */
public class ScanDialog extends BlurDialogFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));

        View view = inflater.inflate(R.layout.activity_main_scandialog, null);
        ImageView btnQrScan = (ImageView)view.findViewById(R.id.btn_scan);
        ImageView btnOk = (ImageView)view.findViewById(R.id.btn_ok);
        ImageView btnCancel = (ImageView)view.findViewById(R.id.btn_cancel);
        ImageView btnClose = (ImageView)view.findViewById(R.id.btn_close);

        UiUtil.applyImageButtonEffect(btnQrScan, new Runnable() {
            public void run() {
                OnQrScan();
            }
        });
        UiUtil.applyImageButtonEffect(btnOk, new Runnable() {
            public void run() {
                OnOk();
            }
        });
        UiUtil.applyImageButtonEffect(btnCancel, new Runnable() {
            public void run() {
                OnCancel();
            }
        });
        UiUtil.applyImageButtonEffect(btnClose, new Runnable() {
            public void run() {
                dismiss();
            }
        });
        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    public static interface OnCompleteListener {
        public abstract void onComplete(String rippleId);
    }

    private OnCompleteListener mListener;

    // make sure the Activity implemented it
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            this.mListener = (OnCompleteListener)activity;
        }
        catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }
    }

    public void OnQrScan(){
        Intent i = new Intent(getActivity(),  QRScannerActivity.class);
        startActivityForResult(i, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                ((EditText)getDialog().findViewById(R.id.txt_rippleid)).setText(data.getStringExtra("rippleid"));
            }
        }
    }

    public void OnOk(){
        this.mListener.onComplete(((EditText)getDialog().findViewById(R.id.txt_rippleid)).getText().toString());
    }

    public void OnCancel(){
        dismiss();
    }
}
