package com.example.moneysave.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneysave.R;
import com.example.moneysave.adapter.BankAdapter;
import com.example.moneysave.adapter.CursorAdapter;
import com.example.moneysave.db.DBHelper;
import com.example.moneysave.dto.DTO_zip;
import com.example.moneysave.dto.SaveInfoDTO;
import com.example.moneysave.etc.UseItem;

import java.sql.Date;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class Activity_Register extends AppCompatActivity {
    TextView goalMoneyTv, nowMoneyTv, begoTv, dayTv, memoTv;
    EditText registerEt;
    RecyclerView recyclerView;
    LinearLayout layout;
    Spinner spinner;

    SharedPreferences sp;
    SharedPreferences.Editor sp_e;

    String result = "";
    String sGoalMoney;
    String sNowMoney;
    String sVer;

    DecimalFormat format = UseItem.getCommaFormat();
    CursorAdapter cursorAdapter;
    BankAdapter bankAdapter;
    InputMethodManager imm;

    SQLiteDatabase db, db_r;
    DBHelper dbHelper;
    DTO_zip selectedDTO;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Objects.requireNonNull(getSupportActionBar()).hide();

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sp_e = sp.edit();

        // DB 쓰기 권한 획득
        dbHelper = new DBHelper(getApplicationContext());
        db = openOrCreateDatabase("saveMoney.db", MODE_PRIVATE, null);
        db_r = dbHelper.getReadableDatabase();

        goalMoneyTv = findViewById(R.id.register_goalMoneyTv);
        nowMoneyTv = findViewById(R.id.register_nowMoneyTv);
        begoTv = findViewById(R.id.register_begoTv);
        dayTv = findViewById(R.id.register_dayTv);
        memoTv = findViewById(R.id.register_memoTv);
        registerEt = findViewById(R.id.register_registerEt);
        recyclerView = findViewById(R.id.register_recyclerView);
        layout = findViewById(R.id.register_layout);
        spinner = findViewById(R.id.register_spinner);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(result)){

                    result = format.format(Double.parseDouble(charSequence.toString().replaceAll(",","")));
                    registerEt.setText(result);
                    registerEt.setSelection(result.length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        registerEt.addTextChangedListener(textWatcher);
        layout.setOnClickListener(v -> imm.hideSoftInputFromWindow(layout.getWindowToken(), 0));

        Button dayChangeBtn = findViewById(R.id.register_dayChangeBtn);
        Button registerBtn = findViewById(R.id.register_registerBtn);

        dayChangeBtn.setOnClickListener(v -> choiceDay());
        registerBtn.setOnClickListener(v -> showDialog());

        setLayout();
    }

    @SuppressLint({"Recycle", "NotifyDataSetChanged"})
    private void setLayout() {
        sGoalMoney = sp.getString("goalMoney", "0");
        sNowMoney = sp.getString("nowMoney", "0");
        sVer = sp.getString("ver", "1");

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        Log.i("TAG", String.valueOf(date));
        dayTv.setText(String.valueOf(date));

        String goalMoney = UseItem.comma(sGoalMoney);
        String nowMoney = UseItem.comma(sNowMoney);
        goalMoneyTv.setText(goalMoney);
        nowMoneyTv.setText(nowMoney);
        memoTv.setText("");
        registerEt.setText("0");

        // DB에서 데이터 조회
        String sql = "select a.*, b.bankName from saveInfo a "
                + "left join bankCodeInfo b on a.bankCode = b.bankCode and b.ver = 0 "
                + "where a.ver = " + sVer + " order by a.date desc;";
        Cursor c = db_r.rawQuery(sql, null);
        Log.i("TAG", "first cursor count : " + c.getCount());
        // 조회된 갯수 확인
        if(c.getCount() == 0) {
            begoTv.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            begoTv.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            // 데이터를 가지고 정리하여 RecyclerView 셋팅
            List<SaveInfoDTO> dtoList = new ArrayList<>();
            while(c.moveToNext()){
                // row 별로 조회를 하는데 칼럼 명으로 가져오는게 아니고 칼럼 인덱스(0부터 ~ )로 가져옴
                int id = c.getInt(0);
                String dataDate = c.getString(1);
                String dataMoney = c.getString(2);
                String dataMemo = c.getString(3);
                String dataVer = c.getString(4);
                String dataBank = c.getString(6);

                SaveInfoDTO dto = new SaveInfoDTO(id, dataDate, dataMoney, dataMemo, dataVer, dataBank);
                dtoList.add(dto);
            }
            cursorAdapter = new CursorAdapter(dtoList, getApplicationContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(Activity_Register.this, RecyclerView.VERTICAL, false));
            recyclerView.setAdapter(cursorAdapter);
        }

        // DB에서 데이터 조회 후 spinner 설정
        String sql2 = "select * from bankCodeInfo where ver = 0;";
        Cursor c2 = db_r.rawQuery(sql2, null);
        Log.i("TAG", "second cursor count : " + c2.getCount());

        List<DTO_zip> dtoZipList = new ArrayList<>();

        while(c2.moveToNext()) {
            String dataVer = c2.getString(0);
            String dataBankName = c2.getString(1);
            String dataBankCode = c2.getString(2);
            Log.i("TAG", "c2 : " + dataBankCode + " / " + dataBankName);

            DTO_zip zip = new DTO_zip(dataVer, dataBankCode, dataBankName);
            dtoZipList.add(zip);
        }

        bankAdapter = new BankAdapter(Activity_Register.this, dtoZipList);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDTO = dtoZipList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setDropDownVerticalOffset(85);
        spinner.setAdapter(bankAdapter);
    }

    private void choiceDay() {
        imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);

        // 달력 객체 얻기
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR); // 년
        int mMonth = calendar.get(Calendar.MONTH); // 월
        int mDay = calendar.get(Calendar.DAY_OF_MONTH); // 일

        DatePickerDialog datePickerDialog = new DatePickerDialog(Activity_Register.this, (view, year, month, day) -> {
            String m = String.valueOf(month + 1);
            String d = String.valueOf(day);
            if(month < 10) {
                m = "0" + m;
            }
            if(day < 10) {
                d = "0" + d;
            }
            String date = year + "-" + m + "-" + d;
            dayTv.setText(date);
        }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    private void showDialog() {
        imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("안내").setMessage("입력한 정보로 저장하시겠습니까?");
        builder.setPositiveButton("예", (dialog, which) -> register());
        builder.setNegativeButton("아니오", (dialog, which) -> {});
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void register() {
        if(selectedDTO.getBankCode().equals("00")) {
            Toast.makeText(this, "은행을 선택해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = dayTv.getText().toString().replaceAll("-", "");
        String strInsertMoney = registerEt.getText().toString().replaceAll(",", "");
        String memo = memoTv.getText().toString();

        if(memo.length() < 1) {
            memo = "메모 없음";
        }

        int insertMoney = Integer.parseInt(strInsertMoney);
        int iNow = Integer.parseInt(sNowMoney);
        iNow += insertMoney;

        // 내부 저장소 저장 : 현재 저축액만
        sp_e.putString("nowMoney", String.valueOf(iNow));
        sp_e.commit();

        // DB 작업 : 금액 저장
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("money", strInsertMoney);
        values.put("ver", sVer);
        values.put("memo", memo);
        values.put("bankCode", selectedDTO.getBankCode());
        db.insert("saveInfo",null, values);

        // date 저장
        values.clear();
        values.put("ver", sVer);
        values.put("date", date.substring(0, 6));
        db.insert("dateInfo", null, values);

        // backInfo 저장
        values.clear();
        values.put("ver", sVer);
        values.put("bankCode", selectedDTO.getBankCode());
        values.put("bankName", selectedDTO.getBankName());
        db.insert("bankCodeInfo", null, values);

        // 다시 화면 갱신
        setLayout();
    }

    @Override
    public void finish() {
        super.finish();
        db.close();
    }
}