package rana.sumit.attendancesystem;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

/**
 * Created by ranaf on 4/20/2016.
 */
public class Signup extends Activity {
    private String[] SPINNERCATEGORY = {"Student", "Teacher"};
    private Button mSignUp, mLogin;
    private EditText mFirstName, mLastName, mEmail, mPassword, mRePassword;
    private CoordinatorLayout coordinatorLayout;
    private String firstName,lastName, email, password, rePassword, category = null;
    private MaterialBetterSpinner mCategorySpinner;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,SPINNERCATEGORY);
        mCategorySpinner = (MaterialBetterSpinner)findViewById(R.id.spinner1);
        mCategorySpinner.setAdapter(adapter);
        mFirstName = (EditText)findViewById(R.id.editText1);
        mLastName = (EditText)findViewById(R.id.editText2);
        mEmail = (EditText)findViewById(R.id.editText3);
        mPassword = (EditText)findViewById(R.id.editText4);
        mRePassword = (EditText)findViewById(R.id.editText5);
        mSignUp = (Button)findViewById(R.id.button1);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validation()) {
                        category = mCategorySpinner.getText().toString();
                        firstName = mFirstName.getText().toString();
                        lastName = mLastName.getText().toString();
                        email = mEmail.getText().toString();
                        password = mPassword.getText().toString();
                        Log.d("category", category);
                        Log.d("firstName", firstName);
                        Log.d("lastName", lastName);
                        Log.d("email", email);
                        Log.d("password", password);
                        /*SignUpTask task = new SignUpTask();
                        task.execute();
                        Intent it = new Intent(getApplicationContext(),DashBoard.class);
                        startActivity(it);
                        finish();*/


                } else {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "No Internet Connection :(", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }


            }
        });
        mLogin = (Button)findViewById(R.id.button2);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getApplicationContext(),Login.class);
                startActivity(it);
                finish();
            }
        });
    }
    private class SignUpTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String ...params) {
            String result = null;
            return result;
        }
        @Override
        protected void onPostExecute(String result) {

        }

    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
    private boolean validation(){
        boolean validate = true;

        firstName = mFirstName.getText().toString().trim();
        if(firstName.length()==0){
            mFirstName.setError("Cannot be blank");
            validate = false;
        }

        lastName= mLastName.getText().toString().trim();
        if (lastName.length()==0) {
            mLastName.setError("Cannot be blank");
            validate = false;
        }

        email = mEmail.getText().toString().trim();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmail.setError("enter a valid Email address");
            validate = false;
        }

        password= mPassword.getText().toString().trim();
        if(password.length()==0) {
            mPassword.setError("Cannot be blank");
            validate = false;
        }
        rePassword= mRePassword.getText().toString().trim();
        if(rePassword.length()==0) {
            mRePassword.setError("Cannot be blank");
            validate = false;
        }

        if(!TextUtils.isEmpty(password) && !TextUtils.isEmpty(rePassword) ){
            if(!password.equals(rePassword)){
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Password Do Not Match !!", Snackbar.LENGTH_LONG);

                snackbar.show();
                mPassword.setText("");
                mRePassword.setText("");
                validate = false;
            }
        }
        if(mCategorySpinner.getText() == null ) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Select Category !!", Snackbar.LENGTH_LONG);
            snackbar.show();
            validate = false;

        }
        return validate;
    }
}
