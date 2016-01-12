package com.xjing.kmlperse;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.xjing.kmlperse.utils.KmlReader;
import com.xjing.kmlperse.utils.Placemark;
import com.xjing.kmlperse.utils.Point;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class KmlPerseMap extends FragmentActivity {
    private AMap aMap;
    private Button replayButton;
    private SeekBar processbar;
    private Marker marker = null;// 当前轨迹点图案
    public Handler timer = new Handler();// 定时器
    public Runnable runnable = null;
    // 存放所有坐标的数组
    private ArrayList<LatLng> latlngList = new ArrayList<LatLng>();
    private ArrayList<LatLng> latlngList_path = new ArrayList<LatLng>();
    // private ArrayList<LatLng> latlngList_path1 = new ArrayList<LatLng>();
    Context context;
    public List<Placemark> mPlacemark = new ArrayList<Placemark>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context = this;
        init();
        float f = 0;
        for (int i = 0; i < latlngList.size() - 1; i++) {
            f += AMapUtils.calculateLineDistance(latlngList.get(i), latlngList.get(i + 1));
        }
        Log.i("float", String.valueOf(f / 1000));
    }

    private KmlReader.Callback kmlReaderCallback = new KmlReader.Callback() {

        @Override
        public void onDocumentParsed(List<Placemark> placemarks) {
            for (Placemark placemark : placemarks) {
                Log.d("-------------", "getLatitude----->" + placemark.toString());
                for (Point mPlacemark : placemark.getPoint()) {
                    latlngList.add(new LatLng(mPlacemark.getLatitude(), mPlacemark.getLongitude()));
                }
                processbar.setMax(latlngList.size());
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlngList.get(0), 4));
            }
        }
    };

    /**
     * 初始化AMap对象
     */
    private void init() {
        replayButton = (Button) findViewById(R.id.btn_replay);
        processbar = (SeekBar) findViewById(R.id.process_bar);
        processbar.setSelected(false);
        processbar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

            }
        });
        // 进度条拖动时 执行相应事件
        processbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            // 复写OnSeeBarChangeListener的三个方法
            // 第一个时OnStartTrackingTouch,在进度开始改变时执行
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            // 第二个方法onProgressChanged是当进度发生改变时执行
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub

                latlngList_path.clear();
                if (progress != 0) {
                    for (int i = 0; i < seekBar.getProgress(); i++) {
                        latlngList_path.add(latlngList.get(i));
                    }
                    drawLine(latlngList_path, progress);
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            // 第三个是onStopTrackingTouch,在停止拖动时执行
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                latlngList_path.clear();
                int current = seekBar.getProgress();
                if (current != 0) {
                    for (int i = 0; i < seekBar.getProgress(); i++) {
                        latlngList_path.add(latlngList.get(i));
                    }
                    drawLine(latlngList_path, current);
                }
            }
        });

        // 初始化runnable开始
        runnable = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                // 要做的事情
                handler.sendMessage(Message.obtain(handler, 1));
            }
        };
        // 初始化runnable结束
        // TODO Auto-generated method stub
        if (aMap == null) {
            aMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if (aMap != null) {
                try {
                    KmlReader reader = new KmlReader(kmlReaderCallback);
                    InputStream fs = getAssets().open("test.kml");
                    reader.read(fs);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void drawLine(ArrayList<LatLng> list, int current) {
        // TODO Auto-generated method stub
        aMap.clear();
        LatLng replayGeoPoint = latlngList.get(current - 1);
        if (marker != null) {
            marker.destroy();
        }
        // 添加汽车位置
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(replayGeoPoint).title("起点").snippet(" ")
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.car)))
                .anchor(0.5f, 0.5f);
        marker = aMap.addMarker(markerOptions);
        // 增加起点开始
        aMap.addMarker(new MarkerOptions().position(latlngList.get(0)).title("起点").icon(BitmapDescriptorFactory
                .fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.nav_route_result_start_point))));
        // 增加起点结束
        PolylineOptions polylineOptions1 = (new PolylineOptions()).addAll(latlngList_path).color(Color.rgb(30, 144, 255))
                .width(6.0f);
        aMap.addPolyline(polylineOptions1);
        if (latlngList_path.size() > 1) {
            PolylineOptions polylineOptions = (new PolylineOptions()).addAll(latlngList_path)
                    .color(Color.rgb(9, 129, 240)).width(6.0f);
            aMap.addPolyline(polylineOptions);
        }
        if (latlngList_path.size() == latlngList.size()) {
            aMap.addMarker(new MarkerOptions().position(latlngList.get(latlngList.size() - 1)).title("终点")
                    .icon(BitmapDescriptorFactory.fromBitmap(
                            BitmapFactory.decodeResource(getResources(), R.drawable.nav_route_result_end_point))));
        }
    }

    // 根据定时器线程传递过来指令执行任务
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                int curpro = processbar.getProgress();
                if (curpro != processbar.getMax()) {
                    processbar.setProgress(curpro + 1);
                    timer.postDelayed(runnable, 1000);// 延迟0.5秒后继续执行
                } else {
                    Button button = (Button) findViewById(R.id.btn_replay);
                    button.setText(" 回放 ");// 已执行到最后一个坐标 停止任务
                }
            }
        }
    };

    public void btn_replay_click(View v) {
        // 根据按钮上的字判断当前是否在回放
        if (replayButton.getText().toString().trim().equals("回放")) {
            if (latlngList.size() > 0) {
                // 假如当前已经回放到最后一点 置0
                if (processbar.getProgress() == processbar.getMax()) {
                    processbar.setProgress(0);
                }
                // 将按钮上的字设为"停止" 开始调用定时器回放
                replayButton.setText(" 停止 ");
                timer.postDelayed(runnable, 10);
            }
        } else {
            // 移除定时器的任务
            timer.removeCallbacks(runnable);
            replayButton.setText(" 回放 ");
        }
    }

}
