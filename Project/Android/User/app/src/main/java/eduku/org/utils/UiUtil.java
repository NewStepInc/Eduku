package eduku.org.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.regex.Pattern;

import eduku.org.R;

public class UiUtil {

    private static final Pattern rfc2822 = Pattern
            .compile("^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$");

    public static boolean checkEmail(String email) {
        return rfc2822.matcher(email).matches();
    }

    public static float setSpUnit(Context context, double size) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        float orig = metrics.scaledDensity;
        metrics.scaledDensity = (float)size;
        return orig;
    }

    /**
     * get the screen size
     */
    public static int getScreenWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        return display.getWidth();
    }

    /**
     * get the screen size
     */
    public static int getScreenHeight(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        return display.getHeight();
    }

    /**
     * get the screen size
     */
    public static int getScreenWidth(Window window) {
        Display display = window.getWindowManager().getDefaultDisplay();
        return display.getWidth();
    }

    /**
     * get the screen size
     */
    public static int getScreenHeight(Window window) {
        Display display = window.getWindowManager().getDefaultDisplay();
        return display.getHeight();
    }

    /**
     * make a view as a button (not ImageView)
     */
    public static void applyButtonEffect(final View view, final Runnable onClickListener) {
        final ColorFilter filter = new LightingColorFilter(0xA0A0A0A0, 0);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Drawable background = v.getBackground();
                    if (background != null) {
                        background.setColorFilter(filter);
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    Drawable background = v.getBackground();
                    if (background != null) {
                        background.setColorFilter(null);
                    }
                    if (onClickListener != null) {
                        float x = event.getX();
                        float y = event.getY();
                        if (checkLocalPointInView(view, x, y)) {
                            onClickListener.run();
                        }
                    }

                }
                v.onTouchEvent(event);
                return true;
            }
        });
    }

    /**
     * make an image view as a button
     */
    public static void applyImageButtonEffect(final ImageView view, final Runnable onClickListener) {
        final ColorFilter filter = new LightingColorFilter(0xA0A0A0A0, 0);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    view.setColorFilter(filter);
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    view.setColorFilter(null);
                    if (onClickListener != null) {
                        float x = event.getX();
                        float y = event.getY();
                        if (checkLocalPointInView(view, x, y)) {
                            onClickListener.run();
                        }
                    }
                }
                return true;
            }
        });
    }

    public static void applyImageButtonEffect(final ImageView view, final int resImageNormal, final int resImageDown, final Runnable onClickListener) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    view.setImageResource(resImageDown);
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    view.setImageResource(resImageNormal);
                    if (onClickListener != null) {
                        float x = event.getX();
                        float y = event.getY();
                        if (checkLocalPointInView(view, x, y)) {
                            onClickListener.run();
                        }
                    }
                }
                return true;
            }
        });
    }

    /**
     * make a view as a button (not ImageView)
     */
    public static void applyTextButtonEffect(final TextView view, final int colorNormal, final int colorFocus, final Runnable onClickListener) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    view.setTextColor(colorFocus);
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    view.setTextColor(colorNormal);
                    if (onClickListener != null) {
                        float x = event.getX();
                        float y = event.getY();
                        if (checkLocalPointInView(view, x, y)) {
                            onClickListener.run();
                        }
                    }
                }
                v.onTouchEvent(event);
                return true;
            }
        });
    }

    /**
     * make a view as a button (not ImageView)
     */
    public static void applyButtonEffect(final View view, final int backgroundColorNormal, final int backgroundColorFocus, final Runnable onClickListener) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    view.setBackgroundColor(backgroundColorFocus);
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    view.setBackgroundColor(backgroundColorNormal);
                    if (onClickListener != null) {
                        float x = event.getX();
                        float y = event.getY();
                        if (checkLocalPointInView(view, x, y)) {
                            onClickListener.run();
                        }
                    }
                }
                v.onTouchEvent(event);
                return true;
            }
        });
    }

    /**
     *  check a point that is in the view visible area
     */
    public static boolean checkLocalPointInView(View view, float x, float y) {
        int width = view.getWidth();
        int height = view.getHeight();
        return 0 < x && x < width && 0 < y && y < height;
    }

    static float getSp(Activity activity) {
        return UiUtil.getScreenWidth(activity) / 400.0f;
    }

    /* Alert functions for Android platform */
    public static void alert(Activity context, Object titleId, Object contentId, final Runnable runnable) {
        alert(context, titleId, contentId, R.string.ok, runnable);
    }

    public static void alertOkCancel(Activity context, Object titleId, Object contentId, final Runnable runnableOK) {
        alert(context, titleId, contentId, R.string.ok, runnableOK, R.string.cancel, null);
    }

    public static void alertYesNo(Activity context, Object titleId, Object contentId, final Runnable runnableOK) {
        alert(context, titleId, contentId, R.string.yes, runnableOK, R.string.no, null);
    }

    public static void alertYesNoCancel(Activity context, Object titleId, Object contentId, final Runnable runnableYes, final Runnable runnableNo) {
        alert(context, titleId, contentId, R.string.yes, runnableYes, R.string.no, runnableNo, R.string.cancel, null);
    }

    public static void alertWithUrl(Activity context, Object titleId, String contentId, final Runnable runnable) {
        float sp = UiUtil.setSpUnit(context, getSp(context));
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            setAlertTitle(alertDialogBuilder, titleId);

            final TextView message = new TextView(context);
            // i.e.: R.string.dialog_message =>
            // "Test this dialog following the link to dtmilano.blogspot.com"
            final SpannableString s =
                    new SpannableString(contentId);
            Linkify.addLinks(s, Linkify.WEB_URLS);
            message.setText(s);
            message.setMovementMethod(LinkMovementMethod.getInstance());

            alertDialogBuilder.setView(message);
            alertDialogBuilder.setCancelable(false);
            setAlertPositiveButton(alertDialogBuilder, R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            TextView messageView = (TextView)alertDialog.findViewById(android.R.id.message);
            messageView.setGravity(Gravity.CENTER);
        } finally {
            UiUtil.setSpUnit(context, sp);
        }
    }

    public static void alert(Activity context, Object titleId, Object contentId, Object okId, final Runnable runnable) {
        float sp = UiUtil.setSpUnit(context, getSp(context));
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            setAlertTitle(alertDialogBuilder, titleId);
            setAlertMessage(alertDialogBuilder, contentId);
            alertDialogBuilder.setCancelable(false);
            setAlertPositiveButton(alertDialogBuilder, okId, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            TextView messageView = (TextView)alertDialog.findViewById(android.R.id.message);
            messageView.setGravity(Gravity.CENTER);
        } finally {
            UiUtil.setSpUnit(context, sp);
        }
    }

    public static void alert(Activity context, Object titleId, Object contentId, Object okId, final Runnable runnableOK, Object cancelId, final Runnable runnableCancel) {
        float sp = UiUtil.setSpUnit(context, getSp(context));
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            setAlertTitle(alertDialogBuilder, titleId);
            setAlertMessage(alertDialogBuilder, contentId);
            alertDialogBuilder.setCancelable(true);
            setAlertPositiveButton(alertDialogBuilder, okId, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (runnableOK != null) {
                        runnableOK.run();
                    }
                }
            });
            setAlertNegativeButton(alertDialogBuilder, cancelId, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (runnableCancel != null) {
                        runnableCancel.run();
                    }
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            TextView messageView = (TextView)alertDialog.findViewById(android.R.id.message);
            messageView.setGravity(Gravity.CENTER);
        } finally {
            UiUtil.setSpUnit(context, sp);
        }
    }


    public static void alert(Activity context, Object titleId, Object contentId
            , Object yesId, final Runnable runnableYes
            , Object noId, final Runnable runnableNo
            , Object cancelId, final Runnable runnableCancel) {

        float sp = UiUtil.setSpUnit(context, getSp(context));
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            setAlertTitle(alertDialogBuilder, titleId);
            setAlertMessage(alertDialogBuilder, contentId);
            alertDialogBuilder.setCancelable(true);
            setAlertPositiveButton(alertDialogBuilder, yesId, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (runnableYes != null) {
                        runnableYes.run();
                    }
                }
            });
            setAlertNeutralButton(alertDialogBuilder, noId, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (runnableNo != null) {
                        runnableNo.run();
                    }
                }
            });
            setAlertNegativeButton(alertDialogBuilder, cancelId, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (runnableCancel != null) {
                        runnableCancel.run();
                    }
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            TextView messageView = (TextView)alertDialog.findViewById(android.R.id.message);
            messageView.setGravity(Gravity.CENTER);
        } finally {
            UiUtil.setSpUnit(context, sp);
        }
    }

    private static void setAlertTitle(AlertDialog.Builder builder, Object value) {
        if (value != null) {
            if (value instanceof Integer) {
                builder.setTitle((Integer)value);
            } else {
                builder.setTitle(value.toString());
            }
        }
    }

    private static void setAlertMessage(AlertDialog.Builder builder, Object value) {
        if (value != null) {
            if (value instanceof Integer) {
                builder.setMessage((Integer) value);
            } else {
                builder.setMessage(value.toString());
            }
        }
    }

    private static void setAlertPositiveButton(AlertDialog.Builder builder, Object value, DialogInterface.OnClickListener listener) {
        if (value != null) {
            if (value instanceof Integer) {
                builder.setPositiveButton((Integer) value, listener);
            } else {
                builder.setPositiveButton(value.toString(), listener);
            }
        }
    }

    private static void setAlertNeutralButton(AlertDialog.Builder builder, Object value, DialogInterface.OnClickListener listener) {
        if (value != null) {
            if (value instanceof Integer) {
                builder.setNeutralButton((Integer) value, listener);
            } else {
                builder.setNeutralButton(value.toString(), listener);
            }
        }
    }

    private static void setAlertNegativeButton(AlertDialog.Builder builder, Object value, DialogInterface.OnClickListener listener) {
        if (value != null) {
            if (value instanceof Integer) {
                builder.setNegativeButton((Integer) value, listener);
            } else {
                builder.setNegativeButton(value.toString(), listener);
            }
        }
    }
}
