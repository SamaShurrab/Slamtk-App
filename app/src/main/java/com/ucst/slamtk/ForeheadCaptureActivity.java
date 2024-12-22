package com.ucst.slamtk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Region;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.ucst.slamtk.Class.Extractor;
import com.ucst.slamtk.Class.Utils;
import com.ucst.slamtk.camera.ui.CameraSourcePreview;
import com.ucst.slamtk.camera.ui.FaceGraphic;
import com.ucst.slamtk.camera.ui.GraphicOverlay;
import com.ucst.slamtk.camera.ui.MyCameraSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import weka.classifiers.lazy.IBk;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;

public class ForeheadCaptureActivity extends AppCompatActivity {

    private static final int RC_HANDLE_GMS = 9001;

    private MyCameraSource mCameraSource = null;
    protected GraphicOverlay mGraphicOverlay;
    private CameraSourcePreview mPreview;
    private FrameLayout framelayout;
    private TextView distance;
    private TextView allow;
    private ImageView tackPhoto;
    private Button returnTackPhoto;
    private Button next;
    private ImageView shawImage;
    private LinearLayout image_crop;
    private LinearLayout image_crop_container;
    boolean allowTackPhoto = false;
    private float mDist = 0;
    double predictedValue;
    private Bitmap photo;
    Extractor ex = new Extractor();
    ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forehead_capture);

//        Change Icons In Status Bar
        View decor= ForeheadCaptureActivity.this.getWindow().getDecorView();
        if(decor.getSystemUiVisibility() != View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR){
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }//if
        else{
            decor.setSystemUiVisibility(0);
        }//else

//    Associate components in the XML file with an object in the java
        framelayout = (FrameLayout) findViewById(R.id.camera);
        progressbar=findViewById(R.id.progressbar);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);
        image_crop = (LinearLayout) findViewById(R.id.image_crop);
        image_crop_container = (LinearLayout) findViewById(R.id.image_crop_container);
        distance = (TextView) findViewById(R.id.distance);
        allow = (TextView) findViewById(R.id.allow);
        tackPhoto =  findViewById(R.id.tack_photo);
        returnTackPhoto = (Button) findViewById(R.id.return_tack_photo);
        next = (Button) findViewById(R.id.next);
        shawImage = (ImageView) findViewById(R.id.shaw_image);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(photo!=null){
                    Intent intent=new Intent(ForeheadCaptureActivity.this,SternumCaptureActivity.class);
                    intent.putExtra("biliForeheadRate",predictedValue);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(ForeheadCaptureActivity.this, getResources().getString(R.string.takephoto), Toast.LENGTH_SHORT).show();
                }
            }
        });


        tackPhoto.setOnClickListener(v -> {
            if(allowTackPhoto && photo == null){
                takePicture();
            }
        });

        returnTackPhoto.setOnClickListener(v -> {
            shawImage.setImageBitmap(null);
            photo = null;
        });

        image_crop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Camera.Parameters params = mCameraSource.mCamera.getParameters();
                int action = event.getAction();
                if (event.getPointerCount() > 1) {
                    if (action == MotionEvent.ACTION_POINTER_DOWN) {
                        mDist = getFingerSpacing(event);
                    } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported()) {
                        handleZoom(event, params);
                    }
                }else{
                    if (action == MotionEvent.ACTION_UP) {
                        mCameraSource.autoFocusManager.start();
                    }
                }
                return true;
            }
        });
    }

    private void handleZoom(MotionEvent event, Camera.Parameters params) {
        int maxZoom = params.getMaxZoom();
        int zoom = params.getZoom();
        float newDist = getFingerSpacing(event);
        if (newDist > mDist) {
            //zoom in
            if (zoom < maxZoom)
                zoom++;
        } else if (newDist < mDist) {
            //zoom out
            if (zoom > 0)
                zoom--;
        }
        mDist = newDist;
        params.setZoom(zoom);
        mCameraSource.mCamera.setParameters(params);
    }

    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }





    private void takePicture(){
        try {
            mCameraSource.takePicture(null, new MyCameraSource.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] bytes) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    photo = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,options);
                    photo = rotateBitmap(photo,90);
                    int scW = framelayout.getWidth();
                    int scH = framelayout.getHeight();
                    float ratio = scW  * 1.0f / photo.getWidth();
                    photo = Bitmap.createScaledBitmap(photo, (int)(photo.getWidth() * ratio), (int)(photo.getHeight() *ratio), true);
                    photo = Bitmap.createBitmap(photo, image_crop.getLeft()*photo.getWidth()/scW, image_crop_container.getTop()/2* photo.getHeight() / scH , image_crop.getWidth(), image_crop.getHeight());
                    if (photo != null) {
                        predictedValue=model(photo);
                        System.out.println("bili Forehead >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> "+predictedValue);
                        shawImage.setImageBitmap(photo);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static Bitmap rotateBitmap(Bitmap origin, float alpha) {
        if (origin == null) {
            return null;
        }
        int width = origin.getWidth();
        int height = origin.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(alpha);
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);
        if (newBM.equals(origin)) {
            return newBM;
        }
        origin.recycle();
        return newBM;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 0x01);
            } else {
                resumeCamera();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                resumeCamera();
            } else {
                Toast.makeText(this, getResources().getString(R.string.notallow), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getResources().getString(R.string.notallow), Toast.LENGTH_SHORT).show();
        }
    }


    private void resumeCamera() {
        Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setProminentFaceOnly(true)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {
            Toast.makeText(getApplicationContext(),"Dependencies are not yet available. ",Toast.LENGTH_LONG).show();
            Log.e("TAG", "Face detector dependencies are not yet available.");
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        mCameraSource = new MyCameraSource.Builder(context, detector)
                .setRequestedPreviewSize(height, width)
                .setFacing(MyCameraSource.CAMERA_FACING_BACK)
                .setRequestedFps(45.0f)
                .build();
        mPreview = new CameraSourcePreview(this);
        framelayout.removeAllViews();
        framelayout.addView(mPreview);
        startCameraSource();
    }





    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e("TAG", "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }



    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    private class GraphicFaceTracker extends Tracker<Face> {
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;

        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay);
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }

        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);

            float faceHeightPx = face.getHeight();

