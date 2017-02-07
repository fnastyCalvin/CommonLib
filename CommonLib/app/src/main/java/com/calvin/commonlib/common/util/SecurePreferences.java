package com.calvin.commonlib.common.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.calvin.commonlib.App;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

//AES+SHA+Base64
//由于加密解密，存储key-value类型必须是String-String
public class SecurePreferences {

    private static final String PRE_NAME = "Pref";
    private static final String SECURE_KEY = "4ue%nsp@4#nm5";

    private static SecurePreferences instance;

    public static class SecurePreferencesException extends RuntimeException {
        public SecurePreferencesException(Throwable e) {
            super(e);
        }
    }

    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";//用于构造密钥
    private static final String KEY_TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String SECRET_KEY_HASH_TRANSFORMATION = "SHA-256";
    private static final String CHARSET = "UTF-8";

    private boolean encryptKeys;
    private Cipher writer;
    private Cipher reader;
    private Cipher keyWriter;
    private SharedPreferences preferences;

    private SecurePreferences(){}

    public static SecurePreferences getInstance(){
        if (instance == null) {
            synchronized (SecurePreferences.class) {
                if (instance == null) {
                    instance = new SecurePreferences(App.getAppContext());
                }
            }
        }
        return instance;
    }

    //chenchaoyuan:默认方式
    private SecurePreferences(Context context) throws SecurePreferencesException {
        try {
            this.writer = Cipher.getInstance(TRANSFORMATION);
            this.reader = Cipher.getInstance(TRANSFORMATION);
            this.keyWriter = Cipher.getInstance(KEY_TRANSFORMATION);
            initCiphers(SECURE_KEY);
            if(null == context){
                context = App.getAppContext();
            }
            this.preferences = context.getSharedPreferences(PRE_NAME, Context.MODE_PRIVATE);
            this.encryptKeys = true;
        } catch (GeneralSecurityException e) {
            throw new SecurePreferencesException(e);
        } catch (UnsupportedEncodingException e) {
            throw new SecurePreferencesException(e);
        } catch (NullPointerException e) {
            // ignore
            throw new SecurePreferencesException(e);
        }
    }

    /**
     * This will initialize an instance of the SecurePreferences class
     *
     * @param context        your current context.
     * @param preferenceName name of preferences file (preferenceName.xml)
     * @param secureKey      the key used for encryption, finding a good key scheme is hard.
     *                       Hardcoding your key in the application is bad, but better than plaintext preferences. Having the user enter the key upon application launch is a safe(r) alternative, but annoying to the user.
     * @param encryptKeys    settings this to false will only encrypt the values,
     *                       true will encrypt both values and keys. Keys can contain a lot of information about
     *                       the plaintext value of the value which can be used to decipher the value.
     * @throws SecurePreferencesException
     */
    private SecurePreferences(Context context, String preferenceName, String secureKey, boolean encryptKeys) throws SecurePreferencesException {
        try {
            this.writer = Cipher.getInstance(TRANSFORMATION);
            this.reader = Cipher.getInstance(TRANSFORMATION);
            this.keyWriter = Cipher.getInstance(KEY_TRANSFORMATION);

            initCiphers(secureKey);

            this.preferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
            this.encryptKeys = encryptKeys;
        } catch (GeneralSecurityException e) {
            throw new SecurePreferencesException(e);
        } catch (UnsupportedEncodingException e) {
            throw new SecurePreferencesException(e);
        }
    }

