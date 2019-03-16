package com.saveethataskdoor.app.attendance;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.saveethataskdoor.app.R;
import com.saveethataskdoor.app.base.BaseActivity;
import com.saveethataskdoor.app.databinding.ActivityYearsBinding;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class YearsActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener {

    ActivityYearsBinding binding;
    Calendar newCalendar = Calendar.getInstance();
    Calendar current = Calendar.getInstance();

    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_years);

        initUI();

        binding.btFirstYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPeriodView(1);
            }
        });
        binding.btSecondYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPeriodView(2);
            }
        });
        binding.btThirdYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPeriodView(3);
            }
        });
        binding.btForthYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPeriodView(4);
            }
        });

        selectedDate = dateFormatter.format(current.getTime());
        binding.tvDate.setText(String.format("Date : %s", selectedDate));
    }

    @Override
    public void initUI() {
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    }

    private void openPeriodView(int year) {
        Intent intent = new Intent(this, PeriodActivity.class);
        intent.putExtra("year", year);
        intent.putExtra("date", selectedDate);
        if (newCalendar.before(current)) {
            intent.putExtra("history", true);
        } else if (newCalendar.equals(current)) {
            intent.putExtra("history", false);
        }
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_history:
                openDatePickerDialog();
                return true;
        }
        return false;
    }

    private void openDatePickerDialog() {
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                YearsActivity.this,
                current.get(Calendar.YEAR),
                current.get(Calendar.MONTH),
                current.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.setMaxDate(current);
        datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        newCalendar.set(year, monthOfYear, dayOfMonth);
        selectedDate = dateFormatter.format(newCalendar.getTime());
        binding.tvDate.setText(String.format("Date : %s", selectedDate));
    }
}
