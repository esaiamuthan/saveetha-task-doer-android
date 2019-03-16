package com.saveethataskdoor.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bumptech.glide.Glide;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.saveethataskdoor.app.base.BaseActivity;
import com.saveethataskdoor.app.databinding.ActivityWebBinding;
import com.shockwave.pdfium.PdfDocument;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

public class WebActivity extends BaseActivity implements OnPageChangeListener, OnLoadCompleteListener,
        OnPageErrorListener {

    ActivityWebBinding binding;
    String pdfFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web);

        initUI();

//        if (Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).getString("url")).contains(".pdf")) {
//
//            findViewById(R.id.pdfView).setVisibility(View.VISIBLE);
//            findViewById(R.id.webView).setVisibility(View.GONE);
//            pdfFileName = getFileName(Uri.parse(Objects.requireNonNull(getIntent().getExtras()).getString("url")));
//
//            if (ActivityCompat.checkSelfPermission(WebActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
//                    && ActivityCompat.checkSelfPermission(WebActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(WebActivity.this,
//                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
//            } else {
//                showPDF();
//            }
//        } else {

        if (Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).getString("url")).contains(".pdf")) {

            binding.imageView.setVisibility(View.GONE);
            findViewById(R.id.webView).setVisibility(View.VISIBLE);

            showWebView();

        } else {
            binding.imageView.setVisibility(View.VISIBLE);
            findViewById(R.id.webView).setVisibility(View.GONE);

            Glide.with(this)
                    .load(getIntent().getExtras().getString("url"))
                    .into(binding.imageView);

        }
        findViewById(R.id.pdfView).setVisibility(View.GONE);

//        }
    }

    @Override
    public void initUI() {
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    private void showWebView() {
        ((WebView) findViewById(R.id.webView)).setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
            }
        });
        ((WebView) findViewById(R.id.webView)).getSettings().setJavaScriptEnabled(true);
        ((WebView) findViewById(R.id.webView)).getSettings().setDomStorageEnabled(true);

        String firebaseUrl = Objects.requireNonNull(getIntent().getExtras()).getString("url");
        String url = "";
        try {
            url = URLEncoder.encode(firebaseUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ((WebView) findViewById(R.id.webView)).loadUrl("http://docs.google.com/gview?embedded=true&url=" + url);
    }

    private void showPDF() {
        ((PDFView) findViewById(R.id.pdfView)).fromUri(Uri.parse(Objects.requireNonNull(getIntent().getExtras()).getString("url")))
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageError(this)
                .load();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                showPDF();
                break;
        }
    }

    Integer pageNumber = 0;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 534;

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = ((PDFView) findViewById(R.id.pdfView)).getDocumentMeta();
        printBookmarksTree(((PDFView) findViewById(R.id.pdfView)).getTableOfContents(), "-");
    }

    private void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e("printBookmarksTree", String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));
    }

    @Override
    public void onPageError(int page, Throwable t) {
        Log.e("onPageError", "Cannot load page " + page);
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }
}
