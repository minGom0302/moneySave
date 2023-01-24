package com.example.moneysave.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneysave.R;
import com.example.moneysave.db.DBHelper;
import com.example.moneysave.dto.SaveInfoDTO;
import com.example.moneysave.etc.UseItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CursorAdapter extends RecyclerView.Adapter<CursorAdapter.MyViewHolder> {
    Context mContext;
    Context context;
    List<SaveInfoDTO> dtoList;
    SharedPreferences sp;
    SharedPreferences.Editor sp_e;

    public CursorAdapter(List<SaveInfoDTO> dtoList, Context mContext) {
        this.dtoList = dtoList;
        this.mContext = mContext;
        sp = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        sp_e = sp.edit();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dateTv, moneyTv, memoTv, bankTv;
        Button deleteBtn;

        public MyViewHolder(@NonNull View view) {
            super(view);
            dateTv = view.findViewById(R.id.listview_save_dateTv);
            moneyTv = view.findViewById(R.id.listview_save_moneyTv);
            memoTv = view.findViewById(R.id.listview_save_memoTv);
            bankTv = view.findViewById(R.id.listview_save_bankTv);
            deleteBtn = view.findViewById(R.id.listview_save_deleteBtn);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.listview_savemoney, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint({"SimpleDateFormat", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SimpleDateFormat format01 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat format02 = new SimpleDateFormat("yyyy-MM-dd");

        SaveInfoDTO dto = dtoList.get(position);
        System.out.println("date value : " + dto.getDate());
        String strDate = null;
        try {
            Date date = format01.parse(dto.getDate());
            if(date != null) {
                strDate = format02.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int id = dto.getId();
        String beforeMoney = dto.getMoney();
        String money = UseItem.comma(beforeMoney);
        String memo = dto.getMemo();
        String bank = dto.getBankName();

        holder.dateTv.setText(strDate);
        holder.moneyTv.setText(money);
        holder.memoTv.setText(memo);
        holder.bankTv.setText(bank);

        holder.deleteBtn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("안내").setMessage("해당 데이터를 삭제하시겠습니까?");
            builder.setPositiveButton("예", ((dialog, which) -> {
                dtoList.remove(position);
                DBHelper helper = new DBHelper(context);
                SQLiteDatabase db = helper.getWritableDatabase();
                String sql = "delete from saveInfo where id = " + id + ";";
                db.execSQL(sql);
                db.close();

                String nowMoney = sp.getString("nowMoney", "0");

                int iNowMoney = Integer.parseInt(nowMoney);
                int iMoney = Integer.parseInt(beforeMoney);

                int result = iNowMoney - iMoney;
                String strResult = String.valueOf(result);

                sp_e.putString("nowMoney", strResult);
                sp_e.commit();

                notifyDataSetChanged();
            }));
            builder.setNegativeButton("아니오", ((dialog, which) -> {

            }));

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return dtoList.size();
    }

}
