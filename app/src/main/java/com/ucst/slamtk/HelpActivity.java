package com.ucst.slamtk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class HelpActivity extends AppCompatActivity {
    ImageView arrowDownImg1,arrowDownImg2,arrowDownImg3,arrowDownImg4,arrowDownImg5,arrowDownImg6,arrowDownImg7,arrowDownImg8,imageView_arrow;
    TextView helpAnswerTv1,helpAnswerTv2,helpAnswerTv3,helpAnswerTv4,helpAnswerTv5,helpAnswerTv6,helpAnswerTv7,helpAnswerTv8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        //        Change Icons In Status Bar
        View decor= HelpActivity.this.getWindow().getDecorView();
        if(decor.getSystemUiVisibility() != View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR){
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }//if
        else{
            decor.setSystemUiVisibility(0);
        }//else

//    Associate components in the XML file with an object in the java
        helpAnswerTv1=findViewById(R.id.helpAnswerTv1);
        helpAnswerTv2=findViewById(R.id.helpAnswerTv2);
        helpAnswerTv3=findViewById(R.id.helpAnswerTv3);
        helpAnswerTv4=findViewById(R.id.helpAnswerTv4);
        helpAnswerTv5=findViewById(R.id.helpAnswerTv5);
        helpAnswerTv6=findViewById(R.id.helpAnswerTv6);
        helpAnswerTv7=findViewById(R.id.helpAnswerTv7);
        helpAnswerTv8=findViewById(R.id.helpAnswerTv8);
        arrowDownImg1=findViewById(R.id.arrowDownImg1);
        arrowDownImg2=findViewById(R.id.arrowDownImg2);
        arrowDownImg3=findViewById(R.id.arrowDownImg3);
        arrowDownImg4=findViewById(R.id.arrowDownImg4);
        arrowDownImg5=findViewById(R.id.arrowDownImg5);
        arrowDownImg6=findViewById(R.id.arrowDownImg6);
        arrowDownImg7=findViewById(R.id.arrowDownImg7);
        arrowDownImg8=findViewById(R.id.arrowDownImg8);
        imageView_arrow=findViewById(R.id.imageView_arrow);

        imageView_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HelpActivity.this,AccountActivity.class);
                startActivity(intent);
            }
        });


        arrowDownImg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(helpAnswerTv1.getVisibility() == View.GONE){
                    helpAnswerTv1.setVisibility(View.VISIBLE);
                    arrowDownImg1.setImageResource(R.drawable.arrow_top);
                }else{
                    helpAnswerTv1.setVisibility(View.GONE);
                    arrowDownImg1.setImageResource(R.drawable.arrow_down);
                }//else
            }//onClick()
        });

        arrowDownImg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(helpAnswerTv2.getVisibility() == View.GONE){
                    helpAnswerTv2.setVisibility(View.VISIBLE);
                    arrowDownImg2.setImageResource(R.drawable.arrow_top);
                }else{
                    helpAnswerTv2.setVisibility(View.GONE);
                    arrowDownImg2.setImageResource(R.drawable.arrow_down);
                }//else
            }//onClick()
        });

        arrowDownImg3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(helpAnswerTv3.getVisibility() == View.GONE){
                    helpAnswerTv3.setVisibility(View.VISIBLE);
                    arrowDownImg3.setImageResource(R.drawable.arrow_top);
                }else{
                    helpAnswerTv3.setVisibility(View.GONE);
                    arrowDownImg3.setImageResource(R.drawable.arrow_down);
                }//else
            }//onClick()
        });

        arrowDownImg4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(helpAnswerTv4.getVisibility() == View.GONE){
                    helpAnswerTv4.setVisibility(View.VISIBLE);
                    arrowDownImg4.setImageResource(R.drawable.arrow_top);
                }else{
                    helpAnswerTv4.setVisibility(View.GONE);
                    arrowDownImg4.setImageResource(R.drawable.arrow_down);
                }//else
            }//onClick()
        });

        arrowDownImg5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(helpAnswerTv5.getVisibility() == View.GONE){
                    helpAnswerTv5.setVisibility(View.VISIBLE);
                    arrowDownImg5.setImageResource(R.drawable.arrow_top);
                }else{
                    helpAnswerTv5.setVisibility(View.GONE);
                    arrowDownImg5.setImageResource(R.drawable.arrow_down);
                }//else
            }//onClick()
        });

        arrowDownImg6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(helpAnswerTv6.getVisibility() == View.GONE){
                    helpAnswerTv6.setVisibility(View.VISIBLE);
                    arrowDownImg6.setImageResource(R.drawable.arrow_top);
                }else{
                    helpAnswerTv6.setVisibility(View.GONE);
                    arrowDownImg6.setImageResource(R.drawable.arrow_down);
                }//else
            }//onClick()
        });

        arrowDownImg7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(helpAnswerTv7.getVisibility() == View.GONE){
                    helpAnswerTv7.setVisibility(View.VISIBLE);
                    arrowDownImg7.setImageResource(R.drawable.arrow_top);
                }else{
                    helpAnswerTv7.setVisibility(View.GONE);
                    arrowDownImg7.setImageResource(R.drawable.arrow_down);
                }//else
            }//onClick()
        });
        arrowDownImg8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(helpAnswerTv8.getVisibility() == View.GONE){
                    helpAnswerTv8.setVisibility(View.VISIBLE);
                    arrowDownImg8.setImageResource(R.drawable.arrow_top);
                }else{
                    helpAnswerTv8.setVisibility(View.GONE);
                    arrowDownImg8.setImageResource(R.drawable.arrow_down);
                }//else
            }//onClick()
        });
    }//HelpActivity
}//onCreate