package com.ucst.slamtk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.icu.util.Calendar;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ucst.slamtk.Class.BabyInformation;

import org.checkerframework.checker.units.qual.C;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public  class Newborn_InformationActivity extends AppCompatActivity {
    ImageView account,delete_date_btn,delete_time_btn;
    TextView userName,birth_date,birth_time,risk_factor_tv;
    EditText babyName;
    Spinner gestationalAgeSp;
    RadioButton male,female,yse,no;
    RadioGroup genderRg,riskFactorRg;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int babyId=1;
    Button submit;
    ImageView move;
    ArrayList<String>nameList;
    ArrayList<Integer>idList;
    String selectedWeek="";
    ArrayList<String>gestational_AgeList;
    ArrayList<String>babyNameList;
    ArrayList<Integer>babyIdList;
    LinearLayout newBornInformtion,alert;
    ArrayList<Integer> ageHour;
    boolean check;
    int birthDay,birthMonth,birthYear,birthHour;

    //    create object of FirebaseFirestore class of access Cloud Firestore
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newborn_information);

        //        create object from SharedPreferences to get id
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(Newborn_InformationActivity.this);
        editor=sharedPreferences.edit();

        //        Change Icons In Status Bar
        View decor= Newborn_InformationActivity.this.getWindow().getDecorView();
        if(decor.getSystemUiVisibility() != View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR){
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }//if
        else{
            decor.setSystemUiVisibility(0);
        }//else

        //    Associate components in the XML file with an object in the java
        account=findViewById(R.id.imageView_account);
        delete_date_btn=findViewById(R.id.delete_date_btn);
        delete_time_btn=findViewById(R.id.delete_time_btn);
        userName=findViewById(R.id.textView_userName);
        birth_date=findViewById(R.id.birth_date);
        birth_time=findViewById(R.id.birth_time);
        babyName=findViewById(R.id.editText_baby_name);
        gestationalAgeSp=findViewById(R.id.spinner_gestational_age);
        male=findViewById(R.id.male_rd);
        female=findViewById(R.id.female_rd);
        yse=findViewById(R.id.yes_rd);
        no=findViewById(R.id.no_rd);
        genderRg=findViewById(R.id.genderRG);
        riskFactorRg=findViewById(R.id.risk_factor_rg);
        submit=findViewById(R.id.submit_btn);
        risk_factor_tv=findViewById(R.id.risk_factor_tv);
        newBornInformtion=findViewById(R.id.newBornInformation);
        alert=findViewById(R.id.newBornInformation_alert);
        move=findViewById(R.id.move);

        ageHour=new ArrayList<>();
        babyNameList=new ArrayList<>();
        babyIdList=new ArrayList<>();

        LocalTime currentTime = LocalTime.now();
        int currentHour = currentTime.getHour(); // 24-hour format

        //        for date picker and time picker
        Calendar calendar=Calendar.getInstance();
        final int year= calendar.get(Calendar.YEAR);
        final int month= calendar.get(Calendar.MONTH);
        final int day= calendar.get(Calendar.DAY_OF_MONTH);
        final int hour =calendar.get(Calendar.HOUR_OF_DAY);
        final int minute =calendar.get(Calendar.MINUTE);

        idList=new ArrayList<>();
        nameList=new ArrayList<>();

        getName(getId());
        getBabyId();
        onStartMethod();

        gestational_AgeList=new ArrayList<>();
        gestational_AgeList.add(getResources().getString(R.string.choose));
        gestational_AgeList.add(getResources().getString(R.string.option1));
        gestational_AgeList.add(getResources().getString(R.string.option2));
        gestational_AgeList.add(getResources().getString(R.string.option3));
        gestational_AgeList.add(getResources().getString(R.string.option4));
        gestational_AgeList.add(getResources().getString(R.string.option5));
        gestational_AgeList.add(getResources().getString(R.string.option6));
        ArrayAdapter spinnerAdapter=new ArrayAdapter<String>(Newborn_InformationActivity.this,R.layout.gestational_age,gestational_AgeList);
        spinnerAdapter.setDropDownViewResource(R.layout.dropdown_spinner_week);
        gestationalAgeSp.setAdapter(spinnerAdapter);

        gestationalAgeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if(position==0){
                    selectedWeek="";
                }else {
                    selectedWeek= (String) gestationalAgeSp.getSelectedItem();
                }//else
            }//onItemSelected()
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });


        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Newborn_InformationActivity.this,AccountActivity.class);
                startActivity(intent);
            }//onClick()
        });

        //      enter date of birth for newborn
        birth_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String birthDateStr=birth_date.getText().toString().trim();
                //        delete content in Edit Text birth date
                if(birthDateStr.isEmpty()) {
                    delete_date_btn.setVisibility(View.GONE);
//                    birth_date.setError(getResources().getString(R.string.required));
                }//if
                else{
                    delete_date_btn.setVisibility(View.VISIBLE);
                    delete_date_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            birth_date.setText("");
                        }
                    });
                }//else
                DatePickerDialog datePickerDialog=new DatePickerDialog(Newborn_InformationActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month=month+1;
                        birthDay=day;
                        birthYear=year;
                        birthMonth=month;
                        birth_date.setText(day+"/"+month+"/"+year);
                    }
                },year,month,day);
                datePickerDialog.show();
            }//onClick
        });

