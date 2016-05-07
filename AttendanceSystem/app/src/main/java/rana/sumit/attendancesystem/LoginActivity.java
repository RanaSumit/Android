package rana.sumit.attendancesystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

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
public class LoginActivity extends Activity implements View.OnClickListener, Validator.ValidationListener{

    private Button mLogin;
    private TextView mSignUp;
    @Password(min = 4, scheme = Password.Scheme.ALPHA_NUMERIC, message = "Must be 4 chars and AlphaNumeric")
    private EditText mPassword;
    @NotEmpty
    @Email
    private EditText mEmail;
    private CoordinatorLayout coordinatorLayout;
    private String EMAIL, PASSWORD = null;
    private LoginTask mTask;
    private Validator validator;
    private static String AUTHORITY = "10.250.170.27:3000";
    private static String AUTH = "auth";
    private static String OPERATION = "login";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mEmail = (EditText) findViewById(R.id.firstName_editText);
        validator = new Validator(this);
        validator.setValidationListener(this);
        mPassword = (EditText) findViewById(R.id.lastName_editText);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        mLogin = (Button) findViewById(R.id.signup_button);
        mLogin.setOnClickListener(this);
        mSignUp = (TextView) findViewById(R.id.link_signup);
        mSignUp.setOnClickListener(this);
    }
    protected void startLoginTask() {
        Intent it = new Intent(this, Dashboard.class);
        startActivity(it);
        finish();
        /*EMAIL = mEmail.getText().toString();
        PASSWORD = mPassword.getText().toString();
        Log.d("startTask", EMAIL);
        Log.d("startTask", PASSWORD);
        mTask = new LoginTask();
        mTask.execute(new String[]{EMAIL,PASSWORD});*/
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.signup_button:
                if(isNetworkConnected()) {
                    validator.validate();
                } else {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "No Internet Connection :(", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                break;
            case R.id.link_signup:
                Intent it = new Intent(this, SignupActivity.class);
                startActivity(it);
                break;
        }
    }

    @Override
    public void onValidationSucceeded() {
            startLoginTask();
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

    private class LoginTask extends AsyncTask<String, Void, User> {
        private Uri uri;
        private User user;
        @Override
        protected User doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            try {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .encodedAuthority(AUTHORITY)
                        .appendPath(AUTH)
                        .appendPath(OPERATION)
                        .appendQueryParameter("email", params[0])
                        .appendQueryParameter("password", params[1]);
                uri = builder.build();
                URL url = new URL(uri.toString());
                Log.d("url", uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                int status = urlConnection.getResponseCode();
                BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                String webPage = "",data="";

                while ((data = bufferedReader.readLine()) != null){
                    webPage += data + "\n";
                }
                bufferedReader.close();
                JSONObject jsonObject = new JSONObject(webPage);
                Log.d("JSON Response", jsonObject.toString());
                //JSONObject source = jsonObject.getJSONObject("_source");
                //Log.d("password",source.getString("password"));
                if(status == 200) {
                    user = new User();
                    //user.setFirstname(source.getString("firstname"));
                    //user.setLastname(source.getString("lastname"));
                }
                else {
                    user = null;
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Login Failure: " + webPage, Snackbar.LENGTH_LONG);
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
            if(user!=null) {

            }else {


            }
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

}
