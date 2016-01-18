package com.spartan.team.smartwaterwatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by ranaf on 12/2/2015.
 */
public class ForgotPassword extends Activity {
    Button resetPassword, reLogin;
    EditText email;
    String uname = null;
    private CoordinatorLayout coordinatorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        resetPassword = (Button) findViewById(R.id.button1);
        reLogin = (Button) findViewById(R.id.button2);
        email = (EditText) findViewById(R.id.editText1);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uname = email.getText().toString();

                if (isNetworkConnected()) {
                    if (validation()) {
                        uname = email.getText().toString();
                        LoginTask task = new LoginTask();
                        task.execute(new String[]{uname});
                        Log.i("Email passed to Async",""+uname);

                    }
                } else {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "No Internet Connection !!", Snackbar.LENGTH_LONG);

                    snackbar.show();
                }


            }
        });
        reLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ForgotPassword.this, LoginActivity.class);
                startActivity(it);
                finish();

            }
        });

    }
    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub


            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("userId", params[0]));

            String result = null;
            String url = "http://smartwaterwatch.mybluemix.net/api/forgot";
            BufferedReader in = null;
            StringBuffer sb = new StringBuffer("");
            try {
                Log.d("1","");
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost(url);
                Log.d("2","");
                UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);
                Log.d("3", "");
                request.setEntity(formEntity);
                Log.d("4", "");
                HttpResponse response = client.execute(request);
                Log.d("5", "");
                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                Log.d("6", "");
                String line = "";
                String NL = System.getProperty("line.separator");
                while ((line = in.readLine()) != null) {
                    sb.append(line + NL);
                }
                in.close();


            } catch (Exception e) {

            }

            result = sb.toString();
            Log.d("response of smartwatch", "" + result);
            return result;
        }




        @Override
        protected void onPostExecute(String result) {
            String res = null;
            try {

                JSONObject reader = new JSONObject(result);
                Log.d("reader",""+reader);

                String status = reader.getString("message");

                res = ""+status;
                Log.d("Json parsed status",""+status);

            }catch (Exception e){
                Log.d("Json Exception",""+e);
            }
            Log.d("response of API", "" + result);

            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, ""+res, Snackbar.LENGTH_LONG);

            snackbar.show();

        }

    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
    private boolean validation() {
        boolean validate = true;
        uname = email.getText().toString();
        if (uname.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(uname).matches()) {
            email.setError("Enter a valid Email address");
            validate = false;
        }

        return validate;
    }

}
