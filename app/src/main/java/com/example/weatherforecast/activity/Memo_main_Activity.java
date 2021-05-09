package com.example.weatherforecast.activity;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.weatherforecast.R;
import com.example.weatherforecast.adapter.Memo_DiaryAdapter;
import com.example.weatherforecast.adapter.Memo_SimpleDiaryAdapter;
import com.example.weatherforecast.db.Diary;
import com.example.weatherforecast.utils.ZipUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



public class Memo_main_Activity extends AppCompatActivity {

    private Context mContext;
    private DrawerLayout mDrawerLayout;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;  //刷新日记
    //从数据库读取日记信息
    private List<Diary> diaryList;
    private RecyclerView.Adapter adapter;

    private final int SIMPLE_TYPE=1;
    private final int GENERAL_TYPE=2;
    private int TYPE;


    private static final int FILE_SELECT_CODE = 0;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_main);
        mContext= Memo_main_Activity.this;

        Toolbar toolbar=findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//隐藏标题
        toolbar.setTitle("天气日记");

        mDrawerLayout=findViewById(R.id.drawer_layout);
        /*
        为mDrawerLayout添加监听器
        现在大部分安卓手机在滑动边缘时都是默认返回上一个活动，如果在这种情况下还采取滑动打开的方式
        就会导致两个功能冲突，影响应用使用体验
        这里规定在滑动窗口开启的情况下解锁滑动模式，允许用户通过滑动关闭滑窗
        在滑窗隐藏的情况下，禁止用户通过滑动边缘打开滑窗，只能通过HomeAsUp按钮
         */
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) { }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onDrawerStateChanged(int newState) { }
        });

        /* *******************************************************************************************
         *
         * 操作按钮 设置点击事件
         *
         ********************************************************************************************/

        FloatingActionButton fabWrite=findViewById(R.id.fab);
        fabWrite.setOnClickListener(new Memo_NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                Intent intent=new Intent(mContext, Memo_EditDiaryActivity.class);
                startActivity(intent);
                finish();  //这里结束当前活动，方便在保存日记回到该页面时重新调用onCreate方法进而刷新日记内容
            }
        });
        FloatingActionButton main_page =findViewById(R.id.fab1);
        main_page.setOnClickListener(new Memo_NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                Intent intent=new Intent(mContext, MainActivity.class);
                startActivity(intent);
                finish();  //这里结束当前活动，方便在保存日记回到该页面时重新调用onCreate方法进而刷新日记内容
            }
        });



        /* *******************************************************************************************
         *
         * 读取规定的布局方式
         *
         ********************************************************************************************/

        recyclerView=findViewById(R.id.recycler_view);
        initDiary();  //在创建时初始化日记内容
        //绑定控制器和适配器
        GridLayoutManager layoutManager=new GridLayoutManager(this,1);
        SharedPreferences pref=getSharedPreferences("TYPE",MODE_PRIVATE);
        TYPE=pref.getInt("TYPE",GENERAL_TYPE);
        switch (TYPE){
            case GENERAL_TYPE:
                /*
                //生命为瀑布流的布局方式，2列，布局方向为垂直
                StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                //解决item跳动
                manager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
                recyclerView.setLayoutManager(manager);
                 */
                adapter=new Memo_DiaryAdapter(diaryList);
                break;
            case SIMPLE_TYPE:
                adapter=new Memo_SimpleDiaryAdapter(diaryList);
                break;
        }

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        /* *******************************************************************************************
         *
         * 刷新日记内容
         *
         ********************************************************************************************/


        //下拉刷新
        swipeRefreshLayout=findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDiary();
            }
        });


    }
    //手机自己的返回
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//如果返回键按下
            Intent intent=new Intent(mContext, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /*
    初始化活动的菜单项内容，只在菜单项被放置时调用一次
    getMenuInflater方法得到MenuInflater对象
    MenuInflater类主要用于将menuXML文件转换为Menu对象
    调用其的inflate方法可以给当前活动创建菜单
    第一个参数指定通过哪一个资源文件创建菜单，第二个参数指定菜单项将添加到哪个Menu对象中
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true; //返回true表示允许创建的菜单显示出来，返回false则菜单不显示
    }

    /**
     * 设置系统状态栏的颜色
     * @param activity 当前活动，通过当前活动获得窗体
     * @param statusColor 要设置的状态栏颜色
     */
    static void setStatusBarColor(Activity activity, int statusColor) {
        Window window = activity.getWindow();
        //取消状态栏透明
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //添加Flag把状态栏设为可绘制模式
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //设置状态栏颜色
        window.setStatusBarColor(statusColor);
        //设置系统状态栏处于可见状态
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        //让view不根据系统窗口来调整自己的布局
        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
        View mChildView = mContentView.getChildAt(0);
        if (mChildView != null) {
            ViewCompat.setFitsSystemWindows(mChildView, false);
            ViewCompat.requestApplyInsets(mChildView);
        }
    }
    /**
     * 初始化日记展示内容
     */
    private void initDiary(){

        //在初次使用应用时，向数据库导入四篇介绍性文章
        SharedPreferences preferences=getSharedPreferences("DATA",MODE_PRIVATE);
        if(preferences.getBoolean("FIRST",true)){
            LitePal.getDatabase();  //创建数据库
            Gson gson=new Gson();
            String jsonData=getJson(mContext,"defaultDiary.json");
            List<Diary> diaries=new ArrayList<Diary>();
            Type listType = new TypeToken<List<Diary>>() {}.getType();
            diaries=gson.fromJson(jsonData,listType);
            for(Diary d:diaries){
                d.setUuid(UUID.randomUUID().toString());
                d.setCover(null);
                d.save();
            }
            SharedPreferences.Editor editor=preferences.edit();
            editor.putBoolean("FIRST",false);
            editor.apply();
        }

        /*
        当直接将从数据库或者其他方式获取的数据源集合或者数组直接赋值给当前数据源时，
        相当于当前数据源的对象发生了变化，当前对象已经不是adapter中的对象了，
        所以adaper调用notifyDataSetChanged()方法不会进行刷新数据和界面的操作

        简言之，直接从数据库获取到的资源集合是一个全新的对象，将这个新对象赋给列表，虽然改变了列表的值，但实际是创建了一个新列表
        所以在调用notifyDataSetChanged时，检查的还是原来的列表中的值，原来列表中的值没有改变，自然不会有更新效果
        所以这里在初始化日记列表时，要首先进行判断，如果是初次赋值，直接赋值即可。如果已经有值
        必须先清空列表，然后调用addAll添加值，保证对象不会改变
         */
        if(diaryList!=null) {
            diaryList.clear();
            diaryList.addAll(LitePal.order("date desc,time desc").find(Diary.class));
        }else {
            diaryList=LitePal.order("date desc,time desc").find(Diary.class);
        }


    }


    /**
     * 读取json文件（位于assets文件夹中的json文件）
     * @param context 当前Context
     * @param fileName 文件名称
     * @return 读取json文件得到的字符串对象（用于解析json）
     */
    public static String getJson(Context context, String fileName){
        StringBuilder stringBuilder = new StringBuilder();
        //获得assets资源管理器
        AssetManager assetManager = context.getAssets();
        //使用IO流读取json文件内容
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName),"utf-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    /**
     * 刷新日记展示内容
     */
    private void refreshDiary(){
        initDiary();  //初始化日记列表
        adapter.notifyDataSetChanged();  //提示适配器检查数据，数据已改变
        swipeRefreshLayout.setRefreshing(false);  //关闭刷新状态
    }


    /**
     * 针对高版本利用Uri进行真实地址的解析
     * @param uri 文件Uri
     */
    @TargetApi(19)
    private void handleOnKitKat(Uri uri){
        String path=null;
        Log.d("YWRBY1", "handleImageOnKitKat: "+uri.toString());
        if(DocumentsContract.isDocumentUri(this,uri)){
            String docID=DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id=docID.split(":")[1];
                String selection= MediaStore.Images.Media._ID+"="+id;
                path=getPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri= ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docID));
                path=getPath(contentUri,null);
            }
        }else if("content".equalsIgnoreCase(uri.getScheme())){
            path=getPath(uri,null);
        }else if("file".equalsIgnoreCase(uri.getScheme())){
            path=uri.getPath();
        }
        importDiary(path);
    }


    /**
     * 针对低版本进行文件真实地址解析
     * @param uri 文件Uri
     */
    private void handleBeforeKitKat(Uri uri){
        String path=getPath(uri,null);
        importDiary(path);
    }


    /**
     * 利用文件真实Uri获取文件地址
     * @param uri 文件真实Uri
     * @param selection
     * @return 文件地址
     */
    private String getPath(Uri uri,String selection){
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if(cursor!=null){
            if(cursor.moveToNext()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /**
     * 恢复日记内容：解压缩备份文件，将数据插入数据库，将图片添加回图片文件夹
     * @param path 备份的压缩文件路径
     */
    private void importDiary(String path){
        try {
            //解压缩外层
            ZipUtil.unzip(path, String.valueOf(getExternalCacheDir()));
            //加压缩图片内容至图片文件夹
            ZipUtil.unzip(getExternalCacheDir()+"/photo.zip", String.valueOf(getExternalFilesDir("photo")));
            //加压缩日记数据库内容为json格式
            ZipUtil.unzip(getExternalCacheDir()+"/diary.zip", String.valueOf(getExternalCacheDir()));

            //json格式数据解析
            Gson gson=new Gson();
            //读取文件
            File file=new File(getExternalCacheDir()+"/backup.json");
            StringBuilder stringBuilder = new StringBuilder();
            InputStream instream = new FileInputStream(file);
            //使用IO流读取json文件内容
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(instream,"utf-8"));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
            } catch (IOException e) {
                Log.d("ywrby1", "importDiary: "+"写入失败");
                e.printStackTrace();
            }
            String jsonData=stringBuilder.toString();
            //解析数据
            ArrayList<Diary> diaries=new ArrayList<Diary>();
            Type listType = new TypeToken<List<Diary>>() {}.getType();
            diaries=gson.fromJson(jsonData,listType);
            for(Diary d:diaries){
                //保存Diary对象
                Diary diary=new Diary();
                diary.setCover(d.getCover());
                diary.setUuid(d.getUuid());
                diary.setWeather(d.getWeather());
                diary.setDate(d.getDate());
                diary.setTime(d.getTime());
                diary.setContent(d.getContent());
                diary.setTitle(d.getTitle());
                diary.save();
            }
            //重新打开主页面，刷新数据库内容
            Intent intent=new Intent(mContext,MainActivity.class);
            startActivity(intent);
            finish();
        } catch (IOException e) {
            Log.d("YWRBY1", "importDiary: 失败");
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    // Get the path
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleOnKitKat(uri);
                    } else {
                        handleBeforeKitKat(uri);
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 对菜单的响应事件
     * @param item 菜单选项
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            //写日记功能
            case R.id.write_diary_menu:
                Intent intent=new Intent(mContext, Memo_EditDiaryActivity.class);
                startActivity(intent);
                finish();  //这里结束当前活动，方便在保存日记回到该页面时重新调用onCreate方法进而刷新日记内容
                break;
            //简单布局
            case R.id.simple_adapter_menu:
                SharedPreferences.Editor editor2=getSharedPreferences("TYPE",MODE_PRIVATE).edit();
                editor2.putInt("TYPE",SIMPLE_TYPE);
                editor2.apply();
                Intent intent2=new Intent(mContext,Memo_main_Activity.class);
                startActivity(intent2);
                finish();
                break;
            //卡片布局
            case R.id.adapter_menu:
                SharedPreferences.Editor editor=getSharedPreferences("TYPE",MODE_PRIVATE).edit();
                editor.putInt("TYPE",GENERAL_TYPE);
                editor.apply();
                Intent intent1=new Intent(mContext,Memo_main_Activity.class);
                startActivity(intent1);
                finish();
                break;
            default:
        }
        return true;
    }

}
