package com.example.weatherforecast.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.IdentityHashMap;

public class DBHelper extends SQLiteOpenHelper {

    private String sql = "create table Weather(" +
            "_id integer primary key autoincrement," +//id
            "city varchar(20) unique not null," +//城市名
            "content text not null)";//存储请求的网络结果
    private Context mcontext;
    public DBHelper(Context context,String name,SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
        mcontext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
//        创建表的操作
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("drop table if exists userinfo");   //有这个数据库则删除重新创建
//        db.execSQL(sql);
    }
    /* 新增一条城市记录*/
    public void addCityInfo(Integer id,String city, String content){
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id",id);
        values.put("city",city);
        values.put("content",content);
        database.insert("Weather",null,values);
    }
}
