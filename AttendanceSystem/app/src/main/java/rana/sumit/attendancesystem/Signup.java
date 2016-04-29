package rana.sumit.attendancesystem;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

/**
 * Created by ranaf on 4/20/2016.
 */
public class Signup extends Fragment {
    private String[] SPINNERCATEGORY = {"Student", "Teacher"};
    private Button mSignUp, mLogin;
    private EditText mFirstName, mLastName, mEmail, mPassword, mRePassword;
    private CoordinatorLayout coordinatorLayout;
    private String firstName,lastName, email, password, rePassword, category = null;
    private MaterialBetterSpinner mCategorySpinner;
    private View mView;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.login, container, false);
        mContext = mView.getContext();

        coordinatorLayout = (CoordinatorLayout) mView.findViewById(R.id.coordinatorLayout);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_dropdown_item_1line,SPINNERCATEGORY);
        mCategorySpinner = (MaterialBetterSpinner) mView.findViewById(R.id.spinner1);
        mCategorySpinner.setAdapter(adapter);
        mFirstName = (EditText) mView.findViewById(R.id.editText1);
        mLastName = (EditText) mView.findViewById(R.id.editText2);
        mEmail = (EditText)mView.findViewById(R.id.editText3);
        mPassword = (EditText)mView.findViewById(R.id.editText4);
        mRePassword = (EditText)mView.findViewById(R.id.editText5);
        mSignUp = (Button)mView.findViewById(R.id.button1);
        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validation()) {
                        category = mCategorySpinner.getText().toString();
                        firstName = mFirstName.getText().toString();
                        lastName = mLastName.getText().toString();
                        email = mEmail.getText().toString();
                        password = mPassword.getText().toString();
                        SignUpTask task = new SignUpTask();
                        task.execute();
                        Intent it = new Intent(mContext,DashBoard.class);
                        startActivity(it);



                } else {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "No Internet Connection :(", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }


            }
        });
        mLogin = (Button) mView.findViewById(R.id.button2);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(mContext,LoginFragment.class);
                startActivity(it);
            }
        });

            return mView;
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
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

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
