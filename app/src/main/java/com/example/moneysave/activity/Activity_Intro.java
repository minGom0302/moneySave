package com.example.moneysave.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.example.moneysave.R;
import com.example.moneysave.db.DBHelper;

import java.util.Objects;

public class Activity_Intro extends AppCompatActivity {

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Objects.requireNonNull(getSupportActionBar()).hide();

        boolean first = sp.getBoolean("firstStart", true);

        if(first) {
            // db 및 table 생성
            SharedPreferences.Editor sp_e = sp.edit();
            DBHelper helper = new DBHelper(Activity_Intro.this);
            SQLiteDatabase db = helper.getWritableDatabase();
            helper.onCreate(db);

            ContentValues values = new ContentValues();
            String[] bankNameArray = {"전체", "KB국민은행", "KB증권", "카카오뱅크", "신한은행", "IBK기업은행", "우리은행", "하나은행", "케이뱅크", "새마을금고", "SC제일은행", "NH농협은행", "신협"};
            for (int i = 1; i < bankNameArray.length; i++) {
                String bankCode;
                if (i < 10) {
                    bankCode = "0" + i;
                } else {
                    bankCode = String.valueOf(i);
                }

                String bankName = bankNameArray[i];

                values.clear();
                values.put("ver", "0");
                values.put("bankName", bankName);
                values.put("bankCode", bankCode);
                db.insert("bankCodeInfo", null, values);
            }

            values.clear();
            values.put("ver", "0");
            values.put("bankName", "기타");
            values.put("bankCode", "99");
            db.insert("bankCodeInfo", null, values);

            db.close();
            sp_e.putBoolean("firstStart", false);
            sp_e.apply();
        }


        new Handler().postDelayed(() -> {
            Intent intent = new Intent(Activity_Intro.this, Activity_Main.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}