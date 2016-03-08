package com.wicam.b_intro_page;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.wicam.R;
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Security;
import com.wicam.a_common_utils.SingleAnswerAsyncTask;
import com.wicam.b_select_schools_page.SelectSchoolActivity;
import com.wicam.c_main_page.MainPage;

import java.io.UnsupportedEncodingException;
import java.util.UUID;


public class IntroActivity extends Activity {

    private String deviceUUID;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.b_intro_page);

        if (!getSharedPreferences("wicam_id", MODE_PRIVATE).getBoolean("madeID", false)) {
            new AlertDialog.Builder(this)
                    .setMessage("본 앱은 기기를 기준으로 하는 사용자 구분을 위하여 기기의 Android_ID 혹은 Device ID를 수집합니다.  전화번호 등의 개인정보는 수집하지 않습니다.")
                    .setPositiveButton("동의", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            checkVersion();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .show();
        }
        else
            checkVersion();
    }

    public void checkVersion() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("기기 등록 / 버전 확인 중");

        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.show();
                registeredCheck();
            }
        }, 1000L);
    }

    public void registeredCheck() { // 완료되는대로 versionCheck를 실행한다.
        new RegisteredCheckAsyncTask(this).execute(new Security().WEB_ADDRESS + "user_id.php?device_id=" + getDeviceUUID());
    }

    public void versionCheck() { // 본 엑티비티의 경우 버전이 합격되는대로 goToSelectSchoolPage를 실행하여 학교 선택 화면으로 이동한다.
        new SingleAnswerAsyncTask(this, this).execute(new Security().WEB_ADDRESS + "version_check.php");
    }

    public void goToSelectSchoolPage() {
        progressDialog.dismiss();
        if (!getSharedPreferences("wicam_id", MODE_PRIVATE).getBoolean("madeID", false))
            getSharedPreferences("wicam_id", MODE_PRIVATE).edit().putBoolean("madeID", true).commit();

        if (new MyCache(this).getMySchoolId().equalsIgnoreCase(""))
            startActivity(new Intent(IntroActivity.this, SelectSchoolActivity.class));
        else
            startActivity(new Intent(IntroActivity.this, MainPage.class));
        finish();
    }

    public String getDeviceUUID() { // 기기의 고유 번호를 구하는 메소드
        final SharedPreferences cache = getSharedPreferences("wicam_id", MODE_PRIVATE);
        final String id = cache.getString("device_id", "");

        if (!id.equalsIgnoreCase("")) {
            deviceUUID = id;
            return deviceUUID;
        } else {
            UUID uuid = null;
            final String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            try {
                if (!"9774d56d682e549c".equals(androidId)) {
                    uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                } else {
                    final String deviceId = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
                    uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
                }
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            (cache.edit()).putString("device_id", uuid.toString()).commit();

            deviceUUID = uuid.toString();
            return deviceUUID;
        }
    }

    public void getNewVersion() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("위캠의 새 버전이 나왔습니다!")
                .setMessage("지금 새 버전으로 업드레이드하세요 ^^")
                .setPositiveButton("지금 받기", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("market://details?id=com.wicam"));
                        startActivity(intent);
                        finish();
                    }
                }).show();
    }

}
