package com.saveethataskdoor.app.home.hod;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.saveethataskdoor.app.R;
import com.saveethataskdoor.app.base.BaseActivity;
import com.saveethataskdoor.app.databinding.ActivityHodBinding;
import com.saveethataskdoor.app.food.FoodShopActivity;
import com.saveethataskdoor.app.food.MyOrderActivity;
import com.saveethataskdoor.app.home.HODHomeActivity;
import com.saveethataskdoor.app.home.StaffHomeActivity;
import com.saveethataskdoor.app.login.LoginActivity;
import com.saveethataskdoor.app.utils.CommonUtils;

import java.util.Objects;

public class HODActivity extends BaseActivity {

    ActivityHodBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hod);
        initUI();
    }


    @Override
    public void initUI() {
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);

        binding.contentHod.tvLeaveLetter.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Please select year")
                    .setSingleChoiceItems(getResources().getStringArray(R.array.year_array), 0, null)
                    .setPositiveButton(android.R.string.ok, (dialog, whichButton) -> {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();

                        Intent intent = new Intent(this, HODHomeActivity.class);
                        intent.putExtra("year", selectedPosition + 1);
                        startActivity(intent);
                    })
                    .show();
        });

        binding.contentHod.tvComplaint.setOnClickListener(v -> {
            CommonUtils.sendMail(this);
        });

        binding.contentHod.tvOrderFood.setOnClickListener(v -> {
            startActivity(new Intent(this, FoodShopActivity.class));
        });
        binding.contentHod.tvMyOrders.setOnClickListener(v -> {
            startActivity(new Intent(this, MyOrderActivity.class));
        });
        binding.contentHod.tvTrackBus.setOnClickListener(v -> {
            CommonUtils.availableSoon(this);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

                break;
        }
        return true;
    }
}
