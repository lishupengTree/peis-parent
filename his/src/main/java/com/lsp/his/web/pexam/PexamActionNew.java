package com.lsp.his.web.pexam;

import com.lsp.his.db.DBOperator;
import com.lsp.his.exception.MwInventoryException;
import com.lsp.his.model.ReturnValue;
import com.lsp.his.tables.Bas_dept;
import com.lsp.his.tables.Bas_hospitals;
import com.lsp.his.tables.Bas_user;
import com.lsp.his.tables.pexam.*;
import com.lsp.his.utils.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
import java.sql.Clob;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/12 9:39
 */

@Controller
@RequestMapping("/pexamNew")
public class PexamActionNew {

    public static int oracleType = 11;// 数据库类型
    //public static org.apache.cxf.endpoint.Client client = JaxWsDynamicClientFactory.newInstance().createClient("http://192.168.1.190:8080/platform/services/lis?wsdl");
    //public static org.apache.cxf.endpoint.Client client = null;
    public static final Map<String, String> hosMap = new HashMap<String, String>();

    static {
        hosMap.put("8001_8001", "192.11.1.252:8080");
        hosMap.put("8002_8002", "192.15.1.252:8080");
        //hosMap.put("8003_8003", "192.1.1.252:8080");
        hosMap.put("8003_8003", "192.1.5.253:7070");
        hosMap.put("8004_8004", "192.17.1.250:8080");
        hosMap.put("8005_8005", "192.12.1.252:8080");
        hosMap.put("8006_8006", "192.71.1.150:7080");
        hosMap.put("8007_8007", "192.23.1.252:6060");
        hosMap.put("8008_8008", "192.29.1.252:8080");
        hosMap.put("8009_8009", "192.41.1.250:8080");
        hosMap.put("8010_8010", "192.14.1.68:8080");
        hosMap.put("8011_8011", "192.21.1.252:8080");

        hosMap.put("8012_8012", "192.16.1.252:6060");//长河
        hosMap.put("8013_8013", "192.20.1.252:6060");//掌起
        //hosMap.put("8014_8014", "192.18.1.119:6060");//天元
        hosMap.put("8014_8014", "192.18.1.188:6060");//天元
        hosMap.put("8015_8015", "192.25.1.252:8080");//崇寿
        hosMap.put("8016_8016", "192.19.1.1:6060");//龙山三北
        hosMap.put("8017_8017", "192.22.1.241:6060");//新浦
        hosMap.put("8018_8018", "192.26.1.252:6060");//附海
        hosMap.put("8019_8019", "192.27.1.243:6060");//桥头
    }

    /************************预约服务页面，所有相关操作(2012-12-18)开始**************************/
    //TODO
    /*
     * 体检人员信息导入---预约服务页面
	 * 2012-12-18
	 */
    @RequestMapping("/importPersonnel")
    public ModelAndView show_importPersonnel(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        String examid = request.getParameter("examid");
        String operatorResult = request.getParameter("operatorResult");
        String method = request.getParameter("method");
        modelMap.put("method", method);
        modelMap.put("examid", examid);
        modelMap.put("operatorResult", operatorResult);
        return new ModelAndView("pexam/importPersonnel");
    }

    //跳转到 回退的页面
    @RequestMapping("/toHuiTui")
    public ModelAndView toHuiTui(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals bh = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = bh.getHosnum();
        String itemcode = request.getParameter("itemcode");
        String pexamid = request.getParameter("pexamid");
        DBOperator db = null;
        String sql = "";
        List<Map> list = null;
        try {
            db = new DBOperator();
            sql = "select * from pexam_items_title a  where a.hosnum='" + hosnum + "' and a.pexamid='" + pexamid + "' and a.itemcode='" + itemcode + "'";
            list = db.find(sql);
            if (ListUtil.listIsNotEmpty(list)) {
                String xmstatus = list.get(0).get("xmstatus").toString();
                if ("完成".equals(xmstatus)) {
                    modelMap.put("itemcode", itemcode);
                    modelMap.put("pexamid", pexamid);
                } else {
                    //查看有木有回退过（可能有多次回退）   有就显示回退原因页面 且不能点击保存按钮， 没有跳到异常页面。
                    sql = "select   *  from PEXAM_TJXMBACK a where a.hosnum='" + hosnum + "' and a.pexamid='" + pexamid + "' and a.itemcode='" + itemcode + "'  order by a.back_date desc  ";
                    list = db.find(sql);
                    if (ListUtil.listIsNotEmpty(list)) {
                        modelMap.put("reason", list.get(0).get("backreason").toString());
                        modelMap.put("flag", "have");
                    } else {
                        modelMap.put("error", "此项目是未完成状态，无需回退！");
                        return new ModelAndView("pexam/HuiTuiErrorPage");
                    }
                }
            } else {
                modelMap.put("error", "体检项目不存在！请联系管理员。");
                return new ModelAndView("pexam/HuiTuiErrorPage");
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("pexam/HuiTuiPage");
    }

    @RequestMapping("/report_tjysz")
    public ModelAndView report_tjysz(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals bh = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = bh.getHosnum();
        String itemcode = request.getParameter("itemcode");
        String pexamid = request.getParameter("pexamid");
        DBOperator db = null;
        String sql = "";
        List<Map> list = null;
        try {
            db = new DBOperator();
            sql = "select * from pexam_items_title a  where a.hosnum='" + hosnum + "' and a.pexamid='" + pexamid + "' and a.itemcode='" + itemcode + "'";
            list = db.find(sql);
            if (ListUtil.listIsNotEmpty(list)) {
                String xmstatus = list.get(0).get("xmstatus").toString();
                String itemuuid = list.get(0).get("itemuuid").toString();
                sql = "select  *  from PEXAM_TJXMreport a where a.hosnum='" + hosnum + "' and a.itemuuid='" + itemuuid + "'     ";
                list = db.find(sql);
                if (ListUtil.listIsNotEmpty(list)) {
                    modelMap.put("reason", list.get(0).get("reportreason").toString());
                    modelMap.put("flag", "have");
                } else {
                    modelMap.put("reason", "");
                }
            } else {
                modelMap.put("error", "体检项目不存在！请联系管理员。");
                return new ModelAndView("pexam/HuiTuiErrorPage");
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("pexam/tjysz_reportPage");
    }

    //保存体检项目回退的原因
    @RequestMapping(value = "/saveBackReason", method = RequestMethod.POST)
    public void saveBackReason(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_user bu = (Bas_user) request.getSession().getAttribute("login_user");
        String userid = bu.getId();
        String username = bu.getName();
        String hosnum = basHospitals.getHosnum();
        String itemcode = request.getParameter("itemcode");
        String pexamid = request.getParameter("pexamid");
        String backreason = request.getParameter("backreason");
        String itemuuid = request.getParameter("itemuuid");
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            //插入回退表
            db.excute("insert into PEXAM_TJXMBACK (UUID, HOSNUM, PEXAMID, ITEMCODE, BACKREASON, BACK_DATE, BACK_PERSONID, BACK_PERSONNAME )" +
                    "values (sys_guid(), '" + hosnum + "', '" + pexamid + "', '" + itemcode + "', '" + backreason + "', sysdate, '" + userid + "', '" + username + "'  )");
            //把结果回执为未检 置空 检查时间和医生
            db.excute("update pexam_items_title a set a.excdate =null ,a.excdoctorid=null,a.excdoctorname=null,a.xmstatus='未检'   where a.hosnum='" + hosnum + "'  and a.pexamid='" + pexamid + "'   and a.itemcode='" + itemcode + "'   ");
            //删除结果表  不然会出现多个 结果的情况
//			db.excute("delete from pexam_results a  where a.hosnum=?  and a.pexamid=?  and a.comid=? "
//						,new Object[]{hosnum,pexamid,itemcode });


            db.commit();
            pw.print("回退成功！");
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            pw.print("回退失败！" + e.getMessage());
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    //插入体检项目到临时表（只是为了在未检的状态来打印导检单）
    @RequestMapping(value = "/insertTitle_print_temp", method = RequestMethod.POST)
    public void insertTitle_print_temp(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_user bu = (Bas_user) request.getSession().getAttribute("login_user");
        String userid = bu.getId();
        String username = bu.getName();
        String hosnum = basHospitals.getHosnum();
        String pexamid = request.getParameter("pexamid");
        PrintWriter pw = null;
        DBOperator db = null;
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        String shortname = basHospitals.getShortname();//医院简称
        String deptcode = basDept.getDeptcode();
        String deptname = basDept.getDeptname();
        Timestamp timesTamp = new Timestamp(System.currentTimeMillis());

        try {
            pw = response.getWriter();
            List<Object[]> pi_1 = new ArrayList<Object[]>();
            db = new DBOperator();
            String sql = "";
            List<Map> list = null;
            Map returnMap = new HashMap();
            //查到此人有木有插到title表里
            sql = "select * from pexam_items_title a where a.pexamid=? and a.hosnum=?";
            list = db.find(sql, new Object[]{pexamid, hosnum});
            if (list.size() == 0) {
                insertTitleAlone(request, response, db, returnMap);
            } else {

            }
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
     * 此方法用来插入title表
	 *
	 */
    public void insertTitleAlone(HttpServletRequest request, HttpServletResponse response, DBOperator db, Map returnMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String hosnum = basHospitals.getHosnum();
        String deptcode = basDept.getDeptcode();
        String deptname = basDept.getDeptname();
        String userid = basUser.getId();
        String username = basUser.getName();
        Timestamp timesTamp = new Timestamp(System.currentTimeMillis());
        String pexamid = request.getParameter("pexamid");// 唯一标识--个人体检编号
        String examid = request.getParameter("examid");// 如果是个人体检的则该参数为”0000“
        String isExpBarcode = "";
        isExpBarcode = ParamBuffer.getParamValue(request, basHospitals.getHosnum(), basHospitals.getNodecode(), "健康体检", "导入名单是否生成条码");
        if (isExpBarcode == null) {
            isExpBarcode = "N";
        }
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "";
            List list = null;
            List<Object[]> pi_1 = new ArrayList<Object[]>();
            // 获取体检人的性别--过滤出有性别要求的体检项目
            sql = "select a.*,(floor(MONTHS_BETWEEN(sysdate,a.dateofbirth)/12)) as age from pexam_mans a where a.hosnum=? and a.pexamid=? and a.bdate is null";
            list = db.find(sql, new Object[]{hosnum, pexamid}, PexamMans.class);
            if (list != null && list.size() > 0) {// 防止重复提交
                PexamMans pexamMan = (PexamMans) list.get(0);
                String sex = pexamMan.getSex();

                System.out.println("获取个人相关的体检项目或套餐");// 如果是团体的话pexamid为空
                sql = "select * from pexam_items a where a.hosnum=?  and a.pexamid=?";
                list = db.find(sql, new Object[]{hosnum, pexamid});
                if (ListUtil.listIsNotEmpty(list)) {
                    String groupids = "";// 收集所有套餐id
                    String itemcodes = "";// 收集所有单独大项id
                    Map map0 = null;
                    for (int i = 0; i < list.size(); i++) {
                        map0 = (Map) list.get(i);
                        if ("y".equals(map0.get("isgroup"))) {// 是否是套餐的标志
                            groupids += "'" + map0.get("itemid") + "',";
                        } else {
                            itemcodes += "'" + map0.get("itemid") + "',";
                        }
                    }
                    // 团体体检--此处未对套餐中有相同体检项目进行去重操作
                    if (groupids.length() > 0) {
                        groupids = groupids.substring(0, groupids.length() - 1);
                        System.out.println("获取组成套餐的体检项目");// 有性别要求的只需在此处加个性别条件就ok
                        //sql = "select b.*,a.groupid,c.groupname from pexam_items_groupdetails a,pexam_items_com b,pexam_items_group c where a.groupid in (?) and a.itemcode=b.comid and c.groupid=a.groupid and a.hosnum=b.hosnum and a.hosnum=c.hosnum ".replace("?", groupids);
                        //没加项
                        if ("".equals(itemcodes)) {
                            sql = "select b.*,a.groupid,c.groupname,d.iszh from pexam_items_groupdetails a,pexam_items_com b,pexam_items_group c,pexam_items_type d where a.groupid in (?) and a.itemcode=b.comid and c.groupid=a.groupid and a.hosnum=b.hosnum and a.hosnum=c.hosnum  and b.parentid=d.typeid ".replace("?", groupids);
                            if (!"".equals(sex)) {
                                sql += " and (b.forsex='不限' or b.forsex='" + sex + "' or b.forsex is null ) ";
                            }
                            sql += " and a.hosnum=? order by b.xgys,b.sn ";
                        } else {
                            //有加项
                            itemcodes = itemcodes.substring(0, itemcodes.length() - 1);
                            sql = "";
                            sql += "select * from  (";
                            sql += "select b.*,a.groupid,c.groupname,d.iszh from pexam_items_groupdetails a,pexam_items_com b,pexam_items_group c,pexam_items_type d where a.groupid in (?) and a.itemcode=b.comid and c.groupid=a.groupid and a.hosnum=b.hosnum and a.hosnum=c.hosnum  and b.parentid=d.typeid ".replace("?", groupids);
                            if (!"".equals(sex)) {
                                sql += " and (b.forsex='不限' or b.forsex='" + sex + "' or b.forsex is null ) ";
                            }
                            sql += " and a.hosnum=?  ";
                            sql += "  union select b.*, '' groupid, '' groupname, '' iszh  from pexam_items_com b  where b.comid in (?) ".replace("?", itemcodes);
                            sql += ")   order by xgys ,sn";
                        }
                        list = db.find(sql, new Object[]{hosnum});
                        //如果存在需要插入的套餐或者加项
                        if (list != null && list.size() > 0) {
                            Map<String, Map<String, String>> lisMap = new HashMap<String, Map<String, String>>();
                            //Map<String,String> lisName=new HashMap<String,String>();
                            for (int i = 0; i < list.size(); i++) {
                                map0 = (Map) list.get(i);
                                String comclass = map0.get("comclass") == null ? "" : map0.get("comclass").toString();
                                String itemuuid = new UUIDGenerator().generate().toString();
                                if ("检验".equals(comclass) || "外送".equals(comclass)) {
                                    String jyyq = map0.get("jyyq") == null ? "" : map0.get("jyyq").toString();   //检验仪器
                                    map0.put("itemuuid", itemuuid);
                                    if (!"".equals(jyyq)) {
                                        String itemname = (String) map0.get("comname");// 组合项目名称
                                        String afterhb_name = map0.get("xmjc") == null ? itemname : map0.get("xmjc").toString();
                                        if (lisMap.get(jyyq) == null) {
                                            Map seq_id = new HashMap();
                                            String tmcode = "";
                                            String codesql = "select seq_pexam_tmcode.nextval from dual";
                                            seq_id = (Map) db.findOne(codesql);
                                            tmcode = "3" + String.valueOf(seq_id.get("nextval"));
                                            Map idMap = new HashMap();
                                            idMap.put("code", tmcode);
                                            idMap.put("parent_comid", itemuuid);
                                            idMap.put(jyyq, afterhb_name);
                                            map0.put("parent_comid", "");
//											map0.put("code",tmcode);
                                            lisMap.put(jyyq, idMap);
                                        } else {
                                            lisMap.get(jyyq).put(jyyq, lisMap.get(jyyq).get(jyyq) + "-" + afterhb_name);
                                            map0.put("parent_comid", lisMap.get(jyyq).get("parent_comid"));
//											map0.put("code",lisMap.get(jyyq).get("code"));
                                        }
                                    } else {
                                        Map seq_id = new HashMap();
                                        String tmcode = "";
                                        String codesql = "select seq_pexam_tmcode.nextval from dual";
                                        seq_id = (Map) db.findOne(codesql);
                                        tmcode = "3" + String.valueOf(seq_id.get("nextval"));
                                        map0.put("parent_comid", "");
                                        map0.put("code", tmcode);
                                    }
                                } else {
                                    map0.put("itemuuid", itemuuid);
                                    map0.put("parent_comid", "");
                                }
                            }

                            for (int i = 0; i < list.size(); i++) {
                                map0 = (Map) list.get(i);
                                String jyyq = map0.get("jyyq") == null ? "" : map0.get("jyyq").toString();   //检验仪器
                                String itemcode = (String) map0.get("comid");// 组合项目id
                                String itemname = (String) map0.get("comname");// 组合项目名称
                                String groupid = (String) map0.get("groupid");// 套餐id
                                String groupname = (String) map0.get("groupname");// 套餐名称
                                String excdeptname = (String) map0.get("excdeptname");//执行科室
                                //增加的几个字段
                                String fpkm = map0.get("fpkm") == null ? "" : map0.get("fpkm").toString(); //发票科目 id
                                String hskm = map0.get("hskm") == null ? "" : map0.get("hskm").toString(); // 核算科目Id
                                String fpkmname = map0.get("fpkmname") == null ? "" : map0.get("fpkmname").toString(); //发票科目 name
                                String hskmname = map0.get("hskmname") == null ? "" : map0.get("hskmname").toString(); //核算科目  name
                                String cost = map0.get("cost") == null ? "" : map0.get("cost").toString(); //价格
                                String ggxm = map0.get("ggxm") == null ? "" : map0.get("ggxm").toString(); //是否公共项目
                                String comclass = map0.get("comclass") == null ? "" : map0.get("comclass").toString();
                                String iszh = (String) map0.get("iszh");//是否组合项
                                String istjxm = map0.get("tjxm") == null ? "" : map0.get("tjxm").toString(); //是否体检项目
                                String tmcode = "";
                                String itemnamejc = itemname;
                                if ("检验".equals(comclass) || "外送".equals(comclass)) {
                                    if (!"".equals(jyyq)) {
                                        tmcode = lisMap.get(jyyq).get("code");
                                        itemnamejc = lisMap.get(jyyq).get(jyyq);
                                        //如果没有合并的则不取简称
                                        if (itemnamejc.split("-").length == 1) {
                                            itemnamejc = itemname;
                                        }
                                    } else {
                                        tmcode = String.valueOf(map0.get("code"));
                                    }
                                }
                                pi_1.add(new Object[]{hosnum, map0.get("itemuuid"), examid, pexamid, itemcode, itemname,
                                        map0.get("excdept"), map0.get("excdeptname"), groupid, groupname, map0.get("sn"),
                                        map0.get("comclass"), userid, username, timesTamp, deptcode, deptname, tmcode, iszh, fpkm, hskm, fpkmname, hskmname,
                                        ggxm, cost, istjxm, itemnamejc, map0.get("parent_comid")});
                            }
                        }
                        Object[][] params_1 = new Object[pi_1.size()][2];
                        for (int j = 0; j < pi_1.size(); j++) {
                            params_1[j] = pi_1.get(j);
                        }
                        //System.out.println("插入合并的检验项目"); //==================================================
                        sql = "insert into pexam_items_title (hosnum,itemuuid,examid,pexamid,itemcode,itemname,excdept,excdeptname,groupid," +
                                "groupname,sn,comclass,sheetdoctorid,sheetdoctorname,sheetdate,sheetdeptid,sheetdeptname,tmcode,iszh" +
                                ", ACCOUNTITEM, INVOICEITEM, ACCOUNTITEMNAME, INVOICEITEMNAME,ggxm,price,tjxm,afterhb_name" +
                                ",parent_comid,xmstatus)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ,?,?,?,?,?,?,?,?,? ,?,'未检')";
                        db.excuteBatch(sql, params_1);
                    }
                }
            }
            returnMap.put("itemsNum", pi_1.size());

            pw.print(JSONObject.fromObject(returnMap).toString());
            db.commit();
        } catch (MwInventoryException mme) {
            mme.printStackTrace();
            db.rollback();
            pw.print("错误：" + mme.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            pw.print("错误：获取数据失败");
        } finally {
            db.freeCon();
            pw.flush();
            pw.close();
        }
    }


    //跳转到 查看 部位
    @RequestMapping("//toBuWeiPage")
    public ModelAndView toBuWeiPage(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals bh = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = bh.getHosnum();
        String indid = request.getParameter("indid");
        String result = URLDecoder.decode(request.getParameter("result"), "utf-8");
        String tsxm = URLDecoder.decode(request.getParameter("tsxm"), "utf-8");
        DBOperator db = null;
        String sql = "";
        List<Map> list = null;
        modelMap.put("indid", indid);
        modelMap.put("result", result);
        modelMap.put("tsxm", tsxm);

        return new ModelAndView("pexam/SeeBuWeiPage");
    }

    //查看体检项目是否有回退记录
    @RequestMapping(value = "/seeIfBackReason", method = RequestMethod.POST)
    public void seeIfBackReason(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_user bu = (Bas_user) request.getSession().getAttribute("login_user");
        String userid = bu.getId();
        String username = bu.getName();
        String hosnum = basHospitals.getHosnum();
        String itemcode = request.getParameter("itemcode");
        String pexamid = request.getParameter("pexamid");
        PrintWriter pw = null;
        DBOperator db = null;
        String sql = "";
        List<Map> list = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            //查询项目的状态 是否完成
            sql = "select to_char(a.excdate,'yyyy-mm-dd') date1 from pexam_items_title a  where a.hosnum='" + hosnum + "' and a.pexamid='" + pexamid + "' and a.itemcode='" + itemcode + "'";
            list = db.find(sql);
            String excdate = list.get(0).get("date1") == null ? "" : list.get(0).get("date1").toString();
            if ("".equals(excdate)) {//如果是未完成  查看是否有回退记录
                sql = "select  * from PEXAM_TJXMBACK a where a.hosnum='" + hosnum + "' and a.pexamid='" + pexamid + "' and a.itemcode='" + itemcode + "' order by a.back_date desc";
                list = db.find(sql);
                if (ListUtil.listIsNotEmpty(list)) {
                    pw.print("ok");
                } else {
                    pw.print("none");
                }
            } else {
                //是完成状态
                pw.print("none");
            }

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

    //跳转到医生查看回退的页面
    @RequestMapping("/toSeeBackPage")
    public ModelAndView toSeeBackPage(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_user bu = (Bas_user) request.getSession().getAttribute("login_user");
        String userid = bu.getId();
        String username = bu.getName();
        String hosnum = basHospitals.getHosnum();
        String itemcode = request.getParameter("itemcode");
        String pexamid = request.getParameter("pexamid");
        modelMap.put("itemcode", itemcode);
        modelMap.put("pexamid", pexamid);
        DBOperator db = null;
        PrintWriter pw = null;
        String sql = "";
        List<Map> list = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            sql = "select  * from PEXAM_TJXMBACK a where a.hosnum='" + hosnum + "' and a.pexamid='" + pexamid + "' and a.itemcode='" + itemcode + "' order by a.back_date desc";
            //list = db.find(sql);
            //modelMap.put("info", "var list=" + JSONArray.fromObject(list).toString());
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            modelMap.put("error", e.getMessage());
            return new ModelAndView("pexam/HuiTuiErrorPage");
        } finally {
            db.freeCon();
        }
        return new ModelAndView("pexam/SeeBackPage");
    }

    //医生查看体检项目的 回退记录
    @RequestMapping(value = "/seeBackReason", method = RequestMethod.POST)
    public void seeBackReason(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_user bu = (Bas_user) request.getSession().getAttribute("login_user");
        String userid = bu.getId();
        String username = bu.getName();
        String hosnum = basHospitals.getHosnum();
        String itemcode = request.getParameter("itemcode");
        String pexamid = request.getParameter("pexamid");
        PrintWriter pw = null;
        DBOperator db = null;
        String sql = "";
        List<Map> list = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            sql = "select  a.*, to_char(a.back_date,'yyyy-mm-dd hh24:mi:ss') date2 from PEXAM_TJXMBACK a where a.hosnum='" + hosnum + "' and a.pexamid='" + pexamid + "' and a.itemcode='" + itemcode + "' order by a.back_date desc";
            list = db.find(sql);
            pw.print(JSONArray.fromObject(list).toString());
            //modelMap.put("info", "var list=" + JSONArray.fromObject(list).toString());
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

    @RequestMapping(value = "/getItemsBuwei", method = RequestMethod.POST)
    public void getItemsBuwei(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_user bu = (Bas_user) request.getSession().getAttribute("login_user");
        String hosnum = basHospitals.getHosnum();
        String tsxm = request.getParameter("tsxm");
        String indid = request.getParameter("indid");
        PrintWriter pw = null;
        DBOperator db = null;
        String sql = "";
        List<Map> list = null;
        Map map = new HashMap();
        try {
            db = new DBOperator();
            pw = response.getWriter();
            sql = "select a.*  from PEXAM_ITEMS_IND_BW a where a.hosnum='" + hosnum + "' and a.indname like '%" + tsxm + "%'";
            list = db.find(sql);
            map.put("buwei_list", list);
            sql = "select a.*,s.classname from  pexam_ind_result a  left join pexam_sugests s on s.sugestid=a.sugestid where a.indid=? ";
            list = db.find(sql, new Object[]{indid});
            map.put("result_list", list);
            pw.print(JSONObject.fromObject(map).toString());
            //modelMap.put("info", "var list=" + JSONArray.fromObject(list).toString());
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


    /************************预约服务页面，所有相关操作(2012-12-18)结束**************************/

    /************************接检页面，所有相关操作(2012-12-18)开始**************************/
    //TODO
    /*
     * 获取接检页面要加载名单的数量
	 * 2012-12-14
	 */
    @RequestMapping(value = "/getPatientListCountNew", method = RequestMethod.POST)
    public void getrepatientlistCount_xj(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String method = request.getParameter("method");
        String examid = request.getParameter("examid");//如果不是团体则就是为"0000"

        ReturnValue returnValue = new ReturnValue();
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "";
            String startSql = "select count(*) as count from (";
            String endSql = ")";

            //System.out.println("查找在检和未检的人员信息及在检人员的完成情况");
            sql = "select a.*,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid and b.excdate is not null) as fin from pexam_mans a where a.hosnum=? and a.examid=? and a.edate is null";
            if ("loadBySearch".equals(method)) {
                String searchvalue = request.getParameter("searchvalue");
                sql += " and (instr(a.inputcpy,'" + searchvalue.toUpperCase() + "')>0 or instr(a.inputcwb,'" + searchvalue.toUpperCase() + "')>0 or instr(a.pexamid,'" + searchvalue + "')>0 or instr(a.patname,'" + searchvalue + "')>0 or instr(a.idnum,'" + searchvalue + "')>0 or instr(a.ybbh,'" + searchvalue + "')>0)";
            } else if ("loadByPexamid".equals(method)) {//根据体检编号
                String loadByPexamid = request.getParameter("loadByPexamid");
                sql += " and a.pexamid='" + loadByPexamid + "' ";
            } else if ("loadByIdNum".equals(method)) {//根据身份证id
                String loadByIdNum = request.getParameter("loadByIdNum");
                sql += " and a.idnum='" + loadByIdNum + "' ";
            }
            sql = startSql + sql + endSql;
            Map<String, Integer> countMap = (Map<String, Integer>) db.findOne(sql, new Object[]{hosnum, examid});
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

    /*
	 * 接检页面体检名单的grid
	 * 2012-12-14
	 */
    @RequestMapping(value = "/getPatientListNew", method = RequestMethod.POST)
    public void getPatientListNew(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String method = request.getParameter("method");
        String examid = request.getParameter("examid");//如果为团体则为“0000”
        int pageIndex = Integer.parseInt(request.getParameter("index").toString()); // 分页索引
        int pageItems = Integer.parseInt(request.getParameter("size").toString()); // 每页数量

        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "";
            List list = null;
            String pagingSql1 = "select OHYEAH.* from (select OHNO.*,rownum no from ("; // 用于分页段1
            String pagingSql2 = ") OHNO where rownum <= ?) OHYEAH where no > ?"; // 用于分页段2

            //System.out.println("查找在检和未检的人员信息及在检人员的完成情况");
            sql = "select a.*,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid and b.excdate is not null) as fin from pexam_mans a where a.hosnum=? and a.examid=? and a.edate is null";
            if ("loadBySearch".equals(method)) {
                String searchvalue = request.getParameter("searchvalue");
                sql += " and (instr(a.inputcpy,'" + searchvalue.toUpperCase() + "')>0 or instr(a.inputcwb,'" + searchvalue.toUpperCase() + "')>0 or instr(a.patname,'" + searchvalue + "')>0 or instr(a.pexamid,'" + searchvalue + "')>0 or instr(a.idnum,'" + searchvalue + "')>0 or instr(a.ybbh,'" + searchvalue + "')>0)";
            } else if ("loadByPexamid".equals(method)) {
                String loadByPexamid = request.getParameter("loadByPexamid");
                sql += "  and a.pexamid='" + loadByPexamid + "' ";
            } else if ("loadByIdNum".equals(method)) {
                String loadByIdNum = request.getParameter("loadByIdNum");
                sql += "  and a.idnum='" + loadByIdNum + "' ";
            }
            sql = pagingSql1 + sql + pagingSql2;
            list = db.find(sql, new Object[]{hosnum, examid, pageIndex * pageItems + pageItems, pageIndex * pageItems});
            JSONArray jsonArr = JSONArray.fromObject(list);
            pw.print(jsonArr.toString());
            System.out.println("=======>" + jsonArr.toString());
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

    @RequestMapping(value = "/savehs_status")
    public void savehs_status(HttpServletRequest request, HttpServletResponse response
            , @RequestParam(required = false) String pexamid
            , @RequestParam(required = false) String status) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            Map map0 = new HashMap();
            System.out.println(status);
            String v_status = "";
            if (status.indexOf("wc") > -1) {
                v_status = "未完成";
            } else {
                v_status = "完成";
            }
            db.excute("update  PEXAM_MANS a set a.hs_status='" + v_status + "' where a.pexamid='" + pexamid + "' ");
            pw.print("success");
            db.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            db.rollback();
            pw.print("fail" + ex.getMessage());
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    /*
	 * 获取某单位总体检人数、在检人数、未开始检人数--体检医生站
	 */
    @RequestMapping(value = "/getPatNameNumNew", method = RequestMethod.POST)
    public void getPatNameNum_xj(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String examid = request.getParameter("examid");
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            Map map0 = new HashMap();
            String sql = "select count(a.pexamid) as tjrs from pexam_mans a where a.hosnum=? and a.examid=?";//获取总人数
            Map<String, Integer> countMap = (Map<String, Integer>) db.findOne(sql, new Object[]{hosnum, examid});
            map0.put("tjrs", String.valueOf(countMap.get("tjrs")));
            sql = "select count(a.pexamid) as zjrs from pexam_mans a where a.hosnum=? and a.examid=? and a.bdate is not null and a.edate is null";//获取在检人数
            countMap = (Map<String, Integer>) db.findOne(sql, new Object[]{hosnum, examid});
            map0.put("zjrs", String.valueOf(countMap.get("zjrs")));
            sql = "select count(a.pexamid) as wjrs from pexam_mans a where a.hosnum=? and a.examid=? and a.bdate is null";//获取未检人数
            countMap = (Map<String, Integer>) db.findOne(sql, new Object[]{hosnum, examid});
            map0.put("wjrs", String.valueOf(countMap.get("wjrs")));
            pw.print(JSONObject.fromObject(map0).toString());
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
	 * 加载个人信息--接检页面
	 * 2012-12-14
	 */
    @RequestMapping("/getPatientInfoNew")
    public void getPatientByPExamidOnRep(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String pexamid = request.getParameter("pexamid");//体检编号
        //产生pexamid条形码
		/*
		String codePath="";
		BarCodeImage bar  = new BarCodeImage(1,33);
		String path = bar.create39Image(pexamid);
		codePath = "../"+path;
		Map personinfo=new HashMap();
		*/
        DBOperator db = new DBOperator();
        PrintWriter pw = null;
        try {
            String sql = "select  to_char(a.bdate,'yyyy-mm-dd hh24:mi:ss')  bdate1 ,  a.*," +
                    "(floor(MONTHS_BETWEEN(sysdate,a.dateofbirth)/12))  as age " +
                    "from " +
                    "pexam_mans a " +
                    "where a.hosnum=? and a.pexamid=?";
            List list = db.find(sql, new Object[]{hosnum, pexamid});

			/*
			personinfo.put("psinfolist", list);
			personinfo.put("codePath", codePath);
			JSONArray jsons = JSONArray.fromObject(personinfo);
			*/
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

    /*
	 * 得到某人的体检信息
	 * 2012-12-14
	 */
    @RequestMapping("/getPatientExanInfoNew")
    public void getPatientExanInfo_xj(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String examid = request.getParameter("examid");//预约id
        String pexamid = request.getParameter("pexamid");//个人体检编号

        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "";
            List list = null;

            //System.out.println("查询某人的体检信息");
            sql = "select * from pexam_items a where a.hosnum=? and a.examid=? and a.pexamid=?";
            list = db.find(sql, new Object[]{hosnum, examid, pexamid});
            if (list.size() == 0) {
                String sql0 = "select * from pexam_items a where a.hosnum=? and a.examid=? and a.pexamid is null";
                list = db.find(sql0, new Object[]{hosnum, examid});
            }

            JSONArray jsons = JSONArray.fromObject(list);
            pw.print(jsons.toString());
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    /*
	 * 套餐下的项目---接检页面
	 * 2012-12-14
	 */
    @RequestMapping(value = "/getGroupItemsNew", method = RequestMethod.POST)
    public void getGroupItems(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String examid = request.getParameter("examid");//预约id
        String pexamid = request.getParameter("pexamid");//个人体检编号
        String status = request.getParameter("status");//在检或者未检
        String groupids = request.getParameter("groupids");//套餐id
        String flag = request.getParameter("flag");  //标志 是获取加项 还是获取 套餐
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "";
            List list = null;
            if ("1".equals(flag)) {
                //是加项   status 是指这个体检的整体的状态
                if ("在检".equals(status)) {
                    sql = "select to_char(a.excdate,'yyyy-mm-dd hh24:mi:ss') excdate,  a.*,b.bookname from pexam_items_title a left join pexam_items_com b on b.comid=a.itemcode where a.hosnum=? and a.examid=? and a.pexamid=? and a.itemcode=? and b.delflag!='y' order by a.excdept,a.comclass, a.tmcode ";
                    list = db.find(sql, new Object[]{hosnum, examid, pexamid, groupids});
                } else if ("未检".equals(status)) {
                    sql = "select a.*,'' excdate  from pexam_items_com a  where a.comid=? and a.hosnum=? and a.delflag!='y'";
                    list = db.find(sql, new Object[]{groupids, hosnum});
                } else {
                    //完成
                    sql = "select to_char(a.excdate,'yyyy-mm-dd hh24:mi:ss') excdate, a.*,b.bookname from pexam_items_title a left join pexam_items_com b on b.comid=a.itemcode where a.hosnum=? and a.examid=? and a.pexamid=? and a.itemcode=? and b.delflag!='y'  order by a.excdept,a.comclass ,a.tmcode  ";
                    list = db.find(sql, new Object[]{hosnum, examid, pexamid, groupids});
                }

            } else {
                if ("在检".equals(status)) {
                    sql = "select to_char(a.excdate,'yyyy-mm-dd hh24:mi:ss') excdate, a.*,b.bookname from pexam_items_title a left join pexam_items_com b on b.comid=a.itemcode where a.hosnum=? and a.examid=? and a.pexamid=? and a.groupid=? and b.delflag!='y'  order by a.excdept,a.comclass ,a.tmcode  ";
                    list = db.find(sql, new Object[]{hosnum, examid, pexamid, groupids});
                } else if ("未检".equals(status)) {
                    sql = "select a.*,'' excdate from pexam_items_com a ,pexam_items_groupdetails b where a.hosnum=b.hosnum and a.comid=b.itemcode and a.hosnum=? and b.groupid=? and a.delflag!='y' order by  a.excdept,  a.xgys";
                    list = db.find(sql, new Object[]{hosnum, groupids});
                } else {
                    //完成
                    sql = "select to_char(a.excdate,'yyyy-mm-dd hh24:mi:ss') excdate, a.*,b.bookname from pexam_items_title a left join pexam_items_com b on b.comid=a.itemcode where a.hosnum=? and a.examid=? and a.pexamid=? and a.groupid=? and b.delflag!='y'  order by a.excdept,a.comclass, a.tmcode ";
                    list = db.find(sql, new Object[]{hosnum, examid, pexamid, groupids});
                }
            }
            JSONArray jsons = JSONArray.fromObject(list);
            pw.print(jsons.toString());
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

    /*
	 * 开始体检,修改为根据套餐和加项启--包含了与lis数据的交换
	 * 2012-12-14
	 */
    @RequestMapping("/startExamNew2")
    public void startExamNew2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String hosnum = basHospitals.getHosnum();
        String shortname = basHospitals.getShortname();//医院简称
        String nodecode = basHospitals.getNodecode();
        String jyjgjm = basHospitals.getJyjgjm();//检验机构简码
        String deptcode = basDept.getDeptcode();
        String deptname = basDept.getDeptname();
        String userid = basUser.getId();
        String username = basUser.getName();
        Timestamp timesTamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyMMdd");
        String codedatestr = dateFormat2.format(new Date());

        String pexamid = request.getParameter("pexamid");// 唯一标识--个人体检编号
        String examid = request.getParameter("examid");// 如果是个人体检的则该参数为”0000“
        String pexamtype = request.getParameter("pexamtype");

        String isExpBarcode = "";
        isExpBarcode = ParamBuffer.getParamValue(request, basHospitals.getHosnum(), basHospitals.getNodecode(), "健康体检", "导入名单是否生成条码");
        if (isExpBarcode == null) {
            isExpBarcode = "N";
        }
        Map mapInfo = new HashMap();
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            int itemsNum = 0;
            String sql = "";
            List list = null;
            List list1 = null;
            List<Object[]> pi_1 = new ArrayList<Object[]>();
            Map returnMap = new HashMap();
            // 获取体检人的性别--过滤出有性别要求的体检项目
            sql = "select a.*,(floor(MONTHS_BETWEEN(sysdate,a.dateofbirth)/12)) as age from pexam_mans a where a.hosnum=? and a.pexamid=? and a.bdate is null";
            list = db.find(sql, new Object[]{hosnum, pexamid}, PexamMans.class);
            returnMap.put("pexamList", list);
            //查到此人有木有插到title表里
            sql = "select * from pexam_items_title a where a.pexamid=? and a.hosnum=?";
            list1 = db.find(sql, new Object[]{pexamid, hosnum});
            if (list1.size() == 0) {
                insertTitleAlone(request, response, db, returnMap);
                System.out.println("更新pexam_mans表的体检开始标志字段");
                sql = "update pexam_mans a set a.bdate=?,a.isover='在检' where a.hosnum=? and a.pexamid=?";
                db.excute(sql, new Object[]{new Timestamp(new Date().getTime()), hosnum, pexamid});
                returnMap.put("itemsNum", pi_1.size());
            } else {
                if (ListUtil.listIsNotEmpty(list)) {// 防止重复提交
                    System.out.println("更新pexam_mans表的体检开始标志字段");
                    sql = "update pexam_mans a set a.bdate=?,a.isover='在检' where a.hosnum=? and a.pexamid=?";
                    db.excute(sql, new Object[]{new Timestamp(new Date().getTime()), hosnum, pexamid});
                }
                returnMap.put("itemsNum", list1.size());
            }
            pw.print(JSONObject.fromObject(returnMap).toString());
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            pw.print("fail" + e.getMessage());
        } finally {
            db.freeCon();
            pw.flush();
            pw.close();
        }
    }

    /**
     * 刷卡->获取病人检验ID
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/getPexamid")
    public void getPexamid(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        DBOperator db = new DBOperator();
        //idNum=" + idNum+"&examid"+examid+"&name"+name
        String idNum = request.getParameter("idNum");
        String examid = request.getParameter("examid");
        String name = request.getParameter("name");
        name = (name.equals("") || name == null) ? "" : URLDecoder.decode(name, "utf-8");
//		System.out.println("===="+name+","+examid+","+idNum);
        try {
            String sql = " select t.pexamid id from pexam_mans t where t.examid=? and t.idnum=? and t.patname = ?";
            List<Map> map0 = db.find(sql, new Object[]{examid, idNum, name});
            if (map0 == null || map0.size() == 0) {
                pw.print("null");
                return;
            }
            pw.print(map0.get(0).get("id").toString());
            db.commit();
        } catch (RuntimeException e) {
            pw.print("fail");
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        pw.close();
    }

    //清空体检数据（撤销体检）
    @RequestMapping({"/wipeExamData"})
    public void wipeExamData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String pexamid = request.getParameter("pexamid");
        Bas_user bas_user = (Bas_user) request.getSession().getAttribute("login_user");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = bas_user.getHosnum();
        PrintWriter pw = response.getWriter();
        DBOperator db = new DBOperator();
        String isExpBarcode = "";
        isExpBarcode = ParamBuffer.getParamValue(request, basHospitals.getHosnum(), basHospitals.getNodecode(), "健康体检", "导入名单是否生成条码");
        String sql1 = "";
        String sql2 = "";
        String sql3 = "";
        String querysql = "";
        List<Map> querylist = new ArrayList<Map>();
        try {
            //先查询 体检项目 是否有出结果的。
            querysql = "select * from pexam_items_title  t where t.pexamid='" + pexamid + "'  and  ( t.status='1' or t.status='0' or  t.excdate is not null )";
            querylist = db.find(querysql);
            if (querylist.size() > 0) {
                pw.print("isintj");
            } else {
                sql1 = "update pexam_mans set isover='',bdate='' where pexamid=? and hosnum=? ";  //更新体检人员表为 未开始状态
                db.excute(sql1, new Object[]{pexamid, hosnum});
                if (isExpBarcode == null || !isExpBarcode.equals("Y")) { //不生成条码  就直接删除
                    sql2 = "delete from pexam_items_title where pexamid=? and hosnum=? ";
                    db.excute(sql2, new Object[]{pexamid, hosnum});
                } else {
                    //生成条码的  不删除，而是更新
                    sql2 = "update pexam_items_title set excdate=null,typeflag='' where pexamid=? and hosnum=? ";
                }
                //删除体检结果表
                sql3 = "delete from pexam_results where pexamid=? and hosnum=? ";
                db.excute(sql3, new Object[]{pexamid, hosnum});
                db.commit();
                pw.print("success");
            }
        } catch (RuntimeException e) {
            pw.print("fail");
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
    }

    //套餐补录
    @RequestMapping({"/loadPackageMakeUp"})
    public ModelAndView loadPackageMakeUp(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        return new ModelAndView("pexam/PackageMakeUp", modelMap);
    }

    @RequestMapping({"/loadItemName"})
    public void loadItemName(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String examid = request.getParameter("examid");
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            List list = new ArrayList();
            String sql = "select c.comid,c.comname from pexam_items s,pexam_items_groupdetails g,pexam_items_com c where s.hosnum=? and s.examid=? and s.itemid=g.groupid and g.itemcode=c.comid";
            list = db.find(sql, new Object[]{hosnum, examid});
            pw = response.getWriter();
            pw.print(JSONArray.fromObject(list).toString());
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
    }

    @RequestMapping({"/insertintoPackage"})
    public void insertintoTc(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        response.setContentType("text/html;charset=utf-8");
        String examid = request.getParameter("examid");
        String begin_date = request.getParameter("begin_date");
        String end_date = request.getParameter("end_date");
        String itemcode = request.getParameter("itemcode");
        DBOperator db = null;
        try {
            db = new DBOperator();
            List list = new ArrayList();
            List list1 = new ArrayList();
            List list2 = new ArrayList();
            String sql = "select m.pexamid,m.sex from pexam_mans m,pexam_items_title t where m.pexamid=t.pexamid and m.examid=? ";
            if ((begin_date != null) && (begin_date != "") && (end_date != null) && (end_date != ""))
                sql = sql + " and to_char(m.bdate,'yyyy-MM-dd') between '" + begin_date + "' and '" + end_date + "'";
            else if ((begin_date != null) && (begin_date != "") && (((end_date == null) || (end_date == ""))))
                sql = sql + " and to_char(m.bdate,'yyyy-MM-dd') > '" + begin_date + "'";
            else if ((((begin_date == null) || (begin_date == ""))) && (end_date != null) && (end_date != ""))
                sql = sql + " and to_char(m.bdate,'yyyy-MM-dd') < '" + end_date + "'";
            else {
                sql = sql + " and m.bdate is not null ";
            }
            sql = sql + " group by m.pexamid,m.sex";
            list = db.find(sql, new Object[]{examid});
            sql = "select s.itemid,s.itemname,c.comid,c.comname,c.comclass,c.excdept,c.excdeptname,c.sn,c.forsex from pexam_items s,pexam_items_groupdetails g,pexam_items_com c where s.hosnum=? and s.examid=? and s.itemid=g.groupid and g.itemcode=c.comid and c.comid=?";
            list1 = db.find(sql, new Object[]{hosnum, examid, itemcode});
            for (int i = 0; i < list.size(); ++i) {
                sql = "select t.itemcode from pexam_items_title t where t.pexamid=? and t.itemcode=? ";
                list2 = db.find(sql, new Object[]{((Map) list.get(i)).get("pexamid"), itemcode});
                if ((list2.size() < 1) && (list1.size() > 0)) {
                    String itemuuid;
                    if ("女".equals(((Map) list1.get(0)).get("forsex"))) {
                        if ("女".equals(((Map) list.get(i)).get("sex"))) {
                            itemuuid = new UUIDGenerator().generate().toString();
                            sql = "insert into pexam_items_title (hosnum,itemuuid,examid,pexamid,itemcode,itemname,excdept,excdeptname,groupid,groupname,sn,comclass) values (?,?,?,?,?,?,?,?,?,?,?,?)";
                            db.excute(sql, new Object[]{hosnum, itemuuid, examid, ((Map) list.get(i)).get("pexamid"), ((Map) list1.get(0)).get("comid"), ((Map) list1.get(0)).get("comname"), ((Map) list1.get(0)).get("excdept"), ((Map) list1.get(0)).get("excdeptname"), ((Map) list1.get(0)).get("itemid"), ((Map) list1.get(0)).get("itemname"), ((Map) list1.get(0)).get("sn"), ((Map) list1.get(0)).get("comclass")});
                        }
                    } else {
                        itemuuid = new UUIDGenerator().generate().toString();
                        sql = "insert into pexam_items_title (hosnum,itemuuid,examid,pexamid,itemcode,itemname,excdept,excdeptname,groupid,groupname,sn,comclass) values (?,?,?,?,?,?,?,?,?,?,?,?)";
                        db.excute(sql, new Object[]{hosnum, itemuuid, examid, ((Map) list.get(i)).get("pexamid"), ((Map) list1.get(0)).get("comid"), ((Map) list1.get(0)).get("comname"), ((Map) list1.get(0)).get("excdept"), ((Map) list1.get(0)).get("excdeptname"), ((Map) list1.get(0)).get("itemid"), ((Map) list1.get(0)).get("itemname"), ((Map) list1.get(0)).get("sn"), ((Map) list1.get(0)).get("comclass")});
                    }
                }
            }
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
    }

    //TODO

    /************************接检页面，所有相关操作(2012-12-18)结束**************************/



	/*
	 * “体检医生站”或“总检医生站”--加载候检人名单--获取条数
	 * 2012-12-14
	 */
    @RequestMapping(value = "/getDoctorStationPatientCountNew", method = RequestMethod.POST)
    public void getDoctorStationPatientCountNew(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        String hosnum = basHospitals.getHosnum();
        String deptCode = basDept.getDeptcode();
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String userid = basUser.getId();
        String method = request.getParameter("method");//总检医生站（mainDoctorCheck）、体检医生站（doctorStation）
        //开始时间和结束时间  体检医生查询当天的数据
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");
        ReturnValue returnValue = new ReturnValue();
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "";
            List list = null;
            Map<String, Integer> countMap = null;
            //----获取登录人员的所有关系科室------
            sql = "select r.office_id from bas_user_dept_role_relation r where r.user_id=? group by r.office_id ";
            List<Map> deptcodelist = db.find(sql, new Object[]{userid});
            String alldeptcode = "(";
            for (int i = 0; i < deptcodelist.size(); i++) {
                if (i == 0) {
                    alldeptcode += "b.excdept=" + deptcodelist.get(i).get("office_id");
                } else {
                    alldeptcode += " or b.excdept=" + deptcodelist.get(i).get("office_id");
                }

            }
            alldeptcode += ")";


            //获取是否区分科室
            String isDishDept = ParamBuffer.getParamValue(request, basHospitals.getHosnum(), basHospitals.getNodecode(), "健康体检", "是否区分科室");

            //	System.out.println("查询候检人名单");
            if ("doctorStation".equals(method)) {//加载体检医生站候检人名单
                if ("Y".equals(isDishDept)) {//区分科室
                    //System.out.println("--------Y----------------------");
                    sql = "select count(*) as count from (select * from (select a.*,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and a.examid=b.examid and a.pexamid=b.pexamid and " + alldeptcode + " ) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and a.examid=b.examid and a.pexamid=b.pexamid and  " + alldeptcode + "  and b.excdate is not null) as fin from pexam_mans a where a.hosnum=?   and a.bdate is not null and a.edate is null  and to_char(a.bdate, 'yyyy-MM-dd') >= '" + starttime + "' and to_char(a.bdate, 'yyyy-MM-dd') <= '" + endtime + "' )CHNO where CHNO.total>0)";
                    countMap = (Map<String, Integer>) db.findOne(sql, new Object[]{hosnum});
                } else {//不区分科室
                    //System.out.println("--------N----------------------");
                    sql = "select count(*) as count from (select * from (select a.*,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and a.examid=b.examid and a.pexamid=b.pexamid) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and a.examid=b.examid and a.pexamid=b.pexamid and b.excdate is not null) as fin from pexam_mans a where a.hosnum=? and to_char(a.bdate,'yyyy')=to_char(sysdate,'yyyy') and a.bdate is not null and a.edate is null)CHNO where CHNO.total>0)";
                    countMap = (Map<String, Integer>) db.findOne(sql, new Object[]{hosnum});
                }
            } else if ("mainDoctorCheck".equals(method)) {//加载总检医生站候检人名单
                sql = "select count(*) as count from (select a.*,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid and b.excdate is not null) as fin from pexam_mans a where a.hosnum=? and to_char(a.bdate,'yyyy-mm-dd')=to_char(sysdate,'yyyy-mm-dd') and a.bdate is not null and a.edate is null)";
                countMap = (Map<String, Integer>) db.findOne(sql, new Object[]{hosnum});
            }
            returnValue.setStatus(true);
            returnValue.setValue(String.valueOf(countMap.get("count")));
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            returnValue.setStatus(false);
            returnValue.setMessage("查询条数失败");
        } finally {
            db.freeCon();
        }
        pw.print(JSONObject.fromObject(returnValue).toString());
        pw.flush();
        pw.close();
    }

    /*
	 * “体检医生站”或“总检医生站”--加载候检人名单
	 */
    @RequestMapping(value = "/getDoctorStationPatientNew", method = RequestMethod.POST)
    public void getDoctorStationPatientNew(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        String hosnum = basHospitals.getHosnum();
        String deptCode = basDept.getDeptcode();

        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String userid = basUser.getId();

        String method = request.getParameter("method");//总检医生站（mainDoctorCheck）、体检医生站（doctorStation）
        int pageIndex = Integer.parseInt(request.getParameter("index").toString()); // 分页索引
        int pageItems = Integer.parseInt(request.getParameter("size").toString()); // 每页数量
        //开始时间和结束时间  体检医生查询当天的数据
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "";
            List list = null;
            //----获取登录人员的所有关系科室------
            sql = "select r.office_id from bas_user_dept_role_relation r where r.user_id=? group by r.office_id ";
            List<Map> deptcodelist = db.find(sql, new Object[]{userid});
            String alldeptcode = "(";
            for (int i = 0; i < deptcodelist.size(); i++) {
                if (i == 0) {
                    alldeptcode += "b.excdept=" + deptcodelist.get(i).get("office_id");
                } else {
                    alldeptcode += " or b.excdept=" + deptcodelist.get(i).get("office_id");
                }

            }
            alldeptcode += ")";

            //获取是否区分科室
            String isDishDept = ParamBuffer.getParamValue(request, basHospitals.getHosnum(), basHospitals.getNodecode(), "健康体检", "是否区分科室");

            String pagingSql1 = "select OHYEAH.* from (select OHNO.*,rownum no from ("; // 用于分页段1
            String pagingSql2 = ") OHNO where rownum <= ?) OHYEAH where no > ?"; // 用于分页段2
            //	System.out.println("查询候检人名单");
            if ("doctorStation".equals(method)) {//加载体检医生站候检人名单
                if ("Y".equals(isDishDept)) {
                    sql = "select * from (select a.*, to_char(a.bdate,'yyyy-mm-dd') bdate1 , (select count(*) from pexam_items_title b where b.hosnum=a.hosnum and a.examid=b.examid and a.pexamid=b.pexamid and " + alldeptcode + " ) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and a.examid=b.examid and a.pexamid=b.pexamid and " + alldeptcode + " and b.excdate is not null) as fin from pexam_mans a where a.hosnum=?  and a.bdate is not null and a.edate is null and to_char(a.bdate, 'yyyy-MM-dd') >= '" + starttime + "' and to_char(a.bdate, 'yyyy-MM-dd') <= '" + endtime + "' )CHNO where CHNO.total>0";
                    sql = pagingSql1 + sql + pagingSql2;
                    list = db.find(sql, new Object[]{hosnum, pageIndex * pageItems + pageItems, pageIndex * pageItems});
                } else {
                    sql = "select * from (select a.*,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and a.examid=b.examid and a.pexamid=b.pexamid) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and a.examid=b.examid and a.pexamid=b.pexamid and b.excdate is not null) as fin from pexam_mans a where a.hosnum=? and to_char(a.bdate,'yyyy')=to_char(sysdate,'yyyy') and a.bdate is not null and a.edate is null)CHNO where CHNO.total>0";
                    sql = pagingSql1 + sql + pagingSql2;
                    list = db.find(sql, new Object[]{hosnum, pageIndex * pageItems + pageItems, pageIndex * pageItems});
                }
            } else if ("mainDoctorCheck".equals(method)) {//加载总检医生站候检人名单
                sql = "select a.*,to_char(a.bdate,'yyyy-mm-dd') bdate1 ,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid  and b.tjxm='Y' ) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid and b.excdate is not null) as fin from pexam_mans a where a.hosnum=? and to_char(a.bdate,'yyyy-mm-dd')=to_char(sysdate,'yyyy-mm-dd') and a.bdate is not null and a.edate is null  order by a.bdate ";
                sql = pagingSql1 + sql + pagingSql2;
                list = db.find(sql, new Object[]{hosnum, pageIndex * pageItems + pageItems, pageIndex * pageItems});
            }
            JSONArray jsonArr = JSONArray.fromObject(list);
            pw.print(jsonArr.toString());
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

    /**
     * 跳转到 修改项目状态的 页面
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/toUpdateStatus")
    public ModelAndView toUpdateStatus(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
        response.setContentType("text/html;charset=utf-8");
        DBOperator db = null;
        String sql = "";
        List<Map> list = new ArrayList<Map>();
        String pexamid = request.getParameter("pexamid");
        String itemuuid = request.getParameter("itemuuid");
        String ret = "";
        String patname = "";
        try {
            db = new DBOperator();
            patname = URLDecoder.decode(request.getParameter("patname"), "utf-8");
            sql = "select * from pexam_items_title a where a.itemuuid='" + itemuuid + "' and a.pexamid='" + pexamid + "' ";
            list = db.find(sql);
            //ret = JSONArray.fromObject(list).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        modelMap.put("patname", patname);
        modelMap.put("pexamid", list.get(0).get("pexamid"));
        modelMap.put("itemname", list.get(0).get("itemname"));
        modelMap.put("itemuuid", list.get(0).get("itemuuid"));
        modelMap.put("xmstatus", list.get(0).get("xmstatus"));
        return new ModelAndView("pexam/toUpdateStatus", modelMap);
    }

    /**
     * 保存方法  --修改项目状态
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "/toUpdateStatusSave", method = RequestMethod.POST)
    public void demo(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        DBOperator db = null;
        PrintWriter pw = null;
        String sql = "";
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String arr = URLDecoder.decode(request.getParameter("arr"), "utf-8");
            JSONObject a = JSONObject.fromObject(arr);
            String itemuuid = a.getString("itemuuid");
            if (itemuuid == null || "".equals(itemuuid)) {
                throw new Exception("意外错误。项目编号为空！保存失败。");
            }
            String patname = a.getString("patname");
            String pexamid = a.getString("pexamid");
            String itemname = a.getString("itemname");
            String xmstatus = a.getString("xmstatus");
            String status_before = "";
            List<Map> list = null;
            List<Object[]> pi_insert = new ArrayList<Object[]>();
            //先查出原来的状态
            sql = "select * from pexam_items_title a where a.pexamid=? and a.itemuuid=?";
            list = db.find(sql, new Object[]{pexamid, itemuuid});
            status_before = list.get(0).get("xmstatus").toString();
            String e_id = list.get(0).get("excdoctorid") == null ? "" : list.get(0).get("excdoctorid").toString();
            if (!status_before.equals(xmstatus)) {
                if (!"".equals(e_id)) {
                    db.excute("update pexam_items_title a set a.excdate=null,a.excdoctorid='',a.excdoctorname=''   where a.itemuuid = '" + itemuuid + "' and a.pexamid='" + pexamid + "' ");
                }
            }

            if ("弃检".equals(xmstatus)) {
                //插入完成时间  完成人。  （如果不插小项结果点击树出不来。因为他的结果没插）
                sql = "update pexam_items_title a set  a.excdate= sysdate ,a.excdoctorid='" + basUser.getId()
                        + "' ,a.excdoctorname='" + basUser.getName() + "'  where a.itemuuid = '" + itemuuid + "' ";
                db.excute(sql);
                //插入小项结果  先删除再插入
                db.excute("delete from pexam_results a where a.itemuuid='" + itemuuid + "'");
                sql = "select * from pexam_items_title a where a.itemuuid='" + itemuuid + "'";
                list = db.find(sql);
                String examid = list.get(0).get("examid").toString();
                String itemcode = list.get(0).get("itemcode").toString();
                sql = "select c.comid,c.comname,c.parentid, it.indid,it.indname  from pexam_items_comdet i   left join pexam_items_com c on c.comid=i.comid  left join pexam_items_ind it on it.indid=i.indid where i.comid='" + itemcode + "'";
                list = db.find(sql);
                for (Map map : list) {
                    pi_insert.add(new Object[]{hosnum, itemuuid, examid, pexamid, "", "", basUser.getId(), basUser.getName(), new Timestamp(new Date().getTime()), itemcode, itemname, map.get("indid"), map.get("indname"), map.get("parentid"), "", "", "", ""});
                }
                if (pi_insert != null && pi_insert.size() > 0) {
                    Object[][] params_insert = new Object[pi_insert.size()][2];
                    for (int i = 0; i < pi_insert.size(); i++) {
                        params_insert[i] = pi_insert.get(i);
                    }
                    sql = "insert into pexam_results(hosnum,itemuuid,examid,pexamid,excdept,excdeptname,excdoctor,excdoctorname,excdate,comid,comname,indid,indname,parentid,result,unnormal,sn,resultunit)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                    db.excuteBatch(sql, params_insert);
                }

            }
            sql = "update pexam_items_title a set a.xmstatus='" + xmstatus + "'  where a.itemuuid = '" + itemuuid + "' and a.pexamid='" + pexamid + "' ";
            db.excute(sql);
            db.commit();
            pw.print("保存成功");
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            db.rollback();
            pw.print("保存失败-" + e.getMessage());
        } finally {
            db.freeCon();
            pw.flush();
            pw.close();
        }
    }


    /*
	 * “体检医生站”或“总检医生站”--搜索加载搜索结果--获取条数
	 * 2012-12-14
	 */
    @RequestMapping(value = "/topSearchPatListCount", method = RequestMethod.POST)
    public void topSearchPatListCount(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        String hosnum = basHospitals.getHosnum();
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String userid = basUser.getId();

        String deptCode = basDept.getDeptcode();
        String starttime = "";
        String endtime = "";
        String comparSearch = request.getParameter("comparSearch");
        String examtype = request.getParameter("examtype");
        String village = "";
        String isTest = "";
        String method = request.getParameter("method");//总检医生站（mainDoctorCheck）、体检医生站（doctorStation）
        String searchvalue = request.getParameter("searchvalue");//过滤字符
//		System.out.println("starttime:"+starttime+"endtime:"+endtime+"village:"+village+"isTest:"+isTest);
        ReturnValue returnValue = new ReturnValue();
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "";
            String addSql = "";
            String str = "";
            List list = null;
            Map<String, Integer> countMap = null;

            if (searchvalue != null && (!"".equals(searchvalue))) {
                addSql = " and (instr(a.inputcpy,'" + searchvalue.toUpperCase() + "')>0 or instr(a.inputcwb,'" + searchvalue.toUpperCase() + "')>0 or instr(a.pexamid,'" + searchvalue + "')>0 or instr(a.idnum,'" + searchvalue + "')>0 or instr(a.patname,'" + searchvalue + "')>0 or instr(a.ybbh,'" + searchvalue + "')>0 or instr(a.mzh,'" + searchvalue + "')>0)";
            }

            //-----获取登录人员所有关联科室------------
            sql = "select r.office_id from bas_user_dept_role_relation r where r.user_id=? group by r.office_id ";
            List<Map> deptcodelist = db.find(sql, new Object[]{userid});
            String alldeptcode = "(";
            for (int i = 0; i < deptcodelist.size(); i++) {
                if (i == 0) {
                    alldeptcode += "b.excdept=" + deptcodelist.get(i).get("office_id");
                } else {
                    alldeptcode += " or b.excdept=" + deptcodelist.get(i).get("office_id");
                }

            }
            alldeptcode += ")";


            //获取是否区分科室
            String isDishDept = ParamBuffer.getParamValue(request, basHospitals.getHosnum(), basHospitals.getNodecode(), "健康体检", "是否区分科室");

            //	System.out.println("查询候检人名单");
            if (("loadByTime").equals(comparSearch)) {
                starttime = request.getParameter("starttime");
                endtime = request.getParameter("endtime");
                String sql1 = "";
                if (starttime != null && starttime != "") {
                    sql1 += " and to_char(a.bdate,'yyyy-MM-dd')>='" + starttime + "'";
                }
                if (endtime != null && endtime != "") {
                    sql1 += " and to_char(a.bdate,'yyyy-MM-dd')<='" + endtime + "'";
                }
                if ("doctorStation".equals(method)) {//加载体检医生站候检人名单
                    //	System.out.println("aaaaaa:"+"Y".equals(isDishDept));
                    if ("Y".equals(isDishDept)) {//区分科室
                        //	System.out.println("2...--to--");
                        sql = "select count(*) as count from (select * from (select a.*,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and a.examid=b.examid and a.pexamid=b.pexamid and " + alldeptcode + " ) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and a.examid=b.examid and a.pexamid=b.pexamid and " + alldeptcode + " and b.excdate is not null) as fin from pexam_mans a where a.hosnum=? and to_char(a.bdate,'yyyy')=to_char(sysdate,'yyyy') and a.bdate is not null and a.edate is null" + sql1 + str + addSql + ")CHNO where CHNO.total>0)";
                        countMap = (Map<String, Integer>) db.findOne(sql, new Object[]{hosnum});
                    } else {//不区分科室
                        //	System.out.println("2...2--to--");
                        sql = "select count(*) as count from (select * from (select a.*,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and a.examid=b.examid and a.pexamid=b.pexamid) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and a.examid=b.examid and a.pexamid=b.pexamid and b.excdate is not null) as fin from pexam_mans a where a.hosnum=? and to_char(a.bdate,'yyyy')=to_char(sysdate,'yyyy') and a.bdate is not null and a.edate is null" + sql1 + str + addSql + ")CHNO where CHNO.total>0)";
                        countMap = (Map<String, Integer>) db.findOne(sql, new Object[]{hosnum});
                    }
                } else if ("mainDoctorCheck".equals(method)) {//加载总检医生站候检人名单

                    isTest = URLDecoder.decode(request.getParameter("isTest"), "utf-8");
                    //village = URLDecoder.decode(request.getParameter("village"),"utf-8");

                    if (isTest != null && !isTest.equals("") && !isTest.equals("全部")) {
                        if ("已打印".equals(isTest)) {
                            sql1 += " and a.printtime is not null ";
                        } else {
                            sql1 += " and a.isover = '" + isTest + "'";
                        }
                    }
                    if (village != null && !village.equals("") && !village.equals("全部")) {
                        sql1 += " and a.village = '" + village + "'";
                    }
                    if (!village.equals("") && village != null) {
                        sql1 += " and (a.village like '%" + village + "%')";
                    }
					/*if(!examtype.equals("") && examtype!=null){
						sql1+=" and (a.examtype like '%"+examtype+"%')";
					}*/
                    if (("1").equals(isTest)) {
                        sql1 += " and (select count(*)  from pexam_items_title b where b.hosnum = a.hosnum and a.examid = b.examid and a.pexamid = b.pexamid)!=(select count(*) from pexam_items_title b  where b.hosnum = a.hosnum and a.examid = b.examid and a.pexamid = b.pexamid and b.excdate is not null)";
                    } else if (("2").equals(isTest)) {
                        sql1 += " and (select count(*)  from pexam_items_title b where b.hosnum = a.hosnum and a.examid = b.examid and a.pexamid = b.pexamid) =(select count(*) from pexam_items_title b  where b.hosnum = a.hosnum and a.examid = b.examid and a.pexamid = b.pexamid and b.excdate is not null)";
                    }
                    //	System.out.println("1////////////////////");
                    sql = "select count(*) as count from (select a.*,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid and b.excdate is not null) as fin from pexam_mans a where a.hosnum=?    and a.bdate is not null and a.edate is null" + sql1 + str + addSql + ")";
                    countMap = (Map<String, Integer>) db.findOne(sql, new Object[]{hosnum});
                }

            } else {
                if ("doctorStation".equals(method)) {//加载体检医生站候检人名单
                    if ("Y".equals(isDishDept)) {//区分科室
                        //System.out.println("2...----");
                        sql = "select count(*) as count from (select * from (select a.*,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and a.examid=b.examid and a.pexamid=b.pexamid and " + alldeptcode + " ) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and a.examid=b.examid and a.pexamid=b.pexamid and " + alldeptcode + " and b.excdate is not null) as fin from pexam_mans a where a.hosnum=?  and a.bdate is not null and a.edate is null" + str + addSql + ")CHNO where CHNO.total>0)";
                        countMap = (Map<String, Integer>) db.findOne(sql, new Object[]{hosnum});
                    } else {//不区分科室
                        //System.out.println("2...2----");
                        sql = "select count(*) as count from (select * from (select a.*,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and a.examid=b.examid and a.pexamid=b.pexamid) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and a.examid=b.examid and a.pexamid=b.pexamid and b.excdate is not null) as fin from pexam_mans a where a.hosnum=? and to_char(a.bdate,'yyyy')=to_char(sysdate,'yyyy') and a.bdate is not null and a.edate is null" + str + addSql + ")CHNO where CHNO.total>0)";
                        countMap = (Map<String, Integer>) db.findOne(sql, new Object[]{hosnum});
                    }
                } else if ("mainDoctorCheck".equals(method)) {//加载总检医生站候检人名单
                    sql = "select count(*) as count from (select a.*,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid and b.excdate is not null) as fin from pexam_mans a where a.hosnum=? and   a.bdate is not null and a.edate is null" + str + addSql + ")";
                    countMap = (Map<String, Integer>) db.findOne(sql, new Object[]{hosnum});
                }
            }
            returnValue.setStatus(true);
            returnValue.setValue(String.valueOf(countMap.get("count")));
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            returnValue.setStatus(false);
            returnValue.setMessage("查询条数失败");
        } finally {
            db.freeCon();
        }
        pw.print(JSONObject.fromObject(returnValue).toString());
        pw.flush();
        pw.close();
    }

    /*
	 * “体检医生站”或“总检医生站”--搜索是加载搜索结果数据
	 */
    @RequestMapping(value = "/topSearchPatListData", method = RequestMethod.POST)
    public void topSearchPatListData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        String hosnum = basHospitals.getHosnum();
        String deptCode = basDept.getDeptcode();

        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String userid = basUser.getId();

        String starttime = "";
        String endtime = "";
        String comparSearch = request.getParameter("comparSearch");
        String examtype = request.getParameter("examtype");
//		String starttime = request.getParameter("starttime");
//		String endtime = request.getParameter("endtime");
        String village = "";
        String isTest = "";
        String method = request.getParameter("method");//总检医生站（mainDoctorCheck）、体检医生站（doctorStation）
        String searchvalue = request.getParameter("searchvalue");
        int pageIndex = Integer.parseInt(request.getParameter("index").toString()); // 分页索引
        int pageItems = Integer.parseInt(request.getParameter("size").toString()); // 每页数量
//		System.out.println("starttime:"+starttime+"endtime:"+endtime+"village:"+village+"isTest:"+isTest);
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "";
            String addSql = "";
            List list = null;
            if (searchvalue != null && (!"".equals(searchvalue))) {
                addSql = " and (instr(a.inputcpy,'" + searchvalue.toUpperCase() + "')>0 or instr(a.inputcwb,'" + searchvalue.toUpperCase() + "')>0 or instr(a.pexamid,'" + searchvalue + "')>0 or instr(a.idnum,'" + searchvalue + "')>0 or instr(a.patname,'" + searchvalue + "')>0 or instr(a.ybbh,'" + searchvalue + "')>0 or instr(a.mzh,'" + searchvalue + "')>0 )";
            }
//			if(!starttime.equals("") && starttime!=null){
//				addSql+=" and to_char(a.bdate,'yyyy-mm-dd') >= '"+starttime+"' ";
//			}
//			if(!endtime.equals("") && endtime!=null){
//				addSql+=" and to_char(a.bdate,'yyyy-mm-dd') <= '"+endtime+"' ";
//			}
            if (!village.equals("") && village != null) {
                addSql += " and (a.village like '%" + village + "%')";
            }
            if (("1").equals(isTest)) {
                addSql += " and (select count(*)  from pexam_items_title b where b.hosnum = a.hosnum and a.examid = b.examid and a.pexamid = b.pexamid)!=(select count(*) from pexam_items_title b  where b.hosnum = a.hosnum and a.examid = b.examid and a.pexamid = b.pexamid and b.excdate is not null)";
            } else if (("2").equals(isTest)) {
                addSql += " and (select count(*)  from pexam_items_title b where b.hosnum = a.hosnum and a.examid = b.examid and a.pexamid = b.pexamid) =(select count(*) from pexam_items_title b  where b.hosnum = a.hosnum and a.examid = b.examid and a.pexamid = b.pexamid and b.excdate is not null)";
            }

            //-----获取登录人员所有关联科室------------
            sql = "select r.office_id from bas_user_dept_role_relation r where r.user_id=? group by r.office_id ";
            List<Map> deptcodelist = db.find(sql, new Object[]{userid});
            String alldeptcode = "(";
            for (int i = 0; i < deptcodelist.size(); i++) {
                if (i == 0) {
                    alldeptcode += "b.excdept=" + deptcodelist.get(i).get("office_id");
                } else {
                    alldeptcode += " or b.excdept=" + deptcodelist.get(i).get("office_id");
                }

            }
            alldeptcode += ")";

            //获取是否区分科室
            String isDishDept = ParamBuffer.getParamValue(request, basHospitals.getHosnum(), basHospitals.getNodecode(), "健康体检", "是否区分科室");

            String pagingSql1 = "select OHYEAH.* from (select OHNO.*,rownum no from ("; // 用于分页段1
            String pagingSql2 = ") OHNO where rownum <= ?) OHYEAH where no > ?"; // 用于分页段2
            //	System.out.println("查询候检人名单");
            if (("loadByTime").equals(comparSearch)) {
                String sqlA = "";
                starttime = request.getParameter("starttime");
                endtime = request.getParameter("endtime");
                if (starttime != null && starttime != "") {
                    sqlA += " and to_char(a.bdate,'yyyy-MM-dd')>='" + starttime + "'";
                }
                if (endtime != null && endtime != "") {
                    sqlA += " and to_char(a.bdate,'yyyy-MM-dd')<='" + endtime + "'";
                }
                if ("doctorStation".equals(method)) {//加载体检医生站候检人名单
                    if ("Y".equals(isDishDept)) {
                        //	System.out.println("1.....tosearch.....");
                        sql = "select * from (select a.*,to_char(a.bdate,'yyyy-mm-dd') bdate1 , (select count(*) from pexam_items_title b where b.hosnum=a.hosnum and a.examid=b.examid and a.pexamid=b.pexamid and " + alldeptcode + " ) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and a.examid=b.examid and a.pexamid=b.pexamid and " + alldeptcode + " and b.excdate is not null) as fin from pexam_mans a where a.hosnum=?  and a.bdate is not null and a.edate is null" + sqlA + addSql + ")CHNO where CHNO.total>0";
                        sql = pagingSql1 + sql + pagingSql2;
                        list = db.find(sql, new Object[]{hosnum, pageIndex * pageItems + pageItems, pageIndex * pageItems});
                    } else {
                        //	System.out.println("1.......2.tosearch..");
                        sql = "select * from (select a.*,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and a.examid=b.examid and a.pexamid=b.pexamid) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and a.examid=b.examid and a.pexamid=b.pexamid and b.excdate is not null) as fin from pexam_mans a where a.hosnum=? and to_char(a.bdate,'yyyy')=to_char(sysdate,'yyyy') and a.bdate is not null and a.edate is null" + sqlA + addSql + ")CHNO where CHNO.total>0";
                        sql = pagingSql1 + sql + pagingSql2;
                        list = db.find(sql, new Object[]{hosnum, pageIndex * pageItems + pageItems, pageIndex * pageItems});
                    }
                } else if ("mainDoctorCheck".equals(method)) {//加载总检医生站候检人名单

                    isTest = URLDecoder.decode(request.getParameter("isTest"), "utf-8");
                    //village = URLDecoder.decode(request.getParameter("village"),"utf-8");

                    if (isTest != null && !isTest.equals("") && !isTest.equals("全部")) {
                        if ("已打印".equals(isTest)) {
                            sqlA += " and a.printtime is not null ";
                        } else {
                            sqlA += " and a.isover = '" + isTest + "'";
                        }
                    }
                    if (village != null && !village.equals("") && !village.equals("全部")) {
                        sqlA += " and a.village = '" + village + "'";
                    }
                    if (!village.equals("") && village != null) {
                        sqlA += " and (a.village like '%" + village + "%')";
                    }
					/*if(!examtype.equals("") && examtype!=null){
						sqlA+=" and (a.examtype like '%"+examtype+"%')";
					}*/
                    if (("1").equals(isTest)) {
                        sqlA += " and (select count(*)  from pexam_items_title b where b.hosnum = a.hosnum and a.examid = b.examid and a.pexamid = b.pexamid)!=(select count(*) from pexam_items_title b  where b.hosnum = a.hosnum and a.examid = b.examid and a.pexamid = b.pexamid and b.excdate is not null)";
                    } else if (("2").equals(isTest)) {
                        sqlA += " and (select count(*)  from pexam_items_title b where b.hosnum = a.hosnum and a.examid = b.examid and a.pexamid = b.pexamid) =(select count(*) from pexam_items_title b  where b.hosnum = a.hosnum and a.examid = b.examid and a.pexamid = b.pexamid and b.excdate is not null)";
                    }
                    //	System.out.println("2..................");
                    sql = "select a.*, to_char(a.bdate,'yyyy-mm-dd') bdate1 , (select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid and b.excdate is not null) as fin from pexam_mans a where a.hosnum=?   and a.bdate is not null and a.edate is null    " + sqlA + "  " + addSql + "     order by a.bdate  ";
                    sql = pagingSql1 + sql + pagingSql2;
                    list = db.find(sql, new Object[]{hosnum, pageIndex * pageItems + pageItems, pageIndex * pageItems});
                }

            } else {
                if ("doctorStation".equals(method)) {//加载体检医生站候检人名单
                    if ("Y".equals(isDishDept)) {
                        //	System.out.println("1..........");
                        sql = "select * from (select a.*,to_char(a.bdate,'yyyy-mm-dd') bdate1 , (select count(*) from pexam_items_title b where b.hosnum=a.hosnum and a.examid=b.examid and a.pexamid=b.pexamid and " + alldeptcode + " ) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and a.examid=b.examid and a.pexamid=b.pexamid and " + alldeptcode + " and b.excdate is not null) as fin from pexam_mans a where a.hosnum=?  and a.bdate is not null and a.edate is null" + addSql + ")CHNO where CHNO.total>0";
                        sql = pagingSql1 + sql + pagingSql2;
                        list = db.find(sql, new Object[]{hosnum, pageIndex * pageItems + pageItems, pageIndex * pageItems});
                    } else {
                        //	System.out.println("1.......2...");
                        sql = "select * from (select a.*,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and a.examid=b.examid and a.pexamid=b.pexamid) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and a.examid=b.examid and a.pexamid=b.pexamid and b.excdate is not null) as fin from pexam_mans a where a.hosnum=? and to_char(a.bdate,'yyyy')=to_char(sysdate,'yyyy') and a.bdate is not null and a.edate is null" + addSql + ")CHNO where CHNO.total>0";
                        sql = pagingSql1 + sql + pagingSql2;
                        list = db.find(sql, new Object[]{hosnum, pageIndex * pageItems + pageItems, pageIndex * pageItems});
                    }
                } else if ("mainDoctorCheck".equals(method)) {//加载总检医生站候检人名单
                    sql = "select a.*, to_char(a.bdate,'yyyy-mm-dd') bdate1 ,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid and b.excdate is not null) as fin from pexam_mans a where a.hosnum=?  and a.bdate is not null and a.edate is null   " + addSql + "  order by a.bdate ";
                    sql = pagingSql1 + sql + pagingSql2;
                    list = db.find(sql, new Object[]{hosnum, pageIndex * pageItems + pageItems, pageIndex * pageItems});
                }
            }

            JSONArray jsonArr = JSONArray.fromObject(list);
            pw.print(jsonArr.toString());
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
	 * “体检医生站”或“总检医生站”--体检项目的树
	 * 2012-12-05
	 */
    @RequestMapping(value = "/createTreeNew", method = RequestMethod.GET)
    public void loadExamineTree_xj(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String userid = basUser.getId();
        //System.out.println("========>"+userid);
        String hosnum = basHospitals.getHosnum();
        String deptCode = basDept.getDeptcode();

        String examid = request.getParameter("examid");//体检编号--如果为个人”0000“
        String pexamid = request.getParameter("pexamid");//体检号
        String method = request.getParameter("method");//体检医生站（doctorStation）、总检医生站（mainDoctorCheck）

        PrintWriter pw = null;
        DBOperator db = null;
        //DBOperator db2 = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            //db2 = new DBOperator("sfdb");
            String sql = "";
            String lissql = "";

            List list = null;
            List<Map> listdate = null;//lis数据
            String defaultSelectNodeId = "";
            sql = "select r.office_id from bas_user_dept_role_relation r where r.user_id=? group by r.office_id ";
            List<Map> deptcodelist = db.find(sql, new Object[]{userid});
            String alldeptcode = "(";
            for (int i = 0; i < deptcodelist.size(); i++) {
                if (i == 0) {
                    alldeptcode += "a.excdept=" + deptcodelist.get(i).get("office_id");
                } else {
                    alldeptcode += " or a.excdept=" + deptcodelist.get(i).get("office_id");
                }

            }
            alldeptcode += ")";
            //获取是否区分科室
            String isDishDept = ParamBuffer.getParamValue(request, basHospitals.getHosnum(), basHospitals.getNodecode(), "健康体检", "是否区分科室");

            if ("doctorStation".equals(method)) {//体检医生站
                if ("Y".equals(isDishDept)) {
                    sql = "select a.itemcode as comid,a.itemname as comname,a.groupid,a.itemuuid,a.excdeptname,a.comclass,nvl(to_char(a.excdate,'yyyy-mm-dd'),'--') as excdates from pexam_items_title a left join pexam_items_com c on a.itemcode=c.comid and c.hosnum=a.hosnum left join pexam_items_type t on c.parentid=t.typeid and t.hosnum=c.hosnum where a.hosnum=? and a.pexamid=? and " + alldeptcode;
                    sql += " order by to_number(t.sn),to_number(a.sn)";
                    list = db.find(sql, new Object[]{hosnum, pexamid});
                } else {
                    sql = "select a.itemcode as comid,a.itemname as comname,a.groupid,a.itemuuid,a.excdeptname,a.comclass ,nvl(to_char(a.excdate,'yyyy-mm-dd'),'--') as excdates from pexam_items_title a left join pexam_items_com c on a.itemcode=c.comid and c.hosnum=a.hosnum left join pexam_items_type t on c.parentid=t.typeid and t.hosnum=c.hosnum where a.hosnum=? and a.pexamid=? order by to_number(t.sn),to_number(a.sn)";
                    list = db.find(sql, new Object[]{hosnum, pexamid});
                }
            } else if ("mainDoctorCheck".equals(method)) {//总检医生站
                sql = "select  a.xmstatus,  a.itemcode as comid, nvl(a.afterhb_name,a.itemname) as comname, a.groupid,a.itemuuid,a.excdeptname,a.comclass ,nvl(to_char(a.excdate,'yyyy-mm-dd'),'--') as excdates from pexam_items_title a left join pexam_items_com c on a.itemcode=c.comid and c.hosnum=a.hosnum left join pexam_items_type t on c.parentid=t.typeid and t.hosnum=c.hosnum where a.tjxm='Y'  and a.hosnum=? and a.pexamid=?  and a.parent_comid is null  order by to_number(t.sn),to_number(a.sn)";
                list = db.find(sql, new Object[]{hosnum, pexamid});
            }

            List<String> lstTree = new ArrayList<String>();
            int i = 1;
            String temp = "";
            String s1 = "{id:0, pId:-1, name:\"体检项目\" , open:true}";
            lstTree.add(s1);
            boolean isLoadLisData = false;//加载的项目中是否存在检验项目，存在true
            List<Map> jylist = new ArrayList();//存放检验项目的list
            if (list != null && list.size() > 0) {
                Map map0 = null;
                for (int j = 0; j < list.size(); j++) {
                    map0 = (Map) list.get(j);
                    String id = (String) map0.get("comid");//加载指标
                    String pid = "0";//现在只有两层结构
                    String excdates = (String) map0.get("excdates");
                    //如果excdates不为'--'则有时间，即表示体检完了的
                    String name = "";//组合名称
                    if ("--".equals(excdates)) {
                        name = (String) map0.get("comname") + "*";//组合名称
                    } else {
                        name = (String) map0.get("comname");//组合名称
                    }
                    String itemuuid = (String) map0.get("itemuuid");//中间表id
                    String groupid = (String) map0.get("groupid");
                    String comclass = (String) map0.get("comclass");
                    String xmstatus = (String) map0.get("xmstatus");
                    String excdoctorname = (String) map0.get("excdoctorname") == null ? "" : (String) map0.get("excdoctorname");//体检医生--空代表为未检
                    Date excdate = (Date) map0.get("excdate");//审核日期
                    //更加状态 添加不同的图标
                    if ("未检".equals(xmstatus)) {
                        temp = "{id:\"" + id + "\",pId:\"" + pid + "\",name:\"" + name + "\",itemuuid:\"" + itemuuid + "\",groupid:\"" + groupid + "\",excdoctorname:\"" + excdoctorname + "\",comclass:\"" + comclass + "\",icon:'img/yhtj/yhtj_wj.gif' }";
                    } else if ("在检".equals(xmstatus)) {
                        temp = "{id:\"" + id + "\",pId:\"" + pid + "\",name:\"" + name + "\",itemuuid:\"" + itemuuid + "\",groupid:\"" + groupid + "\",excdoctorname:\"" + excdoctorname + "\",comclass:\"" + comclass + "\",icon:'img/yhtj/yhtj_zj.gif' }";
                    } else if ("待检".equals(xmstatus)) {
                        temp = "{id:\"" + id + "\",pId:\"" + pid + "\",name:\"" + name + "\",itemuuid:\"" + itemuuid + "\",groupid:\"" + groupid + "\",excdoctorname:\"" + excdoctorname + "\",comclass:\"" + comclass + "\",icon:'img/yhtj/yhtj_dj.gif' }";
                    } else if ("弃检".equals(xmstatus)) {
                        temp = "{id:\"" + id + "\",pId:\"" + pid + "\",name:\"" + name + "\",itemuuid:\"" + itemuuid + "\",groupid:\"" + groupid + "\",excdoctorname:\"" + excdoctorname + "\",comclass:\"" + comclass + "\",icon:'img/yhtj/yhtj_qj.gif' }";
                    } else if ("完成".equals(xmstatus)) {
                        temp = "{id:\"" + id + "\",pId:\"" + pid + "\",name:\"" + name + "\",itemuuid:\"" + itemuuid + "\",groupid:\"" + groupid + "\",excdoctorname:\"" + excdoctorname + "\",comclass:\"" + comclass + "\",icon:'img/yhtj/yhtj_wc.gif' }";
                    }

                    lstTree.add(temp);

                    if (j == 0) {
                        defaultSelectNodeId = id;
                    }
                    if ("检验".equals(comclass)) {//存在检验项目，将标志设置为true
                        if (excdate == null || excdate.equals("")) {//将未保存过的检验项目筛选出来
                            Map itemMap = new HashMap();//存放检验项目的map
                            itemMap.put("comid", id);
                            itemMap.put("comname", name);
                            itemMap.put("itemuuid", itemuuid);
                            jylist.add(itemMap);
                            isLoadLisData = true;
                        }
                    }

                }
            }

            if ("mainDoctorCheck".equals(method)) {
                String resultcheck = "{id:\"zjbg\",pId:\"" + 0 + "\",name:\"体检总结\"}";
                lstTree.add(resultcheck);
                //lstTree.add("{id:\"tjzj\",pId:\"" + 0 + "\",name:\"体检总结\"}");
                lstTree.add("{id:\"jkjy\",pId:\"" + 0 + "\",name:\"健康建议\"}");
                lstTree.add("{id:\"wxts\",pId:\"" + 0 + "\",name:\"温馨提示\"}");

            }
	          /*
				//存在检验，加载lis数据
				if(isLoadLisData){
					lissql="select * from tjjkhyxx x where x.tjbh=? ";
					listdate=db2.find(lissql, new Object[]{pexamid});

					List jydxinds=null;
					int lisnum=0;//已更新某大项下的小项指标数量

					if(listdate!=null&&listdate.size()>0){//代表有该病人的lis数据

						List<Object[]> del_pi = new ArrayList<Object[]>();//删除
						List<Object[]> ins_pi = new ArrayList<Object[]>();//默认初始值插入
						List<Object[]> inslis_pi = new ArrayList<Object[]>();//lis结果插入
						List<Object[]> updlis_pi = new ArrayList<Object[]>();//更新 小项lis结果
						List<Object[]> upd_pi = new ArrayList<Object[]>();//更新

						//遍历检验项目的大项
						for(int n=0;n<jylist.size();n++){
							Map projectMap = null;//每个组合项的数据
							List<Map> indlist=null;//某组合项下小项的集合
							Date excdate=null;
							lisnum=0;//初始化为0;
							StringBuffer sb = new StringBuffer();//检验类型科室小结
							//所有要清空的大项结果
							String comid=(String) jylist.get(n).get("comid");
							String comname=(String) jylist.get(n).get("comname");
							String itemuuid=(String) jylist.get(n).get("itemuuid");
							del_pi.add(new Object[]{hosnum,itemuuid,pexamid});

							//某检验大项下所有要插的小项
							sql="select d.* from pexam_items_ind d,pexam_items_comdet t where d.hosnum=t.hosnum and d.indid=t.indid and  d.hosnum=? and t.comid=?  ";
							jydxinds=db.find(sql, new Object[]{hosnum,comid});
							Map indMap =null;

							//更新lis程序中有结果的小项指标
							sql="select d.*,n.sn,n.indname,r.lisindid,r.lisindname from pexam_items_comdet d left join pexam_items_ind n  on d.hosnum=n.hosnum and d.indid=n.indid left join pexam_lisrelcation r on r.hosnum=d.hosnum and r.tjindid=n.indid where  d.hosnum=? and d.comid=? ";
							indlist=db.find(sql, new Object[]{hosnum,comid});

							if(indlist!=null&&indlist.size()>0){
								List<Object[]> inds_pi = new ArrayList<Object[]>();//小项标准id
								for(int k=0;k<indlist.size();k++){
									String indid=(String) indlist.get(k).get("indid");//体检系统字典库 小项id
									String indname=(String) indlist.get(k).get("indname");//体检系统字典库 小项name
									String lisindid=(String) indlist.get(k).get("lisindid")==null?"":(String) indlist.get(k).get("lisindid");//对照字典表的小项id
									String lisindname=(String) indlist.get(k).get("lisindname")==null?"":(String) indlist.get(k).get("lisindname");//对照字典表的小项name
									int sn=((BigDecimal) indlist.get(k).get("sn")).intValue();
									for(int m=0;m<listdate.size();m++){
										Map lisindMap=new HashMap();
										lisindMap=listdate.get(m);
										String xmbh=(String) lisindMap.get("xmbh");//转换前小项id
										String xmmc=(String) lisindMap.get("xmmc");//转换前小项name

										if(lisindid.equals(xmbh)){
											String dw= (String) lisindMap.get("dw"); //小项单位
											String jgz= (String) lisindMap.get("jgz");//小项结果
											String ckfw= (String) lisindMap.get("ckfw");//参考范围
											Date jyrq= DateUtil.stringToDate((String)lisindMap.get("jyrq"),"yyyy-mm-dd");//检验日期
											String ycts=(String) lisindMap.get("ycts");//异常状态
											if(ycts.equals("d")){
												ycts="↓";
												sb.append(indname+":"+jgz+dw);
												sb.append(" "+ycts);
												sb.append(("".equals(ckfw)?"":("(参考值：" + ckfw + ");")));
											}else if(ycts.equals("g")){
												ycts="↑";
												sb.append(indname+":"+jgz+dw);
												sb.append(" "+ycts);
												sb.append(("".equals(ckfw)?"":("(参考值：" + ckfw + ");")));
											}else {
												ycts="";
											}
											//updlis_pi.add(new Object[]{dw,jgz,ckfw,jyrq,hosnum,pexamid,xmbh});
											inslis_pi.add(new Object[]{hosnum,examid,pexamid,ycts,jgz,comid,comname,indid,
													indname,"检验",itemuuid,sn,ckfw,dw});

											lisnum++;
											if(jyrq!=null && !jyrq.equals("")){
												excdate=jyrq;//检验日期赋为审核日期
											}

										}

									}
								}

							}
							 //System.out.println("-----------------sb:"+sb.toString());
							 System.out.println("--------list大小+listnum:"+jydxinds.size()+";"+lisnum);
							 if(jydxinds.size()==lisnum && lisnum!=0){//相等代表某项检验完成
								 String typeflag="Y";//判断lis结果是手输的 还是 仪器传回来的 Y代表传回来的
								 upd_pi.add(new Object[]{excdate,typeflag,sb.toString(),hosnum,itemuuid});

								 //删除某检验大项下所有小项的结果
									Object[][] del_params = new Object[del_pi.size()][2];
									for(int j=0;j<del_pi.size();j++){
										del_params[j] = del_pi.get(j);
									}
									sql = "delete from pexam_results a where a.hosnum=? and a.itemuuid=? and a.pexamid=?";
									db.excuteBatch(sql,del_params);
									//插入lis结果到pexam_result表
									Object[][] inslis_params = new Object[inslis_pi.size()][2];
									for(int j=0;j<inslis_pi.size();j++){
										inslis_params[j] = inslis_pi.get(j);
									}
									sql = "insert into pexam_results(hosnum,examid,pexamid,unnormal,result,comid,comname,indid,indname," +
											"examtype,itemuuid,sn,range,resultunit)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
									db.excuteBatch(sql, inslis_params);
									//更新pexam_items_title表
									Object[][] upd_params = new Object[upd_pi.size()][2];
									for(int j=0;j<upd_pi.size();j++){
										upd_params[j] = upd_pi.get(j);
									}
									//sql = "update pexam_items_title a set a.excdate=?,a.excdoctorid=?,a.excdoctorname=?,a.checkdate=?,a.checkdoctorid=?,a.checkdoctorname=? where a.hosnum=? and a.itemuuid=?";
									sql = "update pexam_items_title a set a.excdate=?,a.typeflag=?,a.deptsum=? where a.hosnum=? and a.itemuuid=?";
									db.excuteBatch(sql, upd_params);
							 }

						 }


						}
					}
			*/
            JSONArray jsonArr = JSONArray.fromObject(lstTree);
            pw.print(jsonArr.toString());
            db.commit();
            //db2.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            db.rollback();
            //db2.rollback();
            pw.print("fail");
        } finally {
            db.freeCon();
            //db2.freeCon();
        }
        pw.flush();
        pw.close();
    }

    /*
	 * 体检医生站--获取体检项目的指标项
	 * 2012-12-05
	 */
    @RequestMapping(value = "/createExamNew2", method = RequestMethod.POST)
    public void loadexamdetails2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();

        String itemcode = request.getParameter("itemcode");//组合项目id
        String pexamid = request.getParameter("pexamid");//体检编号
        String examid = request.getParameter("examid");//预约id
        String itemuuid = request.getParameter("itemuuid");//pexam_items_title主键
        String sex = request.getParameter("sex");//体检人员性别
        String comClass = request.getParameter("comclass");//项目类型---检验、检查、其他
        String isShowGXY = "";//是否显示收缩压，舒张压
        String lisType = ParamBuffer.getParamValue(request, basHospitals.getHosnum(), basHospitals.getNodecode(), "健康体检", "lis系统开发商");
        ;//lis系统厂商类型
        PrintWriter pw = null;
        DBOperator db = null;
        //DBOperator db2 = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            //db2 = new DBOperator();
            String sql = "";
            String lissql = "";
            Map tempMap = null;
            Map temp = new HashMap();//返回前台的数据
            List list = null;//体检结果
            String deptSum = "";//科室小结
            String typeflag = "";//检验项目手输 还是 自动获取标志 Y代表自动获取
            String isExamStart = "";//是否已经返回lis数据
            String itemname = "";//大项名称

            String excdoctorid = "";//体检医生id
            String excdoctorname = "";//体检医生姓名
            Date excdate = null;//体检医生保存的时间

            String checkdoctorid = "";//审核医生id
            String checkdoctorname = "";//审核医生
            Date checkdate = null;//审核日期

            sql = "select a.tmcode,a.excdoctorname,a.excdate,a.deptsum,a.comclass,a.typeflag,a.itemname from pexam_items_title a where a.hosnum=? and a.pexamid=? and a.itemuuid=? and a.itemcode=?";
            tempMap = (Map) db.findOne(sql, new Object[]{hosnum, pexamid, itemuuid, itemcode});
            itemname = (String) tempMap.get("itemname");
            typeflag = (String) tempMap.get("typeflag") == null ? "" : (String) tempMap.get("typeflag");//
            excdoctorname = (String) tempMap.get("excdoctorname") == null ? "" : (String) tempMap.get("excdoctorname");//体检医生
            deptSum = (String) tempMap.get("deptsum") == null ? "" : (String) tempMap.get("deptsum");//科室小结
            excdate = (Date) tempMap.get("excdate");


            String lisflag = "";//判断检验中是否有检验数据了
            String unMatchDate = " ";//未匹配中项目提示

            //System.out.println("comClass1="+comClass);
            if (("检验".equals(comClass)) || ("外送").equals(comClass)) {
                //System.out.println("comClass2="+comClass);
                //System.out.println("typeflag="+typeflag);
                typeflag = "Y";
                if (("Y").equals(typeflag)) {
                    //直接从结果表查询返回
                    sql = "select * from pexam_results a where a.hosnum=? and a.pexamid=? and a.itemuuid=? and a.comid=?";
                    List<PexamResult> resultList = db.find(sql, new Object[]{hosnum, pexamid, itemuuid, itemcode}, PexamResult.class);

                    //获取小结和审核医生姓名
                    sql = "select a.checkdoctorname,a.excdate,a.deptsum,a.comclass from pexam_items_title a where a.hosnum=? and a.pexamid=? and a.itemuuid=? and a.itemcode=?";
                    tempMap = (Map) db.findOne(sql, new Object[]{hosnum, pexamid, itemuuid, itemcode});

                    //返回前台数据
                    list = resultList;
                    //excdoctorname = (String)tempMap.get("checkdoctorname");//审核医生，如果是”其他“类型则是体检医生
                    deptSum = (String) tempMap.get("deptsum");
                    deptSum = deptSum == null ? "" : deptSum;
                    temp.put("isExamStart", isExamStart);//检验特有的参数

                } else if (("N").equals(typeflag)) {
                    //检验项目手工保存过
                    String operationType = "";

                    operationType = "modify";
                    sql = "select a.numvalue,a.result,a.unnormal,b.maxval,b.maxpromp,b.minval,b.minpromp," +
                            "b.defaultv,b.resultunit,b.resulttype,b.indid,b.indname,b.sn from pexam_results a,pexam_items_ind b " +
                            "where a.hosnum=b.hosnum and a.indid=b.indid and a.hosnum=? and a.comid=? and a.pexamid=? and a.itemuuid=? ";
                    list = db.find(sql, new Object[]{hosnum, itemcode, pexamid, itemuuid}, PexamIndOrResult.class);

                    temp.put("operationType", operationType);//其他类型特有

                } else {
                    //检验项目未获取过
                    //-----------从老板处获取lis检验数据-------------------
                    String tmcode = (String) tempMap.get("tmcode") == null ? "" : (String) tempMap.get("tmcode");//体检医生
                    Map lisDataMap = this.getListDataByItemuuid(hosnum, pexamid, tmcode, db, lisType);
                    String status = (String) lisDataMap.get("status");

                    List<PexamResult> resultList = new ArrayList<PexamResult>();//保存返回前台的数据
                    List<Object[]> pi = new ArrayList<Object[]>();//保存要插入pexam_result的数据
                    StringBuffer sb = new StringBuffer();//收集小结信息


                    excdoctorid = "";//体检医生id
                    excdoctorname = "";//体检医生姓名
                    excdate = null;

                    checkdoctorid = "";//审核医生id
                    checkdoctorname = "";//审核医生名字
                    checkdate = null;//审核时间

                    String comId = "";
                    String comName = "";

                    if ("未开始".equals(status)) {
                        lisflag = "N";
                    } else {
                        excdoctorid = (String) lisDataMap.get("excdoctorid");
                        excdoctorname = (String) lisDataMap.get("excdoctorname");
                        // System.out.println("-----------执行人1"+excdoctorname);
                        excdate = (Date) lisDataMap.get("excdate");
                        if (excdate == null) {
                        } else {
                            excdate = new Timestamp(((Date) lisDataMap.get("excdate")).getTime());//体检时间
                        }

                        checkdoctorname = (String) lisDataMap.get("checkdoctorname");
                        // checkdate=new Timestamp(((Date)lisDataMap.get("checkdate")).getTime());//体检时间

                        checkdate = (Date) lisDataMap.get("checkdate");
                        if (checkdate == null) {
                        } else {
                            checkdate = new Timestamp(((Date) lisDataMap.get("checkdate")).getTime());//体检时间
                        }

                        comId = (String) lisDataMap.get("comid");//大项id
                        comName = (String) lisDataMap.get("comname");//大项名称

                        //------ 检验项目开始匹配--------
                        List<Map> listdate = (List<Map>) lisDataMap.get("resultList");//lis_testbak返回的检验结果集
                        // PexamResult resultObj = null;
                        List jydxinds = null;
                        int lisnum = 0;//已更新某大项下的小项指标数量
                        if (listdate != null && listdate.size() > 0) {//代表有该病人的lis数据
                            lisflag = "Y";//--代表有LIS数据,但不一定有那个项目的结果，lis_testbak是把所有检验结果返回回来的--
                            List<Object[]> del_pi = new ArrayList<Object[]>();//删除
                            List<Object[]> inslis_pi = new ArrayList<Object[]>();//lis结果插入
                            List<Object[]> updlis_pi = new ArrayList<Object[]>();//更新 小项lis结果
                            List<Object[]> upd_pi = new ArrayList<Object[]>();//更新

                            //遍历检验项目的大项
                            Map projectMap = null;//每个组合项的数据
                            List<Map> indlist = null;//某组合项下小项的集合
                            Date lisexcdate = null;
                            lisnum = 0;//初始化为0;
                            //所有要清空的大项结果
                            del_pi.add(new Object[]{hosnum, itemuuid, pexamid});

                            //某检验大项下所有要插的小项
                            sql = "select d.* from pexam_items_ind d,pexam_items_comdet t where d.hosnum=t.hosnum and d.indid=t.indid and  d.hosnum=? and t.comid=?  ";
                            jydxinds = db.find(sql, new Object[]{hosnum, itemcode});
                            Map indMap = null;

                            //更新lis程序中有结果的小项指标
                            sql = "select d.*,n.sn,n.indname,r.lisindid,r.lisindname from pexam_items_comdet d left join pexam_items_ind n  on d.hosnum=n.hosnum and d.indid=n.indid left join pexam_lisrelcation r on r.hosnum=d.hosnum and r.tjindid=n.indid where  d.hosnum=? and r.nodecode=? and d.comid=? ";
                            indlist = db.find(sql, new Object[]{hosnum, nodecode, itemcode});

                            if (indlist != null && indlist.size() > 0) {
                                List<Object[]> inds_pi = new ArrayList<Object[]>();//小项标准id
                                for (int k = 0; k < indlist.size(); k++) {
                                    String indid = (String) indlist.get(k).get("indid");//体检系统字典库 小项id
                                    String indname = (String) indlist.get(k).get("indname");//体检系统字典库 小项name
                                    String lisindid = (String) indlist.get(k).get("lisindid") == null ? "" : (String) indlist.get(k).get("lisindid");//对照字典表的小项id
                                    String lisindname = (String) indlist.get(k).get("lisindname") == null ? "" : (String) indlist.get(k).get("lisindname");//对照字典表的小项name
                                    int sn = ((BigDecimal) indlist.get(k).get("sn")).intValue();
                                    System.out.println("-------体检系统小项id+体检系统项目名称-----------" + indid + ";" + indname);
                                    boolean matchFlag = false;//是否匹配上标志
                                    for (int m = 0; m < listdate.size(); m++) {

                                        Map lisindMap = new HashMap();
                                        //JSONObject lisindMap=new JSONObject();
                                        //lisindMap=(JSONObject) listdate.get(m);
                                        lisindMap = listdate.get(m);
                                        String xmbh = (String) lisindMap.get("indid");//转换前小项id  老板定义的小项id
                                        String xmmc = (String) lisindMap.get("indname");//转换前小项name 老板定义的小项name
                                        //System.out.println("--------取到的lis系统项目111 id+name-----------"+xmbh+";"+xmmc);
                                        //System.out.println("=============="+xmbh);
                                        if (lisindid.equals(xmbh)) {
                                            System.out.println("--------取到的lis系统项目id+name-----------" + xmbh + ";" + xmmc);
                                            String resultunit = (String) lisindMap.get("resultunit"); //小项单位
                                            String result = (String) lisindMap.get("result");//小项结果
                                            String range = (String) lisindMap.get("range");//参考范围
                                            //Date jysj= DateUtil.stringToDate((String)lisindMap.get("jysj"),"yyyy-mm-dd HH:MM:ss");//检验时间
                                            String stringvalue = (String) lisindMap.get("rstatus");//异常状态
                                            if (stringvalue != null && !stringvalue.equals("")) {
                                                sb.append(indname + ":" + result + " " + resultunit);//结果与范围之间体检空格
                                                sb.append(" " + stringvalue);
                                                sb.append(("".equals(range) ? "" : ("(参考值：" + range + ");")));
                                            }

                                            String unnormal = "异常";
                                            if ("".equals(stringvalue)) {
                                                unnormal = "正常";
                                            }
                                            inslis_pi.add(new Object[]{hosnum, examid, pexamid, excdoctorid, excdoctorname, excdate, stringvalue, unnormal, result, itemcode, itemname, indid, indname, "检验", itemuuid, sn, range, resultunit});
                                            lisnum++;
                                            matchFlag = true;
														/*
														if(jysj!=null && !jysj.equals("")){
															excdate=jysj;//检验日期赋为检验时间
														}
														if(jyr!=null && !jyr.equals("")){
															excdoctorname=jyr;//检验人
														}
														if(shsj!=null && !shsj.equals("")){
															checkdate=shsj;//审核日期
														}
														if(shr!=null && !shr.equals("")){
															checkdoctorname=shr;//审核人
														}
														*/
                                        } else {
                                            //-----保存未匹配上的项目-----
                                            System.out.println("-----matchFlag:---" + matchFlag + "----当前m的长度:--" + m + "--lis结果集长度：-------------:" + listdate.size());
                                            if (!matchFlag && ((m + 1) == listdate.size())) {
                                                //indid indname
                                                unMatchDate += " [" + indname + "(" + indid + ")" + "]";
                                            }
                                            //System.out.print("----unMatchDate1:---"+unMatchDate);
                                            //System.out.println("---------没匹配上的编码-------------:"+lisindname);
                                        }

                                    }
                                }

                            }
                            System.out.println("--------list大小+listnum:" + jydxinds.size() + ";" + lisnum + " " + (jydxinds.size() <= lisnum));
                            if (lisnum > 0 && lisnum != 0) {//相等代表某项检验完成
                                if (jydxinds.size() == lisnum) {
                                    lisflag = "F";//代表所有项目匹配上了
                                }
                                typeflag = "Y";//判断lis结果是手输的 还是 仪器传回来的 Y代表传回来的
                                // System.out.println("-----------执行人3"+excdoctorname);
                                upd_pi.add(new Object[]{excdoctorname, excdate, checkdoctorname, checkdate, typeflag, sb.toString(), hosnum, itemuuid});

                                //删除某检验大项下所有小项的结果
                                Object[][] del_params = new Object[del_pi.size()][2];
                                for (int j = 0; j < del_pi.size(); j++) {
                                    del_params[j] = del_pi.get(j);
                                }
                                sql = "delete from pexam_results a where a.hosnum=? and a.itemuuid=? and a.pexamid=?";
                                db.excuteBatch(sql, del_params);
                                //插入lis小项结果到pexam_result表
                                Object[][] inslis_params = new Object[inslis_pi.size()][2];
                                for (int j = 0; j < inslis_pi.size(); j++) {
                                    inslis_params[j] = inslis_pi.get(j);
                                }
                                sql = "insert into pexam_results(hosnum,examid,pexamid,excdoctor,excdoctorname,excdate,stringvalue,unnormal,result,comid,comname,indid,indname,examtype,itemuuid,sn,range,resultunit)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                                db.excuteBatch(sql, inslis_params);
                                //更新pexam_items_title表
                                Object[][] upd_params = new Object[upd_pi.size()][2];
                                for (int j = 0; j < upd_pi.size(); j++) {
                                    upd_params[j] = upd_pi.get(j);
                                }
                                sql = "update pexam_items_title a set a.excdoctorname=?,a.excdate=?,checkdoctorname=?,checkdate=?,typeflag=?,a.deptsum=? where a.hosnum=? and a.itemuuid=?";
                                db.excuteBatch(sql, upd_params);

                                //更新lis状态
                                if (lisType.equals("mq")) {

                                } else {
                                    sql = "update sampling a set a.downloadflag=? where a.hosnum=? and a.patientid=? and a.barcode=?";
                                    //db2.excute(sql,new Object[]{"已下载",hosnum,pexamid,itemuuid});
                                    db.excute(sql, new Object[]{"已下载", hosnum, pexamid, itemuuid});
                                }

                            } else {
                                //----查询到检验返回的数据,但是一项都没匹配上----
                                //lisflag="N";
                                //如果该项未完成
                                excdate = null;
                            }

                        } else {
                            //----查询不到检验返回的数据----
                            lisflag = "N";
                            //如果该项未完成
                            excdate = null;
                        }
                    }


                    //lis结果比对后 是否完成
                    if ("Y".equals(typeflag)) {
                        //直接从结果表查询返回
                        sql = "select * from pexam_results a where a.hosnum=? and a.pexamid=? and a.itemuuid=? and a.comid=?";
                        resultList = db.find(sql, new Object[]{hosnum, pexamid, itemuuid, itemcode}, PexamResult.class);

                        //获取小结和审核医生姓名
                        sql = "select a.checkdoctorname,a.excdate,a.deptsum,a.comclass from pexam_items_title a where a.hosnum=? and a.pexamid=? and a.itemuuid=? and a.itemcode=?";
                        tempMap = (Map) db.findOne(sql, new Object[]{hosnum, pexamid, itemuuid, itemcode});

                        //返回前台数据
                        list = resultList;
                        excdoctorname = (String) tempMap.get("checkdoctorname");//审核医生，如果是”其他“类型则是体检医生
                        deptSum = (String) tempMap.get("deptsum");//检验结果
                        deptSum = deptSum == null ? "" : deptSum;
                        temp.put("isExamStart", isExamStart);//检验特有的参数
                    } else {
                        String operationType = "";
                        operationType = "add";
                        sql = "select c.maxval,c.maxpromp,c.minval,c.minpromp," +
                                "c.defaultv,c.resultunit,c.resulttype,c.indid,c.indname,c.sn " +
                                "from pexam_items_com a, pexam_items_comdet b,pexam_items_ind c " +
                                "where a.hosnum=b.hosnum and a.comid=b.comid and b.indid=c.indid " +
                                "and a.hosnum=c.hosnum and a.hosnum=? and a.comid=? ";
                        //过滤出指标项有性别限制的指标

                        if (!"".equals(sex)) {
                            sql += " and (a.forsex='不限' or a.forsex='" + sex + "')";
                        }

                        sql += " order by c.sn";
                        list = db.find(sql, new Object[]{hosnum, itemcode}, PexamIndOrResult.class);

                        temp.put("operationType", operationType);//其他类型特有

                    }


                }

            } else {

                isShowGXY = ParamBuffer.getParamValue(request, basHospitals.getHosnum(), basHospitals.getNodecode(), "健康体检", "内科显示高血压指标");
                String operationType = "";
                sql = "select a.comid　from pexam_items_com a ,pexam_items_type b where a.hosnum = ? and a.parentid = b.typeid and b.typename = '内科检查'";
                Map comidmap = (Map) db.findOne(sql, new Object[]{hosnum});
                //System.out.println("comid=============="+(String)comidmap.get("comid"));
                //boolean flag = itemcode.equals((String)comidmap.get("comid")==null?"":(String)comidmap.get("comid"));
                boolean flag = true;
                //System.out.println("flag=============="+flag);
                if (tempMap.get("excdate") == null) {//表示未执行过
                    operationType = "add";
                    sql = "select c.maxval,c.maxpromp,c.minval,c.minpromp," +
                            "c.defaultv,c.resultunit,c.resulttype,c.indid,c.indname,c.sn " +
                            "from pexam_items_com a, pexam_items_comdet b,pexam_items_ind c " +
                            "where a.hosnum=b.hosnum and a.comid=b.comid and b.indid=c.indid " +
                            "and a.hosnum=c.hosnum and a.hosnum=? and a.comid=? ";
                    //过滤出指标项有性别限制的指标
                    if (!"".equals(sex)) {
                        sql += " and (c.forsex='不限' or c.forsex=?)";
                    }
                    sql += " order by c.sn";
                    list = db.find(sql, new Object[]{hosnum, itemcode, sex}, PexamIndOrResult.class);

                } else {
                    operationType = "modify";
                    sql = "select a.numvalue,a.result,a.unnormal,b.maxval,b.maxpromp,b.minval,b.minpromp," +
                            "b.defaultv,b.resultunit,b.resulttype,b.indid,b.indname,b.sn from pexam_results a,pexam_items_ind b " +
                            "where a.hosnum=b.hosnum and a.indid=b.indid and a.hosnum=? and a.comid=? and a.pexamid=? and a.itemuuid=? order by b.sn";
                    list = db.find(sql, new Object[]{hosnum, itemcode, pexamid, itemuuid}, PexamIndOrResult.class);
                }
                //System.out.println("list.size============"+list.size());
                temp.put("operationType", operationType);//其他类型特有
                if (flag && "Y".equals(isShowGXY)) {
                    sql = "select r.numvalue, r.result, r.unnormal, c.maxval, c.maxpromp, c.minval,c.minpromp, c.defaultv,c.resultunit,c.resulttype,c.indid, c.indname, c.sn" +
                            " from pexam_results r, pexam_items_ind c,pexam_items_com a, pexam_items_comdet b where  a.hosnum=b.hosnum and a.comid=b.comid and b.indid=c.indid  and a.hosnum=c.hosnum and" +
                            " r.hosnum =? and r.hosnum = c.hosnum and c.indid in ('000000000000130', '000000000000131') and r.pexamid =? and r.indid = c.indid order by c.indid";
                    List ll = db.find(sql, new Object[]{hosnum, pexamid}, PexamIndOrResult.class);
                    //System.out.println("ll.size============"+ll.size());
                    if (ll.size() <= 0) {
                        sql = "select  c.maxval,c.maxpromp,c.minval,c.minpromp,c.defaultv,c.resultunit,c.resulttype,c.indid,c.indname,c.sn  from pexam_items_ind c where c.hosnum = ? and c.indid in ('000000000000130', '000000000000131') ";
                        sql += " order by c.sn";
                        ll = db.find(sql, new Object[]{hosnum}, PexamIndOrResult.class);
                    }
                    //System.out.println("ll.size============"+ll.size());
                    list.addAll(ll);
                }
                //System.out.println("list.size============"+list.size());
            }
            temp.put("details", list);//指标明细
            temp.put("excdoctorname", excdoctorname);//体检医生名字
            temp.put("deptSum", deptSum);//科室小结
            temp.put("typeflag", typeflag);//检验项目手输 还是 自动获取标志
            temp.put("lisflag", lisflag);//判断检验中是否有检验数据了 F(有数据，全匹配上) Y(有数据，部分匹配上) N（无数据）
            //System.out.print("----unMatchDate2:---"+unMatchDate);
            temp.put("unMatchDate", unMatchDate);//未匹配中项目提示

            JSONObject obj = JSONObject.fromObject(temp);
            //System.out.println(obj.toString());
            pw.print(obj.toString());
            db.commit();
            //db2.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            //db2.rollback();
            pw.print("fail");
        } finally {
            db.freeCon();
            //db2.freeCon();
        }
        pw.flush();
        pw.close();
    }

    /*
	 * 常见结果
	 */
    @RequestMapping("/commonResult")
    public ModelAndView commonResult(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String indid = request.getParameter("indid");
        //System.out.println("indid==================="+indid);
        String indname = URLDecoder.decode(request.getParameter("indname"), "utf-8");
        model.put("indid", indid);
        model.put("indname", indname);
        return new ModelAndView("phyexam/commonResult", model);
    }

    /*
	 * 体检医生站--保存体检结果
	 * 2012-12-14
	 */
    @RequestMapping(value = "/saveItemDetailsNew3", method = RequestMethod.POST)
    public void saveItemDetails3(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String nodecode = basHospitals.getNodecode();
        String hosnum = basHospitals.getHosnum();
        String deptCode = basDept.getDeptcode();
        String excdoctorid = basUser.getId();
        String excdoctorname = basUser.getName();
        String comclass = request.getParameter("comclass");//项目类型--检验、其他
        String pexamid = request.getParameter("pexamid");//体检者编号
        String doctorid = request.getParameter("doctorid");//体检医生id
        String doctorname = request.getParameter("doctorname");//体检医生姓名
        String deptsum = URLDecoder.decode(request.getParameter("deptsum"), "utf-8");//科室小结
        String itemuuid = request.getParameter("itemuuid");//
        String typeflag = request.getParameter("typeflag");//检验项目手输 自动获取标志
        deptsum = deptsum.replace("@", "+");
        //deptsum=deptsum.replace("$", "%");
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            String isDishDept = ParamBuffer.getParamValue(request, basHospitals.getHosnum(), basHospitals.getNodecode(), "健康体检", "是否区分科室");

            pw = response.getWriter();
            db = new DBOperator();
            String sql = "";

            if ("检验".equals(comclass) && typeflag.equals("Y")) {
                //	System.out.println("更新pexam_items_title表");
                sql = "update pexam_items_title a set a.checkdate=?,a.checkdoctorid=?,a.checkdoctorname=?,a.deptSum=? where a.hosnum=? and a.itemuuid=?";
                db.excute(sql, new Object[]{new Timestamp(new Date().getTime()), doctorid, doctorname, deptsum, hosnum, itemuuid});
            } else {
                String operationType = request.getParameter("operationType");
                String examid = request.getParameter("examid");
                String comid = request.getParameter("comid");
                String comname = request.getParameter("comname");
                String resultArr = URLDecoder.decode(request.getParameter("resultArr"), "utf-8");
                JSONArray jsonArr = JSONArray.fromObject(resultArr);

                if ("检验".equals(comclass)) {
                    typeflag = "N";
                    //	System.out.println("更新pexam_items_title表");
                    sql = "update pexam_items_title a set a.excdate=?,a.excdoctorid=?,a.excdoctorname=?,a.deptSum=?,a.typeflag=? where a.hosnum=? and a.itemuuid=?";
                    db.excute(sql, new Object[]{new Timestamp(new Date().getTime()), doctorid, doctorname, deptsum, typeflag, hosnum, itemuuid});
                } else {
                    //其他类型
                    //	System.out.println("更新pexam_items_title表");
                    sql = "update pexam_items_title a set a.excdate=?,a.excdoctorid=?,a.excdoctorname=?,a.deptSum=? where a.hosnum=? and a.itemuuid=?";
                    db.excute(sql, new Object[]{new Timestamp(new Date().getTime()), doctorid, doctorname, deptsum, hosnum, itemuuid});
                }

                List<Object[]> pi = new ArrayList<Object[]>();
                List<Object[]> pi_insert = new ArrayList<Object[]>();
                List<Object[]> sugests_insert = new ArrayList<Object[]>();
                List temp = null;
                sql = "delete from pexam_items_sugests s where s.pexamid=? and s.typeid=?";
                db.excute(sql, new Object[]{pexamid, comid});
                for (int i = 0; i < jsonArr.size(); i++) {
                    JSONObject json = jsonArr.getJSONObject(i);
                    String result2 = json.getString("result");//结果
                    String indname = json.getString("indname");//节点name 如何为组合节点 则为父节点name
                    String indid = json.getString("indid");//节点id 如何为组合节点 则为父节点id
                    String isnormal = json.getString("isnormal");//状态
                    String sn = json.getString("sn");
                    String resultunit = json.getString("resultunit");//单位
                    String dtype = json.getString("dtype");
                    String sonindid = json.getString("sonindid");//子节点id
                    String sonindname = json.getString("sonindname");//子节点name
                    String iszh = json.getString("iszh");//是否组合

                    //System.out.println("===>name:"+name);
                    String regEx = "^[0-9]*$";
                    int maxvalue = 0;
                    int minvalue = 0;
                    int result3 = 0;
                    if (dtype == "num" || "num".equals(dtype)) {
                        if (json.getString("maxvalue").matches(regEx)) {
                            maxvalue = Integer.parseInt(json.getString("maxvalue"));
                        }
                        if (json.getString("minvalue").matches(regEx)) {
                            minvalue = Integer.parseInt(json.getString("minvalue"));
                        }
                        if (!"".equals(result2) && result2.matches(regEx)) {
                            result3 = Integer.parseInt(result2);
                        }
                        //System.out.println("maxvalue:"+maxvalue);
                        //System.out.println("minvalue:"+minvalue);
                        //System.out.println("result3:"+result3);
                    }
                    if (maxvalue != 0) {
                        if (result3 > maxvalue) {
                            sql = "select * from pexam_sugests s where s.indname=? and s.resulttype='↑' ";
                            List<Map> list_num = db.find(sql, new Object[]{indname});
                            if (list_num.size() > 0) {
                                String uuid = UuidUtil.getUuid();
                                sugests_insert.add(new Object[]{uuid, hosnum, nodecode, comid, indid, list_num.get(0).get("classname"), sn, list_num.get(0).get("sugestid"), "↑", pexamid});
                            }
                        }
                        if (result3 < minvalue) {
                            sql = "select * from pexam_sugests s where s.indname=? and s.resulttype='↓' ";
                            List<Map> list_num = db.find(sql, new Object[]{indname});
                            if (list_num.size() > 0) {
                                String uuid = UuidUtil.getUuid();
                                sugests_insert.add(new Object[]{uuid, hosnum, nodecode, comid, indid, list_num.get(0).get("classname"), sn, list_num.get(0).get("sugestid"), "↓", pexamid});
                            }
                        }
                    }
                    if ("str".equals(dtype) || dtype == "str") {
                        sql = "select * from pexam_ind_result t where t.hosnum=? and t.indid=? and t.sugestid is not null";
                        List<Map> list = db.find(sql, new Object[]{hosnum, indid});
                        for (int j = 0; j < list.size(); j++) {
                            if (result2.indexOf(list.get(j).get("result").toString()) > -1) {
                                String uuid = UuidUtil.getUuid();
                                sugests_insert.add(new Object[]{uuid, hosnum, nodecode, comid, indid, list.get(j).get("result"), sn, list.get(j).get("sugestid"), list.get(j).get("unnormal"), pexamid});
                            }
                        }
                    }
                    /**
                     * 在内科显示收缩压，舒张压的保存
                     */
                    if (!indid.equals("000000000000130") && !indid.equals("000000000000131")) {
                        if ("add".equals(operationType)) {
                            pi_insert.add(new Object[]{hosnum, itemuuid, examid, pexamid, "", "", doctorid, doctorname, new Timestamp(new Date().getTime()), comid, comname, indid, indname, result2, isnormal, sn, resultunit});
                        } else {
                            pi.add(new Object[]{result2, doctorid, doctorname, new Timestamp(new Date().getTime()), isnormal, sn, resultunit, hosnum, pexamid, indid, comid, itemuuid});
                        }
                    } else {
//						System.out.println("进了舒张压===============收缩压");
                        sql = "select a.comid,a.comname　from pexam_items_com a ,pexam_items_type b where a.hosnum = ? and a.parentid = b.typeid and  b.typeid = '00001'";
                        Map comidmap = (Map) db.findOne(sql, new Object[]{hosnum});
//						System.out.println("comid=============="+(String)comidmap.get("comid"));
                        boolean flag = comid.equals((String) comidmap.get("comid") == null ? "" : (String) comidmap.get("comid"));
//						System.out.println("flag=============="+flag);
                        sql = "select * from pexam_results c where c.hosnum = ? and c.pexamid = ? and c.indid =?";
                        Map li = (Map) db.findOne(sql, new Object[]{hosnum, pexamid, indid});
                        String cmd = (String) comidmap.get("comid") == null ? "" : (String) comidmap.get("comid");
                        String cmdname = (String) comidmap.get("comname") == null ? "" : (String) comidmap.get("comname");
//						System.out.println("li==============="+li);
                        if (li == null || "".equals(li)) {
                            pi_insert.add(new Object[]{hosnum, itemuuid, examid, pexamid, "", "", doctorid, doctorname, new Timestamp(new Date().getTime()), cmd, cmdname, indid, indname, result2, isnormal, sn, resultunit});
                        } else {
                            String result = (String) li.get("result") == null ? "" : (String) li.get("result");
                            String iid = (String) li.get("itemuuid") == null ? "" : (String) li.get("itemuuid");
//							System.out.println("result==============="+result);
//							System.out.println("result2==============="+result2);
//							System.out.println("iid==============="+iid);
                            if (!result.equals(result2)) {
                                pi.add(new Object[]{result2, doctorid, doctorname, new Timestamp(new Date().getTime()), isnormal, sn, resultunit, hosnum, pexamid, indid, cmd, iid});
                            }
                        }
                    }
                }

                if (sugests_insert != null && sugests_insert.size() > 0) {
                    Object[][] params_sugests = new Object[sugests_insert.size()][2];
                    for (int m = 0; m < sugests_insert.size(); m++) {
                        params_sugests[m] = sugests_insert.get(m);
                    }
                    sql = "insert into pexam_items_sugests(pisid,hosnum,nodecode,typeid,indid,result,sn,sugestid,unnormal,pexamid) values(?,?,?,?,?,?,?,?,?,?)";
                    db.excuteBatch(sql, params_sugests);
                }
                if (pi != null && pi.size() > 0) {
                    Object[][] params = new Object[pi.size()][2];
                    for (int i = 0; i < pi.size(); i++) {
                        params[i] = pi.get(i);
                    }
                    //	System.out.println("更新体检项目中的个指标");
                    sql = "update pexam_results a set a.result=?,a.excdoctor=?,a.excdoctorname=?,a.excdate=?,a.unnormal=?,a.sn=?,a.resultunit=? where a.hosnum=? and a.pexamid=? and a.indid=? and a.comid=? and a.itemuuid=?";
                    db.excuteBatch(sql, params);
                }

                if (pi_insert != null && pi_insert.size() > 0) {
                    Object[][] params_insert = new Object[pi_insert.size()][2];
                    for (int i = 0; i < pi_insert.size(); i++) {
                        params_insert[i] = pi_insert.get(i);
                    }
                    sql = "insert into pexam_results(hosnum,itemuuid,examid,pexamid,excdept,excdeptname,excdoctor,excdoctorname,excdate,comid,comname,indid,indname,result,unnormal,sn,resultunit)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                    db.excuteBatch(sql, params_insert);
                }
            }

            List<Map> list = null;
            if ("Y".equals(isDishDept)) {//区分科室
                sql = "select count(a.itemuuid) as count from pexam_items_title a where a.hosnum=? and a.pexamid=? and a.excdept=? and a.excdate is not null";
                list = db.find(sql, new Object[]{hosnum, pexamid, deptCode});
            } else {
                sql = "select count(a.itemuuid) as count from pexam_items_title a where a.hosnum=? and a.pexamid=? and a.excdate is not null";
                list = db.find(sql, new Object[]{hosnum, pexamid});
            }
            if (list != null && list.size() > 0) {
                pw.print(list.get(0).get("count"));
            } else {
                pw.print(0);
            }
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

    //TODO
    /************************体检医生站页面，所有相关操作(2012-12-18)结束***********************/


    /************************总检医生站页面，所有相关操作(2012-12-18)开始***********************/
    //TODO
	/*
	 * 更新(OR 插入)总检报告的结果
	 */
    @RequestMapping(value = "/modifyMainDoctorCheck", method = RequestMethod.POST)
    public void modifyMainDoctorCheck(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String deptCode = basDept.getDeptcode();
        String doctorid = request.getParameter("doctorId");
        String doctorname = request.getParameter("doctorname");
        String pexamid = request.getParameter("pexamid");
        String examid = request.getParameter("examid");
        String deptsum = request.getParameter("deptsum");//体检总结
        deptsum = deptsum.replaceAll("\\r", "<br/>");
        deptsum = deptsum.replaceAll("\\n", "&nbsp;");
        Date bDate = new Date();
        DBOperator db = null;
        PrintWriter pw = null;
        String login_userid = basUser.getId();
        String login_username = basUser.getName();
        try {
            pw = response.getWriter();
            db = new DBOperator();
            //	System.out.println("查看是否存在“体检总结”或“健康建议”");
            String sql = "select * from pexam_deptsum a where a.hosnum=? and a.pexamid=? and a.sumtype in ('体检总结')";
            String sql2 = "delete from PEXAM_DISEASECOUNTS a where a.hosnum=? and a.pexamid=? and a.nodecode=?";
            List<Map> list = db.find(sql, new Object[]{hosnum, pexamid});
            db.excute(sql2, new Object[]{hosnum, pexamid, nodecode});
//			System.out.println("=======list2.size()======="+list2.size());
            if (list != null && list.size() > 0) {//已经对该体检结果进行总结
                String save_userid = list.get(0).get("doctorid") == null ? "" : list.get(0).get("doctorid").toString();
                if (!"".equals(save_userid)) {
                    if (!login_userid.equals(save_userid)) {
                        throw new Exception("此人体检总检已经保存过，禁止他人修改");
                    }
                }

                List<Object[]> pi = new ArrayList<Object[]>();
                pi.add(new Object[]{doctorname, doctorid, bDate, deptsum, deptCode, "", "", hosnum, pexamid, "体检总结"});
                Object[][] params = new Object[pi.size()][2];
                for (int i = 0; i < pi.size(); i++) {
                    params[i] = pi.get(i);
                }
                sql = "update pexam_deptsum a set a.doctorname=?,a.doctorid=?,a.examdate=?,a.deptsum=?,a.excdept=?,a.DISPRESSION=?,a.DEFORMITY=? where a.hosnum=? and a.pexamid=? and a.sumtype=?";
                db.excuteBatch(sql, params);
            } else {//未对体检结果进行总结及建议
                //-----更新人员表 已完成状态--------
//				String sql_man="update pexam_mans m set m.isover='完成' where m.hosnum=? and m.pexamid=? ";
//				db.excute(sql_man, new Object[]{hosnum,pexamid});

                List<Object[]> pi = new ArrayList<Object[]>();
                pi.add(new Object[]{hosnum, examid, deptCode, login_userid, bDate, pexamid, deptsum, "体检总结", login_username, "", ""});
                Object[][] params = new Object[pi.size()][2];
                for (int i = 0; i < pi.size(); i++) {
                    params[i] = pi.get(i);
                }
                sql = "insert into pexam_deptsum (hosnum,examid,excdept,doctorid,examdate,pexamid,deptsum,sumtype,doctorname,DISPRESSION,DEFORMITY)values(?,?,?,?,?,?,?,?,?,?,?)";
                db.excuteBatch(sql, params);
            }

//			sql = "update  pexam_deptsum a set a.completedate=to_date(?,'yyyy-mm-dd hh24:mi:ss') where a.pexamid=?";
//			int num = db.excute(sql,new Object[]{StrUtil.dateToStr(new Date(),"yyyy-MM-dd HH:mm:ss"), pexamid});
//			if(num==0){
//				throw  new Exception("将体检病人更新为完成状态失败");
//			}

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

    @RequestMapping(value = "/modifyMainDoctorCheck1", method = RequestMethod.POST)
    public void modifyMainDoctorCheck1(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String deptCode = basDept.getDeptcode();
        String doctorid = request.getParameter("doctorId");
        String doctorname = request.getParameter("doctorname");

        String pexamid = request.getParameter("pexamid");
        String examid = request.getParameter("examid");

        String suggestion = request.getParameter("suggestion");//健康建议
        String radio = request.getParameter("radio");//体检印象
        String textValues = request.getParameter("textValues");
        String idNum = request.getParameter("idNum");
        String cjValue = request.getParameter("cjValue");
        Date bDate = new Date();

        DBOperator db = null;
        PrintWriter pw = null;
        String login_userid = basUser.getId();
        String login_username = basUser.getName();
        try {
            pw = response.getWriter();
            db = new DBOperator();
            //	System.out.println("查看是否存在“体检总结”或“健康建议”");
            String sql = "select * from pexam_deptsum a where a.hosnum=? and a.pexamid=? and a.sumtype in ('健康建议')";
            String sql2 = "delete from PEXAM_DISEASECOUNTS a where a.hosnum=? and a.pexamid=? and a.nodecode=?";
            List<Map> list = db.find(sql, new Object[]{hosnum, pexamid});
            db.excute(sql2, new Object[]{hosnum, pexamid, nodecode});
//			System.out.println("=======list2.size()======="+list2.size());
            if (list != null && list.size() > 0) {//已经对该体检结果进行总结
                String save_userid = list.get(0).get("doctorid") == null ? "" : list.get(0).get("doctorid").toString();
                if (!"".equals(save_userid)) {
                    if (!login_userid.equals(save_userid)) {
                        throw new Exception("此人健康建议已经保存过，禁止他人修改");
                    }
                }

                List<Object[]> pi = new ArrayList<Object[]>();
                pi.add(new Object[]{doctorname, doctorid, bDate, suggestion, deptCode, radio, cjValue, hosnum, pexamid, "健康建议"});
                Object[][] params = new Object[pi.size()][2];
                for (int i = 0; i < pi.size(); i++) {
                    params[i] = pi.get(i);
                }
                sql = "update pexam_deptsum a set a.doctorname=?,a.doctorid=?,a.examdate=?,a.deptsum=?,a.excdept=?,a.DISPRESSION=?,a.DEFORMITY=? where a.hosnum=? and a.pexamid=? and a.sumtype=?";
                db.excuteBatch(sql, params);
            } else {//未对体检结果进行总结及建议
                //-----更新人员表 已完成状态--------
//				String sql_man="update pexam_mans m set m.isover='完成' where m.hosnum=? and m.pexamid=? ";
//				db.excute(sql_man, new Object[]{hosnum,pexamid});

                List<Object[]> pi = new ArrayList<Object[]>();
                pi.add(new Object[]{hosnum, examid, deptCode, login_userid, bDate, pexamid, suggestion, "健康建议", login_username, radio, cjValue});
                Object[][] params = new Object[pi.size()][2];
                for (int i = 0; i < pi.size(); i++) {
                    params[i] = pi.get(i);
                }
                sql = "insert into pexam_deptsum (hosnum,examid,excdept,doctorid,examdate,pexamid,deptsum,sumtype,doctorname,DISPRESSION,DEFORMITY)values(?,?,?,?,?,?,?,?,?,?,?)";
                db.excuteBatch(sql, params);
            }

//			sql = "update  pexam_deptsum a set a.completedate=to_date(?,'yyyy-mm-dd hh24:mi:ss') where a.pexamid=?";
//			int num = db.excute(sql,new Object[]{StrUtil.dateToStr(new Date(),"yyyy-MM-dd HH:mm:ss"), pexamid});
//			if(num==0){
//				throw  new Exception("将体检病人更新为完成状态失败");
//			}
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

    /**
     * 总检医师 点击完成按钮，讲这个体检完成，完成之后 体检医生不能再保存信息
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/completePexam", method = RequestMethod.POST)
    public void completePexam(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String deptCode = basDept.getDeptcode();
        String pexamid = request.getParameter("pexamid");
        String examid = request.getParameter("examid");
        Date bDate = new Date();
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            //-----更新人员表 已完成状态--------
            String sql_man = "update pexam_mans m set m.isover='完成' where m.hosnum=? and m.pexamid=? ";
            db.excute(sql_man, new Object[]{hosnum, pexamid});

            //如果pexam_deptsum 表没记录的话（意味着医生没保存 ）  去更新 会报错。
            String sql = "";
            sql = "";

            sql = "update  pexam_deptsum a set a.completedate=to_date(?,'yyyy-mm-dd hh24:mi:ss'),a.doctorname='" + basUser.getName() + "'  where a.pexamid=?";
            int num = db.excute(sql, new Object[]{StrUtil.dateToStr(new Date(), "yyyy-MM-dd HH:mm:ss"), pexamid});
            if (num != 0) {
                pw.print("总检单完成成功！");
            } else {
                pw.print("fail：更新失败，请先保存再完成");
            }
            db.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            db.rollback();
            pw.print("fail" + ex.getMessage());
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    /**
     * 初始化页面的时候吧数据   体检总结  健康建议   温馨提示    传到前台   --lsp   2016-5-3 15:01:11
     *
     * @param request
     * @param response
     * @param modelMap
     * @return
     * @throws Exception
     */
    @RequestMapping("/zjbg1")
    public ModelAndView zjbg1(HttpServletRequest request,
                              HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        modelMap.put("doctorName", basUser.getName());// 体检医生名字
        modelMap.put("doctorId", basUser.getId());// 体检医生id
        String isDishDept = "";// 是否区分科室参数

        String examid = request.getParameter("examid");
        String pexamid = request.getParameter("pexamid");
        DBOperator db = null;
        db = new DBOperator();
        List<Map> list = null;
        String sql = "select a.* from bas_dicts a where a.nekey = '1900' and hosnum = '0000'  and a.nevalue !='!'";
        list = db.find(sql);
        //db.commit();
        //db.freeCon();

        JSONArray jsonArr = JSONArray.fromObject(list);
        // 获取是否区分科室
        isDishDept = ParamBuffer.getParamValue(request, basHospitals.getHosnum(), basHospitals.getNodecode(), "健康体检", "是否区分科室");
        modelMap.put("isDishDept", isDishDept);
        modelMap.put("examid", examid);
        modelMap.put("pexamid", pexamid);
        modelMap.put("disease", "var disease=" + jsonArr.toString());

        //------获取某人总检信息---------
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();//用来保存要传回前台的数据
        Map<String, Object> map = new HashMap<String, Object>();//用来保存要传回前台的数据

        //温馨提示
        sql = "select * from pexam_mans_wxts a  where a.pexamid=? ";
        list = db.find(sql, new Object[]{pexamid});
        String wxts_re = "";
        if (list != null && list.size() > 0) {
            java.sql.Clob c = (java.sql.Clob) list.get(0).get("wxts_1");
            wxts_re = StrUtil.oracleClob2Str(c);
        }
        map.put("wxts_re", wxts_re);

        //健康建议
        sql = "select * from pexam_deptsum a where a.pexamid=? and a.sumtype='健康建议' ";
        list = db.find(sql, new Object[]{pexamid});
        String jkjy_re = "";
        if (list != null && list.size() > 0) {
            java.sql.Clob c = (java.sql.Clob) list.get(0).get("deptsum");
            jkjy_re = StrUtil.oracleClob2Str(c);
        } else {
            //不存在健康建议  就去表里查数据 在生成一个字符串
            sql = " select * from ( select result,sugesttext,t.sn from pexam_items_sugests s,pexam_sugests t where s.sugestid=t.sugestid and s.pexamid=? and t.sugesttext is not null " +
                    " union  " +
                    " select s.classname,s.sugesttext,s.sn from pexam_results r , pexam_sugests s  where r.pexamid=? and r.examtype='检验' and r.unnormal is not null and  r.indid=s.indid and r.unnormal=s.unnormal" +
                    "  ) order by sn ";
            list = db.find(sql, new Object[]{pexamid, pexamid});
            for (int i = 0; i < list.size(); i++) {
                String classname = list.get(i).get("result") == null ? "" : list.get(i).get("result").toString();
                String sugesttext = list.get(i).get("sugesttext") == null ? "" : list.get(i).get("sugesttext").toString();
                sugesttext = "<span style=\"font-size:16px;\">" + sugesttext + "</span>";
                if (i == 0) {
                    jkjy_re += "<span style=\"font-size:16px;\">" + classname + "</span>" + "<br/>" + sugesttext;
                } else {
                    jkjy_re += "<br/><br/>" + "<span style=\"font-size:16px;\">" + classname + "</span>" + "<br/>" + sugesttext;
                }
            }
        }
        map.put("jkjy_re", jkjy_re);

        //体检总结
        sql = "select * from pexam_deptsum a where a.pexamid=? and a.sumtype='体检总结' ";
        list = db.find(sql, new Object[]{pexamid});
        String tjzj_re = "";
        if (list != null && list.size() > 0) {
            java.sql.Clob c = (java.sql.Clob) list.get(0).get("deptsum");
            tjzj_re = StrUtil.oracleClob2Str(c);
        } else {
            //不存在体检总结  就去表里查数据 在生成一个字符串
            sql = "select a.itemname as itemname,a.deptsum,a.comclass,a.excdate from pexam_items_title a left join pexam_items_com c on a.itemcode = c.comid and c.hosnum = a.hosnum left join pexam_items_type t  on c.parentid = t.typeid   and t.hosnum = c.hosnum where a.tjxm = 'Y'  and a.hosnum = ?  and a.pexamid = ?  and a.parent_comid is null  and a.deptsum is not null order by to_number(t.sn), to_number(a.sn)";
            list = db.find(sql, new Object[]{hosnum, pexamid});
            for (Map<String, Object> map2 : list) {
                String itemname = map2.get("itemname") == null ? "" : map2.get("itemname").toString();
                String deptsum = map2.get("deptsum") == null ? "" : map2.get("deptsum").toString();
                if (Equ(deptsum, '\r')) { //如果最后一个字符是\r  就去掉
                    deptsum = deptsum.substring(0, deptsum.length() - 1);
                }
                deptsum = deptsum.replaceAll("\\r", "<br/>&nbsp;&nbsp;");
                tjzj_re += "<span class='deptsumTitle'>" + itemname + "</span>" + "：<br/>" + "&nbsp;&nbsp;" + deptsum + "<br/><br/>";
            }
        }
        map.put("tjzj_re", tjzj_re);


        db.freeCon();
        JSONObject jsonObj = JSONObject.fromObject(map);
        modelMap.put("deptsumJson", jsonObj.toString());

        return new ModelAndView("pexam/zjbg1", modelMap);
    }
//	@RequestMapping("/zjbg1")
//	public ModelAndView zjbg1(HttpServletRequest request,HttpServletResponse response,ModelMap modelMap)throws Exception{
//		response.setContentType("text/html;charset=utf-8");
//		Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
//		Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
//		modelMap.put("doctorName",basUser.getName());//体检医生名字
//		modelMap.put("doctorId", basUser.getId());//体检医生id
//		String isDishDept = "";//是否区分科室参数
//
//		String examid = request.getParameter("examid");
//		String pexamid = request.getParameter("pexamid");
//		DBOperator db = null;
//		db = new DBOperator();
//		List<Map> list = null;
//		String sql = "select a.* from bas_dicts a where a.nekey = '1900' and hosnum = '0000'  and a.nevalue !='!'";
//		list = db.find(sql);
//		db.commit();
//		db.freeCon();
////		String[] nevalues = new String[list.size()];
////		for(int i = 0; i<list.size();i++){
////			nevalues[i] = (String) list.get(i);
////		}\
//		//JSONObject jsonObj = JSONObject.fromObject(list);
//		JSONArray jsonArr = JSONArray.fromObject(list);
//		//获取是否区分科室
//		isDishDept = ParamBuffer.getParamValue(request,basHospitals.getHosnum(),basHospitals.getNodecode(),"健康体检", "是否区分科室");
////		System.out.println("doctorName======================"+basUser.getName());
////		System.out.println("doctorId======================"+basUser.getId());
////		System.out.println("isDishDept======================"+isDishDept);
//		modelMap.put("isDishDept",isDishDept);
//		modelMap.put("examid",examid);
//		modelMap.put("pexamid",pexamid);
//		modelMap.put("disease","var disease="+jsonArr.toString());
//		return new ModelAndView("pexam/zjbg1",modelMap);
//	}

    /*
	 * 体检报告页面
	 */
    @RequestMapping("/zjbg")
    public ModelAndView zjbg(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        modelMap.put("doctorName", basUser.getName());//体检医生名字
        modelMap.put("doctorId", basUser.getId());//体检医生id
        String isDishDept = "";//是否区分科室参数

        String examid = request.getParameter("examid");
        String pexamid = request.getParameter("pexamid");
        DBOperator db = null;
        db = new DBOperator();
        List<Map> list = null;
        String sql = "select a.* from bas_dicts a where a.nekey = '1900' and hosnum = '0000'  and a.nevalue !='!'";
        list = db.find(sql);
        db.commit();
        db.freeCon();
//		String[] nevalues = new String[list.size()];
//		for(int i = 0; i<list.size();i++){
//			nevalues[i] = (String) list.get(i);
//		}\
        //JSONObject jsonObj = JSONObject.fromObject(list);
        JSONArray jsonArr = JSONArray.fromObject(list);
        //获取是否区分科室
        isDishDept = ParamBuffer.getParamValue(request, basHospitals.getHosnum(), basHospitals.getNodecode(), "健康体检", "是否区分科室");
//		System.out.println("doctorName======================"+basUser.getName());
//		System.out.println("doctorId======================"+basUser.getId());
//		System.out.println("isDishDept======================"+isDishDept);
        modelMap.put("isDishDept", isDishDept);
        modelMap.put("examid", examid);
        modelMap.put("pexamid", pexamid);
        modelMap.put("disease", "var disease=" + jsonArr.toString());
        return new ModelAndView("pexam/zjbg", modelMap);
    }

    /*
	 * 体检报告页面   健康建议
	 */
    @RequestMapping("/zjbg_jkjy")
    public ModelAndView zjbg_jkjy(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        modelMap.put("doctorName", basUser.getName());//体检医生名字
        modelMap.put("doctorId", basUser.getId());//体检医生id
        String isDishDept = "";//是否区分科室参数

        String examid = request.getParameter("examid");
        String pexamid = request.getParameter("pexamid");
        DBOperator db = null;
        db = new DBOperator();
        List<Map> list = null;
        String sql = "select a.* from bas_dicts a where a.nekey = '1900' and hosnum = '0000'  and a.nevalue !='!'";
        list = db.find(sql);
        db.commit();
        db.freeCon();
//		String[] nevalues = new String[list.size()];
//		for(int i = 0; i<list.size();i++){
//			nevalues[i] = (String) list.get(i);
//		}\
        //JSONObject jsonObj = JSONObject.fromObject(list);
        JSONArray jsonArr = JSONArray.fromObject(list);
        //获取是否区分科室
        isDishDept = ParamBuffer.getParamValue(request, basHospitals.getHosnum(), basHospitals.getNodecode(), "健康体检", "是否区分科室");
//		System.out.println("doctorName======================"+basUser.getName());
//		System.out.println("doctorId======================"+basUser.getId());
//		System.out.println("isDishDept======================"+isDishDept);
        modelMap.put("isDishDept", isDishDept);
        modelMap.put("examid", examid);
        modelMap.put("pexamid", pexamid);
        modelMap.put("disease", "var disease=" + jsonArr.toString());
        return new ModelAndView("pexam/zjbg_jkjy", modelMap);
    }

    /*
	 * 温馨提示 页面
	 */
    @RequestMapping("/wxts")
    public ModelAndView wxts(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        modelMap.put("doctorName", basUser.getName());//体检医生名字
        modelMap.put("doctorId", basUser.getId());//体检医生id
        String isDishDept = "";//是否区分科室参数
        String examid = request.getParameter("examid");
        String pexamid = request.getParameter("pexamid");
        String wxts = "";
        String patname = "";
        String sex = "";
        List<Map> vmList = new ArrayList<Map>();
        StringBuffer sb = new StringBuffer();
        Map map = new HashMap();
        map.put("hosname", basHospitals.getHosname());
        map.put("username", basUser.getName());
        DBOperator db = null;
        db = new DBOperator();
        List<Map> list = null;
        String sql = "select  m.pexamid,a.wxts_1 ,m.patname,m.sex   from  pexam_mans m  left join  Pexam_Mans_wxts a on m.pexamid=a.pexamid where m.pexamid = ?  and m.hosnum= ?   ";
        list = db.find(sql, new Object[]{pexamid, basHospitals.getHosnum()});
        if (ListUtil.listIsNotEmpty(list)) {
            patname = list.get(0).get("patname") == null ? "" : list.get(0).get("patname").toString();
            sex = list.get(0).get("sex") == null ? "" : list.get(0).get("sex").toString();
            java.sql.Clob c = (java.sql.Clob) list.get(0).get("wxts_1");
            wxts = StrUtil.oracleClob2Str(c);
        } else {
            patname = "XXX";
        }
        if ("男".equals(sex)) {
            patname += "先生";
        } else {
            patname += "女士";
        }
        map.put("patname", patname);
        String time = DateConvertor.getChDate(StrUtil.dateToStr(new Date()));
        map.put("time", time);
        vmList.add(map);
        String vmpath = PexamAction_ExpRort.getExpPath("exp");
        String vmname = "tj_wxts.vm";
        String vm = PexamAction_ExpRort.generateVm(vmpath, vmname, "lists", vmList);

        if (!"".equals(wxts) && wxts != null && !"null".equals(wxts)) {
            modelMap.put("nowWxts", wxts);
        } else {
            modelMap.put("nowWxts", vm);
        }
        db.freeCon();
        return new ModelAndView("pexam/zj_wxts", modelMap);
    }

    /*
	 * 保存 总检医生写的 温馨提示
	 */
    @RequestMapping(value = "/SaveWxts", method = RequestMethod.POST)
    public void SaveWxts(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String hosnum = basHospitals.getHosnum();
        String deptCode = basDept.getDeptcode();

        String examid = URLDecoder.decode(request.getParameter("examid"), "utf-8");
        String pexamid = URLDecoder.decode(request.getParameter("pexamid"), "utf-8");

        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String s = request.getParameter("wxts_1");
            String wxts_1 = s;//URLDecoder.decode(s, "UTF-8");
            //String sql = "update pexam_mans a set a.wxts_1=?,a.wxts_2=?,a.wxts_3=? where a.hosnum=? and a.pexamid=?  ";
            String sql = "select * from pexam_mans a where a.hosnum=? and a.PEXAMID=?";
            List<Map> list = db.find(sql, new Object[]{hosnum, pexamid});
            String patname = "", sex = "";
            if (ListUtil.listIsNotEmpty(list)) {
                patname = list.get(0).get("patname") == null ? "" : list.get(0).get("patname").toString();
                sex = list.get(0).get("sex") == null ? "" : list.get(0).get("sex").toString();
            } else {
                patname = "";
                sex = "";
            }
            list = db.find("select* from pexam_mans_wxts a where a.pexamid='" + pexamid + "' ");
            if (ListUtil.listIsNotEmpty(list)) {
                String doctorid = list.get(0).get("doctorid") == null ? "" : list.get(0).get("doctorid").toString();
                String doctorname = list.get(0).get("doctorname") == null ? "" : list.get(0).get("doctorname").toString();
                if (!"".equals(doctorid) && !doctorid.equals(basUser.getId())) {
                    throw new Exception("温馨提示已被修改，不能再次修改！");
                } else {
                    db.excute("update pexam_mans_wxts a set a.wxts_1=?  ,a.doctorid=? ,a.doctorname=?  where a.pexamid=? ", new Object[]{wxts_1, basUser.getId(), basUser.getName(), pexamid});
                }
            } else {
                db.excute("insert into PEXAM_MANS_WXTS (WXTS_1, HOSNUM, PEXAMID ,patname,sex,doctorid,doctorname) values (?, ?,?,?,?,?,?)", new Object[]{wxts_1, hosnum, pexamid, patname, sex, basUser.getId(), basUser.getName()});
            }

            db.commit();
            pw.print("保存成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            db.rollback();
            pw.print("fail" + ex.getMessage());
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    public static String unescape(String src) {
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length());
        int lastPos = 0, pos = 0;
        char ch;
        while (lastPos < src.length()) {
            pos = src.indexOf("%", lastPos);
            if (pos == lastPos) {
                if (src.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else {
                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                } else {
                    tmp.append(src.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }

    /*
	 * 返回  总检医生写的 温馨提示
	 */
    @RequestMapping(value = "/GETWxts", method = RequestMethod.POST)
    public void GETWxts(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String hosnum = basHospitals.getHosnum();
        String deptCode = basDept.getDeptcode();

        String examid = request.getParameter("examid");
        String pexamid = request.getParameter("pexamid");

        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            Map map = new HashMap();
            String sql = "select * from PEXAM_MANS_WXTS a  where a.hosnum=? and a.pexamid=?  ";
            List<Map> list = db.find(sql, new Object[]{hosnum, pexamid});
            if (ListUtil.listIsNotEmpty(list)) {
                java.sql.Clob c = (java.sql.Clob) list.get(0).get("wxts_1");
                map.put("wxts_1", StrUtil.oracleClob2Str(c));
            } else {
                map.put("wxts_1", "");
            }

            sql = "select * from pexam_deptsum  a  where a.hosnum=? and a.pexamid=? and a.sumtype='健康建议'  ";
            list = db.find(sql, new Object[]{hosnum, pexamid});
            if (ListUtil.listIsNotEmpty(list)) {
                java.sql.Clob c = (java.sql.Clob) list.get(0).get("deptsum");
                String deptsum_jkjy = "";
                deptsum_jkjy = StrUtil.oracleClob2Str(c) == null ? "" : StrUtil.oracleClob2Str(c);
                //查看这个人 有木有完成 ，有的话 结论建议增加  总检医生
                sql = "select to_char(a.completedate,'yyyy-mm-dd hh24:mi:ss') completedate,a.doctorname from pexam_deptsum a where a.pexamid=? ";
                list = db.find(sql, new Object[]{pexamid});
                String completedateStr = "";
                String completeDoctorname = "";
                if (ListUtil.listIsNotEmpty(list)) {
                    completedateStr = list.get(0).get("completedate") == null ? "" : list.get(0).get("completedate").toString();
                    completeDoctorname = list.get(0).get("doctorname") == null ? "" : list.get(0).get("doctorname").toString();
                    if (!"".equals(completedateStr) && !"".equals(completeDoctorname)) {
                        deptsum_jkjy = deptsum_jkjy + "<br/><br/><br/><span style=\"margin-left: 450px;\">总检医生：" + completeDoctorname + "&nbsp;&nbsp;&nbsp;&nbsp;</span><br><span style=\"margin-left: 450px;\">总检时间：" + completedateStr + "</span>";
                    }
                }
                map.put("deptsum_jkjy", deptsum_jkjy);
            } else {
                map.put("deptsum_jkjy", "");
            }

            pw.print(JSONObject.fromObject(map));
        } catch (Exception ex) {
            ex.printStackTrace();
            db.rollback();
            pw.print("fail" + ex.getMessage());
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    /*
	 * 护士点击 总检报告打印 插入打印的时间
	 *  多次点击打印 不更新 打印时间
	 *  lsp  2016-5-16 15:02:15
	 *
	 */
    @RequestMapping(value = "/insertPrintDate", method = RequestMethod.POST)
    public void insertPrintDate(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String hosnum = basHospitals.getHosnum();
        String deptCode = basDept.getDeptcode();

        String examid = request.getParameter("examid");
        String pexamid = request.getParameter("pexamid");

        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            Map map = new HashMap();
            String sql = "";
            List<Map> list = db.find("select a.printtime from PEXAM_MANS a where a.hosnum=? and a.pexamid=? ", new Object[]{hosnum, pexamid});
            //查看字段 printtime是否有值
            map = list.get(0);
            String printtime = map.get("printtime") == null ? "" : map.get("printtime").toString();
            if (!"".equals(printtime)) {
                pw.print("打印成功");
            } else {
                sql = "update PEXAM_MANS a set a.printtime = to_date(?,'yyyy-mm-dd')  where a.hosnum=? and a.pexamid=? ";
                int a = db.excute(sql, new Object[]{StrUtil.dateToStr(new Date()), hosnum, pexamid});
                if (a == 0) {
                    pw.print("fail:打印时间更新失败");
                } else {
                    pw.print("打印成功");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            db.rollback();
            pw.print("fail" + ex.getMessage());
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    /*
	 * 体检报告页面
	 */
    @RequestMapping("/{reportType}")
    public ModelAndView reportPage(@PathVariable("reportType") String reportType, HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        String examid = request.getParameter("examid");
        String pexamid = request.getParameter("pexamid");
        modelMap.put("examid", examid);
        modelMap.put("pexamid", pexamid);
        return new ModelAndView("pexam/" + reportType, modelMap);
    }

    /*
	 * 显示异常指标项，即体检总结
	 */
    @RequestMapping("/getUnnormalInfo")
    public void getUnnormalInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String deptCode = basDept.getDeptcode();
        String type = request.getParameter("type");//哪个页面调取的
        String pexamid = request.getParameter("pexamid");//体检编号
        String examid = request.getParameter("examid");//预约编号

        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();//用来保存要传回前台的数据
            Map<String, Object> map0 = new HashMap<String, Object>();//用来保存要传回前台的数据
            List<Map<String, Object>> list = null;

            //System.out.println("查看是否已经保存过了体检总结");
            String sql = "select * from pexam_deptsum a where a.hosnum=? and a.examid=? and a.pexamid=? and a.sumtype='体检总结'";
            list = db.find(sql, new Object[]{hosnum, examid, pexamid});

            if (list != null && list.size() > 0) {//总检医生站--已经对该体检人结果进行总结
                map0.put("isExistSum", "Y");//表示总检医生站已对该病人进行体检总结
                java.sql.Clob c = (Clob) list.get(0).get("deptsum");
                String deptsum = StrUtil.oracleClob2Str(c).replaceAll("\\r", "<br/>");
                deptsum = deptsum.replaceAll("\\n", "<br/>");
                list.get(0).put("deptsum", deptsum);
                map0.put("deptSum", list);


            } else {//总检医生站--还未对该体检人结果进行总结
                map0.put("isExistSum", "N");//表示总检医生站未对该病人进行体检总结
                sql = "select a.itemname as itemname,a.deptsum,a.comclass,a.excdate from pexam_items_title a left join pexam_items_com c on a.itemcode = c.comid and c.hosnum = a.hosnum left join pexam_items_type t  on c.parentid = t.typeid   and t.hosnum = c.hosnum where a.tjxm = 'Y'  and a.hosnum = ?  and a.pexamid = ?  and a.parent_comid is null  and a.deptsum is not null order by to_number(t.sn), to_number(a.sn)";
                list = db.find(sql, new Object[]{hosnum, pexamid});
                for (Map<String, Object> map : list) {  //循环list 把科室小结中有\r的 替换成 <br/>
                    String deptsum = map.get("deptsum") == null ? "" : map.get("deptsum").toString();
                    if (!"".equals(deptsum)) {//如果不为空
                        if (Equ(deptsum, '\r')) { //如果最后一个字符是\r  就去掉
                            deptsum = deptsum.substring(0, deptsum.length() - 1);
                        }
                        deptsum = deptsum.replaceAll("\\r", "<br/>&nbsp;&nbsp;");
                        map.put("deptsum", deptsum);
                    }
                    if ((map.get("itemname").toString()).indexOf("肝功") > -1) {
                        map.put("itemname", "生化检验");
                    }
                }
                map0.put("deptSum", list);


            }

            JSONObject jsonObj = JSONObject.fromObject(map0);
            pw.print(jsonObj.toString());
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    @RequestMapping("/getUnnormalInfo1")
    public void getUnnormalInfo1(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String deptCode = basDept.getDeptcode();
        String type = request.getParameter("type");//哪个页面调取的
        String pexamid = request.getParameter("pexamid");//体检编号
        String examid = request.getParameter("examid");//预约编号

        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();//用来保存要传回前台的数据
            Map<String, Object> map0 = new HashMap<String, Object>();//用来保存要传回前台的数据
            List<Map<String, Object>> list = null;
            String sql = "select * from pexam_deptsum a where a.hosnum=? and a.examid=? and a.pexamid=? and a.sumtype='健康建议'";
            list = db.find(sql, new Object[]{hosnum, examid, pexamid});

            if (list != null && list.size() > 0) {//总检医生站--已经对该体检人结果进行总结
                map0.put("isExistSum", "Y");//表示总检医生站已对该病人进行体检总结
                //System.out.println("查询健康建议");
                java.sql.Clob c = (Clob) list.get(0).get("deptsum");
                String deptsum = StrUtil.oracleClob2Str(c);
                deptsum.replaceAll("\\r", "<br/>");
                list.get(0).put("deptsum", deptsum);  //将内容 从Clob 转成String
                map0.put("suggest", list);

            } else {//总检医生站--还未对该体检人结果进行总结
                map0.put("isExistSum", "N");//表示总检医生站未对该病人进行体检总结

            }

            JSONObject jsonObj = JSONObject.fromObject(map0);
            pw.print(jsonObj.toString());
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
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

    //TODO

    /************************总检医生站页面，所有相关操作(2012-12-18)结束***********************/

	/*
	 * 开始体检,修改为根据套餐和加项启
	 * 2012-12-05
	 */
    @RequestMapping("/startExamNew")
    public void startExamNew(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String hosnum = basHospitals.getHosnum();

        String pexamid = request.getParameter("pexamid");//唯一标识--个人体检编号
        String examid = request.getParameter("examid");//如果是个人体检的则该参数为”0000“

        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            int itemsNum = 0;
            String sql = "";
            List list = null;
            //获取体检人的性别--过滤出有性别要求的体检项目
            sql = "select a.sex from pexam_mans a where a.hosnum=? and a.pexamid=?";
            list = db.find(sql, new Object[]{hosnum, pexamid});
            String sex = "";
            if (list != null && list.size() > 0) {
                sex = (String) ((Map) list.get(0)).get("sex");
            }

            //System.out.println("更新pexam_mans表的体检开始标志字段");//此处体检日期可能要从前台传递过来
            sql = "update pexam_mans a set a.bdate=? where a.hosnum=? and a.pexamid=?";
            db.excute(sql, new Object[]{new Date(), hosnum, pexamid});

            //	System.out.println("获取个人相关的体检项目或套餐");//如果是团体的话pexamid为空
            sql = "select * from pexam_items a where (a.hosnum=? and a.examid=? and a.pexamid is null) or (a.hosnum=? and a.examid=? and a.pexamid=?)";
            list = db.find(sql, new Object[]{hosnum, examid, hosnum, examid, pexamid});
            if (list != null && list.size() > 0) {
                String groupids = "";
                String itemcodes = "";
                String itemCodesAll = "";
                Map map0 = null;
                List<Object[]> pi = new ArrayList<Object[]>();
                for (int i = 0; i < list.size(); i++) {
                    map0 = (Map) list.get(i);
                    if ("y".equals(map0.get("isgroup"))) {
                        groupids += "'" + map0.get("itemid") + "',";
                    } else {
                        itemcodes += "," + map0.get("itemid") + "',";
                    }
                }

                //获取套餐下的体检项目--此处未对套餐中有相同体检项目进行去重操作
                if (groupids.length() > 0) {
                    groupids = groupids.substring(0, groupids.length() - 1);
                    //	System.out.println("获取组成套餐的体检项目");//有性别要求的只需在此处加个性别条件就ok
                    //sql = "select a.*,b.groupid from pexam_items_def a ,pexam_items_groupdetails b where a.hosnum=b.hosnum and a.itemcode=b.itemcode and b.groupid in (?)".replace("?", groupids);
                    sql = "select b.*,a.groupid,c.groupname from pexam_items_groupdetails a,pexam_items_com b,pexam_items_group c where a.groupid in (?) and a.itemcode=b.comid and c.groupid=a.groupid and a.hosnum=b.hosnum and a.hosnum=c.hosnum ".replace("?", groupids);
                    sql += " and a.hosnum=?";
                    if (!"".equals(sex)) {
                        sql += " and (b.forsex='不限' or b.forsex='" + sex + "')";
                    }
                    list = db.find(sql, new Object[]{hosnum});
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            map0 = (Map) list.get(i);
                            String itemcode = (String) map0.get("comid");//组合项目id
                            String itemname = (String) map0.get("comname");//组合项目名称
                            String groupid = (String) map0.get("groupid");//套餐id
                            String groupname = (String) map0.get("groupname");//套餐名称
                            itemCodesAll += "'" + itemcode + "',";
                            pi.add(new Object[]{hosnum, new UUIDGenerator().generate().toString(), examid, pexamid, itemcode, itemname, map0.get("excdept"), map0.get("excdeptname"), groupid, groupname});
                        }
                    }
                }

                //获取单项的项目明细
                if (itemcodes.length() > 0) {
                    itemcodes = itemcodes.substring(0, itemcodes.length() - 1);
                    //	System.out.println("获取体检项目的相关信息");
                    sql = "select * from pexam_items_com a where a.comid in (?)".replace("?", itemcodes);
                    sql += " and a.hosnum=?";
                    if (!"".equals(sex)) {
                        sql += " and (a.forsex='不限' or a.forsex='" + sex + "')";
                    }
                    list = db.find(sql, new Object[]{hosnum});
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            map0 = (Map) list.get(i);
                            String itemcode = (String) map0.get("comid");
                            String itemname = (String) map0.get("comname");
                            itemCodesAll += "'" + itemcode + "',";
                            pi.add(new Object[]{hosnum, new UUIDGenerator().generate().toString(), examid, pexamid, itemcode, itemname, map0.get("excdept"), map0.get("excdeptname"), "", ""});
                        }
                    }
                }
                Object[][] params = new Object[pi.size()][2];
                for (int i = 0; i < pi.size(); i++) {
                    params[i] = pi.get(i);
                }
                itemsNum = pi.size();
                //System.out.println("插入具体要体检的体检项目");
                sql = "insert into pexam_items_title (hosnum,itemuuid,examid,pexamid,itemcode,itemname,excdept,excdeptname,groupid,groupname)values(?,?,?,?,?,?,?,?,?,?)";
                db.excuteBatch(sql, params);

                if (itemCodesAll.length() > 0) {
                    itemCodesAll = itemCodesAll.substring(0, itemCodesAll.length() - 1);
                    //sql = "select  a.itemcode,a.itemname,a.excdept,a.itemclass,b.detailid,b.detailname from pexam_items_def a,pexam_items_details b where a.itemcode=b.parentid and a.hosnum=? and a.itemcode in (" + itemCodesAll +") and b.hosnum=a.hosnum";
                    sql = "select a.comid,a.comname,a.excdept,a.excdeptname,a.comclass,c.indid,c.indname from pexam_items_com a, pexam_items_comdet b,pexam_items_ind c where a.hosnum=b.hosnum and a.comid=b.comid and b.indid=c.indid and a.hosnum=c.hosnum and a.comid in (" + itemCodesAll + ") and a.hosnum=?";
                    //过滤出指标项有性别限制的指标
                    if (!"".equals(sex)) {
                        sql += " and (a.forsex='不限' or a.forsex='" + sex + "')";
                    }
                    list = db.find(sql, new Object[]{hosnum});
                    pi = new ArrayList<Object[]>();
                    for (int i = 0; i < list.size(); i++) {
                        map0 = (Map) list.get(i);
                        //pi.add(new Object[]{hosnum,examid,map0.get("indid"),"","",map0.get("excdept"),"",null,null,null,null,pexamid,map0.get("comname"),map0.get("comid"),map0.get("comclass"),map0.get("indname")});
                        pi.add(new Object[]{hosnum, examid, pexamid, map0.get("excdept"), map0.get("excdeptname"), "", "", null, map0.get("comid"), map0.get("comname"), map0.get("indid"), map0.get("indname")});
                    }
                    params = new Object[pi.size()][2];
                    for (int i = 0; i < pi.size(); i++) {
                        params[i] = pi.get(i);
                    }
                    sql = "insert into pexam_results(hosnum,examid,pexamid,excdept,excdeptname,excdoctor," +
                            "excdoctorname,excdate,comid,comname,indid," +
                            "indname)values(?,?,?,?,?,?,?,?,?,?,?,?)";
                    db.excuteBatch(sql, params);
                }
            }
            pw.print(itemsNum);
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
	 * 总检医生站--获取体检项目的指标项
	 * 2012-12-05
	 */
    @RequestMapping(value = "/createExamNew3", method = RequestMethod.POST)
    public void loadexamdetails3(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String itemcode = request.getParameter("itemcode");//项目id
        String pexamid = request.getParameter("pexamid");
        String itemuuid = request.getParameter("itemuuid");
        String comclass = request.getParameter("comclass");
        String examid = request.getParameter("examid");

        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "";
            String isExamStart = "已体检";
            Map tempMap = null;
            Map temp = new HashMap();//返回前台的数据
            List list = null;//体检结果
            String excdoctorname = "";//体检医生姓名
            String deptSum = "";//科室小结

            if ("检验".equals(comclass)) {
                //Object[] objects = ClientFactory.getClientInstance().invoke("getLisRInfoByProjectId",hosnum,itemuuid);
                //String xml = objects[0].toString();
                //String xml = lisServerImpl.getLisRInfoByProjectId(hosnum, itemuuid);
                String xml = "";
                Document doc = doc = DocumentHelper.parseText(xml);
                Element rootElt = doc.getRootElement(); // 获取根节点
                String code = rootElt.elementText("code");
                System.out.println("调用web serivce 返回的结果：" + rootElt.elementText("message"));
                if ("200".equals(code)) {//查询成功
                    Element projectElt = rootElt.element("project");
                    code = projectElt.elementText("code");
                    System.out.println("====>" + code);
                    if ("200".equals(code)) {
                        List<PexamResult> resultList = new ArrayList<PexamResult>();//保存返回前台的数据
                        List<Object[]> pi = new ArrayList<Object[]>();//保存要插入pexam_result2的数据
                        StringBuffer sb = new StringBuffer();//收集小结信息

                        itemuuid = projectElt.elementText("projectid");//体检医生编号
                        String examDoctorId = projectElt.elementText("testid");//体检医生编号
                        String examDoctorName = projectElt.elementText("testname");//体检医生姓名
                        String examDateStr = projectElt.elementText("tesdate");//体检时间
                        Date examDate = null;
                        Timestamp examtims = null;
                        if (!"".equals(examDateStr)) {
                            examDate = DateUtil.stringToDate(examDateStr, "yyyy-MM-dd HH:mm:ss");
                            examtims = new Timestamp(examDate.getTime());
                        } else {
                            examDate = new Date();
                            examtims = new Timestamp((new Date()).getTime());
                        }
                        String checkDoctorId = projectElt.elementText("auditid");//体检医生编号
                        String checkDoctorName = projectElt.elementText("auditname");//体检医生姓名
                        String checkDateStr = projectElt.elementText("auditdate");
                        Timestamp checktims = null;
                        if (!"".equals(checkDateStr)) {
                            checktims = new Timestamp((DateUtil.stringToDate(checkDateStr, "yyyy-MM-dd HH:mm:ss")).getTime());
                        } else {
                            checktims = new Timestamp((new Date()).getTime());
                        }
                        String comId = projectElt.elementText("comid");
                        String comName = projectElt.elementText("comname");
                        Iterator resultIter = projectElt.elementIterator("result");
                        PexamResult resultObj = null;
                        while (resultIter.hasNext()) {
                            Element resultElt = (Element) resultIter.next();
                            String indId = resultElt.elementText("itemid");
                            String indName = resultElt.elementText("itemname");
                            String result = resultElt.elementText("result");
                            String resultStatus = resultElt.elementText("resultstatus");
                            String unnormal = "异常";
                            if ("".equals(resultStatus)) {
                                unnormal = "正常";
                            }
                            String sn = resultElt.elementText("sn");
                            String resultunit = resultElt.elementText("unit");
                            String range = resultElt.elementText("range");
                            pi.add(new Object[]{hosnum, examid, pexamid, examDoctorId, examDoctorName, examtims, resultStatus, result,
                                    comId, comName, indId, indName, "检验", unnormal, itemuuid, sn, resultunit, range});
                            if ("异常".equals(unnormal)) {
                                sb.append(indName + ":" + result + resultunit);
                                sb.append(("".equals(resultStatus) ? "" : (" " + resultStatus)));
                                sb.append(("".equals(range) ? ";" : (" (参考值：" + range + ");")));
                            }

                            resultObj = new PexamResult();
                            resultObj.setComid(comId);
                            resultObj.setComname(comName);
                            resultObj.setExamid(examid);
                            resultObj.setExamtype("检验");
                            resultObj.setExcdate(examDate);
                            resultObj.setExcdoctorid(examDoctorId);
                            resultObj.setExcdoctorname(examDoctorName);
                            resultObj.setHosnum(hosnum);
                            resultObj.setIndid(indId);
                            resultObj.setIndname(indName);
                            resultObj.setItemuuid(itemuuid);
                            resultObj.setPexamid(pexamid);
                            resultObj.setRange(range);
                            resultObj.setResult(result);
                            resultObj.setResultunit(resultunit);
                            resultObj.setSn("".equals(sn) ? 0 : Long.parseLong(sn));
                            resultObj.setStringvalue(resultStatus);
                            resultObj.setUnnormal(unnormal);
                            resultList.add(resultObj);
                        }
                        Object[][] params = new Object[pi.size()][2];
                        for (int i = 0; i < pi.size(); i++) {
                            params[i] = pi.get(i);
                        }

                        sql = "delete from pexam_results a where a.hosnum=? and a.pexamid=? and a.itemuuid=?";
                        db.excute(sql, new Object[]{hosnum, pexamid, itemuuid});

                        sql = "insert into pexam_results(hosnum,examid,pexamid,excdoctor,excdoctorname,excdate,stringvalue," +
                                "result,comid,comname,indid,indname,examtype,unnormal,itemuuid,sn,resultunit,range)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                        db.excuteBatch(sql, params);

                        sql = " update pexam_items_title a set a.excdate=?,a.excdoctorid=?,a.excdoctorname=?,a.checkdate=?,a.checkdoctorid=?,a.checkdoctorname=?,a.deptsum=? where a.hosnum=? and a.itemuuid=? and a.pexamid=?";
                        db.excute(sql, new Object[]{examtims, examDoctorId, examDoctorName, checktims, checkDoctorId, checkDoctorName, sb.toString(), hosnum, itemuuid, pexamid});
//						db.commit();

                        //此处还要调用接口更新wed serivce中的数据--回置状态
                        //objects = ClientFactory.getClientInstance().invoke("updateLisInfoStatus",hosnum,pexamid,itemuuid);
                        //xml = objects[0].toString();
                        //xml = lisServerImpl.updateLisInfoStatus(hosnum,pexamid,itemuuid);
                        xml = "";
                        doc = DocumentHelper.parseText(xml);
                        rootElt = doc.getRootElement(); // 获取根节点
                        code = rootElt.elementText("code");
                        if (!"200".equals(code)) {//回置状态失败!
                            throw new Exception(rootElt.elementText("message"));
                        }

                        //返回前台
                        list = resultList;
                        excdoctorname = checkDoctorName;
                        deptSum = sb.toString();

                    } else if ("203".equals(code)) {//结果已被调取--去pexam_result2表里取

                        sql = "select * from pexam_results a where a.hosnum=? and a.pexamid=? and a.itemuuid=? and a.comid=?";
                        List<PexamResult> resultList = db.find(sql, new Object[]{hosnum, pexamid, itemuuid, itemcode}, PexamResult.class);

                        //获取小结和审核医生姓名
                        sql = "select a.checkdoctorname,a.excdate,a.deptsum,a.comclass from pexam_items_title a where a.hosnum=? and a.pexamid=? and a.itemuuid=? and a.itemcode=?";
                        tempMap = (Map) db.findOne(sql, new Object[]{hosnum, pexamid, itemuuid, itemcode});

                        //返回前台数据
                        list = resultList;
                        excdoctorname = (String) tempMap.get("checkdoctorname");//审核医生，如果是”其他“类型则是体检医生
                        deptSum = (String) tempMap.get("deptsum");
                        //System.out.println("=========>"+deptSum);
                    } else if ("201".equals(code)) {//检验未开始--提示未进行体检
                        isExamStart = "未体检";

                        list = new ArrayList();
                        excdoctorname = "";
                        deptSum = "";
                    }
                } else {//对方查询出异常
                    throw new Exception("wedServiceError:" + rootElt.elementText("message"));
                }
            } else {
                sql = "select a.excdoctorname,a.excdate,a.deptsum from pexam_items_title a where a.hosnum=? and a.pexamid=? and a.itemuuid=? and a.itemcode=?";
                tempMap = (Map) db.findOne(sql, new Object[]{hosnum, pexamid, itemuuid, itemcode});
                excdoctorname = (String) tempMap.get("excdoctorname") == null ? "" : (String) tempMap.get("excdoctorname");
                deptSum = (String) tempMap.get("deptsum") == null ? "" : (String) tempMap.get("deptsum");
                Object aa = tempMap.get("excdate");
                list = new ArrayList<PexamResult>();
                if (tempMap.get("excdate") == null) {//表示未执行过
                    isExamStart = "未体检";
                } else {
                    sql = "select a.indname,a.result,a.resultunit from pexam_results a where a.hosnum=? and a.pexamid=? and a.itemuuid=? and a.comid=?";
                    list = db.find(sql, new Object[]{hosnum, pexamid, itemuuid, itemcode}, PexamResult.class);
                }
            }

            temp.put("details", list);//指标明细
            temp.put("excdoctorname", excdoctorname);//体检医生
            temp.put("deptSum", deptSum);//科室小结
            temp.put("isExamStart", isExamStart);//是否体检

            JSONObject obj = JSONObject.fromObject(temp);
            pw.print(obj.toString());
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
	 * 体检医生站--获取体检项目的指标项
	 * 2012-12-05
	 */
    @RequestMapping(value = "/createExamNew", method = RequestMethod.POST)
    public void loadexamdetails2_xj(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String itemcode = request.getParameter("itemcode");//项目id
        String pexamid = request.getParameter("pexamid");

        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            Map temp = new HashMap();
            //String sql = "select t.*, b.result,b.unnormal from pexam_items_details t,pexam_results b where t.hosnum=? and t.parentid=? and b.pexamid=? and t.detailid=b.detailid and t.hosnum=b.hosnum order by t.detailid";
            //此处要修改------
            String sql = "select a.stringname,a.numvalue,a.result,a.unnormal,b.maxval,b.maxpromp,b.minval,b.minpromp," +
                    "b.defaultv,b.resultunit,b.resulttype,b.indid,b.indname from pexam_results a,pexam_items_ind b " +
                    "where a.hosnum=b.hosnum and a.indid=b.indid and a.hosnum=? and a.comid=? and a.pexamid=? order by b.sn";
            List<PexamIndOrResult> list = db.find(sql, new Object[]{hosnum, itemcode, pexamid}, PexamIndOrResult.class);
            if (list != null && list.size() > 0) {

            } else {
                sql = "select a.sex from pexam_mans a where a.hosnum=? and a.pexamid=?";
                list = db.find(sql, new Object[]{hosnum, pexamid});
                String sex = "";
                if (list != null && list.size() > 0) {
                    sex = (String) ((Map) list.get(0)).get("sex");
                }
                sql = "select b.* from pexam_items_comdet a,pexam_items_ind b where a.hosnum=b.hosnum and a.indid=b.indid and a.hosnum=? and a.comid=?";
                if (!"".equals(sex)) {
                    sql += " and (b.forsex='不限' or b.forsex='" + sex + "')";
                }
                sql += " order by b.sn";
                list = db.find(sql, new Object[]{hosnum, itemcode}, Pexam_items_details_ex.class);
            }
            //查询体检医生
            sql = "select a.excdoctorname from pexam_items_title a where a.hosnum=? and a.pexamid=? and a.itemcode=?";
            List titleList = db.find(sql, new Object[]{hosnum, pexamid, itemcode});
            String excdoctorname = "";
            if (titleList != null && titleList.size() > 0) {
                Map map0 = (Map) titleList.get(0);
                excdoctorname = (String) map0.get("excdoctorname") == null ? "" : (String) map0.get("excdoctorname");
            }
            temp.put("details", list);//指标明细
            temp.put("excdoctorname", excdoctorname);
            JSONObject obj = JSONObject.fromObject(temp);
            pw.print(obj.toString());
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
	 * 获取科室小结
	 */
    @RequestMapping(value = "/getDeptSumNew", method = RequestMethod.POST)
    public void getdeptSum_xj(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String hosnum = basHospitals.getHosnum();
        String deptCode = basDept.getDeptcode();

        String examid = request.getParameter("examid");
        String pexamid = request.getParameter("pexamid");

        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            //System.out.println("更具科室获取科室小结");
            String sql = "select * from pexam_deptsum a where a.hosnum=? and a.examid=? and a.pexamid=? and a.excdept=? and a.sumtype='科室小结'";
            List list = db.find(sql, new Object[]{hosnum, examid, pexamid, deptCode});
            JSONArray jsonArr = JSONArray.fromObject(list);
            pw.print(jsonArr.toString());
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
	 * 体检医生站--保存体检结果
	 */
    @RequestMapping(value = "/saveItemDetailsNew2", method = RequestMethod.POST)
    public void saveItemDetails2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String hosnum = basHospitals.getHosnum();
        String deptCode = basDept.getDeptcode();
        String excdoctorid = basUser.getId();
        String excdoctorname = basUser.getName();

        String operationType = request.getParameter("operationType");//add或者modify
        String examid = request.getParameter("examid");//如果是“个人体检”为“0000”
        String pexamid = request.getParameter("pexamid");//体检编号
        String comid = request.getParameter("comid");//组合项id
        String comname = request.getParameter("comname");//项目名称
        String itemuuid = request.getParameter("itemuuid");//
        String deptSum = request.getParameter("deptSum");//科室小结
        String doctorid = request.getParameter("doctorid");//该项体检医生医生id--目前都保存为空
        String doctorname = request.getParameter("doctorname");//该项体检医生名字
        String resultArr = URLDecoder.decode(request.getParameter("resultArr"), "utf-8");
        JSONArray jsonArr = JSONArray.fromObject(resultArr);
        Date excdate = new Date();

        PrintWriter pw = null;
        DBOperator db = null;
        try {
            String isDishDept = ParamBuffer.getParamValue(request, basHospitals.getHosnum(), basHospitals.getNodecode(), "健康体检", "是否区分科室");

            pw = response.getWriter();
            db = new DBOperator();
            String sql = "";
            //System.out.println("更新pexam_items_title表");
            sql = "update pexam_items_title a set a.excdate=?,a.excdoctorid=?,a.excdoctorname=?,a.deptSum=? where a.hosnum=? and a.itemuuid=?";
            db.excute(sql, new Object[]{excdate, doctorid, doctorname, deptSum, hosnum, itemuuid});

            List<Object[]> pi = new ArrayList<Object[]>();
            List<Object[]> pi_insert = new ArrayList<Object[]>();
            List temp = null;
            for (int i = 0; i < jsonArr.size(); i++) {
                JSONObject json = jsonArr.getJSONObject(i);
                String result2 = json.getString("result");//结果
                String indname = json.getString("indname");
                String indid = json.getString("indid");//指标明细id
                String isnormal = json.getString("isnormal");//状态
                String sn = json.getString("sn");
                if ("add".equals(operationType)) {
                    pi_insert.add(new Object[]{hosnum, itemuuid, examid, pexamid, "", "", doctorid, doctorname, excdate, comid, comname, indid, indname, result2, isnormal, sn});
                } else {
                    pi.add(new Object[]{result2, doctorid, doctorname, excdate, isnormal, sn, hosnum, pexamid, indid, comid, itemuuid});
                }
            }

            if (pi != null && pi.size() > 0) {
                Object[][] params = new Object[pi.size()][2];
                for (int i = 0; i < pi.size(); i++) {
                    params[i] = pi.get(i);
                }
                //System.out.println("更新体检项目中的个指标");
                sql = "update pexam_results a set a.result=?,a.excdoctor=?,a.excdoctorname=?,a.excdate=?,a.unnormal=?,a.sn=? where a.hosnum=? and a.pexamid=? and a.indid=? and a.comid=? and a.itemuuid=?";
                db.excuteBatch(sql, params);
            }

            if (pi_insert != null && pi_insert.size() > 0) {
                Object[][] params_insert = new Object[pi_insert.size()][2];
                for (int i = 0; i < pi_insert.size(); i++) {
                    params_insert[i] = pi_insert.get(i);
                }
                sql = "insert into pexam_results(hosnum,itemuuid,examid,pexamid,excdept,excdeptname,excdoctor,excdoctorname,excdate,comid,comname,indid,indname,result,unnormal,sn)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                db.excuteBatch(sql, params_insert);
            }

            List<Map> list = null;
            if ("Y".equals(isDishDept)) {//区分科室
                sql = "select count(a.itemuuid) as count from pexam_items_title a where a.hosnum=? and a.pexamid=? and a.excdept=? and a.excdate is not null";
                list = db.find(sql, new Object[]{hosnum, pexamid, deptCode});
            } else {
                sql = "select count(a.itemuuid) as count from pexam_items_title a where a.hosnum=? and a.pexamid=? and a.excdate is not null";
                list = db.find(sql, new Object[]{hosnum, pexamid});
            }
            if (list != null && list.size() > 0) {
                pw.print(list.get(0).get("count"));
            } else {
                pw.print(0);
            }
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
	 * 体检医生站--保存体检结果
	 */
    @RequestMapping(value = "/saveItemDetailsNew", method = RequestMethod.POST)
    public void saveItemDetails_xj(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String hosnum = basHospitals.getHosnum();
        String deptCode = basDept.getDeptcode();
        String excdoctorid = basUser.getId();
        String excdoctorname = basUser.getName();

        String examid = request.getParameter("examid");//如果是“个人体检”为“0000”
        String pexamid = request.getParameter("pexamid");//体检编号
        String comid = request.getParameter("comid");//组合项id
        String itemname = request.getParameter("itemname");//项目名称
        String itemuuid = request.getParameter("itemuuid");//
        String result = request.getParameter("result");//科室小结
        String doctorid = request.getParameter("doctorid");//该项体检医生医生id--目前都保存为空
        String doctorname = request.getParameter("doctorname");//该项体检医生名字
        String resultArr = URLDecoder.decode(request.getParameter("resultArr"), "utf-8");
        JSONArray jsonArr = JSONArray.fromObject(resultArr);
        Date excdate = new Date();

        PrintWriter pw = null;
        DBOperator db = null;
        try {
            String isDishDept = ParamBuffer.getParamValue(request, basHospitals.getHosnum(), basHospitals.getNodecode(), "健康体检", "是否区分科室");

            pw = response.getWriter();
            db = new DBOperator();
            String sql = "";
            //System.out.println("更新pexam_items_title表");
            sql = "update pexam_items_title a set a.excdate=?,a.excdoctorid=?,a.excdoctorname=? where a.hosnum=? and a.itemuuid=?";
            db.excute(sql, new Object[]{excdate, doctorid, doctorname, hosnum, itemuuid});

            List<Object[]> pi = new ArrayList<Object[]>();
            List<Object[]> pi_insert = new ArrayList<Object[]>();
            List temp = null;
            for (int i = 0; i < jsonArr.size(); i++) {
                JSONObject json = jsonArr.getJSONObject(i);
                String result2 = json.getString("result");//结果
                String indid = json.getString("indid");//指标明细id
                String isnormal = json.getString("isnormal");//状态
                //判断是否存在--在开始体检之后添加的项目就可能存在结果表中无数据的情况
                String sqlIsEx = "select * from pexam_results a where a.hosnum=? and a.pexamid=? and a.indid=? and a.comid=?";
                temp = db.find(sqlIsEx, new Object[]{hosnum, pexamid, indid, comid});
                if (temp != null && temp.size() > 0) {
                    pi.add(new Object[]{result2, doctorid, doctorname, excdate, isnormal, hosnum, pexamid, indid, comid});
                } else {
                    sql = "select c.comid,c.comname,c.excdept,c.excdeptname,c.comclass,a.indid,a.indname from pexam_items_ind a,pexam_items_comdet b,pexam_items_com c where a.hosnum=? and a.indid=? and a.indid=b.indid anda.hosnum=b.hosnum and c.hosnum=a.hosnum and c.comid=b.comid and c.comid=?";
                    temp = db.find(sql, new Object[]{hosnum, indid, comid});
                    Map map0 = (Map) temp.get(0);
                    pi.add(new Object[]{hosnum, examid, pexamid, map0.get("excdept"), map0.get("excdeptname"), doctorid, doctorname, excdate, map0.get("comid"), map0.get("comname"), map0.get("indid"), map0.get("indname"), result2, isnormal});
                    //sql = "insert into pexam_results(hosnum,examid,pexamid,excdept,excdeptname,excdoctor,excdoctorname,excdate,comid,comname,indid,indname,result,unnormal)values(?,?,?,?,?,?,?,?,?,?,?,?)";
                }
            }

            if (pi != null && pi.size() > 0) {
                Object[][] params = new Object[pi.size()][2];
                for (int i = 0; i < pi.size(); i++) {
                    params[i] = pi.get(i);
                }
                //System.out.println("更新体检项目中的个指标");
                sql = "update pexam_results a set a.result=?,a.excdoctor=?,a.excdoctorname=?,a.excdate=?,a.unnormal=? where a.hosnum=? and a.pexamid=? and a.indid=? and a.comid=?";
                db.excuteBatch(sql, params);
            }

            if (pi_insert != null && pi_insert.size() > 0) {
                Object[][] params_insert = new Object[pi_insert.size()][2];
                for (int i = 0; i < pi_insert.size(); i++) {
                    params_insert[i] = pi_insert.get(i);
                }
                sql = "insert into pexam_results(hosnum,examid,pexamid,excdept,excdeptname,excdoctor,excdoctorname,excdate,comid,comname,indid,indname,result,unnormal)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                db.excuteBatch(sql, params_insert);
            }

//			sql = "delete from pexam_deptsum a where a.hosnum=? and a.examid=? and a.pexamid=? and a.excdept=? and a.sumtype='科室小结'";
//			db.excute(sql,new Object[]{hosnum,examid,pexamid,deptCode});
//
//			sql = "insert into pexam_deptsum (hosnum,examid,excdept,deptsum,doctorid,examdate,pexamid,sumtype) values (?,?,?,?,?,?,?,?)";
//			db.excute(sql,new Object[]{hosnum,examid,deptCode,result,excdoctorid,excdate,pexamid,"科室小结"});

            List<Map> list = null;
            if ("Y".equals(isDishDept)) {//区分科室
                sql = "select count(a.itemuuid) as count from pexam_items_title a where a.hosnum=? and a.pexamid=? and a.examid=? and a.excdept=? and a.excdate is not null";
                list = db.find(sql, new Object[]{hosnum, pexamid, examid, deptCode});
            } else {
                sql = "select count(a.itemuuid) as count from pexam_items_title a where a.hosnum=? and a.pexamid=? and a.excdept=? and a.excdate is not null";
                list = db.find(sql, new Object[]{hosnum, pexamid, examid});
            }
            if (list != null && list.size() > 0) {
                pw.print(list.get(0).get("count"));
            } else {
                pw.print(0);
            }
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

    @RequestMapping("/loadCommonResult")
    public void loadCommonResult(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String indid = request.getParameter("indid");
        //System.out.println("indid==================="+indid);
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "";
            sql = "select * from pexam_ind_result a where a.hosnum=? and a.indid=?";
            List list = db.find(sql, new Object[]{hosnum, indid});
            String vmpagckage = "com/cpinfo/his/template/pexam";
            String vmname = "pexamIndResult.vm";
            String vm = VelocityUtils.generateGridVm(vmpagckage, vmname, "showList", list);
            pw.print(vm);
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    /*
	 * excel数据导入,体检人员信息导入
	 * 2012-12-18
	 */
    @RequestMapping("/importExcel")
    public void importExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");

        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String jyjgjm = basHospitals.getJyjgjm();//检验机构简码

        String deptcode = basDept.getDeptcode();
        String deptname = basDept.getDeptname();
        String userid = basUser.getId();
        String username = basUser.getName();
        String isExpBarcode = "";
        isExpBarcode = ParamBuffer.getParamValue(request, basHospitals.getHosnum(), basHospitals.getNodecode(), "健康体检", "导入名单是否生成条码");


        Timestamp timesTamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        String operatorResult = "fail";// 导入数据是否成功标志
        String examid = "";//
        Map xslmap = null;
        // 缓冲区域
        File tempPathFile = new File(request.getSession().getServletContext()
                .getRealPath("\\")
                + "uploadtemp\\");
        if (!tempPathFile.exists()) {
            tempPathFile.mkdirs();
        }
        // 默认路径
        String uploadTo = request.getSession().getServletContext().getRealPath(
                "\\")
                + "upload\\";
        // 支持的文件类型
        String[] errorType = {".xls"};
        // Create a factory for disk-based file items
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // 设置缓冲区大小，这里是4kb
        factory.setSizeThreshold(4096);
        // 设置缓冲区目录
        factory.setRepository(tempPathFile);
        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);
        // Set overall request size constraint
        // 设置最大文件尺寸，这里是4MB
        upload.setSizeMax(20 * 1024 * 1024);
        // 开始读取上传信息
        List fileItems = new ArrayList();
        try {
            fileItems = upload.parseRequest(request);
        } catch (FileUploadException e1) {
            e1.printStackTrace();
        }
        // 依次处理每个上传的文件
        Iterator iter = fileItems.iterator();
        System.out.println("fileItems的大小是" + fileItems.size());
        // 正则匹配，过滤路径取文件名
        String regExp = "(.+)$";
        // String regExp = ".+\\..+$";
        Pattern p = Pattern.compile(regExp);
        while (iter.hasNext()) {
            FileItem item = (FileItem) iter.next();
            // 忽略其他不是文件域的所有表单信息
            System.out.println("正在处理" + item.getFieldName());
            System.out.println("item.isFormField():" + item.isFormField());
            if (!item.isFormField()) {
                String name = item.getName();
                long size = item.getSize();
                if ((name == null || name.equals("")) && size == 0) {
                    continue;
                }
                Matcher m = p.matcher(name);
                boolean result = m.find();
                if (result) {
                    boolean flag = false;
                    for (int temp = 0; temp < errorType.length; temp++) {
                        if (m.group(1).toLowerCase().endsWith(errorType[temp])) {
                            flag = true;
                        }
                    }
                    if (!flag) {
                        System.out.println("上传了不支持的文件类型");
                        throw new IOException(name + ": wrong type");
                    }
                    System.out.println("m.group(1):" + m.group(1));
                    System.out.println("m.group(1).substring(m.group(1).indexOf('.'):"
                            + m.group(1).substring(m.group(1).indexOf(".")));
                    try {
                        String fileName = uploadTo
                                + new SimpleDateFormat("yyyyMMddHHmmssSSS")
                                .format(new Date())
                                + m.group(1).substring(
                                m.group(1).lastIndexOf("."));
                        System.out.println("fileName====" + fileName);
                        item.write(new File(fileName));
                        // 调用ReadExcel类进行读出excel
                        xslmap = ReadExcel.readExcel(fileName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                examid = item.getString();
                System.out.println("这是一个表单项:" + examid);
            }
        }

        DBOperator db = null;
        List<Object[]> pi = new ArrayList<Object[]>();//---存放个人体检信息---
        List<Object[]> pi2 = new ArrayList<Object[]>();//--存放个人体检项目参数--

        String sql = "";
        //如果oracle为11g
        Map<String, BigDecimal> seq_id_11 = null;
        //如果oracle为10g
        Map<String, Double> seq_id_10 = null;
        String repectNames = "";//重复人员 名单
        try {
            db = new DBOperator();
            sql = "select * from pexam_main a where a.hosnum=? and a.nodecode=? and a.examid=?";
            Map mainMap = (Map) db.findOne(sql, new Object[]{hosnum, nodecode, examid});
            String mexamtype = (String) mainMap.get("examtype");
            sql = "select * from pexam_mans a where a.hosnum=? and a.nodecode=? and a.examid=?";
            List list = db.find(sql, new Object[]{hosnum, nodecode, examid});
            Iterator iter2 = list.iterator();
            Map temp = null;
            String cardId = "";
            String pname = "";
            while (iter2.hasNext()) {
                temp = (Map) iter2.next();
                cardId = (String) temp.get("idnum");
                pname = (String) temp.get("patname");
                if (xslmap.get(cardId) != null) {
                    xslmap.remove(cardId);
                    repectNames += pname + "(" + cardId + ")";//重复人员名单
                }
            }

            Iterator newIter = xslmap.keySet().iterator();
            Map newMap = null;
			/*
			 * String codePath = "";//条码图片路径 BarCodeImage bar = new
			 * BarCodeImage(1,33);
			 */
            while (newIter.hasNext()) {
                newMap = (Map) xslmap.get(newIter.next());
                sql = "select seq_pexamid.nextval from dual";
                //Map<String, Double> seq_id = (Map<String, Double>) db.findOne(sql);

                String pexamid = null;
                if (oracleType == 11) {
                    seq_id_11 = (Map<String, BigDecimal>) db.findOne(sql);
                    pexamid = String.valueOf(seq_id_11.get("nextval").longValue());
                } else {
                    seq_id_10 = (Map<String, Double>) db.findOne(sql);
                    pexamid = String.valueOf(seq_id_10.get("nextval").longValue());
                }


                String idnum = (String) newMap.get("sfz");
                String sex = CardNumOperator.getSexByPid(idnum);
                String patname = (String) newMap.get("xm");
                if (sex == null) {
                    operatorResult = patname;
                    throw new Exception();
                }
                Date dateofbirth = CardNumOperator.getBirthdayByPid(idnum);
                String inputcpy = WordUtil.trans2PyCode(patname);
                String inputcwb = WordUtil.trans2WbCode(patname);
                String address = (String) newMap.get("jtzz");
                String ybbh = (String) newMap.get("ybh");// 医保号
                String township = (String) newMap.get("zxjd");// 镇乡/街道
                String village = (String) newMap.get("csq");// 村/社区
                String zh = (String) newMap.get("zm");// 组名

                //-----添加人员信息参数-----
                pi.add(new Object[]{hosnum, examid, newMap.get("xh"),
                        pexamid, "身份证", idnum,
                        patname, sex, dateofbirth, township, village,
                        mexamtype, newMap.get("lxdh"), inputcpy, inputcwb,
                        address, ybbh, zh, nodecode});


                //-----五院(和五常)导入的名单自动生成检验条码------
                if (isExpBarcode != null && isExpBarcode.equals("Y")) {
                    System.out.println("------开始生成条码导入，插入体检项目-----");// 如果是团体的话pexamid为空
                    sql = "select * from pexam_items a where (a.hosnum=? and a.examid=? and a.pexamid is null) or (a.hosnum=? and a.examid=? and a.pexamid=?)";
                    list = db.find(sql, new Object[]{hosnum, examid, hosnum, examid, pexamid});
                    if (list != null && list.size() > 0) {
                        String groupids = "";// 收集所有套餐id
                        String itemcodes = "";// 收集所有单独大项id
                        Map map2 = null;

                        for (int i = 0; i < list.size(); i++) {
                            map2 = (Map) list.get(i);
                            if ("y".equals(map2.get("isgroup"))) {// 是否是套餐的标志
                                groupids += "'" + map2.get("itemid") + "',";
                            } else {
                                itemcodes += "," + map2.get("itemid") + "',";
                            }
                        }

                        // 团体体检--此处未对套餐中有相同体检项目进行去重操作
                        if (groupids.length() > 0) {
                            groupids = groupids.substring(0, groupids.length() - 1);
                            System.out.println("获取组成套餐的体检项目");// 有性别要求的只需在此处加个性别条件就ok
                            sql = "select b.*,a.groupid,c.groupname from pexam_items_groupdetails a,pexam_items_com b,pexam_items_group c where a.groupid in (?) and a.itemcode=b.comid and c.groupid=a.groupid and a.hosnum=b.hosnum and a.hosnum=c.hosnum ".replace("?", groupids);
                            sql += " and a.hosnum=?";
                            if (!"".equals(sex)) {
                                sql += " and (b.forsex='不限' or b.forsex='" + sex + "')";
                            }
                            list = db.find(sql, new Object[]{hosnum});
                            if (list != null && list.size() > 0) {
                                for (int i = 0; i < list.size(); i++) {
                                    Map map = (Map) list.get(i);
                                    String itemcode = (String) map.get("comid");// 组合项目id
                                    String itemname = (String) map.get("comname");// 组合项目名称
                                    String groupid = (String) map.get("groupid");// 套餐id
                                    String groupname = (String) map.get("groupname");// 套餐名称
                                    String excdeptname = (String) map.get("excdeptname");//执行科室

                                    String itemuuid = new UUIDGenerator().generate().toString();
                                    String tmcode = "";//检验条码

                                    if ("外送".equals(map.get("comclass")) || "检验".equals(map.get("comclass"))) {// 如果是检验项目则把该项目传给lis

                                        String sqlname = "seq_pexam_tmcode_" + jyjgjm + ".nextval";
                                        sql = "select " + sqlname + " from dual";

                                        long codeseq;
                                        if (oracleType == 11) {
                                            seq_id_11 = (Map<String, BigDecimal>) db.findOne(sql);
                                            tmcode = jyjgjm + "5" + String.valueOf(seq_id_11.get("nextval").longValue());
                                        } else {
                                            seq_id_10 = (Map<String, Double>) db.findOne(sql);
                                            tmcode = jyjgjm + "5" + String.valueOf(seq_id_10.get("nextval").longValue());
                                        }


                                    }

                                    pi2.add(new Object[]{hosnum, itemuuid, examid, pexamid, itemcode, itemname,
                                            map.get("excdept"), map.get("excdeptname"), groupid, groupname, map.get("sn"),
                                            map.get("comclass"), userid, username, timesTamp, deptcode, deptname, tmcode});

                                }
                            }
                        }

                    }

                }


            }

            //-----导入名单插入个人信息表-----
            Object[][] params = new Object[pi.size()][2];
            for (int i = 0; i < pi.size(); i++) {
                params[i] = pi.get(i);
            }

            sql = "insert into pexam_mans(hosnum,examid,sn,pexamid,idtype,idnum,patname,sex,dateofbirth,township,village,examtype,phonecall,inputcpy,inputcwb,address,ybbh,zh,nodecode,adddate)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate)";
            db.excuteBatch(sql, params);


            Object[][] params_tit = new Object[pi2.size()][2];
            for (int i = 0; i < pi2.size(); i++) {
                params_tit[i] = pi2.get(i);
            }
            System.out.println("插入具体要体检的体检项目");
            sql = "insert into pexam_items_title (hosnum,itemuuid,examid,pexamid,itemcode,itemname,excdept,excdeptname,groupid," +
                    "groupname,sn,comclass,sheetdoctorid,sheetdoctorname,sheetdate,sheetdeptid,sheetdeptname,tmcode)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            db.excuteBatch(sql, params_tit);


            db.commit();
            operatorResult = "success";
        } catch (Exception e) {
            System.out.println("pi.size():" + pi.size());
            Object[][] params = new Object[pi.size()][2];
            for (int i = 0; i < pi.size(); i++) {
                params[i] = pi.get(i);
            }
            sql = "insert into pexam_mans(hosnum,examid,sn,pexamid,idtype,idnum,patname,sex,dateofbirth,township,village,examtype,phonecall,inputcpy,inputcwb,address,ybbh,zh,nodecode,adddate)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate)";
            db.excuteBatch(sql, params);

            Object[][] params_tit = new Object[pi2.size()][2];
            for (int i = 0; i < pi2.size(); i++) {
                params_tit[i] = pi2.get(i);
            }
            System.out.println("插入具体要体检的体检项目");
            sql = "insert into pexam_items_title (hosnum,itemuuid,examid,pexamid,itemcode,itemname,excdept,excdeptname,groupid," +
                    "groupname,sn,comclass,sheetdoctorid,sheetdoctorname,sheetdate,sheetdeptid,sheetdeptname,tmcode)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            db.excuteBatch(sql, params_tit);

            db.commit();
            e.printStackTrace();
            // db.rollback();
        } finally {
            db.freeCon();
        }
        operatorResult = URLEncoder.encode(operatorResult, "utf-8");
        repectNames = URLEncoder.encode(repectNames, "utf-8");
        String basePath = "/pexam/importPersonnel.htm?examid=" + examid
                + "&operatorResult=" + operatorResult + "&method=continue&repectNames=" + repectNames;
        // response.sendRedirect(basePath);
        request.getRequestDispatcher(basePath).forward(request, response);
    }

    /*
	 * ModExcel   模版。。下载
	 * 2012-12-18
	 */
    @RequestMapping("/ModExcel")
    public void ModExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        response.setCharacterEncoding("utf-8");

        OutputStream os = null;
        //System.out.println("导excel方法二：------");
        String name = "";
        try {

            //拼接表格数据
            List<Object[]> list = new ArrayList<Object[]>();
            Object[] obj = {"", "", "", "", "", ""};
            list.add(obj);

            String title = name + "人员列表模版";
            String dateStr = DateUtil.dateToString(new Date(), "yyyyMMddHHmmss");
            String fileName = title + "-" + dateStr + ".xls";
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            os = response.getOutputStream();
            ExcelUtils eu = new ExcelUtils();
            eu.export(os, title, new String[]{"序号", "姓名", "性别", "出生日期", "证件类型", "证件号"},
                    new int[]{25, 25, 20, 35, 30, 50}, list);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
	 * ModExcel2        有现成的xls供下载 模版。。下载 -------肖新华------2013-02-21
	 * 2012-12-18
	 */
    @RequestMapping(value = "/ModExcel2", method = RequestMethod.POST)
    public void download_stu(HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        //Bas_hospitals bh = (Bas_hospitals)request.getSession().getAttribute("login_hospital");
        //String filename = URLDecoder.decode(request.getParameter("filename"), "utf-8");
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        String filename = "model.xls";
        java.io.BufferedInputStream bis = null;
        java.io.BufferedOutputStream bos = null;
        try {
            filename = new String(filename.getBytes("iso8859-1"), "gb2312");
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "inline; filename="
                    + new String(filename.getBytes("gb2312"), "iso8859-1"));
            //绝对路径
            //		bis = new java.io.BufferedInputStream(new java.io.FileInputStream(
            //					"F:\\myeclipse6.5\\运行文件\\Platform\\WebRoot\\upload\\List\\model.xls"));
            //相对路径
            //	bis = new java.io.BufferedInputStream(new java.io.FileInputStream(request.getRealPath("/")+"/upload/List/model.xls"));
            bis = new java.io.BufferedInputStream(new java.io.FileInputStream(request.getRealPath("/") + "upload/Pexam/model_stu.xls"));
            bos = new java.io.BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null)
                bis.close();
            if (bos != null)
                bos.close();
        }
    }

    @RequestMapping(value = "/ModExcel3", method = RequestMethod.POST)
    public void download(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        //Bas_hospitals bh = (Bas_hospitals)request.getSession().getAttribute("login_hospital");
        //String filename = URLDecoder.decode(request.getParameter("filename"), "utf-8");
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        String filename = "model.xls";
        java.io.BufferedInputStream bis = null;
        java.io.BufferedOutputStream bos = null;
        try {
            filename = new String(filename.getBytes("iso8859-1"), "gb2312");
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-disposition", "inline; filename="
                    + new String(filename.getBytes("gb2312"), "iso8859-1"));
            //绝对路径
            //		bis = new java.io.BufferedInputStream(new java.io.FileInputStream(
            //					"F:\\myeclipse6.5\\运行文件\\Platform\\WebRoot\\upload\\List\\model.xls"));
            //相对路径
            //	bis = new java.io.BufferedInputStream(new java.io.FileInputStream(request.getRealPath("/")+"/upload/List/model.xls"));
            bis = new java.io.BufferedInputStream(new java.io.FileInputStream(request.getRealPath("/") + "upload/Pexam/model.xls"));
            bos = new java.io.BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null)
                bis.close();
            if (bos != null)
                bos.close();
        }
    }


    @RequestMapping("/examPersonnelRegister")
    public ModelAndView examPersonnalRegist(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        return new ModelAndView("pexam/examPersonnelRegister", modelMap);
    }


    /************************刷卡从医保获取体检信息操作并将人员信息插入体检人员表（开始）*****************************/
    //刷卡从医保获取体检信息操作并将人员信息插入体检人员表
	/*
	 * 从医保中心获取参保人员信息并转换为Pexam_mans对象
	 */
    public Pexam_mans getPexamManFromInsurance(String value, int type, String hospital_id, String oper_id, String oper_name) throws Exception {
        Pexam_mans pexamMan = new Pexam_mans();
//        InsuranceInfoQuery insuranceInfoQuery = new InsuranceInfoQuery();
//        PersonInfo personInfo = insuranceInfoQuery.queryPersonInfo(value, type, hospital_id, oper_id, oper_name);
//        //PersonInfo personInfo = insuranceInfoQuery.getPersonInfo(value, type, hospital_id, oper_id, oper_name);
//        pexamMan.setPatname(personInfo.getName());//姓名
//        int sexCode = personInfo.getSex();
//        String sex = "男";
//        if (sexCode == 0) {
//            sex = "女";
//        }
//        pexamMan.setSex(sex);//性别
//        pexamMan.setIdnum(personInfo.getIdcard());//证件号
//        pexamMan.setIdtype("身份证");//证件类型
//        String birthdayStr = personInfo.getBirthday();
//        Date dateofbirth = null;
//        if (!(birthdayStr == null || "".equals(birthdayStr))) {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//            dateofbirth = sdf.parse(birthdayStr);
//        }
//        pexamMan.setDateofbirth(dateofbirth);//出生日期
//        pexamMan.setInscardno(personInfo.getInsr_code());//医疗卡号
//        pexamMan.setAddress(personInfo.getAddress());//地址
//        pexamMan.setPerstype(personInfo.getPers_type());//人员类别编码
        return pexamMan;
    }

    /*
	 * 读卡后自动将体检人员信息录入系统并对人员信息及体检记录进行验证
	 */
    @RequestMapping(value = "/autoCreatePexamInfo", method = RequestMethod.POST)
    public void autoCreatePexamInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String nodecode = basHospitals.getNodecode();
        String jsonStr = request.getParameter("jsonStr");
        JSONObject jsonObj = JSONObject.fromObject(jsonStr);
        String examId = jsonObj.getString("examId");//预约编号
        String cardNo = jsonObj.getString("cardNo");//卡号
        String cardType = jsonObj.getString("cardType");//卡类型
        String idNum = "";//身份证号
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            List list = null;
            String sql = "";

            Map returnMap = new HashMap();
            String status = "Y";//本年度是否参加过体检的标志（“Y”参加过体检,“N”未参加过体检）
            String pexamId = "";//本次体检编号
            String msg = "";//提示信息

            String centerCode = "47039027-7";//医院在医保中心的编号
            Pexam_mans pexamMan = getPexamManFromInsurance(cardNo, Integer.parseInt(cardType), centerCode, basUser.getId(), basUser.getName());

            idNum = pexamMan.getIdnum();//身份证号
            sql = "select * from pexam_mans a where a.examid=? and a.idnum=?";
            List<Pexam_mans> pexamMansList = db.find(sql, new Object[]{examId, idNum}, Pexam_mans.class);
            //判断是否已经登记过
            if (pexamMansList != null && pexamMansList.size() > 0) {
                Pexam_mans pexamMans = null;
                Date bdate = null;//开始体检时间--看查询的记录中是否有未开始体检的记录
                for (int i = 0; i < pexamMansList.size(); i++) {
                    pexamMans = pexamMansList.get(i);
                    bdate = pexamMans.getBdate();
                    if (bdate == null) {//有未开始体检的记录
                        pexamId = pexamMans.getPexamid();
                        break;
                    }
                }
                if (!(pexamId == null || "".equals(pexamId))) {//未参加体检
                    status = "N";
                } else {
                    status = "Y";//本年度已参加过体检
                    msg = "本年度此人已参加过体检，是否再次参加体检";
                }
            } else {//本次预约中无此人登记信息，从医保中心获取该人信息
                String examType = "";//体检类型
                sql = "select * from pexam_main a where a.hosnum=? and a.examid=?";//获取体检类型--如果是农民体检则要对人员信息进行验证，如果参保类型不是农保者不予以参加体检
                list = db.find(sql, new Object[]{basHospitals.getHosnum(), examId});
                if (list != null && list.size() > 0) {
                    Map tempMap = (Map) list.get(0);
                    examType = (String) tempMap.get("examtype");
                } else {
                    throw new Exception("找不到对应的预约单位");
                }
                int perstype = pexamMan.getPerstype();
                if ((!"农民体检".equals(examType)) || (perstype == 64 || perstype == 65 || perstype == 66 || perstype == 67)) {
                    sql = "select seq_pexamid.nextval from dual";
                    Map<String, Double> seq_id = (Map<String, Double>) db.findOne(sql);
                    pexamId = Integer.toString(seq_id.get("nextval").intValue());
                    String inputcpy = WordUtil.trans2PyCode(pexamMan.getPatname());
                    String inputcwb = WordUtil.trans2WbCode(pexamMan.getPatname());

                    pexamMan.setHosnum(basHospitals.getHosnum());
                    pexamMan.setExamid(examId);
                    pexamMan.setExamtype(examType);
                    pexamMan.setPexamid(pexamId);
                    pexamMan.setInputcpy(inputcpy);
                    pexamMan.setInputcwb(inputcwb);
					/*
					String codePath = "";//条码图片路径
					//产生条码
					BarCodeImage bar  = new BarCodeImage(1,33);
					String path = bar.create39Image(Integer.toString(seq_id.get("nextval").intValue()));
					codePath = "../"+path;
					*/
                    sql = "insert into pexam_mans(hosnum,examid,pexamid,idtype,idnum,patname,sex,dateofbirth,examtype," +
                            "phonecall,inputcpy,inputcwb,nodecode,adddate)values(?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate)";
                    db.excute(sql, new Object[]{pexamMan.getHosnum(), pexamMan.getExamid(), pexamMan.getPexamid(), pexamMan.getIdtype(),
                            pexamMan.getIdnum(), pexamMan.getPatname(), pexamMan.getSex(), pexamMan.getDateofbirth(), pexamMan.getExamtype(),
                            pexamMan.getPhonecall(), pexamMan.getInputcpy(), pexamMan.getInputcwb(), nodecode});
                } else {
                    msg = "此人参保类型是不农保，是否允许参加农民体检？";
                }
                status = "N";
            }
            returnMap.put("status", status);
            returnMap.put("pexamId", pexamId);
            returnMap.put("idNum", idNum);
            returnMap.put("msg", msg);
            JSONObject returnJson = JSONObject.fromObject(returnMap);
            pw.print(returnJson.toString());
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
	 * 直接将体检人员信息插入体检人员表
	 */
    @RequestMapping(value = "/createPexamInfo", method = RequestMethod.POST)
    public void createPexamInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");

        String jsonStr = request.getParameter("jsonStr");
        JSONObject jsonObj = JSONObject.fromObject(jsonStr);
        String examId = jsonObj.getString("examId");//预约编号
        String cardNo = jsonObj.getString("cardNo");//卡号
        String cardType = jsonObj.getString("cardType");//卡类型

        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();

            //调用医保查询个人信息
            String centerCode = "47039027-7";//医院在医保中心的编号
            Pexam_mans pexamMan = getPexamManFromInsurance(cardNo, Integer.parseInt(cardType), centerCode, basUser.getId(), basUser.getName());

            String sql = "";
            List list = null;
            sql = "select * from pexam_main a where a.hosnum=? and a.examid=?";
            list = db.find(sql, new Object[]{basHospitals.getHosnum(), examId});
            String examType = "";
            if (list != null && list.size() > 0) {
                Map tempMap = (Map) list.get(0);
                examType = (String) tempMap.get("examtype");
            } else {
                throw new Exception("找不到对应的预约单位");
            }

            sql = "select seq_pexamid.nextval from dual";
            Map<String, Double> seq_id = (Map<String, Double>) db.findOne(sql);
            String pexamId = Integer.toString(seq_id.get("nextval").intValue());
            String inputcpy = WordUtil.trans2PyCode(pexamMan.getPatname());
            String inputcwb = WordUtil.trans2WbCode(pexamMan.getPatname());

            pexamMan.setHosnum(basHospitals.getHosnum());
            pexamMan.setExamid(examId);
            pexamMan.setExamtype(examType);
            pexamMan.setPexamid(pexamId);
            pexamMan.setInputcpy(inputcpy);
            pexamMan.setInputcwb(inputcwb);
			/*
			String codePath = "";//条码图片路径
			//产生条码
			BarCodeImage bar  = new BarCodeImage(1,33);
			String path = bar.create39Image(Integer.toString(seq_id.get("nextval").intValue()));
			codePath = "../"+path;
			*/
            sql = "insert into pexam_mans(hosnum,examid,pexamid,idtype,idnum,patname,sex,dateofbirth,examtype," +
                    "phonecall,inputcpy,inputcwb,adddate)values(?,?,?,?,?,?,?,?,?,?,?,?,sysdate)";
            db.excute(sql, new Object[]{pexamMan.getHosnum(), pexamMan.getExamid(), pexamMan.getPexamid(), pexamMan.getIdtype(),
                    pexamMan.getIdnum(), pexamMan.getPatname(), pexamMan.getSex(), pexamMan.getDateofbirth(), pexamMan.getExamtype(),
                    pexamMan.getPhonecall(), pexamMan.getInputcpy(), pexamMan.getInputcwb()});
            db.commit();
            pw.print(pexamId);
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
    //TODO

    /************************刷卡从医保获取体检信息操作并将人员信息插入体检人员表（结束）*****************************/


	/*
	 * 总检批量打印
	 * */
    @RequestMapping("/getSomePrintData")
    public ModelAndView getSomePrintData(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String isPrintCover = ParamBuffer.getParamValue(request, basHospitals.getHosnum(), basHospitals.getNodecode(), "健康体检", "是否打印总检报告封面");
        modelMap.put("isPrintCover", isPrintCover);
        modelMap.put("hosname", basHospitals.getHosname());
        modelMap.put("doctorname", basUser.getName());
        modelMap.put("hosnum", hosnum);

//		DBOperator db = null;
//		PrintWriter pw = null;
//		try{
//			db = new DBOperator();
//			//获取预约体检名称
//			String sql = "select * from pexam_main where hosnum=? order by bookdate desc";
//			List<Pexam_main> list = db.find(sql, new Object[] { hosnum },Pexam_main.class);
//			JSONArray jsonArr = JSONArray.fromObject(list);
//			pw.print(jsonArr.toString());
//		}catch(Exception e){
//			e.printStackTrace();
//			db.rollback();
//		}finally{
//			db.freeCon();
//		}
        return new ModelAndView("pexam/printMoreData", modelMap);
    }

    /*
	 * 通过体检名称、村、组、年龄和性别、体检时间加载体检人员列表
	 * */
    @RequestMapping("/loadCountByPatAndTime")
    public void loadByCountPatAndTime(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String examid = request.getParameter("examid");
        String village = URLDecoder.decode(request.getParameter("village"), "utf-8");
        String school = URLDecoder.decode(request.getParameter("school"), "utf-8");
        String zh = request.getParameter("zh");
        String age_begin = request.getParameter("age_begin");
        String age_end = request.getParameter("age_end");
        String sex = request.getParameter("sex");
        String printflag = request.getParameter("isPrint");
        String begin_date = request.getParameter("begin_date");
        String end_date = request.getParameter("end_date");
        String status = request.getParameter("status");        //一键总检查询“在检”人员
        ReturnValue returnValue = new ReturnValue();
        PrintWriter pw = null;
        DBOperator db = null;
        String sql = "";
        try {
            pw = response.getWriter();
            db = new DBOperator();

            if (status != null && "yjzj".equals(status)) {    //一键总检页面
                sql = "select count(*) as count,sum(decode(fin,total,'0','1')) as sum from (select a.*,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid and b.excdate is not null) as fin from pexam_mans a where a.printflag is null and a.hosnum=? and a.examid='" + examid + "' and a.bdate is not null and a.isover='在检' ";
            } else {
                if ("ALL".equals(examid)) {//查询全部批打人员名单
                    sql = "select count(*) as count from (select a.*,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid and b.excdate is not null) as fin from pexam_mans a where a.printflag is null and a.hosnum=?  and a.bdate is not null and a.isover='完成' ";
                } else {
                    sql = "select count(*) as count from (select a.*,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid and b.excdate is not null) as fin from pexam_mans a where a.printflag is null and a.hosnum=? and a.examid='" + examid + "' and a.bdate is not null and a.isover='完成' ";
                }
            }

            if (village != null && village != "") {
                sql += " and a.village like '%" + village + "%'";
            }
            if (zh != null && zh != "") {
                sql += " and a.zh='" + zh + "组'";
            }

            if (school != null && school != "") {
                sql += " and a.township like '%" + school + "%'";
            }

            if (age_begin != null && age_begin != "" && age_end != null && age_end != "") {
                sql += " and trunc(extract(year from sysdate)-extract(year from dateofbirth)) between '" + age_begin + "' and '" + age_end + "'";
            } else if (age_begin != null && age_begin != "" && (age_end == null || age_end == "")) {
                sql += " and trunc(extract(year from sysdate)-extract(year from dateofbirth)) >='" + age_begin + "'";
            } else if ((age_begin == null || age_begin == "") && age_end != null && age_end != "") {
                sql += " and trunc(extract(year from sysdate)-extract(year from dateofbirth)) <='" + age_end + "'";
            }

            if (begin_date != null && begin_date != "" && end_date != null && end_date != "") {
                sql += " and to_char(a.bdate,'yyyy-MM-dd') between '" + begin_date + "' and '" + end_date + "'";
            } else if (begin_date != null && begin_date != "" && (end_date == null || end_date == "")) {
                sql += " and to_char(a.bdate,'yyyy-MM-dd')>'" + begin_date + "'";
            } else if ((begin_date == null || begin_date == "") && end_date != null && end_date != "") {
                sql += " and to_char(a.bdate,'yyyy-MM-dd')<'" + end_date + "'";
            }
            if (sex != "" && sex != null && sex != "0" && !("0").equals(sex)) {
                if (("1").equals(sex)) {
                    sex = "男";
                } else if (("2").equals(sex)) {
                    sex = "女";
                }
                sql += " and a.sex='" + sex + "'";
            }
            if (printflag != "" && printflag != null && printflag != "0" && !("0").equals(printflag)) {
                if (("1").equals(printflag)) {
                    printflag = "Y";
                    sql += " and a.isprint='" + printflag + "'";
                } else if (("2").equals(printflag)) {
                    printflag = " is null";
                    sql += " and a.isprint" + printflag;
                }
            }
            sql += ")";
            Map<String, Integer> countMap = (Map<String, Integer>) db.findOne(sql, new Object[]{hosnum});
            returnValue.setStatus(true);
            if (status != null && "yjzj".equals(status)) {    //一键总检页面
                returnValue.setValue(String.valueOf(countMap.get("count")) + ";" + String.valueOf(countMap.get("sum")));
            } else {
                returnValue.setValue(String.valueOf(countMap.get("count")));
            }
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

    @RequestMapping("/loadPlistByPatAndTime")
    public void loadPlistByPatAndTime(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String examid = request.getParameter("examid");
        String village = URLDecoder.decode(request.getParameter("village"), "utf-8");
        String school = URLDecoder.decode(request.getParameter("school"), "utf-8");
        String zh = request.getParameter("zh");
        String age_begin = request.getParameter("age_begin");
        String age_end = request.getParameter("age_end");
        String begin_date = request.getParameter("begin_date");
        String end_date = request.getParameter("end_date");
        String sex = request.getParameter("sex");
        String printflag = request.getParameter("isPrint");
        String status = request.getParameter("status");        //一键总检查询“在检”人员
        int pageIndex = Integer.parseInt(request.getParameter("index").toString()); // 分页索引
        int pageItems = Integer.parseInt(request.getParameter("size").toString()); // 每页数量

        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "";
            List list = null;
            String pagingSql1 = "select OHYEAH.* from (select OHNO.*,rownum no from ("; // 用于分页段1
            String pagingSql2 = ") OHNO where rownum <= ?) OHYEAH where no > ?"; // 用于分页段2

            if (status != null && "yjzj".equals(status)) {    //一键总检页面
                sql = "select a.*,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid and b.excdate is not null) as fin from pexam_mans a where a.printflag is null and a.hosnum=? and a.examid='" + examid + "' and  a.bdate is not null and a.isover='在检' ";
            } else {
                //查找在检和未检的人员信息及在检人员的完成情况
                if ("ALL".equals(examid)) {
                    sql = "select a.*,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid and b.excdate is not null) as fin from pexam_mans a where a.printflag is null and a.hosnum=?  and  a.bdate is not null and a.isover='完成' ";
                } else {
                    sql = "select a.*,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid and b.excdate is not null) as fin from pexam_mans a where a.printflag is null and a.hosnum=? and a.examid='" + examid + "' and  a.bdate is not null and a.isover='完成' ";
                }
            }

            if (village != null && village != "") {
                sql += " and a.village like '%" + village + "%'";
            }
            if (zh != null && zh != "") {
                sql += " and a.zh='" + zh + "组'";
            }
            if (school != null && school != "") {
                sql += " and a.township like '%" + school + "%'";
            }

            if (age_begin != null && age_begin != "" && age_end != null && age_end != "") {
                sql += " and trunc(extract(year from sysdate)-extract(year from dateofbirth)) between '" + age_begin + "' and '" + age_end + "'";
            } else if (age_begin != null && age_begin != "" && (age_end == null || age_end == "")) {
                sql += " and trunc(extract(year from sysdate)-extract(year from dateofbirth)) >='" + age_begin + "'";
            } else if ((age_begin == null || age_begin == "") && age_end != null && age_end != "") {
                sql += " and trunc(extract(year from sysdate)-extract(year from dateofbirth)) <='" + age_end + "'";
            }
            if (begin_date != null && begin_date != "" && end_date != null && end_date != "") {
                sql += " and to_char(a.bdate,'yyyy-MM-dd') between '" + begin_date + "' and '" + end_date + "'";
            } else if (begin_date != null && begin_date != "" && (end_date == null || end_date == "")) {
                sql += " and to_char(a.bdate,'yyyy-MM-dd')>'" + begin_date + "'";
            } else if ((begin_date == null || begin_date == "") && end_date != null && end_date != "") {
                sql += " and to_char(a.bdate,'yyyy-MM-dd')<'" + end_date + "'";
            }
            if (sex != "" && sex != null && sex != "0" && !("0").equals(sex)) {
                if (("1").equals(sex)) {
                    sex = "男";
                } else if (("2").equals(sex)) {
                    sex = "女";
                }
                sql += " and a.sex='" + sex + "'";
            }
            if (printflag != "0" && !("0").equals(printflag)) {
                if (("1").equals(printflag)) {
                    printflag = "Y";
                    sql += " and a.isprint='" + printflag + "'";
                } else if (("2").equals(printflag)) {
                    printflag = " is null";
                    sql += " and a.isprint" + printflag;
                }

            }
            sql += " order by fin,pexamid asc";
            sql = pagingSql1 + sql + pagingSql2;
            list = db.find(sql, new Object[]{hosnum, pageIndex * pageItems + pageItems, pageIndex * pageItems});
            JSONArray jsonArr = JSONArray.fromObject(list);
            pw.print(jsonArr.toString());
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
	 * 打印所有总检报告
	 * */
    @RequestMapping("/getAllPatlistByExamid")
    public void getAllPatlistByExamid(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String examid = request.getParameter("examid");
        String village = URLDecoder.decode(request.getParameter("village"), "utf-8");
        String school = URLDecoder.decode(request.getParameter("school"), "utf-8");
        String zh = request.getParameter("zh");
        String age_begin = request.getParameter("age_begin");
        String age_end = request.getParameter("age_end");
        String begin_date = request.getParameter("begin_date");
        String end_date = request.getParameter("end_date");
        String sex = request.getParameter("sex");
        String printBeginAmount = request.getParameter("printBeginAmount");
        String printEndAmount = request.getParameter("printEndAmount");
        String printflag = request.getParameter("isPrint");
        String pagingSql1 = "";
        String pagingSql2 = "";
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "";
            List list = null;
            sql = "select a.*,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid and b.excdate is not null) as fin from pexam_mans a where a.printflag is null and a.hosnum=? and a.bdate is not null and a.isover='完成' ";

            if (examid != null && examid != "" && !"ALL".equals(examid)) {
                sql += " and a.examid= '" + examid + "'";
            }

            if (village != null && village != "") {
                sql += " and a.village like '%" + village + "%'";
            }
            if (zh != null && zh != "") {
                sql += " and a.zh='" + zh + "组'";
            }

            if (school != null && school != "") {
                sql += " and a.township like '%" + school + "%'";
            }

            if (age_begin != null && age_begin != "" && age_end != null && age_end != "") {
                sql += " and trunc(extract(year from sysdate)-extract(year from dateofbirth)) between '" + age_begin + "' and '" + age_end + "'";
            } else if (age_begin != null && age_begin != "" && (age_end == null || age_end == "")) {
                sql += " and trunc(extract(year from sysdate)-extract(year from dateofbirth)) >='" + age_begin + "'";
            } else if ((age_begin == null || age_begin == "") && age_end != null && age_end != "") {
                sql += " and trunc(extract(year from sysdate)-extract(year from dateofbirth)) <='" + age_end + "'";
            }
            if (begin_date != null && begin_date != "" && end_date != null && end_date != "") {
                sql += " and to_char(a.bdate,'yyyy-MM-dd') between '" + begin_date + "' and '" + end_date + "'";
            } else if (begin_date != null && begin_date != "" && (end_date == null || end_date == "")) {
                sql += " and to_char(a.bdate,'yyyy-MM-dd')>'" + begin_date + "'";
            } else if ((begin_date == null || begin_date == "") && end_date != null && end_date != "") {
                sql += " and to_char(a.bdate,'yyyy-MM-dd')<'" + end_date + "'";
            }
            if (sex != "" && sex != null && sex != "0" && !("0").equals(sex)) {
                if (("1").equals(sex)) {
                    sex = "男";
                } else if (("2").equals(sex)) {
                    sex = "女";
                }
                sql += " and a.sex='" + sex + "'";
            }
            if (printflag != "0" && !("0").equals(printflag)) {
                if (("1").equals(printflag)) {
                    printflag = "Y";
                    sql += " and a.isprint='" + printflag + "'";
                } else if (("2").equals(printflag)) {
                    printflag = " is null";
                    sql += " and a.isprint" + printflag;
                }

            }
            if (printBeginAmount != null && printBeginAmount != "" && printEndAmount != null && printEndAmount != "") {
                pagingSql1 = "select OHYEAH.* from (select OHNO.*,rownum no from (";
                pagingSql2 = ") OHNO where rownum <= '" + printEndAmount
                        + "') OHYEAH where no >= '" + printBeginAmount + "'";
            } else if (printBeginAmount != null && printBeginAmount != ""
                    && (printEndAmount == null || printEndAmount == "")) {
                pagingSql1 = "select OHYEAH.* from (select OHNO.*,rownum no from (";
                pagingSql2 = ") OHNO ) OHYEAH where no >='" + printBeginAmount
                        + "'";
            } else if ((printBeginAmount == null || printBeginAmount == "")
                    && printEndAmount != null && printEndAmount != "") {
                pagingSql1 = "select OHNO.*,rownum no from (";
                pagingSql2 = ") OHNO where rownum <= '" + printEndAmount + "'";
            }
            sql += " order by pexamid asc";
            sql = pagingSql1 + sql + pagingSql2;
            list = db.find(sql, new Object[]{hosnum});
            JSONArray jsonArr = JSONArray.fromObject(list);
            //System.out.println("-------------"+jsonArr.toString());
            pw.print(jsonArr.toString());
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


    //初始化村/社区combo
    @RequestMapping("/shequcombo")
    public void getShequCombo(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "select * from bas_dicts a where a.nekey='1111' and a.hosnum=? and a.nevalue!='!'";
            List list = db.find(sql, new Object[]{hosnum});
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

    //开始体检重打前先保存个人信息
    //初始化村/社区combo
    @RequestMapping("/savepersoninfo")
    public void savePersonInfo(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String pexamid = request.getParameter("pexamid");
        String address = request.getParameter("address");
        String phonecall = request.getParameter("phonecall");
        String village = request.getParameter("village");
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "update pexam_mans a set a.address=? , a.village=? , a.phonecall=? "
                    + "where a.pexamid=? and a.hosnum=? ";
            db.excute(sql, new Object[]{address, village, phonecall, pexamid, hosnum});
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

    //村（社区）字典维护
    @RequestMapping("/communityUpdate")
    public ModelAndView show_CommunityUpdate(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");


        return new ModelAndView("pexam/communityUpdate");
    }

    //字典分页
    @RequestMapping(value = "/getDictCount", method = RequestMethod.GET)
    public void getDictCount(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String content = URLDecoder.decode(request.getParameter("content"), "utf-8");
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "select count(*) from bas_dicts where nevalue !='!' and nekey =1111 and hosnum='" + hosnum + "'";
            if (null != content && !"".equals(content)) {
                sql += " and contents like '%" + content + "%' ";
            }
            List<Map<String, BigDecimal>> countlist = db.find(sql.toString());
            int totalCount = countlist.get(0).get("count(*)").intValue();
            db.commit();
            pw.print(totalCount);
            pw.flush();
            pw.close();
        } catch (Exception e) {
            pw.print("0");
            pw.flush();
            pw.close();
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }
    }

    //字典表数据
    @RequestMapping(value = "/loadGrid")
    public void loadDictGridByDictId(HttpServletRequest request, HttpServletResponse response) {
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        response.setContentType("text/html;charset=utf-8");
        int pageIndex = Integer.parseInt(request.getParameter("pageIndex"));
        int pageSize = Integer.parseInt(request.getParameter("pageSize"));
        String content = request.getParameter("content");
        DBOperator db = null;
        PrintWriter pw = null;
        StringBuffer sqlpage = new StringBuffer();
        try {
            content = URLDecoder.decode(content, "utf-8");
            db = new DBOperator();
            pw = response.getWriter();
            String pagingSql1 = "select OHYEAH.* from (select OHNO.*,rownum no from ("; // 用于分页
            // 段1
            String pagingSql2 = ") OHNO where rownum <= ?) OHYEAH where no > ?"; // 用于分页段2
            String sql = "select * from bas_dicts a where a.nekey='1111' and hosnum=? ";
            if (null != content && !"".equals(content)) {
                sql += " and contents like '%" + content + "%' ";
            }
            sql += " order by nevalue";
            String sqlQuery = pagingSql1 + sql + pagingSql2;
            List list = db.find(sqlQuery, new Object[]{hosnum, pageIndex * pageSize + pageSize, pageIndex * pageSize});
            JSONArray jsons = JSONArray.fromObject(list);
            pw.print(jsons.toString());
            pw.flush();
            pw.close();
            db.commit();
        } catch (Exception e) {
            db.rollback();
            pw.print("fail");
            pw.flush();
            pw.close();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }
    }

    //删除操作
    @RequestMapping(value = "/deleteRow", method = RequestMethod.POST)
    public void deleteRowByDictID(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String str = request.getParameter("str");
        DBOperator db = null;
        try {
            db = new DBOperator();
            String sql = "delete from bas_dicts t where   t.dictid = ?";
            db.excute(sql, new Object[]{str});
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();

        } finally {
            db.freeCon();

        }
    }

    //保存操作
    @RequestMapping(value = "/updateOrinsert", method = RequestMethod.POST)
    public void updateOrinsert(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String data = request.getParameter("data");
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "";
            JSONArray jsons = JSONArray.fromObject(data);
            for (int i = 0; i < jsons.size(); i++) {
                JSONObject json = jsons.getJSONObject(i);
                String dictid = json.getString("dictid");
                String nevalue = json.getString("nevalue");
                String inputcpy = json.getString("inputcpy");
                String inputcwb = json.getString("inputcwb");
                String contents = json.getString("str");
                String nekey = "1111";
                if (inputcpy.equals("") || inputcpy == null) {
                    inputcpy = WordUtil.trans2PyCode(contents);
                    inputcwb = WordUtil.trans2WbCode(contents);
                    sql = "insert into bas_dicts (dictid,nevalue,inputcpy,inputcwb,contents,hosnum,nekey,sysname) "
                            + "values(?,?,?,?,?,?,?,'基础') ";
                    db.excute(sql, new Object[]{dictid, nevalue, inputcpy, inputcwb, contents, hosnum, nekey});
                } else {
                    inputcpy = WordUtil.trans2PyCode(contents);
                    inputcwb = WordUtil.trans2WbCode(contents);
                    sql = "update bas_dicts a set a.nevalue=?, a.inputcpy=?, a.inputcwb=?, a.contents=? where a.dictid=? ";
                    db.excute(sql, new Object[]{nevalue, inputcpy, inputcwb, contents, dictid});
                }
            }
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            pw.print("fail");

        } finally {
            db.freeCon();

        }
    }

    //验证字典项目是否存在
    @RequestMapping(value = "/checknevalue", method = RequestMethod.POST)
    public void checknevalue(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nevalue = request.getParameter("nevalue");
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "select * from bas_dicts a "
                    + "where a.nekey='1111' and a.hosnum=? and a.nevalue=?";
            List list = db.find(sql, new Object[]{hosnum, nevalue});
            if (list.size() > 0) {
                pw.print("Y");

            }
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            pw.print("fail");

        } finally {
            db.freeCon();

        }
    }

    /*
	 * 某人已打印完成，插入完成标志
	 * */
    @RequestMapping("/insertPrintFlag")
    public void insertPrintFlag(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String pexamid = request.getParameter("pexamid");
        ReturnValue returnValue = new ReturnValue();
        String sql = "";
        DBOperator db = new DBOperator();
        try {
            sql = "update pexam_mans t set t.printflag='Y' where t.hosnum=? and t.pexamid=? ";
            db.excute(sql, new Object[]{hosnum, pexamid});
            returnValue.setValue(pexamid + "已打印！");
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
    }

    /*
	 * 如果已全部打印完成，置空完成打印标志
	 * */
    @RequestMapping("/cleanprintflag")
    public void cleanprintflag(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String begin_date = request.getParameter("begin_date");
        String end_date = request.getParameter("end_date");
        String examid = request.getParameter("examid");//如果为团体则为“0000”
        String sql = " ";
        DBOperator db = new DBOperator();
        try {
            sql = "update pexam_mans t set t.printflag='' where t.hosnum=? and t.examid=? and to_char(t.bdate,'yyyy-MM-dd') between ? and ?";
            db.excute(sql, new Object[]{hosnum, examid, begin_date, end_date});
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }

    }

    //打印体检预约单
    @SuppressWarnings("unchecked")
    @RequestMapping("/doPrintY")
    public ModelAndView doPrintY(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosname = basHospitals.getHosname();
        //System.out.println("hosname:================"+hosname);
        String isPrintSelfInfo = ParamBuffer.getParamValue(request, basHospitals.getHosnum(), basHospitals.getNodecode(), "健康体检", "是否打印个人信息条码");
        String isPrintAllBarcode = ParamBuffer.getParamValue(request, basHospitals.getHosnum(), basHospitals.getNodecode(), "健康体检", "批打是否打印条码");
        //String isPrintAllBarcode = request.getParameter("isPrintAllBarcode");//批打是否打印条码
        modelMap.put("isPrintSelfInfo", isPrintSelfInfo);
        modelMap.put("isPrintAllBarcode", isPrintAllBarcode);
        modelMap.put("hosname", hosname);
        return new ModelAndView("pexam/printReservationData", modelMap);

    }

    //加载体检名称
    @RequestMapping("/loadexamName")
    public void loadexamName(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            //获取预约体检名称
            String sql = "select * from pexam_main a where a.hosnum=? and a.nodecode=? and (a.iszf!='Y' or a.iszf is null) order by a.bookdate desc";
            List<Pexam_main> list = db.find(sql, new Object[]{hosnum, nodecode}, Pexam_main.class);
            JSONArray jsonArr = JSONArray.fromObject(list);
            pw.print(jsonArr.toString());
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }

    }

    @RequestMapping("/loadCountByPatAndAge")
    public void loadCountByPatAndAge(HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String examid = request.getParameter("examid");// 如果不是团体则就是为"0000"
        String town_village = URLDecoder.decode(request
                .getParameter("town_village"), "utf-8");
        String zh_begin = URLDecoder.decode(request.getParameter("zh_begin"),
                "utf-8");
        String zh_end = URLDecoder.decode(request.getParameter("zh_end"),
                "utf-8");
        String age_begin = request.getParameter("age_begin");
        String age_end = request.getParameter("age_end");
        String name = URLDecoder.decode(request.getParameter("name"), "utf-8");
        String isTest = request.getParameter("isTest");
        ReturnValue returnValue = new ReturnValue();
        PrintWriter pw = null;
        DBOperator db = null;
        String sql = "";
        try {
            pw = response.getWriter();
            db = new DBOperator();
            sql = "select count(*) count from pexam_mans t where t.hosnum=? and t.examid=? ";
            if (zh_begin != null && zh_begin != "") {
                if (zh_end == null || zh_end == "") {
                    sql += " and t.zh= '" + zh_begin + "组'";
                }
            }
            if (zh_end != null && zh_end != "") {
                if (zh_begin == null || zh_begin == "") {
                    sql += " and t.zh= '" + zh_end + "组'";
                }
            }

            if (zh_begin != null && zh_begin != "" && zh_end != null
                    && zh_end != "") {
				/*
				sql = "select count(*) count from (select s.*, to_number(substr(s.zh, 0, instr(s.zh, '组') - 1))value FROM pexam_mans s where not REGEXP_LIKE(trim(s.zh), '^([0-9]+/.[0-9]+)$|^[0-9]+$')) t,bas_dept a,bas_hospitals b "
						+ " where a.hosnum = ?  and a.hosnum = b.hosnum"
						+ " and a.nodecode = b.nodecode and a.deptname = '体检中心' and a.hosnum = t.hosnum   and t.examid = ? and t.value between "
						+ zh_begin + " and " + zh_end;
		       */
                sql = "select count(*) count from (select s.*, to_number( case when regexp_like(substr(s.zh, 0, instr(s.zh, '组') - 1),'^([0-9]+/.[0-9]+)$|^[0-9]+$') then substr(s.zh, 0, instr(s.zh, '组') - 1) else '88888888' end   )value FROM pexam_mans s where not REGEXP_LIKE(trim(s.zh), '^([0-9]+/.[0-9]+)$|^[0-9]+$')) t,bas_dept a,bas_hospitals b "
                        + " where a.hosnum = ?  and a.hosnum = b.hosnum"
                        + " and a.nodecode = b.nodecode and a.deptname = '体检中心' and a.hosnum = t.hosnum   and t.examid = ? and t.value between "
                        + zh_begin + " and " + zh_end;
            }

            if (town_village != null && town_village != "") {
                sql += " and t.village like '%" + town_village + "%'";
            }
            if (age_begin != null && age_begin != "" && age_end != null
                    && age_end != "") {
                sql += " and trunc(extract(year from sysdate)-extract(year from dateofbirth)) between '"
                        + age_begin + "' and '" + age_end + "'";
            } else if (age_begin != null && age_begin != ""
                    && (age_end == null || age_end == "")) {
                sql += " and trunc(extract(year from sysdate)-extract(year from dateofbirth)) >'"
                        + age_begin + "'";
            } else if ((age_begin == null || age_begin == "")
                    && age_end != null && age_end != "") {
                sql += " and trunc(extract(year from sysdate)-extract(year from dateofbirth)) <'"
                        + age_end + "'";
            }
            if (name != null && name != "") {
                sql += " and t.patname like '%" + name + "%'";
            }
            if (("1").equals(isTest)) {
                sql += " and t.bdate is null";
            } else if (("2").equals(isTest)) {
                sql += " and t.bdate is not null and not EXISTS (select a.pexamid from pexam_deptsum a where a.pexamid = t.pexamid)";
            }

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


    @RequestMapping("/loadPlistByPatAndAge")
    public void loadPlistByPatAndAge(HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String examid = request.getParameter("examid");// 如果不是团体则就是为"0000"
        String town_village = URLDecoder.decode(request
                .getParameter("town_village"), "utf-8");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute(
                "login_user");
        String zh_begin = URLDecoder.decode(request.getParameter("zh_begin"),
                "utf-8");
        String zh_end = URLDecoder.decode(request.getParameter("zh_end"),
                "utf-8");
        String age_begin = request.getParameter("age_begin");
        String age_end = request.getParameter("age_end");
        String name = URLDecoder.decode(request.getParameter("name"), "utf-8");
        String isTest = request.getParameter("isTest");

        int pageIndex = Integer.parseInt(request.getParameter("index")
                .toString()); // 分页索引
        int pageItems = Integer.parseInt(request.getParameter("size")
                .toString()); // 每页数量
        String pagingSql1 = "select OHYEAH.* from (select OHNO.*,rownum no from ("; // 用于分页段1
        String pagingSql2 = ") OHNO where rownum <= ?) OHYEAH where no > ?"; // 用于分页段2
        PrintWriter pw = null;
        DBOperator db = null;
        String sql = "";
        try {
            List<Map> list = null;
            pw = response.getWriter();
            db = new DBOperator();
            sql = "select t.patname,t.sex,to_char(dateofbirth,'yyyy-MM-dd') dateofbirth,t.phonecall,t.village,t.examid,t.pexamid,t.examtype,trunc(months_between(sysdate,dateofbirth)/12) age,t.address,t.idnum,t.zh,t.ybbh,t.codepath,to_char(bdate,'yyyy-MM-dd') bdate,a.location,b.hosname from pexam_mans t,bas_dept a,bas_hospitals b where a.hosnum=? and a.nodecode=? and a.hosnum=b.hosnum and a.nodecode=b.nodecode and a.deptname='体检中心' and t.hosnum=? and t.examid=? ";
            if (zh_begin != null && zh_begin != "") {
                if (zh_end == null || zh_end == "") {
                    sql += " and t.zh= '" + zh_begin + "组'";
                }
            }
            if (zh_end != null && zh_end != "") {
                if (zh_begin == null || zh_begin == "") {
                    sql += " and t.zh= '" + zh_end + "组'";
                }
            }

            if (zh_begin != null && zh_begin != "" && zh_end != null && zh_end != "") {
				/*
				sql = "select t.patname, t.sex,to_char(dateofbirth, 'yyyy-MM-dd') dateofbirth,t.phonecall,t.village,t.examid,"
						+ "t.pexamid,t.examtype,trunc(months_between(sysdate, dateofbirth) / 12) age,t.address,t.idnum,t.zh,t.ybbh,t.codepath,to_char(bdate, 'yyyy-MM-dd') bdate,a.location,b.hosname from (select s.*, to_number(substr(s.zh, 0, instr(s.zh, '组') - 1)) value FROM pexam_mans s where not REGEXP_LIKE(trim(s.zh), '^([0-9]+/.[0-9]+)$|^[0-9]+$')) t,bas_dept a,bas_hospitals b "
						+ " where a.hosnum = ? and a.nodecode = ? and a.hosnum = b.hosnum"
						+ " and a.nodecode = b.nodecode and a.deptname = '体检中心' and t.hosnum = ? and t.examid = ? and t.value between "
						+ zh_begin + " and " + zh_end;
				*/
                sql = "select t.patname, t.sex,to_char(dateofbirth, 'yyyy-MM-dd') dateofbirth,t.phonecall,t.village,t.examid,"
                        + "t.pexamid,t.examtype,trunc(months_between(sysdate, dateofbirth) / 12) age,t.address,t.idnum,t.zh,t.ybbh,t.codepath,to_char(bdate, 'yyyy-MM-dd') bdate,a.location,b.hosname from (select s.*, to_number( case when regexp_like(substr(s.zh, 0, instr(s.zh, '组') - 1),'^([0-9]+/.[0-9]+)$|^[0-9]+$') then substr(s.zh, 0, instr(s.zh, '组') - 1) else '88888888' end ) value FROM pexam_mans s where not REGEXP_LIKE(trim(s.zh), '^([0-9]+/.[0-9]+)$|^[0-9]+$')) t,bas_dept a,bas_hospitals b "
                        + " where a.hosnum = ? and a.nodecode = ? and a.hosnum = b.hosnum"
                        + " and a.nodecode = b.nodecode and a.deptname = '体检中心' and t.hosnum = ? and t.examid = ? and t.value between "
                        + zh_begin + " and " + zh_end;

            }
            if (town_village != null && town_village != "") {
                sql += " and t.village like '%" + town_village + "%'";
            }

            if (age_begin != null && age_begin != "" && age_end != null
                    && age_end != "") {
                sql += " and trunc(extract(year from sysdate)-extract(year from dateofbirth)) between '"
                        + age_begin + "' and '" + age_end + "'";
            } else if (age_begin != null && age_begin != ""
                    && (age_end == null || age_end == "")) {
                sql += " and trunc(extract(year from sysdate)-extract(year from dateofbirth)) >'"
                        + age_begin + "'";
            } else if ((age_begin == null || age_begin == "")
                    && age_end != null && age_end != "") {
                sql += " and trunc(extract(year from sysdate)-extract(year from dateofbirth)) <'"
                        + age_end + "'";
            }
            if (name != null && name != "") {
                sql += " and t.patname like '%" + name + "%'";
            }
            if (("1").equals(isTest)) {
                sql += " and t.bdate is null";
            } else if (("2").equals(isTest)) {
                sql += " and t.bdate is not null and not EXISTS (select a.pexamid from pexam_deptsum a where a.pexamid = t.pexamid)";
            }
            sql += " order by t.pexamid";
            sql = pagingSql1 + sql + pagingSql2;
            list = db.find(sql, new Object[]{basHospitals.getHosnum(),
                    basUser.getNodecode(), hosnum, examid,
                    pageIndex * pageItems + pageItems, pageIndex * pageItems});
            JSONArray jsonArr = JSONArray.fromObject(list);
            pw.print(jsonArr.toString());
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }


    @RequestMapping("/loadAllPlist")
    public void loadAllPlist(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String examid = request.getParameter("examid");// 如果不是团体则就是为"0000"
        String town_village = URLDecoder.decode(request.getParameter("town_village"), "utf-8");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String zh_begin = URLDecoder.decode(request.getParameter("zh_begin"), "utf-8");
        String zh_end = URLDecoder.decode(request.getParameter("zh_end"), "utf-8");
        String age_begin = request.getParameter("age_begin");
        String age_end = request.getParameter("age_end");
        String name = URLDecoder.decode(request.getParameter("name"), "utf-8");
        String isTest = request.getParameter("isTest");
        String printBeginAmount = request.getParameter("printBeginAmount");
        String printEndAmount = request.getParameter("printEndAmount");
        DBOperator db = null;
        PrintWriter pw = null;
        String sql = "";
        String pexamid = "";
        String codePath = "";
        String pagingSql1 = "";
        String pagingSql2 = "";
        try {
            List<Map> list = new ArrayList<Map>();
            pw = response.getWriter();
            db = new DBOperator();
            sql = "select t.patname,t.sex,to_char(dateofbirth,'yyyy-MM-dd') dateofbirth,t.phonecall,t.village,t.examid,t.pexamid,t.examtype,trunc(months_between(sysdate,dateofbirth)/12) age,t.address,t.idnum,t.zh,t.ybbh,t.codepath,a.location,b.hosname from pexam_mans t,bas_dept a,bas_hospitals b where a.hosnum=? and a.nodecode=? and a.hosnum=b.hosnum and a.nodecode=b.nodecode and a.deptname='体检中心' and t.hosnum=? and t.examid=? ";
            if (zh_begin != null && zh_begin != "") {
                if (zh_end == null || zh_end == "") {
                    sql += " and t.zh= '" + zh_begin + "组'";
                }
            }
            if (zh_end != null && zh_end != "") {
                if (zh_begin == null || zh_begin == "") {
                    sql += " and t.zh= '" + zh_end + "组'";
                }
            }
            if (zh_begin != null && zh_begin != "" && zh_end != null && zh_end != "") {
				/*
				sql = "select t.patname, t.sex,to_char(dateofbirth, 'yyyy-MM-dd') dateofbirth,t.phonecall,t.village,t.examid,"
						+ "t.pexamid,t.examtype,trunc(months_between(sysdate, dateofbirth) / 12) age,t.address,t.idnum,t.zh,t.ybbh,t.codepath,to_char(bdate, 'yyyy-MM-dd') bdate,a.location,b.hosname from (select s.*, to_number(substr(s.zh, 0, instr(s.zh, '组') - 1)) value FROM pexam_mans s where not REGEXP_LIKE(trim(s.zh), '^([0-9]+/.[0-9]+)$|^[0-9]+$')) t,bas_dept a,bas_hospitals b "
						+ " where a.hosnum = ? and a.nodecode = ? and a.hosnum = b.hosnum"
						+ " and a.nodecode = b.nodecode and a.deptname = '体检中心' and t.hosnum = ? and t.examid = ? and t.value between "
						+ zh_begin + " and " + zh_end;
				*/

                sql = "select t.patname, t.sex,to_char(dateofbirth, 'yyyy-MM-dd') dateofbirth,t.phonecall,t.village,t.examid,"
                        + "t.pexamid,t.examtype,trunc(months_between(sysdate, dateofbirth) / 12) age,t.address,t.idnum,t.zh,t.ybbh,t.codepath,to_char(bdate, 'yyyy-MM-dd') bdate,a.location,b.hosname from (select s.*, to_number( case when regexp_like(substr(s.zh, 0, instr(s.zh, '组') - 1),'^([0-9]+/.[0-9]+)$|^[0-9]+$') then substr(s.zh, 0, instr(s.zh, '组') - 1) else '88888888' end ) value FROM pexam_mans s where not REGEXP_LIKE(trim(s.zh), '^([0-9]+/.[0-9]+)$|^[0-9]+$')) t,bas_dept a,bas_hospitals b "
                        + " where a.hosnum = ? and a.nodecode = ? and a.hosnum = b.hosnum"
                        + " and a.nodecode = b.nodecode and a.deptname = '体检中心' and t.hosnum = ? and t.examid = ? and t.value between "
                        + zh_begin + " and " + zh_end;
            }
            if (town_village != null && town_village != "") {
                sql += " and t.village like '%" + town_village + "%'";
            }
            if (age_begin != null && age_begin != "" && age_end != null
                    && age_end != "") {
                sql += " and trunc(extract(year from sysdate)-extract(year from dateofbirth))  between '"
                        + age_begin + "' and '" + age_end + "'";
            } else if (age_begin != null && age_begin != ""
                    && (age_end == null || age_end == "")) {
                sql += " and trunc(extract(year from sysdate)-extract(year from dateofbirth))  >'"
                        + age_begin + "'";
            } else if ((age_begin == null || age_begin == "")
                    && age_end != null && age_end != "") {
                sql += " and trunc(extract(year from sysdate)-extract(year from dateofbirth))  <'"
                        + age_end + "'";
            }
            if (name != null && name != "") {
                sql += " and t.patname like '%" + name + "%'";
            }
            if (("1").equals(isTest)) {
                sql += " and t.bdate is null";
            } else if (("2").equals(isTest)) {
                sql += " and t.bdate is not null and not EXISTS (select a.pexamid from pexam_deptsum a where a.pexamid = t.pexamid)";
            }
            if (printBeginAmount != null && printBeginAmount != ""
                    && printEndAmount != null && printEndAmount != "") {
                pagingSql1 = "select OHYEAH.* from (select OHNO.*,rownum no from (";
                pagingSql2 = ") OHNO where rownum <= '" + printEndAmount
                        + "') OHYEAH where no >= '" + printBeginAmount + "'";
            } else if (printBeginAmount != null && printBeginAmount != ""
                    && (printEndAmount == null || printEndAmount == "")) {
                pagingSql1 = "select OHYEAH.* from (select OHNO.*,rownum no from (";
                pagingSql2 = ") OHNO ) OHYEAH where no >='" + printBeginAmount
                        + "'";
            } else if ((printBeginAmount == null || printBeginAmount == "")
                    && printEndAmount != null && printEndAmount != "") {
                pagingSql1 = "select OHNO.*,rownum no from (";
                pagingSql2 = ") OHNO where rownum <= '" + printEndAmount + "'";
            }
            sql += " order by t.pexamid asc";
            sql = pagingSql1 + sql + pagingSql2;
            list = db.find(sql, new Object[]{basHospitals.getHosnum(),
                    basUser.getNodecode(), hosnum, examid});
            JSONArray jsonArr = JSONArray.fromObject(list);
            pw.print(jsonArr.toString());
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    @RequestMapping("/doChangeTime")
    public ModelAndView doChangeTime(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        String examid = request.getParameter("examid");
        String printType = request.getParameter("printType");
        modelMap.put("printType", printType);
        DBOperator db = null;
        List<Map> list = new ArrayList<Map>();
        try {
            String sql = "select pm.patname, to_char(t.bookdate,'yyyy-MM-dd') bookdate from pexam_main t left join pexam_mans pm on pm.examid=t.examid where t.examid=?";

            db = new DBOperator();
            list = db.find(sql, new Object[]{examid});
            if (list.size() > 0) {
                modelMap.put("bookdate", list.get(0).get("bookdate").toString());
                modelMap.put("patname", list.get(0).get("patname").toString());
            }
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("pexam/doChangeTime", modelMap);
    }

    //打印lis条码
    @RequestMapping("/doPrintTM")
    public void doPrintTM(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        String pexamid = request.getParameter("pexamid");
        String itemtypes = request.getParameter("itemtypes");
        String sql = "";
        DBOperator db = null;
        List<Map> list = null;
        List<Map> templist = null;
        PrintWriter pw = null;
        try {
            sql = "select t.itemcode,t.itemname,j.jysample from pexam_items_title t,pexam_jyitems j where t.pexamid=? and t.comclass='检验' and t.itemcode=j.jyitemid and j.itemtypes=? ";
            db = new DBOperator();
            list = new ArrayList<Map>();
            templist = new ArrayList<Map>();
            list = db.find(sql, new Object[]{pexamid, itemtypes});
            Map map0 = new HashMap();
            String seq_jyitemid = "";
            for (int i = 0; i < list.size(); i++) {
                boolean flag = map0.containsKey(list.get(i).get("jysample"));
                if (flag) {
                    sql = "insert into pexam_specimen_relation(specimenid,specimen_itemid,jyitemid,jysample,pexamid,itemname) values (?,?,?,?,?,?)";
                    String uuid = UuidUtil.getUuid();
                    db.excute(sql, new Object[]{uuid, map0.get(list.get(i).get("jysample")), list.get(i).get("itemcode"), list.get(i).get("jysample"), pexamid, list.get(i).get("itemname")});
                } else {
                    sql = "select seq_jyitemid.nextval from dual";
                    Map<String, Double> seq_id = (Map<String, Double>) db.findOne(sql);
                    seq_jyitemid = Integer.toString(seq_id.get("nextval").intValue());
                    map0.put(list.get(i).get("jysample"), seq_jyitemid);
                    sql = "insert into pexam_specimen_relation(specimenid,specimen_itemid,jyitemid,jysample,pexamid,itemname) values (?,?,?,?,?,?)";
                    String uuid = UuidUtil.getUuid();
                    db.excute(sql, new Object[]{uuid, seq_jyitemid, list.get(i).get("itemcode"), list.get(i).get("jysample"), pexamid, list.get(i).get("itemname")});
                }
            }
            sql = "select t.specimen_itemid,t.itemname from pexam_specimen_relation t where t.pexamid=? order by specimen_itemid";
            templist = db.find(sql, new Object[]{pexamid});
            //System.out.println("条形码序列***==========>"+JSONArray.fromObject(templist).toString());
            pw = response.getWriter();
            pw.print(JSONArray.fromObject(templist).toString());
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }

    }

    @RequestMapping("/searchExamidBySearch")
    public void searchExamidBySearch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        String searchvalue = request.getParameter("searchvalue");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String sql = "";
        DBOperator db = null;
        List<Map> list = null;
        PrintWriter pw = null;
        try {
            sql = "select t.examid,m.examname from PEXAM_MANS t left join pexam_main m on t.examid=m.examid where" +
                    " t.hosnum =? and (t.idnum='" + searchvalue + "' or t.patname like '%" + searchvalue + "%' or t.pexamid='" + searchvalue + "' or t.ybbh='" + searchvalue + "' or t.inputcpy like '" + searchvalue + "%' or t.inputcwb like '" + searchvalue + "%')";
            db = new DBOperator();
            list = db.find(sql, new Object[]{hosnum});
            pw = response.getWriter();
            pw.print(JSONArray.fromObject(list).toString());
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
    }

    @RequestMapping(value = "/getCheckPexam", method = RequestMethod.POST)
    public void getCheckPexam(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        //System.out.println("getCheckPexam");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        //System.out.println("hosnum:"+hosnum);
        String sql = "";
        DBOperator db = null;
        List<Map> list = null;
        PrintWriter pw = null;
        try {
            sql = "select c.comname,c.comclass,c.forsex from pexam_items_group a, PEXAM_Items_groupDetails b, PEXAM_ITEMS_COM c where a.groupid = b.groupid and b.itemcode =c.comid " +
                    " and a.groupname='退休人员体检' and c.comclass = '检查' and  a.hosnum ='" + hosnum + "'";
            db = new DBOperator();
            list = db.find(sql);
            pw = response.getWriter();
            pw.print(JSONArray.fromObject(list).toString());
            //System.out.println(JSONArray.fromObject(list).toString());
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
    }

    //自定义体检预约单时间
    @SuppressWarnings("unchecked")
    @RequestMapping("/doPrintTime")
    public ModelAndView doPrintTime(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        String type = request.getParameter("type");
        modelMap.put("type", type);
        return new ModelAndView("pexam/printReservationTime", modelMap);

    }

    /**
     * 查询体检人在疾病统计表中是否有数据
     */
    @RequestMapping(value = "/selectDisease", method = RequestMethod.POST)
    public void selectDisease(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String pexamid = request.getParameter("pexamid");
        ReturnValue returnValue = new ReturnValue();
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "";
            List list = null;
            //	System.out.println("查找在检和未检的人员信息及在检人员的完成情况");
            sql = "select count(*) as count from PEXAM_DEPTSUM a ,pexam_main b where a.examid = b.examid and a.hosnum=? and b.nodecode=? and a.pexamid=? and a.DISPRESSION is not null ";
            Map<String, Integer> countMap = (Map<String, Integer>) db.findOne(sql, new Object[]{hosnum, nodecode, pexamid});
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

    @RequestMapping(value = "/getDictList")
    public void getDictList(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String code = request.getParameter("code");
            List<Map> list = null;
            String sql = "select * from pexam_sugests s where s.hosnum = ? and (instr(lower(s.pybm),?)>0 or instr(s.classname,?)>0) order by length(s.sn)";
            list = db.find(sql, new Object[]{hosnum, code.toLowerCase(), code}, 20);
            if (list.size() <= 0) {
                list = db.find(sql, new Object[]{"0000", code.toLowerCase(), code}, 20);
            }
            JSONArray jsons = JSONArray.fromObject(list);
            //System.out.println(jsons.toString());
            pw.print(jsons.toString());

            db.commit();
        } catch (Exception e) {
            db.rollback();
            pw.print("fail");
            e.printStackTrace();
        } finally {
            try {
                db.freeCon();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /*
	 * 疾病类型
	 */
    @RequestMapping("/getDiseaseInfo")
    public void getDiseaseInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String deptCode = basDept.getDeptcode();

        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            List list = null;
            String sql = "select a.* from bas_dicts a where a.nekey = '1900' and hosnum = '0000'  and a.nevalue !='!' order by a.nevalue";
            list = db.find(sql);
            JSONArray jsonArr = JSONArray.fromObject(list);
            pw.print(jsonArr.toString());
            //System.out.println(jsonArr.toString());
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            pw.print("fial");
            db.rollback();
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    /*
	 * 疾病建议
	 */
    @RequestMapping("/getDiseaseSugests")
    public void getDiseaseSugests(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String classname = request.getParameter("classname");
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            List list = null;
            String sql = "select a.* from PEXAM_SUGESTS a where a.classname = ? and a.hosnum = ? ";
            list = db.find(sql, new Object[]{classname, hosnum});
            if (list.size() <= 0) {
                sql = "select a.* from PEXAM_SUGESTS a where a.classname = ? and a.hosnum = '0000' ";
                list = db.find(sql, new Object[]{classname});
            }
            JSONArray jsonArr = JSONArray.fromObject(list);
            pw.print(jsonArr.toString());
            //System.out.println(jsonArr.toString());
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    //后台管理获取体检医院，服务中心树数据
    @RequestMapping(value = "/gethospitalsList2")
    public void gethospitalsList2(HttpServletRequest request, HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();

        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            //Bas_hospitals basHospital = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
            List<String> tree = new ArrayList<String>();
            String sql1 = "select * from bas_hospitals s where s.hosnum = '0000' and s.nodecode = '0000'";
            String sql2 = "select * from bas_hospitals a where  a.hosnum! = '0000' order by a.hosname";
            String sql4 = "select m.hosnum,m.nodecode,m.village from pexam_mans m where m.village is not null group by m.hosnum,m.nodecode,m.village";
            String temp = "";
            Bas_hospitals hos = (Bas_hospitals) db.findOne(sql1, Bas_hospitals.class);
            String id = hos.getHosnum();
            String name = hos.getHosname();
            String hs;
            String pid;
            temp = "{id:\"" + id + "\"," + "pId:\"" + "" + "\",name:\"" + name + "\",open:true}";
            tree.add(temp);
			/*
			 * 加载服务站
			 */
//			String sql1 = "select *from pexam_sugests s where s.hosnum=? and s.nodecode=? order by s.sugestid,s.sn ";
            List<Bas_hospitals> treelist = db.find(sql2, Bas_hospitals.class);
            for (Bas_hospitals li : treelist) {
                hs = li.getHosnum();
                id = li.getNodecode();
                name = li.getHosname();
                pid = li.getSupunit();
                //System.out.println(id+";"+name+";"+pid+"--------------------------");
                //\代表引号有意义
                //temp = "{id:" + id + ",pId:" + pid + ",name:\"" + name+ "\",mid:\"" + id + "\"}";// 子节点
                temp = "{id:\"" + id + "\"," + "pId:\"" + pid + "\",name:\"" + name + "\",open:false}";
                //  "{id:\"A001" +jgdm + "\"," + "pId:\"A01"+opt_type+"\",name:\""+jgname+"\",open:false}";
                tree.add(temp);
            }

            List<Pexam_mans> list = db.find(sql4, Pexam_mans.class);
            for (Pexam_mans pp : list) {
                String village = pp.getVillage();
                pid = pp.getNodecode();
//				System.out.println("village=================="+village);
                temp = "{id:\"" + village + "\"," + "pId:\"" + pid + "\",name:\"" + village + "\",open:false}";
                tree.add(temp);
            }
            JSONArray jsons = JSONArray.fromObject(tree);
            //System.out.println(jsons.toString());
            //request.setAttribute("treeNodes", "var treeNodes="+ jsons.toString());
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

    //	//体检总人数查询 后台管理点击树节点 加载iframe
//	@RequestMapping(value = "/pexamVillageCount")
//	public String pexamVillageCount(HttpServletRequest request,HttpServletResponse response)throws Exception{
//		response.setContentType("text/html;charset=utf-8");
//		Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
//		String hosnum = basHospitals.getHosnum();
//		String nodecode =basHospitals.getNodecode();
//		String nodeid=request.getParameter("nodeid");//节点id
//		String nodepid=request.getParameter("nodepid");//父节点id
//		String nodetype=request.getParameter("nodetype");//节点类型
// 		String sql=null;
// 		PrintWriter pw = null;
// 		ReturnValue returnValue = new ReturnValue();
//		DBOperator db = null;
//		List<Map> suglist=new ArrayList<Map>();
//		try {
//			pw = response.getWriter();
//			db = new DBOperator();
//			String startSql = "select count(*)as count ,sum(checked)as hascount,sum(counts)as atcount,sum(countd)as allcount from (";
//			String endSql = ")";
//			sql = "select a.village,a.examtype,(select count(v.village) from pexam_mans v where v.village = a.village and v.hosnum = a.hosnum  and v.examtype = a.examtype and v.bdate is not null"+
//				  " and EXISTS (select d.pexamid from pexam_deptsum d where v.pexamid = d.pexamid)) checked,(select count(v.village)"+
//				  " from pexam_mans v where v.village = a.village and v.hosnum = a.hosnum and v.examtype = a.examtype and v.bdate is not null) counts , (select count(v.village) from pexam_mans v  where v.village = a.village and v.hosnum = a.hosnum and v.examtype = a.examtype) countd" +
//				  " from pexam_mans a where a.hosnum = ? and a.nodecode = ? and a.village is not null";
//			sql+=" group by a.hosnum, a.examtype, a.village order by a.examtype";
//			sql = startSql + sql + endSql;
//			Map<String, Integer> countMap =null;
//			if("0000".equals(nodepid)){
//				countMap = (Map<String, Integer>) db.findOne(sql,new Object[]{nodeid,nodeid});
//				System.out.println("countMap.toString="+countMap.toString());
//			}else if("7007".equals(nodepid)){
//				countMap = (Map<String, Integer>) db.findOne(sql,new Object[]{nodepid,nodeid});
//			}
//			returnValue.setStatus(true);
//			returnValue.setValue(String.valueOf(JSONObject.fromObject(countMap).toString()));
//			System.out.println(JSONObject.fromObject(countMap).toString());
//
//			//System.out.println("-------------------"+suglist.size());
//
//			db.commit();
//			//System.out.println(jsons.toString());
//		} catch (Exception e) {
//			e.printStackTrace();
//			db.rollback();
//			returnValue.setStatus(false);
//			returnValue.setMessage("查询总数失败！");
//		}finally{
//			db.freeCon();
//		}
//		request.setAttribute("count",JSONObject.fromObject(returnValue).toString());
//		request.setAttribute("nodeid", nodeid);
//		request.setAttribute("nodepid", nodepid);
//		request.setAttribute("nodetype",nodetype);
//		return "pexam/pexamVillageList";
//	}
//
    //体检总人数查询 后台管理点击树节点 加载iframe
    @RequestMapping(value = "/pexamSearchVillageCount")
    public void pexamSearchVillageCount(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String nodeid = request.getParameter("nodeid");//节点id
        String nodepid = request.getParameter("nodepid");//父节点id
        String nodetype = request.getParameter("nodetype");//节点类型
        String village = URLDecoder.decode(request.getParameter("village"), "utf-8");
        String examtype = URLDecoder.decode(request.getParameter("examtype"), "utf-8");
        String sql = null;
        PrintWriter pw = null;
        ReturnValue returnValue = new ReturnValue();
        DBOperator db = null;
        List<Map> suglist = new ArrayList<Map>();
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String startSql = "select count(*)as count ,sum(checked)as hascount,sum(counts)as atcount,sum(countd)as allcount from (";
            String endSql = ")";
            sql = "select a.village,a.examtype,(select count(v.village) from pexam_mans v where v.village = a.village and v.hosnum = a.hosnum  and v.examtype = a.examtype and v.bdate is not null" +
                    " and EXISTS (select d.pexamid from pexam_deptsum d where v.pexamid = d.pexamid)) checked,(select count(v.village)" +
                    " from pexam_mans v where v.village = a.village and v.hosnum = a.hosnum and v.examtype = a.examtype and v.bdate is not null) counts , (select count(v.village) from pexam_mans v  where v.village = a.village and v.hosnum = a.hosnum and v.examtype = a.examtype) countd" +
                    " from pexam_mans a where a.hosnum = ? and a.nodecode = ? and a.village is not null";
            if (!"".equals(examtype) && examtype != null) {
                sql += " and a.examtype = '" + examtype + "'";
            }
            if (!"".equals(village) && village != null) {
                sql += " and a.village = '" + village + "'";
            }
            sql += " group by a.hosnum, a.examtype, a.village order by a.examtype";
            sql = startSql + sql + endSql;
            Map<String, Integer> countMap = null;
            if ("0000".equals(nodepid)) {
                countMap = (Map<String, Integer>) db.findOne(sql, new Object[]{nodeid, nodeid});
            } else if ("7007".equals(nodepid)) {
                countMap = (Map<String, Integer>) db.findOne(sql, new Object[]{nodepid, nodeid});
            }
            returnValue.setStatus(true);
            returnValue.setValue(String.valueOf(JSONObject.fromObject(countMap).toString()));
            //System.out.println(JSONObject.fromObject(countMap).toString());
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

    //体检总人数查询 后台管理点击树节点 加载iframe
    @RequestMapping(value = "/pexamVillageList", method = RequestMethod.POST)
    public void pexamVillageList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String nodeid = request.getParameter("nodeid");//节点id
        String nodepid = request.getParameter("nodepid");//父节点id
        String nodetype = request.getParameter("nodetype");//节点类型
        String village = URLDecoder.decode(request.getParameter("village"), "utf-8");
        String examtype = URLDecoder.decode(request.getParameter("examtype"), "utf-8");
        int pageIndex = Integer.parseInt(request.getParameter("index").toString()); // 分页索引
        int pageItems = Integer.parseInt(request.getParameter("size").toString()); // 每页数量
        String sql = null;
        PrintWriter pw = null;
        DBOperator db = null;
        List<Map> suglist = new ArrayList<Map>();
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String pagingSql1 = "select OHYEAH.* from (select OHNO.*,rownum no from ("; // 用于分页段1
            String pagingSql2 = ") OHNO where rownum <= ?) OHYEAH where no > ?"; // 用于分页段2
            sql = "select a.village,a.examtype,(select count(v.village) from pexam_mans v where v.village = a.village and v.hosnum = a.hosnum  and v.examtype = a.examtype and v.bdate is not null" +
                    " and EXISTS (select d.pexamid from pexam_deptsum d where v.pexamid = d.pexamid)) checked,(select count(v.village)" +
                    " from pexam_mans v where v.village = a.village and v.hosnum = a.hosnum and v.examtype = a.examtype and v.bdate is not null)nochecked,(select count(v.village) from pexam_mans v where v.village = a.village and v.hosnum = a.hosnum  and v.examtype = a.examtype ) counts from pexam_mans a where a.hosnum = ? and a.nodecode = ? and a.village is not null";
            if (!"".equals(examtype) && examtype != null) {
                sql += " and a.examtype = '" + examtype + "'";
            }
            if (!"".equals(village) && village != null) {
                sql += " and a.village = '" + village + "'";
            }
            sql += " group by a.hosnum, a.examtype, a.village order by a.examtype";
            sql = pagingSql1 + sql + pagingSql2;
            if ("0000".equals(nodepid)) {
                suglist = db.find(sql, new Object[]{nodeid, nodeid, pageIndex * pageItems + pageItems, pageIndex * pageItems});
            } else if ("7007".equals(nodepid)) {
                suglist = db.find(sql, new Object[]{nodepid, nodeid, pageIndex * pageItems + pageItems, pageIndex * pageItems});
            }
            //System.out.println("-------------------"+suglist.size());
            JSONArray jsons = JSONArray.fromObject(suglist);
            pw.print(jsons.toString());
            db.commit();
            //System.out.println(jsons.toString());
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


    //体检总人数查询 后台管理点击树节点 加载iframe
    @RequestMapping(value = "/pexamCobmoType", method = RequestMethod.POST)
    public void pexamCobmoType(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String sql = null;
        PrintWriter pw = null;
        DBOperator db = null;
        List<Map> suglist = new ArrayList<Map>();
        try {
            pw = response.getWriter();
            db = new DBOperator();
            sql = "select * from bas_dicts c where c.nekey='1057' and c.nevalue !='!' and c.hosnum = '0000' ";
            suglist = db.find(sql);
            //System.out.println("-------------------"+suglist.size());
            JSONArray jsons = JSONArray.fromObject(suglist);
            pw.print(jsons.toString());
            db.commit();
            //System.out.println(jsons.toString());
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

    //体检人次查询
    @RequestMapping("/medicalstatistics")
    public String medicalstatistics(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        String nodeid = request.getParameter("nodeid");//节点id
        String nodepid = request.getParameter("nodepid");//父节点id
        String nodetype = request.getParameter("nodetype");//节点类型
        String status = request.getParameter("status");
        String s_date1 = request.getParameter("s_date1");
        String s_date2 = request.getParameter("s_date2");
        request.setAttribute("status", status);
        request.setAttribute("s_date1", s_date1);
        request.setAttribute("s_date2", s_date2);
        request.setAttribute("nodeid", nodeid);
        request.setAttribute("nodepid", nodepid);
        request.setAttribute("nodetype", nodetype);
        //System.out.println("status="+status);
        return "pexam/medicalStatistics";//medicalStatistics  TiJianYuHang
    }

    //各医院人次总条数
    @RequestMapping(value = "/medicalStatisticsCount", method = RequestMethod.POST)
    public void medicalStatisticsCount(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");
        String hosnum = request.getParameter("hosnum");
        String nodecode = request.getParameter("nodecode");
        String pexamtypes = request.getParameter("pexamtypes");
        String pexamtype = URLDecoder.decode(request.getParameter("pexamtype"), "utf-8");
        ReturnValue returnValue = new ReturnValue();
        DBOperator db = new DBOperator();
        try {
            String sql1 = "select count(*)as count ,sum(county)as hascount,sum(counta)as atcount,sum(countd)as allcount from (";
            String sql2 = ") OHNO";
            String sql = "select a.hosnum,a.hosname, a.nodecode,b.examtype,(select count(*) from pexam_mans t where a.hosnum = t.hosnum and t.examtype = b.examtype and t.bdate is not null and EXISTS (select d.pexamid from pexam_deptsum d" +
                    " where t.pexamid = d.pexamid)) as county,(select count(*) from pexam_mans t where a.hosnum = t.hosnum and t.examtype = b.examtype and t.bdate is not null) as counta , (select count(*) from pexam_mans t where a.hosnum = t.hosnum and t.examtype = b.examtype ) as countd";

            sql += " from bas_hospitals a left join pexam_mans b on a.hosnum = b.hosnum where a.hosnum != '0000'  and a.supunit = '7007' "; //排除卫生局
            String str[] = pexamtypes.split(",");
//			for(int i=0;i<str.length;i++){//根据体检类型和bdate不为空统计数据
//				sql+=" sum(case when t.examtype='"+str[i]+"' and t.bdate is not null ";
            if (!starttime.equals("") && starttime != null) {
                sql += " and to_char(b.bdate,'yyyy-mm-dd') >= '" + starttime + "' ";
            }
            if (!endtime.equals("") && endtime != null) {
                sql += " and to_char(b.bdate,'yyyy-mm-dd') <= '" + endtime + "' ";
            }
//				sql+=" then 1 else 0 end)  as "+str[i]+",";
//			}
            //sql=sql.substring(0,sql.length()-1);

            if (!hosnum.equals("") && hosnum != null) {
                sql += " and a.hosnum='" + hosnum + "' ";
            }
            if (!nodecode.equals("") && nodecode != null) {
                sql += " and a.nodecode='" + nodecode + "' ";
            }
            if (!pexamtype.equals("") && pexamtype != null) {
                sql += " and b.examtype='" + pexamtype + "' ";
            }
            sql += " group by a.hosnum,a.hosname,a.nodecode,b.examtype";
            String sqlStr = sql1 + sql + sql2;
            Map<String, Integer> countMap = (Map<String, Integer>) db.findOne(sqlStr, new Object[]{});
            db.commit();
//			JSONObject json =
            returnValue.setStatus(true);
            returnValue.setValue(String.valueOf(JSONObject.fromObject(countMap).toString()));
            //	System.out.println(JSONObject.fromObject(countMap).toString());
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
            returnValue.setStatus(false);
            returnValue.setMessage("查询总数失败！");
        } finally {
            db.freeCon();
        }
        PrintWriter pw = response.getWriter();
        pw.print(JSONObject.fromObject(returnValue).toString());
        pw.flush();
        pw.close();
    }

    //体检记录数据查询
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/medicalStatisticsInfo", method = RequestMethod.POST)
    public void medicalStatisticsInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        String hosnum = request.getParameter("hosnum");
        String nodecode = request.getParameter("nodecode");
        String pexamtypes = request.getParameter("pexamtypes");
        String starttime = request.getParameter("starttime");
        String endtime = request.getParameter("endtime");
        String pexamtype = URLDecoder.decode(request.getParameter("pexamtype"), "utf-8");
        int pageIndex = Integer.parseInt(request.getParameter("index").toString()); // 分页索引
        int pageItems = Integer.parseInt(request.getParameter("size").toString()); // 每页数量
        DBOperator db = new DBOperator();
        try {
            String pagingSql1 = "select OHYEAH.* from (select OHNO.*,rownum no from ("; // 用于分页段1
            String pagingSql2 = ") OHNO where rownum <= ?) OHYEAH where no > ?"; // 用于分页段2
            String sql = "select a.hosnum,a.hosname, a.nodecode,b.examtype,(select count(*) from pexam_mans t where a.hosnum = t.hosnum and t.examtype = b.examtype and t.bdate is not null and EXISTS (select d.pexamid from pexam_deptsum d" +
                    " where t.pexamid = d.pexamid)) as county,(select count(*) from pexam_mans t where a.hosnum = t.hosnum and t.examtype = b.examtype and t.bdate is not null) as countn,(select count(*) from pexam_mans t where a.hosnum = t.hosnum and t.examtype = b.examtype) as counta ";
            String str[] = pexamtypes.split(",");
            sql += " from bas_hospitals a left join pexam_mans b on a.hosnum = b.hosnum where a.hosnum != '0000'  and a.supunit = '7007' "; //排除卫生局
//			for(int i=0;i<str.length;i++){//根据体检类型和bdate不为空统计数据
//				sql+=" sum(case when t.examtype='"+str[i]+"' and t.bdate is not null ";
            if (!starttime.equals("") && starttime != null) {
                sql += " and to_char(b.bdate,'yyyy-mm-dd') >= '" + starttime + "' ";
            }
            if (!endtime.equals("") && endtime != null) {
                sql += " and to_char(b.bdate,'yyyy-mm-dd') <= '" + endtime + "' ";
            }
//				sql+=" then 1 else 0 end)  as "+str[i]+",";
//			}
//			sql=sql.substring(0,sql.length()-1);

            if (!hosnum.equals("") && hosnum != null) {
                sql += " and a.hosnum='" + hosnum + "' ";
            }
            if (!nodecode.equals("") && nodecode != null) {
                sql += " and a.nodecode='" + nodecode + "' ";
            }
            if (!pexamtype.equals("") && pexamtype != null) {
                sql += " and b.examtype='" + pexamtype + "' ";
            }
            sql += " group by a.hosnum,a.hosname,a.nodecode,b.examtype";
            sql = pagingSql1 + sql + pagingSql2;
            List list = db.find(sql, new Object[]{pageIndex * pageItems + pageItems, pageIndex * pageItems});
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

    //加载医院名称combo
    @RequestMapping("/showhosname")
    public void showHosname(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "select a.inputcpy,a.inputcwb,a.hosnum,a.nodecode,a.hosname from bas_hospitals a "
                    + " where a.supunit = '7007' group by a.inputcpy,a.inputcwb,a.hosnum,a.nodecode,a.hosname";
            List list = db.find(sql, new Object[]{});
            JSONArray jsons = JSONArray.fromObject(list);
            pw.print("var threehosnamelist=" + jsons.toString());
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }

    }

    //体检人次查询
    @RequestMapping("/pexamPersonview")
    public String pexamPersonview(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        String nodeid = nodeid = URLDecoder.decode(request.getParameter("nodeid"), "utf-8");//节点id
        String nodepid = request.getParameter("nodepid");//父节点id
        String nodetype = request.getParameter("nodetype");//节点类型
        request.setAttribute("nodeid", nodeid);
        request.setAttribute("nodepid", nodepid);
        request.setAttribute("nodetype", nodetype);
        return "pexam/pexamPersonList";
    }

    //体检总人数查询 后台管理点击树节点 加载iframe
    @RequestMapping(value = "/pexamPersonCount")
    public void pexamPersonCount(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String nodeid = URLDecoder.decode(request.getParameter("nodeid"), "utf-8");//节点id
        String nodepid = request.getParameter("nodepid");//父节点id
        String nodetype = request.getParameter("nodetype");//节点类型
        String patname = URLDecoder.decode(request.getParameter("patname"), "utf-8");
        String examtype = URLDecoder.decode(request.getParameter("examtype"), "utf-8");
        String sql = null;
        PrintWriter pw = null;
        ReturnValue returnValue = new ReturnValue();
        DBOperator db = null;
        List<Map> suglist = new ArrayList<Map>();
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String startSql = "select count(*) as count from (";
            String endSql = ")";
            sql = "select * from pexam_mans a where a.bdate is not null and EXISTS (select d.pexamid from pexam_deptsum d where a.pexamid = d.pexamid) and a.village = ? and a.nodecode =?";
            if (!"".equals(examtype) && examtype != null) {
                sql += " and a.examtype = '" + examtype + "'";
            }
            if (!"".equals(patname) && patname != null) {
                sql += " and a.patname = '" + patname + "'";
            }
            sql += "  order by a.patname";
            sql = startSql + sql + endSql;
            Map<String, Integer> countMap = null;
            countMap = (Map<String, Integer>) db.findOne(sql, new Object[]{nodeid, nodepid});
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

    //体检总人数查询 后台管理点击树节点 加载iframe
    @RequestMapping(value = "/pexamPersonList", method = RequestMethod.POST)
    public void pexamPersonList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String nodeid = nodeid = URLDecoder.decode(request.getParameter("nodeid"), "utf-8");//节点id
        String nodepid = request.getParameter("nodepid");//父节点id
        String nodetype = request.getParameter("nodetype");//节点类型
        String patname = URLDecoder.decode(request.getParameter("patname"), "utf-8");
        String examtype = URLDecoder.decode(request.getParameter("examtype"), "utf-8");
        int pageIndex = Integer.parseInt(request.getParameter("index").toString()); // 分页索引
        int pageItems = Integer.parseInt(request.getParameter("size").toString()); // 每页数量
        String sql = null;
        PrintWriter pw = null;
        DBOperator db = null;
        List<Map> suglist = new ArrayList<Map>();
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String pagingSql1 = "select OHYEAH.* from (select OHNO.*,rownum no from ("; // 用于分页段1
            String pagingSql2 = ") OHNO where rownum <= ?) OHYEAH where no > ?"; // 用于分页段2
            sql = "select a.examid,a.hosnum,a.patname,a.sex,trunc(months_between( sysdate,a.dateofbirth)/12) as age, "
                    + "decode(a.idtype,'身份证',a.idnum,'')as idnum ,a.phonecall,a.examtype,a.pexamid,to_char(a.bdate,'yyyy-mm-dd') as bdate ,a.village,a.address "
                    + " from pexam_mans a where a.bdate is not null and EXISTS (select d.pexamid from pexam_deptsum d where a.pexamid = d.pexamid) and a.village = ? and a.nodecode =?";
            if (!"".equals(examtype) && examtype != null) {
                sql += " and a.examtype = '" + examtype + "'";
            }
            if (!"".equals(patname) && patname != null) {
                sql += " and a.patname = '" + patname + "'";
            }
            sql += "  order by a.patname";
            sql = pagingSql1 + sql + pagingSql2;
            suglist = db.find(sql, new Object[]{nodeid, nodepid, pageIndex * pageItems + pageItems, pageIndex * pageItems});
            //System.out.println("-------------------"+suglist.size());
            JSONArray jsons = JSONArray.fromObject(suglist);
            pw.print(jsons.toString());
            //System.out.println("=======>"+jsons.toString());
            db.commit();
            //System.out.println(jsons.toString());
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

    //--------------------------农民体检疾病统计 徐闯 2013-04-15------------------------
    //农民体检参合情况 后台管理点击树节点 加载iframe
    @RequestMapping(value = "/pexamVillageCountCH")
    public String pexamVillageCountCH(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String nodeid = request.getParameter("nodeid");//节点id
        String nodepid = request.getParameter("nodepid");//父节点id
        String nodetype = request.getParameter("nodetype");//节点类型
        String sql = null;
        PrintWriter pw = null;
        ReturnValue returnValue = new ReturnValue();
        DBOperator db = null;
        List<Map> suglist = new ArrayList<Map>();
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String startSql = "select count(*) as count from (";
            String endSql = ")";
            sql = "select a.village,a.examtype,(select count(v.village) from pexam_mans v where v.village = a.village and v.hosnum = a.hosnum  and v.examtype = a.examtype and v.bdate is not null" +
                    " and EXISTS (select d.pexamid from pexam_deptsum d where v.pexamid = d.pexamid)) checked,(select count(v.village) from pexam_mans v where v.village = a.village" +
                    " and v.hosnum = a.hosnum and v.examtype = a.examtype and v.bdate is not null and not EXISTS (select d.pexamid from pexam_deptsum d where v.pexamid = d.pexamid)) nochecked,(select count(v.village)" +
                    " from pexam_mans v where v.village = a.village and v.hosnum = a.hosnum and v.examtype = a.examtype and v.bdate is not null) counts from pexam_mans a where a.hosnum = ? and a.nodecode = ? and a.village is not null";
            sql += " group by a.hosnum, a.examtype, a.village order by a.examtype";
            sql = startSql + sql + endSql;
            Map<String, Integer> countMap = null;
            if ("0000".equals(nodepid)) {
                countMap = (Map<String, Integer>) db.findOne(sql, new Object[]{nodeid, nodeid});
            } else if ("7007".equals(nodepid)) {
                countMap = (Map<String, Integer>) db.findOne(sql, new Object[]{nodepid, nodeid});
            }
            returnValue.setStatus(true);
            returnValue.setValue(String.valueOf(countMap.get("count")));

            //System.out.println("-------------------"+suglist.size());

            db.commit();
            //System.out.println(jsons.toString());
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            returnValue.setStatus(false);
            returnValue.setMessage("查询总数失败！");
        } finally {
            db.freeCon();
        }
        request.setAttribute("count", JSONObject.fromObject(returnValue).toString());
        request.setAttribute("nodeid", nodeid);
        request.setAttribute("nodepid", nodepid);
        request.setAttribute("nodetype", nodetype);
        return "pexam/pexamVillageListCH";
    }


    //农民体检疾病统计情况 后台管理点击树节点 加载iframe
    @RequestMapping(value = "/pexamVillageCountPS")
    public String pexamVillageCountPS(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String nodeid = request.getParameter("nodeid");//节点id
        String nodepid = request.getParameter("nodepid");//父节点id
        String nodetype = request.getParameter("nodetype");//节点类型
        String sql = null;
        PrintWriter pw = null;
        ReturnValue returnValue = new ReturnValue();
        DBOperator db = null;
        List<Map> suglist = new ArrayList<Map>();
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String startSql = "select count(*) as count from (";
            String endSql = ")";
            sql = "select a.village,a.examtype,(select count(v.village) from pexam_mans v where v.village = a.village and v.hosnum = a.hosnum  and v.examtype = a.examtype and v.bdate is not null" +
                    " and EXISTS (select d.pexamid from pexam_deptsum d where v.pexamid = d.pexamid)) checked,(select count(v.village) from pexam_mans v where v.village = a.village" +
                    " and v.hosnum = a.hosnum and v.examtype = a.examtype and v.bdate is not null and not EXISTS (select d.pexamid from pexam_deptsum d where v.pexamid = d.pexamid)) nochecked,(select count(v.village)" +
                    " from pexam_mans v where v.village = a.village and v.hosnum = a.hosnum and v.examtype = a.examtype and v.bdate is not null) counts from pexam_mans a where a.hosnum = ? and a.nodecode = ? and a.village is not null";
            sql += " group by a.hosnum, a.examtype, a.village order by a.examtype";
            sql = startSql + sql + endSql;
            Map<String, Integer> countMap = null;
            if ("0000".equals(nodepid)) {
                countMap = (Map<String, Integer>) db.findOne(sql, new Object[]{nodeid, nodeid});
            } else if ("7007".equals(nodepid)) {
                countMap = (Map<String, Integer>) db.findOne(sql, new Object[]{nodepid, nodeid});
            }
            returnValue.setStatus(true);
            returnValue.setValue(String.valueOf(countMap.get("count")));

            //System.out.println("-------------------"+suglist.size());

            db.commit();
            //System.out.println(jsons.toString());
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            returnValue.setStatus(false);
            returnValue.setMessage("查询总数失败！");
        } finally {
            db.freeCon();
        }
        request.setAttribute("count", JSONObject.fromObject(returnValue).toString());
        request.setAttribute("nodeid", nodeid);
        request.setAttribute("nodepid", nodepid);
        request.setAttribute("nodetype", nodetype);
        return "pexam/pexamVillageListPS";
    }

    //跳转显示参合的页面
    @RequestMapping(value = "/pexamDisease")
    public String pexamDisease(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
//		Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
//		String hosnum = basHospitals.getHosnum();
//		String nodecode =basHospitals.getNodecode();
//		String nodetype=request.getParameter("nodetype");//节点类型
        String nodeid = request.getParameter("nodeid");//节点id
        String nodepid = request.getParameter("nodepid");//父节点id
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String tablename = URLDecoder.decode(request.getParameter("tablename"), "utf-8");

        request.setAttribute("nodeid", nodeid);
        request.setAttribute("nodepid", nodepid);
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        request.setAttribute("tablename", tablename);
        return "pexam/pexamVillageDisease";
    }


    // 参合情况统计
    @RequestMapping(value = "/pexamDiseaseCount")
    public void pexamDiseaseCount(HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String nodeid = request.getParameter("nodeid");// 节点id
        String nodepid = request.getParameter("nodepid");// 父节点id
        String nodetype = request.getParameter("nodetype");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String flag = request.getParameter("flag");

        PrintWriter pw = null;
        DBOperator db = null;
        String hosname = null;
        List<Map> list = new ArrayList<Map>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date bdate = sdf.parse(startDate);
        Date edate = sdf.parse(endDate);
        String datesql = getDateSql(bdate, edate, "bdate");
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "select * from (select hos.hosnum,hos.nodecode,hos.hosname,y.a,y.b,y.c,y.d,y.e from (select s.hosnum,"
                    + "count(case when TRUNC(months_between(sysdate, s.dateofbirth) / 12) >= '0' and  TRUNC(months_between(sysdate, s.dateofbirth) / 12) <= '6' then '1' else null END) a,"
                    + "count(case when TRUNC(months_between(sysdate, s.dateofbirth) / 12) > '6' and TRUNC(months_between(sysdate, s.dateofbirth) / 12) <= '18' then '2' else  null END) b,"
                    + "count(case when TRUNC(months_between(sysdate, s.dateofbirth) / 12) > '18' and TRUNC(months_between(sysdate, s.dateofbirth) / 12) <= '45' then '3'  else null END) c,"
                    + "count(case when TRUNC(months_between(sysdate, s.dateofbirth) / 12) > '45' and TRUNC(months_between(sysdate, s.dateofbirth) / 12) <= '60' then '4' else null END) d,"
                    + "count(case when TRUNC(months_between(sysdate, s.dateofbirth) / 12) > '60' then '5' else null END) e "
                    + "FROM (select w.* from (select s.* from (select * from pexam_mans where "
                    + datesql
                    + ") s,(select examid from pexam_main where appointtype!='N') n where s.examid=n.examid) w,"
                    + "(select distinct pexamid from pexam_deptsum) dm where w.pexamid=dm.pexamid) s group by s.hosnum) y,(select hosnum,nodecode,hosname from bas_hospitals) hos where hos.hosnum=y.hosnum(+)) where hosnum=nodecode and hosnum!='0000'";

            String wsql = "select s.nodecode,s.hosname,w.a,w.b,w.c,w.d,w.e from (select x.* from (SELECT s.nodecode, count(case when TRUNC(months_between(sysdate, s.dateofbirth) / 12) >= '0' and "
                    + "TRUNC(months_between(sysdate, s.dateofbirth) / 12) <= '6' then '1' else null END) a, count(case when TRUNC(months_between(sysdate, s.dateofbirth) / 12) > '6' and "
                    + "TRUNC(months_between(sysdate, s.dateofbirth) / 12) <= '18' then '2' else null END) b, count(case when TRUNC(months_between(sysdate, s.dateofbirth) / 12) > '18' and "
                    + "TRUNC(months_between(sysdate, s.dateofbirth) / 12) <= '45' then '3' else null END) c,count(case when TRUNC(months_between(sysdate, s.dateofbirth) / 12) > '45' and "
                    + "TRUNC(months_between(sysdate, s.dateofbirth) / 12) <= '60' then '4' else null END) d,count(case when TRUNC(months_between(sysdate, s.dateofbirth) / 12) > '60' then "
                    + "'5' else null END) e FROM (select a.* from (select s.* from ( select * from pexam_mans where "
                    + datesql
                    + "and hosnum = '7007') s,(select * from pexam_main n where n.appointtype != 'N') n  where s.examid = n.examid) a,pexam_deptsum m where a.pexamid = m.pexamid) s GROUP BY s.nodecode) x,bas_hospitals s "
                    + "where x.nodecode = s.nodecode) w,(select nodecode,hosname from bas_hospitals where hosnum='7007') s where s.nodecode=w.nodecode(+)";

            String sql1 = "select * from(" + wsql + ") where nodecode=?";
            String sql2 = "select * from(" + sql + ") where hosnum=?";
            String sql3 = wsql + " order by nodecode desc";

            if ("scenter".equals(flag)) {
                if ("7007".equals(hosnum) && "7007".equals(nodecode)) {
                    list = db.find(sql3);
                } else if ("7007".equals(hosnum) && !"7007".equals(nodecode)) {
                    list = db.find(sql1, new Object[]{nodecode});
                } else {
                    list = db.find(sql2, new Object[]{nodecode});
                }
            } else {

                if ("7007".equals(nodepid)) {
                    list = db.find(sql1, new Object[]{nodeid});
                } else if ("0000".equals(nodepid)) {
                    if ("7007".equals(nodeid)) {
                        list = db.find(sql3);
                    } else {
                        list = db.find(sql2, new Object[]{nodeid});
                    }

                } else if ("0000".equals(nodeid)) {
                    list = db.find(sql);
                }
            }
            pw = response.getWriter();
            pw.write(JSONArray.fromObject(list).toString());
            //System.out.println(JSONArray.fromObject(list).toString());

            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }

    }


    /**
     * bdate:开始时间，edate:结束时间,str:(数据库总中的)日期字段
     */
    private String getDateSql(Date bdate, Date edate, String str) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        //edate=DateUtils.addMonths(edate, 1);
        //System.out.println("====getDateSql====="+fmt.format(bdate)+"====="+fmt.format(edate)+"==================");
        return " to_char(" + str + ",'YYYY-mm-dd') between '" + fmt.format(bdate) + "' AND '" + fmt.format(edate) + "'";
    }


    //体检疾病统计（负责跳转视图页面）
    @RequestMapping(value = "/pexamSumCount")
    public String pexamSumCount(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String nodeid = request.getParameter("nodeid");//节点id
        String nodepid = request.getParameter("nodepid");//父节点id
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String tablename = URLDecoder.decode(request.getParameter("tablename"), "utf-8");
        request.setAttribute("nodeid", nodeid);
        request.setAttribute("nodepid", nodepid);
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        request.setAttribute("tablename", tablename);
        if ("7007".equals(nodepid)) {
            DBOperator db = new DBOperator();
            Map<String, Integer> countMap = null;
            Map<String, Integer> hosnameMap = null;
            String sqlDeptsum = "select count(distinct pexamid) counts from (select m.*,s.nodecode from pexam_mans s,pexam_deptsum m where m.pexamid=s.pexamid(+)) where hosnum=? and nodecode=?";
            String sqlhosname = "select hosname from bas_hospitals where hosnum=? and nodecode=?";
            countMap = (Map<String, Integer>) db.findOne(sqlDeptsum, new Object[]{nodepid, nodeid});
            hosnameMap = (Map<String, Integer>) db.findOne(sqlhosname, new Object[]{nodepid, nodepid});
            request.setAttribute("counts", String.valueOf(countMap.get("counts")));
            request.setAttribute("hosname", String.valueOf(hosnameMap.get("hosname")));
            db.commit();
            db.freeCon();
            return "pexam/pexamDiseaSumFromStation";
        } else {
            return "pexam/pexamDiseasesSum";
        }

    }

    // 体检疾病统计
    @RequestMapping(value = "/pexamDiseasesCount")
    public void pexamDiseasesCount(HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String nodeid = request.getParameter("nodeid");// 节点id
        String nodepid = request.getParameter("nodepid");// 父节点id
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String examtype = request.getParameter("examtype");
        String flag = request.getParameter("flag");
        //System.out.println("examtype====" + examtype);
        String time = request.getParameter("time");
        //System.out.println("time====" + time);
        String agesql = "";
        String addSql = "";
        if ("06".equals(time)) {
            agesql = getAgeSql("0", "6", "a.dateofbirth");

        }
        if ("618".equals(time)) {
            agesql = getAgeSql("6", "18", "a.dateofbirth");

        }
        if ("1860".equals(time)) {
            agesql = getAgeSql("0", "60", "a.dateofbirth");

        }
        if ("60".equals(time)) {
            agesql = getAgeSql("61", "200", "a.dateofbirth");

        }
//		if ("全部".equals(time)) {
//			agesql = "and 1=1";
//
//		}

        if (!"全部".equals(examtype)) {
            addSql = " and a.examtype = '" + examtype + "' ";
        }

        PrintWriter pw = null;
        DBOperator db = null;
        String hosname = null;
        List<Map> list = new ArrayList<Map>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date bdate = sdf.parse(startDate);
        Date edate = sdf.parse(endDate);
        String datesql = getDateSql(bdate, edate, "examdate");
        String datesql2 = " and " + getDateSql(bdate, edate, "a.bdate");
        String datesql3 = " and " + getDateSql(bdate, edate, "a.bdate");

        try {
            pw = response.getWriter();
            db = new DBOperator();
            String bsql = "select * from (";
            String esql = ")where bhosnum=?";

            String sql = "select * from (select a.countmax,a.hosnum ahosnum,b.*,c.counthb from ( select s.hosnum, count(distinct s.pexamid) countmax "
                    + "from (select a.* from pexam_mans a where a.isover = '完成' "
                    + addSql
                    + datesql2
                    + agesql
                    + ") s group by hosnum) a, (select x.hosnum,count(distinct x.pexamid) countmin,"
                    + "count(case when x.distype = '高血压' then '1' else null end) gxy,"
                    + "count(case when x.distype = '糖尿病' then '1' else null end) tnb,"
                    + "count(case when x.distype = '高脂血症' then '1' else null end) gzxz,"
                    + "count(case when x.distype = '肝功能异常' then '1' else  null end) ggnyc,"
                    + "count(case when x.distype = '肾功能异常' then '1'  else null  end) sgnyc,"
                    + "count(case when x.distype = '恶性肿瘤' then '1' else  null end) exzl,"
                    + "count(case when x.distype = '良性肿瘤' then '1' else null end) lxzl,"
                    + "count(case when x.distype = '胆囊炎' then '1'  else null end) dny,"
                    + "count(case when x.distype = '泌尿生殖系统疾病' then '1' else null end) mnszxtjb,"
                    + "count(case when x.distype = '慢性阻塞性肺疾病' then '1'  else null end) mxzsxfjb,"
                    + "count(case when x.distype = '精神疾病' then  '1' else null end) jsjb,"
                    + "count(case when x.distype = '肺结核' then '1' else null end) fjh,"
                    + "count(case when x.distype = '脂肪肝' then '1' else null end) zfg,"
                    + "count(case when x.distype = '血常规异常' then '1' else null end) xcgyc,"
                    + "count(case when x.distype = '心脏疾病' then '1' else null end) xzjb,"
                    + "count(case when x.distype = '龋齿' then '1' else null end) qc,"
                    + "count(case when x.distype = '胆囊息肉' then '1' else null end) dnxr,"
                    + "count(case when x.distype = '肝内胆管结石' then '1' else null end) gndgjs,"

                    + "count(case when x.distype = '胆囊结石' then '1' else null end) dnjs,"
                    + "count(case when x.distype = '肝囊肿' then '1' else null end) gnz,"
                    + "count(case when x.distype = '胆固醇结晶' then '1' else null end) dgcjj,"
                    + "count(case when x.distype = '肝硬化' then '1' else null end) gyh,"
                    + "count(case when x.distype = '肝占位性病变' then '1' else null end) gzwxbb,"
                    + "count(case when x.distype = '肝脏血管瘤考虑' then '1' else null end) gzxgl,"

                    + "count(case when x.distype not in ('高血压','糖尿病','高脂血症','肝功能异常','肾功能异常','恶性肿瘤','良性肿瘤','胆囊炎',"
                    + "'泌尿生殖系统疾病', '精神疾病', '慢性阻塞性肺疾病', '肺结核','脂肪肝','血常规异常','心脏疾病','龋齿','胆囊息肉','肝内胆管结石',"
                    + "'胆囊结石','肝囊肿','胆固醇结晶','肝硬化','肝占位性病变','肝脏血管瘤考虑') then '1' else null end) qt "
                    + "from (select b.*, a.dateofbirth, a.examtype, m.examdate "
                    + "from pexam_mans a, pexam_diseasecounts b,(select distinct pexamid,examdate from pexam_deptsum) m "
                    + "where a.pexamid = b.pexamid  and b.pexamid = m.pexamid "
                    + addSql + datesql3 + agesql
                    + ") x group by x.hosnum) b,( select s.hosnum, count(distinct s.pexamid) counthb from (select b.*,a.dateofbirth,a.examtype from pexam_mans a,pexam_diseasecounts b "
                    + "where a.pexamid = b.pexamid "
                    + addSql + datesql3 + agesql
                    + ") y,pexam_results s  where y.pexamid = s.pexamid and s.indname like '乙肝%' group by s.hosnum)c where a.hosnum=b.hosnum(+)"
                    + " and a.hosnum=c.hosnum(+))a,(select hosnum bhosnum,hosname from bas_hospitals where supunit='0000')b where b.bhosnum=a.ahosnum(+)";

            String sqlx = "select * from (select a.*,b.hosname,b.nodecode bnodecode from (select a.countmax,b.counthb,c.* from (select s.nodecode,count(distinct s.pexamid) countmax from (select m.*,a.nodecode from pexam_deptsum m,pexam_mans a  where m.pexamid=a.pexamid and a.hosnum='7007' "

                    + addSql + datesql3 + agesql
                    + ") s group by s.hosnum,s.nodecode) a,(select y.nodecode,count(distinct s.pexamid) counthb from (select b.* from pexam_mans a,"
                    + "pexam_diseasecounts b,pexam_deptsum m where a.pexamid = b.pexamid and b.pexamid = m.pexamid and a.hosnum='7007' "

                    + addSql + datesql3 + agesql
                    + ") y,pexam_results s where y.pexamid = s.pexamid and s.indname like '乙肝%' group by s.hosnum,y.nodecode) b,"
                    + "(select x.nodecode,count(distinct x.pexamid) countmin,count(case  when x.distype = '高血压' then '1'  else null end) gxy,"
                    + "count(case when x.distype = '糖尿病' then '1' else null end) tnb,count(case when x.distype = '高脂血症' then '1' else null end) gzxz,"
                    + "count(case when x.distype = '肝功能异常' then '1' else null end) ggnyc,count(case when x.distype = '肾功能异常' then '1' else null end) sgnyc,"
                    + "count(case when x.distype = '恶性肿瘤' then '1' else null end) exzl,count(case when x.distype = '良性肿瘤' then '1' else null end) lxzl,"
                    + "count(case when x.distype = '胆囊炎' then '1' else null end) dny,count(case when x.distype = '泌尿生殖系统疾病' then '1' else null end) mnszxtjb,"
                    + "count(case when x.distype = '慢性阻塞性肺疾病' then '1' else null end) mxzsxfjb,count(case when x.distype = '精神疾病' then '1' else null end) jsjb,"
                    + "count(case when x.distype = '肺结核' then '1' else  null end) fjh,count(case when x.distype not in ('高血压','糖尿病','高脂血症','肝功能异常','肾功能异常','恶性肿瘤','良性肿瘤',"
                    + "'胆囊炎','泌尿生殖系统疾病','精神疾病','慢性阻塞性肺疾病','肺结核') then '1' else null end) qt "
                    + "from (select b.* from pexam_mans a, pexam_diseasecounts b, (select distinct pexamid, examdate from pexam_deptsum) m "
                    + "where a.pexamid = b.pexamid and b.pexamid = m.pexamid and a.hosnum='7007' "

                    + addSql
                    + datesql3
                    + agesql
                    + ") x group by x.nodecode) c where a.nodecode=b.nodecode(+) and a.nodecode=c.nodecode(+))a,"
                    + "(select nodecode,hosname from bas_hospitals where hosnum='7007')b where b.nodecode=a.nodecode(+)) where bnodecode=?";

            String sql2 = bsql + sql + esql;
            if ("scenter".equals(flag)) {
                if ("7007".equals(hosnum) && !"7007".equals(nodecode)) {
                    list = db.find(sqlx, new Object[]{nodecode});
                } else {
                    list = db.find(sql2, new Object[]{nodecode});
                }
            } else {


                if ("0000".equals(nodeid)) {
                    list = db.find(sql);
                }
                if ("0000".equals(nodepid)) {
                    list = db.find(sql2, new Object[]{nodeid});
                }
                if ("7007".equals(nodepid)) {
                    list = db.find(sqlx, new Object[]{nodeid});

                }
            }
            pw.write(JSONArray.fromObject(list).toString());
            //System.out.println(JSONArray.fromObject(list).toString());
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }

    }

    private String getAgeSql(String age1, String age2, String str) {
        return "and TRUNC(extract(year from sysdate)-extract(year from " + str + ")) >= " + age1
                + " and TRUNC(extract(year from sysdate)-extract(year from " + str + ")) <="
                + age2;
    }

    //体检类型combo数据查询
    @RequestMapping("/getcombo")
    public void getcombo(HttpServletRequest request,
                         HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        DBOperator db = null;
        PrintWriter pw = null;
        List<Map> list = new ArrayList<Map>();

        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "select d.contents from bas_dicts d where d.nekey='1057' and d.nevalue!='!'";
            list = db.find(sql);
            JSONArray arr = JSONArray.fromObject(list);
            //System.out.println("-----" + arr.toString() + "--------");
            pw.print(arr.toString());
            pw.flush();
            pw.close();
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.freeCon();
        }
    }


    //后台管理获取体检医院，服务中心树数据,不包括村
    @RequestMapping(value = "/gethospitalsList3")
    public void gethospitalsList3(HttpServletRequest request, HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();

        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            List<String> tree = new ArrayList<String>();
            String node = "{id:'0000', pid:'', name:'余杭区卫生局',open:true}";
            tree.add(node);
			/*
			 * 查询数据tree 数据
			 */
            String temp = "";
            String sql1 = "select * from bas_hospitals s where s.hosnum !='0000'";
            List<Bas_hospitals> treelist = db.find(sql1, Bas_hospitals.class);
            for (Bas_hospitals li : treelist) {
                String hs = li.getHosnum();
                String id = li.getNodecode();
                String name = li.getHosname();
                String pid = li.getSupunit();
                temp = "{id:\"" + id + "\"," + "pId:\"" + pid + "\",name:\"" + name + "\",open:false}";
                tree.add(temp);
            }

            JSONArray jsons = JSONArray.fromObject(tree);
            //System.out.println(jsons.toString());
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


    @RequestMapping(params = "method=getusers")
    public void load_operator(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        List<Bas_user> basUsers = null;

        DBOperator dbo = new DBOperator();
        try {
            String sql = "select * from bas_user where hosnum = ?";
            basUsers = dbo.find(sql, new Object[]{hosnum}, Bas_user.class);
            dbo.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            dbo.rollback();
        } finally {
            dbo.freeCon();
        }
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();

        StringBuilder sb = new StringBuilder("");
        sb.append("[");
        for (Bas_user basType : basUsers) {
            if (sb.length() != 1) {
                sb.append(",");
            }
            sb.append("{'nevalue':'");
            sb.append(basType.getId());
            sb.append("','contents':'");
            sb.append(basType.getName());
            sb.append("','inputcwb':'");
            sb.append(basType.getInput_cwb());
            sb.append("','inputcpy':'");
            sb.append(basType.getInput_cpy());
            sb.append("'}");
        }
        sb.append("]");
        pw.print(sb.toString());
        pw.flush();
        pw.close();
    }

    @RequestMapping(value = "/getVillages")
    public void getVillages(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            List<Map> list = null;
            String sql = "select m.village from pexam_mans m where m.hosnum = ? group by m.village order by m.village";
            list = db.find(sql, new Object[]{hosnum});
            JSONArray jsons = JSONArray.fromObject(list);
            //System.out.println(jsons.toString());
            //System.out.println("jsons.toString()=========="+jsons.toString());
            pw.print(jsons.toString());
            db.commit();
        } catch (Exception e) {
            db.rollback();
            pw.print("fail");
            e.printStackTrace();
        } finally {
            try {
                db.freeCon();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //	@RequestMapping(value = "/loadCommonResults")
//	public void loadCommonResults(HttpServletRequest request,HttpServletResponse response)throws Exception{
//		response.setCharacterEncoding("utf-8");
//		Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
//		String indid=request.getParameter("indid");
//		//int pageIndex = Integer.parseInt(request.getParameter("index").toString()); // 分页索引
//		//int pageItems = Integer.parseInt(request.getParameter("size").toString()); // 每页数量
//		String hosnum=basHospitals.getHosnum();
//		DBOperator db=null;
//		String sql="";
//		PrintWriter pw=null;
//		try{
//			db=new DBOperator();
//			//String pagingSql1 = "select OHYEAH.* from (select OHNO.*,rownum no from ("; // 用于分页段1
//			//String pagingSql2 = ") OHNO where rownum <= ?) OHYEAH where no > ?"; // 用于分页段2
//			//sql="select * from pexam_ind_result t where t.hosnum=? and t.indid=?";
//			//sql=pagingSql1+sql+pagingSql2;
//			//List list=db.find(sql, new Object[]{hosnum,indid,pageIndex * pageItems + pageItems, pageIndex * pageItems});
//			sql="select * from pexam_ind_result t where t.hosnum=? and t.indid=?";
//			List list=db.find(sql, new Object[]{hosnum,indid});
//			pw=response.getWriter();
//			JSONArray jsons = JSONArray.fromObject(list);
//			pw.print(jsons.toString());
//		}catch(Exception e){
//			db.rollback();
//			e.printStackTrace();
//		}finally{
//			db.freeCon();
//		}
//
//	}
//
    @RequestMapping(value = "/getIndSuggest")
    public void getIndSuggest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("utf-8");
        String pexamid = request.getParameter("pexamid");
        DBOperator db = null;
        String sql = "";
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            sql = "select * from ( select result,sugesttext,t.sn from pexam_items_sugests s,pexam_sugests t where s.sugestid=t.sugestid and s.pexamid=? and t.sugesttext is not null " +
                    " union  " +
                    " select s.classname,s.sugesttext,s.sn from pexam_results r , pexam_sugests s  where r.pexamid=? and r.examtype='检验' and r.unnormal is not null and  r.indid=s.indid and r.unnormal=s.unnormal" +
                    "  ) order by sn ";
            List<Map> list = db.find(sql, new Object[]{pexamid, pexamid});
            pw = response.getWriter();
            JSONArray jsons = JSONArray.fromObject(list);
            pw.print(jsons.toString());
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }

    }

    @RequestMapping(value = "/getItemsType")
    public void getItemsType(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("utf-8");
        String pexamid = request.getParameter("pexamid");
        DBOperator db = null;
        String sql = "";
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            sql = "select a.itemname,a.excdeptname,b.location from pexam_items_title a,bas_dept b where a.pexamid = ? and a.comclass = '其他' and a.hosnum = b.hosnum and a.excdept = b.deptcode order by a.itemuuid";
            List<Map> list = db.find(sql, new Object[]{pexamid});
            pw = response.getWriter();
            JSONArray jsons = JSONArray.fromObject(list);
            //System.out.println("jsons.toString():"+jsons.toString());
            pw.print(jsons.toString());
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }

    }


    //-----------2013-04-27 慈溪版开始体检 直接与lis数据库进行交互--------------------
	/*
	 * 开始体检,包含了与本地lis数据的交换
	 */
    @RequestMapping("/startExamNew3")
    public void startExamNew3(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String hosnum = basHospitals.getHosnum();

        String pexamid = request.getParameter("pexamid");//唯一标识--个人体检编号
        String examid = request.getParameter("examid");//如果是个人体检的则该参数为”0000“

        DBOperator db = null;
        DBOperator db2 = null;
        PrintWriter pw = null;

        String errormsg = "fail";//返回的错误信息
        try {
            db = new DBOperator("appdb");
            pw = response.getWriter();
            int itemsNum = 0;
            String sql = "";
            String sql2 = "";
            List list = null;

            Map returnMap = new HashMap();
            //获取体检人的性别--过滤出有性别要求的体检项目
            sql = "select a.*,to_char(sysdate,'yyyy')-to_char(a.dateofbirth,'yyyy') as age from pexam_mans a where a.hosnum=? and a.pexamid=? and a.bdate is null";
            list = db.find(sql, new Object[]{hosnum, pexamid}, PexamMans.class);
            returnMap.put("pexamList", list);
            if (list != null && list.size() > 0) {//防止重复提交
                PexamMans pexamMan = (PexamMans) list.get(0);
                String sex = pexamMan.getSex();

                //System.out.println("更新pexam_mans表的体检开始标志字段");//此处体检日期可能要从前台传递过来
                sql = "update pexam_mans a set a.bdate=? where a.hosnum=? and a.pexamid=?";
                db.excute(sql, new Object[]{new Timestamp(new Date().getTime()), hosnum, pexamid});

                //System.out.println("获取个人相关的体检项目或套餐");//如果是团体的话pexamid为空
                sql = "select * from pexam_items a where (a.hosnum=? and a.examid=? and a.pexamid is null) or (a.hosnum=? and a.examid=? and a.pexamid=?)";
                list = db.find(sql, new Object[]{hosnum, examid, hosnum, examid, pexamid});
                if (list != null && list.size() > 0) {
                    String groupids = "";//收集所有套餐id
                    String itemcodes = "";//收集所有单独大项id
                    Map map0 = null;

                    //List<LisItem> lisItemsArr = new ArrayList<LisItem>();//保存上传给lis的数据
                    //LisItem lisItem = null;

                    List<Object[]> pi = new ArrayList<Object[]>();//存放体检项目参数
                    List<Object[]> pi2 = new ArrayList<Object[]>();//存放检验项目参数
                    for (int i = 0; i < list.size(); i++) {
                        map0 = (Map) list.get(i);
                        if ("y".equals(map0.get("isgroup"))) {//是否是套餐的标志
                            groupids += "'" + map0.get("itemid") + "',";
                        } else {
                            itemcodes += "," + map0.get("itemid") + "',";
                        }
                    }
                    //团体的体检
                    //获取套餐下的体检项目--此处未对套餐中有相同体检项目进行去重操作
                    if (groupids.length() > 0) {
                        groupids = groupids.substring(0, groupids.length() - 1);
                        //System.out.println("获取组成套餐的体检项目");//有性别要求的只需在此处加个性别条件就ok
                        //sql = "select a.*,b.groupid from pexam_items_def a ,pexam_items_groupdetails b where a.hosnum=b.hosnum and a.itemcode=b.itemcode and b.groupid in (?)".replace("?", groupids);
                        sql = "select b.*,a.groupid,c.groupname from pexam_items_groupdetails a,pexam_items_com b,pexam_items_group c where a.groupid in (?) and a.itemcode=b.comid and c.groupid=a.groupid and a.hosnum=b.hosnum and a.hosnum=c.hosnum ".replace("?", groupids);
                        sql += " and a.hosnum=?";
                        if (!"".equals(sex)) {
                            sql += " and (b.forsex='不限' or b.forsex='" + sex + "')";
                        }
                        list = db.find(sql, new Object[]{hosnum});
                        if (list != null && list.size() > 0) {
                            for (int i = 0; i < list.size(); i++) {
                                map0 = (Map) list.get(i);
                                String itemcode = (String) map0.get("comid");//组合项目id
                                String itemname = (String) map0.get("comname");//组合项目名称
                                String groupid = (String) map0.get("groupid");//套餐id
                                String groupname = (String) map0.get("groupname");//套餐名称
                                String itemuuid = new UUIDGenerator().generate().toString();
                                pi.add(new Object[]{hosnum, itemuuid, examid, pexamid, itemcode, itemname, map0.get("excdept"), map0.get("excdeptname"), groupid, groupname, map0.get("sn"), map0.get("comclass")});

                                if ("外送".equals(map0.get("comclass")) || "检验".equals(map0.get("comclass"))) {//如果是检验项目则把该项目传给lis
                                    String tjbh = pexamid;
                                    String xm = pexamMan.getPatname();
                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                    Date csrqdate = (Date) pexamMan.getDateofbirth();
                                    String csrq = "";
                                    if (csrqdate != null && !csrqdate.equals("")) {
                                        csrq = df.format(csrqdate);//出生日期
                                    }
                                    int fhbz = 0;
                                    String yyzhxmbh = itemcode;//体检系统里的项目组合comid
                                    String sbh = pexamMan.getYbbh();//农保编号
                                    Date sqrqdate = new Date();//申请日期
                                    SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String sqrq = df2.format(sqrqdate);


                                    String yyzhxmmc = itemname;//体检项目名称
                                    String zhxmbh = "";//组合项目编码

                                    //检验大项字典匹配转换
                                    String zhsql = null;
                                    Map ppMap = null;
                                    zhsql = "select * from pexam_lisrelcation r where r.hosnum=? and r.tjindid=? ";
                                    ppMap = (Map) db.findOne(zhsql, new Object[]{hosnum, itemcode});
                                    if (ppMap != null && !ppMap.isEmpty()) {
                                        zhxmbh = (String) ppMap.get("lisindid");
                                    }

                                    int mzh = 0;//门诊号
                                    pi2.add(new Object[]{tjbh, xm, sex, csrq, fhbz, yyzhxmbh, sbh, sqrq, yyzhxmmc, zhxmbh, mzh});
                                }
                            }
                        }
                    }

                    //个人的体检
                    if (itemcodes.length() > 0) {
                        itemcodes = itemcodes.substring(0, itemcodes.length() - 1);
                        //System.out.println("获取体检项目的相关信息");
                        sql = "select * from pexam_items_com a where a.comid in (?)".replace("?", itemcodes);
                        sql += " and a.hosnum=?";
                        if (!"".equals(sex)) {
                            sql += " and (a.forsex='不限' or a.forsex='" + sex + "')";
                        }
                        list = db.find(sql, new Object[]{hosnum});
                        if (list != null && list.size() > 0) {
                            for (int i = 0; i < list.size(); i++) {
                                map0 = (Map) list.get(i);
                                //System.out.println("=========="+map0);
                                String itemcode = (String) map0.get("comid");
                                String itemname = (String) map0.get("comname");
                                String itemuuid = new UUIDGenerator().generate().toString();
//								System.out.println(map0.get("excdept"));
                                pi.add(new Object[]{hosnum, itemuuid, examid, pexamid, itemcode, itemname, map0.get("excdept"), map0.get("excdeptname"), "", "", map0.get("sn"), map0.get("comclass")});
                                //	System.out.println(pi.size()+"-"+pi);
                                if ("外送".equals(map0.get("comclass")) || "检验".equals(map0.get("comclass"))) {//如果是检验项目则把该项目传给lis
                                    int length = pexamid.length();
                                    String tjbh = "";
                                    if (length <= 13) {
                                        for (int j = 0; j < 13 - length; j++) {
                                            tjbh += "0";
                                        }
                                    }

                                    tjbh = pexamid;
                                    String xm = pexamMan.getPatname();
                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
                                    Date csrqdate = (Date) pexamMan.getDateofbirth();
                                    String csrq = "";
                                    if (csrqdate != null && csrqdate.equals("")) {
                                        csrq = df.format(csrqdate);//出生日期
                                    }
                                    int fhbz = 0;
                                    String yyzhxmbh = itemcode;//可能是仪器号
                                    String sbh = itemuuid;//可能是申请编号 一个序列 项目惟一编号
                                    Date sqrqdate = new Date();//申请日期
                                    SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    String sqrq = df2.format(sqrqdate);
                                    String yyzhxmmc = pexamMan.getExamtype();//体检类型
                                    String zhxmbh = itemcode;//组合项目编码
                                    int mzh = 0;//门诊号
                                    pi2.add(new Object[]{tjbh, xm, sex, csrq, fhbz, yyzhxmbh, sbh, sqrq, yyzhxmmc, zhxmbh, mzh});

                                }
                            }
                        }
                    }

                    //插入pexam_items_title表
                    Object[][] params = new Object[pi.size()][2];
                    for (int i = 0; i < pi.size(); i++) {
                        params[i] = pi.get(i);
                    }
                    itemsNum = pi.size();
                    //System.out.println("插入具体要体检的体检项目");
                    sql = "insert into pexam_items_title (hosnum,itemuuid,examid,pexamid,itemcode,itemname,excdept,excdeptname,groupid,groupname,sn,comclass)values(?,?,?,?,?,?,?,?,?,?,?,?)";
                    db.excuteBatch(sql, params);

                    //插入lis程序的数据库（插入了lis系统才能打印条码）
                    if (pi2.size() > 0) { //是否含有检验项
                        String dbname = "cx" + hosnum;
                        db2 = new DBOperator(dbname);//调用的db名字
                        //查询此申请单是否申请过检验流程
                        sql2 = " select * from tjjkjysqxx t where t.tjbh=? ";
                        List list2 = new ArrayList();
                        list2 = db2.find(sql2, new Object[]{pexamid});
                        if (list2.size() > 0) {
                            errormsg = "申请信息已存在";
                            throw new Exception();
                        } else {
                            Object[][] params2 = new Object[pi2.size()][2];
                            for (int i = 0; i < pi2.size(); i++) {
                                params2[i] = pi2.get(i);
                            }
                            //itemsNum = pi2.size();
                            sql2 = "insert into tjjkjysqxx (tjbh,xm,xb,csrq,fhbz,yyzhxmbh,sbh,sqrq,yyzhxmmc,zhxmbh,mzh) values (?,?,?,?,?,?,?,?,?,?,?) ";
                            db2.excuteBatch(sql2, params2);
                        }
                    }

                }
            }
            returnMap.put("itemsNum", itemsNum);
//			returnMap.put("codePath",codePath);
            pw.print(JSONObject.fromObject(returnMap).toString());
            db.commit();
            if (db2 != null) {
                db2.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();

            if (db2 != null) {
                db2.rollback();
            }
            pw.print(errormsg);
        } finally {
            db.freeCon();
            if (db2 != null) {
                db2.freeCon();
            }
        }
        pw.flush();
        pw.close();
    }

    //---------------------------暂不使用--------------------------
	/*
	 * “体检医生站”或“总检医生站”--体检项目的树 慈溪版加载体检项目树 刷新结果表
	 */
    @RequestMapping(value = "/createTreeNew2", method = RequestMethod.GET)
    public void loadExamineTree_xj2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String hosnum = basHospitals.getHosnum();
        String deptCode = basDept.getDeptcode();

        String examid = request.getParameter("examid");//体检编号--如果为个人”0000“
        String pexamid = request.getParameter("pexamid");//体检号
        String method = request.getParameter("method");//体检医生站（doctorStation）、总检医生站（mainDoctorCheck）

        PrintWriter pw = null;
        DBOperator db = null;
        DBOperator db2 = null;
        try {
            pw = response.getWriter();
            db = new DBOperator("appdb");
            String dbname = "cx" + hosnum;
            db2 = new DBOperator(dbname);//调用的db名字
            String sql = "";
            String lissql = "";

            List list = null;
            List<Map> listdate = null;//lis数据
            String defaultSelectNodeId = "";

            //获取是否区分科室
            String isDishDept = ParamBuffer.getParamValue(request, basHospitals.getHosnum(), basHospitals.getNodecode(), "健康体检", "是否区分科室");

            if ("doctorStation".equals(method)) {//体检医生站
                if ("Y".equals(isDishDept)) {
                    sql = "select a.itemcode as comid,a.itemname as comname,a.groupid,a.itemuuid,a.excdeptname,a.comclass from pexam_items_title a where a.hosnum=? and a.pexamid=? and a.excdept=? order by a.sn";
                    list = db.find(sql, new Object[]{hosnum, pexamid, deptCode});
                } else {
                    sql = "select a.itemcode as comid,a.itemname as comname,a.groupid,a.itemuuid,a.excdeptname,a.comclass from pexam_items_title a where a.hosnum=? and a.pexamid=? order by a.sn";
                    list = db.find(sql, new Object[]{hosnum, pexamid});
                }
            } else if ("mainDoctorCheck".equals(method)) {//总检医生站
                sql = "select a.itemcode as comid,a.itemname as comname,a.groupid,a.itemuuid,a.excdeptname,a.comclass from pexam_items_title a where a.hosnum=? and a.pexamid=? order by a.sn";
                list = db.find(sql, new Object[]{hosnum, pexamid});
            }

            List<String> lstTree = new ArrayList<String>();
            int i = 1;
            String temp = "";
            String s1 = "{id:0, pId:-1, name:\"体检项目\" , open:true}";
            lstTree.add(s1);
            boolean isLoadLisData = false;//加载的项目中是否存在检验项目，存在true
            List<Map> jylist = new ArrayList();//存放检验项目的list
            if (list != null && list.size() > 0) {
                Map map0 = null;
                for (int j = 0; j < list.size(); j++) {
                    map0 = (Map) list.get(j);
                    String id = (String) map0.get("comid");//加载指标
                    String pid = "0";//现在只有两层结构
                    String name = (String) map0.get("comname");//组合名称
                    String itemuuid = (String) map0.get("itemuuid");//中间表id
                    String groupid = (String) map0.get("groupid");
                    String comclass = (String) map0.get("comclass");
                    String excdoctorname = (String) map0.get("excdoctorname") == null ? "" : (String) map0.get("excdoctorname");//体检医生--空代表为未检
                    temp = "{id:\"" + id + "\",pId:\"" + pid + "\",name:\"" + name + "\",itemuuid:\"" + itemuuid + "\",groupid:\"" + groupid + "\",excdoctorname:\"" + excdoctorname + "\",comclass:\"" + comclass + "\"}";
                    lstTree.add(temp);
                    if (j == 0) {
                        defaultSelectNodeId = id;
                    }
                    if ("检验".equals(comclass)) {//存在检验项目，将标志设置为true
                        if (excdoctorname == null || excdoctorname.equals("")) {//将未保存过的检验项目筛选出来
                            Map itemMap = new HashMap();//存放检验项目的map
                            itemMap.put("comid", id);
                            itemMap.put("comname", name);
                            itemMap.put("itemuuid", itemuuid);
                            jylist.add(itemMap);
                            isLoadLisData = true;
                        }
                    }

                }
            }

            if ("mainDoctorCheck".equals(method)) {
                String resultcheck = "{id:\"zjbg\",pId:\"" + 0 + "\",name:\"总检报告\"}";
                lstTree.add(resultcheck);
            }


            //存在检验，加载lis数据
            if (isLoadLisData) {

                lissql = "select * from tjjkhyxx x where x.tjbh=? ";
                listdate = db2.find(lissql, new Object[]{pexamid});

                List jydxinds = null;
                int lisnum = 0;//已更新某大项下的小项指标数量

                if (listdate != null && listdate.size() > 0) {//代表有该病人的lis数据

                    List<Object[]> del_pi = new ArrayList<Object[]>();//删除
                    List<Object[]> ins_pi = new ArrayList<Object[]>();//默认初始值插入
                    List<Object[]> inslis_pi = new ArrayList<Object[]>();//lis结果插入
                    List<Object[]> updlis_pi = new ArrayList<Object[]>();//更新 小项lis结果
                    List<Object[]> upd_pi = new ArrayList<Object[]>();//更新

                    //遍历检验项目的大项
                    for (int n = 0; n < jylist.size(); n++) {
                        Map projectMap = null;//每个组合项的数据
                        List<Map> indlist = null;//某组合项下小项的集合
                        Date excdate = null;
                        lisnum = 0;//初始化为0;

                        //所有要清空的大项结果
                        String comid = (String) jylist.get(n).get("comid");
                        String comname = (String) jylist.get(n).get("comname");
                        String itemuuid = (String) jylist.get(n).get("itemuuid");
                        del_pi.add(new Object[]{hosnum, itemuuid, pexamid});

                        //某检验大项下所有要插的小项
                        sql = "select d.* from pexam_items_ind d,pexam_items_comdet t where d.hosnum=t.hosnum and d.indid=t.indid and  d.hosnum=? and t.comid=?  ";
                        jydxinds = db.find(sql, new Object[]{hosnum, comid});
                        //Map comMap = null;
                        Map indMap = null;
							/*
							if(jydxinds!=null && jydxinds.size()>0){
							   for(int a=0;a<jydxinds.size();a++){
								    indMap=(Map) jydxinds.get(a);
								    String indid = (String)indMap.get("indid");
								    String indname = (String)indMap.get("indname");
									String resultunit = (String)indMap.get("resultunit");
									String result = (String)indMap.get("defaultv");
									String minval=(String)indMap.get("minval");
									String maxval=(String)indMap.get("maxval");
									String range = minval+"~"+maxval;

								    ins_pi.add(new Object[]{hosnum,examid,pexamid,result,comid,comname,indid,
											indname,"检验",itemuuid,indMap.get("sn"),range,resultunit});
							   }

							}
							*/

                        //更新lis程序中有结果的小项指标
                        sql = "select d.*,n.sn from pexam_items_comdet d left join pexam_items_ind n on d.hosnum=n.hosnum and d.indid=n.indid where  d.hosnum=? and d.comid=? ";
                        indlist = db.find(sql, new Object[]{hosnum, comid});

                        if (indlist != null && indlist.size() > 0) {
                            List<Object[]> inds_pi = new ArrayList<Object[]>();//小项标准id
                            for (int k = 0; k < indlist.size(); k++) {
                                String indid = (String) indlist.get(k).get("indid");
                                int sn = ((BigDecimal) indlist.get(k).get("sn")).intValue();
                                for (int m = 0; m < listdate.size(); m++) {
                                    Map lisindMap = new HashMap();
                                    lisindMap = listdate.get(m);
                                    String xmbh = (String) lisindMap.get("xmbh");//转换前小项id
                                    String xmmc = (String) lisindMap.get("xmmc");//转换前小项name
                                    String zhxmbh = "";
                                    String zhxmmc = "";

                                    //检验小项字典匹配转换
                                    String zhsql = null;
                                    Map ppMap = null;
                                    zhsql = "select * from pexam_lisrelcation r where r.hosnum=? and r.lisindid=? ";
                                    ppMap = (Map) db.findOne(zhsql, new Object[]{hosnum, xmbh});
                                    if (ppMap != null && !ppMap.isEmpty()) {
                                        zhxmbh = (String) ppMap.get("tjindid");
                                        zhxmmc = (String) ppMap.get("tjindname");
                                    }

                                    if (indid.equals(zhxmbh)) {
                                        String dw = (String) lisindMap.get("dw"); //小项单位
                                        String jgz = (String) lisindMap.get("jgz");//小项结果
                                        String ckfw = (String) lisindMap.get("ckfw");//参考范围
                                        Date jyrq = DateUtil.stringToDate((String) lisindMap.get("jyrq"), "yyyy-mm-dd");//检验日期
                                        //updlis_pi.add(new Object[]{dw,jgz,ckfw,jyrq,hosnum,pexamid,xmbh});
                                        inslis_pi.add(new Object[]{hosnum, examid, pexamid, jgz, comid, comname, xmbh,
                                                xmmc, "检验", itemuuid, sn, ckfw, dw});

                                        lisnum++;
                                        excdate = jyrq;//检验日期赋为审核日期
                                    }

                                }
                            }

                        }

                        System.out.println("--------list大小+listnum:" + jydxinds.size() + ";" + lisnum);
                        if (jydxinds.size() == lisnum && lisnum != 0) {//相等代表某项检验完成
                            upd_pi.add(new Object[]{excdate, hosnum, itemuuid});

                            //删除某检验大项下所有小项的结果
                            Object[][] del_params = new Object[del_pi.size()][2];
                            for (int j = 0; j < del_pi.size(); j++) {
                                del_params[j] = del_pi.get(j);
                            }
                            sql = "delete from pexam_results a where a.hosnum=? and a.itemuuid=? and a.pexamid=?";
                            db.excuteBatch(sql, del_params);
									/*
									//插入所有小项的初始值
									Object[][] ins_params = new Object[ins_pi.size()][2];
									for(int j=0;j<ins_pi.size();j++){
										ins_params[j] = ins_pi.get(j);
									}
									sql = "insert into pexam_results(hosnum,examid,pexamid,result,comid,comname,indid,indname," +
											"examtype,itemuuid,sn,range,resultunit)values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
									db.excuteBatch(sql, ins_params);
									*/
                            //插入lis结果到pexam_result表
                            Object[][] inslis_params = new Object[inslis_pi.size()][2];
                            for (int j = 0; j < inslis_pi.size(); j++) {
                                inslis_params[j] = inslis_pi.get(j);
                            }
                            sql = "insert into pexam_results(hosnum,examid,pexamid,result,comid,comname,indid,indname," +
                                    "examtype,itemuuid,sn,range,resultunit)values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
                            db.excuteBatch(sql, inslis_params);
								    /*
									//更新lis的检验结果
									Object[][] updlis_params = new Object[updlis_pi.size()][2];
									for(int j=0;j<updlis_pi.size();j++){
										updlis_params[j] = updlis_pi.get(j);
									}
									sql="update pexam_results r set r.resultunit=?,r.result=?,r.range=?,r.excdate=? where r.hosnum=? and r.pexamid=? and r.indid=? ";
									db.excuteBatch(sql, updlis_params);
									*/
                            //更新pexam_items_title表
                            Object[][] upd_params = new Object[upd_pi.size()][2];
                            for (int j = 0; j < upd_pi.size(); j++) {
                                upd_params[j] = upd_pi.get(j);
                            }
                            //sql = "update pexam_items_title a set a.excdate=?,a.excdoctorid=?,a.excdoctorname=?,a.checkdate=?,a.checkdoctorid=?,a.checkdoctorname=? where a.hosnum=? and a.itemuuid=?";
                            sql = "update pexam_items_title a set a.excdate=? where a.hosnum=? and a.itemuuid=?";
                            db.excuteBatch(sql, upd_params);
                        }

                    }


                }
            }

            JSONArray jsonArr = JSONArray.fromObject(lstTree);
            pw.print(jsonArr.toString());
            db.commit();
            db2.commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            db.rollback();
            db2.rollback();
            pw.print("fail");
        } finally {
            db.freeCon();
            db2.freeCon();
        }
        pw.flush();
        pw.close();
    }


    @SuppressWarnings("unchecked")
    @RequestMapping("/Select")
    public void Select(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        List<Map> itemsComList = null;
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "";
            sql = "select OHYEAH.* from (select OHNO.*, rownum no from (select * from pexam_ind_result p) OHNO where rownum <= '5') OHYEAH where no > '0'";
            itemsComList = db.find(sql);
            db.commit();
            //System.out.println(JSONArray.fromObject(itemsComList).toString());
            pw.print(JSONArray.fromObject(itemsComList).toString());
            pw.flush();
            pw.close();
        } catch (Exception e) {
            pw.print("fail");
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
    }



	/*
	 * 修改医生站和总检医生站
	 */

    //获取体检类型
    @RequestMapping(value = "/getExamtype")
    public void getExamtype(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            List<Map> list = null;
            String sql = "select m.examtype from pexam_mans m where m.hosnum = ? group by m.examtype order by m.examtype";
            sql = "select ba.contents from bas_dicts  ba where ba.nekey='1057' and ba.nevalue!='!' group by ba.contents ";
            list = db.find(sql);
            JSONArray jsons = JSONArray.fromObject(list);
            //System.out.println("hosnum="+hosnum);
            //System.out.println(jsons.toString());
            //System.out.println("jsons.toString()=====examtype=====1"+jsons.toString());
            pw.print(jsons.toString());
            db.commit();
        } catch (Exception e) {
            db.rollback();
            pw.print("fail");
            e.printStackTrace();
        } finally {
            try {
                db.freeCon();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("/itemsCombo")
    public ModelAndView itemsCombo(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String indid = request.getParameter("indid");
        modelMap.put("indid", indid);
        modelMap.put("hosnum", hosnum);
        return new ModelAndView("pexam/itemsComList", modelMap);
    }

    //体检医生站 新增   诊断建议
    @RequestMapping(value = "/andNewSugget")
    public void andNewSugget(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        DBOperator db = null;
        PrintWriter pw = response.getWriter();
        Bas_dept bd = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_hospitals bh = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_user bu = (Bas_user) request.getSession().getAttribute("login_user");
        String deptname = bd.getDeptname();
        String sql = "";
        List<Map> list = null;
        try {
            String KSMC = ""; //科室名称
            String parentid = ""; //父节点id
            String SN = ""; //序号
            db = new DBOperator();
            String name = URLDecoder.decode(request.getParameter("name"), "UTF-8");
            Map map = new HashMap();
            //查询 节点关系 ，是否有根节点
            sql = "select a.ksmc,a.parentid  from pexam_sugests  a where a.ksmc like '%" + deptname + "%'  and a.parentid!='0000' ";
            list = db.find(sql);
            if (ListUtil.listIsNotEmpty(list)) {
                KSMC = list.get(0).get("ksmc").toString();
                parentid = list.get(0).get("parentid").toString();
            } else {
                //throw new Exception("未找到诊断对应的父节点，无法添加诊断。");
                sql = "select * from pexam_sugests s  where s.parentid = '0000'  order by s.ksbh desc";
                list = db.find(sql);
                if (ListUtil.listIsNotEmpty(list)) {
                    String NewSuggestid = list.get(0).get("sugestid").toString();
                    String NewSN = list.get(0).get("sn").toString();
                    NewSuggestid = (Integer.parseInt(NewSuggestid) + 1) + "";
                    NewSN = (Integer.parseInt(NewSN) + 1) + "";
                    //插入 父根节点
                    db.excute("insert into pexam_sugests (SUGESTID, KSBH, CLASSNAME, PYBM, SUGESTTEXT, PARENTID, HOSNUM, NODECODE, DOCTORID, CDATE, CMAN, SN, DIAGNO, INDNAME, RESULTTYPE, INDID, UNNORMAL, KSMC)" +
                                    "values (?, ?, ?, ?, null, '0000', ?, ?, ?, to_date(?, 'yyyy-mm-dd'), ?, ?, null, null, null, null, null, ?) ",
                            new Object[]{NewSuggestid, NewSuggestid, deptname, WordUtil.trans2PyCode(deptname),
                                    bh.getHosnum(), bh.getNodecode(), bu.getId(), StrUtil.dateToStr(new Date()), bu.getName(), NewSN, deptname});
                    KSMC = deptname;
                    parentid = NewSuggestid;
                    db.commit();
                } else {
                    throw new Exception("根节点0000 不存在，数据库异常！请联系系统工程师");
                }
            }

            sql = "select * from pexam_sugests a  where a.classname like '%" + name + "%'";
            list = db.find(sql);
            if (ListUtil.listIsNotEmpty(list)) {
                throw new Exception("已经存在诊断：" + name);
            } else {
                sql = "insert into pexam_sugests (SUGESTID, KSBH, CLASSNAME, PYBM, SUGESTTEXT, PARENTID, HOSNUM, NODECODE, DOCTORID, CDATE, CMAN, SN, DIAGNO, INDNAME, RESULTTYPE, INDID, UNNORMAL, KSMC)" +
                        "	values (?, null, ?, ?, null, ?, ?, ?, ?, to_date(?, 'yyyy-mm-dd'), ?, null , null, null, null, null, null, ? )";
                db.excute(sql, new Object[]{UuidUtil.getUUID(), name, WordUtil.trans2PyCode(name), parentid, bh.getHosnum(), bh.getNodecode(), bu.getId(), StrUtil.dateToStr(new Date()), bu.getName(), KSMC});
                db.commit();
                pw.print("添加成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            pw.print("fail" + e.getMessage());
        } finally {
            db.freeCon();
            pw.flush();
            pw.close();
        }

    }

    //常见结果分页
    @RequestMapping(value = "/getCommonResultsCount")
    public void getCommonResultsCount(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String indid = request.getParameter("indid");
        String comparSearch = request.getParameter("comparSearch");
        String resultname = request.getParameter("resultname");
        String hosnum = basHospitals.getHosnum();
        DBOperator db = null;
        String sql = "";
        PrintWriter pw = null;
        try {
            db = new DBOperator();

            sql = "select count(t.indid) num from pexam_ind_result t left join pexam_sugests s on t.sugestid=s.sugestid where t.hosnum=? and t.indid=? ";
            if (("loadByTime").equals(comparSearch) && !("").equals(resultname)) {
                sql += " and result like '%" + resultname + "%'";
            }
            List<Map> list = db.find(sql, new Object[]{hosnum, indid});
            pw = response.getWriter();
            pw.print(list.get(0).get("num"));
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }

    }

    @RequestMapping(value = "/loadCommonResults")
    public void loadCommonResults(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String indid = request.getParameter("indid");
        String type = request.getParameter("type");
        int pageIndex = 0;
        int pageItems = 0;
        if (("1").equals(type)) {
            pageIndex = Integer.parseInt(request.getParameter("curPage"));
            pageItems = Integer.parseInt(request.getParameter("pageSize"));
        }
        String comparSearch = request.getParameter("comparSearch");
        String resultname = request.getParameter("resultname");

        String hosnum = basHospitals.getHosnum();
        DBOperator db = null;
        String sql = "";
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            String pagingSql1 = "select OHYEAH.* from (select OHNO.*,rownum no from ("; // 用于分页段1
            String pagingSql2 = ") OHNO where rownum <= '" + (pageIndex * pageItems + pageItems) + "') OHYEAH where no > '" + (pageIndex * pageItems) + "'"; // 用于分页段2
            //sql="select * from pexam_ind_result t where t.hosnum=? and t.indid=?";
            //sql=pagingSql1+sql+pagingSql2;
            //List list=db.find(sql, new Object[]{hosnum,indid,pageIndex * pageItems + pageItems, pageIndex * pageItems});
            sql = "select t.*,s.classname from pexam_ind_result t left join pexam_sugests s on t.sugestid=s.sugestid where t.hosnum=? and t.indid=? ";
            if (("loadByTime").equals(comparSearch) && !("").equals(resultname)) {
                sql += " and result like '%" + resultname + "%'";
            }
            sql += " order by to_number(t.sn)";
            if (("1").equals(type)) {
                sql = pagingSql1 + sql + pagingSql2;

            }
            //List list=db.find(sql, new Object[]{hosnum,indid});
            sql = sql;
            List list = db.find(sql, new Object[]{hosnum, indid});
            pw = response.getWriter();
            JSONArray jsons = JSONArray.fromObject(list);
            pw.print(jsons.toString());
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }

    }

    @SuppressWarnings("unchecked")
    @RequestMapping("/SelectCombo")
    public void SelectCombo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        String selectVal = URLDecoder.decode(request.getParameter("selectVal"), "utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        List<Map> itemsComList = null;
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "";
            sql = "select * from pexam_sugests pir where (pir.hosnum=? or pir.hosnum='0000') and pir.pybm like '" + selectVal.toUpperCase() + "%' or pir.CLASSNAME like '%" + selectVal.toUpperCase() + "%'";
            itemsComList = db.find(sql, hosnum);
            pw.print(JSONArray.fromObject(itemsComList).toString());
            pw.flush();
            pw.close();
        } catch (Exception e) {
            pw.print("fail");
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
    }

    @RequestMapping(value = "/dosaveCommonResult")
    public void dosaveCommonResult(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            String json_commonresult = URLDecoder.decode(request.getParameter("json_commonresult"), "utf-8");
            JSONArray commonresult = JSONArray.fromObject(json_commonresult);
            if (commonresult.size() > 0) {
                List<Object[]> pi = new ArrayList<Object[]>();
                List<Object[]> pi2 = new ArrayList<Object[]>();
                JSONObject obj = null;
                for (int i = 0; i < commonresult.size(); i++) {
                    obj = (JSONObject) commonresult.get(i);
                    String indid = obj.getString("indid");
                    String comresid = obj.getString("comresid");
                    String unnormal = obj.getString("unnormal");
                    String result = obj.getString("result");
                    String sugestid = obj.getString("sugestid");
                    String sn = obj.getString("sn");
                    System.out.println(indid + "--" + comresid + "--" + unnormal + "--" + result + "--" + sugestid + "--" + sn);

                    String ss = "select * from pexam_ind_result a where a.comresid=?";
                    List list = db.find(ss, new Object[]{comresid});
                    if (list.size() > 0) {
                        pi2.add(new Object[]{indid, unnormal, result, sugestid, sn, hosnum, comresid});
                    } else {
                        pi.add(new Object[]{hosnum, indid, comresid, unnormal, result, sugestid, sn});
                    }
                }

                Object[][] params = new Object[pi.size()][2];
                Object[][] params2 = new Object[pi2.size()][2];
                for (int i = 0; i < pi.size(); i++) {
                    params[i] = pi.get(i);
                }
                for (int i = 0; i < pi2.size(); i++) {
                    params2[i] = pi2.get(i);
                }

                if (params2.length > 0) {
                    String sql = "update pexam_ind_result a set a.indid =?,a.unnormal=?,a.result=?,a.sugestid=?,a.sn=? where a.hosnum = ? and a.comresid=? ";
                    db.excuteBatch(sql, params2);
                }
                if (params.length > 0) {
                    String sql = "insert into pexam_ind_result(hosnum,indid,comresid,unnormal,result,sugestid,sn) values(?,?,?,?,?,?,?)";
                    db.excuteBatch(sql, params);
                }
                pw = response.getWriter();
                pw.print("success");
            }
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }
    }

    @RequestMapping(value = "/dodeleteCommonResult")
    public void dodeleteCommonResult(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            String json_commonresult = URLDecoder.decode(request.getParameter("json_commonresult"), "utf-8");
            System.out.println("json_commonresult:" + json_commonresult);
            JSONArray commonresult = JSONArray.fromObject(json_commonresult);

            if (commonresult.size() > 0) {
                List<Object[]> pi = new ArrayList<Object[]>();
                JSONObject obj = null;
                for (int i = 0; i < commonresult.size(); i++) {
                    obj = commonresult.getJSONObject(i);
                    String indid = obj.getString("indid");
                    String comresid = obj.getString("comresid");
                    System.out.println("*************" + indid + "--" + comresid + "--");
                    pi.add(new Object[]{hosnum, indid, comresid});
                }

                Object[][] params = new Object[pi.size()][2];
                for (int i = 0; i < pi.size(); i++) {
                    params[i] = pi.get(i);
                }

                String sql = "delete from pexam_ind_result t where t.hosnum=? and t.indid=? and t.comresid=? ";
                db.excuteBatch(sql, params);
                pw = response.getWriter();
                pw.print("success");
            }
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }
    }

    @RequestMapping(value = "/checkCommonResultsName")
    public void checkCommonResultsName(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        DBOperator db = null;
        PrintWriter pw = null;
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String result = URLDecoder.decode(request.getParameter("result"), "utf-8");
            System.out.println("===========>" + result);
            String indid = request.getParameter("indid");
            String sql = "select * from pexam_ind_result t where t.hosnum=? and t.indid=? and t.result=? ";
            List<Map> list = db.find(sql, new Object[]{hosnum, indid, result});
            if (list.size() > 0) {
                map.put("result", "success");
                Map mm = list.get(0);
                map.put("comresid", mm.get("comresid"));
            } else {
                map.put("result", "fail");
            }
            //map.put("comresid", list.get(0));
            JSONObject jsonObj = JSONObject.fromObject(map);
            System.out.println("jsonObj.toString():" + jsonObj.toString());
            pw.print(jsonObj.toString());
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }
    }

    //------------------------验证该体检人员是否保存过-----------
    @RequestMapping(value = "/CheckDeptSum")
    public void CheckDeptSum(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String pexamid = request.getParameter("pexamid");
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            //System.out.println("===========>"+pexamid);
            String sql = "select * from pexam_deptsum d where d.hosnum=? and d.pexamid=? ";
            List<Map> list = db.find(sql, new Object[]{hosnum, pexamid});
            if (list.size() > 0) {
                pw.print("Y");
            } else {
                pw.print("N");
            }
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }
    }

    //------------------------回滚已获取的检验结果-----------
    @RequestMapping(value = "/RollbackItemdetils")
    public void RollbackItemdetils(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String pexamid = request.getParameter("pexamid");
        String itemuuid = request.getParameter("itemuuid");
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "update pexam_items_title a set a.excdoctorname='',a.excdate='',checkdoctorname='',checkdate='',typeflag='',a.deptsum='' where a.hosnum=? and a.pexamid=? and a.itemuuid=?  ";
            db.excute(sql, new Object[]{hosnum, pexamid, itemuuid});

            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }
    }

    // 跳转显示参合的页面(服务中心)
    @RequestMapping(value = "/pexamDiseaseScenter")
    public String pexamDiseaseScenter(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        String flag = request.getParameter("flag");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        request.setAttribute("flag", flag);
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        if ("7007".equals(hosnum)) {
            DBOperator db = new DBOperator();
            Map<String, Integer> hosnameMap = null;
            String sqlhosname = "select hosname from bas_hospitals where hosnum=? and nodecode=?";
            hosnameMap = (Map<String, Integer>) db.findOne(sqlhosname, new Object[]{hosnum, hosnum});
            db.commit();
            db.freeCon();
            request.setAttribute("hosname", String.valueOf(hosnameMap.get("hosname")));

        }
        return "pexam/pexamVillageDisease";
    }

    // 体检疾病统计（负责跳转视图页面）
    @RequestMapping(value = "/pexamSumCountScenter")
    public String pexamSumCountScenter(HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String examtype = URLDecoder.decode(request.getParameter("examtype"), "utf-8");
        String time = URLDecoder.decode(request.getParameter("time"), "utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String flag = request.getParameter("flag");
        request.setAttribute("startDate", startDate);
        request.setAttribute("endDate", endDate);
        request.setAttribute("examtype", examtype);
        request.setAttribute("time", time);
        request.setAttribute("flag", flag);
        if ("7007".equals(hosnum)) {
            DBOperator db = new DBOperator();
            Map<String, Integer> hosnameMap = null;
            String sqlhosname = "select hosname from bas_hospitals where hosnum=? and nodecode=?";
            hosnameMap = (Map<String, Integer>) db.findOne(sqlhosname, new Object[]{hosnum, hosnum});
            db.commit();
            db.freeCon();
            request.setAttribute("hosname", String.valueOf(hosnameMap.get("hosname")));

        }
        return "pexam/pexamDiseasesSum";

    }

    //----------- 2013-05-27 更新病人打印标志 徐闯----------------
    @RequestMapping("/updateIsPrint")
    public void updateIsPrint(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String pexamid = request.getParameter("pexamid");//体检编号
        DBOperator db = null;
        try {
            db = new DBOperator();
            String sql = "update pexam_mans m set m.isprint='Y' where m.hosnum=? and m.pexamid=? ";
            db.excute(sql, new Object[]{hosnum, pexamid});
            db.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
    }

    // -----------------打印预约单添加导出按钮------------------------
    @RequestMapping("/exportMwInsheetsQuery")
    public void exportMwInsheetsQuery(HttpServletRequest request,
                                      HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String examid = request.getParameter("examid");// 如果不是团体则就是为"0000"
        String town_village = URLDecoder.decode(request
                .getParameter("town_village"), "utf-8");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute(
                "login_user");
        String zh_begin = URLDecoder.decode(request.getParameter("zh_begin"),
                "utf-8");
        String zh_end = URLDecoder.decode(request.getParameter("zh_end"),
                "utf-8");
        String age_begin = request.getParameter("age_begin");
        String age_end = request.getParameter("age_end");
        String name = URLDecoder.decode(request.getParameter("name"), "utf-8");
        String isTest = request.getParameter("isTest");
        DBOperator db = null;
        String sql = "";
        try {
            List list = null;
            db = new DBOperator();
            sql = "select t.patname,t.sex,t.idnum,to_char(dateofbirth,'yyyy-MM-dd') dateofbirth,t.phonecall,t.village,t.address,t.zh from pexam_mans t,bas_dept a,bas_hospitals b where a.hosnum=? and a.nodecode=? and a.hosnum=b.hosnum and a.nodecode=b.nodecode and a.deptname='体检中心' and t.hosnum=? and t.examid=? ";
            if (zh_begin != null && zh_begin != "") {
                if (zh_end == null || zh_end == "") {
                    sql += " and t.zh= '" + zh_begin + "组'";
                }
            }
            if (zh_end != null && zh_end != "") {
                if (zh_begin == null || zh_begin == "") {
                    sql += " and t.zh= '" + zh_end + "组'";
                }
            }
            if (zh_begin != null && zh_begin != "" && zh_end != null && zh_end != "") {
				/*
				sql = "select t.patname,t.sex,t.idnum,to_char(dateofbirth,'yyyy-MM-dd') dateofbirth,t.phonecall,t.village,t.address,t.zh from (select s.*, to_number(substr(s.zh, 0, instr(s.zh, '组') - 1)) value FROM pexam_mans s where not REGEXP_LIKE(trim(s.zh), '^([0-9]+/.[0-9]+)$|^[0-9]+$')) t,bas_dept a,bas_hospitals b "
						+ " where a.hosnum = ? and a.nodecode = ? and a.hosnum = b.hosnum"
						+ " and a.nodecode = b.nodecode and a.deptname = '体检中心' and t.hosnum = ? and t.examid = ? and t.value between "
						+ zh_begin + " and " + zh_end;
				*/

                sql = "select t.patname,t.sex,t.idnum,to_char(dateofbirth,'yyyy-MM-dd') dateofbirth,t.phonecall,t.village,t.address,t.zh from (select s.*, to_number( case when regexp_like(substr(s.zh, 0, instr(s.zh, '组') - 1),'^([0-9]+/.[0-9]+)$|^[0-9]+$') then substr(s.zh, 0, instr(s.zh, '组') - 1) else '88888888' end ) value FROM pexam_mans s where not REGEXP_LIKE(trim(s.zh), '^([0-9]+/.[0-9]+)$|^[0-9]+$')) t,bas_dept a,bas_hospitals b "
                        + " where a.hosnum = ? and a.nodecode = ? and a.hosnum = b.hosnum"
                        + " and a.nodecode = b.nodecode and a.deptname = '体检中心' and t.hosnum = ? and t.examid = ? and t.value between "
                        + zh_begin + " and " + zh_end;

            }
            if (town_village != null && town_village != "") {
                sql += " and t.village like '%" + town_village + "%'";
            }

            if (age_begin != null && age_begin != "" && age_end != null
                    && age_end != "") {
                sql += " and trunc(extract(year from sysdate)-extract(year from dateofbirth)) between '"
                        + age_begin + "' and '" + age_end + "'";
            } else if (age_begin != null && age_begin != ""
                    && (age_end == null || age_end == "")) {
                sql += " and trunc(extract(year from sysdate)-extract(year from dateofbirth)) >'"
                        + age_begin + "'";
            } else if ((age_begin == null || age_begin == "")
                    && age_end != null && age_end != "") {
                sql += " and trunc(extract(year from sysdate)-extract(year from dateofbirth)) <'"
                        + age_end + "'";
            }
            if (name != null && name != "") {
                sql += " and t.patname like '%" + name + "%'";
            }
            if (("1").equals(isTest)) {
                sql += " and t.bdate is null";
            } else if (("2").equals(isTest)) {
                sql += " and t.bdate is not null and not EXISTS (select a.pexamid from pexam_deptsum a where a.pexamid = t.pexamid)";
            }
            sql += " order by t.village,t.zh";
            list = db.find(sql, new Object[]{basHospitals.getHosnum(),
                    basUser.getNodecode(), hosnum, examid});
			/*for(int i=0;i<list.size();i++){
				Map map = (Map) list.get(i);
				String idnum = (String) map.get("idnum");

				String headID = idnum.substring(0,6);
				String lastID = idnum.substring(idnum.length()-6);
				String newID = headID+"****"+lastID;
				map.put("idnum", newID);

				map.put("idnum", idnum);
			}*/
            String[] titles = new String[]{"姓名", "性别", "身份证", "出生日期", "联系电话", "村",
                    "家庭地址", "组号"};
            int[] length = new int[]{15, 15, 25, 25, 25, 25, 35, 25,};
            // 导出
            String fileNameTemp = URLEncoder.encode(basHospitals.getHosname()
                    + "预约单打印列表.xls", "UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename="
                    + new String(fileNameTemp.getBytes("utf-8"), "gbk"));
            OutputStream os = response.getOutputStream();
            ExcelUtils eu = new ExcelUtils();
            eu.export(os, titles, length, DbUtils.ListMapToListObject(list));
            os.flush();
            os.close();
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
    }


    /*
	 * ---------------把没有体检的项目的医生默认选择为最后一个体检医生名字----------
	 */
    @RequestMapping(value = "/SearchDoctorname", method = RequestMethod.POST)
    public void SearchDoctorname(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute(
                "login_dept");
        String hosnum = basHospitals.getHosnum();
        String treeNodeId = request.getParameter("treeNodeId");
        ReturnValue returnValue = new ReturnValue();
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            // String sql = "select a.excdoctorname from pexam_results a where
            // a.hosnum='"+hosnum+"' and a.comid='"+treeNodeId+"' and
            // a.excdate=(select max(excdate) from pexam_results where
            // hosnum=a.hosnum and comid=a.comid ) group by a.excdoctorname";
            String sql = "select a.excdoctorname from pexam_items_title a where a.hosnum='"
                    + hosnum
                    + "'  and a.itemcode='"
                    + treeNodeId
                    + "' and a.excdate=(select max(excdate) from pexam_items_title where hosnum=a.hosnum and itemcode=a.itemcode ) ";
            Map<String, String> DoctornameMap = null;
            DoctornameMap = (Map<String, String>) db.findOne(sql);
            String doctorname = "";
            if (DoctornameMap == null || DoctornameMap.isEmpty()) {

            } else {
                doctorname = String.valueOf(DoctornameMap.get("excdoctorname"));
            }
            returnValue.setValue(doctorname);
            //System.out.println("------------" + Doctorname + "-------------");
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        pw.print(JSONObject.fromObject(returnValue).toString());
        pw.flush();
        pw.close();
    }

    //-------2013-08-26----- 条码重打--------
    @RequestMapping("/doReplaceCodePrint")
    public void doReplaceCodePrint(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        request.setCharacterEncoding("utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");

        String hosnum = basHospitals.getHosnum();
        String shortname = basHospitals.getShortname();// 医院简称
        String nodecode = basHospitals.getNodecode();
        // String yytybm=basHospitals.getYytybm();//医院统一编码

        String deptcode = basDept.getDeptcode();
        String deptname = basDept.getDeptname();
        String userid = basUser.getId();
        String username = basUser.getName();

        Timestamp timesTamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyMMdd");
        String codedatestr = dateFormat2.format(new Date());
        // System.out.println("条码日期------"+codedatestr);

        String pexamid = request.getParameter("pexamid");// 唯一标识--个人体检编号
        String examid = request.getParameter("examid");// 如果是个人体检的则该参数为”0000“

        DBOperator db = null;
        PrintWriter pw = null;

        String errormsg = "fail";// 返回的错误信息
        try {
            db = new DBOperator();
            pw = response.getWriter();
            int itemsNum = 0;
            String sql = "";
            String sql2 = "";
            List list = null;

            Map returnMap = new HashMap();
            List<Map> barCodeList = new ArrayList<Map>();// 收集检验条码数据
            List<Map> examBarCodeList = new ArrayList<Map>();// 收集检查条码数据

            // 获取体检人的性别--过滤出有性别要求的体检项目

            sql = "select a.*, (floor(MONTHS_BETWEEN(sysdate,a.dateofbirth)/12))  as age from pexam_mans a where a.hosnum=? and a.pexamid=? and a.bdate is not null";
            list = db.find(sql, new Object[]{hosnum, pexamid}, PexamMans.class);
            returnMap.put("pexamList", list);

            if (list != null && list.size() > 0) {// 防止重复提交
                PexamMans pexamMan = (PexamMans) list.get(0);
                String patname = pexamMan.getPatname();
                String sex = pexamMan.getSex();
                String age = pexamMan.getAge();


                String str = request.getParameter("checkIds");
                String[] checkIds = str.split("/");
                //System.out.println("----------------------------------"+checkIds);
                List list_lis = null;
                System.out.println("checkIds.length~~~~~" + checkIds.length);
                for (int i = 0; i < checkIds.length; i++) {
                    sql = "select a.* ,b.xgysname,b.printnum,b.sfws,b.hbmc from pexam_items_title a  left join pexam_items_com b on b.comid=a.itemcode  where a.hosnum = ? and a.pexamid = ? and a.comclass in ('检验','外送') and a.itemuuid='" + checkIds[i] + "'";
                    list_lis = db.find(sql, new Object[]{hosnum, pexamid});
                    for (int j = 0; j < list_lis.size(); j++) {
                        //System.out.println(list_lis.size());
                        Map mm_lis = (Map) list_lis.get(j);
                        String printnum = mm_lis.get("printnum") == null ? "" : mm_lis.get("printnum").toString();
                        Map barCodeMap = new LinkedHashMap();
                        barCodeMap.put("shortname", shortname);// 医院简写
                        barCodeMap.put("pexamid", pexamid);// 体检编号
                        barCodeMap.put("excdeptname", mm_lis.get("excdeptname"));// 执行科室

                        barCodeMap.put("itemname", mm_lis.get("hbmc") == null ? mm_lis.get("afterhb_name") : mm_lis.get("hbmc"));// 取合并名称字段，如果为空 取本身的名字 而不是afterhb_name
                        // barCodeMap.put("itemuuid",
                        // mm_lis.get("itemuuid"));//主键--即条码
                        barCodeMap.put("itemuuid", mm_lis.get("tmcode"));// 主键--即条码
                        barCodeMap.put("doctorname", username);// 开单医生
                        barCodeMap.put("patientname", patname);// 体检人姓名
                        barCodeMap.put("sex", sex);// 性别
                        barCodeMap.put("age", age);// 年龄
                        barCodeMap.put("printdate", dateFormat.format(new Date()));// 打印时间
                        barCodeMap.put("xgys", mm_lis.get("xgysname") == null ? "" : mm_lis.get("xgysname"));
                        barCodeMap.put("printnum", printnum);//打印次数
                        barCodeMap.put("sfws", mm_lis.get("sfws") == null ? "" : mm_lis.get("sfws"));//是否外送
                        barCodeList.add(barCodeMap);
//						if("8821".equals(mm_lis.get("itemcode"))){
//							barCodeList.add(barCodeMap);
//						}
                        Pattern p = Pattern.compile("[0-9]{1}");
                        Matcher m = p.matcher(printnum);
                        if (m.find()) {
                            for (int x = 1; x < Integer.parseInt(printnum); x++) {
                                barCodeList.add(barCodeMap);
                            }
                        }

                    }
                }

            }
			/*
			Iterator it=barCodeList.iterator();
			int t=-1;
			// 合并 相同的条码的检验 成一条记录，且itemname 累加 。
			while (it.hasNext()) {
				Map map=(Map)it.next();
				if(true){
					if(t>0){
						String upCode=String.valueOf(barCodeList.get(t).get("itemuuid"));
						if(upCode.equals(map.get("itemuuid"))){
							barCodeList.get(t).put("itemname", barCodeList.get(t).get("itemname").toString()+"+"+map.get("itemname"));
							it.remove();
							continue;
						}
					}
				}
				t++;
			} */
            returnMap.put("barCodeList", barCodeList);
            //System.out.println(JSONObject.fromObject(returnMap).toString());
            pw.print(JSONObject.fromObject(returnMap).toString());
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            pw.print(errormsg);
        } finally {
            db.freeCon();

        }
        pw.flush();
        pw.close();
    }

    @RequestMapping({"/OneKeyStartExam"})
    public void oneKeyStartExam(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String hosnum = basHospitals.getHosnum();
        String shortname = basHospitals.getShortname();
        String nodecode = basHospitals.getNodecode();

        String deptcode = basDept.getDeptcode();
        String deptname = basDept.getDeptname();
        String userid = basUser.getId();
        String username = basUser.getName();

        String examid = request.getParameter("examid");
        String school = URLDecoder.decode(request.getParameter("school"), "utf-8");
        String grade = request.getParameter("grade");
        String sclass_begin = URLDecoder.decode(request.getParameter("sclass_begin"), "utf-8");
        String sclass_end = URLDecoder.decode(request.getParameter("sclass_end"), "utf-8");
        String age_begin = request.getParameter("age_begin");
        String age_end = request.getParameter("age_end");
        String name = URLDecoder.decode(request.getParameter("name"), "utf-8");
        String isTest = request.getParameter("isTest");

        Timestamp timesTamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        DBOperator db = null;
        PrintWriter pw = null;
        String returnMessage = "";
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "";
            List list = null;
            List pexamMansList = new ArrayList();

            //判断是否为学生体检
            sql = "select * from pexam_main m where m.examid=?";
            List<Map> mainList = db.find(sql, examid);
            if (mainList != null && mainList.size() > 0) {
                if (mainList.get(0).get("examtype") != null && "学生体检".equals(mainList.get(0).get("examtype"))) {
                    Map returnMap = new HashMap();
                    sql = "select * from pexam_mans t where t.hosnum=? and t.examid=? ";
                    if ((sclass_begin != null) && (sclass_begin != "") && ((
                            (sclass_end == null) || (sclass_end == "")))) {
                        sql = sql + " and t.zh= '" + sclass_begin + "'";
                    }

                    if ((sclass_end != null) && (sclass_end != "") && ((
                            (sclass_begin == null) || (sclass_begin == "")))) {
                        sql = sql + " and t.zh= '" + sclass_end + "'";
                    }

                    if ((sclass_begin != null) && (sclass_begin != "") && (sclass_end != null) && (sclass_end != "")) {
                        sql = sql + " and  t.zh between " + sclass_begin + " and " + sclass_end;
                    }

                    if ((school != null) && (school != "")) {
                        sql = sql + " and t.township like '%" + school + "%'";
                    }

                    if ((grade != null) && (grade != "")) {
                        sql = sql + " and t.village = '" + grade + "'";
                    }

                    if ((age_begin != null) && (age_begin != "") && (age_end != null) &&
                            (age_end != ""))
                        sql = sql + " and trunc(extract(year from sysdate)-extract(year from dateofbirth)) between '" +
                                age_begin + "' and '" + age_end + "'";
                    else if ((age_begin != null) && (age_begin != "") && ((
                            (age_end == null) || (age_end == ""))))
                        sql = sql + " and trunc(extract(year from sysdate)-extract(year from dateofbirth)) >'" +
                                age_begin + "'";
                    else if ((((age_begin == null) || (age_begin == ""))) &&
                            (age_end != null) && (age_end != "")) {
                        sql = sql + " and trunc(extract(year from sysdate)-extract(year from dateofbirth)) <'" +
                                age_end + "'";
                    }
                    if ((name != null) && (name != "")) {
                        sql = sql + " and t.patname like '%" + name + "%'";
                    }
                    if ("1".equals(isTest))
                        sql = sql + " and t.bdate is null";
                    else if ("2".equals(isTest)) {
                        sql = sql + " and t.bdate is not null and not EXISTS (select a.pexamid from pexam_deptsum a where a.pexamid = t.pexamid)";
                    }

                    pexamMansList = db.find(sql, new Object[]{hosnum, examid});
                    List pi_up = new ArrayList();
                    List pi = new ArrayList();

                    if ((pexamMansList != null) && (pexamMansList.size() > 0)) {
                        for (int m = 0; m < pexamMansList.size(); ++m) {
                            Map pmanMap = (Map) pexamMansList.get(m);
                            String pexamid = (String) pmanMap.get("pexamid");

                            sql = "select a.*,to_char(sysdate,'yyyy')-to_char(a.dateofbirth,'yyyy') as age from pexam_mans a where a.hosnum=? and a.pexamid=? and a.bdate is null";
                            list = db.find(sql, new Object[]{hosnum, pexamid}, PexamMans.class);

                            if ((list != null) && (list.size() > 0)) {
                                PexamMans pexamMan = (PexamMans) list.get(0);
                                String patname = pexamMan.getPatname();
                                String sex = pexamMan.getSex();
                                String age = pexamMan.getAge();

                                pi_up.add(new Object[]{new Timestamp(new Date().getTime()), hosnum, pexamid});

                                sql = "select * from pexam_items a where (a.hosnum=? and a.examid=? and a.pexamid is null) or (a.hosnum=? and a.examid=? and a.pexamid=?)";
                                list = db.find(sql, new Object[]{hosnum, examid, hosnum, examid, pexamid});
                                if ((list != null) && (list.size() > 0)) {
                                    String groupids = "";
                                    String itemcodes = "";
                                    Map map0 = null;
                                    for (int i = 0; i < list.size(); ++i) {
                                        map0 = (Map) list.get(i);
                                        if ("y".equals(map0.get("isgroup")))
                                            groupids = groupids + "'" + map0.get("itemid") + "',";
                                        else {
                                            itemcodes = itemcodes + "," + map0.get("itemid") + "',";
                                        }

                                    }

                                    if (groupids.length() > 0) {
                                        groupids = groupids.substring(0, groupids.length() - 1);
                                        System.out.println("获取组成套餐的体检项目");
                                        sql = "select b.*,a.groupid,c.groupname from pexam_items_groupdetails a,pexam_items_com b,pexam_items_group c where a.groupid in (?) and a.itemcode=b.comid and c.groupid=a.groupid and a.hosnum=b.hosnum and a.hosnum=c.hosnum ".replace("?", groupids);
                                        sql = sql + " and a.hosnum=?";
                                        if (!("".equals(sex))) {
                                            sql = sql + " and (b.forsex='不限' or b.forsex='" + sex + "')";
                                        }
                                        list = db.find(sql, new Object[]{hosnum});
                                        if ((list != null) && (list.size() > 0)) {
                                            for (int i = 0; i < list.size(); ++i) {
                                                map0 = (Map) list.get(i);
                                                String itemcode = (String) map0.get("comid");
                                                String itemname = (String) map0.get("comname");
                                                String groupid = (String) map0.get("groupid");
                                                String groupname = (String) map0.get("groupname");
                                                String excdeptname = (String) map0.get("excdeptname");

                                                String itemuuid = new UUIDGenerator().generate().toString();
                                                String tmcode = "";
                                                pi.add(new Object[]{hosnum, itemuuid, examid, pexamid, itemcode, itemname,
                                                        map0.get("excdept"), map0.get("excdeptname"), groupid, groupname, map0.get("sn"),
                                                        map0.get("comclass"), userid, username, timesTamp, deptcode, deptname, tmcode});
                                            }
                                        }
                                    }

                                }

                            }

                        }

                        System.out.println("更新pexam_mans表的体检开始标志字段");
                        Object[][] params_up = new Object[pi_up.size()][2];
                        for (int i = 0; i < pi_up.size(); ++i) {
                            params_up[i] = ((Object[]) pi_up.get(i));
                        }
                        sql = "update pexam_mans a set a.bdate=?,a.isover='在检' where a.hosnum=? and a.pexamid=?";
                        db.excuteBatch(sql, params_up);

                        Object[][] params = new Object[pi.size()][2];
                        for (int i = 0; i < pi.size(); ++i) {
                            params[i] = ((Object[]) pi.get(i));
                        }

                        System.out.println("插入具体要体检的体检项目");
                        sql = "insert into pexam_items_title (hosnum,itemuuid,examid,pexamid,itemcode,itemname,excdept,excdeptname,groupid,groupname,sn,comclass,sheetdoctorid,sheetdoctorname,sheetdate,sheetdeptid,sheetdeptname,tmcode)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                        db.excuteBatch(sql, params);
                    }

                    pw.print("success");
                    db.commit();
                } else {
                    pw.print("选择的体检项目不是学生体检，请重新选择！");
                }
            } else {
                pw.print("体检项目选择有误，请重新选择！");
            }


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
	 * 一键总检页面
	 * */
    @RequestMapping("/showSuggestMoreData")
    public ModelAndView showSuggestMoreData(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        modelMap.put("hosname", basHospitals.getHosname());
        modelMap.put("doctorname", basUser.getName());
        modelMap.put("hosnum", hosnum);

        return new ModelAndView("pexam/suggestMoreData", modelMap);
    }

    // 获得重打条码选择项(itemname)
    @RequestMapping("/getItems")
    public void getItems(HttpServletRequest request,
                         HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");

        String hosnum = basHospitals.getHosnum();
        String shortname = basHospitals.getShortname();// 医院简称
        String nodecode = basHospitals.getNodecode();
        // String yytybm=basHospitals.getYytybm();//医院统一编码

        String deptcode = basDept.getDeptcode();
        String deptname = basDept.getDeptname();
        String userid = basUser.getId();
        String username = basUser.getName();

        Timestamp timesTamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyMMdd");
        String codedatestr = dateFormat2.format(new Date());
        // System.out.println("条码日期------"+codedatestr);

        String pexamid = request.getParameter("pexamid");// 唯一标识--个人体检编号
        String examid = request.getParameter("examid");// 如果是个人体检的则该参数为”0000“

        DBOperator db = null;
        PrintWriter pw = null;

        String errormsg = "fail";// 返回的错误信息
        try {
            db = new DBOperator();
            pw = response.getWriter();
            int itemsNum = 0;
            String sql = "";
            String sql2 = "";
            List list = null;

            Map returnMap = new HashMap();
            List<Map> barCodeList = new ArrayList<Map>();// 收集检验条码数据
            List<Map> examBarCodeList = new ArrayList<Map>();// 收集检查条码数据
            // 获取体检人的性别--过滤出有性别要求的体检项目
            //  and a.bdate is not null
            sql = "select a.*,to_char(sysdate,'yyyy')-to_char(a.dateofbirth,'yyyy') as age from pexam_mans a where a.hosnum=? and a.pexamid=?   ";
            list = db.find(sql, new Object[]{hosnum, pexamid},
                    PexamMans.class);
            returnMap.put("pexamList", list);

            if (list != null && list.size() > 0) {// 防止重复提交
                PexamMans pexamMan = (PexamMans) list.get(0);
                String patname = pexamMan.getPatname();
                String sex = pexamMan.getSex();
                String age = pexamMan.getAge();

                sql = "select * from pexam_items_title a where a.hosnum = ? and a.pexamid = ? and a.comclass in ('检验','外送')  and a.parent_comid is null  order by a.tmcode,a.sn";
                List list_lis = db.find(sql, new Object[]{hosnum, pexamid});
                for (int i = 0; i < list_lis.size(); i++) {
                    Map mm_lis = (Map) list_lis.get(i);
                    Map barCodeMap = new HashMap();
                    barCodeMap.put("itemname", mm_lis.get("afterhb_name"));// 大项名称
                    barCodeMap.put("itemuuid", mm_lis.get("itemuuid"));// 大项名称
                    barCodeList.add(barCodeMap);
                }

            }

            pw.print(JSONArray.fromObject(barCodeList).toString());
            // db.rollback();
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            pw.print(errormsg);
        } finally {
            db.freeCon();

        }
        pw.flush();
        pw.close();
    }

    /**
     * 一键总检
     * 一键总检只对“在检”状态的学生体检
     */
    @RequestMapping(value = "/oneKeySuggest", method = RequestMethod.POST)
    public void oneKeySuggest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String deptCode = basDept.getDeptcode();
        String doctorid = basUser.getId();
        String doctorname = basUser.getName();

        String examid = request.getParameter("examid");

        String village = URLDecoder.decode(request.getParameter("village"), "utf-8");
        String zh = request.getParameter("zh");
        String age_begin = request.getParameter("age_begin");
        String age_end = request.getParameter("age_end");
        String begin_date = request.getParameter("begin_date");
        String end_date = request.getParameter("end_date");
        //	System.out.println("=======cjValue======="+cjValue);
        Date bDate = new Date();
        String sql = "";

        DBOperator db = null;
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            //判断是否为学生体检
            sql = "select * from pexam_main m where m.examid=?";
            List<Map> mainList = db.find(sql, examid);
            if (mainList != null && mainList.size() > 0) {
                if (mainList.get(0).get("examtype") != null && "学生体检".equals(mainList.get(0).get("examtype"))) {
                    sql = "select a.*,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid) as total,(select count(*) from pexam_items_title b where b.hosnum=a.hosnum and b.examid=a.examid and b.pexamid=a.pexamid and b.excdate is not null) as fin from pexam_mans a where a.printflag is null and a.hosnum=? and a.bdate is not null and a.isover='在检' ";

                    if (examid != null && examid != "" && !"ALL".equals(examid)) {
                        sql += " and a.examid= '" + examid + "'";
                    }

                    if (village != null && village != "") {
                        sql += " and a.village like '%" + village + "%'";
                    }
                    if (zh != null && zh != "") {
                        sql += " and a.zh='" + zh + "'";
                    }

                    if (age_begin != null && age_begin != "" && age_end != null && age_end != "") {
                        sql += " and trunc(extract(year from sysdate)-extract(year from dateofbirth)) between '" + age_begin + "' and '" + age_end + "'";
                    } else if (age_begin != null && age_begin != "" && (age_end == null || age_end == "")) {
                        sql += " and trunc(extract(year from sysdate)-extract(year from dateofbirth)) >='" + age_begin + "'";
                    } else if ((age_begin == null || age_begin == "") && age_end != null && age_end != "") {
                        sql += " and trunc(extract(year from sysdate)-extract(year from dateofbirth)) <='" + age_end + "'";
                    }
                    if (begin_date != null && begin_date != "" && end_date != null && end_date != "") {
                        sql += " and to_char(a.bdate,'yyyy-MM-dd') between '" + begin_date + "' and '" + end_date + "'";
                    } else if (begin_date != null && begin_date != "" && (end_date == null || end_date == "")) {
                        sql += " and to_char(a.bdate,'yyyy-MM-dd')>'" + begin_date + "'";
                    } else if ((begin_date == null || begin_date == "") && end_date != null && end_date != "") {
                        sql += " and to_char(a.bdate,'yyyy-MM-dd')<'" + end_date + "'";
                    }

                    List<Map> itemsList = db.find(sql, hosnum);

                    if (itemsList != null && itemsList.size() > 0) {
                        List<Object[]> pi = new ArrayList<Object[]>();
                        List<Object[]> pii = new ArrayList<Object[]>();

                        //获取异常结果
                        sql = "select a.itemname,a.deptsum from pexam_items_title a where a.hosnum=? and a.pexamid=? order by a.sn";

                        for (int i = 0; i < itemsList.size(); i++) {
                            List<Map> list = new ArrayList<Map>();
                            String pexamid = itemsList.get(i).get("pexamid").toString();
                            pi.add(new Object[]{hosnum, itemsList.get(i).get("pexamid"), nodecode});
                            list = db.find(sql, new Object[]{hosnum, itemsList.get(i).get("pexamid")});
                            if (list != null && list.size() > 0) {
                                String deptsum = "";
                                String suggestion = "";
                                String dispression = "健康或正常"; //一键总检默认为 “健康或正常”
                                String str = "";
                                for (int j = 0; j < list.size(); j++) {
                                    if (list.get(j).get("deptsum") != null && !"".equals(list.get(j).get("deptsum"))) {
                                        str = list.get(j).get("deptsum").toString();
                                    } else {
                                        str = "无异常结果";
                                    }
                                    deptsum += (j + 1) + "、" + list.get(j).get("itemname") + ":" + "\r    " + str + "\r";
                                }
                                System.out.println(pexamid + " " + deptsum);
                                pii.add(new Object[]{hosnum, examid, deptCode, doctorid, bDate, pexamid, deptsum, "体检总结", doctorname, dispression, ""});
                                pii.add(new Object[]{hosnum, examid, deptCode, doctorid, bDate, pexamid, suggestion, "健康建议", doctorname, dispression, ""});
                            }
                        }
                        Object[][] params = new Object[pi.size()][2];
                        for (int i = 0; i < pi.size(); i++) {
                            params[i] = pi.get(i);
                        }
                        //未对体检结果进行总结及建议
                        //-----更新人员表 已完成状态--------
                        String sql_man = "update pexam_mans m set m.isover='完成' where m.hosnum=? and m.pexamid=? and m.nodecode=?";
                        db.excuteBatch(sql_man, params);

                        Object[][] params_d = new Object[pii.size()][2];
                        for (int i = 0; i < pii.size(); i++) {
                            params_d[i] = pii.get(i);
                        }
                        sql = "insert into pexam_deptsum (hosnum,examid,excdept,doctorid,examdate,pexamid,deptsum,sumtype,doctorname,DISPRESSION,DEFORMITY)values(?,?,?,?,?,?,?,?,?,?,?)";
                        db.excuteBatch(sql, params_d);

                        pw.print("总检成功");
                        db.commit();
                    } else {
                        pw.print("没有需要总检人员");
                    }
                } else {
                    pw.print("选中的项目不是学生体检，请重新选择！");
                }
            } else {
                pw.print("体检项目不存在，请重新选择！");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            db.rollback();
            pw.print("总检失败");
        } finally {
            db.freeCon();
            pw.flush();
            pw.close();
        }
    }

    /**
     * 获得医保卡信息
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/queryCardInfo")
    public void queryCardInfo(HttpServletRequest request, HttpServletResponse response) throws Exception {
        /*
        PrintWriter pw=null;
		Bas_hospitals hospital = (Bas_hospitals)request.getSession().getAttribute("login_hospital");
		String cardno = request.getParameter("cardno");
		String nCenter_code=hospital.getNcentercode();//农保中心编码
		String iserror = "";
		try{
			response.setContentType("text/html;charset=utf-8");
			pw = response.getWriter();
			FarmPersonInfo person = new FarmPersonInfo();
			JsFarmServerImpl hisjs = new JsFarmServerImpl();
			String readid=IPUtils.getClientIP(request);
			iserror = hisjs.getPersonInfo(cardno,person,nCenter_code,readid);
			person.setCardNo(cardno);
			if (!iserror.equals("")) {
				throw new MwInventoryException(iserror);
			}
			JSONObject jsonObject = JSONObject.fromObject(person);
			pw.print(jsonObject.toString());
		} catch (MwInventoryException mme) {
			mme.printStackTrace();
			pw.print("错误：" + mme.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			pw.print("错误：" + "数据库处理异常！");
		} finally {
			pw.flush();
			pw.close();
		}
		*/
    }


    //打印标志
    @RequestMapping(value = "/updatePrintFlag")
    public void updatePrintFlag(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String hosnum = basHospitals.getHosnum();
        String doctor = basUser.getName();
        String pexamid = request.getParameter("pexamid");
        PrintWriter pw = null;
        DBOperator db = null;
        String sql = "";
        try {
            pw = response.getWriter();
            db = new DBOperator();
            sql = "update pexam_mans s set s.isprint='Y',s.printdoctor = ? ,s.printtime = ? where s.hosnum=? and s.pexamid=?";
            db.excute(sql, new Object[]{doctor, new Timestamp(new Date().getTime()), hosnum, pexamid});
            pw.print(pexamid + "打印完成！");
            db.commit();
        } catch (Exception e) {
            db.rollback();
            pw.print("fail");
            e.printStackTrace();
        } finally {
            db.freeCon();
        }
    }

    //重打检验条码
    @RequestMapping("/getBarCode")
    public void getBarCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String hosnum = basHospitals.getHosnum();
        String username = basUser.getName();

        String pexamid = StrUtil.strNullToEmpty(request.getParameter("pexamid"));//体检人员编号
        String examid = StrUtil.strNullToEmpty(request.getParameter("examid"));

        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            //获取检验条码
            String sql = "select decode(a.comclass,'检验',a.itemuuid,b.pexamid) as itemuuid," +
                    "a.itemname||decode(a.comclass,'检验','',' (外送)') as itemname,a.sheetdoctorname as doctorname," +
                    "to_char(sysdate,'yyyy-mm-dd hh24:mi') as printdate," +
                    "b.patname as patientname,to_char(sysdate,'yyyy')-to_char(b.dateofbirth,'yyyy') as age,b.sex " +
                    "from pexam_items_title a,pexam_mans b where a.hosnum=? and a.pexamid=? and a.examid=? and (a.comclass='检验' or a.comclass='检验外送') " +
                    "and a.pexamid=b.pexamid and a.examid=b.examid order by a.sn";
            List<Map> barCodeList = db.find(sql, new Object[]{hosnum, pexamid, examid});

			/*
			sql = "select * from pexam_items a,pexam_items_com b " +
				"where a.hosnum=? and a.examid=? and a.pexamid=? and a.itemid=b.comid " +
				"and a.hosnum=b.hosnum and a.itemtype!='T' and b.comclass='检验外送'";
			List<Map> outItemsList = db.find(sql,new Object[]{hosnum,examid,pexamid});
			if(barCodeList!=null && barCodeList.size()>0){
				Map barCodeMap = barCodeList.get(0);
				Map outItemMap = null;
				for(Map temp : outItemsList){
					outItemMap = new HashMap();
					outItemMap.put("itemuuid", pexamid);
					outItemMap.put("itemname", temp.get("comname") + " (外送)");
					outItemMap.put("sheetdoctorname",barCodeMap.get("sheetdoctorname"));
					outItemMap.put("printdate", barCodeMap.get("printdate"));
					outItemMap.put("patientname", barCodeMap.get("patientname"));
					outItemMap.put("age", barCodeMap.get("age"));
					outItemMap.put("sex", barCodeMap.get("sex"));
					barCodeList.add(outItemMap);
				}
			}else{

			}
			*/
            //获取检查条码
            sql = "select a.itemuuid,a.itemname,a.sheetdoctorname as doctorname,to_char(sysdate,'yyyy-mm-dd hh24:mi') as printdate," +
                    "b.patname as patientname,to_char(sysdate,'yyyy')-to_char(b.dateofbirth,'yyyy') as age,b.sex,'体检' as zsname,b.pexamid " +
                    "from pexam_items_title a,pexam_mans b where a.hosnum=? and a.pexamid=? and a.examid=? and a.comclass='检查' " +
                    "and a.pexamid=b.pexamid and a.examid=b.examid";
            List<Map> examBarCodeList = db.find(sql, new Object[]{hosnum, pexamid, examid});
            Map returnMap = new HashMap();
            returnMap.put("barCodeList", barCodeList);
            returnMap.put("examBarCodeList", examBarCodeList);
            JSONObject json = JSONObject.fromObject(returnMap);
            System.out.println(json.toString());
            pw.print(json.toString());
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

    //-------显示体检医生站---------
    @RequestMapping("/doctorStationNew")
    public ModelAndView show_doctorstation(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String hosnum = basHospitals.getHosnum();

        modelMap.put("doctorName", basUser.getName());//体检医生名字
        modelMap.put("doctorId", basUser.getId());//体检医生id
        modelMap.put("hosnum", hosnum);
        String isDishDept = "";//是否区分科室参数
        String lisType = "";// lis系统厂商类型
        DBOperator db = new DBOperator();
        try {
            //获取是否区分科室
            isDishDept = ParamBuffer.getParamValue(request, basHospitals.getHosnum(), basHospitals.getNodecode(), "健康体检", "是否区分科室");
            lisType = ParamBuffer.getParamValue(request, basHospitals.getHosnum(), basHospitals.getNodecode(), "健康体检", "lis系统开发商");
            modelMap.put("isDishDept", isDishDept);
            modelMap.put("lisType", lisType);
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("pexam/doctorstation4", modelMap);
    }

    // -------2013-08-26----- 预约服务处批量条码重打--------
    @RequestMapping("/doAllCodePrint")
    public void doAllCodePrint(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");

        String hosnum = basHospitals.getHosnum();
        String shortname = basHospitals.getShortname();// 医院简称
        String nodecode = basHospitals.getNodecode();
        // String yytybm=basHospitals.getYytybm();//医院统一编码

        String deptcode = basDept.getDeptcode();
        String deptname = basDept.getDeptname();
        String userid = basUser.getId();
        String username = basUser.getName();

        Timestamp timesTamp = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyMMdd");
        String codedatestr = dateFormat2.format(new Date());

        String pexamid = request.getParameter("pexamid");// 唯一标识--个人体检编号

        DBOperator db = null;
        PrintWriter pw = null;

        String errormsg = "fail";// 返回的错误信息
        try {
            db = new DBOperator();
            pw = response.getWriter();
            int itemsNum = 0;
            String sql = "";
            String sql2 = "";
            List list = null;

            Map returnMap = new HashMap();
            List<Map> barCodeList = new ArrayList<Map>();// 收集检验条码数据
            List<Map> examBarCodeList = new ArrayList<Map>();// 收集检查条码数据

            // 获取体检人的性别--过滤出有性别要求的体检项目

            sql = "select a.*,to_char(sysdate,'yyyy')-to_char(a.dateofbirth,'yyyy') as age from pexam_mans a where a.hosnum=? and a.pexamid=? ";
            list = db.find(sql, new Object[]{hosnum, pexamid}, PexamMans.class);
            returnMap.put("pexamList", list);

            if (list != null && list.size() > 0) {// 防止重复提交
                PexamMans pexamMan = (PexamMans) list.get(0);
                String patname = pexamMan.getPatname();
                String sex = pexamMan.getSex();
                String age = pexamMan.getAge();

                List list_lis = null;
                sql = "select * from pexam_items_title a where a.hosnum = ? and a.pexamid = ? and a.comclass in ('检验','外送') ";
                list_lis = db.find(sql, new Object[]{hosnum, pexamid});
                for (int j = 0; j < list_lis.size(); j++) {
                    System.out.println(list_lis.size());
                    Map mm_lis = (Map) list_lis.get(j);
                    Map barCodeMap = new HashMap();
                    barCodeMap.put("shortname", shortname);// 医院简写
                    barCodeMap.put("pexamid", pexamid);// 体检编号
                    barCodeMap.put("excdeptname", mm_lis.get("excdeptname"));// 执行科室

                    barCodeMap.put("itemname", mm_lis.get("itemname"));// 大项名称
                    barCodeMap.put("itemuuid", mm_lis.get("tmcode"));// 主键--即条码
                    barCodeMap.put("doctorname", username);// 开单医生
                    barCodeMap.put("patientname", patname);// 体检人姓名
                    barCodeMap.put("sex", sex);// 性别
                    barCodeMap.put("age", age);// 年龄
                    barCodeMap.put("printdate", dateFormat.format(new Date()));// 打印时间
                    barCodeList.add(barCodeMap);
                }

            }
            returnMap.put("barCodeList", barCodeList);
            pw.print(JSONObject.fromObject(returnMap).toString());
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            pw.print(errormsg);
        } finally {
            db.freeCon();

        }
        pw.flush();
        pw.close();
    }

    //获取图片路径
    @RequestMapping(value = "/qmimg")
    public void qmimg(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String doctorId = basUser.getId();
        String pexamid = request.getParameter("pexamid");
        //System.out.println(doctorId);
        PrintWriter pw = null;
        DBOperator db = null;
        String sql = "";
        try {
            pw = response.getWriter();
            db = new DBOperator();
            sql = "select * from pexam_deptsum where pexamid = ?";
            List<Map> list = db.find(sql, new Object[]{pexamid});
            if (list.size() > 0) {
                doctorId = (String) list.get(0).get("doctorid");
            }
            sql = "select img from bas_user where id='" + doctorId + "'";
            Map getimgMap = (Map) db.findOne(sql);
            pw.print(JSONObject.fromObject(getimgMap).toString());
            //System.out.println(JSONObject.fromObject(getimgMap).toString());
            db.commit();

        } catch (Exception e) {
            db.rollback();
            pw.print("fail");
            e.printStackTrace();
        } finally {
            db.freeCon();
        }
        pw.flush();
        pw.close();
    }

    public Map getListDataByItemuuid(String hosnum, String pexamid, String itemuuid, DBOperator db2, String lisType) throws Exception {
        Map lisDataMap = new HashMap();
        String sql = "";
        if (lisType != null && lisType.equals("mq")) {
            sql = "select * from lis_testback_mq a where a.hosnum=? and a.patientid=? and a.barcode=?  and a.stopflag is null and a.stopflagtime is null order by a.orderno";
        } else if (lisType != null && lisType.equals("lj")) {
            sql = "select * from lis_testback_lj a where a.hosnum=? and a.patientid=? and a.barcode=?  and a.stopflag is null and a.stopflagtime is null order by a.orderno";
        } else {
            sql = "select * from lis_testback a where a.hosnum=? and a.patientid=? and a.barcode=? and a.downloadflag is null order by a.orderno";
        }
        List<Map> lisDataList = db2.find(sql, new Object[]{hosnum, pexamid, itemuuid});
        String tempItemuuid = "";
        if (lisDataList != null && lisDataList.size() > 0) {//数据未下载过
            Map tempMap = lisDataList.get(0);
            lisDataMap.put("status", "未下载");
            lisDataMap.put("excdoctorid", "");//化验医生id
            lisDataMap.put("excdoctorname", tempMap.get("operater"));//化验医生姓名
            lisDataMap.put("excdate", tempMap.get("operatedate"));//化验日期
            lisDataMap.put("checkdoctorid", "");//审核医生id
            lisDataMap.put("checkdoctorname", tempMap.get("checker"));//审核医生姓名
            lisDataMap.put("checkdate", tempMap.get("aduitdate"));//审核时间
            lisDataMap.put("comid", tempMap.get("itemid"));//大项id
            lisDataMap.put("comname", tempMap.get("checkitem"));//大项名称
            List resultList = new ArrayList();
            Map resultMap = null;
            for (Map map : lisDataList) {
                resultMap = new HashMap();

                resultMap.put("indid", map.get("checkitemid"));//指标id
                resultMap.put("indname", map.get("checkitemname"));//指标名称
                resultMap.put("result", map.get("result"));//结果
                resultMap.put("resultunit", map.get("unit"));//单位
                resultMap.put("rstatus", map.get("resultstatus"));//标志
                resultMap.put("minvalue", "");//下限
                resultMap.put("maxvalue", "");//上限
                resultMap.put("range", map.get("reference"));//参考值
                resultMap.put("sn", map.get("orderno"));//排序

                resultList.add(resultMap);
            }
            lisDataMap.put("resultList", resultList);
        } else {
            //sql = "select * from lis_testback a where a.hosnum=? and a.patientid=? and a.barcode=?";//是否有数据来判断是否化验已开始
            if (lisType != null && lisType.equals("mq")) {
                sql = "select * from lis_testback_mq a where a.hosnum=? and a.patientid=? and a.barcode=?  and a.stopflag is null and a.stopflagtime is null order by a.orderno";
            } else if (lisType != null && lisType.equals("lj")) {
                sql = "select * from lis_testback_lj a where a.hosnum=? and a.patientid=? and a.barcode=?  and a.stopflag is null and a.stopflagtime is null order by a.orderno";
            } else {
                sql = "select * from lis_testback a where a.hosnum=? and a.patientid=? and a.barcode=? order by a.orderno";
            }

            List<Map> isLisStart = db2.find(sql, new Object[]{hosnum, pexamid, itemuuid});
            if (isLisStart != null && isLisStart.size() > 0) {
                Map tempMap = (Map) isLisStart.get(0);
                lisDataMap.put("excdoctorid", "");//化验医生id
                lisDataMap.put("excdoctorname", tempMap.get("operater"));//化验医生姓名
                lisDataMap.put("excdate", tempMap.get("operatedate"));//化验日期
                lisDataMap.put("checkdoctorid", "");//审核医生id
                lisDataMap.put("checkdoctorname", tempMap.get("checker"));//审核医生姓名
                lisDataMap.put("checkdate", tempMap.get("aduitdate"));//审核时间
                lisDataMap.put("comid", tempMap.get("itemid"));//大项id
                lisDataMap.put("comname", tempMap.get("checkitem"));//大项名称
                List resultList = new ArrayList();
                Map resultMap = null;
                for (Map map : isLisStart) {
                    resultMap = new HashMap();

                    resultMap.put("indid", map.get("checkitemid"));//指标id
                    resultMap.put("indname", map.get("checkitemname"));//指标名称
                    resultMap.put("result", map.get("result"));//结果
                    resultMap.put("resultunit", map.get("unit"));//单位
                    resultMap.put("rstatus", map.get("resultstatus"));//标志
                    resultMap.put("minvalue", "");//下限
                    resultMap.put("maxvalue", "");//上限
                    resultMap.put("range", map.get("reference"));//参考值
                    resultMap.put("sn", map.get("orderno"));//排序

                    resultList.add(resultMap);
                }
                lisDataMap.put("resultList", resultList);
                lisDataMap.put("status", "已下载");
            } else {
                lisDataMap.put("status", "未开始");
            }
        }
        return lisDataMap;
    }

    //开始体检打印导诊单   或者  重打导诊单子的时候调用 。
    @RequestMapping("/getItemNameByPexamId")
    public void getItemNameByPexamId(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        String pexamId = request.getParameter("pexamid");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            Map map = new HashMap();
            String sql_a = "";
            List<Map> list_a = null;
            String sql = "select '' num,c.excdeptname,c.cqch,  c.needtime,t.groupname,c.wxts,c.sfws,c.bookname,t.comclass,d.deptname,t.itemcode, t.afterhb_name  itemname,t.tmcode, c.xgys from pexam_items_title t left join pexam_items_com c on t.itemcode = c.comid and t.hosnum = c.hosnum left join pexam_items_type p on c.parentid=p.typeid and c.hosnum=p.hosnum left join bas_dept d on c.excdept = d.deptcode where t.hosnum=? and t.pexamid=?  and t.parent_comid is null  and c.cqch='餐前'   and t.groupname is not null and c.delflag!='y' order by c.excdeptname desc ,c.xgys  ";
            List<Map> list = new ArrayList<Map>();
            list = db.find(sql, new Object[]{hosnum, pexamId});
            map.put("cq_list", list); //餐前list
            //插入对应 的科室数量
            sql_a = "select count(c.excdeptname) num, c.excdeptname from pexam_items_title t left join pexam_items_com c on t.itemcode = c.comid and t.hosnum = c.hosnum where t.hosnum = '" + hosnum + "' and t.pexamid = '" + pexamId + "' and t.parent_comid is null and c.cqch='餐前'  and t.groupname is not null  and c.delflag!='y' group  by c.excdeptname";
            list_a = db.find(sql_a);
            for (Iterator iter = list.iterator(); iter.hasNext(); ) {
                Map map0 = (Map) iter.next();
                for (Map map2 : list_a) {
                    if (map0.get("excdeptname").equals(map2.get("excdeptname"))) {
                        map0.put("num", map2.get("num"));
                    }
                }
            }


            sql = "select '' num,c.excdeptname,c.cqch,  c.needtime,t.groupname,c.wxts,c.sfws,c.bookname,t.comclass,d.deptname,t.itemcode, t.afterhb_name  itemname,t.tmcode, c.xgys  from pexam_items_title t left join pexam_items_com c on t.itemcode = c.comid and t.hosnum = c.hosnum left join pexam_items_type p on c.parentid=p.typeid and c.hosnum=p.hosnum left join bas_dept d on c.excdept = d.deptcode where t.hosnum=? and t.pexamid=?  and t.parent_comid is null  and c.cqch='餐后' and t.groupname is not null and c.delflag!='y'   order by c.excdeptname desc ,t.tmcode  ";
            list = db.find(sql, new Object[]{hosnum, pexamId});
            map.put("ch_list", list); //餐后list
            //插入对应 的科室数量
            sql_a = "select count(c.excdeptname) num, c.excdeptname from pexam_items_title t left join pexam_items_com c on t.itemcode = c.comid and t.hosnum = c.hosnum where t.hosnum = '" + hosnum + "' and t.pexamid = '" + pexamId + "' and t.parent_comid is null and c.cqch='餐后'  and t.groupname is not null and c.delflag!='y'  group by c.excdeptname";
            list_a = db.find(sql_a);
            for (Iterator iter = list.iterator(); iter.hasNext(); ) {
                Map map0 = (Map) iter.next();
                for (Map map2 : list_a) {
                    if (map0.get("excdeptname").equals(map2.get("excdeptname"))) {
                        map0.put("num", map2.get("num"));
                    }
                }
            }
            //加项
            sql = "select '' num, c.excdeptname, c.cqch, c.needtime, t.groupname, c.wxts, c.sfws, c.bookname, t.comclass, d.deptname, t.itemcode, t.afterhb_name itemname, t.tmcode from pexam_items_title t left join pexam_items_com c on t.itemcode = c.comid and t.hosnum = c.hosnum left join pexam_items_type p on c.parentid = p.typeid and c.hosnum = p.hosnum left join bas_dept d on c.excdept = d.deptcode where t.hosnum = '" + hosnum + "' and t.pexamid = '" + pexamId + "' and t.parent_comid is null and t.groupname is null and c.delflag!='y' order by c.excdeptname desc ,t.tmcode";
            list = db.find(sql);
            map.put("jx_list", list);  //加项list
            /**
             while (it.hasNext()) {
             Map map=(Map)it.next();
             if("检验".equals(map.get("comclass"))){
             if(t>0){
             String upCode=String.valueOf(list.get(t).get("tmcode"));
             if(upCode.equals(map.get("tmcode"))){
             list.get(t).put("itemname", list.get(t).get("itemname").toString()+"+"+map.get("itemname"));
             it.remove();
             continue;
             }
             }
             }
             t++;
             }*/
            //这里查找到 检验科的 项目 中  采血的和不采血的数量。。
            sql = " select count(*) cx from pexam_items_title t left join pexam_items_com c on t.itemcode = c.comid and t.hosnum = c.hosnum where t.hosnum = ? and t.pexamid = ? and t.parent_comid is null and t.comclass = '检验' and t.excdeptname like '%检验%'   and c.xgys is not null and c.delflag!='y' union all select count(*) cx from pexam_items_title t left join pexam_items_com c on t.itemcode = c.comid and t.hosnum = c.hosnum where t.hosnum = ? and t.pexamid = ? and t.parent_comid is null and t.comclass = '检验' and t.excdeptname like '%检验%' and c.xgys is null and c.delflag!='y' ";
            list = db.find(sql, new Object[]{hosnum, pexamId, hosnum, pexamId});
            map.put("cx_list", list);

            pw.print(JSONObject.fromObject(map).toString());
        } catch (Exception e) {
            e.printStackTrace();
            pw.print("fail");
        } finally {
            db.freeCon();
            pw.flush();
            pw.close();

        }
    }

    //此方法 作废（无用）
    @RequestMapping("/getItemNameByPexamId1")
    public void getItemNameByPexamId1(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        String pexamId = request.getParameter("pexamid");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            Map map = new HashMap();
            String sql_a = "";
            List<Map> list_a = null;
            String sql = "select '' num,c.excdeptname,c.cqch,  c.needtime,t.groupname,c.wxts,c.sfws,c.bookname,t.comclass,d.deptname,t.itemcode, t.afterhb_name  itemname,t.tmcode from pexam_items_title_print t left join pexam_items_com c on t.itemcode = c.comid and t.hosnum = c.hosnum left join pexam_items_type p on c.parentid=p.typeid and c.hosnum=p.hosnum left join bas_dept d on c.excdept = d.deptcode where t.hosnum=? and t.pexamid=?  and t.parent_comid is null  and c.cqch='餐前'   and t.groupname is not null  order by c.excdeptname desc   ";
            List<Map> list = new ArrayList<Map>();
            list = db.find(sql, new Object[]{hosnum, pexamId});
            map.put("cq_list", list); //餐前list
            //插入对应 的科室数量
            sql_a = "select count(c.excdeptname) num, c.excdeptname from pexam_items_title_print t left join pexam_items_com c on t.itemcode = c.comid and t.hosnum = c.hosnum where t.hosnum = '" + hosnum + "' and t.pexamid = '" + pexamId + "' and t.parent_comid is null and c.cqch='餐前'  and t.groupname is not null group by c.excdeptname";
            list_a = db.find(sql_a);
            for (Iterator iter = list.iterator(); iter.hasNext(); ) {
                Map map0 = (Map) iter.next();
                for (Map map2 : list_a) {
                    if (map0.get("excdeptname").equals(map2.get("excdeptname"))) {
                        map0.put("num", map2.get("num"));
                    }
                }
            }


            sql = "select '' num,c.excdeptname,c.cqch,  c.needtime,t.groupname,c.wxts,c.sfws,c.bookname,t.comclass,d.deptname,t.itemcode, t.afterhb_name  itemname,t.tmcode from pexam_items_title_print t left join pexam_items_com c on t.itemcode = c.comid and t.hosnum = c.hosnum left join pexam_items_type p on c.parentid=p.typeid and c.hosnum=p.hosnum left join bas_dept d on c.excdept = d.deptcode where t.hosnum=? and t.pexamid=?  and t.parent_comid is null  and c.cqch='餐后' and t.groupname is not null  order by c.excdeptname desc  ";
            list = db.find(sql, new Object[]{hosnum, pexamId});
            map.put("ch_list", list); //餐后list
            //插入对应 的科室数量
            sql_a = "select count(c.excdeptname) num, c.excdeptname from pexam_items_title_print t left join pexam_items_com c on t.itemcode = c.comid and t.hosnum = c.hosnum where t.hosnum = '" + hosnum + "' and t.pexamid = '" + pexamId + "' and t.parent_comid is null and c.cqch='餐后'  and t.groupname is not null   group by c.excdeptname";
            list_a = db.find(sql_a);
            for (Iterator iter = list.iterator(); iter.hasNext(); ) {
                Map map0 = (Map) iter.next();
                for (Map map2 : list_a) {
                    if (map0.get("excdeptname").equals(map2.get("excdeptname"))) {
                        map0.put("num", map2.get("num"));
                    }
                }
            }
            //加项
            sql = "select '' num, c.excdeptname, c.cqch, c.needtime, t.groupname, c.wxts, c.sfws, c.bookname, t.comclass, d.deptname, t.itemcode, t.afterhb_name itemname, t.tmcode from pexam_items_title_print t left join pexam_items_com c on t.itemcode = c.comid and t.hosnum = c.hosnum left join pexam_items_type p on c.parentid = p.typeid and c.hosnum = p.hosnum left join bas_dept d on c.excdept = d.deptcode where t.hosnum = '" + hosnum + "' and t.pexamid = '" + pexamId + "' and t.parent_comid is null and t.groupname is null order by c.excdeptname desc ";
            list = db.find(sql);
            map.put("jx_list", list);  //加项list


            pw.print(JSONObject.fromObject(map).toString());
        } catch (Exception e) {
            e.printStackTrace();
            pw.print("fail");
        } finally {
            db.freeCon();
            pw.flush();
            pw.close();

        }
    }

    @RequestMapping("/getGroupByPexamId")
    public void getGroupByPexamId(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        String pexamId = request.getParameter("pexamid");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "select rownum rn ,to_char(b.adddate,'yyyymmdd') bdate1,b.* from pexam_mans b where b.examid in ( select a.examid from pexam_mans a where a.pexamid='" + pexamId + "' and a.hosnum='" + hosnum + "')    order by b.bdate ";
            List<Map> list = new ArrayList<Map>();
            list = db.find(sql);
            //查套餐的取工作日时间
            sql = "select * from pexam_items_group g where g.groupid in (select a.itemid from pexam_items  a where a.pexamid='" + pexamId + "' and a.isgroup='y'  )";
            List<Map> list_day = db.find(sql);
            String workday = list_day.get(0).get("workday") == null ? "1" : list_day.get(0).get("workday").toString();//工作日
            String tjxh = "";
            Date tjDate = new Date();
            //得到体检序号
            if (ListUtil.listIsNotEmpty(list)) {
                String time1 = list.get(0).get("bdate1").toString();
                tjDate = StrUtil.strToDate(time1, "yyyyMMdd");
                for (Map map : list) {
                    if (pexamId.equals(map.get("pexamid"))) {
                        tjxh = time1 + "-00" + map.get("rn").toString();
                        break;
                    }
                }
            }
            //查套餐名称
            sql = "select a.itemname from pexam_items a where a.hosnum=?   and a.pexamid=?  and a.isgroup='y' ";
            list = db.find(sql, new Object[]{hosnum, pexamId});
            String groupname = list.get(0).get("itemname").toString();

            String getpage_time = "";
            Calendar after = null;
            Calendar now = Calendar.getInstance();
            now.setTime(tjDate);//设置体检日期
            Date dd = DateUtil.getworkday(tjDate, Integer.parseInt(workday));
            after = DateUtil.DatToeCal(dd);
            getpage_time = after.get(Calendar.YEAR) + "年" + (after.get(Calendar.MONTH) + 1) + "月" + after.get(Calendar.DATE) + "日(" + workday + "个工作日)";
            System.out.println("取报告日期：" + getpage_time);
            pw.print(tjxh + "," + getpage_time + "," + groupname);
        } catch (Exception e) {
            e.printStackTrace();
            pw.print("fail");
        } finally {
            db.freeCon();
            pw.flush();
            pw.close();


        }
    }

    @RequestMapping("/getGroupByPexamId1")
    public void getGroupByPexamId1(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        String pexamId = request.getParameter("pexamid");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            //添加的时间 adddate
            String sql = "select rownum rn ,to_char(b.adddate,'yyyymmdd') bdate1,to_char(m.bookdate, 'yyyymmdd') bookdate,b.* from pexam_mans b left join pexam_main m on m.examid=b.examid where b.examid in ( select a.examid from pexam_mans a where a.pexamid='" + pexamId + "' and a.hosnum='" + hosnum + "')   ";
            List<Map> list = new ArrayList<Map>();
            list = db.find(sql);
            //查套餐的取工作日时间
            sql = "select * from pexam_items_group g where g.groupid in ( select a.groupid from pexam_items_title_print a where a.pexamid='" + pexamId + "' and a.groupid is not null and rownum<=1 )";
            List<Map> list_day = db.find(sql);
            String workday = list_day.get(0).get("workday") == null ? "1" : list_day.get(0).get("workday").toString();//工作日
            String tjxh = "";
            Date tjDate = new Date();
            String lg = "";
            //得到体检序号  用rownum作为序号
            if (ListUtil.listIsNotEmpty(list)) {
                String time1 = list.get(0).get("bookdate").toString();
                tjDate = StrUtil.strToDate(time1, "yyyyMMdd");
                lg = StrUtil.dateToStr(tjDate);
                for (Map map : list) {
                    if (pexamId.equals(map.get("pexamid"))) {
                        tjxh = time1 + "-00" + map.get("rn").toString();
                        break;
                    }
                }
            }
            //查套餐名称
            sql = "select a.itemname from pexam_items a where a.hosnum=?   and a.pexamid=?  and a.isgroup='y' ";
            list = db.find(sql, new Object[]{hosnum, pexamId});
            String groupname = list.get(0).get("itemname").toString();

            String getpage_time = "";
            Calendar after = null;
            Calendar now = Calendar.getInstance();
            now.setTime(tjDate);//设置体检日期
            Date dd = DateUtil.getworkday(tjDate, Integer.parseInt(workday));
            after = DateUtil.DatToeCal(dd);
            getpage_time = after.get(Calendar.YEAR) + "年" + (after.get(Calendar.MONTH) + 1) + "月" + after.get(Calendar.DATE) + "日(" + workday + "个工作日)";
            System.out.println("取报告日期：" + getpage_time);
            pw.print(tjxh + "," + getpage_time + "," + groupname + "," + lg);
        } catch (Exception e) {
            e.printStackTrace();
            pw.print("fail");
        } finally {
            db.freeCon();
            pw.flush();
            pw.close();


        }
    }

    //============	体检报告排期查询，能查询出每天预约的人次====================
    @RequestMapping(value = "/yuyueQueryshow")
    public ModelAndView yuyueQueryshow(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        Bas_hospitals hospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        request.setAttribute("deptName", basDept.getDeptname());
        request.setAttribute("doctorName", basUser.getName());
        return new ModelAndView("/phyexam/yuyueQueryshow", modelMap);
    }

    /**
     * 分页查询
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/yuyueQuerycount", method = RequestMethod.GET)
    public void yuyueQuerycount(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        String startdate = request.getParameter("startdate");
        String enddate = request.getParameter("enddate");
        String name = request.getParameter("name");//交易流水号
        Bas_hospitals hospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = hospitals.getHosnum();
        String nodecode = hospitals.getNodecode();
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String useid = basUser.getId();
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "select count(1) num from pexam_main a   left join pexam_mans m on m.examid =a.examid   left join pexam_items i on i.pexamid=m.pexamid where to_char(a.bookdate,'yyyy-mm-dd') between ? and ?    and i.isgroup='y' and m.pexamid is not null ";
            Map result = (Map) db.findOne(sql, new Object[]{startdate, enddate});
            pw.print(result.get("num"));
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

    /**
     * 分页查询
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/yuyueQueryData", method = RequestMethod.POST)
    public void yuyueQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        String startdate = request.getParameter("startdate");
        String enddate = request.getParameter("enddate");
        int curPage = Integer.parseInt(request.getParameter("curPage"));
        int pageSize = Integer.parseInt(request.getParameter("pageSize"));
        String name = request.getParameter("name");//交易流水号
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String useid = basUser.getId();
        Bas_hospitals hospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = hospitals.getHosnum();
        String nodecode = hospitals.getNodecode();
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "select to_char(a.bookdate ,'yyyy-mm-dd') bookdate,m.pexamid,m.patname,m.sex,f_getagetype('Y',m.patientid) age,m.idnum,m.address, m.phonecall, m.guoji,m.minzu,m.whcd,m.zjxy,i.itemname from pexam_main a   left join pexam_mans m on m.examid =a.examid   left join pexam_items i on i.pexamid=m.pexamid where to_char(a.bookdate,'yyyy-mm-dd') between ? and ?    and i.isgroup='y' and m.pexamid is not null order by a.bookdate desc   ";
            sql = "select * from (select a.*,rownum num from (" + sql + ") a where rownum<=?) where num>?";
            List<Map> result = db.find(sql, new Object[]{startdate, enddate, curPage * pageSize, (curPage - 1) * pageSize});
            JSONArray jsons = JSONArray.fromObject(result);
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
    //========end=======================
}