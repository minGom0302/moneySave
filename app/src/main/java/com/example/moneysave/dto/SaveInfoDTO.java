package com.example.moneysave.dto;

public class SaveInfoDTO {
    public int id;
    public String date;
    public String money;
    public String memo;
    public String ver;
    public String bankName;

    public SaveInfoDTO(int id, String date, String money, String memo, String ver, String bankName) {
        this.id = id;
        this.date = date;
        this.money = money;
        this.memo = memo;
        this.ver = ver;
        this.bankName = bankName;
    }

    public String getVer() {
        return ver;
    }

    public void setVer(String ver) {
        this.ver = ver;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
