package com.wicam.a_common_utils.account_related;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import com.wicam.a_common_utils.account_related.RequestAuthority.RequestAuthorityActivity;
import com.wicam.a_common_utils.account_related.comment_related.WriteCommentActivity;
import com.wicam.a_common_utils.account_related.make_nickname.MakeNicknameActivity;
import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.c_content_add.ContentAddActivity;

/**
 * Created by Hyeonmin on 2015-07-21.
 */
public class NickNameNeededTask {
    //닉네임이 없는 상태에서 리뷰를 쓰는 작업 동을 하려 할 때 먼저 닉네팀을 확인한 뒤 없으면 생성한다.

    private int code; // 0: 컨텐츠 만들기, 1: 아이템 만들기, 2: 코멘트 남기기
    private final int CREATE_CONTENT = 0;
    private final int CREATE_ITEM = 1;
    private final int WRITE_COMMENT = 2;
    private final int REQUEST_AUTHORITY = 3;

    public NickNameNeededTask(Activity fromActivity, int code) {
        new MyCache(fromActivity).setNicknameNeededTaskCode(code);
        if (new MyCache(fromActivity).getMyNickname().trim().equalsIgnoreCase("")) {
            fromActivity.startActivityForResult(new Intent(fromActivity, MakeNicknameActivity.class), 0);
        }
        else if (new MyCache(fromActivity).getBlackList() == 1) {
            new AlertDialog.Builder(fromActivity)
                    .setMessage("문제가 되는 활동으로 인해 컨텐츠나 댓글을 게시할 수 없는 블랙리스트 회원으로 등록되셨습니다.  고객센터에 문의하시기 바랍니다.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    })
                    .show();
        }
        else {
            switch (code) {
                case CREATE_CONTENT:
                    fromActivity.startActivityForResult(new Intent(fromActivity, ContentAddActivity.class), 0);
                    break;
                case CREATE_ITEM:
                    new OpenCreateItemActivity().execute(fromActivity);
                    break;
                case WRITE_COMMENT:
                    fromActivity.startActivityForResult(new Intent(fromActivity, WriteCommentActivity.class), 0);
                    break;
                case REQUEST_AUTHORITY:
                    fromActivity.startActivityForResult(new Intent(fromActivity, RequestAuthorityActivity.class), 0);
                    break;
            }
        }
    }
}
