package com.spartan.team.smartwaterwatch;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.spartan.team.smartwaterwatch.utils.AsyncTaskCompleteListener;
import com.spartan.team.smartwaterwatch.utils.ManageSensorTask;
import com.spartan.team.smartwaterwatch.utils.Sensor;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ranaf on 11/20/2015.
 */
public class HomeFragment extends Fragment implements AsyncTaskCompleteListener<ArrayList<Sensor>> {

    ListView list ;
    private ArrayList<Sensor> sensors;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup viewGroup, Bundle savedInstanceState) {
      View  view = inflater.inflate(R.layout.home_fragment, viewGroup, false);
        list = (ListView) view.findViewById(R.id.List);
        String userId = getData();
        Log.d("Stored userId", "" + userId);
        ManageSensorTask manageSensorTask = new ManageSensorTask(userId,this);
        manageSensorTask.execute();
        return view;
    }


    private String getData(){
        String userId;
        String json=getArguments().getString("JSON");
        Log.i("JSON INSIDE FRAGEMENT", "" + json);
        //String response = null;

        try {
            JSONObject reader1 = new JSONObject(json);
            Log.d("reader",""+reader1);

            JSONObject user = reader1.getJSONObject("user");

            userId = user.getString("_id");

            Log.d("userId", "" + userId);
            return userId;

        }catch (Exception e){
            Log.d("Json Exception",""+e);
            return null;
        }


    }


    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    @Override
    public void onTaskComplete(ArrayList<Sensor> result) {
        sensors = result;
        CustomAdapter adapter = new CustomAdapter();
        list.setAdapter(adapter);
        Log.d("Tag", sensors.get(0).getSensorName());
    }

    class ViewHolder {
        public TextView sensorName = null;
        public TextView sensorType = null;
        public TextView sensorActivation = null;


        public ViewHolder(View v) {
            
            sensorName = (TextView) v.findViewById(R.id.text1);
            sensorType = (TextView) v.findViewById(R.id.text2);
            sensorActivation = (TextView) v.findViewById(R.id.text3);
        }

        public void populateViewHolder(Sensor sensor) {
            sensorName.setText(sensor.getSensorName());
            sensorType.setText(sensor.getSensorType());
            sensorActivation.setText(sensor.getSensorStatus());
        }
    }

    public class CustomAdapter extends ArrayAdapter<ArrayList<Sensor>>{
        ViewHolder holder;
        public CustomAdapter() {
            super(getActivity(), R.layout.sensor_list_item);
        }
        @Override
        public int getCount() {
            return sensors.size();

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = ((getActivity()).getLayoutInflater());
                convertView = inflater.inflate(R.layout.sensor_list_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.populateViewHolder(sensors.get(position));

            return (convertView);
        }
    }

}
