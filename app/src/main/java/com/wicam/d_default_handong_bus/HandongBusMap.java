package com.wicam.d_default_handong_bus;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;

import com.wicam.R;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

/**
 * Created by Hyeonmin on 2015-04-04.
 */
public class HandongBusMap extends Activity implements MapView.MapViewEventListener {

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

        MapPolyline polyline = new MapPolyline();
        polyline.setTag(1000);
        polyline.setLineColor(Color.parseColor("#bb33a7ff")); // Polyline 컬러 지정.

        // Polyline 좌표 지정.
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.104100, 129.390475)); //------------ 한동대
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.105588, 129.395427));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.105579, 129.398270));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.101253, 129.405769));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.100976, 129.409396));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.099988, 129.410812));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.097075, 129.408838));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.093139, 129.418021));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.083515, 129.415146));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.082787, 129.415747));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.081660, 129.415125));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.080602, 129.411112));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.082666, 129.409760));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.081937, 129.406155));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.081799, 129.396864));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.081851, 129.391950));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.076067, 129.392605));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.076397, 129.394826));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.076397, 129.397047));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.070812, 129.399439));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.069945, 129.399096));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.068297, 129.390373));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.067074, 129.388646));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.066736, 129.386243));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.060812, 129.379398));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.054177, 129.376208));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.053422, 129.375553));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.052512, 129.373547));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.052035, 129.373955));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.051219, 129.372463));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.048855, 129.371837));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.048764, 129.372111));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.040262, 129.370061));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.040648, 129.367009));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.040991, 129.366789));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.043980, 129.368184));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.044162, 129.368420));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.048425, 129.370963));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.051255, 129.371860));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.051580, 129.372144));
        polyline.addPoint(MapPoint.mapPointWithGeoCoord(36.052508, 129.373571));
        // Polyline 지도에 올리기.
        myMapView.addPolyline(polyline);

        MapPOIItem marker1 = new MapPOIItem();
        marker1.setItemName("한동대학교");
        marker1.setMapPoint(MapPoint.mapPointWithGeoCoord(36.104100, 129.390475));
        marker1.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker1.setShowCalloutBalloonOnTouch(true);
        myMapView.addPOIItem(marker1);

        MapPOIItem marker2 = new MapPOIItem();
        marker2.setItemName("한독셀프세차장");
        marker2.setMapPoint(MapPoint.mapPointWithGeoCoord(36.081924, 129.405637));
        marker2.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker2.setShowCalloutBalloonOnTouch(true);
        myMapView.addPOIItem(marker2);

        MapPOIItem marker3 = new MapPOIItem();
        marker3.setItemName("하나로마트");
        marker3.setMapPoint(MapPoint.mapPointWithGeoCoord(36.081792, 129.398894));
        marker3.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker3.setShowCalloutBalloonOnTouch(true);
        myMapView.addPOIItem(marker3);

        MapPOIItem marker4 = new MapPOIItem();
        marker4.setItemName("E1주유소");
        marker4.setMapPoint(MapPoint.mapPointWithGeoCoord(36.081851, 129.392248));
        marker4.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker4.setShowCalloutBalloonOnTouch(true);
        myMapView.addPOIItem(marker4);

        MapPOIItem marker5 = new MapPOIItem();
        marker5.setItemName("장흥초등학교");
        marker5.setMapPoint(MapPoint.mapPointWithGeoCoord(36.078293, 129.392337));
        marker5.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker5.setShowCalloutBalloonOnTouch(true);
        myMapView.addPOIItem(marker5);

        MapPOIItem marker6 = new MapPOIItem();
        marker6.setItemName("양덕사거리");
        marker6.setMapPoint(MapPoint.mapPointWithGeoCoord(36.076403, 129.395824));
        marker6.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker6.setShowCalloutBalloonOnTouch(true);
        myMapView.addPOIItem(marker6);

        MapPOIItem marker7 = new MapPOIItem();
        marker7.setItemName("환호동 주민센터");
        marker7.setMapPoint(MapPoint.mapPointWithGeoCoord(36.069684, 129.397632));
        marker7.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker7.setShowCalloutBalloonOnTouch(true);
        myMapView.addPOIItem(marker7);

        MapPOIItem marker8 = new MapPOIItem();
        marker8.setItemName("해맞이 그린빌");
        marker8.setMapPoint(MapPoint.mapPointWithGeoCoord(36.068861, 129.393098));
        marker8.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker8.setShowCalloutBalloonOnTouch(true);
        myMapView.addPOIItem(marker8);

        MapPOIItem marker9 = new MapPOIItem();
        marker9.setItemName("명지탕");
        marker9.setMapPoint(MapPoint.mapPointWithGeoCoord(36.065588, 129.384766));
        marker9.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker9.setShowCalloutBalloonOnTouch(true);
        myMapView.addPOIItem(marker9);

        MapPOIItem marker10 = new MapPOIItem();
        marker10.setItemName("두호동 농협");
        marker10.setMapPoint(MapPoint.mapPointWithGeoCoord(36.060670, 129.379306));
        marker10.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker10.setShowCalloutBalloonOnTouch(true);
        myMapView.addPOIItem(marker10);

        MapPOIItem marker11 = new MapPOIItem();
        marker11.setItemName("항구우체국");
        marker11.setMapPoint(MapPoint.mapPointWithGeoCoord(36.055240, 129.376619));
        marker11.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker11.setShowCalloutBalloonOnTouch(true);
        myMapView.addPOIItem(marker11);

        MapPOIItem marker12 = new MapPOIItem();
        marker12.setItemName("경북광유주유소");
        marker12.setMapPoint(MapPoint.mapPointWithGeoCoord(36.050823, 129.372230));
        marker12.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker12.setShowCalloutBalloonOnTouch(true);
        myMapView.addPOIItem(marker12);

        MapPOIItem marker13 = new MapPOIItem();
        marker13.setItemName("기쁨의교회");
        marker13.setMapPoint(MapPoint.mapPointWithGeoCoord(36.045376, 129.371234));
        marker13.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker13.setShowCalloutBalloonOnTouch(true);
        myMapView.addPOIItem(marker13);

        MapPOIItem marker14 = new MapPOIItem();
        marker14.setItemName("육거리");
        marker14.setMapPoint(MapPoint.mapPointWithGeoCoord(36.040786, 129.366851));
        marker14.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker14.setShowCalloutBalloonOnTouch(true);
        myMapView.addPOIItem(marker14);

        MapPOIItem marker15 = new MapPOIItem();
        marker15.setItemName("기쁨의교회");
        marker15.setMapPoint(MapPoint.mapPointWithGeoCoord(36.046369, 129.369759));
        marker15.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker15.setShowCalloutBalloonOnTouch(true);
        myMapView.addPOIItem(marker15);

        MapPOIItem marker16 = new MapPOIItem();
        marker16.setItemName("현대로데오타워");
        marker16.setMapPoint(MapPoint.mapPointWithGeoCoord(36.050458, 129.371595));
        marker16.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker16.setShowCalloutBalloonOnTouch(true);
        myMapView.addPOIItem(marker16);

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
