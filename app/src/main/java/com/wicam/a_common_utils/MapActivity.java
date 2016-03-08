package com.wicam.a_common_utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;

import com.wicam.R;
import com.wicam.a_common_utils.common_values.Singleton;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

/**
 * Created by Hyeonmin on 2015-04-04.
 */
public class MapActivity extends Activity implements MapView.MapViewEventListener {

    private MapView myMapView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.d_handong_bus_map);

        myMapView = new MapView(this);
        myMapView.setMapViewEventListener(this);
        myMapView.setDaumMapApiKey("7fc5b69490e546becf8cd111a97ab8394581e6c2");
        myMapView.setMapType(MapView.MapType.Standard);

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_layout);
        mapViewContainer.addView(myMapView);

    }

    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(0, intent);
        finish();
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

        myMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(Singleton.create().getLatitude(), Singleton.create().getLongitude()), 1, false);
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(Singleton.create().getMarkerName());
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(Singleton.create().getLatitude(), Singleton.create().getLongitude()));
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker.setShowCalloutBalloonOnTouch(true);
        mapView.addPOIItem(marker);

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
        System.out.println("하하하하하하");
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {
    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }
}
