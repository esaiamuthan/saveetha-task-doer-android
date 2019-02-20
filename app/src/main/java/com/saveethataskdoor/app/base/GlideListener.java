package com.saveethataskdoor.app.base;

import android.graphics.drawable.Drawable;


public interface GlideListener {
    void onImageLoaded(Drawable resource);
    void onImageFailed();
}
