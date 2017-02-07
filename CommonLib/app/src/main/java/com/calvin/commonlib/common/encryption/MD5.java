package com.calvin.commonlib.common.encryption;


import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * MD5(全部为小写)
 */
public class MD5 {
    private static final String TAG                  = MD5.class.getSimpleName();
    private static final String CHARSET              = "UTF-8";

    public static MessageDigest getMD5Digest() throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("MD5");
    }

    /**
     * MD5加密 16位
     * @param s 需要加密的字符串
     * @param charset 字符集
     * @return
     */
    public final static String MD5_16bit(String s, String charset) {
        if(charset == null || "".equals(charset)) charset=CHARSET;
        try {
            byte[] btInput = s.getBytes(charset);
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            return convert_16bit(md);
        } catch (Exception e) {
            return null;
        }
    }

    /***
     * MD5加密 32位
     * @param s 需要加密的字符串
     * @param charset 字符集
     * @return
     */
    public final static String MD5_32bit(String s, String charset) {
        if(charset == null || "".equals(charset)) charset=CHARSET;
        try {
            byte[] btInput = s.getBytes(charset);
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            return convert_32bit(md);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 验证文件是否与相应的MD5码匹配
     * @param md5
     * @param updateFile
     * @return
     */
    public static boolean checkMD5(String md5, File updateFile) {
        if (TextUtils.isEmpty(md5) || updateFile == null) {
            Log.e(TAG, "MD5 string empty or updateFile null");
            return false;
        }

        String calculatedDigest = calculateMD5(updateFile);
        if (calculatedDigest == null) {
            Log.e(TAG, "calculatedDigest null");
            return false;
        }

        Log.v(TAG, "Calculated digest: " + calculatedDigest);
        Log.v(TAG, "Provided digest: " + md5);

        return calculatedDigest.equalsIgnoreCase(md5);
    }

    /**
     * 根据文件生成对应的MD5码（32位小写）
     * @param updateFile
     * @return
     */
    public static String calculateMD5(File updateFile) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Exception while getting digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(updateFile);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Exception while getting FileInputStream", e);
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "Exception on closing MD5 input stream", e);
            }
        }
    }

    /**
     * 将md5过后的bytes转成String
     * @param md5bytes
     * @return  32位
     */
    private static String convert_32bit(byte[] md5bytes){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < md5bytes.length; i++) {
            int val = ((int) md5bytes[i]) & 0xff;
            if (val < 16){
                sb.append("0");
            }
            sb.append(Integer.toHexString(val));
        }
        return sb.toString();
    }

    /**
     * 将md5过后的bytes转成String
     * @param md5bytes
     * @return  16位
     */
    private static String convert_16bit(byte[] md5bytes){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < md5bytes.length; i++) {
            int val = ((int) md5bytes[i]) & 0xff;
            if (val < 16){
                sb.append("0");
            }
            sb.append(Integer.toHexString(val));
        }
        return sb.toString().substring(8,24);
    }
	
	public static String sha1(byte[] data)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.reset();
            md.update(data);
            return byteToHex(md.digest());
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return null;
    }


    public static String sha1(String data)
    {
        try
        {
            return sha1(data.getBytes("UTF-8"));
        }
        catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return null;
    }


    private static String byteToHex(final byte[] hash)
    {
        Formatter formatter = new Formatter();
        for(byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String s = "待转换字符串";
        String encodeStr = MD5_16bit(s,null);
        String decodeStr = MD5_32bit(s,null);
        System.out.println("转换前：" + s);
        System.out.println("转换后：" + encodeStr);
        System.out.println("还原后：" + decodeStr+"...."+decodeStr.length());
    }
}
