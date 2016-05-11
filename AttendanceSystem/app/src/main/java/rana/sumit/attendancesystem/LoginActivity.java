package rana.sumit.attendancesystem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
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
    @Password(min = 4, scheme = Password.Scheme.NUMERIC, message = "Must be 4 characters and Numeric")
    private EditText mPassword;
    @NotEmpty
    @Email
    private EditText mEmail;
    private CoordinatorLayout coordinatorLayout;
    private String EMAIL, PASSWORD = null;
    private LoginTask mLoginTask;
    private Validator mValidator;
    private static String AUTHORITY = "192.168.1.29:3000";
    private static String AUTH = "auth";
    private static String OPERATION = "login";
    public static  String MyPREFERENCES = "MyPrefs" ;
    SharedPreferences sharedpreferences;
    private ProgressDialog mProgressDialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        boolean exists = sharedpreferences.contains("uname");
        if(exists){
            Intent i = new Intent(LoginActivity.this, Dashboard.class);
            startActivity(i);
            finish();
        }
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Authenticating .......");
        mEmail = (EditText) findViewById(R.id.firstName_editText);
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);
        mPassword = (EditText) findViewById(R.id.lastName_editText);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        mLogin = (Button) findViewById(R.id.login_button);
        mLogin.setOnClickListener(this);
        mSignUp = (TextView) findViewById(R.id.link_signup);
        mSignUp.setOnClickListener(this);
    }
    protected void startLoginTask() {
        EMAIL = mEmail.getText().toString();
        PASSWORD = mPassword.getText().toString();
        Log.d("startTask", EMAIL);
        Log.d("startTask", PASSWORD);
        mProgressDialog.show();
        mLoginTask = new LoginTask();
        mLoginTask.execute(new String[]{EMAIL,PASSWORD});
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.login_button:
                if(isNetworkConnected()) {
                    /*Intent it = new Intent(LoginActivity.this, Dashboard.class);
                    startActivity(it);
                    finish();*/
                    mValidator.validate();
                }else {
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
        private String uri;
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
                Log.d("Builder", builder.toString());
                uri = builder.build().toString();
                URL url = new URL(uri);
                Log.d("connection", "1");
                urlConnection = (HttpURLConnection) url.openConnection();
                Log.d("connection", "2");
                urlConnection.setRequestMethod("GET");
                Log.d("connection", "3");
                int status = urlConnection.getResponseCode();
                Log.d("connection", "4");
                Log.d("Status", Integer.toString(status));
                BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                String webPage = "",data="";
                while ((data = bufferedReader.readLine()) != null){
                    webPage += data + "\n";
                }
                bufferedReader.close();
                Log.d("Response", webPage);
                JSONObject jsonObject = new JSONObject(webPage);
                Log.d("JSON", jsonObject.toString());
                JSONObject source = jsonObject.getJSONObject("user");
                Log.d("email",source.getString("email"));
                if(source.getString("email").equals(params[0])) {
                    user = new User();
                    user.setFirstname(source.getString("firstName"));
                    user.setLastname(source.getString("lastName"));
                }
                else {
                    user = null;
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
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("uname", EMAIL);
                editor.commit();
                Intent it = new Intent(LoginActivity.this, Dashboard.class);
                startActivity(it);
                finish();
            }else {
                Log.d("null","null");
            }
            mProgressDialog.dismiss();

        }
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
