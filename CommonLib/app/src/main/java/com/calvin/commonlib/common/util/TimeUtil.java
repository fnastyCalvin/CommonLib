package com.calvin.commonlib.common.util;

import java.util.Date;

/**
 * Created by jiangtao on 2016/3/30 14:45.
 */
public class TimeUtil {
    private TimeUtil(){}

    /**
     * 以秒为单位计算时间间隔
     */
    private static final long MIN = 60;
    private static final long HOUR = MIN * 60;
    private static final long DAY = HOUR * 24;
    private static final long WEEK = DAY * 7;
    private static final long MONTH = DAY * 30;
    private static final long YEAR = MONTH * 12;

    public static String getInterval(Date date) {
        if (date == null) {
            return "";
        }
        long time = date.getTime();
        return getInterval(time);
    }

    public static String getInterval(long time) {
        long seconds = (System.currentTimeMillis() - time) / 1000;
        if (seconds < 3) {
            return "刚刚";
        } else if (seconds < MIN) {
            return seconds + "秒钟";
        } else if (seconds < HOUR) {
            return seconds / MIN + "分钟";
        } else if (seconds < DAY) {
            return seconds / HOUR + "小时";
        } else if (seconds < YEAR * 5) {
            return seconds / DAY + "天";
        } else {
            return seconds / DAY + "天";
        }
    }
}
