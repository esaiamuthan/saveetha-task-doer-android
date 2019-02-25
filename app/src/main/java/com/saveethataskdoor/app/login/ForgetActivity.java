package com.saveethataskdoor.app.login;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.saveethataskdoor.app.R;

import com.saveethataskdoor.app.base.BaseActivity;
import com.saveethataskdoor.app.databinding.ActivityForgetBinding;

public class ForgetActivity extends BaseActivity implements View.OnClickListener {

    private ActivityForgetBinding binding;

    private String TAG = ForgetActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forget);
        initUI();
    }

    @Override
    public void initUI() {
        setSupportActionBar(binding.tbForget);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.cvValidateForget.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                hideKeyboard();
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cvValidateForget:
                hideKeyboard();
                if (checkFields()) {
                    callForgetPasswordAPI();
                }
                break;
        }
    }

    private void callForgetPasswordAPI() {
        binding.linearProgress.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().sendPasswordResetEmail(binding.etForgetEmail.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    private String TAG;

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        binding.linearProgress.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgetActivity.this,
                                    "Reset password code has been emailed to "
                                            + binding.etForgetEmail.getText().toString(),
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Log.e(TAG, "Error in sending reset password code",
                                    task.getException());
                            Toast.makeText(ForgetActivity.this,
                                    "There is a problem with reset password, try later.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean checkFields() {
        boolean validated = true;
        if (binding.etForgetEmail.getText().toString().isEmpty()) {
            binding.etForgetEmail.setError("Please enter email address");
            validated = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etForgetEmail.getText().toString()).matches()) {
            binding.etForgetEmail.setError("Please enter valid email address");
            validated = false;
        }
        return validated;
    }
}
