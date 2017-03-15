package eduku.org.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;

import eduku.org.R;

public class LoadingProgressDialog extends Dialog {

    // MyApplication context
    Activity activity;

    // loading image
    static Bitmap bitmapLoadingWhite;

    public LoadingProgressDialog(Activity activity, int style, float x, float y, float size) {

        super(activity, R.style.DlgTheme);

        this.activity = activity;

        initialize(style, x, y, size);
    }

    public LoadingProgressDialog(Activity activity) {

        super(activity, R.style.DlgTheme);

        this.activity = activity;

        initialize(0, 0.5f, 0.5f, 0.1f);
    }

    @Override
    public void onBackPressed() {
    }

    // initialize the dialog
    protected void initialize(int style, float fx, float fy, float fSize) {

        // User Interface
        {
            RelativeLayout rootLayout = new RelativeLayout(activity);

            int screenWidth = UiUtil.getScreenWidth(getWindow());
            int screenHeight = UiUtil.getScreenHeight(getWindow());
            int size = (int)(fSize * screenWidth);
            int mx = (int)(fx * screenWidth) - size / 2;
            int my = (int)(fy * screenHeight) - size / 2;

            // Loading
            if (style == 0) {
                rootLayout.setBackgroundColor(0xA0000000);
                TimingProgressView view = new TimingProgressView(activity);
                RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(size, size);
                layout.leftMargin = mx;
                layout.topMargin = my;
                view.setLayoutParams(layout);
                rootLayout.addView(view);
            } else {
                // load progress image
                initializeImages();

                ImageView view = new ImageView(activity);
                view.setImageBitmap(bitmapLoadingWhite);

                RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(size, size);
                layout.leftMargin = mx;
                layout.topMargin = my;
                view.setLayoutParams(layout);
                view.setScaleType(ImageView.ScaleType.FIT_XY);
                rootLayout.addView(view);

                // Animation
                {
                    RotateAnimation animRotate = new RotateAnimation(
                            0.0f, 360.0f
                            , RotateAnimation.RELATIVE_TO_SELF, 0.5f
                            , RotateAnimation.RELATIVE_TO_SELF, 0.5f);
                    animRotate.setRepeatCount(RotateAnimation.INFINITE);
                    animRotate.setInterpolator(new LinearInterpolator());
                    animRotate.setDuration(1000);
                    view.startAnimation(animRotate);
                }
            }

            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT);
            addContentView(rootLayout, params);
        }
    }

    // initialize loading images
    protected void initializeImages() {
        if (bitmapLoadingWhite == null) {
            //InputStream is = LoadingProgressDialog.class.getResourceAsStream("progressbar_white.png");
            //bitmapLoadingWhite = BitmapFactory.decodeStream(is);
            bitmapLoadingWhite = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.progressbar_white);
        }
    }


    public static class TimingProgressView extends View {

        float angle = 0.0f;

        public TimingProgressView(Context context) {
            super(context);
        }

        Paint paint = new Paint();
        RectF oval = new RectF();
        public void onDraw(Canvas canvas) {
            int width = this.getWidth();
            int height = this.getHeight();
            int border = (int)(width * 0.1);
            oval.left = border;
            oval.top = border;
            oval.right = width - border;
            oval.bottom = height - border;
            paint.setStrokeWidth(border);
            paint.setColor(0xffff6000);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawOval(oval, paint);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawArc(oval, -90, angle, true, paint);
        }


        Timer timerFlow;
        TimerTask taskFlow = new TimerTask() {
            long timeBefore;
            public void run() {
                long time = SystemClock.elapsedRealtime();
                if (timeBefore == 0) {
                    timeBefore = time;
                } else {
                    angle += 360.0f / 2000 * (time - timeBefore);
                    if (angle > 360) {
                        angle = 0;
                    }
                    postInvalidate();
                }
                timeBefore = time;
            }
        };

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (timerFlow != null) {
                timerFlow.purge();
                timerFlow.cancel();
            }
            timerFlow = new Timer();
            timerFlow.schedule(taskFlow, 10, 10);
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (timerFlow != null) {
                timerFlow.purge();
                timerFlow.cancel();
                timerFlow = null;
            }
        }
    }
}