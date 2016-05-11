package rana.sumit.attendancesystem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

import rana.sumit.attendancesystem.Utils.Course;

/**
 * Created by ranaf on 5/4/2016.
 */
public class HomeFragment extends Fragment {
    private BluetoothAdapter mBluetoothAdapter;
    ArrayList<BluetoothDevice> pairedDeviceArrayList;
    private ListView mCourseList;
    private ArrayAdapter listAdapter;
    ArrayAdapter<BluetoothDevice> pairedDeviceAdapter;
    private TextView mBluetoothStatus;
    private static String AUTHORITY = "192.168.1.29:3000";
    private static String AUTH = "courses";
    private static String OPERATION = "getCourses";
    private ProgressDialog mProgressDialog ;
    ThreadConnectBTdevice myThreadConnectBTdevice;
    ThreadConnected myThreadConnected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, viewGroup, false);
        mCourseList = (ListView) view.findViewById(R.id.course_list);
        listAdapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1);
        mCourseList.setAdapter(listAdapter);
        mBluetoothStatus = (TextView) view.findViewById(R.id.bluetooth_status);
        Activity a = getActivity();
        if(a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter==null){
            mBluetoothStatus.setText("Bluetooth not supported");
        }else {
            if(!mBluetoothAdapter.isEnabled()) {
                mBluetoothStatus.setText("Status: Disabled");
            }
            if(isNetworkConnected()) {
                mProgressDialog = new ProgressDialog(getContext());
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setMessage("Loading .......");
                String email = "karanbir.singh@sjsu.edu";
                GetCoursesTask courseTask = new GetCoursesTask();
                courseTask.execute(email);
                mProgressDialog.show();
                setup();
                mCourseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        if(!mBluetoothAdapter.isEnabled()) {
                            mBluetoothAdapter.enable();
                            mBluetoothStatus.setText("Status: Enabled");
                        }
                        if(myThreadConnected!=null){
                            Log.d("Thread Connected", "1");
                            String payload = "ananya.misra@sjsu.edu~572fba08bd64a89543be2696~";
                            byte[] bytesToSend = payload.getBytes();
                            myThreadConnected.write(bytesToSend);
                        }
                        Toast.makeText(getContext(), "Hi: " + position, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getContext(), "No Internet Connection !!", Toast.LENGTH_SHORT).show();
            }
        }
        return view;
    }

    private class GetCoursesTask extends AsyncTask<String, Void, ArrayList<Course>> {
        private String uri;
        private ArrayList<Course> coursesList = new ArrayList<>();
        @Override
        protected ArrayList<Course> doInBackground(String... params) {
            try {
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .encodedAuthority(AUTHORITY)
                        .appendPath(AUTH)
                        .appendPath(OPERATION)
                        .appendQueryParameter("email", params[0]);
                Log.d("Builder", builder.toString());
                uri = builder.build().toString();
                URL url = new URL(uri);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                int status = urlConnection.getResponseCode();
                BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                String webPage = "",data="";
                while ((data = bufferedReader.readLine()) != null){
                    webPage += data + "\n";
                }
                bufferedReader.close();
                if(status == 200){
                    JSONObject jsonObject = new JSONObject(webPage);
                    Log.d("JSON", jsonObject.toString());
                    JSONArray courses = jsonObject.getJSONArray("courses");
                    for(int i =0; i <= courses.length();i++){
                        JSONObject objCourse = courses.getJSONObject(i);
                        Course course = new Course();
                        course.setCourseName(objCourse.getString("courseId"));
                        course.setCourseId(objCourse.getString("courseName"));
                        course.setId(objCourse.getString("_id"));
                        coursesList.add(course);
                    }
                }
            } catch (IOException e) {
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return coursesList;
        }
        @Override
        protected void onPostExecute(ArrayList<Course> courses) {
            if(!courses.isEmpty()){
                for(int i=0; i < courses.size();i++){
                    Course course = courses.get(i);
                    listAdapter.addAll(course.getCourseName());
                }
            }
            mProgressDialog.dismiss();
        }
    }
    private class ThreadConnectBTdevice extends Thread {
        private BluetoothSocket bluetoothSocket = null;
        private final BluetoothDevice bluetoothDevice;
        public ThreadConnectBTdevice(BluetoothDevice device) {
            bluetoothDevice = device;
            try {
                Method method;
                method = device.getClass().getMethod("createRfcommSocket", new Class[] { int.class } );
                bluetoothSocket = (BluetoothSocket) method.invoke(device, 1);
            }catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        @Override
        public void run() {
            boolean success = false;
            try {
                bluetoothSocket.connect();
                success = true;
            } catch (IOException e) {
                e.printStackTrace();
                final String eMessage = e.getMessage();
                getActivity().runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        mBluetoothStatus.setText("something wrong bluetoothSocket.connect(): \n" + eMessage);
                        Log.d("error", eMessage);
                    }});
                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if(success){
                final String msgconnected = "connect successful:\n"
                        + "BluetoothSocket: " + bluetoothSocket + "\n"
                        + "BluetoothDevice: " + bluetoothDevice;
                Log.d("Message: ", msgconnected);
                getActivity().runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        mBluetoothStatus.setText(msgconnected);
                    }});

                startThreadConnected(bluetoothSocket);
            }else{
                mBluetoothStatus.setText("No paired devices connected !!");
            }
        }
        public void cancel() {

            Toast.makeText(getContext(),
                    "close bluetoothSocket",
                    Toast.LENGTH_LONG).show();

            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void startThreadConnected(BluetoothSocket socket){

        myThreadConnected = new ThreadConnected(socket);
        myThreadConnected.start();
    }
    private class ThreadConnected extends Thread {
        private final BluetoothSocket connectedBluetoothSocket;
        private final InputStream connectedInputStream;
        private final OutputStream connectedOutputStream;

        public ThreadConnected(BluetoothSocket socket) {
            connectedBluetoothSocket = socket;
            InputStream in = null;
            OutputStream out = null;

            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connectedInputStream = in;
            connectedOutputStream = out;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (true) {
                try {
                    bytes = connectedInputStream.read(buffer);
                    String strReceived = new String(buffer, 0, bytes);
                    final String msgReceived = String.valueOf(bytes) +
                            " bytes received:\n"
                            + strReceived;

                    getActivity().runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            mBluetoothStatus.setText(msgReceived);
                        }});

                } catch (IOException e) {
                    e.printStackTrace();

                    final String msgConnectionLost = "Connection lost:\n"
                            + e.getMessage();
                    getActivity().runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            mBluetoothStatus.setText(msgConnectionLost);
                        }});
                }
            }
        }
        public void write(byte[] buffer) {
            try {
                connectedOutputStream.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void cancel() {
            try {
                connectedBluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void setup() {
        if(mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.enable();
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                pairedDeviceArrayList = new ArrayList<BluetoothDevice>();
                for (BluetoothDevice device : pairedDevices) {
                    pairedDeviceArrayList.add(device);
                }
                BluetoothDevice device = pairedDeviceArrayList.get(0);
                Log.d("Device", device.toString());
                myThreadConnectBTdevice = new ThreadConnectBTdevice(device);
                myThreadConnectBTdevice.start();
            }
        }else {
            Toast.makeText(getContext(), "Enable Bluetooth !!", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}