package com.lsp.his.utils;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/12 9:49
 */
public class ReadExcel {


    public static Map readExcel(String pathname) {
        Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
        Map<Integer, String> titleMap = new HashMap<Integer, String>();
        Map temp = null;
        try {
            //打开文件
            Workbook book = Workbook.getWorkbook(new File(pathname));
            //取得第一个sheet
            Sheet sheet = book.getSheet(0);
            //取得行数
            int rows = sheet.getRows();
            for (int i = 0; i < rows; i++) {
                temp = new HashMap();
                Cell[] cell = sheet.getRow(i);
                if (i == 0) {//获取第一行数据，得到列头
                    for (int j = 0; j < cell.length; j++) {
                        //getCell(列，行)
                        String columnName = "";
                        if ("身份证".equals(sheet.getCell(j, i).getContents().trim())) {
                            columnName = "sfz";
                        } else if ("序号".equals(sheet.getCell(j, i).getContents().trim())) {
                            columnName = "xh";
                        } else if ("名字".equals(sheet.getCell(j, i).getContents().trim())) {
                            columnName = "xm";
                        } else if ("养老保障参保情况".equals(sheet.getCell(j, i).getContents().trim())) {
                            columnName = "ybbzcbqk";
                        } else if ("联系电话".equals(sheet.getCell(j, i).getContents().trim())) {
                            columnName = "lxdh";
                        } else if ("享受状态".equals(sheet.getCell(j, i).getContents().trim())) {
                            columnName = "xszk";
                        } else if ("预约时间".equals(sheet.getCell(j, i).getContents().trim())) {
                            columnName = "yysj";
                        } else if ("家庭住址".equals(sheet.getCell(j, i).getContents().trim())) {
                            columnName = "jtzz";
                        } else {
                            columnName = "columnName" + j;
                        }
                        titleMap.put(j, columnName);
                        System.out.print(sheet.getCell(j, i).getContents());
                    }
                } else {
                    String cardId = "";
                    for (int j = 0; j < cell.length; j++) {
                        String aaa = titleMap.get(j);
                        if ("sfz".equals(aaa)) {
                            cardId = sheet.getCell(j, i).getContents().trim();
                        }
                        temp.put(titleMap.get(j), sheet.getCell(j, i).getContents().trim());
                    }
                    map.put(cardId, temp);
                }
            }
            //关闭文件
            book.close();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }


}
