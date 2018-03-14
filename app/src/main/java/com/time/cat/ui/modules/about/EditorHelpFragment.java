package com.time.cat.ui.modules.about;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.time.cat.R;
import com.time.cat.ui.base.BaseFragment;

import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;

public class EditorHelpFragment extends BaseFragment {
    @BindString(R.string.drawer_item_help) String TITLE;
    @BindView(R.id.docs_webview) WebView webView;

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentConfig(true, false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_help;
    }

    @Override
    public void initView() {
        toolbarTitle = TITLE;
        super.initView();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appCompatActivity);
        setWebView();
    }

    public void setWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDefaultFontSize(16);

        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebChromeClient(new WebChromeClient());
        String value = sharedPreferences.getString("language", "zh");
        Locale locale;
        if (value.equals("auto")) {
            locale = Locale.getDefault();
        } else {
            if (value.contains("_")) {
                String[] parts = value.split("_");
                locale = new Locale(parts[0], parts[1]);
            } else {
                locale = new Locale(value);
            }
        }
        webView.loadUrl("file:///android_asset/markdown-cheatsheet-"
                + locale.getLanguage() + ".html");
    }
}
