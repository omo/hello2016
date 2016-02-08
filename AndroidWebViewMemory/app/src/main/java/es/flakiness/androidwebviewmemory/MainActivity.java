package es.flakiness.androidwebviewmemory;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Debug;
import android.os.Debug.MemoryInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.io.ByteArrayInputStream;

public class MainActivity extends AppCompatActivity {

    static class TestPageFeeder extends WebViewClient {
        static final Uri PAGE_URI = Uri.parse("http://example.com/test");
        static final byte[] PAGE_DATA =
                "<body><script>\nconsole.log(document);\n</script></body>".getBytes();

        @TargetApi(VERSION_CODES.LOLLIPOP)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view,
                WebResourceRequest request) {
            if (request.getUrl().equals(PAGE_URI)) {
                return new WebResourceResponse(
                        "text/html", "utf-8", new ByteArrayInputStream(PAGE_DATA));
            }

            return super.shouldInterceptRequest(view, request);
        }
    };

    private WebView mWebView;
    private TextView mReportText;
    private Thread mMonitoringThread;
    private MemoryInfo mMemInfo = new MemoryInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = (WebView)findViewById(R.id.main_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new TestPageFeeder());
        mWebView.loadUrl("file:///android_asset/index.html");
        mReportText = (TextView)findViewById(R.id.report_text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMonitoringThread  = new Thread(new Runnable() {
            @Override
            public void run() {
                monitorLoop();
            }
        });
        mMonitoringThread.start();
    }

    @Override
    protected void onPause() {
        mMonitoringThread.interrupt();
        super.onPause();
    }

    private void monitorLoop() {
        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return;
            }

            monitorAndReport();
        }
    }

    private void monitorAndReport() {
        Runtime.getRuntime().gc();
        Debug.getMemoryInfo(mMemInfo);
        final String report = String.format(
                "Dalvik:%d Native:%d Total:%d",
                mMemInfo.dalvikPss, mMemInfo.nativePss, mMemInfo.getTotalPss());
        mReportText.post(new Runnable() {
            @Override
            public void run() {
                mReportText.setText(report);
            }
        });
    }
}
