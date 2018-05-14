package example.com.hb.diary.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.EditText;

import example.com.hb.diary.R;

/**
 * Created by HP ProBook on 4/20/2018.
 */

public class LineEditText extends EditText {
    private Paint mPaint = new Paint();
    private boolean isUnderline = true;

    public LineEditText(Context context) {
        super(context);
        initPaint();
    }

    public LineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LineEditText);
        final int N = typedArray.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.LineEditText_isUnderline:
                    isUnderline = typedArray.getBoolean(attr, true);
                    break;
                default:
                    break;
            }
        }
        typedArray.recycle();
        initPaint();
    }

    public LineEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initPaint();
    }

    private void initPaint() {
        mPaint.setStyle(Paint.Style.STROKE);
        if (isUnderline) {
            int lineColor = Color.BLACK;
            mPaint.setColor(lineColor);//(0x80000000);
        } else {
            mPaint.setColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        int left = getLeft();
        int right = getRight();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        int height = getHeight();
        int lineHeight = getLineHeight();
        int count = (height-paddingTop-paddingBottom) / lineHeight;

        for (int i = 0; i < count; i++) {
            int baseline = lineHeight * (i+1) + paddingTop;
            canvas.drawLine(left, baseline, right, baseline, mPaint);
        }

        super.onDraw(canvas);
    }

    public boolean isUnderline() {
        return isUnderline;
    }

    public void setUnderline(boolean isUnderline) {
        this.isUnderline = isUnderline;
        initPaint();
        invalidate();
    }
}