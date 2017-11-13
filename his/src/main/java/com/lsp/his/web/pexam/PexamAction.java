package com.lsp.his.web.pexam;

import com.lsp.his.db.DBOperator;
import com.lsp.his.model.ReturnValue;
import com.lsp.his.tables.Bas_dept;
import com.lsp.his.tables.Bas_dicts;
import com.lsp.his.tables.Bas_hospitals;
import com.lsp.his.tables.Bas_user;
import com.lsp.his.tables.pexam.*;
import com.lsp.his.utils.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lsp.his.utils.PageModel;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/8 23:51
 */

@Controller
@RequestMapping("/pexam")
public class PexamAction {

    private static final String Object = null;
    static List<Integer> out = new ArrayList<Integer>();

    /************************ 预约服务页面，所有相关操作(2012-12-18)开始 **************************/
    // TODO
    @RequestMapping("/show3")
    public String show3(HttpServletRequest request, HttpServletResponse response) {
        return "pexam/pexamItem";
    }

    @RequestMapping("/exptjReportPDF")
    public String exptjReportPDF(HttpServletRequest request,
                                 HttpServletResponse response) {
        return "pexam/exptjReportPDF";
    }

    @RequestMapping("/deptsumSuggestInfo")
    public String deptsumSuggestInfo(HttpServletRequest request,
                                     HttpServletResponse response) {
        return "pexam/deptsumSuggestInfo";
    }

    // 导读
    @RequestMapping("/daodu")
    public ModelAndView daodu(HttpServletRequest request,
                              HttpServletResponse response, ModelMap modelMap) throws Exception {

        String name = URLDecoder.decode(request.getParameter("name"), "utf-8");
        String sex = URLDecoder.decode(request.getParameter("sex"), "utf-8");
        if (sex.equals("男")) {
            name = name + "先生";
        } else {
            name = name + "女士";
        }
        modelMap.put("name", name);
        return new ModelAndView("pexam/daodu", modelMap);
    }

