package com.calvin.commonlib.common.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by calvin on 2014/10/20.
 */
public class CommonUtil {

    private static Boolean isExit = false;

    private CommonUtil() {
        throw new RuntimeException("do not construct");
    }

    public static void hideInputMethod(Context context, IBinder windowToken) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void hideInputMethod(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void showInputMethod(final Context context, final View v) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(v, 0);
            }
        }, 100);
    }

    public static boolean isShownKeyboard(@NonNull final Activity activity) {
        final boolean[] isShown = {false};
        final View decorView = activity.getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                decorView.getWindowVisibleDisplayFrame(r);
                int screenHeight = decorView.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

//                Log.d(TAG, "keypadHeight = " + keypadHeight);

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
//                    Log.d(TAG, "keyboard is opened = ");
                    isShown[0] =  true;
                } else {
                    // keyboard is closed
//                    Log.d(TAG, "keyboard is closed = ");
                    isShown[0] =  false;
                }
            }
        });
        return isShown[0];
    }

    public static String isNull(String str) {
        if (TextUtils.isEmpty(str)) {
            return "";
        } else {
            return str;
        }
    }

    /**
     * 判断是否快速点击
     * @param gap 两次点击的时间间隔
     */
    private static long lastClickTime;
    public static boolean isFastDoubleClick(long gap) {
        if(gap <= 0)
        {
            gap = 1000;
        }
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < gap) {
            return true;
        }
        lastClickTime = time;
        return false;
    }


    /**
     * 读取配置信息的值
     *
     * @param contex
     * @param key
     */
    public static String getValueFromProperties(Context contex, String key) {
        if (contex == null || TextUtils.isEmpty(key)) {
            throw new NullPointerException("params is null");
        }
        Properties props = new Properties();
        try {
            InputStream in = contex.getAssets().open("formConfig.properties");
            props.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return props.getProperty(key, "");
    }

    /**
     * 将光标移到到尾部
     * @param editText
     */
    public static void moveCursor2End(EditText editText)
    {
        if(editText.hasFocus())
        {
            Editable text = editText.getText();
            int position = text.length();
            Selection.setSelection(text, position);
        }
    }

    /**
     * 将服务器端yyyyMMdd格式的转出为想要的格式
     * @param oldDate
     * @param format 默认yyyy-MM-dd
     * @return
     */
    public static String changeDateFormat(String oldDate, String format)
    {
        SimpleDateFormat odlSDF = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf = null;
        if(TextUtils.isEmpty(format))
        {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        }
        else
        {
            sdf = new SimpleDateFormat(format);
        }
        String dateStr = null;
        try {
            Date date  = odlSDF.parse(CommonUtil.isNull(oldDate));
            dateStr = sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        return dateStr;
    }

    /**
     * 将服务器端yyyy.MM.dd格式的转出为想要的格式
     * @param oldDate
     * @param format 默认yyyy-MM-dd
     * @return
     */
    public static String changeDateFormat2(String oldDate, String format)
    {
        SimpleDateFormat odlSDF = new SimpleDateFormat("yyyy.MM.dd");
        SimpleDateFormat sdf = null;
        if(TextUtils.isEmpty(format))
        {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        }
        else
        {
            sdf = new SimpleDateFormat(format);
        }
        String dateStr = null;
        try {
            Date date  = odlSDF.parse(CommonUtil.isNull(oldDate));
            dateStr = sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        return dateStr;
    }



    /**
     * 转化为百分数
     * @param str
     * @return
     */
    public static String parsePercentString(String str)
    {
        if(TextUtils.isEmpty(str))
        {return "0";}
        Double d = Double.valueOf(CommonUtil.isNull(str));
        DecimalFormat df = new DecimalFormat("0.00");
        String rate = df.format(d*100.00d);
        return rate;
    }

    /***
     * MD5加密 32位
     * @param s 需要加密的字符串
     * @param charset 字符集
     * @return
     */
    public final static String MD5Encoder(String s, String charset) {
        try {
            byte[] btInput = s.getBytes(charset);
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < md.length; i++) {
                int val = ((int) md[i]) & 0xff;
                if (val < 16){
                    sb.append("0");
                }
                sb.append(Integer.toHexString(val));
            }
            return sb.toString();
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 验证15或者18位身份证号
     * @param no
     * @return
     */
    public static boolean isIDCardNO(String no){
//        Pattern pattern = Pattern.compile("^(\\d{6})(\\d{4})(\\d{2})(\\d{2})(\\d{3})([0-9]|X)$");
        Pattern pattern = Pattern.compile("/(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)/");
        Matcher matcher = pattern.matcher(no);
        return matcher.matches();
    }

    public static boolean isEmail(String email){
	    String str="[\\w!#$%&'*+/=?^_`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 验证中文字符
     * @param str
     * @return
     */
    public static boolean isChinese(String str){
        boolean mark = false;
        Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]");
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            mark = true;
            sb.append(matcher.group());
        }
        if (mark) {
            System.out.println("匹配的字符串为：" + sb.toString());
        }
        return mark;
    }

    public static boolean isMobileNO(String mobiles){
         Pattern pattern = Pattern.compile("1[0-9]{10}");
         Matcher matcher = pattern.matcher(mobiles);
         return matcher.matches();
    }


    /**
     * 密码符合规则：6~20位，必须数字和字母的组合
     * @param pwd
     * @return
     */
    public static boolean checkPassword(String pwd){
        Pattern pattern = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,20}$");
        Matcher matcher = pattern.matcher(pwd);
        return matcher.matches();
    }

    public static String getVersion(Context context)
    {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        String version = "";
        try {
            packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
            version = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return version;
    }
    
    
	public static String getCurrentDate(){
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datestr =  sDateFormat.format(new Date());
		return datestr;
	}
	
/*	public static Bitmap addTime2Bitmap(Bitmap bitmap){
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		Canvas canvas = new Canvas(bitmap); 
		Rect mBounds = new Rect();
		canvas.drawRect(bitmap.getWidth()/2,bitmap.getHeight()-50,bitmap.getWidth(),0, paint);		
        paint.setColor(Color.WHITE); 
        paint.setTextSize(8);
        float textWidth = mBounds.width();  
        float textHeight = mBounds.height(); 
        canvas.drawText(getCurrentDate(), canvas.getWidth() / 2 - textWidth/2, canvas.getHeight() / 2  + textHeight/2, paint);          
        canvas.drawBitmap(bitmap, 10, 60, paint);      
        return bitmap;
	}*/
	
	
	//从file转为byte[]
	  public static byte[] getBytesFromFile(File f){
	     if (f == null){
	         return null;
	    }
	     try {
	         FileInputStream stream = new FileInputStream(f);
	         ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
	         byte[] b = new byte[1000];
	         int n;
	         while ((n = stream.read(b)) != -1)
	             out.write(b, 0, n);
	          stream.close();
	          out.close();
	          return out.toByteArray();
	      } catch (IOException e){
	     }
	      return null;
	   }









	//从byte[]转file
	  public static File getFileFromBytes(byte[] b, String outputFile) {
	      BufferedOutputStream stream = null;
	      File file = null;
	      try {
	      file = new File(outputFile);
	           FileOutputStream fstream = new FileOutputStream(file);
	           stream = new BufferedOutputStream(fstream);
	           stream.write(b);
	       } catch (Exception e) {
	           e.printStackTrace();
	      } finally {
	          if (stream != null) {
	               try {
	                  stream.close();
	               } catch (IOException e1) {
	                  e1.printStackTrace();
	              }
	          }
	      }
	       return file;
	   }
	  
	  
	  
	  public static File saveBitmapToSd(String imagepath, Bitmap bitmap) {
	        // 生成完整的图片文件路径名称
	        File file = new File(imagepath);
	        if (!file.exists()) {
	            try {
	                FileOutputStream out = new FileOutputStream(file);
	                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
	                    out.flush();
	                    out.close();
	                    return file;
	                }
	            } catch (FileNotFoundException e) {
	                e.printStackTrace();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        return null;
	        
	    }
	  
	  
	  
	  
	
	/**
     * 图片上画字
     * */
	public static Bitmap drawTextAtBitmap(Bitmap bitmap){
        String text = getCurrentDate();
        int x = bitmap.getWidth();
        int y = bitmap.getHeight();

        // 创建一个和原图同样大小的位图
        Bitmap newbit = Bitmap.createBitmap(x,y, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(newbit);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // 在原始位置0，0插入原图
        canvas.drawBitmap(bitmap, 0, 0, paint);

        paint.setColor(Color.WHITE);

        paint.setTextSize(10);

        // 在原图指定位置写上字
        //canvas.drawText(text, x/2 + 100 , y-50, paint);

        canvas.drawText(text, x/2+20 , y-10 , paint);

        canvas.save(Canvas.ALL_SAVE_FLAG);

        // 存储
        canvas.restore();

        return newbit;
    }

    /**
     * 按照比例
     */
    public static Bitmap getImageThumbnailAuto(String imagePath, int width) throws Exception {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int height = width * h / w;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        Bitmap thumb = null;
        if (be != 1) {
            thumb = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            bitmap.recycle();
            return thumb;
        }
        return bitmap;
    }

    /**
     * 旋转图片
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        bitmap.recycle();
        return resizedBitmap;
    }

    /**
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap dealImage(String imagePath) {
        Bitmap bitmap = null;
        Bitmap result = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int ratio = w / 320;
        int height = h / ratio ;
        options.inSampleSize = ratio;
        bitmap = BitmapFactory.decodeFile(imagePath,options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        Bitmap thumb = ThumbnailUtils.extractThumbnail(bitmap, 320, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        bitmap.recycle();
        bitmap = null;
        int degree = CommonUtil.readPictureDegree(imagePath);

        if(degree != 0)
        {
            result = CommonUtil.rotaingImageView(degree,thumb);
            thumb.recycle();
            thumb = null;
        }
        else
        {
            return thumb;
        }
        return result;
    }

    /**
     * 双击两次退出应用
     */
    public static void exitBy2Click(Context context) {
        Timer tExit = null;// 定时器类
        if (isExit == false) {
            isExit = true;// 准备退出
            Toast.makeText(context, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            tExit = new Timer();//
            tExit.schedule(new TimerTask() {

                @Override
                public void run() {
                    isExit = false;// 取消退出
                }
            }, 2000);// 如果2秒钟内没有按下返回键,则启动定时器取消刚才执行的任务
        }else{
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    public static String parseDay(String day) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String today = sdf.format(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE,-1);
        String yesterday = sdf.format(calendar.getTime());
        if(today.equals(day)){
            return "今天";
        }
        if(yesterday.equals(day)){
            return "昨天";
        }
        return day.substring(day.length()-4,day.length());
    }

    /**
     * 去除字符串中所包含的空格（包括:空格(全角，半角)、制表符、换页符等）
     * @param s
     * @return
     */
    public static String removeAllBlank(String s){
        String result = "";
        if(null!=s && !"".equals(s)){
            result = s.replaceAll("[　*| *| *|//s*]*", "");
        }
        return result;
    }

    /**
     * 重启一个Activity
     *
     * @param activity Activity
     */
    public static void restartActivity(final Activity activity) {
        Intent intent = activity.getIntent();
        activity.overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.finish();
        activity.overridePendingTransition(0, 0);
        activity.startActivity(intent);
    }
}
