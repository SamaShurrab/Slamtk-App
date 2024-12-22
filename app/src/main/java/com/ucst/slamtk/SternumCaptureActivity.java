package com.ucst.slamtk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Region;
import android.hardware.Camera;
import android.icu.util.Calendar;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ucst.slamtk.Class.Extractor;
import com.ucst.slamtk.Class.HistoryExaminations;
import com.ucst.slamtk.Class.Utils;
import com.ucst.slamtk.camera.ui.MyCameraSource;
import com.ucst.slamtk.cameraSternum.CameraUtils;
import com.ucst.slamtk.cameraSternum.CameraView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import weka.classifiers.lazy.IBk;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SelectedTag;

public class SternumCaptureActivity extends AppCompatActivity {

    private FrameLayout framelayout;
    private LinearLayout image_crop;
    private LinearLayout image_crop_container;
    private android.hardware.Camera mCameraSource = null;
    private CameraView cameraView;
    private ImageView shawImage;
    private ImageView tackPhoto;
    private Button returnTackPhoto;
    private Bitmap photo;
    private Button viewResult;
    private float mDist = 0;
    //    create object of FirebaseFirestore class of access Cloud Firestore
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    SharedPreferences sharedPreferences;
    int gestationalAgeInt;
    SharedPreferences.Editor editor;
    String riskFactor;
    ArrayList<Integer> examinationIdList;
    double predictedValue;
    String gestationalAge;
    int examinationId=1;
    ArrayList<Integer>babyIdList;
    ArrayList<String>riskFactorList;
    ArrayList<String>gestationalAgeList;
    String riskType;
    String recommindation1,recommindation2,recommindation3;
    Extractor ex = new Extractor();
    ProgressBar progressbar;
    double biliTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sternum_capture);

        //        create object from SharedPreferences to get id
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(SternumCaptureActivity.this);
        editor=sharedPreferences.edit();


        //        Change Icons In Status Bar
        View decor = SternumCaptureActivity.this.getWindow().getDecorView();
        if (decor.getSystemUiVisibility() != View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }//if
        else {
            decor.setSystemUiVisibility(0);
        }//else

        //        Associate components in the XML file with an object in the java
        framelayout = (FrameLayout) findViewById(R.id.camera);
        progressbar=findViewById(R.id.progressbar);
        image_crop = (LinearLayout) findViewById(R.id.image_crop);
        image_crop_container = (LinearLayout) findViewById(R.id.image_crop_container);
        shawImage = (ImageView) findViewById(R.id.shaw_image);
        tackPhoto = findViewById(R.id.tack_photo);
        returnTackPhoto = (Button) findViewById(R.id.return_tack_photo);
        viewResult = (Button) findViewById(R.id.viewResult);

        babyIdList=new ArrayList<>();
        gestationalAgeList=new ArrayList<>();
        riskFactorList=new ArrayList<>();
        examinationIdList=new ArrayList<>();
        System.out.println("bili Forehead Rate >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> "+getIntent().getDoubleExtra("biliForeheadRate",0));

        getExaminationId();
        getBabyDataFromFireStore();

        tackPhoto.setOnClickListener(v -> {
            if(photo==null){
                tackPhoto();
            }
        });

        returnTackPhoto.setOnClickListener(v -> {
            shawImage.setImageBitmap(null);
            photo = null;
        });

        viewResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(photo==null){
                    Toast.makeText(SternumCaptureActivity.this, getString(R.string.takephotoset), Toast.LENGTH_SHORT).show();
                }else{
                            determinationRiskType(getAgeHour(),gestationalAgeInt,riskFactor,biliTotal);
                            getRecommindations(riskType);
                            addDataToFireStore(new HistoryExaminations(getSampleDate(),String.valueOf(examinationId),getSampleTime(),getBabyId(),String.valueOf(biliTotal),riskType,recommindation1,recommindation2,recommindation3));
                            Intent intent=new Intent(SternumCaptureActivity.this,ResultActivity.class);
                            intent.putExtra("predictedBilirubin",biliTotal);
                            intent.putExtra("riskType",riskType);
                            intent.putExtra("recommindation1",recommindation1);
                            intent.putExtra("recommindation2",recommindation2);
                            intent.putExtra("recommindation3",recommindation3);
                            startActivity(intent);
                            finish();
                }
            }
        });

        image_crop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Camera.Parameters params = mCameraSource.getParameters();
                int action = event.getAction();
                if (event.getPointerCount() > 1) {
                    if (action == MotionEvent.ACTION_POINTER_DOWN) {
                        mDist = getFingerSpacing(event);
                    } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported()) {
                        handleZoom(event, params);
                    }
                }else{
                    if (action == MotionEvent.ACTION_UP) {
                        cameraView.autoFocusManager.start();
                    }
                }
                return true;
            }
        });

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
        double at0 = ex.colorDistribution(photo);
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

    public double calculateBiliTotal(double biliSternum,double biliForehead){
        return (biliForehead+biliSternum)/2;
    }


    private void tackPhoto(){
        try {
            mCameraSource.takePicture(null, null,new android.hardware.Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    photo = BitmapFactory.decodeByteArray(data, 0, data.length,options);
                    photo = rotateBitmap(photo,90);
                    int scW = framelayout.getWidth();
                    int scH = framelayout.getHeight();
                    float ratio = scW  * 1.0f / photo.getWidth();
                    photo = Bitmap.createScaledBitmap(photo, (int)(photo.getWidth() * ratio), (int)(photo.getHeight() *ratio), true);
                    photo = Bitmap.createBitmap(photo, image_crop.getLeft()*photo.getWidth()/scW, (image_crop_container.getTop()* photo.getHeight() / scH)-1 , image_crop.getWidth(), image_crop.getHeight());
                    if (photo != null) {
                        predictedValue=model(photo);
                        System.out.println("bili Sternum >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> "+predictedValue);
                        biliTotal=calculateBiliTotal(predictedValue,getIntent().getDoubleExtra("biliForeheadRate",0));
                        System.out.println("bili Total >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> "+biliTotal);
                        shawImage.setImageBitmap(photo);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//tackPhoto()

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
    }//rotateBitmap()

    private void handleZoom(MotionEvent event, android.hardware.Camera.Parameters params) {
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
        mCameraSource.setParameters(params);
    }//handleZoom()

    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }//getFingerSpacing()

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 0x01);
            } else {
                resumeCamera();
            }
        }else {
            resumeCamera();
        }
    }//onResume()

    private void resumeCamera() {
        if (mCameraSource != null) {
            mCameraSource.stopPreview();
            mCameraSource.release();
            if (cameraView != null) {
                cameraView.setReleased(true);
            }
            mCameraSource = null;
        }
        mCameraSource = CameraUtils.open();
        cameraView = new CameraView(this, mCameraSource);
        cameraView.setReleased(false);
        framelayout.removeAllViews();
        framelayout.addView(cameraView);
    }//resumeCamera()

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
    }//onRequestPermissionsResult()

    public void getExaminationId(){
        if(checkInternet()){
            firebaseFirestore.collection("HistoryExaminations").orderBy("examinationId", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        QuerySnapshot querySnapshot=task.getResult();
                        if(querySnapshot.getDocuments().isEmpty()){
                         examinationId=1;
                        }else{
                            if(querySnapshot !=null) {
                                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                    examinationIdList.add(Integer.valueOf(documentSnapshot.getString("examinationId")));
                                }//for()
                                if(examinationIdList.contains(examinationId)){
                                    examinationId=examinationIdList.get(examinationIdList.size()-1)+1;
                                }else {
                                    examinationId= 1;
                                }
                            }//if()
                        }
                    }//if()
                }//onComplete()
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }//onFailure()
            });
        }else{
            Toast.makeText(this, getResources().getString(R.string.noIntrnet), Toast.LENGTH_SHORT).show();
        }
    }//getExaminationId()

    public String getSampleDate(){
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // Months are zero-based, so add 1
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        return  currentDay+"/"+currentMonth+"/"+currentYear;}

    public String getSampleTime(){
        Calendar calendar = Calendar.getInstance();
        LocalTime currentTime = LocalTime.now();
        int currentHour = currentTime.getHour(); // 24-hour format
        int currentMinute = currentTime.getMinute();
        int amPm=calendar.get(Calendar.AM_PM);
        String AmPm;
        if(amPm==0){
            AmPm="am";
        }else {
            AmPm="pm";
        }
        return  currentHour+":"+currentMinute+" "+AmPm;}

    public int getBabyId(){return  sharedPreferences.getInt("babyId",0);}

    public int getAgeHour(){return  sharedPreferences.getInt("ageHour",0);}

    public boolean isRtl(View view) {return view.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;}//isRtl()



    public void getBabyDataFromFireStore(){
        if(checkInternet()){
            firebaseFirestore.collection("BabyInformation").whereEqualTo("babyId",String.valueOf(getBabyId())).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        QuerySnapshot querySnapshot=task.getResult();
                        if(querySnapshot !=null) {
                            for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                riskFactorList.add(documentSnapshot.getString("babyRiskFactor"));
                                babyIdList.add(Integer.parseInt(documentSnapshot.getString("babyId")));
                                gestationalAgeList.add(documentSnapshot.getString("babyGestationalAge"));
                            }
                            if(babyIdList.contains(getBabyId())){
                                int index=babyIdList.indexOf(getBabyId());
                                riskFactor=riskFactorList.get(index);
                                gestationalAge=gestationalAgeList.get(index);
                                if(isRtl(getWindow().getDecorView().getRootView())){
                                    gestationalAgeInt= Integer.parseInt(gestationalAge.substring(8,9));
                                }else{
                                    gestationalAgeInt= Integer.parseInt(gestationalAge.substring(0,1));
                                }

                            }
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }else{
            Toast.makeText(this, getResources().getString(R.string.noIntrnet), Toast.LENGTH_SHORT).show();
        }

    }//getBabyDataFromFireStore()

    // To check internet connection
    public boolean checkInternet(){
        ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        if(networkInfo!=null){
            if(networkInfo.isConnected() || networkInfo.isFailover()){
                return true;
            }//if
            else{
                return false;
            }//else
        }else{
            return false;
        }//else
    }//checkInternet()

    public void determinationRiskType(int ageHour, int gestational_age,String riskFactor,double predictedBilirubin){
//        low
        if(gestational_age>=38&&riskFactor.equals("no")){
            if(ageHour>=48&&ageHour<=96){
                if(ageHour==48){
                    if(predictedBilirubin==10||predictedBilirubin<10){
                        riskType=getResources().getString(R.string.low_risk);
                    }else{
                        double substraction=predictedBilirubin-10;
                        if(substraction>=5){
                            riskType=getResources().getString(R.string.High_risk);
                        }else{
                            riskType=getResources().getString(R.string.medium_risk);
                        }
                    }
                }else if(ageHour>48||ageHour<=60){
                    if(predictedBilirubin==12||predictedBilirubin<12){
                        riskType=getResources().getString(R.string.low_risk);
                    }else{
                        double substraction=predictedBilirubin-12;
                        if(substraction>=4){
                            riskType=getResources().getString(R.string.High_risk);
                        }else{
                            riskType=getResources().getString(R.string.medium_risk);
                        }
                    }
                }else if(ageHour>60||ageHour<=72){
                    if(predictedBilirubin==13||predictedBilirubin<13){
                        riskType=getResources().getString(R.string.low_risk);
                    }else{
                        double substraction=predictedBilirubin-13;
                        if(substraction>=4){
                            riskType=getResources().getString(R.string.High_risk);
                        }else{
                            riskType=getResources().getString(R.string.medium_risk);
                        }
                    }
                } else {
                    if(predictedBilirubin==14||predictedBilirubin<14){
                        riskType=getResources().getString(R.string.low_risk);
                    }else{
                        double substraction=predictedBilirubin-14;
                        if(substraction>=3){
                            riskType=getResources().getString(R.string.High_risk);
                        }else{
                            riskType=getResources().getString(R.string.medium_risk);
                        }
                    }
                }
            }else{
                if(predictedBilirubin==15||predictedBilirubin<15){
                    riskType=getResources().getString(R.string.low_risk);
                }else{
                    double substraction=predictedBilirubin-15;
                    if(substraction>=5){
                        riskType=getResources().getString(R.string.High_risk);
                    }else{
                        riskType=getResources().getString(R.string.medium_risk);
                    }
                }
            }
//            meduim
        } else if (gestational_age>=35&&gestational_age<=37&&riskFactor.equals("no")||gestational_age>37&&riskFactor.equals("yes")) {
            if(ageHour>=48&&ageHour<=96){
                if(ageHour==48){
                    if(predictedBilirubin==13){
                        riskType=getResources().getString(R.string.medium_risk);
                    } else if (predictedBilirubin<13) {
                        double substraction=predictedBilirubin-13;
                        if(substraction<=-3){
                            riskType=getResources().getString(R.string.low_risk);
                        }else{
                            riskType=getResources().getString(R.string.medium_risk);
                        }
                    } else{
                        riskType=getResources().getString(R.string.High_risk);
                    }
                }else if(ageHour>48||ageHour<=60){
                    if(predictedBilirubin==14){
                        riskType=getResources().getString(R.string.medium_risk);
                    } else if (predictedBilirubin<14) {
                        double substraction=predictedBilirubin-14;
                        if(substraction<=-2){
                            riskType=getResources().getString(R.string.low_risk);
                        }else{
                            riskType=getResources().getString(R.string.medium_risk);
                        }
                    } else{
                        riskType=getResources().getString(R.string.High_risk);
                    }
                }else if(ageHour>60||ageHour<=72){
                    if(predictedBilirubin==15){
                        riskType=getResources().getString(R.string.medium_risk);
                    } else if (predictedBilirubin<15) {
                        double substraction=predictedBilirubin-15;
                        if(substraction<=-2){
                            riskType=getResources().getString(R.string.low_risk);
                        }else{
                            riskType=getResources().getString(R.string.medium_risk);
                        }
                    } else{
                        riskType=getResources().getString(R.string.High_risk);
                    }
                } else if(ageHour>72||ageHour<=84) {
                    if(predictedBilirubin==16){
                        riskType=getResources().getString(R.string.medium_risk);
                    } else if (predictedBilirubin<16) {
                        double substraction=predictedBilirubin-16;
                        if(substraction<=-2){
                            riskType=getResources().getString(R.string.low_risk);
                        }else{
                            riskType=getResources().getString(R.string.medium_risk);
                        }
                    } else{
                        riskType=getResources().getString(R.string.High_risk);
                    }
                }else{
                    if(predictedBilirubin==17){
                        riskType=getResources().getString(R.string.medium_risk);
                    } else if (predictedBilirubin<17) {
                        double substraction=predictedBilirubin-17;
                        if(substraction<=-3){
                            riskType=getResources().getString(R.string.low_risk);
                        }else{
                            riskType=getResources().getString(R.string.medium_risk);
                        }
                    } else{
                        riskType=getResources().getString(R.string.High_risk);
                    }
                }
            }else{
                if(predictedBilirubin==18){
                    riskType=getResources().getString(R.string.medium_risk);
                } else if (predictedBilirubin<18) {
                    double substraction=predictedBilirubin-18;
                    if(substraction<=-3){
                        riskType=getResources().getString(R.string.low_risk);
                    }else{
                        riskType=getResources().getString(R.string.medium_risk);
                    }
                } else{
                    riskType=getResources().getString(R.string.High_risk);
                }
            }
        }else{
//            High
            if(ageHour>=48&&ageHour<=96){
                if(ageHour==48){
                    if(predictedBilirubin==15){
                        riskType=getResources().getString(R.string.High_risk);
                    } else if (predictedBilirubin<15) {
                        double substraction=predictedBilirubin-15;
                        if(substraction<=-5){
                            riskType=getResources().getString(R.string.low_risk);
                        }else{
                            riskType=getResources().getString(R.string.medium_risk);
                        }
                    } else{
                        riskType=getResources().getString(R.string.High_risk);
                    }
                }else if(ageHour>48||ageHour<=60){
                    if(predictedBilirubin==16){
                        riskType=getResources().getString(R.string.High_risk);
                    } else if (predictedBilirubin<16) {
                        double substraction=predictedBilirubin-16;
                        if(substraction<=-2){
                            riskType=getResources().getString(R.string.low_risk);
                        }else{
                            riskType=getResources().getString(R.string.medium_risk);
                        }
                    } else{
                        riskType=getResources().getString(R.string.High_risk);
                    }
                }else if(ageHour>60||ageHour<=72){
                    if(predictedBilirubin==17){
                        riskType=getResources().getString(R.string.High_risk);
                    } else if (predictedBilirubin<17) {
                        double substraction=predictedBilirubin-17;
                        if(substraction<=-4){
                            riskType=getResources().getString(R.string.low_risk);
                        }else{
                            riskType=getResources().getString(R.string.medium_risk);
                        }
                    } else{
                        riskType=getResources().getString(R.string.High_risk);
                    }
                } else if(ageHour>72||ageHour<=84) {
                    if(predictedBilirubin==18){
                        riskType=getResources().getString(R.string.High_risk);
                    } else if (predictedBilirubin<18) {
                        double substraction=predictedBilirubin-18;
                        if(substraction<=-4){
                            riskType=getResources().getString(R.string.low_risk);
                        }else{
                            riskType=getResources().getString(R.string.medium_risk);
                        }
                    } else{
                        riskType=getResources().getString(R.string.High_risk);
                    }
                }else{
                    if(predictedBilirubin==19){
                        riskType=getResources().getString(R.string.High_risk);
                    } else if (predictedBilirubin<19) {
                        double substraction=predictedBilirubin-19;
                        if(substraction<=-5){
                            riskType=getResources().getString(R.string.low_risk);
                        }else{
                            riskType=getResources().getString(R.string.medium_risk);
                        }
                    } else{
                        riskType=getResources().getString(R.string.High_risk);
                    }
                }
            }else{
                if(predictedBilirubin==20){
                    riskType=getResources().getString(R.string.High_risk);
                } else if (predictedBilirubin<20) {
                    double substraction=predictedBilirubin-20;
                    if(substraction<=-5){
                        riskType=getResources().getString(R.string.low_risk);
                    }else{
                        riskType=getResources().getString(R.string.medium_risk);
                    }
                } else{
                    riskType=getResources().getString(R.string.High_risk);
                }
            }
        }
    }//determinationRiskType()

    public void getRecommindations(String riskType){
        if(riskType.equals(getResources().getString(R.string.low_risk))){
            recommindation1=getResources().getString(R.string.recommendationLow1);
            recommindation2=getResources().getString(R.string.recommendationLow2);
            recommindation3=getResources().getString(R.string.recommendationLow3);

        } else if (riskType.equals(getResources().getString(R.string.medium_risk))) {
            recommindation1=getResources().getString(R.string.recommendationMeduim1);
            recommindation2=getResources().getString(R.string.recommendationMeduim2);
            recommindation3=getResources().getString(R.string.recommendationMeduim3);
        }else{
            recommindation1=getResources().getString(R.string.recommendationHigh1);
            recommindation2=getResources().getString(R.string.recommendationHigh2);
            recommindation3=getResources().getString(R.string.recommendationHigh3);
        }
    }//getRecommindations()

    public void addDataToFireStore(HistoryExaminations historyExaminations) {
        if(checkInternet()){
            Map<String, Object> examinationsMap = new HashMap<>();
            examinationsMap.put("examinationId", historyExaminations.getExaminationId());
            examinationsMap.put("babyId", historyExaminations.getBabyId());
            examinationsMap.put("sampleTime", historyExaminations.getSampleTime());
            examinationsMap.put("sampleDate", historyExaminations.getSampleDate());
            examinationsMap.put("bilirubinRate", historyExaminations.getBilirubinRate());
            examinationsMap.put("riskType", historyExaminations.getRiskType());
            examinationsMap.put("recommendation1", historyExaminations.getRecommendation1());
            examinationsMap.put("recommendation2", historyExaminations.getRecommendation2());
            examinationsMap.put("recommendation3", historyExaminations.getRecommendation3());
            firebaseFirestore.collection("HistoryExaminations").add(examinationsMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(SternumCaptureActivity.this, getResources().getString(R.string.successstoreHE), Toast.LENGTH_SHORT).show();
                }//onSuccess()
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SternumCaptureActivity.this, getResources().getString(R.string.failstoreHE), Toast.LENGTH_SHORT).show();
                }//onFailure()
            });
        }else{
            Toast.makeText(this, getResources().getString(R.string.noIntrnet), Toast.LENGTH_SHORT).show();
        }
    }
    Instances loadDataset(){
        Instances data = null;
        try {

            InputStream is = getResources().openRawResource(R.raw.sternum_before_without_cm0);
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