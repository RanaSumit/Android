package rana.sumit.attendancesystem;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
/**
 * Created by ranaf on 5/4/2016.
 */
public class CoursesFragment extends Fragment {

    private ListView mCoursesList;
    private CoordinatorLayout coordinatorLayout;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> COURSELIST;
    private static String AUTHORITY = "192.168.1.29:3000";
    private static String AUTH = "courses";
    private static String OPERATION = "getCourses";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_courses, viewGroup, false);
        Activity a = getActivity();
        if(a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);
        mCoursesList = (ListView) view.findViewById(R.id.course_list);
        GetCourse getCourse = new GetCourse();
        //getCourse.execute();
        return view;
    }
    private class GetCourse extends AsyncTask<String, Void, String> {

        private Uri uri;
        @Override
        protected String doInBackground(String... params) {
            String result = null;
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
                if(status == 200) {
                    Log.d("Response", webPage);
                    result = webPage;
                }
                else {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Registration Failed: " + webPage, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            } catch (IOException e) {

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }
        @Override
        protected void onPostExecute(String user) {
            if(user!=null) {
                COURSELIST = new ArrayList<String>();
                COURSELIST.add(user);
                arrayAdapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_list_item_1, COURSELIST);
                mCoursesList.setAdapter(arrayAdapter);
            }
        }
    }
}
