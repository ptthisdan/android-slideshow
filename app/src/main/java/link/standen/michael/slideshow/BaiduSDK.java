package link.standen.michael.slideshow;

import android.content.res.AssetManager;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class BaiduSDK {
    private class SDKInfo {
        public String so_name;
        public String modelPath;
        public String description;

        public SDKInfo(String so, String model, String desc) {
            so_name = so;
            modelPath = model;
            description = desc;
        }
    }
    private HashMap<String,SDKInfo> sdks;
    private HashMap<String,Integer> modes;
    private String used;

    public BaiduSDK(){
        sdks = new HashMap<String,SDKInfo>();
        SDKInfo sdk = new SDKInfo("BARhumanpose3d-landscape","mdlModels/20201203","横向YUV接口");
        sdks.put("land-yuv",sdk);
        sdk = new SDKInfo("BARhumanpose3d-por-901","mdlModels/20201231","纵向YUV接口");
        sdks.put("por-yuv",sdk);
        sdk = new SDKInfo("BARhumanpose3d-por-rgba","mdlModels/20201231","纵向RGBA接口");
        sdks.put("por-rgba",sdk);
        // used = "land-yuv";

        sdk = new SDKInfo("BARhumanpose3d","mdlModels/20210319","通用YUV接口");
        sdks.put("comm-yuv",sdk);
        sdk = new SDKInfo("BARhumanpose3d-0322","mdlModels/20210322","通用YUV接口");
        sdks.put("comm-yuv-0322",sdk);

        sdk = new SDKInfo("BARhumanpose3d-0331","mdlModels/20210331","通用YUV接口");
        sdks.put("comm-yuv-0331",sdk);

        used = "comm-yuv";

        modes = new HashMap<String,Integer>();
        // 0: 双杠
        // 1: 引体向上
        // 2: 俯卧撑 头左 20 头右21
        // 3: 臀桥 头左 30 头右 31
        // 4: 仰卧起坐 头左 40 头右 41
        modes.put("双杠臂屈伸",0);
        modes.put("引体向上",1);
        modes.put("屈臂悬垂",1);
        modes.put("俯卧撑",21);
        modes.put("俯卧撑左",20);
        modes.put("臂桥左",30);
        modes.put("臂桥右",31);
        modes.put("仰卧起坐",41);
        modes.put("仰卧起坐左",40);
    }

    public void UseSDK(String flavor){
        used = flavor;
        SDKInfo info = sdks.get(flavor);
        ARMdlHumanPoseJNI.initJNI(info.so_name);

    }
    public String getUsedSO(){
        SDKInfo info = sdks.get(used);
        return info.so_name;
    }
    public int getMode(String name){
        return modes.get(name);
    }
    public String getUsedModel(){
        SDKInfo info = sdks.get(used);
        return info.modelPath;
    }

    public void InitModels(AssetManager asset, int mode){
        SDKInfo info = sdks.get(used);
        ARMdlHumanPoseJNI.setAssetManager(asset);
        ARMdlHumanPoseJNI.initPoseFromAsset(info.modelPath,info.modelPath,info.modelPath,1,mode);
    }

    public int PredictPose(ByteBuffer data, int width, int height, int angle,
                           boolean isFront, float[] bodyKeyPoints){
        return ARMdlHumanPoseJNI.predictPose(data,width,height,angle,isFront,bodyKeyPoints);
    }
}
