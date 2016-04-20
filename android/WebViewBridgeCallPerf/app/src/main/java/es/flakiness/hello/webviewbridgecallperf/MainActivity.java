package es.flakiness.hello.webviewbridgecallperf;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView;
    private TextView mReportText;

    public class Bridge {
        @JavascriptInterface
        public void doNothing() {}

        @JavascriptInterface
        public void reportResult(final String result) {
            mReportText.post(new Runnable() {
                @Override
                public void run() {
                    mReportText.setText(result);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        setContentView(R.layout.activity_main);

        mReportText = (TextView)findViewById(R.id.report_text);
        mWebView = (WebView)findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new Bridge(), "bridge");
        mWebView.loadUrl("file:///android_asset/index.html");
    }
}
