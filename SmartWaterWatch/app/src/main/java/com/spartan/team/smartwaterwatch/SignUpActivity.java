package com.spartan.team.smartwaterwatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;


/**
 * Created by ranaf on 11/10/2015.
 */
public class SignUpActivity extends Activity {

    EditText fName, lName, em, pwd, rPwd, ph;
    RadioGroup gd;
    Button submit;
    TextView SignupLink;
    String firstName, lastName, email, password, rePassword, phone, gender;
    int selectedId;
    RadioButton sex;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        fName = (EditText) findViewById(R.id.editText1);
        lName = (EditText) findViewById(R.id.editText2);
        em = (EditText) findViewById(R.id.editText3);
        pwd = (EditText) findViewById(R.id.editText4);
        rPwd = (EditText) findViewById(R.id.editText5);
        ph = (EditText) findViewById(R.id.editText6);
        gd = (RadioGroup) findViewById(R.id.RadioGroup);
        submit = (Button) findViewById(R.id.button1);
        SignupLink = (TextView) findViewById(R.id.link_login);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkConnected()){
                    if(validation()){
                        Log.d("Button Clicked", "");
                        selectedId = gd.getCheckedRadioButtonId();
                        sex = (RadioButton) findViewById(selectedId);
                        gender = sex.getText().toString();
                        firstName = fName.getText().toString();
                        lastName = lName.getText().toString();
                        email = em.getText().toString();
                        password = pwd.getText().toString();
                        phone = ph.getText().toString();
                        LoginTask task = new LoginTask();
                        task.execute(new String[]{firstName, lastName, email, password, phone, gender});
                    }
                }
                else{
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "No Internet Connection !!", Snackbar.LENGTH_LONG);

                    snackbar.show();
                }


            }

            });
        SignupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(it);
                finish();


            }
        });

    }


    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String json = "";
            StringBuffer sb = new StringBuffer("");
            String res = null;
            String url = "http://smartwaterwatch.mybluemix.net/auth/signup";
            BufferedReader in = null;
            try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("firstName", params[0]);
            jsonObject.accumulate("lastName", params[1]);
            jsonObject.accumulate("userId", params[2]);
                jsonObject.accumulate("password", params[3]);
                jsonObject.accumulate("phone", params[4]);
                jsonObject.accumulate("gender", params[5]);
                json = jsonObject.toString();
                Log.d("1", "");
                HttpClient client = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);
                Log.d("JSON ARRAY INPUT",""+json);

                Log.d("3", "");
                StringEntity se = new StringEntity(json);
                httpPost.setEntity(se);
                Log.d("4", "");
                HttpResponse response = client.execute(httpPost);
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
            res = sb.toString();
            Log.d("Response from API", "" + res);
            return res;
        }

        @Override
        protected void onPostExecute(String result) {

            if (result.equals("success")) {
                //navigate to Main Menu
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Success !!" + result, Snackbar.LENGTH_LONG);snackbar.show();

            } else {
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "" + result, Snackbar.LENGTH_LONG);snackbar.show();
            }
        }

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
    private boolean validation(){
        boolean validate = true;

        firstName = fName.getText().toString().trim();
        if(firstName.length()==0){
            fName.setError("Cannot be blank");
            validate = false;
        }

        lastName= lName.getText().toString().trim();
        if(lastName.length()==0) {
            lName.setError("Cannot be blank");
            validate = false;
        }

        phone= ph.getText().toString().trim();
        if(phone.length() < 10) {
            ph.setError("Enter 10 digit number");
            validate = false;
        }
        email = em.getText().toString().trim();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            em.setError("enter a valid userId address");
            validate = false;
        }

        password= pwd.getText().toString().trim();
        if(password.length()==0) {
            pwd.setError("Cannot be blank");
            validate = false;
        }
        rePassword= rPwd.getText().toString().trim();
        if(rePassword.length()==0) {
            rPwd.setError("Cannot be blank");
            validate = false;
        }

        if(!TextUtils.isEmpty(password) && !TextUtils.isEmpty(rePassword) ){
            if(!password.equals(rePassword)){
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Password Do Not Match !!", Snackbar.LENGTH_LONG);

                snackbar.show();
                pwd.setText("");
                rPwd.setText("");
                validate = false;
            }
        }


        phone= ph.getText().toString().trim();
        if(phone.length()==0) {
            ph.setError("Cannot be blank");
            validate = false;
        }
        if(gd.getCheckedRadioButtonId()== -1){
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Select Your Gender !!", Snackbar.LENGTH_LONG);

            snackbar.show();
            validate = false;
        }

        return validate;
    }
}
