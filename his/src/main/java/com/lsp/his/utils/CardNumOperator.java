package com.lsp.his.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/12 9:50
 */
public class CardNumOperator {

    /**
     * 根据身份证号获取生日
     *
     * @param pid 身份证号
     */
    public static Date getBirthdayByPid(String pid) throws Exception {
        Calendar birthdayCaoendar = Calendar.getInstance();
        if (pid != null && (pid.trim().length() == 15 || pid.trim().length() == 18)) {
            if (pid.trim().length() == 15) {
                String pid18 = "";//pid15To18(pid);
                birthdayCaoendar.set(Calendar.YEAR, Integer.parseInt(pid18.substring(6, 10)));
                //Month 值是基于 0 的。例如，0 表示 January
                birthdayCaoendar.set(Calendar.MONTH, Integer.parseInt(pid18.substring(10, 12)) - 1);
                birthdayCaoendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(pid18.substring(12, 14)));
            } else {
                birthdayCaoendar.set(Calendar.YEAR, Integer.parseInt(pid.substring(6, 10)));
                //Month 值是基于 0 的。例如，0 表示 January
                birthdayCaoendar.set(Calendar.MONTH, Integer.parseInt(pid.substring(10, 12)) - 1);
                birthdayCaoendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(pid.substring(12, 14)));
            }
            return birthdayCaoendar.getTime();
        } else {
            return null;
        }
    }


    /**
     * 根据身份证号获取性别
     *
     * @param pid 身份证号
     * @return 性别 F为女M为男
     */
    public static String getSexByPid(String pid) throws Exception {
        if (pid != null && (pid.trim().length() == 15 || pid.trim().length() == 18)) {
            if (pid.trim().length() == 15) {
                String pid18 = "";//pid15To18(pid);
                if (Integer.parseInt(pid18.substring(16, 17)) % 2 == 0) {
                    return "女";
                } else {
                    return "男";
                }
            } else {
                if (Integer.parseInt(pid.substring(16, 17)) % 2 == 0) {
                    return "女";
                } else {
                    return "男";
                }
            }
        } else {
            return null;
        }
    }

}
