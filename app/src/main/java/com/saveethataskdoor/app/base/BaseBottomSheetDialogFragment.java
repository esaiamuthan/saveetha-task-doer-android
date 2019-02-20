package com.saveethataskdoor.app.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created on 24-Jan-18.
 */

public class BaseBottomSheetDialogFragment extends BottomSheetDialogFragment implements IBaseView {
    private BaseActivity mActivity;
    private AlertDialog dialogQuitAlert;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) context;
            this.mActivity = activity;
            activity.onFragmentAttached();
        }
    }

    @Override
    public void initUI() {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onError(String message) {
        if (mActivity != null) {
            mActivity.onError(message);
        }
    }

    @Override
    public void onError(@StringRes int resId) {
        if (mActivity != null) {
            mActivity.onError(resId);
        }
    }

    @Override
    public void showMessage(String message) {
        if (mActivity != null) {
            mActivity.showMessage(message);
        }
    }

    @Override
    public void showMessage(@StringRes int resId) {
        if (mActivity != null) {
            mActivity.showMessage(resId);
        }
    }

    @Override
    public boolean isNetworkConnected() {
        return mActivity != null && mActivity.isNetworkConnected();
    }

    @Override
    public void onDetach() {
        mActivity = null;
        super.onDetach();
    }

    @Override
    public void hideKeyboard() {
        if (mActivity != null) {
            mActivity.hideKeyboard();
        }
    }

    @Override
    public void hideKeyboard(View view) {
        if (mActivity != null) {
            mActivity.hideKeyboard(view);
        }
    }

    @Override
    public int getStatusBarHeight() {
        return getBaseActivity().getStatusBarHeight();
    }

    @Override
    public void loadImage(ImageView imageView, String url, GlideListener listener) {
        getBaseActivity().loadImage(imageView, url, listener);

    }

    @Override
    public void loadImage(ImageView imageView, String url, int height, int width, GlideListener listener) {
        getBaseActivity().loadImage(imageView, url, height, width, listener);
    }

    @Override
    public void loadImage(CircleImageView imageView, String url, GlideListener listener) {
        getBaseActivity().loadImage(imageView, url, listener);
    }

    @Override
    public void loadImage(CircleImageView imageView, String url, int height, int width, GlideListener listener) {
        getBaseActivity().loadImage(imageView, url, height, width, listener);
    }


    @Override
    public BaseActivity getBaseActivity() {
        return mActivity;
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected void requestPermissionsSafely(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    protected boolean hasPermission(String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                getBaseActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void showAlertWithPositiveButton(int style, String msg) {
        getBaseActivity().showAlertWithPositiveButton(style, msg);
    }


    @Override
    public void applyFontForMenuItem(MenuItem item, Typeface typeface, int color) {
        getBaseActivity().applyFontForMenuItem(item, typeface, color);
    }
}
