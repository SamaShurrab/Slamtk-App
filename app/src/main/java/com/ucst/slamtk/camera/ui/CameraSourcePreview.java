package com.ucst.slamtk.camera.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.images.Size;
import com.google.android.gms.vision.CameraSource;
import com.ucst.slamtk.camera.ui.MyCameraSource;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

public class CameraSourcePreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "CameraSourcePreview";

    private Context mContext;
    private boolean mStartRequested;
    private boolean mSurfaceAvailable;
    private MyCameraSource mCameraSource;
    private int defaultBrightness = 12;
    private GraphicOverlay mOverlay;


    public CameraSourcePreview(Context context) {
        super(context);
        mContext = context;
        mStartRequested = false;
        mSurfaceAvailable = false;
        getHolder().addCallback(this);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        super.setOnClickListener(l);
    }



    public void start(MyCameraSource cameraSource) throws IOException {
        if (cameraSource == null) {
            stop();
        }

        mCameraSource = cameraSource;

        if (mCameraSource != null) {
            mStartRequested = true;

            startIfReady();
        }
    }

    public void start(MyCameraSource cameraSource, GraphicOverlay overlay) throws IOException {
        mOverlay = overlay;
        start(cameraSource);
    }

    public void stop() {
        if (mCameraSource != null) {
            mCameraSource.stop();
        }
    }

    public void release() {
        if (mCameraSource != null) {
            mCameraSource.release();
            mCameraSource = null;
        }
    }

    @SuppressLint("MissingPermission")
    private void startIfReady() throws IOException {
        if (mStartRequested && mSurfaceAvailable) {
            mCameraSource.start(getHolder());
            try {
                initCamera();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Exception","initCamera");
            }
            if (mOverlay != null) {
                Size size = mCameraSource.getPreviewSize();
                int min = Math.min(size.getWidth(), size.getHeight());
                int max = Math.max(size.getWidth(), size.getHeight());
                if (isPortraitMode()) {
                    mOverlay.setCameraInfo(min, max, mCameraSource.getCameraFacing());
                } else {
                    mOverlay.setCameraInfo(max, min, mCameraSource.getCameraFacing());
                }
                mOverlay.clear();
            }
            mStartRequested = false;
        }
    }


    private void initCamera() throws Exception {
        Camera.Parameters parameters = mCameraSource.mCamera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        for (Camera.Size size : previewSizes) {
            if (size.width / 16 == size.height / 9) {
                parameters.setPreviewSize(size.width, size.height);
                break;
            }
        }
        List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
        for (Camera.Size size : pictureSizes) {
            if (size.width / 16 == size.height / 9) {
                parameters.setPictureSize(size.width, size.height);
                break;
            }
        }
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraSource.getCameraFacing(), info);
        int rotation = info.orientation % 360;
        parameters.setRotation(rotation);
        mCameraSource.mCamera.setDisplayOrientation(90);
        parameters.setJpegQuality(100);
        mCameraSource.mCamera.setParameters(parameters);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        mSurfaceAvailable = true;
        try {
            startIfReady();
        } catch (IOException e) {
            Log.e(TAG, "Could not start camera source.", e);
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        mSurfaceAvailable = false;
    }

    private boolean isPortraitMode() {
        int orientation = mContext.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return false;
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            return true;
        }

        Log.d(TAG, "isPortraitMode returning false by default");
        return false;
    }
}
