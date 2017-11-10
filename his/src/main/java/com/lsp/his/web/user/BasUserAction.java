package com.lsp.his.web.user;

import com.lsp.his.db.DBOperator;
import com.lsp.his.model.ReturnValue;
import com.lsp.his.model.ZTreeNode;
import com.lsp.his.tables.Bas_dept;
import com.lsp.his.tables.Bas_dicts;
import com.lsp.his.tables.Bas_hospitals;
import com.lsp.his.tables.Bas_user;
import com.lsp.his.utils.ListUtil;
import com.lsp.his.utils.StrUtil;
import com.lsp.his.utils.VelocityUtils;
import com.lsp.his.utils.WordUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.*;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/8 23:05
 */

@Controller
@RequestMapping("/user")
public class BasUserAction {

    /**
     * 显示院区列表
     *
     * @return
     */
    @RequestMapping("/show")
    public ModelAndView show(HttpServletRequest request, HttpServletResponse response) {
        ModelMap model = new ModelMap();
        Bas_hospitals hospital = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        model.put("hosnum", StrUtil.strNullToEmpty(hospital.getHosnum()));
        model.put("nodecode", StrUtil.strNullToEmpty(hospital.getNodecode()));

        Bas_dept dept = (Bas_dept) request.getSession().getAttribute("login_dept");
        model.put("deptcode", StrUtil.strNullToEmpty(dept.getDeptcode()));
        return new ModelAndView("user/show", model);
    }

    /**
     * 显示院区列表
     *
     * @return
     */
    @RequestMapping("/showGlobal")
    public ModelAndView showGlobal(HttpServletRequest request, HttpServletResponse response) {
        ModelMap model = new ModelMap();
        Bas_hospitals hospital = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        model.put("hosnum", StrUtil.strNullToEmpty(hospital.getHosnum()));
        model.put("nodecode", StrUtil.strNullToEmpty(hospital.getNodecode()));

        Bas_dept dept = (Bas_dept) request.getSession().getAttribute("login_dept");
        model.put("deptcode", StrUtil.strNullToEmpty(dept.getDeptcode()));
        return new ModelAndView("user/showGlobal", model);
    }

    @RequestMapping(value = "/loadHosZtree", method = RequestMethod.GET)
    public void loadHosZtree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");

        String hosnum = basHospitals.getHosnum();
        String nodecode = basHospitals.getNodecode();
        String deptcode = basDept.getDeptcode();
        String operator = basUser.getId();

