package example.com.hb.diary.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import example.com.hb.diary.R;
import example.com.hb.diary.Utils.InternetConnectionUtil;
import example.com.hb.diary.fragment.OfflineFragment;

public class HelpActivity extends BaseActivity {
    private static final String HELP_URL = "https://www.writediary.com/help/app?lang=en#top";
    Toolbar toolbar;
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        if (InternetConnectionUtil.isOnline(this)) {
            showHelp();
        } else {
            alertOffline();
        }
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Show web view
     */
    private void showHelp() {
        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new MyBrowser());
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(HELP_URL);
    }

    /**
     * Alert to user when connection if off
     */
    private void alertOffline() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fr = new OfflineFragment();
        ft.replace(android.R.id.content, fr);
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
