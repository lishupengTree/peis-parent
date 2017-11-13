package com.lsp.his.utils;

import com.lsp.his.db.DBOperator;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.UnderlineStyle;
import jxl.read.biff.BiffException;
import jxl.write.*;
import jxl.write.Number;
import jxl.write.biff.RowsExceededException;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/9 0:06
 */
public class ExcelUtils {

    private WritableCellFormat titleWcf;//excel标题

    private WritableCellFormat legendWcf;//excel表头

    private WritableCellFormat contentWcf;//excel正文

    public WritableCellFormat getTitleWcf() {
        return titleWcf;
    }

    public void setTitleWcf(WritableCellFormat titleWcf) {
        this.titleWcf = titleWcf;
    }

    public WritableCellFormat getLegendWcf() {
        return legendWcf;
    }

    public void setLegendWcf(WritableCellFormat legendWcf) {
        this.legendWcf = legendWcf;
    }

    public WritableCellFormat getContentWcf() {
        return contentWcf;
    }

    public void setContentWcf(WritableCellFormat contentWcf) {
        this.contentWcf = contentWcf;
    }

    public ExcelUtils() throws WriteException {
        init();
    }

    /**
     * 初始化标题字体、表头字体、内容字体的样式
     * @throws WriteException
     */
    private void init() throws WriteException{
        // 设置标题字体
        WritableFont titleWf = new WritableFont(WritableFont.ARIAL, 18, WritableFont.BOLD,
                false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
        this.titleWcf = new WritableCellFormat(titleWf);
        this.titleWcf.setAlignment(jxl.format.Alignment.CENTRE); // 设置对齐方式
        this.titleWcf.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.NONE, Colour.BLACK);// 设置细边框

        // 设置表头字体
        WritableFont legendWf = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD,
                false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
        this.legendWcf = new WritableCellFormat(legendWf);
        this.legendWcf.setAlignment(jxl.format.Alignment.CENTRE); // 设置水平对齐方式
        this.legendWcf.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE); // 设置垂直对齐方式
        this.legendWcf.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN, Colour.BLACK);// 设置细边框
        this.legendWcf.setWrap(true);

        // 设置正文字体
        WritableFont contentWf = new WritableFont(WritableFont.ARIAL, 12, WritableFont.NO_BOLD,
                false, UnderlineStyle.NO_UNDERLINE, Colour.BLACK);
        this.contentWcf = new WritableCellFormat(contentWf);
        this.contentWcf.setAlignment(jxl.format.Alignment.CENTRE); // 设置对齐方式
        contentWcf.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE); // 设置垂直对齐方式
        contentWcf.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN, Colour.BLACK);// 设置细边框
    }

    /**
     * 创建表头
     * @param wsheet
     * @param x
     * @param y
     * @param legends
     * @param sizes
     * @return 表头所在的行数
     * @throws WriteException
     * @throws RowsExceededException
     */
    public int buildLegend(WritableSheet wsheet, int x, int y, String[] legends, int[] sizes) throws RowsExceededException, WriteException{
        for(int i=0; i<legends.length; i++){
            Label wlabel = new Label(x+i, y, legends[i], this.legendWcf);
            wsheet.setColumnView(x+i, sizes[i]);
            wsheet.addCell(wlabel);
        }
        return 1;
    }

    /**
     * 创建额外内容
     * @param wsheet
     * @param x
     * @param y
     */
    public void buildExtraContent(WritableSheet wsheet, int x, int y){
    }

    public void export(OutputStream os, List<Object[]> datas) throws BiffException, IOException, RowsExceededException, WriteException {
        this.export(null ,os, null, null, null, datas, 0, 0);
    }

    public void export(OutputStream os, String[] legends, int[] sizes, List<Object[]> datas) throws BiffException, IOException, RowsExceededException, WriteException {
        this.export(null ,os, null, legends, sizes, datas, 0, 0);
    }

    public void export(OutputStream os, String title, String[] legends, int[] sizes, List<Object[]> datas) throws BiffException, IOException, RowsExceededException, WriteException {
        this.export(null ,os, title, legends, sizes, datas, 0, 0);
    }

    public void export(OutputStream os, String title, String[] legends, int[] sizes, List<Object[]> datas, int offsetX, int offsetY) throws BiffException, IOException, RowsExceededException, WriteException {
        this.export(null ,os, title, legends, sizes, datas, offsetX, offsetY);
    }

    /**
     * 将已有数据写入Excel
     * @param templateFile 模板文件
     * @param os 数据流，如果是写本地文件的话，可以是FileOutputStream;如果是写Web下载的话，可以是ServletOupputStream
     * @param title 工作簿的标题,如果不用的话,可以写null或者""
     * @param legends 表头名称
     * @param sizes 设定每一列的宽度
     * @param datas 数据集
     * @param offsetX 单元格横向偏移量
     * @param offsetY 单元格纵向偏移量
     * @throws BiffException
     * @throws IOException
     * @throws RowsExceededException
     * @throws WriteException
     */
    public void export(File templateFile, OutputStream os, String title, String[] legends, int[] sizes, List<Object[]> datas, int offsetX, int offsetY) throws BiffException, IOException, RowsExceededException, WriteException {
        WritableWorkbook wbook = null;
        WritableSheet wsheet = null;
        if(templateFile!=null){
            Workbook wb = Workbook.getWorkbook(templateFile);
            wbook = Workbook.createWorkbook(os, wb);
            wsheet = wbook.getSheet(0);
        }else{
            wbook = Workbook.createWorkbook(os); // 建立excel文件
            wsheet = wbook.createSheet("第一页", 0); // sheet名称
        }

        if (title != null && !title.trim().equals("")) {// 添加标题
            wsheet.mergeCells(offsetX, offsetY, offsetX+datas.get(0).length-1, offsetY); // 合并单元格
            Label wlabel = new Label(offsetX, offsetY, title, this.titleWcf);
            wsheet.addCell(wlabel);
        }

        // 如果有标题的话，要设置一下偏移
        int rowIndex = 1;
        if (title == null || title.trim().equals("")) {
            rowIndex = 0;
        }

        //创建表头
        if(legends!=null && sizes!=null){
            rowIndex = rowIndex + this.buildLegend(wsheet, offsetX, offsetY+rowIndex, legends, sizes);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        WritableCell wcell = null;//单元格
        //往Excel输出数据
        for (int i=0;i<datas.size();i++) {
            for(int j=0;j<datas.get(i).length;j++){
                Object value = datas.get(i)[j];
                if(value instanceof java.lang.Number) {
                    wcell = new Number(offsetX+j, offsetY+rowIndex, ((java.lang.Number)value).doubleValue(), this.contentWcf);
                }else if(value instanceof java.util.Date) {
                    wcell = new Label(offsetX+j, offsetY+rowIndex, sdf.format((java.util.Date)value), this.contentWcf);
                }else{
                    wcell = new Label(offsetX+j, offsetY+rowIndex, (java.lang.String)value, this.contentWcf);
                }
                wsheet.addCell(wcell);
            }
            rowIndex++;
        }

        //创建额外内容
        buildExtraContent(wsheet, offsetX, offsetY+rowIndex);

        wbook.write(); // 写入文件
        wbook.close();
        os.flush();
        os.close();
    }

    /**
     * 将已有数据写入Excel,运用本地文件修改前2行为标题得到
     * @param templateFile 模板文件
     * @param os 数据流，如果是写本地文件的话，可以是FileOutputStream;如果是写Web下载的话，可以是ServletOupputStream
     * @param title 工作簿的标题,必须的
     * @param datas 数据集
     * @param offsetX 单元格横向偏移量
     * @param offsetY 单元格纵向偏移量
     */
    public void exportWithTitle(File templateFile, OutputStream os, String title, List<Object[]> datas, int offsetX, int offsetY) throws BiffException, IOException, RowsExceededException, WriteException {
        WritableWorkbook wbook = null;
        WritableSheet wsheet = null;
        if(templateFile!=null){
            Workbook wb = Workbook.getWorkbook(templateFile);
            wbook = Workbook.createWorkbook(os, wb);
            wsheet = wbook.getSheet(0);
        }else{
            wbook = Workbook.createWorkbook(os); // 建立excel文件
            wsheet = wbook.createSheet("第一页", 0); // sheet名称
        }

        if (title != null && !title.trim().equals("")) {// 添加标题

            WritableCell cell = wsheet.getWritableCell(0,0);
            if(cell.getType() == CellType.LABEL){
                Label lable = (Label)cell;
                lable.setString(title);
            }
        }


        int rowIndex = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        WritableCell wcell = null;//单元格
        //往Excel输出数据
        for (int i=0;i<datas.size();i++) {
            for(int j=0;j<datas.get(i).length;j++){
                Object value = datas.get(i)[j];
                if(value instanceof java.lang.Number) {
                    wcell = new Number(offsetX+j, offsetY+rowIndex, ((java.lang.Number)value).doubleValue(), this.contentWcf);
                }else if(value instanceof java.util.Date) {
                    wcell = new Label(offsetX+j, offsetY+rowIndex, sdf.format((java.util.Date)value), this.contentWcf);
                }else{
                    wcell = new Label(offsetX+j, offsetY+rowIndex, (java.lang.String)value, this.contentWcf);
                }
                wsheet.addCell(wcell);
            }
            rowIndex++;
        }

        //创建额外内容
        buildExtraContent(wsheet, offsetX, offsetY+rowIndex);

        wbook.write(); // 写入文件
        wbook.close();
        os.flush();
        os.close();
    }


    /**
     * 导出单行标题的Excel
     * @param response
     * @param listMaps  数据集合
     * @param titles	标题列表
     * @param length    单元格宽度
     * @param newName   新建EXCEL 名称
     */
    public static void exportExcel(HttpServletResponse response, List<Map<String,Object>> listMaps, String[] titles, int[] length, String newName) throws Exception, Exception, Exception, Exception{
        response.setContentType("application/x-msdownload;charset=gbk");
        response.setCharacterEncoding("UTF-8");
        String fileNameTemp = URLEncoder.encode(newName, "UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + new String(fileNameTemp.getBytes("utf-8"), "gbk"));
        OutputStream os = response.getOutputStream();
        ExcelUtils eu = new ExcelUtils();
        List<Object[]> listObjects = new ArrayList<Object[]>();
        if(listMaps != null){
            listObjects = DbUtils.ListMapToListObject(listMaps);
        }
        eu.export(os, titles ,length,listObjects);
        os.flush();
        os.close();
    }


    public static void main(String[] args) {
        try {
            DBOperator db = new DBOperator();
            try {
                String sql = "select t.patientid,t.clctimes,t.patname,t.dateofbirth,t.email from bas_patients t";
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> maps = db.find(sql);
                ExcelUtils eu = new ExcelUtils();
                File file = new File("D:/test.xls");
                OutputStream os = new FileOutputStream(file);
                eu.export(os, "测试", new String[]{"病人标识","就诊次数","病人姓名","出生年月","电子邮箱"}, new int[]{28,12,15,20,25}, DbUtils.ListMapToListObject(maps), 0, 0);
                db.commit();
                System.out.println("导出成功！");
            } catch (Exception e) {
                e.printStackTrace();
                db.rollback();
            } finally {
                db.freeCon();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
