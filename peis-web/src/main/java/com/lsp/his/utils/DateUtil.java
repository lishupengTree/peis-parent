package com.lsp.his.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/8 23:58
 */
public class DateUtil {
    private static List<Calendar> holidayList;
    private static boolean holidayFlag;

    public static Calendar addDateByWorkDay(Calendar src, int adddays) {
        // Calendar result = null;
        holidayFlag = false;
        for (int i = 0; i < adddays; i++) {
            // 把源日期加一天
            src.add(Calendar.DAY_OF_MONTH, 1);
            holidayFlag = checkHoliday(src);
            if (holidayFlag) {
                i--;
            }
            //System.out.println(src.getTime());
        }
        //System.out.println("Final Result:" + src.getTime());
        return src;
    }

    public static boolean checkHoliday(Calendar src) {
        boolean result = false;
        if (holidayList == null) {
            initHolidayList();
        }
        // 先检查是否是周六周日(有些国家是周五周六)
        if (src.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
                || src.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return true;
        }
        for (Calendar c : holidayList) {
            if (src.get(Calendar.MONTH) == c.get(Calendar.MONTH)
                    && src.get(Calendar.DAY_OF_MONTH) == c
                    .get(Calendar.DAY_OF_MONTH)) {
                result = true;
            }
        }
        return result;
    }

    private static void initHolidayList() {
        holidayList = new ArrayList();

        // 五一劳动节
        Calendar may1 = Calendar.getInstance();
        may1.set(Calendar.MONTH, Calendar.MAY);
        may1.set(Calendar.DAY_OF_MONTH, 1);
        holidayList.add(may1);

        Calendar may2 = Calendar.getInstance();
        may2.set(Calendar.MONTH, Calendar.MAY);
        may2.set(Calendar.DAY_OF_MONTH, 2);
        holidayList.add(may2);

        Calendar may3 = Calendar.getInstance();
        may3.set(Calendar.MONTH, Calendar.MAY);
        may3.set(Calendar.DAY_OF_MONTH, 3);
        holidayList.add(may3);

        Calendar h3 = Calendar.getInstance();
        h3.set(2000, 1, 1);
        holidayList.add(h3);

        Calendar h4 = Calendar.getInstance();
        h4.set(2000, 12, 25);
        holidayList.add(h4);

        // 中国母亲节：五月的第二个星期日
        Calendar may5 = Calendar.getInstance();
        // 设置月份为5月
        may5.set(Calendar.MONTH, Calendar.MAY);
        // 设置星期:第2个星期
        may5.set(Calendar.DAY_OF_WEEK_IN_MONTH, 2);
        // 星期日
        may5.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        // System.out.println(may5.getTime());

        holidayList.add(may5);
    }
    public static Date stringToDate(String str,String format){
        if(str==null||"".equals(str)){
            return null;
        }
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        try{
            return sdf.parse(str);
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static String dateToString(Date date,String format){
        if(date==null){
            return "";
        }
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        try{
            return sdf.format(date);
        }catch(Exception e){
            return "";
        }
    }
    public static Long getDaysBetween(Date startDate, Date endDate) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(startDate);
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(endDate);
        endCalendar.set(Calendar.HOUR_OF_DAY, 0);
        endCalendar.set(Calendar.MINUTE, 0);
        endCalendar.set(Calendar.SECOND, 0);
        endCalendar.set(Calendar.MILLISECOND, 0);

        return (endCalendar.getTime().getTime() - startCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24);
    }

    //long类型的字符串转化为指定格式的时间字符串
    public static String strlongTodatestr(String lonstr,String format){
        if(lonstr==null||"".equals(lonstr)){
            return null;
        }
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        try{
            Long lontime=Long.parseLong(lonstr);
            Date dt1=new Date(lontime);
            String datestr=sdf.format(dt1);
            return datestr;

        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 判断当前日期为星期几
     * @param date
     * @return
     */
    public static int dayOfWeek(Date date) {
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.setTime(date);
        int weekDay = aCalendar.get(Calendar.DAY_OF_WEEK)-1;
        return weekDay;
    }

    /**
     * 获取指定日期指定天数后的日期
     * @param date 指定日期
     * @param index 指定天数
     * @param flag 是否将时分秒归0
     * @return
     */
    public static Date getNextDate(Date date, int index, boolean flag) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);// 获得当前时间
        if (flag) {
            // 日期不变，把时间设定为00：00：00
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 00);
            cal.set(Calendar.SECOND, 00);
        }
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) + index);
        return cal.getTime();
    }

    public static Date getDate(Date currentDate, int days) {
		/*
		 * 1，根据传入日期获取下一天日期
		 * 2，判断下一天日期是否为工作日，如果是则设置下一次循环日期为此日期
		 *       如果不为工作日，为周6，日期前进2天，为周天前进1天
		 * 3, 获取指定天数后的工作日
		 */
        Date date = currentDate;
		/* 设置循环次数
		 * 如果含最后一天则循环 days + 1 天，不需要含最后一天，则循环 days次
		 *  */
        for (int i = 0; i < days + 1; i++) {

            Date nextDate = getNextDate(date, 1, false); //获取下一天的日期
            int weekDay = dayOfWeek(nextDate); //下一天日期为星期几
            if (weekDay == 1) { //为星期六
                date = getNextDate(date, 2, false);
            } else if (weekDay == 6) { //为星期天
                date = getNextDate(date, 3, false);
            } else {
                date = nextDate;
            }
        }
        return date;
    }

    public static Calendar DatToeCal(Date d){
        Calendar  c = Calendar.getInstance();
        c.setTime(d);
        return c;
    }
    public static Date getworkday(Date date, int itervalByDay) {
        long millisceonds = date.getTime();
        for (int i = 1; i <= itervalByDay; i++) {
            millisceonds += 24 * 60 * 60 * 1000;
            date.setTime(millisceonds);
            if (date.getDay() == 0 || date.getDay() == 6)
                i--;
        }
        return date;
    }

    public static void main(String[] args) {

    }


}
