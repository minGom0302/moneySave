package com.example.moneysave.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.moneysave.R;
import com.example.moneysave.adapter.BankAdapter;
import com.example.moneysave.adapter.CursorAdapter;
import com.example.moneysave.adapter.RoundAdapter;
import com.example.moneysave.dto.DTO_zip;
import com.example.moneysave.dto.SaveInfoDTO;
import com.example.moneysave.etc.UseItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Activity_Statistic extends AppCompatActivity {
    TextView goalMoneyTv, saveMoneyTv, memo01Tv, memo02Tv;
    Spinner roundSpinner, dateSpinner, bankSpinner;
    RecyclerView recyclerView;

    SharedPreferences sp;
    SQLiteDatabase db_r;

    BankAdapter bankAdapter;
    CursorAdapter cursorAdapter;
    RoundAdapter roundAdapter, dateAdapter;
    DTO_zip bankSelectedDTO, roundSelectedDTO, dateSelectedDTO;

    String mVer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        Objects.requireNonNull(getSupportActionBar()).hide();

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        db_r = openOrCreateDatabase("saveMoney.db", MODE_PRIVATE, null);

        mVer = sp.getString("ver", "1");

        goalMoneyTv = findViewById(R.id.statistic_goalMoneyTv);
        saveMoneyTv = findViewById(R.id.statistic_saveMoneyTv);
        memo01Tv = findViewById(R.id.statistic_memoTv);
        memo02Tv = findViewById(R.id.statistic_memo02Tv);
        roundSpinner = findViewById(R.id.statistic_spinnerRound);
        dateSpinner = findViewById(R.id.statistic_spinnerDate);
        bankSpinner = findViewById(R.id.statistic_spinnerBank);
        recyclerView = findViewById(R.id.statistic_recyclerView);

        Button searchBtn = findViewById(R.id.statistic_searchBtn);
        searchBtn.setOnClickListener(v -> search());

        setLayout();
    }

    @SuppressLint("Recycle")
    private void setLayout() {
        /************************** 은행 설정 ************************/
        String sql1 = "select * from bankCodeInfo where ver = " + mVer + " group by bankCode order by bankCode;";
        Cursor c1 = db_r.rawQuery(sql1, null);

        List<DTO_zip> bankList = new ArrayList<>();

        DTO_zip z = new DTO_zip(mVer, "00", "전체");
        bankList.add(z);
        while(c1.moveToNext()) {
            String dataVer = c1.getString(0);
            String dataBankName = c1.getString(1);
            String dataBankCode = c1.getString(2);

            DTO_zip zip = new DTO_zip(dataVer, dataBankCode, dataBankName);
            bankList.add(zip);
        }

        bankAdapter = new BankAdapter(Activity_Statistic.this, bankList);
        bankSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bankSelectedDTO = bankList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        bankSpinner.setDropDownVerticalOffset(85);
        bankSpinner.setAdapter(bankAdapter);

        /************************ 회차 설정 **********************/
        List<DTO_zip> roundList = new ArrayList<>();

        String sql2 = "select * from verInfo;";
        Cursor c2 = db_r.rawQuery(sql2, null);

        while(c2.moveToNext()) {
            String dataRound = c2.getString(0);

            DTO_zip zip = new DTO_zip(dataRound);
            roundList.add(zip);
        }

        roundAdapter = new RoundAdapter(Activity_Statistic.this, roundList, 0);
        roundSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                roundSelectedDTO = roundList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        roundSpinner.setDropDownVerticalOffset(85);
        roundSpinner.setAdapter(roundAdapter);

        /************************ 월 설정 **********************/
        List<DTO_zip> dateList = new ArrayList<>();

        String sql3 = "select * from dateInfo where ver = " + mVer + " group by date order by date;";
        Cursor c3 = db_r.rawQuery(sql3, null);

        DTO_zip zipETC = new DTO_zip(mVer, "전체");
        dateList.add(zipETC);
        while(c3.moveToNext()) {
            String dataVer = c3.getString(0);
            String dataDate = c3.getString(1);

            DTO_zip zip = new DTO_zip(dataVer, dataDate);
            dateList.add(zip);
        }

        dateAdapter = new RoundAdapter(Activity_Statistic.this, dateList, 1);
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dateSelectedDTO = dateList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dateSpinner.setDropDownVerticalOffset(85);
        dateSpinner.setAdapter(dateAdapter);
    }

    @SuppressLint("Recycle")
    private void search() {
        String round = roundSelectedDTO.getRound().substring(0, 1);
        String sql1 = "select * from goalInfo where ver = " + round + ";";
        Cursor c1 = db_r.rawQuery(sql1, null);

        c1.moveToLast();
        String text = c1.getString(1);
        goalMoneyTv.setText(UseItem.comma(text));

        String bankCode = bankSelectedDTO.getBankCode(); // 01 or 00
        String date = dateSelectedDTO.getOriginalDate(); // 202205 or 전체

        String sql2 = "select a.*, b.bankName from saveInfo a "
                + "left join bankCodeInfo b on a.bankCode = b.bankCode and b.ver = 0"
                + " where a.ver = " + round;
        if(!date.equals("전체")) {
            int i1 = Integer.parseInt(date) * 100;
            int i2 = i1 + 100;
            sql2 += " and " + i1 + " < a.date and a.date < " + i2;
        }
        if(!bankCode.equals("00")) {
            sql2 += " and a.bankCode = '" + bankCode + "'";
        }
        sql2 += " order by a.date desc;";
        Cursor c2 = db_r.rawQuery(sql2, null);

        memo02Tv.setVisibility(View.GONE);
        if(c2.getCount() == 0) {
            memo01Tv.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }

        int total = 0;

        List<SaveInfoDTO> dtoList = new ArrayList<>();
        while(c2.moveToNext()){
            // row 별로 조회를 하는데 칼럼 명으로 가져오는게 아니고 칼럼 인덱스(0부터 ~ )로 가져옴
            int id = c2.getInt(0);
            String dataDate = c2.getString(1);
            String dataMoney = c2.getString(2);
            String dataMemo = c2.getString(3);
            String dataVer = c2.getString(4);
            String dataBank = c2.getString(6);
            String bankCodeData = c2.getString(5);
            Log.i("TAG", bankCodeData);

            total += Integer.parseInt(dataMoney);

            SaveInfoDTO dto = new SaveInfoDTO(id, dataDate, dataMoney, dataMemo, dataVer, dataBank);
            dtoList.add(dto);
        }

        cursorAdapter = new CursorAdapter(dtoList, getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(Activity_Statistic.this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(cursorAdapter);
        recyclerView.setVisibility(View.VISIBLE);

        saveMoneyTv.setText(UseItem.comma(String.valueOf(total)));
    }
}