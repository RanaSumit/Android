package rana.sumit.attendancesystem;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

/**
 * Created by ranaf on 5/4/2016.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    private static final int REQUEST_ENABLE_BT = 1;
    private Button mOnBtn, mOffBtn, mFindBtn;
    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private ListView mSearchList;
    private TextView mText;
    private ArrayAdapter<String> BtArrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, viewGroup, false);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            mOnBtn.setEnabled(false);
            mOffBtn.setEnabled(false);
            mFindBtn.setEnabled(false);
            mText.setText("Status: Not Supported");
            Toast.makeText(getContext(), "Your device does not support Bluetooth",
                    Toast.LENGTH_LONG).show();
        } else {
            mText = (TextView) view.findViewById(R.id.text);
            mOnBtn = (Button) view.findViewById(R.id.turn_on);
            mOffBtn = (Button) view.findViewById(R.id.turn_off);
            mFindBtn = (Button) view.findViewById(R.id.find_devices);
            mOnBtn.setOnClickListener(this);
            mOffBtn.setOnClickListener(this);
            mFindBtn.setOnClickListener(this);
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.turn_on:
                on(v);
                break;
            case R.id.turn_off:
                off(v);
                break;
            case R.id.find_devices:
                list(v);
                break;
        }
    }
    public void on(View view) {
        if (mBluetoothAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);
            Toast.makeText(getContext(), "Bluetooth turned on !", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getContext(), "Bluetooth is already on !", Toast.LENGTH_SHORT).show();
        }
    }
    public void off(View view) {
        mBluetoothAdapter.disable();
        mText.setText("Status: Disabled");
        Toast.makeText(getContext(), "Bluetooth turned off !", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_BT) {
            if(mBluetoothAdapter.isEnabled()) {
                mText.setText("Status: Enabled");
            }else {
                mText.setText("Status: Disabled");
            }
        }
    }


    public void list(View view) {
        if(mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            Toast.makeText(getContext(), "Searching......", Toast.LENGTH_SHORT).show();
        } else {
            BtArrayAdapter.clear();
            mBluetoothAdapter.startDiscovery();
            getActivity().registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(bReceiver);

    }
    BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name and the MAC address of the object to the arrayAdapter
                BtArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                BtArrayAdapter.notifyDataSetChanged();
            }
        }
    };

}