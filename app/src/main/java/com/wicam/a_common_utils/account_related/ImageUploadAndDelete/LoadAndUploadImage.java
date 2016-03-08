package com.wicam.a_common_utils.account_related.ImageUploadAndDelete;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.wicam.a_common_utils.common_values.MyCache;
import com.wicam.a_common_utils.common_values.Security;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;

/**
 * Created by Hyeonmin on 2015-07-22.
 */
public class LoadAndUploadImage {
    private ImageUploadingActivity imageUploadingActivity;
    private int serverResponseCode = 0;
    private HttpURLConnection conn = null;
    private DataOutputStream dos = null;
    private String lineEnd = "\r\n";
    private String twoHyphens = "--";
    private String boundary = "*****";
    private int bytesRead, bytesAvailable, bufferSize;
    private byte[] buffer;
    private int maxBufferSize = 1 * 2000 * 2000;

    public LoadAndUploadImage(ImageUploadingActivity imageUploadingActivity) {
        this.imageUploadingActivity = imageUploadingActivity;
    }

    // ī�޶�� ������ ������ �Ʒ��� ���� ������ �̸��� ���Ϸ� ����ȴ�.
    public File getTempImageFile() {
        File path = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + imageUploadingActivity.getPackageName() + "/temp/");
        if (!path.exists()) {
            path.mkdirs();
        }
        File file = new File(path, "tempimage.png");
        return file;
    }

    // ���������� �ҷ����� URI�� ���޵ǹǷ�, ���� ���� ����ǵ��� URI���� ���Ϸ� �������ش�.
    public void copyUriToFile(Uri srcUri, File target) { // target�� getTempImageFile�� ���ڷ� �޴´�.
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        FileChannel fcin = null;
        FileChannel fcout = null;
        try {
            // ��Ʈ�� ����
            inputStream = (FileInputStream) imageUploadingActivity.getContentResolver().openInputStream(srcUri);
            outputStream = new FileOutputStream(target);

            // ä�� ����
            fcin = inputStream.getChannel();
            fcout = outputStream.getChannel();

            // ä���� ���� ��Ʈ�� ����
            long size = fcin.size();
            fcin.transferTo(0, size, fcout);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fcout.close();
            } catch (IOException ioe) {
            }
            try {
                fcin.close();
            } catch (IOException ioe) {
            }
            try {
                outputStream.close();
            } catch (IOException ioe) {
            }
            try {
                inputStream.close();
            } catch (IOException ioe) {
            }
        }
    }

    public void doFinalProcess() {
        // ó�� �̹����� �޾ƿ� ������ ���� ũ��� (�޸� ���� ����)
        imageUploadingActivity.bitmapToUpload = loadImageWithSampleSize(getTempImageFile());

        // image boundary size �� �µ��� �̹��� ���.
        imageUploadingActivity.bitmapToUpload = resizeImageWithinBoundary(imageUploadingActivity.bitmapToUpload);

        // ����� �̹����� getTempImageFile�� �ٽ� ����
        saveBitmapToFile(imageUploadingActivity.bitmapToUpload);

        imageUploadingActivity.imageToUpload.setImageBitmap(imageUploadingActivity.bitmapToUpload);
    }

    // ó�� �̹����� �޾ƿ� ������ ���� ũ��� (�޸� ���� ����)
    private Bitmap loadImageWithSampleSize(File file) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        int width = options.outWidth;
        int height = options.outHeight;
        int longSide = Math.max(width, height);
        int sampleSize = 1;
        if (longSide > imageUploadingActivity.mImageSizeBoundary) {
            sampleSize = longSide / imageUploadingActivity.mImageSizeBoundary;
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        options.inDither = false;

        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        return bitmap;
    }

    // ������ �۰� �޾ƿ� �̹����� �ٽ� ��Ȯ�ϰ� ������ ����
    private Bitmap resizeImageWithinBoundary(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width > height) {
            if (width > imageUploadingActivity.mImageSizeBoundary) {
                bitmap = resizeBitmapWithWidth(bitmap, imageUploadingActivity.mImageSizeBoundary);
            }
        } else {
            if (height > imageUploadingActivity.mImageSizeBoundary) {
                bitmap = resizeBitmapWithHeight(bitmap, imageUploadingActivity.mImageSizeBoundary);
            }
        }
        return bitmap;
    }

    private Bitmap resizeBitmapWithHeight(Bitmap source, int wantedHeight) {
        if (source == null)
            return null;

        int width = source.getWidth();
        int height = source.getHeight();

        float resizeFactor = wantedHeight * 1f / height;

        int targetWidth, targetHeight;
        targetWidth = (int) (width * resizeFactor);
        targetHeight = (int) (height * resizeFactor);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true);

        return resizedBitmap;
    }

    private Bitmap resizeBitmapWithWidth(Bitmap source, int wantedWidth) {
        if (source == null)
            return null;

        int width = source.getWidth();
        int height = source.getHeight();

        float resizeFactor = wantedWidth * 1f / width;

        int targetWidth, targetHeight;
        targetWidth = (int) (width * resizeFactor);
        targetHeight = (int) (height * resizeFactor);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, true);

        return resizedBitmap;
    }

    /** �̹��� ������ ���� ��, ī�޶� rotation ������ ������ ȸ�� ������. */
    public void correctCameraOrientation(File imgFile) {
        Bitmap bitmap = loadImageWithSampleSize(imgFile);
        try {
            ExifInterface exif = new ExifInterface(imgFile.getAbsolutePath());
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int exifRotateDegree = exifOrientationToDegrees(exifOrientation);
            bitmap = rotateImage(bitmap, exifRotateDegree);
            saveBitmapToFile(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap rotateImage(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != converted) {
                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch (OutOfMemoryError ex) {
            }
        }
        return bitmap;
    }

    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private void saveBitmapToFile(Bitmap bitmap) {
        File target = getTempImageFile();
        try {
            FileOutputStream fos = new FileOutputStream(target, false);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
        }
    }


    //---------------------------------------------------------------------------- ���δ� �̹��� ���μ���, �Ʒ��δ� ���ε�

    public void doPhotoUpload(String fileName){

        try {

            // open a URL connection to the Servlet
            FileInputStream fileInputStream = new FileInputStream(getTempImageFile());
            URL url = new URL(new Security().WEB_ADDRESS + "image_upload.php"
                    + "?default_code=" + new MyCache(imageUploadingActivity).getDefaultCode() + "&content_id=" + new MyCache(imageUploadingActivity).getContentId()
                    + "&content_type=" + new MyCache(imageUploadingActivity).getContentType()
            );

            // Open a HTTP  connection to  the URL
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", fileName);

            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename="+ fileName + "" + lineEnd);
            dos.writeBytes(lineEnd);

            // create a buffer of  maximum size
            bytesAvailable = fileInputStream.available();

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {

                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            }

            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();

            Log.i("uploadFile", "HTTP Response is : "+ serverResponseMessage + ": " + serverResponseCode);

            //close the streams //
            fileInputStream.close();
            dos.flush();
            dos.close();

        } catch (Exception e) {

        }

        imageUploadingActivity.imageUploadDone();
    }


}
