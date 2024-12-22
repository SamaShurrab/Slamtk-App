 package com.ucst.slamtk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ucst.slamtk.Class.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class EnterOtpCodeActivity extends AppCompatActivity {
    EditText otpNumber1,otpNumber2,otpNumber3,otpNumber4,otpNumber5,otpNumber6;
    Button verify;
    ProgressBar progressBar;
    TextView resendOn,timerTv;
    //    create object of FirebaseFirestore class of access Cloud Firestore
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    String verificationId="";
    final int SIGNUP=1;
    final int FORGETPASSWORDKEY=2;
    final static int PERSONALINFORMATION=3;
    final static String PersonalInformationKey="PERSONALINFORMATION";
    final String signupKey="signup";
    final String FORGETPASSWORD="ForgetPasswordKey";
    ArrayList<String>phoneList;
    ArrayList<String> passwordList;
    ArrayList<Integer> userIdList;
    String verificationIdNew="";
    UserData userDataObject;
    int userId=1;
    CountDownTimer timer;
    long timeLeftInMillis;
    final long COUNTDOWN_DURATION = 60000; // 60 seconds
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp_code);

        //        create object from SharedPreferences to get id
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(EnterOtpCodeActivity.this);
        editor=sharedPreferences.edit();

        //        Change Icons In Status Bar
        View decor = EnterOtpCodeActivity.this.getWindow().getDecorView();
        if (decor.getSystemUiVisibility() != View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }//if
        else {
            decor.setSystemUiVisibility(0);
        }//else

        //    Associate components in the XML file with an object in the java
        otpNumber1=findViewById(R.id.editTextNumber_otp1);
        otpNumber2=findViewById(R.id.editTextNumber_otp2);
        otpNumber3=findViewById(R.id.editTextNumber_otp3);
        otpNumber4=findViewById(R.id.editTextNumber_otp4);
        otpNumber5=findViewById(R.id.editTextNumber_otp5);
        otpNumber6=findViewById(R.id.editTextNumber_otp6);
        verify=findViewById(R.id.button_send_otp);
        resendOn=findViewById(R.id.textView_resend);
        progressBar=findViewById(R.id.progressbar);
        timerTv=findViewById(R.id.timer);

        phoneList=new ArrayList<>();
        passwordList=new ArrayList<>();
        userIdList=new ArrayList<>();
        userDataObject=new UserData();

        String phoneStr=getIntent().getStringExtra("phone");
        String phoneForgetPass=getIntent().getStringExtra("phoneForget");
        String phonePersonalInformation=getIntent().getStringExtra("phonePersonalInformation");

        setupOtpInputs();
        getUserId();

        //Upon pressing the submit button, it will scan the entered verification code and store the user data in the firebase
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkInternet()){
                    if(otpNumber1.getText().toString().isEmpty()||otpNumber2.getText().toString().isEmpty()||otpNumber3.getText().toString().isEmpty()||otpNumber4.getText().toString().isEmpty()||otpNumber5.getText().toString().isEmpty()||otpNumber6.getText().toString().isEmpty()){
                        Toast.makeText(EnterOtpCodeActivity.this, getResources().getString(R.string.empty), Toast.LENGTH_SHORT).show();
                    }else{
                        String otpCode = otpNumber1.getText().toString() + otpNumber2.getText().toString() + otpNumber3.getText().toString() + otpNumber4.getText().toString() + otpNumber5.getText().toString() + otpNumber6.getText().toString();
                        // To verify the verification code to store the data transferred from SignUp Activity to EnterOtpCode Activity
                        if (getIntent().getIntExtra(signupKey, 0) == SIGNUP) {
                            //To fetch data from an SignUp Activity to store it in Firebase
                            String nameStr=getIntent().getStringExtra("name");
                            String passwordStr=getIntent().getStringExtra("password");
                            verificationId=getIntent().getStringExtra("verificationId");
                            storeDataSignUp(userId,phoneStr,otpCode,passwordStr,nameStr,verificationId);
                        } else if (getIntent().getIntExtra(FORGETPASSWORD,0)==FORGETPASSWORDKEY) {
                            forgetPassword(phoneForgetPass,getIntent().getStringExtra("verificationIdForget"),otpCode);
                        }else{
                            String nameStr=getIntent().getStringExtra("namePersonalInformation");
                            personalInformation(getId(),phonePersonalInformation,nameStr,getIntent().getStringExtra("verificationIdPersonalInformation"),otpCode);
                        }//else
                    }//else
                }else{
                    Toast.makeText(EnterOtpCodeActivity.this, getResources().getString(R.string.noIntrnet), Toast.LENGTH_SHORT).show();
                }//else
            }//onClick()
        });
        startTimer();
        disableResendButton();

        //To resend the verification code if the verification code did not arrive the first time
        resendOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otpNumber1.setText("");
                otpNumber2.setText("");
                otpNumber3.setText("");
                otpNumber4.setText("");
                otpNumber5.setText("");
                otpNumber6.setText("");
                if(checkInternet()){
                    if (getIntent().getIntExtra(signupKey, 0) == SIGNUP) {
                        getOtpCode(phoneStr);
                    } else if (getIntent().getIntExtra(FORGETPASSWORD,0)==FORGETPASSWORDKEY) {
                        getOtpCode(phoneForgetPass);
                    }//else if()
                    disableResendButton();
                    startTimer();
                }else {
                    Toast.makeText(EnterOtpCodeActivity.this,getResources().getString(R.string.noIntrnet),Toast.LENGTH_SHORT).show();
                }//else

            }//onClick()
        });

    }//EnterOtpCodeActivity

    private void personalInformation(int userId, String phonePersonalInformation, String nameStr, String verificationIdPersonalInformation, String otpCode) {
        if (verificationId != null) {
            progressBar.setVisibility(View.VISIBLE);
            verify.setVisibility(View.GONE);
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationIdPersonalInformation, otpCode);
            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    verify.setVisibility(View.VISIBLE);
                    if (task.isSuccessful()) {
                        firebaseFirestore.collection("UserData").whereEqualTo("userId",String.valueOf(userId)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                            // Update the fields
                                            documentSnapshot.getReference().update("userName", nameStr);
                                            documentSnapshot.getReference().update("phone", phonePersonalInformation);
                                            Toast.makeText(EnterOtpCodeActivity.this, getResources().getString(R.string.change_data), Toast.LENGTH_SHORT).show();
                                            Intent intent=new Intent(EnterOtpCodeActivity.this,AccountActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        }//for
                                    }//if
                                }else{
                                    Toast.makeText(EnterOtpCodeActivity.this, getResources().getString(R.string.fail_change), Toast.LENGTH_SHORT).show();
                                }//else
                            }//onComplete()
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EnterOtpCodeActivity.this, getResources().getString(R.string.fail_change), Toast.LENGTH_SHORT).show();
                            }//onFailure()
                        });
                    } //if
                    else {
                        Toast.makeText(EnterOtpCodeActivity.this, getResources().getString(R.string.invalidOtp), Toast.LENGTH_SHORT).show();
                    }//else
                }//onComplete()
            });
        }//if

    }//personalInformation()

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

    //    To move the cursor all that the user number one typed from the verification code to edit text the other
    public void setupOtpInputs(){
        otpNumber1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                if(!charSequence.toString().trim().isEmpty()){otpNumber2.requestFocus();}//if
            }//onTextChanged()

            @Override
            public void afterTextChanged(Editable editable) {}});

        otpNumber2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                if(!charSequence.toString().trim().isEmpty()){otpNumber3.requestFocus();}//if
            }//onTextChanged()

            @Override
            public void afterTextChanged(Editable editable) {}});

        otpNumber3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                if(!charSequence.toString().trim().isEmpty()){otpNumber4.requestFocus();}//if
            }//onTextChanged

            @Override
            public void afterTextChanged(Editable editable) {}});

        otpNumber4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                if(!charSequence.toString().trim().isEmpty()){otpNumber5.requestFocus();}//if
            }//onTextChanged()

            @Override
            public void afterTextChanged(Editable editable) {}});

        otpNumber5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                if(!charSequence.toString().trim().isEmpty()){otpNumber6.requestFocus();}//if
            }//onTextChanged()

            @Override
            public void afterTextChanged(Editable editable) {}});
    }//setupOtpInputs()

    //    To store user data if the otp code value is correct in Firebase
    public void storeDataSignUp(int userID,String phone , String otpCode,String password , String name,String verificationId){
        if (verificationId != null) {
            progressBar.setVisibility(View.VISIBLE);
            verify.setVisibility(View.GONE);
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, otpCode);
            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    verify.setVisibility(View.VISIBLE);
                    if (task.isSuccessful()) {
                        Map<String,String > userData=new HashMap<>();
                        userData.put("userId", String.valueOf(userID));
                        userData.put("userName",name);
                        userData.put("phone",phone);
                        userData.put("password",password);
                        firebaseFirestore.collection("UserData").add(userData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(EnterOtpCodeActivity.this, getResources().getString(R.string.successfullysignup), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EnterOtpCodeActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }//onSuccess()
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EnterOtpCodeActivity.this, getResources().getString(R.string.fail), Toast.LENGTH_SHORT).show();
                            }//onFailure()
                        });
                    } //if
                    else {
                        Toast.makeText(EnterOtpCodeActivity.this, getResources().getString(R.string.invalidOtp), Toast.LENGTH_SHORT).show();
                    }//else
                }//onComplete()
            });
        }//if
    }//storeDataSignUp()

    public void forgetPassword(String phone,String verificationId,String otpCode){
        if(verificationId !=null){
            progressBar.setVisibility(View.VISIBLE);
            verify.setVisibility(View.GONE);
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, otpCode);
            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    verify.setVisibility(View.VISIBLE);
                    if (task.isSuccessful()) {
                        getData(phone);
                    } //if
                    else {
                        Toast.makeText(EnterOtpCodeActivity.this, getResources().getString(R.string.invalidOtp), Toast.LENGTH_SHORT).show();
                    }//else
                }//onComplete()
            });
        }//if
    }//storeForgetPassword()

    public void getUserId(){
        firebaseFirestore.collection("UserData").orderBy("userId", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    QuerySnapshot querySnapshot=task.getResult();
                    if(querySnapshot !=null) {
                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                            userIdList.add(Integer.valueOf(documentSnapshot.getString("userId").toString()));
                        }//for()
                        if(userIdList.contains(userId)){
                            int lastId= userIdList.get(userIdList.size()-1);
                            userId=lastId+1;
                        }else {
                            userId=1;
                        }
                    }//if()
                }//if()
            }//onComplete()
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EnterOtpCodeActivity.this, getResources().getString(R.string.fail_changePass), Toast.LENGTH_SHORT).show();
            }//onFailure()
        });
    }//getUserId()

    //    to get Data from firestore cloud
    public void getData(String phone){
        firebaseFirestore.collection("UserData").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    QuerySnapshot querySnapshot=task.getResult();
                    if(querySnapshot !=null) {
                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                            phoneList.add(documentSnapshot.getString("phone").toString());
                            passwordList.add(documentSnapshot.getString("password").toString());
                        }//for()
                        //mobile is exist in firebase database
                        if(phoneList.contains(phone)){
                            int index=phoneList.indexOf(phone);
                            String passwordStr=passwordList.get(index);
                            Intent intent = new Intent(EnterOtpCodeActivity.this, Reset_PasswordActivity.class);
                            intent.putExtra("phone",phone);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(EnterOtpCodeActivity.this, getResources().getString(R.string.account_login), Toast.LENGTH_SHORT).show();
                        }//else
                    }//if()
                }else {
                    Toast.makeText(EnterOtpCodeActivity.this, getResources().getString(R.string.fail_changePass), Toast.LENGTH_SHORT).show();
                }//else
            }//onComplete()
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EnterOtpCodeActivity.this, getResources().getString(R.string.fail_changePass), Toast.LENGTH_SHORT).show();
            }//onFailure()
        });
    }//getData()

    //    In the case of re-sending a otp code again
    public void getOtpCode(String phone){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+970"+phone,//phone number to verify
                60,//Timeout duration
                TimeUnit.SECONDS,//Unit of time
                EnterOtpCodeActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        progressBar.setVisibility(View.GONE);
                        verify.setVisibility(View.VISIBLE);
                    }//onVerificationCompleted()
                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        progressBar.setVisibility(View.GONE);
                        verify.setVisibility(View.VISIBLE);
                        Toast.makeText(EnterOtpCodeActivity.this,getResources().getString(R.string.tryagain),Toast.LENGTH_SHORT).show();
                    }//onVerificationFailed()

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        progressBar.setVisibility(View.GONE);
                        verify.setVisibility(View.VISIBLE);
                        verificationIdNew=verificationId;
                    }//onCodeSent()
                });
    }//getOtpCode()

    private void startTimer() {
        timer = new CountDownTimer(COUNTDOWN_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountdownText();
            }

            @Override
            public void onFinish() {
                enableResendButton();
            }
        }.start();
    }

    private void updateCountdownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeLeftFormatted = String.format("%02d:%02d", minutes, seconds);
        timerTv.setText(timeLeftFormatted);
    }

    private void disableResendButton() {
        resendOn.setVisibility(View.GONE);
        resendOn.setTextColor(getResources().getColor(R.color.Dim_Gray));
    }

    private void enableResendButton() {
        resendOn.setVisibility(View.VISIBLE);
        resendOn.setTextColor(getResources().getColor(R.color.black));
    }
    public int getId(){
        return  sharedPreferences.getInt("id",0);
    }//getId()

}//EnterOtpCodeActivity