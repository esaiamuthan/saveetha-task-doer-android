package com.saveethataskdoor.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.saveethataskdoor.app.base.BaseActivity;
import com.saveethataskdoor.app.databinding.ActivityReviewBinding;
import com.saveethataskdoor.app.model.Leave;
import com.saveethataskdoor.app.model.Token;
import com.saveethataskdoor.app.model.User;
import com.saveethataskdoor.app.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class ReviewActivity extends BaseActivity {

    private ActivityReviewBinding binding;

    private FirebaseAuth mAuth;

    private String TAG = ReviewActivity.class.getSimpleName();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private User currentUserInfo;

    private String dateType = "";

    private Leave leave;

    Calendar calendar;
    String dateFormat = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_review);
        initUI();
    }


    @SuppressLint("DefaultLocale")
    @Override
    public void initUI() {

        calendar = Calendar.getInstance();

        if (calendar.get(Calendar.MONTH) < 10) {
            if (calendar.get(Calendar.DAY_OF_MONTH) < 10)
                dateFormat = String.format("0%d-0%d-%d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
            else
                dateFormat = String.format("%d-0%d-%d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        } else
            dateFormat = String.format("%d-%d-%d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));

        leave = (Leave) getIntent().getSerializableExtra("leave");

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        db.collection("saveetha")
                .document(Objects.requireNonNull(mAuth.getUid()))
                .get().addOnSuccessListener(documentSnapshot -> {
            currentUserInfo = documentSnapshot.toObject(User.class);

            handleViews();
        });

        binding.contentReview.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.accept) {
                binding.contentReview.etRejectReason.setVisibility(View.GONE);
            } else if (checkedId == R.id.reject) {
                binding.contentReview.etRejectReason.setVisibility(View.VISIBLE);
            }
        });

        switch (leave.getLeaveType()) {
            case "Fever":
                loadContentFever();
                break;
            case "Sick":
                loadContentSick();
                break;
            case "Marriage function":
                loadContent();
                break;
            case "Vacation":
                loadContentVacation();
                break;
        }

        if (leave.getFileURI() != null) {
            binding.contentReview.tvFile.setVisibility(View.VISIBLE);
        } else {
            binding.contentReview.tvFile.setVisibility(View.GONE);
        }

        binding.contentReview.tvFile.setOnClickListener(v -> {
            Intent intent
                    = new Intent(this, WebActivity.class);
            intent.putExtra("url", leave.getFileURI());
            startActivity(intent);
        });
    }

    private void handleViews() {
        switch (PreferenceManager.getProfileType(this)) {
            case "Student":
                binding.contentReview.radioGroup.setVisibility(View.GONE);
                break;
            case "Staff":
                if (leave.getStatus() == 104 || leave.getStatus() == 101)
                    binding.contentReview.radioGroup.setVisibility(View.GONE);
                else
                    binding.contentReview.radioGroup.setVisibility(View.VISIBLE);
                break;
            case "HOD":
                if (leave.getStatus() == 104 || leave.getStatus() == 102)
                    binding.contentReview.radioGroup.setVisibility(View.GONE);
                else
                    binding.contentReview.radioGroup.setVisibility(View.VISIBLE);
                break;
            case "Principal":
                if (leave.getStatus() == 104 || leave.getStatus() == 103)
                    binding.contentReview.radioGroup.setVisibility(View.GONE);
                else if (leave.getStatus() == 102)
                    binding.contentReview.radioGroup.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_review, menu);

        if (leave.getStatus() == 103)
            menu.findItem(R.id.action_print).setVisible(true);
        else
            menu.findItem(R.id.action_print).setVisible(false);

        switch (PreferenceManager.getProfileType(this)) {
            case "Student":
                menu.findItem(R.id.action_submit).setVisible(false);
                break;
            case "Staff":
                if (leave.getStatus() == 100) {
                    menu.findItem(R.id.action_submit).setVisible(true);
                } else
                    menu.findItem(R.id.action_submit).setVisible(false);
                break;
            case "HOD":
                if (leave.getStatus() == 101) {
                    menu.findItem(R.id.action_submit).setVisible(true);
                } else
                    menu.findItem(R.id.action_submit).setVisible(false);
                break;
            case "Principal":
                if (leave.getStatus() == 102) {
                    menu.findItem(R.id.action_submit).setVisible(true);
                } else
                    menu.findItem(R.id.action_submit).setVisible(false);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                hideKeyboard();
                finish();
                break;
            case R.id.action_print:
                createWebPrintJob(binding.contentReview.webView);
                break;
            case R.id.action_submit:
                hideKeyboard();
                if (binding.contentReview.accept.isChecked()) {
                    callUpdateLeave(true);
                } else if (binding.contentReview.reject.isChecked()) {
                    callUpdateLeave(false);
                } else {
                    Toast.makeText(this, "Please select accept or reject", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
    }

    private void createWebPrintJob(WebView webView) {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

        // Get a print adapter instance
        PrintDocumentAdapter printAdapter;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            printAdapter = webView.createPrintDocumentAdapter("content.pdf");
        } else {
            printAdapter = webView.createPrintDocumentAdapter();
        }

        // Create a print job with name and adapter instance
        String jobName = getString(R.string.app_name) + " Document";

        PrintAttributes attributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(new PrintAttributes.Resolution("id", Context.PRINT_SERVICE, 200, 200))
                .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .build();

        printManager.print(jobName, printAdapter, attributes);


    }

    private void callUpdateLeave(boolean isAccepted) {
        binding.linearProgress.setVisibility(View.VISIBLE);

        if (isAccepted) {
            switch (PreferenceManager.getProfileType(this)) {
                case "Staff":
                    leave.setStatus(101);
                    break;
                case "HOD":
                    leave.setStatus(102);
                    break;
                case "Principal":
                    leave.setStatus(103);
                    break;
            }
        } else {
            leave.setStatus(104);
            leave.setRejectedReason(binding.contentReview.etRejectReason.getText().toString());

            switch (PreferenceManager.getProfileType(this)) {
                case "Staff":
                    leave.setRejectedBy("Staff");
                    break;
                case "HOD":
                    leave.setRejectedBy("HOD");
                    break;
                case "Principal":
                    leave.setRejectedBy("Principal");
                    break;
            }
        }

        db.collection("saveetha")
                .document("leave_forms")
                .collection("leave_letters")
                .document(leave.documentId)
                .set(leave)
                .addOnSuccessListener(documentReference -> {
                    binding.linearProgress.setVisibility(View.GONE);

                    setPush(leave);

                    finish();
                })
                .addOnFailureListener(throable -> {
                    binding.linearProgress.setVisibility(View.GONE);

                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure" + throable.getMessage());
                    try {
                        throw Objects.requireNonNull(throable);
                    } catch (FirebaseAuthWeakPasswordException e) {
                        Toast.makeText(ReviewActivity.this, "Weak password.",
                                Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(ReviewActivity.this, "Invalid email address.",
                                Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthUserCollisionException e) {
                        Toast.makeText(ReviewActivity.this, "User already exists.",
                                Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(ReviewActivity.this, "" + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setPush(Leave leave) {
        switch (PreferenceManager.getProfileType(this)) {
            case "Staff":
                db.collection("tokens")
                        .whereEqualTo("uId", leave.getuId())
                        .whereEqualTo("type", "Student")
                        .whereEqualTo("department", leave.getDepartment())
                        .whereArrayContains("year", leave.getYear())
                        .limit(1)
                        .get().addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        Token token = document.toObject(Token.class);
                        if (leave.getStatus() == 104)
                            sendNotification(token.getToken(), "Request rejected by Staff \n Reason : " + leave.getRejectedReason());
                        else
                            sendNotification(token.getToken(), "Request forwarded to HOD");
                    }
                });
                if (leave.getStatus() != 104)
                    db.collection("tokens")
                            .whereEqualTo("type", "HOD")
                            .whereEqualTo("department", leave.getDepartment())
                            .limit(1)
                            .get().addOnCompleteListener(task -> {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Token token = document.toObject(Token.class);

                            sendNotification(token.getToken(), "Request came from Staff " + token.getName() + " for " + leave.getName());
                        }
                    });
                break;
            case "HOD":

                db.collection("tokens")
                        .whereEqualTo("uId", leave.getuId())
                        .whereEqualTo("type", "Student")
                        .whereEqualTo("department", leave.getDepartment())
                        .whereArrayContains("year", leave.getYear())
                        .limit(1)
                        .get().addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        Token token = document.toObject(Token.class);
                        if (leave.getStatus() == 104)
                            sendNotification(token.getToken(), "Request rejected by HOD \n Reason : " + leave.getRejectedReason());
                        else
                            sendNotification(token.getToken(), "Request forwarded to Principal");
                    }
                });
                db.collection("tokens")
                        .whereEqualTo("type", "Staff")
                        .whereEqualTo("department", leave.getDepartment())
                        .whereArrayContains("year", leave.getYear())
                        .limit(1)
                        .get().addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        Token token = document.toObject(Token.class);
                        if (leave.getStatus() == 104)
                            sendNotification(token.getToken(), "Request rejected by HOD for " + leave.getName() + "\n Reason : " + leave.getRejectedReason());
                        else
                            sendNotification(token.getToken(), "Request forwarded to Principal for " + leave.getName());
                    }
                });
                if (leave.getStatus() != 104)
                    db.collection("tokens")
                            .whereEqualTo("type", "Principal")
                            .limit(1)
                            .get().addOnCompleteListener(task -> {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Token token = document.toObject(Token.class);

                            sendNotification(token.getToken(), "Request came from HOD of " + leave.getDepartment() + " for " + leave.getName());
                        }
                    });
                break;
            case "Principal":

                db.collection("tokens")
                        .whereEqualTo("uId", leave.getuId())
                        .whereEqualTo("type", "Student")
                        .whereEqualTo("department", leave.getDepartment())
                        .whereArrayContains("year", leave.getYear())
                        .limit(1)
                        .get().addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        Token token = document.toObject(Token.class);
                        if (leave.getStatus() == 104)
                            sendNotification(token.getToken(), "Request rejected by Principal \n Reason : " + leave.getRejectedReason());
                        else
                            sendNotification(token.getToken(), "Leave Approved by Principal");
                    }
                });
                db.collection("tokens")
                        .whereEqualTo("type", "Staff")
                        .whereEqualTo("department", leave.getDepartment())
                        .whereArrayContains("year", leave.getYear())
                        .limit(1)
                        .get().addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        Token token = document.toObject(Token.class);
                        if (leave.getStatus() == 104)
                            sendNotification(token.getToken(), "Request rejected by Principal for " + leave.getName() + "\n Reason : " + leave.getRejectedReason());
                        else
                            sendNotification(token.getToken(), "Leave Approved by Principal for " + leave.getName());
                    }
                });
                db.collection("tokens")
                        .whereEqualTo("type", "HOD")
                        .whereEqualTo("department", leave.getDepartment())
                        .limit(1)
                        .get().addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        Token token = document.toObject(Token.class);
                        if (leave.getStatus() == 104)
                            sendNotification(token.getToken(), "Request rejected by Principal for " + leave.getName() + "\n Reason : " + leave.getRejectedReason());
                        else
                            sendNotification(token.getToken(), "Leave Approved by Principal for " + leave.getName());
                    }
                });
                break;
        }
    }

    private void loadContent() {
        String year = "";
        if (leave.getYear() == 1)
            year = leave.getYear() + "st year";
        else if (leave.getYear() == 2)
            year = leave.getYear() + "nd year";
        else if (leave.getYear() == 3)
            year = leave.getYear() + "rd year";
        else if (leave.getYear() == 4)
            year = leave.getYear() + "th year";

        String htmlContent = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "<div class=\"ct-table-wrap\">\n" +
                "<table class=\"ct-table ct-table-bordered\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td><p align=\"right\">Date : " + dateFormat +
                "</p><br><b>From</b><br>" +
                "" + leave.getName() + " - " + leave.getUserId() + "<br>" +
                "" + leave.getDepartment() + ", " + year + "<br>" +
                " Saveetha Engineering College<br> " +
                " Poonamalli<br>" +
                "<br><b>To</b><br>" +
                "The Principal<br>" +
                " Saveetha Engineering College<br> " +
                " Poonamalli<br><br>" +
                "\n" +
                "<div><b>Subject : </b> Requesting leave for " + leave.getSubject() + "</div>\n" +
                "<br><br>" +
                "Respected Sir/Madam,<br><br>As I need to attend my "
                + leave.getSubject() + "," +
                "I am unable to attend the regular classes From : " +
                "" + leave.getStartDate() + " To : " + leave.getEndDate() + "" +
                ".And hence I kindly request you to grant me leave on that days." +
                " <br><br>Thanking you!<br><br>Your's Sincerely,<br>" + leave.getName() + "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</div>\n" +
                "\n" +
                "</body>\n" +
                "</html>";

        binding.contentReview.webView.loadData(htmlContent, "text/html", "UTF-8");
    }

    private void loadContentVacation() {
        String year = "";
        if (leave.getYear() == 1)
            year = leave.getYear() + "st year";
        else if (leave.getYear() == 2)
            year = leave.getYear() + "nd year";
        else if (leave.getYear() == 3)
            year = leave.getYear() + "rd year";
        else if (leave.getYear() == 4)
            year = leave.getYear() + "th year";

        String htmlContent = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "<div class=\"ct-table-wrap\">\n" +
                "<table class=\"ct-table ct-table-bordered\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td><p align=\"right\">Date : " + dateFormat +
                "</p><br><b>From</b><br>" +
                "" + leave.getName() + " - " + leave.getUserId() + "<br>" +
                "" + leave.getDepartment() + ", " + year + "<br>" +
                " Saveetha Engineering College<br> " +
                " Poonamalli<br>" +
                "<br><b>To</b><br>" +
                "The Principal<br>" +
                " Saveetha Engineering College<br> " +
                " Poonamalli<br><br>" +
                "\n" +
                "<div><b>Subject : </b> Requesting leave for " + leave.getSubject() + "</div>\n" +
                "<br><br>" +
                "Respected Sir/Madam,<br><br>As I am going for  "
                + leave.getSubject() + "," +
                "I am unable to attend the regular classes From : " +
                "" + leave.getStartDate() + " To : " + leave.getEndDate() + "" +
                ".And hence I kindly request you to grant me leave on that days." +
                " <br><br>Thanking you!<br><br>Your's Sincerely,<br>" + leave.getName() + "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</div>\n" +
                "\n" +
                "</body>\n" +
                "</html>";

        binding.contentReview.webView.loadData(htmlContent, "text/html", "UTF-8");
    }

    private void loadContentSick() {
        String year = "";
        if (leave.getYear() == 1)
            year = leave.getYear() + "st year";
        else if (leave.getYear() == 2)
            year = leave.getYear() + "nd year";
        else if (leave.getYear() == 3)
            year = leave.getYear() + "rd year";
        else if (leave.getYear() == 4)
            year = leave.getYear() + "th year";

        String htmlContent = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "<div class=\"ct-table-wrap\">\n" +
                "<table class=\"ct-table ct-table-bordered\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td><p align=\"right\">Date : " + dateFormat +
                "</p><br><b>From</b><br>" +
                "" + leave.getName() + " - " + leave.getUserId() + "<br>" +
                "" + leave.getDepartment() + ", " + year + "<br>" +
                " Saveetha Engineering College<br> " +
                " Poonamalli<br>" +
                "<br><b>To</b><br>" +
                "The Principal<br>" +
                " Saveetha Engineering College<br> " +
                " Poonamalli<br><br>" +
                "\n" +
                "<div><b>Subject : </b> Requesting leave for " + leave.getSubject() + "</div>\n" +
                "<br><br>" +
                "Respected Sir/Madam,<br><br>" +
                "Due to "
                + leave.getSubject() + "," +
                "I am unable to attend the regular classes From : " +
                "" + leave.getStartDate() + " To : " + leave.getEndDate() + "" +
                ".And hence I kindly request you to grant me leave on that days." +
                " <br><br>Thanking you!<br><br>Your's Sincerely,<br>" + leave.getName() + "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</div>\n" +
                "\n" +
                "</body>\n" +
                "</html>";

        binding.contentReview.webView.loadData(htmlContent, "text/html", "UTF-8");
    }

    private void loadContentFever() {
        String year = "";
        if (leave.getYear() == 1)
            year = leave.getYear() + "st year";
        else if (leave.getYear() == 2)
            year = leave.getYear() + "nd year";
        else if (leave.getYear() == 3)
            year = leave.getYear() + "rd year";
        else if (leave.getYear() == 4)
            year = leave.getYear() + "th year";

        String htmlContent = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<body>\n" +
                "<div class=\"ct-table-wrap\">\n" +
                "<table class=\"ct-table ct-table-bordered\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td><p align=\"right\">Date : " + dateFormat +
                "</p><br><b>From</b><br>" +
                "" + leave.getName() + " - " + leave.getUserId() + "<br>" +
                "" + leave.getDepartment() + ", " + year + "<br>" +
                " Saveetha Engineering College<br> " +
                " Poonamalli<br>" +
                "<br><b>To</b><br>" +
                "The Principal<br>" +
                " Saveetha Engineering College<br> " +
                " Poonamalli<br><br>" +
                "\n" +
                "<div><b>Subject : </b> Requesting leave for " + leave.getSubject() + "</div>\n" +
                "<br><br>" +
                "Respected Sir/Madam,<br><br>" +
                "Due to "
                + leave.getSubject() + "," +
                "I am unable to attend the regular classes on : " +
                "" + leave.getStartDate() + "" +
                ".And hence I kindly request you to grant me leave on that day." +
                " <br><br>Thanking you!<br><br>Your's Sincerely,<br>" + leave.getName() + "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</div>\n" +
                "\n" +
                "</body>\n" +
                "</html>";

        binding.contentReview.webView.loadData(htmlContent, "text/html", "UTF-8");
    }
}
