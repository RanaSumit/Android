package rana.sumit.attendancesystem;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ranaf on 4/20/2016.
 */
public class Login extends Fragment {
    private Button mLogin;
    private TextView mSignUp;
    private EditText mEmail, mPassword;
    private Context mContext;
    private CoordinatorLayout coordinatorLayout;
    private String email, password = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login, container, false);
        mContext = view.getContext();
        mEmail = (EditText) view.findViewById(R.id.editText1);
        mPassword = (EditText) view.findViewById(R.id.editText2);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);
        mLogin = (Button) view.findViewById(R.id.button1);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkConnected()) {
                    if(validation()) {
                        LoginTask task = new LoginTask();
                        task.execute();
                    }
                } else {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "No Internet Connection :(", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }


            }
        });
        mSignUp = (Button) view.findViewById(R.id.button2);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(mContext, Signup.class);
                startActivity(it);
                getActivity().finish();
            }
        });
        return view;
    }
    private class LoginTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String forecastJsonStr = null;
            try{
                //Login URL
                URL url = new URL("http://192.168.1.29:3000/auth/login");
                //Create the request to open the connection with GET
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                //Read the input stream into String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null) {
                    return null;
                } else {
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    forecastJsonStr = buffer.toString();
                    return forecastJsonStr;

                }
            } catch (IOException e) {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "" + e, Snackbar.LENGTH_LONG);
                snackbar.show();
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }


        }

    }
    private boolean validation() {
        boolean validate = true;

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

        return validate;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

}
