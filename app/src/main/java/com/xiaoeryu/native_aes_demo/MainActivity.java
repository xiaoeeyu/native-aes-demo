package com.xiaoeryu.native_aes_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import com.xiaoeryu.native_aes_demo.databinding.ActivityMainBinding;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native_aes_demo' library on application startup.
    static {
        System.loadLibrary("native_aes_demo");
    }

    private ActivityMainBinding binding;

    // 加密
    public static String Encrypt(String sSrc, String sKey) throws Exception {
        if (sKey == null) {
            System.out.print("Key为空null");
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
            System.out.print("Key长度不是16位");
            return null;
        }
        byte[] raw = sKey.getBytes("utf-8");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/填充方式"
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));

        String result = null;
        result = new String(Base64.encode(encrypted, Base64.DEFAULT));
        return result;//使用BASE64做转码功能
    }

    // 解密
    public static String Decrypt(String sSrc, String sKey) throws Exception {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                System.out.print("Key为空null");
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                System.out.print("Key长度不是16位");
                return null;
            }
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = Base64.decode(sSrc.getBytes("UTF-8"), Base64.DEFAULT);
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original, "utf-8");
                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }

    public static void testJavaAES() throws Exception {
        /*
         * 此处使用AES-128-ECB加密模式，key需要为16位。
         */
        String cKey = "0123456789abcdef";
        // 需要加密的字串
        String cSrc = "testaesecb";

        // 加密
        String enString = Encrypt(cSrc, cKey);
        Log.e("aes", cSrc + "--java加密后的字串是：" + enString);
        // 解密
        String DeString = Decrypt(enString, cKey);
        Log.e("aes", enString + "--java解密后的字串是：" + DeString);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Example of a call to a native method
        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    testJavaAES();
                    String cSrc = "testaesecb";
                    Log.i("AES", cSrc + "---" + encrypt(cSrc));
                    String decrypt = "zS4ZzC2/8w9TfAVU7HQubg==";
                    Log.i("AES", decrypt + "---" + decrypt(decrypt));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * A native method that is implemented by the 'native_aes_demo' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    public static native String encrypt(String content);

    public static native String decrypt(String content);
}