package com.wicam.d_default_restaurant.detail_page;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;

import com.wicam.R;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

/**
 * Created by Hyeonmin on 2015-04-04.
 */
public class RestaurantMap extends Activity implements MapView.MapViewEventListener {

    private MapView myMapView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        setContentView(R.layout.d_handong_bus_map);

        myMapView = new MapView(this);
        myMapView.setMapViewEventListener(this);
        myMapView.setDaumMapApiKey("1529db8c07f9d7476bc182edf5d2f3d7");
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
        myMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(36.068861, 129.393098), 4, false);


    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
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
