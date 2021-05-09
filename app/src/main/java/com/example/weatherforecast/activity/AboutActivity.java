package com.example.weatherforecast.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.example.weatherforecast.R;
import com.example.weatherforecast.utils.ToastUtils;

/**
 * @Description
 * @Author tang
 * @Date2021-04-30 16:13
 */
public class AboutActivity extends Activity implements View.OnClickListener {
    TextView tvBlog;//博客
    TextView tvCode;//源码
    TextView tvAuthor;//作者
    TextView tvCopyEmail;//复制邮箱
    Toolbar toolbar;
    private ClipboardManager myClipboard;
    private ClipData myClip;

    //博客地址
    private String CSDN_BLOG_URL = "https://blog.csdn.net/qq_38436214/category_9880722.html";
    //源码地址
    private String GITHUB_URL = "https://github.com/lilongweidev/GoodWeather";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initView();
    }
    private void initView(){
        tvBlog = findViewById(R.id.tv_blog);
        tvCode = findViewById(R.id.tv_code);
        tvAuthor = findViewById(R.id.tv_author);
        tvCopyEmail = findViewById(R.id.tv_copy_email);
        toolbar = findViewById(R.id.about_toolbar);
        Back(toolbar);
    }
    //设置返回点击事件
    protected void Back(Toolbar toolbar) {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(AboutActivity.this, MainActivity.class);
                startActivity(backIntent);
                AboutActivity.this.finish();
            }
        });
    }
    //手机自己的返回
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//如果返回键按下
            Intent backIntent = new Intent(AboutActivity.this, MainActivity.class);
            startActivity(backIntent);
            AboutActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //博客地址
            case R.id.tv_blog:
                jumpUrl(CSDN_BLOG_URL);
                break;
            //源码地址
            case R.id.tv_code:
                jumpUrl(GITHUB_URL);
                break;
            //复制邮箱
            case R.id.tv_copy_email:
                myClipboard = (ClipboardManager) this.getSystemService(CLIPBOARD_SERVICE);
                myClip = ClipData.newPlainText("text", "lonelyholiday@qq.com");
                myClipboard.setPrimaryClip(myClip);
                ToastUtils.showShortToast(this, "邮箱已复制");
                break;
            default:
                break;
        }
    }
    /**
     * 跳转URL
     *
     * @param url 地址
     */
    private void jumpUrl(String url) {
        if (url != null) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } else {
            ToastUtils.showShortToast(this, "未找到相关地址");
        }
    }
}
