package com.spartan.team.smartwaterwatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String JSON = null;
    TextView username, email;
    private CoordinatorLayout coordinatorLayout;
    String uname = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        SharedPreferences preferences = getSharedPreferences("MyPrefs", 0);
        if(preferences.contains("JSON")){
            JSON = preferences.getString("JSON", null);
            Log.i("Preferences exists !!", JSON);
        }else{
            JSON = getIntent().getExtras().getString("JSON");
            Log.i("JSON Profile Data", JSON);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                try {
                    JSONObject reader1 = new JSONObject(JSON);
                    Log.d("reader",""+reader1);

                    JSONObject user = reader1.getJSONObject("user");
                    email = (TextView) drawerView.findViewById(R.id.email);
                    email.setText(user.getString("email"));
                    username = (TextView) drawerView.findViewById(R.id.Username);
                    username.setText(user.getString("firstName") + user.getString("lastName"));

                }catch (Exception e){
                    Log.d("Json Exception",""+e);
                }

                super.onDrawerOpened(drawerView);
            }
        };

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFrame, new ManageSensorFragment()).commit();
            navigationView.getMenu().getItem(0).setChecked(true);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {

            LogOut task = new LogOut();
            task.execute(new String[]{uname});
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment;
        switch (id){
            case R.id.nav_home:
                fragment = new ManageSensorFragment();
                break;
            case R.id.nav_profile:
                Bundle bundle1=new Bundle();
                bundle1.putString("JSON", JSON);
                fragment = new ProfileFragment();
                fragment.setArguments(bundle1);
                break;
            case R.id.nav_manage_sensor:
                Bundle bundle2=new Bundle();
                bundle2.putString("JSON", JSON);
                fragment = new HomeFragment();
                fragment.setArguments(bundle2);
                break;
            default:
                fragment = new HomeFragment();
                break;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame, fragment);
        ft.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private class LogOut extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

            //ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            //postParameters.add(new BasicNameValuePair("email", params[0]));
            String result = null;
            //String url = "http://10.189.93.95:3000/auth/signout";
            String url = "http://smartwaterwatch.mybluemix.net/auth/signout";
            BufferedReader in = null;
            StringBuffer sb = new StringBuffer("");

            try {
                Log.d("1","");
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(url);
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
                Log.d("Json Exception", "" + e);
            }

            result = sb.toString();
            Log.d("response of smartwatch", "" + result);
            return result;
        }




        @Override
        protected void onPostExecute(String result) {

            SharedPreferences preferences = getSharedPreferences("MyPrefs", 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();
            Toast.makeText(getApplicationContext(), "You have been logged out",
                    Toast.LENGTH_LONG).show();
            Intent it = new Intent(Dashboard.this, LoginActivity.class);
            startActivity(it);
            finish();

        }

    }
}
