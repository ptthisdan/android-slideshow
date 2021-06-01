//package link.standen.michael.slideshow;
package com.baidu.arpose;

import android.content.res.AssetManager;

import java.nio.ByteBuffer;

public class ARMdlHumanPoseJNI {

    static {
        System.loadLibrary("BARhumanpose3d");
    }

    public static void initJNI(String name){
        System.loadLibrary(name);
    }

    // 设置AssetManager
    public static native int setAssetManager(AssetManager assetManager);

    /** 从SD卡初始化
     * @param detectDir 检测模型路径
     * @param clsDir 分类模型路径
     * @param poseDir 肢体点模型路径
     * @param filter_type 滤波等级,取值范围[1,2,3],取值越高,滤波程度越高,其他值不滤波
     **/
    public static native int initPose(String detectDir, String clsDir, String poseDir, int filter_type);

    /** 从asstes初始化
     * @param detectDir 检测模型路径
     * @param clsDir 分类模型路径
     * @param poseDir 肢体点模型路径
     * @param filter_type 滤波等级,取值范围[1,2,3],取值越高,滤波程度越高,其他值不滤波
     *     // pose_mode:
     *     // 0: 双杠
     *     // 1: 引体向上
     *     // 2: 俯卧撑 头左 20 头右21
     *     // 3: 臀桥 头左 30 头右 31
     *     // 4: 仰卧起坐 头左 40 头右 41
     *     int pose_mode = 41;
     **/
    public static native int initPoseFromAsset(String detectDir, String clsDir, String poseDir, int filter_type,int pose_mode);

    /** 肢体点预测
     * @param data 摄像头bgr数据
     * @param width 图像宽
     * @param height 图像高
     * @param angle 图像顺时针旋转为预览图所需的角度， 取值范围[0,90,180,270]
     * @param isFront 是否前置
     * @param bodyKeyPoints 肢体点返回数组，大小为16 * 2, 格式为x0,y0,x1,...
     * @return int 返回值为人数，取值[0,1]
     **/
    public static native int predictPose(ByteBuffer data, int width, int height, int angle,
                                         boolean isFront, float[] bodyKeyPoints);
    // SDK模型资源释放
    public static native int releasePose();

}