// Get the known height of the face in centimeters
            float faceHeightCm = 15; // Assuming an average face height of 15cm

// Get the focal length of the camera in millimeters
            float focalLengthMm = 1.5f; // Assuming a focal length of 4.8mm

// Convert the focal length to centimeters
            float focalLengthCm = focalLengthMm * 1000;

// Calculate the distance using the formula
            int distanceCm = (int) ((faceHeightCm * focalLengthCm) / faceHeightPx);

            setText(allow,getResources().getString(R.string.capture), getDrawable(R.drawable.face));
            allowTackPhoto = false;
            if(distanceCm == 20){
                setText(distance,getResources().getString(R.string.distance), getDrawable(R.drawable.distance));
                allowTackPhoto = true;
            }else if(distanceCm > 20){
                setText(distance,getResources().getString(R.string.come), getDrawable(R.drawable.distance));
            }else{
                setText(distance,getResources().getString(R.string.back), getDrawable(R.drawable.distance));
            }
        }

        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
            allowTackPhoto = false;
            setText(allow,getResources().getString(R.string.noface), getDrawable(R.drawable.no_face));
            setText(distance,"", getDrawable(R.drawable.distance));
        }

        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
            allowTackPhoto = false;
            setText(allow,getResources().getString(R.string.noface), getDrawable(R.drawable.no_face));
            setText(distance,"", getDrawable(R.drawable.distance));
        }

        private void setText(final TextView text, final String value, Drawable drawable){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    text.setText(value);
                    text.setBackgroundDrawable(drawable);
                }
            });
        }
    }
    @Override
    public void onBackPressed() {

    }

    public double model(Bitmap photo){
        double predictedValue;
//        progressbar.setVisibility(View.VISIBLE);
//        next.setVisibility(View.GONE);
//        returnTackPhoto.setVisibility(View.GONE);
//        distance.setText(getResources().getString(R.string.wait_minute));
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
                Utils u = new Utils();
                Region r = u.computeRegion(photo, 0, 0, photo.getWidth(), photo.getHeight());
                double at0 = ex.colorMoments(photo)[0];
                double at1 = ex.colorMoments(photo)[1];
                double at2 = ex.colorMoments(photo)[4];
                Instances data = loadDataset();
                IBk ibkModel = new IBk();
                ibkModel.setKNN(1); // Set the number of neighbors (k)
                ibkModel.setDistanceWeighting(new SelectedTag(IBk.WEIGHT_INVERSE, IBk.TAGS_WEIGHTING)); // Inverse distance weighting
                try {
                    ibkModel.buildClassifier(data);//train IBK model by dataset (data)
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Instance newInstance = new Instance(data.numAttributes());
                newInstance.setDataset(data);
                newInstance.setValue(0, at0); // Set attribute 1 value for the new instance
                newInstance.setValue(1, at1); // Set attribute 2 value for the new instance
                newInstance.setValue(2, at2); // Set attribute 5 value for the new instance
                try {
                    predictedValue = ibkModel.classifyInstance(newInstance);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
//            }
//        }).start();
        return predictedValue;
    }

    Instances loadDataset(){
        Instances data = null;
        try {

            InputStream is = getResources().openRawResource(R.raw.forehead_before);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            data = new Instances(reader);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (data != null) {
            data.setClassIndex(data.numAttributes() - 1);
        }
        return data;
    }
}