

package com.saveethataskdoor.app.base;


import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * Base class that implements the Presenter interface and provides a base implementation for
 * onAttach() and onDetach(). It also handles keeping a reference to the mvpView that
 * can be accessed from the children classes by calling getMvpView().
 */
public class BasePresenter<V extends IBaseView> implements IBasePresenter<V> {

    private V mMvpView;


    @Override
    public void onAttach(V mvpView) {
        mMvpView = mvpView;
    }

    @Override
    public void onDetach() {
        mMvpView = null;
    }

    @Override
    public boolean isViewAttached() {
        return mMvpView != null;
    }

    protected V getMvpView() {
        return mMvpView;
    }

    public void checkViewAttached() {
        if (!isViewAttached()) throw new ViewNotAttachedException();
    }

    @Override
    public int getDeviceHeight(Activity activity) {
        DisplayMetrics metrices = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrices);
        return metrices.heightPixels;
    }

    @Override
    public int getDeviceWidth(Activity activity) {
        DisplayMetrics metrices = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrices);
        return metrices.widthPixels;
    }


    public static class ViewNotAttachedException extends RuntimeException {
         ViewNotAttachedException() {
            super("Please call Presenter.onAttach(MvpView) before" +
                    " requesting data to the Presenter");
        }
    }
}
