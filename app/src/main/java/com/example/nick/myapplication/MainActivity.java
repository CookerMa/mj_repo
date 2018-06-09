package com.example.nick.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;

public class MainActivity extends AppCompatActivity {
    PDFView pdfView;
    public static final String SAMPLE_FILE = "test.pdf";
    private float pageTop, pageBottom, pageLeft, pageRight;
    private String MTEXT = "保存至手机";
    Paint mPaint = new Paint();
    Paint mTextPaint = new Paint();
    boolean b = true;
    private RectF rectF;
    private float baseline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPaint.setColor(Color.parseColor("#363636"));
        mTextPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(45);


        //模拟冲突
        //我是一个大西瓜
        final DisplayMetrics metrics = getResources().getDisplayMetrics();

        pdfView = findViewById(R.id.pdf);
        pdfView.fromAsset(SAMPLE_FILE)
                .enableSwipe(true) // allows to block changing pages using swipe
                .swipeHorizontal(true)
                .enableDoubletap(false)
                .defaultPage(0)
                .onDrawAll(new OnDrawListener() {
                    @Override
                    public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

                        if (b) {
                            final Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();

                            int top = metrics.heightPixels - getStatusHeight(MainActivity.this);
                            pageTop = top / 2 - pageHeight / 2;
                            pageBottom = top / 2 + pageHeight / 2;
                            pageLeft = 0;
                            pageRight = metrics.widthPixels;

                            Log.e("mj", "top = " + pageTop + "----------end = " + pageBottom + "----------left =" +
                                    pageLeft + "--------pageR = " + pageRight
                            );


                            float rt = metrics.widthPixels / 2 - 195;
                            float rr = metrics.widthPixels / 2 + 195;
                            rectF = new RectF(rt, pageTop + 145, rr, pageTop + 250);

                            baseline = (rectF.bottom + rectF.top - fontMetrics.bottom - fontMetrics.top) / 2;

                          b = false ;
                        }

                        canvas.drawRoundRect(rectF, 150, 150, mPaint);
                        canvas.drawText(MTEXT, rectF.centerX(), baseline, mTextPaint);
                    }
                })
                .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                .password(null)
                .scrollHandle(null)
                .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                .spacing(0)
                .load();

//        pdfView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                float y = event.getY();
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    if (y < pageTop || y > pageBottom) {
//                        float compareY = y - metrics.heightPixels/2 + pageTop/2 ;
//                        if (rectF.contains(event.getX(),compareY)) {
//                            Log.e("mj", "scale ====================");
//                        } else
//                            Log.e("mj", "finish =====================");
//                    } else {
//                        Log.e("mj", "tap ==================");
//                        return false ;
//                    }
//                    return true;
//                }
//                return true;
//            }
//        });

    }

    public int getStatusHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

}
