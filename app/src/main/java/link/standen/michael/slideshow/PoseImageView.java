package link.standen.michael.slideshow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.github.psambit9791.jdsp.signal.Smooth;
import com.github.psambit9791.jdsp.signal.peaks.FindPeak;
import com.github.psambit9791.jdsp.signal.peaks.Peak;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PoseImageView extends android.support.v7.widget.AppCompatImageView {
    private static final String TAG = PoseImageView.class.getName();
    private HashMap<String, ArrayList<Double>> result = new HashMap<String, ArrayList<Double>>();

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

    public PoseImageView(Context context) {
        super(context);
        generateResultHashMap(colNames);
    }
    public PoseImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        generateResultHashMap(colNames);
    }

    public PoseImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        generateResultHashMap(colNames);
    }

    String[] colNames = {
            "右脚踝-x","右脚踝-y",   //        {"name":"右脚踝", "pos":0},
            "右膝-x","右膝-y",      //        {"name":"右膝", "pos":1},
            "右股-x","右股-y",      //        {"name":"右股", "pos":2},
            "左股-x","左股-y",      //        { "name":"左股","pos":3},
            "左膝-x","左膝-y",      //        { "name":"左膝","pos":4},
            "左脚踝-x","左脚踝-y",    //        { "name":"左脚踝","pos":5},
            //"盆骨-x","盆骨-y",      //        { "name":"盆骨","pos":3},
            "胸部-x","胸部-y",      //        { "name":"胸部","pos":6},
            "脖子-x","脖子-y",      //        { "name":"脖子","pos":7},
            "下巴-x","下巴-y",      //        { "name":"下巴","pos":8},
            "鼻子-x","鼻子-y",      //        { "name":"鼻子","pos":9},
            "头部-x","头部-y",      //        { "name":"头部","pos":10},
            "右手腕-x","右手腕-y",    //        { "name":"右手腕","pos":11},
            "右手肘-x","右手肘-y",    //        { "name":"右手肘","pos":12},
            "右肩-x","右肩-y",      //        { "name":"右肩","pos":13},
            "左肩-x","左肩-y",      //        { "name":"左肩","pos":14},
            "左手肘-x","左手肘-y",    //        { "name":"左手肘","pos":15},
            "左手腕-x","左手腕-y",    //        { "name":"左手腕","pos":16}
    };

    public String getMcode() {
        return mcode;
    }

    public void setMcode(String mcode) {
        this.mcode = mcode;

        if(Objects.equals(mcode, "仰卧起坐")){
            primaryJoint = "左肩-y";
            primaryPeakThresholdJoint = "左股-y";
            primaryTroughThresholdJoint = "左膝-y";
            primayryDetectMethod = DetectMethod.PEAK_AND_TROUGH;
            secondaryJoint = "左手肘-x";
            secondaryTroughThresholdJoint = "左股-x";
            primayryDetectMethod = DetectMethod.TROUGH;
        }else if(Objects.equals(mcode, "引体向上")){
            primaryJoint = "下巴-y";
            primaryPeakThresholdJoint = "右手肘-y";
//            primaryTroughThresholdJoint = "右手腕-y";
            primaryTroughThreshold = 0.2;
            primayryDetectMethod = DetectMethod.TROUGH;
            secondaryJoint = "左脚踝-y";
            secondaryTroughThreshold = 0.75;
            secondaryDetectMethod = DetectMethod.TROUGH;
        }else if(Objects.equals(mcode, "双杠臂屈伸")){
            primaryJoint = "胸部-y";
            primaryPeakThresholdJoint = "右手腕-y";
            primaryTroughThresholdJoint = "右手肘-y";
//            primaryTroughThreshold = 0.2;
            primayryDetectMethod = DetectMethod.PEAK_AND_TROUGH;
            secondaryJoint = "右膝-y";
            secondaryTroughThreshold = 0.55;
            secondaryDetectMethod = DetectMethod.PEAK;
        }else if(Objects.equals(mcode, "俯卧撑")){
            primaryJoint = "右肩-y";
            primaryPeakThresholdJoint = "右脚踝-y";
            primaryPeakThresholdShift = -0.11;
            primaryTroughThresholdJoint = "右脚踝-y";
            primaryTroughThresholdShift = -0.15;
//            primaryTroughThreshold = 0.2;
            primayryDetectMethod = DetectMethod.PEAK_AND_TROUGH;
            secondaryJoint = "右股-y";
            secondaryPeakThresholdJoint = "右脚踝-y";
            secondaryPeakThresholdShift = -0.07;
            secondaryTroughThresholdJoint = "右脚踝-y";
            secondaryTroughThresholdShift = -0.09;
            secondaryTroughProminence = 0.0;
            secondaryDetectMethod = DetectMethod.PEAK_AND_TROUGH;
        }
    }

    String mcode = "引体向上";
    enum DetectMethod{
        IGNORE,
        PEAK,
        TROUGH,
        PEAK_AND_TROUGH
    }

    private int totalCount = 0;
    private int invalidCount = 0;
    private int lastCount = 0;

    private int lastPrimaryPeak = 0;
    private int lastPrimaryTrough = 0;
    private int lastFound = 0;

    private String primaryJoint = "";
    private String primaryPeakThresholdJoint = "";
    private String primaryTroughThresholdJoint = "";
    private Double primaryPeakThreshold = 0.0;
    private Double primaryPeakThresholdShift = 0.0;
    private Double primaryPeakProminence = 0.01;
    private Double primaryTroughThreshold = 0.0;
    private Double primaryTroughThresholdShift = 0.0;
    private Double primaryTroughProminence = 0.01;
    private DetectMethod primayryDetectMethod = DetectMethod.PEAK;

    private String secondaryJoint = "";
    private String secondaryPeakThresholdJoint = "";
    private String secondaryTroughThresholdJoint = "";
    private Double secondaryPeakThreshold = 0.0;
    private Double secondaryPeakThresholdShift = 0.0;
    private Double secondaryPeakProminence = 0.01;
    private Double secondaryTroughThreshold = 0.0;
    private Double secondaryTroughThresholdShift = 0.0;
    private Double secondaryTroughProminence = 0.01;
    private DetectMethod secondaryDetectMethod = DetectMethod.IGNORE;

    private String errJoint = "";


    public void generateResultHashMap(String[] colNames) {
        for (int i=0; i<colNames.length; i++) {
            this.result.put(colNames[i], new ArrayList<Double>());
        }
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
                        bgr[pixel_now] = 64;
                        bgr[pixel_now+1]=64;
                        bgr[pixel_now+2]=64;
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
            Log.d(TAG, num + "people detected!");
            drawPose(canvas, mPaint);

            Log.d(TAG, "Add kpnts!");
            for (int i = 0; i < colNames.length; i++) {
                result.get(colNames[i]).add(((double) (kpnts[i])));
            }

            if (result.get(colNames[0]).size() - lastFound >= 30 ) {

                Log.d(TAG, "Generate signal");

                ArrayList<Double> inputPrimary = new ArrayList<>(result.get(primaryJoint).subList(lastFound, result.get(primaryJoint).size()));
                double[] primarySignal = new double[inputPrimary.size()];
//                double[] primarySignal = new double[result.get(primaryJoint).size()];

                for (int i = 0; i < primarySignal.length; i++) {
//                    primarySignal[i] = result.get(primaryJoint).get(i);
                    primarySignal[i] = inputPrimary.get(i);
                }
                Log.d(TAG, "primarySignal: " + Arrays.toString(primarySignal));

                if(!primaryPeakThresholdJoint.equals("")){
                    primaryPeakThreshold = result.get(primaryPeakThresholdJoint).subList(lastFound, result.get(primaryJoint).size()).stream().mapToDouble(a->a).average().orElse(0.0);
                    primaryPeakThreshold += primaryPeakThresholdShift;
                }

                if(!primaryTroughThresholdJoint.equals("")){
                    primaryTroughThreshold = result.get(primaryTroughThresholdJoint).subList(lastFound, result.get(primaryJoint).size()).stream().mapToDouble(a->a).average().orElse(0.0);
                    primaryTroughThreshold += primaryTroughThresholdShift;
                }


                Log.d(TAG, "smooth signal & detect peaks: peakThreshold=" + primaryPeakThreshold + " throughThreshold=" + primaryTroughThreshold);
                try {
                    Log.d(TAG, "Count primaryJoint"+primaryJoint);
                    ArrayList<Integer> outFilteredPrimary = countSignal(primarySignal, primaryPeakThreshold, primaryPeakProminence, primaryTroughThreshold, primaryTroughProminence, primayryDetectMethod);
                    int countPrimary = outFilteredPrimary.size();


                    if (countPrimary > 0) {
                        errJoint = "";

                        // Slice aligned with primary Joint
                        ArrayList<Double> inputSecondary = new ArrayList<>(result.get(secondaryJoint).subList(lastFound, result.get(primaryJoint).size()));
                        double[] secondarySignal = new double[inputSecondary.size()];

                        for (int i = 0; i < secondarySignal.length; i++) {
                            secondarySignal[i] = inputSecondary.get(i);
                        }

                        if (!secondaryPeakThresholdJoint.equals("")) {
                            secondaryPeakThreshold = result.get(secondaryPeakThresholdJoint).subList(lastFound, result.get(primaryJoint).size()).stream().mapToDouble(a -> a).average().orElse(0.0);
                            secondaryPeakThreshold += secondaryPeakThresholdShift;
                        }

                        if (!secondaryTroughThresholdJoint.equals("")) {
                            secondaryTroughThreshold = result.get(secondaryTroughThresholdJoint).subList(lastFound, result.get(primaryJoint).size()).stream().mapToDouble(a -> a).average().orElse(0.0);
                            secondaryTroughThreshold += secondaryTroughThresholdShift;
                        }

                        Log.d(TAG, "secondarySignal: " + Arrays.toString(secondarySignal));
                        Log.d(TAG, "smooth signal & detect peaks: peakThreshold=" + secondaryPeakThreshold + " throughThreshold=" + secondaryTroughThreshold);

                        lastFound += outFilteredPrimary.get(countPrimary - 1);
                        Log.e(TAG, " lastFound: " + lastFound + " foundValue:" + primarySignal[outFilteredPrimary.get(countPrimary - 1)]);

                        Log.d(TAG, "Count secondaryJoint: " + secondaryJoint + " lastFound: " + lastFound + "lastSignal: " + result.get(primaryJoint).size());


                        ArrayList<Integer> outFilteredSecondary = countSignal(secondarySignal, secondaryPeakThreshold, secondaryPeakProminence, secondaryTroughThreshold, secondaryTroughProminence, secondaryDetectMethod);
                        int countSecondary = outFilteredSecondary.size();

                        if (countPrimary != countSecondary) {
                            errJoint = errJoint + secondaryJoint;
                            countPrimary = 0;
                            Log.e(TAG, "Error secondaryJoint:" + secondaryJoint);
                        }

                    }

                    if (countPrimary > 0) {

                        totalCount += countPrimary;
                        lastCount = totalCount;
                    }

                    Log.e(TAG, "totalCount:" + totalCount);

                } catch (Exception e) {
                    Log.e(TAG, "detect peaks exception:" + e.getMessage());
                }
            }
        }

    }


    public ArrayList<Integer> commonElements(int[] a, int[] b){
        ArrayList<Integer> common = new ArrayList<Integer>();
        Set<Integer> set = new HashSet<Integer>();

        for(int ele:a){
            set.add(ele);
        }

        for(int ele:b){
            if(set.contains(ele)){
                common.add(ele);
            }
        }

        return common;
    }

    private ArrayList<Integer> countSignal(double[] Signal, double peakThreshold, double peakProminence, double troughThreshold, double troughProminence, DetectMethod detectMethod){
        Smooth s1 = new Smooth(Signal, 5, "triangular");
        FindPeak fp = new FindPeak(s1.smoothSignal());
        Peak out_peaks = fp.detectPeaks();
        int[] out_peaks_filtered1 = out_peaks.filterByHeight(peakThreshold, null);
        int[] out_peaks_filtered2 = out_peaks.filterByProminence(peakProminence, null);
        ArrayList<Integer> out_peaks_filtered = new ArrayList<Integer>();
        Log.d(TAG, " out_peaks.filterByHeight:" + Arrays.toString(out_peaks_filtered1) + "  out_peaks.filterByProminence:" + Arrays.toString(out_peaks_filtered2));
        if(out_peaks_filtered1.length > 0 && out_peaks_filtered2.length > 0) {
            out_peaks_filtered = commonElements(out_peaks_filtered1, out_peaks_filtered2);
            Log.d(TAG, " out_peaks_filtered:" + out_peaks_filtered.toString());
        }
        //int[] out_troughs_filtered = out_troughs.filterByHeight(0.0-troughThreshold, null); //TODO: Bullshit to be fixed

        double[] reverseSignal = new double[Signal.length];
        for (int i=0; i<reverseSignal.length; i++) {
            reverseSignal[i] = 0 - Signal[i];
        }
        Smooth s2 = new Smooth(reverseSignal, 5, "triangular");
        FindPeak fp2 = new FindPeak(s2.smoothSignal());
        Peak out_troughs = fp2.detectPeaks();
        int[] out_troughs_filtered1 = out_troughs.filterByHeight(0-troughThreshold, null);
        int[] out_troughs_filtered2 = out_troughs.filterByProminence(troughProminence, null);
        Log.d(TAG, " out_troughs.filterByHeight:" + Arrays.toString(out_troughs_filtered1) + "  out_troughs.filterByProminence:" + Arrays.toString(out_troughs_filtered2));
        ArrayList<Integer> out_troughs_filtered = new ArrayList<Integer>();
        if(out_troughs_filtered1.length > 0 && out_troughs_filtered2.length > 0) {
            out_troughs_filtered = commonElements(out_troughs_filtered1, out_troughs_filtered2);
            Log.d(TAG, " out_troughs_filtered:" + out_troughs_filtered.toString());
        }

        if(DetectMethod.TROUGH == detectMethod){
            return out_troughs_filtered;
        }else if(DetectMethod.PEAK == detectMethod){
            return out_peaks_filtered;
        }else {
            return out_peaks_filtered.size() < out_troughs_filtered.size() ? out_peaks_filtered : out_troughs_filtered;
        }
    }


    private void drawPose(Canvas canvas, Paint mpaint) {
        int w = canvas.getWidth();
        int h = canvas.getHeight();

        mPaint.setColor(Color.YELLOW);
        mPaint.setStrokeWidth(5);
        mPaint.setTextSize(200);

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

        if(!errJoint.equals("")) {
            canvas.drawText(errJoint, 200, 200, mPaint);
        }else {
            canvas.drawText(Integer.toString(totalCount), 200, 200, mPaint);
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
