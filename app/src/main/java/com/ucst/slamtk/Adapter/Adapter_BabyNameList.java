package com.ucst.slamtk.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ucst.slamtk.Class.BabyInformation;
import com.ucst.slamtk.R;

import java.util.ArrayList;

public class Adapter_BabyNameList extends BaseAdapter {

    Context context;
    ArrayList<String> babyNameList;

    LayoutInflater layoutInflater;

    public Adapter_BabyNameList(Context context, ArrayList<String> babyNameList){
        this.context=context;
        this.babyNameList=babyNameList;
        layoutInflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return babyNameList.size();
    }

    @Override
    public Object getItem(int position) {
        return babyNameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view=layoutInflater.inflate(R.layout.listview_babyname,viewGroup,false);
        TextView babyName=view.findViewById(R.id.baby_name_Tv);
        babyName.setText(babyNameList.get(position));
        return view;
    }

}//Adapter_BabyNameList
