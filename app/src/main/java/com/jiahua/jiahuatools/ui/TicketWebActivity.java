package com.jiahua.jiahuatools.ui;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jiahua.jiahuatools.R;
import com.jiahua.jiahuatools.bean.UserAndPassword;
import com.orhanobut.logger.Logger;

import org.litepal.crud.DataSupport;

public class TicketWebActivity extends AppCompatActivity {

    WebView webView;
    WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_web);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.md_yellow_500));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_yellow_24dp);

        webView = (WebView)findViewById(R.id.web_test);

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            //网页缓存完后执行，但不靠谱
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Logger.e("==================");

            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

        });
        UserAndPassword userAndPassword = DataSupport.findFirst(UserAndPassword.class);
        String user = userAndPassword.getUser();
        String password = userAndPassword.getPassword();
        String TicketID = getIntent().getStringExtra("TicketID");
        String postData = "Action=" + "Login" + "&" +
                "Lang=" + "zh_CN" + "&" +
                //.append("TimeOffset=").append("-480").append("&")
                "User=" + user + "&" +
                "Password=" + password;

        webSettings = webView.getSettings();
        //webSettings.setJavaScriptEnabled(true);
        //适配屏幕
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        //支持放大缩小
        webSettings.setBuiltInZoomControls(true);
        //设置网页默认编码
        webSettings.setDefaultTextEncodingName("utf-8");
        //隐藏放大缩小的按钮
        webSettings.setDisplayZoomControls(false);
        //不使用缓存，只从网络获取数据。
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//支持内容重新布局
        //webSettings.setDomStorageEnabled(true);//支持Html5标签
        //webView.loadUrl("https://imotom01.dd.ezbox.cc:34443/otrs/index.pl");
        //webView.loadUrl("https://imotom01.dd.ezbox.cc:34443//otrs/index.pl?Action=AgentTicketProcess;Subaction=DisplayActivityDialog;TicketID=10;ProcessEntityID=Process-f5aa2fcad2661c1c1f3def4f077c34b8;ActivityDialogEntityID=ActivityDialog-1c45797209778dfbd44f0c1a5068a8fe");
        //webView.postUrl("https://imotom01.dd.ezbox.cc:34443/otrs/index.pl?Action=AgentTicketProcess;Subaction=DisplayActivityDialog;TicketID=10;ProcessEntityID=Process-f5aa2fcad2661c1c1f3def4f077c34b8;ActivityDialogEntityID=ActivityDialog-1c45797209778dfbd44f0c1a5068a8fe",
        webView.postUrl("https://imotom01.dd.ezbox.cc:34443/otrs/index.pl?Action=AgentTicketZoom;TicketID="
                        + TicketID,
                postData.getBytes());
    }

    @Override
    protected void onDestroy() {
        webView.loadUrl("https://imotom01.dd.ezbox.cc:34443/otrs/index.pl?Action=Logout;");
        //https://imotom01.dd.ezbox.cc:34443/otrs/index.pl?Action=Logout;ChallengeToken=Bz8CA577Gd8e36zyjR9S7Zezp3GkfdDS;
        //https://imotom01.dd.ezbox.cc:34443/otrs/index.pl?Action=Logout;ChallengeToken=Fnz0GbnQORhUBOIA70k1PqheeJo3tIHN;
        //webView.removeAllViews();
        webView.destroy();
        System.gc();
        //System.exit(0);
        super.onDestroy();
    }

    //shouldOverrideKeyEvent(WebView view, KeyEvent event)


    @Override
    public void onBackPressed() {
        //返回上个页面
        if(webView.canGoBack()){
            webView.goBack();
            Logger.e("----------------");
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);

    }
}
