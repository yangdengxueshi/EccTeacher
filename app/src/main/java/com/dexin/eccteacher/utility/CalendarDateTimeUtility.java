package com.dexin.eccteacher.utility;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日历、日期、时间 工具
 */
public final class CalendarDateTimeUtility {
    /**
     * 将传递过来的日期做偏移处理后返回对应的格式
     *
     * @param date      日期（传递的参数date）
     * @param offset    偏移量（支持负数）
     * @param formatStr 格式化字符串
     * @return 偏移后的date对应的星期
     */
    public static String getSpecialDateFormat(Date date, int offset, String formatStr) {
        Date offsetDate = translationDate(date, offset);
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr, Locale.CHINA);          // EEEE:星期五         E / EE /EEE: 周五
//        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.US);             // EEEE:Thursday      E / EE /EEE: Sun Mon Tue Wed Thu Fri Sat
        return sdf.format(offsetDate);
    }

    /**
     * 将传递过来的日期进行偏移后返回
     *
     * @param date   日期（传递的参数date）
     * @param offset 偏移量（支持负数）
     * @return 偏移后的date
     */
    public static Date translationDate(Date date, int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, offset);                    //日期偏移，（TODO 对应可以做 其它时间量 的偏移）
        return calendar.getTime();
    }
}
