package com.saveethataskdoor.app;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class PushNotifictionHelper {

    public final static String AUTH_KEY_FCM = "AIzaSyAEPsewyx7cgArvgiO-ew0-jT-bWGgpMhk";
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";

    public static String sendPushNotification(String deviceToken)
            throws IOException {
        String result = "";
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
            info.put("body", "message body"); // Notification
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // body
        try {
            json.put("notification", info);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public void sendAndroidPush(String input) {
        final String apiKey = "AIzaSyAEPsewyx7cgArvgiO-ew0-jT-bWGgpMhk";
        StringBuffer response = new StringBuffer();
        try {
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + apiKey);

            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            os.write(input.getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + input);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;


            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        // print result
        System.out.println(response.toString());
    }
}
