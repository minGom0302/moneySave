package com.example.moneysave.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.moneysave.R;
import com.example.moneysave.dto.DTO_zip;

import java.util.List;

public class BankAdapter extends BaseAdapter {
    Context context;
    List<DTO_zip> dtoList;

    public BankAdapter(Context context, List<DTO_zip> dtoList) {
        this.context = context;
        this.dtoList = dtoList;
    }

    @Override
    public int getCount() {
        return dtoList.size();
    }

    @Override
    public Object getItem(int position) {
        return dtoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "MissingInflatedId"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.spinner_bank, parent, false);

        TextView codeTv = view.findViewById(R.id.spinner_bank_code);
        TextView nameTv = view.findViewById(R.id.spinner_bank_name);

        codeTv.setText(dtoList.get(position).getBankCode());
        nameTv.setText(dtoList.get(position).getBankName());

        return view;
    }
}
