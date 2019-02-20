package com.saveethataskdoor.app.base;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import de.hdodenhof.circleimageview.CircleImageView;
import com.saveethataskdoor.app.R;
import com.saveethataskdoor.app.utils.CustomTypefaceSpan;

public class BaseActivity extends AppCompatActivity implements com.saveethataskdoor.app.base.IBaseView, com.saveethataskdoor.app.base.BaseFragment.Callback {
    protected final int NO_COLOR = -1;
    public final int NO_STYLE_FOR_DIALOG = 0;


    private static Toast toast;

    @Override
    public BaseActivity getBaseActivity() {
        return BaseActivity.this;
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
    public void onError(int resId) {

    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void showMessage(int resId) {

    }

    @Override
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean networkEnabled = (cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected());
        if (!networkEnabled) {
            if (toast != null)
                toast.cancel();
            toast = Toast.makeText(this, R.string.alert_no_network, Toast.LENGTH_SHORT);

            toast.show();

        }
        return networkEnabled;
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public int getStatusBarHeight() {
        if (Build.VERSION.SDK_INT < 21)
            return (int) getResources().getDimension(R.dimen.spacing_medium);
        else
            return (int) getResources().getDimension(R.dimen.spacing_medium);
    }

    @Override
    public void showAlertWithPositiveButton(int style, String msg) {
        AlertDialog.Builder builder;
        if (style == NO_STYLE_FOR_DIALOG)
            builder = new AlertDialog.Builder(this);
        else
            builder = new AlertDialog.Builder(this, style);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(msg);
        builder.setPositiveButton(getString(R.string.alert_ok), (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    @Override
    public void loadImage(ImageView imageView, String url, GlideListener listener) {
        if (url != null && url.length() > 4) {
            if (listener != null)
                Glide.with(this).load(url).listener(getListener(listener)).into(imageView);
            else Glide.with(this).load(url).into(imageView);
        }

    }

    @Override
    public void loadImage(ImageView imageView, String url, int height, int width, GlideListener listener) {
        if (url != null && url.length() > 4) {
            if (listener != null)
                Glide.with(this).load(url).listener(getListener(listener)).into(imageView);
            else Glide.with(this).load(url).into(imageView);
        }
    }

    @Override
    public void loadImage(CircleImageView imageView, String url, GlideListener listener) {
        if (url != null && url.length() > 4) {
            if (listener != null)
                Glide.with(this).load(url).listener(getListener(listener)).into(imageView);
            else Glide.with(this).load(url).into(imageView);
        } else
            imageView.setImageResource(0);
    }

    @Override
    public void loadImage(CircleImageView imageView, String url, int height, int width, GlideListener listener) {
        if (url != null && url.length() > 4) {
            if (listener != null)
                Glide.with(this).load(url).listener(getListener(listener)).into(imageView);
            else Glide.with(this).load(url).into(imageView);
        }
    }

    @Override
    public void onFragmentAttached() {

    }

    @Override
    public void onFragmentDetached(String tag) {

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
                checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }



    private RequestListener<Drawable> getListener(GlideListener glideListener) {
        return new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                glideListener.onImageFailed();
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                glideListener.onImageLoaded(resource);
                return false;
            }
        };
    }

    public boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void applyFontForMenuItem(MenuItem item, Typeface typeface, int color) {
        try {
            SpannableString title = new SpannableString(item.getTitle());
            if (color != -1)
                title.setSpan(new ForegroundColorSpan(color), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            title.setSpan(new CustomTypefaceSpan("", typeface), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            item.setTitle(title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
