package com.ucst.slamtk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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


public class PersonalInformationActivity extends AppCompatActivity {
    EditText userName,secondTermMobileNumber;
    Spinner firstTermMobileNumber;
    LinearLayout linearLayoutEdit;
    Button edit,cancel;
    SharedPreferences sharedPreferences;
    ImageView imageView_arrow;
    SharedPreferences.Editor editor;
    ArrayList<String>firstTermPhoneList;
    ProgressBar progressBar;
    String firstTermPhoneStr="";
    final static int PERSONALINFORMATION=3;
    final static String PersonalInformationKey="PERSONALINFORMATION";
    ArrayList<String>phoneList;
    ArrayList<Integer>idList;
    //    create object of FirebaseFirestore class of access Cloud Firestore
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);

        //        create object from SharedPreferences to get id
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(PersonalInformationActivity.this);
        editor=sharedPreferences.edit();

        //        Change Icons In Status Bar
        View decor = PersonalInformationActivity.this.getWindow().getDecorView();
        if (decor.getSystemUiVisibility() != View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }//if
        else {
            decor.setSystemUiVisibility(0);
        }//else

        //    Associate components in the XML file with an object in the java
        userName=findViewById(R.id.editText_full_name_account);
        secondTermMobileNumber=findViewById(R.id.editText_phone_number);
        firstTermMobileNumber=findViewById(R.id.spinner_first_term_phone);
        linearLayoutEdit=findViewById(R.id.linearLayout);
        edit=findViewById(R.id.edit_btn);
        cancel=findViewById(R.id.button_cancel);
        progressBar=findViewById(R.id.progress_bar);
        imageView_arrow=findViewById(R.id.imageView_arrow);

        phoneList=new ArrayList<>();
        idList=new ArrayList<>();

        // to put design and value in spinner
        firstTermPhoneList=new ArrayList<>();
        firstTermPhoneList.add("056");
        firstTermPhoneList.add("059");
        ArrayAdapter spinnerAdapter=new ArrayAdapter<String>(PersonalInformationActivity.this,R.layout.first_term_phone,firstTermPhoneList);
        spinnerAdapter.setDropDownViewResource(R.layout.dropdown_spinner_phone);
        firstTermMobileNumber.setAdapter(spinnerAdapter);

        // To add the beginning of the spinner number with the rest of the numbers in edit text
        firstTermMobileNumber.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                firstTermPhoneStr=firstTermMobileNumber.getItemAtPosition(position).toString();
            }//onItemSelected
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}});

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PersonalInformationActivity.this,AccountActivity.class);
                startActivity(intent);
            }//onClick()
        });

        imageView_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(PersonalInformationActivity.this,AccountActivity.class);
                startActivity(intent);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkInternet()){
                    if(userName.getText().toString().isEmpty()||secondTermMobileNumber.getText().toString().isEmpty()||firstTermPhoneStr.isEmpty()){
                        Toast.makeText(PersonalInformationActivity.this, getResources().getString(R.string.empty), Toast.LENGTH_SHORT).show();
                    }else{
                        //check if name contain only characters
                        if(!userName.getText().toString().matches("[a-z,A-z,أ-ي,[ ]]*")) {userName.setError(getResources().getString(R.string.validatename));}//if
                        //check if phone contains 7 numbers
                        else if(!secondTermMobileNumber.getText().toString().matches("[0-9]{7}")) {secondTermMobileNumber.setError(getResources().getString(R.string.validatephone));}// else if
                        else{
                            String mobileNumber=firstTermPhoneStr+secondTermMobileNumber.getText().toString();
                            getData(mobileNumber);

                        }//else
                    }//else
                }else{
                    Toast.makeText(PersonalInformationActivity.this, getResources().getString(R.string.noIntrnet), Toast.LENGTH_SHORT).show();
                }//else

            }//onClick()
        });

    }//onCreate()

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

    public void getData(String phone){
        firebaseFirestore.collection("UserData").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    QuerySnapshot querySnapshot=task.getResult();
                    if(querySnapshot !=null) {
                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                            phoneList.add(documentSnapshot.getString("phone").toString());
                            idList.add(Integer.valueOf(documentSnapshot.getString("userId").toString()));
                        }//for()
                        //check if phone number is exist in firebase Database
                        System.out.println("idList.contains(getId()) : ************************************ >> "+idList.contains(getId()));
                        if(idList.contains(getId())){
                            int index=idList.indexOf(getId());
                            String phoneDb=phoneList.get(index);
                            if(phoneDb.equals(phone)){
                                generateOtpCode(phone);
                            } else if (phoneList.contains(phone)) {
                                Toast.makeText(PersonalInformationActivity.this, getResources().getString(R.string.phoneExist), Toast.LENGTH_SHORT).show();
                            }else{
                                generateOtpCode(phone);
                            }//else
                        }//if()
                    }//if
                }else {
                    Toast.makeText(PersonalInformationActivity.this, getResources().getString(R.string.fail), Toast.LENGTH_SHORT).show();
                }//else
            }//onComplete()
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PersonalInformationActivity.this, getResources().getString(R.string.fail), Toast.LENGTH_SHORT).show();
            }//onFailure()
        });
    }//getData()

    //    to generate otp code
    public void generateOtpCode(String Phone){
        progressBar.setVisibility(View.VISIBLE);
        linearLayoutEdit.setVisibility(View.GONE);
//     To generate a Otp code that will be sent to the user to verify that they are the owner of the number
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+970"+Phone,//phone number to verify
                60,//Timeout duration
                TimeUnit.SECONDS,//Unit of time
                PersonalInformationActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        progressBar.setVisibility(View.GONE);
                        linearLayoutEdit.setVisibility(View.VISIBLE);
                    }//onVerificationCompleted()
                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        progressBar.setVisibility(View.GONE);
                        linearLayoutEdit.setVisibility(View.VISIBLE);
                        Toast.makeText(PersonalInformationActivity.this,getResources().getString(R.string.tryagain),Toast.LENGTH_SHORT).show();
                    }//onVerificationFailed()

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        progressBar.setVisibility(View.GONE);
                        linearLayoutEdit.setVisibility(View.VISIBLE);
                        Dialog dialog=new Dialog(PersonalInformationActivity.this);
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
                                passUserData(verificationId,firstTermPhoneStr+secondTermMobileNumber.getText().toString(),userName.getText().toString());
                            }//onClick()
                        });
                        dialog.show();
                    }//onCodeSent()
                });
    }//generateOtpCode()

    //    Passing the data to the EnterOtpCode Activity and storing it in case the entered number is actually for the user
    public void passUserData(String verificationId,String userPhone,String userName){
        Intent intent=new Intent(PersonalInformationActivity.this,EnterOtpCodeActivity.class);
        intent.putExtra(PersonalInformationKey,PERSONALINFORMATION);
        intent.putExtra("namePersonalInformation",userName);
        intent.putExtra("phonePersonalInformation",userPhone);
        intent.putExtra("verificationIdPersonalInformation",verificationId);
        startActivity(intent);
    }//passUserData;

}//PersonalInformationActivity