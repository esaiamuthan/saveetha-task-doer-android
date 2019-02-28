package com.saveethataskdoor.app.bonofide;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saveethataskdoor.app.R;
import com.saveethataskdoor.app.ReviewActivity;
import com.saveethataskdoor.app.base.BaseActivity;
import com.saveethataskdoor.app.databinding.ActivityBonofideReviewBinding;
import com.saveethataskdoor.app.databinding.ActivityReviewBinding;
import com.saveethataskdoor.app.model.Bonofide;
import com.saveethataskdoor.app.model.Leave;
import com.saveethataskdoor.app.model.User;
import com.saveethataskdoor.app.utils.PreferenceManager;

import java.util.Calendar;
import java.util.Objects;

public class BonofideReviewActivity extends BaseActivity {

    private ActivityBonofideReviewBinding binding;

    private FirebaseAuth mAuth;

    private String TAG = ReviewActivity.class.getSimpleName();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private User currentUserInfo;

    private String dateType = "";

    Bonofide bonofide;

    String dateFormat = "";
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_bonofide_review);
        initUI();
    }

    @Override
    public void initUI() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bonofide = (Bonofide) getIntent().getSerializableExtra("bonofide");

        calendar = Calendar.getInstance();

        if (calendar.get(Calendar.MONTH) < 10) {
            if (calendar.get(Calendar.DAY_OF_MONTH) < 10)
                dateFormat = String.format("0%d-0%d-%d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
            else
                dateFormat = String.format("%d-0%d-%d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));
        } else
            dateFormat = String.format("%d-%d-%d", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR));


        mAuth = FirebaseAuth.getInstance();

        db.collection("saveetha")
                .document(Objects.requireNonNull(mAuth.getUid()))
                .get().addOnSuccessListener(documentSnapshot -> {
            currentUserInfo = documentSnapshot.toObject(User.class);

            handleViews();

            loadContentFever();
        });

    }

    private void handleViews() {
        if (PreferenceManager.getProfileType(this).equals("Student")) {
            binding.contentBonofideReview.accept.setVisibility(View.GONE);
        } else {
            binding.contentBonofideReview.accept.setVisibility(View.VISIBLE);
        }
    }


    private void loadContentFever() {
        String year = "";
        if (bonofide.getYear() == 1)
            year = bonofide.getYear() + "st year";
        else if (bonofide.getYear() == 2)
            year = bonofide.getYear() + "nd year";
        else if (bonofide.getYear() == 3)
            year = bonofide.getYear() + "rd year";
        else if (bonofide.getYear() == 4)
            year = bonofide.getYear() + "th year";

        String htmlContent = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<title>Page Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<div class=\"ct-table-wrap\">\n" +
                "<table class=\"ct-table ct-table-bordered\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td><p align=\"right\">Date : " + dateFormat + "</p><br><b>\n" +
                "<br><b>From</b><br>" + bonofide.getName() + "<br>" +
                "" + bonofide.getCollegeId() + "<br>" +
                "Saveetha Engineering College, Thandalam<br><br>\n" +
                "\n" +
                "<br><b>To</b><br> The Principal<br>" +
                "Saveetha Engineering College, Thandalam<br><br>\n" +
                "\n" +
                "<div><b>Subject : </b>" + bonofide.getSubject() + "</\n" +
                "<br><br>Respected sir/madam,<br><br>I " + bonofide.getName() + " Son/Daughter of " + bonofide.getFatherName() + ",Studying " + bonofide.getDepartment() + " - " + year + "need a bonafide certificate for " + bonofide.getSubject() + ".So ,I Kindly request you to issue me a bonafied certificate. <br><br>Thanking you!<br><br>Your's Sincerely,<br>" + bonofide.getName() + "</td>" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</div>\n" +
                "\n" +
                "</body>\n" +
                "</html>\n";
        binding.contentBonofideReview.webView.loadData(htmlContent, "text/html", "UTF-8");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_review, menu);

        menu.findItem(R.id.action_print).setVisible(false);
        if (PreferenceManager.getProfileType(this).equals("Student"))
            menu.findItem(R.id.action_submit).setVisible(false);
        else if (bonofide.getStatus() == 100) {
            menu.findItem(R.id.action_submit).setVisible(true);
        } else
            menu.findItem(R.id.action_submit).setVisible(false);

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
                if (binding.contentBonofideReview.accept.isChecked()) {
                    callUpdateBonofide();
                } else {
                    Toast.makeText(this, "Please select accept or reject", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
    }

    private void callUpdateBonofide() {
        binding.linearProgress.setVisibility(View.VISIBLE);

        bonofide.setStatus(101);

        db.collection("bonofide")
                .document(bonofide.getDocumentId())
                .set(bonofide)
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
                        Toast.makeText(this, "Weak password.",
                                Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(this, "Invalid email address.",
                                Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthUserCollisionException e) {
                        Toast.makeText(this, "User already exists.",
                                Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(this, "" + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
