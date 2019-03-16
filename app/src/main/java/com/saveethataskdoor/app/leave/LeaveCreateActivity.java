package com.saveethataskdoor.app.leave;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.saveethataskdoor.app.R;
import com.saveethataskdoor.app.base.BaseActivity;
import com.saveethataskdoor.app.databinding.ActivityLeaveCreateBinding;
import com.saveethataskdoor.app.model.Token;
import com.saveethataskdoor.app.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
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

                binding.chooseFile.setVisibility(View.GONE);

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

                    if (position == 2)
                        binding.chooseFile.setVisibility(View.VISIBLE);
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

        storageReference = FirebaseStorage.getInstance().getReference();
        binding.chooseFile.setOnClickListener(v -> {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant

                    return;
                } else
                    openAlertDialog();
            } else
                openAlertDialog();

        });
    }

    private static final int PICK_IMAGE_REQUEST = 234;
    private Uri filePath;
    private StorageReference storageReference;
    boolean isPDF = false;
    final static int PICK_PDF_CODE = 2342;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 88;

    //method to show file chooser
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //this function will get the pdf from the storage
    private void choosePDF() {
        //for greater than lolipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            return;
        }

        //creating an intent for file chooser
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PDF_CODE);
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            binding.tvFile.setText("File Selected");
        } else if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file
                filePath = data.getData();

                binding.tvFile.setText("File Selected");
            } else {
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! do the
                    // calendar task you need to do.
                    openAlertDialog();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }

    private void openAlertDialog() {
        String[] listItems = getResources().getStringArray(R.array.choose_array);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("Choose File Type");
        mBuilder.setSingleChoiceItems(listItems, -1, (dialogInterface, position) -> {
            if (position == 0) {
                isPDF = false;
                chooseImage();
            } else if (position == 1) {
                isPDF = true;
                choosePDF();
            }
            dialogInterface.dismiss();
        });

        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

    //this method will upload the file
    private void uploadFile(DocumentReference documentReference, Map<String, Object> user) {
        //if there is a file to upload
        if (filePath != null) {
            //displaying a progress dialog while upload is going on
            binding.linearProgress.setVisibility(View.VISIBLE);

            final StorageReference reference;

            if (isPDF)
                reference = storageReference.child("pdf/" + System.currentTimeMillis() + ".pdf");
            else
                reference = storageReference.child("images/" + System.currentTimeMillis() + ".jpg");

            reference.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        //if the upload is successfull
                        //hiding the progress dialog
                        binding.linearProgress.setVisibility(View.GONE);

                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri downloadUrl = uri;
                                Log.e("downloadUrl", downloadUrl.toString());


                                //Do what you want with the url

//                                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                                    intent.setDataAndType(downloadUrl, "application/pdf");
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    Intent newIntent = Intent.createChooser(intent, "Open File");
//                                    try {
//                                        startActivity(newIntent);
//                                    } catch (ActivityNotFoundException e) {
//                                        // Instruct the user to install a PDF reader here, or something
//                                    }

                                user.put("fileURI", downloadUrl.toString());

                                db.collection("saveetha")
                                        .document("leave_forms")
                                        .collection("leave_letters")
                                        .document(documentReference.getId())
                                        .set(user)
                                        .addOnSuccessListener(documentReference -> {
                                            sendPush();
                                            finish();
                                        });
                            }
                        });

                        //and displaying a success toast
//                        Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(exception -> {
                        //if the upload is not successfull
                        //hiding the progress dialog
                        binding.linearProgress.setVisibility(View.GONE);

                        //and displaying error message
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        //calculating progress percentage
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
        }
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
        user.put("uId", Objects.requireNonNull(mAuth.getUid()));

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

                    if (binding.spLeaveType.getSelectedItemPosition() == 2) {
                        uploadFile(documentReference, user);
                    } else {
                        sendPush();
                        finish();
                    }

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

    private void sendPush() {
        db.collection("tokens")
                .whereEqualTo("type", "Staff")
                .whereEqualTo("department", currentUserInfo.getDepartment())
                .whereArrayContains("year", currentUserInfo.getYearList().get(0))
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        Token token = document.toObject(Token.class);

                        sendNotification(token.getToken(),
                                "Leave request from " + currentUserInfo.getName());

                        break;
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
                } else if (binding.spLeaveType.getSelectedItemPosition() == 2) {
                    if (filePath == null) {
                        Toast.makeText(this, "Please upload a file", Toast.LENGTH_SHORT).show();
                        validated = false;
                    }
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
        String dateFormat;
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

    public final static String AUTH_KEY_FCM = "AIzaSyAEPsewyx7cgArvgiO-ew0-jT-bWGgpMhk";
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";

    public String sendPushNotification(String deviceToken)
            throws IOException {
        URL url = new URL(API_URL_FCM);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "key=" + AUTH_KEY_FCM);
        conn.setRequestProperty("Content-Type", "application/json");

        JSONObject json = new JSONObject();

        try {
            json.put("to", deviceToken.trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject info = new JSONObject();
        try {
            info.put("title", "notification title"); // Notification title
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            String year = "";
            if (currentUserInfo.getYearList().get(0) == 1)
                year = currentUserInfo.getYearList().get(0) + "st year";
            else if (currentUserInfo.getYearList().get(0) == 2)
                year = currentUserInfo.getYearList().get(0) + "nd year";
            else if (currentUserInfo.getYearList().get(0) == 3)
                year = currentUserInfo.getYearList().get(0) + "rd year";
            else if (currentUserInfo.getYearList().get(0) == 4)
                year = currentUserInfo.getYearList().get(0) + "th year";

            info.put("body", "Leave request from " + currentUserInfo.getName() + " " + year); // Notification
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // body
        try {
            json.put("notification", info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String result;
        try {
            OutputStreamWriter wr = new OutputStreamWriter(
                    conn.getOutputStream());
            wr.write(json.toString());
            wr.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
            result = "Success";
        } catch (Exception e) {
            e.printStackTrace();
            result = "Failure";
        }
        System.out.println("GCM Notification is sent successfully");
        return result;
    }

    @SuppressLint("StaticFieldLeak")
    public void sendNotification(String token) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {

                    sendPushNotification(token);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
}
