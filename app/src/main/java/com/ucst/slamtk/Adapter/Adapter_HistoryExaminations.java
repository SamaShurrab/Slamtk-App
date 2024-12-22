package com.ucst.slamtk.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.ucst.slamtk.Class.HistoryExaminations;
import com.ucst.slamtk.R;

import java.util.ArrayList;

public class Adapter_HistoryExaminations extends BaseAdapter {

    Context context;
    ArrayList<String> sampleDateList;
    ArrayList<String> sampleTimeList;
    ArrayList<String> riskTypeList;
    ArrayList<String> recommendations1;
    ArrayList<String> recommendations2;
    ArrayList<String> recommendations3;
    ArrayList<String> bilirubinRateList;
    LayoutInflater layoutInflater;

    public Adapter_HistoryExaminations(Context context,ArrayList<String> sampleDateList,ArrayList<String> sampleTimeList,ArrayList<String> riskTypeList,ArrayList<String> recommendations1,ArrayList<String> recommendations2,ArrayList<String> recommendations3,ArrayList<String> bilirubinRateList){
        this.context=context;
        this.sampleDateList=sampleDateList;
        this.sampleTimeList=sampleTimeList;
        this.riskTypeList=riskTypeList;
        this.bilirubinRateList=bilirubinRateList;
        this.recommendations1=recommendations1;
        this.recommendations2=recommendations2;
        this.recommendations3=recommendations3;
        layoutInflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {

        return sampleDateList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view=layoutInflater.inflate(R.layout.historyexaminations_data,viewGroup,false);
        TextView sampleDate=view.findViewById(R.id.sampleDate);
        TextView sampleTime=view.findViewById(R.id.sampleTime);
        TextView bilirubinRate=view.findViewById(R.id.bili_rate);
        TextView riskType=view.findViewById(R.id.riskType);
        TextView recommindation1=view.findViewById(R.id.Recommendation1);
        TextView recommindation2=view.findViewById(R.id.Recommendation2);
        TextView recommindation3=view.findViewById(R.id.Recommendation3);

        sampleDate.append(" "+sampleDateList.get(position));
        sampleTime.setText(sampleTimeList.get(position));
        bilirubinRate.append(" "+" Mmol/L"+bilirubinRateList.get(position));
        riskType.append(" "+riskTypeList.get(position));
        recommindation1.setText(recommendations1.get(position));
        recommindation2.setText(recommendations2.get(position));
        recommindation3.setText(recommendations3.get(position));
        return view;
    }
}//Adapter_HistoryExaminations
