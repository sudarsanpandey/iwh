package com.example.spandey.iwh;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class signupActivity extends AppCompatActivity implements View.OnClickListener{

    Uri imageUri = null;
    private  final int TAKE_PICTURE = 1;
    private final int SELECT_PICTURE = 2;

    private static String SIGN_UP_URL="http://wcsc02.ad.ydesigngroup.com/signup";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        ImageButton ctc_btn = (ImageButton)findViewById(R.id.ctc_pic_btn);
        ctc_btn.setOnClickListener(this);

        Button upload_btn = (Button)findViewById(R.id.upload_pic_btn);
        upload_btn.setOnClickListener(this);

        Button sign_up_btn = (Button)findViewById(R.id.signup_btn);
        sign_up_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ctc_pic_btn:
                take_picture();
                break;
            case R.id.upload_pic_btn:
                select_picture();
                break;
            case R.id.signup_btn:
                post_data();
                break;
        }
    }
    public void select_picture(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
    }
    public void take_picture(){
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    /**
     * It will take the picture and save it to the given frame
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = imageUri;
                    getContentResolver().notifyChange(selectedImage, null);
                    ImageView imageView = (ImageView) findViewById(R.id.ctc_pic_btn);
                    ContentResolver cr = getContentResolver();
                    Bitmap bitmap;
                    try {
                        bitmap = android.provider.MediaStore.Images.Media
                                .getBitmap(cr, selectedImage);

                        imageView.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                                .show();
                        Log.e("Camera", e.toString());
                    }
                }
                break;
            case SELECT_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImage = data.getData();
                    getContentResolver().notifyChange(selectedImage, null);
                    ImageView imageView = (ImageView) findViewById(R.id.ctc_pic_btn);
                    ContentResolver cr = getContentResolver();
                    Bitmap bitmap;
                    try {
                        bitmap = android.provider.MediaStore.Images.Media
                                .getBitmap(cr, selectedImage);

                        imageView.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                                .show();
                        Log.e("Camera", e.toString());
                    }
                    //String selectedImagePath = getPath(selectedImage);
                }
                    break;
        }
    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        // this is our fallback here
        return uri.getPath();
    }

    public void post_data(){
        String username = String.valueOf(((TextView) findViewById(R.id.new_username_tf)).getText());
        String password = sha256(String.valueOf(((TextView)findViewById(R.id.new_password_tf)).getText()));

        String first_name = String.valueOf(((TextView) findViewById(R.id.new_firstname_tf)).getText());
        String last_name = String.valueOf(((TextView)findViewById(R.id.new_lastname_tf)).getText());

       // Toast.makeText(this,"Username : "+username+"\npassword : "+password+"\nfirst_name : "+first_name+"\nlast_name : "+last_name,Toast.LENGTH_LONG).show();
    }

    /**
     * Generates a SHA-256 Hash for any string
     * @param base
     * @return
     */
    private String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException nse){
            Toast.makeText(signupActivity.this,"Algorithm not supported",Toast.LENGTH_LONG).show();
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
        return null;
    }

    public class signupClass extends AsyncTask<String, Integer, String> {
        String response = "NOK";
        @Override
        protected void onPreExecute() {

        };

        @Override
        protected String doInBackground(String... params) {
            String post_url = SIGN_UP_URL;

            try {
                URL url = new URL(post_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                //set connection Header
                conn.setRequestMethod("POST");
                //conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", params[0]);
                jsonObject.put("password", params[1]);
                jsonObject.put("first_name",params[2]);
                jsonObject.put("last_name",params[3]);
                //System.out.println("username: "+params[0]+" password:"+params[1]);
                //send post request
                conn.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(jsonObject.toString());
                wr.flush();
                wr.close();

                String reply="FALSE";
                int responseCode = conn.getResponseCode();
                //System.out.println("Response CODE : "+responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) { //success,200 code
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    //System.out.println(response); //gets the response
                    reply = String.valueOf(response);
                }
                conn.disconnect();
                response=reply;
                return null;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException ioe){
                ioe.printStackTrace();
            }catch (Exception aOE){
                aOE.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            //if response is not ok
            if(!response.equals("OK")){
                Toast.makeText(signupActivity.this,"Login Unsuccessful!! \nPlease Check Credentials!!",Toast.LENGTH_LONG).show();
            }
            else if (response.equals("OK")){
                Toast.makeText(signupActivity.this, "PostExecute" + ":" + response, Toast.LENGTH_LONG).show();
            }
            return;
        }
    }
}
