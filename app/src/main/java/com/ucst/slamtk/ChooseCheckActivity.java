 package com.ucst.slamtk;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


 public class ChooseCheckActivity extends AppCompatActivity {
    Button newCheck , previousExamination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        //        Change Icons In Status Bar
        View decor= ChooseCheckActivity.this.getWindow().getDecorView();
        if(decor.getSystemUiVisibility() != View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR){
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }//if
        else{
            decor.setSystemUiVisibility(0);
        }//else
        //Associate components in the XML file with an object in the java
        newCheck=findViewById(R.id.button_new_check);
        previousExamination=findViewById(R.id.button_previous_examinations);

        newCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChooseCheckActivity.this, Newborn_InformationActivity.class);
                startActivity(intent);
            }//onClick
        });

        previousExamination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChooseCheckActivity.this, HistoryExaminationsActivity.class);
                startActivity(intent);
            }//onClick
        });


    }//onCreate()
}//ChooseCheckActivity