//            enter time of birth for newborn
        birth_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String birthTimeStr=birth_time.getText().toString().trim();
                // delete content in Edit Text birth Time
                if(birthTimeStr.isEmpty()) {
                    delete_time_btn.setVisibility(View.GONE);
//                    birth_time.setError(getResources().getString(R.string.required));
                }//if
                else{
                    delete_time_btn.setVisibility(View.VISIBLE);
                    delete_time_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            birth_time.setText("");
                        }
                    });
                }//else
                TimePickerDialog timePickerDialog=new TimePickerDialog(Newborn_InformationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        birthHour=hour;
                        birth_time.setText(hour+":"+minute);
                    }
                },hour,minute,false);
                timePickerDialog.show();
            }
        });

        risk_factor_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog=new Dialog(Newborn_InformationActivity.this);
                dialog.setContentView(R.layout.risk_factor_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.getWindow().setWindowAnimations(R.style.RiskFactorAnimation);
                ImageView imageView_close=dialog.findViewById(R.id.imageView_close);
                imageView_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.cancel();
                    }//onClick()
                });
                dialog.show();
                dialog.getWindow().setGravity(Gravity.BOTTOM);
            }//onClick()
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeValidAgeInHour(ageHour);
                Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);
                int currentMonth = calendar.get(Calendar.MONTH) + 1; // Months are zero-based, so add 1
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                int currentMinute=calendar.get(Calendar.MINUTE);
                int amPm=calendar.get(Calendar.AM_PM);
                if(checkInternet()){
                    if(babyName.getText().toString().isEmpty()||isCheckedGender()||selectedWeek.isEmpty()||birth_time.getText().toString().isEmpty()||birth_date.getText().toString().isEmpty()||isCheckedRiskFactor()){
                        Toast.makeText(Newborn_InformationActivity.this, getResources().getString(R.string.empty), Toast.LENGTH_SHORT).show();
                    }else{
                        if(!babyName.getText().toString().matches("[a-z,A-z,أ-ي,[ ]]*")) {babyName.setError(getResources().getString(R.string.validatename));}//if
                        else if(checkDate(currentDay,currentMonth,currentYear,birthDay,birthMonth,birthYear)){Toast.makeText(Newborn_InformationActivity.this, getResources().getString(R.string.invalid), Toast.LENGTH_SHORT).show();}
                        else if (!ageHour.contains(calculateAgeBabyInHour(currentDay,birthDay,currentMonth,birthMonth,currentHour,birthHour))) {
                            Toast.makeText(Newborn_InformationActivity.this, getResources().getString(R.string.invaliage), Toast.LENGTH_SHORT).show();
                        }else{
                            firebaseFirestore.collection("BabyInformation").orderBy("babyId", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()) {
                                        QuerySnapshot querySnapshot=task.getResult();
                                        if(querySnapshot !=null) {
                                            for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                                babyNameList.add(documentSnapshot.getString("babyName").toString());
                                                babyIdList.add(Integer.valueOf(documentSnapshot.getString("babyId").toString()));
                                            }//for()
                                            if(babyNameList.contains(babyName.getText().toString())){
                                                int index =babyNameList.indexOf(babyName.getText().toString());
                                                babyId=babyIdList.get(index);
                                                if (amPm == Calendar.AM) {
                                                    saveData(currentDay+"/"+currentMonth+"/"+currentYear,currentHour+":"+currentMinute+" Am",babyId,calculateAgeBabyInHour(currentDay,birthDay,currentMonth,birthMonth,currentHour,birthHour),selectedWeek);
                                                } else {
                                                    saveData(currentDay+"/"+currentMonth+"/"+currentYear,currentHour+":"+currentMinute+" Pm",babyId,calculateAgeBabyInHour(currentDay,birthDay,currentMonth,birthMonth,currentHour,birthHour),selectedWeek);
                                                }
                                                Intent intent = new Intent(Newborn_InformationActivity.this, ForeheadCaptureActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }else{
                                                if (amPm == Calendar.AM) {
                                                    saveData(currentDay+"/"+currentMonth+"/"+currentYear,currentHour+":"+currentMinute+" Am",babyId,calculateAgeBabyInHour(currentDay,birthDay,currentMonth,birthMonth,currentHour,birthHour),selectedWeek);
                                                } else {
                                                    saveData(currentDay+"/"+currentMonth+"/"+currentYear,currentHour+":"+currentMinute+" Pm",babyId,calculateAgeBabyInHour(currentDay,birthDay,currentMonth,birthMonth,currentHour,birthHour),selectedWeek);
                                                }
                                                addDataToFirestore(new BabyInformation(String.valueOf(babyId),String.valueOf(getId()),babyName.getText().toString(),getGender(),birth_date.getText().toString(),birth_time.getText().toString(),selectedWeek, getRiskFactor()));
                                            }
                                        }//if()
                                    }//if()
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {}
                            });
                        }//else
                    }//else
                }else{
                    Toast.makeText(Newborn_InformationActivity.this, getResources().getString(R.string.noIntrnet), Toast.LENGTH_SHORT).show();
                }//else
            }//onClick()
        });



    }// onCreate()

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
                                userName.setText(nameList.get(idList.indexOf(id)));
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

    public int calculateAgeBabyInHour(int currentDate, int birthDate, int currentMonth,int birthMonth,int currentHour, int birthHour) {
        int days;
        int hours=0;
        if(currentMonth>birthMonth){
            days = currentDate - birthDate+30;
            hours=days*24;
        }else{
            days=currentDate-birthDate;
            hours = (days * 24) + (currentHour - birthHour);
        }

        return hours;
    }

    public boolean checkDate(int currentDate,int currentMonth,int currentYear,int birthDate,int birthMonth,int birthYear){
        boolean check=false;
        if(currentYear<birthYear){
            check=true;
        }else{
            if(currentMonth < birthMonth){
                check=true;
            }else{
                if(currentMonth>birthMonth){
                    check=false;
                }else{
                    if (currentDate < birthDate) {
                        check=true;
                    } else {
                        check=false;
                    }//else
                }
            }
        }
        return check;
    }//checkDate()

    public boolean isCheckedGender(){
        if(!(male.isChecked()||female.isChecked())){
            return true;
        }else{
            return false;
        }//else
    }//isCheckedGender()

    public boolean isCheckedRiskFactor(){
        if(!(yse.isChecked()||no.isChecked())){
            return true;
        }else{
            return false;
        }//else
    }//isCheckedGender()

    public void storeValidAgeInHour(ArrayList<Integer>age){
        for(int i=48;i<=336;i++){
            age.add(i);
        }
    }

    private void addDataToFirestore(BabyInformation babyInformation) {
        Map<String, Object> babyMap = new HashMap<>();
        babyMap.put("babyId", babyInformation.getBabyId());
        babyMap.put("babyName", babyInformation.getBabyName());
        babyMap.put("parentId", babyInformation.getUserId());
        babyMap.put("babyGender", babyInformation.getGender());
        babyMap.put("babyBirthDate", babyInformation.getBirthDate());
        babyMap.put("babyBirthTime", babyInformation.getBirthTime());
        babyMap.put("babyRiskFactor", babyInformation.getRiskFactor());
        babyMap.put("babyGestationalAge", babyInformation.getGestationalAge());
        firebaseFirestore.collection("BabyInformation").add(babyMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(Newborn_InformationActivity.this, getResources().getString(R.string.success_save_baby), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Newborn_InformationActivity.this, ForeheadCaptureActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }//onSuccess()
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Newborn_InformationActivity.this, getResources().getString(R.string.fail_save_baby), Toast.LENGTH_SHORT).show();
            }//onFailure()
        });
    }

    public void getBabyId(){
        firebaseFirestore.collection("BabyInformation").orderBy("babyId", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    QuerySnapshot querySnapshot=task.getResult();
                    if(querySnapshot !=null) {
                        for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                            babyIdList.add(Integer.valueOf(documentSnapshot.getString("babyId").toString()));
                        }//for()
                        if(babyIdList.contains(babyId)){
                            int lastId= babyIdList.get(babyIdList.size()-1);
                            babyId=lastId+1;
                        }else {
                            babyId=1;
                        }
                    }//if()
                }//if()
            }//onComplete()
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Newborn_InformationActivity.this, getResources().getString(R.string.fail_changePass), Toast.LENGTH_SHORT).show();
            }//onFailure()
        });
    }//getUserId()

    public String getGender(){
        if(male.isChecked()){
            return "male";
        }else{
            return "female";
        }
    }

    public String getRiskFactor(){
        if(yse.isChecked()){
            return "yes";
        }else{
            return "no";
        }
    }

    public void saveData(String currentDate,String currentTime,int babyId,int age,String gestationalAge){
        editor.putString("sampleDate",currentDate);
        editor.putString("sampleTime",currentTime);
        editor.putInt("babyId",babyId);
        editor.putInt("ageHour",age);
        editor.putString("gestationalAge",gestationalAge);
        editor.apply();
    }//saveData

    public void onStartMethod(){
        new Handler().postAtTime(new Runnable() {
            @Override
            public void run() {
                alert.setVisibility(View.VISIBLE);
                newBornInformtion.setVisibility(View.GONE);
                Animation animation = AnimationUtils.loadAnimation(Newborn_InformationActivity.this, R.anim.zoom_in_dialog);
                alert.startAnimation(animation);
            }
        },500);

        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        alert.setVisibility(View.GONE);
                        newBornInformtion.setVisibility(View.VISIBLE);
                        Animation animation = AnimationUtils.loadAnimation(Newborn_InformationActivity.this, R.anim.slide_in);
                        newBornInformtion.startAnimation(animation);
            }
        });


    }//onStartMethod()

}// class Newborn_InformationActivity
