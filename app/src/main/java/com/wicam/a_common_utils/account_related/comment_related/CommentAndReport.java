package com.wicam.a_common_utils.account_related.comment_related;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wicam.R;
import com.wicam.a_common_utils.WicamColors;
import com.wicam.a_common_utils.account_related.NickNameNeededTask;
import com.wicam.a_common_utils.account_related.item_detail_comment.ItemData;

/**
 * Created by Hyeonmin on 2015-07-21.
 */
public class CommentAndReport {
    DetailActivityWithCommentAndReport detailActivityWithCommentAndReport;
    Button commentBtn, reportBtn;
    ItemData itemData;
    ImageView reportBtnImage;
    TextView reportBtnText, reportBtnNumber;

    public CommentAndReport(final DetailActivityWithCommentAndReport detailActivityWithCommentAndReport, final ItemData itemData) {
        this.detailActivityWithCommentAndReport = detailActivityWithCommentAndReport;
        this.commentBtn = (Button)detailActivityWithCommentAndReport.findViewById(R.id.comment_button);
        this.reportBtn = (Button)detailActivityWithCommentAndReport.findViewById(R.id.report_button);
        this.reportBtnImage = (ImageView)detailActivityWithCommentAndReport.findViewById(R.id.report_button_image);
        this.reportBtnText = (TextView)detailActivityWithCommentAndReport.findViewById(R.id.report_button_text);
        this.reportBtnNumber = (TextView)detailActivityWithCommentAndReport.findViewById(R.id.report_button_number);

        this.itemData = itemData;

        commentBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ((TextView) detailActivityWithCommentAndReport.findViewById(R.id.comment_button_text)).setTextColor(new WicamColors().WC_BLUE);
                        break;
                    case MotionEvent.ACTION_UP:
                        ((TextView) detailActivityWithCommentAndReport.findViewById(R.id.comment_button_text)).setTextColor(new WicamColors().TEXT_NORMAL);
                        new NickNameNeededTask(detailActivityWithCommentAndReport, 2);
                        break;
                }
                return false;
            }
        });

        reportBtnImage.setImageResource(itemData.getReports() == 0 ? R.drawable.report_btn_image_off : R.drawable.report_btn_image_on);
        reportBtnText.setTextColor(itemData.getReports() == 0 ? new WicamColors().TEXT_VERY_LIGHT : new WicamColors().TEXT_NORMAL);
        reportBtnNumber.setText("×" + (itemData.getReports()));
        if (itemData.getReports() > 0 || detailActivityWithCommentAndReport.reportOnly == 1) {
            reportBtn.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            ((TextView) detailActivityWithCommentAndReport.findViewById(R.id.report_button_text)).setTextColor(new WicamColors().WC_BLUE);
                            break;
                        case MotionEvent.ACTION_UP:
                            ((TextView) detailActivityWithCommentAndReport.findViewById(R.id.report_button_text)).setTextColor(new WicamColors().TEXT_NORMAL);
                            detailActivityWithCommentAndReport.reportOnly = 1 - detailActivityWithCommentAndReport.reportOnly;
                            detailActivityWithCommentAndReport.contentsRefresh();
                            reportBtnText.setText(detailActivityWithCommentAndReport.reportOnly == 0 ? "오류제보 보기" : "전체 댓글 보기");
                            break;
                    }
                    return false;
                }
            });
        }

    }
}