        ReturnValue returnValue = new ReturnValue();
        DBOperator db = new DBOperator();
        try {
            //加载当前登录人所在科室对应的病区的所有住院病人记录
            //String sql = "select t.hosnum,t.nodecode,t.hosname,t.supunit from Bas_hospitals t where t.nodetype='医院' and (t.hosnum = ? or t.supunit = ?) order by t.supunit,t.hosnum";
            //List<Bas_hospitals> hospitals = db.find(sql, new Object[]{hosnum, hosnum}, Bas_hospitals.class);
            String sql = "select t.hosnum, t.nodecode, t.hosname, t.supunit from Bas_hospitals t  " +
                    "where t.nodetype = '医院'  connect by  prior t.nodecode= t.supunit  start with t.hosnum='0000'" +
                    "order by sn";
            @SuppressWarnings("unchecked")
            List<Bas_hospitals> hospitals = db.find(sql, Bas_hospitals.class);
            String zTreeNodes = this.buildHosZTree(hospitals);
            returnValue.setStatus(true);
            returnValue.setMessage("加载卫生院成功！");
            returnValue.setValue(zTreeNodes);

            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();

            returnValue.setStatus(false);
            returnValue.setMessage("加载卫生院失败！");
        } finally {
            db.freeCon();
        }

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(JSONObject.fromObject(returnValue).toString());
        pw.flush();
        pw.close();
    }

    private String buildHosZTree(List<Bas_hospitals> hospitals) {
        StringBuilder sb = new StringBuilder("");
        sb.append("[");
        Iterator<Bas_hospitals> iterator = hospitals.iterator();
        while (iterator.hasNext()) {
            if (sb.length() > 1) {
                sb.append(",");
            }
            Bas_hospitals hospital = iterator.next();
            ZTreeNode zTreeNode = new ZTreeNode();
            zTreeNode.setId(hospital.getHosnum());
            zTreeNode.setPid(hospital.getSupunit());
            zTreeNode.setName(hospital.getHosname());
            zTreeNode.setOpen(true);
            zTreeNode.setClick("loadTree('" + hospital.getHosnum() + "','" + hospital.getNodecode() + "')");
            sb.append(zTreeNode);
        }
        sb.append("]");

        return sb.toString();
    }

    /**
     * ajax方式加载科室树
     *
     * @param request
     * @param response
     * @param modelMap
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/tree", method = RequestMethod.POST)
    public void loadTreeStr(HttpServletRequest request,
                            HttpServletResponse response, ModelMap modelMap) throws Exception {
        String hosnum = request.getParameter("hosnum");
        String hosNodecode = request.getParameter("hoscode");
        hosNodecode = hosnum;//自动设置为本院级管理
        // 查询数据
        DBOperator db = null;
        List hosList = null;
        List ksList = null;//科室
        List bqList = null;//病区
        try {
            db = new DBOperator();
            String sql = "";
            if (hosnum.equals(hosNodecode)) {
                sql = "select hosnum,hosname,nodecode,distcode,orgtype from BAS_Hospitals a where a.hosnum=?";
                hosList = db.find(sql, new Object[]{hosnum});

                sql = "select deptcode,deptname,parentid,nodecode,isleaf from bas_dept t "
                        + "where t.isdeleted='N' and t.HOSNum=? and deptclass='科室' order by deptcode";
                ksList = db.find(sql, new Object[]{hosnum});

                sql = "select deptcode,deptname,parentid,nodecode,isleaf from bas_dept t "
                        + "where t.isdeleted='N' and t.HOSNum=? and deptclass='病区' order by deptcode";
                bqList = db.find(sql, new Object[]{hosnum});
            } else {
                sql = "select hosnum,hosname,nodecode,distcode from BAS_Hospitals a where a.hosnum=? and a.nodecode=?";
                hosList = db.find(sql, new Object[]{hosnum, hosNodecode});

                sql = "select deptcode,deptname,parentid,nodecode,isleaf from bas_dept t "
                        + "where t.isdeleted='N' and t.HOSNum=? and t.nodecode=? and deptclass='科室' order by deptcode";
                ksList = db.find(sql, new Object[]{hosnum, hosNodecode});
            }
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
        String nodecode = "";
        String distcode = "";
        if (ListUtil.listIsNotEmpty(hosList)) {// 取得医院的名称，
            for (Iterator iterator = hosList.iterator(); iterator.hasNext(); ) {
                Map tempMap = (Map) iterator.next();
                if (hosNodecode.equals((String) tempMap.get("nodecode"))) {// 得到当前医院或院区
                    name = (String) tempMap.get("hosname");
                    nodecode = (String) tempMap.get("nodecode");
                    distcode = (String) tempMap.get("distcode");
                }
            }
        }

        if (name.equals("")) {
            lstTree.add("没有找到对应医院");
        } else {// 拼装树
            String s1 = "{id:\"0\", pId:\"-1\", name:\"" + name + "\" , open:true, distCode:\"" + distcode + "\"}";// 根节点
            lstTree.add(s1);

            String s2 = "";
            //科室
            s2 = "{id:\"01\", pId:\"0\", name:\"科室\",isLast:\"N\", treeType:\"ks\",topShow:\"Y\"," +
                    "yqId:\"" + nodecode + "\"," +
                    "yqName:\"" + name + "\"}";// 2级节点--科室
            lstTree.add(s2);
            if (ListUtil.listIsNotEmpty(ksList)) {// 存在科室
                for (Iterator ksIterator = ksList.iterator(); ksIterator.hasNext(); ) {
                    Map ksMap = (Map) ksIterator.next();
                    if (nodecode.equals((String) ksMap.get("nodecode"))) {//加载本院的科室
                        temp = "{id:\"" + ksMap.get("deptcode") + "\"," +
                                "pId:\"" + StrUtil.strNullToDef((String) ksMap.get("parentid"), "01") + "\"," +
                                "name:\"" + ksMap.get("deptname") + "\"," +
                                "isLast:\"" + ksMap.get("isleaf") + "\"," +
                                "treeType:\"ks\", " +
                                "yqId:\"" + nodecode + "\"," +
                                "yqName:\"" + name + "\"}";// 子节点
                        lstTree.add(temp);
                    }
                }
            }
            if (hosnum.equals(hosNodecode)) {//医院编码和院区编码相等时，才维护病区、服务站
                //病区
                s2 = "{id:\"02\", pId:\"0\", name:\"病区\", isLast:\"N\", treeType:\"bq\", topShow:\"Y\", " +
                        "yqId:\"" + nodecode + "\"," +
                        "yqName:\"" + name + "\"}";// 2级节点--病区
                lstTree.add(s2);
                if (ListUtil.listIsNotEmpty(bqList)) {// 存在病区
                    for (Iterator bqIterator = bqList.iterator(); bqIterator.hasNext(); ) {
                        Map bqMap = (Map) bqIterator.next();
                        String mySkin = "ico_close";
                        if (nodecode.equals((String) bqMap.get("nodecode"))) {//加载本院的病区
                            temp = "{id:\"" + bqMap.get("deptcode") + "\"," +
                                    "pId:\"" + StrUtil.strNullToDef((String) bqMap.get("parentid"), "02") + "\"," +
                                    "name:\"" + bqMap.get("deptname") + "\"," +
                                    "isLast:\"" + bqMap.get("isleaf") + "\"," +
                                    "treeType:\"bq\"," +
                                    "yqId:\"" + nodecode + "\"," +
                                    "yqName:\"" + name + "\"," +
                                    "iconSkin: \"" + mySkin + "\"}";// 子节点
                            lstTree.add(temp);
                        }
                    }
                }

                //社区卫生服务站，村卫生室
                Map typeMap = new LinkedHashMap<String, String>();
                typeMap.put("03", "社区卫生服务站");
                typeMap.put("04", "村卫生室");

                for (Iterator<Map.Entry<String, String>> typeIterator = typeMap.entrySet().iterator(); typeIterator.hasNext(); ) {
                    Map.Entry<String, String> entry = typeIterator.next();
                    String typeId = entry.getKey();
                    String typeName = entry.getValue();
                    s2 = "{id:\"" + typeId + "\", pId:\"0\", name:\"" + typeName + "\", isLast:\"N\", treeType:\"fwz\", topShow:\"Y\", " +
                            "distCode:\"" + distcode + "\"}";// 2级节点--服务站
                    lstTree.add(s2);
                    if (ListUtil.listIsNotEmpty(hosList)) {// 存在院区
                        for (Iterator hosIterator = hosList.iterator(); hosIterator.hasNext(); ) {
                            Map hosMap = (Map) hosIterator.next();
                            String curNodeCode = (String) hosMap.get("nodecode");// 院区编码
                            String curHosName = (String) hosMap.get("hosname");// 院区名称
                            if (typeName.equals(((String) hosMap.get("orgtype")))) {//根据机构分类--进行分类
                                temp = "{id:\"" + typeId + curNodeCode + "\"," +
                                        "pId:\"" + typeId + "\"," +
                                        "name:\"" + (String) hosMap.get("hosname") + "\"," +
                                        "treeType:\"fwz\"}";// 子节点
                                lstTree.add(temp);//3级节点
                                temp = "{id:\"01" + typeId + curNodeCode + "\", pId:\"" + typeId + curNodeCode + "\", name:\"科室\",isLast:\"N\", treeType:\"ks\",topShow:\"Y\"," +
                                        "yqId:\"" + curNodeCode + "\"," +
                                        "yqName:\"" + curHosName + "\"}";
                                lstTree.add(temp);// 4级节点--科室

                                if (ListUtil.listIsNotEmpty(ksList)) {// 存在科室
                                    for (Iterator ksIterator = ksList.iterator(); ksIterator.hasNext(); ) {
                                        Map ksMap = (Map) ksIterator.next();
                                        if (curNodeCode.equals(ksMap.get("nodecode"))) {// 匹配的院区下面才加
                                            temp = "{id:\"" + ksMap.get("deptcode") + "\"," +
                                                    "pId:\"" + StrUtil.strNullToDef((String) ksMap.get("parentid"), "01" + typeId + curNodeCode) + "\"," +
                                                    "name:\"" + ksMap.get("deptname") + "\"," +
                                                    "isLast:\"" + ksMap.get("isleaf") + "\"," +
                                                    "treeType:\"ks\"," +
                                                    "yqId:\"" + curNodeCode + "\"," +
                                                    "yqName:\"" + curHosName + "\"}";// 子节点
                                            lstTree.add(temp);
                                        }
                                    }
                                }
                            }
                        }
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

    /**
     * ajax方式加载角色范围树
     *
     * @param request
     * @param response
     * @param modelMap
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/role_tree", method = RequestMethod.GET)
    public void loadDeptScopeTree(HttpServletRequest request,
                                  HttpServletResponse response, ModelMap modelMap) throws Exception {
        Bas_user user = (Bas_user) request.getSession().getAttribute("login_user");
        Bas_hospitals hospital = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        Bas_dept basDept = (Bas_dept) request.getSession().getAttribute("login_dept");
        String checkedScope = request.getParameter("checkedScope");// 选中的范围
        String[] checkedList = null;
        if (StrUtil.strIsNotEmpty(checkedScope)) {
            checkedList = checkedScope.split(";");
        }

        // 组装的树数据格式
        List<String> lstTree = new ArrayList<String>();
        // 查询数据
        DBOperator db = null;
        List scopeList = null;
        List<Bas_dicts> roleLvlList = null;
        try {
            db = new DBOperator();
            String sql = "select b.lvl from bas_user_dept_role_relation a,bas_role b " +
                    "where a.hosnum=? and a.nodecode=? and a.user_id=? and a.office_id=? and a.role_id is not null and b.id=a.role_id";
            Map lvlMap = (Map) db.findOne(sql, new Object[]{basDept.getHosnum(), basDept.getNodecode(), user.getId(), basDept.getDeptcode()});
            String lvl = "1";
            if (lvlMap != null && lvlMap.size() > 0) {
                lvl = (String) lvlMap.get("lvl");
            }
            if ("1".equals(lvl)) {
                lvl = "'1','2','3'";
            } else if ("2".equals(lvl)) {
                lvl = "'2','3'";
            } else if ("3".equals(lvl)) {
                lvl = "'3'";
            } else {
                lvl = "'1','2','3'";
            }
            sql = "select id,lvl,name from BAS_ROLE t where t.lvl in (" + lvl + ") order by lvl,index_no";
            scopeList = db.find(sql);

            sql = "select * from bas_dicts t where t.hosnum = '0000' and t.nekey = 60 and t.nevalue != '!' and t.nevalue in (" + lvl + ") order by t.nevalue asc";
            roleLvlList = db.find(sql, Bas_dicts.class);
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }

        String s1 = "{id:\"0\", pId:\"-1\", name:\"所有角色\" , open:true}";// 根节点
        lstTree.add(s1);

        if (ListUtil.listIsNotEmpty(roleLvlList)) {
            List<String> sysLvlList = new ArrayList<String>();
            for (Iterator<Bas_dicts> roleLvlIterator = roleLvlList.iterator(); roleLvlIterator.hasNext(); ) {
                Bas_dicts tempDict = roleLvlIterator.next();
                sysLvlList.add(tempDict.getNevalue());
                String s2 = "{id:\"" + tempDict.getNevalue() + "\", pId:\"0\", name:\"" + tempDict.getContents() + "\" , open:true}";//角色级别
                lstTree.add(s2);
            }

            if (ListUtil.listIsNotEmpty(scopeList)) {// 存在角色
                for (Iterator scopeIterator = scopeList.iterator(); scopeIterator
                        .hasNext(); ) {
                    Map scopeMap = (Map) scopeIterator.next();
                    String sysLvl = (String) scopeMap.get("lvl");
                    if (StrUtil.strCheckInList(sysLvl, sysLvlList)) {//角色级别存在时，才添加下级
                        String temp = "{id:\"" + scopeMap.get("id") + "\","
                                + "pId:\"" + sysLvl + "\","
                                + "name:\"" + scopeMap.get("name") + "\","
                                + "checked:" + StrUtil.strIsInArrary((String) scopeMap.get("id"), checkedList) + ","
                                + "isLast:\"Y\"}";// 子节点
                        lstTree.add(temp);
                    }
                }
            }
        }

        // 传到前台
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = null;
        pw = response.getWriter();
//		System.out.print(JSONArray.fromObject(lstTree).toString());
        pw.print(JSONArray.fromObject(lstTree).toString());
        pw.flush();
        pw.close();
    }

    /**
     * 转到用户列表页面（同院区下所有用户）
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/user_list_other")
    public ModelAndView showListOther(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String hosnum = request.getParameter("hosnum");
        String nodecode = request.getParameter("nodecode");
        String deptcode = request.getParameter("deptcode");
        String type = request.getParameter("type");

        ModelMap model = new ModelMap();
        model.put("hosnum", hosnum);
        model.put("nodecode", nodecode);
        model.put("deptcode", deptcode);
        model.put("type", type);
        return new ModelAndView("user/user_list_other", model);
    }

    /**
     * ajax方式加载用户列表显示数据（同院区下所有用户）
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/user_list_other_load", method = RequestMethod.GET)
    public void loadListOther(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        String hosnum = request.getParameter("hosnum");
        String nodecode = request.getParameter("nodecode");
        String deptcode = request.getParameter("deptcode");

        // 数据库查询
        DBOperator db = null;
        List userList = null;
        try {
            db = new DBOperator();
            String sql = "select t1.*,t.deptname  from bas_dept t,(select a.id,a.hosnum,a.user_key,a.name,a.sex,a.idcard," +
                    "case when b.office_id is not null then '科室' else '病区' end depttype," +
                    "case when b.office_id is not null then b.office_id else b.shayne_id end deptid " +
                    "from bas_user a, bas_user_dept_role_relation b " +
                    "where a.id=b.user_id and (b.office_id is null or b.shayne_id is null)" +
                    "and a.hosnum=? and a.nodecode=? and not exists" +
                    "(select rowid from bas_user_dept_role_relation d " +
                    "where d.user_id = a.id and d.hosnum = ? and d.nodecode = ? " +
                    "and (d.office_id = ? or d.shayne_id = ?)) ) t1 " +
                    "where t.hosnum=t1.hosnum and t.deptcode=t1.deptid and t.isdeleted='N' order by t.deptname,t1.user_key";
            userList = db.find(sql, new Object[]{hosnum, nodecode, hosnum, nodecode, deptcode, deptcode});
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }

        // 数据填充到模版
        response.setContentType("text/xml;charset=utf-8");
        String vmpagckage = "com/lsp/his/template/user/";
        String vmname = "user_list_other.vm";
        PrintWriter pw = null;
        pw = response.getWriter();
        String vm = VelocityUtils.generateGridVm(vmpagckage, vmname, "userList", userList);
        pw.print(vm);
        pw.flush();
        pw.close();
    }

    /**
     * 保存已有用户到本科室
     *
     * @param request
     * @param response
     * @param
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/user_other_saved", method = RequestMethod.POST)
    public void doOtherSaved(HttpServletRequest request,
                             HttpServletResponse response)
            throws Exception {
        String hosnum = request.getParameter("hosnum");
        String nodecode = request.getParameter("nodecode");
        String deptcode = request.getParameter("deptcode");
        String type = request.getParameter("type");
        String checkIds = StrUtil.strNullToEmpty(request.getParameter("checkIds"));
        String[] Ids = checkIds.split(",");//得到用户ID以及科室

        Set<String> userIdSet = new HashSet<String>();
        for (int i = 0; i < Ids.length; i++) {
            String[] userIds = Ids[i].split(";");
            if (userIds.length == 2) {
                userIdSet.add(userIds[0]);
            }
        }

        DBOperator db = null;
        try {
            db = new DBOperator();
            for (Iterator<String> iterator = userIdSet.iterator(); iterator.hasNext(); ) {
                String userId = iterator.next();
                if (StrUtil.strIsNotEmpty(userId)) {
                    if ("ks".equals(type)) {
                        db.excute("insert into bas_user_dept_role_relation(HOSNUM,USER_ID,OFFICE_ID,SHAYNE_ID,ROLE_ID,NODECODE)" +
                                        " values(?,?,?,?,?,?)",
                                new Object[]{hosnum, userId, deptcode, null, null, nodecode});
                    } else if ("bq".equals(type)) {
                        db.excute("insert into bas_user_dept_role_relation(HOSNUM,USER_ID,OFFICE_ID,SHAYNE_ID,ROLE_ID,NODECODE)" +
                                        " values(?,?,?,?,?,?)",
                                new Object[]{hosnum, userId, null, deptcode, null, nodecode});
                    }
                }
            }
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            pw.print("success");
        } catch (Exception e) {
            e.printStackTrace();
            pw.print("fail");
        }
    }


    /**
     * 转到用户列表页面
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/user_list")
    public ModelAndView showList(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String hosnum = request.getParameter("hosnum");
        String nodecode = request.getParameter("nodecode");
        String deptcode = request.getParameter("deptcode");
        String type = request.getParameter("type");
        String islast = request.getParameter("islast");

        ModelMap model = new ModelMap();
        model.put("hosnum", hosnum);
        model.put("nodecode", nodecode);
        model.put("deptcode", deptcode);
        model.put("type", type);
        model.put("islast", islast);
        return new ModelAndView("user/user_list", model);
    }

    /**
     * ajax方式加载用户列表显示数据
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/user_list_load", method = RequestMethod.GET)
    public void loadList(HttpServletRequest request,
                         HttpServletResponse response) throws Exception {
        String hosnum = request.getParameter("hosnum");
        String nodecode = request.getParameter("nodecode");
        String deptcode = request.getParameter("deptcode");
        String type = request.getParameter("type");
        String islast = request.getParameter("islast");

        // 数据库查询
        DBOperator db = null;
        List userList = null;
        try {
            db = new DBOperator();
            if ("ks".equals(type)) {//科室
                if (StrUtil.strIsNotEmpty(islast)) {
                    if (StrUtil.strIsNotEmpty(deptcode)) {

                    } else {
                        userList = db.find("select t1.*,t.deptname  from bas_dept t," +
                                        "(select a.hosnum,a.id,a.job_no,a.name,a.sex,a.idcard,a.phone,a.mobile,b.office_id deptid " +
                                        "from bas_user a, bas_user_dept_role_relation b where a.stop_sign='N' and a.del_sign='N' " +
                                        "and b.role_id is null and b.user_id=a.id and b.hosnum=? and b.nodecode=? and b.office_id is not null) t1 " +
                                        "where t.hosnum=t1.hosnum and t.deptcode=t1.deptid and t.isdeleted='N' order by t.deptname",
                                new Object[]{hosnum, nodecode});
                    }
                } else {
                    String sql = "select t1.*,t.deptname  from bas_dept t," +
                            "(select a.hosnum,a.id,a.job_no,a.name,a.sex,a.idcard,a.phone,a.mobile,b.office_id deptid " +
                            "from bas_user a, bas_user_dept_role_relation b where a.stop_sign='N' and a.del_sign='N' " +
                            "and b.role_id is null and b.user_id=a.id and b.hosnum=? and b.nodecode=? and b.office_id=?) t1 " +
                            "where t.hosnum=t1.hosnum and t.deptcode=t1.deptid and t.isdeleted='N'";
                    userList = db.find(sql, new Object[]{hosnum, nodecode, deptcode});
                }
            } else if ("bq".equals(type)) {//病区
                if (StrUtil.strIsNotEmpty(islast)) {
                    if (StrUtil.strIsNotEmpty(deptcode)) {

                    } else {
                        userList = db.find("select t1.*,t.deptname  from bas_dept t," +
                                        "(select a.hosnum,a.id,a.job_no,a.name,a.sex,a.idcard,a.phone,a.mobile,b.shayne_id deptid " +
                                        "from bas_user a, bas_user_dept_role_relation b where a.stop_sign='N' and a.del_sign='N' " +
                                        "and b.role_id is null and b.user_id=a.id and b.hosnum=? and b.nodecode=? and b.shayne_id is not null) t1 " +
                                        "where t.hosnum=t1.hosnum and t.deptcode=t1.deptid and t.isdeleted='N' order by t.deptname",
                                new Object[]{hosnum, nodecode});
                    }
                } else {
                    String sql = "select t1.*,t.deptname  from bas_dept t," +
                            "(select a.hosnum,a.id,a.job_no,a.name,a.sex,a.idcard,a.phone,a.mobile,b.shayne_id deptid " +
                            "from bas_user a, bas_user_dept_role_relation b where a.stop_sign='N' and a.del_sign='N' " +
                            "and b.role_id is null and b.user_id=a.id and b.hosnum=? and b.nodecode=? and b.shayne_id=?) t1 " +
                            "where t.hosnum=t1.hosnum and t.deptcode=t1.deptid and t.isdeleted='N'";
                    userList = db.find(sql, new Object[]{hosnum, nodecode, deptcode});
                }
            }
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }
        // 数据填充到模版
        response.setContentType("text/xml;charset=utf-8");
        String vmpagckage = "com/lsp/his/template/user/";
        String vmname = "user_list.vm";
        PrintWriter pw = null;
        pw = response.getWriter();
        String vm = VelocityUtils.generateGridVm(vmpagckage, vmname, "userList", userList);
        pw.print(vm);
        pw.flush();
        pw.close();
    }

    /**
     * 转到用户新增页面
     *
     * @param request
     * @param response
     * @param modelMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/user_save")
    public ModelAndView doSave(HttpServletRequest request,
                               HttpServletResponse response, ModelMap modelMap) throws Exception {
        // 新增需要传入默认编码；修改和查看需要传入ID
        String operationType = request.getParameter("operationType");// 操作类型：add（新增）,modify（修改）,view（查看）
        String operationId = request.getParameter("operationId");// 操作行的ID
        String defaultHos = request.getParameter("defaultHos");// 默认的医院编码
        String defaultNode = request.getParameter("defaultNode");// 默认的院区
        String defaultDept = request.getParameter("defaultDept");// 默认的科室
        String defaultType = request.getParameter("defaultType");// 默认的科室类型
        String save_num = StrUtil.strNullToDef(request.getParameter("save_num"), "0");//保存次数

        DBOperator db = null;
        List<Bas_user> userList = null;
        List<Bas_dicts> bds = null;
        List<Map> roleList = null;
        List<Map> deptAllList = null;
        try {
            db = new DBOperator();
            String sql = "select * from bas_dicts t where t.hosnum = '0000' and t.nekey in(34, 59) and t.nevalue != '!' order by t.nevalue asc";
            bds = db.find(sql, Bas_dicts.class);

            sql = "select a.deptcode, a.deptname, a.inputcpy, a.inputcwb  from bas_dept a " +
                    "where a.hosnum=? and a.nodecode=? and a.isdeleted='N'";
            deptAllList = db.find(sql, new Object[]{defaultHos, defaultNode});

            if ("modify".equals(operationType) || "view".equals(operationType)) {// 修改；查看
                String[] ids = operationId.split(";");
                if (ids.length == 2) {
                    defaultDept = ids[1];//更新为当前选择记录的科室代码
                    sql = "select * from bas_user t where id=?";
                    if (StrUtil.strIsNotEmpty(ids[0])) {
                        userList = db.find(sql, new Object[]{ids[0]}, Bas_user.class);
                        if ("ks".equals(defaultType)) {
                            roleList = db.find("select distinct role_id from BAS_USER_DEPT_ROLE_RELATION where user_id=? and office_id = ? and role_id is not null", new Object[]{ids[0], defaultDept});
                        } else if ("bq".equals(defaultType)) {
                            roleList = db.find("select distinct role_id from BAS_USER_DEPT_ROLE_RELATION where user_id=? and shayne_id = ? and role_id is not null", new Object[]{ids[0], defaultDept});
                        }
                    }
                }
            }

            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }

        List<Bas_dicts> sexList = new ArrayList<Bas_dicts>();// 性别
        List<Bas_dicts> postList = new ArrayList<Bas_dicts>();// 职称
        for (Bas_dicts bd : bds) {
            if (bd.getNekey() == 34 && ("男".equals(bd.getContents()) || "女".equals(bd.getContents()))) {
                sexList.add(bd);
            } else if (bd.getNekey() == 59) {
                postList.add(bd);
            }
        }
        if (deptAllList == null) {
            deptAllList = new ArrayList<Map>();
        }
        modelMap.put("deptAllSize", deptAllList.size());
        modelMap.put("deptAllList", deptAllList);
        modelMap.put("sexList", sexList);
        modelMap.put("postList", postList);

        modelMap.put("defaultDept", defaultDept);
        modelMap.put("defaultType", defaultType);
        modelMap.put("operationType", operationType);
        modelMap.put("save_num", save_num);
        if ("add".equals(operationType)) {// 新增
            Bas_user user = new Bas_user();
            user.setHosnum(defaultHos);
            user.setNodecode(defaultNode);
            //给页面传入 一个默认的工号
            db = new DBOperator();
            String gonghao = "yh"; //工号
            gonghao = gonghao + defaultDept.substring(3, defaultDept.length());
            String sql = "select b.job_no from bas_user  b where b.hosnum='1001' and upper(b.job_no) like 'YH" + defaultDept.substring(3, defaultDept.length()) + "%' order by b.job_no  ";
            List<Map> templist = db.find(sql);
            if (ListUtil.listIsNotEmpty(templist)) {
                Map tempmap = templist.get(templist.size() - 1);
                String a = tempmap.get("job_no").toString();
                a = a.substring(4, a.length());
                String b = (Integer.parseInt(a) + 1) + "";
                if (b.length() == 1) {
                    b = "00" + b;
                }
                if (b.length() == 2) {
                    b = "0" + b;
                }
                gonghao = gonghao + "" + b;
            } else {
                gonghao = gonghao + "001";
            }
            user.setJob_no(gonghao);
            modelMap.put("user", user);
        } else {// 修改；查看
            if (ListUtil.listIsNotEmpty(roleList)) {
                StringBuffer checkedScope = new StringBuffer();
                for (Iterator<Map> iterator = roleList.iterator(); iterator.hasNext(); ) {
                    Map scopeMap = iterator.next();
                    checkedScope.append(scopeMap.get("role_id") + ";");
                }
                modelMap.put("checkedScope", checkedScope.toString());
            }
            Bas_user user = (Bas_user) ListUtil.distillFirstRow(userList);
            modelMap.put("user", user);
        }

        return new ModelAndView("user/user_save", modelMap);
    }

    /**
     * 新增或者修改用户表
     *
     * @param request
     * @param response
     * @param
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/user_saved", method = RequestMethod.POST)
    public void doSaved(HttpServletRequest request,
                        HttpServletResponse response, Bas_user user, String job_no)
            throws Exception {
        System.out.println(job_no);
        user.setJob_no(job_no);
        String operationType = request.getParameter("operationType");// 操作类型：add（新增）,modify（修改）
        String defaultDept = request.getParameter("defaultDept");// 默认的科室
        String defaultType = request.getParameter("defaultType");// 默认的科室类型
        String temp_birthdate = request.getParameter("temp_birthdate");//出生日期
        user.setBirthdate(StrUtil.strToDate(temp_birthdate));//字符串转日期

        String addscopes = request.getParameter("addscopes");// 新增的范围
        String removescopes = request.getParameter("removescopes");// 删除的范围

        if (StrUtil.strIsNotEmpty(user.getUser_key())) {
            user.setInput_cwb(WordUtil.trans2WbCode(user.getUser_key()));
            user.setInput_cpy(WordUtil.trans2PyCode(user.getUser_key()));
        }

        response.setContentType("text/html;charset=utf-8");
        String userlevel = request.getParameter("userlevel");// 人员级别
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            if (user.getConsole_sign() != null && user.getConsole_sign().equals("on")) {
                user.setConsole_sign("Y");
            } else {
                user.setConsole_sign("N");
            }
            if ("add".equals(operationType)) {
                List basuserList = db.find("select * from bas_user b  where b.job_no=? ", new Object[]{user.getJob_no()});
                if (basuserList.size() > 0) {
                    pw.print("该工号已被使用，请重新输入一个工号");
                    return;
                } else {
                    List numList = db.find("select rowid num from bas_dept b where b.hosnum=? " +
                                    "and b.nodecode=? and b.deptcode=? and b.isdeleted='N'",
                            new Object[]{user.getHosnum(), user.getNodecode(), defaultDept});
                    if (!ListUtil.listIsNotEmpty(numList)) {
                        pw.print("noexists");
                        return;
                    }
                    //根据序列生成定长补0的字符串
                    String[] params = {""};
                    int[] types = {java.sql.Types.VARCHAR};
                    boolean[] isOut = {true};
                    db.oracleCall("{? = call FUN_BAS_USER_ID_CREATE()}", params, types, isOut);
                    if ("".equals(params[0])) {
                        throw new Exception("生成用户ID出错，请检查");
                    }
                    user.setId(params[0]);//主键
                    user.setReg_date(new Date());//日期
                    user.setDel_sign("N");//删除标志
                    user.setStop_sign("N");//停权标志
                    db.excute("insert into bas_user(ID,NODECODE,HOSNUM,USER_KEY,PASSWORD,NAME,IDCARD,SEX,BIRTHDATE," +
                                    "PHONE,MOBILE,SHORT_MOBILE,EMAIL,POST,POST_CODE,INDEX_NO,REG_DATE,STOP_SIGN,DEL_SIGN,REMARK," +
                                    "INPUT_CPY,INPUT_CWB,INPUT_CUSTOM,JOB_NO,PERSON_DEPT,POSID,CONSOLE_SIGN,userlevel) " +
                                    "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                            new Object[]{user.getId(),
                                    user.getNodecode(), user.getHosnum(),
                                    user.getUser_key(), user.getPassword(),
                                    user.getName(), user.getIdcard(),
                                    user.getSex(), user.getBirthdate(),
                                    user.getPhone(), user.getMobile(),
                                    user.getShort_mobile(),
                                    user.getEmail(), user.getPost(),
                                    user.getPost_code(),
                                    user.getIndex_no(), user.getReg_date(),
                                    user.getStop_sign(),
                                    user.getDel_sign(), user.getRemark(),
                                    user.getInput_cpy(),
                                    user.getInput_cwb(),
                                    user.getInput_custom(),
                                    user.getJob_no(),
                                    user.getPerson_dept(),
                                    user.getPosid(), user.getConsole_sign(), userlevel});
                    if ("ks".equals(defaultType)) {//添加关系表
                        db.excute("insert into bas_user_dept_role_relation(HOSNUM,USER_ID,OFFICE_ID,SHAYNE_ID,ROLE_ID,NODECODE)" +
                                        " values(?,?,?,?,?,?)",
                                new Object[]{user.getHosnum(), user.getId(), defaultDept, null, null, user.getNodecode()});
                    } else if ("bq".equals(defaultType)) {
                        db.excute("insert into bas_user_dept_role_relation(HOSNUM,USER_ID,OFFICE_ID,SHAYNE_ID,ROLE_ID,NODECODE)" +
                                        " values(?,?,?,?,?,?)",
                                new Object[]{user.getHosnum(), user.getId(), null, defaultDept, null, user.getNodecode()});
                    }
                }
            } else if ("modify".equals(operationType)) {
                db.excute("update bas_user set NODECODE=?,HOSNUM=?,USER_KEY=?,PASSWORD=?,NAME=?,IDCARD=?," +
                                "SEX=?,BIRTHDATE=?,PHONE=?,MOBILE=?,SHORT_MOBILE=?,EMAIL=?,POST=?,POST_CODE=?,INDEX_NO=?," +
                                "REMARK=?,INPUT_CPY=?,INPUT_CWB=?,INPUT_CUSTOM=?,JOB_NO=?,PERSON_DEPT=?,POSID=?,CONSOLE_SIGN=?,userlevel=?  where ID=?",
                        new Object[]{user.getNodecode(),
                                user.getHosnum(),
                                user.getUser_key(), user.getPassword(),
                                user.getName(), user.getIdcard(),
                                user.getSex(), user.getBirthdate(),
                                user.getPhone(), user.getMobile(),
                                user.getShort_mobile(),
                                user.getEmail(), user.getPost(),
                                user.getPost_code(),
                                user.getIndex_no(), user.getRemark(),
                                user.getInput_cpy(),
                                user.getInput_cwb(),
                                user.getInput_custom(),
                                user.getJob_no(), user.getPerson_dept(),
                                user.getPosid(), user.getConsole_sign(), userlevel, user.getId()});
            }
            /*新增或者删除角色*/
            if (StrUtil.strIsNotEmpty(removescopes)) {// 批量删除菜单范围
                String[] removeList = removescopes.split(";");
                for (int i = 0; i < removeList.length; i++) {
                    if ("ks".equals(defaultType)) {//添加关系表
                        db.excute("delete from bas_user_dept_role_relation where hosnum=? " +
                                        "and nodecode=? and office_id=? and user_id=? and role_id=?",
                                new Object[]{user.getHosnum(), user.getNodecode(), defaultDept, user.getId(), removeList[i]});
                    } else if ("bq".equals(defaultType)) {
                        db.excute("delete from bas_user_dept_role_relation where hosnum=? " +
                                        "and nodecode=? and SHAYNE_ID=? and user_id=? and role_id=?",
                                new Object[]{user.getHosnum(), user.getNodecode(), defaultDept, user.getId(), removeList[i]});
                    }
                }
            }
            if (StrUtil.strIsNotEmpty(addscopes)) {// 批量新增菜单范围
                String[] addList = addscopes.split(";");
                for (int i = 0; i < addList.length; i++) {
                    if ("ks".equals(defaultType)) {//添加关系表
                        db.excute("insert into bas_user_dept_role_relation(HOSNUM,USER_ID,OFFICE_ID,SHAYNE_ID,ROLE_ID,NODECODE)" +
                                        " values(?,?,?,?,?,?)",
                                new Object[]{user.getHosnum(), user.getId(), defaultDept, null, addList[i], user.getNodecode()});
                    } else if ("bq".equals(defaultType)) {
                        db.excute("insert into bas_user_dept_role_relation(HOSNUM,USER_ID,OFFICE_ID,SHAYNE_ID,ROLE_ID,NODECODE)" +
                                        " values(?,?,?,?,?,?)",
                                new Object[]{user.getHosnum(), user.getId(), null, defaultDept, addList[i], user.getNodecode()});
                    }
                }
            }
            db.commit();
            pw.print(user.getId());
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            pw.print("fail");
        } finally {
            db.freeCon();
        }
    }

    /**
     * 用户名是否重复验证
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/user_save_check", method = RequestMethod.POST)
    public void doSaveCheck(HttpServletRequest request,
                            HttpServletResponse response) throws Exception {
        String user_key = request.getParameter("user_key");

        List rowidList = null;
        DBOperator db = null;
        try {
            db = new DBOperator();
            if (StrUtil.strIsNotEmpty(user_key)) {
                String sql = "select rowid from bas_user where user_key=?";
                rowidList = db.find(sql, new Object[]{user_key});
            }
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = null;
        pw = response.getWriter();
        if (ListUtil.listIsNotEmpty(rowidList)) {
            pw.print("Y");
        } else {
            pw.print("N");
        }
    }

    /**
     * 删除用户表
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/user_remove", method = RequestMethod.POST)
    public void doDelete(HttpServletRequest request,
                         HttpServletResponse response) throws Exception {
        String checkIds = StrUtil.strNullToEmpty(request.getParameter("checkIds"));
        //String deptcode = StrUtil.strNullToEmpty(request.getParameter("deptcode"));
        String type = StrUtil.strNullToEmpty(request.getParameter("type"));
        String[] Ids = checkIds.split(",");

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            for (int i = 0; i < Ids.length; i++) {
                String[] userIds = Ids[i].split(";");
                if (userIds.length == 2) {
                    if ("ks".equals(type)) {
                        String delSql = "delete from bas_user_dept_role_relation where office_id=? and user_id=?";//删除关系数据
                        db.excute(delSql, new Object[]{userIds[1], userIds[0]});
                    } else if ("bq".equals(type)) {
                        String delSql = "delete from bas_user_dept_role_relation where shayne_id=? and user_id=?";//删除关系数据
                        db.excute(delSql, new Object[]{userIds[1], userIds[0]});
                    }
                    List relationList = db.find("select rowid from bas_user_dept_role_relation b where b.user_id=? and rownum=1",
                            new Object[]{userIds[0]});
                    if (!ListUtil.listIsNotEmpty(relationList)) {//relationList为空
                        db.excute("update bas_user a set a.del_sign='Y' where a.id=?", new Object[]{userIds[0]});
                    }
                }
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

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/userorder")
    public ModelAndView userorder(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String hosnum = request.getParameter("hosnum");
        String nodecode = request.getParameter("nodecode");
        String deptcode = request.getParameter("deptcode");
        String type = request.getParameter("type");
        String islast = request.getParameter("islast");

        ModelMap model = new ModelMap();
        model.put("hosnum", hosnum);
        model.put("nodecode", nodecode);
        model.put("deptcode", deptcode);
        model.put("type", type);
        model.put("islast", islast);
        return new ModelAndView("user/user_list2", model);
    }

    @RequestMapping(value = "/userCount")
    public void userCount(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            Bas_hospitals basHospital = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
            String sql = "select count(*) num from ( select distinct t1.hosnum,t1.job_no,t1.name,t1.sex,t1.idcard,t1.phone,t1.mobile from bas_dept t, " +
                    "(select a.hosnum, a.id,a.job_no,a.name, a.sex,a.idcard,a.phone,a.mobile,b.office_id deptid from bas_user a, " +
                    "bas_user_dept_role_relation b where a.stop_sign = 'N' and a.del_sign = 'N' and b.role_id is null and b.user_id = a.id  " +
                    "and b.hosnum = ?) t1 where t.hosnum = t1.hosnum " +
                    "and t.deptcode = t1.deptid and t.isdeleted = 'N'  order by job_no  ) ";
            List<Map> list = db.find(sql, new Object[]{basHospital.getHosnum()});
            //System.out.println(list.get(0).get("num"));
            pw.print(list.get(0).get("num"));

            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            pw.print("fail");
        } finally {
            try {
                db.freeCon();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "/userData")
    public void userData(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            Bas_hospitals basHospital = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
            int curPage = Integer.parseInt(request.getParameter("curPage"));
            int pageSize = Integer.parseInt(request.getParameter("pageSize"));
            String sql = " select distinct t1.hosnum,t1.job_no,t1.name,t1.sex,t1.idcard,t1.phone,t1.mobile from bas_dept t, " +
                    "(select a.hosnum, a.id,a.job_no,a.name, a.sex,a.idcard,a.phone,a.mobile,b.office_id deptid from bas_user a, " +
                    "bas_user_dept_role_relation b where a.stop_sign = 'N' and a.del_sign = 'N' and b.role_id is null and b.user_id = a.id  " +
                    "and b.hosnum = ?) t1 where t.hosnum = t1.hosnum " +
                    "and t.deptcode = t1.deptid and t.isdeleted = 'N'  order by job_no   ";
            String pagingSql1 = "select OHYEAH.* from (select OHNO.*,rownum no from ("; // 用于分页// 段1
            String pagingSql2 = ") OHNO where rownum <= ?) OHYEAH where no > ?"; // 用于分页段2

            List<Map> list = db.find(pagingSql1 + sql + pagingSql2, new Object[]{basHospital.getHosnum(), curPage * pageSize, (curPage - 1) * pageSize});

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
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

}

