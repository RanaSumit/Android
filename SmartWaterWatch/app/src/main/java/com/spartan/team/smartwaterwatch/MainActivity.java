package com.spartan.team.smartwaterwatch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by ranaf on 11/21/2015.
 */
public class MainActivity extends Activity implements View.OnClickListener{

    Button login, signUp;
    SharedPreferences sharedpreferences;
    public static  String MyPREFERENCES = "MyPrefs" ;
    public static MainActivity mainActivityInstance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


        mainActivityInstance = this;
        login = (Button) findViewById(R.id.button1);
        signUp = (Button) findViewById(R.id.button2);
        login.setOnClickListener(this);
        signUp.setOnClickListener(this);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        boolean exists = sharedpreferences.contains("uname");
        if(exists){
            Intent i = new Intent(MainActivity.this, Dashboard.class);
            startActivity(i);
            finish();
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId() ){
            case R.id.button1:
                Intent it1 = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(it1);

                break;
            case R.id.button2:
                Intent it2 = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(it2);

                break;

        }

    }
}
