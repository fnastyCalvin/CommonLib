package com.calvin.commonlib.common.util;

import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jiangtao on 2016/5/5 20:07.
 */
public class TextViewUtil {
    private TextViewUtil() {
        //no instance
    }

    /**
     * 获取TextView长度
     *
     * @param textView
     * @return
     */
    public static int getTextLength(TextView textView) {
        TextPaint paint = textView.getPaint();
        return (int) Layout.getDesiredWidth(textView.getText().toString(), 0,textView.getText().length(), paint);
    }

    /**
     * 给某段支付设置下划线
     */
    public static SpannableString underLine(String str, String underLineStr) {
        // 创建一个 SpannableString对象
        SpannableString sp = new SpannableString(str);
        int index = str.indexOf(underLineStr);
        //设置背景颜色, StrikethroughSpan()是设置中划线
        sp.setSpan(new UnderlineSpan(), index, index + underLineStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
    }

    /**
     * 高亮所有关键字
     *
     * @param str 这个字符串
     * @param key 关键字
     */
    public static SpannableString highlightKeyword(String str, String key, int highlightColor) {
        SpannableString sp = new SpannableString(str);

        Pattern p = Pattern.compile(key);
        Matcher m = p.matcher(str);

        while (m.find()) {  //通过正则查找，逐个高亮
            int start = m.start();
            int end = m.end();
            sp.setSpan(new ForegroundColorSpan(highlightColor), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return sp;
    }

    /**
     * 创建一个含有超链接的字符串
     *
     * @param text      整段字符串
     * @param clickText 含有超链接的字符
     * @param url       超链接
     */
    public static SpannableString createLinkText(String text, String clickText, String url) {
        SpannableString sp = new SpannableString(text);
        int index = text.indexOf(clickText);
        // 设置超链接
        sp.setSpan(new URLSpan(url), index, index + clickText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sp;
    }

    /**
     * 高亮整段字符串
     */
    public static SpannableStringBuilder highLightStr(String str, int color) {
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        style.setSpan(new ForegroundColorSpan(color), 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return style;
    }

    /**
     * 高亮代码片段
     *
     * @param str          整段字符串
     * @param highLightStr 要高亮的代码段
     * @param color        高亮颜色
     * @return
     */
    public static SpannableStringBuilder highLightStr(String str, String highLightStr, int color) {
        int start = str.indexOf(highLightStr);
        SpannableStringBuilder style = new SpannableStringBuilder(str);
        // new BackgroundColorSpan(Color.RED)背景高亮
        // ForegroundColorSpan(Color.RED) 字体高亮
        style.setSpan(new ForegroundColorSpan(color), start, start+ highLightStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return style;
    }
}
