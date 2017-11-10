package com.lsp.his.web.phyexam;

import com.lsp.his.db.DBOperator;
import com.lsp.his.model.ReturnValue;
import com.lsp.his.tables.Bas_dept;
import com.lsp.his.tables.Bas_dicts;
import com.lsp.his.tables.Bas_hospitals;
import com.lsp.his.tables.Bas_user;
import com.lsp.his.tables.pexam.PexamItemsCom;
import com.lsp.his.tables.pexam.PexamItemsInd;
import com.lsp.his.tables.pexam.PexamItemsType;
import com.lsp.his.tables.pexam.Pexam_items_group;
import com.lsp.his.utils.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/9 12:49
 */

@Controller
@RequestMapping("/phyexam")
public class PhyexamAction {

    public static int oracleType = 11;// 数据库类型

    /*
     * 项目类型设置页面
	 */
    @RequestMapping("/pexamItemsType")
    public ModelAndView pexamItemsType(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        response.setContentType("text/html;charset=utf-8");
        return new ModelAndView("phyexam/pexamItemsType", model);
    }

    /**
     * 获取最大 的数字
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/getItemFisrtNum", method = RequestMethod.POST)
    public void getItemFisrtNum(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String input = URLDecoder.decode(request.getParameter("input"), "utf-8");
        ReturnValue returnValue = new ReturnValue();
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            if (StringUtil.strIsNotEmpty(input)) {
                String[] a = input.split("\\\n");
                String[] b = a[a.length - 1].split("\\.");
                int c = Integer.parseInt(b[0]) + 1;
                pw.print(c);
            } else {
                pw.print(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            pw.print(1);
            pw.flush();
            pw.close();
        } finally {
            pw.flush();
            pw.close();
        }
    }

    @RequestMapping(value = "/itemsTypeCount", method = RequestMethod.POST)
    public void itemsTypeCount(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String typeName = request.getParameter("typeName");

        ReturnValue returnValue = new ReturnValue();
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "select count(a.typeid) as count from pexam_items_type a where a.hosnum='" + hosnum + "'";
            if (typeName != null && !"".equals(typeName)) {
                sql += " and a.typename=" + typeName;
            }
            Map<String, Integer> countMap = (Map<String, Integer>) db.findOne(sql);
            db.commit();
            returnValue.setStatus(true);
            returnValue.setValue(String.valueOf(countMap.get("count")));
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

    @RequestMapping(value = "/itemsTypeData", method = RequestMethod.POST)
    public void itemsTypeData(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String typeName = request.getParameter("typeName");
        int pageIndex = Integer.parseInt(request.getParameter("index").toString()); // 分页索引
        int pageItems = Integer.parseInt(request.getParameter("size").toString()); // 每页数量
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String pagingSql1 = "select OHYEAH.* from (select OHNO.*,rownum no from ("; // 用于分页段1
            String pagingSql2 = ") OHNO where rownum <= ?) OHYEAH where no > ?"; // 用于分页段2
            String sql = "select * from pexam_items_type a where a.hosnum='" + hosnum + "'";
            if (typeName != null && !"".equals(typeName)) {
                sql += " and a.typename=" + typeName;
            }
            sql += " order by a.sn";
            sql = pagingSql1 + sql + pagingSql2;
            List list = db.find(sql, new Object[]{pageIndex * pageItems + pageItems, pageIndex * pageItems});
            pw.print(JSONArray.fromObject(list).toString());
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

    @RequestMapping("/itemsTypeAdd")
    public ModelAndView itemsTypeAdd(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        //新增的话 operationType=add  typeId=null；
        //修改的话 operationType=modify  typeId=‘大项的id’；
        String operationType = request.getParameter("operationType");
        String typeId = request.getParameter("typeId");
        model.put("operationType", operationType);
        PexamItemsType itemsType = null;
        if ("add".equals(operationType)) {
            itemsType = new PexamItemsType();
        } else {
            DBOperator db = null;
            PrintWriter pw = null;
            try {
                db = new DBOperator();
                pw = response.getWriter();
                String sql = "select * from pexam_items_type a where a.hosnum=? and a.typeid=?";
                List<PexamItemsType> list = db.find(sql, new Object[]{hosnum, typeId}, PexamItemsType.class);
                itemsType = (PexamItemsType) ListUtil.distillFirstRow(list);
                db.commit();
            } catch (Exception e) {
                e.printStackTrace();
                db.rollback();
                pw.print("fail");
            } finally {
                db.freeCon();
            }
        }
        model.put("pexam", itemsType);
        return new ModelAndView("phyexam/itemsTypeAdd", model);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/itemsTypeCheckRepeat", method = RequestMethod.POST)
    public void itemsTypeCheckRepeat(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String typename = request.getParameter("typename");
        PrintWriter pw = null;
        pw = response.getWriter();
        if (StrUtil.strIsNotEmpty(typename)) {
            DBOperator db = null;
            try {
                db = new DBOperator();
                List rowidList = null;
                String sql = "select rowid from pexam_items_type a where a.hosnum=? and a.typename=?";
                rowidList = db.find(sql, new Object[]{hosnum, typename});
                if (ListUtil.listIsNotEmpty(rowidList)) {
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

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/itemsTypeSave", method = RequestMethod.POST)
    public void itemsTypeSave(HttpServletRequest request, HttpServletResponse response, PexamItemsType pexam) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String operationType = request.getParameter("operationType");// 操作类型：add（新增）,modify（修改）

        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            pexam.setHosnum(hosnum);
            String sql = "";
            if ("add".equals(operationType)) {
                String qry = "select max(a.typeid) maxcode from pexam_items_type a where a.hosnum=?";
                List codeList = db.find(qry, new Object[]{hosnum});
                String typeid = "";
                if (ListUtil.listIsNotEmpty(codeList) && ((Map) codeList.get(0)).get("maxcode") != null) {// 存在同级别
                    typeid = (String) ((Map) codeList.get(0)).get("maxcode");
                    int num = Integer.parseInt(typeid) + 1;// 科室编码转整数
                    if (num < 10) {
                        typeid = "0" + String.valueOf(num);
                    } else {
                        typeid = String.valueOf(num);
                    }
                } else {
                    typeid = "01";// 初始化项目检查代码
                }
                pexam.setTypeid(typeid);
                sql = "insert into pexam_items_type(hosnum,typeid,typename,sn,descriptions,comments)values(?,?,?,?,?,?)";
                db.excute(sql, new Object[]{pexam.getHosnum(), pexam.getTypeid(), pexam.getTypename(), pexam.getSn(),
                        pexam.getDescriptions(), pexam.getComments()});
            } else {
                sql = "update pexam_items_type a set a.typename=?,a.sn=?,a.descriptions=?,a.comments=? where a.hosnum=? and a.typeid=?";
                db.excute(sql, new Object[]{pexam.getTypename(), pexam.getSn(), pexam.getDescriptions(), pexam.getComments(),
                        pexam.getHosnum(), pexam.getTypeid()});
            }
            pw.print(pexam.getTypeid());
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

    @RequestMapping(value = "/itemsTypeRemoveCheck", method = RequestMethod.POST)
    public void itemsTypeRemoveCheck(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String checkIds = StrUtil.strNullToEmpty(request.getParameter("checkIds"));
        String[] Ids = checkIds.split(",");

        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            List numList = null;
            Map temp = new HashMap();
            String isExist = "N";
            for (int i = 0; i < Ids.length; i++) {
                numList = db.find("select rowid from pexam_items_ind a where a.hosnum=? and a.parentid=? and rownum=1",
                        new Object[]{hosnum, Ids[i]});
                if (ListUtil.listIsNotEmpty(numList)) {
                    isExist = "Y";
                    temp.put("rowId", Ids[i]);
                    break;
                }
            }
            temp.put("isExist", isExist);
            pw.print(JSONObject.fromObject(temp).toString());
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

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/itemsTypeRemove", method = RequestMethod.POST)
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String checkIds = StrUtil.strNullToEmpty(request.getParameter("checkIds"));
        String[] Ids = checkIds.split(",");

        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "";
            List<Object[]> pi = new ArrayList<Object[]>();
            for (int i = 0; i < Ids.length; i++) {
                pi.add(new Object[]{hosnum, Ids[i]});
            }
            Object[][] params = new Object[pi.size()][2];
            for (int i = 0; i < pi.size(); i++) {
                params[i] = pi.get(i);
            }
            sql = "delete from pexam_items_type a where a.hosnum=? and a.typeid=?";
            db.excuteBatch(sql, params);
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

    //==================================================体检项目指标设置============================================
    //TODO
    @RequestMapping("/pexamItemsInd")
    public ModelAndView pexamItemsInd(HttpServletRequest request, HttpServletResponse response, ModelMap modelmap) {
        response.setContentType("text/html;charset=utf-8");
        return new ModelAndView("phyexam/pexamItemsInd", modelmap);
    }

    /*
     * 体检项目设置加载树
	 */
    @RequestMapping("/itemsIndTree")
    public void loadLabTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
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
            sql = "select a.typeid,a.typename from pexam_items_type a where a.hosnum=? order by to_number(a.sn)";
            List tempList = db.find(sql, basHospitals.getHosnum());
            for (Iterator iterator = tempList.iterator(); iterator.hasNext(); ) {
                Map tempMap = (Map) iterator.next();
                temp = "{id:\"" + tempMap.get("typeid") + "\"," +
                        "pId:\"0\"," +
                        "name:\"" + tempMap.get("typename") + "\"}";
                lstTree.add(temp);
            }

            sql = "select a.indid,a.indname,a.parentid from pexam_items_ind a where a.hosnum=? order by to_number(a.sn)";
            tempList = db.find(sql, basHospitals.getHosnum());
            for (Iterator iterator = tempList.iterator(); iterator.hasNext(); ) {
                Map tempMap = (Map) iterator.next();
                temp = "{id:\"" + tempMap.get("indid") + "\"," +
                        "pId:\"" + tempMap.get("parentid") + "\"," +
                        "name:\"" + tempMap.get("indname") + "\"}";
                lstTree.add(temp);
            }
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

    /*
     * 加载体检项目list
	 */
    @RequestMapping(value = "/itemsIndList")
    public ModelAndView itemsIndList(HttpServletRequest request, HttpServletResponse response, ModelMap modelmap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String parentId = request.getParameter("parentId");
        if (parentId == null) {
            parentId = "";
        }
        modelmap.put("parentid", parentId);
        modelmap.put("hosnum", hosnum);
        return new ModelAndView("phyexam/itemsIndList", modelmap);
    }

