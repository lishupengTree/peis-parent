package com.lsp.his.web.pexam;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/12 9:46
 */

import com.lsp.his.db.DBOperator;
import com.lsp.his.tables.Bas_dept;
import com.lsp.his.tables.Bas_hospitals;
import com.lsp.his.tables.Bas_user;
import com.lsp.his.utils.ListUtil;
import com.lsp.his.utils.StrUtil;
import com.lsp.his.utils.VelocityUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.NumberTool;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * 总检报告 导出 pdf 格式
 *
 * @author 李树鹏    2016-03-21 17:46:25
 */
@Controller
@RequestMapping("/pexam")
public class PexamAction_ExpRort {
    public static void main(String[] args) throws Exception {
        StringBuffer sb = new StringBuffer();
        String path = getExpPath("exp\\1.txt");
        sb.append(readTxtFile(path));
        String[] a = sb.toString().split(",");
        System.out.println(a.length);
    }

//	public void a1() throws IOException{
//		PexamAction_ExpRort in = new PexamAction_ExpRort ();
//		StringBuffer buf = new StringBuffer();
//		String basePath1 = System.getProperty("user.dir")
//		+ "\\test\\tools\\pdf/xhtmlrenderer";
//		String inputFile = basePath1 + "/" + "template.xhtml";
//		String path = in.getClass().getResource("/").getPath();
//		//sb.append(readTxtFile(path + "exp_1.txt"));
//		buf.append(readTxtFile(path + "template.xhtml"));
//		try {
//			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
//					.newDocumentBuilder();
//			DocumentBuilderFactory factory = DocumentBuilderFactory
//					.newInstance();
//			factory.setNamespaceAware(true);
//			factory.setAttribute(
//					"http://java.sun.com/xml/jaxp/properties/schemaLanguage",
//					"http://www.w3.org/2001/XMLSchema");
//			Document doc = (Document) builder.parse(new InputSource(
//					new StringReader(buf.toString())));
//			ITextRenderer renderer = new ITextRenderer();
//			renderer.getFontResolver().addFont("C:/Windows/Fonts/ARIALUNI.TTF",   BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//			//renderer.getFontResolver().addFont("C:/WINDOWS/Fonts/sylfaen.TTF",BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//			renderer.setDocument(doc, null);
//			renderer.layout();
//			String basePath = System.getProperty("user.dir")
//					+ "/test/tools/pdf/xhtmlrenderer";
//			String outputFile = path + "test.pdf";
//			OutputStream os = new FileOutputStream(outputFile);
//			renderer.createPDF(os);
//			os.flush();
//			os.close();
//			System.out.println(22);
//
//		} catch (Exception ex) {
//			ex.printStackTrace();
//
//			// writer.print(ex.getMessage());
//		}
//	}

