package com.lsp.peis.utils;

import java.util.List;
import java.util.Map;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/6 16:40
 */
public class ListUtil {

    /**
     * 判断list不为空
     * @param list
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean listIsNotEmpty(List list){
        if(list != null && list.size() >= 1){
            return true;
        }else{
            return false;
        }
    }

    /**
     * 提取list的第一行数据
     * @param list
     * @return
     */
    @SuppressWarnings("unchecked")
    public static Object distillFirstRow(List list){
        if(listIsNotEmpty(list)){
            return list.get(0);
        }else{
            return null;
        }
    }
    /**
     * list中的一个字段 组装成String
     * @param list
     * @param name
     * @return
     */

    public static String getListstring(List<Map> list, String name){
        if(!listIsNotEmpty(list)){
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map map : list) {
            String code = (String)map.get(name);
            sb.append(code+",");
        }
        //sb.substring(0, sb.length()-1);
        return sb.toString();
    }
    public static String getListstring1(List<Map> list,String name){
        if(!listIsNotEmpty(list)){
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map map : list) {
            String code = (String)map.get(name);
            sb.append("'" + code+ "'"+ ",");
        }
        String s = sb.toString().substring(0, sb.length()-1);

        return s;
    }


}
