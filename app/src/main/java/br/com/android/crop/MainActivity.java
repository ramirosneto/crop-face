package br.com.android.crop;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import br.com.android.crop.databinding.ActivityMainBinding;
import br.com.android.crop.utils.ImageUtils;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PictureCallback {
    private final int CAMERA_PERMISSION_REQUEST_CODE = 9878;

    private ActivityMainBinding mBinding;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setListeners();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initCamera();
            } else {
                Toast.makeText(this, getString(R.string.permission_message), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            initCamera();
        }
    }

    private void setListeners() {
        mBinding.btnTakeSelfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });
    }

    private void initCamera() {
        mBinding.btnTakeSelfie.setVisibility(View.GONE);
        mBinding.contentCamera.setVisibility(View.VISIBLE);

        setupSurfaceHolder();
    }

    private void setupSurfaceHolder() {
        mSurfaceHolder = mBinding.surfaceView.getHolder();

        if (mSurfaceHolder != null) {
            mSurfaceHolder.addCallback(this);
        }
    }

    private void startCamera() {
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        mCamera.setDisplayOrientation(90);
        disableSnapButton();

        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.setFaceDetectionListener(setFaceDetectorListener());
            mCamera.startFaceDetection();
            mCamera.startPreview();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private Camera.FaceDetectionListener setFaceDetectorListener() {
        return new Camera.FaceDetectionListener() {
            @Override
            public void onFaceDetection(Camera.Face[] faces, Camera camera) {
                if (faces.length > 0) {
                    int left = faces[0].rect.left;
                    int right = faces[0].rect.right;
                    int top = faces[0].rect.top;
                    int bottom = faces[0].rect.bottom;

                    Rect faceRect = new Rect(left, top, right, bottom);
                    Rect overlayRect = ImageUtils.captureRegionForScreen(mBinding.faceOverlay.getWidth(), mBinding.faceOverlay.getHeight());

                    if (overlayRect.intersect(faceRect)) {
                        enableSnapButton();
                    }
                } else {
                    disableSnapButton();
                }
            }
        };
    }

    Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
        }
    };

    Camera.PictureCallback mPictureCallbackRAW = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] arg0, Camera arg1) {
        }
    };

    Camera.PictureCallback mPictureCallbackJPG = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] arg0, Camera arg1) {
            Bitmap bitmapPicture = BitmapFactory.decodeByteArray(arg0, 0, arg0.length);
            Bitmap correctBmp = Bitmap.createBitmap(bitmapPicture, 0, 0,
                    bitmapPicture.getWidth(), bitmapPicture.getHeight(), null, true);
            Bitmap cropedImage = ImageUtils.cropFace(correctBmp);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            cropedImage.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            byte[] byteArray = stream.toByteArray();
            startActivity(PreviewImageActivity.intentToShow(getApplicationContext(), byteArray));
        }
    };

    private void enableSnapButton() {
        mBinding.snapButton.setEnabled(true);
        mBinding.snapButton.setAlpha(1.0f);
        mBinding.snapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(mShutterCallback, mPictureCallbackRAW, mPictureCallbackJPG);
            }
        });
    }

    private void disableSnapButton() {
        mBinding.snapButton.setEnabled(false);
        mBinding.snapButton.setAlpha(0.3f);
        mBinding.snapButton.setOnClickListener(null);
    }

    // SurfaceHolder implementation region

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopFaceDetection();
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    // endregion

    // Camera implementation region

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
    }

    // endregion
}