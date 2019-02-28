package com.saveethataskdoor.app.leave;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saveethataskdoor.app.R;
import com.saveethataskdoor.app.base.BaseActivity;
import com.saveethataskdoor.app.databinding.ActivityLeaveCreateBinding;
import com.saveethataskdoor.app.model.User;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LeaveCreateActivity extends BaseActivity
        implements DatePickerDialog.OnDateSetListener {

    private ActivityLeaveCreateBinding binding;

    private FirebaseAuth mAuth;

    private String TAG = LeaveCreateActivity.class.getSimpleName();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private User currentUserInfo;

    private String dateType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_leave_create);
        initUI();
    }

    @Override
    public void initUI() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        binding.linearProgress.setVisibility(View.VISIBLE);
        db.collection("saveetha")
                .document(Objects.requireNonNull(mAuth.getUid()))
                .get().addOnSuccessListener(documentSnapshot -> {
            currentUserInfo = documentSnapshot.toObject(User.class);

            binding.linearProgress.setVisibility(View.GONE);
        });

        binding.spLeaveType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    binding.tlDate.setVisibility(View.GONE);
                    binding.tlStartDate.setVisibility(View.GONE);
                    binding.tlEndDate.setVisibility(View.GONE);
                } else if (position == 1) {
                    binding.tlDate.setVisibility(View.VISIBLE);
                    binding.tlStartDate.setVisibility(View.GONE);
                    binding.tlEndDate.setVisibility(View.GONE);
                } else {
                    binding.tlDate.setVisibility(View.GONE);
                    binding.tlStartDate.setVisibility(View.VISIBLE);
                    binding.tlEndDate.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        binding.etDate.setOnClickListener(v -> {
            dateType = "date";
            openDatePickerDialog();
        });

        binding.etStartDate.setOnClickListener(v -> {
            dateType = "start_date";
            openDatePickerDialog();
        });

        binding.etEndDate.setOnClickListener(v -> {
            dateType = "end_date";
            openDatePickerDialog();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                hideKeyboard();
                finish();
                break;
            case R.id.action_submit:
                hideKeyboard();
                if (checkFields()) {
                    callLetterCreateAPI();
                }
                break;
        }
        return true;
    }

    private void callLetterCreateAPI() {
        binding.linearProgress.setVisibility(View.VISIBLE);

        Map<String, Object> user = new HashMap<>();
        user.put("name", currentUserInfo.getName());
        user.put("userId", currentUserInfo.getUserId());
        user.put("department", currentUserInfo.getDepartment());
        user.put("year", currentUserInfo.getYearList().get(0));
        user.put("uId", mAuth.getUid());

        user.put("leaveType", binding.spLeaveType.getSelectedItem().toString());
        user.put("subject", binding.etSubject.getText().toString());
        user.put("date", binding.etDate.getText().toString());
        user.put("startDate", binding.etStartDate.getText().toString());
        user.put("endDate", binding.etEndDate.getText().toString());

        user.put("status", 100);
        user.put("rejectedReason", "");
        user.put("rejectedBy", "");
        user.put("createdAt", System.currentTimeMillis());

        // Add a new document with a generated ID

        db.collection("saveetha")
                .document("leave_forms")
                .collection("leave_letters")
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    binding.linearProgress.setVisibility(View.GONE);
                    finish();
                })
                .addOnFailureListener(throable -> {
                    binding.linearProgress.setVisibility(View.GONE);

                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure" + throable.getMessage());
                    try {
                        throw Objects.requireNonNull(throable);
                    } catch (FirebaseAuthWeakPasswordException e) {
                        Toast.makeText(LeaveCreateActivity.this, "Weak password.",
                                Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(LeaveCreateActivity.this, "Invalid email address.",
                                Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthUserCollisionException e) {
                        Toast.makeText(LeaveCreateActivity.this, "User already exists.",
                                Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(LeaveCreateActivity.this, "" + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean checkFields() {
        boolean validated = true;
        if (binding.spLeaveType.getSelectedItemPosition() == 0) {
            showMessage("Please select leave type");
            validated = false;
        } else if (binding.etSubject.getText().toString().isEmpty()) {
            binding.etSubject.setError("Please enter subject");
            validated = false;
        } else if (!binding.scAccept.isChecked()) {
            Toast.makeText(this, "Please accept to forward", Toast.LENGTH_SHORT).show();
            validated = false;
        } else {
            if (binding.spLeaveType.getSelectedItemPosition() == 1) {
                if (binding.etDate.getText().toString().isEmpty()) {
                    binding.etDate.setError("Please select date");
                    validated = false;
                }
            } else {
                if (binding.etStartDate.getText().toString().isEmpty()) {
                    binding.etStartDate.setError("Please select start date");
                    validated = false;
                } else if (binding.etEndDate.getText().toString().isEmpty()) {
                    binding.etEndDate.setError("Please select end date");
                    validated = false;
                }
            }
        }
        return validated;
    }

    private void openDatePickerDialog() {
        hideKeyboard();
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(this,
                R.style.MyDialogTheme,
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String dateFormat = "";
        if (month < 10) {
            if (dayOfMonth < 10)
                dateFormat = String.format("0%d-0%d-%d", dayOfMonth, month + 1, year);
            else
                dateFormat = String.format("%d-0%d-%d", dayOfMonth, month + 1, year);
        } else
            dateFormat = String.format("%d-%d-%d", dayOfMonth, month + 1, year);

        if (dateType.equals("date")) {
            binding.etDate.setText(dateFormat);
        } else if (dateType.equals("start_date")) {
            binding.etStartDate.setText(dateFormat);
        } else if (dateType.equals("end_date")) {
            binding.etEndDate.setText(dateFormat);
        }
    }
}
