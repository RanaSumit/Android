package rana.sumit.attendancesystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by ranaf on 4/28/2016.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(this);    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_button:
                Intent it = new Intent(this, LoginActivity.class);
                startActivity(it);
                break;
        }
    }
}
