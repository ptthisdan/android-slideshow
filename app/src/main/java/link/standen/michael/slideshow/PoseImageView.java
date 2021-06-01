package link.standen.michael.slideshow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import java.nio.ByteBuffer;

public class PoseImageView extends android.support.v7.widget.AppCompatImageView {
    private static final String TAG = PoseImageView.class.getName();

    public static float[] kpnts = new float[120];  // > 3*34
    public static int num;

    int resizeWidth = 320;
    int resizeHeight = 180;
    int mask_left = (int)(0.2*resizeWidth);
    int mask_right = (int)(0.8*resizeWidth);

    public PoseImageView(Context context) {
        super(context);
    }
    public PoseImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PoseImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        Drawable drawable = this.getDrawable();
        if(drawable!=null) {
            GlideBitmapDrawable bitmapDrawable = ((GlideBitmapDrawable) drawable);
//            Bitmap bitmap = bitmapDrawable.getBitmap();
            Bitmap bitmap = (scaleBitmap(bitmapDrawable.getBitmap(), resizeWidth, resizeHeight)).copy(Bitmap.Config.ARGB_8888, false);
            byte[] bgr = bitmapToBgr(bitmap);
            // Mask out left & right edge
            for(int i=0; i<resizeHeight; i++){
                for(int j=0; j<resizeWidth; j++){
                    int pixel_now = 3*(i*resizeWidth + j);
                    if(j<=mask_left || j>=mask_right){
                        bgr[pixel_now] = 0;
                        bgr[pixel_now+1]=0;
                        bgr[pixel_now+2]=0;
                    }
                }
            }
            ByteBuffer buf = ByteBuffer.allocateDirect(bgr.length);
            buf.put(bgr);

            int num = com.baidu.arpose.ARMdlHumanPoseJNI.predictPose(buf, resizeWidth, resizeHeight, 0, false, kpnts);

            if(num>0){
                Log.e(TAG, num + "people detected!");
            }


        }

        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.BLACK);
        super.onDraw(canvas);

        canvas.drawLine(0, 0, 100, 100, p);

        canvas.drawLine(0, 0, 20, 20, p);
        canvas.drawLine(20, 0, 0, 20, p);

    }

    private Bitmap scaleBitmap(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
                true);
    }

    /**
     * bitmap转化为bgr数据，格式为{@link Bitmap.Config#ARGB_8888}
     *
     * @param image 传入的bitmap
     * @return bgr数据
     */
    private static byte[] bitmapToBgr(Bitmap image) {
        if (image == null) {
            return null;
        }
//        Bitmap.Config config = image.getConfig();
        int bytes = image.getByteCount();

        ByteBuffer buffer = ByteBuffer.allocate(bytes);
        image.copyPixelsToBuffer(buffer);
        byte[] temp = buffer.array();
        byte[] pixels = new byte[(temp.length / 4) * 3];
        for (int i = 0; i < temp.length / 4; i++) {
            pixels[i * 3] = temp[i * 4 + 2];
            pixels[i * 3 + 1] = temp[i * 4 + 1];
            pixels[i * 3 + 2] = temp[i * 4];
        }
        return pixels;
    }

}
