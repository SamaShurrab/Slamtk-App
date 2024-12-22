package com.ucst.slamtk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Reset_PasswordActivity extends AppCompatActivity {
    EditText password;
    Button verify;
    boolean passwordVisible=false;
    //    create object of FirebaseFirestore class of access Cloud Firestore
    FirebaseFirestore firebaseFirestore= FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        //        Change Icons In Status Bar
        View decor = Reset_PasswordActivity.this.getWindow().getDecorView();
        if (decor.getSystemUiVisibility() != View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }//if
        else {
            decor.setSystemUiVisibility(0);
        }//else

        //    Associate components in the XML file with an object in the java
        password=findViewById(R.id.editText_password);
        verify=findViewById(R.id.button_verify);

        //Hide password and show password
        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int right=2;
                if(motionEvent.getAction()== MotionEvent.ACTION_UP){
                    if(motionEvent.getRawX()>=password.getRight()-password.getCompoundDrawables()[right].getBounds().width()){
                        int selection=password.getSelectionEnd();
                        if(passwordVisible){
                            if(isRtl(getWindow().getDecorView().getRootView())){
                                //set drawable image here
                                password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.eye_slash_password,0,R.drawable.password,0);
                            }else{
                                //set drawable image here
                                password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.password,0,R.drawable.eye_slash_password,0);
                            }//else
                            //for hide password
                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible=false;
                        }else{
                            if (isRtl(getWindow().getDecorView().getRootView())) {
                                password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.eye_password,0,R.drawable.password,0);
                            }else{
                                password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.password,0,R.drawable.eye_password,0);
                            }//else
                            //for show password
                            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible=true;
                        }//else
                        password.setSelection(selection);
                        return true;
                    }//if()
                }//if()
                return false;
            }//onTouch()
        });

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkInternet()){
                    if(password.getText().toString().isEmpty()){
                        password.setError(getResources().getString(R.string.required));
                    }else{
                        if(!password.getText().toString().matches("[A-z,a-z,0-9]")&&password.getText().toString().length()<8){password.setError(getResources().getString(R.string.validatepassword));
                        }else{
                                firebaseFirestore.collection("UserData").whereEqualTo("phone",getIntent().getStringExtra("phone")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful()){
                                            QuerySnapshot querySnapshot = task.getResult();
                                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                                    // Update the fields
                                                    documentSnapshot.getReference().update("password", password.getText().toString());
                                                    Toast.makeText(Reset_PasswordActivity.this, getResources().getString(R.string.change_password), Toast.LENGTH_SHORT).show();
                                                    Intent intent=new Intent(Reset_PasswordActivity.this,ResetSuccessfullyActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }//for
                                            }//if
                                        }else{
                                            Toast.makeText(Reset_PasswordActivity.this, getResources().getString(R.string.fail_changePass), Toast.LENGTH_SHORT).show();
                                        }//else
                                    }//onComplete()
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(Reset_PasswordActivity.this, getResources().getString(R.string.fail_changePass), Toast.LENGTH_SHORT).show();
                                    }//onFailure()
                                });
                        }//else
                    }//else
                }else {
                    Toast.makeText(Reset_PasswordActivity.this, getResources().getString(R.string.noIntrnet), Toast.LENGTH_SHORT).show();
                }//else
            }//onClick()
        });

    }//onCreate()

    // get layout direction
    public boolean isRtl(View view) {
        return view.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }//isRtl

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

}//Reset_PasswordActivity