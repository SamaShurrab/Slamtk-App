package com.ucst.slamtk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {
    EditText userName, secondTermMobileNumber;
    Spinner firstTermMobileNumberSp;
    EditText password;
    Button signUp;
    ProgressBar progress_bar;
    TextView logIn;
    ArrayList<String>firstTermPhoneList;
    String firstTermPhoneStr="";
    boolean passwordVisible=false;
    final String signupKey="signup";
    final int SIGNUP=1;
    ArrayList<String>phoneList;
    //    create object of FirebaseFirestore class of access Cloud Firestore
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Change Icons In Status Bar
        View decor = SignUpActivity.this.getWindow().getDecorView();
        if (decor.getSystemUiVisibility() != View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }//if
        else {
            decor.setSystemUiVisibility(0);
        }//else

        //    Associate components in the XML file with an object in the java
        userName=findViewById(R.id.editText_full_name);
        secondTermMobileNumber=findViewById(R.id.editText_phone_number);
        firstTermMobileNumberSp=findViewById(R.id.spinner_first_term_phone);
        password=findViewById(R.id.editText_password);
        signUp=findViewById(R.id.button_signup);
        progress_bar=findViewById(R.id.progress_bar);
        logIn=findViewById(R.id.textView_login);

        phoneList=new ArrayList<>();

        // to put design and value in spinner
        firstTermPhoneList=new ArrayList<>();
        firstTermPhoneList.add("056");
        firstTermPhoneList.add("059");
        ArrayAdapter spinnerAdapter=new ArrayAdapter<String>(SignUpActivity.this,R.layout.first_term_phone,firstTermPhoneList);
        spinnerAdapter.setDropDownViewResource(R.layout.dropdown_spinner_phone);
        firstTermMobileNumberSp.setAdapter(spinnerAdapter);

        // To add the beginning of the spinner number with the rest of the numbers in edit text
        firstTermMobileNumberSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                firstTermPhoneStr=firstTermMobileNumberSp.getItemAtPosition(position).toString();
            }//onItemSelected
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}});
        //Hide password and show password
        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int right=2;
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
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

        // When you click on the text, it moves to an Login Activity
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(intent);
            }//onClick
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkInternet()){
                    if(isEmpty(userName.getText().toString(),secondTermMobileNumber.getText().toString(),password.getText().toString())){
                        Toast.makeText(SignUpActivity.this, getResources().getString(R.string.empty), Toast.LENGTH_SHORT).show();
                    }else{
                        //check if name contain only characters
                        if(!userName.getText().toString().matches("[a-z,A-z,أ-ي,[ ]]*")) {userName.setError(getResources().getString(R.string.validatename));}//if
                        //check if phone contains 7 numbers
                        else if(!secondTermMobileNumber.getText().toString().matches("[0-9]{7}")) {secondTermMobileNumber.setError(getResources().getString(R.string.validatephone));}// else if
                        //check if password contain at least 8 characters and numbers
                        else if(!password.getText().toString().matches("[A-z,a-z,0-9]")&&password.getText().toString().length()<8){password.setError(getResources().getString(R.string.validatepassword));}//else if
                        else {
                            String mobileNumber=firstTermPhoneStr+secondTermMobileNumber.getText().toString();
                            getData(mobileNumber);
                        }//else
                    }//else
                }else{
                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.noIntrnet), Toast.LENGTH_SHORT).show();
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
            if(networkInfos.isConnected() || networkInfos.isFailover()||networkInfos.isRoaming()){
                return true;
            }//if
            else{
                return false;
            }//else
        }else{
            return false;
        }//else
    }//checkInternet()

//    to check is feild is empty or not
    public boolean isEmpty(String name,String mobileNumber,String passwrod){
        boolean empty=true;
        if(name.isEmpty()||mobileNumber.isEmpty()||passwrod.isEmpty()){
            empty=true;
        }else {
            empty=false;
        }//else
        return empty;
    }//isEmpty()

    public void getData(String phone){
        firebaseFirestore.collection("UserData").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    QuerySnapshot querySnapshot=task.getResult();
                    if(querySnapshot !=null) {
                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                            phoneList.add(documentSnapshot.getString("phone").toString());
                        }//for()
                        //check if phone number is exist in firebase Database
                        if(phoneList.contains(phone)){
                            Toast.makeText(SignUpActivity.this,getResources().getString(R.string.alreadyhaveaccount),Toast.LENGTH_SHORT).show();
                        }else{
                            generateOtpCode(phone);
                        }//else
                    }//if
                }else {
                    Toast.makeText(SignUpActivity.this, getResources().getString(R.string.fail), Toast.LENGTH_SHORT).show();
                }//else
            }//onComplete()
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, getResources().getString(R.string.fail), Toast.LENGTH_SHORT).show();
            }//onFailure()
        });
    }//getData()

    //    to generate otp code
    public void generateOtpCode(String Phone){
        progress_bar.setVisibility(View.VISIBLE);
        signUp.setVisibility(View.GONE);
//     To generate a Otp code that will be sent to the user to verify that they are the owner of the number
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+970"+Phone,//phone number to verify
                60,//Timeout duration
                TimeUnit.SECONDS,//Unit of time
                SignUpActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        progress_bar.setVisibility(View.GONE);
                        signUp.setVisibility(View.VISIBLE);
                    }//onVerificationCompleted()
                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        progress_bar.setVisibility(View.GONE);
                        signUp.setVisibility(View.VISIBLE);
                        Toast.makeText(SignUpActivity.this,getResources().getString(R.string.tryagain),Toast.LENGTH_SHORT).show();
                    }//onVerificationFailed()

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        progress_bar.setVisibility(View.GONE);
                        signUp.setVisibility(View.VISIBLE);
                        Dialog dialog=new Dialog(SignUpActivity.this);
                        dialog.setContentView(R.layout.verification_dialog);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                        dialog.getWindow().setWindowAnimations(R.style.AnimationDialog);
                        ImageView close=dialog.findViewById(R.id.imageView_close);
                        /*When clicking on the closing image, it will close dialog and the Activity will move to EnterOtpCode Activity
                        to write the confirmation code sent to the user to make sure that he is the owner of the number and store the data in Firebase*/
                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.cancel();
                                passUserData(verificationId,firstTermPhoneStr+secondTermMobileNumber.getText().toString(),userName.getText().toString(),password.getText().toString());
                            }//onClick()
                        });
                        dialog.show();
                    }//onCodeSent()
                });
    }//generateOtpCode()

    //    Passing the data to the EnterOtpCode Activity and storing it in case the entered number is actually for the user
    public void passUserData(String verificationId,String userPhone,String userName,String userPassword){
        Intent intent=new Intent(SignUpActivity.this,EnterOtpCodeActivity.class);
        intent.putExtra(signupKey,SIGNUP);
        intent.putExtra("name",userName);
        intent.putExtra("password",userPassword);
        intent.putExtra("phone",userPhone);
        intent.putExtra("verificationId",verificationId);
        startActivity(intent);
    }//passUserData;
}//SignUpActivity