package rana.sumit.attendancesystem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class MainFragment extends Fragment implements View.OnClickListener {
    private Button mNextButton;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, viewGroup, false);
        mNextButton = (Button) view.findViewById(R.id.button1);
        mNextButton.setOnClickListener(this);
        return view;
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button1:
                FragmentTransaction t = this.getFragmentManager().beginTransaction();
                Fragment mFrag = new LoginFragment();
                t.replace(R.id.activity_frame, mFrag);
                t.commit();
                break;
        }
    }
}