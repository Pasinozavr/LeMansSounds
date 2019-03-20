package com.example.lemanssounds;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import java.io.InputStream;
/**
 * downloads image by link and set in chosen bitmap
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private ImageView bmImage;
    /**
     * choose which imageView to fill with image
     *
     * @param bmImage imageView
     */
    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }
    /**
     * download file and save it in
     *
     * @param urls link to image
     * @return image
     */
    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Err", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }
    /**
     * sets image in chosen imageView
     *
     * @param result Bitmap file
     */
    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}