package com.wicam.c_content_add;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.wicam.R;
import com.wicam.a_common_utils.EditTextAndTextCount;
import com.wicam.a_common_utils.UTFConvert;
import com.wicam.a_common_utils.WebViewPage;
import com.wicam.a_common_utils.WicamColors;
import com.wicam.a_common_utils.account_related.add_item.select_schools.SelectSchoolsActivity;
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Security;
import com.wicam.a_common_utils.common_values.Singleton;
import com.wicam.c_main_page.content_drawer.ContentListData;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Hyeonmin on 2015-08-10.
 */
public class ContentAddActivity extends Activity {

    private CardView addAndroid, addWebsite, addCustom;
    private boolean inputOn = false;
    private EditText contentNameEt, descriptionEt, urlLinkEt, packageNameEt, contactEt;
    private TextView contentNameTc, descriptionTc, urlLinkTc, packageNameTc, contactTc;
    private Button schoolBtn, packageNameFindBtn, packageNameTestBtn, urlTestBtn, addSubmitBtn;
    private String schoolNames, contentName, contentType, description, urlLink, packageName, contact;
    private ArrayList<String> schoolIdList, schoolIdListOriginal; // 학교를 고르다 취소할 때를 대비해, 고르기 전 카피본을 하나 만듦
    private ContentListData contentData;
    private ProgressDialog progressDialog;
    private boolean setEditTextsOnce = true; // 수정하러 들어왔을 때 첫 한 번만 EditText들을 세팅해준다.
    private boolean wrote = false;
    private int addOrModify;
    private MyCache myCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.c_content_add);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("컨텐츠를 업로드하고 있습니다.");
        myCache = new MyCache(this);

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
            this.contentData = Singleton.create().getContentListData();
            for (String schoolName : contentData.getSchoolNameList()) {
                schoolNames += schoolName;
                if (contentData.getSchoolNameList().indexOf(schoolName) < contentData.getSchoolNameList().size() - 1) {
                    schoolNames += ", ";
                }
            }

            this.schoolIdList = (ArrayList<String>)contentData.getSchoolIdList().clone();
            Singleton.create().setSchoolIdList(schoolIdList);
            schoolIdListOriginal = (ArrayList<String>)schoolIdList.clone();

            contentName = contentData.getContent_name();
            contentType = contentData.getContent_type();
            urlLink = contentData.getUrl_link();
            packageName = contentData.getPackage_name();
            description = contentData.getDescription();
            contact = contentData.getContact();

            viewToggle();
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
                        startActivityForResult(new Intent(ContentAddActivity.this, SelectSchoolsActivity.class), 0);
                        break;
                }
                return false;
            }
        });

        contentNameEt = (EditText)findViewById(R.id.content_name_edit_text);
        contentNameTc = (TextView)findViewById(R.id.content_name_text_count);
        new EditTextAndTextCount(contentNameEt, contentNameTc, 20).setEditTextAndTextCount();

        descriptionEt = (EditText)findViewById(R.id.description_edit_text);
        descriptionTc = (TextView)findViewById(R.id.description_text_count);
        new EditTextAndTextCount(descriptionEt, descriptionTc, 100).setEditTextAndTextCount();

        urlLinkEt = (EditText)findViewById(R.id.url_edit_text);
        urlLinkTc = (TextView)findViewById(R.id.url_text_count);
        new EditTextAndTextCount(urlLinkEt, urlLinkTc, 1000).setEditTextAndTextCount();

        packageNameEt = (EditText)findViewById(R.id.package_edit_text);
        packageNameTc = (TextView)findViewById(R.id.package_text_count);
        new EditTextAndTextCount(packageNameEt, packageNameTc, 40).setEditTextAndTextCount();

        contactEt = (EditText)findViewById(R.id.contact_edit_text);
        contactTc = (TextView)findViewById(R.id.contact_text_count);
        new EditTextAndTextCount(contactEt, contactTc, 30).setEditTextAndTextCount();

        packageNameFindBtn = (Button)findViewById(R.id.package_app);
        packageNameFindBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        packageNameFindBtn.setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        packageNameFindBtn.setTextColor(new WicamColors().TEXT_NORMAL);
                        break;
                    case MotionEvent.ACTION_UP:
                        packageNameFindBtn.setTextColor(new WicamColors().TEXT_NORMAL);
                        final String packageName = "nadesico.Package.Info.List.Lite";
                        if (isAppInstalled(packageName)) {
                            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
                            startActivity(launchIntent);
                        } else {
                            new AlertDialog.Builder(ContentAddActivity.this)
                                    .setMessage("패키지명 보기 앱이 아직 설치되어 있지 않습니다.  Play스토어로 이동하시겠습니까?")
                                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(Uri.parse("market://details?id=" + packageName));
                                            startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                                    .show();
                        }
                        break;
                }
                return false;
            }
        });

        packageNameTestBtn = (Button)findViewById(R.id.package_test);
        packageNameTestBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        packageNameTestBtn.setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        packageNameTestBtn.setTextColor(new WicamColors().TEXT_NORMAL);
                        break;
                    case MotionEvent.ACTION_UP:
                        packageNameTestBtn.setTextColor(new WicamColors().TEXT_NORMAL);
                        final String packageName = packageNameEt.getText().toString();
                        if (isAppInstalled(packageName)){
                            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
                            startActivity(launchIntent);
                        }
                        else {
                            new AlertDialog.Builder(ContentAddActivity.this)
                                    .setMessage("앱이 설치되어 있지 않습니다.  Play스토어로 이동하시겠습니까?")
                                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(Uri.parse("market://details?id=" + packageName));
                                            startActivity(intent);
                                        }
                                    })
                                    .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                                    .show();
                        }
                        break;
                }
                return false;
            }
        });

        urlTestBtn = (Button)findViewById(R.id.url_test);
        urlTestBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        urlTestBtn.setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        urlTestBtn.setTextColor(new WicamColors().TEXT_NORMAL);
                        break;
                    case MotionEvent.ACTION_UP:
                        urlTestBtn.setTextColor(new WicamColors().TEXT_NORMAL);
                        if (urlLinkEt.getText().toString().trim().equalsIgnoreCase(""))
                            return false;
                        startActivity(new Intent(ContentAddActivity.this, WebViewPage.class).putExtra("url_link", getUrl(urlLinkEt.getText().toString())));
                        break;
                }
                return false;
            }
        });

        addAndroid = (CardView)findViewById(R.id.android_card_view);
        addAndroid.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ((TextView)findViewById(R.id.android_card_view_title)).setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        ((TextView)findViewById(R.id.android_card_view_title)).setTextColor(new WicamColors().TEXT_NORMAL);
                        break;
                    case MotionEvent.ACTION_UP:
                        ((TextView)findViewById(R.id.android_card_view_title)).setTextColor(new WicamColors().TEXT_NORMAL);
                        contentType = "3";
                        viewToggle();
                        break;
                }
                return false;
            }
        });

        addWebsite = (CardView)findViewById(R.id.website_card_view);
        addWebsite.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ((TextView)findViewById(R.id.website_card_view_title)).setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        ((TextView)findViewById(R.id.website_card_view_title)).setTextColor(new WicamColors().TEXT_NORMAL);
                        break;
                    case MotionEvent.ACTION_UP:
                        ((TextView)findViewById(R.id.website_card_view_title)).setTextColor(new WicamColors().TEXT_NORMAL);
                        contentType = "2";
                        viewToggle();
                        break;
                }
                return false;
            }
        });

        addCustom = (CardView)findViewById(R.id.custom_card_view);
        addCustom.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ((TextView)findViewById(R.id.custom_card_view_title)).setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        ((TextView)findViewById(R.id.custom_card_view_title)).setTextColor(new WicamColors().TEXT_NORMAL);
                        break;
                    case MotionEvent.ACTION_UP:
                        ((TextView)findViewById(R.id.custom_card_view_title)).setTextColor(new WicamColors().TEXT_NORMAL);
                        contentType = "1";
                        viewToggle();
                        break;
                }
                return false;
            }
        });

        addSubmitBtn = (Button)findViewById(R.id.content_add_submit);
        addSubmitBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        addSubmitBtn.setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        addSubmitBtn.setTextColor(new WicamColors().TEXT_NORMAL);
                        break;
                    case MotionEvent.ACTION_UP:
                        addSubmitBtn.setTextColor(new WicamColors().TEXT_NORMAL);
                        contentAddSubmit();

                        break;
                }
                return false;
            }
        });

        setTextsByValues();
    }

    public void contentAddSubmit() {

        if (contentNameEt.getText().toString().trim().equalsIgnoreCase("") || descriptionEt.getText().toString().trim().equalsIgnoreCase("")) {
            new AlertDialog.Builder(this)
                    .setMessage("컨텐츠 이름과 설명은 필수 입력사항입니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .show();
            return;
        }

        if (contentType.equalsIgnoreCase("3") && packageNameEt.getText().toString().trim().equalsIgnoreCase("")) {
            new AlertDialog.Builder(this)
                    .setMessage("앱의 패키지명을 입력해주세요.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .show();
            return;
        }

        if (contentType.equalsIgnoreCase("2") && urlLinkEt.getText().toString().trim().equalsIgnoreCase("")) {
            new AlertDialog.Builder(this)
                    .setMessage("웹사이트의 URL을 입력해주세요.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .show();
            return;
        }
        String nickname = "";
        try {
            nickname = new UTFConvert().convert(myCache.getMyNickname());
            contentName = new UTFConvert().convert(contentNameEt.getText().toString());
            description = new UTFConvert().convert(descriptionEt.getText().toString());
            urlLink = new UTFConvert().convert(getUrl(urlLinkEt.getText().toString()));
            packageName = new UTFConvert().convert(packageNameEt.getText().toString());
            contact = new UTFConvert().convert(contactEt.getText().toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String schoolId = "";
        if (schoolIdList.size() == 1)
            schoolId = schoolIdList.get(0);
        else {
            for (String id: schoolIdList)
                schoolId += "|" + id + "|";
        }

        if (addOrModify == 0) {
            new ContentAddAsyncTask(ContentAddActivity.this).execute(new Security().WEB_ADDRESS + "add_content.php?user_id=" + myCache.getMyId()
                            + "&user_nickname=" + nickname + "&device_id=" + myCache.getDeviceId()
                            + "&content_type=" + contentType
                            + "&content_name=" + contentName + "&description=" + description + "&contact=" + contact
                            + "&url_link=" + urlLink + "&package_name=" + packageName
                            + "&school_id=" + schoolId
            );
        }
        else {
            new ContentAddAsyncTask(ContentAddActivity.this).execute(new Security().WEB_ADDRESS + "modify_content.php?content_id=" + contentData.getContent_id()
                    + "&user_id=" + myCache.getMyId() + "&user_nickname=" + nickname + "&device_id=" + myCache.getDeviceId()
                    + "&content_name=" + contentName + "&description=" + description + "&contact=" + contact + "&school_id=" + schoolId
                    + "&url_link=" + urlLink + "&package_name=" + packageName
            );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) { // 학교를 고르고 왔을 때
            if (resultCode == RESULT_OK) {
                schoolNames = data.getStringExtra("schoolNames");
                schoolIdListOriginal = (ArrayList<String>) schoolIdList.clone();
            } else {
                schoolIdList = (ArrayList<String>) schoolIdListOriginal.clone();
            }
        }

        setTextsByValues();
    }

    public void viewToggle() {
        inputOn = !inputOn;

        ((ScrollView)findViewById(R.id.add_content_choose_type)).setVisibility(inputOn ? View.GONE : View.VISIBLE);
        ((ScrollView)findViewById(R.id.add_content_input_values)).setVisibility(!inputOn ? View.GONE : View.VISIBLE);
        ((RelativeLayout)findViewById(R.id.package_name)).setVisibility(contentType.equalsIgnoreCase("3") ? View.VISIBLE : View.GONE);
        ((RelativeLayout)findViewById(R.id.package_name_divider)).setVisibility(contentType.equalsIgnoreCase("3") ? View.VISIBLE : View.GONE);
        ((RelativeLayout)findViewById(R.id.website_url)).setVisibility(contentType.equalsIgnoreCase("2") ? View.VISIBLE : View.GONE);
        ((RelativeLayout)findViewById(R.id.website_url_divider)).setVisibility(contentType.equalsIgnoreCase("2") ? View.VISIBLE : View.GONE);

    }

    public void setTextsByValues() { // 입력받은, 혹은 수정하기 위해 전달받은 값들에 따라 화면의 텍스트들을 세팅
        ((TextView)findViewById(R.id.school_to_see_text)).setText(schoolNames);

        if (addOrModify == 1 && setEditTextsOnce) {
            setEditTextsOnce = false;
            contentNameEt.setText(contentName);
            descriptionEt.setText(description);
            urlLinkEt.setText(urlLink);
            packageNameEt.setText(packageName);
            contactEt.setText(contact);
        }

    }

    private boolean isAppInstalled(String packageName) {
        PackageManager pm = getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    public String getUrl(String url) {
        return url.equalsIgnoreCase("")? "" : url.contains("http://") ? url : "http://" + url;
    }

    public void afterContentUploaded() {
        setResult(1);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (inputOn && addOrModify == 0) {
            viewToggle();
            return;
        }

        setResult(0);
        finish();
    }

}
