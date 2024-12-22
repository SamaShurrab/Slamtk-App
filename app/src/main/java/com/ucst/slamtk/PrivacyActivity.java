package com.ucst.slamtk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class PrivacyActivity extends AppCompatActivity {
    EditText currentPassword,newPassword;
    Button edit,cancel;
    boolean newPasswordVisible=false;
    boolean currentPasswordVisible=false;
    ImageView imageView_arrow;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    //    create object of FirebaseFirestore class of access Cloud Firestore
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        //        create object from SharedPreferences to get id
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(PrivacyActivity.this);
        editor=sharedPreferences.edit();


        //        Change Icons In Status Bar
        View decor= PrivacyActivity.this.getWindow().getDecorView();
        if(decor.getSystemUiVisibility() != View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR){
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }//if
        else{
            decor.setSystemUiVisibility(0);
        }//else

        //        Associate components in the XML file with an object in the java
        currentPassword=findViewById(R.id.editText_cuurent_password);
        newPassword=findViewById(R.id.editText_new_password);
        edit=findViewById(R.id.edit_btn);
        cancel=findViewById(R.id.button_cancel);
        imageView_arrow=findViewById(R.id.imageView_arrow);

        imageView_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PrivacyActivity.this,AccountActivity.class);
                startActivity(intent);
            }
        });

        //      Hide password and show password
        currentPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int right=2;
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    if(motionEvent.getRawX()>=currentPassword.getRight()-currentPassword.getCompoundDrawables()[right].getBounds().width()){
                        int selection=currentPassword.getSelectionEnd();
                        if(currentPasswordVisible){
                            if(isRtl(getWindow().getDecorView().getRootView())){
                                //set drawable image here
                                currentPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.eye_slash_password,0,R.drawable.password,0);
                            }else{
                                //set drawable image here
                                currentPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.password,0,R.drawable.eye_slash_password,0);
                            }
//                            for hide password
                            currentPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            currentPasswordVisible=false;
                        }else{
                            if(isRtl(getWindow().getDecorView().getRootView())){
                                //set drawable image here
                                currentPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.eye_password,0,R.drawable.password,0);
                            }else{
                                //set drawable image here
                                currentPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.password,0,R.drawable.eye_password,0);
                            }
//                            for show password
                            currentPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            currentPasswordVisible=true;
                        }
                        currentPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        //      Hide password and show password
        newPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int right=2;
                if(motionEvent.getAction()== MotionEvent.ACTION_UP){
                    if(motionEvent.getRawX()>=newPassword.getRight()-newPassword.getCompoundDrawables()[right].getBounds().width()){
                        int selection=newPassword.getSelectionEnd();
                        if(newPasswordVisible){
                            if(isRtl(getWindow().getDecorView().getRootView())){
                                //set drawable image here
                                newPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.eye_slash_password,0,R.drawable.password,0);
                            }else{
                                //set drawable image here
                                newPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.password,0,R.drawable.eye_slash_password,0);
                            }
                            //                            for hide password
                            newPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            newPasswordVisible=false;
                        }else{
                            if(isRtl(getWindow().getDecorView().getRootView())){
                                //set drawable image here
                                newPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.eye_password,0,R.drawable.password,0);
                            }else{
                                //set drawable image here
                                newPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.password,0,R.drawable.eye_password,0);
                            }
//                            for show password
                            newPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            newPasswordVisible=true;
                        }
                        newPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });



        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PrivacyActivity.this,AccountActivity.class);
                startActivity(intent);
            }//onClick()
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkInternet()){
                    if(currentPassword.getText().toString().isEmpty()||newPassword.getText().toString().isEmpty()){
                        Toast.makeText(PrivacyActivity.this, getResources().getString(R.string.empty_password), Toast.LENGTH_SHORT).show();
                    }else{
                        if(!newPassword.getText().toString().matches("[A-z,a-z,0-9]")&&newPassword.getText().toString().length()<8){newPassword.setError(getResources().getString(R.string.validatepassword));}
                        else if (!currentPassword.getText().toString().matches("[A-z,a-z,0-9]")&&currentPassword.getText().toString().length()<8) {currentPassword.setError(getResources().getString(R.string.validatepassword));}
                        else if (!newPassword.getText().toString().equals(currentPassword.getText().toString())) {
                            Toast.makeText(PrivacyActivity.this, getResources().getString(R.string.notequal_password), Toast.LENGTH_SHORT).show();
                        } else {
                            firebaseFirestore.collection("UserData").whereEqualTo("userId",String.valueOf(getId())).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        QuerySnapshot querySnapshot = task.getResult();
                                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                            for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                                // Update the fields
                                                documentSnapshot.getReference().update("password", currentPassword.getText().toString());
                                                Toast.makeText(PrivacyActivity.this, getResources().getString(R.string.change_password), Toast.LENGTH_SHORT).show();
                                                Intent intent=new Intent(PrivacyActivity.this,AccountActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }//for
                                        }//if
                                    }else{
                                        Toast.makeText(PrivacyActivity.this, getResources().getString(R.string.fail_changePass), Toast.LENGTH_SHORT).show();
                                    }//else
                                }//onComplete()
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(PrivacyActivity.this, getResources().getString(R.string.fail_changePass), Toast.LENGTH_SHORT).show();
                                }//onFailure()
                            });
                        }//else
                    }//else
                }else{
                    Toast.makeText(PrivacyActivity.this, getResources().getString(R.string.noIntrnet), Toast.LENGTH_SHORT).show();
                }//else
            }//onClick()
        });

    }//onCreate()

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

    public int getId(){
        return  sharedPreferences.getInt("id",0);
    }//getId()

    public boolean isRtl(View view) {
        return view.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }//isRtl()

}//PrivacyActivity