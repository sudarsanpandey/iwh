package com.example.spandey.iwh;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button sign_in = (Button)findViewById(R.id.sign_in_btn);
        sign_in.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_btn:
                // do stuff
                TextView login = (TextView)findViewById(R.id.login_tf);
                TextView pass = (TextView)findViewById(R.id.pass_tf);
                String username = String.valueOf(login.getText());
                String password = "";
                try {
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    byte[] hash = digest.digest(String.valueOf(pass.getText()).getBytes(StandardCharsets.UTF_8));
                    password = hash.toString();
                }catch (NoSuchAlgorithmException nse){
                    Toast.makeText(this,"Algorithm not supported",Toast.LENGTH_LONG).show();
                }
                if(password.length() > 0){
                    new postLoginClass().execute(username, password);
                }
                break;
        }
    }


    public class postLoginClass extends AsyncTask<String, Integer, String> {
        String response = "NOK";
        @Override
        protected void onPreExecute() {

        };

        @Override
        protected String doInBackground(String... params) {

            String post_url = "http://192.168.1.241/login_post.php";
            String POST_PARAMS = "username="+params[0]+"&password="+params[1];

            URL url = null;
            try {
                url = new URL(post_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");

                conn.setDoOutput(true);
                OutputStream os = new BufferedOutputStream(conn.getOutputStream());
                os.write(POST_PARAMS.getBytes());
                os.flush();
                os.close();
                // For POST only - END

                String reply="FALSE";
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) { //success
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    reply = String.valueOf(response);
                }
                if (reply.toLowerCase().equals("ok")){
                    response = reply;
                    return "OK";
                }
                conn.disconnect();
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
            // TODO Auto-generated method stub
            Toast.makeText(MainActivity.this,"PostExecute"+":"+response,Toast.LENGTH_LONG).show();
            return;
        }
    }
}
