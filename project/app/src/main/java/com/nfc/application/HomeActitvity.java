package com.nfc.application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActitvity extends BaseActivity {
    Button logout;
    Button information;
    SharedPreferences preferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_home);
        logout= findViewById(R.id.btn_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击注销按键后调用LoginActivity提供的resetSprfMain()方法执行editorMain.putBoolean("main",false);，即将"main"对应的值修改为false

                SharedPreferences.Editor editor=preferences.edit();
                editor.putBoolean("main",false);
                editor.apply();
                Intent intent=new Intent(HomeActitvity.this,LoginActivity.class);
                startActivity(intent);
                HomeActitvity.this.finish();
            }
        });

        information = findViewById(R.id.btn_information);
        information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击注销按键后调用LoginActivity提供的resetSprfMain()方法执行editorMain.putBoolean("main",false);，即将"main"对应的值修改为false
                Intent intent=new Intent(HomeActitvity.this,InformationActivity.class);
                startActivity(intent);
            }
        });
    }


    }

