package com.wicam.a_common_utils.account_related.make_nickname;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wicam.R;
import com.wicam.a_common_utils.EditTextAndTextCount;
import com.wicam.a_common_utils.UTFConvert;
import com.wicam.a_common_utils.account_related.OpenCreateItemActivity;
import com.wicam.a_common_utils.account_related.RequestAuthority.RequestAuthorityActivity;
import com.wicam.a_common_utils.account_related.comment_related.WriteCommentActivity;
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Security;
import com.wicam.c_content_add.ContentAddActivity;

import java.io.UnsupportedEncodingException;

/**
 * Created by Hyeonmin on 2015-07-21.
 */
public class MakeNicknameActivity extends Activity {

    // 닉네임이 만들어지지 않은 사용자가 가입하는 페이지

    private EditText editText, topEditText, bottomEditText;
    private TextView pageTitle, textCount, topTextCount, bottomTextCount;
    private String nickname;
    private Button checkButton, confirmButton, haveNickname;
    private MakeNicknameActivity makeNicknameActivity = this;
    private LinearLayout nicknameEditTextll, twoEditTexts;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_common_make_nickname);

        pageTitle = (TextView)findViewById(R.id.make_nickname_title);
        nicknameEditTextll = (LinearLayout)findViewById(R.id.input_nickname);
        twoEditTexts = (LinearLayout)findViewById(R.id.two_edit_texts);

        editText = (EditText)findViewById(R.id.make_nickname_edit_text);
        topEditText = (EditText)findViewById(R.id.top_edit_text);
        bottomEditText = (EditText)findViewById(R.id.bottom_edit_text);
        textCount = (TextView)findViewById(R.id.make_nickname_text_count);
        topTextCount = (TextView)findViewById(R.id.top_edit_text_count);
        bottomTextCount = (TextView)findViewById(R.id.bottom_edit_text_count);
        new EditTextAndTextCount(editText, textCount, 10).setEditTextAndTextCount();

        checkButton = (Button)findViewById(R.id.make_nickname_check_button);
        checkButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        checkButton.setBackgroundResource(R.drawable.rounded_light_blue);
                        break;
                    case MotionEvent.ACTION_UP:
                        checkButton.setBackgroundResource(R.drawable.rounded_dark_blue);
                        checkNicknameOverlap();
                        break;
                }
                return false;
            }
        });

        confirmButton = (Button)findViewById(R.id.two_edit_texts_confirm_button);

        haveNickname = (Button)findViewById(R.id.have_nickname_button);
        haveNickname.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        haveNickname.setBackgroundResource(R.drawable.rounded_semi_blue);
                        break;
                    case MotionEvent.ACTION_UP:
                        haveNickname.setBackgroundResource(R.drawable.rounded_dark_blue);
                        requestUsedNicknameAndPassword();
                        break;
                }
                return false;
            }
        });
    }

    public void checkNicknameOverlap() {
        if (editText.getText().toString().trim().equalsIgnoreCase(""))
            return;

        try {
            nickname = new UTFConvert().convert(editText.getText().toString().replace("\n", ""));
            new CheckNicknameOverlapAsyncTask(makeNicknameActivity).execute(new Security().WEB_ADDRESS + "check_nickname_overlap.php?user_nickname=" + nickname);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void overlapCheckResult(int result) {
        if (result == 0) { // 사용 가능한 닉네임일 경우
            new AlertDialog.Builder(this)
                    .setMessage("사용 가능한 닉네임입니다. '" + editText.getText().toString() + "'으로 등록하시겠습니까?")
                    .setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestPasswordTwice();
                        }
                    })
                    .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .show();
        }
        else { // 이미 같은 닉네임이 있는 경우
            new AlertDialog.Builder(this)
                    .setMessage("이미 존재하는 닉네임입니다.  다른 닉네임을 입력해주세요.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .show();
        }
    }

    public void requestPasswordTwice() { // 닉네임 중복없음이 확인되면 패스워드 두번입력 화면을 보여준다.
        pageTitle.setText("사용하실 패스워드(8자 이상)를 두 번 입력하세요.");
        nicknameEditTextll.setVisibility(View.GONE);
        twoEditTexts.setVisibility(View.VISIBLE);
        haveNickname.setVisibility(View.GONE);
        new EditTextAndTextCount(topEditText, topTextCount, 20).setEditTextAndTextCount();
        new EditTextAndTextCount(bottomEditText, bottomTextCount, 20).setEditTextAndTextCount();
        confirmButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        confirmButton.setBackgroundResource(R.drawable.rounded_semi_blue);
                        break;
                    case MotionEvent.ACTION_UP:
                        confirmButton.setBackgroundResource(R.drawable.rounded_dark_blue);
                        if (!topEditText.getText().toString().equalsIgnoreCase(bottomEditText.getText().toString())) {
                            new AlertDialog.Builder(makeNicknameActivity)
                                    .setMessage("두 칸의 패스워드가 일치하지 않습니다.  다시 확인해주세요.")
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                                    .show();
                        }
                        else if (topEditText.getText().toString().length() < 8) {
                            new AlertDialog.Builder(makeNicknameActivity)
                                    .setMessage("패스워드는 8자 이상 입력해주세요.")
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                                    .show();
                        }
                        else {
                            String password = "";
                            try {
                                password = new UTFConvert().convert(topEditText.getText().toString());
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            new SetNicknameAsyncTask(makeNicknameActivity).execute(new Security().WEB_ADDRESS
                                    + "set_nickname.php?user_id=" + new MyCache(makeNicknameActivity).getMyId() + "&user_nickname=" + nickname + "&password=" + password);
                        }
                        break;
                }
                return false;
            }
        });
    }

    public void requestUsedNicknameAndPassword() { // '사용하던 닉네임이 있습니다' 버튼을 누르면 나타나는, 닉네임과 패스워드 요청
        pageTitle.setText("사용하던 닉네임과 패스워드를 입력하세요.");
        nicknameEditTextll.setVisibility(View.GONE);
        twoEditTexts.setVisibility(View.VISIBLE);
        haveNickname.setVisibility(View.GONE);
        new EditTextAndTextCount(topEditText, topTextCount, 10).setEditTextAndTextCount();
        new EditTextAndTextCount(bottomEditText, bottomTextCount, 20).setEditTextAndTextCount();
        topEditText.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_NORMAL);

        confirmButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        confirmButton.setBackgroundResource(R.drawable.rounded_light_blue);
                        break;
                    case MotionEvent.ACTION_UP:

                        String nickname = "", password = "";
                        try {
                            nickname = new UTFConvert().convert(topEditText.getText().toString());
                            password = new UTFConvert().convert(bottomEditText.getText().toString());
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        confirmButton.setBackgroundResource(R.drawable.rounded_dark_blue);
                        new ChangeDeviceAsyncTask(MakeNicknameActivity.this).execute(
                                new Security().WEB_ADDRESS + "change_nickname_device.php?device_id=" + new MyCache(MakeNicknameActivity.this).getDeviceId()
                                        + "&nickname=" + nickname + "&password=" + password
                        );
                        break;
                }
                return false;
            }
        });
    }

    public void goToNicknameNeededActivity(int newOrChange) {
        if (newOrChange == 0)
            new MyCache(makeNicknameActivity).setMyNickname(editText.getText().toString().replace("\n", ""));
        switch (new MyCache(makeNicknameActivity).getNicknameNeededTaskCode()) {
            case 0:
                startActivityForResult(new Intent(MakeNicknameActivity.this, ContentAddActivity.class), 0);
                break;
            case 1:
                new OpenCreateItemActivity().execute(MakeNicknameActivity.this);
                break;
            case 2:
                startActivityForResult(new Intent(MakeNicknameActivity.this, WriteCommentActivity.class), 0);
                break;
            case 3:
                startActivityForResult(new Intent(MakeNicknameActivity.this, RequestAuthorityActivity.class), 0);
                break;
        }
    }

    @Override // 아이디를 갓 만든 상태에서도 글을 쓰거나 하고 돌아올 때의 화면전환을 그대로 하기 위함
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null || data.getStringExtra("itemId") == null)
            setResult(resultCode);
        else {
            String itemId = data.getStringExtra("itemId");
            Intent intent = new Intent().putExtra("itemId", itemId);
            setResult(resultCode, intent);
        }
        finish();
    }

}