    //导出总检报告 pdf
    @RequestMapping(value = "/ExpPDFData")
    public void ExpPDFData(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_hospitals basHospital = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospital.getHosnum();
        String pexamid = request.getParameter("pexamid");
        PrintWriter pw = null;
        DBOperator db = null;
        String sql = "";
        List<Map> list = null;
        Map m = new HashMap();
        m.put("name", "lsp");
        list = new ArrayList<Map>();
        list.add(m);
        try {
            db = new DBOperator();
            StringBuilder sb = new StringBuilder();
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
            sb.append("<head>");
            sb.append(readTxtFile(getExpPath("exp\\exp_headcss.txt")));
            sb.append("</head>");
            sb.append("<body style=\"font-family:'Arial Unicode MS'\">");
            //设置页眉 和 页脚
            sb.append(gethtmlFirstPage(db, list));
            sb.append("<div style=\"page-break-after:always\"></div>");  //分页

            sb.append("<div class='version'>");
            sb.append(gethtmlDaodu(db));
            sb.append("<div style=\"page-break-after:always\"></div>");
            sb.append(gethtmlOne(db, pexamid, hosnum));
            sb.append("<div style=\"page-break-after:always\"></div>");
            sb.append(gethtmlTwo(db, pexamid, hosnum));
            sb.append("<div style=\"page-break-after:always\"></div>");
            sb.append(gethtmlThree(db, pexamid, hosnum));

            sb.append("</div>");
            sb.append("</body></html>");

            writeFile("c:\\report.html", sb.toString());
            pw = response.getWriter();
            pw.print("导出html成功");

//			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//			factory.setNamespaceAware(true);
//			factory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage","http://www.w3.org/2001/XMLSchema");
//			Document doc = (Document) builder.parse(new InputSource(new StringReader(sb.toString())));
//			ITextRenderer renderer = new ITextRenderer();
//			renderer.getFontResolver().addFont("C:/Windows/Fonts/ARIALUNI.TTF",   BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//			renderer.setDocument(doc, null);
//			renderer.layout();
//
//			OutputStream os = new FileOutputStream("c:\\1.pdf");
//			renderer.createPDF(os);
//			os.flush();
//			os.close();
//
//	        response.setContentType("application/x-msdownload;charset=gbk");
//			response.setCharacterEncoding("UTF-8");
//			response.setHeader("Content-Disposition", "attachment; filename="+ URLEncoder.encode("总检报告.pdf", "UTF-8"));
//			os = response.getOutputStream();
//			FileInputStream inputStream = new FileInputStream("c:\\1.pdf");
//			byte[] content = new byte[1024];
//			int length = 0;
//			while ((length = inputStream.read(content)) != -1) {
//				os.write(content, 0, length);
//			}
//            inputStream.close();
//            os.flush();
//            os.close();
//            inputStream.close();


        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            try {
                db.freeCon();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 首页
     *
     * @param db
     * @param list 输入的参数   病人信息的几个字段
     * @return
     * @throws Exception
     */
    public static String gethtmlFirstPage(DBOperator db, List<Map> list) throws Exception {
        //使用vm模版 写入参数值
        StringBuffer sb = new StringBuffer();
        String path = getExpPath("exp");
        String vm = "";
        String vmname = "exp_firstpage.vm";
        vm = generateVm(path, vmname, "list", list);
        sb.append(vm);

        return sb.toString();
    }

    /**
     * @param
     * @param vmname
     * @param listname
     * @param objects
     * @return
     * @throws Exception
     */
    public static String generateVm(String path, String vmname, String listname, List<? extends Object> objects) throws Exception {
        if (objects.size() > 0) {
            Properties p = new Properties();
            //String classpath = VelocityUtils.class.getResource("/").getPath();//取得src的路径
            p.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, path);
            //设置velocity的编码
            p.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
            p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
            p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
            //初始化并取得Velocity引擎
            VelocityEngine ve = new VelocityEngine();
            ve.init(p);
            //取得velocity的模版
            Template t = null;
            t = ve.getTemplate(vmname);
            //取得velocity的上下文context
            VelocityContext context = new VelocityContext();
            context.put("number", new NumberTool());
            context.put("date", new DateTool());
            context.put(listname, objects);
            //输出流
            StringWriter writer = new StringWriter();
            //转换输出
            t.merge(context, writer);
            return writer.toString();
        } else {
            return null;
        }
    }

    /**
     * css 样式
     *
     * @param db
     * @return
     * @throws Exception
     */
    public static String gethtmlHeadCss(DBOperator db) throws Exception {
        StringBuffer sb = new StringBuffer();
        String path = Thread.currentThread().getContextClassLoader().getResource("").toString();
        path = path.replace('/', '\\'); // 将/换成\
        path = path.replace("file:", ""); //去掉file:
        path = path.replace("classes\\", ""); //去掉class\
        path = path.substring(1); //去掉第一个\,如 \D:\JavaWeb...
        path += "exp_headcss.txt";
        sb.append(readTxtFile(path));

        return sb.toString();
    }

    /**
     * 返回 web-inf下的路径
     *
     * @param filename
     * @return
     * @throws Exception
     */
    public static String getExpPath(String filename) throws Exception {
        String path = Thread.currentThread().getContextClassLoader().getResource("").toString();
        path = path.replace('/', '\\'); // 将/换成\
        path = path.replace("file:", ""); //去掉file:
        path = path.replace("classes\\", ""); //去掉class\
        path = path.substring(1); //去掉第一个\,如 \D:\JavaWeb...
        path += filename;
        return path;
    }

    /**
     * 导读
     *
     * @param db
     * @return
     * @throws Exception
     */
    public static String gethtmlDaodu(DBOperator db) throws Exception {
        StringBuffer sb = new StringBuffer();
        String path = getExpPath("exp\\exp_daodu.txt");
        sb.append(readTxtFile(path));

        return sb.toString();
    }

    /**
     * 第一部分
     *
     * @param db
     * @return
     * @throws Exception
     */
    public static String gethtmlOne(DBOperator db, String pexamid, String hosnum) throws Exception {
        String sql = "";
        List<Map> list = null;
        StringBuffer sb = new StringBuffer();
        sql = "select * from pexam_deptsum a where a.hosnum=? and a.pexamid=? and a.sumtype='体检总结'";
        list = db.find(sql, new Object[]{hosnum, pexamid});
        //第一部分
        sb.append("<p style=\"font-size:18px; line-height:50px; text-align:center; display:block;margin-right:130px;font-weight: bold;\">" +
                "第一部分	结果汇总</p>");
        sb.append("<table width=\"680\"><tr><td style=\"line-height: 1.5;\">");
        if (list.size() > 0) {
            java.sql.Clob c = (java.sql.Clob) list.get(0).get("deptsum");
            sb.append(StrUtil.oracleClob2Str(c));
        } else {
            sb.append("");
        }
        sb.append("</td></tr></table>");

        return sb.toString();
    }

    /**
     * 第二部分
     *
     * @param db
     * @return
     * @throws Exception
     */
    public static String gethtmlTwo(DBOperator db, String pexamid, String hosnum) throws Exception {
        String sql = "";
        List<Map> list = null;
        StringBuffer sb = new StringBuffer();
        sql = "select * from pexam_deptsum a where a.sumtype='健康建议' and a.pexamid=? ";
        list = db.find(sql, new Object[]{pexamid});
        sb.append("<p style=\"font-size:18px; line-height:50px; text-align:center; display:block;margin-right:130px;font-weight: bold;\">第二部分	结论及建议</p>");
        sb.append("<table width=\"680\"><tr><td style=\"line-height: 1.5;\">");
        if (list.size() > 0) {
            java.sql.Clob c = (java.sql.Clob) list.get(0).get("deptsum");
            String deptsum = StrUtil.oracleClob2Str(c);
            if (deptsum != null && deptsum != "") {
                deptsum = deptsum.replaceAll("\\<p>|</p>", "");
            }
            sql = "select to_char(a.completedate,'yyyy-mm-dd hh24:mi:ss') completedate,a.doctorname from pexam_deptsum a where a.pexamid=? ";
            list = db.find(sql, new Object[]{pexamid});
            String completedateStr = "";
            String completeDoctorname = "";
            if (ListUtil.listIsNotEmpty(list)) {
                completedateStr = list.get(0).get("completedate") == null ? "" : list.get(0).get("completedate").toString();
                completeDoctorname = list.get(0).get("doctorname") == null ? "" : list.get(0).get("doctorname").toString();
                if (!"".equals(completedateStr) && !"".equals(completeDoctorname)) {
                    deptsum = deptsum + "<br/><br/><br/><span style=\"margin-left: 450px;\">总检医生：" + completeDoctorname + "&nbsp;&nbsp;&nbsp;&nbsp;</span><br><span style=\"margin-left: 450px;\">总检时间：" + completedateStr + "</span>";
                }
            }
            sb.append(deptsum);
        } else {
            sb.append("");
        }
        sb.append("</td></tr></table>");

        return sb.toString();
    }

    /**
     * 第三部分
     *
     * @param db
     * @return
     * @throws Exception
     */
    public static String gethtmlThree(DBOperator db, String pexamid, String hosnum) throws Exception {
        String sql = "";
        List<Map> list = null;
        StringBuffer sb = new StringBuffer();
        sb.append("<p style=\"font-size:18px; line-height:50px; text-align:center; display:block;margin-right:130px;font-weight: bold;\">第三部分	各科检查</p>");
        sql = "select a.itemcode, a.itemname, a.excdept, a.excdeptname, a.groupid, a.comclass, to_char(a.excdate, 'yyyy-mm-dd hh24:mi:ss') excdate, a.excdoctorname, r.indid, r.indname, r.result, r.resultunit, decode(null,i.minval,'',i.minval)  || '-' || decode(null,i.maxval,'',i.maxval) range , a.deptsum from pexam_items_title a left join pexam_results r on r.itemuuid = a.itemuuid  left join pexam_items_ind i on i.indid=r.indid  where a.pexamid = ? and a.tjxm='Y' and a.comclass='其他' and a.xmstatus='完成'  order by  to_number(a.sn) , to_number(i.sn) ";
        list = db.find(sql, new Object[]{pexamid});
        deptsumBr(list);  //处理list里的deptsum 字段
        String vmpagckage = "com/cpinfo/his/template/maintenance/";
        String vm = "";
        String vmname = "zjbg_gkjc.vm";
        //特殊处理（吴宝珠 耳鼻咽喉科  不显示 喉部和鼻咽部 小项。）
        for (int i = 0; i < list.size(); i++) {
            Map map2 = list.get(i);
            String itemname = map2.get("itemname").toString();
            String indname = map2.get("indname") == null ? "" : map2.get("indname").toString();
            if ("200024".equals(pexamid)) {
                String itemcode = map2.get("itemcode").toString();
                String indid = map2.get("indid").toString();
                if ("12935".equals(indid) || "13098".equals(indid)) {
                    list.remove(i);
                }
            }
            map2.put("itemname", itemname.replaceAll("\\(男\\)", ""));  //把类似与(男) 这样格式的字符串 去掉
            map2.put("itemname", itemname.replaceAll("\\(女\\)", ""));
        }
        vm = VelocityUtils.generateVm(vmpagckage, vmname, "list", list);
        sb.append(vm);
        //comclass='检验'
        sql = "select a.parent_comid,a.itemuuid ,a.itemcode, a.itemname, a.excdept, a.excdeptname, a.groupid, a.comclass, to_char(a.excdate, 'yyyy-mm-dd hh24:mi:ss') excdate, a.excdoctorname, r.indid, r.indname, r.result, r.resultunit, r.range,r.unnormal, a.deptsum from pexam_items_title a left join pexam_results r on r.itemuuid = a.itemuuid left join pexam_items_com c on a.itemcode = c.comid where a.pexamid = ? and a.tjxm='Y' and a.comclass='检验' and a.xmstatus='完成'  order by  to_number(c.sn), to_number(r.sn)";
        list = db.find(sql, new Object[]{pexamid});
        deptsumBr(list);//处理list里的deptsum 字段
        afternameToAllName(list, db, pexamid, ""); //检验的简称 变成全称
        for (int j = 0; j < list.size(); j++) {
            Map map3 = list.get(j);
            if ((map3.get("itemname").toString()).indexOf("肝功") > -1) {
                map3.put("itemname", "生化检验");
            }
            if (map3.get("parent_comid") != null) {
                list.remove(j);
            }
        }
        vm = VelocityUtils.generateVm(vmpagckage, vmname, "list", list);
        sb.append(vm);
        //comclass='检查'
        sql = "select a.itemcode, a.itemname, a.excdept, a.excdeptname, a.groupid, a.comclass, to_char(a.excdate, 'yyyy-mm-dd hh24:mi:ss') excdate, a.excdoctorname, r.indid, r.indname, r.result, r.resultunit, r.range, a.deptsum from pexam_items_title a left join pexam_results r on r.itemuuid = a.itemuuid where a.pexamid = ? and a.tjxm='Y' and a.comclass='检查' and a.xmstatus='完成'  order by  to_number(a.sn) ";
        list = db.find(sql, new Object[]{pexamid});
        deptsumBr(list); //处理list里的deptsum 字段
        vm = VelocityUtils.generateVm(vmpagckage, "zjbg_gkjc1.vm", "list", list);
        sb.append(vm);

        return sb.toString();
    }


    //最后一个字符是不是想要的字符
    //str——字符串， ch——想检查的字符
    public static boolean Equ(String str, char ch) {
        if (str == null || "".equals(str)) {
            return false;
        }
        if (str.charAt(str.length() - 1) == ch) {
            return true;
        } else {
            return false;
        }
    }

    public static void deptsumBr(List<Map> list) throws Exception {
        for (Map map : list) {
            String deptsum = map.get("deptsum") == null ? "" : map.get("deptsum").toString();
            if (Equ(deptsum, '\r')) { //如果最后一个字符是\r  就去掉
                deptsum = deptsum.substring(0, deptsum.length() - 1);
            }
            deptsum = deptsum.replaceAll("\\r", "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            map.put("deptsum", deptsum);
        }
    }

    public static void afternameToAllName(List<Map> list, DBOperator db, String pexamid, String type) throws Exception {
        String sql = "select a.itemuuid,a.itemname,a.parent_comid,a.tmcode,a.afterhb_name  from pexam_items_title a where a.pexamid=? and a.tjxm = 'Y' and a.comclass='检验' and a.xmstatus='完成' order by a.parent_comid ";
        if ("1".equals(type)) {
            sql = "select a.itemname ,a.parent_comid, a.itemuuid  from pexam_items_title a left join pexam_items_com c on a.itemcode = c.comid and c.hosnum = a.hosnum left join pexam_items_type t on c.parentid = t.typeid and t.hosnum = c.hosnum where a.tjxm = 'Y'  and a.pexamid = ? and a.deptsum is not null order by a.parent_comid ";
        }
        List<Map> tList = db.find(sql, new Object[]{pexamid});
        Map nameMap = new HashMap(); //存放全称的map
        for (Map map : tList) {
            String itemuuid = map.get("itemuuid").toString();
            String parent_comid = map.get("parent_comid") == null ? "" : map.get("parent_comid").toString();
            String itemname = map.get("itemname").toString();
            if (!"".equals(parent_comid)) {
                if (nameMap.get(parent_comid) == null) {
                    nameMap.put(parent_comid, itemname);
                } else {
                    nameMap.put(parent_comid, nameMap.get(parent_comid) + "+" + itemname);
                }
            } else {
                if (nameMap.get(itemuuid) != null) {
                    nameMap.put(itemuuid, nameMap.get(itemuuid) + "+" + itemname);
                }
            }
        }
        for (int i = 0; i < list.size(); i++) {
            Map map2 = list.get(i);
            String itemuuid = map2.get("itemuuid").toString();
            if (!"1".equals(type)) {
                String result = map2.get("result") == null ? "" : map2.get("result").toString(); //检验的小项结果
                String unnormal = map2.get("unnormal") == null ? "" : map2.get("unnormal").toString(); //检验的正常是否的 标志 unnormal
                if ("l".equals(unnormal) || "ll".equals(unnormal)) {
                    map2.put("result", result + "&nbsp;↓");
                } else if ("h".equals(unnormal) || "hh".equals(unnormal)) {
                    map2.put("result", result + "&nbsp;↑");
                }
            }
            if (nameMap.get(itemuuid) != null) {
                map2.put("itemname", nameMap.get(itemuuid));
            }
        }
        for (int i = 0; i < list.size(); i++) {
            Map map3 = list.get(i);
            if ((map3.get("itemname").toString()).indexOf("肝功") > -1) {
                map3.put("itemname", "生化检验");
            }
            if (map3.get("parent_comid") != null) {
                list.remove(i);
            }
        }
    }

    /**
     * 读txt文件并返回 读取 的字符串
     *
     * @param filePath
     * @return
     * @throws
     */
    public static String readTxtFile(String filePath) throws IOException {
        StringBuilder sb = new StringBuilder();
        String encoding = "UTF-8";
        File file = new File(filePath);
        if (file.isFile() && file.exists()) { //判断文件是否存在
            InputStreamReader read = new InputStreamReader(
                    new FileInputStream(file), encoding);//考虑到编码格式
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineTxt = null;
            while ((lineTxt = bufferedReader.readLine()) != null) {
                sb.append(lineTxt);
            }
            read.close();
        }
        return sb.toString();
    }

    /**
     * 写 文本文件  txt 或者 html
     *
     * @param filename 文件名（绝对路径+文件名+后缀）
     * @param content  写入的内容
     * @throws
     */
    public static void writeFile(String filename, String content) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            file.createNewFile();
        } else {
            file.delete();
            file.createNewFile();
        }
        RandomAccessFile mm = null;
        FileOutputStream o = null;
        try {
            o = new FileOutputStream(file);
            o.write(content.getBytes("UTF-8"));
            o.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mm != null) {
                mm.close();
            }
        }
    }
}

