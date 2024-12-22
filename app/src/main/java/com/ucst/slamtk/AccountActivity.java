package com.ucst.slamtk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity {

    ImageView personalInfo,privacy,help,previousExamination,newExamination,contact_with_doctor;
    TextView textView_user_name;
    Button logOut;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ArrayList<String>nameList;
    ArrayList<Integer>idList;
    //    create object of FirebaseFirestore class of access Cloud Firestore
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //        create object from SharedPreferences to get id
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(AccountActivity.this);
        editor=sharedPreferences.edit();

        //        Change Icons In Status Bar
        View decor= AccountActivity.this.getWindow().getDecorView();
        if(decor.getSystemUiVisibility() != View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR){
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }//if
        else{
            decor.setSystemUiVisibility(0);
        }//else

//    Associate components in the XML file with an object in the java
        personalInfo=findViewById(R.id.imageView_arrow_personal_info);
        privacy=findViewById(R.id.imageView_Privacy_arrow);
        previousExamination=findViewById(R.id.imageView_previous_examinations);
        help=findViewById(R.id.imageView_help);
        logOut=findViewById(R.id.button_logout_account);
        newExamination=findViewById(R.id.imageView_arrow_newExamination);
        textView_user_name=findViewById(R.id.textView_user_name);
        contact_with_doctor=findViewById(R.id.imageView_contact_with_doctor);

        idList=new ArrayList<>();
        nameList=new ArrayList<>();

        getName(getId());

        personalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AccountActivity.this,PersonalInformationActivity.class);
                startActivity(intent);
            }//onClick()
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(AccountActivity.this,PrivacyActivity.class);
                startActivity(intent);
            }//onClick()
        });

        previousExamination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(AccountActivity.this, HistoryExaminationsActivity.class);
                startActivity(intent);
            }//onClick()
        });

        contact_with_doctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(AccountActivity.this, ContactWithDoctor.class);
                startActivity(intent);
            }//onClick()
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(AccountActivity.this,HelpActivity.class);
                startActivity(intent);
            }//onClick()
        });

        newExamination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(AccountActivity.this, Newborn_InformationActivity.class);
                startActivity(intent);
            }//onClick()
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog=new Dialog(AccountActivity.this);
                dialog.setContentView(R.layout.logout_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.getWindow().setWindowAnimations(R.style.AnimationDialog);
                dialog.getWindow().setGravity(Gravity.CENTER);
                Button exit=dialog.findViewById(R.id.button_exit);
                Button cancel=dialog.findViewById(R.id.button_cancel);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }//onClick()
                });

                exit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        editor.clear();
//                        editor.apply();
//                        Intent intent=new Intent(AccountActivity.this,SplashActivity.class);
//                        startActivity(intent);
                        AccountActivity.this.finish();
                        startActivity(new Intent(AccountActivity.this,LoginActivity.class));
//                        dialog.dismiss();
//                        finish();
//                        System.exit(0);
//                        dialog.dismiss();

                    }//onClick()
                });
                dialog.show();

            }//onClick()
        });

    }//onCreate()

    public void getName(int id){
        if(checkInternet()){
            firebaseFirestore.collection("UserData").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        QuerySnapshot querySnapshot=task.getResult();
                        if(querySnapshot !=null) {
                            for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                nameList.add(documentSnapshot.getString("userName").toString());
                                idList.add(Integer.valueOf(documentSnapshot.getString("userId").toString()));
                            }//for()
                            //mobile is exist in firebase database
                            if(idList.contains(id)){
                                textView_user_name.setText(nameList.get(idList.indexOf(id)));
                            }//if()
                        }//if()
                    }//if()
                }//onComplete()
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }//onFailure()
            });
        }else {
            Toast.makeText(this, getResources().getString(R.string.noIntrnet), Toast.LENGTH_SHORT).show();
        }//else
    }//getName()

    public int getId(){
        return  sharedPreferences.getInt("id",0);
    }//getId()

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




}//AccountActivity