package link.standen.michael.slideshow.util;

public class ImageUtilJni {
    static {
        System.loadLibrary("imageutil");
    }

    public static native int getVersion();

    /**
     * yuv转bgr流
     *
     * @param yuvBuf         原始yuv数据
     * @param yuv_height     原始yuv宽
     * @param yuv_width      原始yuv高
     * @param resized_height 目标数据宽
     * @param resized_width  目标数据宽
     * @param angle          旋转角度 -90，0，90，180
     * @param is_flip        是否镜像 0不镜像，1镜像
     * @param bgrBuf         结果bgr数据
     *
     * @return
     */
    public static native int yuv2bgr(byte[] yuvBuf,
                                     int yuv_height,
                                     int yuv_width,
                                     int resized_height,
                                     int resized_width,
                                     int angle,
                                     int is_flip,
                                     byte[] bgrBuf);
}
