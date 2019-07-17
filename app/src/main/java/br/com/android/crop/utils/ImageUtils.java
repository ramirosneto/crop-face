package br.com.android.crop.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;

public class ImageUtils {

    public static Rect captureRegionForScreen(Integer width, Integer height) {
        return new Rect((width / 2) - (int) ((width / 2) * 0.75),
                (height / 2) - (int) ((height / 2) * 0.50),
                (width / 2) + (int) ((width / 2) * 0.75),
                (height / 2) + (int) ((height / 2) * 0.50));
    }

    public static Bitmap cropFace(Bitmap bitmap) {
        Rect rect = captureRegionForScreen(bitmap.getWidth(), bitmap.getHeight());
        Matrix matrix = new Matrix();
        matrix.postRotate(270);
        return Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height(), matrix, true);
    }
}