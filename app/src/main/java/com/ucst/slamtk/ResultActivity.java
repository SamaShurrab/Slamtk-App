package com.ucst.slamtk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.ucst.slamtk.Class.HistoryExaminations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    TextView biliResult,riskType_Tv,recommendation1,recommendation2,recommendation3;
    ImageView riskType_img;
    Button button_previous_examinations;

    //    create object of FirebaseFirestore class of access Cloud Firestore
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //        Change Icons In Status Bar
        View decor= ResultActivity.this.getWindow().getDecorView();
        if(decor.getSystemUiVisibility() != View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR){
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }//if
        else{
            decor.setSystemUiVisibility(0);
        }//else

        //    Associate components in the XML file with an object in the java
        biliResult=findViewById(R.id.biliResult);
        riskType_Tv=findViewById(R.id.riskType_Tv);
        riskType_img=findViewById(R.id.riskType_img);
        button_previous_examinations=findViewById(R.id.button_previous_examinations);
        recommendation1=findViewById(R.id.recommendation1);
        recommendation2=findViewById(R.id.recommendation2);
        recommendation3=findViewById(R.id.recommendation3);

        System.out.println("Bili Result: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> "+getIntent().getDoubleExtra("predictedBilirubin",0));

        biliResult.setText(getIntent().getDoubleExtra("predictedBilirubin",0)+"");
        riskType_Tv.setText(getIntent().getStringExtra("riskType"));
        getRiskTypeImage(riskType_Tv.getText().toString());
        recommendation1.setText(getIntent().getStringExtra("recommindation1"));
        recommendation2.setText(getIntent().getStringExtra("recommindation2"));
        recommendation3.setText(getIntent().getStringExtra("recommindation3"));

        button_previous_examinations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(ResultActivity.this,HistoryExaminationsActivity.class);
                startActivity(intent1);
            }
        });

    }

    public void getRiskTypeImage(String riskType){
        if(riskType.equals(getResources().getString(R.string.low_risk))){
            riskType_img.setImageResource(R.drawable.risk_type_low);
        } else if (riskType.equals(getResources().getString(R.string.medium_risk))) {
            riskType_img.setImageResource(R.drawable.risk_type_mediam);
        }else{
            riskType_img.setImageResource(R.drawable.risk_type_high);
        }
    }

}//ResultActivity