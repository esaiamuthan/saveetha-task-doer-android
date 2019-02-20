package com.saveethataskdoor.app.base;

import android.graphics.Typeface;
import android.support.annotation.StringRes;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import de.hdodenhof.circleimageview.CircleImageView;


public interface IBaseView {

    BaseActivity getBaseActivity();

    void initUI();

    void showLoading();

    void hideLoading();

    void onError(@StringRes int resId);

    void onError(String message);

    void showMessage(String message);

    void showMessage(@StringRes int resId);

    boolean isNetworkConnected();

    void hideKeyboard();

    void hideKeyboard(View view);

    int getStatusBarHeight();

    void showAlertWithPositiveButton(int style, String msg);

    void loadImage(ImageView imageView, String url, GlideListener listener);

    void loadImage(ImageView imageView, String url, int height, int width, GlideListener listener);

    void loadImage(CircleImageView imageView, String url, GlideListener listener);

    void loadImage(CircleImageView imageView, String url, int height, int width, GlideListener listener);

    void applyFontForMenuItem(MenuItem item, Typeface typeface, int color);

}
