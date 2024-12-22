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
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ForgetPasswordActivity extends AppCompatActivity {
    Spinner firstTermPhone_Sp;
    EditText secondTermPhone;
    Button send;
    TextView signup;
    List<String> firstTermPhoneList;
    ArrayAdapter <String>spinnerAdapter;
    String firstTermPhoneStr="";
    ArrayList<String> phoneList;
    final int FORGETPASSWORDKEY=2;
    final String FORGETPASSWORD="ForgetPasswordKey";
    ProgressBar progressbar;
    //    create object of FirebaseFirestore class of access Cloud Firestore
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgrt_password);

        //        Change Icons In Status Bar
        View decor = ForgetPasswordActivity.this.getWindow().getDecorView();
        if (decor.getSystemUiVisibility() != View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }//if
        else {
            decor.setSystemUiVisibility(0);
        }//else

        //        Associate components in the XML file with an object in the java
        firstTermPhone_Sp=findViewById(R.id.spinner_first_term_phone);
        secondTermPhone=findViewById(R.id.editText_phone_number);
        send=findViewById(R.id.button_send_otp);
        progressbar=findViewById(R.id.progressbar);
        signup=findViewById(R.id.textView_signup);

        phoneList=new ArrayList<>();

        // to put design and value in spinner
        firstTermPhoneList=new ArrayList<>();
        firstTermPhoneList.add("056");
        firstTermPhoneList.add("059");
        spinnerAdapter=new ArrayAdapter<String>(ForgetPasswordActivity.this,R.layout.first_term_phone,firstTermPhoneList);
        spinnerAdapter.setDropDownViewResource(R.layout.dropdown_spinner_phone);
        firstTermPhone_Sp.setAdapter(spinnerAdapter);

        // To add the beginning of the spinner number with the rest of the numbers in edit text
        firstTermPhone_Sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                firstTermPhoneStr=firstTermPhone_Sp.getItemAtPosition(position).toString();
            }//onItemSelected
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}});

        //        When you click on the text of signup, it will move to the SignUp Activity
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ForgetPasswordActivity.this, SignUpActivity.class);
                startActivity(intent);
            }//onClick
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkInternet()){
                    if(secondTermPhone.getText().toString().isEmpty()){
                        secondTermPhone.setError(getResources().getString(R.string.required));
                    }else{
                        //check if phone contains 7 numbers
                        if(!secondTermPhone.getText().toString().matches("[0-9]{7}")) {secondTermPhone.setError(getResources().getString(R.string.validatephone));
                        }else{
                            String Phone=firstTermPhoneStr+secondTermPhone.getText().toString();
                            getData(Phone);
                        }//else
                    }//else
                }else{
                    Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.noIntrnet), Toast.LENGTH_SHORT).show();
                }//else
            }//onClick()
        });


    }//onCreate()

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
                        if(!phoneList.contains(phone)){
                            Toast.makeText(ForgetPasswordActivity.this,getResources().getString(R.string.account_login),Toast.LENGTH_SHORT).show();
                        }else{
                            generateOtpCode(phone);
                        }//else
                    }//if
                }else {
                    Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.fail_changePass), Toast.LENGTH_SHORT).show();
                }//else
            }//onComplete()
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.fail_changePass), Toast.LENGTH_SHORT).show();
            }//onFailure()
        });
    }//getData()

    //    to generate otp code
    public void generateOtpCode(String Phone){
        progressbar.setVisibility(View.VISIBLE);
        send.setVisibility(View.GONE);
//                               To generate a Otp code that will be sent to the user to verify that they are the owner of the number
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+970"+Phone,//phone number to verify
                60,//Timeout duration
                TimeUnit.SECONDS,//Unit of time
                ForgetPasswordActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Toast.makeText(ForgetPasswordActivity.this, "aaaaaaaaaaaaa", Toast.LENGTH_SHORT).show();
                        progressbar.setVisibility(View.GONE);
                        send.setVisibility(View.VISIBLE);
                    }//onVerificationCompleted()
                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        progressbar.setVisibility(View.GONE);
                        send.setVisibility(View.VISIBLE);
                        Toast.makeText(ForgetPasswordActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }//onVerificationFailed()

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        progressbar.setVisibility(View.GONE);
                        send.setVisibility(View.VISIBLE);
                        Dialog dialog=new Dialog(ForgetPasswordActivity.this);
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
                                passUserData(verificationId,firstTermPhoneStr+secondTermPhone.getText().toString());
                            }//onClick()
                        });
                        dialog.show();
                    }//onCodeSent()
                });
    }//generateOtpCode()

    //    Passing the data to the EnterOtpCode Activity and storing it in case the entered number is actually for the user
    public void passUserData(String verificationId,String userPhone){
        Intent intent=new Intent(ForgetPasswordActivity.this, EnterOtpCodeActivity.class);
        intent.putExtra(FORGETPASSWORD,FORGETPASSWORDKEY);
        intent.putExtra("phoneForget",userPhone);
        intent.putExtra("verificationIdForget",verificationId);
        startActivity(intent);
    }//passUserData;
}//ForgetPasswordActivity