package com.example.moneysave.dto;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DTO_zip {
    // 스피너 목록을 만들 DTO
    // 회차
    String round;
    // 월별
    String ver;
    String date;
    String originalDate;
    // 은행별
    String bankVer;
    String bankCode;
    String bankName;

    /********** 회차 **********/
    public DTO_zip(String round) {
        this.round = round + "회차";
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    /********** 월별 **********/
    @SuppressLint("SimpleDateFormat")
    public DTO_zip(String ver, String date) {
        this.ver = ver;
        originalDate = date;

        if(date.equals("전체")) {
            this.date = date;
            return;
        }

        SimpleDateFormat input = new SimpleDateFormat("yyyyMM");
        SimpleDateFormat output = new SimpleDateFormat("yyyy년 MM월");
        try {
            Date inputDate = input.parse(date);
            if(inputDate != null) {
                this.date = output.format(inputDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getOriginalDate() {
        return originalDate;
    }

    public void setOriginalDate(String originalDate) {
        this.originalDate = originalDate;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    /********** 은행별 **********/
    public DTO_zip(String bankVer, String bankCode, String bankName) {
        this.bankVer = bankVer;
        this.bankCode = bankCode;
        this.bankName = bankName;
    }

    public String getBankVer() {
        return bankVer;
    }

    public void setBankVer(String bankVer) {
        this.bankVer = bankVer;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
