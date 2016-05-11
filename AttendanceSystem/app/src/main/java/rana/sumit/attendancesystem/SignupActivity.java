package rana.sumit.attendancesystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import rana.sumit.attendancesystem.Utils.User;

/**
 * Created by ranaf on 4/20/2016.
 */
public class SignupActivity extends Activity implements View.OnClickListener, Validator.ValidationListener{
    private String[] SPINNERCATEGORY = {"Student", "Teacher"};
    private Button mSignUpButton;
    private TextView mLogin;
    private Validator mValidator;
    private CoordinatorLayout coordinatorLayout;
    private String FIRSTNAME, LASTNAME, EMAIL, PASSWORD, REPASSWORD, USERTYPE = null;
    private MaterialBetterSpinner mCategorySpinner;
    private SignUpTask mSignupTask;
    private static String AUTHORITY = "192.168.1.29:3000";
    private static String AUTH = "auth";
    private static String OPERATION = "register";
    @NotEmpty
    private EditText mFirstName, mLastName;
    @Password(min = 4, scheme = Password.Scheme.NUMERIC, message = "Must be 4 characters and Numeric")
    private EditText mPassword, mRePassword;
    @NotEmpty
    @Email
    private EditText mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mValidator = new Validator(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mValidator.setValidationListener(this);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,SPINNERCATEGORY);
        mCategorySpinner = (MaterialBetterSpinner) findViewById(R.id.category_spinner);
        mCategorySpinner.setAdapter(adapter);
        mFirstName = (EditText) findViewById(R.id.firstName_editText);
        mLastName = (EditText) findViewById(R.id.lastName_editText);
        mEmail = (EditText) findViewById(R.id.email_editText);
        mPassword = (EditText) findViewById(R.id.password_editText);
        mRePassword = (EditText)findViewById(R.id.rePassword_editText);
        mSignUpButton = (Button) findViewById(R.id.login_button);
        mSignUpButton.setOnClickListener(this);
        mLogin = (TextView) findViewById(R.id.link_login);
        mLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.login_button:
                if(isNetworkConnected()) {
                    mValidator.validate();
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
    public void onValidationSucceeded() { startSignupTask(); }

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

    protected void startSignupTask() {
        FIRSTNAME = mFirstName.getText().toString();
        LASTNAME = mLastName.getText().toString();
        EMAIL = mEmail.getText().toString();
        PASSWORD = mPassword.getText().toString();
        USERTYPE = mCategorySpinner.getText().toString();
        if(USERTYPE.equals(SPINNERCATEGORY[0])) {
            Log.d("Category", SPINNERCATEGORY[0]);
            //USERTYPE is student
            USERTYPE = "0";
        }else {
            Log.d("Category", SPINNERCATEGORY[1]);
            //USERTYPE is teacher
            USERTYPE = "1";
        }
        mSignupTask = new SignUpTask();
        mSignupTask.execute(new String[]{FIRSTNAME,LASTNAME,EMAIL,PASSWORD,USERTYPE});
    }

    private class SignUpTask extends AsyncTask<String, Void, User> {
        private Uri uri;
        private User user;
        @Override
        protected User doInBackground(String ...params) {
            HttpURLConnection urlConnection = null;
            try {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .encodedAuthority(AUTHORITY)
                        .appendPath(AUTH)
                        .appendPath(OPERATION)
                        .appendQueryParameter("firstName", params[0])
                        .appendQueryParameter("lastName", params[1])
                        .appendQueryParameter("email", params[2])
                        .appendQueryParameter("password", params[3])
                        .appendQueryParameter("userType", params[4]);
                uri = builder.build();
                URL url = new URL(uri.toString());
                Log.d("url", uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                int status = urlConnection.getResponseCode();
                BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                String webPage = "",data="";
                while ((data = bufferedReader.readLine()) != null){
                    webPage += data + "\n";
                }
                bufferedReader.close();
                JSONObject jsonObject = new JSONObject(webPage);
                Log.d("JSON Response", jsonObject.toString());
                if(status == 201) {
                    user = new User();
                    user.setEmail(EMAIL);
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Registration Successful !", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                else {
                    user = null;
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Registration Failed: " + webPage, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            } catch (IOException e) {

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return user;
        }
        @Override
        protected void onPostExecute(User user) {
            if(user != null) {
                Intent it = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(it);
                finish();
            }
        }

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

}
