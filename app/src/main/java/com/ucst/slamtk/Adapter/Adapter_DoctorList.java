package com.ucst.slamtk.Adapter;

import static androidx.core.app.ActivityCompat.requestPermissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.core.content.ContextCompat;

import com.ucst.slamtk.R;

import java.util.ArrayList;

public class Adapter_DoctorList extends BaseAdapter {

    Context context;
    ArrayList<String> doctorNameList;
    ArrayList<String> doctorSpecializationList;
    ArrayList<String> doctorJobTitleList;
    ArrayList<String> doctorPhoneList;
    LayoutInflater layoutInflater;

    public Adapter_DoctorList(Context context, ArrayList<String> doctorNameList,ArrayList<String> doctorSpecializationList,ArrayList<String> doctorJobTitleList,ArrayList<String> doctorPhoneList){
        this.context=context;
        this.doctorNameList=doctorNameList;
        this.doctorSpecializationList=doctorSpecializationList;
        this.doctorJobTitleList=doctorJobTitleList;
        this.doctorPhoneList=doctorPhoneList;
        layoutInflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return doctorNameList.size();
    }

    @Override
    public Object getItem(int position) {
        return doctorNameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view=layoutInflater.inflate(R.layout.listview_doctor,viewGroup,false);

        TextView doctorName=view.findViewById(R.id.doctor_name);
        doctorName.append(" "+doctorNameList.get(position));

        TextView doctor_Specialization=view.findViewById(R.id.doctor_Specialization);
        doctor_Specialization.append(" "+doctorSpecializationList.get(position));

        TextView doctor_JobTitle=view.findViewById(R.id.doctor_JobTitle);
        doctor_JobTitle.append(" "+doctorJobTitleList.get(position));

        ImageView whatsapp=view.findViewById(R.id.whatsapp);
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://api.whatsapp.com/send?phone=" + doctorPhoneList.get(position);
                try {
                    PackageManager pm = context.getApplicationContext().getPackageManager();
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    context.startActivity(i);
                } catch (PackageManager.NameNotFoundException e) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
            }
        });

        ImageView phone=view.findViewById(R.id.phone);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+doctorPhoneList.get(position)));
                context.startActivity(intent);
            }
        });

        ImageView message=view.findViewById(R.id.message);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(context,Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ){
                        requestPermissions((Activity) context,new String[]{Manifest.permission.SEND_SMS}, 0x01);
                }
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("sms:"+doctorPhoneList.get(position)));
                context.startActivity(intent);
            }
        });
        return view;
    }
}
