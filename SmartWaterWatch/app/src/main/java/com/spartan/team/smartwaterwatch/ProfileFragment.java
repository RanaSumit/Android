package com.spartan.team.smartwaterwatch;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by ranaf on 11/20/2015.
 */
public class ProfileFragment extends Fragment {
    Button editButton;
    EditText fname, lname, em, ph;
    String firstName, lastName, email, phone;
    private CoordinatorLayout coordinatorLayout;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, viewGroup, false);
        fname= (EditText)view.findViewById(R.id.editText1);
        lname= (EditText)view.findViewById(R.id.editText2);
        em= (EditText)view.findViewById(R.id.editText3);
        ph= (EditText)view.findViewById(R.id.editText4);
        editButton = (Button)view.findViewById(R.id.button1);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);
        getData();
        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                if(isNetworkConnected()){

                    if(validation()){
                        firstName = fname.getText().toString();
                        lastName = lname.getText().toString();
                        phone = ph.getText().toString();
                        email = em.getText().toString();
                        LoginTask task = new LoginTask();
                        task.execute(new String[]{firstName,lastName ,phone, email});
                    }

                }else{
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "No Internet Connection !!", Snackbar.LENGTH_LONG);

                    snackbar.show();
                }

            }
        });
        return view;
    }

    private void getData(){
        String json=getArguments().getString("JSON");
        Log.i("JSON INSIDE FRAGEMENT", "" + json);
        //String response = null;

        try {
            JSONObject reader1 = new JSONObject(json);
            Log.d("reader",""+reader1);

            JSONObject user = reader1.getJSONObject("user");
            phone = user.getString("phoneNumber");
            lastName = user.getString("lastName");
            firstName = user.getString("firstName");
            email = user.getString("email");
            Log.d("phoneNumber",""+phone);
            Log.d("lastName",""+lastName);
            Log.d("firstName",""+firstName);
            Log.d("email", "" + email);
            fname.setText(firstName);
            lname.setText(lastName);
            em.setText(email);
            em.setClickable(false);
            em.setKeyListener(null);
            ph.setText(phone);

        }catch (Exception e){
            Log.d("Json Exception",""+e);
        }


    }



    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //mProgressStatus = 0;
        }

        @Override
        protected String doInBackground(String... params) {
            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("firstName", params[0]));
            postParameters.add(new BasicNameValuePair("lastName", params[1]));
            postParameters.add(new BasicNameValuePair("phoneNumber", params[2]));
            postParameters.add(new BasicNameValuePair("email", params[3]));
            Log.d("Parameters",""+postParameters);

            String result = null;
            //String url = "http://10.189.93.95:3000/auth/update";
            String url = "http://smartwaterwatch.mybluemix.net/auth/update";
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

            return result;
        }




        @Override
        protected void onPostExecute(String result) {

            Log.d("Response from API", "" + result);
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "" + result, Snackbar.LENGTH_LONG);

            snackbar.show();
        }

    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
    private boolean validation(){
        boolean validate = true;

        firstName = fname.getText().toString().trim();
        if(firstName.length()==0){
            fname.setError("Cannot be blank");
            validate = false;
        }

        lastName= lname.getText().toString().trim();
        if(lastName.length()==0) {
            lname.setError("Cannot be blank");
            validate = false;
        }

        phone= ph.getText().toString().trim();
        if(phone.length() != 10) {
            ph.setError("Enter 10 digit number");
            validate = false;
        }


        phone= ph.getText().toString().trim();
        if (phone.length()==0) {
            ph.setError("Cannot be blank");
            validate = false;
        }

        return validate;
    }

}