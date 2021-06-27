package link.standen.michael.slideshow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
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
    boolean bInValidRegion = true;
    boolean bInReadyZone = true;
    int[] LINES = {
            0, 1, 1, 2,
            3, 4, 4, 5,
            2, 6, 3, 6,
            6, 7, 7, 8,
            8, 9, 9, 10,
            11, 12, 12, 13,
            7, 13, 7, 14,
            14, 15, 15, 16};
    int[] LINECOLORS = {Color.BLUE, Color.BLUE,
            Color.CYAN, Color.CYAN,
            Color.RED, Color.RED,
            Color.GRAY, Color.GRAY,
            Color.BLACK, Color.BLACK,
            Color.GREEN, Color.GREEN,
            Color.BLACK, Color.BLACK,
            Color.YELLOW, Color.YELLOW
    };

    int lineLen = LINES.length / 2;

    Paint mPaint = new Paint();
    Path mPath = new Path();
    String mcode = "引体向上";

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

            num = com.baidu.arpose.ARMdlHumanPoseJNI.predictPose(buf, resizeWidth, resizeHeight, 0, false, kpnts);


        }

        super.onDraw(canvas);
//        drawRefLines(canvas, mPaint);
        if(num>0){
            Log.e(TAG, num + "people detected!");
            drawPose(canvas, mPaint);
        }

    }
    private void drawPose(Canvas canvas, Paint mpaint) {
        int w = canvas.getWidth();
        int h = canvas.getHeight();

        mpaint.setColor(Color.YELLOW);
        mPaint.setStrokeWidth(5);

        for (int i = 0; i < num; i++) {
            // 画线
            for (int j = 0; j < lineLen; j++) {
                int k0 = LINES[j * 2];
                int k1 = LINES[j * 2 + 1];
                int xx0 = (int) (kpnts[i * 17 * 2 + k0 * 2] * w);
                int yy0 = (int) (kpnts[i * 17 * 2 + k0 * 2 + 1] * h);
                int xx1 = (int) (kpnts[i * 17 * 2 + k1 * 2] * w);
                int yy1 = (int) (kpnts[i * 17 * 2 + k1 * 2 + 1] * h);
                if(bInReadyZone) {
                    mpaint.setColor(LINECOLORS[j]);
                }
                canvas.drawLine(xx0, yy0, xx1, yy1, mpaint);
            }
        }
    }

    private void drawRefLines(Canvas canvas, Paint mPaint) {
        int w = canvas.getWidth();
        int h = canvas.getHeight();

        if(bInValidRegion){
            mPaint.setColor(Color.GREEN);
        }else{
            mPaint.setColor(Color.RED);
        }

        mPath.reset();

        if(mcode.equalsIgnoreCase("引体向上") || mcode.equalsIgnoreCase("屈臂悬垂")){
            // 右手腕、左手腕不在单杠横杠附近区域
            mPath.moveTo(w*0.25f, h*0.95f); // bottomLeft
            mPath.lineTo(w*0.25f, h*0.15f); // - topLeft
            mPath.lineTo(w*0.75f, h*0.15f); // - topRight
            mPath.lineTo(w*0.75f, h*0.95f); // - bottomRight
        } else if(mcode.equalsIgnoreCase("双杠臂屈伸")){
            mPath.moveTo(w*0.45f, h*1.0f); // bottomLeft
            mPath.lineTo(w*0.45f, h*0.5f); // - topLeft
            mPath.lineTo(w*0.55f, h*0.5f); // - topRight
            mPath.lineTo(w*0.55f, h*1.0f); // - bottomRight
        } else if(mcode.equalsIgnoreCase("30米穿杆跑")){
            mPath.moveTo(w*0.45f, h*0.5f); // topLeft
            mPath.lineTo(w*0.15f, h*0.9f); // - bottomLeft
            mPath.lineTo(w*0.85f, h*0.9f); // - bottomRight
            mPath.lineTo(w*0.55f, h*0.5f); // - topRight
        } else if(mcode.equalsIgnoreCase(("杠上行走"))){
            mPath.moveTo(w*0.125f, h*0.4f);
            mPath.lineTo(w*0.875f, h*0.4f); // 横杠
            mPath.moveTo(w*0.208f, h*0.4f);
            mPath.lineTo(w*0.208f, h*0.9f); // 左立柱
            mPath.moveTo(w*0.792f, h*0.4f);
            mPath.lineTo(w*0.792f, h*0.9f); // 右立柱
        } else {
            mPath.moveTo(w*0.2f, h*0.1f); // topLeft
            mPath.lineTo(w*0.8f, h*0.1f); // - topRight
            mPath.lineTo(w*0.8f, h*0.9f); // - bottomRight
            mPath.lineTo(w*0.2f, h*0.9f); // - bottomLeft
            mPath.lineTo(w*0.2f, h*0.1f); // - topLeft
        }

        canvas.drawPath(mPath, mPaint);
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
