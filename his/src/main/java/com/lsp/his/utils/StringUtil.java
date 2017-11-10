package com.lsp.his.utils;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/9 12:50
 */

import java.util.ArrayList;


public class StringUtil {
    /**
     * 分割字符串
     *
     * @param str       原始字符串
     * @param splitsign 分隔符
     * @return String[] 分割后的字符串数组
     */
    @SuppressWarnings("unchecked")
    public static String[] split(String str, String splitsign) {
        int index;
        if (str == null || splitsign == null)
            return null;
        ArrayList al = new ArrayList();
        while ((index = str.indexOf(splitsign)) != -1) {
            al.add(str.substring(0, index));
            str = str.substring(index + splitsign.length());
        }
        al.add(str);
        return (String[]) al.toArray(new String[0]);
    }

    /**
     * 替换字符串
     *
     * @param from   String 原始字符串
     * @param to     String 目标字符串
     * @param source String 母字符串
     * @return String 替换后的字符串
     */
    public static String replace(String from, String to, String source) {
        if (source == null || from == null || to == null)
            return null;
        StringBuffer bf = new StringBuffer("");
        int index = -1;
        while ((index = source.indexOf(from)) != -1) {
            bf.append(source.substring(0, index) + to);
            source = source.substring(index + from.length());
            index = source.indexOf(from);
        }
        bf.append(source);
        return bf.toString();
    }

    /**
     * 判断字符串不为空  不为空返回true
     *
     * @param str
     * @return
     */
    public static boolean strIsNotEmpty(String str) {
        if (str == null || str.equals("")) {
            return false;
        } else {
            return true;
        }
    }


    public static void main(String[] args) {

        String[] a = StringUtil.split("2222,", ",");
        System.out.println(a.toString());
    }
}

