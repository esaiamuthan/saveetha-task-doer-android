package com.saveethataskdoor.app.home;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.saveethataskdoor.app.R;
import com.saveethataskdoor.app.base.BaseActivity;
import com.saveethataskdoor.app.bonofide.BonofideActivity;
import com.saveethataskdoor.app.databinding.ActivityHomeBinding;
import com.saveethataskdoor.app.food.FoodShopActivity;
import com.saveethataskdoor.app.food.MyOrderActivity;
import com.saveethataskdoor.app.leave.LeaveListActivity;
import com.saveethataskdoor.app.login.LoginActivity;
import com.saveethataskdoor.app.utils.CommonUtils;

public class HomeActivity extends BaseActivity {

    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        initUI();
    }

    @Override
    public void initUI() {
        setSupportActionBar(binding.toolbar);

        binding.contentHome.tvLeaveLetter.setOnClickListener(v -> {
            startActivity(new Intent(this, LeaveListActivity.class));
        });
        binding.contentHome.tvBonofide.setOnClickListener(v -> {
            Intent intent = new Intent(this, BonofideActivity.class);
            intent.putExtra("profile", "student");
            startActivity(intent);
        });

        binding.contentHome.tvComplaint.setOnClickListener(v -> {
            CommonUtils.sendMail(this);
        });
        binding.contentHome.tvOrderFood.setOnClickListener(v -> {
            startActivity(new Intent(this, FoodShopActivity.class));
        });
        binding.contentHome.tvMyOrders.setOnClickListener(v -> {
            startActivity(new Intent(this, MyOrderActivity.class));
        });

        binding.contentHome.tvTrackBus.setOnClickListener(v -> {
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
            case android.R.id.home:
                finish();
                break;
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

                break;
        }
        return true;
    }
}
