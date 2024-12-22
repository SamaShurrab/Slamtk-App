package com.ucst.slamtk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class ResetSuccessfullyActivity extends AppCompatActivity {
    Button logIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_successfully);

        //        Change Icons In Status Bar
        View decor= ResetSuccessfullyActivity.this.getWindow().getDecorView();
        if(decor.getSystemUiVisibility() != View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR){
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }//if
        else{
            decor.setSystemUiVisibility(0);
        }//else

//    Associate components in the XML file with an object in the java
        logIn=findViewById(R.id.button_login);

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ResetSuccessfullyActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }//onClick()
        });
    }//onCreate
}//ResetSuccessfullyActivity