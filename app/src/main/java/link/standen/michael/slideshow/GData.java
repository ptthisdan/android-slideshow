package link.standen.michael.slideshow;

import android.app.Application;
import android.content.Context;
import android.util.Base64;

import com.baidu.ar.d.c;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class GData extends Application {
    private Boolean authed;
    private Boolean pose_inited;
    private static final int MAX_ENCRYPT_BLOCK=117;
    private static KeyPair keyPair;

    public boolean getAuthed(){
        return this.authed;
    }
    public void setAuthed(){
        this.authed= true;
    }
    public boolean isPoseInited(){
        return pose_inited;
    }
    public void setPoseInited(){
        pose_inited = true;
    }
    public static final String publickeystr="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDQOGYzCAZJDjojFl31bISbqIQX\n" +
            "idoqxaHbq6I/WBhxl4NxFZswAtLczBIFMEmi1u2eINsSXfwX26FT//MmuN6LhF8H\n" +
            "oQEka7rTodyKVOoboLahrWd44Jxwc6qWPXokHEb4zJWpo7lH+SIyeQDvv5fwOLH7\n" +
            "cJKr7P3/v5Npj7024QIDAQAB";
    public static final String prvlickeystr="MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANA4ZjMIBkkOOiMW\n" +
            "XfVshJuohBeJ2irFoduroj9YGHGXg3EVmzAC0tzMEgUwSaLW7Z4g2xJd/BfboVP/\n" +
            "8ya43ouEXwehASRrutOh3IpU6hugtqGtZ3jgnHBzqpY9eiQcRvjMlamjuUf5IjJ5\n" +
            "AO+/l/A4sftwkqvs/f+/k2mPvTbhAgMBAAECgYBCR0GYytwdUw8EbjE4VvObDLgH\n" +
            "OhUc+k28O9TobuVQ+xaW33bHgI6YQ8kUp7s4v0thwzmYjhwZiArBTz6Zu8zybv4I\n" +
            "+tExO3f0Xfjb74sdg9p8PUXZ2JpFr0ujwCLBnmgRUzus1dbeOkuyCVZnphIE1U1l\n" +
            "4Oh+DWJ7UnI0EmEwAQJBAO7Wd2HO0L1DDKYmztFB+kr6uo/QPbFA+8IpPb1jQO1G\n" +
            "gYjuMhzPv0CSGuHp+Q6WiGz45JjCM+hZC2YRMyMXyoECQQDfLrXch60MWBgAfjqL\n" +
            "x64Xesm6UuagnIEt0245WV3DfkdW7tm+T3MiDSYk3x2DZUk5GQ0+J+FyI2GUubYO\n" +
            "I3xhAkA2spU6qCO0BIQEFUUuNV7+Bvfs8bL/QjOmHs3N7soyzP/jfTGq2YUiY5OW\n" +
            "g4APcDgkRMgNmARNRP9QS8YbVlqBAkEAorOpzvB2HlamgM2FHtveNa3FHHlNOm99\n" +
            "2QuXAmX87gTEO0L7KDtBYuSVFzI0kNKpE+AIgHSQbSYQAF12tE34YQJAFU+XYPkK\n" +
            "TBZe5Rjq043PxqMls25/km4xBB7puCP/F6moI3ytV8BJybedTfPC2tm8vbKrbOt3\n" +
            "M4WgrzeJH027Kw==";
    public static final String lic="{\"authType\":6,\"ignoreNetError\":\"1\",\"noAuthTip\":\"1\",\"pkg\":\"65eeab24d3e669c51dcf2e4b4410ee29\",\"appId\":\"20245\",\"apiKey\":\"33094157a8a0f9bf10a28513cb7254f2\",\"features\":[3000],\"_t\":1603840839,\"pkgs\":[\"65eeab24d3e669c51dcf2e4b4410ee29\"]}";

    public static byte[] encode(byte[] data){
        ByteArrayOutputStream var6 = new ByteArrayOutputStream();
        try {
            // byte[] key = Base64.decode(publickeystr.getBytes(), 0);
            // PrivateKey key = keyPair.getPrivate();
            //byte[] k = keyPair.getPrivate().getEncoded();
            //String prvk = Base64.encodeToString(k,0);
            byte[] decoded = Base64.decode(prvlickeystr, 0);
            RSAPrivateKey key = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
            KeyFactory var3 = KeyFactory.getInstance("RSA");
            Cipher var5 = Cipher.getInstance("RSA/None/PKCS1Padding");
            var5.init(1, key);

            int var7 = data.length;
            int var8 = 0;

            while (var7 > var8) {
                int var10 = var7 - var8;
                if (var10 > 117) {
                    var10 = 117;
                }

                byte[] var9 = var5.doFinal(data, var8, var10);
                var8 += var10;
                var6.write(var9);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return var6.toByteArray();
    }

    public static byte[] loadLicense(Context var0) {
        InputStream var1 = null;
        ByteArrayOutputStream var2 = null;

        Object var4;
        try {
            var1 = var0.getAssets().open("dumixar.license");
            var2 = new ByteArrayOutputStream();
            byte[] var3 = new byte[512];
            boolean var11 = false;

            int var12;
            while((var12 = var1.read(var3)) != -1) {
                var2.write(var3, 0, var12);
            }

            byte[] var5 = var2.toByteArray();
            return var5;
        } catch (IOException var9) {
            var4 = null;
        } finally {
            c.a(var2);
            c.a(var1);
        }

        return (byte[])var4;
    }

    public static byte[] decode(byte[] var0, byte[] var1) {
        ByteArrayOutputStream var6 = new ByteArrayOutputStream();
        try {
            // byte[] key = Base64.decode(var0, 0);
            // byte[] k =keyPair.getPublic().getEncoded();
            //String pubk = Base64.encodeToString(k,0);
            byte[] key = Base64.decode(publickeystr, 0);
            X509EncodedKeySpec var2 = new X509EncodedKeySpec(key);
            KeyFactory var3 = KeyFactory.getInstance("RSA");
            PublicKey var4 = var3.generatePublic(var2);
            Cipher var5 = Cipher.getInstance("RSA/None/PKCS1Padding");
            var5.init(2, var4);

            int var7 = var1.length;
            int var8 = 0;

            while (var7 > var8) {
                int var10 = var7 - var8;
                if (var10 > 128) {
                    var10 = 128;
                }

                byte[] var9 = var5.doFinal(var1, var8, var10);
                var8 += var10;
                var6.write(var9);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return var6.toByteArray();
    }
    public static byte[] encrypt(byte[] var0) {
        try {
            byte[] var2 = encode( var0);
            return var2;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void parse(byte[] var0) {
        try {

            byte[] var2 = decode("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDn6dzi813ZXXfMIeXrxJxtVekfpxksX9N5XPh9g4D94cOvZnYL93PngexbPfGW9T7DhGnPdgRxR6Ux1pGRdTfrL9yK8nR7uCa5Va9IXbNd4T5QPpbmJ5hvmk7qg8GY8BxcC/0M+a5ylVP8bUDq50Y9Si+7g844wOCbrOkzSe920wIDAQAB".getBytes(), var0);
            String jsonstr = new String(var2);
            JSONObject var3 = new JSONObject(jsonstr);

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public void onCreate(){
       /*
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            final int KEY_SIZE = 1024;// 没什么好说的了，这个值关系到块加密的大小，可以更改，但是不要太大，否则效率会低
            keyPairGen.initialize(KEY_SIZE, new SecureRandom());
            keyPair = keyPairGen.generateKeyPair();

        }catch (Exception e){
            e.printStackTrace();
        }
*/
        authed = false;
        pose_inited = false;
        super.onCreate();


    }
}
