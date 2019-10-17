package com.example.wyf.classchat.weight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.util.DisplayUtils;

/**
 * Created by Administrator on 2018/3/1/001.
 */

@SuppressLint("AppCompatCustomView")
public class MsgCountUnreadView extends View {

    private static final String TAG = "MyView";
    private Context ctx;
    private int radius;
    private int type;
    private String text;
    private Paint circlePaint;
    private Paint textPaint;
    private int h = getHeight() / 2 ;

    public MsgCountUnreadView(Context context) {
        this(context, null);
    }

    public MsgCountUnreadView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MsgCountUnreadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ctx = context;
        radius = DisplayUtils.dp2px(ctx, 8);
        circlePaint = new Paint();
        circlePaint.setColor(ctx.getResources().getColor(R.color.colorPrimary));
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setStrokeCap(Paint.Cap.ROUND);
        circlePaint.setStrokeWidth(50);
        circlePaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(ctx.getResources().getColor(R.color.white));
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextSize(DisplayUtils.sp2px(ctx, 10));
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Rect rect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), rect);
        int baseLine = (getHeight() - rect.height()) / 2 + rect.height();
        int halfHeight = getHeight() / 2;
        int startPos;
        switch (type) {
            case 1:
                canvas.drawCircle(getWidth() - radius, halfHeight, radius, circlePaint);
                canvas.drawText(text, getWidth() - radius, baseLine, textPaint);
                break;
            case 2:
                startPos = 40;
                int endPos = 24;
                canvas.drawLine(startPos, halfHeight, getWidth() - endPos, halfHeight, circlePaint);
                canvas.drawText(text, (getWidth() - startPos - endPos) / 2 + startPos, baseLine, textPaint);
                break;
            case 3:
                startPos  = 25;
                canvas.drawLine(startPos, halfHeight, getWidth() - startPos, halfHeight, circlePaint);
                canvas.drawText(text, getWidth() / 2, baseLine, textPaint);
                break;
        }
        super.onDraw(canvas);
    }

    public void setText(String text) {
        type = text.length() == 1 ? 1 : text.length() == 2 ? 2 : 3;
        this.text = text;
        invalidate();
    }
}

