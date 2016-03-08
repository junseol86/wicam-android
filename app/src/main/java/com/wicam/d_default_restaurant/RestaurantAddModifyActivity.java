package com.wicam.d_default_restaurant;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wicam.R;
import com.wicam.a_common_utils.EditTextAndTextCount;
import com.wicam.a_common_utils.UTFConvert;
import com.wicam.a_common_utils.WicamColors;
import com.wicam.a_common_utils.account_related.add_item.select_location.SelectLocationActivity;
import com.wicam.a_common_utils.account_related.add_item.select_schools.SelectSchoolsActivity;
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Security;
import com.wicam.a_common_utils.common_values.Singleton;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-25.
 */
public class RestaurantAddModifyActivity extends Activity {

    private Button schoolBtn, genreBtn, mapBtn, submitBtn;
    private TextView mapText;
    private String[] genres = {"한식", "분식", "중식", "일식", "양식", "세계", "고기", "치킨", "카페", "주점"};
    private String schoolNames, restaurantName, genre="", address, keyword, phone1, phone2, myNickname;
    private Double latitude, longitude;
    private ArrayList<String> schoolIdList, schoolIdListOriginal; // 학교를 고르다 취소할 때를 대비해, 고르기 전 카피본을 하나 만듦
    private RestaurantData restaurantData;
    private EditText restaurantNameEditText, addressEditText, keywordEditText, phone1EditText, phone2EditText;
    private TextView restaurantNameCount, addressCount, keywordTextCount, phone1TextCount, phone2TextCount;
    private ProgressDialog progressDialog;
    private boolean setEditTextsOnce = true; // 수정하러 들어왔을 때 첫 한 번만 EditText들을 세팅해준다.
    private boolean wrote = false;
    private int addOrModify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_restaurant_add_new);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("식당정보를 올리고 있습니다.");

        addOrModify = Singleton.create().getAddOrModify();

        if (Singleton.create().getAddOrModify() == 0) { // 추가
            schoolNames = new MyCache(this).getMySchoolName();

            schoolIdList = new ArrayList<String>();
            schoolIdList.add(new MyCache(this).getMySchoolId());
            Singleton.create().setSchoolIdList(schoolIdList);
            schoolIdListOriginal = (ArrayList<String>)schoolIdList.clone();

        }
        else { // 변경
            schoolNames = "";
            this.restaurantData = Singleton.create().getRestaurantData();
            for (String schoolName : restaurantData.getSchoolNameList()) {
                schoolNames += schoolName;
                if (restaurantData.getSchoolNameList().indexOf(schoolName) < restaurantData.getSchoolNameList().size() - 1) {
                    schoolNames += ", ";
                }
            }

            this.schoolIdList = (ArrayList<String>)restaurantData.getSchoolIdList().clone();
            Singleton.create().setSchoolIdList(schoolIdList);
            schoolIdListOriginal = (ArrayList<String>)schoolIdList.clone();

            restaurantName = restaurantData.getItemName();
            genre = restaurantData.getGenre();
            address = restaurantData.getAddress();
            keyword = restaurantData.getDescription();
            phone1 = restaurantData.getPhone1();
            phone2 = restaurantData.getPhone2();
            latitude = restaurantData.getLatitude().trim().equalsIgnoreCase("") ? null : Double.parseDouble(restaurantData.getLatitude());
            longitude = restaurantData.getLongitude().trim().equalsIgnoreCase("") ? null: Double.parseDouble(restaurantData.getLongitude());
        }

        schoolBtn = (Button)findViewById(R.id.school_btn);
        schoolBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        schoolBtn.setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        schoolBtn.setTextColor(new WicamColors().TEXT_NORMAL);
                        break;
                    case MotionEvent.ACTION_UP:
                        schoolBtn.setTextColor(new WicamColors().TEXT_NORMAL);

                        Singleton.create().setSchoolIdList(schoolIdList);
                        startActivityForResult(new Intent(RestaurantAddModifyActivity.this, SelectSchoolsActivity.class), 0);
                        break;
                }
                return false;
            }
        });


        restaurantNameEditText = (EditText)findViewById(R.id.restaurant_name_edit_text);
        restaurantNameCount = (TextView)findViewById(R.id.restaurant_name_text_count);
        new EditTextAndTextCount(restaurantNameEditText, restaurantNameCount, 20).setEditTextAndTextCount();

        genreBtn = (Button)findViewById(R.id.pick_genre_button);
        genreBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        genreBtn.setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        genreBtn.setTextColor(new WicamColors().TEXT_NORMAL);
                        break;
                    case MotionEvent.ACTION_UP:
                        genreBtn.setTextColor(new WicamColors().TEXT_NORMAL);
                        new AlertDialog.Builder(RestaurantAddModifyActivity.this).setTitle("종류를 선택하세요")
                                .setItems(genres, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        genre = genres[i];
                                        setTextsByValues();
                                    }
                                }).show();
                        break;
                }
                return false;
            }
        });

        addressEditText = (EditText)findViewById(R.id.address_edit_text);
        addressCount = (TextView)findViewById(R.id.address_text_count);
        new EditTextAndTextCount(addressEditText, addressCount, 40).setEditTextAndTextCount();

        keywordEditText = (EditText)findViewById(R.id.description_edit_text);
        keywordTextCount = (TextView)findViewById(R.id.description_text_count);
        new EditTextAndTextCount(keywordEditText, keywordTextCount, 50).setEditTextAndTextCount();

        phone1EditText = (EditText)findViewById(R.id.phone1_edit_text);
        phone1TextCount = (TextView)findViewById(R.id.phone1_text_count);
        new EditTextAndTextCount(phone1EditText, phone1TextCount, 15).setEditTextAndTextCount();

        phone2EditText = (EditText)findViewById(R.id.phone2_edit_text);
        phone2TextCount = (TextView)findViewById(R.id.phone2_text_count);
        new EditTextAndTextCount(phone2EditText, phone2TextCount, 15).setEditTextAndTextCount();

        mapText = (TextView)findViewById(R.id.map_text);
        mapBtn = (Button)findViewById(R.id.map_btn);
        mapBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mapBtn.setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        mapBtn.setTextColor(new WicamColors().TEXT_NORMAL);
                        break;
                    case MotionEvent.ACTION_UP:
                        mapBtn.setTextColor(new WicamColors().TEXT_NORMAL);
                        if (latitude == null || longitude == null) // 위치값이 없으면 지도버튼은 지도를 고르는 화면으로
                            startActivityForResult(new Intent(RestaurantAddModifyActivity.this, SelectLocationActivity.class), 1);
                        else { // 위치값이 있으면 지도버튼은 값들을 클리어
                            latitude = null;
                            longitude = null;
                            setTextsByValues();
                        }
                        break;
                }
                return false;
            }
        });

        submitBtn = (Button)findViewById(R.id.restaurant_add_submit);
        submitBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        submitBtn.setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_UP:
                        submitBtn.setTextColor(new WicamColors().TEXT_NORMAL);
                        addRestaurantSubmit();
                        break;
                }
                return false;
            }
        });

        setTextsByValues();
    }

    public void setTextsByValues() { // 입력받은, 혹은 수정하기 위해 전달받은 값들에 따라 화면의 텍스트들을 세팅
        ((TextView)findViewById(R.id.school_to_see_text)).setText(schoolNames);


        if (genre != null)
            genreBtn.setText(genre.equalsIgnoreCase("") ? "종류를 선택하세요 ▼" : genre + " ▼");

        if (addOrModify == 1 && setEditTextsOnce) {
            setEditTextsOnce = false;
            restaurantNameEditText.setText(!restaurantNameEditText.getText().toString().equalsIgnoreCase("") ? "" : restaurantName);
            addressEditText.setText(address);
            keywordEditText.setText(keyword);
            phone1EditText.setText(phone1);
            phone2EditText.setText(phone2);
        }

        if (latitude != null && longitude != null) {
            mapText.setText(String.valueOf(latitude).substring(0, 6) + ", " + String.valueOf(longitude).substring(0, 7));
            mapBtn.setText("지도상 위치 삭제");
        }
        else {
            mapText.setText("(없음)");
            mapBtn.setText("지도에서 찾기");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) { // 학교를 고르고 왔을 때
            if (resultCode == RESULT_OK) {
                schoolNames = data.getStringExtra("schoolNames");
                schoolIdListOriginal = (ArrayList<String>)schoolIdList.clone();
            }
            else {
                schoolIdList = (ArrayList<String>)schoolIdListOriginal.clone();
            }
        }
        else if (requestCode == 1) { // 지도에서 위치를 찍고 왔을 때
            if (resultCode == RESULT_OK) {
                latitude = data.getDoubleExtra("latitude", 0.0);
                longitude = data.getDoubleExtra("longitude", 0.0);
            }
        }
        setTextsByValues();
    }

    public void addRestaurantSubmit() {
        try {
            restaurantName = new UTFConvert().convert(restaurantNameEditText.getText().toString());
            genre = new UTFConvert().convert(genre);
            address = new UTFConvert().convert(addressEditText.getText().toString());
            keyword = new UTFConvert().convert(keywordEditText.getText().toString());
            phone1 = new UTFConvert().convert(phone1EditText.getText().toString());
            phone2 = new UTFConvert().convert(phone2EditText.getText().toString());
            myNickname = new UTFConvert().convert(new MyCache(this).getMyNickname());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (restaurantName.trim().equalsIgnoreCase("") || genre.trim().equalsIgnoreCase("") || address.trim().equalsIgnoreCase("")) {

            new AlertDialog.Builder(this)
                    .setMessage("식당명, 종류, 간략 위치 정보는 필수 기입 사항입니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .show();
            return;
            }

        String schoolId = "";
        if (schoolIdList.size() == 1)
            schoolId = schoolIdList.get(0);
        else {
            for (String id: schoolIdList)
                schoolId += "|" + id + "|";
        }

        if (wrote)
            return;
        wrote = true;
        progressDialog.show();
        if (addOrModify == 0) { // 추가
            new RestaurantAddModifyAsyncTask(this).execute(new Security().WEB_ADDRESS + "add_restaurant.php?user_id=" + new MyCache(this).getMyId() + "&device_id=" + new MyCache(this).getDeviceId()
                    + "&user_nickname=" + myNickname + "&restaurant_name=" + restaurantName + "&genre=" + genre
                    + "&description=" + keyword + "&address=" + address + "&latitude=" + (latitude == null ? "" : String.valueOf(latitude)) + "&longitude=" + (longitude == null ? "" : String.valueOf(longitude))
                    + "&phone1=" + phone1 + "&phone2=" + phone2 + "&school_id=" + schoolId);
        }
        else { // 변경
            new RestaurantAddModifyAsyncTask(this).execute(new Security().WEB_ADDRESS + "modify_restaurant.php?user_id=" + new MyCache(this).getMyId() + "&device_id=" + new MyCache(this).getDeviceId()
                    + "&default_code=" + new MyCache(this).getDefaultCode() + "&content_id=" + new MyCache(this).getContentId() + "&item_id=" + restaurantData.getItemId()
                    + "&user_nickname=" + myNickname + "&restaurant_name=" + restaurantName + "&genre=" + genre
                    + "&description=" + keyword + "&address=" + address + "&latitude=" + (latitude == null ? "" : String.valueOf(latitude)) + "&longitude=" + (longitude == null ? "" : String.valueOf(longitude))
                    + "&phone1=" + phone1 + "&phone2=" + phone2 + "&school_id=" + schoolId);
        }

    }

    public void addRestaurantResult(String restaurantId) {
        progressDialog.dismiss();
        Intent intent = new Intent().putExtra("itemId", restaurantId);
        setResult(1, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("편집을 종료하고 나가시겠습니까?.")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
    }
}
