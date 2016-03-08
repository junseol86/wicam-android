package com.wicam.a_common_utils.account_related.ImageUploadAndDelete;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by Hyeonmin on 2015-07-22.
 */
public abstract class ImageUploadingActivity extends Activity {

    protected ImageView imageToUpload;
    protected Bitmap bitmapToUpload;
    protected Button imageLoad;
    protected LoadAndUploadImage loadAndUploadImage;
    public final int REQ_CODE_PICK_GALLERY = 900001;
    public final int REQ_CODE_PICK_CAMERA = 900002;
    public int mImageSizeBoundary = 1280;
    public ProgressDialog dialog;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new ProgressDialog(this);
        dialog.setTitle("�̹����� ���ε��ϰ� �ֽ��ϴ�.");
        loadAndUploadImage = new LoadAndUploadImage(this);
    }

    abstract protected void imageUploadDone();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_PICK_GALLERY && resultCode == Activity.RESULT_OK) {
            // �������� ��� ��ٷ� data �� uri�� �Ѿ��.
            Uri uri = data.getData();
            loadAndUploadImage.copyUriToFile(uri, loadAndUploadImage.getTempImageFile());
            loadAndUploadImage.doFinalProcess();
        } else if (requestCode == REQ_CODE_PICK_CAMERA && resultCode == Activity.RESULT_OK) {
            // ī�޶��� ��� file �� ������� ���ƿ�.
            // ī�޶� ȸ�� ����.
            loadAndUploadImage.correctCameraOrientation(loadAndUploadImage.getTempImageFile());
            loadAndUploadImage.doFinalProcess();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}
