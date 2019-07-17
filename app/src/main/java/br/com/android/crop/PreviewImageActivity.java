package br.com.android.crop;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import br.com.android.crop.databinding.ActivityPreviewImageBinding;

public class PreviewImageActivity extends AppCompatActivity {
    private static final String IMAGE_EXTRA = "IMAGE_EXTRA";

    private ActivityPreviewImageBinding mBinding;

    public static Intent intentToShow(Context context, byte[] byteArray) {
        Intent intent = new Intent(context, PreviewImageActivity.class);
        intent.putExtra(IMAGE_EXTRA, byteArray);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_preview_image);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            byte[] byteArray = extras.getByteArray(IMAGE_EXTRA);
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            mBinding.preview.setImageBitmap(bitmap);
        } else {
            throw new IllegalArgumentException("bitmap empty");
        }
    }
}