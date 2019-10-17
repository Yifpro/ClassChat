package com.example.wyf.classchat.feature.login;


import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.wyf.classchat.R;
import com.example.wyf.classchat.base.IAppInitContract;

import butterknife.BindView;


/**
 * @author WYF on 2017/9/22.
 */
public class ServiceTermsActivity extends AppCompatActivity implements IAppInitContract.IActivity {

    @BindView(R.id.myProgressBar)
    ProgressBar bar;
    @BindView(R.id.myWebView)
    WebView webView;

    @Override
    public void init() {
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    bar.setVisibility(View.INVISIBLE);

                } else {
                    if (View.INVISIBLE == bar.getVisibility()) {
                        bar.setVisibility(View.VISIBLE);
                    }
                    bar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);

            }

        });
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        String url = "https://www.bilibili.com/";
        webView.loadUrl(url);




































    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_service_terms;
    }
}