    protected void initCiphers(String secureKey) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidAlgorithmParameterException {
        IvParameterSpec ivSpec = getIv();
        SecretKeySpec secretKey = getSecretKey(secureKey);

        writer.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);//模式为加密
        reader.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);//模式为解密
        keyWriter.init(Cipher.ENCRYPT_MODE, secretKey);//模式为加密
    }

    // 算法参数
    protected IvParameterSpec getIv() {
        byte[] iv = new byte[writer.getBlockSize()];
        System.arraycopy("fldsjfodasjifudslfjdsaofshaufihadsf".getBytes(), 0, iv, 0, writer.getBlockSize());
        return new IvParameterSpec(iv);
    }

    // 根据给定的字节数组构造一个密钥。
    protected SecretKeySpec getSecretKey(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] keyBytes = createKeyBytes(key);
        return new SecretKeySpec(keyBytes, TRANSFORMATION);
    }

    // SHA加密key
    protected byte[] createKeyBytes(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(SECRET_KEY_HASH_TRANSFORMATION);
        md.reset();
        byte[] keyBytes = md.digest(key.getBytes(CHARSET));
        return keyBytes;
    }

    public void put(String key, boolean value) {
        put(key, String.valueOf(value));
    }

    public void put(String key, String value) {
        if (value == null) {
            removeValue(key);
        } else {// String 数据保存
            putValue(toKey(key), value);
        }
    }

    public boolean containsKey(String key) {
        return preferences.contains(toKey(key));
    }

    // delete key and value
    public void removeValue(String key) {
        preferences.edit().remove(toKey(key)).apply();
    }

    public String getString(String key) throws SecurePreferencesException {
        if (preferences.contains(toKey(key))) {
            String securedEncodedValue = preferences.getString(toKey(key), "");
            return decrypt(securedEncodedValue);
        }
        return "";
    }

    //[S]----ADD BY CHENCY

    public boolean getBoolean(String key) throws SecurePreferencesException {
        if (preferences.contains(toKey(key))) {
            String securedEncodedValue = preferences.getString(toKey(key), "");
            String result = decrypt(securedEncodedValue);
            return Boolean.parseBoolean(result);
        }
        return false;
    }

    // 仅在未加密时，获取所有key，用于置换后清空字段
    public void replaceAll() throws SecurePreferencesException {
        if(encryptKeys){
            List<String> keys = new ArrayList<String>();
            Map<String, ?> maps = preferences.getAll();
            for(Map.Entry<String,?> entry : maps.entrySet()){
                // 将明文另存为密文
                put(entry.getKey(), entry.getValue().toString());
                keys.add(entry.getKey());
            }
            removeOldValue(keys);
        }
    }

    // 未加密前的key判断
    public boolean containsOldKey(String key) {
        return preferences.contains(key);
    }

    private void removeOldValue(List<String> keys) {
        SharedPreferences.Editor editor = preferences.edit();
        for(String key : keys){
            editor.remove(key);
        }
        editor.apply();
    }

    //[E]----ADD BY CHENCY

    public void clear() {
        preferences.edit().clear().apply();
    }

    // key加密
    private String toKey(String key) {
        if (encryptKeys)
            return encrypt(key, keyWriter);
        else return key;
    }

    //加密保存（key上层已处理）
    private void putValue(String key, String value) throws SecurePreferencesException {
        String secureValueEncoded = encrypt(value, writer);
        preferences.edit().putString(key, secureValueEncoded).apply();
    }

    // 将Cipher的value执行加密解密结果
    protected String encrypt(String value, Cipher writer) throws SecurePreferencesException {
        byte[] secureValue;
        try {
            secureValue = convert(writer, value.getBytes(CHARSET));
        } catch (UnsupportedEncodingException e) {
            throw new SecurePreferencesException(e);
        }
        String secureValueEncoded = Base64.encodeToString(secureValue, Base64.NO_WRAP);
        return secureValueEncoded;
    }

    // 解密结果
    protected String decrypt(String securedEncodedValue) {
        // Base64.NO_WRAP ： 当字符串过长（一般超过76）时不自动在中间添加换行符
        byte[] securedValue = Base64.decode(securedEncodedValue, Base64.NO_WRAP);
        byte[] value = convert(reader, securedValue);
        if(value ==  null || value.length<=0) return "";
        try {
            return new String(value, CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new SecurePreferencesException(e);
        }
    }

    // 执行加密解密
    private static byte[] convert(Cipher cipher, byte[] bs) throws SecurePreferencesException {
        try {
            return cipher.doFinal(bs);
        } catch (Exception e) {
            throw new SecurePreferencesException(e);
        }
    }
}