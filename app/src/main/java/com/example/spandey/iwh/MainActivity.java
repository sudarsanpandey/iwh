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
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
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

	private static String LOGIN_URL="http://wcsc02.ad.ydesigngroup.com/login";
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
		//get the menu item id first
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
                String password = sha256(String.valueOf(pass.getText()));
                if(password.length() > 0){
                    new postLoginClass().execute(username, password);
                }
                break;
        }
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
             Toast.makeText(MainActivity.this,"Algorithm not supported",Toast.LENGTH_LONG).show();
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
        return null;
    }

    public class postLoginClass extends AsyncTask<String, Integer, String> {
        String response = "NOK";
        @Override
        protected void onPreExecute() {

        };

        @Override
        protected String doInBackground(String... params) {
            String post_url = LOGIN_URL;

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
                Toast.makeText(MainActivity.this,"Login Unsuccessful!! \nPlease Check Credentials!!",Toast.LENGTH_LONG).show();
            }
            else if (response.equals("OK")){
                Toast.makeText(MainActivity.this, "PostExecute" + ":" + response, Toast.LENGTH_LONG).show();
            }
            return;
        }
    }
}
