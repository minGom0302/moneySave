package com.example.moneysave.popup;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneysave.R;
import com.example.moneysave.etc.UseItem;

import java.text.DecimalFormat;

public class Popup_Goal extends Activity {
    TextView goalMoneyTv;
    EditText goalMoneyEt;

    SharedPreferences sp;
    SharedPreferences.Editor sp_e;
    DecimalFormat format = UseItem.getCommaFormat();

    String result = "";
    SQLiteDatabase db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup_goal);

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sp_e = sp.edit();

        goalMoneyTv = findViewById(R.id.goal_goalMoneyTv);
        goalMoneyEt = findViewById(R.id.goal_goalMoneyEt);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(result)){

                    result = format.format(Double.parseDouble(charSequence.toString().replaceAll(",","")));
                    goalMoneyEt.setText(result);
                    goalMoneyEt.setSelection(result.length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        goalMoneyEt.addTextChangedListener(textWatcher);

        Button registerBtn = findViewById(R.id.goal_registerBtn);
        Button cancelBtn = findViewById(R.id.goal_cancelBtn);

        registerBtn.setOnClickListener(v -> register());
        cancelBtn.setOnClickListener(v -> finish());

        setLayout();
    }

    private void setLayout() {
        String s1 = sp.getString("goalMoney", "0");
        String goalMoney = UseItem.comma(s1);
        goalMoneyTv.setText(goalMoney);
    }

    @SuppressLint("Recycle")
    private void register() {
        Log.i("TAG", String.valueOf(goalMoneyEt.getText().length()));
        if(goalMoneyEt.getText().length() > 0) {
            // 내부 저장소 작업
            String s1 = goalMoneyEt.getText().toString();
            String s2 = s1.replaceAll(",", "");
            boolean goalBoolean = sp.getBoolean("goalBoolean", true);

            sp_e.putString("goalMoney", s2);
            sp_e.commit();

            // DB 저장, 회차랑 목표금액
            String s3 = sp.getString("ver", "1");
            db = openOrCreateDatabase("saveMoney.db", MODE_PRIVATE, null);

            String sql1 = "select * from verInfo where ver = " + s3 + ";";
            Cursor c1 = db.rawQuery(sql1, null);
            String sql;

            if(c1.getCount() == 0) {
                ContentValues values = new ContentValues();
                values.put("ver", s3);
                db.insert("verInfo",null, values);
                sql = "insert into goalinfo (ver, goalMoney) values (" + s3 + ", " + s2 + ");";
            } else {
                sql = "update goalInfo set goalMoney = " + s2 + " where ver = " + s3 + ";";
            }

            db.execSQL(sql);
            db.close();

            finish();
        } else {
            Toast.makeText(this, "금액을 입력해주시기 바랍니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return event.getAction() != MotionEvent.ACTION_OUTSIDE;
    }
}