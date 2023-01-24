package com.example.moneysave.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    static String DB_NAME = "saveMoney.db";
    static int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql1 = "CREATE TABLE if not exists saveInfo ("
                + "id integer primary key autoincrement,"
                + "date varchar(10),"
                + "money varchar(40),"
                + "memo varchar(200),"
                + "ver varchar(10),"
                + "bankCode varchar(2));";
        String sql2 = "CREATE TABLE if not exists goalInfo ("
                + "ver varchar(10),"
                + "goalMoney varchar(40));";
        String sql3 = "CREATE TABLE if not exists nowMoneyInfo ("
                + "ver varchar(10),"
                + "nowMoney varchar(40));";
        String sql4 = "CREATE TABLE if not exists verInfo ("
                + "ver varchar(10));";
        String sql5 = "CREATE TABLE if not exists dateInfo ("
                + "ver varchar(10),"
                + "date varchar(6));";
        String sql6 = "CREATE TABLE if not exists bankCodeInfo ("
                + "ver varchar(10),"
                + "bankName varchar(30),"
                + "bankCode varchar(2));";

        db.execSQL(sql1);
        db.execSQL(sql2);
        db.execSQL(sql3);
        db.execSQL(sql4);
        db.execSQL(sql5);
        db.execSQL(sql6);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
