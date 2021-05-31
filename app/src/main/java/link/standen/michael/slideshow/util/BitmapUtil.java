package link.standen.michael.slideshow.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapUtil {
    private static final String LOG_TAG = "BitmapUtil";
    private static int mIndex = 0;
    private static Bitmap mBitmap = null;

    private static Bitmap getBmp(String filepath) {
        try {
            File file = new File(filepath);
            if (file.exists()) {
                mBitmap = BitmapFactory.decodeFile(filepath);
            }
        } catch (Exception e) {
        }
        return mBitmap;
    }
    private static void saveBitmap(Bitmap bitmap, String filepath) {
        File file = new File(filepath);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void saveImages(final String dstdir, final Mat mat) {
        new Thread() {
            @Override
            public void run() {
                Bitmap bm = null;
                bm = Bitmap.createBitmap( mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat, bm);
                // String savename = "/sdcard/anakin/2222.jpg";
                // Log.d(LOG_TAG,"Saving to" + savename);
                // saveBitmap(bm,savename);
                // if(bm != null && !bm.isRecycled()){
                //     // 回收并且置为null
                //     bm.recycle();
                //     bm = null;
                // }
                System.gc();
            }
        }.start();
    }
    public static Bitmap loadImageByIndex(String srcdir, Mat mRgba) {
        Log.d(LOG_TAG, mIndex + ".jpg");
        String imgpath = srcdir + mIndex + ".jpg";
        getBmp(imgpath);
        Utils.bitmapToMat(mBitmap, mRgba);
        mIndex += 1;
        // if(mIndex >= 200) {
        //     mIndex = 120;
        // }
        return mBitmap;
    }
}

