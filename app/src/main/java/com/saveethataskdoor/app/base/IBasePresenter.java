package com.saveethataskdoor.app.base;

import android.app.Activity;


public interface IBasePresenter<V extends IBaseView> {

    void onAttach(V mvpView);

    void onDetach();

    int getDeviceHeight(Activity activity);
    int getDeviceWidth(Activity activity);

    boolean isViewAttached();

}
