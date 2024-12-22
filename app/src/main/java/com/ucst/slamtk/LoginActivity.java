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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    Spinner firstTermMobileNumber;
    EditText secondTermMobileNumber,password;
    CheckBox rememberMe;
    TextView forgetPassword,signUp;
    Button logIn;
    ArrayList<String>firstTermPhoneList;
    String firstTermPhoneStr="";
    public static final String SHARED_PREFS="sharedPrefs";
    ArrayList<String>passwordList;
    ArrayList<String>phoneList;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ArrayList<Integer>idList;
    boolean passwordVisible=false;
    //    create object of DatabaseReference class of access firebase's Realtime Database
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        Change Icons In Status Bar
        View decor = LoginActivity.this.getWindow().getDecorView();
        if (decor.getSystemUiVisibility() != View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }//if
        else {
            decor.setSystemUiVisibility(0);
        }//else

        //        create object from SharedPreferences to save session data
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        editor=sharedPreferences.edit();

        //    Associate components in the XML file with an object in the java
        firstTermMobileNumber=findViewById(R.id.spinner_first_term_phone);
        secondTermMobileNumber=findViewById(R.id.editText_phone_number_login);
        password=findViewById(R.id.editText_password);
        rememberMe=findViewById(R.id.checkBox_remember);
        forgetPassword=findViewById(R.id.textView_forget_password);
        signUp=findViewById(R.id.textView_signup);
        logIn=findViewById(R.id.button_login);

        phoneList=new ArrayList<>();
        passwordList=new ArrayList<>();
        idList=new ArrayList<>();

        //        to put design and value in spinner
        firstTermPhoneList=new ArrayList<>();
        firstTermPhoneList.add("056");
        firstTermPhoneList.add("059");
        ArrayAdapter spinnerAdapter=new ArrayAdapter<String>(LoginActivity.this,R.layout.first_term_phone,firstTermPhoneList);
        spinnerAdapter.setDropDownViewResource(R.layout.dropdown_spinner_phone);
        firstTermMobileNumber.setAdapter(spinnerAdapter);

//        Returns the value of the first part of the phone number chosen by the user
        firstTermMobileNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                firstTermPhoneStr=firstTermMobileNumber.getItemAtPosition(position).toString();
            }//onItemSelected

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}});

        //      Hide password and show password
        password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int right=2;
                if(motionEvent.getAction()== MotionEvent.ACTION_UP){
                    if(motionEvent.getRawX()>=password.getRight()-password.getCompoundDrawables()[right].getBounds().width()){
                        int selection=password.getSelectionEnd();
                        if(passwordVisible){
                            if(isRtl(getWindow().getDecorView().getRootView())){
                                //                                set drawable image here
                                password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.eye_slash_password,0,R.drawable.password,0);
                            }else{
                                //                               set drawable image here
                                password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.password,0,R.drawable.eye_slash_password,0);
                            }//else
                            //                           for hide password
                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible=false;
                        }else{
                            if(isRtl(getWindow().getDecorView().getRootView())){
                                //                               set drawable image here
                                password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.eye_password,0,R.drawable.password,0);
                            }else{
                                //                               set drawable image here
                                password.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.password,0,R.drawable.eye_password,0);
                            }//else
//                            for show password
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

        //        When you press checkbox it saves the session, and when you close the application and return to it, it goes to the Choose Activity not an Login Activity
        checkRememberMe();

        //       When you press checkbox it saves the session, and when you close the application and return to it, it goes to the Choose Activity not an Login Activity
        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    //to saves the session, and when you close the application and return to it, it goes to the Choose Activity not an Login Activity
                    SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("rememberMe","true");
                    editor.apply();
                } else if (!compoundButton.isChecked()) {
                    SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("rememberMe","false");
                    editor.apply();
                }//else if()
            }//onCheckedChanged
        });

        //      When you press the forgot password text it will move to the ForgetPassword Activity
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,ForgetPasswordActivity.class);
                startActivity(intent);
            }//onClick()
        });

//        When you click on the text of signup, it will move to the SignUp Activity
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }//onClick
        });

        //        When you click on the login button, it will be checked whether the entered number exists or not
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //To check internet connection we use Firebase DB
                if(checkInternet()){
                    //check if phone or password is empty
                    if(secondTermMobileNumber.getText().toString().isEmpty()||password.getText().toString().isEmpty()){
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.empty), Toast.LENGTH_SHORT).show();
                    }else{
                        //check if phone contains 7 numbers
                        if(!secondTermMobileNumber.getText().toString().matches("[0-9]{7}")) {secondTermMobileNumber.setError(getResources().getString(R.string.validatephone));}//if
                        //check if password contain at least 8 characters and numbers
                        else if(!password.getText().toString().matches("[A-z,a-z,0-9]")&&password.getText().toString().length()<8){password.setError(getResources().getString(R.string.validatepassword));}//else if
                        else{
                            String Phone=firstTermPhoneStr+secondTermMobileNumber.getText().toString();
                            getData(Phone,password.getText().toString());
                        }//else
                    }//else
                }else{
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.noIntrnet), Toast.LENGTH_SHORT).show();
                }//else

            }//onClick()
        });





    }//onCreate

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

    //   get layout direction
    public boolean isRtl(View view) {
        return view.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }//isRtl()

    //    When you press checkbox it saves the session, and when you close the application and return to it, it goes to the Choose Activity not an Login Activity
    public void checkRememberMe(){
        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        String rememberMe=sharedPreferences.getString("rememberMe","");
        if(rememberMe.equals("true")){
            Intent intent = new Intent(LoginActivity.this, ChooseCheckActivity.class);
            startActivity(intent);
        }//if
    }//RememberMe()

    //    to get Data from firestore cloud
    public void getData(String phone,String password){
        firebaseFirestore.collection("UserData").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    QuerySnapshot querySnapshot=task.getResult();
                    if(querySnapshot !=null) {
                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                            phoneList.add(documentSnapshot.getString("phone").toString());
                            passwordList.add(documentSnapshot.getString("password").toString());
                            idList.add(Integer.valueOf(documentSnapshot.getString("userId").toString()));
                        }//for()
                        if(phoneList.contains(phone)){
                            int index=phoneList.indexOf(phone);
                            int id=idList.get(index);
                            //mobile is exist in firebase database
                            //now get password of user from firebase data and match it with user entered  password
                            if (passwordList.contains(password)) {
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.successfullylogin), Toast.LENGTH_SHORT).show();
                                saveData(id);
                                Intent intent = new Intent(LoginActivity.this, ChooseCheckActivity.class);
                                startActivity(intent);
                            }else {
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.wrongpassword), Toast.LENGTH_SHORT).show();
                            }//else
                        }else{
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.account_login), Toast.LENGTH_SHORT).show();
                        }//else
                    }//if()
                }else {
                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.faillogin), Toast.LENGTH_SHORT).show();
                }//else
            }//onComplete()
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.faillogin), Toast.LENGTH_SHORT).show();
            }//onFailure()
        });
    }//getData()

    //    To save the phone number in SharedPreferences to be used in other interfaces when saving the session
    public void saveData(int id){
        editor.putInt("id",id);
        editor.apply();
    }//saveData()

}//LoginActivity