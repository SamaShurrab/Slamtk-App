package com.ucst.slamtk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.ucst.slamtk.Adapter.Adapter_BabyNameList;

import java.util.ArrayList;

public class  HistoryExaminationsActivity extends AppCompatActivity {
    ListView baby_names_listview;
    ArrayList<String> babyNameList;
    //    create object of FirebaseFirestore class of access Cloud Firestore
    FirebaseFirestore firebaseFirestore=FirebaseFirestore.getInstance();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ArrayList<String>nameList;
    ArrayList<Integer>idList;
    ImageView imageView_account;
    TextView userName;
    LinearLayout historyExamination,alert;
    int babyId;
    ImageView move;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_examinations);

        //        create object from SharedPreferences to get id
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(HistoryExaminationsActivity.this);
        editor=sharedPreferences.edit();

        //        Change Icons In Status Bar
        View decor= HistoryExaminationsActivity.this.getWindow().getDecorView();
        if(decor.getSystemUiVisibility() != View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR){
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }//if
        else{
            decor.setSystemUiVisibility(0);
        }//else

//    Associate components in the XML file with an object in the java
        baby_names_listview=findViewById(R.id.baby_names_listview);
        imageView_account=findViewById(R.id.imageView_account);
        userName=findViewById(R.id.textView_userName);
        historyExamination=findViewById(R.id.historyExamination);
        alert=findViewById(R.id.historyExamination_alert);
        move=findViewById(R.id.move);

        imageView_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HistoryExaminationsActivity.this,AccountActivity.class);
                startActivity(intent);
            }
        });
        nameList=new ArrayList<>();
        idList=new ArrayList<>();
        getName(getId());
        babyNameList=new ArrayList<>();
        getBabyName(babyNameList,getId());
        onStartMethod();

        baby_names_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String babyName= (String) baby_names_listview.getItemAtPosition(position);
                if(checkInternet()){
                    firebaseFirestore.collection("BabyInformation").whereEqualTo("babyName",babyName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                QuerySnapshot querySnapshot=task.getResult();
                                if(querySnapshot!=null){
                                    for(DocumentSnapshot documentSnapshot:querySnapshot.getDocuments()){
                                        babyId=Integer.parseInt(documentSnapshot.get("babyId").toString());
                                        Intent intent=new Intent(HistoryExaminationsActivity.this,HistoryExaminationsDataActivity.class);
                                        intent.putExtra("babyNAme",babyNameList.get(position));
                                        intent.putExtra("babyId",babyId);
                                        startActivity(intent);
                                    }
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }else{
                    Toast.makeText(HistoryExaminationsActivity.this, getResources().getString(R.string.noIntrnet), Toast.LENGTH_SHORT).show();
                }
            }//onItemClick()
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

    public void getBabyName(ArrayList<String>babyNameList,int parentId){
        if(checkInternet()){
            firebaseFirestore.collection("BabyInformation").whereEqualTo("parentId",String.valueOf(parentId)).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        QuerySnapshot querySnapshot=task.getResult();
                        if(querySnapshot!=null){
                            for(DocumentSnapshot documentSnapshot:querySnapshot.getDocuments()){
                                babyNameList.add(documentSnapshot.getString("babyName"));
                            }
                            Adapter_BabyNameList adapter_babyNameList=new Adapter_BabyNameList(HistoryExaminationsActivity.this,babyNameList);
                            baby_names_listview.setAdapter(adapter_babyNameList);
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }else{
            Toast.makeText(this, getResources().getString(R.string.noIntrnet), Toast.LENGTH_SHORT).show();
        }
    }//getBabyName()

    public int getId(){
        return  sharedPreferences.getInt("id",0);
    }//getId()

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

    public void onStartMethod(){
        new Handler().postAtTime(new Runnable() {
            @Override
            public void run() {
                alert.setVisibility(View.VISIBLE);
                historyExamination.setVisibility(View.GONE);
                Animation animation = AnimationUtils.loadAnimation(HistoryExaminationsActivity.this, R.anim.zoom_in_dialog);
                alert.startAnimation(animation);
            }
        },500);
        move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        alert.setVisibility(View.GONE);
                        historyExamination.setVisibility(View.VISIBLE);
                        Animation animation = AnimationUtils.loadAnimation(HistoryExaminationsActivity.this, R.anim.slide_in);
                        historyExamination.startAnimation(animation);

            }
        });

    }//onStartMethod()

}//HistoryExaminationsActivity()

