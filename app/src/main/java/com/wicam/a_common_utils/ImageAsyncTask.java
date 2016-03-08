package com.wicam.a_common_utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.wicam.a_common_utils.scroll_to_update.ImageContainingData;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageAsyncTask extends AsyncTask {
	private RecyclerView.Adapter adapter;
	private int position;
	private String imageDirectory, imageName;
	private ImageView imageView;
	private Bitmap image;
	private ImageContainingData imageContainingData;


	public ImageAsyncTask(RecyclerView.Adapter adapter, String imageDirectory, String imageName, int position, ImageView imageView, ImageContainingData imageContainingData){
		this.adapter = adapter;
		this.position = position;
		this.imageDirectory = imageDirectory;
		this.imageName = imageName;
		this.imageView = imageView;
		this.imageContainingData = imageContainingData;
	}

	@Override
	protected void onPostExecute(Object result) { //------------------------ onPostExecute 메소드들은 @Override가 붙어야 실행된다.  Source → Override/Implement Methods...
		// TODO Auto-generated method stub
		super.onPostExecute(result);

		int ivWidth = imageView.getWidth();
		int ivHeight = imageView.getHeight();
		int picWidth = image.getWidth();
		int picHeight = image.getHeight();
		Bitmap croppedImage;

		if ((double)picWidth / (double)picHeight > (double)ivWidth / (double)ivHeight) {
			int width, height;
			height = picHeight;
			width = (int)((double)picHeight * (double)ivWidth / (double)ivHeight);
			croppedImage = Bitmap.createBitmap(image, (picWidth-width)/2, 0, width, height);
		}
		else {
			int width, height;
			width = picWidth;
			height = (int)((double)picWidth * (double)ivHeight / (double)ivWidth);
			croppedImage = Bitmap.createBitmap(image, 0, (picHeight-height)/2, width, height);

		}

		imageContainingData.setImage(croppedImage);

		imageView.setImageBitmap(croppedImage);
		adapter.notifyItemChanged(position);
	}
	   
	class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}
		
		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
		            int bite = read();
		        
		            if (bite < 0) {
		               break;  // we reached EOF
		            } else {
		               bytesSkipped = 1;   // we read one byte
		            }
		         }
		         totalBytesSkipped += bytesSkipped;
		     }
		      return totalBytesSkipped;
		}
	}

	@Override
	protected Object doInBackground(Object... params) {
		try{
			InputStream bis;
			String image_url = imageDirectory + imageName + ".jpg";
//			System.out.println(image_url);
			bis = new java.net.URL(image_url).openStream();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			image = BitmapFactory.decodeStream(new FlushedInputStream(bis), null, options);
			bis.close();
			
		} catch(Exception e) {
			e.printStackTrace();	
		}
		return null;
	}

}