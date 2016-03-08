package com.wicam.a_common_utils.account_related.add_item.select_location;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wicam.R;
import com.wicam.a_common_utils.UTFConvert;
import com.wicam.a_common_utils.WicamColors;
import com.wicam.a_common_utils.common_values.Security;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-27.
 */
public class SelectLocationActivity extends Activity implements MapView.MapViewEventListener {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ProgressDialog progressDialog;
    private EditText searchReigon;
    private Button searchRegionBtn, selectOkBtn;
    private MapView myMapView;
    private ImageView mapPointer;
    private Double latitude, longitude;

    private ArrayList<LocationData> locationList = new ArrayList<LocationData>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_common_select_location);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("목록들 다운로드하고 있습니다.");

        myMapView = new MapView(this);
        myMapView.setDaumMapApiKey("7fc5b69490e546becf8cd111a97ab8394581e6c2");
        myMapView.setMapType(MapView.MapType.Standard);
        myMapView.setMapViewEventListener(this);

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_container);
        mapViewContainer.addView(myMapView);

        recyclerView = (RecyclerView)findViewById(R.id.select_location_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        searchReigon = (EditText)findViewById(R.id.select_location_edit_text);
        searchReigon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((TextView) findViewById(R.id.select_location_edit_text_description)).setVisibility(s.toString().trim().length() > 0 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        searchReigon.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER)
                    searchReigon();
                return false;
            }
        });

        searchRegionBtn = (Button)findViewById(R.id.select_location_search_btn);
        searchRegionBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        searchRegionBtn.setBackgroundResource(R.drawable.rounded_semi_blue);
                        break;
                    case MotionEvent.ACTION_UP:
                        searchRegionBtn.setBackgroundResource(R.drawable.rounded_dark_blue);
                        searchReigon();
                        break;
                }
                return false;
            }
        });

        selectOkBtn = (Button)findViewById(R.id.select_location_ok_btn);
        selectOkBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        selectOkBtn.setBackgroundColor(new WicamColors().WC_DARK_BLUE);
                        break;
                    case MotionEvent.ACTION_UP:
                        selectOkBtn.setBackgroundColor(new WicamColors().WC_BLUE);
                        Intent intent = new Intent();
                        intent.putExtra("latitude", latitude).putExtra("longitude", longitude);
                        setResult(SelectLocationActivity.RESULT_OK, intent);
                        finish();
                        break;
                }
                return false;
            }
        });
        mapPointer = (ImageView)findViewById(R.id.map_pointer);
    }

    public void searchReigon() {
        if (searchReigon.getText().toString().trim().equalsIgnoreCase(""))
            return;
        progressDialog.show();
        locationList.removeAll(locationList);
        String regionKeyword = "";
        try {
            regionKeyword = new UTFConvert().convert(searchReigon.getText().toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (regionKeyword.equalsIgnoreCase(""))
            return;
        new LocationSearchAsyncTask(SelectLocationActivity.this).execute(new Security().WEB_ADDRESS + "find_location.php?address=" + regionKeyword);
    }

    public void showSearchResult() {
        progressDialog.dismiss();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchReigon.getWindowToken(), 0);
        recyclerView.setVisibility(View.VISIBLE);
        selectOkBtn.setVisibility(View.GONE);
        SelectLocationAdapter adapter = new SelectLocationAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    public void selectRegion(Double latitude, Double longitude) {
        recyclerView.setVisibility(View.GONE);
        myMapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(latitude, longitude), 0, false);
        selectOkBtn.setVisibility(View.VISIBLE);
        mapPointer.setVisibility(View.VISIBLE);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public ArrayList<LocationData> getLocationList() {
        return locationList;
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {
    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        mapView.setMapCenterPointAndZoomLevel(mapPoint, -2, false);
        selectOkBtn.setVisibility(View.VISIBLE);
        mapPointer.setVisibility(View.VISIBLE);
        latitude = mapPoint.getMapPointGeoCoord().latitude;
        longitude = mapPoint.getMapPointGeoCoord().longitude;
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
        selectOkBtn.setVisibility(View.GONE);
        mapPointer.setVisibility(View.GONE);
    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }
}
