package com.wicam.a_common_utils;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.wicam.R;

/**
 * Created by Hyeonmin on 2015-07-14.
 */
public class WebViewPage extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState);
        setContentView(R.layout.a_common_webview);

        webView = (WebView)findViewById(R.id.a_common_web_view);

        webView.setInitialScale(1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDomStorageEnabled(true);

        System.out.println(getIntent().getStringExtra("url_link"));
        webView.loadUrl(getIntent().getStringExtra("url_link"));
        webView.reload();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack())
            webView.goBack();
        else
            finish();
    }
}
