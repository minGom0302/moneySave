package com.example.moneysave.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.moneysave.R;
import com.example.moneysave.etc.Backspace;
import com.example.moneysave.etc.UseItem;
import com.example.moneysave.popup.Popup_Goal;

import java.util.Objects;

public class Activity_Main extends AppCompatActivity {
    SharedPreferences sp;
    SharedPreferences.Editor sp_e;
    SQLiteDatabase db;
    Backspace backspace = new Backspace(Activity_Main.this);

    TextView goalMoneyTv, nowMoneyTv, nowMoneyPercentTv;
    ProgressBar nowPb;
    Button moneySetBtn, endBtn;
    ImageView goalImgView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).hide();

        db = openOrCreateDatabase("saveMoney.db", MODE_PRIVATE, null);

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sp_e = sp.edit();

        goalMoneyTv = findViewById(R.id.main_goalMoneyTv);
        nowMoneyTv = findViewById(R.id.main_nowMoneyTv);
        nowMoneyPercentTv = findViewById(R.id.main_nowMoneyPercent);
        nowPb = findViewById(R.id.main_nowMoneyPb);
        goalImgView = findViewById(R.id.main_nowImg);

        Button goalSetBnt = findViewById(R.id.main_goalSetBtn);
        Button statisticBtn = findViewById(R.id.main_statisticBtn);
        moneySetBtn = findViewById(R.id.main_moneySetBtn);
        endBtn = findViewById(R.id.main_endBtn);

        goalSetBnt.setOnClickListener(v -> changeLayout(0));
        moneySetBtn.setOnClickListener(v -> changeLayout(1));
        statisticBtn.setOnClickListener(v -> changeLayout(2));
        endBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(Activity_Main.this);
            builder.setTitle("안내").setMessage("등록된 데이터로 마감처리 하시겠습니까?");
            builder.setPositiveButton("예", ((dialog, which) -> saveEnd()));
            builder.setNeutralButton("아니오", (dialog, which) -> {});
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        layoutSetting();
    }

    @Override
    protected void onResume() {
        super.onResume();
        layoutSetting();
    }

    @SuppressLint("DefaultLocale")
    private void layoutSetting() {
        String s1 = sp.getString("goalMoney", "0");
        String s2 = sp.getString("nowMoney", "0");
        String goalMoney = UseItem.comma(s1);
        String nowMoney = UseItem.comma(s2);
        goalMoneyTv.setText(goalMoney);
        nowMoneyTv.setText(nowMoney);

        int i1 = Integer.parseInt(s1);
        int i2 = Integer.parseInt(s2);
        double d = (double) i2 / i1 * 100;
        String percent;
        String setPercent;
        if(i1 == 0) {
            d = 0;
            percent = "0";
            setPercent = "0";
        } else {
            percent = String.format("%.0f", d);
            setPercent = String.format("%.1f", d);
        }
        nowMoneyPercentTv.setText(setPercent);

        nowPb.setIndeterminate(false);
        nowPb.setProgress(Integer.parseInt(percent));

        if(d == 0) {
            goalImgView.setImageResource(R.drawable.icon_img006);
        } else if(d < 25) {
            goalImgView.setImageResource(R.drawable.icon_img001);
        } else if(d < 50) {
            goalImgView.setImageResource(R.drawable.icon_img002);
        } else if(d < 70) {
            goalImgView.setImageResource(R.drawable.icon_img003);
        } else if(d < 100) {
            goalImgView.setImageResource(R.drawable.icon_img004);
        } else {
            goalImgView.setImageResource(R.drawable.icon_img005);
        }

        if(!s1.equals("0")) {
            moneySetBtn.setEnabled(true);
            endBtn.setEnabled(true);
        } else {
            moneySetBtn.setEnabled(false);
            endBtn.setEnabled(false);
        }
    }

    private void changeLayout(int cnd) {
        Intent intent = null;
        switch (cnd) {
            case 0:
                intent = new Intent(this, Popup_Goal.class);
                break;
            case 1:
                intent = new Intent(this, Activity_Register.class);
                break;
            case 2:
                intent = new Intent(this, Activity_Statistic.class);
                break;
        }
        db.close();
        startActivity(intent);
    }

    private void saveEnd() {
        db = openOrCreateDatabase("saveMoney.db", MODE_PRIVATE, null);
        String s1 = sp.getString("ver", "1");
        String goalMoney = sp.getString("goalMoney", "0");
        String nowMoney = sp.getString("nowMoney", "0");

        // DB 작업 : 최종적으로 목표금액과 저축금액 저장
        ContentValues values = new ContentValues();
        values.put("ver", s1);
        values.put("goalMoney", goalMoney);
        db.insert("goalInfo",null, values);
        values.clear();
        values.put("ver", s1);
        values.put("nowMoney", nowMoney);
        db.insert("nowMoneyInfo", null, values);

        // 내부 저장소 작업 : 돈 0으로 초기화하고 버전 업
        int i1 = Integer.parseInt(s1) + 1;

        sp_e.putString("goalMoney", "0");
        sp_e.putString("nowMoney", "0");
        sp_e.putString("ver", String.valueOf(i1));

        sp_e.commit();

        layoutSetting();
    }

    @Override
    public void onBackPressed() {
        backspace.onBackPressed("'뒤로가기'를 한번 더 누르면 종료됩니다.");
        db.close();
    }
}