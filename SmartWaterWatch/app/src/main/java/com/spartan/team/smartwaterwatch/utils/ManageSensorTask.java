package com.spartan.team.smartwaterwatch.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by karanbir on 09/12/15.
 */
public class ManageSensorTask  extends AsyncTask<String, Void, ArrayList<Sensor>> {
    private String userId;
    private AsyncTaskCompleteListener<ArrayList<Sensor>> callback;
    public ManageSensorTask(String userId, AsyncTaskCompleteListener<ArrayList<Sensor>> callback){
        this.userId = userId;
        this.callback = callback;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }
    @Override
    protected ArrayList<Sensor> doInBackground(String... params) {
        // TODO Auto-generated method stub
    Log.d("ask", "running");

        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("_id", userId));
        String result = null;
        //String url = "http://10.189.93.95:3000/auth/login";
        String url = "http://smartwaterwatch.mybluemix.net/sensor/availableSensors";
        BufferedReader in = null;
        StringBuffer sb = new StringBuffer("");
        try {
            Log.d("Email in Home", "" + userId);
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
        Log.d("response of smartwatch", "" + result);

        ArrayList<Sensor> sensorArrayList = new ArrayList<>();


        try{
            JSONArray sensorArray = new JSONArray(result);
            Log.d("SensorArray",""+sensorArray);
            for(int i =0; i<sensorArray.length();i++){
                JSONObject sensorObject = sensorArray.getJSONObject(i);
                Sensor sensor = new Sensor();
                sensor.setSensorName(sensorObject.getString("sensorName"));
                sensor.setSensorType(sensorObject.getString("sensorType"));
                sensor.setSensorStatus(sensorObject.getString("activated"));
                sensorArrayList.add(sensor);
            }
        }
        catch (Exception e){
            Log.d("Exception", "" + e);
        }

        return sensorArrayList;
    }




    @Override
    protected void onPostExecute(ArrayList<Sensor> sensorArrayList) {
        Log.d("ArrayList", sensorArrayList.get(0).getSensorName());
        callback.onTaskComplete(sensorArrayList);
        super.onPostExecute(sensorArrayList);



    }


}
