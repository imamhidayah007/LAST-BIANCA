package com.imam.biancamobile;

import android.content.Intent;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class webview extends AppCompatActivity {


    private WebView websiteku;
    private ProgressBar loading; //Menambahkan widget ProgressBar
    SwipeRefreshLayout swipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);


        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadWeb();
            }
        });

        LoadWeb();




    }


    public void LoadWeb(){





        websiteku = findViewById(R.id.my_web);
        loading = findViewById(R.id.progress); //Menginisialisasi ID ProgressBar
        final String url = getIntent().getStringExtra("link");


        String titlekalender = getIntent().getStringExtra("Title");


        //  String titlekalender = getIntent().getStringExtra("titlekalender");
        //  getSupportActionBar().setTitle(titlekalender);



        WebSettings web_settings = websiteku.getSettings();
        web_settings.setJavaScriptEnabled(true);
        web_settings.setAllowContentAccess(true);
        web_settings.setUseWideViewPort(true);
        web_settings.setLoadsImagesAutomatically(true);
        web_settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        web_settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        web_settings.setEnableSmoothTransition(true);
        web_settings.setDomStorageEnabled(true);


        websiteku.getSettings().setSupportZoom(true);
        websiteku.getSettings().setBuiltInZoomControls(true);
        websiteku.getSettings().setDisplayZoomControls(false);


        if(Build.VERSION.SDK_INT >= 19){
            websiteku.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }else{
            websiteku.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        swipe.setRefreshing(true);
        websiteku.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.toString());
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                //ProgressBar akan Menghilang setelah Halaman Selesai Dimuat
                super.onPageFinished(view, url);
                loading.setVisibility(View.GONE);
                //ketika loading selesai, ison loading akan hilang
                swipe.setRefreshing(false);
            }
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                websiteku.loadUrl("file:///android_asset/eror.html");
                getSupportActionBar().setTitle("OOPPSSS !!! ");
            }

        });

        websiteku.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                //loading akan jalan lagi ketika masuk link lain
                // dan akan berhenti saat loading selesai
                if(websiteku.getProgress()== 100){
                    swipe.setRefreshing(false);
                } else {
                    swipe.setRefreshing(true);
                }
                loading.setVisibility(View.VISIBLE);
                loading.setProgress(newProgress);
                if(newProgress == 100){
                    //ProgressBar akan Menghilang setelah Valuenya mencapat 100%
                    loading.setVisibility(View.GONE);
                }
                super.onProgressChanged(view, newProgress);
            }

        });




        websiteku.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        websiteku.loadUrl(url);




            getSupportActionBar().setSubtitle(url);
            getSupportActionBar().setTitle(titlekalender);





    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebView webView = (WebView) findViewById(R.id.my_web);

        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;



        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)


        return super.onKeyDown(keyCode, event);

    }



}
