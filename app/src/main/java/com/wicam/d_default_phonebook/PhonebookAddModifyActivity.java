package com.wicam.d_default_phonebook;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wicam.R;
import com.wicam.a_common_utils.EditTextAndTextCount;
import com.wicam.a_common_utils.UTFConvert;
import com.wicam.a_common_utils.WebViewPage;
import com.wicam.a_common_utils.WicamColors;
import com.wicam.a_common_utils.account_related.ImageUploadAndDelete.ImageDeleteAsyncTask;
import com.wicam.a_common_utils.account_related.ImageUploadAndDelete.ImageUploadingActivity;
import com.wicam.a_common_utils.account_related.ImageUploadAndDelete.LoadAndUploadImage;
import com.wicam.a_common_utils.account_related.add_item.select_schools.SelectSchoolsActivity;
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Security;
import com.wicam.a_common_utils.common_values.Singleton;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-07-25.
 */
public class PhonebookAddModifyActivity extends ImageUploadingActivity {

    private Button schoolBtn, kindBtn, urlTest, submitBtn;
    private Button imageLoadButton, imageClearButton, imageRestoreButton;
    private String[] kinds = {"학교기관", "학생기관", "동아리", "학회", "편의"};
    private String schoolNames, phonebookName, kind="", urlLink, description, phone1, phone2, myNickname;
    private ArrayList<String> schoolIdList, schoolIdListOriginal; // 학교를 고르다 취소할 때를 대비해, 고르기 전 카피본을 하나 만듦
    private PhonebookData phonebookData;
    private EditText phonebookNameEditText, urlLinkEditText, descriptionEditText, phone1EditText, phone2EditText;
    private TextView phonebookNameCount, descriptionTextCount, urlLinkTextCount, phone1TextCount, phone2TextCount;
    private int imageState = 0; //Submit시 취해야 할 이미지 관련 작업 - 0:변화없음 1:이미지삽입,수정 2:이미지(있으면)삭제
    private String phonebookIdToReturn; // 입력, 수정, 삭제 후 원래 리스트로 돌아갈때 줄 itemId
    private ProgressDialog progressDialog;
    private boolean setEditTextsOnce = true; // 수정하러 들어왔을 때 첫 한 번만 EditText들을 세팅해준다.
    private boolean wrote = false;
    private int addOrModify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.d_phonebook_add_new);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("기관·동아리 정보를 올리고 있습니다.");

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
            this.phonebookData = Singleton.create().getPhonebookData();
            for (String schoolName : phonebookData.getSchoolNameList()) {
                schoolNames += schoolName;
                if (phonebookData.getSchoolNameList().indexOf(schoolName) < phonebookData.getSchoolNameList().size() - 1) {
                    schoolNames += ", ";
                }
            }

            this.schoolIdList = (ArrayList<String>)phonebookData.getSchoolIdList().clone();
            Singleton.create().setSchoolIdList(schoolIdList);
            schoolIdListOriginal = (ArrayList<String>)schoolIdList.clone();

            phonebookName = phonebookData.getItemName();
            kind = phonebookData.getKind();
            urlLink = phonebookData.getUrlLink();
            description = phonebookData.getDescription();
            phone1 = phonebookData.getPhone1();
            phone2 = phonebookData.getPhone2();
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
                        startActivityForResult(new Intent(PhonebookAddModifyActivity.this, SelectSchoolsActivity.class), 0);
                        break;
                }
                return false;
            }
        });

        phonebookNameEditText = (EditText)findViewById(R.id.phonebook_name_edit_text);
        phonebookNameCount = (TextView)findViewById(R.id.phonebook_name_text_count);
        new EditTextAndTextCount(phonebookNameEditText, phonebookNameCount, 20).setEditTextAndTextCount();

        kindBtn = (Button)findViewById(R.id.pick_kind_button);
        kindBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        kindBtn.setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        kindBtn.setTextColor(new WicamColors().TEXT_NORMAL);
                        break;
                    case MotionEvent.ACTION_UP:
                        kindBtn.setTextColor(new WicamColors().TEXT_NORMAL);
                        new AlertDialog.Builder(PhonebookAddModifyActivity.this).setTitle("종류를 선택하세요")
                                .setItems(kinds, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(Intent.ACTION_DIAL);
                                        kind = kinds[i];
                                        setTextsByValues();
                                    }
                                }).show();
                        break;
                }
                return false;
            }
        });


        phone1EditText = (EditText)findViewById(R.id.phone1_edit_text);
        phone1TextCount = (TextView)findViewById(R.id.phone1_text_count);
        new EditTextAndTextCount(phone1EditText, phone1TextCount, 15).setEditTextAndTextCount();

        phone2EditText = (EditText)findViewById(R.id.phone2_edit_text);
        phone2TextCount = (TextView)findViewById(R.id.phone2_text_count);
        new EditTextAndTextCount(phone2EditText, phone2TextCount, 15).setEditTextAndTextCount();


        urlLinkEditText = (EditText)findViewById(R.id.url_edit_text);
        urlLinkTextCount = (TextView)findViewById(R.id.url_text_count);
        new EditTextAndTextCount(urlLinkEditText, urlLinkTextCount, 1000).setEditTextAndTextCount();

        urlTest = (Button)findViewById(R.id.url_test);
        urlTest.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           if (urlLinkEditText.getText().toString().trim().equalsIgnoreCase(""))
                                               return;
                                           startActivity(new Intent(PhonebookAddModifyActivity.this, WebViewPage.class).putExtra("url_link", getUrl(urlLinkEditText.getText().toString())));
                                       }
                                   });

        descriptionEditText = (EditText)findViewById(R.id.description_edit_text);
        descriptionTextCount = (TextView)findViewById(R.id.description_text_count);
        new EditTextAndTextCount(descriptionEditText, descriptionTextCount, 500).setEditTextAndTextCount();

        submitBtn = (Button)findViewById(R.id.phonebook_add_submit);
        submitBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        submitBtn.setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_UP:
                        submitBtn.setTextColor(new WicamColors().TEXT_NORMAL);
                        addPhonebookSubmit();
                        break;
                }
                return false;
            }
        });

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
                        new AlertDialog.Builder(PhonebookAddModifyActivity.this).setTitle("올릴 이미지")
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
                        imageRestoreButton.setVisibility((addOrModify == 1 && phonebookData.getImage() != null) ? View.VISIBLE : View.GONE);
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
                        imageToUpload.setImageBitmap(phonebookData.getImage());
                        imageState = 0;
                        imageRestoreButton.setVisibility(View.GONE);
                        imageClearButton.setVisibility((addOrModify == 1 && phonebookData.getImage() != null) ? View.VISIBLE : View.GONE);
                        break;
                }
                return false;
            }
        });

        setTextsByValues();
    }

    public void setTextsByValues() { // 입력받은, 혹은 수정하기 위해 전달받은 값들에 따라 화면의 텍스트들을 세팅
        ((TextView)findViewById(R.id.school_to_see_text)).setText(schoolNames);

        if (kind != null)
            kindBtn.setText(kind.equalsIgnoreCase("") ? "종류를 선택하세요 ▼" : kind + " ▼");

        if (addOrModify == 1 && setEditTextsOnce) {
            setEditTextsOnce = false;
            phonebookNameEditText.setText(!phonebookNameEditText.getText().toString().equalsIgnoreCase("") ? "" : phonebookName);
            kindBtn.setText(kind + " ▼");
            urlLinkEditText.setText(urlLink);
            descriptionEditText.setText(description);
            phone1EditText.setText(phone1);
            phone2EditText.setText(phone2);

            if (phonebookData.getImage() != null)
                imageToUpload.setImageBitmap(phonebookData.getImage());
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
        else if (requestCode == REQ_CODE_PICK_GALLERY && resultCode == Activity.RESULT_OK) {
            // 갤러리의 경우 곧바로 data 에 uri가 넘어옴.
            Uri uri = data.getData();
            loadAndUploadImage.copyUriToFile(uri, loadAndUploadImage.getTempImageFile());
            loadAndUploadImage.doFinalProcess();
            imageState = 1;
            imageClearButton.setVisibility(View.VISIBLE);
            imageLoadButton.setVisibility(View.GONE);
            imageRestoreButton.setVisibility((addOrModify == 1 && phonebookData.getImage() != null) ? View.VISIBLE : View.GONE);
        } else if (requestCode == REQ_CODE_PICK_CAMERA && resultCode == Activity.RESULT_OK) {
            // 카메라의 경우 file 로 결과물이 돌아옴.
            // 카메라 회전 보정.
            loadAndUploadImage.correctCameraOrientation(loadAndUploadImage.getTempImageFile());
            loadAndUploadImage.doFinalProcess();
            imageState = 1;
            imageClearButton.setVisibility(View.VISIBLE);
            imageLoadButton.setVisibility(View.GONE);
            imageRestoreButton.setVisibility((addOrModify == 1 && phonebookData.getImage() != null) ? View.VISIBLE : View.GONE);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

        setTextsByValues();
    }

    public String getUrl(String url) {
        return url.equalsIgnoreCase("")? "" : url.contains("http://") ? url : "http://" + url;
    }

    public void addPhonebookSubmit() {
        try {
            phonebookName = new UTFConvert().convert(phonebookNameEditText.getText().toString());
            kind = new UTFConvert().convert(kind);
            description = new UTFConvert().convert(descriptionEditText.getText().toString());
            urlLink = new UTFConvert().convert(getUrl(urlLinkEditText.getText().toString()));
            phone1 = new UTFConvert().convert(phone1EditText.getText().toString());
            phone2 = new UTFConvert().convert(phone2EditText.getText().toString());
            myNickname = new UTFConvert().convert(new MyCache(this).getMyNickname());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (phonebookName.trim().equalsIgnoreCase("") || kind.trim().equalsIgnoreCase("")) {

            new AlertDialog.Builder(this)
                    .setMessage("기관·동아리명, 종류는 필수 기입 사항입니다.")
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
            new PhonebookAddModifyAsyncTask(this).execute(new Security().WEB_ADDRESS + "add_phonebook.php?user_id=" + new MyCache(this).getMyId() + "&device_id=" + new MyCache(this).getDeviceId()
                    + "&user_nickname=" + myNickname + "&phonebook_name=" + phonebookName + "&kind=" + kind
                    + "&description=" + description + "&url_link=" + urlLink
                    + "&phone1=" + phone1 + "&phone2=" + phone2 + "&school_id=" + schoolId);
        }
        else { // 변경
            new PhonebookAddModifyAsyncTask(this).execute(new Security().WEB_ADDRESS + "modify_phonebook.php?user_id=" + new MyCache(this).getMyId() + "&device_id=" + new MyCache(this).getDeviceId()
                    + "&default_code=" + new MyCache(this).getDefaultCode() + "&content_id=" + new MyCache(this).getContentId() + "&item_id=" + phonebookData.getItemId()
                    + "&user_nickname=" + myNickname + "&phonebook_name=" + phonebookName + "&kind=" + kind
                    + "&description=" + description + "&url_link=" + urlLink
                    + "&phone1=" + phone1 + "&phone2=" + phone2 + "&school_id=" + schoolId);
        }

    }

    public void addPhonebookResult(final String phonebookId) {
        this.phonebookIdToReturn = phonebookId;
        if (imageState == 0) {
            progressDialog.dismiss();
            Intent intent = new Intent().putExtra("itemId", phonebookIdToReturn);
            setResult(1, intent);
            finish();
        }
        else if (imageState == 1) {
            new Thread(new Runnable() {
                public void run() {
                    loadAndUploadImage.doPhotoUpload("item_" + phonebookId + ".jpg");
                }
            }).start();
        }
        else if (imageState == 2) {
            new ImageDeleteAsyncTask(this).execute(new Security().WEB_ADDRESS + "image_delete.php?default_code=" + new MyCache(this).getDefaultCode()
                    + "&content_id=" + new MyCache(this).getContentId() + "&content_type=" + new MyCache(this).getContentType()
                    + "&image_name=" + "item_" + phonebookId + ".jpg");
        }
    }
    
    @Override
    protected void imageUploadDone() {
        progressDialog.dismiss();
        Intent intent = new Intent().putExtra("itemId", phonebookIdToReturn);
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
