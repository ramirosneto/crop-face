package br.com.android.crop.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class FaceOverlay extends View {

    private Paint mTransparentPaint = new Paint();
    private Paint mBorderColor = new Paint();
    private Path mPath = new Path();

    RectF rect = null;

    public FaceOverlay(Context context) {
        super(context);
    }

    public FaceOverlay(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaints();
    }

    public FaceOverlay(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaints();
    }

    private void initPaints() {
        mTransparentPaint.setColor(Color.TRANSPARENT);
        mTransparentPaint.setStrokeWidth(10f);

        mBorderColor.setColor(ContextCompat.getColor(getContext(), android.R.color.white));
        mBorderColor.setStrokeWidth(10f);
        mBorderColor.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (rect == null) {
            rect = new RectF(ImageUtils.captureRegionForScreen(getWidth(), getHeight()));
        }

        mPath.reset();

        mPath.addOval(rect, Path.Direction.CW);
        mPath.setFillType(Path.FillType.INVERSE_EVEN_ODD);

        canvas.drawOval(rect, mTransparentPaint);
        canvas.drawOval(rect, mBorderColor);

        canvas.drawPath(mPath, mTransparentPaint);
        canvas.clipPath(mPath);
    }
}