package rana.sumit.attendancesystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.List;

/**
 * Created by ranaf on 4/20/2016.
 */
public class SignupActivity extends Activity implements View.OnClickListener, Validator.ValidationListener{
    @Password(min = 4, scheme = Password.Scheme.ALPHA_NUMERIC, message = "Must be 4 chars and AlphaNumeric")
    @NotEmpty
    @Email
    private String[] SPINNERCATEGORY = {"Student", "Teacher"};
    private Button mSignUpButton;
    private TextView mLogin;
    private EditText mFirstName, mLastName, mEmail, mPassword, mRePassword;
    private CoordinatorLayout coordinatorLayout;
    private String firstName,lastName, email, password, rePassword, category = null;
    private MaterialBetterSpinner mCategorySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,SPINNERCATEGORY);
        mCategorySpinner = (MaterialBetterSpinner) findViewById(R.id.category_spinner);
        mCategorySpinner.setAdapter(adapter);
        mFirstName = (EditText) findViewById(R.id.firstName_editText);
        mLastName = (EditText) findViewById(R.id.lastName_editText);
        mEmail = (EditText) findViewById(R.id.email_editText);
        mPassword = (EditText) findViewById(R.id.password_editText);
        mRePassword = (EditText)findViewById(R.id.rePassword_editText);
        mSignUpButton = (Button) findViewById(R.id.signup_button);
        mSignUpButton.setOnClickListener(this);
        mLogin = (TextView) findViewById(R.id.link_login);
        mLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.signup_button:
                if(isNetworkConnected()) {
                    category = mCategorySpinner.getText().toString();
                    firstName = mFirstName.getText().toString();
                    lastName = mLastName.getText().toString();
                    email = mEmail.getText().toString();
                    password = mPassword.getText().toString();
                    SignUpTask task = new SignUpTask();
                    task.execute();
                } else {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "No Internet Connection :(", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                break;
            case R.id.link_login:
                Intent it = new Intent(this, LoginActivity.class);
                startActivity(it);
                finish();
                break;
        }

    }

    @Override
    public void onValidationSucceeded() {

    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
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
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

}
