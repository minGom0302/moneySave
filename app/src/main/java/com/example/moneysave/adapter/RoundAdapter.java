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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RoundAdapter extends BaseAdapter {
    Context context;
    List<DTO_zip> dtoList;
    int cnd;

    public RoundAdapter(Context context, List<DTO_zip> dtoList, int cnd) {
        this.context = context;
        this.dtoList = dtoList;
        this.cnd = cnd;
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

    @SuppressLint({"ViewHolder", "MissingInflatedId", "SimpleDateFormat"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.spinner_round, parent, false);

        TextView tv = view.findViewById(R.id.spinner_round_ver);
        if(cnd == 0) {
            tv.setText(dtoList.get(position).getRound());
        } else if(cnd == 1) {
            tv.setText(dtoList.get(position).getDate());
        }

        return view;
    }
}
