package com.wicam.a_common_utils.account_related.RequestAuthority;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Security;
import com.wicam.a_common_utils.common_values.Singleton;

import java.io.UnsupportedEncodingException;

/**
 * Created by Hyeonmin on 2015-07-29.
 */
public class RequestAuthorityActivity extends Activity {

    String itemId, itemName, userNickname;
    EditText reasonEditText, phoneEditText;
    TextView itemNameTv, reasonTextCount, phoneTextCount;
    Button submitBtn;
    MyCache myCache;
    String phoneNumber, reason;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_common_request_authority);
        myCache = new MyCache(this);

        itemId = Singleton.create().getItemDataList().get(Singleton.create().getItemPosition()).getItemId();
        itemName = Singleton.create().getItemDataList().get(Singleton.create().getItemPosition()).getItemName();
        itemNameTv = (TextView)findViewById(R.id.authority_request_item_name);
        itemNameTv.setText(itemName);

        reasonEditText = (EditText)findViewById(R.id.authority_request_reason_edit_text);
        reasonTextCount = (TextView)findViewById(R.id.authority_request_reason_text_count);
        new EditTextAndTextCount(reasonEditText, reasonTextCount, 200).setEditTextAndTextCount();

        phoneEditText = (EditText)findViewById(R.id.authority_request_phone_edit_text);
        phoneTextCount = (TextView)findViewById(R.id.authority_request_phone_text_count);
        new EditTextAndTextCount(phoneEditText, phoneTextCount, 15).setEditTextAndTextCount();

        submitBtn = (Button)findViewById(R.id.request_authority_submit);
        submitBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        submitBtn.setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_UP:
                        submitBtn.setTextColor(new WicamColors().TEXT_NORMAL);
                        requestAuthority();
                        break;
                }
                return false;
            }
        });
    }

    public void requestAuthority() {
        if (phoneEditText.getText().toString().trim().equalsIgnoreCase("") || reasonEditText.getText().toString().trim().equalsIgnoreCase("")) {
            new AlertDialog.Builder(this)
                    .setMessage("요청사유와 전화번호를 입력해주세요.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .show();
        }
        else {

            try {
                itemName = new UTFConvert().convert(itemName);
                userNickname = new UTFConvert().convert(myCache.getMyNickname());
                phoneNumber = new UTFConvert().convert(phoneEditText.getText().toString());
                reason = new UTFConvert().convert(reasonEditText.getText().toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            new RequestAuthorityAsyncTask(this).execute(new Security().WEB_ADDRESS
                    + "request_authority.php?default_code=" + myCache.getDefaultCode() + "&content_idx=" + myCache.getContentId() + "&item_id=" + itemId + "&item_name=" + itemName
                    + "&user_id=" + myCache.getMyId() + "&user_nickname=" + userNickname + "&device_id=" + myCache.getDeviceId()
                    + "&phone_number=" + phoneNumber + "&reason=" + reason);
        }
    }

    public void requestResult() {
        new AlertDialog.Builder(this)
                .setMessage("요청이 접수되었습니다.")
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .show();
    }
}
