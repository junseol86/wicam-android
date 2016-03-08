package com.wicam.d_default_delivery;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wicam.R;
import com.wicam.a_common_utils.EditTextAndTextCount;
import com.wicam.a_common_utils.UTFConvert;
import com.wicam.a_common_utils.WicamColors;
import com.wicam.a_common_utils.account_related.ImageUploadAndDelete.ImageDeleteAsyncTask;
import com.wicam.a_common_utils.account_related.ImageUploadAndDelete.ImageUploadingActivity;
import com.wicam.a_common_utils.account_related.ImageUploadAndDelete.LoadAndUploadImage;
import com.wicam.a_common_utils.account_related.add_item.select_schools.SelectSchoolsActivity;
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Security;
import com.wicam.a_common_utils.common_values.Singleton;
import com.wicam.d_default_delivery.menu_page.DeliveryMenuActivity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-25.
 */
public class DeliveryAddModifyActivity extends ImageUploadingActivity {

    private Button schoolBtn, menuTestBtn, imageLoadButton, imageClearButton, imageRestoreButton, submitBtn;
    private String schoolNames, deliveryName, phone1, phone2, condition, description, includes, menu, myNickname;
    private ArrayList<String> schoolIdList, schoolIdListOriginal; // 학교를 고르다 취소할 때를 대비해, 고르기 전 카피본을 하나 만듦
    private DeliveryData deliveryData;
    private EditText deliveryNameEditText, phone1EditText, phone2EditText, conditionEditText, descriptionEditText, menuEditText;
    private TextView deliveryNameCount, phone1TextCount, phone2TextCount, conditionTextCount, descriptionTextCount, menuTextCount;
    private ArrayList<CheckBox> checkBoxList = new ArrayList<CheckBox>();
    private int imageState = 0; //Submit시 취해야 할 이미지 관련 작업 - 0:변화없음 1:이미지삽입,수정 2:이미지(있으면)삭제
    private String deliveryIdToReturn; // 입력, 수정, 삭제 후 원래 리스트로 돌아갈때 줄 itemId
    private ProgressDialog progressDialog;
    private boolean setEditTextsOnce = true; // 수정하러 들어왔을 때 첫 한 번만 EditText들을 세팅해준다.
    private boolean wrote = false;
    private int addOrModify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_delivery_add_new);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("업체정보를 올리고 있습니다.");

        loadAndUploadImage = new LoadAndUploadImage(this);
        addOrModify = Singleton.create().getAddOrModify();

        if (addOrModify == 0) { // 추가
            schoolNames = new MyCache(this).getMySchoolName();

            schoolIdList = new ArrayList<String>();
            schoolIdList.add(new MyCache(this).getMySchoolId());
            Singleton.create().setSchoolIdList(schoolIdList);
            schoolIdListOriginal = (ArrayList<String>)schoolIdList.clone();

        }
        else { // 변경
            schoolNames = "";
            this.deliveryData = Singleton.create().getDeliveryData();
            for (String schoolName : deliveryData.getSchoolNameList()) {
                schoolNames += schoolName;
                if (deliveryData.getSchoolNameList().indexOf(schoolName) < deliveryData.getSchoolNameList().size() - 1) {
                    schoolNames += ", ";
                }
            }

            this.schoolIdList = (ArrayList<String>)deliveryData.getSchoolIdList().clone();
            Singleton.create().setSchoolIdList(schoolIdList);
            schoolIdListOriginal = (ArrayList<String>)schoolIdList.clone();

            deliveryName = deliveryData.getItemName();
            phone1 = deliveryData.getPhone1();
            phone2 = deliveryData.getPhone2();
            includes = deliveryData.getIncludes();
            condition = deliveryData.getDeliveryCondition();
            menu = deliveryData.getMenu();
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
                        startActivityForResult(new Intent(DeliveryAddModifyActivity.this, SelectSchoolsActivity.class), 0);
                        break;
                }
                return false;
            }
        });


        deliveryNameEditText = (EditText)findViewById(R.id.delivery_name_edit_text);
        deliveryNameCount = (TextView)findViewById(R.id.delivery_name_text_count);
        new EditTextAndTextCount(deliveryNameEditText, deliveryNameCount, 20).setEditTextAndTextCount();

        phone1EditText = (EditText)findViewById(R.id.phone1_edit_text);
        phone1TextCount = (TextView)findViewById(R.id.phone1_text_count);
        new EditTextAndTextCount(phone1EditText, phone1TextCount, 15).setEditTextAndTextCount();

        phone2EditText = (EditText)findViewById(R.id.phone2_edit_text);
        phone2TextCount = (TextView)findViewById(R.id.phone2_text_count);
        new EditTextAndTextCount(phone2EditText, phone2TextCount, 15).setEditTextAndTextCount();

        conditionEditText = (EditText)findViewById(R.id.condition_edit_text);
        conditionTextCount = (TextView)findViewById(R.id.condition_text_count);
        new EditTextAndTextCount(conditionEditText, conditionTextCount, 20).setEditTextAndTextCount();

        descriptionEditText = (EditText)findViewById(R.id.description_edit_text);
        descriptionTextCount = (TextView)findViewById(R.id.description_text_count);
        new EditTextAndTextCount(descriptionEditText, descriptionTextCount, 500).setEditTextAndTextCount();

        checkBoxList.add((CheckBox) findViewById(R.id.fried_chicken));
        checkBoxList.add((CheckBox) findViewById(R.id.non_fried_chicken));
        checkBoxList.add((CheckBox) findViewById(R.id.korean));
        checkBoxList.add((CheckBox) findViewById(R.id.snack));
        checkBoxList.add((CheckBox)findViewById(R.id.chinese));
        checkBoxList.add((CheckBox)findViewById(R.id.japanese));
        checkBoxList.add((CheckBox)findViewById(R.id.noodle));
        checkBoxList.add((CheckBox)findViewById(R.id.bento));
        checkBoxList.add((CheckBox)findViewById(R.id.pizza));
        checkBoxList.add((CheckBox)findViewById(R.id.pork));
        checkBoxList.add((CheckBox)findViewById(R.id.fast_food));
        checkBoxList.add((CheckBox)findViewById(R.id.others));

        imageToUpload = (ImageView)findViewById(R.id.image_to_be_uploaded);

        imageLoadButton = (Button)findViewById(R.id.image_load_button);
        imageLoadButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        imageLoadButton.setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        imageLoadButton.setTextColor(new WicamColors().TEXT_NORMAL);
                        break;
                    case MotionEvent.ACTION_UP:
                        imageLoadButton.setTextColor(new WicamColors().TEXT_NORMAL);

                        final String[] loadImageChoice = {"카메라로 찍기", "갤러리에서 선택하기"};
                        new AlertDialog.Builder(DeliveryAddModifyActivity.this).setTitle("올릴 이미지")
                                .setItems(loadImageChoice, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (i == 0) {
                                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(loadAndUploadImage.getTempImageFile()));
                                            intent.putExtra("return-data", true);
                                            startActivityForResult(intent, REQ_CODE_PICK_CAMERA);
                                        } else {
                                            Intent intent = new Intent(Intent.ACTION_PICK);
                                            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                                            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                            startActivityForResult(intent, REQ_CODE_PICK_GALLERY);
                                        }
                                    }
                                }).show();

                        break;
                }
                return false;
            }
        });

        imageClearButton = (Button)findViewById(R.id.image_clear_button);
        imageClearButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        imageClearButton.setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        imageClearButton.setTextColor(new WicamColors().TEXT_NORMAL);
                        break;
                    case MotionEvent.ACTION_UP:
                        imageClearButton.setTextColor(new WicamColors().TEXT_NORMAL);
                        imageToUpload.setImageBitmap(null);
                        imageState = 2;
                        imageRestoreButton.setVisibility((addOrModify == 1 && deliveryData.getImage() != null) ? View.VISIBLE : View.GONE);
                        imageClearButton.setVisibility(View.GONE);
                        imageLoadButton.setVisibility(View.VISIBLE);
                        break;
                }
                return false;
            }
        });

        imageRestoreButton = (Button)findViewById(R.id.image_restore_button);
        imageRestoreButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        imageRestoreButton.setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        imageRestoreButton.setTextColor(new WicamColors().TEXT_NORMAL);
                        break;
                    case MotionEvent.ACTION_UP:
                        imageRestoreButton.setTextColor(new WicamColors().TEXT_NORMAL);
                        imageToUpload.setImageBitmap(deliveryData.getImage());
                        imageState = 0;
                        imageRestoreButton.setVisibility(View.GONE);
                        imageClearButton.setVisibility((addOrModify == 1 && deliveryData.getImage() != null) ? View.VISIBLE : View.GONE);
                        break;
                }
                return false;
            }
        });

        menuEditText = (EditText)findViewById(R.id.menu_edit_text);
        menuTextCount = (TextView)findViewById(R.id.menu_text_count);

        new EditTextAndTextCount(menuEditText, menuTextCount, 2000).setEditTextAndTextCount();

        menuTestBtn = (Button)findViewById(R.id.edit_menu_test);
        menuTestBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        menuTestBtn.setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        menuTestBtn.setTextColor(new WicamColors().TEXT_NORMAL);
                        break;
                    case MotionEvent.ACTION_UP:
                        menuTestBtn.setTextColor(new WicamColors().TEXT_NORMAL);

                        menu = menuEditText.getText().toString();
                        phone1 = phone1EditText.getText().toString();
                        phone2 = phone2EditText.getText().toString();
                        Intent intent = new Intent(DeliveryAddModifyActivity.this, DeliveryMenuActivity.class);
                        intent.putExtra("menu", menu);
                        intent.putExtra("phone1", phone1);
                        intent.putExtra("phone2", phone2);
                        startActivity(intent);
                        break;
                }
                return false;
            }
        });

        submitBtn = (Button)findViewById(R.id.delivery_add_submit);
        submitBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        submitBtn.setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_UP:
                        submitBtn.setTextColor(new WicamColors().TEXT_NORMAL);
                        addDeliverySubmit();
                        break;
                }
                return false;
            }
        });

        setTextsByValues();
    }

    public void setTextsByValues() { // 입력받은, 혹은 수정하기 위해 전달받은 값들에 따라 화면의 텍스트들을 세팅
        ((TextView)findViewById(R.id.school_to_see_text)).setText(schoolNames);

        if (addOrModify == 1 && setEditTextsOnce) { // 수정일 시 첫 한 번만 실행할 것들
            setEditTextsOnce = false;
            deliveryNameEditText.setText(!deliveryNameEditText.getText().toString().equalsIgnoreCase("") ? "" : deliveryName);
            phone1EditText.setText(phone1);
            phone2EditText.setText(phone2);
            conditionEditText.setText(condition);
            descriptionEditText.setText(description);
            menuEditText.setText(menu);

            for (CheckBox checkBox : checkBoxList) {
                if (includes.contains("|"+ checkBox.getTag().toString() + "|"))
                    checkBox.setChecked(true);
            }

            if (deliveryData.getImage() != null)
                imageToUpload.setImageBitmap(deliveryData.getImage());
            imageClearButton.setVisibility(View.VISIBLE);
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
            }
        }
        else if (requestCode == REQ_CODE_PICK_GALLERY && resultCode == Activity.RESULT_OK) {
            // 갤러리의 경우 곧바로 data 에 uri가 넘어옴.
            Uri uri = data.getData();
            loadAndUploadImage.copyUriToFile(uri, loadAndUploadImage.getTempImageFile());
            loadAndUploadImage.doFinalProcess();
            imageState = 1;
            imageClearButton.setVisibility(View.VISIBLE);
            imageLoadButton.setVisibility(View.GONE);
            imageRestoreButton.setVisibility((addOrModify == 1 && deliveryData.getImage() != null) ? View.VISIBLE : View.GONE);
        } else if (requestCode == REQ_CODE_PICK_CAMERA && resultCode == Activity.RESULT_OK) {
            // 카메라의 경우 file 로 결과물이 돌아옴.
            // 카메라 회전 보정.
            loadAndUploadImage.correctCameraOrientation(loadAndUploadImage.getTempImageFile());
            loadAndUploadImage.doFinalProcess();
            imageState = 1;
            imageClearButton.setVisibility(View.VISIBLE);
            imageLoadButton.setVisibility(View.GONE);
            imageRestoreButton.setVisibility((addOrModify == 1 && deliveryData.getImage() != null) ? View.VISIBLE : View.GONE);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

        setTextsByValues();
    }

    public void addDeliverySubmit() {

        includes = "";
        for (CheckBox checkBox : checkBoxList) {
            if (checkBox.isChecked())
                includes += ("|" + checkBox.getTag().toString() + "|");
        }

        try {
            deliveryName = new UTFConvert().convert(deliveryNameEditText.getText().toString());
            phone1 = new UTFConvert().convert(phone1EditText.getText().toString());
            phone2 = new UTFConvert().convert(phone2EditText.getText().toString());
            description = new UTFConvert().convert(descriptionEditText.getText().toString());
            condition = new UTFConvert().convert(conditionEditText.getText().toString());
            menu = new UTFConvert().convert(menuEditText.getText().toString());
            includes = new UTFConvert().convert(includes);
            myNickname = new UTFConvert().convert(new MyCache(this).getMyNickname());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (deliveryName.trim().equalsIgnoreCase("") || phone1.trim().equalsIgnoreCase("") || includes.equalsIgnoreCase("")) {
            new AlertDialog.Builder(this)
                    .setMessage("업체명, 포함메뉴, 전화번호는 필수 기입 사항입니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .show();
            return;
            }

        if ((addOrModify == 0 && (imageState == 0 || imageState == 2) && menu.trim().equalsIgnoreCase(""))
                || (addOrModify == 1 && imageState == 2 && menu.trim().equalsIgnoreCase(""))
                || (addOrModify == 1 && imageState == 0 && deliveryData.getImage() == null && menu.trim().equalsIgnoreCase(""))
                ) {
            new AlertDialog.Builder(this)
                    .setMessage("전단지 이미지와 메뉴 둘 중 하나는 입력되어야 합니다.")
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
            new DeliveryAddModifyAsyncTask(this).execute(new Security().WEB_ADDRESS + "add_delivery.php?user_id=" + new MyCache(this).getMyId() + "&device_id=" + new MyCache(this).getDeviceId()
                    + "&user_nickname=" + myNickname + "&delivery_name=" + deliveryName + "&menu=" + menu
                    + "&delivery_condition=" + condition + "&description=" + description + "&includes=" + includes
                    + "&phone1=" + phone1 + "&phone2=" + phone2 + "&school_id=" + schoolId);
        }
        else { // 변경
            new DeliveryAddModifyAsyncTask(this).execute(new Security().WEB_ADDRESS + "modify_delivery.php?user_id=" + new MyCache(this).getMyId() + "&device_id=" + new MyCache(this).getDeviceId()
                    + "&default_code=" + new MyCache(this).getDefaultCode() + "&content_id=" + new MyCache(this).getContentId() + "&item_id=" + deliveryData.getItemId()
                    + "&user_nickname=" + myNickname + "&delivery_name=" + deliveryName + "&menu=" + menu
                    + "&description=" + description + "&delivery_condition=" + condition + "&includes=" + includes
                    + "&phone1=" + phone1 + "&phone2=" + phone2 + "&school_id=" + schoolId);
        }
    }

    public void addDeliveryResult(final String deliveryId) {
        this.deliveryIdToReturn = deliveryId;
        if (imageState == 0) {
            progressDialog.dismiss();
            Intent intent = new Intent().putExtra("itemId", deliveryIdToReturn);
            setResult(1, intent);
            finish();
        }
        else if (imageState == 1) {
            new Thread(new Runnable() {
                public void run() {
                    loadAndUploadImage.doPhotoUpload("item_" + deliveryId + ".jpg");
                }
            }).start();
        }
        else if (imageState == 2) {
            new ImageDeleteAsyncTask(this).execute(new Security().WEB_ADDRESS + "image_delete.php?default_code=" + new MyCache(this).getDefaultCode()
                    + "&content_id=" + new MyCache(this).getContentId() + "&content_type=" + new MyCache(this).getContentType()
                    + "&image_name=" + "item_" + deliveryId + ".jpg");
        }
    }

    @Override
    protected void imageUploadDone() {
        progressDialog.dismiss();
        Intent intent = new Intent().putExtra("itemId", deliveryIdToReturn);
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
