package com.ucst.slamtk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ucst.slamtk.Adapter.Adapter_HistoryExaminations;
import com.ucst.slamtk.Class.HistoryExaminations;

import java.util.ArrayList;

public class HistoryExaminationsDataActivity extends AppCompatActivity {
    ListView babyNameLV;

    ArrayList<String> sampleDateList;
    ArrayList<String> sampleTimeList;
    ArrayList<String> riskTypeList;
    ArrayList<String> recommendations1;
    ArrayList<String> recommendations2;
    ImageView imageView_arrow;
    ArrayList<String> recommendations3;
    ArrayList<String> bilirubinRateList;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextView babyName;
    LinearLayout historyExaminationData,alert;
    ImageView move;

    //    create object of FirebaseFirestore class of access Cloud Firestore
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_examinations_data);


        //        create object from SharedPreferences to get id
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(HistoryExaminationsDataActivity.this);
        editor=sharedPreferences.edit();

        //        Change Icons In Status Bar
        View decor= HistoryExaminationsDataActivity.this.getWindow().getDecorView();
        if(decor.getSystemUiVisibility() != View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR){
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }//if
        else{
            decor.setSystemUiVisibility(0);
        }//else

//    Associate components in the XML file with an object in the java
        babyNameLV=findViewById(R.id.babyCheck_listVieww);
        babyName=findViewById(R.id.babyName);
        historyExaminationData=findViewById(R.id.historyExaminationData);
        alert=findViewById(R.id.historyExaminationData_alert);
        imageView_arrow=findViewById(R.id.imageView_arrow);
        move=findViewById(R.id.move);

        babyName.setText(getIntent().getStringExtra("babyNAme"));

        sampleDateList=new ArrayList<>();
        sampleTimeList=new ArrayList<>();
        riskTypeList=new ArrayList<>();
        bilirubinRateList=new ArrayList<>();
        recommendations1=new ArrayList<>();
        recommendations2=new ArrayList<>();
        recommendations3=new ArrayList<>();
        getData(sampleDateList,sampleTimeList,riskTypeList,recommendations1,recommendations2,recommendations3,bilirubinRateList);
        onStartMethod();
        imageView_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HistoryExaminationsDataActivity.this, HistoryExaminationsActivity.class);
                startActivity(intent);
            }
        });
    }

//    public int getBabyId(){
//
//        return  sharedPreferences.getInt("babyId",0);}


    public void getData(ArrayList<String> sampleDateList,ArrayList<String> sampleTimeList,ArrayList<String> riskTypeList,ArrayList<String> recommendations1,ArrayList<String> recommendations2,ArrayList<String> recommendations3,ArrayList<String> bilirubinRateList){
        if(checkInternet()) {
            firebaseFirestore.collection("HistoryExaminations").whereEqualTo("babyId",getIntent().getIntExtra("babyId",0)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if(querySnapshot!=null){
                            for(DocumentSnapshot documentSnapshot:querySnapshot.getDocuments()){
                                sampleDateList.add(documentSnapshot.getString("sampleDate"));
                                sampleTimeList.add(documentSnapshot.getString("sampleTime"));
                                bilirubinRateList.add(documentSnapshot.getString("bilirubinRate"));
                                riskTypeList.add(documentSnapshot.getString("riskType"));
                                recommendations1.add(documentSnapshot.getString("recommendation1"));
                                recommendations2.add(documentSnapshot.getString("recommendation2"));
                                recommendations3.add(documentSnapshot.getString("recommendation3"));
                            }
                        }
                        Adapter_HistoryExaminations adapter_historyExaminations=new Adapter_HistoryExaminations(HistoryExaminationsDataActivity.this,sampleDateList,sampleTimeList,riskTypeList,recommendations1,recommendations2,recommendations3,bilirubinRateList);
                        babyNameLV.setAdapter(adapter_historyExaminations);
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
    }//getData()

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

    public void onStartMethod(){
        new Handler().postAtTime(new Runnable() {
            @Override
            public void run() {
                alert.setVisibility(View.VISIBLE);
                historyExaminationData.setVisibility(View.GONE);
                Animation animation = AnimationUtils.loadAnimation(HistoryExaminationsDataActivity.this, R.anim.slide_in);
                alert.startAnimation(animation);
            }
        },500);
        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        alert.setVisibility(View.GONE);
                        historyExaminationData.setVisibility(View.VISIBLE);
                        Animation animation = AnimationUtils.loadAnimation(HistoryExaminationsDataActivity.this, R.anim.zoom_in_dialog);

            }
        });

    }//onStartMethod()

}