    @RequestMapping("/loadItemsInd")
    public void loadItemsInd(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String parentId = request.getParameter("parentId");
        DBOperator db = null;
        PrintWriter pw = null;
        Map temp = new HashMap();//返回前台的数据
        List itemsIndList = null;
        List itemsIndList2 = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "";
            if (parentId.equals("0")) {
                sql = "select * from pexam_items_ind a where a.hosnum='1001' order by to_number(a.sn)";
            } else {
                sql = "select * from pexam_items_ind a where a.hosnum='1001' and a.parentid= '" + parentId + "' order by to_number(a.sn) ";
            }
            itemsIndList = db.find(sql);
            if (parentId.equals("0")) {
                sql = "select * from pexam_items_ind a where a.hosnum= ? order by to_number(a.sn)";
            } else {
                sql = "select * from pexam_items_ind a where a.hosnum= ? and a.parentid= '" + parentId + "' order by to_number(a.sn)";
            }
            itemsIndList2 = db.find(sql, new Object[]{hosnum});
            db.commit();
            temp.put("parentId", parentId);
            temp.put("itemsIndList", itemsIndList);//全区
            temp.put("itemsIndList2", itemsIndList2);//本院
            JSONObject obj = JSONObject.fromObject(temp);
            pw.print(obj.toString());
            //System.out.println("obj.toString()======="+obj.toString());
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

    @RequestMapping(value = "/itemsIndAdd", method = RequestMethod.GET)
    public ModelAndView itemsIndAdd(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String save_num = StrUtil.strNullToDef(request.getParameter("save_num"), "0");//保存次数
        String operationType = request.getParameter("operationType");// 操作类型：add（新增）,modify（修改）,view（查看）
        String parentid = request.getParameter("parentid");// 操作行的ID

        DBOperator db = null;
        PrintWriter pw = null;
        List<Bas_dicts> bds = null;
        PexamItemsInd details = null;
        PexamItemsInd details2 = null;
        List indResList = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "";
            bds = db.find("select * from bas_dicts t where t.hosnum = '0000' and t.nekey in(49, 41, 42, 51) and t.nevalue != '!' " +
                    "order by t.nevalue asc", Bas_dicts.class);
            List<Bas_dicts> sexList = new ArrayList<Bas_dicts>();//性别
            List<Bas_dicts> resultList = new ArrayList<Bas_dicts>();//结果类型
            List<Bas_dicts> inputList = new ArrayList<Bas_dicts>();//输入方式
            List<Bas_dicts> unitList = new ArrayList<Bas_dicts>();//数值单位
            for (Bas_dicts bd : bds) {
                if (bd.getNekey() == 49) {
                    sexList.add(bd);
                } else if (bd.getNekey() == 41) {
                    resultList.add(bd);
                } else if (bd.getNekey() == 42) {
                    inputList.add(bd);
                } else if (bd.getNekey() == 51) {
                    unitList.add(bd);
                }
            }

            modelMap.put("sexList", sexList);
            modelMap.put("resultList", resultList);
            modelMap.put("inputList", inputList);
            modelMap.put("unitList", unitList);
            modelMap.put("operationType", operationType);
            modelMap.put("save_num", save_num);
            modelMap.put("hosnum", hosnum);
            if ("add".equals(operationType)) {
                sql = "select seq_pexam.nextval from dual ";
                List<Map> tempList = db.find(sql);
//                String indId = Integer.toString((int)(double)(Double)tempList.get(0).get("nextval"));
                //---------10g转11g序号修改
                String indId = null;
                if (oracleType == 11) {
                    indId = Integer.toString(((BigDecimal) tempList.get(0).get("nextval")).intValue());
                } else {
                    indId = Integer.toString((int) (double) (Double) tempList.get(0).get("nextval"));
                }
                sql = "select max(decode(null,a.sn,0,a.sn))+1 as code from pexam_items_ind a where a.hosnum=? and a.parentid=?";
                tempList = db.find(sql, new Object[]{hosnum, parentid});
                long sn = 1;
                Map temp = tempList.get(0);
                if (temp.get("code") != null) {
                    sn = (int) ((BigDecimal) temp.get("code")).doubleValue();
                }
                details = new PexamItemsInd();
                details.setParentid(parentid);
//				details.setIndid(indId);
                details.setSn(sn);
            } else {// 修改；查看
                String indId = request.getParameter("indId");
                System.out.println("indId:=============================" + indId);
                sql = "select * from pexam_items_ind a where a.hosnum=? and a.indid=?";
                List<PexamItemsInd> itemsIndList = db.find(sql, new Object[]{"0000", indId}, PexamItemsInd.class);
                details = (PexamItemsInd) ListUtil.distillFirstRow(itemsIndList);
                itemsIndList = db.find(sql, new Object[]{hosnum, indId}, PexamItemsInd.class);
                details2 = (PexamItemsInd) ListUtil.distillFirstRow(itemsIndList);
//				sql = "select * from pexam_ind_result a where a.hosnum=? and a.indid=?";
//				indResList = db.find(sql,new Object[]{hosnum,indId});
//				modelMap.put("indResList", "var indResList = " +JSONArray.fromObject(indResList).toString());
            }
            modelMap.put("details", details);
            modelMap.put("details2", details2);
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("phyexam/itemsIndAdd", modelMap);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/saveComRes", method = RequestMethod.POST)
    public void saveComRes(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String indid = request.getParameter("indid");
        String status = request.getParameter("status");
        String comres = request.getParameter("comRes");
        String comresid = request.getParameter("comResId");

        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "";
            if (comresid != null && (!"".equals(comresid))) {//更新
                sql = "update pexam_ind_result a set a.result=?,a.unnormal=? where a.hosnum=? and a.indid=? and a.comresid=?";
                db.excute(sql, new Object[]{comres, status, hosnum, indid, comresid});
            } else {
                comresid = new UUIDGenerator().generate().toString();
                sql = "insert into pexam_ind_result(hosnum,indid,result,unnormal,comresid)values(?,?,?,?,?)";
                db.excute(sql, new Object[]{hosnum, indid, comres, status, comresid});
            }
            pw.print(comresid);
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

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/delIndRes", method = RequestMethod.GET)
    public void delIndRes(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String comresid = request.getParameter("comresid");

        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "delete from pexam_ind_result a where a.comresid=? and a.hosnum=?";
            db.excute(sql, new Object[]{comresid, hosnum});
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

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/itemsIndCheckRepeat", method = RequestMethod.POST)
    public void itemsIndCheckRepeat(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
//		Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
//		String hosnum = basHospitals.getHosnum();
        String indname = request.getParameter("indname");
        PrintWriter pw = null;
        pw = response.getWriter();
        if (StrUtil.strIsNotEmpty(indname)) {
            DBOperator db = null;
            try {
                db = new DBOperator();
                List rowidList = null;
                String sql = "select rowid from pexam_items_ind a where a.hosnum='0000' and a.indname=?";
                rowidList = db.find(sql, new Object[]{indname});
                if (ListUtil.listIsNotEmpty(rowidList)) {
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

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/itemsIndSave", method = RequestMethod.POST)
    public void itemsIndSave(HttpServletRequest request, HttpServletResponse response, PexamItemsInd pexam) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        System.out.println("==============itemsIndSave==================");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String operationType = request.getParameter("operationType");// 操作类型：add（新增）,modify（修改）
        System.out.println("indname==================" + pexam.getIndname());

        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            pexam.setHosnum(hosnum);
            String sql = "";
            if ("add".equals(operationType)) {
                sql = "insert into pexam_items_ind(hosnum,indid,indname,forsex,resultunit,maxval,minval,minpromp,maxpromp," +
                        "defaultv,comments,sn,resulttype,parentid)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                db.excute(sql, new Object[]{pexam.getHosnum(), pexam.getIndid(), pexam.getIndname(),
                        pexam.getForsex(), pexam.getResultunit(), pexam.getMaxval(),
                        pexam.getMinval(), pexam.getMinpromp(), pexam.getMaxpromp(),
                        pexam.getDefaultv(), pexam.getComments(), pexam.getSn(),
                        pexam.getResulttype(), pexam.getParentid()});
            } else {
                sql = "select * from pexam_items_ind a where a.hosnum=? and a.indid=?";
                List list = db.find(sql, new Object[]{hosnum, pexam.getIndid()});
                if (list.size() > 0) {
                    sql = "update pexam_items_ind a set a.indname=?,a.forsex=?,a.resultunit=?,a.maxval=?,a.minval=?," +
                            "a.minpromp=?,a.maxpromp=?,a.defaultv=?,a.comments=?,a.sn=?,a.resulttype=?," +
                            "a.parentid=? where a.hosnum=? and a.indid=?";
                    db.excute(sql, new Object[]{pexam.getIndname(), pexam.getForsex(),
                            pexam.getResultunit(), pexam.getMaxval(), pexam.getMinval(),
                            pexam.getMinpromp(), pexam.getMaxpromp(), pexam.getDefaultv(),
                            pexam.getComments(), pexam.getSn(), pexam.getResulttype(),
                            pexam.getParentid(), pexam.getHosnum(), pexam.getIndid()});
                } else {
                    sql = "insert into pexam_items_ind(hosnum,indid,indname,forsex,resultunit,maxval,minval,minpromp,maxpromp," +
                            "defaultv,comments,sn,resulttype,parentid)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                    db.excute(sql, new Object[]{pexam.getHosnum(), pexam.getIndid(), pexam.getIndname(),
                            pexam.getForsex(), pexam.getResultunit(), pexam.getMaxval(),
                            pexam.getMinval(), pexam.getMinpromp(), pexam.getMaxpromp(),
                            pexam.getDefaultv(), pexam.getComments(), pexam.getSn(),
                            pexam.getResulttype(), pexam.getParentid()});
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
        pw.flush();
        pw.close();
    }

    @RequestMapping(value = "/itemsIndRemoveCheck", method = RequestMethod.POST)
    public void itemsIndRemoveCheck(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String checkIds = StrUtil.strNullToEmpty(request.getParameter("checkIds"));
        String[] Ids = checkIds.split(",");

        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            List numList = null;
            Map temp = new HashMap();
            String isExist = "N";
            for (int i = 0; i < Ids.length; i++) {
                numList = db.find("select rowid from pexam_items_comdet a where a.hosnum=? and a.indid=? and rownum=1",
                        new Object[]{hosnum, Ids[i]});
                if (ListUtil.listIsNotEmpty(numList)) {
                    isExist = "Y";
                    temp.put("rowId", Ids[i]);
                    break;
                }
            }
            temp.put("isExist", isExist);
            pw.print(JSONObject.fromObject(temp).toString());
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

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/itemsIndRemove", method = RequestMethod.POST)
    public void itemsIndRemove(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String checkIds = StrUtil.strNullToEmpty(request.getParameter("checkIds"));
        String[] Ids = checkIds.split(",");

        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "";
            List<Object[]> pi = new ArrayList<Object[]>();
            for (int i = 0; i < Ids.length; i++) {
                pi.add(new Object[]{hosnum, Ids[i]});
            }
            Object[][] params = new Object[pi.size()][2];
            for (int i = 0; i < pi.size(); i++) {
                params[i] = pi.get(i);
            }
            sql = "delete from pexam_items_ind a where a.hosnum=? and a.indid=?";
            db.excuteBatch(sql, params);
            sql = "delete from pexam_ind_result a where a.hosnum=? and a.indid=?";
            db.excuteBatch(sql, params);
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

    //==================================================体检项目组合设置============================================
    @RequestMapping("/pexamItemsCom")
    public ModelAndView pexamItemsCom(HttpServletRequest request, HttpServletResponse response, ModelMap modelmap) {
        response.setContentType("text/html;charset=utf-8");

        return new ModelAndView("phyexam/pexamItemsCom", modelmap);
    }

    @RequestMapping("/itemsComTree")
    public void itemsComTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

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
            sql = "select a.typeid,a.typename from pexam_items_type a where a.hosnum='1001' order by TO_NUMBER(a.SN)";
            List tempList = db.find(sql);
            for (Iterator iterator = tempList.iterator(); iterator.hasNext(); ) {
                Map tempMap = (Map) iterator.next();
                temp = "{id:\"" + tempMap.get("typeid") + "\"," +
                        "pId:\"0\"," +
                        "name:\"" + tempMap.get("typename") + "\"}";
                lstTree.add(temp);
            }

			/*
            sql = "select a.comid,a.comname,a.parentid from pexam_items_com a where a.hosnum=? order by a.sn";
			tempList = db.find(sql,new Object[]{hosnum});
			for(Iterator iterator = tempList.iterator(); iterator.hasNext();) {
				Map tempMap = (Map)iterator.next();
				temp = "{id:\"" + tempMap.get("comid") + "\"," +
						"pId:\"" + tempMap.get("parentid") +"\"," +
						"name:\"" + tempMap.get("comname")+ "\"}";
				lstTree.add(temp);
			}
			*/
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

    @RequestMapping(value = "/itemsComList")
    public ModelAndView itemsComList(HttpServletRequest request, HttpServletResponse response, ModelMap modelmap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String parentId = request.getParameter("parentId");
        if (parentId == null) {
            parentId = "";
        }
        modelmap.put("parentid", parentId);
        return new ModelAndView("phyexam/itemsComList", modelmap);
    }

    @RequestMapping("/loadItemsCom")
    public void loadItemsCom(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String parentId = request.getParameter("parentId");
        DBOperator db = null;
        PrintWriter pw = null;
        List itemsComList = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "";
            sql = "select * from pexam_items_com a where a.hosnum=? and a.nodecode=? and a.parentid=? and a.delflag='n' order by a.sn";
            itemsComList = db.find(sql, new Object[]{hosnum, nodecode, parentId});
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        String vmpagckage = "com/cpinfo/his/template/pexam";
        String vmname = "pexamItemsCom.vm";
        String vm = VelocityUtils.generateGridVm(vmpagckage, vmname, "showList", itemsComList);
        pw.print(vm);
        pw.flush();
        pw.close();
    }

    @RequestMapping("/itemsComAdd")
    public ModelAndView doSave(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String save_num = StrUtil.strNullToDef(request.getParameter("save_num"), "0");//保存次数
        String operationType = request.getParameter("operationType");// 操作类型：add（新增）,modify（修改）,view（查看）
        String parentid = request.getParameter("parentid");

        DBOperator db = null;
        PrintWriter pw = null;
        List<Bas_dicts> bds = null;
        PexamItemsCom pexam = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "";
            bds = db.find("select * from bas_dicts t where t.hosnum = '0000' and t.nekey in(50, 49) and t.nevalue != '!' " +
                    "order by t.nevalue asc", Bas_dicts.class);
            List<Bas_dicts> classList = new ArrayList<Bas_dicts>();// 项目类别
            List<Bas_dicts> sexList = new ArrayList<Bas_dicts>();// 使用性别
            for (Bas_dicts bd : bds) {
                if (bd.getNekey() == 50) {
                    classList.add(bd);
                } else if (bd.getNekey() == 49) {
                    sexList.add(bd);
                }
            }

            modelMap.put("classList", classList);
            modelMap.put("sexList", sexList);
            modelMap.put("operationType", operationType);
            modelMap.put("save_num", save_num);
            if ("add".equals(operationType)) {
                sql = "select seq_pexam_comid.nextval from dual ";
                List<Map> tempList = db.find(sql);

                //---------10g转11g序号修改
                String comId = null;
                if (oracleType == 11) {
                    comId = Integer.toString(((BigDecimal) tempList.get(0).get("nextval")).intValue());
                } else {
                    comId = Integer.toString((int) (double) (Double) tempList.get(0).get("nextval"));
                }

                sql = "select max(decode(null,a.sn,0,a.sn))+1 as code from pexam_items_com a where a.hosnum=? and a.nodecode=? and a.parentid=?";
                tempList = db.find(sql, new Object[]{hosnum, nodecode, parentid});
                long sn = 1;
                Map temp = tempList.get(0);
                if (temp.get("code") != null) {
                    sn = (int) ((BigDecimal) temp.get("code")).doubleValue();
                }
                pexam = new PexamItemsCom();
                pexam.setParentid(parentid);
                pexam.setComid(comId);
                pexam.setSn(sn);
            } else {// 修改；查看
                String comId = request.getParameter("comId");
                sql = "select * from pexam_items_com a where a.hosnum=? and a.nodecode=? and a.comid=?";
                List<PexamItemsCom> itemsComList = db.find(sql, new Object[]{hosnum, nodecode, comId}, PexamItemsCom.class);
                pexam = (PexamItemsCom) ListUtil.distillFirstRow(itemsComList);
                //System.out.println(pexam.getXgys());
                sql = "select * from pexam_items_comdet a where a.hosnum=? and a.comid=?";
                List<Map> scopeList = db.find(sql, new Object[]{hosnum, comId});
                if (ListUtil.listIsNotEmpty(scopeList)) {
                    StringBuffer checkedScope = new StringBuffer();
                    for (Iterator<Map> iterator = scopeList.iterator(); iterator.hasNext(); ) {
                        Map scopeMap = iterator.next();
                        checkedScope.append(scopeMap.get("indid") + ";");
                    }
                    modelMap.put("checkedScope", checkedScope.toString());
                }
            }
            pexam.setHosnum(hosnum);
            modelMap.put("pexam", pexam);
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("phyexam/itemsComAdd", modelMap);
    }

    @RequestMapping(value = "/selectedIndTree", method = RequestMethod.POST)
    public void selectedIndTree(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String parentid = request.getParameter("parentid");
        String checkedScope = request.getParameter("checkedScope");// 选中的范围
        String[] checkedList = null;
        if (StrUtil.strIsNotEmpty(checkedScope)) {
            checkedList = checkedScope.split(";");
        }

        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "";
            List tempList = null;

            List<String> lstTree = new ArrayList<String>();
            String temp = "";

            sql = "select a.indid,a.parentid,a.indname,a.itemcost from pexam_items_ind a where a.hosnum=? and a.parentid=? order by a.sn";
            tempList = db.find(sql, new Object[]{hosnum, parentid});

            boolean flag1 = true;
            boolean flag2 = true;
            for (Iterator iterator = tempList.iterator(); iterator.hasNext(); ) {
                Map tempMap = (Map) iterator.next();
                flag1 = StrUtil.strIsInArrary((String) tempMap.get("indid"), checkedList);
                if (!flag1) {
                    flag2 = false;
                }
                String cost = tempMap.get("itemcost") == null ? "0" : tempMap.get("itemcost").toString();
                temp = "{id:\"" + tempMap.get("indid") + "\"," +
                        "pId:\"" + tempMap.get("parentid") + "\"," +
                        "name:\"" + tempMap.get("indname") + "\"," +
                        "cost:\"" + cost + "\"," +
                        "checked:" + flag1 + "}";
                lstTree.add(temp);
            }
            if (lstTree.size() < 1) {
                flag2 = false;
            }
            String s1 = "{id:\"" + parentid + "\", pId:-1, name:\"体检项目\",cost:\"0\",checked:" + flag2 + ",open:true}";
            lstTree.add(s1);

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

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/itemsComCheckRepeat", method = RequestMethod.POST)
    public void itemsComCheckRepeat(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String comname = request.getParameter("comname");
        PrintWriter pw = null;
        pw = response.getWriter();
        if (StrUtil.strIsNotEmpty(comname)) {
            DBOperator db = null;
            try {
                db = new DBOperator();
                List rowidList = null;
                String sql = "select rowid from pexam_items_com a where a.hosnum=? and a.comname=?";
                rowidList = db.find(sql, new Object[]{hosnum, comname});
                if (ListUtil.listIsNotEmpty(rowidList)) {
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

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/itemsComSave", method = RequestMethod.POST)
    public void itemsComSave(HttpServletRequest request, HttpServletResponse response, PexamItemsCom pexam) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String operationType = request.getParameter("operationType");// 操作类型：add（新增）,modify（修改）
        String addscopes = request.getParameter("addscopes");//新增的范围
        String removescopes = request.getParameter("removescopes");//删除的范围
        System.out.println("comid======>" + pexam.getComid());
        System.out.println("comname======>" + pexam.getComname());
        String xgys = request.getParameter("xgys");  //血管颜色
        String fpkmname = URLDecoder.decode(request.getParameter("fpkmname"), "utf-8");
        String hskmname = URLDecoder.decode(request.getParameter("hskmname"), "utf-8");
        String xgysname = URLDecoder.decode(request.getParameter("xgysname"), "utf-8");  //血管颜色 name
        String cqch = URLDecoder.decode(request.getParameter("cqch"), "utf-8");
        String memo = URLDecoder.decode(request.getParameter("memo"), "utf-8");
        pexam.setCqch(cqch);
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            pexam.setHosnum(hosnum);
            String sql = "";
            if ("add".equals(operationType)) {
                sql = "insert into pexam_items_com(hosnum,comid,comname,comclass,forsex,excdept," +
                        "excdeptname,cost,sn,descriptions,comments,parentid,isuse,nodecode,xgys, " +
                        "FPKM, HSKM, GGXM,FPKMNAME,HSKMNAME,xgysname,jyyq,tjxm, xmjc,bookname,printnum,wxts,sfws,needtime,cqch,sheettype,memo,hbmc,bgxs" +
                        ")values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                db.excute(sql, new Object[]{pexam.getHosnum(), pexam.getComid(), pexam.getComname(),
                        pexam.getComclass(), pexam.getForsex(), pexam.getExcdept(),
                        pexam.getExcdeptname(), pexam.getCost(), pexam.getSn(),
                        pexam.getDescriptions(), pexam.getComments(), pexam.getParentid(), pexam.getIsuse(), nodecode, xgys,
                        pexam.getFpkm(), pexam.getHskm(), pexam.getGgxm(), fpkmname, hskmname, xgysname, pexam.getJyyq()
                        , pexam.getTjxm(), pexam.getXmjc(), pexam.getBookname(), pexam.getPrintnum(), pexam.getWxts(), pexam.getSfws(), pexam.getNeedtime(), pexam.getCqch(), pexam.getSheettype(), memo, pexam.getHbmc(), pexam.getBgxs()});
            } else {
                sql = "update pexam_items_com a set a.comname=?,a.comclass=?,a.forsex=?,a.excdept=?," +
                        "a.excdeptname=?,a.cost=?,a.sn=?,a.descriptions=?,a.comments=?," +
                        "a.parentid=?,a.isuse=?,a.xgys=? ,a.FPKM=?,a.HSKM=?,a.GGXM=? ,a.FPKMNAME=?,a.HSKMNAME=? " +
                        ",a.xgysname=?  ,a.jyyq=? ,a.tjxm=? ,a.xmjc=?,a.bookname=?,a.printnum=?,a.wxts=?,a.sfws=?,a.needtime=?,a.cqch=?,a.sheettype=?,a.memo=?,a.hbmc=?,a.bgxs=?  " +
                        "where a.hosnum=? and a.nodecode=? and a.comid=? ";
                db.excute(sql, new Object[]{pexam.getComname(), pexam.getComclass(), pexam.getForsex(), pexam.getExcdept(),
                        pexam.getExcdeptname(), pexam.getCost(), pexam.getSn(), pexam.getDescriptions(), pexam.getComments(),
                        pexam.getParentid(), pexam.getIsuse(), xgys,
                        pexam.getFpkm(), pexam.getHskm(), pexam.getGgxm(), fpkmname, hskmname, xgysname, pexam.getJyyq(), pexam.getTjxm(), pexam.getXmjc(),
                        pexam.getBookname(), pexam.getPrintnum(), pexam.getWxts(), pexam.getSfws(), pexam.getNeedtime(), pexam.getCqch(), pexam.getSheettype(), memo, pexam.getHbmc(), pexam.getBgxs(),
                        pexam.getHosnum(), nodecode, pexam.getComid()});
                //更新 有这个组合的套餐的价格。
                List<Map> groupList = null;
                sql = "select * from pexam_items_group  a where a.groupid in (select b.groupid from pexam_items_groupdetails b where b.itemcode in ('" + pexam.getComid() + "') ) and a.hosnum='" + hosnum + "' ";
                groupList = db.find(sql);
                for (Map map2 : groupList) {
                    String groupid = map2.get("groupid").toString();
                    //套餐的价格是组合价格加起来的
                    sql = "select nvl(sum(c.cost) ,0 ) costs from pexam_items_groupdetails a left join pexam_items_com c on c.comid=a.itemcode  where a.groupid='" + groupid + "' and a.hosnum='" + hosnum + "' ";
                    List<Map> costGroupList = db.find(sql);
                    System.out.println("更新套餐价格为：" + costGroupList.get(0).get("costs"));
                    db.excute("update pexam_items_group a set a.cost = ? where a.hosnum = ? and a.groupid=? ", new Object[]{costGroupList.get(0).get("costs"), hosnum, groupid});
                    //db.commit();
                }

            }

			/*新增或者删除角色*/
            if (StrUtil.strIsNotEmpty(removescopes)) {// 批量删除范围
                String[] removeList = removescopes.split(";");
                for (int i = 0; i < removeList.length; i++) {
                    db.excute("delete from pexam_items_comdet where hosnum=? and comid=? and indid=?",
                            new Object[]{hosnum, pexam.getComid(), removeList[i]});
                }
            }
            if (StrUtil.strIsNotEmpty(addscopes)) {// 批量新增范围
                String[] addList = addscopes.split(";");
                String comdetid = new UUIDGenerator().generate().toString();
                for (int i = 0; i < addList.length; i++) {
                    db.excute("insert into pexam_items_comdet(hosnum,comdetid,comid,indid)values(?,?,?,?)",
                            new Object[]{hosnum, comdetid, pexam.getComid(), addList[i]});
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
        pw.flush();
        pw.close();
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/itemsComRemove", method = RequestMethod.POST)
    public void itemsComRemove(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String checkIds = StrUtil.strNullToEmpty(request.getParameter("checkIds"));
        String[] Ids = checkIds.split(",");

        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "";
            List<Object[]> pi = new ArrayList<Object[]>();
            for (int i = 0; i < Ids.length; i++) {
                pi.add(new Object[]{hosnum, Ids[i]});
            }
            Object[][] params = new Object[pi.size()][2];
            for (int i = 0; i < pi.size(); i++) {
                params[i] = pi.get(i);
            }
            // 软删除  把删除标记更改为 y
            sql = "update pexam_items_com a set a.delflag='y'  where a.hosnum=? and a.comid=?";
            db.excuteBatch(sql, params);
//			sql = "delete from pexam_items_com a where a.hosnum=? and a.comid=?";
//			db.excuteBatch(sql, params);
//			sql = "delete from pexam_items_comdet a where a.hosnum=? and a.comid=?";
//			db.excuteBatch(sql, params);
//			db.commit();
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

    //===================================体检套餐=====================================================
    @RequestMapping("/pexamItemsGroup")
    public ModelAndView pexamItemsGroup(HttpServletRequest request, HttpServletResponse response, ModelMap modelmap) {
        response.setContentType("text/html;charset=utf-8");
        String type = request.getParameter("type");
        request.setAttribute("type", type);
        return new ModelAndView("phyexam/pexamItemsGroup", modelmap);
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/itemsGroupTree", method = RequestMethod.POST)
    public void itemsGroupTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        List<String> lstTree = new ArrayList<String>();
        String temp = "";
        String s1 = "{id:\"0\", pId:-1, name:\"体检套餐\" , myType:\"group\",open:true}";
        lstTree.add(s1);

        DBOperator db = null;
        PrintWriter pw = null;
        List groupList = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();

            String sql = "select groupid,groupname,cost from pexam_items_group t where " +
                    " t.hosnum=? and t.nodecode=? and t.delflag='n' ";
            groupList = db.find(sql, new Object[]{hosnum, nodecode});
            for (Iterator iterator = groupList.iterator(); iterator.hasNext(); ) {//添加体检套餐
                Map tempMap = (Map) iterator.next();
                temp = "{id:\"" + tempMap.get("groupid") + "\"," +
                        "pId:\"0\"," +
                        "cost:" + tempMap.get("cost") + "," +
                        "name:\"" + tempMap.get("groupname") + "\"," +
                        "myType:\"item_0\"}";

                lstTree.add(temp);
            }
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }
        pw.print(JSONArray.fromObject(lstTree).toString());
        pw.flush();
        pw.close();
    }

    @RequestMapping("/loadItemsGroup")
    public void loadItemsGroup(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        DBOperator db = null;
        PrintWriter pw = null;
        List itemsGroupList = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "";
            sql = "select * from pexam_items_group a where a.hosnum=? and a.nodecode=? and a.delflag='n' order by a.groupname";
            itemsGroupList = db.find(sql, new Object[]{hosnum, nodecode});
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        String vmpagckage = "com/cpinfo/his/template/pexam";
        String vmname = "pexamItemsGroup.vm";
        String vm = VelocityUtils.generateGridVm(vmpagckage, vmname, "showList", itemsGroupList);
        pw.print(vm);
        pw.flush();
        pw.close();
    }

    @RequestMapping(value = "/itemsGroupList")
    public ModelAndView itemsGroupList(HttpServletRequest request, HttpServletResponse response, ModelMap modelmap) throws Exception {
        String opentype = request.getParameter("opentype");
        request.setAttribute("opentype", opentype);
        response.setContentType("text/html;charset=utf-8");
        return new ModelAndView("phyexam/itemsGroupList", modelmap);
    }

    @RequestMapping("/itemsGroupAdd")
    public ModelAndView itemsGroupAdd(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        String opentype = request.getParameter("opentype");
        request.setAttribute("opentype", opentype);
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String save_num = StrUtil.strNullToDef(request.getParameter("save_num"), "0");//保存次数
        String operationType = request.getParameter("operationType");// 操作类型：add（新增）,modify（修改）,view（查看）

        DBOperator db = null;
        PrintWriter pw = null;
        Pexam_items_group group = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "";

            modelMap.put("operationType", operationType);
            modelMap.put("save_num", save_num);
            if ("add".equals(operationType)) {
                sql = "select seq_pexam_groupid.nextval from dual ";
                List<Map> tempList = db.find(sql);

                //---------10g转11g序号修改
                String groupid = null;
                if (oracleType == 11) {
                    groupid = Integer.toString(((BigDecimal) tempList.get(0).get("nextval")).intValue());
                } else {
                    groupid = Integer.toString((int) (double) (Double) tempList.get(0).get("nextval"));
                }

                sql = "select max(decode(null,a.sn,0,a.sn))+1 as code from pexam_items_group a where a.hosnum=? and a.nodecode=? ";
                tempList = db.find(sql, new Object[]{hosnum, nodecode});
                long sn = 1;
                Map temp = tempList.get(0);
                if (temp.get("code") != null) {
                    sn = (int) ((BigDecimal) temp.get("code")).doubleValue();
                }
                group = new Pexam_items_group();
                group.setGroupid(groupid);
                group.setSn(sn);
            } else {// 修改;查看;引入
                String groupId = request.getParameter("groupId");
                sql = "select * from pexam_items_group a where a.hosnum=? and a.nodecode=? and a.groupid=?";
                List<Pexam_items_group> itemsGroupList = null;
                if (operationType.equals("introduce")) {        //如果是引入，则使用卫生局hosnum,nodecode
                    hosnum = "0000";
                    nodecode = "0000";
                }
                itemsGroupList = db.find(sql, new Object[]{hosnum, nodecode, groupId}, Pexam_items_group.class);
                group = (Pexam_items_group) ListUtil.distillFirstRow(itemsGroupList);

                sql = "select * from PEXAM_ITEMS_GROUPDETAILS a where a.hosnum=? and a.groupid=?";
                List<Map> scopeList = db.find(sql, new Object[]{hosnum, groupId});
                if (ListUtil.listIsNotEmpty(scopeList)) {
                    StringBuffer checkedScope = new StringBuffer();
                    for (Iterator<Map> iterator = scopeList.iterator(); iterator.hasNext(); ) {
                        Map scopeMap = iterator.next();
                        checkedScope.append(scopeMap.get("itemcode") + ";");
                    }
                    modelMap.put("checkedScope", checkedScope.toString());
                }
            }
            String examItem = ParamBuffer.getParamValue(request, basHospitals.getHosnum(), basHospitals.getNodecode(), "健康体检", "农民体检项目");
            if (examItem != null && !"".equals(examItem)) {
                String[] farmItems = examItem.split(";");
                sql = "select c.itemcode,c.itemname from chg_meditem c where c.itemcode in (?)";
                String itemids = "";
                for (int i = 0; i < farmItems.length; i++) {
                    itemids = itemids + "'" + farmItems[i] + "',";
                }
                if (!"".equals(itemids)) {
                    itemids = itemids.substring(0, itemids.length() - 1);
                }
                List<Map> itemlist = db.find(sql.replace("?", itemids));
                modelMap.put("farmItems", itemlist);
            }
            group.setHosnum(hosnum);
            modelMap.put("group", group);

            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("phyexam/itemsGroupAdd", modelMap);
    }

    @RequestMapping(value = "/selectedComTree", method = RequestMethod.POST)
    public void selectedComTree(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        String opentype = request.getParameter("opentype");
        request.setAttribute("opentype", opentype);
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String checkedScope = request.getParameter("checkedScope");// 选中的范围
        String[] checkedList = null;
        if (StrUtil.strIsNotEmpty(checkedScope)) {
            checkedList = checkedScope.split(";");
        }
        String operationType = request.getParameter("operationType");
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "";
            List tempList = null;

            List<String> lstTree = new ArrayList<String>();
            String temp = "";
            if (operationType.equals("introduce")) {
                hosnum = "0000";
                nodecode = "0000";
            }
            if ("1".equals(opentype)) {  //等于1，表示是护士界面，过滤加载未勾选的树
                String checkedList1 = ""; //被选中的树id
                for (int k = 0; k < checkedList.length; k++) {
                    checkedList1 += checkedList[k] + ",";
                }
                checkedList1 = checkedList1.substring(0, checkedList1.length() - 1);
                sql = "select a.comid,a.parentid,a.comname,a.memo,a.cost,a.comclass from pexam_items_com a left join pexam_items_type t on a.parentid=t.typeid where a.hosnum=? and a.nodecode=? and a.delflag='n'  and a.comid in (" + checkedList1 + ") order by to_number(t.sn), to_number(a.sn)";
            } else {
                sql = "select a.comid,a.parentid,a.comname,a.memo,a.cost,a.comclass from pexam_items_com a left join pexam_items_type t on a.parentid=t.typeid where a.hosnum=? and a.nodecode=? and a.delflag='n'  order by to_number(t.sn), to_number(a.sn)";
            }
            tempList = db.find(sql, new Object[]{hosnum, nodecode});

            boolean flag1 = true;
            boolean flag2 = true;
            for (Iterator iterator = tempList.iterator(); iterator.hasNext(); ) {
                Map tempMap = (Map) iterator.next();
                flag1 = StrUtil.strIsInArrary((String) tempMap.get("comid"), checkedList);
                if (!flag1) {
                    flag2 = false;
                }
                String memo = (String) tempMap.get("memo");
                String comclass = tempMap.get("comclass").toString();
                if (memo != null) {
                    temp = "{id:\"" + tempMap.get("comid") + "\"," +
                            "pId:\"0\"," +
                            "highlight:false," +
                            "cost:" + tempMap.get("cost") + "," +
                            "name:\"" + tempMap.get("comname") + "(" + tempMap.get("memo") + ")" + "\"," +
                            "checked:" + flag1 + "}";
                } else {
                    if ("检验".equals(comclass)) {
                        temp = "{id:\"" + tempMap.get("comid") + "\"," +
                                "pId:\"0\"," +
                                "highlight:false," +
                                "cost:" + tempMap.get("cost") + "," +
                                "name:\"" + tempMap.get("comname") + "(" + tempMap.get("comid") + ")" + "\"," +
                                "checked:" + flag1 + "}";
                    } else {
                        temp = "{id:\"" + tempMap.get("comid") + "\"," +
                                "pId:\"0\"," +
                                "highlight:false," +
                                "cost:" + tempMap.get("cost") + "," +
                                "name:\"" + tempMap.get("comname") + "\"," +
                                "checked:" + flag1 + "}";
                    }
                }


                lstTree.add(temp);
            }
            if (lstTree.size() < 1) {
                flag2 = false;
            }
            String s1 = "{id:\"0\", pId:-1, name:\"体检项目\",checked:" + flag2 + ",cost:\"0\",open:true}";
            lstTree.add(s1);

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

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/itemsGroupCheckRepeat", method = RequestMethod.POST)
    public void itemsGroupCheckRepeat(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String groupname = request.getParameter("groupname");
        PrintWriter pw = null;
        pw = response.getWriter();
        if (StrUtil.strIsNotEmpty(groupname)) {
            DBOperator db = null;
            try {
                db = new DBOperator();
                List rowidList = null;
                String sql = "select rowid from pexam_items_group a where a.hosnum=? and a.groupname=?";
                rowidList = db.find(sql, new Object[]{hosnum, groupname});
                if (ListUtil.listIsNotEmpty(rowidList)) {
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

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/itemsGroupSave", method = RequestMethod.POST)
    public void itemsGroupSave(HttpServletRequest request, HttpServletResponse response, Pexam_items_group group) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String operationType = request.getParameter("operationType");// 操作类型：add（新增）,modify（修改）
        String addscopes = request.getParameter("addscopes");//新增的范围
        String removescopes = request.getParameter("removescopes");//删除的范围
        String farmItemSelect = request.getParameter("farmItemSelect");//社保项目
        String yhblSelect = request.getParameter("yhblSelect");  //体检优惠比例
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            group.setHosnum(hosnum);
            String sql = "";
            if ("add".equals(operationType)) {
                if (hosnum.equals("0000")) {
                    sql = "insert into pexam_items_group(HOSNUM,nodecode,GROUPID,GROUPNAME,COST,COMMENTS,FARMITEM,yhbl,workday,sprice) values(?,?,?,?,?,?,?,?,?,?)";
                } else {
                    sql = "insert into pexam_items_group(HOSNUM,nodecode,GROUPID,GROUPNAME,COST,COMMENTS,FARMITEM,yhbl,workday,sprice) values(?,?,?,?,?,?,?,?,?,?)";
                }
                db.excute(sql, new Object[]{group.getHosnum(), nodecode, group.getGroupid(), group.getGroupname(), group.getCost(), group.getComments(), farmItemSelect, yhblSelect, group.getWorkday(), group.getSprice()});
            } else {
                if (hosnum.equals("0000")) {
                    sql = "update pexam_items_group set sprice=?, GROUPNAME=?,COST=?,COMMENTS=?,FARMITEM=?  ,yhbl=?,workday=?  where hosnum=? and nodecode=? and groupid=?";
                } else {
                    sql = "update pexam_items_group set sprice=?, GROUPNAME=?,COST=?,COMMENTS=?,FARMITEM=? ,yhbl=?,workday=?  where hosnum=? and nodecode=? and groupid=?";
                }
                db.excute(sql, new Object[]{group.getSprice(), group.getGroupname(), group.getCost(), group.getComments(), farmItemSelect, yhblSelect, group.getWorkday(), group.getHosnum(), nodecode, group.getGroupid()});
            }

			/*新增或者删除角色*/
            if (StrUtil.strIsNotEmpty(removescopes)) {// 批量删除范围
                String[] removeList = removescopes.split(";");
                for (int i = 0; i < removeList.length; i++) {
                    db.excute("delete from pexam_items_groupdetails where hosnum=? and groupid=? and itemcode=?",
                            new Object[]{hosnum, group.getGroupid(), removeList[i]});
                }
            }
            if (StrUtil.strIsNotEmpty(addscopes)) {// 批量新增范围
                String[] addList = addscopes.split(";");
                //String comdetid = new UUIDGenerator().generate().toString();
                for (int i = 0; i < addList.length; i++) {
                    db.excute("insert into pexam_items_groupdetails(HOSNUM,groupid,itemcode,sn) values(?,?,?,?)",
                            new Object[]{hosnum, group.getGroupid(), addList[i], 1});
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
        pw.flush();
        pw.close();
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/itemsGroupRemove", method = RequestMethod.POST)
    public void itemsGroupRemove(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String checkIds = StrUtil.strNullToEmpty(request.getParameter("checkIds"));
        String[] Ids = checkIds.split(",");

        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            for (int i = 0; i < Ids.length; i++) {
                db.excute("update pexam_items_group b  set b.delflag='y' where b.hosnum=? and b.groupid=?", new Object[]{hosnum, Ids[i]});//删除关系表
//				db.excute("delete from pexam_items_groupdetails b where b.hosnum=? and b.groupid=?",new Object[]{hosnum,Ids[i]});//删除关系表
//				db.excute("delete from pexam_items_group a where a.hosnum=? and a.groupid=?",new Object[]{hosnum,Ids[i]});
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
    }

    //======================================后台参数配置结束=============================
    //======================================预约服务=====================================
    /*
     * 加载新建体检人员信息
	 */
    @RequestMapping("/personInfoEntry")
    public ModelAndView show_personInfoEntry(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String examid = request.getParameter("examid");
        String pexamid = request.getParameter("pexamid");
        String unit = request.getParameter("unit");//体检名称
        unit = (unit == null ? "" : URLDecoder.decode(unit, "utf-8"));
        String examtype = request.getParameter("examtype");//体检类型
        examtype = (examtype == null ? "" : URLDecoder.decode(examtype, "utf-8"));
        String str = request.getParameter("str");//判断是从哪个页面调这个弹出窗口
        modelMap.put("str", str);
        modelMap.put("unit", unit);
        modelMap.put("examtype", examtype);
        modelMap.put("examid", examid);
        DBOperator db = new DBOperator();
        try {
            //套餐信息
            String sql = " select a.groupid,a.groupname,a.cost from pexam_items_group a where a.hosnum=? and a.nodecode=? and a.delflag='n' ";
            List<Pexam_items_group> listgroup = db.find(sql, new Object[]{hosnum, nodecode}, Pexam_items_group.class);
            //单项目信息
            String sqlitems = "select a.comid,a.comname,a.cost from pexam_items_com a where a.hosnum=? and a.nodecode=? and a.delflag='n'";
            List<PexamItemsCom> listitems = db.find(sqlitems, new Object[]{hosnum, nodecode}, PexamItemsCom.class);
            modelMap.put("listitems", listitems);
            modelMap.put("listgroup", listgroup);
            if (pexamid != null) {
                //查看 --
                sql = "select a.patientid,a.examid,a.pexamid,a.idtype,a.idnum,a.patname,to_char(a.dateofbirth,'yyyy-mm-dd') as dateofbirth,a.sex, "
                        + "a.maritalstatus,a.professional,a.ybbh ,a.province,a.city,a.county,a.township,a.village,a.laddress,a.phonecall,address" +
                        "  ,MINZU, GUOJI, WHCD, ZJXY, KWXD, WORDINCOMPUTER, SHUIMIAN, YENIAO, TSYS,zkl,wordaddress "
                        + "from pexam_mans a "
                        + "where a.hosnum=? and a.examid=? and a.pexamid=?";
                List list = db.find(sql, new Object[]{hosnum, examid, pexamid});//体检人信息
                request.setAttribute("listbase", "var listbase=" + JSONArray.fromObject(list.get(0)).toString());

                sql = "select a.examname,a.examtype from pexam_main a where a.hosnum=? and a.examid=?";
                List listinfo = db.find(sql, new Object[]{hosnum, examid});//体检名字及体检类型
                request.setAttribute("listinfo", "var listinfo=" + JSONArray.fromObject(listinfo.get(0)).toString());
                modelMap.put("pexamid", pexamid);
            } else {
                //新增
                sql = "select seq_pexamid.nextval from dual";

                //---------10g转11g序号修改
                String indId = null;
                System.out.println("oracleType----------" + oracleType);
                if (oracleType == 11) {
                    Map<String, BigDecimal> seq_id = (Map<String, BigDecimal>) db.findOne(sql);
                    request.setAttribute("pexamid", seq_id.get("nextval").longValue());
                } else {
                    Map<String, Double> seq_id = (Map<String, Double>) db.findOne(sql);
                    request.setAttribute("pexamid", seq_id.get("nextval").longValue());
                }


                request.setAttribute("examid", examid);
            }
            db.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("phyexam/personInfoEntry", modelMap);
    }

    //获取单位列表
    @SuppressWarnings("unchecked")
    @RequestMapping("/getUnitList")
    public void getUnitList(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        DBOperator db = new DBOperator();
        PrintWriter pw = null;
        try {
            String sql = "select * from pexam_main pm where pm.hosnum=? order by pm.examid ";
            List<Map> unitlist = db.find(sql, new Object[]{hosnum});

            pw = response.getWriter();
            pw.print(JSONArray.fromObject(unitlist).toString());
            pw.flush();
            pw.close();
            db.commit();

        } catch (RuntimeException e) {
            pw.print("fail");
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
    }

    @RequestMapping("/SearchItemsList")
    public void SearchItemsList(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        DBOperator db = null;
        PrintWriter pw = response.getWriter();
        Bas_dept bd = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_hospitals bh = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_user bu = (Bas_user) request.getSession().getAttribute("login_user");
        String sql = "";
        List<Map> list = null;
        String input = URLDecoder.decode(request.getParameter("input"), "UTF-8");
        try {
            db = new DBOperator();
            Map map = new HashMap();
            input = input.toUpperCase();
            sql = "select a.comid,a.comname,a.cost from pexam_items_com a where (a.comname like '%" + input + "%' or a.inputcpy like '%" + input + "%' or a.inputcwb like '%" + input + "%') and a.delflag='n'";
            list = db.find(sql, new Object[]{});
            pw.print(JSONArray.fromObject(list).toString());
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
     * 保存新建体检人员
     * 启用crm只会更新套餐加项    否则增删都会
     */
    @RequestMapping("/savepatient")
    public void savePatient(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        String str = request.getParameter("str");
        String database_version = (String) request.getSession().getAttribute("database_version");
        String patname = "";
        String sex = "";
        Date dateofbirth = null;
        String professional = "";
        String maritalstatus = "";
        String inscardno = "";//医疗卡号
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
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        String deptcode = basDept.getDeptcode();
        String deptname = basDept.getDeptname();
        String jyjgjm = basHospitals.getJyjgjm();//检验机构简码
        jyjgjm = "";
        System.out.println("----jyjgjm-----" + jyjgjm);
        String userid = basUser.getId();
        String username = basUser.getName();
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String sn = "";
        String examid = "";
        String id = "";
        String phonecall = "";
        String province = "";
        String city = "";
        String county = "";
        String township = "";
        String village = "";
        String laddress = "";
        String unit = "";
        String examtype = "";
        String homeaddress = "";
        String itemsinfo = request.getParameter("itemsinfo");
        JSONArray jsons = JSONArray.fromObject(itemsinfo);
        String json1Str = URLDecoder.decode(request.getParameter("json1"), "utf-8");
        JSONArray json1 = JSONArray.fromObject(json1Str);
        JSONObject json = json1.getJSONObject(0);
        examid = json.getString("examid");
        pexamid = json.getString("pexamid");
        idtype = json.getString("idtype");
        idnum = json.getString("idnum");
        inscardno = json.getString("inscardno");
        patname = json.getString("patname");
        //新增的9个字段
        String minzu = json.getString("minzu"); //民族
        String guoji = json.getString("guoji"); //国籍
        String whcd = json.getString("whcd"); //文化程度
        String zjxy = json.getString("zjxy"); //宗教信仰
        String kwxd = json.getString("kwxd"); //口味咸淡
        String wordincomputer = json.getString("wordincomputer"); //电脑前工作
        String shuimian = json.getString("shuimian"); //睡眠
        String yeniao = json.getString("yeniao"); //夜尿
        String tsys = json.getString("tsys"); //特殊饮食习惯
        //===========================
        String inputcpy = WordUtil.trans2PyCode(patname);
        String inputcwb = WordUtil.trans2WbCode(patname);

        sex = json.getString("sex");
        phonecall = json.getString("phonecall");
        unit = json.getString("unit");
        dateofbirth = DateUtil.stringToDate(json.getString("dateofbirth"), "yyyy-MM-dd");
        professional = json.getString("professional");//职业
        maritalstatus = json.getString("maritalstatus");//婚姻状态

        province = json.getString("Province_R");//省
        city = json.getString("City_R");//市
        county = json.getString("County_R");//县
        township = json.getString("township_R");//镇
        village = json.getString("village_R");//乡
        laddress = json.getString("add_R");//路牌
        examtype = json.getString("examtype");//体检类型
        homeaddress = json.getString("homeaddress");//家庭住址
        String zkl = json.getString("zkl");//折扣率
        String wordaddress = json.getString("wordaddress");//工作单位
        DBOperator db = new DBOperator();
        PrintWriter pw = null;
        pw = response.getWriter();
        Timestamp timesTamp = new Timestamp(System.currentTimeMillis());
        String patsql = "";
        List<Map> list = null;
        String group_str = request.getParameter("group_str");//所选的套餐id
        String items_str = request.getParameter("items_str");//所选的套餐组合

        try {
            if (!str.equals("dblclick")) { //新增
                    /*
                    String codePath = "";//条码图片路径
					//产生条码
					BarCodeImage bar  = new BarCodeImage(1,33);
					String path = bar.create39Image(pexamid);
					codePath = "../"+path;
					*/
                //先判断在病人表里有木有已经建过档的人
                patsql = "select * from bas_patient_ids a where a.idno='" + idnum + "' and a.idtype='" + idtype + "'";
                list = db.find(patsql);
                if (ListUtil.listIsNotEmpty(list)) {
                    throw new Exception("证件类型：" + idtype + " " + "证件号:" + idnum + ";已经存在！请引入！");
                }
                String patientid = "";
                if (database_version == null || database_version.equals("11g") || database_version.equals("")) {
                    Map seq_id = (Map) db.findOne("select seq_patientid.nextval FROM DUAL");
                    patientid = seq_id.get("nextval").toString();
                } else {
                    Map<String, Double> seq_id = (Map<String, Double>) db.findOne("select seq_patientid.nextval FROM DUAL");
                    patientid = seq_id.get("nextval").toString();
                }
                //======= 插入 病人表 ============================================
                patsql = "insert into bas_patients (hosnum,patientid,clctimes,inptimes,patname,sex,bloodtype,dateofbirth,instype,discount" +
                        ",pattype,idtype,idnum,homeadd,postcode,contacter,relation,phonecall,email,professional" +
                        ",nationality,national,culturaldegree,maritalstatus,unitname,addtype,province,city,county,township" +
                        ",village,laddress,confidential,pasthis,allergyhis,docdate,operator,operatorname,passwd" +
                        ",provinceid,cityid,countyid,townshipid,villageid,age_type) " +
                        "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,((to_char(sysdate,'yyyy'))- ?||',Y'))";
                db.excute(patsql, new Object[]{hosnum,
                        patientid, 0, 0,
                        patname, sex, "",
                        dateofbirth, "自费", "普通会员",
                        null, idtype, idnum,
                        homeaddress, "", "",
                        "", phonecall, "",
                        professional, guoji,
                        minzu, whcd,
                        maritalstatus, "",
                        "", province, city,
                        county, township, village,
                        laddress, "",
                        "", "", new Date(),
                        userid, username, "123456",
                        "", "", "",
                        "", "",
                        json.getString("dateofbirth").substring(0, 4)});
                //====插入 病人卡类型表 =============================
                try {
                    db.excute("insert into BAS_Patient_IDs(HOSNUM,PATIENTID,IDTYPE,IDNO) values(?,?,?,?)",
                            new Object[]{hosnum, patientid, "就诊卡", patientid});
                } catch (Exception e) {
                    e.printStackTrace();
                    //throw new Exception("已有标示号为"+patientid+"的记录");
                }
                if (idtype != null && !idtype.equals("")) {
                    try {
                        db.excute("insert into BAS_Patient_IDs(HOSNUM,PATIENTID,IDTYPE,IDNO) values(?,?,?,?)",
                                new Object[]{hosnum, patientid, idtype, idnum});

                    } catch (Exception e) {
                        e.printStackTrace();
                        //throw new Exception("已有标示号为"+idnum+"的记录");
                    }
                }
                //================================================
                String mzh = "0";
                String sqlString = " insert into pexam_mans(hosnum,examid,sn,pexamid,idtype,idnum,ybbh,patname,"//inscardno
                        + "sex,dateofbirth,professional,maritalstatus,genexamdoctor,doctorname,examresult,examsuggest,genexamdate,"
                        + "bdate,edate,invoiceid,comments,province,city,county,township,village,laddress,examtype,phonecall,address,inputcpy,inputcwb,nodecode,adddate,mzh" +
                        ",MINZU, GUOJI, WHCD, ZJXY, KWXD, WORDINCOMPUTER, SHUIMIAN, YENIAO, TSYS, patientid,zkl,wordaddress) " +
                        "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?,?,?,?,?,?,?,?,?,?,  ?,?,?)";
                db.excute(sqlString, new Object[]{hosnum, examid, sn,
                        pexamid, idtype, idnum, inscardno, patname, sex,
                        dateofbirth, professional, maritalstatus,
                        genexamdoctor, doctorname, examresult, examsuggest,
                        genexamdate, bdate, edate, invoiceid, comments, province, city, county, township, village, laddress, examtype, phonecall, homeaddress, inputcpy, inputcwb, nodecode, mzh
                        , minzu, guoji, whcd, zjxy, kwxd, wordincomputer, shuimian, yeniao, tsys, patientid, zkl, wordaddress});
            } else {
                //更新
                String sql = "update pexam_mans a set a.patname=?,a.idtype=?,a.idnum=?, "
                        + "a.dateofbirth=?,a.sex=?,a.ybbh=?,a.phonecall=?, "//inscardno
                        + "a.maritalstatus=?,a.professional=?,a.province=?, "
                        + "a.city=?,a.county=?,a.township=?,a.village=?,a.laddress=?,address=? " +
                        "  ,a.MINZU=?, a.GUOJI=?, a.WHCD=?, a.ZJXY=?, a.KWXD=?, a.WORDINCOMPUTER=?, a.SHUIMIAN=?, a.YENIAO=?, a.TSYS=?, a.zkl=?,a.wordaddress=?  "
                        + "where a.hosnum=? and a.examid=? and a.pexamid=?";
                db.excute(sql, new Object[]{patname, idtype, idnum, dateofbirth, sex, inscardno, phonecall, maritalstatus,
                        professional, province, city, county, township, village, laddress, homeaddress
                        , minzu, guoji, whcd, zjxy, kwxd, wordincomputer, shuimian, yeniao, tsys, zkl, wordaddress, hosnum, examid, pexamid});
                //更新病人表====

                //更新病人标识表=====

            }

            //System.out.println("sbbbbbbbbbbbbbbbbbbb");
            Map temp = null;
            String tempsql = "";
            List<Map> templist = new ArrayList<Map>();
            tempsql = "select a.bdate from pexam_mans a where a.pexamid='" + pexamid + "'";
            templist = db.find(tempsql);
            String startdate = "";
            if (ListUtil.listIsNotEmpty(templist)) {
                startdate = templist.get(0).get("bdate") == null ? "" : templist.get(0).get("bdate").toString();
            }
            List<Object[]> pi = new ArrayList<Object[]>();
            Map info = (Map) db.findOne("select count(1) num from  pexam_items_title a  where a.pexamid='" + pexamid + "' ");
            int titleFlag = Integer.parseInt(info.get("num").toString());
            if (StrUtil.strIsNotEmpty(startdate) || titleFlag != 0) { //已经点击开始体检 或者 没开始但是点击了重打导诊单
                List<Map> tempList = null;
                Map testm = new HashMap();
                //1、不能移除 已经开始体检的套餐  //2、不能移除 已经开始体检的加项
                tempsql = "select * from pexam_items a where  a.pexamid ='" + pexamid + "' ";
                tempList = db.find(tempsql);
                for (Map map2 : tempList) {
                    String isgroup = map2.get("isgroup") == null ? "" : map2.get("isgroup").toString();
                    String itemid = map2.get("isgroup") == null ? "" : map2.get("itemid").toString();
                    if ("y".equals(isgroup)) {
                        if (!group_str.equals(itemid)) {
                            throw new Exception("已经开始体检不能更换套餐！如要替换请先撤销体检再尝试。");
                        }
                    } else {
                        if (items_str.indexOf(itemid) < 0) {
                            //如果此项目没采血 没项目结果 没和其他项目合并 没收费  就可删除
                            List<Map> testList = db.find("select * from pexam_items_title a where a.pexamid='" + pexamid + "' and a.itemcode in ('" + itemid + "')  and a.status is null");
                            if (ListUtil.listIsNotEmpty(testList)) {
                                if (testList.get(0).get("parent_comid") == null) {
                                    testList = db.find("select * from pexam_items_title a where 1=1 and (  a.itemuuid='" + testList.get(0).get("itemuuid") + "' or  a.parent_comid='" + testList.get(0).get("itemuuid") + "'  )");
                                    if (testList.size() > 1) {
                                        throw new Exception(testList.get(0).get("itemname") + "已经与其它检验项目合并，不能移除！");
                                    } else if (testList.size() == 1) {
                                        testList = db.find("select * from pexam_tjcx  a where a.itemuuid='" + testList.get(0).get("itemuuid") + "' ");
                                        if (ListUtil.listIsNotEmpty(testList)) {
                                            throw new Exception(testList.get(0).get("itemname") + "已经采血，不能移除！");
                                        } else {
                                            testList = db.find("select * from pexam_results  a where a.itemuuid='" + testList.get(0).get("itemuuid") + "' ");
                                            if (ListUtil.listIsNotEmpty(testList)) {
                                                throw new Exception(testList.get(0).get("itemname") + "已经有项目结果，不能移除！");
                                            } else {
                                                int x = db.excute("delete from pexam_items_title where pexamid='" + pexamid + "' and itemcode='" + itemid + "'");
                                                System.out.println("移除 加项,成功条数：" + x);
                                            }
                                        }
                                    }
                                } else {
                                    throw new Exception(testList.get(0).get("itemname") + "已经与其它检验项目合并，不能移除！");
                                }
                            } else {
                                throw new Exception(testList.get(0).get("itemname") + "已经收费，不能移除！");
                            }
                        }
                    }
                }
                //======================================================
                tempsql = "select * from PEXAM_ITEMS_TITLE where pexamid='" + pexamid + "'";
                templist = db.find(tempsql);
                String itemcodestring = ListUtil.getListstring(templist, "itemcode"); //此人在检的所有 组合

                String add_items = "";  //新增的组合
                for (int i = 0; i < jsons.size(); i++) {
                    temp = (Map) jsons.get(i);
                    String itemid = (String) (temp.get("itemid").equals("null") ? "" : temp.get("itemid"));
                    String isgroup = (String) (temp.get("isgroup").equals("null") ? "" : temp.get("isgroup"));
                    if ("n".equals(isgroup)) {
                        if (itemcodestring.indexOf(itemid) < 0) {
                            add_items += "'" + itemid + "',";
                        }
                    }
                }
                if (!"".equals(add_items)) {
                    add_items = add_items.substring(0, add_items.length() - 1);
                    String add_itemsSql = "select b.*, '' groupid, '' groupname, '' iszh  from pexam_items_com b  where b.comid in (" + add_items + ") ";
                    List<Map> add_itemsList = db.find(add_itemsSql);
                    Map map0 = null;
                    List<Object[]> pi_1 = new ArrayList<Object[]>();
                    if (ListUtil.listIsNotEmpty(add_itemsList)) { //循环要添加的体检项目
                        Map<String, Map<String, String>> lisMap = new HashMap<String, Map<String, String>>();
                        for (int i = 0; i < add_itemsList.size(); i++) {
                            map0 = (Map) add_itemsList.get(i);
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
                                        lisMap.put(jyyq, idMap);
                                    } else {
                                        lisMap.get(jyyq).put(jyyq, lisMap.get(jyyq).get(jyyq) + "-" + afterhb_name);
                                        map0.put("parent_comid", lisMap.get(jyyq).get("parent_comid"));
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

                        for (int i = 0; i < add_itemsList.size(); i++) {
                            map0 = (Map) add_itemsList.get(i);
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
                        Object[][] params_1 = new Object[pi_1.size()][2];
                        for (int j = 0; j < pi_1.size(); j++) {
                            params_1[j] = pi_1.get(j);
                        }
                        db.excuteBatch("insert into pexam_items_title (hosnum,itemuuid,examid,pexamid,itemcode,itemname,excdept,excdeptname,groupid," +
                                "groupname,sn,comclass,sheetdoctorid,sheetdoctorname,sheetdate,sheetdeptid,sheetdeptname,tmcode,iszh" +
                                ", ACCOUNTITEM, INVOICEITEM, ACCOUNTITEMNAME, INVOICEITEMNAME,ggxm,price,tjxm,afterhb_name" +
                                ",parent_comid,xmstatus)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,? ,?,?,?,?,?,?,?,?,? ,?,'未检')", params_1);
                    }
                }
            } else {
                //尚未开始体检的


            }

            String sql = "delete from  pexam_items a where a.examid=? and a.pexamid=? and a.hosnum=?";//先删掉个人的全部套餐
            db.excute(sql, new Object[]{examid, pexamid, hosnum});
            pi = new ArrayList<Object[]>();

            for (int i = 0; i < jsons.size(); i++) {
                temp = (Map) jsons.get(i);//temp装入循环的结果
                String cost = (String) (temp.get("cost").equals("null") ? "0" : temp.get("cost"));
                pi.add(new Object[]{temp.get("itemname"), cost, temp.get("itemid"), temp.get("isgroup"), hosnum, examid, pexamid});//Object[]装入插入语句所需要的参数
            }
            Object[][] params = new Object[pi.size()][2];
            sql = "";
            for (int i = 0; i < pi.size(); i++) {
                params[i] = pi.get(i);
            }                                                //然后在添加选中的套餐
            sql = "insert into pexam_items(itemname,cost,itemid,isgroup,hosnum,examid,pexamid)values(?,?,?,?,?,?,?)";
            db.excuteBatch(sql, params);


            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            pw.print("失败：" + e.getMessage());
        } finally {
            db.freeCon();
        }
    }

    //获取4个所有字典数据
    //--增加了几个字典的数据
    // 6003,8,9    宗教信仰    民族  文化程度
    //去掉国家这个 字典  7
    @RequestMapping("/getdict")
    public void getdict(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "select t.nekey,t.nevalue,t.contents,t.isdefault,t.inputcpy,t.inputcwb from bas_dicts t where t.hosnum = ? and t.nekey in(4,5,6,34,6003,8,9) and t.nevalue != '!' order by t.nevalue asc";
            @SuppressWarnings("unchecked")
            List<Bas_dicts> bds = db.find(sql, "0000", Bas_dicts.class);

            Map<String, List<Bas_dicts>> map = new HashMap<String, List<Bas_dicts>>();

            List<Bas_dicts> idtypeBds = new ArrayList<Bas_dicts>();// 证件类别
            List<Bas_dicts> maritalstatusBds = new ArrayList<Bas_dicts>();// 婚姻状况
            List<Bas_dicts> professionalBds = new ArrayList<Bas_dicts>();// 职业
            List<Bas_dicts> sexBds = new ArrayList<Bas_dicts>();// 性别

            List<Bas_dicts> zjxyBds = new ArrayList<Bas_dicts>();// 宗教信仰
            List<Bas_dicts> guojiaBds = new ArrayList<Bas_dicts>(); // 国家
            List<Bas_dicts> minzuBds = new ArrayList<Bas_dicts>();  //民族
            List<Bas_dicts> whcdBds = new ArrayList<Bas_dicts>(); //文化程度

            String _idtype = "";
            String _maritalstatus = "";
            String _professional = "";
            String _sex = "";
            String _zjxy = ""; //默认值
            String _guojia = ""; //默认值
            String _minzu = ""; //默认值
            String _whcd = ""; //默认值
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
                } else if (bd.getNekey() == 6003) {
                    zjxyBds.add(bd);
                    if ("Y".equals(bd.getIsdefault())) {
                        _zjxy = bd.getContents();
                    }
                }
//				else if (bd.getNekey() == 7) {
//					guojiaBds.add(bd);
//					if ("Y".equals(bd.getIsdefault())) {
//						_guojia = bd.getContents();
//					}
//				}
                else if (bd.getNekey() == 8) {
                    minzuBds.add(bd);
                    if ("Y".equals(bd.getIsdefault())) {
                        _minzu = bd.getContents();
                    }
                } else if (bd.getNekey() == 9) {
                    whcdBds.add(bd);
                    if ("Y".equals(bd.getIsdefault())) {
                        _whcd = bd.getContents();
                    }
                }
            }

            map.put("idtypeBds", idtypeBds);
            map.put("maritalstatusBds", maritalstatusBds);
            map.put("sexBds", sexBds);
            map.put("professionalBds", professionalBds);
            map.put("zjxyBds", zjxyBds);   //宗教信仰
            //map.put("guojiaBds", guojiaBds);   //国家
            map.put("minzuBds", minzuBds);   //民族
            map.put("whcdBds", whcdBds);   //文化程度

            db.commit();
            pw.print(JSONArray.fromObject(map).toString());
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
    }

    //字典数据过滤器
    @RequestMapping(value = "/dict/{dictType}", method = RequestMethod.GET)
    public void loadDicts(@PathVariable("dictType") String dictType, HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
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

    // 获取省市镇乡 list
    @RequestMapping(value = "/getPlaceList")
    public void getPlaceList(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        // System.out.println("getPlaceLst+++++++++到了！！！！！！！！！");
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String type = request.getParameter("type");
            String code = request.getParameter("code");
            String inputCpy = request.getParameter("inputCpy");
            String sql = null;
            List<Map> list = null;
            if ("province".equals(type)) {
                sql = "select nevalue,contents,inputcpy,inputcwb from bas_dicts where hosnum=? and nekey=? and (isdeleted is null or lower(isdeleted)=?) and nevalue like ? and length(nevalue)=? and inputcpy like ?";
                list = db.find(sql, new Object[]{"0000", 1, "n", "%0000", 6,
                        "%" + inputCpy.toUpperCase() + "%"}, 20);
            } else if ("city".equals(type)) {
                sql = "select nevalue,contents,inputcpy,inputcwb from bas_dicts where hosnum=? and nekey=? and (isdeleted is null or lower(isdeleted)=?) and nevalue like ? and nevalue!=? and length(nevalue)=? and inputcpy like ?";
                list = db.find(sql, new Object[]{"0000", 1, "n",
                        code.substring(0, 2) + "%00", code, 6,
                        "%" + inputCpy.toUpperCase() + "%"}, 20);
            } else if ("county".equals(type)) {
                sql = "select nevalue,contents,inputcpy,inputcwb from bas_dicts where hosnum=? and nekey=? and (isdeleted is null or lower(isdeleted)=?) and nevalue like ? and nevalue!=? and length(nevalue)=? and inputcpy like ?";
                list = db.find(sql, new Object[]{"0000", 1, "n",
                        code.substring(0, 4) + "%", code, 6,
                        "%" + inputCpy.toUpperCase() + "%"}, 20);
            } else if ("township".equals(type)) {
                sql = "select nevalue,contents,inputcpy,inputcwb from bas_dicts where hosnum=? and nekey=? and (isdeleted is null or lower(isdeleted)=?) and nevalue like ? and nevalue!=? and length(nevalue)=? and inputcpy like ?";
                list = db.find(sql, new Object[]{"0000", 1, "n",
                        code + "%000", code, 12,
                        "%" + inputCpy.toUpperCase() + "%"}, 20);
            }

            JSONArray jsons = JSONArray.fromObject(list);
            //System.out.println("jsons=" + jsons.toString());
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
 * 获取团体的套餐teamExamInfo
 */

    @RequestMapping(value = "/teamExamInfo")
    public void getTeamExamInfo(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        String examid = request.getParameter("examid");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "select * from pexam_items a where a.hosnum=? and a.examid=? and a.pexamid is null";
            List list = db.find(sql, new Object[]{hosnum, examid});
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
    }

    /*
     * 获取个人所有套餐
	 */
    @RequestMapping(value = "ExamInfo")
    public void getExamInfo(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        String examid = request.getParameter("examid");
        String pexamid = request.getParameter("pexamid");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "select * from pexam_items a where a.hosnum=? and a.examid=? and a.pexamid=?";
            List list = db.find(sql, new Object[]{hosnum, examid, pexamid});
            if (list.size() == 0) {
                sql = "select * from pexam_items a where a.hosnum=? and a.examid=? and a.pexamid is null";
                list = db.find(sql, new Object[]{hosnum, examid});
            }
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
    }

    //加载大项Combo
    @RequestMapping("/addTestItems")
    public void addTestItems(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("textml;charset=utf-8");
        DBOperator db = null;
        String sql = "";
        PrintWriter pw = null;
        try {
            List<Map> list = new ArrayList<Map>();
            db = new DBOperator();
            sql = "select t.jyitemid,t.jyitemName from pexam_jyitems t";
            list = db.find(sql);
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

    /**
     * 转到执行科室选择页面
     *
     * @return
     */
    @RequestMapping("/dept_execute_select")
    public ModelAndView showDeptExecute(HttpServletRequest request,
                                        HttpServletResponse response) throws Exception {
        String deptcode = request.getParameter("deptcode");
        String hosnum = request.getParameter("hosnum");
        ModelMap model = new ModelMap();
        //model.put("hosnum", "1005");
        model.put("hosnum", hosnum);
        model.put("deptcode", deptcode);
        return new ModelAndView("pexam/dept_execute_select", model);
    }

    @RequestMapping(value = "/getItemsInd")
    public void getItemsInd(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
//	Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
//	String hosnum = basHospitals.getHosnum();
        String parentid = request.getParameter("parentid");
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            List<Map> list = null;
            String sql = "select * from bas_dicts d where d.nekey ='1201' and d.option01 = ?";
            list = db.find(sql, new Object[]{parentid});
            JSONArray jsons = JSONArray.fromObject(list);
            //System.out.println(jsons.toString());
            System.out.println("jsons.toString()==========" + jsons.toString());
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

    /**
     * ajax方式加载执行科室树
     *
     * @param request
     * @param response
     * @param modelMap
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/dept_execute_tree", method = RequestMethod.POST)
    public void loadExecuteTreeStr(HttpServletRequest request,
                                   HttpServletResponse response, ModelMap modelMap) throws Exception {
        String hosnum = request.getParameter("hosnum");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String nodecode = basHospitals.getNodecode();
        System.out.println(nodecode);
        // 查询数据
        DBOperator db = null;
        List hosList = null;
        List ksList = null;
//		List bqList = null;
        try {
            db = new DBOperator();
            String sql = "select hosnum,hosname,nodecode,distcode from BAS_Hospitals a where a.hosnum=? and a.nodecode=?";
            hosList = db.find(sql, new Object[]{hosnum, nodecode});
            sql = "select deptcode,deptname,parentid,nodecode,isleaf from bas_dept t "
                    + "where t.HOSNum=? and t.nodecode=? and deptclass='科室' and isdeleted='N'";
            ksList = db.find(sql, new Object[]{hosnum, nodecode});
//			sql = "select deptcode,deptname,parentid,nodecode,isleaf from bas_dept t "
//					+ "where t.HOSNum=? and deptclass='病区' ";
//			bqList = db.find(sql, new Object[]{hosnum});
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }

        // 组装的树数据格式
        List<String> lstTree = new ArrayList<String>();
        String temp = "";
        String name = "";
//		String nodecode = "";
//		String distcode = "";
        if (ListUtil.listIsNotEmpty(hosList)) {// 取得医院的名称，
            for (Iterator iterator = hosList.iterator(); iterator.hasNext(); ) {
                Map tempMap = (Map) iterator.next();
                //			if (((String) tempMap.get("hosnum")).equals((String) tempMap.get("nodecode"))) {// 医院编码和节点编码相同
                name = (String) tempMap.get("hosname");
//					nodecode = (String) tempMap.get("nodecode");
//					distcode = (String) tempMap.get("distcode");
//				}
            }
        }

        if (name.equals("")) {
            lstTree.add("没有找到对应医院");
        } else {// 拼装树
            String s1 = "{id:\"0\", pId:\"-1\", name:\"" + name + "\" , open:true}";// 根节点
            lstTree.add(s1);

//			String s2 = "";

            if (hosList.size() >= 2) {// 存在“院区”，3级节点--院区
                //s2 = "{id:\"10\", pId:\"0\", name:\"科室\" }";// 2级节点--科室
                //lstTree.add(s2);
                for (Iterator hosIterator = hosList.iterator(); hosIterator.hasNext(); ) {
                    Map hosMap = (Map) hosIterator.next();
                    String curNodeCode = (String) hosMap.get("nodecode");// 院区编码
                    temp = "{id:\"01" + curNodeCode + "\"," +
                            "pId:\"0\"," +
                            "name:\"" + StrUtil.strGoalToDef((String) hosMap.get("hosname"), name, "本部") + "\"}";// 子节点
                    lstTree.add(temp);

                    if (ListUtil.listIsNotEmpty(ksList)) {// 存在科室
                        for (Iterator ksIterator = ksList.iterator(); ksIterator.hasNext(); ) {
                            Map ksMap = (Map) ksIterator.next();
                            if (hosMap.get("nodecode").equals(
                                    ksMap.get("nodecode"))) {// 匹配的院区下面才加
                                temp = "{id:\"" + ksMap.get("deptcode") + "\"," +
                                        "pId:\"" + StrUtil.strNullToDef((String) ksMap.get("parentid"), "01" + hosMap.get("nodecode")) + "\"," +
                                        "name:\"" + ksMap.get("deptname") + "\"," +
                                        "isLast:\"" + ksMap.get("isleaf") + "\"," +
                                        "yqName:\"" + StrUtil.strGoalToDef((String) hosMap.get("hosname"), name, "本部") + "\"}";// 子节点
                                lstTree.add(temp);
                            }
                        }
                    }
                }
            } else {// 不存在“院区”，3级节点--科室
                //				s2 = "{id:\"10\", pId:\"0\", name:\"科室\",isLast:\"N\", treeType:\"ks\", yqId:\""
                //						+ nodecode + "\" }";// 2级节点--科室
                //				lstTree.add(s2);
                if (ListUtil.listIsNotEmpty(ksList)) {// 存在科室
                    for (Iterator ksIterator = ksList.iterator(); ksIterator.hasNext(); ) {
                        Map ksMap = (Map) ksIterator.next();
                        temp = "{id:\"" + ksMap.get("deptcode") + "\"," +
                                "pId:\"" + StrUtil.strNullToDef((String) ksMap.get("parentid"), "0") + "\"," +
                                "name:\"" + ksMap.get("deptname") + "\"," +
                                "isLast:\"" + ksMap.get("isleaf") + "\"," +
                                "yqName:\"" + name + "\"}";// 子节点
                        lstTree.add(temp);
                    }
                }
            }
        }

        // 传到前台
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = null;
        pw = response.getWriter();
        pw.print(JSONArray.fromObject(lstTree).toString());
        pw.flush();
        pw.close();
    }

    //--------------------农保人员库 相关 2013-05-27 徐闯------------------
    /*
     * 加载新建体检人员信息
	 */
    @RequestMapping("/nbpersonInfoEntry")
    public ModelAndView show_nbpersonInfoEntry(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String ybbh = request.getParameter("ybbh");
        String str = request.getParameter("str");//判断是从哪个页面调这个弹出窗口
        modelMap.put("str", str);
        modelMap.put("unit", ybbh);
        DBOperator db = new DBOperator();
        try {
            String sql = "";
            if (ybbh != null) {
                // System.out.println("44444--------------444444444");
                sql = "select a.idtype,a.idnum,a.patname,to_char(a.dateofbirth,'yyyy-mm-dd') as dateofbirth,a.sex, "
                        + "a.maritalstatus,a.professional,a.ybbh ,a.province,a.city,a.county,a.township,a.village,a.laddress,a.phonecall,address "
                        + "from pexam_nbpeoples a "
                        + "where a.hosnum=? and a.ybbh=? ";
                List list = db.find(sql, new Object[]{hosnum, ybbh});//体检人信息
                request.setAttribute("listbase", "var listbase=" + JSONArray.fromObject(list.get(0)).toString());

            } else {
                //System.out.println("-------------农保库新建农保人员-------------");

            }
            db.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("phyexam/nbpersonInfoEntry", modelMap);
    }

    //注册时 验证农保号是否已存在
    @RequestMapping(value = "/checkYbbh")
    public void checkYbbh(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String ybbh = request.getParameter("ybbh");
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            Map countMap = new HashMap();
            String sql = "select count(*) as count from pexam_nbpeoples p where p.hosnum=? and p.ybbh=? ";
            countMap = (Map) db.findOne(sql, new Object[]{hosnum, ybbh});
            int count = 0;
            if (countMap != null && !countMap.isEmpty()) {
                count = ((BigDecimal) countMap.get("count")).intValue();
            }
            //System.out.println(jsons.toString());
            System.out.println("jsons.toString()==========" + count);
            pw.print(count);
            db.commit();
        } catch (Exception e) {
            db.rollback();
            pw.print("fail");
            e.printStackTrace();
        } finally {
            db.freeCon();
        }
    }


    //保存新建农保库人员
    @RequestMapping("/savenbpatient")
    public void saveNbPatient(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String str = request.getParameter("str");
        String patname = "";
        String sex = "";
        Date dateofbirth = null;
        String professional = "";
        String maritalstatus = "";
        String ybbh = "";//农保号
        String idtype = "";
        String idnum = "";
        String genexamdoctor = "";
        String doctorname = "";
        String examresult = "";
        String examsuggest = "";
        Date genexamdate = null;
        String invoiceid = "";
        String comments = "";
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String sn = "8888";
        String id = "";
        String phonecall = "";
        String province = "";
        String city = "";
        String county = "";
        String township = "";
        String village = "";
        String laddress = "";
        String unit = "";
        String examtype = "";
        String homeaddress = "";
        String json1Str = URLDecoder.decode(request.getParameter("json1"), "utf-8");
        JSONArray json1 = JSONArray.fromObject(json1Str);
        JSONObject json = json1.getJSONObject(0);
        idtype = json.getString("idtype");
        idnum = json.getString("idnum");
        ybbh = json.getString("ybbh");
        patname = json.getString("patname");
        String inputcpy = WordUtil.trans2PyCode(patname);
        String inputcwb = WordUtil.trans2WbCode(patname);

        sex = json.getString("sex");
        phonecall = json.getString("phonecall");
        dateofbirth = DateUtil.stringToDate(json.getString("dateofbirth"), "yyyy-MM-dd");
        professional = json.getString("professional");//职业
        maritalstatus = json.getString("maritalstatus");//婚姻状态

        province = json.getString("Province_R");//省
        city = json.getString("City_R");//市
        county = json.getString("County_R");//县
        township = json.getString("township_R");//镇
        village = json.getString("village_R");//乡
        laddress = json.getString("add_R");//路牌
        homeaddress = json.getString("homeaddress");//家庭住址
        DBOperator db = new DBOperator();
        PrintWriter pw = null;
        pw = response.getWriter();
        try {
            if (!str.equals("dblclick")) {
                String sqlString = " insert into pexam_nbpeoples(hosnum,sn,idtype,idnum,ybbh,patname,"
                        + "sex,dateofbirth,professional,maritalstatus,genexamdoctor,doctorname,examresult,examsuggest,genexamdate,"
                        + "invoiceid,comments,province,city,county,township,village,laddress,examtype,phonecall,address,inputcpy,inputcwb,nodecode,adddate) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate)";
                db.excute(sqlString, new Object[]{hosnum, sn,
                        idtype, idnum, ybbh, patname, sex,
                        dateofbirth, professional, maritalstatus,
                        genexamdoctor, doctorname, examresult, examsuggest,
                        genexamdate, invoiceid, comments, province, city, county, township, village, laddress, examtype, phonecall, homeaddress, inputcpy, inputcwb, nodecode});

            } else {
                String sql = "update pexam_nbpeoples a set a.patname=?,a.idtype=?,a.idnum=?, "
                        + "a.dateofbirth=?,a.sex=?,a.phonecall=?, "//inscardno
                        + "a.maritalstatus=?,a.professional=?,a.province=?, "
                        + "a.city=?,a.county=?,a.township=?,a.village=?,a.laddress=?,address=? "
                        + "where a.hosnum=? and a.ybbh=? ";
                db.excute(sql, new Object[]{patname, idtype, idnum, dateofbirth, sex, phonecall, maritalstatus,
                        professional, province, city, county, township, village, laddress, homeaddress, hosnum, ybbh});

            }

            db.commit();
        } catch (RuntimeException e) {
            e.printStackTrace();
            db.rollback();
            pw.print("fail");
        } finally {
            db.freeCon();
        }
    }

    //-------------删除农保库人员------
    @RequestMapping("/delnbpatient")
    public void delnbpatient(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String ybbh = request.getParameter("ybbh");
        DBOperator db = new DBOperator();
        PrintWriter pw = null;
        try {
            String sql = "delete from pexam_nbpeoples where hosnum=? and ybbh=?";
            db.excute(sql, new Object[]{hosnum, ybbh});
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

    //----新建  引入弹出窗口----------
    @RequestMapping("/nbperson_pullIn")
    public ModelAndView nbperson_pullIn(HttpServletRequest request, HttpServletResponse response, ModelMap modelmap) {
        response.setContentType("text/html;charset=utf-8");
        //System.out.println("------------------------");
        String examid = request.getParameter("examid");
        modelmap.put("examid", examid);
        return new ModelAndView("pexam/nbperson_pullIn", modelmap);
    }


    //----从农保库引入到体检人员登记表-------
    //修改 --lsp
    //从病人表  引入到体检人员登记表-------  是病人表！！！
    @RequestMapping(value = "/NbpersonToPexamman", method = RequestMethod.POST)
    public void NbpersonToPexamman(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String checkIds = StrUtil.strNullToEmpty(request.getParameter("checkIds"));
        String[] Ids = checkIds.split(",");//得到病人id
        String examid = request.getParameter("examid");   //体检Id
        Set<String> userIdSet = new HashSet<String>();
        String nbsql = "";
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            db = new DBOperator();
            List<Map> nblist = new ArrayList();
            String ybbhstrs = " ( ";
            for (int i = 0; i < Ids.length; i++) {
                String ybbh = Ids[i];
                if (i == 0) {
                    ybbhstrs += "'" + ybbh + "'";
                } else {
                    ybbhstrs += ",'" + ybbh + "'";
                }
                System.out.println("-----------" + ybbh);
            }
            ybbhstrs += " ) ";  //病人id串
            List<Object[]> pi = new ArrayList<Object[]>();
            nbsql = "select * from PEXAM_Main t where t.examid='" + examid + "'";
            List<Map> PEXAM_Mainlist = db.find(nbsql);
            String examtype = PEXAM_Mainlist.get(0).get("examtype").toString();

            nbsql = "select p.*,to_char(p.dateofbirth,'yyyy-mm-dd') dateofbirth1  from bas_patients p where p.hosnum=? and p.patientid in " + ybbhstrs;
            nblist = db.find(nbsql, new Object[]{hosnum});
            String pidsql = "";
            String pexamid = "";   //体检id
            String nowtime = StrUtil.dateToStr(new Date(), "yyyy-MM-dd HH:mm:ss");
            for (int j = 0; j < nblist.size(); j++) {
                pidsql = "select seq_pexamid.nextval from dual";
                Map nbpersonMap = nblist.get(j);
                //---------10g转11g序号修改
                if (oracleType == 11) {
                    Map<String, BigDecimal> seq_id = (Map<String, BigDecimal>) db.findOne(pidsql);
                    pexamid = seq_id.get("nextval").toString();
                    //request.setAttribute("pexamid",seq_id.get("nextval").intValue());
                } else {
                    Map<String, Double> seq_id = (Map<String, Double>) db.findOne(pidsql);
                    pexamid = Long.toString((seq_id.get("nextval").longValue()));
                }
                //得到病人表的一些字段
                String patientid = (String) nbpersonMap.get("patientid");
                String idtype = (String) nbpersonMap.get("idtype");
                String idnum = (String) nbpersonMap.get("idnum");
                String patname = (String) nbpersonMap.get("patname");
                String sex = (String) nbpersonMap.get("sex");
                String dateofbirth1 = (String) nbpersonMap.get("dateofbirth1");
                String phonecall = (String) nbpersonMap.get("phonecall");
                String homeadd = (String) nbpersonMap.get("homeadd");
                //赋值
                pi.add(new Object[]{hosnum, examid, pexamid, idtype, idnum, patname, sex, dateofbirth1, phonecall,
                        homeadd,
                        hosnum, nowtime, patientid
                });
            }
            Object[][] params = new Object[pi.size()][2];
            for (int i = 0; i < pi.size(); i++) {
                params[i] = pi.get(i);
            }
            String insertsql = "insert into PEXAM_MANS (HOSNUM, EXAMID, SN, PEXAMID, IDTYPE, IDNUM, INSCARDNO, PATNAME, SEX, DATEOFBIRTH, " +
                    "PROFESSIONAL, MARITALSTATUS, GENEXAMDOCTOR, DOCTORNAME, EXAMRESULT, EXAMSUGGEST, GENEXAMDATE, " +
                    "BDATE, EDATE, INVOICEID, " +
                    "COMMENTS, CULTURALDEGREE, PROVINCE, CITY, COUNTY, TOWNSHIP, VILLAGE, LADDRESS, CENSUSTYPE, PROVINCE_C, CITY_C, COUNTY_C, " +
                    "TOWNSHIP_C, VILLAGE_C, LADDRESS_C," +
                    " EXAMTYPE, PHONECALL, INPUTCPY, INPUTCWB, ADDRESS, YBBH, ZH, CODEPATH, PRINTFLAG, " +
                    "NODECODE, " +
                    "ADDDATE, ISPRINT, ISOVER, PRINTDOCTOR, PRINTTIME, PATIENTID, MZH)" +
                    " values (?, ?, null, ?, ?, ?, null, ?, ?, to_date(?, 'yyyy-mm-dd'), null, null, null, null, null, null, null," +
                    " null, null, null," +
                    " null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, " +
                    "'" + examtype + "',?, null, null, ?, " +
                    "null, null, null, null, ?, " +
                    "to_date(?, 'yyyy-mm-dd hh24:mi:ss'), null, null, null, null, ?, '0')";
            db.excuteBatch(insertsql, params);
            db.commit();
            pw = response.getWriter();
            pw.print("success");
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            pw.print("fail" + e.getMessage());
        } finally {
            db.freeCon();
        }
    }


    @RequestMapping(value = "/getItemsType")
    public void getItemsType(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
//		Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
//		String hosnum = basHospitals.getHosnum();
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            List<Map> list = null;
            String sql = "select * from bas_dicts d where d.nekey ='1202' and d.nevalue != '!' order by d.nevalue ";
            list = db.find(sql);
            JSONArray jsons = JSONArray.fromObject(list);
            //System.out.println(jsons.toString());
            System.out.println("jsons.toString()==========" + jsons.toString());
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

    //----------2014-02-15 xc 2014版体检小项维护------------
    @RequestMapping(value = "/itemsIndList2")
    public ModelAndView itemsIndList2(HttpServletRequest request, HttpServletResponse response, ModelMap modelmap) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String parentId = request.getParameter("parentId");
        if (parentId == null) {
            parentId = "";
        }
        modelmap.put("parentid", parentId);
        modelmap.put("hosnum", hosnum);
        return new ModelAndView("phyexam/itemsIndList2", modelmap);
    }

    //------小项新增----
    @RequestMapping(value = "/itemsIndAdd2", method = RequestMethod.GET)
    public ModelAndView itemsIndAdd2(HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) throws Exception {
        response.setContentType("text/html;charset=utf-8");

        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String save_num = StrUtil.strNullToDef(request.getParameter("save_num"), "0");//保存次数
        String operationType = request.getParameter("operationType");// 操作类型：add（新增）,modify（修改）,view（查看）

        DBOperator db = null;
        PrintWriter pw = null;
        List<Bas_dicts> bds = null;
        PexamItemsInd details = null;
        PexamItemsInd details2 = null;
        List indResList = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql = "";
            bds = db.find("select * from bas_dicts t where t.hosnum = '0000' and t.nekey in(49, 41, 42, 51) and t.nevalue != '!' " +
                    "order by t.nevalue asc", Bas_dicts.class);
            List<Bas_dicts> sexList = new ArrayList<Bas_dicts>();//性别
            List<Bas_dicts> resultList = new ArrayList<Bas_dicts>();//结果类型
            List<Bas_dicts> inputList = new ArrayList<Bas_dicts>();//输入方式
            List<Bas_dicts> unitList = new ArrayList<Bas_dicts>();//数值单位
            for (Bas_dicts bd : bds) {
                if (bd.getNekey() == 49) {
                    sexList.add(bd);
                } else if (bd.getNekey() == 41) {
                    resultList.add(bd);
                } else if (bd.getNekey() == 42) {
                    inputList.add(bd);
                } else if (bd.getNekey() == 51) {
                    unitList.add(bd);
                }
            }

            modelMap.put("sexList", sexList);
            modelMap.put("resultList", resultList);
            modelMap.put("inputList", inputList);
            modelMap.put("unitList", unitList);

            modelMap.put("operationType", operationType);
            modelMap.put("save_num", save_num);
            modelMap.put("hosnum", hosnum);
            if ("add".equals(operationType)) {
                String parentid = request.getParameter("parentid");// 该节点的父节点
                sql = "select seq_pexam.nextval from dual ";
                List<Map> tempList = db.find(sql);
                //---------10g转11g序号修改
                String indId = null;
                if (oracleType == 11) {
                    indId = String.valueOf((BigDecimal) tempList.get(0).get("nextval"));
                } else {
                    indId = String.valueOf((int) (double) (Double) tempList.get(0).get("nextval"));
                }
                sql = "select max(decode(null,a.sn,0,a.sn))+1 as code from pexam_items_ind a where a.hosnum=? and a.parentid=?";
                tempList = db.find(sql, new Object[]{hosnum, parentid});
                long sn = 1;
                Map temp = tempList.get(0);
                if (temp.get("code") != null) {
                    sn = (int) ((BigDecimal) temp.get("code")).doubleValue();
                }
                details = new PexamItemsInd();
                details.setParentid(parentid);//父节点
                details.setSn(sn);//序号
                details.setIndid(indId);//体检小项id
            } else {// 修改；查看
                String indId = request.getParameter("indId");
                List<PexamItemsInd> itemsIndList = new ArrayList<PexamItemsInd>();
                sql = "select * from pexam_items_ind a where a.hosnum=? and a.indid=?";
                itemsIndList = db.find(sql, new Object[]{hosnum, indId}, PexamItemsInd.class);
                if (itemsIndList != null && itemsIndList.size() > 0) {
                    details = (PexamItemsInd) ListUtil.distillFirstRow(itemsIndList);
                } else {
                    itemsIndList = db.find(sql, new Object[]{"0000", indId}, PexamItemsInd.class);
                    details = (PexamItemsInd) ListUtil.distillFirstRow(itemsIndList);
                }


				/*
				itemsIndList = db.find(sql,new Object[]{hosnum,indId},PexamItemsInd.class);
				details2 = (PexamItemsInd) ListUtil.distillFirstRow(itemsIndList);
				*/
            }
            modelMap.put("details", details);//新增项目是存放 父节点 序号 小项id
            //modelMap.put("details2", details2);//查看修改时 存放项目信息
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("phyexam/itemsIndAdd2", modelMap);
    }

    //----获取该大项下所有小项-----
    @RequestMapping(value = "/getItemsInd2")
    public void getItemsInd2(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String parentid = request.getParameter("parentid");
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            List<Map> list = null;
            String sql = "";
            if (hosnum == "0000") {
                sql = " select * from pexam_items_ind d where d.hosnum='0000' order by d.indname ";
                list = db.find(sql);
            } else {
                sql = " select * from pexam_items_ind d where d.hosnum='0000' and d.parentid=? order by d.indname ";
                list = db.find(sql, new Object[]{parentid});
            }
            //String sql = "select * from bas_dicts d where d.nekey ='1201' and d.option01 = ?";
            //list =db.find(sql,new Object[]{parentid});
            JSONArray jsons = JSONArray.fromObject(list);
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

    //----指标项目保存-------
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/itemsIndSave2", method = RequestMethod.POST)
    public void itemsIndSave2(HttpServletRequest request, HttpServletResponse response, PexamItemsInd pexam) throws Exception {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String operationType = request.getParameter("operationType");// 操作类型：add（新增）,modify（修改）
        System.out.println("indname==================" + pexam.getIndname());

        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            pexam.setHosnum(hosnum);
            String sql = "";
            if ("add".equals(operationType)) {
                //----插入操作----
                String indname = pexam.getIndname();
                String inputcpy = WordUtil.trans2PyCode(indname);
                String inputcwb = WordUtil.trans2WbCode(indname);

                sql = "insert into pexam_items_ind(itemcost,hosnum,indid,indname,forsex,resultunit,maxval,minval,minpromp,maxpromp," +
                        "defaultv,comments,sn,resulttype,parentid,inputcpy,inputcwb,pdcode,pdname,isjy,iszh,tsxm)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                db.excute(sql, new Object[]{pexam.getItemcost(), pexam.getHosnum(), pexam.getIndid(), indname,
                        pexam.getForsex(), pexam.getResultunit(), pexam.getMaxval(),
                        pexam.getMinval(), pexam.getMinpromp(), pexam.getMaxpromp(),
                        pexam.getDefaultv(), pexam.getComments(), pexam.getSn(),
                        pexam.getResulttype(), pexam.getParentid(), inputcpy, inputcwb, pexam.getPdcode(), pexam.getPdname(), pexam.getIsjy(), pexam.getIszh(), pexam.getTsxm()});
            } else {
                //----更新操作----
                sql = "select * from pexam_items_ind a where a.hosnum=? and a.indid=?";
                List<Map> list = db.find(sql, new Object[]{hosnum, pexam.getIndid()});
                if (list.size() > 0) {
                    sql = "update pexam_items_ind a set a.itemcost=?,a.indname=?,a.forsex=?,a.resultunit=?,a.maxval=?,a.minval=?," +
                            "a.minpromp=?,a.maxpromp=?,a.defaultv=?,a.comments=?,a.sn=?,a.resulttype=?," +
                            "a.parentid=?,a.pdcode=?,a.pdname=?,a.isjy=?,a.iszh=?,a.tsxm=?  where a.hosnum=? and a.indid=?";
                    db.excute(sql, new Object[]{pexam.getItemcost(), pexam.getIndname(), pexam.getForsex(),
                            pexam.getResultunit(), pexam.getMaxval(), pexam.getMinval(),
                            pexam.getMinpromp(), pexam.getMaxpromp(), pexam.getDefaultv(),
                            pexam.getComments(), pexam.getSn(), pexam.getResulttype(),
                            pexam.getParentid(), pexam.getPdcode(), pexam.getPdname(), pexam.getIsjy(), pexam.getIszh(), pexam.getTsxm()
                            , pexam.getHosnum(), pexam.getIndid()});


                    //更新组合的价格
                    sql = "select * from pexam_items_com a  where a.comid in ( select c.comid from pexam_items_comdet c where c.indid = ? ) and a.hosnum=?  ";
                    list = db.find(sql, new Object[]{list.get(0).get("indid").toString(), hosnum});
                    String comids = ListUtil.getListstring1(list, "comid");
                    for (Map map : list) {
                        //组合的价格是小项价格加起来的
                        sql = "select nvl(sum(i.itemcost) ,0 ) costs from pexam_items_comdet a  left join pexam_items_ind i on i.indid=a.indid   where a.comid = '" + map.get("comid").toString() + "' and a.hosnum='" + hosnum + "' ";
                        List<Map> costComList = db.find(sql);
                        System.out.println("更新组合价格为：" + costComList.get(0).get("costs"));
                        db.excute("update pexam_items_com a set a.cost = ? where a.hosnum = ? and a.comid=? ", new Object[]{costComList.get(0).get("costs"), hosnum, map.get("comid").toString()});
                    }
                    System.out.println("================================================================");
                    //更新套餐的价格
                    if (!"".equals(comids)) {
                        sql = "select * from pexam_items_group  a where a.groupid in (select b.groupid from pexam_items_groupdetails b where b.itemcode in (" + comids + ") ) and a.hosnum=? ";
                        list = db.find(sql, new Object[]{hosnum});
                        for (Map map2 : list) {
                            //套餐的价格是组合价格加起来的
                            sql = "select nvl(sum(c.cost) ,0 ) costs from pexam_items_groupdetails a left join pexam_items_com c on c.comid=a.itemcode  where a.groupid='" + map2.get("groupid").toString() + "' and a.hosnum='" + hosnum + "' ";
                            List<Map> costGroupList = db.find(sql);
                            System.out.println("更新套餐价格为：" + costGroupList.get(0).get("costs"));
                            db.excute("update pexam_items_group a set a.cost = ? where a.hosnum = ? and a.groupid=? ", new Object[]{costGroupList.get(0).get("costs"), hosnum, map2.get("groupid").toString()});
                        }
                    }
                } else {
                    //---下面服务站引入时进行插入操作----
                    String indname = pexam.getIndname();
                    String inputcpy = WordUtil.trans2PyCode(indname);
                    String inputcwb = WordUtil.trans2WbCode(indname);
                    sql = "insert into pexam_items_ind(hosnum,indid,indname,forsex,resultunit,maxval,minval,minpromp,maxpromp," +
                            "defaultv,comments,sn,resulttype,parentid,inputcpy,inputcwb,pdcode,pdname,isjy,iszh,tsxm)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                    db.excute(sql, new Object[]{pexam.getHosnum(), pexam.getIndid(), indname,
                            pexam.getForsex(), pexam.getResultunit(), pexam.getMaxval(),
                            pexam.getMinval(), pexam.getMinpromp(), pexam.getMaxpromp(),
                            pexam.getDefaultv(), pexam.getComments(), pexam.getSn(),
                            pexam.getResulttype(), pexam.getParentid(), inputcpy, inputcwb, pexam.getPdcode(), pexam.getPdname(), pexam.getIsjy(), pexam.getIszh(), pexam.getTsxm()});

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
        pw.flush();
        pw.close();
    }

    /**
     * 比较2个字符串类型的 数字的 大小   第一个数字 大于 第二个数字返回1  相等返回0 小于返回 -1
     *
     * @param a
     * @param b
     */
    public static String compareStrings(String a, String b) throws Exception {
        double a1 = Double.parseDouble(a);
        double b1 = Double.parseDouble(b);
        BigDecimal val1 = new BigDecimal(a);
        BigDecimal val2 = new BigDecimal(b);
        String result = "";
        if (val1.compareTo(val2) < 0) {
            result = "-1";
        }
        if (val1.compareTo(val2) == 0) {
            result = "0";
        }
        if (val1.compareTo(val2) > 0) {
            result = "1";
        }
        return result;
    }

    /**
     * 2个字符串类型的 数字 相减
     *
     * @param a
     * @param b
     * @return
     */
    public static Double subStrings(String a, String b) throws Exception {
        double a1 = Double.parseDouble(a);
        double b1 = Double.parseDouble(b);
        double re = a1 - b1;
        return re;
    }

    //----获取大项类型-----
    @RequestMapping(value = "/getItemsType2")
    public void getItemsType2(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            List<Map> list = null;
            String sql = "select * from pexam_items_type p where p.hosnum='0000' order by p.sn ";
            list = db.find(sql);
            JSONArray jsons = JSONArray.fromObject(list);
            //System.out.println(jsons.toString());
            System.out.println("jsons.toString()==========" + jsons.toString());
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

    @RequestMapping("/addckz")
    public String addckz() {
        return "phyexam/addckz";
    }

    @RequestMapping(value = "/findCKRef", method = RequestMethod.POST)
    public void findCKRef(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        String sex = request.getParameter("sex");
        String age = request.getParameter("age");
        String sampleType = request.getParameter("sampleType");
        String indid = request.getParameter("indid");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            String sqlH = "select id ";
            String sqlF = " from pexam_itemref where 1=1 ";
            if (!age.equals("0")) {
                sqlH = sqlH + ",(case when (agedown<30) then agedown"
                        + " when (agedown>=30 and agedown<365) then agedown/30"
                        + " else  agedown/365 end) as agedown,"//agedown
                        + " (case when (ageup<30) then ageup"
                        + " when (ageup>=30 and ageup<365) then ageup/30"
                        + " else  ageup/365 end) as ageup," //ageup
                        + "(case when (agedown<30) then '天'"
                        + " when (agedown>=30 and agedown<365) then '月'"
                        + " else  '岁' end) as downunit,"//downunit
                        + "(case when (ageup<30) then '天'"
                        + " when (ageup>=30 and ageup<365) then '月'"
                        + " else  '岁' end) as upunit";//upunit
                sqlF = sqlF + " and agedown is not null and ageup is not null and hosnum='" + hosnum + "' ";
            }
            if (!sex.equals("0")) {
                sqlH = sqlH + ",sex";
                sqlF = sqlF + " and sex is not null";
            }
            if (!sampleType.equals("0")) {
                sqlH = sqlH + ",sampletype";
                sqlF = sqlF + " and sampletype is not null";
            }
            sqlH = sqlH + ",up, down";
            sqlF = sqlF + " and indid='" + indid + "'";
            List<Map> list = db.find(sqlH + sqlF);
            JSONArray json = JSONArray.fromObject(list);
            System.out.println(json.toString());
            pw = response.getWriter();
            pw.print(json.toString());
            pw.flush();

        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            pw.close();
            db.freeCon();
        }
    }

    @RequestMapping(value = "/doSave2", method = RequestMethod.POST)
    public void doSave2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String[] ids = request.getParameter("ids").split(",");
        //System.out.println(Arrays.toString(ids));
        String indid = request.getParameter("indid");
        //System.out.println(ckid);
        String[] downs = request.getParameter("downs").split(",");
        //System.out.println(Arrays.toString(manlimitdowns));
        String[] ups = request.getParameter("ups").split(",");
        String[] startages = request.getParameter("startages").split(",");
        //System.out.println(Arrays.toString(startages));
        String[] endages = request.getParameter("endages").split(",");
        String[] sexs = request.getParameter("sexs").split(",");
        String age = request.getParameter("age");
        String sex = request.getParameter("sex");
        //System.out.println(Arrays.toString(endages));
        //System.out.println(condition);
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();
            String sql;
            for (int i = 0; i < ids.length; i++) {//次数不多使用顺环
                String id = ids[i];
                List<Map> list = db.find("select id from pexam_itemref where id=" + id);

                if (list.size() > 0) {
                    Object[] object = new Object[6];
                    sql = "update pexam_itemref set agedown=to_number(?),ageup=to_number(?),down=?,up=?,sex=? where id=? and hosnum='" + hosnum + "' ";
                    if (!age.equals("0")) {
                        object[0] = startages[i];
                        object[1] = endages[i];
                    } else {
                        object[0] = "";
                        object[1] = "";
                    }
                    object[2] = downs[i];
                    object[3] = ups[i];
                    if (!sex.equals("0")) {
                        object[4] = sexs[i];
                    } else {
                        object[4] = "";
                    }
                    object[5] = id;
                    db.excute(sql, object);
                } else {
                    Object[] object = new Object[8];
                    sql = "insert into pexam_itemref(id,indid,agedown,ageup,down,up,sex,print,hosnum) values(?,?,?,?,?,?,?,?,'" + hosnum + "')";
                    object[0] = id;
                    object[1] = indid;
                    if (!age.equals("0")) {
                        object[2] = startages[i];
                        object[3] = endages[i];
                    } else {
                        object[2] = "";
                        object[3] = "";
                    }
                    object[4] = downs[i];
                    object[5] = ups[i];
                    if (!sex.equals("0")) {
                        object[6] = sexs[i];
                    } else {
                        object[6] = "";
                    }

                    object[7] = downs[i] + "-" + ups[i];
                    db.excute(sql, object);

                    //pexam_items_ind表haveref状态置Y
                    sql = "update pexam_items_ind t set t.haveref='Y' where t.indid=? and t.hosnum='" + hosnum + "'";
                    db.excute(sql, new Object[]{indid});
                }
            }
            pw.print("success");
            db.commit();
        } catch (Exception e) {
            pw.print("fail");
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
            pw.flush();
            pw.close();
        }
    }

    @RequestMapping(value = "/doDelete2", method = RequestMethod.POST)
    public void doDelete2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String id = request.getParameter("id");
        DBOperator db = null;
        try {
            db = new DBOperator();
            List<Map> list = db.find("select id from pexam_itemref where id='" + id + "' and hosnum='" + hosnum + "'");
            if (list.size() > 0) {
                db.excute("delete from pexam_itemref where id='" + id + "' and hosnum='" + hosnum + "'");
            }
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
    }

    @RequestMapping(value = "/getCheckitemReferenceId", method = RequestMethod.POST)
    public void getId(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String indid = request.getParameter("indid");
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            String sql = "select nvl(max(to_number(id)),0) as id from pexam_itemref ";
            db = new DBOperator();
            List<Map> list = db.find(sql);
            System.out.println();
            String id = Integer.parseInt(list.get(0).get("id") + "") + 1 + "";
            //System.out.println(id);
            pw = response.getWriter();
            pw.print(id);

        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
            pw.flush();
            pw.close();
        }
    }

    @RequestMapping("/getWSJGroup")
    public void getWSJGroup(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("utf-8");
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "select * from pexam_items_group a where a.hosnum='0000' and a.nodecode='0000' order by a.sn";
            List<Map> list = db.find(sql, new Object[]{});
            JSONArray jsonArr = JSONArray.fromObject(list);
            pw.print(jsonArr.toString());
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
            pw.flush();
            pw.close();
        }
    }

    //引入卫生局设的套餐
    @RequestMapping("/itemsGroupIntroduce")
    public void itemsGroupIntroduce(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        request.setCharacterEncoding("utf-8");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        DBOperator db = null;
        PrintWriter pw = null;
        String sql = "";
        String groupid = request.getParameter("groupid");
        String arr = request.getParameter("arr");
        //System.out.println(arr);
        JSONArray jsonArr = JSONArray.fromObject(arr);
        try {

            pw = response.getWriter();
            db = new DBOperator();
            sql = "select seq_pexam_groupid.nextval from dual ";
            Map map = (Map) db.findOne(sql);
            String new_groupid = "";
            if (oracleType == 11) {
                new_groupid = Integer.toString(((BigDecimal) map.get("nextval")).intValue());
            } else {
                new_groupid = Integer.toString((int) (double) (Double) map.get("nextval"));
            }

            sql = "insert into pexam_items_group select ?,?,groupname,cost,comments,sn,stopflag,?," +
                    "farmitem from pexam_items_group where groupid = ?";
            db.excute(sql, new Object[]{hosnum, new_groupid, nodecode, groupid});        //插入套餐

            sql = "select itemcode from pexam_items_groupdetails where hosnum='0000' and groupid=? ";
            List<Map> comList = db.find(sql, new Object[]{groupid});
            String sqlCom = "insert into pexam_items_com select ?,?,comname,comclass,forsex,?,?,cost," +
                    "sn,descriptions,comments,parentid,isuse,?,iscompare,year,jctype from pexam_items_com where comid = ? ";
            Object[][] params = new Object[comList.size()][2];
            List<Object[]> pi = new ArrayList<Object[]>();

            for (int i = 0; i < comList.size(); i++) {
                String sql0 = "select seq_pexam_comid.nextval from dual ";
                String comid = (String) comList.get(i).get("itemcode");
                String new_comid = "";
                Map mapComSeq = (Map) db.findOne(sql0);
                if (oracleType == 11) {
                    new_comid = Integer.toString(((BigDecimal) mapComSeq.get("nextval")).intValue());
                } else {
                    new_comid = Integer.toString((int) (double) (Double) mapComSeq.get("nextval"));
                }
                //更新执行科室
                for (int j = 0; j < jsonArr.size(); j++) {
                    String comid0 = jsonArr.getJSONObject(j).getString("comid");
                    String excdept = jsonArr.getJSONObject(j).getString("excdept");
                    String excdeptname = jsonArr.getJSONObject(j).getString("excdeptname");
                    if (comid0.equals(comid)) {
                        db.excute(sqlCom, new Object[]{hosnum, new_comid, excdept, excdeptname, nodecode, comid});
                    }
                }
                String sql1 = "insert into pexam_items_groupdetails select ?,?,sn,? " +
                        "from pexam_items_groupdetails where groupid=? and itemcode=?";
                db.excute(sql1, new Object[]{hosnum, new_groupid, new_comid, groupid, comid});        //插入套餐-大项关系

                String sql2 = "insert into pexam_items_comdet select ?,sys_guid(),?,indid,(select decode(max(sn),null,1,max(sn)+1) from pexam_items_comdet where comid=?) from pexam_items_comdet where comid=?";
                db.excute(sql2, new Object[]{hosnum, new_comid, comid, comid});                            //插入大项-小项关系
            }

			/*for(int j=0;j<pi.size();j++){
				params[j] = pi.get(j);
			}

			db.excute(sqlCom,new Object[]{params});		//插入大项
			*/
            db.commit();
            pw.print("success");
        } catch (Exception e) {
            pw.print("fail");
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
            pw.flush();
            pw.close();
        }
    }

    //获取套餐中组合项目的指标
    @RequestMapping("/getInds")
    public void getInds(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String comid = request.getParameter("comid");
        String operationType = request.getParameter("operationType");
        DBOperator db = null;
        PrintWriter pw = null;
        String sql = "";
        try {
            pw = response.getWriter();
            db = new DBOperator();
            if (operationType.equals("introduce")) {
                hosnum = "0000";
                nodecode = "0000";
            }
            sql = "select a.indid,a.indname,a.forsex,a.resulttype,b.sn from pexam_items_ind a,pexam_items_comdet b where a.indid=b.indid and a.hosnum=? and b.comid=? order by to_number(a.sn)";
            List list = db.find(sql, new Object[]{hosnum, comid});
            JSONArray jsonArr = JSONArray.fromObject(list);
            pw.print(jsonArr.toString());
        } catch (Exception e) {
            pw.print("fail");
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
            pw.flush();
            pw.close();
        }
    }

    //一键引入卫生局项目指标
    @RequestMapping("/oneKeyIntroduce")
    public void oneKeyIntroduce(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        DBOperator db = null;
        PrintWriter pw = null;
        String sql1 = "";
        String sql2 = "";
        String errorMsg = "";
        int num = 0;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            sql1 = "select indid from pexam_items_ind where hosnum='0000'";
            List<Map> list1 = db.find(sql1);
            for (int i = 0; i < list1.size(); i++) {
                String indid = (String) list1.get(i).get("indid");
                sql2 = "select indid from pexam_items_ind where hosnum=? and indid=?";
                List<Map> list2 = db.find(sql2, new Object[]{hosnum, indid});
                if (list2.size() == 0) {
                    sql2 = "insert into pexam_items_ind select ?,indid,indname,forsex,resultunit,maxval,minval,minpromp," +
                            "maxpromp,defaultv,comments,sn,resulttype,parentid,iscompare,islisind,iszh,isjy,inputcpy," +
                            "inputcwb,pdname,pdcode,haveref from pexam_items_ind where indid=? and hosnum='0000'";
                    db.excute(sql2, new Object[]{hosnum, indid});
                    num++;
                } else if (list2.size() != 1) {
                    errorMsg = "项目id" + indid + "重复，请联系管理员！";
                }
            }
            db.commit();
            pw.print(Integer.toString(num));
        } catch (Exception e) {
            pw.print(errorMsg);
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
            pw.flush();
            pw.close();
        }
    }

    //保存小项显示顺序
    @RequestMapping("/saveIndSN")
    public void saveIndSN(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String sn = request.getParameter("sn");
        String comid = request.getParameter("comid");
        String indid = request.getParameter("indid");
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "update pexam_items_comdet set sn = to_number(?) where comid=? and indid=? ";
            db.excute(sql, new Object[]{sn, comid, indid});
            pw.print("success");
        } catch (Exception e) {
            pw.print("fail");
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
            pw.flush();
            pw.close();
        }
    }

    //获取科室信息
    @RequestMapping("/getDept")
    public void getDept(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String groupid = request.getParameter("groupid");
        DBOperator db = null;
        PrintWriter pw = null;
        String sql = "";
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String hosname = "b.hosname";
            if (hosnum.equals(nodecode)) {
                sql = "select * from bas_hospitals where hosnum=? and nodecode!=?";        //查看是否存在下级
                List list = db.find(sql, new Object[]{hosnum, nodecode});
                if (list.size() > 0) {
                    hosname = "'本部'";
                }
            }
            sql = "select a.*,a.deptname||'（'||" + hosname + "||'）' as excdeptname from bas_dept a,bas_hospitals b where a.hosnum=? and a.nodecode=? and a.hosnum=b.hosnum and a.nodecode=b.nodecode";
            List<Map> list = db.find(sql, new Object[]{hosnum, nodecode});
            JSONArray json = JSONArray.fromObject(list);
            pw.print(json.toString());
        } catch (Exception e) {
            pw.print("fail");
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
            pw.flush();
            pw.close();
        }
    }

    //获取科室信息
    @RequestMapping("/getWSJItems")
    public void getWSJItems(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setCharacterEncoding("utf-8");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession()
                .getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String groupid = request.getParameter("groupid");
        DBOperator db = null;
        PrintWriter pw = null;
        String sql = "";
        try {
            pw = response.getWriter();
            db = new DBOperator();
            sql = "select c.* from pexam_items_group a,pexam_items_groupdetails b,pexam_items_com c where a.hosnum='0000' and a.nodecode='0000' and b.groupid=? and a.groupid=b.groupid and b.itemcode=c.comid ";
            List<Map> list = db.find(sql, new Object[]{groupid});
            JSONArray json = JSONArray.fromObject(list);
            pw.print(json.toString());
        } catch (Exception e) {
            pw.print("fail");
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
            pw.flush();
            pw.close();
        }
    }

    //获取组合的适用性别
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getitemsex", method = RequestMethod.POST)
    public void demo(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        DBOperator db = null;
        PrintWriter pw = null;
        String itemcode = request.getParameter("itemcode");
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String sql = "select a.forsex from PEXAM_ITEMS_COM a where a.comid='" + itemcode + "' ";
            List<Map> list = db.find(sql);
            String forsex = list.get(0).get("forsex").toString();
            if ("不限".equals(forsex)) {
                pw.print("男|女");
                System.out.println("男|女");
            } else {
                pw.print(forsex);
                System.out.println(forsex);
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            pw.print("fail");
        } finally {
            db.freeCon();
            pw.flush();
            pw.close();
        }
    }
}