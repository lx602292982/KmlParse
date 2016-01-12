package com.xjing.kmlperse;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.xjing.kmlperse.utils.KmlReader;
import com.xjing.kmlperse.utils.Placemark;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener,
        AMap.OnMarkerDragListener, AMap.OnMapLoadedListener, AMap.InfoWindowAdapter {
    private MapView mapView;
    private AMap aMap;
    private LatLng latlng = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在onCreat方法中给aMap对象赋值
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 必须要写
        startReadingKml();
        init();
    }

    private void startReadingKml() {
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

    private KmlReader.Callback kmlReaderCallback = new KmlReader.Callback() {

        @Override
        public void onDocumentParsed(List<Placemark> placemarks) {
            for (Placemark placemark : placemarks) {
                Log.d("-------------", "getLatitude----->" + placemark.toString());
                addMarkersToMap(placemark);
            }
        }
    };

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        setUpMap();
    }

    private void setUpMap() {
        aMap.setOnMarkerDragListener(this);// 设置marker可拖拽事件监听器
        aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
        aMap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
        aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
    }

    private void addMarkersToMap(Placemark mark) {
        ArrayList<MarkerOptions> list = new ArrayList<>();
        double latitude = mark.getPoint().get(0).getLatitude();
        double longitude = mark.getPoint().get(0).getLongitude();
        latlng = new LatLng(latitude, longitude);
        MarkerOptions options = new MarkerOptions();
        options.title(mark.getName().replaceAll("S=.*", ""));
        options.position(latlng);
        options.icon(BitmapDescriptorFactory.fromView(getMyView(mark.getName().replaceAll("S=.*", ""))));
        options.visible(true);
        options.draggable(true);
        options.snippet("http://img.ivsky.com/img/tupian/co/201509/13/zhagana-007.jpg");
        list.add(options);
        for (int i = 0; i < list.size(); i++) {
            aMap.addMarker(list.get(i));
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    @Override
    public View getInfoWindow(Marker marker) {
        return InfoWindow(marker);
    }

    @Override
    public View getInfoContents(Marker marker) {
        return InfoWindow(marker);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
//        Toast.makeText(getApplicationContext(), marker.getTitle(), Toast.LENGTH_SHORT).show();
    }

    private View InfoWindow(Marker marker) {
        View infoWindow = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.info_window, null);
        ImageView img_view = (ImageView) infoWindow.findViewById(R.id.img_view);
        Picasso.with(getApplication())
                .load(marker.getSnippet())
                .fit()
                .centerCrop()
                .into(img_view);
        return infoWindow;
    }

    @Override
    public void onMapLoaded() {
        // 设置所有maker显示在当前可视区域地图中
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(latlng)
                .build();
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 15));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }


    protected View getMyView(String pm_val) {
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_info_window, null);
        TextView tv_val = (TextView) view.findViewById(R.id.snippet);
        String title = pm_val.replace(" ", "\n");
        tv_val.setText(title);
        return view;
    }
}




