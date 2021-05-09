package com.example.weatherforecast.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DBManager {
    /* 查找数据库当中城市列表*/
    public static List<String> queryAllCityName(DBHelper dbHelper){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query("Weather", null, null, null, null, null,null);
        List<String> cityList = new ArrayList<>();
        while (cursor.moveToNext()) {
            String city = cursor.getString(cursor.getColumnIndex("city"));
            cityList.add(city);
        }
        return cityList;
    }
    /* 根据城市名称，替换信息内容*/
    public static int updateInfoByCity(String city, String content, DBHelper dbHelper){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("content",content);
        return database.update("Weather",values,"city=?", new String[]{city});
    }
    /* 新增一条城市记录*/
    public static void addCityInfo(String city, String content, DBHelper dbHelper){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("city",city);
        values.put("content",content);
        database.insert("Weather",null,values);
    }
    /* 根据城市名，查询数据库当中的内容*/
    public static String queryInfoByCity(String city, DBHelper dbHelper){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query("Weather", null, "city=?", new String[]{city}, null, null, null);
        if (cursor.getCount()>0) {
            cursor.moveToFirst();
            String content = cursor.getString(cursor.getColumnIndex("content"));
            return content;
        }
        return null;
    }


    /* 查询数据库当中的全部信息*/
    public static List<DatabaseBean> queryAllInfo(DBHelper dbHelper){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query("Weather", null, null, null, null, null, null);
        List<DatabaseBean> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            String city = cursor.getString(cursor.getColumnIndex("city"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            DatabaseBean bean = new DatabaseBean(id, city, content);
            list.add(bean);
        }
        return list;
    }

    /* 根据城市名称，删除这个城市在数据库当中的数据*/
    public static int deleteInfoByCity(String city,DBHelper dbHelper){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database.delete("Weather","city=?",new String[]{city});
    }

    /* 删除表当中所有的数据信息*/
    public static void deleteAllInfo(DBHelper dbHelper){
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String sql = "delete from info";
        database.execSQL(sql);
    }
}
