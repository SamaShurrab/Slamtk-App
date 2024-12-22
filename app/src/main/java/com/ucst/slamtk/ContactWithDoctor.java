package com.ucst.slamtk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ucst.slamtk.Adapter.Adapter_BabyNameList;
import com.ucst.slamtk.Adapter.Adapter_DoctorList;

import java.util.ArrayList;

public class ContactWithDoctor extends AppCompatActivity {
    ListView doctors_listview;
    LinearLayout contact_with_doctors,contact_with_doctors_alert;
    ArrayList<String> doctorNameList;
    ArrayList<String> doctorJobTitleList;
    ArrayList<String> doctorSpecializationList;
    ArrayList<String> doctorPhoneList;
    ImageView imageView_arrow,move;
    //    create object of FirebaseFirestore class of access Cloud Firestore
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_with_doctor);

        //        Change Icons In Status Bar
        View decor= ContactWithDoctor.this.getWindow().getDecorView();
        if(decor.getSystemUiVisibility() != View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR){
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }//if
        else{
            decor.setSystemUiVisibility(0);
        }//else

        doctors_listview=findViewById(R.id.doctors_listview);
        contact_with_doctors=findViewById(R.id.contact_with_doctors);
        contact_with_doctors_alert=findViewById(R.id.contact_with_doctors_alert);
        imageView_arrow=findViewById(R.id.imageView_arrow);
        move=findViewById(R.id.move);

        imageView_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ContactWithDoctor.this,AccountActivity.class);
                startActivity(intent);
            }
        });

        doctorNameList=new ArrayList<>();
        doctorSpecializationList=new ArrayList<>();
        doctorJobTitleList=new ArrayList<>();
        doctorPhoneList=new ArrayList<>();
        getDoctorData(doctorNameList,doctorJobTitleList,doctorSpecializationList,doctorPhoneList);
        onStartMethod();
    }

    // To check internet connection
    public boolean checkInternet(){
        ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfos=connectivityManager.getActiveNetworkInfo();
        if(networkInfos!=null){
            if(networkInfos.isConnected() || networkInfos.isFailover()){
                return true;
            }//if
            else{
                return false;
            }//else
        }else{
            return false;
        }//else
    }//checkInternet()

    // get layout direction
    public boolean isRtl(View view) {
        return view.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }//isRtl

    public void onStartMethod(){
        new Handler().postAtTime(new Runnable() {
            @Override
            public void run() {
                contact_with_doctors_alert.setVisibility(View.VISIBLE);
                contact_with_doctors.setVisibility(View.GONE);
                Animation animation = AnimationUtils.loadAnimation(ContactWithDoctor.this, R.anim.zoom_in_dialog);
                contact_with_doctors_alert.startAnimation(animation);
            }
        },500);
        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        contact_with_doctors_alert.setVisibility(View.GONE);
                        contact_with_doctors.setVisibility(View.VISIBLE);
                        Animation animation = AnimationUtils.loadAnimation(ContactWithDoctor.this, R.anim.slide_in);
                        contact_with_doctors.startAnimation(animation);

            }
        });

    }//onStartMethod()

    public void getDoctorData(ArrayList<String> doctorNameList,ArrayList<String> doctorJobTitleList,ArrayList<String> doctorSpecializationList,ArrayList<String> doctorPhoneList){
        if(checkInternet()){
            firebaseFirestore.collection("DoctorData").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        QuerySnapshot querySnapshot=task.getResult();
                        if(querySnapshot!=null){
                            for(DocumentSnapshot documentSnapshot:querySnapshot.getDocuments()){
                                if(isRtl(getWindow().getDecorView().getRootView())){
                                    doctorNameList.add(documentSnapshot.getString("nameAr"));
                                    doctorSpecializationList.add(documentSnapshot.getString("SpecializationAr"));
                                    doctorJobTitleList.add(documentSnapshot.getString("JobTitleAr"));
                                }else{
                                    doctorNameList.add(documentSnapshot.getString("nameEn"));
                                    doctorSpecializationList.add(documentSnapshot.getString("SpecializationEn"));
                                    doctorJobTitleList.add(documentSnapshot.getString("JobTitleEn"));
                                }
                                doctorPhoneList.add(documentSnapshot.getString("doctorPhone"));
                            }
                            Adapter_DoctorList adapter_doctorList=new Adapter_DoctorList(ContactWithDoctor.this,doctorNameList,doctorSpecializationList,doctorJobTitleList,doctorPhoneList);
                            doctors_listview.setAdapter(adapter_doctorList);
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
    }

}