    /*
     * 预约服务页面 2012-12-05
	 */
    @SuppressWarnings("unchecked")
    @RequestMapping("/appointment")
    public ModelAndView show(HttpServletRequest request,
                             HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String isDishDept = "";// 是否区分科室参数
        String isPrintA5 = "";
        String isPrintCervical = "";
        DBOperator db = null;
        try {
            db = new DBOperator();

            // 预约信息
            String sqlmain = "select a.examid,a.examname,a.bookdate from pexam_main a where a.excdate is null and a.tjyear=to_char(sysdate,'yyyy') and a.hosnum=? and a.nodecode=? and  a.iszf is null order by a.examid desc";
            // List<Pexam_main> listmain = db.find(sqlmain,new
            // Object[]{hosnum,nodecode}, Pexam_main.class);
            // 套餐信息
            String sql = " select a.groupid,a.groupname,a.cost from pexam_items_group a where a.hosnum=? and a.nodecode=? and a.delflag='n'";
            List<Pexam_items_group> listgroup = db.find(sql, new Object[]{
                    hosnum, nodecode}, Pexam_items_group.class);
            // 单项目信息
            String sqlitems = "select a.comid,a.comname,a.cost from pexam_items_com a where a.hosnum=? and a.nodecode=?";
            List<PexamItemsCom> listitems = db.find(sqlitems, new Object[]{
                    hosnum, nodecode}, PexamItemsCom.class);
            isDishDept = ParamBuffer.getParamValue(request, basHospitals
                    .getHosnum(), basHospitals.getNodecode(), "健康体检", "是否区分科室");
            isPrintA5 = ParamBuffer.getParamValue(request, basHospitals
                    .getHosnum(), basHospitals.getNodecode(), "健康体检", "退休A5");
            isPrintCervical = ParamBuffer.getParamValue(request, basHospitals
                            .getHosnum(), basHospitals.getNodecode(), "健康体检",
                    "退休是否打印宫颈刮片");

            System.out.println("isDishDept:" + isDishDept);
            System.out.println("isPrintA5:" + isPrintA5);
            modelMap.put("isDishDept", isDishDept);
            modelMap.put("isPrintA5", isPrintA5);
            modelMap.put("isPrintCervical", isPrintCervical);
            // modelMap.put("listmain", listmain); //预约信息
            modelMap.put("listitems", listitems);
            modelMap.put("listgroup", listgroup);
            // 是否启用CRM预约
            String ifstartcrm = ParamBuffer.getParamValue(request, hosnum,
                    nodecode, "院区", "是否启用CRM预约");
            modelMap.put("ifstartcrm", ifstartcrm);
            //
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("pexam/appointment", modelMap);
    }

    public static void main(String[] args) throws Exception {
        // html2PDF();
    }

    // 第一部分 结论汇总
    @RequestMapping("/pexam_resultSumInfo")
    public ModelAndView pexam_resultSumInfo(HttpServletRequest request,
                                            HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        String pexamid = request.getParameter("pexamid");
        modelMap.put("pexamid", pexamid);
        DBOperator db = null;
        Bas_dept bd = (Bas_dept) request.getSession()
                .getAttribute("login_dept");
        Bas_hospitals bh = (Bas_hospitals) request.getSession().getAttribute(
                "login_hospital");
        Bas_user bu = (Bas_user) request.getSession()
                .getAttribute("login_user");
        String sql = "";
        List<Map> list = null;
        try {
            db = new DBOperator();
            Map map = new HashMap();
            // 这里得判断病人是完成状态还是在检状态， 2个状态查询的sql不一样的。
            sql = "select * from pexam_mans a where a.pexamid=?";
            list = db.find(sql, new Object[]{pexamid});
            String isover = list.get(0).get("isover").toString();
            // isover = "在检";
            if ("在检".equals(isover)) {
                sql = "select a.itemname,a.deptsum,a.comclass,a.excdate,a.itemuuid,a.parent_comid  from pexam_items_title a left join pexam_items_com c on a.itemcode = c.comid and c.hosnum = a.hosnum left join pexam_items_type t  on c.parentid = t.typeid   and t.hosnum = c.hosnum where a.tjxm = 'Y'  and a.hosnum = ?  and a.pexamid = ?   and a.deptsum is not null order by to_number(t.sn), to_number(a.sn)";
                list = db.find(sql, new Object[]{bh.getHosnum(), pexamid});
                if (list.size() > 0) {
                    int i = 1;
                    String deptsum = "";
                    afternameToAllName(list, db, pexamid, "1"); // 检验的简称 变成全称
                    for (int j = 0; j < list.size(); j++) {
                        Map map3 = list.get(j);
                        if ((map3.get("itemname").toString()).indexOf("肝功") > -1) {
                            map3.put("itemname", "生化检验");
                        }
                        if (map3.get("parent_comid") != null) {
                            list.remove(j);
                        }
                    }
                    for (Map map2 : list) {
                        String a = map2.get("itemname") == null ? "" : map2
                                .get("itemname").toString();
                        a = "<span class='deptsumTitle'>" + a + "</span>"
                                + "<br/>&nbsp;&nbsp;&nbsp;&nbsp;";
                        String b = map2.get("deptsum") == null ? "" : map2.get(
                                "deptsum").toString();
                        // deptsum += foematInteger(i)+"、【"+a+"】\r"+b+"\r";
                        if (Equ(b, '\r')) { // 如果最后一个字符是\r 就去掉
                            b = b.substring(0, b.length() - 1);
                        }
                        b = b
                                .replaceAll("\\r",
                                        "<br/>&nbsp;&nbsp;&nbsp;&nbsp;");
                        // b = ReplaceByReg(b,"[0-9]+(、)","");
                        if (i == list.size()) {
                            deptsum += a + b;
                        } else {
                            deptsum += a + b + "<br/><br/>";
                        }
                        i++;
                    }
                    // deptsum = deptsum.replace("\r", "<br/>");
                    modelMap.put("suminfo", deptsum);
                } else {
                    modelMap.put("suminfo", "");
                }
            } else {
                sql = "select * from pexam_deptsum a where a.hosnum=? and a.pexamid=? and a.sumtype='体检总结'";
                list = db.find(sql, new Object[]{bh.getHosnum(), pexamid});
                if (list.size() > 0) {
                    // String deptsum =
                    // list.get(0).get("deptsum")==null?"":list.get(0).get("deptsum").toString();
                    java.sql.Clob c = (java.sql.Clob) list.get(0)
                            .get("deptsum");
                    // list.get(0).put("deptsum", StrUtil.oracleClob2Str(c));
                    // //将内容 从Clob 转成String
                    modelMap.put("suminfo", StrUtil.oracleClob2Str(c));
                } else {
                    modelMap.put("suminfo", "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("pexam/pexam_resultSumInfo", modelMap);
    }

    @RequestMapping("/pexam_resultSumInfo1")
    public ModelAndView pexam_resultSumInfo1(HttpServletRequest request,
                                             HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        DBOperator db = null;
        Bas_dept bd = (Bas_dept) request.getSession()
                .getAttribute("login_dept");
        Bas_hospitals bh = (Bas_hospitals) request.getSession().getAttribute(
                "login_hospital");
        Bas_user bu = (Bas_user) request.getSession()
                .getAttribute("login_user");
        String examid = request.getParameter("examid");
        String pexamid = request.getParameter("pexamid");
        String type = request.getParameter("type");
        String node = request.getParameter("node");
        modelMap.put("pexamid", pexamid);
        try {
            db = new DBOperator();
            Map map = new HashMap();
            String sql = "select * from pexam_deptsum  a  where a.hosnum=? and a.pexamid=? and a.sumtype='健康建议'  ";
            List<Map> list = db.find(sql, new Object[]{bh.getHosnum(),
                    pexamid});
            if (ListUtil.listIsNotEmpty(list)) {
                java.sql.Clob c = (java.sql.Clob) list.get(0).get("deptsum");
                String deptsum_jkjy = "";
                deptsum_jkjy = StrUtil.oracleClob2Str(c) == null ? "" : StrUtil
                        .oracleClob2Str(c);
                // 查看这个人 有木有完成 ，有的话 结论建议增加 总检医生
                sql = "select to_char(a.completedate,'yyyy-mm-dd hh24:mi:ss') completedate,a.doctorname from pexam_deptsum a where a.pexamid=? ";
                list = db.find(sql, new Object[]{pexamid});
                String completedateStr = "";
                String completeDoctorname = "";
                if (ListUtil.listIsNotEmpty(list)) {
                    completedateStr = list.get(0).get("completedate") == null ? ""
                            : list.get(0).get("completedate").toString();
                    completeDoctorname = list.get(0).get("doctorname") == null ? ""
                            : list.get(0).get("doctorname").toString();
                    if (!"".equals(completedateStr)
                            && !"".equals(completeDoctorname)) {
                        deptsum_jkjy = deptsum_jkjy
                                + "<br/><br/><br/><span style=\"margin-left: 450px;\">总检医生："
                                + completeDoctorname
                                + "&nbsp;&nbsp;&nbsp;&nbsp;</span><br><span style=\"margin-left: 450px;\">总检时间："
                                + completedateStr + "</span>";
                    }
                }
                map.put("deptsum_jkjy", deptsum_jkjy);
            } else {
                map.put("deptsum_jkjy", "");
            }

            modelMap.put("name", "第二部分	结论及建议");
            modelMap.put("info", map.get("deptsum_jkjy").toString());

        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("pexam/pexam_resultSumInfo1", modelMap);
    }

    @RequestMapping("/pexam_resultSumInfo2")
    public ModelAndView pexam_resultSumInfo2(HttpServletRequest request,
                                             HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        DBOperator db = null;
        Bas_dept bd = (Bas_dept) request.getSession()
                .getAttribute("login_dept");
        Bas_hospitals bh = (Bas_hospitals) request.getSession().getAttribute(
                "login_hospital");
        Bas_user bu = (Bas_user) request.getSession()
                .getAttribute("login_user");
        String examid = request.getParameter("examid");
        String pexamid = request.getParameter("pexamid");
        String type = request.getParameter("type");
        String node = request.getParameter("node");
        modelMap.put("pexamid", pexamid);
        try {
            db = new DBOperator();
            Map map = new HashMap();
            String sql = "select * from PEXAM_MANS_WXTS a  where a.hosnum=? and a.pexamid=?  ";
            List<Map> list = db.find(sql, new Object[]{bh.getHosnum(),
                    pexamid});
            if (ListUtil.listIsNotEmpty(list)) {
                java.sql.Clob c = (java.sql.Clob) list.get(0).get("wxts_1");
                map.put("wxts_1", StrUtil.oracleClob2Str(c));
            } else {
                map.put("wxts_1", "");
            }

            modelMap.put("wxts", "y");
            modelMap.put("name", "温馨提示");
            modelMap.put("info", map.get("wxts_1").toString());

        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("pexam/pexam_resultSumInfo2", modelMap);
    }

    // 第二部分 结果及建议
    @RequestMapping("/pexam_resultandsuggest")
    public ModelAndView pexam_resultandsuggest(HttpServletRequest request,
                                               HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        String pexamid = request.getParameter("pexamid");
        modelMap.put("pexamid", pexamid);
        DBOperator db = null;
        Bas_dept bd = (Bas_dept) request.getSession()
                .getAttribute("login_dept");
        Bas_hospitals bh = (Bas_hospitals) request.getSession().getAttribute(
                "login_hospital");
        Bas_user bu = (Bas_user) request.getSession()
                .getAttribute("login_user");
        String sql = "";
        List<Map> list = null;
        try {
            db = new DBOperator();
            Map map = new HashMap();
            // 这里得判断病人是完成状态还是在检状态， 2个状态查询的sql不一样的。
            sql = "select * from pexam_mans a where a.pexamid=?";
            list = db.find(sql, new Object[]{pexamid});
            String isover = list.get(0).get("isover").toString();
            // isover = "在检";
            if ("在检".equals(isover)) {
                sql = "select result,sugesttext from pexam_items_sugests s,pexam_sugests t where s.sugestid=t.sugestid and s.pexamid=?  and t.sugesttext is not null  union   select s.classname,s.sugesttext from pexam_results r , pexam_sugests s  where r.pexamid=? and r.examtype='检验' and r.unnormal is not null and  r.indid=s.indid and r.unnormal=s.unnormal  ";
                list = db.find(sql, new Object[]{pexamid, pexamid});
                if (list.size() > 0) {
                    int i = 1;
                    String deptsum = "";
                    for (Map map2 : list) {
                        String a = map2.get("result") == null ? "" : map2.get(
                                "result").toString();
                        a = "<span style=\"font-size:18px;font-weight:bold;\">"
                                + a + "</span>"
                                + "<br/>&nbsp;&nbsp;&nbsp;&nbsp;";
                        String b = map2.get("sugesttext") == null ? "" : map2
                                .get("sugesttext").toString();
                        b = "<span style=\"font-size:16px;\">" + b + "</span>";
                        // deptsum += foematInteger(i)+"、【"+a+"】\r"+b+"\r";

                        if (i == list.size()) {
                            deptsum += a + b;
                        } else {
                            deptsum += a + b + "<br/><br/>";
                        }
                        i++;
                    }
                    // deptsum = deptsum.replace("\r", "<br/>");
                    modelMap.put("deptsnum", deptsum);
                } else {
                    modelMap.put("deptsnum", "");
                }
            } else {
                sql = "select * from pexam_deptsum a where a.sumtype='健康建议' and a.pexamid=? ";
                list = db.find(sql, new Object[]{pexamid});
                if (list.size() > 0) {
                    // String deptsum =
                    // list.get(0).get("deptsum")==null?"":list.get(0).get("deptsum").toString();
                    java.sql.Clob c = (java.sql.Clob) list.get(0)
                            .get("deptsum");
                    // list.get(0).put("deptsum", StrUtil.oracleClob2Str(c));
                    // //将内容 从Clob 转成String
                    String deptsum = StrUtil.oracleClob2Str(c);
                    deptsum = deptsum.replaceAll("\\<p>|</p>", "");

                    sql = "select to_char(a.completedate,'yyyy-mm-dd hh24:mi:ss') completedate,a.doctorname from pexam_deptsum a where a.pexamid=? ";
                    list = db.find(sql, new Object[]{pexamid});
                    String completedateStr = "";
                    String completeDoctorname = "";
                    if (ListUtil.listIsNotEmpty(list)) {
                        completedateStr = list.get(0).get("completedate") == null ? ""
                                : list.get(0).get("completedate").toString();
                        completeDoctorname = list.get(0).get("doctorname") == null ? ""
                                : list.get(0).get("doctorname").toString();
                        if (!"".equals(completedateStr)
                                && !"".equals(completeDoctorname)) {
                            deptsum = deptsum
                                    + "<br/><br/><br/><span style=\"margin-left: 450px;\">总检医生："
                                    + completeDoctorname
                                    + "&nbsp;&nbsp;&nbsp;&nbsp;</span><br><span style=\"margin-left: 450px;\">总检时间："
                                    + completedateStr + "</span>";
                        }
                    }
                    // 已经保存过 把<p> 和 </p> 去掉
                    // out = new ArrayList<Integer>();
                    // out = stringNumbers(deptsum,"【"); //匹配的字符的 索引
                    // StringBuffer buffer = new StringBuffer(deptsum);
                    // //根据索引进行替换
                    // int i =1 ;
                    // for (int in : out) {
                    // if(i!=1){
                    // buffer.replace(in+2, in+3, foematInteger(i)+"、【");
                    // }else{
                    // buffer.replace(in, in+1, foematInteger(i)+"、【");
                    // }
                    // i++;
                    // }
                    // //deptsum = buffer.toString();
                    // deptsum = deptsum.replace("\r", "<br/>");
                    modelMap.put("deptsnum", deptsum);
                } else {
                    modelMap.put("deptsnum", "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("pexam/pexam_resultandsuggest", modelMap);
    }

    // 最后一个字符是不是想要的字符
    // str——字符串， ch——想检查的字符
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

    /**
     * 用正则表达式 替换字符串
     */
    public static String ReplaceByReg(String str, String regex, String replace) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        String abc = matcher.replaceAll(replace);
        return abc;
    }

    // 数字 转成 中文
    private static String foematInteger(int num) {
        String[] units = {"", "十", "百", "千", "万", "十万", "百万", "千万", "亿", "十亿",
                "百亿", "千亿", "万亿"};
        char[] numArray = {'零', '一', '二', '三', '四', '五', '六', '七', '八', '九'};
        char[] val = String.valueOf(num).toCharArray();
        int len = val.length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            String m = val[i] + "";
            int n = Integer.valueOf(m);
            boolean isZero = n == 0;
            String unit = units[(len - 1) - i];
            if (isZero) {
                if ('0' == val[i - 1]) {
                    // not need process if the last digital bits is 0
                    continue;
                } else {
                    // no unit for 0
                    sb.append(numArray[n]);
                }
            } else {
                sb.append(numArray[n]);
                sb.append(unit);
            }
        }
        return sb.toString();
    }

    /**
     * @param str 输入的字符串
     * @param ch  查找的字符
     * @return 查找到的索引 存放在list中
     */
    public static List<Integer> stringNumbers(String str, String ch) {
        for (int i = 0; i < str.length(); i++) {
            i = str.indexOf(ch, i);
            if (i < 0)
                break;
            System.out.println(i);
            out.add(i);
        }
        return out;
    }

    // 第三部分 各科检查
    @RequestMapping("/everyItemsResult")
    public ModelAndView everyItemsResult(HttpServletRequest request,
                                         HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        String pexamid = request.getParameter("pexamid");
        modelMap.put("pexamid", pexamid);
        DBOperator db = null;
        Bas_dept bd = (Bas_dept) request.getSession()
                .getAttribute("login_dept");
        Bas_hospitals bh = (Bas_hospitals) request.getSession().getAttribute(
                "login_hospital");
        Bas_user bu = (Bas_user) request.getSession()
                .getAttribute("login_user");
        String sql = "";
        List<Map> list = null;
        try {
            db = new DBOperator();
            Map map = new HashMap();
            StringBuffer sb = new StringBuffer();
            // comclass='其他' //下面的sql （体检项目的参考范围取的是小项表的范围）
            sql = "select a.itemcode, a.itemname, a.excdept, a.excdeptname, a.groupid, a.comclass, to_char(a.excdate, 'yyyy-mm-dd hh24:mi:ss') excdate, a.excdoctorname, r.indid, r.indname, r.result, r.resultunit, decode(null,i.minval,'',i.minval)  || '-' || decode(null,i.maxval,'',i.maxval) range , a.deptsum from pexam_items_title a left join pexam_results r on r.itemuuid = a.itemuuid  left join pexam_items_ind i on i.indid=r.indid  left join pexam_items_com c  on a.itemcode = c.comid  left join pexam_items_type t on c.parentid = t.typeid  where a.pexamid = ? and a.tjxm='Y' and a.comclass='其他' and a.xmstatus='完成'  and c.bgxs='是' order by  to_number(t.sn) ,to_number(a.sn) , to_number(i.sn),r.indid  ";
            // sql =
            // "select a.itemcode, a.itemname,a.excdept,a.excdeptname,a.groupid,a.comclass,to_char(a.excdate,'yyyy-mm-dd hh24:mi:ss') excdate ,a.excdoctorname, i.indid,i.indname ,r.result,r.resultunit,r.range ,a.deptsum, i.tsxm  from pexam_items_title a left join pexam_items_comdet c on c.comid=a.itemcode left join pexam_items_com pi on pi.comid = a.itemcode left join pexam_items_type t on pi.parentid = t.typeid left join pexam_items_ind i on i.indid=c.indid left join pexam_results r on r.indid=i.indid and r.pexamid=a.pexamid where a.pexamid=? and a.comclass!='检验' and a.comclass!='检查' and a.comclass!='外送' and a.tjxm='Y' and i.indid is not null  order by  to_number(t.sn) ,to_number(i.sn)   ";
            list = db.find(sql, new Object[]{pexamid});
            deptsumBr(list); // 处理list里的deptsum 字段
            String vmpagckage = "com/lsp/his/template/maintenance/";
            String vm = "";
            String vmname = "zjbg_gkjc.vm";
            // 特殊处理（吴宝珠 耳鼻咽喉科 不显示 喉部和鼻咽部 小项。）
            for (int i = 0; i < list.size(); i++) {
                Map map2 = list.get(i);
                String itemname = map2.get("itemname").toString();
                String indname = map2.get("indname") == null ? "" : map2.get(
                        "indname").toString();
                if ("200024".equals(pexamid)) {
                    String itemcode = map2.get("itemcode").toString();
                    String indid = map2.get("indid").toString();
                    if ("12935".equals(indid) || "13098".equals(indid)) {
                        list.remove(i);
                    }
                }
                map2.put("itemname", itemname.replaceAll("\\(男\\)", "")); // 把类似与(男)
                // 这样格式的字符串
                // 去掉
                map2.put("itemname", itemname.replaceAll("\\(女\\)", ""));
            }
            vm = VelocityUtils.generateVm(vmpagckage, vmname, "list", list);
            if (list.size() > 0) {
                sb.append(vm);
            }
            // comclass='检验'
            sql = "select a.parent_comid,a.itemuuid ,a.itemcode, a.itemname, a.excdept, a.excdeptname, a.groupid, a.comclass, to_char(a.excdate, 'yyyy-mm-dd hh24:mi:ss') excdate, a.excdoctorname, r.indid, r.indname, r.result, r.resultunit, r.range,r.unnormal, a.deptsum from pexam_items_title a left join pexam_results r on r.itemuuid = a.itemuuid left join pexam_items_com c on a.itemcode = c.comid left join pexam_items_type t on c.parentid = t.typeid  where a.pexamid = ? and a.tjxm='Y' and a.comclass='检验' and a.xmstatus='完成' and a.parent_comid is null and c.bgxs='是'   order by  to_number(t.sn),to_number(c.sn), to_number(r.sn),r.indid ";
            list = db.find(sql, new Object[]{pexamid});
            deptsumBr(list);// 处理list里的deptsum 字段
            afternameToAllName(list, db, pexamid, ""); // 检验的简称 变成全称
            for (int j = 0; j < list.size(); j++) {
                Map map3 = list.get(j);
                if (map3.get("parent_comid") != null) {
                    list.remove(j);
                }
            }
            // System.out.println("bbbbbbbb");
            vm = VelocityUtils.generateVm(vmpagckage, vmname, "list", list);
            if (list.size() > 0) {
                sb.append(vm);
            }
            // comclass='检查'
            sql = "select a.itemcode, a.itemname, a.excdept, a.excdeptname, a.groupid, a.comclass, to_char(a.excdate, 'yyyy-mm-dd hh24:mi:ss') excdate, a.excdoctorname, r.indid, r.indname, r.result, r.resultunit, r.range, a.deptsum from pexam_items_title a left join pexam_results r on r.itemuuid = a.itemuuid left join pexam_items_com c  on a.itemcode = c.comid  left join pexam_items_type t on c.parentid = t.typeid  where a.pexamid = ? and a.tjxm='Y' and a.comclass='检查' and a.xmstatus='完成' and c.bgxs='是'  order by  to_number(t.sn),to_number(a.sn),r.indid  ";
            list = db.find(sql, new Object[]{pexamid});
            deptsumBr(list); // 处理list里的deptsum 字段
            vm = VelocityUtils.generateVm(vmpagckage, "zjbg_gkjc1.vm", "list",
                    list);
            if (list.size() > 0) {
                sb.append(vm);
            }
            modelMap.put("vm", sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("pexam/everyItemsResult", modelMap);
    }

    public static void deptsumBr(List<Map> list) throws Exception {
        for (Map map : list) {
            String deptsum = map.get("deptsum") == null ? "" : map.get(
                    "deptsum").toString();
            if (Equ(deptsum, '\r')) { // 如果最后一个字符是\r 就去掉
                deptsum = deptsum.substring(0, deptsum.length() - 1);
            }
            deptsum = deptsum.replaceAll("\\r",
                    "<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            map.put("deptsum", deptsum);
        }
    }

    public static void afternameToAllName(List<Map> list, DBOperator db,
                                          String pexamid, String type) throws Exception {
        String sql = "select a.itemuuid,a.itemname,a.parent_comid,a.tmcode,a.afterhb_name  from pexam_items_title a where a.pexamid=? and a.tjxm = 'Y' and a.comclass='检验' and a.xmstatus='完成' order by a.parent_comid ";
        if ("1".equals(type)) {
            sql = "select a.itemname ,a.parent_comid, a.itemuuid  from pexam_items_title a left join pexam_items_com c on a.itemcode = c.comid and c.hosnum = a.hosnum left join pexam_items_type t on c.parentid = t.typeid and t.hosnum = c.hosnum where a.tjxm = 'Y'  and a.pexamid = ? and a.deptsum is not null and c.bgxs='是' order by a.parent_comid ";
        }
        List<Map> tList = db.find(sql, new Object[]{pexamid});
        Map nameMap = new HashMap(); // 存放全称的map
        for (Map map : tList) {
            String itemuuid = map.get("itemuuid").toString();
            String parent_comid = map.get("parent_comid") == null ? "" : map
                    .get("parent_comid").toString();
            String itemname = map.get("itemname").toString();
            if (!"".equals(parent_comid)) {
                if (nameMap.get(parent_comid) == null) {
                    nameMap.put(parent_comid, itemname);
                } else {
                    nameMap.put(parent_comid, nameMap.get(parent_comid) + "+"
                            + itemname);
                }
            } else {
                if (nameMap.get(itemuuid) != null) {
                    nameMap.put(itemuuid, nameMap.get(itemuuid) + "+"
                            + itemname);
                }
            }
        }
        for (int i = 0; i < list.size(); i++) {
            Map map2 = list.get(i);
            String itemuuid = map2.get("itemuuid").toString();
            if (!"1".equals(type)) {
                String result = map2.get("result") == null ? "" : map2.get(
                        "result").toString(); // 检验的小项结果
                String unnormal = map2.get("unnormal") == null ? "" : map2.get(
                        "unnormal").toString(); // 检验的正常是否的 标志 unnormal
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

    // 第四部分 检验检查
    @RequestMapping("/examItemsResult")
    public ModelAndView examItems(HttpServletRequest request,
                                  HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        String pexamid = request.getParameter("pexamid");
        modelMap.put("pexamid", pexamid);
        DBOperator db = null;
        Bas_dept bd = (Bas_dept) request.getSession()
                .getAttribute("login_dept");
        Bas_hospitals bh = (Bas_hospitals) request.getSession().getAttribute(
                "login_hospital");
        Bas_user bu = (Bas_user) request.getSession()
                .getAttribute("login_user");
        String sql = "";
        List<Map> list = null;
        try {
            db = new DBOperator();
            Map map = new HashMap();
            sql = "select a.itemcode, a.itemname, a.excdept, a.excdeptname, a.groupid, a.comclass, to_char(a.excdate, 'yyyy-mm-dd hh24:mi:ss') excdate, a.excdoctorname, r.indid, r.indname, r.result, r.resultunit, r.range, a.deptsum from pexam_items_title a left join pexam_results r on r.itemuuid = a.itemuuid where a.pexamid = ? and a.tjxm='Y' and (a.comclass = '检验' or a.comclass='外送') order by a.itemcode ";
            list = db.find(sql, new Object[]{pexamid});
            String vmpagckage = "com/lsp/his/template/maintenance/";
            String vm = "";
            String vmname = "zjbg_gkjc.vm";
            vm = VelocityUtils.generateVm(vmpagckage, vmname, "list", list);
            modelMap.put("vm", vm);
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("pexam/examItemsResult", modelMap);
    }

    // 第四部分 检查检查
    @RequestMapping("/checkItemsResult")
    public ModelAndView checkItems(HttpServletRequest request,
                                   HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        String pexamid = request.getParameter("pexamid");
        modelMap.put("pexamid", pexamid);
        DBOperator db = null;
        Bas_dept bd = (Bas_dept) request.getSession()
                .getAttribute("login_dept");
        Bas_hospitals bh = (Bas_hospitals) request.getSession().getAttribute(
                "login_hospital");
        Bas_user bu = (Bas_user) request.getSession()
                .getAttribute("login_user");
        String sql = "";
        List<Map> list = null;
        try {
            db = new DBOperator();
            Map map = new HashMap();
            sql = "select a.itemcode, a.itemname,a.excdept,a.excdeptname,a.groupid,a.comclass,to_char(a.excdate,'yyyy-mm-dd hh24:mi:ss') excdate ,a.excdoctorname, i.indid,i.indname ,r.result,r.resultunit,r.range ,a.deptsum, i.tsxm  from pexam_items_title a left join pexam_items_comdet c on c.comid=a.itemcode left join pexam_items_com pi on pi.comid = a.itemcode left join pexam_items_type t on pi.parentid = t.typeid left join pexam_items_ind i on i.indid=c.indid left join pexam_results r on r.indid=i.indid and r.pexamid=a.pexamid where a.pexamid=? and a.comclass = '检查'  and a.tjxm='Y' and i.indid is not null  order by  to_number(t.sn) ,to_number(i.sn)   ";
            list = db.find(sql, new Object[]{pexamid});
            String vmpagckage = "com/lsp/his/template/maintenance/";
            String vm = "";
            String vmname = "zjbg_gkjc.vm";
            vm = VelocityUtils.generateVm(vmpagckage, vmname, "list", list);
            modelMap.put("vm", vm);
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("pexam/checkItemsResult", modelMap);
    }

    // 一般项目
    @RequestMapping("/pexamItem")
    public ModelAndView pexamItem(HttpServletRequest request,
                                  HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute(
                "login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute(
                "login_user");
        String hosnum = basHospitals.getHosnum();
        String examid = request.getParameter("examid");// 预约编号
        String pexamid = request.getParameter("pexamid");// 体检者编号
        List itemlist = null;
        Set<Map> set = new HashSet();

        DBOperator db = null;
        try {
            db = new DBOperator();
            String sql = "select p.EXCDOCTORNAME,to_char(p.EXCDATE,'yyyy-mm-dd') as excutedate,t.itemname,p.INDNAME,p.RESULT,p.resultunit,t.DEPTSUM,p.range,d.minval,d.maxval from pexam_results p left join pexam_items_title t on t.itemcode = p.comid left join pexam_items_com i on i.comid=p.comid left join pexam_items_ind d on d.indid = p.indid left join pexam_items_type tt on tt.typeid = i.parentid where t.tjxm='Y' and p.examid=? and p.pexamid = ? and (i.comclass!='检验' and i.comclass!='检查' and i.comclass!='外送') and p.examid = t.examid and p.pexamid = t.pexamid  order by to_number(tt.sn), to_number(i.sn),to_number(d.sn)";
            itemlist = db.find(sql, new Object[]{examid, pexamid});
            sql = "select distinct (p.comid),t.itemname as comname,c.sn,tt.sn,t.DEPTSUM as deptsum,t.EXCDOCTORNAME as name ,to_char(t.EXCDATE,'yyyy-mm-dd') as excutedate from pexam_results p left join pexam_items_title t on t.pexamid = p.pexamid and t.itemuuid = p.itemuuid left join pexam_items_com c on c.comid = p.comid  left join pexam_items_type tt on tt.typeid = c.parentid where p.examid=? and p.pexamid = ? and t.tjxm='Y' and (t.comclass!='检验' and t.comclass!='检查' and t.comclass!='外送') order by  to_number(tt.sn), to_number(c.sn)";
            List list = db.find(sql, new Object[]{examid, pexamid});
            /**
             * for(int i=0;i<itemlist.size();i++){ Map<String,Object> map = new
             * HashMap<String,Object>(); Object s =
             * ((Map)itemlist.get(i)).get("itemname"); Object name =
             * ((Map)itemlist.get(i)).get("excdoctorname"); Object date =
             * ((Map)itemlist.get(i)).get("excutedate"); Object DEPTSUM =
             * ((Map)itemlist.get(i)).get("deptsum"); map.put("comname",s);
             * map.put("name",name); map.put("date", date); map.put("DEPTSUM",
             * DEPTSUM); //System.out.println(map.toString()); set.add(map); }
             * // String sql2 =
             * "  select comname from pexam_results where examid.pexamid group by comname; "
             * ; // itemlist2 = db.find(sql2,new
             * Object[]{"20150523080128360","625534"}); /** for(int
             * i=0;i<itemlist.size();i++){ Map map = (Map)itemlist.get(i);
             * map.put("itemname", map.get("")); }
             */
            // Map<String,Object> map = (Map)itemlist.get(0);
            // System.out.println(set.toString());
            // System.out.println(itemlist.toString());
            modelMap.put("itemsList", itemlist);
            modelMap.put("itemcoms", list);
        } catch (Exception e) {

        } finally {
            db.freeCon();
        }

        return new ModelAndView("pexam/pexamItem", modelMap);
    }

    /**
     * 检查项目
     */
    @RequestMapping("/checkItem")
    public ModelAndView checkItem(HttpServletRequest request,
                                  HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute(
                "login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute(
                "login_user");
        String hosnum = basHospitals.getHosnum();
        String examid = request.getParameter("examid");// 预约编号
        String pexamid = request.getParameter("pexamid");// 体检者编号
        List itemlist = null;
        Set<Map> set = new HashSet();

        DBOperator db = null;
        try {
            db = new DBOperator();

            String sql = "select p.EXCDOCTORNAME,to_char(p.EXCDATE,'yyyy-mm-dd') as excutedate,t.itemname,p.INDNAME,p.RESULT,p.resultunit,t.DEPTSUM,i.comclass from pexam_results p left join pexam_items_title t on t.itemcode = p.comid left join pexam_items_com i on i.comid=p.comid  left join pexam_items_ind d on d.indid = p.indid left join pexam_items_type tt on tt.typeid = i.parentid where p.examid=? and p.pexamid = ?  and i.comclass = '检查' and t.tjxm = 'Y' and p.examid = t.examid and p.pexamid = t.pexamid order by to_number(tt.sn), to_number(i.sn),to_number(d.sn)";
            itemlist = db.find(sql, new Object[]{examid, pexamid});
            sql = "select distinct (p.comid),t.itemname as comname,c.sn,tt.sn,t.DEPTSUM as deptsum,t.EXCDOCTORNAME as name ,to_char(t.EXCDATE,'yyyy-mm-dd') as excutedate from pexam_results p left join pexam_items_title t on t.pexamid = p.pexamid and t.itemuuid = p.itemuuid left join pexam_items_com c on c.comid = p.comid  left join pexam_items_type tt on tt.typeid = c.parentid where p.examid=? and p.pexamid = ? and t.comclass='检查' and t.tjxm='Y' order by  to_number(tt.sn), to_number(c.sn)";
            List list = db.find(sql, new Object[]{examid, pexamid});
            // Map<String,Object> map = (Map)itemlist.get(0);
            System.out.println(set.toString());
            System.out.println(itemlist.toString());
            modelMap.put("itemsList", itemlist);
            modelMap.put("itemcoms", list);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.freeCon();
        }

        return new ModelAndView("pexam/checkItems", modelMap);
    }

    /**
     * 检验项目
     */
    @RequestMapping("/examineItem")
    public ModelAndView examineItem(HttpServletRequest request,
                                    HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute(
                "login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute(
                "login_user");
        String hosnum = basHospitals.getHosnum();
        String examid = request.getParameter("examid");// 预约编号
        String pexamid = request.getParameter("pexamid");// 体检者编号
        List itemlist = null;
        Set<Map> set = new HashSet();

        DBOperator db = null;
        try {
            db = new DBOperator();
            String sql = "select p.EXCDOCTORNAME,to_char(p.EXCDATE,'yyyy-mm-dd') as excutedate,t.itemname,p.INDNAME,p.RESULT,p.resultunit,t.DEPTSUM,i.comclass,p.range,p.resultunit,p.unnormal,t.itemuuid from pexam_results p left join pexam_items_title t on t.itemcode = p.comid left join pexam_items_com i on i.comid=p.comid left join pexam_items_ind d on d.indid = p.indid   left join pexam_items_type tt on tt.typeid = i.parentid where p.examid=? and p.pexamid = ?  and (i.comclass = '检验' or i.comclass='外送') and p.examid = t.examid and p.pexamid = t.pexamid and t.tjxm='Y' order by to_number(tt.sn), to_number(i.sn),to_number(d.sn)";// 组合
            itemlist = db.find(sql, new Object[]{examid, pexamid});
            sql = "select distinct (t.itemname) as itemname, t.parent_comid from pexam_results r left join pexam_items_title t on t.parent_comid = r.itemuuid where t.tjxm='Y' and t.pexamid=? and t.examid= ?";
            List<Map> list = db.find(sql, new Object[]{pexamid, examid});
            sql = "select distinct(t.itemuuid),t.itemname as comname,to_char(t.EXCDATE,'yyyy-mm-dd') as excdate,t.EXCDOCTORNAME as name,tt.sn,i.sn from pexam_results p left join pexam_items_title t on t.itemcode = p.comid and t.itemuuid = p.itemuuid left join pexam_items_com i on i.comid=p.comid left join pexam_items_type tt on tt.typeid = i.parentid  and tt.hosnum = i.hosnum where p.examid=? and p.pexamid = ?  and (i.comclass = '检验' or i.comclass='外送') and p.examid = t.examid and p.pexamid = t.pexamid and t.tjxm='Y' order by  to_number(tt.sn), to_number(i.sn)";
            List<Map> itemlist2 = db
                    .find(sql, new Object[]{examid, pexamid});// 组合结果
            for (int i = 0; i < itemlist2.size(); i++) {
                String ss = itemlist2.get(i).get("comname").toString();
                for (int j = 0; j < list.size(); j++) {
                    if (list.get(j).get("parent_comid").toString().equals(
                            itemlist2.get(i).get("itemuuid").toString())) {
                        ss = ss + "+" + list.get(j).get("itemname").toString();
                    }
                }
                if (ss.equals(itemlist2.get(i).get("comname").toString())) {
                    itemlist2.get(i).put("itemnames", "");
                } else
                    itemlist2.get(i).put("itemnames", ss);
            }
            modelMap.put("itemsList", itemlist);
            modelMap.put("itemcoms", itemlist2);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.freeCon();
        }

        return new ModelAndView("pexam/examine", modelMap);
    }

    // 分页显示 预约列表
    @RequestMapping("/showmainlist")
    public void showmainlist(HttpServletRequest request,
                             HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        DBOperator db = null;
        PrintWriter pw = null;
        String currentPage = request.getParameter("currentPage");
        String findname = request.getParameter("findname");
        int pagesize = Integer.parseInt(request.getParameter("pagesize")); // 每页大小
        if (null == currentPage || "".equals(currentPage)) {
            currentPage = "1";
        }
        try {
            db = new DBOperator();
            pw = response.getWriter();
            PageModel<Pexam_main> pageModel = this.findALL(Integer
                            .parseInt(currentPage), pagesize, hosnum, nodecode,
                    findname);
            // modelMap.put("info", pageModel);
            JSONArray jsons = JSONArray.fromObject(pageModel);
            // System.out.println(jsons.toString());
            pw.print(jsons.toString());
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            pw.print("fail");
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    /*
     * 取得预约信息--预约服务 2012-12-05
	 */
    @RequestMapping("/getmaininfo")
    public void get(HttpServletRequest request, HttpServletResponse response,
                    ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String examid = request.getParameter("examid");// 预约编号

        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "select * from pexam_main where hosnum=? and examid=?";
            List<Pexam_main> list = db.find(sql,
                    new Object[]{hosnum, examid}, Pexam_main.class);
            JSONArray jsons = JSONArray.fromObject(list);
            pw.print(jsons.toString());
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            pw.print("fail");
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    /*
     * 获取预约套餐项目 2012-12-05
	 */
    @RequestMapping("/getitemsinfo")
    public void getItems(HttpServletRequest request,
                         HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String examid = request.getParameter("examid");// 预约编号

        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "select * from pexam_items where hosnum=? and examid=? and pexamid is null";// 个人附加的套餐则pexamid不为空
            List<Pexam_items> list = db.find(sql,
                    new Object[]{hosnum, examid}, Pexam_items.class);
            JSONArray jsons = JSONArray.fromObject(list);
            pw.print(jsons.toString());
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            pw.print("fail");
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    /*
     * 人员列表条数
	 */
    @RequestMapping(value = "/loadAppPatCount", method = RequestMethod.GET)
    public void loadAppPatCount(HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String examid = request.getParameter("examid");// 预约id

        ReturnValue returnValue = new ReturnValue();
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "select count(*) as count from pexam_mans a where a.hosnum=? and a.examid=? and a.pexamid not in ( select e.patientid from crm_reserve_exam e  ) ";
            Map<String, Integer> countMap = (Map<String, Integer>) db.findOne(
                    sql, new Object[]{hosnum, examid});
            returnValue.setStatus(true);
            returnValue.setValue(String.valueOf(countMap.get("count")));
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            returnValue.setStatus(false);
            returnValue.setMessage("加载数据失败！");
        } finally {
            db.freeCon();
        }
        pw.print(JSONObject.fromObject(returnValue).toString());
        pw.flush();
        pw.close();
    }

    /*
     * 得到某个单位预约的病人列表
	 */
    @RequestMapping("/getAppPatList")
    public void getPatientByExamid(HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String method = request.getParameter("method");
        // System.out.println("<@@@@@@@@@@@@@@@@@@@@@@@@"+method);
        String examid = request.getParameter("examid");
        int pageIndex = Integer.parseInt(request.getParameter("index")
                .toString()); // 分页索引
        int pageItems = Integer.parseInt(request.getParameter("size")
                .toString()); // 每页数量
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String pagingSql1 = "select OHYEAH.* from (select OHNO.*,to_char(rownum) no from ("; // 用于分页段1
            String pagingSql2 = ") OHNO where rownum <= ?) OHYEAH where no > ?"; // 用于分页段2
            System.out.println("查找在预约列表的人员信息");
            String sql = "select a.*,to_number((floor(MONTHS_BETWEEN(sysdate,a.dateofbirth)/12)))||'' age,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid and b.excdate is not null) as fin from pexam_mans a where a.hosnum=? and a.examid=? and a.edate is null and a.pexamid not in ( select e.patientid from crm_reserve_exam e  )";
            if ("loadBySearch".equals(method)) {
                String searchvalue = request.getParameter("searchvalue");
                if (!searchvalue.equals("")) {
                    searchvalue = URLDecoder.decode(searchvalue, "UTF-8");
                }
                sql += " and (instr(a.inputcpy,'" + searchvalue.toUpperCase()
                        + "')>0 or instr(a.inputcwb,'"
                        + searchvalue.toUpperCase()
                        + "')>0 or instr(a.patname,'" + searchvalue
                        + "')>0 or instr(a.pexamid,'" + searchvalue
                        + "')>0 or instr(a.idnum,'" + searchvalue
                        + "')>0 or instr(a.ybbh,'" + searchvalue + "')>0)";
            } else if ("loadByPexamid".equals(method)) {// 根据体检编号
                String loadByPexamid = request.getParameter("loadByPexamid");
                sql += " and a.pexamid='" + loadByPexamid + "' ";
            } else if ("loadByIdNum".equals(method)) {// 根据身份证id
                String loadByIdNum = request.getParameter("loadByIdNum");
                sql += " and a.idnum='" + loadByIdNum + "' ";
            }
            String sqlQuery = pagingSql1 + sql + pagingSql2;
            List<Map> list = db.find(sqlQuery, new Object[]{hosnum, examid,
                    pageIndex * pageItems + pageItems, pageIndex * pageItems});
            // 循环list 把它的age转化为int类型的
            // for (int i=0;i<list.size();i++) {
            // Map map = list.get(i);
            // String age =
            // map.get("age").toString();//Integer.parseInt(map.get("age").toString())
            // + "";
            // System.out.println(age);
            // map.put("age", 11);
            // }

            String vmpagckage = "com/lsp/his/template/pexam/";
            String vmname = "grid_patient_appoint.vm";
            String vm = VelocityUtils.generateGridVm(vmpagckage, vmname,
                    "Patientlist", list);
            pw.print(vm);
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            pw.print("fail");
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    /*
     * 预约列表搜索
	 */
    @RequestMapping("/findappointment")
    public void findAppointment(HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String findname = request.getParameter("findname");

        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            String currentPage = request.getParameter("currentPage");
            if (null == currentPage || "".equals(currentPage)) {
                currentPage = "1";
            }
            pw = response.getWriter();
            List<Pexam_main> list = null;
            String sqlmain = "select * from pexam_main where excdate is null and tjyear=to_char(sysdate,'yyyy') and hosnum=? ";
            if (findname != null && (!"".equals(findname))) {
                sqlmain += " and examname like '%" + findname + "%'";

            }
            sqlmain += " order by examid desc";
            list = db.find(sqlmain, new Object[]{hosnum}, Pexam_main.class);
            JSONArray jsons = JSONArray.fromObject(list);
            pw.print(jsons.toString());
            db.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
    }

    // 分页函数 --lsp
    public PageModel<Pexam_main> findALL(int currentPage, int pageSize,
                                         String hosnum, String nodecode, String findname) {
        DBOperator db = null;
        List<Pexam_main> infolist = null;
        List<Map> list = null;
        String sql = "";
        String total = "";
        String pagingSql1 = "select OHYEAH.* from (select OHNO.*,rownum no from ("; // 分页段1
        String pagingSql2 = ") OHNO where rownum <= ?) OHYEAH where no > ?"; // 分页段2
        int a = (currentPage - 1) * pageSize + pageSize;
        int b = (currentPage - 1) * pageSize;
        try {
            db = new DBOperator();
            StringBuffer sb = new StringBuffer();
            if (StrUtil.strIsNotEmpty(findname)) {
                sb.append(" and to_char(a.bookdate,'yyyy-mm-dd')='" + findname
                        + "'");
            }
            sql = "select count(*) sum from pexam_main a where a.hosnum=? and a.nodecode=? and  a.iszf is null   "
                    + sb.toString() + " order by a.examid desc ";
            list = db.find(sql, new Object[]{hosnum, nodecode});
            total = list.get(0).get("sum").toString();
            sql = "select a.examid,a.examname,a.bookdate from pexam_main a where a.hosnum='"
                    + hosnum
                    + "' and a.nodecode='"
                    + nodecode
                    + "' and  a.iszf is null "
                    + sb.toString()
                    + " order by a.bookdate desc";
            String sql_all = pagingSql1 + sql + pagingSql2;
            infolist = db.find(sql_all, new Object[]{a, b});

        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        PageModel<Pexam_main> pageModel = new PageModel<Pexam_main>();
        pageModel.setCurrentPage(currentPage);
        pageModel.setList(infolist);
        pageModel.setPageSize(pageSize);
        pageModel.setTotalRecord(Integer.parseInt(total));
        return pageModel;
    }

    /*
     * 预约套餐项目保存--预约服务页面 2012-12-05
	 */
    @RequestMapping("/saveappointment")
    public void newAppointment(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String itemname = "";// 组合名称
        String itemid = "";// 组合项id
        String pexamid = "";//
        String cost = "";// 价格
        String sn = "";// 排序
        String chgcode = "";// 收费发票号
        String invoiceid = "";// 收费发票号

        // 基本信息
        String json1Str = URLDecoder.decode(request.getParameter("json1"),
                "utf-8");
        JSONArray json1 = JSONArray.fromObject(json1Str);
        JSONObject json = json1.getJSONObject(0);

        String examid = json.getString("examid");// 体检编号
        String unitname = json.getString("unitname");// 体检单位名称
        // 商业体检预约可不输入单位
        if ("".equals(unitname)) {
            unitname = "商业体检";
        }
        String examtype = json.getString("examtype");
        // String salesman = json.getString("salesman");
        String examname = json.getString("examname");// 体检名称
        String appointtype = "否";// 预约类型
        double unitprice = "".equals(json.getString("unitprice")) ? 0 : Double
                .valueOf(json.getString("unitprice"));// 单价
        double discount = "".equals(json.getString("discount")) ? 1 : Double
                .valueOf(json.getString("discount"));// 折扣率
        double totalamt = "".equals(json.getString("discamt")) ? 0 : Double
                .valueOf(json.getString("discamt"));// 总金额
        Integer examqty = "".equals(json.getString("examqty")) ? 0 : Integer
                .valueOf(json.getString("examqty"));
        Date bookdate = new Date();
        String tjyear = "";
        SimpleDateFormat sf = new SimpleDateFormat("yyyy");
        tjyear = sf.format(new Date());
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String s = json.getString("bookdate").toString();
            // 查询该预约是否已经存在
            String checkString = "select a.*,to_char(a.bookdate,'yyyy-mm-dd') bookdate1  from pexam_main a  where hosnum=? and nodecode=? and examid=?";
            List<Map> list = db.find(checkString, new Object[]{hosnum,
                    nodecode, examid});
            if (list != null && list.size() > 0) {// 存在，更新
                String date = list.get(0).get("bookdate").toString();
                // 不能修改预约日期和预约名称
                // 判断页面上传来的日期和表里的日期是否一直
                if (!s.equals(date)) {
                    throw new Exception("不能修改此预约单的预约日期！建议新建一个预约。");
                }
                String sql = "update pexam_main set hosnum=?,nodecode=?,unitname=?,"
                        + "examtype=?,unitprice=?,examqty=?,"
                        + "totalamt=?,discount=?,appointtype=?,tjyear=? where examid=?";
                // db.excute(sql,new
                // Object[]{hosnum,nodecode,unitname,examname,examtype,salesman,bookdate,
                // unitprice,examqty,totalamt,discount,examid});
                db.excute(sql, new Object[]{hosnum, nodecode, unitname,
                        examtype, unitprice, examqty, totalamt, discount,
                        appointtype, tjyear, examid});
            } else {
                checkString = "select * from pexam_main a where to_char(a.bookdate,'yyyy-mm-dd')='"
                        + s + "'  and (a.iszf!='Y'  or a.iszf is null)  ";
                list = db.find(checkString);
                if (list != null && list.size() > 0) {
                    throw new Exception("当前日期已有预约！");
                }
                String sql = "insert into pexam_main(hosnum,nodecode,examid,unitname,examname,examtype,"
                        + "bookdate,unitprice,examqty,totalamt,discount,appointtype,tjyear)values(?,?,?,?,?,?,to_date(?,'yyyy-MM-dd'),?,?,?,?,?,?)";
                db.excute(sql, new Object[]{hosnum, nodecode, examid,
                        unitname, examname, examtype, s, unitprice, examqty,
                        totalamt, discount, appointtype, tjyear});
            }

            // 体检项目数据
            String json2Str = URLDecoder.decode(request.getParameter("json2"),
                    "utf-8");
            JSONArray json2 = JSONArray.fromObject(json2Str);
            String sqld = "delete from pexam_items where hosnum=? and examid=?";
            db.excute(sqld, new Object[]{hosnum, examid});
            for (int i = 0; i < json2.size(); i++) {
                json = json2.getJSONObject(i);
                itemname = json.getString("itemname");
                itemid = json.getString("itemid");
                pexamid = "";
                cost = json.getString("cost");
                String isgroup = json.getString("isgroup");
                String excdept = "";
                String excdeptname = "";
                String forsex = "";
                String itemclass = "";
                String sqlitem = "insert into pexam_items "
                        + "(hosnum,itemid,examid,sn,pexamid,chgcode,itemname,invoiceid,cost,excdept,excdeptname,forsex,itemclass,isgroup)"
                        + "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                db.excute(sqlitem, new Object[]{hosnum, itemid, examid, sn,
                        null, chgcode, itemname, invoiceid, cost, excdept,
                        excdeptname, forsex, itemclass, isgroup});
            }
            pw.print("success");
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            pw.print("fail" + e.getMessage());
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    /*
     * 获取预约信息----预约服务2012-12-05
	 */
    @SuppressWarnings("unchecked")
    @RequestMapping("/refreshnav")
    public void refreShow(HttpServletRequest request,
                          HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String examid = request.getParameter("examid");// 预约id

        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sqlmain = "select * from pexam_main a where a.excdate is null and a.tjyear=to_char(sysdate,'yyyy') and a.hosnum=? and a.nodecode=? and (a.iszf!='Y' or a.iszf is null) order by examid desc";
            List<Pexam_main> list = db.find(sqlmain, new Object[]{hosnum,
                    nodecode}, Pexam_main.class);
            JSONArray jsons = JSONArray.fromObject(list);
            pw.print(jsons.toString());
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            pw.print("fail");
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    /*
     * 删除预约相关信息 2012-12-05
	 */
    @RequestMapping("/delappointment")
    public void delAppointment(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String examid = request.getParameter("examid");

        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            // String sqlmain =
            // "delete from pexam_main where hosnum=? and examid=?";
            String sqlmain = "update pexam_main  set iszf='Y' where hosnum=? and examid=?";
            db.excute(sqlmain, new Object[]{hosnum, examid});
            /*
             * String sql =
			 * "delete from pexam_items where hosnum=? and examid=?";
			 * db.excute(sql, new Object[] { hosnum, examid });
			 */
            /*
             * String sqlmans =
			 * "delete from pexam_mans where hosnum=? and examid=?";
			 * db.excute(sqlmans, new Object[] { hosnum, examid });
			 */
            pw.print("删除成功");
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            pw.print("fail");
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    /*
     * 录入人员和人员信息修改页面
	 */
    @RequestMapping("/newpatientpar")
    public String show_newpatientpar(HttpServletRequest request,
                                     HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosName = basHospitals.getHosname();

        String examid = request.getParameter("examid");// 预约id
        String pexamid = request.getParameter("pexamid");// 体检编号

        if (pexamid != null) {
            DBOperator db = new DBOperator();
            try {
                String sql = "select a.examid,a.pexamid,a.idtype,a.idnum,a.patname,a.dateofbirth,a.sex,a.maritalstatus,a.professional,a.inscardno from pexam_mans a where a.hosnum=? and a.examid=? and a.pexamid=?";
                List<Pexam_mans> list = db.find(sql, new Object[]{
                                basHospitals.getHosnum(), examid, pexamid},
                        Pexam_mans.class);
                if (list != null && list.size() > 0) {
                    Pexam_mans pm = list.get(0);
                    request.setAttribute("examid", pm.getExamid());
                    request.setAttribute("pexamid", pm.getPexamid());
                    request.setAttribute("idtype", pm.getIdtype());
                    request.setAttribute("idnum", pm.getIdnum());
                    request.setAttribute("patname", pm.getPatname());
                    request.setAttribute("dateofbirth", DateUtil.dateToString(
                            pm.getDateofbirth(), "yyyy-MM-dd"));
                    request.setAttribute("sex", pm.getSex());
                    request
                            .setAttribute("maritalstatus", pm
                                    .getMaritalstatus());
                    request.setAttribute("professional", pm.getProfessional());
                    request.setAttribute("inscardno", pm.getInscardno());
                }
                db.commit();
            } catch (Exception e) {
                e.printStackTrace();
                db.rollback();
            } finally {
                db.freeCon();
            }
        } else {
            DBOperator db = new DBOperator();
            try {
                String sql = "select seq_pexamid.nextval from dual";
                Map<String, Double> seq_id = (Map<String, Double>) db
                        .findOne(sql);
                request.setAttribute("pexamid", seq_id.get("nextval")
                        .intValue());
                db.commit();
            } catch (Exception ex) {
                ex.printStackTrace();
                db.rollback();
            } finally {
                db.freeCon();
            }
            request.setAttribute("examid", examid);
        }
        return "pexam/newPatient2";
    }

    /************************ 预约服务页面，所有相关操作(2012-12-18)结束 **************************/

    /************************ 接检页面，所有相关操作(2012-12-18)开始 **************************/
    // TODO
    /*
     * 接检页面 2012-12-05
	 */
    @SuppressWarnings("unchecked")
    @RequestMapping("/reception")
    public ModelAndView show_reception(HttpServletRequest request,
                                       HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String hosname = basHospitals.getHosname();
        String isDishDept = "";// 是否区分科室参数
        String isPrintA5 = "";
        String isPrintCervical = "";
        String isPrintDGT = "";
        String isPrintBarcode = "";
        String isPrintSelfInfo = "";

        DBOperator db = null;
        try {
            db = new DBOperator();
            // 获取预约体检名称
            // String sql =
            // "select * from pexam_main a where a.hosnum=? and a.tjyear=to_char(sysdate,'yyyy') order by examid asc";
            String sql = "select * from pexam_main a where a.hosnum=? and a.tjyear=to_char(sysdate,'yyyy') and a.iszf is null order by examid asc";
            // List<Pexam_main> list = db.find(sql, new Object[]
            // {hosnum},Pexam_main.class);

            // 体检项目
            String sqlitems = "select * from pexam_items_com where hosnum=? and nodecode=?";
            List<PexamItemsCom> listitems = db.find(sqlitems, new Object[]{
                    hosnum, nodecode}, PexamItemsCom.class);

            // 体检套餐
            String sqlgroup = " select * from pexam_items_group where hosnum=? and nodecode=?";
            List<Pexam_items_group> listgroup = db.find(sqlgroup, new Object[]{
                    hosnum, nodecode}, Pexam_items_group.class);
            isDishDept = ParamBuffer.getParamValue(request, basHospitals
                    .getHosnum(), basHospitals.getNodecode(), "健康体检", "是否区分科室");
            isPrintA5 = ParamBuffer.getParamValue(request, basHospitals
                    .getHosnum(), basHospitals.getNodecode(), "健康体检", "退休A5");
            isPrintCervical = ParamBuffer.getParamValue(request, basHospitals
                            .getHosnum(), basHospitals.getNodecode(), "健康体检",
                    "退休是否打印宫颈刮片");
            ;
            isPrintDGT = ParamBuffer
                    .getParamValue(request, basHospitals.getHosnum(),
                            basHospitals.getNodecode(), "健康体检", "是否打印登记表");
            isPrintBarcode = ParamBuffer.getParamValue(request, basHospitals
                            .getHosnum(), basHospitals.getNodecode(), "健康体检",
                    "是否打印检验条码");
            isPrintSelfInfo = ParamBuffer.getParamValue(request, basHospitals
                            .getHosnum(), basHospitals.getNodecode(), "健康体检",
                    "是否打印个人信息条码");

            modelMap.put("hosname", hosname);
            modelMap.put("isDishDept", isDishDept);
            modelMap.put("isPrintA5", isPrintA5);
            modelMap.put("isPrintCervical", isPrintCervical);
            modelMap.put("isPrintDGT", isPrintDGT);
            modelMap.put("isPrintBarcode", isPrintBarcode);
            modelMap.put("isPrintSelfInfo", isPrintSelfInfo);
            // modelMap.put("listmain", list);
            modelMap.put("listitems", listitems); // 预约列表
            modelMap.put("listgroup", listgroup);
            modelMap.put("hosnum", hosnum);
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("pexam/reception", modelMap);
    }

    // 获取 预约列表信息。
    @RequestMapping(value = "/findYYXX", method = RequestMethod.POST)
    public void findYYXX(HttpServletRequest request,
                         HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String findtime = request.getParameter("findtime");
        String enddate = request.getParameter("enddate");
        ReturnValue returnValue = new ReturnValue();
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "";
            StringBuffer sb = new StringBuffer();
            if (!"".equals(findtime)) {
                sb.append(" and to_char(a.bookdate,'yyyy-mm-dd')  >= '"
                        + findtime + "'");
            }
            if (!"".equals(enddate)) {
                sb.append(" and to_char(a.bookdate,'yyyy-mm-dd')  <= '"
                        + enddate + "'");
            }
            // System.out.println("查找在检和未检的人员信息及在检人员的完成情况");
            sql = "select a.examid,a.examname from pexam_main a where  1=1"
                    + sb.toString()
                    + "and (a.iszf is null or a.iszf !='Y')  order by a.bookdate";
            List<Map> list = db.find(sql);
            // System.out.println(returnValue.toString());
            pw.print(JSONArray.fromObject(list).toString());
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            pw.print("fail" + e.getMessage());
        } finally {
            db.freeCon();
        }
    }

    /*
     * 加载预约信息---接检页面 2012-12-05
	 */
    @RequestMapping("/getPexamMainInfo")
    public void getPexamMainInfo(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String examid = request.getParameter("examid");

        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sqlString = "select * from pexam_main where hosnum=? and examid=?";
            List list = db.find(sqlString, new Object[]{
                    basHospitals.getHosnum(), examid});
            JSONObject json = JSONObject.fromObject(list.get(0));
            pw.print(json.toString());
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            pw.print("查询失败");
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    /**
     * 导向通过医保查询页面
     *
     * @param request
     * @param response
     * @param modelMap
     * @throws Exception
     */
    @RequestMapping(value = "/selectbyins")
    public String selectByIns(HttpServletRequest request,
                              HttpServletResponse response, ModelMap modelMap) throws Exception {
        DBOperator db = new DBOperator();
        try {
            String sql = "select t.nekey,t.nevalue,t.contents,t.isdefault,t.inputcpy,t.inputcwb from bas_dicts t where t.hosnum = ? and t.nekey=140 and t.nevalue != '!' order by t.nevalue asc";
            List<Bas_dicts> instypeBds = db.find(sql, "0000", Bas_dicts.class);
            String _instype = "";
            modelMap.addAttribute("_instype", _instype);
            modelMap.addAttribute("instypeBds", instypeBds);
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            throw new Exception("数据库处理异常！");
        } finally {
            db.freeCon();
        }
        return "pexam/selectbyins";
    }

    /************************ 接检页面，所有相关操作(2012-12-18)结束 **************************/

    /************************ 体检医生站页面，所有相关操作(2012-12-18)开始 **************************/
    // TODO

	/*
     * 加载个人信息--体检医生站页面 2012-12-05
	 */
    @RequestMapping("/getpatientinfo")
    public void getPatientByPExamidOnRep(HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String pexamid = request.getParameter("pexamid");// 体检编号

        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "select (floor(MONTHS_BETWEEN(sysdate,a.dateofbirth)/12))  as age , a.*,"
                    + "  to_char(a.bdate,'yyyy-MM-dd') isbdate "
                    + "from "
                    + "pexam_mans a " + "where a.hosnum=? and a.pexamid=?";
            List list = db.find(sql, new Object[]{hosnum, pexamid});
            JSONArray jsons = JSONArray.fromObject(list);
            pw.print(jsons.toString());
            db.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
            pw.print("fail");
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    // TODO
    /************************ 体检医生站页面，所有相关操作(2012-12-18)结束 **************************/

    /************************ 总检医生站页面，所有相关操作(2012-12-18)开始 **************************/
    // TODO

	/*
     * 总检医生站首页
	 */
    @SuppressWarnings("unchecked")
    @RequestMapping("/maindoctorcheck")
    public ModelAndView fullreviewerofdoctor(HttpServletRequest request,
                                             HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute(
                "login_user");
        String DoubleReport = ParamBuffer.getParamValue(request, basHospitals
                .getHosnum(), basHospitals.getNodecode(), "健康体检", "总检报告是否打印两页");
        String ZJDoctor = ParamBuffer.getParamValue(request, basHospitals
                .getHosnum(), basHospitals.getNodecode(), "健康体检", "默认总检医生");
        System.out.println("=======>ZJDoctor:" + ZJDoctor);
        modelMap.put("ZJDoctor", ZJDoctor);
        // String
        // isPrintCover=ParamBuffer.getParamValue(request,basHospitals.getHosnum(),basHospitals.getNodecode(),"健康体检",
        // "是否打印总检报告封面");
        String isPrintCover = "Y";
        // String
        // PrintOneReport=ParamBuffer.getParamValue(request,basHospitals.getHosnum(),basHospitals.getNodecode(),"健康体检",
        // "打印一页总检报告");
        String PrintOneReport = "N";
        System.out.println("=======>DoubleReport:" + DoubleReport);
        modelMap.put("isPrintCover", isPrintCover);
        modelMap.put("DoubleReport", DoubleReport);
        modelMap.put("hosname", basHospitals.getHosname());
        modelMap.put("doctorname", basUser.getName());
        modelMap.put("doctorId", basUser.getId());
        modelMap.put("PrintOneReport", PrintOneReport);
        return new ModelAndView("pexam/maindoctorcheck", modelMap);
    }

    // TODO

    /************************ 体检医生站页面，所有相关操作(2012-12-18)结束 **************************/

    @RequestMapping("/newpatient")
    public String show() {
        return "pexam/newpatient";
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("/doctorstation")
    public ModelAndView show_doctorstation(HttpServletRequest request,
                                           HttpServletResponse response, ModelMap modelMap) throws Exception {
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute(
                "login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute(
                "login_user");

        DBOperator db = new DBOperator();
        try {
            String sql = "select * from pexam_main where hosnum=?  ";
            List<Pexam_main> list = db.find(sql, new Object[]{hosnum},
                    Pexam_main.class);
            System.out.println("=======>" + basUser.getName());
            modelMap.put("name", basUser.getName());
            modelMap.put("listmain", list);
            db.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("pexam/doctorstation", modelMap);
    }

    @RequestMapping("/doctorstationorgin")
    public ModelAndView show_doctorstationorgin(HttpServletRequest request,
                                                HttpServletResponse response, ModelMap modelMap) throws Exception {
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        DBOperator db = new DBOperator();
        try {
            String sql = "select * from pexam_main where hosnum=?  ";
            List<Pexam_main> list = db.find(sql, new Object[]{hosnum},
                    Pexam_main.class);
            modelMap.put("listmain", list);
            db.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("pexam/doctorstation", modelMap);
    }

    // 验证套餐中是否存在此项目
    @RequestMapping("/checkitemsingroup")
    public void checkItemsinGroup(HttpServletRequest request,
                                  HttpServletResponse response, ModelMap modelMap) throws Exception {

        String itemcode = request.getParameter("itemcode");
        String groupids = request.getParameter("groupids");
        DBOperator db = new DBOperator();
        try {
            String sql = "select * from pexam_items_groupdetails  where itemcode ='"
                    + itemcode
                    + "'  and  groupid in (?)".replace("?", groupids);
            List list = db.find(sql);
            response.setContentType("text/html;charset=utf-8");
            PrintWriter pw = null;
            pw = response.getWriter();
            if (list.size() > 0 && list != null) {
                pw.print("exist");
            } else {
                pw.print("fail");
            }
            pw.flush();
            pw.close();
            db.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
    }

    /*
     * 保存新预约--代替原来方法(saveappointment)
	 */
    @RequestMapping(value = "/saveappointment2", method = RequestMethod.POST)
    public void saveappointment2(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute(
                "login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute(
                "login_user");
        String hosnum = basHospitals.getHosnum();

        // 获取前台传过来的数据
        String json1Str = request.getParameter("json1");// 体检预约表信息
        String json2Str = request.getParameter("json2");// 体检项目表信息

        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "";
            List list = null;
            /************ 体检预约表 ***********/
            JSONArray json1 = JSONArray.fromObject(json1Str);
            JSONObject jsonObj = json1.getJSONObject(0);
            String examid = jsonObj.getString("examid");// 体检编号
            String unitname = jsonObj.getString("unitname");// 体检单位名称
            String examname = jsonObj.getString("examname");// 体检名称
            String salesman = jsonObj.getString("salesman");// 业务员
            Date bookdate = DateUtil.stringToDate(
                    jsonObj.getString("bookdate"), "yyyy-MM-dd");// 预约时间
            String unitprice = jsonObj.getString("unitprice");// 单价
            String examqty = jsonObj.getString("examqty");// 体检人数
            String discount = jsonObj.getString("discount");// 折扣率
            String discamt = jsonObj.getString("discamt");// 折扣金额
            String examtype = "职工体检";// 体检类型

            System.out.println("判断是否已经存在此预约信息");
            sql = "select * from pexam_main a where a.hosnum=? and a.examid=?";
            list = db.find(sql, new Object[]{hosnum, examid});
            if (list != null && list.size() > 0) {// 已存在记录--更新
                System.out.println("更新体检预约表");
                sql = "update pexam_main set unitname=?,examname=?,"
                        + "examtype=?,salesman=?,bookdate=?,unitprice=?,examqty=?,"
                        + "discount=?,discamt=? where hosnum=? and examid=?";
                db.excute(sql, new Object[]{unitname, examname, examtype,
                        salesman, bookdate, unitprice, examqty, discount,
                        discamt, hosnum, examid});
            } else {
                System.out.println("向体检预约表中插入一条数据");
                sql = "insert into pexam_main(hosnum,examid,unitname,examname,examtype,salesman,"
                        + "bookdate,unitprice,examqty,discount,discamt)values(?,?,?,?,?,?,?,?,?,?,?)";
                db.excute(sql, new Object[]{hosnum, examid, unitname,
                        examname, examtype, salesman, bookdate, unitprice,
                        examqty, discount, discamt});
            }

            /**************** 体检项目表 ***************/
            JSONArray json2 = JSONArray.fromObject(json2Str);
            System.out.println("删除该examid所有体检项");
            sql = "delete from pexam_items a where a.hosnum=? and a.examid=?";
            db.excute(sql, new Object[]{hosnum, examid});
            JSONObject obj = null;
            List<Object[]> pi = new ArrayList<Object[]>();
            for (int i = 0; i < json2.size(); i++) {
                obj = json2.getJSONObject(i);
                String itemid = obj.getString("itemid");
                String itemname = obj.getString("itemname");
                Double cost = obj.getDouble("cost");
                String isgroup = obj.getString("isgroup");
                pi.add(new Object[]{hosnum, itemid, examid, itemname, cost,
                        isgroup});
            }
            Object[][] params = new Object[pi.size()][2];
            for (int i = 0; i < pi.size(); i++) {
                params[i] = pi.get(i);
            }
            System.out.println("向体检项目表中插入记录");
            sql = "insert into pexam_items (hosnum,itemid,examid,itemname,cost,isgroup)values (?,?,?,?,?,?)";
            db.excuteBatch(sql, params);
            pw.print("success");
            db.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            db.rollback();
            pw.print("fail");
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    @RequestMapping("/savepatient")
    public void savePatient(HttpServletRequest request,
                            HttpServletResponse response) throws Exception {

        String patname = "";
        String sex = "";
        Date dateofbirth = null;
        String professional = "";
        String maritalstatus = "";
        String inscardno = "";
        String pexamid = "";
        String idtype = "";
        String idnum = "";
        String genexamdoctor = "";
        String doctorname = "";
        String examresult = "";
        String examsuggest = "";
        Date genexamdate = null;
        Date bdate = null;
        Date edate = null;
        String invoiceid = "";
        String comments = "";
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String sn = "";
        String examid = "";
        String id = "";

        examid = request.getParameter("examid");
        pexamid = request.getParameter("pexamid");
        String json1Str = URLDecoder.decode(request.getParameter("json1"),
                "utf-8");
        JSONArray json1 = JSONArray.fromObject(json1Str);
        JSONObject json = json1.getJSONObject(0);
        patname = json.getString("patname");
        sex = json.getString("sex");
        professional = json.getString("professional");
        maritalstatus = json.getString("maritalstatus");
        inscardno = json.getString("inscardno");
        idtype = json.getString("idtype");
        idnum = json.getString("idnum");
        dateofbirth = DateUtil.stringToDate(json.getString("dateofbirth"),
                "yyyy-MM-dd");
        DBOperator db = new DBOperator();
        PrintWriter pw = null;
        pw = response.getWriter();
        System.out.println("===============");
        System.out.println("examid=" + examid + ",pexamid=" + pexamid);
        try {
            String sql = "select * from pexam_main a where a.hosnum=? and a.examid=?";
            Map mainMap = (Map) db
                    .findOne(sql, new Object[]{hosnum, examid});
            String examtype = (String) mainMap.get("examtype");
            sql = "select * from pexam_mans where hosnum=? and pexamid=?";
            List list = db.find(sql, new Object[]{hosnum, pexamid});
            if (list.size() > 0) {
                String sqlString = "update pexam_mans set "
                        + "hosnum=?,examid=?,sn=?,idtype=?,idnum=?,inscardno=?,patname=?,sex=?,dateofbirth=?,professional=?,maritalstatus=?,g"
                        + "enexamdoctor=?,doctorname=?,examresult=?,examsuggest=?,genexamdate=?,invoiceid=?,comments=?,examtype=? where"
                        + " pexamid=?";
                db.excute(sqlString, new Object[]{hosnum, examid, sn, idtype,
                        idnum, inscardno, patname, sex, dateofbirth,
                        professional, maritalstatus, genexamdoctor, doctorname,
                        examresult, examsuggest, genexamdate, invoiceid,
                        comments, examtype, pexamid});
                pw.print("update");
            } else {
                String sqlString = " insert into pexam_mans(hosnum,examid,sn,pexamid,idtype,idnum,inscardno,patname,"
                        + "sex,dateofbirth,professional,maritalstatus,genexamdoctor,doctorname,examresult,examsuggest,genexamdate,"
                        + "bdate,edate,invoiceid,comments,examtype) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                db.excute(sqlString, new Object[]{hosnum, examid, sn,
                        pexamid, idtype, idnum, inscardno, patname, sex,
                        dateofbirth, professional, maritalstatus,
                        genexamdoctor, doctorname, examresult, examsuggest,
                        genexamdate, bdate, edate, invoiceid, comments,
                        examtype});
                pw.print("insert");
            }
            db.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
            pw.print("失败");
        } finally {
            db.freeCon();
        }
    }

    @RequestMapping(value = "/getPexamid")
    public void getPexamid(HttpServletRequest request,
                           HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "select seq_pexamid.nextval from dual";
            Map<String, Double> seq_id = (Map<String, Double>) db.findOne(sql);
            pw.print(seq_id.get("nextval").intValue());
            db.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            db.rollback();
            pw.print("fail");
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    // 得到套餐的项目
    @RequestMapping("/getitemsbygroup")
    public void getItemsByGroup(HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String groupid = request.getParameter("groupid");
        DBOperator db = new DBOperator();
        try {
            String sql = "select * from pexam_items_def t where t.itemcode in( select t.itemcode from pexam_items_groupdetails  t where t.hosnum=? and t.groupid=?)";
            List<Pexam_items_def> list = db.find(sql, new Object[]{hosnum,
                    groupid}, Pexam_items_def.class);
            response.setContentType("text/html;charset=utf-8");
            JSONArray jsons = JSONArray.fromObject(list);
            PrintWriter pw = null;
            pw = response.getWriter();
            pw.print(jsons.toString());
            db.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }

    }

    @RequestMapping("/getitembycode")
    public void getItemByCode(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String itemcode = request.getParameter("itemcode");
        DBOperator db = new DBOperator();
        try {
            String sql = "select cost,comid itemcode,comname itemname from pexam_items_com where  hosnum = ? and comid=?";
            List<Pexam_items_def> list = db.find(sql, new Object[]{hosnum,
                    itemcode}, Pexam_items_def.class);
            response.setContentType("text/html;charset=utf-8");
            JSONArray jsons = JSONArray.fromObject(list);
            PrintWriter pw = null;
            pw = response.getWriter();
            pw.print(jsons.toString());
            db.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }

    }

    // 得到某个预约信息
    @RequestMapping("/getappointmentinfo")
    public void getAppointmentinfo(HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        String examid = request.getParameter("examid");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        DBOperator db = new DBOperator();
        try {
            String sql = "select * from pexam_main where excdate is not null and hosnum=? and examid =?";
            List<Pexam_main> list = db.find(sql,
                    new Object[]{hosnum, examid}, Pexam_main.class);
            JSONArray jsons = JSONArray.fromObject(list);
            request.setAttribute("appointmentinfo", "var appointmentinfo="
                    + jsons.toString());// 预约的基本信息
            // 已选的套餐_在体检项目表中
            db.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
    }

    // 得到某个人的体检信息
    @RequestMapping("/getpatientpexaminfo")
    public void getPatientpExaminfo(HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
        String pexamid = request.getParameter("pexamid");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        DBOperator db = new DBOperator();
        try {
            String sql = "select * from pexam_mans  where hosnum=? and pexamid=?";
            List<Pexam_mans> list = db.find(sql,
                    new Object[]{hosnum, pexamid}, Pexam_mans.class);
            JSONArray jsons = JSONArray.fromObject(list);
            request.setAttribute("patientbasinfo", "var patientbasinfo="
                    + jsons.toString()); // 病人基本信息
            // 病人体检项目
            String sqlitem = "";
            db.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
    }

    // 接检页面的grid
    @RequestMapping("/getrepatientlist")
    public void getPatientByExamidOnRep(HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        String examid = request.getParameter("examid");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        DBOperator db = new DBOperator();
        try {
            // 结果表中的有记录的就已经完成
            String sql = "select * from pexam_mans where  hosnum=? and examid=? ";
            List<Pexam_mans> list = db.find(sql,
                    new Object[]{hosnum, examid}, Pexam_mans.class);
            response.setContentType("text/xml;charset=utf-8");
            PrintWriter pw = null;
            pw = response.getWriter();
            String vmpagckage = "com/lsp/his/template/pexam/";
            String vmname = "grid_patient_reception.vm";
            String vm = VelocityUtils.generateGridVm(vmpagckage, vmname,
                    "Patientlist", list);
            pw.print(vm);
            pw.flush();
            pw.close();
            db.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
    }

    // 体检医生站加载候检人名单
    @RequestMapping("/getdoctorstationpatient")
    public void getDoctorStationPatient(HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        response.setContentType("text/xml;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        DBOperator db = new DBOperator();
        try {
            String sql = "select * from pexam_mans where  hosnum=? and bdate is not null";
            List<Pexam_mans> list = db.find(sql, new Object[]{hosnum},
                    Pexam_mans.class);
            PrintWriter pw = null;
            pw = response.getWriter();
            String vmpagckage = "com/lsp/his/template/pexam/";
            String vmname = "grid_doctorstation.vm";
            String vm = VelocityUtils.generateGridVm(vmpagckage, vmname,
                    "Patientlist", list);
            pw.print(vm);
            pw.flush();
            pw.close();
            db.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }

    }

    // 开始体检
    @RequestMapping("/startexam")
    public void startExam(HttpServletRequest request,
                          HttpServletResponse response) throws Exception {
        response.setContentType("text/xml;charset=utf-8");
        System.out.println("1111111111111111111111111111111111");
        String pexamid = request.getParameter("pexamid");
        String examid = request.getParameter("examid");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        DBOperator db = new DBOperator();
        Date bDate = new Date();
        String sn = "";
        String chgcode = "";
        String invoiceid = "";
        try {
            // 更新状态
            String sql = "update pexam_mans set hosnum=?,examid=?,bdate=? where pexamid= ?";
            db.excute(sql, new Object[]{hosnum, examid, bDate, pexamid});

            // 点人的时候先根据pid去查，没有在查eid，得到默认套餐的项目
            // getpatientexaninfo

            // 插入的时候把选择的项目和pid一起插入
            JSONObject json = null;
            String json2Str = URLDecoder.decode(request.getParameter("json2"),
                    "utf-8");
            JSONArray json2 = JSONArray.fromObject(json2Str);
            // String sqld="delete from pexam_items where hosnum=? and examid=?
            // and pexamid=?";
            // db.excute(sqld,new Object[]{hosnum,examid,pexamid});
            for (int i = 0; i < json2.size(); i++) {
                json = json2.getJSONObject(i);
                String itemname = json.getString("itemname");
                String itemid = json.getString("itemid");
                String cost = json.getString("cost");
                String sqlitem = "insert into pexam_items (hosnum,itemid,examid,sn,pexamid,chgcode,itemname,invoiceid,cost) values (?,?,?,?,?,?,?,?,?)";
                db.excute(sqlitem, new Object[]{hosnum, itemid, examid, sn,
                        pexamid, chgcode, itemname, invoiceid, cost});
            }
            // 将体检指标插入体检结果表
            String sqli = "select * from pexam_items where hosnum=? and  examid=? and pexamid =? ";
            List<Pexam_items> list = db.find(sqli, new Object[]{hosnum,
                    examid, pexamid}, Pexam_items.class);
            // System.out.println("----------------!!!!!------"+
            // JSONArray.fromObject(list).toString());
            List<Object[]> pi = new ArrayList();
            for (Pexam_items p : list) {
                pi.add(new Object[]{p.getHosnum(), p.getExamid(),
                        p.getItemid(), "", "", p.getExcdept(),
                        p.getExcdeptname(), null, null, null, null, pexamid,
                        p.getItemname(), p.getItemcode()});
            }
            Object[][] params = new Object[pi.size()][2];
            for (int i = 0; i < pi.size(); i++) {
                params[i] = pi.get(i);
            }
            db
                    .excuteBatch(
                            "insert into pexam_results (hosnum,examid,detailid,rsheetno,sn,excdept,doctorid,examdate,stringvalue,"
                                    + "numvalue,result,pexamid,itemname,itemcode) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                            params);
            // db.commit();
            response.setContentType("text/html;charset=utf-8");
            PrintWriter pw = null;
            pw = response.getWriter();
            pw.print("插入成功");
            pw.flush();
            pw.close();
            db.commit();

        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }

    }

    /*
     * // 开始体检,修改为根据套餐和加项启
	 *
	 * @RequestMapping("/startexam2") public void startExam2(HttpServletRequest
	 * request,HttpServletResponse response) throws Exception {
	 * response.setContentType("text/html;charset=utf-8"); Bas_hospitals
	 * basHospitals = (Bas_hospitals)
	 * request.getSession().getAttribute("login_hospital"); Bas_dept basDept =
	 * (Bas_dept) request.getSession().getAttribute("login_dept"); Bas_user
	 * basUser = (Bas_user) request.getSession().getAttribute("login_user");
	 * String hosnum = basHospitals.getHosnum();
	 *
	 * String pexamid = request.getParameter("pexamid");//体检编号 String examid =
	 * request.getParameter("examid");//预约编号
	 *
	 * DBOperator db = null; PrintWriter pw = null; try { db = new DBOperator();
	 * pw = response.getWriter();
	 *
	 * int itemsNum = 0; String sql = ""; List list = null;
	 * System.out.println("性别过滤"); sql =
	 * "select a.sex from pexam_mans a where a.hosnum=? and a.examid=?"; list =
	 * db.find(sql,new Object[]{hosnum,examid}); String sex = "";
	 * if(list!=null&&list.size()>0){ sex =
	 * (String)((Map)list.get(0)).get("sex"); }
	 *
	 * System.out.println("更新pexam_mans表的体检开始标志字段");//日期可能由前台传递过来--占时有后台获取 sql =
	 * "update pexam_mans a set a.bdate=? where a.hosnum=? and a.pexamid=?";
	 * db.excute(sql,new Object[]{new Date(),hosnum,pexamid});
	 *
	 * System.out.println("获取个人相关的体检项目或套餐"); sql =
	 * "select * from pexam_items a where (a.hosnum=? and a.examid=? and a.pexamid is null) or (a.hosnum=? and a.examid=? and a.pexamid=?)"
	 * ; list = db.find(sql,new Object[]{hosnum,examid,hosnum,examid,pexamid});
	 * if(list!=null&&list.size()>0){ String groupids = ""; String itemcodes =
	 * ""; String itemCodesAll = ""; Map map = null; List<Object[]> pi = new
	 * ArrayList<Object[]>(); for(int i=0;i<list.size();i++){ map =
	 * (Map)list.get(i); if("y".equals(map.get("isgroup"))){ groupids += "'" +
	 * map.get("itemid") + "',"; }else{ itemcodes += "," + map.get("itemid") +
	 * "',"; } } //收集套餐下的项目--此处暂时为对套餐下有相同项目的去重处理 if(groupids.length()>0){
	 * groupids = groupids.substring(0,groupids.length()-1);
	 * System.out.println("获取组成套餐的体检项目");//有性别要求的只需在此处加个性别条件就ok sql =
	 * "select a.*,b.groupid from pexam_items_def a ,pexam_items_groupdetails b where a.hosnum=b.hosnum and a.itemcode=b.itemcode and b.groupid in (?)"
	 * .replace("?", groupids); sql += " and a.hosnum=?"; if(!"".equals(sex)){
	 * sql += " and (a.forsex='不限' or a.forsex='" + sex + "')"; } list =
	 * db.find(sql,new Object[]{hosnum}); if(list!=null&&list.size()>0){ for(int
	 * i=0;i<list.size();i++){ map = (Map)list.get(i); String itemcode =
	 * (String)map.get("itemcode"); String itemname =
	 * (String)map.get("itemname"); String groupid = (String)map.get("groupid");
	 * itemCodesAll += "'" + itemcode + "',"; pi.add(new Object[]{hosnum,new
	 * UUIDGenerator
	 * ().generate().toString(),examid,pexamid,itemcode,itemname,map
	 * .get("excdept"),map.get("excdeptname"),groupid}); } } } //查找单项体检项目的详细信息
	 * if(itemcodes.length()>0){ itemcodes =
	 * itemcodes.substring(0,itemcodes.length()-1); sql = "" } }
	 *
	 * JSONObject json = null; String json2Str =
	 * URLDecoder.decode(request.getParameter("json2"),"utf-8"); JSONArray json2
	 * = JSONArray.fromObject(json2Str); String groupdids = "";
	 *
	 * for(int i = 0; i < json2.size(); i++){ json = json2.getJSONObject(i);
	 * String itemname = json.getString("itemname"); String itemid =
	 * json.getString("itemid"); String cost = json.getString("cost"); String
	 * isgroup = json.getString("isgroup"); String sqlitem = ""; if
	 * (isgroup.equals("y")) {// taocan groupdids += "'" + itemid + "'" + ","; }
	 * sqlitem =
	 * "insert into pexam_items (hosnum,itemid,examid,sn,pexamid,chgcode,itemname,invoiceid,cost) values (?,?,?,?,?,?,?,?,?)"
	 * ; db.excute(sqlitem, new
	 * Object[]{hosnum,itemid,examid,sn,pexamid,chgcode,
	 * itemname,invoiceid,cost}); }
	 *
	 * groupdids = groupdids.substring(0, groupdids.length() - 1); String
	 * sqlgString =
	 * "select *  from pexam_items_def a ,pexam_items_groupdetails b where a.itemcode=b.itemcode and b.groupid in (?)"
	 * .replace("?", groupdids); List list2 = db.find(sqlgString);
	 * List<Object[]> pi2 = new ArrayList(); Map m = new HashMap(); String
	 * itemCodeStr = "";
	 *
	 * for (int i2 = 0; i2 < list2.size(); i2++) { m = (Map) list2.get(i2);
	 * String sqlNex = "select clc_recipeno_seq.nextval from dual"; List<Map>
	 * temp = db.find(sqlNex); pi2.add(new Object[] { hosnum, examid,
	 * m.get("itemcode"),temp.get(0).get("nextval"),"","", null, null, null,
	 * null,pexamid, m.get("itemname"), m.get("itemcode"),m.get("itemclass")});
	 * itemCodeStr += "'" + m.get("itemcode") + "',"; }
	 *
	 * if(itemCodeStr.length()>0){ itemCodeStr = itemCodeStr.substring(0,
	 * itemCodeStr.length()-1); String sql1 =
	 * "select  a.itemcode,a.itemname,b.detailid from pexam_items_def a,pexam_items_details b where a.itemcode=b.parentid and a.hosnum=? and a.itemcode in ("
	 * + itemCodeStr +") and b.hosnum=a.hosnum"; List<Map> temp =
	 * db.find(sql1,new Object[]{hosnum}); for(int i=0;i<temp.size();i++){ Map
	 * map = temp.get(i); pi2.add(new
	 * Object[]{hosnum,examid,map.get("detailid"),
	 * "","","",null,null,null,null,pexamid
	 * ,map.get("itemname"),map.get("itemcode"),""}); } }
	 *
	 * Object[][] params2 = new Object[pi2.size()][2]; for (int i = 0; i <
	 * pi2.size(); i++) { params2[i] = pi2.get(i); }db.excuteBatch(
	 * "insert into pexam_results(hosnum,examid,detailid,rsheetno,sn,doctorid,examdate,stringvalue,numvalue,result,pexamid,itemname,itemcode,examtype) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
	 * ,params2); db.commit(); } catch (Exception e) { e.printStackTrace();
	 * db.rollback(); pw.print("fail"); } finally { db.freeCon(); }
	 * pw.print("插入成功"); pw.flush(); pw.close(); }
	 */
    // 根据科室和pexaid得到某个病人在这个科室需要检验的项目
    @RequestMapping(value = "/getitemsbydeptpatient", method = RequestMethod.POST)
    public void getItemsBydeptpatient(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String excdept = "内科";
        // System.out.println(excdept);
        String pexamid = request.getParameter("pexamid");
        System.out.println("pexamid" + pexamid);
        DBOperator db = new DBOperator();
        try {
            String sql = "select * from pexam_results where  hosnum=? and excdept =?  and pexamid =? order by sn ";
            List<Pexam_results> list = db.find(sql, new Object[]{hosnum,
                    excdept, pexamid}, Pexam_results.class);
            JSONArray jsons = JSONArray.fromObject(list);
            PrintWriter pw = null;
            pw = response.getWriter();
            pw.print(jsons.toString());
            pw.flush();
            pw.close();
            db.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }

    }

    // 得到体检医生站的树
    @RequestMapping(value = "/getitemsbydeptpatient2", method = RequestMethod.POST)
    public void getItemsBydeptpatient2(HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String excdept = "内科";
        // System.out.println(excdept);
        String pexamid = request.getParameter("pexamid");
        DBOperator db = new DBOperator();
        try {
            String sql = "select * from pexam_results where  hosnum=? and excdept =?  and pexamid =? order by sn ";
            List<Pexam_results> list = db.find(sql, new Object[]{hosnum,
                    excdept, pexamid}, Pexam_results.class);
            JSONArray jsons = JSONArray.fromObject(list);
            PrintWriter pw = null;
            pw = response.getWriter();
            pw.print(jsons.toString());
            pw.flush();
            pw.close();
            db.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }

    }

    /*
     * 加载某人的体检套餐及项目
	 */
    @RequestMapping("/getpatientexaninfo")
    public void getPatientExanInfo(HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String examid = request.getParameter("examid");// 预约单位编号--为空，则是个人体检
        String pexamid = request.getParameter("pexamid");// 体检人员编号

        DBOperator db = new DBOperator();
        PrintWriter pw = null;
        try {
            String sqli = "select * from pexam_items a where a.hosnum=? and a.examid=? and (a.pexamid=? or a.pexamid is null)";
            List<Pexam_items> list = db.find(sqli, new Object[]{hosnum,
                    examid, pexamid}, Pexam_items.class);
            JSONArray jsons = JSONArray.fromObject(list);
            pw = response.getWriter();
            pw.print(jsons.toString());

            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            pw.print("fail");
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    // 根据项目代码得到指标项目和字典
    @RequestMapping(value = "/exam", method = RequestMethod.GET)
    public void loadexamdetails(HttpServletRequest request,
                                HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        String itemcode = request.getParameter("itemcode");
        DBOperator db = new DBOperator();
        try {
            // List<Object> dataList=new ArrayList<Object>();
            String sql = " select * from  pexam_items_details where itemcode=?";
            List<Pexam_items_details> list = db.find(sql,
                    new Object[]{itemcode}, Pexam_items_details.class);
            // dataList.add(list);

            for (Pexam_items_details p : list) {
                String detailString = "";
                String dhosnum = "0000";
                String nekey = p.getUsedict();
                if (nekey != null && nekey != "") {

                    String sqld = "select * from bas_dicts where hosnum=? and nekey =? order by nevalue";
                    List<Bas_dicts> listb = db.find(sqld, new Object[]{
                            dhosnum, nekey}, Bas_dicts.class);
                    for (Bas_dicts b : listb) {
                        detailString += b.getContents() + ",";
                    }
                } else {
                    detailString += "无,有,";
                }
                p.setDetails(detailString.substring(0,
                        detailString.length() - 1));
            }
            JSONArray jsons = JSONArray.fromObject(list);
            PrintWriter pw = null;
            pw = response.getWriter();
            pw.print(jsons.toString());
            pw.flush();
            pw.close();
            db.commit();

        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }

    }

    // 在项目明细表中得到指标项目
    @RequestMapping(value = "/createexam", method = RequestMethod.GET)
    public void loadexamdetails2(HttpServletRequest request,
                                 HttpServletResponse response, ModelMap modelMap) throws Exception {
        String dhosnum = "0000";
        response.setContentType("text/html;charset=utf-8");
        String itemcode = request.getParameter("itemcode");
        String pexamid = request.getParameter("pexamid");
        DBOperator db = new DBOperator();
        try {
            // List<Object> dataList=new ArrayList<Object>();
            String sql2 = "select * from  pexam_items_details t,pexam_results b  where t.parentid=? and b.pexamid=? and t.detailid=b.detailid order by t.detailid";
            List<Pexam_items_details> list = db.find(sql2, new Object[]{
                    itemcode, pexamid}, Pexam_items_details.class);
            if (list.size() > 0 && list != null) {
            } else {
                String sql = " select * from  pexam_items_details where parentid=? order by detailid";
                list = db.find(sql, new Object[]{itemcode},
                        Pexam_items_details.class);
            }
            for (Pexam_items_details p : list) {
                String nekey = p.getUsedict();
                if (nekey != null && nekey != "") {
                    String detailString = "";
                    String sqld = "select * from bas_dicts where hosnum=? and nekey =? and nevalue!='!'  order by nevalue ";
                    List<Bas_dicts> listb = db.find(sqld, new Object[]{
                            dhosnum, nekey}, Bas_dicts.class);
                    for (Bas_dicts b : listb) {
                        detailString += b.getContents() + ",";
                    }
                    p.setDetails(detailString.substring(0, detailString
                            .length() - 1));
                } else {
                    p.setDetails("");
                }
            }
            JSONArray jsons = JSONArray.fromObject(list);
            System.out.println("========================" + jsons.toString());
            PrintWriter pw = null;
            pw = response.getWriter();
            pw.print(jsons.toString());
            pw.flush();
            pw.close();
            db.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }

    }

    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    public void loadExamine(HttpServletRequest request,
                            HttpServletResponse response, ModelMap modelMap) throws Exception {

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = null;
        pw = response.getWriter();
        // String tree = "";
        DBOperator dbo = new DBOperator();
        String sql = "select * from pexam_items_def where itemcode >=18 ";
        List<Pexam_items_def> list = dbo.find(sql, Pexam_items_def.class);
        dbo.commit();
        dbo.freeCon();
        List<String> lstTree = new ArrayList<String>();
        int i = 1;
        String temp = "";
        String s1 = "{id:05, pId:0, name:\"孕前检查项目\" , open:true}";
        lstTree.add(s1);
        if (lstTree.size() != 0) {
            for (Pexam_items_def pi : list) {
                i++;
                String id = pi.getItemcode();
                String name = pi.getItemname();
                String pid = pi.getParentid();
                temp = "{id:" + id + ",pId:" + pid + ",name:\"" + name + "\"}";
                System.out.println(temp);
                lstTree.add(temp);
            }
        }
        pw.print(JSONArray.fromObject(lstTree).toString());
    }

    @RequestMapping(value = "/createtree", method = RequestMethod.GET)
    public void loadExamineTree(HttpServletRequest request,
                                HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute(
                "login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute(
                "login_user");

        String examid = request.getParameter("examid");// 体检编号
        String pexamid = request.getParameter("pexamid");// 个人体检号
        String maincheck = request.getParameter("maincheck");// 是否是总检医生站

        PrintWriter pw = null;
        DBOperator dbo = null;

        pw = response.getWriter();
        dbo = new DBOperator();

        String sql = "select * from pexam_results where examid=? and pexamid=?";
        List<Pexam_results> list = dbo.find(sql,
                new Object[]{examid, pexamid}, Pexam_results.class);
        String prlist = "";
        for (Pexam_results c : list) {
            prlist += "'" + c.getDetailid() + "'" + ",";
        }
        List<Pexam_items_def> listp = null;
        if (prlist.length() > 0) {
            prlist = prlist.substring(0, prlist.length() - 1);
            String sqld = " select * from pexam_items_def  where  itemcode in (?) "
                    .replace("?", prlist);
            listp = dbo.find(sqld, Pexam_items_def.class);
        }

        List<String> lstTree = new ArrayList<String>();
        int i = 1;
        String temp = "";
        // String s1 = "{id:0, pId:99, name:\"内科体检项目\" , open:true}";
        String s1 = "{id:0, pId:99, name:\"" + "体检项目\" , open:true}";
        lstTree.add(s1);
        // 查询健康体检字典 获取默认设置
        String sqltj = "";
        sqltj = "select * from BAS_PARMS where scope='健康体检' and hosnum=? and nodecode =? ";
        Map tjszMap = new HashMap();
        tjszMap = (Map) dbo.findOne(sqltj, new Object[]{
                basHospitals.getHosnum(), basHospitals.getNodecode()});
        String parmvalue = "Y";

        if (tjszMap != null && !tjszMap.isEmpty()) {
            parmvalue = (String) tjszMap.get("parmvalue");
        }
        dbo.commit();
        dbo.freeCon();
        if ("Y".equals(maincheck)) {
            for (Pexam_items_def pi : listp) {
                i++;
                String id = pi.getItemcode();
                String name = pi.getItemname();
                String pid = pi.getParentid();
                temp = "{id:\"" + id + "\",pId:\"" + pid + "\",name:\"" + name
                        + "\"}";
                lstTree.add(temp);
            }
        } else {
            if (parmvalue.equals("Y")) {
                for (Pexam_items_def pi : listp) {
                    if (pi.getExcdeptname().contains((basDept.getDeptname()))) { // 过滤
                        i++;
                        String id = pi.getItemcode();
                        String name = pi.getItemname();
                        String pid = pi.getParentid();
                        temp = "{id:\"" + id + "\",pId:\"" + pid + "\",name:\""
                                + name + "\"}";
                        lstTree.add(temp);
                    }
                }

            } else {
                for (Pexam_items_def pi : listp) {
                    i++;
                    String id = pi.getItemcode();
                    String name = pi.getItemname();
                    String pid = pi.getParentid();
                    temp = "{id:\"" + id + "\",pId:\"" + pid + "\",name:\""
                            + name + "\"}";
                    lstTree.add(temp);
                }
            }

        }
        // 若过来的页面为总检的页面，则显示总检报告
        if ("Y".equals(maincheck)) {
            String resultcheck = "{id:\"" + 66 + "\",pId:\"" + 0
                    + "\",name:\"总检报告\"}";
            lstTree.add(resultcheck);
        }
        pw.print(JSONArray.fromObject(lstTree).toString());
    }

    @RequestMapping("/saveitemdetails")
    public void saveItemDetails(HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute(
                "login_dept");
        String hosnum = basHospitals.getHosnum();
        String examid = request.getParameter("examid");
        String pexamid = request.getParameter("pexamid");
        String result2 = request.getParameter("result");
        String itemcode = request.getParameter("itemcode");
        String itemname = request.getParameter("itemname");
        Date examdate = new Date();
        String excdept = basDept.getDeptcode();
        String doctorid = "";

        response.setContentType("text/html;charset=utf-8");
        String json2Str = URLDecoder.decode(request.getParameter("json2"),
                "utf-8");
        JSONArray json2 = JSONArray.fromObject(json2Str);
        DBOperator db = new DBOperator();
        try {
            List<Object[]> insertlist = new ArrayList();
            List<Object[]> updatelist = new ArrayList();
            for (int i = 0; i < json2.size(); i++) {
                JSONObject json = json2.getJSONObject(i);
                String detailid = json.getString("name");
                String rsheetno = "";
                String sn = "";
                String stringvalue = "";
                String numvalue = "";
                String result = json.getString("value");
                String sqlIsEx = "select * from pexam_results a where a.hosnum=? and a.examid=? and a.pexamid=? and a.detailid=?";
                List list = db.find(sqlIsEx, new Object[]{hosnum, examid,
                        pexamid, detailid});
                if (list != null && list.size() > 0) {
                    updatelist.add(new Object[]{excdept, examdate, result,
                            hosnum, examid, pexamid, detailid});
                } else {
                    insertlist.add(new Object[]{hosnum, examid, detailid,
                            rsheetno, sn, excdept, doctorid, examdate,
                            stringvalue, numvalue, result, pexamid, itemname,
                            itemcode});
                }
            }

            //
            Object[][] deleteparams = new Object[updatelist.size()][2];
            for (int i = 0; i < updatelist.size(); i++) {
                deleteparams[i] = updatelist.get(i);
            }
            db
                    .excuteBatch(
                            "update pexam_results a set a.excdept=?,a.examdate=?,a.result=? where a.hosnum=? and a.examid=? and a.pexamid=? and a.detailid=?",
                            deleteparams);

            // 批量插入
            Object[][] insertparams = new Object[insertlist.size()][2];
            for (int i = 0; i < insertlist.size(); i++) {
                insertparams[i] = insertlist.get(i);
            }
            db
                    .excuteBatch(
                            "insert into pexam_results (hosnum,examid,detailid,rsheetno,sn,excdept,doctorid,examdate,stringvalue,numvalue,result,pexamid,itemname,itemcode) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                            insertparams);

            // 先删除后插入，防止重复插入
            String sql = "delete from pexam_deptsum where hosnum=? and examid=? and pexamid=? and doctorid is null";
            db.excute(sql, new Object[]{hosnum, examid, pexamid});
            String sqldeptString = "insert into pexam_deptsum (hosnum,examid,excdept,deptsum,doctorid,examdate,pexamid) values (?,?,?,?,?,?,?)";
            db.excute(sqldeptString, new Object[]{hosnum, examid, excdept,
                    result2, doctorid, examdate, pexamid});
            db.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
    }

    // 医生小结自动提示
    @RequestMapping("/autosuggest")
    public void autoSuggest(HttpServletRequest request,
                            HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String detailid = request.getParameter("detailid");// 小项编码
        String result = request.getParameter("result");// 小项体检结果
        String pexamid = request.getParameter("pexamid");//
        String examid = request.getParameter("examid");
        DBOperator db = new DBOperator();
        PrintWriter pw = null;
        pw = response.getWriter();
        try {
            String sqli = "select * from pexam_items_details where hosnum=? and  detailid=? ";
            List<Pexam_items_details> list = db.find(sqli, new Object[]{
                    hosnum, detailid}, Pexam_items_details.class);
            String suggest = "";
            Pexam_items_details p = list.get(0);
            Double minval = p.getMinval();// 下限
            Double maxval = p.getMaxval();// 上限
            String minpromp = p.getMinpromp();// 下限提示
            String maxpromp = p.getMaxpromp();// 上限提示
            Boolean unnormal = false;
            if (minval != null && maxval != null) {
                double d = Double.parseDouble(result);
                if (minval > d) {
                    suggest = minpromp;
                    unnormal = true;
                }
                if (maxval < d) {
                    suggest = maxpromp;
                    unnormal = true;
                }
                if (unnormal) {// 如果他的指标有问题的，则把这一条记录中的UNNORMAL = "Y"
                    db
                            .excute(
                                    "update pexam_results set unnormal = ? where hosnum=? and examid = ? and detailid = ? and pexamid=? ",
                                    new Object[]{"Y", hosnum, examid,
                                            detailid, pexamid});
                    pw.print(suggest);
                } else {
                    pw.print("");
                }
            }
            db.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    /**
     * 得到体检医生的信息
     */

    @RequestMapping("/getdeptSum")
    public void getdeptSum(HttpServletRequest request,
                           HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        String examid = request.getParameter("examid");
        String pexamid = request.getParameter("pexamid");

        String type = request.getParameter("type") == null ? "" : request
                .getParameter("type");// 总共有三种类型类型。TOTAL,SUGGESTION，NULL
        DBOperator db = new DBOperator();
        try {
            String sqldString = "select * from pexam_deptsum where examid=? and pexamid=?"
                    + " and doctorid= ?";
            List<Pexam_deptsum> list2 = null;
            if ("".equals(type)) {
                list2 = db
                        .find(
                                "select * from pexam_deptsum where examid=? and pexamid=? and doctorid is null",
                                new String[]{examid, pexamid},
                                Pexam_deptsum.class);
            } else {
                list2 = db.find(sqldString, new String[]{examid, pexamid,
                        type}, Pexam_deptsum.class);
            }
            String deptsumString = "";
            if (list2.size() > 0) {
                deptsumString = list2.get(0).getDeptsum();
            }
            PrintWriter pw = null;
            pw = response.getWriter();
            pw.print(deptsumString);// 把存储在数据库中的数据替换掉
            pw.flush();
            pw.close();
            db.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
    }

    // 套餐下的项目
    @RequestMapping("/getgroupitems")
    public void getGroupItems(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        response.setContentType("text/xml;charset=utf-8");
        // 这个处理待改下
        String groupids = "'" + request.getParameter("groupids") + "'";

        DBOperator db = new DBOperator();
        // Date bDate= new Date();
        try {
            String sqlgString = "select *  from pexam_items_def a ,pexam_items_groupdetails b where a.itemcode=b.itemcode and b.groupid in (?)"
                    .replace("?", groupids);
            List<Pexam_items_def> showList = db.find(sqlgString,
                    Pexam_items_def.class);
            response.setContentType("text/html;charset=utf-8");
            PrintWriter pw = null;
            pw = response.getWriter();
            // String vmpagckage = "com/cpinfo/his/template/pexam/";
            // String vmname = "grid_items_details.vm";
            // String vm = VelocityUtils.generateGridVm(vmpagckage,
            // vmname,"showList",showList);
            // pw.print(vm);
            JSONArray jsons = JSONArray.fromObject(showList);
            pw.print(jsons.toString());
            System.out.println("++++++++++++++++>" + jsons.toString());
            pw.flush();
            pw.close();
            db.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }

    }

    // 套餐下的项目2
    @RequestMapping("/getgroupitemspop")
    public void getGroupItemspop(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        response.setContentType("text/xml;charset=utf-8");
        // 这个处理待改下
        String groupids = "'" + request.getParameter("groupids") + "'";

        DBOperator db = new DBOperator();
        // Date bDate= new Date();
        try {
            String sqlgString = "select *  from pexam_items_def a ,pexam_items_groupdetails b where a.itemcode=b.itemcode and b.groupid in (?)"
                    .replace("?", groupids);
            List list = db.find(sqlgString);
            String retString = "";

            Map m = new HashMap();
            for (int i = 0; i < list.size(); i++) {
                m = (Map) list.get(i);
                String name = (String) m.get("itemname");
                retString += name + ",";
            }

            retString = retString.substring(0, retString.length() - 1);
            response.setContentType("text/html;charset=utf-8");
            PrintWriter pw = null;
            pw = response.getWriter();
            pw.print(retString);
            pw.flush();
            pw.close();
            db.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }

    }

    @RequestMapping("/delnewpatient")
    public void delnewpatient(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String examid = request.getParameter("examid");
        String pexamid = request.getParameter("pexamid");
        DBOperator db = new DBOperator();
        PrintWriter pw = null;
        try {
            String sql = "delete from pexam_mans where hosnum=? and examid=? and pexamid=?";
            db.excute(sql, new Object[]{hosnum, examid, pexamid});
            response.setContentType("text/html;charset=utf-8");
            pw = response.getWriter();
            pw.print("删除成功");
            db.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
            pw.print("删除失败");
        } finally {
            db.freeCon();
        }
    }

    @RequestMapping("/importPersonnel")
    public ModelAndView show_importPersonnel(HttpServletRequest request,
                                             HttpServletResponse response, ModelMap modelMap) throws Exception {
        String examid = request.getParameter("examid");
        String operatorResult = request.getParameter("operatorResult");
        String method = request.getParameter("method");
        modelMap.put("method", method);
        modelMap.put("examid", examid);
        modelMap.put("operatorResult", operatorResult);
        return new ModelAndView("pexam/importPersonnel");
    }

    /**
     * 得到医院编号，examid，套餐id，封装成string
     *
     * @param request
     * @param response
     * @param modelMap
     * @return
     * @throws Exception
     */
    @RequestMapping("/importPersonne2")
    public ModelAndView show_importPersonne2(HttpServletRequest request,
                                             HttpServletResponse response, ModelMap modelMap) throws Exception {
        String examid = request.getParameter("examid");
        DBOperator db = null;
        try {
            db = new DBOperator();
            String itemid = "";
            String sql = "select * from pexam_main where  examid=? ";
            List<Pexam_main> list = db.find(sql, new Object[]{examid},
                    Pexam_main.class);
            String sqll = "select * from pexam_items where  examid=? ";
            List<Pexam_items> li = db.find(sqll, new Object[]{examid},
                    Pexam_items.class);
            for (int i = 0; i < li.size(); i++) {
                itemid += li.get(i).getItemid() + ",";
            }
            String[] ites = itemid.split(",");
            // System.out.println("importPersonne2=========>"+list.get(0).getExamname());
            // modelMap.put("examid", list);
            Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                    .getAttribute("login_hospital");
            String hosnum = basHospitals.getHosnum();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            formatter.format(new Date());
            String[] str = {hosnum, list.get(0).getExamid()};
            for (int i = 0; i < ites.length; i++) {
                // 在原有string数组上增加信息
                str = Arrays.copyOf(str, str.length + 1);
                str[str.length - 1] = "'" + ites[i].toString() + "'";
            }
            request.getSession().setAttribute("new_pex", str);
            // System.out.println("str======>"+str.length);
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("pexam/importPersonne2");
    }

    @RequestMapping("/saveImportPersonal")
    public void saveImportPersonal(HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String jsonStr = URLDecoder.decode(request.getParameter("json"),
                "utf-8");
        JSONArray jsonArr = JSONArray.fromObject(jsonStr);
        JSONObject json = null;
        DBOperator db = null;
        PrintWriter pw = null;
        System.out.println(jsonArr.toString());
        try {
            db = new DBOperator();
            pw = response.getWriter();
            for (int i = 0; i < jsonArr.size(); i++) {
                json = jsonArr.getJSONObject(i);
                String sqlString = "insert into pexam_mans (hosnum,examid,sn,pexamid,idtype,idnum,inscardno,patname,sex,dateofbirth,professional,maritalstatus,"
                        + "genexamdoctor,doctorname,examresult,examsuggest,genexamdate,bdate,edate,invoiceid,comments) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                db.excute(sqlString, new Object[]{
                        hosnum,
                        json.getString("examid"),
                        "",
                        json.getString("pexamid"),
                        json.getString("idtype"),
                        json.getString("idnum"),
                        json.getString("inscardno"),
                        json.getString("patname"),
                        json.getString("sex"),
                        DateUtil.stringToDate(json.getString("dateofbirth"),
                                "yyyy-MM-dd"), json.getString("professional"),
                        json.getString("maritalstatus"), "", "", "", "", "",
                        "", "", "", ""});
            }
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            pw.print("插入成功");
        } finally {
            db.freeCon();
        }
    }

    // 获取体检建议列表
    @RequestMapping(value = "/getdiseaseList")
    public String getDtMainList(HttpServletRequest request,
                                HttpServletResponse response) {
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String hosname = basHospitals.getHosname();
        String nodecode = basHospitals.getNodecode();
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            List<String> tree = new ArrayList<String>();
            String sql1 = "";
            String temp = "";
            sql1 = "select a.sugestid,a.classname,a.parentid,a.hosnum from  pexam_sugests a  where a.hosnum=? and a.nodecode=? and a.parentid='0000'  order by a.sn ";
            List<Map> treelist = db.find(sql1,
                    new Object[]{hosnum, nodecode});
            for (Map map : treelist) {
                String id = map.get("sugestid").toString();
                String name = map.get("classname").toString();
                String pid = map.get("parentid").toString();
                String hosnum1 = map.get("hosnum").toString();
                String tree_id = hosnum1 + "_" + id;
                // temp = "{id:\"" +tree_id + "\"," + "pId:\""+ hosnum1+"_"+pid+
                // "\",name:\"" + name+"\",open:false,isParent:true}";
                temp = "{id:\"" + id + "\"," + "pId:\"" + pid + "\",name:\""
                        + name + "\",open:false,isParent:true}";
                tree.add(temp);
            }
            JSONArray jsons = JSONArray.fromObject(tree);
            // System.out.println(jsons.toString());
            request.setAttribute("treeNodes", "var treeNodes="
                    + jsons.toString());
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            request.setAttribute("fail", "fail");
        } finally {
            try {
                db.freeCon();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "pexam/diseaseList";
    }

    /**
     * ********************* getadvicesList**获取医嘱
     * *****************************************
     */
    @RequestMapping(value = "/getadvicesList", method = RequestMethod.POST)
    public void getaddressList(HttpServletRequest request,
                               HttpServletResponse response) {
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();

        PrintWriter pw = null;
        DBOperator db = null;
        String sugid = request.getParameter("sugid");
        String parentid = request.getParameter("parentid");
        // String ispar =request.getParameter("ispar");
        String sql = "select s.sugestid,s.classname,s.pybm,s.sugesttext,s.parentid,to_char(s.cdate,'yyyy-mm-dd') as cdate,s.cman,s.sn from pexam_sugests s where s.hosnum=? and sugestid='"
                + sugid + "' order by s.sugestid,s.sn ";
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String temp = "";
            List<Map> backsugest = db.find(sql, new Object[]{hosnum});
            /*
             * String id = backsugest.get(0).getSugestid(); //
			 * System.out.println(id); String sql2 =
			 * "select *from pexam_sugests where parentid=" + id;
			 * List<Pexam_sugests> backsugest2 = db.find(sql2,
			 * Pexam_sugests.class);
			 */
            // System.out.println(backsugest2.size());

            JSONArray jsons = JSONArray.fromObject(backsugest);
            System.out.println(jsons.toString());
            request.setAttribute("sugests", jsons.toString());
            pw.print(jsons.toString());

            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            request.setAttribute("fail", "fail");

        } finally {
            db.freeCon();
        }
        // return "pexam/diseaseList";
    }

    /**
     * ****************************************************
     */
    @RequestMapping(value = "/getsomelike", method = RequestMethod.POST)
    public void getsomelike(HttpServletRequest request,
                            HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        PrintWriter pw = null;
        DBOperator db = null;
        String sel_1 = request.getParameter("sel");
        String sel = "";
        if (sel_1.length() > 0) {
            // sel=sel_1.toLowerCase();
            sel = sel_1.toUpperCase();
        }
        // 获取搜索类型
        String schtype = request.getParameter("schtype");
        // System.out.print(sel+"+++++++++-----------------"+schtype);
        // String sql =
        // "select s.sugestid,s.classname,s.pybm,s.sugesttext,s.parentid,to_char(s.cdate,'yyyy-mm-dd') as cdate,s.cman,s.sn from pexam_sugests s where s.hosnum=?  and (classname like '%"
        // + sel+ "%' or pybm like '%"+sel+"%')  order by s.sn " ;
        String sql = "select s.sugestid,s.classname,s.pybm,s.sugesttext,s.parentid,to_char(s.cdate,'yyyy-mm-dd') as cdate,s.cman,s.sn from pexam_sugests s where (s.hosnum=? or s.hosnum='0000')  and (classname like '%"
                + sel + "%' or pybm like '%" + sel + "%')  order by s.sn ";

        try {
            pw = response.getWriter();
            db = new DBOperator();
            String temp = "";
            List<Map> backsugest = db.find(sql, new Object[]{hosnum});
            List<Map> backsugest2 = new ArrayList<Map>();
            String ids = "";
            if (backsugest != null && backsugest.size() > 0) {
                for (int i = 0; i < backsugest.size(); i++) {
                    if (i == (backsugest.size() - 1)) {
                        ids += "'" + backsugest.get(i).get("sugestid") + "'";
                    } else {
                        ids += "'" + backsugest.get(i).get("sugestid") + "',";
                    }
                }
                String sql1 = "select s.sugestid,s.classname,s.pybm,s.sugesttext,s.parentid,to_char(s.cdate,'yyyy-mm-dd') as cdate,s.cman,s.sn from pexam_sugests s where s.hosnum=? and s.nodecode=? and parentid in ("
                        + ids + ") order by s.sugestid,s.sn";
                backsugest2 = db.find(sql1, new Object[]{hosnum, nodecode});
            }

            // System.out.println(ids+"+++++++++++++++++++");

            if (schtype.equals("mhsch")) {
                JSONArray jsons = JSONArray.fromObject(backsugest);
                System.out.println(jsons.toString());
                request.setAttribute("sugests", jsons.toString());
                pw.print(jsons.toString());
            } else {
                JSONArray jsons2 = JSONArray.fromObject(backsugest2);
                System.out.println(jsons2.toString());
                request.setAttribute("sugests", jsons2.toString());
                pw.print(jsons2.toString());
            }
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            request.setAttribute("fail", "fail");

        } finally {
            db.freeCon();
        }
        // return "pexam/diseaseList";
    }

    /*
     *
	 * 更新(OR 插入)总检报告的结果
	 */
    @RequestMapping(value = "/modifymaindoctorcheck", method = RequestMethod.POST)
    public void updateunnormalvalue(HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
        String pexamid = request.getParameter("pexamid");
        String examid = request.getParameter("examid");
        String detailid = request.getParameter("detailid");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();// 得到医院的ID
        DBOperator db = new DBOperator();
        Date bDate = new Date();

        String deptsum = request.getParameter("deptsum")
                .replace("\n", "<br />");// 更新输入的内容
        String excdept = request.getParameter("excdept");
        String result = request.getParameter("result");
        String suggestion = request.getParameter("suggestion").replace("\n",
                "<br />");
        PrintWriter pw = response.getWriter();
        response.setContentType("text/html;charset=utf-8");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        try {
            // 更新状态
            String sql = "select * from PEXAM_RESULT_VIEW  "
                    + "WHERE pexamid= ? AND examid = ? "
                    + "AND hosnum = ? AND excdept = ? "
                    + "AND doctorid = 'total'";
            List<Pexam_results> list = db.find(sql, new Object[]{pexamid,
                    examid, hosnum, excdept}, Pexam_result_view.class);
            String sql2 = "INSERT INTO  pexam_deptsum(deptsum,hosnum,examid,pexamid,excdept,doctorid) VALUES(?,?,?,?,?,'total')";
            String sql3 = "DELETE FROM pexam_deptsum WHERE hosnum = ? AND examid = ? AND pexamid = ? ";
            String sql4 = "INSERT INTO  pexam_deptsum(deptsum,hosnum,examid,pexamid,excdept,doctorid)VALUES(?,?,?,?,?,'suggestion')";
            db.excute(sql3, new Object[]{hosnum, examid, pexamid});
            db.excute(sql4, new Object[]{suggestion, hosnum, examid, pexamid,
                    excdept}); // 插入医生的建议
            db.excute(sql2, new Object[]{deptsum, hosnum, examid, pexamid,
                    excdept});
            pw.print("插入成功");
            pw.flush();
            pw.close();
            db.commit();

        } catch (Exception e) {
            pw.print("插入失败");
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }

    }

    /*
     * 显示建议
	 */
    @RequestMapping("/getSuggestion")
    public void getSuggestion(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        response.setContentType("text/html;charset=utf-8");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        String pexamid = request.getParameter("pexamid");
        String examid = request.getParameter("examid");
        String hosnum = basHospitals.getHosnum();
        PrintWriter pw = response.getWriter();
        DBOperator db = new DBOperator();
        try {
            String sql2 = "SELECT * FROM pexam_deptsum WHERE pexamid = ? AND hosnum = ? AND examid = ? AND doctorid = 'suggestion'";
            // String sql2 =
            // "SELECT * FROM pexam_deptsum WHERE pexamid = ? AND hosnum = ? AND examid = ?";
            List<Pexam_deptsum> ptsumlist = db.find(sql2, new Object[]{
                    pexamid, hosnum, examid}, Pexam_deptsum.class);
            /*
			 * 若已经生成 DOCTORID字段为TOTOAL的记录。则直接显示
			 */
            // convertstringUtil convert = new convertstringUtil();
            // ptsumlist.get(0).setDeptsum(convert.unescape(ptsumlist.get(0).getDeptsum()));
            JSONArray jsons = JSONArray.fromObject(ptsumlist);
            // System.out.println("getUnnormalinfo===" +jsons.toString());
            pw.write(jsons.toString());
            pw.flush();
            pw.close();
            db.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }

    }

    // 显示是异常指标项目
    @RequestMapping("/getUnnormalinfo")
    public void getUnnormalinfo(HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        response.setContentType("text/html;charset=utf-8");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);/* 禁用缓存 */
        String pexamid = request.getParameter("pexamid");
        String examid = request.getParameter("examid");
        String hosnum = basHospitals.getHosnum();
        PrintWriter pw = response.getWriter();
        DBOperator db = new DBOperator();
        try {
            String sql3 = "SELECT a.detailname, b.result,b.detailid,b.EXCDEPT,"
                    + "(SELECT detailname FROM pexam_items_details c WHERE c.detailid=a.parentid)"
                    + "AS dept,"
                    + "(SELECT d.DEPTSUM FROM pexam_deptsum d WHERE b.pexamid = d.pexamid AND b.hosnum = d.hosnum AND  "
                    + "b.examid = d.examid  AND b.excdept = d.excdept AND d.DOCTORID NOT IN ('suggestion')) AS deptsum "
                    + "FROM pexam_items_details a , pexam_results b "
                    + "WHERE a.detailid = b.detailid AND  b.unnormal='Y'  AND  b.pexamid =? AND b.hosnum = ?  AND b.examid = ?";

            String sql = "SELECT A .detailname, b.result, b.detailid, b.EXCDEPT,"
                    + " ( SELECT detailname FROM pexam_items_details c WHERE c.detailid = A .parentid )"
                    + " AND b.excdept = D .excdept AND D .DOCTORID NOT IN ('suggestion')) AS deptsum FROM pexam_items_details A, pexam_results b "
                    + "WHERE A .detailid = b.detailid AND b.unnormal = 'Y' AND b.pexamid =?  AND b.hosnum =? AND b.examid =?";

            String sql2 = "SELECT * FROM pexam_deptsum WHERE pexamid = ? AND hosnum = ? AND examid = ? AND doctorid = 'total'";

            List psList = db.find(sql3,
                    new Object[]{pexamid, hosnum, examid});
            List<Pexam_deptsum> ptsumlist = db.find(sql2, new Object[]{
                    pexamid, hosnum, examid}, Pexam_deptsum.class);
            /**
             * 若已经生成 DOCTORID字段为TOTOAL的记录。则直接显示
             */
            // convertstringUtil convert = new convertstringUtil();
            if (ptsumlist.size() > 0) {
                // ptsumlist.get(0).setDeptsum(convert.unescape(ptsumlist.get(0).getDeptsum()));
                JSONArray jsons = JSONArray.fromObject(ptsumlist);
                // System.out.println("getUnnormalinfo===" +jsons.toString());
                pw.write(jsons.toString());
                pw.flush();
                pw.close();
                db.commit();
            } else {
                JSONArray jsons = JSONArray.fromObject(psList);
                // System.out.println("getUnnormalinfo===" +jsons.toString());
                pw.write(jsons.toString());
                pw.flush();
                pw.close();
                db.commit();
            }

        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
    }

    @RequestMapping("/getdetSum")
    public void getdetSum(HttpServletRequest request,
                          HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String pexamid = request.getParameter("pexamid");
        String examid = request.getParameter("examid");
        DBOperator db = new DBOperator();
        PrintWriter pw = response.getWriter();
        try {
            String sql = "select a.deptsum from pexam_deptsum a where a.hosnum=? and a.examid=? and a.pexamid=?";
            List list = db.find(sql, new Object[]{hosnum, examid, pexamid});
            pw.print(((Map) list.get(0)).get("deptsum"));
            db.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            db.rollback();
            pw.print("fail");
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    @RequestMapping("/getUnnormalinfo2")
    public void getUnnormalinfo2(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String pexamid = request.getParameter("pexamid");
        String examid = request.getParameter("examid");
        String hosnum = basHospitals.getHosnum();
        PrintWriter pw = response.getWriter();
        DBOperator db = new DBOperator();
        try {
            List data = new ArrayList();
            List temp = new ArrayList();
            String sql = "select a.itemname,a.itemcode,a.result,b.detailname from pexam_results a,pexam_items_details b where a.hosnum=? and a.examid=? and a.pexamid=? and a.unnormal='Y' and b.detailid=a.detailid";
            String sql2 = "select distinct a.itemcode,a.itemname from pexam_results a where a.hosnum=? and a.examid=? and a.pexamid=? and a.unnormal='Y' ";
            List list = db.find(sql, new Object[]{hosnum, examid, pexamid});
            List list2 = db
                    .find(sql2, new Object[]{hosnum, examid, pexamid});

            JSONArray jsons = JSONArray.fromObject(list);
            System.out.println(jsons.toString());
            pw.print(jsons.toString());
            db.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            db.rollback();
            pw.print("fail");
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    // 取得检查结果的号码
    @RequestMapping("/getRsheetno")
    public void getRsheetno(HttpServletRequest request,
                            HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute(
                "login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute(
                "login_user");
        String hosnum = basHospitals.getHosnum();

        String itemcode = request.getParameter("itemcode");
        String pexamid = request.getParameter("pexamid");
        String examid = request.getParameter("examid");

        DBOperator db = null;
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "select a.rsheetno from pexam_results a where a.hosnum=? and a.examid=? and a.pexamid=? and a.detailid=?";
            List list = db.find(sql, new Object[]{hosnum, examid, pexamid,
                    itemcode});
            pw.print(JSONArray.fromObject(list).toString());
            db.commit();
        } catch (RuntimeException e) {
            pw.print("fail");
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    @RequestMapping("/getPeopleList")
    public void getPeopleList(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        String code = request.getParameter("code");
        code = code.toUpperCase();
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "select a.xm,a.sfzjh,a.sbbh,a.xb,a.dz from tjryxx a where a.option1 like '%"
                    + code + "%'";
            List list = db.find(sql);
            JSONArray jsons = JSONArray.fromObject(list);
            pw.print(jsons.toString());
            db.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
            pw.print("fail");
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    @RequestMapping("/getdict")
    public void getdict(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType("text/html;charset=utf-8");

        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "select t.nekey,t.nevalue,t.contents,t.isdefault,t.inputcpy,t.inputcwb from bas_dicts t where t.hosnum = ? and t.nekey in(4,5,6,34) and t.nevalue != '!' order by t.nevalue asc";
            @SuppressWarnings("unchecked")
            List<Bas_dicts> bds = db.find(sql, "0000", Bas_dicts.class);

            Map<String, List<Bas_dicts>> map = new HashMap<String, List<Bas_dicts>>();

            List<Bas_dicts> idtypeBds = new ArrayList<Bas_dicts>();// 证件类别
            List<Bas_dicts> maritalstatusBds = new ArrayList<Bas_dicts>();// 婚姻状况
            List<Bas_dicts> professionalBds = new ArrayList<Bas_dicts>();// 职业
            List<Bas_dicts> sexBds = new ArrayList<Bas_dicts>();// 性别

            String _idtype = "";
            String _maritalstatus = "";
            String _professional = "";
            String _sex = "";
            for (Bas_dicts bd : bds) {
                if (bd.getNekey() == 4) {
                    idtypeBds.add(bd);
                    if ("Y".equals(bd.getIsdefault())) {
                        _idtype = bd.getContents();
                    }
                } else if (bd.getNekey() == 5) {
                    maritalstatusBds.add(bd);
                    if ("Y".equals(bd.getIsdefault())) {
                        _maritalstatus = bd.getContents();
                    }
                } else if (bd.getNekey() == 6) {
                    professionalBds.add(bd);
                    if ("Y".equals(bd.getIsdefault())) {
                        _professional = bd.getContents();
                    }
                } else if (bd.getNekey() == 34) {
                    sexBds.add(bd);
                    if ("Y".equals(bd.getIsdefault())) {
                        _sex = bd.getContents();
                    }
                }
            }

            map.put("idtypeBds", idtypeBds);
            map.put("maritalstatusBds", maritalstatusBds);
            map.put("sexBds", sexBds);
            map.put("professionalBds", professionalBds);
            db.commit();
            pw.print(JSONArray.fromObject(map).toString());
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
    }

    @RequestMapping(value = "/dict/{dictType}", method = RequestMethod.GET)
    public void loadDicts(@PathVariable("dictType") String dictType,
                          HttpServletRequest request, HttpServletResponse response,
                          ModelMap modelMap) throws Exception {
        DBOperator db = new DBOperator();
        try {
            int nekey = 0;
            if ("idtype".equals(dictType)) {// 证件类别
                nekey = 4;
            } else if ("sex".equals(dictType)) {// 性别
                nekey = 34;
            } else if ("professional".equals(dictType)) {// 职业
                nekey = 6;
            } else if ("maritalstatus".equals(dictType)) {// 婚姻状况
                nekey = 5;
            }

            String sql = "select t.nevalue,t.contents,t.inputcpy,t.inputcwb from bas_dicts t where t.hosnum = ? and t.nekey = ? and t.nevalue != '!' order by t.nevalue asc";
            @SuppressWarnings("unchecked")
            List<Bas_dicts> bds = db.find(sql, new Object[]{"0000", nekey},
                    Bas_dicts.class);

            StringBuilder sb = new StringBuilder("");
            sb.append("[");
            for (Bas_dicts bd : bds) {
                if (sb.length() != 1) {
                    sb.append(",");
                }
                sb.append("{'nevalue':'");
                sb.append(bd.getContents());
                sb.append("','contents':'");
                sb.append(bd.getContents());
                sb.append("','inputcpy':'");
                sb.append(bd.getInputcpy());
                sb.append("','inputcwb':'");
                sb.append(bd.getInputcwb());
                sb.append("'}");
            }
            sb.append("]");

            db.commit();

            response.setContentType("text/html;charset=utf-8");
            PrintWriter pw = response.getWriter();
            pw.print(sb.toString());
            pw.flush();
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
    }

    /**
     * 获取体检类型
     *
     * @param request
     * @param response
     * @param examid
     */
    @RequestMapping(value = "/getExamType", method = RequestMethod.GET)
    private void getExamType(HttpServletRequest request,
                             HttpServletResponse response, String examid) {
        response.setContentType("text/html;charset=utf-8");
        DBOperator db = null;
        String sql = "";
        try {
            db = new DBOperator();
            PrintWriter pw = response.getWriter();
            sql = "select * from pexam_main t where t.examid = ?";
            Map map = (Map) db.findOne(sql, examid);
            if (map == null) {
                pw.print("");
            } else {
                pw.print(map.get("examtype"));
            }
            pw.flush();
            pw.close();
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }
    }

    /**
     * 获取农民体检报告格式所需数据
     *
     * @param request
     * @param response
     * @param pexamid
     * @param examid
     */
    @RequestMapping(value = "/getFarmExamResult", method = RequestMethod.GET)
    private void getFarmExamResult(HttpServletRequest request,
                                   HttpServletResponse response, String pexamid, String examid) {
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        response.setContentType("text/html;charset=utf-8");
        DBOperator db = null;
        String sql = "";
        try {
            db = new DBOperator();
            PrintWriter pw = response.getWriter();
            sql = "select t1.result,t2.resultunit,t2.detailname,t2.defaultv from pexam_results t1,pexam_items_details t2 where t1.detailid=t2.detailid and t1.hosnum = ? and t1.examid = ? and t1.pexamid = ?";
            List<Map> list = db.find(sql, new Object[]{hosnum, examid,
                    pexamid});
            System.out.println(JSONArray.fromObject(list));
            pw.print(JSONArray.fromObject(list));
            pw.flush();
            pw.close();
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }
    }

    /**
     * 体检预约人员XML文件上传
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/file_upload", method = RequestMethod.POST)
    public void fileUpload(HttpServletRequest request,
                           HttpServletResponse response) throws Exception {
        // System.out.println("=======================>>>>>>file_upload...");
        response.setContentType("text/html;charset=utf-8");
        String imgPath1 = request.getSession().getServletContext().getRealPath(
                "/");
        String imgPath = imgPath1;
        imgPath += "upload\\Pexam\\";
        String returnPath = "upload\\Pexam\\";
        File imgFile = null;
        imgFile = new File(imgPath);
        if (!imgFile.exists()) {
            imgFile.mkdirs();
        }
        DiskFileItemFactory fac = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(fac);
        upload.setHeaderEncoding("utf-8");
        List fileList = null;
        fileList = upload.parseRequest(request);
        Iterator<FileItem> it = fileList.iterator();
        while (it.hasNext()) {
            FileItem item = it.next();
            if (null != item && null != item.getName()) {
                String imgName = item.getName(); // 原始的文件名
                imgFile = new File(imgPath + imgName);
                if (!imgFile.exists()) { // 文件不存在
                    item.write(imgFile);
                    returnPath += imgName;
                } else { // 文件存在
                    String fName = imgName.substring(0, imgName
                            .lastIndexOf('.'));// 获取名称
                    String xName = imgName.substring(imgName.lastIndexOf('.')); // 获取后缀
                    String newImgName = fName + "_0" + xName;
                    imgFile = new File(imgPath + newImgName);
                    int num = 1;
                    while (imgFile.exists()) {
                        newImgName = fName + "_" + num + xName;
                        imgFile = new File(imgPath + newImgName);
                        num++;
                    }
                    item.write(imgFile);
                    returnPath += newImgName;
                }
            }
        }

        PrintWriter pw = response.getWriter();
        OfflinePexamAction op = new OfflinePexamAction();
        // 得到医院编号、examid、套餐id，并解析文件
        Object[] str = (Object[]) request.getSession().getAttribute("new_pex");
        System.out.println(str[0].toString());
        op.offlinePexam(imgPath1 + returnPath, str);
        pw.print(returnPath);
        pw.flush();
        pw.close();
    }

    /**
     * 在上传体检人员预约表时，检查是否里面有人员
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public void check(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType("text/html;charset=utf-8");
        // System.out.println("check..........");
        String examid = request.getParameter("examid");
        DBOperator db = new DBOperator();
        PrintWriter pw = null;
        pw = response.getWriter();
        try {
            String sqldString = "select * from pexam_main where examid=" + "'"
                    + examid + "'";
            List<Pexam_main> main = db.find(sqldString, Pexam_main.class);
            if (main.isEmpty()) {
                pw.print("no");
            } else {
                pw.print("yes");
            }
            pw.flush();
            pw.close();
            db.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
    }

    /*
	 * 体检报告封面数据处理
	 */
    @RequestMapping("/frontCover")
    public ModelAndView show1(HttpServletRequest request,
                              HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String examid = request.getParameter("examid");
        String pexamid = request.getParameter("pexamid");
        DBOperator db = null;
        try {
            db = new DBOperator();
            String sql = "select f_getage(a.dateofbirth) age ,to_char(a.printtime,'yyyy-mm-dd') printtime1,  a.* from pexam_mans a where a.hosnum=? and a.examid=? and a.pexamid=?";
            List<Pexam_mans> list = db.find(sql, new Object[]{hosnum, examid,
                    pexamid}, Pexam_mans.class);
            modelMap.put("pexamMans", list.get(0));

            String sql1 = "select * from pexam_main b where b.examid=?";
            List<Pexam_main> list1 = db.find(sql1, new Object[]{examid},
                    Pexam_main.class);
            String sql2 = "select to_char(EXAMDATE,'yyyy-mm-dd') as datestr from  pexam_deptsum where examid = ? and pexamid = ?";
            List<Object> list2 = db
                    .find(sql2, new Object[]{examid, pexamid});
            String dateStr = "";
            String date = "";
            if (list2.size() > 0) {
                dateStr = ((Map) list2.get(0)).get("datestr").toString();
                String[] dateArray = dateStr.split("-");
                date = dateArray[0] + "年" + dateArray[1] + "月" + dateArray[2]
                        + "日";
            }
            modelMap.put("datestr", date);
            modelMap.put("pexamMain", list1.get(0));
            modelMap.put("hosname", basHospitals.getHosname());
            // ===========放置几个 页面需要的值 =========================
            modelMap.put("pexamid", pexamid);
            modelMap.put("name", list.get(0).getPatname());
            modelMap.put("sex", list.get(0).getSex());
            modelMap.put("age", list.get(0).getAge() + "岁");
            modelMap.put("address", list.get(0).getAddress());
            modelMap.put("wordaddress", list.get(0).getWordaddress()); // 工作单位
            // 打印日期
            modelMap.put("printtime", list.get(0).getPrinttime1() == null ? ""
                    : list.get(0).getPrinttime1());

        } catch (Exception ex1) {
            ex1.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("pexam/frontCover", modelMap);
    }

    /*
	 * 体检明细指标
	 */
    @RequestMapping("/pexamItemsDetailValue")
    public ModelAndView pexamItemsDetailValue(HttpServletRequest request,
                                              HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute(
                "login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute(
                "login_user");
        String hosnum = basHospitals.getHosnum();

        String examid = request.getParameter("examid");
        String pexamid = request.getParameter("pexamid");
        System.out.println("========>其他体检项目");
        DBOperator db = null;
        try {
            db = new DBOperator();
            String bdate = "";
            List itemsList = new ArrayList();// 保存所有异常指标项
            System.out.println("获取“其他”体检项目");// 如果有多个套餐且套餐之间有相同的体检项目--就会出现相同的体检项目
            String sql = "select b.*,a.excdate,a.excdoctorname from pexam_items_title a,pexam_items_com b where a.hosnum=b.hosnum and a.itemcode=b.comid and a.hosnum=? and a.examid=? and a.pexamid=? and (b.comclass!='检验' and b.comclass!='检查' and b.comclass!='外送') order by b.sn";
            List itemsDefList = db.find(sql, new Object[]{hosnum, examid,
                    pexamid});
            if (itemsDefList != null && itemsDefList.size() > 0) {

                sql = "select * from pexam_mans a where a.hosnum=? and a.pexamid=? and a.examid=?";
                List list = db.find(sql,
                        new Object[]{hosnum, pexamid, examid});
                Map map = (Map) list.get(0);
                bdate = DateUtil.dateToString((Date) map.get("bdate"),
                        "yyyy-MM-dd");

                System.out.println("获取类型是“其他”的体检项目指标");
                sql = "select b.indname as detailname,b.resultunit,a.comid,a.result from pexam_results a,pexam_items_ind b,pexam_items_com c where a.hosnum=b.hosnum and a.indid=b.indid and a.hosnum=? and a.examid=? and a.pexamid=? and a.hosnum=c.hosnum and c.comid=a.comid and (c.comclass!='检验' and c.comclass!='检查' and c.comclass!='外送') order by b.sn";
                List itemsDetList = db.find(sql, new Object[]{hosnum, examid,
                        pexamid});
                for (int i = 0; i < itemsDefList.size(); i++) {// 体检项
                    Map itemsMap = new HashMap();// 保存一个体检项目
                    List<Map> temp = new ArrayList<Map>();// 体检项目下的异常指标

                    Map itemsDefMap = (Map) itemsDefList.get(i);
                    String itemcode = (String) itemsDefMap.get("comid");// 体检项目id
                    String itemname = (String) itemsDefMap.get("comname");// 体检项目名称
                    for (int j = 0; j < itemsDetList.size(); j++) {// 项目指标
                        Map itemsDetMap = (Map) itemsDetList.get(j);
                        String itemcode2 = (String) itemsDetMap.get("comid");
                        if (itemcode.equals(itemcode2)) {
                            if (null == itemsDetMap.get("result")) {
                                itemsDetMap.put("resultunit", "");
                            }
                            temp.add(itemsDetMap);
                        }
                    }
                    itemsMap.put("itemname", itemname);// 项目名称
                    itemsMap.put("itemsDet", temp);// 指标明细集合
                    itemsMap.put("excdate", itemsDefMap.get("excdate"));// 体检日期--暂时不用这个
                    itemsMap.put("excdoctorname", itemsDefMap
                            .get("excdoctorname"));// 体检医生
                    if (null == itemsDefMap.get("excdoctorname")) {
                        itemsMap.put("bdate", "");// 体检日期
                    } else {
                        itemsMap.put("bdate", bdate);// 体检日期
                    }
                    itemsList.add(itemsMap);
                }
            }

            // 检验检查
            List itemsList1 = new ArrayList();// 保存所有异常指标项
            System.out.println("获取“检验”或“检查”或“外送”体检项目");// 如果有多个套餐且套餐之间有相同的体检项目--就会出现相同的体检项目
            String sql1 = "select a.itemuuid,a.itemcode,a.itemname,to_char(a.excdate, 'yyyy-mm-dd') as excdate,a.excdoctorname,to_char(a.checkdate, 'yyyy-mm-dd') as checkdate,a.checkdoctorname,c.comclass"
                    + " from pexam_items_title a,pexam_items_com c"
                    + " where a.hosnum = ? and a.pexamid = ? and a.itemcode=c.comid"
                    + " and (c.comclass = '检验' or c.comclass='检查' or c.comclass='外送')"
                    + " order by c.sn";
            List itemsDefList1 = db
                    .find(sql1, new Object[]{hosnum, pexamid});
            if (itemsDefList1 != null && itemsDefList1.size() > 0) {

                System.out.println("获取类型是“检验”或“检查”或“外送”的体检项目指标");

                sql1 = "select a.indname,a.resultunit,a.comid,a.itemuuid,a.result,a.range,a.stringvalue,a.unnormal from pexam_results a, pexam_items_com c "
                        + " where a.hosnum = ? and a.pexamid = ? and a.comid = c.comid and (c.comclass = '检验' or c.comclass='检查' or c.comclass='外送') ";

                // -----只显示检验异常项目-----
                // sql1 =
                // "select a.indname,a.resultunit,a.comid,a.itemuuid,a.result,a.range,a.stringvalue,a.unnormal from pexam_results a, pexam_items_com c "
                // +" where a.hosnum = ? and a.pexamid = ? and a.comid = c.comid and (c.comclass = '检验' or c.comclass='检查') and a.unnormal is not null ";

                List itemsDetList1 = db.find(sql1, new Object[]{hosnum,
                        pexamid});
                for (int i = 0; i < itemsDefList1.size(); i++) {// 体检项
                    Map itemsMap1 = new HashMap();// 保存一个体检项目
                    List<Map> temp1 = new ArrayList<Map>();// 体检项目下的异常指标

                    Map itemsDefMap1 = (Map) itemsDefList1.get(i);
                    String comclass = (String) itemsDefMap1.get("comclass");
                    String itemcode = (String) itemsDefMap1.get("itemcode");// 体检项目id
                    String itemname = (String) itemsDefMap1.get("itemname");// 体检项目名称
                    String itemuuid = (String) itemsDefMap1.get("itemuuid");
                    for (int j = 0; j < itemsDetList1.size(); j++) {// 项目指标
                        Map itemsDetMap1 = (Map) itemsDetList1.get(j);
                        String itemcode2 = (String) itemsDetMap1.get("comid");
                        String itemuuid2 = (String) itemsDetMap1
                                .get("itemuuid");
                        if (itemcode.equals(itemcode2)
                                && itemuuid.equals(itemuuid2)) {
                            temp1.add(itemsDetMap1);
                        }
                    }
                    if (itemsDefMap1.get("excdate") != null
                            && (!"".equals(itemsDefMap1.get("excdate")))) {

                    } else {
                        itemname = itemname + "(未检?)";
                    }
                    itemsMap1.put("comclass", comclass);
                    itemsMap1.put("itemname", itemname);
                    itemsMap1.put("examdate", itemsDefMap1.get("excdate"));// 体检时间
                    itemsMap1.put("examdoctorname", itemsDefMap1
                            .get("excdoctorname"));// 体检医生姓名
                    itemsMap1.put("checkdate", itemsDefMap1.get("checkdate"));// 审核时间
                    itemsMap1.put("checkdoctorname", itemsDefMap1
                            .get("checkdoctorname"));// 审核医生
                    itemsMap1.put("itemsDet1", temp1);
                    itemsList1.add(itemsMap1);
                }
            }
            modelMap.put("itemsList", itemsList);
            modelMap.put("itemsList1", itemsList1);
            System.out.println(itemsList);
            System.out.println(itemsList1);
            db.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("pexam/pexamItemsDetailValue", modelMap);
    }

    /*
	 * 体检明细指标(类型为“检验”或“检查”)
	 */
    @RequestMapping("/pexamItemsDetailValue2")
    public ModelAndView pexamItemsDetailValue2(HttpServletRequest request,
                                               HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute(
                "login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute(
                "login_user");
        String hosnum = basHospitals.getHosnum();

        String examid = request.getParameter("examid");// 预约编号
        String pexamid = request.getParameter("pexamid");// 体检者编号

        DBOperator db = null;
        try {
            db = new DBOperator();
            List itemsList = new ArrayList();// 保存所有异常指标项
            System.out.println("获取“检验”或“检查”体检项目");// 如果有多个套餐且套餐之间有相同的体检项目--就会出现相同的体检项目
            String sql = "select a.itemuuid,a.itemcode,a.itemname,to_char(a.excdate, 'yyyy-mm-dd') as excdate,a.excdoctorname,to_char(a.checkdate, 'yyyy-mm-dd') as checkdate,a.checkdoctorname"
                    + " from pexam_items_title a,pexam_items_com c"
                    + " where a.hosnum = ? and a.pexamid = ? and a.itemcode=c.comid"
                    + " and c.comclass != '其他'" + " order by c.sn";

            List itemsDefList = db.find(sql, new Object[]{hosnum, pexamid});
            if (itemsDefList != null && itemsDefList.size() > 0) {

                System.out.println("获取类型是“检验”或“检查”的体检项目指标");
                sql = "select a.indname,a.resultunit,a.comid,a.itemuuid,a.result,a.range,a.stringvalue from pexam_results a, pexam_items_com c "
                        + " where a.hosnum = ? and a.pexamid = ? and a.comid = c.comid and c.comclass != '其他'";

                List itemsDetList = db.find(sql,
                        new Object[]{hosnum, pexamid});
                for (int i = 0; i < itemsDefList.size(); i++) {// 体检项
                    Map itemsMap = new HashMap();// 保存一个体检项目
                    List<Map> temp = new ArrayList<Map>();// 体检项目下的异常指标

                    Map itemsDefMap = (Map) itemsDefList.get(i);
                    String itemcode = (String) itemsDefMap.get("itemcode");// 体检项目id
                    String itemname = (String) itemsDefMap.get("itemname");// 体检项目名称
                    String itemuuid = (String) itemsDefMap.get("itemuuid");
                    for (int j = 0; j < itemsDetList.size(); j++) {// 项目指标
                        Map itemsDetMap = (Map) itemsDetList.get(j);
                        String itemcode2 = (String) itemsDetMap.get("comid");
                        String itemuuid2 = (String) itemsDetMap.get("itemuuid");
                        if (itemcode.equals(itemcode2)
                                && itemuuid.equals(itemuuid2)) {
                            temp.add(itemsDetMap);
                        }
                    }
                    if (itemsDefMap.get("excdate") != null
                            && (!"".equals(itemsDefMap.get("excdate")))) {

                    } else {
                        itemname = itemname + "(未检?)";
                    }
                    itemsMap.put("itemname", itemname);
                    itemsMap.put("examdate", itemsDefMap.get("excdate"));// 体检时间
                    itemsMap.put("examdoctorname", itemsDefMap
                            .get("excdoctorname"));// 体检医生姓名
                    itemsMap.put("checkdate", itemsDefMap.get("checkdate"));// 审核时间
                    itemsMap.put("checkdoctorname", itemsDefMap
                            .get("checkdoctorname"));// 审核医生
                    itemsMap.put("itemsDet", temp);
                    itemsList.add(itemsMap);
                }
            }
            modelMap.put("itemsList", itemsList);
            db.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("pexam/pexamItemsDetailValue2", modelMap);
    }

    /*
	 * 体检明细
	 */
    @RequestMapping("/pexamItemsDetailValue3")
    public ModelAndView pexamItemsDetailValue3(HttpServletRequest request,
                                               HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute(
                "login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute(
                "login_user");
        String hosnum = basHospitals.getHosnum();

        String examid = request.getParameter("examid");
        String pexamid = request.getParameter("pexamid");

        DBOperator db = null;
        try {
            db = new DBOperator();
            String bdate = "";
            List itemsList = new ArrayList();// 保存所有异常指标项
            System.out.println("获取“其他”体检项目");// 如果有多个套餐且套餐之间有相同的体检项目--就会出现相同的体检项目
            String sql = "select b.*,a.excdate,a.excdoctorname from pexam_items_title a,pexam_items_com b where a.hosnum=b.hosnum and a.itemcode=b.comid and a.hosnum=? and a.examid=? and a.pexamid=? and b.comclass='其他' order by b.sn";
            List itemsDefList = db.find(sql, new Object[]{hosnum, examid,
                    pexamid});
            if (itemsDefList != null && itemsDefList.size() > 0) {

                sql = "select * from pexam_mans a where a.hosnum=? and a.pexamid=? and a.examid=?";
                List list = db.find(sql,
                        new Object[]{hosnum, pexamid, examid});
                Map map = (Map) list.get(0);
                bdate = DateUtil.dateToString((Date) map.get("bdate"),
                        "yyyy-MM-dd");

                System.out.println("获取类型是“其他”的体检项目指标");
                sql = "select b.indname as detailname,b.resultunit,a.comid,a.result from pexam_results a,pexam_items_ind b,pexam_items_com c where a.hosnum=b.hosnum and a.indid=b.indid and a.hosnum=? and a.examid=? and a.pexamid=? and a.hosnum=c.hosnum and c.comid=a.comid and c.comclass='其他'order by b.sn";
                List itemsDetList = db.find(sql, new Object[]{hosnum, examid,
                        pexamid});
                for (int i = 0; i < itemsDefList.size(); i++) {// 体检项
                    Map itemsMap = new HashMap();// 保存一个体检项目
                    List<Map> temp = new ArrayList<Map>();// 体检项目下的异常指标

                    Map itemsDefMap = (Map) itemsDefList.get(i);
                    String itemcode = (String) itemsDefMap.get("comid");// 体检项目id
                    String itemname = (String) itemsDefMap.get("comname");// 体检项目名称
                    for (int j = 0; j < itemsDetList.size(); j++) {// 项目指标
                        Map itemsDetMap = (Map) itemsDetList.get(j);
                        String itemcode2 = (String) itemsDetMap.get("comid");
                        if (itemcode.equals(itemcode2)) {
                            if (null == itemsDetMap.get("result")) {
                                itemsDetMap.put("resultunit", "");
                            }
                            temp.add(itemsDetMap);
                        }
                    }
                    itemsMap.put("itemname", itemname);// 项目名称
                    itemsMap.put("itemsDet", temp);// 指标明细集合
                    itemsMap.put("excdate", itemsDefMap.get("excdate"));// 体检日期--暂时不用这个
                    itemsMap.put("excdoctorname", itemsDefMap
                            .get("excdoctorname"));// 体检医生
                    if (null == itemsDefMap.get("excdoctorname")) {
                        itemsMap.put("bdate", "");// 体检日期
                    } else {
                        itemsMap.put("bdate", bdate);// 体检日期
                    }
                    itemsList.add(itemsMap);
                }
            }
            modelMap.put("itemsList", itemsList);

            itemsList = new ArrayList();// 保存所有异常指标项
            System.out.println("获取“检验”或“检查”体检项目");// 如果有多个套餐且套餐之间有相同的体检项目--就会出现相同的体检项目
            sql = "select a.itemuuid,a.itemcode,a.itemname,to_char(a.excdate,'yyyy-mm-dd') as excdate,a.excdoctorname,"
                    + "to_char(a.checkdate,'yyyy-mm-dd') as checkdate,a.checkdoctorname "
                    + "from pexam_items_title a "
                    + "where a.hosnum=? and a.pexamid=? and a.comclass!='其他' order by a.sn";
            itemsDefList = db.find(sql, new Object[]{hosnum, pexamid});
            if (itemsDefList != null && itemsDefList.size() > 0) {

                System.out.println("获取类型是“检验”或“检查”的体检项目指标");
                sql = "select a.indname,a.resultunit,a.comid,a.itemuuid,a.result,a.range,a.stringvalue "
                        + "from pexam_results a where a.hosnum=? and a.pexamid=? and a.examtype!='其他'";
                List itemsDetList = db.find(sql,
                        new Object[]{hosnum, pexamid});
                for (int i = 0; i < itemsDefList.size(); i++) {// 体检项
                    Map itemsMap = new HashMap();// 保存一个体检项目
                    List<Map> temp = new ArrayList<Map>();// 体检项目下的异常指标

                    Map itemsDefMap = (Map) itemsDefList.get(i);
                    String itemcode = (String) itemsDefMap.get("itemcode");// 体检项目id
                    String itemname = (String) itemsDefMap.get("itemname");// 体检项目名称
                    String itemuuid = (String) itemsDefMap.get("itemuuid");
                    for (int j = 0; j < itemsDetList.size(); j++) {// 项目指标
                        Map itemsDetMap = (Map) itemsDetList.get(j);
                        String itemcode2 = (String) itemsDetMap.get("comid");
                        String itemuuid2 = (String) itemsDetMap.get("itemuuid");
                        if (itemcode.equals(itemcode2)
                                && itemuuid.equals(itemuuid2)) {
                            temp.add(itemsDetMap);
                        }
                    }
                    if (itemsDefMap.get("excdate") != null
                            && (!"".equals(itemsDefMap.get("excdate")))) {

                    } else {
                        itemname = itemname + "(拒检)";
                    }
                    itemsMap.put("itemname", itemname);
                    itemsMap.put("examdate", itemsDefMap.get("excdate"));// 体检时间
                    itemsMap.put("examdoctorname", itemsDefMap
                            .get("excdoctorname"));// 体检医生姓名
                    itemsMap.put("checkdate", itemsDefMap.get("checkdate"));// 审核时间
                    itemsMap.put("checkdoctorname", itemsDefMap
                            .get("checkdoctorname"));// 审核医生
                    itemsMap.put("itemsDet", temp);
                    itemsList.add(itemsMap);
                }
            }
            modelMap.put("itemsList2", itemsList);
            db.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("pexam/pexamItemsDetailValue", modelMap);
    }

    /*
	 * 农民体检结果保存
	 */
    @RequestMapping("/examResultSave")
    public void examResultSave(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute(
                "login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute(
                "login_user");

        String hosnum = basHospitals.getHosnum();
        String username = basUser.getName();
        String userman = basUser.getUser_key();

        String pexamid = request.getParameter("pexamid");
        String examResult = request.getParameter("examResult");
        Date date = new Date();

        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();

            String sql = "update pexam_mans a set a.examresult=?,a.genexamdoctor=?,a.doctorname=?,a.genexamdate=? where a.hosnum=? and a.pexamid=?";
            db.excute(sql, new Object[]{examResult, userman, username, date,
                    hosnum, pexamid});

            db.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            db.rollback();
            pw.print("fail");
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    /*
	 * 进入体检总计页面
	 */
    @RequestMapping("/CheckAll2")
    public String CheckAll2(HttpServletRequest request,
                            HttpServletResponse response) throws Exception {
        // TODO
        response.setContentType("text/html;charset=utf-8");
        DBOperator db = null;

        try {

            db = new DBOperator();
            String sql = "select s.unitname from pexam_main s group by s.unitname";
            List itemlist = db.find(sql);
            request.getSession().setAttribute("itemlist", itemlist);
            db.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            db.rollback();

        } finally {
            db.freeCon();
        }
        return "pexam/CheckAll";
    }

    /*
	 * 体检总计
	 */
    @RequestMapping("/CheckAll")
    public void CheckAll(HttpServletRequest request,
                         HttpServletResponse response) throws Exception {
        // TODO
        response.setContentType("text/html;charset=utf-8");
        System.out.println("进CheckAll方法：");
        String type = null;
        String examtypes = null;
        String beginDate = request.getParameter("beginDate");
        String endDate = request.getParameter("endDate");
        type = request.getParameter("type");

        if (type != null && !type.equals("")) {
            examtypes = URLDecoder.decode(request.getParameter("examtypes"),
                    "utf-8");
        }
        DBOperator db = new DBOperator();
        PrintWriter pw = null;
        pw = response.getWriter();
        String sql;
        String sql2;
        String sql3;
        int a0 = 0, a13 = 0, a1 = 0, a2 = 0, a3 = 0, a4 = 0, a5 = 0, a6 = 0, a7 = 0, a8 = 0, a9 = 0, a10 = 0, a11 = 0, a12 = 0, a15 = 0;
        try {
            ArrayList<DiseaseEntity> list = new ArrayList<DiseaseEntity>();
            ArrayList<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();

            Map map = new HashMap();
            DiseaseEntity disease = null;
            DiseaseEntity disease2 = null;
            int a = 1, j = 0;
            if (beginDate == null && endDate == null) {
                a = 1;
                j = 12;
            } else {
                // a=Integer.valueOf(beginDate.substring(0,4)));//valueOf不知道是不是这样写，自己改，substring不知道是不是这样截取，自己改
                // j=Integer.valueOf(endDate.substring(0,7));
            }
            Map entity = null;
            List list_ = this.getYearMonthList(examtypes, beginDate, endDate);
            for (int i = 0; i < list_.size(); i++) {

                entity = (Map) list_.get(i);
                a1 = this.getAmount(entity.get("yearmonth").toString(),
                        "'高血压'", beginDate, endDate, examtypes);
                a2 = this.getAmount(entity.get("yearmonth").toString(),
                        "'糖尿病'", beginDate, endDate, examtypes);
                a3 = this.getAmount(entity.get("yearmonth").toString(),
                        "'高脂血症'", beginDate, endDate, examtypes);
                a4 = this.getAmount(entity.get("yearmonth").toString(),
                        "'肝功能异常'", beginDate, endDate, examtypes);
                a5 = this.getAmount(entity.get("yearmonth").toString(),
                        "'肾功能异常'", beginDate, endDate, examtypes);
                a6 = this.getAmount(entity.get("yearmonth").toString(),
                        "'恶性肿瘤' ", beginDate, endDate, examtypes);
                a7 = this.getAmount(entity.get("yearmonth").toString(),
                        "'良性肿瘤'", beginDate, endDate, examtypes);
                a8 = this.getAmount(entity.get("yearmonth").toString(),
                        "'胆囊炎'", beginDate, endDate, examtypes);
                a9 = this.getAmount(entity.get("yearmonth").toString(),
                        "'泌尿生殖系统疾病'", beginDate, endDate, examtypes);
                a10 = this.getAmount(entity.get("yearmonth").toString(),
                        "'慢性阻塞性肺疾病'", beginDate, endDate, examtypes);
                a11 = this.getAmount(entity.get("yearmonth").toString(),
                        "'精神疾病'", beginDate, endDate, examtypes);
                a12 = this.getAmount(entity.get("yearmonth").toString(),
                        "'肺结核'", beginDate, endDate, examtypes);
                // 病人人数。。。
                sql = "select count(*) amount from (select count(*) from (select * from pexam_results p ";
                if (examtypes != null && !examtypes.equals("")) {
                    sql = sql + " ,pexam_main  m ";
                }

                sql = sql + " where p.result is not null ";
                if (beginDate != null && endDate != null
                        && !beginDate.equals("") && !endDate.equals("")) {
                    sql = sql
                            + " and to_char(p.excdate,'yyyy-mm-dd')  between '"
                            + beginDate + "' and '" + endDate + "'";
                    sql = sql
                            + " and substr(to_char(p.excdate,'yyyy-mm-dd'),0,7) =  '"
                            + entity.get("yearmonth").toString() + "'";

                } else {
                    sql = sql
                            + " and substr(to_char(p.excdate,'yyyy-mm-dd'),0,7) =  '"
                            + entity.get("yearmonth").toString() + "'";
                }
                if (examtypes != null && !examtypes.equals("")) {
                    sql = sql + " and  p.examid=m.examid and m.unitname= '"
                            + examtypes + "'";
                }

                sql = sql + " )s group by s.excdoctorname)";

                // 体检总人数
                sql2 = "select count(*) amounts from (select count(*) from (select * from pexam_results p ";
                if (examtypes != null && !examtypes.equals("")) {
                    sql2 = sql2 + " ,pexam_main  m ";
                }

                sql2 = sql2 + " where 1=1 ";
                if (beginDate != null && endDate != null
                        && !beginDate.equals("") && !endDate.equals("")) {
                    sql2 = sql2
                            + " and  to_char(p.excdate,'yyyy-mm-dd')  between '"
                            + beginDate + "' and '" + endDate + "'";
                    sql2 = sql2
                            + " and substr(to_char(p.excdate,'yyyy-mm-dd'),0,7) =  '"
                            + entity.get("yearmonth").toString() + "'";
                } else {
                    sql2 = sql2
                            + " and substr(to_char(p.excdate,'yyyy-mm-dd'),0,7) =  '"
                            + entity.get("yearmonth").toString() + "'";
                }
                if (examtypes != null && !examtypes.equals("")) {
                    sql2 = sql2 + " and  p.examid=m.examid and m.unitname= '"
                            + examtypes + "'";
                }

                sql2 = sql2 + " ) s group by s.excdoctorname)";

                // 最后字段。。。。。其他
                sql3 = "select count(*) amount3 from (select count(*) from (select *  from pexam_results p ";
                if (examtypes != null && !examtypes.equals("")) {
                    sql3 = sql3 + " ,pexam_main  m";
                }

                sql3 = sql3 + " where p.result is not null ";
                if (beginDate != null && endDate != null
                        && !beginDate.equals("") && !endDate.equals("")) {
                    sql3 = sql3
                            + " and substr(to_char(p.excdate,'yyyy-mm-dd'),0,7)  between '"
                            + beginDate + "' and '" + endDate + "'";
                    sql3 = sql3
                            + " and substr(to_char(p.excdate,'yyyy-mm-dd'),0,7) =  '"
                            + entity.get("yearmonth").toString() + "'";
                } else {
                    sql3 = sql3
                            + " and substr(to_char(p.excdate,'yyyy-mm-dd'),0,7) =  '"
                            + entity.get("yearmonth").toString() + "'";
                    //
                }
                if (examtypes != null && !examtypes.equals("")) {
                    sql3 = sql3 + " and  p.examid=m.examid and m.unitname= '"
                            + examtypes + "'";
                }

                sql3 = sql3
                        + "  and p.excdoctorname not in(select excdoctorname "
                        + "  from pexam_results where result in ( '高血压','','糖尿病','高脂血症','肝功能异常','肾功能异常'"
                        + ",'恶性肿瘤','良性肿瘤','胆囊炎','泌尿生殖系统疾病','慢性阻塞性肺疾病','精神疾病','肺结核') )";

                sql3 = sql3 + " )s group by s.excdoctorname)";

                HashMap obj = null;

                // 病人人数。。。
                obj = (HashMap) db.findOne(sql);
                a0 = Integer.valueOf(obj.get("amount").toString());
                // 体检总人数
                obj = (HashMap) db.findOne(sql2);
                a13 = Integer.valueOf(obj.get("amounts").toString());
                // 其他疾病人数
                obj = (HashMap) db.findOne(sql3);
                a15 = Integer.valueOf(obj.get("amount3").toString());
                disease = new DiseaseEntity();
                disease.setTjAmount(a13);
                disease.setBrAmount(a0);
                disease.setMonth(entity.get("yearmonth").toString());
                disease.setGxyAmount(a1);
                disease.setTybAmount(a2);
                disease.setGzxzAmount(a3);
                disease.setGgnAmount(a4);
                disease.setSgnAmount(a5);
                disease.setExzltjAmount(a6);
                disease.setLxzlAmount(a7);
                disease.setDnlAmount(a8);
                disease.setNnszAmount(a9);
                disease.setCopdAmount(a10);
                disease.setJsbAmount(a11);
                disease.setFjhAmount(a12);
                disease.setQtAmount(a15);
                list.add(disease);
            }

            // request.setAttribute("list", list);
            request.getSession().setAttribute("checkAlllist", list);
            JSONArray jsons = JSONArray.fromObject(list);
            pw.print(jsons.toString());
            System.out.println("aaaaaaaaaaaaaaaaaaaa");
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            pw.print("fail");
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    /*
	 * 获得各个病例数量
	 */
    public int getAmount(String yearMonth, String sqlCase, String date1,
                         String date2, String examtypes) {// sqlCase病例
        DBOperator db = null;
        String sql = "";
        sql = "select count(*) amount from pexam_results p ";
        if (examtypes != null && !examtypes.equals("")) {
            sql = sql + ",pexam_main  m";
        }

        sql = sql + " where 1=1 ";
        if (date1 != null && date2 != null && !date1.equals("")
                && !date2.equals("")) {
            sql = sql
                    + " and substr(to_char(p.excdate,'yyyy-mm-dd'),0,7)  between '"
                    + date1 + "' and '" + date2 + "'";
            sql = sql + " and substr(to_char(p.excdate,'yyyy-mm-dd'),0,7) =  '"
                    + yearMonth + "'";
        } else {
            sql = sql + " and substr(to_char(p.excdate,'yyyy-mm-dd'),0,7) =  '"
                    + yearMonth + "'";
            //
        }
        if (examtypes != null && !examtypes.equals("")) {
            sql = sql + " and  p.examid=m.examid and m.unitname= '" + examtypes
                    + "'";
        }

        sql = sql + " and p.result = " + sqlCase.trim();

        HashMap obj = null;
        try {

            db = new DBOperator();
            obj = (HashMap) db.findOne(sql);
            db.commit();

        } catch (Exception ex) {
            ex.printStackTrace();
            db.rollback();

        } finally {
            db.freeCon();
        }
        return Integer.valueOf(obj.get("amount").toString());
    }

    // 得到年月
    public List getYearMonthList(String sqlCase, String date1, String date2) {// 获得一段时间内的时间（year-month）分组集合
        DBOperator db = null;
        sqlCase = (sqlCase == null ? "" : sqlCase);
        String sql = "";
        sql = "select substr(to_char(excdate,'yyyy-mm-dd'),1,7) yearmonth from pexam_results where 1=1 ";
        if (date1 != null && date2 != null && !date1.equals("")
                && !date2.equals("")) {
            sql = sql + " and to_char(excdate,'YYYY-MM-DD')  between '" + date1
                    + "' and '" + date2 + "'";
        } else {

            sql = sql
                    + " and substr(to_char(excdate,'YYYY-MM-DD'),0,4) = substr(to_char(sysdate,'yyyy-mm-dd'),0,4)";
        }
        // sql = sql +" and result = " +sqlCase.trim();
        sql = sql + " group by substr(to_char(excdate,'yyyy-mm-dd'),1,7)";

        List list = null;
        try {

            db = new DBOperator();
            list = db.find(sql);

            db.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            db.rollback();

        } finally {
            db.freeCon();
        }
        return list;
    }

    /*
	 * 体检人数—————— 导出excel 方法1：直接后台取数据，或直接用sql语句
	 */
    @RequestMapping("/ExportCheckAll")
    public void get_MedPriceXSLinventProlossList(HttpServletRequest request,
                                                 HttpServletResponse response) throws IOException {
        // TODO
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        DBOperator db = null;
        String sql = null;
        int num1 = 0, num2 = 0, num3 = 0, num4 = 0, num5 = 0, num6 = 0, num7 = 0, num8 = 0, num9 = 0, num10 = 0, num11 = 0, num12 = 0, num13 = 0, num14 = 0, num15 = 0;
        List list = new ArrayList();
        try {
            // sql="select a.hosnum from pexam_mans a where a.pexamid='107543'";
            // List<Map<String, Object>> mapslist=db.find(sql, new
            // Object[]{});orequest.getSession().getAttribute("checkAlllist")

            List<Map<String, Object>> mapslist = (List<Map<String, Object>>) request
                    .getSession().getAttribute("checkAlllist");
            List<Map<String, Object>> listmaps2 = new ArrayList<Map<String, Object>>();

            DiseaseEntity map1;
            for (int i = 0; i < mapslist.size(); i++) {
                map1 = (DiseaseEntity) mapslist.get(i);
                System.out.println(map1.getBrAmount());
                String month = map1.getMonth();// 盘点日期
                int Tj = map1.getTjAmount();// 盘点日期
                int Br = map1.getBrAmount();// 盘点日期
                int Gxy = map1.getGxyAmount();
                int Tyb = map1.getTybAmount();// 盘点日期
                int GzxzAmount = map1.getGzxzAmount();// 盘点日期
                int GgnAmount = map1.getGgnAmount();// 盘点日期
                int SgnAmount = map1.getSgnAmount();// 盘点日期
                int ExzltjAmount = map1.getExzltjAmount();// 盘点日期
                int LxzlAmount = map1.getLxzlAmount();// 盘点日期
                int DnlAmount = map1.getDnlAmount();// 盘点日期
                int NnszAmount = map1.getNnszAmount();// 盘点日期
                int CopdAmount = map1.getCopdAmount();// 盘点日期
                int JsbAmount = map1.getJsbAmount();// 盘点日期
                int FjhAmount = map1.getFjhAmount();// 盘点日期
                int QtAmount = map1.getQtAmount();// 盘点日期

                num1 = num1 + Tj;
                num2 = num2 + Br;
                num3 = num3 + Gxy;
                num4 = num4 + Tyb;
                num5 = num5 + GzxzAmount;
                num6 = num6 + GgnAmount;
                num7 = num7 + SgnAmount;
                num8 = num8 + ExzltjAmount;
                num9 = num9 + LxzlAmount;
                num10 = num10 + DnlAmount;
                num11 = num11 + NnszAmount;
                num12 = num12 + CopdAmount;
                num13 = num13 + JsbAmount;
                num14 = num14 + FjhAmount;
                num15 = num15 + QtAmount;

                Map<String, Object> map2 = new LinkedHashMap<String, Object>();
                map2.put("month", month);
                map2.put("tjAmount", Tj);
                map2.put("brAmount", Br);
                map2.put("gxyAmount", Gxy);
                map2.put("tybAmount", Tyb);
                map2.put("gzxzAmount", GzxzAmount);
                map2.put("ggnAmount", GgnAmount);
                map2.put("sgnAmount", SgnAmount);
                map2.put("exzltjAmount", ExzltjAmount);
                map2.put("lxzlAmount", LxzlAmount);
                map2.put("dnlAmount", DnlAmount);
                map2.put("nnszAmount", NnszAmount);
                map2.put("copdAmount", CopdAmount);
                map2.put("jsbAmount", JsbAmount);
                map2.put("fjhAmount", FjhAmount);
                map2.put("qtAmount", QtAmount);

                listmaps2.add(map2);

            }

            Map<String, Object> map3 = new LinkedHashMap<String, Object>();
            map3.put("heji", "合计");
            map3.put("num1", num1);
            map3.put("num2", num2);
            map3.put("num3", num3);
            map3.put("num4", num4);
            map3.put("num5", num5);
            map3.put("num6", num6);
            map3.put("num7", num7);
            map3.put("num8", num8);
            map3.put("num9", num9);
            map3.put("num10", num10);
            map3.put("num11", num11);
            map3.put("num12", num12);
            map3.put("num13", num13);
            map3.put("num14", num14);
            map3.put("num15", num15);

            listmaps2.add(map3);

            response.setContentType("application/x-msdownload;charset=gbk");
            String title = "体检统计表";
            String dateStr = DateUtil
                    .dateToString(new Date(), "yyyyMMddHHmmss");
            String fileName = title + "-" + dateStr + ".xls";
            String fileNameTemp = URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename="
                    + new String(fileNameTemp.getBytes("utf-8"), "gbk"));

            OutputStream os = response.getOutputStream();
            ExcelUtils eu = new ExcelUtils();
            eu.export(os, title, new String[]{"日期", "体检人数", "病人总数", "高血压",
                    "糖尿病", "高脂血症", "肝功能异常", "肾功能异常", "恶性肿瘤", "良性肿瘤", "胆囊炎",
                    "泌尿生殖系疾病", "慢性阻塞性肺病", "精神疾病", "肺结核", "其他"}, new int[]{
                    14, 12, 16, 15, 15, 14, 14, 14, 15, 15, 15, 20, 18, 15, 15,
                    15}, DbUtils.ListMapToListObject(listmaps2));
            os.flush();
            os.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出表格数据
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/Allcheckexport2")
    public void Allcheckexport2(HttpServletRequest request,
                                HttpServletResponse response, String datas, String name) {
        OutputStream os = null;
        System.out.println("导excel方法二：------");
        try {
            // datas = URLDecoder.decode(datas,"utf-8");
            // columns = URLDecoder.decode(columns,"utf-8");
            // 从jsptype判断标题名

            // 表头
            // String[] legends = columns.split(",");
            // String[] legends2 = null;

            // 每一列的宽度
            // int length1 = legends2==null?legends.length:legends2.length;
            // int[] sizes = new int[length1];
            // for(int i=0;i<sizes.length;i++){
            // sizes[i] = 10;
            // }
            // 制表人
            // String loginname =
            // ((Bas_user)request.getSession().getAttribute("login_user")).getName();
            // 表格第二行信息
            // String[] extrainfo = new String[sizes.length];
            // for(int i=0;i<extrainfo.length;i++){
            // if(i==extrainfo.length-4){
            // extrainfo[i] = "制表人：";
            // }else if(i==extrainfo.length-3){
            // extrainfo[i] = loginname;
            // }else if(i==extrainfo.length-2){
            // extrainfo[i] = "核算日期：";
            // }else if(i==extrainfo.length-1){
            // extrainfo[i] = (new SimpleDateFormat("yyyy-MM")).format(new
            // Date());
            // }else{
            // extrainfo[i] = "";
            // }
            // }
            // 拼接表格数据
            List<Object[]> list = new ArrayList<Object[]>();
            // list.add(extrainfo);
            // list.add(legends);
            // if(legends2!=null){
            // // list.add(legends2);
            // }
            String[] datastrArray = datas.split(";");
            for (String datastr : datastrArray) {
                String[] dataArray = datastr.split(",");
                for (int i = 0; i < dataArray.length; i++) {
                    if ("null".equals(dataArray[i])) {
                        dataArray[i] = "";
                    }
                }
                list.add(dataArray);
            }

            response.setContentType("application/x-msdownload;charset=gbk");
            String title = "体检统计表";
            String dateStr = DateUtil
                    .dateToString(new Date(), "yyyyMMddHHmmss");
            String fileName = title + "-" + dateStr + ".xls";
            String fileNameTemp = URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename="
                    + new String(fileNameTemp.getBytes("utf-8"), "gbk"));
            os = response.getOutputStream();
            ExcelUtils eu = new ExcelUtils();
            eu.export(os, title, new String[]{"日期", "体检人数", "病人总数", "高血压",
                    "糖尿病", "高脂血症", "肝功能异常", "肾功能异常", "恶性肿瘤", "良性肿瘤", "胆囊炎",
                    "泌尿生殖系疾病", "慢性阻塞性肺病", "精神疾病", "肺结核", "其他"}, new int[]{
                    14, 12, 16, 15, 15, 14, 14, 14, 15, 15, 15, 20, 18, 15, 15,
                    15}, list);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ------------------------------------2013-01-28 徐闯 体检建议后台管理
    // -----------------------
    @RequestMapping("/pexamSugestItems")
    public ModelAndView pexamSugestItems(HttpServletRequest request,
                                         HttpServletResponse response, ModelMap modelmap) {
        response.setContentType("text/html;charset=utf-8");
        return new ModelAndView("phyexam/pexamSugestItems", modelmap);
    }

    // aaa
    // 后台管理获取体检建议树数据
    @RequestMapping(value = "/getdiseaseList2")
    public void getDtMainList2(HttpServletRequest request,
                               HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String hosname = basHospitals.getHosname();
        String nodecode = basHospitals.getNodecode();

        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            List<String> tree = new ArrayList<String>();
            // String
            // parentnode="{id:'0000_0',pId:'00',name:'江山市卫生局标准模板',open:true}";
            // String node =
            // "{id:'0000_0000', pId:'0000_0', name:'疾病列表',open:true}";
            // tree.add(parentnode);
            // tree.add(node);
            // if(!hosnum.equals("0000")&&hosnum!="0000"){
            // String
            // parentnode1="{id:\""+hosnum+"_0"+"\",pId:'00',name:\""+hosname+"\",open:true}";
            // String node1 =
            // "{id:\""+hosnum+"_"+hosnum+"\",pId:\""+hosnum+"_0"+"\",name:'疾病列表',open:true}";
            // tree.add(parentnode1);
            // tree.add(node1);
            //
            // }
			/*
			 * 查询数据tree 数据
			 */
            String temp = "";
            String sql1 = "select * from pexam_sugests s where s.hosnum='1001' and s.parentid='0000'  order by s.sn ";
            List<Pexam_sugests> treelist = db.find(sql1, Pexam_sugests.class);
            for (Pexam_sugests li : treelist) {
                String id = li.getSugestid();
                String name = li.getClassname();
                String pid = li.getParentid();
                String hosnum1 = li.getHosnum();
                String tree_id = hosnum1 + "_" + id;
                String kbsh = String.valueOf(li.getKsbh());
                temp = "{id:\"" + id + "\"," + "pId:\"" + pid + "\",name:\""
                        + name + "\",open:false,isParent:true}";
                tree.add(temp);
            }
            /**
             * sql1 ="select * from pexam_sugests s where s.hosnum=? and s.nodecode=?  and rownum<1000 order by s.sn "
             * ; treelist = db.find(sql1,new
             * Object[]{hosnum,hosnum},Pexam_sugests.class); for (Pexam_sugests
             * li : treelist) { String id = li.getSugestid(); String name =
             * li.getClassname(); String pid = li.getParentid(); String
             * hosnum1=li.getHosnum(); String tree_id=hosnum1+"_"+id; temp =
             * "{id:\"" +tree_id + "\"," + "pId:\""+ hosnum1+"_"+pid+
             * "\",name:\"" + name+"\",open:false}"; tree.add(temp); }
             */
            JSONArray jsons = JSONArray.fromObject(tree);
            // System.out.println(jsons.toString());
            pw.print(jsons);
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            request.setAttribute("fail", "fail");
        } finally {
            try {
                db.freeCon();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "/getdiseaseList3")
    public void getDtMainList3(HttpServletRequest request,
                               HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String hosname = basHospitals.getHosname();
        String nodecode = basHospitals.getNodecode();
        String parentid = request.getParameter("id");
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            List<String> tree = new ArrayList<String>();

            String temp = "";
            String sql1 = "select * from pexam_sugests s where s.hosnum='1001' and s.parentid='"
                    + parentid + "' order by s.sn ";
            List<Pexam_sugests> treelist = db.find(sql1, Pexam_sugests.class);
            for (Pexam_sugests li : treelist) {
                String id = li.getSugestid();
                String name = li.getClassname();
                String pid = li.getParentid();
                String hosnum1 = li.getHosnum();
                String tree_id = hosnum1 + "_" + id;
                String kbsh = String.valueOf(li.getKsbh());
                temp = "{id:\"" + id + "\"," + "pId:\"" + pid + "\",name:\""
                        + name + "\",open:false,isParent:false}";
                tree.add(temp);
            }

            JSONArray jsons = JSONArray.fromObject(tree);
            // System.out.println(jsons.toString());
            pw.print(jsons);
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            request.setAttribute("fail", "fail");
        } finally {
            try {
                db.freeCon();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 体检建议 后台管理点击树节点 加载iframe
    @RequestMapping(value = "/pexamSugestList")
    public String pexamSugestList(HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        // String node1=request.getParameter("node1");//hosnum
        // String node2=request.getParameter("node2");//sugid
        // String nodeid=request.getParameter("nodeid");//选中节点id
        // String nodepid=request.getParameter("nodepid");//选中父节点id
        String sugid = request.getParameter("sugid");
        String parentid = request.getParameter("parentid");
        String sql = null;
        PrintWriter pw = null;
        DBOperator db = null;
        List<Map> suglist = new ArrayList<Map>();
        try {
            pw = response.getWriter();
            db = new DBOperator();
            sql = "select s.sugestid,s.classname,s.pybm,s.sugesttext,s.parentid,to_char(s.cdate,'yyyy-mm-dd') as cdate,s.cman,s.sn,s.hosnum from pexam_sugests s where s.sugestid=? and s.hosnum=? order by s.sn ";
            suglist = db.find(sql, new Object[]{sugid, hosnum});
            JSONArray jsons = JSONArray.fromObject(suglist);
            request.setAttribute("suglist", jsons.toString());
            request.setAttribute("hosnum", hosnum);
            request.setAttribute("node1", hosnum);
            request.setAttribute("node2", hosnum);
            request.setAttribute("nodeid", sugid);
            request.setAttribute("nodepid", parentid);
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            request.setAttribute("fail", "fail");
        } finally {
            db.freeCon();
        }
        return "phyexam/pexamSugestList";
    }

    // 体检建议 新增 修改 查看
    @RequestMapping(value = "/pexamSugestAdd", method = RequestMethod.GET)
    public ModelAndView pexamSugestAdd(HttpServletRequest request,
                                       HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        Bas_user basuser = (Bas_user) request.getSession().getAttribute(
                "login_user");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String butType = request.getParameter("butType");// 操作类型：add（新增）,modify（修改）,view（查看）
        String hosnum1 = request.getParameter("hosnum");
        String nodeid = request.getParameter("nodeid");// 选中的父节点
        // 父id
        String nodepid = request.getParameter("nodepid");
        String node1 = request.getParameter("node1");
        String node2 = request.getParameter("node2");
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "";

            if ("add".equals(butType)) {
                String doctorid = basuser.getJob_no();// 登陆医生id
                String cman = basuser.getName();// 登陆医生姓名
                int sn = 1;
                sql = "select max(t.sn) as sn from PEXAM_SUGESTS t  where t.parentid=? and t.hosnum=? ";
                Map maxMap = (Map) db.findOne(sql, new Object[]{nodepid,
                        node1});
                if (maxMap == null || maxMap.isEmpty()) {
                    sn = 1;
                } else {
                    BigDecimal bsn = (BigDecimal) maxMap.get("sn");
                    if (bsn == null) {
                        sn = 1;
                    } else {
                        sn = bsn.intValue() + 1;
                    }

                }
                Date nowdate = new Date();
                String cdate = DateUtil.dateToString(nowdate, "yyyy-MM-dd");
                modelMap.put("cman", cman);
                modelMap.put("cdate", cdate);
                modelMap.put("sn", sn);
            } else {// 修改；查看
                String sugestid1 = request.getParameter("sugestid");// 当前修改节点
                String sn = "";
                sql = "select s.sugestid,s.classname,s.sugesttext,s.parentid,s.doctorid,to_char(s.cdate,'yyyy-mm-dd') as cdate,s.cman,s.sn,s.pgpd,s.yxyx from pexam_sugests s where s.sugestid=? and s.hosnum=? ";
                Map psMap = (Map) db.findOne(sql, new Object[]{sugestid1,
                        node1});
                if (psMap != null && !psMap.isEmpty()) {
                    String sugestid = (String) psMap.get("sugestid");
                    String classname = (String) psMap.get("classname");
                    String cman = (String) psMap.get("cman");
                    String cdate = (String) psMap.get("cdate");
                    BigDecimal bdsn = (BigDecimal) psMap.get("sn");
                    if (bdsn != null) {
                        sn = String.valueOf(bdsn.intValue());
                    }
                    String sugesttext = (String) psMap.get("sugesttext");
                    String parentid = (String) psMap.get("parentid");
                    String pgpd = psMap.get("pgpd") == null ? "" : psMap.get(
                            "pgpd").toString();
                    String yxyx = psMap.get("yxyx") == null ? "" : psMap.get(
                            "yxyx").toString();
                    modelMap.put("sugestid", sugestid1);
                    modelMap.put("classname", classname);
                    modelMap.put("cman", cman);
                    modelMap.put("cdate", cdate);
                    modelMap.put("sn", sn);
                    modelMap.put("sugesttext", sugesttext);
                    modelMap.put("parentid", parentid);
                    modelMap.put("hosnum", hosnum);
                    modelMap.put("hosnum1", hosnum1);
                    modelMap.put("pgpd", pgpd);
                    modelMap.put("yxyx", yxyx);
                }
            }
            modelMap.put("node1", node1);
            modelMap.put("node2", node2);
            modelMap.put("nodeid", nodeid);
            modelMap.put("nodepid", nodepid);
            modelMap.put("butType", butType);
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("phyexam/pexamSugestAdd", modelMap);
    }

    // 体检建议 删除按钮
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/pexamSugestRemove", method = RequestMethod.POST)
    public void pexamSugestRemove(HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();

        String checkIds = request.getParameter("checkIds");
        String node1 = request.getParameter("node1");
        String node2 = request.getParameter("node2");
        String[] Ids = checkIds.split(",");

        PrintWriter pw = null;
        DBOperator db = null;

        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "";

            List<Object[]> pi = new ArrayList<Object[]>();
            for (int i = 0; i < Ids.length; i++) {
                String sugestid = Ids[i];
                sql = " delete from pexam_sugests s where s.sugestid=? and s.hosnum=?";
                db.excute(sql, new Object[]{sugestid, node1});
                sql = "delete pexam_sugests s where s.parentid=? and s.hosnum=? ";
                db.excute(sql, new Object[]{sugestid, node1});
            }

            db.commit();
            pw.print("success");
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            pw.print("fail");
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    // 体检建议 诊断名称重复验证
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/ClassNameCheckRepeat", method = RequestMethod.POST)
    public void ClassNameCheckRepeat(HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String classname = request.getParameter("classname");
        String node1 = request.getParameter("node1");
        PrintWriter pw = null;
        pw = response.getWriter();
        if (StrUtil.strIsNotEmpty(classname)) {
            DBOperator db = null;
            try {
                db = new DBOperator();
                List<Map> rowidList = null;
                String sql = "select s.* from pexam_sugests s where s.hosnum = ? and s.classname =? and s.pgpd is null and s.yxyx is null union all select s.* from pexam_sugests s where s.hosnum = ? and s.classname =? and (s.pgpd is not null or s.yxyx is not null)  ";
                rowidList = db.find(sql, new Object[]{node1, classname,
                        node1, classname});
                int size = rowidList.size();
                if (size >= 2) {
                    pw.print("Y");
                } else {
                    pw.print("N");
                }
                db.commit();
            } catch (Exception e) {
                e.printStackTrace();
                db.rollback();
                pw.print("fail");
            } finally {
                db.freeCon();
            }
        } else {
            pw.print("N");
        }
        pw.flush();
        pw.close();
    }

    // 体检建议 新增保存
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/ClassNameSave", method = RequestMethod.POST)
    public void ClassNameSave(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        Bas_user basuser = (Bas_user) request.getSession().getAttribute(
                "login_user");
        String doctorid = basuser.getJob_no();// 创建建议医生id

        String butType = request.getParameter("butType");// 操作类型：add（新增）,modify（修改）
        String node1 = request.getParameter("node1");
        String node2 = request.getParameter("node2");
        String nodepid = request.getParameter("nodepid");
        String classname = request.getParameter("classname").trim();
        String cman = request.getParameter("cman");
        Date cdate = new Date();// 创建时间
        String sn = request.getParameter("sn");// 显示顺序
        String sugesttext = request.getParameter("sugesttext");// 诊断建议
        String pgpd = request.getParameter("pgpd");
        String yxyx = request.getParameter("yxyx");

        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "";
            if ("add".equals(butType)) {

                UUIDGenerator uuid = new UUIDGenerator();
                String nextSid = uuid.generate().toString();
                // String
                // pybm=WordUtil.trans2PyCode(classname.trim()).toLowerCase();
                String pybm = WordUtil.trans2PyCode(classname.trim())
                        .toUpperCase();
                sql = "insert into pexam_sugests(sugestid,classname,pybm,sugesttext,parentid,hosnum,nodecode,doctorid,cdate,cman,sn,pgpd,yxyx) values(?,?,?,?,?,?,?,?,?,?,?,?,?) ";
                db.excute(sql, new Object[]{nextSid, classname, pybm,
                        sugesttext, nodepid, hosnum, nodecode, doctorid, cdate,
                        cman, sn, pgpd, yxyx});
                pw.print(nextSid);
            } else {
                System.out.println("修改++++++++++++++++");
                String sugestid = request.getParameter("sugestid");
                int snint = Integer.parseInt(sn);
                // String
                // pybm=WordUtil.trans2PyCode(classname.trim()).toLowerCase();
                String pybm = WordUtil.trans2PyCode(classname.trim())
                        .toUpperCase();
                sql = "update pexam_sugests s set s.classname=?,s.pybm=?,s.sugesttext=?,s.sn=?,s.pgpd=?,s.yxyx=?  where s.sugestid=? and s.hosnum=? and nodecode=?";
                db.excute(sql, new Object[]{classname, pybm, sugesttext,
                        snint, pgpd, yxyx, sugestid, hosnum, nodecode});

            }
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            pw.print("fail");
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    /*
	 * 体检建议后台管理 搜索
	 */
    @RequestMapping(value = "/getsugestlike", method = RequestMethod.POST)
    public void getsugestlike(HttpServletRequest request,
                              HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();

        PrintWriter pw = null;
        DBOperator db = null;
        String sel_1 = request.getParameter("sel");
        String sel = "";
        if (sel_1.length() > 0) {
            // sel=sel_1.toLowerCase();
            sel = sel_1.toUpperCase();
        }
        String sql = "select s.sugestid,s.classname,s.pybm,s.sugesttext,s.parentid,to_char(s.cdate,'yyyy-mm-dd') as cdate,s.cman,s.sn,s.hosnum from pexam_sugests s where s.hosnum=? and (s.classname like '%"
                + sel
                + "%' or s.pybm like '%"
                + sel
                + "%') order by s.sn,s.sugestid";
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String temp = "";
            List<Map> backsugest = db.find(sql, new Object[]{hosnum});
            JSONArray jsons = JSONArray.fromObject(backsugest);
            System.out.println(jsons.toString());
            request.setAttribute("sugests", jsons.toString());
            pw.print(jsons.toString());
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            request.setAttribute("fail", "fail");

        } finally {
            db.freeCon();
        }
    }

    // 体检建议 刷新grid
    @RequestMapping(value = "/reLoadGrid")
    public void reLoadGrid(HttpServletRequest request,
                           HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();

        String nodeid = request.getParameter("nodeid");// 节点id
        String nodepid = request.getParameter("nodepid");// 父节点id
        String nodetype = request.getParameter("nodetype");// 节点类型
        String sql = null;

        PrintWriter pw = null;
        DBOperator db = null;
        List<Map> suglist = new ArrayList<Map>();
        try {
            pw = response.getWriter();
            db = new DBOperator();

            if (nodetype.equals("true")) {
                sql = "select s.sugestid,s.classname,s.pybm,s.sugesttext,s.parentid,to_char(s.cdate,'yyyy-mm-dd') as cdate,s.cman,s.sn from pexam_sugests s where s.parentid=? and s.hosnum=? and s.nodecode=? order by s.sn,s.sugestid ";
            } else {
                sql = "select s.sugestid,s.classname,s.pybm,s.sugesttext,s.parentid,to_char(s.cdate,'yyyy-mm-dd') as cdate,s.cman,s.sn from pexam_sugests s where s.sugestid=? and s.hosnum=? and s.nodecode=? order by s.sn,s.sugestid ";
            }
            suglist = db.find(sql, new Object[]{nodeid, hosnum, nodecode});
            // System.out.println("-------------------"+suglist.size());
            JSONArray jsons = JSONArray.fromObject(suglist);
            pw.print(jsons.toString());

            db.commit();
            // System.out.println(jsons.toString());
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            pw.print("fail");
        } finally {
            db.freeCon();
        }
    }

    // 通过最大的子节点获取下一个字符型的id
    public String NumToSugestid(String maxsid) {
        String headSid = maxsid.substring(0, 1);
        String befSid = maxsid.substring(1, 6);
        String endSid = maxsid.substring(6, maxsid.length());
        // System.out.println(headSid+";"+befSid+";"+endSid);
        int befsidnum = Integer.parseInt(befSid) + 1;
        String nextsid = String.valueOf(befsidnum);
        int nextlen = nextsid.length();
        if (nextlen < 6) {
            for (int i = 1; i < 6 - nextlen; i++) {
                nextsid = "0" + nextsid;
            }
        }
        nextsid = headSid + nextsid + endSid;
        return nextsid;

    }

    // 获取下一级字母
    public String getNextHeadid(String maxsid) {
        char headSid = maxsid.charAt(0);
        int headSidnum = (int) headSid + 1;
        // System.out.println("数字："+headSidnum);
        char nextHeadChar = (char) headSidnum;
        String nextHeadSid = String.valueOf(nextHeadChar);
        return nextHeadSid;
    }

    /*
	 * 获取接检页面要加载名单的数量 搜索 2013-3-16
	 */
    @RequestMapping(value = "/getPatientListCount", method = RequestMethod.POST)
    public void getrepatientlistCount_xj(HttpServletRequest request,
                                         HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String method = request.getParameter("method");
        String examid = request.getParameter("examid");// 如果不是团体则就是为"0000"
        ReturnValue returnValue = new ReturnValue();
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "";
            String startSql = "select count(*) as count from (";
            String endSql = ")";
            sql = "select a.*,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid and b.excdate is not null) as fin from pexam_mans a where a.hosnum=? and a.examid=? and a.edate is null";
            if ("loadBySearch".equals(method)) {
                String searchvalue = request.getParameter("searchvalue");
                if (searchvalue != null && searchvalue != "") {
                    sql += " and (instr(a.inputcpy,'"
                            + searchvalue.toUpperCase()
                            + "')>0 or instr(a.inputcwb,'"
                            + searchvalue.toUpperCase()
                            + "')>0 or instr(a.pexamid,'" + searchvalue
                            + "')>0 or instr(a.patname,'" + searchvalue
                            + "')>0 or instr(a.idnum,'" + searchvalue
                            + "')>0 or instr(a.ybbh,'" + searchvalue + "')>0)";
                }
            } else if ("loadByPexamid".equals(method)) {// 根据体检编号
                String loadByPexamid = request.getParameter("loadByPexamid");
                sql += " and a.pexamid='" + loadByPexamid + "' ";
            } else if ("loadByIdNum".equals(method)) {// 根据身份证id
                String loadByIdNum = request.getParameter("loadByIdNum");
                sql += " and a.idnum='" + loadByIdNum + "' ";
            }
            sql = startSql + sql + endSql;
            Map<String, Integer> countMap = (Map<String, Integer>) db.findOne(
                    sql, new Object[]{hosnum, examid});
            returnValue.setStatus(true);
            returnValue.setValue(String.valueOf(countMap.get("count")));
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            returnValue.setStatus(false);
            returnValue.setMessage("查询总数失败！");
        } finally {
            db.freeCon();
        }
        pw.print(JSONObject.fromObject(returnValue).toString());
        pw.flush();
        pw.close();
    }

    // 自助体检
    @SuppressWarnings("unchecked")
    @RequestMapping("/selfTJ")
    public ModelAndView selfTJ(HttpServletRequest request,
                               HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute(
                "login_user");
        modelMap.put("hosname", basHospitals.getHosname());
        modelMap.put("doctorname", basUser.getName());
        return new ModelAndView("pexam/selfTJ", modelMap);
    }

    // 总检页面体检人员信息
    @RequestMapping(value = "/loadTJPersonInfo")
    public void loadTJPersonInfo(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String pexamid = request.getParameter("pexamid");
        System.out.println("======>" + pexamid);
        PrintWriter pw = null;
        DBOperator db = null;
        List<Map> list = new ArrayList<Map>();
        String sql = "";
        try {
            pw = response.getWriter();
            db = new DBOperator();
            // sql="select s.patname,s.sex,trunc(months_between(sysdate,s.dateofbirth)/12) age,s.idnum,s.phonecall,s.village,s.address,to_char(s.bdate,'yyyy-MM-dd') bdate from pexam_mans s where s.pexamid=? ";
            sql = "select s.patname,s.sex,to_char(sysdate,'yyyy')-to_char(s.dateofbirth,'yyyy') as age,s.idnum,s.phonecall,s.village,s.address,to_char(s.bdate,'yyyy-MM-dd') bdate,examtype from pexam_mans s where s.pexamid=? ";
            list = db.find(sql, new Object[]{pexamid});

            // ----------更新人员的打印标志------------
            String sql_man = "update pexam_mans m set m.isprint='Y' where m.hosnum=? and m.pexamid=? ";
            db.excute(sql_man, new Object[]{hosnum, pexamid});

            JSONArray jsons = JSONArray.fromObject(list);
            pw.print(jsons.toString());

            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            pw.print("fail");
        } finally {
            db.freeCon();
        }
    }

    @RequestMapping(value = "/saveOrUpdate")
    public void saveOrUpdarePexamInfo(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String pexamid = request.getParameter("pexamid");
        String village = request.getParameter("village");
        String address = request.getParameter("address");
        String phonecall = request.getParameter("phonecall");

        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "update pexam_mans set village=?,address=?,phonecall=? where pexamid=?";
            db.excute(sql,
                    new Object[]{village, address, phonecall, pexamid});
            db.commit();
            pw.print("true");
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            pw.print("false");
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    /*
	 * 体检明细指标
	 */
    @RequestMapping("/pexamItemsDetailValue_stu")
    public ModelAndView pexamItemsDetailValue_stu(HttpServletRequest request,
                                                  HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute(
                "login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute(
                "login_user");
        String hosnum = basHospitals.getHosnum();

        String examid = request.getParameter("examid");
        String pexamid = request.getParameter("pexamid");

        DBOperator db = null;
        try {
            db = new DBOperator();
            String bdate = "";
            String birthday = "";
            List itemsList = new ArrayList();
            Map dateMap = new HashMap();
            System.out.println("获取“其他”体检项目");
            String sql = "select b.*,a.excdate,a.excdoctorname from pexam_items_title a,pexam_items_com b where a.hosnum=b.hosnum and a.itemcode=b.comid and a.hosnum=? and a.examid=? and a.pexamid=? and b.comclass='其他' order by b.sn";
            List itemsDefList = db.find(sql, new Object[]{hosnum, examid,
                    pexamid});
            if ((itemsDefList != null) && (itemsDefList.size() > 0)) {

                sql = "select * from pexam_mans a where a.hosnum=? and a.pexamid=? and a.examid=?";
                List list = db.find(sql,
                        new Object[]{hosnum, pexamid, examid});
                Map map = (Map) list.get(0);
                bdate = DateUtil.dateToString((Date) map.get("bdate"),
                        "yyyy-MM-dd");
                birthday = DateUtil.dateToString((Date) map.get("dateofbirth"),
                        "yyyy-MM-dd");

                dateMap.put("bdate", bdate);
                dateMap.put("birthday", birthday);
                String sex = (String) map.get("sex");
                System.out.println("获取类型是“其他”的体检项目指标");
                sql = "select b.indname as detailname,b.resultunit,a.comid,a.result from pexam_results a,pexam_items_ind b,pexam_items_com c where a.hosnum=b.hosnum and a.indid=b.indid and a.hosnum=? and a.examid=? and a.pexamid=? and a.hosnum=c.hosnum and c.comid=a.comid and c.comclass='其他'order by b.sn";
                List itemsDetList = db.find(sql, new Object[]{hosnum, examid,
                        pexamid});
                for (int i = 0; i < itemsDefList.size(); ++i) {
                    Map itemsMap = new HashMap();
                    List temp = new ArrayList();

                    Map itemsDefMap = (Map) itemsDefList.get(i);
                    String itemcode = (String) itemsDefMap.get("comid");
                    String itemname = (String) itemsDefMap.get("comname");
                    if (itemcode.equals("6626")) {
                        itemname = "身高、体重、肺活量";
                    } else if (itemcode.equals("6627")) {
                        itemname = "内科";
                    } else if (itemcode.equals("6628")) {
                        itemname = "眼科";
                    } else if (itemcode.equals("6629")) {
                        itemname = "口腔";
                    } else if (itemcode.equals("6630")) {

                    } else if (itemcode.equals("6631")) {
                        itemname = "谷丙转氨酶";
                    } else if (itemcode.equals("6632")) {
                        itemname = "外科";
                    }
                    int feihuoliang = 0;
                    int weight = 1;
                    int feiweight = 1;
                    String ratioandlevel = "";
                    double height = 1;
                    double weightheight = 1;
                    String BMIandlevel = "";
                    DecimalFormat df = new DecimalFormat(".0");
                    double left = 0;
                    double right = 0;
                    String vision = "";
                    for (int j = 0; j < itemsDetList.size(); ++j) {
                        Map itemsDetMap = (Map) itemsDetList.get(j);
                        if (itemsDetMap.get("detailname").equals("肺活量")) {
                            feihuoliang = Integer.parseInt((String) itemsDetMap
                                    .get("result"));
                        }
                        if (itemsDetMap.get("detailname").equals("体重")) {
                            weight = Integer.parseInt((String) itemsDetMap
                                    .get("result"));
                        }
                        feiweight = feihuoliang / weight;
                        if (itemsDetMap.get("detailname").equals("身高")) {
                            height = Double.parseDouble((String) itemsDetMap
                                    .get("result")) / 100;
                        }
                        weightheight = Double.parseDouble(df
                                .format((weight / (height * height))));
                        if (itemsDetMap.get("detailname").equals("视力（右）")) {
                            right = Double.parseDouble((String) itemsDetMap
                                    .get("result"));
                        }
                        if (itemsDetMap.get("detailname").equals("视力（左）")) {
                            left = Double.parseDouble((String) itemsDetMap
                                    .get("result"));
                        }
                    }
                    for (int j = 0; j < itemsDetList.size(); ++j) {
                        Map itemsDetMap = (Map) itemsDetList.get(j);
                        Map map2 = new HashMap();
                        boolean flag = false;
                        String itemcode2 = (String) itemsDetMap.get("comid");
                        if (itemcode.equals(itemcode2)) {
                            if (itemsDetMap.get("result") == null) {
                                itemsDetMap.put("resultunit", "");
                            }
                            if (j == itemsDetList.size() - 1
                                    && itemcode.equals("6626")) {
                                Map map0 = new HashMap();
                                map0.put("detailname", "肺活量体重指数");
                                map0.put("resultunit", "");
                                map0.put("comid", "6626");
                                if (sex.equals("男")) {
                                    if (feiweight >= 75) {
                                        ratioandlevel = feiweight + " 优秀";
                                    } else if (feiweight >= 64) {
                                        ratioandlevel = feiweight + " 良好";
                                    } else if (feiweight >= 54) {
                                        ratioandlevel = feiweight + " 及格";
                                    } else if (feiweight > 0) {
                                        ratioandlevel = feiweight + " 不及格";
                                    } else {
                                        ratioandlevel = feiweight + " ";
                                    }
                                } else if (sex.equals("女")) {
                                    if (feiweight >= 70) {
                                        ratioandlevel = feiweight + " 优秀";
                                    } else if (feiweight >= 57) {
                                        ratioandlevel = feiweight + " 良好";
                                    } else if (feiweight >= 44) {
                                        ratioandlevel = feiweight + " 及格";
                                    } else if (feiweight > 0) {
                                        ratioandlevel = feiweight + " 不及格";
                                    } else {
                                        ratioandlevel = feiweight + " ";
                                    }
                                }
                                map0.put("result", ratioandlevel);
                                temp.add(map0);
                                Map map1 = new HashMap();
                                map1.put("detailname", "BMI（身体体质指数）");
                                map1.put("resultunit", "");
                                map1.put("comid", "6626");
                                if (weightheight >= 30) {
                                    BMIandlevel = weightheight + " 重度肥胖";
                                } else if (weightheight >= 27) {
                                    BMIandlevel = weightheight + " 肥胖";
                                } else if (weightheight >= 24) {
                                    BMIandlevel = weightheight + " 超重";
                                } else if (weightheight >= 18) {
                                    BMIandlevel = weightheight + " 正常";
                                } else if (weightheight > 0) {
                                    BMIandlevel = weightheight + " 偏瘦";
                                } else {
                                    BMIandlevel = weightheight + " ";
                                }
                                map1.put("result", BMIandlevel);
                                temp.add(map1);
                            }
                            System.out.println(j + "============"
                                    + (itemsDetList.size() - 1)
                                    + "============" + itemcode);
                            if (itemcode.equals("6628")) {
                                if (itemsDetMap.get("detailname").equals(
                                        "视力（左）")) {
                                    map2 = new HashMap();
                                    map2.put("detailname", "视力状况");
                                    map2.put("resultunit", "");
                                    map2.put("comid", "6628");
                                    if (left >= 4.9) {
                                        vision = "正常";
                                    } else if (left >= 4.6) {
                                        vision = "轻度低下";
                                    } else if (left >= 4.4) {
                                        vision = "中度低下";
                                    } else {
                                        vision = "重度低下";
                                    }
                                    map2.put("result", vision);
                                    flag = true;
                                    // temp.add(map2);
                                }
                                if (itemsDetMap.get("detailname").equals(
                                        "视力（右）")) {
                                    map2 = new HashMap();
                                    map2.put("detailname", "视力状况");
                                    map2.put("resultunit", "");
                                    map2.put("comid", "6628");
                                    if (right >= 4.9) {
                                        vision = "正常";
                                    } else if (right >= 4.6) {
                                        vision = "轻度低下";
                                    } else if (right >= 4.4) {
                                        vision = "中度低下";
                                    } else {
                                        vision = "重度低下";
                                    }
                                    map2.put("result", vision);
                                    flag = true;
                                    // temp.add(map2);
                                }

                                System.out
                                        .println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
                                                + map2);
                            }
                            temp.add(itemsDetMap);
                            if (flag) {
                                temp.add(map2);
                                flag = false;
                            }
                        }
                    }
                    itemsMap.put("itemname", itemname);
                    itemsMap.put("itemsDet", temp);
                    itemsMap.put("excdoctorname", itemsDefMap
                            .get("excdoctorname"));

                    itemsList.add(itemsMap);
                }
            }

            sql = "select a.*,to_char(sysdate,'yyyy')-to_char(a.dateofbirth,'yyyy') as age from pexam_mans a where a.hosnum=? and a.pexamid=? and a.bdate is not null";
            PexamMans patMap = (PexamMans) db.findOne(sql, new Object[]{
                    hosnum, pexamid}, PexamMans.class);
            modelMap.put("patInfo", patMap);
            modelMap.put("itemsList", itemsList);
            modelMap.put("dateMap", dateMap);
            db.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("pexam/pexamItemsDetailValue_stu", modelMap);
    }


    // 传染病上报 填报页面
    @RequestMapping("/showcontagionrpt")
    public String showcontagionrpt(HttpServletRequest request, ModelMap model)
            throws Exception {
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute(
                "login_user");
        SimpleDateFormat smf = new SimpleDateFormat();
        String id = request.getParameter("id");
        String sql = "";
        String type = request.getParameter("type");
        String pexamid = request.getParameter("pexamid"); // 体检id
        String view = "";
        model.addAttribute("type", type);
        DBOperator db = new DBOperator();
        try {
            if (type.equals("doreadd") || type.equals("modify")) {
                sql = "select * from clc_contagionrpt t where t.rptid=? ";
                List<Map<String, Object>> list = db.find(sql, id);
                String professional = (String) list.get(0).get("professional");
                String distype = (String) list.get(0).get("distype");
                String distype2 = (String) list.get(0).get("distype2");
                model.addAttribute("professional", professional);
                model.addAttribute("distype", distype);
                model.addAttribute("distype2", distype2);
                model.addAttribute("pexamid", pexamid);
                model.addAttribute("con", list.get(0));
                view = "pexam/contagionRPT";
            } else if (!("".equals(id) || id == null)) {
                sql = "select * from clc_contagionrpt t where t.rptid=? ";
                List<Map<String, Object>> list = db.find(sql, id);
                String receptid = (String) list.get(0).get("receptman");
                if (receptid == null || receptid.isEmpty()) {
                    model.addAttribute("ishand", "no");
                } else {
                    model.addAttribute("ishand", "yes");
                }
                model.addAttribute("con", list.get(0));
                model.addAttribute("pexamid", pexamid);
                view = "pexam/contagionrptshow";
            } else {
                String dtmainid = request.getParameter("dtMainId");
                String patientId = request.getParameter("patientId");
                String diagNo = request.getParameter("diagNo");
                String diagname = URLDecoder.decode(request
                        .getParameter("diagName"), "utf-8");
                Map<String, Object> map = new HashMap<String, Object>();
                // 查找病人的一些基本信息 查体检人员表
                sql = "select * from pexam_mans t where t.pexamid=?";
                List<Map<String, Object>> list1 = db.find(sql,
                        new Object[]{pexamid});
                if (list1.get(0).get("patientid") == null) {
                    throw new Exception("体检人员表中病人标识号为空！");
                } else {
                    patientId = list1.get(0).get("patientid").toString();
                }
                sql = "select * from bas_patients t where t.patientid=?  ";
                List<Map<String, Object>> list = db.find(sql,
                        new Object[]{patientId});
                if (!list.get(0).get("dateofbirth").toString().isEmpty()) {
                    Date birth = (Date) list.get(0).get("dateofbirth");
                    Date now = new Date();
                    int yearsold = now.getYear() - birth.getYear() + 1;
                    list.get(0).put("yearsold", yearsold);
                }
                list.get(0).put("doctorid", basUser.getId());
                list.get(0).put("doctorname", basUser.getName());
                list.get(0).put("dtmainid", dtmainid);
                list.get(0).put("diagid", diagNo);
                list.get(0).put("diagname", diagname);
                model.addAttribute("pexamid", pexamid);
                model.addAttribute("ishand", "show");
                Map newmap = new HashMap();
                System.out.println(list.get(0));
                model.addAttribute("con", list.get(0));
                view = "pexam/contagionRPT";
            }
            sql = "select * from bas_dicts t where t.nekey=135";
            List<Bas_dicts> job_dict = db.find(sql, Bas_dicts.class);
            model.addAttribute("job_dict", job_dict);

        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            model.addAttribute("msg", e.getMessage());
        } finally {
            db.freeCon();
        }
        return view;
    }


    public static Date strToDate(String str, String format) {
        if (str == null) {
            return null;
        }
        SimpleDateFormat sf = new SimpleDateFormat(format);
        try {
            return sf.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    // 体检小项 常见结果维护 commonresults
    @RequestMapping(value = "/toshowcommonresults")
    public ModelAndView toshowcommonresults(HttpServletRequest request,
                                            HttpServletResponse response) {
        Map map = new HashMap();
        map.put("menuid", "");
        return new ModelAndView("pexam/indcommonresult", map);
    }

    /*
	 * 体检小项 常见结果维护 加载体检项目树
	 */
    @RequestMapping("/itemsIndTreecommonresults")
    public void loadLabTree(HttpServletRequest request,
                            HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosum = basHospitals.getHosnum();
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();

            List<String> lstTree = new ArrayList<String>();
            String temp = "";
            String s1 = "{id:\"0\", pId:-1, name:\"体检项目\",open:true}";
            lstTree.add(s1);

            String sql = "";
            // 大项
            sql = "select a.typeid,a.typename from pexam_items_type a where a.hosnum='"
                    + hosum + "' order by a.sn";
            List tempList = db.find(sql);
            for (Iterator iterator = tempList.iterator(); iterator.hasNext(); ) {
                Map tempMap = (Map) iterator.next();
                temp = "{id:\"" + tempMap.get("typeid") + "\"," + "pId:\"0\","
                        + "name:\"" + tempMap.get("typename") + "\"}";
                lstTree.add(temp);
            }
            // 小项
            sql = "select a.indid,a.indname,a.parentid,a.resulttype from pexam_items_ind a where a.hosnum='"
                    + hosum + "' order by a.sn";
            tempList = db.find(sql);
            for (Iterator iterator = tempList.iterator(); iterator.hasNext(); ) {
                Map tempMap = (Map) iterator.next();
                temp = "{id:\"" + tempMap.get("indid") + "\"," + "pId:\""
                        + tempMap.get("parentid") + "\"," + "ifind:\"" + "y"
                        + "\"," + "resulttype:\"" + tempMap.get("resulttype")
                        + "\"," + "name:\"" + tempMap.get("indname") + "\"}";
                lstTree.add(temp);
            }
            // 小项的常见结果
            // sql =
            // "select a.indid,a.result,a.comresid from pexam_ind_result a  where a.hosnum='"+hosum+"' order by a.indid";
            // tempList = db.find(sql);
            // for(Iterator iterator = tempList.iterator(); iterator.hasNext();)
            // {
            // Map tempMap = (Map)iterator.next();
            // temp = "{id:\"" + tempMap.get("comresid") + "\"," +
            // "pId:\"" + tempMap.get("indid") +"\"," +
            // "name:\"" + tempMap.get("result")+ "\"}";
            // lstTree.add(temp);
            // }

            pw.print(JSONArray.fromObject(lstTree).toString());
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            pw.print("fail");
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    // 获取小项的常见结果
    @RequestMapping(value = "/getindresult")
    public ModelAndView itemsIndList2(HttpServletRequest request,
                                      HttpServletResponse response, ModelMap modelmap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String indid = request.getParameter("indid");
        modelmap.put("indid", indid);
        System.out.println(indid);
        modelmap.put("hosnum", hosnum);
        return new ModelAndView("pexam/getindresult", modelmap);
    }

    // =======导出体检的 总检报告PDF 页面的 方法 =======开始====== lsp 2016-03-20
    // 19:31:50==========================================================================================
    @RequestMapping(value = "/searchExpPDFCount")
    public void searchExpPDFCount(HttpServletRequest request,
                                  HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            Bas_user basUser = (Bas_user) request.getSession().getAttribute(
                    "login_user");
            Bas_dept basDept = (Bas_dept) request.getSession().getAttribute(
                    "login_dept");
            Bas_hospitals basHospital = (Bas_hospitals) request.getSession()
                    .getAttribute("login_hospital");
            String scope = URLDecoder.decode(request.getParameter("scope"),
                    "utf-8");
            String medName = URLDecoder.decode(request.getParameter("medName"),
                    "utf-8");
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");

            StringBuffer sb = new StringBuffer(
                    "select count(1) num  from pexam_mans a where a.bdate is not null  and a.hosnum=? and a.nodecode=? ");

            if (!"".equals(startDate) && "".equals(endDate)) {
                sb.append(" and a.bdate>=to_date('" + startDate
                        + "','yyyy-MM-dd')");
            } else if ("".equals(startDate) && !"".equals(endDate)) {
                sb.append(" and a.bdate<=to_date('" + endDate
                        + "','yyyy-MM-dd')");
            } else if (!"".equals(startDate) && !"".equals(endDate)) {
                sb.append(" and a.bdate>=to_date('" + startDate
                        + "','yyyy-MM-dd') and a.bdate<=to_date('" + endDate
                        + "','yyyy-MM-dd')");
            }
            if (!"".equals(medName)) {
                sb.append(" and (a.patname like '" + medName
                        + "%'  or a.pexamid='" + medName + "' ) ");
            }
            sb.append("");
            String sql = sb.toString();
            List<Map> list = db.find(sql, new Object[]{
                    basHospital.getHosnum(), basHospital.getNodecode()});

            pw.print(list.get(0).get("num"));

            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            pw.print("fail");
        } finally {
            try {
                db.freeCon();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "/searchExpPDFData")
    public void searchExpPDFData(HttpServletRequest request,
                                 HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            Bas_user basUser = (Bas_user) request.getSession().getAttribute(
                    "login_user");
            Bas_dept basDept = (Bas_dept) request.getSession().getAttribute(
                    "login_dept");
            Bas_hospitals basHospital = (Bas_hospitals) request.getSession()
                    .getAttribute("login_hospital");
            String scope = URLDecoder.decode(request.getParameter("scope"),
                    "utf-8");
            String medName = URLDecoder.decode(request.getParameter("medName"),
                    "utf-8");
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");
            int curPage = Integer.valueOf(request.getParameter("curPage"));
            int pageSize = Integer.valueOf(request.getParameter("pageSize"));

            StringBuffer sb = new StringBuffer(
                    "select a.pexamid ,a.patname ,to_char(a.bdate,'yyyy-mm-dd hh24:mi:ss') bdate , p.itemname ,p.cost  , a.isover from pexam_mans a  left join pexam_items p on p.pexamid=a.pexamid  where a.bdate is not null  and p.isgroup='y'  and a.hosnum='"
                            + basHospital.getHosnum()
                            + "' and a.nodecode='"
                            + basHospital.getNodecode() + "'   ");
            if (!"".equals(startDate) && "".equals(endDate)) {
                sb.append(" and a.bdate>=to_date('" + startDate
                        + "','yyyy-MM-dd')");
            } else if ("".equals(startDate) && !"".equals(endDate)) {
                sb.append(" and a.bdate<=to_date('" + endDate
                        + "','yyyy-MM-dd')");
            } else if (!"".equals(startDate) && !"".equals(endDate)) {
                sb.append(" and a.bdate>=to_date('" + startDate
                        + "','yyyy-MM-dd') and a.bdate<=to_date('" + endDate
                        + "','yyyy-MM-dd')");
            }
            if (!"".equals(medName)) {
                sb.append(" and (a.patname like '" + medName
                        + "%'  or a.pexamid='" + medName + "' ) ");
            }
            sb.append("order by a.bdate  desc ");
            String sql = sb.toString();
            String pagingSql1 = "select OHYEAH.* from (select OHNO.*,rownum no from ("; // 分页段1
            String pagingSql2 = ") OHNO where rownum <= ?) OHYEAH where no > ?"; // 分页段2

            List<Map> list = db
                    .find(pagingSql1 + sql + pagingSql2, new Object[]{
                            curPage * pageSize, (curPage - 1) * pageSize});
            JSONArray jsons = JSONArray.fromObject(list);
            pw.print(jsons.toString());

            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            pw.print("fail");
        } finally {
            try {
                db.freeCon();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ====================================================结束==================================================================================================

    /**
     * 医技科室服务量统计
     */
    @RequestMapping("/yjks_fwl")
    public void yjks_fwl(HttpServletRequest request,
                         HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            Map map = new HashMap();
            Bas_user basUser = (Bas_user) request.getSession().getAttribute(
                    "login_user");
            Bas_dept basDept = (Bas_dept) request.getSession().getAttribute(
                    "login_dept");
            Bas_hospitals basHospital = (Bas_hospitals) request.getSession()
                    .getAttribute("login_hospital");
            String startDate = request.getParameter("startDate");
            String endDate = request.getParameter("endDate");
            String sql = "select excdeptname name,sum(num) num from (select count(i.excdeptname) num, i.excdeptname from clc_recipe a left join clc_recipe_details d on d.recipeid = a.recipeid left join exem_sheets e on e.sheetid = d.medcode left join exem_items i on i.itemid = e.itemid where a.recipetype = '检查' and a.chgid is not null and to_char(a.ricipedate, 'yyyy-mm-dd') BETWEEN ? AND ? group by i.excdeptname union select count(i.excdeptname) num, i.excdeptname from inp_orders a left join exem_sheets e on e.sheetid = a.medcode left join exem_items i on i.itemid = e.itemid where to_char(a.ordertime, 'yyyy-mm-dd') BETWEEN ? AND ? AND a.ordertype IN ('检查') group by i.excdeptname union select count(c.excdeptname) num, c.excdeptname from pexam_items_title a left join pexam_items_com c on c.comid = a.itemcode where a.comclass = '检查' and to_char(a.sheetdate, 'yyyy-mm-dd') BETWEEN ? AND ? and a.status = '1' and a.invoice_no is not null and c.sheettype is not null group by c.excdeptname) group by excdeptname ";
            List<Map> list = db.find(sql, new Object[]{startDate, endDate});
            map.put("gnks", list); // 功能科室数量

            JSONObject jsons = JSONObject.fromObject(map);
            pw.print(jsons.toString());
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            pw.print("fail");
        } finally {
            try {
                db.freeCon();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
