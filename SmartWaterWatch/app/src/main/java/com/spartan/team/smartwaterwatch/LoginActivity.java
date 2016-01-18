package com.spartan.team.smartwaterwatch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

import butterknife.ButterKnife;

public class LoginActivity extends Activity {

    EditText usernameText, passwordText;
    Button Login;
    TextView LoginLink,ForgotPassword;
    public static  String MyPREFERENCES = "MyPrefs" ;
    public static  String uname = "";
    public static  String pwd = "";
    public String JSONarray = null;
    SharedPreferences sharedpreferences;
    private CoordinatorLayout coordinatorLayout;
    private int mProgressStatus = 0;
    private ProgressDialog mProgressDialog ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        ButterKnife.inject(this);
        usernameText = (EditText) findViewById(R.id.editText1);
        passwordText = (EditText) findViewById(R.id.editText2);
        LoginLink = (TextView) findViewById(R.id.link_signup);
        ForgotPassword = (TextView) findViewById(R.id.link_forgot);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        Login = (Button) findViewById(R.id.button1);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        boolean exists = sharedpreferences.contains("uname");
        Log.i("Exists", "" + exists);
        if(exists){
            Intent i = new Intent(LoginActivity.this, Dashboard.class);
            startActivity(i);
            finish();
        }
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Authenticating .......");

        Login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                uname = usernameText.getText().toString();
                pwd = passwordText.getText().toString();
                if (isNetworkConnected()) {
                    if (validation()) {
                        mProgressDialog.show();
                        LoginTask task = new LoginTask();
                        task.execute(new String[]{uname, pwd});
                    }
                } else {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "No Internet Connection !!", Snackbar.LENGTH_LONG);

                    snackbar.show();
                }


            }
        });
        LoginLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(it);
                finish();


            }
        });
        ForgotPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(LoginActivity.this, com.spartan.team.smartwaterwatch.ForgotPassword.class);
                startActivity(it);
                finish();
            }
        });
    }

    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressStatus = 0;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub


            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("email", params[0]));
            postParameters.add(new BasicNameValuePair("password", params[1]));
            String result = null;
            //String url = "http://10.189.93.95:3000/auth/login";
            String url = "http://smartwaterwatch.mybluemix.net/auth/login";
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

            JSONarray = result;

            Log.i("JSON to Profile",""+result);
            String res = null;

            try {

                JSONObject reader = new JSONObject(result);
                Log.d("reader",""+reader);

                String status = reader.getString("state");

                    res = ""+status;
                    Log.d("Json parsed status",""+status);
                    if(res.equals("failure")){
                        res = ""+reader.getString("message");
                        res = ""+reader.getString("message");
                        Log.d("Failure message",""+res);
                    }
                }catch (Exception e){
                    Log.d("Json Exception",""+e);
                }

            if (res.equals("success")) {
                //navigate to Main Menu
                SharedPreferences.Editor editor = sharedpreferences.edit();
                String uN  = usernameText.getText().toString();
                editor.putString("uname", uN);
                editor.putString("JSON", result);
                editor.commit();
                Intent i = new Intent(LoginActivity.this, Dashboard.class);
                i.putExtra("JSON", "" + JSONarray);
                startActivity(i);
                MainActivity.mainActivityInstance.finish();
                finish();
            } else {
                usernameText.setText("");
                passwordText.setText("");
                Snackbar snackbar =  Snackbar
                        .make(coordinatorLayout, "" + res, Snackbar.LENGTH_LONG);

                snackbar.show();


            }
            mProgressDialog.dismiss();
        }

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


    private boolean validation() {
        boolean validate = true;
        String email = usernameText.getText().toString();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            usernameText.setError("Enter a valid Email address");
            validate = false;
        }
        String pass = passwordText.getText().toString();
        if (pass.isEmpty() || pass.length() < 4) {
            passwordText.setError("Password Must be longer than four characters !!");
            validate = false;
        }

        return validate;
    }

}


