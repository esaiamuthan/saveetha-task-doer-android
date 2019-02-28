package com.saveethataskdoor.app.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class CommonUtils {

    @SuppressLint("IntentReset")
    public static void sendMail(Activity activity) {

        Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Complaints");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.setData(Uri.parse("mailto:principal@saveetha.ac.in")); // or just "mailto:" for blank
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        activity.startActivity(intent);
    }

    @SuppressLint("IntentReset")
    public static void availableSoon(Activity activity) {
        Toast.makeText(activity, "This feature will be available soon...", Toast.LENGTH_SHORT).show();
    }
}
