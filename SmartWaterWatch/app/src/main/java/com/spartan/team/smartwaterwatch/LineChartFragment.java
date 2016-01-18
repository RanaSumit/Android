package com.spartan.team.smartwaterwatch;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PreviewLineChartView;

/**
 * Created by ranaf on 12/7/2015.
 */
public class LineChartFragment extends Fragment {

    private LineChartView chart;
    private PreviewLineChartView previewChart;
    private LineChartData data;
    /**
     * Deep copy of data.
     */
    private LineChartData previewData;


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_line_chart, viewGroup, false);

        //chart = (LineChartView) view.findViewById(R.id.chart);
        //previewChart = (PreviewLineChartView) view.findViewById(R.id.chart_preview);

        GetSensorIDTask task = new GetSensorIDTask();
        task.execute();

        // Generate data for previewed chart and copy of that data for preview chart.
        //generateDefaultData();

        //chart.setLineChartData(data);
        // Disable zoom/scroll for previewed chart, visible chart ranges depends on preview chart viewport so
        // zoom/scroll is unnecessary.
        //chart.setZoomEnabled(false);
        //chart.setScrollEnabled(false);

        //previewChart.setLineChartData(previewData);
        //previewChart.setViewportChangeListener(new ViewportListener());

        //previewX(false);

        return view;
    }



    private void generateDefaultData() {
        int numValues = 10;

        List<PointValue> values = new ArrayList<PointValue>();
        for (int i = 0; i < numValues; ++i) {
            values.add(new PointValue(i, (float) Math.random() * 100f));
        }

        Line line1 = new Line(values);
        Line line2 = new Line(values);
        Line line3 = new Line(values);

        line1.setColor(ChartUtils.COLOR_GREEN);
        line1.setHasPoints(false);// too many values so don't draw points.
        line2.setColor(ChartUtils.COLOR_RED);
        line2.setHasPoints(false);
        line3.setColor(ChartUtils.COLOR_BLUE);
        line3.setHasPoints(false);

        List<Line> lines = new ArrayList<Line>();
        lines.add(line1);
        lines.add(line2);
        lines.add(line3);

        data = new LineChartData(lines);
        data.setAxisXBottom(new Axis());
        data.setAxisYLeft(new Axis().setHasLines(true));

        // prepare preview data, is better to use separate deep copy for preview chart.
        // Set color to grey to make preview area more visible.
        previewData = new LineChartData(data);
        previewData.getLines().get(0).setColor(ChartUtils.DEFAULT_DARKEN_COLOR);

    }

    private void previewY() {
        Viewport tempViewport = new Viewport(chart.getMaximumViewport());
        float dy = tempViewport.height() / 4;
        tempViewport.inset(0, dy);
        previewChart.setCurrentViewportWithAnimation(tempViewport);
        previewChart.setZoomType(ZoomType.VERTICAL);
    }

    private void previewX(boolean animate) {
        Viewport tempViewport = new Viewport(chart.getMaximumViewport());
        float dx = tempViewport.width() / 4;
        tempViewport.inset(dx, 0);
        if (animate) {
            previewChart.setCurrentViewportWithAnimation(tempViewport);
        } else {
            previewChart.setCurrentViewport(tempViewport);
        }
        previewChart.setZoomType(ZoomType.HORIZONTAL);
    }

    private void previewXY() {
        // Better to not modify viewport of any chart directly so create a copy.
        Viewport tempViewport = new Viewport(chart.getMaximumViewport());
        // Make temp viewport smaller.
        float dx = tempViewport.width() / 4;
        float dy = tempViewport.height() / 4;
        tempViewport.inset(dx, dy);
        previewChart.setCurrentViewportWithAnimation(tempViewport);
    }



    private class ViewportListener implements ViewportChangeListener {

        @Override
        public void onViewportChanged(Viewport newViewport) {
            // don't use animation, it is unnecessary when using preview chart.
            chart.setCurrentViewport(newViewport);
        }

    }


    private class GetSensorIDTask extends AsyncTask<String, Void, ArrayList<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            // TODO Auto-generated method stub



            String result = null;
           // String url = "http://10.189.93.95:3000/sensor/availableSensors";
            String url = "http://smartwaterwatch.mybluemix.net/sensor/availableSensors";
            BufferedReader in = null;
            StringBuffer sb = new StringBuffer("");
            HttpClient client = new DefaultHttpClient();
            try {
                Log.d("1", "");

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

            }

            result = sb.toString();
            Log.d("response of smartwatch", "" + result);
            String res = null;
            ArrayList<String> sensorIDs = new ArrayList<String>();
            ArrayList<String> mainSensorData = new ArrayList<>();
            try {

                JSONArray sensors = new JSONArray(result);
                for(int i = 0; i<sensors.length();i++){

                    JSONObject sensor = new JSONObject();
                    sensor = sensors.getJSONObject(i);
                    String id = sensor.getString("_id");
                    sensorIDs.add(id);
                }
                String url2 = "http://smartwaterwatch.mybluemix.net/sensor/data/"+sensorIDs.get(0);
                HttpGet request = new HttpGet(url2);

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
                String dataOne = sb.toString();
                Log.d("Data", dataOne
                );
                JSONArray dataOneArray = new JSONArray(dataOne);
                for(int i = 0 ;i< dataOneArray.length();i++){
                    JSONObject sensor = dataOneArray.getJSONObject(i);
                    Log.d("Sensor", sensor.toString());
                    String data = sensor.getString("data");
                    mainSensorData.add(data);
                }




            }catch (Exception e){
                Log.d("Json Exception",""+e);
            }

            return mainSensorData;
        }




        @Override
        protected void onPostExecute(ArrayList<String> result) {

            int numValues = result.size();

            List<PointValue> values = new ArrayList<PointValue>();
            for (int i = 0; i < numValues; ++i) {
                values.add(new PointValue(i, Float.parseFloat(result.get(i))));
            }

            Line line1 = new Line(values);

            line1.setColor(ChartUtils.COLOR_GREEN);
            line1.setHasPoints(false);// too many values so don't draw points.


            List<Line> lines = new ArrayList<Line>();
            lines.add(line1);


            data = new LineChartData(lines);
            data.setAxisXBottom(new Axis());
            data.setAxisYLeft(new Axis().setHasLines(true));

            // prepare preview data, is better to use separate deep copy for preview chart.
            // Set color to grey to make preview area more visible.
            previewData = new LineChartData(data);
            previewData.getLines().get(0).setColor(ChartUtils.DEFAULT_DARKEN_COLOR);





        }

    }


}
