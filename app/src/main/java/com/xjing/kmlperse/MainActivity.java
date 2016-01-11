package com.xjing.kmlperse;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.xjing.kmlperse.utils.KmlReader;
import com.xjing.kmlperse.utils.Placemark;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startReadingKml();
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
            // TODO Auto-generated method stub
            for (Placemark placemark : placemarks) {
                Log.d("-------------", "getLatitude----->" + placemark.toString());
            }
        }
    };
}
