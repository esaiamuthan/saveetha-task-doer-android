package com.saveethataskdoor.app.login;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.saveethataskdoor.app.R;

import com.saveethataskdoor.app.base.BaseActivity;
import com.saveethataskdoor.app.databinding.ActivityForgetBinding;

public class ForgetActivity extends BaseActivity {
    private ActivityForgetBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forget);
        initUI();
    }

    @Override
    public void initUI() {
    }
}
