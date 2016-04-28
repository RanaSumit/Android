package rana.sumit.attendancesystem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by ranaf on 4/28/2016.
 */
public class MainActivity extends Activity {
    private Button mNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNext = (Button)findViewById(R.id.button1);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getApplicationContext(), Login.class);
                startActivity(it);
                finish();
            }
        });
    }
}
