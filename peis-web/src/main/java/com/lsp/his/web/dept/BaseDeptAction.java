package com.lsp.his.web.dept;

import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lsp.his.db.DBOperator;
import com.lsp.his.model.ReturnValue;
import com.lsp.his.model.ZTreeNode;
import com.lsp.his.tables.*;
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



@Controller
@RequestMapping("/dept")
public class BaseDeptAction {

    /**
     * 载入下拉的地址数据
     *
     * @param request
     * @param response
     * @param modelMap
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/qhdm", method = RequestMethod.GET)
    public void loadQhdm(HttpServletRequest request,
                         HttpServletResponse response, ModelMap modelMap) throws Exception {
        String defaultDist = request.getParameter("defaultDist");// 默认的行政区划

        // 数据库查询
        DBOperator db = null;
        List<Bas_dicts> provinceBds = null;
        try {
            db = new DBOperator();
            String sql = "select t.nevalue,t.contents,t.inputcpy,t.inputcwb from bas_dicts t where t.hosnum = '0000' "
                    + "and t.nekey = 1 and t.nevalue != '!' and t.nevalue like ?";
            provinceBds = db.find(sql, new Object[]{defaultDist + "%"}, Bas_dicts.class);
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }

        // 组装数据
        StringBuilder sb = new StringBuilder("");
        sb.append("[");
        for (Bas_dicts provinceBd : provinceBds) {
            if (sb.length() != 1) {
                sb.append(",");
            }
            sb.append("{'nevalue':'");
            sb.append(provinceBd.getNevalue());
            sb.append("','contents':'");
            sb.append(provinceBd.getContents());
            sb.append("','inputcpy':'");
            sb.append(provinceBd.getInputcpy());
            sb.append("','inputcwb':'");
            sb.append(provinceBd.getInputcwb());
            sb.append("'}");
        }
        sb.append("]");
        // [{'nevalue':'310000','contents':'上海市','inputcpy':'SHS','inputcwb':'HIY'}]
        // 返回前台
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(sb.toString());
        pw.flush();
        pw.close();
    }

    /**
     * 载入下拉的该院区的所有科室
     *
     * @param request
     * @param response
     * @param modelMap
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/dept_all", method = RequestMethod.GET)
    public void loadDeptAll(HttpServletRequest request,
                            HttpServletResponse response, ModelMap modelMap) throws Exception {
        String hosnum = request.getParameter("hosnum");
        String hosNodecode = request.getParameter("nodecode");

        // 数据库查询
        DBOperator db = null;
        List deptAllList = null;
        try {
            db = new DBOperator();
            String sql = "select a.deptcode, a.deptname, a.inputcpy, a.inputcwb  from bas_dept a " +
                    "where a.hosnum=? and a.nodecode=? and a.isdeleted='N'";
            deptAllList = db.find(sql, new Object[]{hosnum, hosNodecode});
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }

        // 组装数据
        StringBuilder sb = new StringBuilder("");
        sb.append("[");
        if (ListUtil.listIsNotEmpty(deptAllList)) {
            for (int i = 0; i < deptAllList.size(); i++) {
                if (sb.length() != 1) {
                    sb.append(",");
                }
                Map<String, String> map = (Map<String, String>) deptAllList.get(i);
                sb.append("{'nevalue':'");
                sb.append(map.get("deptcode"));
                sb.append("','contents':'");
                sb.append(map.get("deptname"));
                sb.append("','inputcpy':'");
                sb.append(map.get("inputcpy"));
                sb.append("','inputcwb':'");
                sb.append(map.get("inputcwb"));
                sb.append("'}");
            }
            sb.append("]");
        }
        // [{'nevalue':'310000','contents':'上海市','inputcpy':'SHS','inputcwb':'HIY'}]
        // 返回前台
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(sb.toString());
        pw.flush();
        pw.close();
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
        String hosNodecode = request.getParameter("nodecode");
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
                        + "where t.isdeleted='N' and t.isleaf='N' and t.HOSNum=? and deptclass='科室' ";
                ksList = db.find(sql, new Object[]{hosnum});

                sql = "select deptcode,deptname,parentid,nodecode,isleaf from bas_dept t "
                        + "where t.isdeleted='N' and t.HOSNum=? and deptclass='病区' ";
                bqList = db.find(sql, new Object[]{hosnum});
            } else {
                sql = "select hosnum,hosname,nodecode,distcode from BAS_Hospitals a where a.hosnum=? and a.nodecode=?";
                hosList = db.find(sql, new Object[]{hosnum, hosNodecode});

                sql = "select deptcode,deptname,parentid,nodecode,isleaf from bas_dept t "
                        + "where t.isdeleted='N' and t.isleaf='N' and t.HOSNum=? and t.nodecode=? and deptclass='科室' ";
                ksList = db.find(sql, new Object[]{hosnum, hosNodecode});

//				sql = "select deptcode,deptname,parentid,nodecode,isleaf from bas_dept t "
//						+ "where t.HOSNum=? and t.nodecode=? and deptclass='病区' ";
//				bqList = db.find(sql, new Object[]{hosnum, hosNodecode});
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
                        String leaf = (String) bqMap.get("isleaf");
                        String mySkin = "ico_close";
                        if ("Y".equals(leaf)) {
                            mySkin = "bq_leaf";
                        }
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

    // *************************************************类别分割线********************************************************************
    // *************************************************执行科室操作********************************************************************

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

        return new ModelAndView("dept/dept_execute_select", model);
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
        // 查询数据
        DBOperator db = null;
        List hosList = null;
        List ksList = null;
//		List bqList = null;
        try {
            db = new DBOperator();
            String sql = "select hosnum,hosname,nodecode,distcode from BAS_Hospitals a where a.hosnum=?";
            hosList = db.find(sql, new Object[]{hosnum});
            sql = "select deptcode,deptname,parentid,nodecode,isleaf from bas_dept t "
                    + "where t.HOSNum=? and deptclass='科室' and isdeleted='N'";
            ksList = db.find(sql, new Object[]{hosnum});
//			sql = "select deptcode,deptname,parentid,nodecode,isleaf from bas_dept t "
//					+ "where t.HOSNum=? and deptclass='病区' ";
//			bqList = db.find(sql, new Object[]{hosnum});
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
                if (((String) tempMap.get("hosnum")).equals((String) tempMap.get("nodecode"))) {// 医院编码和节点编码相同
                    name = (String) tempMap.get("hosname");
//					nodecode = (String) tempMap.get("nodecode");
//					distcode = (String) tempMap.get("distcode");
                }
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

    // *************************************************类别分割线********************************************************************
    // *************************************************院区操作********************************************************************

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
        return new ModelAndView("dept/show", model);
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
        return new ModelAndView("dept/showGlobal", model);
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
                    "where t.nodetype = '医院'  connect by  prior t.nodecode= t.supunit  start with t.hosnum='0000'";
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
            zTreeNode.setClick("loadTree(true,'" + hospital.getHosnum() + "','" + hospital.getNodecode() + "')");
            sb.append(zTreeNode);
        }
        sb.append("]");

        return sb.toString();
    }

    /**
     * 转到院区新增页面
     *
     * @param request
     * @param response
     * @param modelMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/hospital_add")
    public ModelAndView hospitalAdd(HttpServletRequest request,
                                    HttpServletResponse response, ModelMap modelMap) throws Exception {
        // 新增需要传入默认编码；修改和查看需要传入ID
        String operationType = request.getParameter("operationType");// 操作类型：add（新增）,modify（修改）,view（查看）
        String operationId = request.getParameter("operationId");// 操作行的ID
        String defaultHos = request.getParameter("defaultHos");// 默认的医院编码
        String defaultDist = request.getParameter("defaultDist");// 默认的行政区划
        String orgType = StrUtil.strDecodeUTF8(request.getParameter("orgType"));//机构分类
        String save_num = StrUtil.strNullToDef(request.getParameter("save_num"), "0");//保存次数

        DBOperator db = null;
        List<Bas_hospitals> hospitals = null;
        List<Bas_dicts> distList = null;
        List<Bas_dicts> bds = null;
        try {
            db = new DBOperator();
            String sql = "select * from bas_dicts t where t.hosnum = '0000' and t.nekey in(24,25) and t.nevalue != '!' order by t.nevalue asc";
            bds = db.find(sql, Bas_dicts.class);

            sql = "select t.nevalue,t.contents,t.inputcpy,t.inputcwb from bas_dicts t where t.hosnum = '0000' "
                    + "and t.nekey = 1 and t.nevalue != '!' and t.nevalue like ?";
            distList = db.find(sql, new Object[]{defaultDist + "%"}, Bas_dicts.class);// 行政区划

            if ("modify".equals(operationType) || "view".equals(operationType)) {// 修改；查看
                sql = "select * from bas_hospitals t where hosnum=? and nodecode=?";
                String[] codes = operationId.split(";");
                if (codes != null && codes.length == 2) {
                    hospitals = db.find(sql,
                            new Object[]{codes[0], codes[1]},
                            Bas_hospitals.class);
                }
            }
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }

        List<Bas_dicts> degreeList = new ArrayList<Bas_dicts>();// 医院等级
        List<Bas_dicts> levelList = new ArrayList<Bas_dicts>();// 等级级别
        for (Bas_dicts bd : bds) {
            if (bd.getNekey() == 24) {
                degreeList.add(bd);
            } else if (bd.getNekey() == 25) {
                levelList.add(bd);
            }
        }
        modelMap.put("degreeList", degreeList);
        modelMap.put("levelList", levelList);

        modelMap.put("distList", distList);
        modelMap.put("defaultDist", defaultDist);

        modelMap.put("operationType", operationType);
        modelMap.put("save_num", save_num);
        if ("add".equals(operationType)) {// 新增
            Bas_hospitals hos = new Bas_hospitals();
            hos.setHosnum(defaultHos);
            hos.setDistcode(defaultDist);
            //hos.setSupunit(defaultHos);
            hos.setOrgtype(orgType);
            modelMap.put("hos", hos);
        } else {// 修改；查看
            Bas_hospitals hos = (Bas_hospitals) ListUtil
                    .distillFirstRow(hospitals);
            modelMap.put("hos", hos);
        }

        return new ModelAndView("dept/hospital_add", modelMap);
    }

    /**
     * 新增或者修改医院表
     *
     * @param request
     * @param response
     * @param hospital
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/hospital_added", method = RequestMethod.POST)
    public void hospitalAdded(HttpServletRequest request,
                              HttpServletResponse response, Bas_hospitals hospital)
            throws Exception {
        String operationType = request.getParameter("operationType");// 操作类型：add（新增）,modify（修改）
        String old_nodecode = "";
        if ("modify".equals(operationType)) {
            old_nodecode = request.getParameter("old_nodecode");
        }

        hospital.setInputcwb(WordUtil.trans2WbCode(hospital.getHosname()));
        hospital.setInputcpy(WordUtil.trans2PyCode(hospital.getHosname()));

        DBOperator db = null;
        try {
            db = new DBOperator();
            if ("add".equals(operationType)) {
                // 更新从医院继承的属性
                String qry = "select hosnum,nodecode,hosdegree,hosdname,degreelevel,degreelname,orgtype,supunit from BAS_Hospitals a "
                        + "where a.hosnum=a.nodecode and a.hosnum=?";
                List extendList = db.find(qry, new Object[]{hospital.getHosnum()});
                Map map = (Map) ListUtil.distillFirstRow(extendList);
                //hospital.setHosdegree((String) map.get("hosdegree"));
                //hospital.setHosdname((String) map.get("hosdname"));
                hospital.setDegreelevel((String) map.get("degreelevel"));
                hospital.setDegreelname((String) map.get("degreelname"));
                String sonhosnum = (String) map.get("hosnum");
                String sonnodecode = (String) hospital.getNodecode();
                if (sonhosnum == sonnodecode) {
                    hospital.setHosdegree((String) map.get("hosdegree"));
                    hospital.setHosdname((String) map.get("hosdname"));
                    hospital.setSupunit((String) map.get("supunit"));
                } else {
                    //保存页面没有相关填写组件，故医院等级暂时写死先
                    hospital.setHosdegree("3");
                    hospital.setHosdname("三级");
                    hospital.setSupunit((String) map.get("hosnum"));
                }


                //hospital.setOrgtype((String) map.get("orgtype"));

                db.excute("insert into bas_hospitals(HOSDNAME,DEGREELNAME,ISENABLED,HOSNUM,NODECODE,HOSNAME,DISTCODE,SUPUNIT,NODETYPE,HOSDEGREE,ORGTYPE,EMPNUMBER,BEDS,DOCTORS,NURSES,ADDRESS,TEL,INTRODUCTION,INPUTCPY,INPUTCWB,DEGREELEVEL,SHORTNAME) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                        new Object[]{hospital.getHosdname(),
                                hospital.getDegreelname(),
                                hospital.getIsenabled(),
                                hospital.getHosnum(),
                                hospital.getNodecode(),
                                hospital.getHosname(),
                                hospital.getDistcode(),
                                hospital.getSupunit(),
                                hospital.getNodetype(),
                                hospital.getHosdegree(),
                                hospital.getOrgtype(),
                                hospital.getEmpnumber(),
                                hospital.getBeds(),
                                hospital.getDoctors(),
                                hospital.getNurses(),
                                hospital.getAddress(),
                                hospital.getTel(),
                                hospital.getIntroduction(),
                                hospital.getInputcpy(),
                                hospital.getInputcwb(),
                                hospital.getDegreelevel(), hospital.getShortname()});
            } else if ("modify".equals(operationType)) {
                db.excute("update bas_hospitals set ISENABLED=?,HOSNUM=?,NODECODE=?,HOSNAME=?,DISTCODE=?," +
                                "NODETYPE=?,ORGTYPE=?,EMPNUMBER=?,BEDS=?,DOCTORS=?,NURSES=?,ADDRESS=?,TEL=?," +
                                "INTRODUCTION=?,INPUTCPY=?,INPUTCWB=?, SHORTNAME=? where hosnum=? and nodecode=?",
                        new Object[]{
                                hospital.getIsenabled(),
                                hospital.getHosnum(),
                                hospital.getNodecode(),
                                hospital.getHosname(),
                                hospital.getDistcode(),
                                //不更新supunit,
                                hospital.getNodetype(),
                                hospital.getOrgtype(),
                                hospital.getEmpnumber(),
                                hospital.getBeds(),
                                hospital.getDoctors(),
                                hospital.getNurses(),
                                hospital.getAddress(),
                                hospital.getTel(),
                                hospital.getIntroduction(),
                                hospital.getInputcpy(),
                                hospital.getInputcwb(),
                                hospital.getShortname(),
                                hospital.getHosnum(),
                                old_nodecode});
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
     * 院区信息是否重复验证
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/hospital_add_check", method = RequestMethod.POST)
    public void hospitalAddCheck(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        String hosnum = request.getParameter("hosnum");
        String nodecode = request.getParameter("nodecode");
        String hosname = request.getParameter("hosname");

        List hosList = null;
        DBOperator db = null;
        try {
            db = new DBOperator();
            if (StrUtil.strIsNotEmpty(nodecode) && StrUtil.strIsNotEmpty(hosname)) {
                String sql = "select nodecode,hosname from bas_hospitals where hosnum=? and (nodecode=? or hosname=?)";
                hosList = db.find(sql, new Object[]{hosnum, nodecode, hosname});
            } else if (StrUtil.strIsNotEmpty(nodecode)) {
                String sql = "select nodecode,hosname from bas_hospitals where hosnum=? and nodecode=?";
                hosList = db.find(sql, new Object[]{hosnum, nodecode});
            } else if (StrUtil.strIsNotEmpty(hosname)) {
                String sql = "select nodecode,hosname from bas_hospitals where hosnum=? and hosname=?";
                hosList = db.find(sql, new Object[]{hosnum, hosname});
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
        if (ListUtil.listIsNotEmpty(hosList)) {
            String reVal = "";
            int codeNum = 0;
            int nameNum = 0;
            for (int i = 0; i < hosList.size(); i++) {
                Map map = (Map) hosList.get(i);
                if (StrUtil.strIsNotEmpty(nodecode) && nodecode.equals((String) map.get("nodecode"))) {
                    codeNum++;
                }
                if (StrUtil.strIsNotEmpty(hosname) && hosname.equals((String) map.get("hosname"))) {
                    nameNum++;
                }
            }
            if (codeNum >= 1 && nameNum >= 1) {
                reVal = "double";
            } else if (codeNum >= 1) {
                reVal = "code";
            } else if (nameNum >= 1) {
                reVal = "name";
            } else {
                reVal = "Y";
            }
            pw.print(reVal);
        } else {
            pw.print("N");
        }
    }

    /**
     * 删除医院表
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/hospital_remove", method = RequestMethod.GET)
    public void hospitalDelete(HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
        String checkIds = StrUtil.strNullToEmpty(request
                .getParameter("checkIds"));
        String[] Ids = checkIds.split(",");

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            for (int i = 0; i < Ids.length; i++) {
                String[] id = Ids[i].split(";");
                if (id.length == 2) {
                    String qry = "select rowid from bas_dept where hosnum=? and nodecode=? and hosnum!=nodecode and rownum=1";//取一行数据
                    List deptList = db.find(qry, new Object[]{id[0], id[1]});
                    if (ListUtil.listIsNotEmpty(deptList)) {
                        db.rollback();
                        pw.print(Ids[i]);
                        return;
                    }
                    String sql = "delete from bas_hospitals where hosnum=? and nodecode=? and hosnum!=nodecode";
                    db.excute(sql, new Object[]{id[0], id[1]});
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

    /**
     * ajax方式加载医院表格显示数据
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/hospital_list_load", method = RequestMethod.GET)
    public void loadHospitalTable(HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
        String hosnum = request.getParameter("hosnum");
        String orgType = StrUtil.strDecodeUTF8(request.getParameter("orgType"));

        // 数据库查询
        DBOperator db = null;
        List hospitals = null;
        try {
            db = new DBOperator();
//			String sql = " select hosnum,nodecode,decode(hosnum,nodecode,'本部',hosname) hosname,hosdname,degreelname" +
//					" from BAS_Hospitals a where a.hosnum=? and exists (select * from bas_hospitals b " +
//					"where b.nodecode <> a.hosnum and b.hosnum = a.hosnum)";
            String sql = "select hosnum,nodecode,hosname,hosdname,degreelname " +
                    "from BAS_Hospitals a where a.hosnum=? and a.orgType=?";
            hospitals = db.find(sql, new Object[]{hosnum, orgType});
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }

        // 数据填充到模版
        response.setContentType("text/xml;charset=utf-8");
        String vmpagckage = "com/cpinfo/his/template/dept/";
        String vmname = "hospitals.vm";
        PrintWriter pw = null;
        pw = response.getWriter();
        String vm = VelocityUtils.generateGridVm(vmpagckage, vmname,
                "hospitals", hospitals);
        pw.print(vm);
        pw.flush();
        pw.close();
    }

    /**
     * ajax方式加载医院表格显示数据
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/hospital_list")
    public ModelAndView hospitalList(HttpServletRequest request,
                                     HttpServletResponse response) throws Exception {
        String hosnum = request.getParameter("hosnum");
        String distcode = request.getParameter("distcode");
        String orgType = StrUtil.strDecodeUTF8(request.getParameter("orgType"));

        ModelMap model = new ModelMap();
        model.put("hosnum", hosnum);
        model.put("distcode", distcode);
        model.put("orgType", orgType);
        return new ModelAndView("dept/hospital_list", model);
    }

    // *************************************************类别分割线********************************************************************
    // *************************************************科室操作********************************************************************

    /**
     * 显示院区列表
     *
     * @return
     */
    @RequestMapping("/dept_show")
    public ModelAndView deptShow() {
        ModelMap model = new ModelMap();
        model.put("hosnum", "1005");
        return new ModelAndView("dept/dept_show", model);
    }

    /**
     * 转到科室新增页面
     *
     * @param request
     * @param response
     * @param modelMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/dept_add")
    public ModelAndView deptAdd(HttpServletRequest request,
                                HttpServletResponse response, ModelMap modelMap) throws Exception {
        // 新增需要传入默认编码；修改和查看需要传入ID
        String operationType = request.getParameter("operationType");// 操作类型：add（新增）,modify（修改）,view（查看）
        String operationId = request.getParameter("operationId");// 操作行的ID
        String defaultHos = request.getParameter("defaultHos");// 默认的医院编码
        String defaultNode = request.getParameter("defaultNode");// 默认的院区编码
        String defaultDept = request.getParameter("defaultDept");// 默认父类科室
        String defaultClass = StrUtil.strDecodeUTF8(request.getParameter("defaultClass"));// 默认科室大类
        String yqName = StrUtil.strDecodeUTF8(request.getParameter("yqName"));// 默认的院区名称

        String save_num = StrUtil.strNullToDef(request.getParameter("save_num"), "0");//保存次数
        String save_tree_num = StrUtil.strNullToDef(request.getParameter("save_tree_num"), "0");//保存次数

        DBOperator db = null;
        List<Bas_dept> depts = null;// 科室信息
        List scopeList = null;// 服务范围
        List<Bas_dicts> bds = null;
        try {
            db = new DBOperator();
            String sql = "select * from bas_dicts t where t.hosnum = '0000' and t.nekey=13 and t.nevalue != '!' order by t.nevalue asc";
            bds = db.find(sql, Bas_dicts.class);

            if ("modify".equals(operationType) || "view".equals(operationType)) {// 修改；查看
                sql = "select * from bas_dept t where hosnum=? and deptcode=? and isdeleted='N'";
                String[] codes = operationId.split(";");
                if (codes != null && codes.length == 2) {
                    depts = db.find(sql, new Object[]{codes[0], codes[1]},
                            Bas_dept.class);
                }
                sql = "select t.deptcode,t.deptname,t.isleaf from bas_dept t, bas_dept_scope t1 "
                        + "where t1.hosnum = ? and t1.deptcode = ? and t.deptcode = t1.targetcode and t.hosnum=t1.hosnum and t.isdeleted='N'";// 查询服务范围
                scopeList = db.find(sql, new Object[]{codes[0], codes[1]});
            }
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }

        List<Bas_dicts> depttypeList = new ArrayList<Bas_dicts>();// 科室类别
        for (Bas_dicts bd : bds) {
            if (bd.getNekey() == 13) {
                depttypeList.add(bd);
            }
        }
        modelMap.put("depttypeList", depttypeList);

        modelMap.put("yqName", yqName);
        modelMap.put("operationType", operationType);
        modelMap.put("save_num", save_num);
        modelMap.put("save_tree_num", save_tree_num);
        if ("add".equals(operationType)) {// 新增
            Bas_dept dept = new Bas_dept();
            dept.setHosnum(defaultHos);
            dept.setNodecode(defaultNode);
            dept.setParentid(defaultDept);
            dept.setDeptclass(defaultClass);
            modelMap.put("dept", dept);
        } else {// 修改；查看
            if (ListUtil.listIsNotEmpty(scopeList)) {
                StringBuffer checkedCodes = new StringBuffer();
                StringBuffer checkedNames = new StringBuffer();
                for (Iterator<Map> iterator = scopeList.iterator(); iterator
                        .hasNext(); ) {
                    Map scopeMap = iterator.next();
                    checkedCodes.append(scopeMap.get("deptcode") + ";");
                    if ("Y".equals(scopeMap.get("isleaf"))) {// 只显示最后一级的科室
                        checkedNames.append(scopeMap.get("deptname") + "；");
                    }
                }
                modelMap.put("checkedCodes", checkedCodes.toString());
                modelMap.put("checkedNames", checkedNames.toString());
            }
            Bas_dept dept = (Bas_dept) ListUtil.distillFirstRow(depts);
            modelMap.put("dept", dept);
        }

        return new ModelAndView("dept/dept_add", modelMap);
    }

    /**
     * 新增或者修改科室信息
     *
     * @param request
     * @param response
     * @param dept
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/dept_added", method = RequestMethod.POST)
    public void deptAdded(HttpServletRequest request,
                          HttpServletResponse response, Bas_dept dept) throws Exception {
        String operationType = request.getParameter("operationType");// 操作类型：add（新增）,modify（修改）
        String old_depttype = StrUtil.strNullToEmpty(request.getParameter("old_depttype"));//旧的科室类别
        String checkedCodes = StrUtil.strNullToEmpty(request.getParameter("checkedCodes"));//科室服务范围

        dept.setInputcwb(WordUtil.trans2WbCode(dept.getDeptname()));
        dept.setInputcpy(WordUtil.trans2PyCode(dept.getDeptname()));

        DBOperator db = null;
        try {
            db = new DBOperator();
            if ("add".equals(operationType)) {
                String parentCode = dept.getParentid();
                String deptCode = "";
//				if (StrUtil.strIsNotEmpty(parentCode)) {// 存在父类科室
//					String qry = " select max(deptcode) maxcode from BAS_DEPT a where a.hosnum=? "
//							+ "and a.deptclass=? and a.parentid=?";
//					// and a.nodecode='"+dept.getNodecode()+"' //排除院区
//					List codeList = db.find(qry, new Object[]{dept.getHosnum(), dept.getDeptclass(), parentCode});
//					if (ListUtil.listIsNotEmpty(codeList) && ((Map)codeList.get(0)).get("maxcode") != null) {
//						deptCode = (String) ((Map) codeList.get(0))
//								.get("maxcode");
//						if (deptCode.length() >= 6) {
//							int num = Integer.parseInt(deptCode
//									.substring(deptCode.length() - 2)) + 1;
//							if (num < 10) {
//								deptCode = parentCode + "0"
//										+ String.valueOf(num);
//							} else {
//								deptCode = parentCode + String.valueOf(num);
//							}
//
//						}
//					} else {
//						deptCode = parentCode + "01";// 初始化科室代码
//					}
//				} else {// 不存在父类科室
//					String qry = " select max(deptcode) maxcode from BAS_DEPT a where a.hosnum=? "
//							+ "and a.deptclass=?  and a.parentid is null";
//					List codeList = db.find(qry, new Object[]{dept.getHosnum(), dept.getDeptclass()});
//					if (ListUtil.listIsNotEmpty(codeList) && ((Map)codeList.get(0)).get("maxcode") != null) {// 存在同级别科室
//						deptCode = (String) ((Map) codeList.get(0))
//								.get("maxcode");
//						int num = Integer.parseInt(deptCode) + 1;// 科室编码转整数
//						deptCode = String.valueOf(num);
//
//					} else {
//						if ("科室".equals(dept.getDeptclass())) {
//							deptCode = "1001";// 初始化科室编码
//						} else if ("病区".equals(dept.getDeptclass())) {
//							deptCode = "2001";// 初始化病区编码
//						}
//					}
//				}
                String qry = " select max(deptcode) maxcode from BAS_DEPT";
                Map map = (Map) db.findOne(qry);
                int num = Integer.parseInt((String) map.get("maxcode")) + 1;
                deptCode = String.valueOf(num);
                dept.setDeptcode(deptCode);
                dept.setIsdeleted("N");// 没有作废
                db.excute("insert into bas_dept(HOSNUM,NODECODE,DEPTCODE,DEPTNAME,SHORTNAME,PARENTID,ISLEAF,DEPTCLASS,CLINICALTYPE," +
                                "ISACCDEPT,DEPTTYPE,CLCFLAG,EMCFLAG,INPFLAG,MATFLAG,HERBSFLAG,CNMFLAG,WMFLAG,PREPAY,LOCATION,ISDELETED," +
                                "INPUTCPY,INPUTCWB,MATERIALFLAG) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                        new Object[]{dept.getHosnum(),
                                dept.getNodecode(), dept.getDeptcode(),
                                dept.getDeptname(),
                                dept.getShortname(),
                                dept.getParentid(), dept.getIsleaf(),
                                dept.getDeptclass(),
                                dept.getClinicaltype(),
                                dept.getIsaccdept(),
                                dept.getDepttype(), dept.getClcflag(),
                                dept.getEmcflag(), dept.getInpflag(),
                                dept.getMatflag(), dept.getHerbsflag(),
                                dept.getCnmflag(), dept.getWmflag(),
                                dept.getPrepay(), dept.getLocation(),
                                dept.getIsdeleted(),
                                dept.getInputcpy(), dept.getInputcwb(),
                                dept.getMaterialflag()});
            } else if ("modify".equals(operationType)) {
                db.excute("update bas_dept set HOSNUM=?,NODECODE=?,DEPTCODE=?,DEPTNAME=?,SHORTNAME=?,PARENTID=?,ISLEAF=?," +
                                "DEPTCLASS=?,CLINICALTYPE=?,ISACCDEPT=?,DEPTTYPE=?,CLCFLAG=?,EMCFLAG=?,INPFLAG=?,MATFLAG=?,HERBSFLAG=?," +
                                "CNMFLAG=?,WMFLAG=?,PREPAY=?,LOCATION=?,INPUTCPY=?,INPUTCWB=?,MATERIALFLAG=? where hosnum=? and deptcode=?",
                        new Object[]{dept.getHosnum(),
                                dept.getNodecode(), dept.getDeptcode(),
                                dept.getDeptname(),
                                dept.getShortname(),
                                dept.getParentid(), dept.getIsleaf(),
                                dept.getDeptclass(),
                                dept.getClinicaltype(),
                                dept.getIsaccdept(),
                                dept.getDepttype(), dept.getClcflag(),
                                dept.getEmcflag(), dept.getInpflag(),
                                dept.getMatflag(), dept.getHerbsflag(),
                                dept.getCnmflag(), dept.getWmflag(),
                                dept.getPrepay(), dept.getLocation(),
                                dept.getInputcpy(), dept.getInputcwb(),
                                dept.getMaterialflag(),
                                dept.getHosnum(), dept.getDeptcode()});
                if (!"".equals(checkedCodes) && !old_depttype.equals(dept.getDepttype())) {//有服务范围，且科室类别发生变化，修改服务范围表的服务类别
                    db.excute("update bas_dept_scope set scopetype=? where hosnum=? and deptcode=?",
                            new Object[]{dept.getDepttype(), dept.getHosnum(), dept.getDeptcode()});
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
            pw.print(dept.getDeptcode());
        } catch (Exception e) {
            e.printStackTrace();
            pw.print("fail");
        }
    }

    /**
     * 是否有孩子科室验证
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/dept_child_check", method = RequestMethod.POST)
    public void deptDeptChildCheck(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String idstr = request.getParameter("idstr");
        String isleaf = request.getParameter("isleaf");
        String deptclass = request.getParameter("deptclass");
        String userFlag = StrUtil.strNullToEmpty(request.getParameter("userFlag"));

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            String[] id = idstr.split(";");
            if (id.length == 2 && StrUtil.strIsNotEmpty(deptclass)) {
                if ("N".equals(isleaf)) {//原来不是末级才判断
                    String sql = "select rowid num from bas_dept where hosnum=? and parentid=? and rownum=1 and isdeleted='N'";
                    List numList = db.find(sql, new Object[]{id[0], id[1]});
                    if (ListUtil.listIsNotEmpty(numList)) {
                        pw.print(idstr);
                        return;
                    }
                } else if ("Y".equals(isleaf)) {//是末级，且是病区
                    if ("病区".equals(deptclass)) {//病区的时候，判断是否有床位记录
                        String qry = "select rowid num from bas_beds where hosnum=? and wardno=? and rownum=1";
                        List bedList = db.find(qry, new Object[]{id[0], id[1]});
                        if (ListUtil.listIsNotEmpty(bedList)) {
                            pw.print("病区_" + idstr);
                            return;
                        }
                    }
                }
                if ("Y".equals(userFlag)) {//验证科室下面是否存在用户
                    List relationList = db.find("select rowid num from bas_user_dept_role_relation where (hosnum=? and (office_id = ? or shayne_id = ?))" +
                                    " or user_id in(select id from bas_user where hosnum=? and person_dept=?) and rownum=1",
                            new Object[]{id[0], id[1], id[1], id[0], id[1]});//查询科室下面的用户的关系
                    if (ListUtil.listIsNotEmpty(relationList)) {
                        pw.print("用户_relation_" + idstr);
                        return;
                    }
                    List personList = db.find("select rowid num from bas_user where hosnum=? and person_dept=? and del_sign='N' and rownum=1",
                            new Object[]{id[0], id[1]});//查询科室下面的用户
                    if (ListUtil.listIsNotEmpty(personList)) {
                        pw.print("用户_person_" + idstr);
                        return;
                    }
                }
            }
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }
        pw.print("N");
    }

    /**
     * 科室名称是否重复验证
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/dept_add_check", method = RequestMethod.POST)
    public void deptDeptAddCheck(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        String hosnum = request.getParameter("hosnum");
        String nodecode = request.getParameter("nodecode");
        String deptname = request.getParameter("deptname");

        DBOperator db = null;
        List bedList = null;
        try {
            db = new DBOperator();
            String sql = "select rowid from bas_dept where hosnum=? and nodecode=? and deptname=? and isdeleted='N'";
            bedList = db.find(sql, new Object[]{hosnum, nodecode, deptname});
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = null;
        pw = response.getWriter();
        if (ListUtil.listIsNotEmpty(bedList)) {
            pw.print("Y");
        } else {
            pw.print("N");
        }
    }

    /**
     * 删除科室信息
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/dept_remove", method = RequestMethod.POST)
    public void deptDelete(HttpServletRequest request,
                           HttpServletResponse response) throws Exception {
        String checkIds = StrUtil.strNullToEmpty(request.getParameter("checkIds"));
        String[] Ids = checkIds.split(",");

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = null;
        DBOperator db = null;
        try {
            pw = response.getWriter();
            db = new DBOperator();
            for (int i = 0; i < Ids.length; i++) {
                String[] id = Ids[i].split(";");
                if (id.length == 2) {
                    String scopeSql = "delete from bas_dept_scope where hosnum=? and deptcode=?";//删除服务范围
                    db.excute(scopeSql, new Object[]{id[0], id[1]});
                    String sql = "delete from bas_dept where hosnum=? and deptcode=?";
                    db.excute(sql, new Object[]{id[0], id[1]});
                }
            }
            db.commit();
            pw.print("success");
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            pw.print("fail");
        } finally {
            db.freeCon();
        }
    }

    /**
     * ajax方式加载科室表格显示数据
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/dept_list_load", method = RequestMethod.GET)
    public void loadDeptListTable(HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
        String hosnum = request.getParameter("hosnum");
        String nodecode = request.getParameter("nodecode");
        String deptcode = request.getParameter("deptcode");
        String deptclass = StrUtil.strDecodeUTF8(request
                .getParameter("deptclass"));

        // 数据库查询
        List deptList = new ArrayList();
        if (StrUtil.strIsNotEmpty(hosnum) && StrUtil.strIsNotEmpty(nodecode)) {
            DBOperator db = null;
            try {
                db = new DBOperator();
                String sql = "";
                if (StrUtil.strIsNotEmpty(deptcode)) {
                    sql = " select * from BAS_DEPT a where a.hosnum=? and a.nodecode=? and a.deptclass=? and a.isdeleted='N' and a.parentid=? order by deptcode";
                    deptList = db.find(sql, new Object[]{hosnum, nodecode, deptclass, deptcode});
                } else {
                    sql = " select * from BAS_DEPT a where a.hosnum=? and a.nodecode=? and a.deptclass=? and a.isdeleted='N' and a.parentid is null order by deptcode";
                    deptList = db.find(sql, new Object[]{hosnum, nodecode, deptclass});
                }

            } catch (Exception e) {
                db.rollback();
                e.printStackTrace();
            } finally {
                db.freeCon();
            }
        }

        // 数据填充到模版
        // if(ListUtil.listIsNotEmpty(scopeList)){
        response.setContentType("text/xml;charset=utf-8");
        String vmpagckage = "com/lsp/his/template/dept/";
        String vmname = "dept_list.vm";
        PrintWriter pw = null;
        pw = response.getWriter();
        String vm = VelocityUtils.generateGridVm(vmpagckage, vmname, "depts",
                deptList);
        pw.print(vm);
        pw.flush();
        pw.close();
        // }
    }

    /**
     * ajax方式加载科室表格显示数据
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/dept_list")
    public ModelAndView deptList(HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        String hosnum = request.getParameter("hosnum");
        String nodecode = request.getParameter("nodecode");
        String deptcode = request.getParameter("deptcode");
        String deptclass = StrUtil.strDecodeUTF8(request.getParameter("deptclass"));
        String yqName = StrUtil.strDecodeUTF8(request.getParameter("yqName"));

        ModelMap model = new ModelMap();
        model.put("hosnum", hosnum);
        model.put("nodecode", nodecode);
        model.put("deptcode", deptcode);
        model.put("deptclass", deptclass);
        model.put("yqName", yqName);
        return new ModelAndView("dept/dept_list", model);
    }

    // *************************************************类别分割线********************************************************************
    // *************************************************科室服务范围操作********************************************************************

    /**
     * ajax方式加载科室服务范围树
     *
     * @param request
     * @param response
     * @param modelMap
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/dept_scope_tree", method = RequestMethod.GET)
    public void loadDeptScopeTree(HttpServletRequest request,
                                  HttpServletResponse response, ModelMap modelMap) throws Exception {
        String hosnum = request.getParameter("hosnum");// 医院编码
        String yqCode = request.getParameter("yqCode");// 院区编码
        String yqName = StrUtil.strDecodeUTF8(request.getParameter("yqName"));// 院区名称
        String checkedScope = request.getParameter("checkedScope");// 选中的范围

        String[] checkedList = null;
        if (StrUtil.strIsNotEmpty(checkedScope)) {
            checkedList = checkedScope.split(";");
        }

        // 组装的树数据格式
        List<String> lstTree = new ArrayList<String>();
        if (StrUtil.strIsNotEmpty(yqCode)) {
            // 查询数据
            DBOperator db = null;
            List scopeList = null;
            try {
                db = new DBOperator();
                String sql = "select deptcode,deptname,parentid,isleaf from bas_dept t "
                        + "where t.HOSNum=? and t.nodecode=? and t.isdeleted='N'";
                scopeList = db.find(sql, new Object[]{hosnum, yqCode});
            } catch (Exception e) {
                db.rollback();
                e.printStackTrace();
            } finally {
                db.freeCon();
            }

            String s1 = "{id:\"a" + yqCode + "\", pId:\"0\", name:\"" + yqName + "\" , open:true}";// 根节点
            lstTree.add(s1);
            if (ListUtil.listIsNotEmpty(scopeList)) {// 存在服务范围科室
                for (Iterator scopeIterator = scopeList.iterator(); scopeIterator
                        .hasNext(); ) {
                    Map scopeMap = (Map) scopeIterator.next();
                    String temp = "{id:\"" + scopeMap.get("deptcode") + "\"," +
                            "pId:\"" + StrUtil.strNullToDef((String) scopeMap.get("parentid"), "a" + yqCode) + "\"," +
                            "name:\"" + scopeMap.get("deptname") + "\"," +
                            "isLast:\"" + scopeMap.get("isleaf") + "\"," +
                            "checked:" + StrUtil.strIsInArrary((String) scopeMap.get("deptcode"), checkedList) + "}";// 子节点
                    lstTree.add(temp);
                }
            }
        } else {
            lstTree.add("没有找到对应院区");
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
     * 转到科室服务范围新增页面
     *
     * @param request
     * @param response
     * @param modelMap
     * @return
     * @throws Exception
     */
    @RequestMapping("/dept_scope_add")
    public ModelAndView deptScopeAdd(HttpServletRequest request,
                                     HttpServletResponse response, ModelMap modelMap) throws Exception {
        // 新增需要传入默认编码；修改和查看需要传入ID
        // String operationType =
        // request.getParameter("operationType");//操作类型：add（新增）,modify（修改）,view（查看）
        // String operationId = request.getParameter("operationId");//操作行的ID
        String defaultHos = request.getParameter("defaultHos");// 默认的医院编码
        String defaultNode = request.getParameter("defaultNode");// 默认的院区编码
        String defaultDept = request.getParameter("defaultDept");// 默认的科室编码
        String defaultType = StrUtil.strDecodeUTF8(request
                .getParameter("defaultType"));// 默认的科室类别
        String yqName = StrUtil.strDecodeUTF8(request.getParameter("yqName"));// 院区名称
        String checkedScope = request.getParameter("checkedScope");// 已经选择的服务范围

        // DBOperator db = new DBOperator();
        // String sql = "select deptcode,deptname from bas_dept t where
        // t.isleaf='Y' and t.nodecode='"+defaultNode+"'";//查询院区下所有的科室
        // @SuppressWarnings("unchecked")
        // List<Bas_dept> deptList = db.find(sql, Bas_dept.class);

        // List<Bas_dept_scope> scopeList = null;
        // if("modify".equals(operationType) ||
        // "view".equals(operationType)){//修改；查看
        // sql = "select * from bas_dept t where hosnum=? and deptcode=? and
        // scopetype=? and targetcode=?";
        // String[] codes = operationId.split(";");
        // if(codes != null && codes.length == 4){
        // scopeList = db.find(sql, new Object[]{codes[0], codes[1], codes[2],
        // codes[3]}, Bas_dept_scope.class);
        // }
        // }
        // } catch (Exception e) {db.rollback();} finally{db.freeCon();}

        // modelMap.put("deptList", deptList);

        modelMap.put("yqName", yqName);
        modelMap.put("yqCode", defaultNode);
        modelMap.put("checkedScope", checkedScope);
        // modelMap.put("operationType", operationType);
        // if("add".equals(operationType)){//新增
        Bas_dept_scope deptScope = new Bas_dept_scope();
        deptScope.setHosnum(defaultHos);
        deptScope.setDeptcode(defaultDept);
        deptScope.setScopetype(defaultType);
        //System.out.println("defaultType="+defaultType);
        modelMap.put("scope", deptScope);
        // }else{//修改；查看
        // Bas_dept_scope deptScope =
        // (Bas_dept_scope)ListUtil.distillFirstRow(scopeList);
        // modelMap.put("scope", deptScope);
        // }

        return new ModelAndView("dept/dept_scope_add", modelMap);
    }

    /**
     * 新增或者修改科室服务范围信息
     *
     * @param request
     * @param response
     * @param scope
     * @throws Exception
     */
    @RequestMapping(value = "/dept_scope_added", method = RequestMethod.POST)
    public void deptScopeAdded(HttpServletRequest request,
                               HttpServletResponse response, Bas_dept_scope scope)
            throws Exception {
        // String operationType =
        // request.getParameter("operationType");//操作类型：add（新增）,modify（修改）
        String addscopes = request.getParameter("addscopes");// 新增的范围
        String removescopes = request.getParameter("removescopes");// 删除的范围
        String scopNames = request.getParameter("scopNames");
        DBOperator db = null;
        try {
            db = new DBOperator();

            if (StrUtil.strIsNotEmpty(removescopes)) {// 批量删除范围
                String[] removeList = removescopes.split(";");
                for (int i = 0; i < removeList.length; i++) {
                    scope.setTargetcode(removeList[i]);
                    String sql = "delete from bas_dept_scope where hosnum=? and deptcode=? and targetcode=?";
                    db.excute(sql, new Object[]{scope.getHosnum(),
                            scope.getDeptcode(),
                            scope.getTargetcode()});
                }
            }
//			String[] snames=scopNames.split("；");
            if (StrUtil.strIsNotEmpty(addscopes)) {// 批量新增范围
                String[] addList = addscopes.split(";");
                for (int i = 0; i < addList.length; i++) {
                    scope.setTargetcode(addList[i]);
                    db.excute("insert into bas_dept_scope(HOSNUM,SCOPETYPE,DEPTCODE,TARGETCODE,COMMENTS) values(?,?,?,?,?)",
                            new Object[]{scope.getHosnum(),
                                    //scope.getScopetype(),
                                    scopNames,//snames[i],
                                    scope.getDeptcode(),
                                    scope.getTargetcode(),
                                    scope.getComments()});
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
     * 删除科室服务范围信息
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/dept_scope_remove", method = RequestMethod.GET)
    public void deptScopeDelete(HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        String checkIds = StrUtil.strNullToEmpty(request
                .getParameter("checkIds"));
        String[] Ids = checkIds.split(",");

        DBOperator db = null;
        try {
            db = new DBOperator();
            for (int i = 0; i < Ids.length; i++) {
                String[] id = Ids[i].split(";");
                if (id.length == 4) {
                    String sql = "delete from bas_dept_scope where hosnum=? and deptcode=? and scopetype=? and targetcode=?";
                    db.excute(sql, new Object[]{id[0], id[1], id[2], id[3]});
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
     * ajax方式加载科室服务范围表格显示数据
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/dept_scope_list", method = RequestMethod.GET)
    public void loadDeptScopeTable(HttpServletRequest request,
                                   HttpServletResponse response) throws Exception {
        String hosnum = request.getParameter("hosnum");
        String deptcode = request.getParameter("deptcode");

        // 数据库查询
        List scopeList = new ArrayList();
        if (StrUtil.strIsNotEmpty(hosnum) && StrUtil.strIsNotEmpty(deptcode)) {
            DBOperator db = null;
            try {
                db = new DBOperator();
                String sql = " select * from BAS_DEPT_Scope a where a.hosnum=? and a.deptcode=?";
                scopeList = db.find(sql, new Object[]{hosnum, deptcode});
            } catch (Exception e) {
                db.rollback();
                e.printStackTrace();
            } finally {
                db.freeCon();
            }
        }

        // 数据填充到模版
        // if(ListUtil.listIsNotEmpty(scopeList)){
        response.setContentType("text/xml;charset=utf-8");
        String vmpagckage = "com/cpinfo/his/template/dept/";
        String vmname = "dept_scope.vm";
        PrintWriter pw = null;
        pw = response.getWriter();
        String vm = VelocityUtils.generateGridVm(vmpagckage, vmname, "scopes",
                scopeList);
        pw.print(vm);
        pw.flush();
        pw.close();
        // }
    }

    // *************************************************类别分割线********************************************************************
    // *************************************************床位操作********************************************************************

    /**
     * 转到床位新增页面
     *
     * @param request
     * @param response
     * @param modelMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/dept_bed_add")
    public ModelAndView deptBedAdd(HttpServletRequest request,
                                   HttpServletResponse response, ModelMap modelMap) throws Exception {
        // 新增需要传入默认编码；修改和查看需要传入ID
        String operationType = request.getParameter("operationType");// 操作类型：add（新增）,modify（修改）,view（查看）
        String operationId = request.getParameter("operationId") == null ? "" : URLDecoder.decode(request.getParameter("operationId"), "UTF-8");// 操作行的ID
        String defaultHos = request.getParameter("defaultHos");// 默认的医院编码
        String defaultDept = request.getParameter("defaultDept");// 默认的病区编码
        String save_num = StrUtil.strNullToDef(request.getParameter("save_num"), "0");//保存次数

        DBOperator db = null;
        List<Bas_beds> bedList = null;
        List<Bas_dept> deptList = null;// 病区服务科室list
        List<Bas_dicts> bds = null;
        try {
            db = new DBOperator();
            String dept_sql = "select deptcode,deptname from bas_dept t where exists"
                    + "(select rowid from bas_dept_scope t1 where t1.hosnum=? and t1.deptcode=? and t1.targetcode=t.deptcode) and t.isdeleted='N'";// 查询病区服务的科室

            String sql = "select * from bas_dicts t where t.hosnum = '0000' and t.nekey=49 and t.nevalue != '!' order by t.nevalue asc";
            bds = db.find(sql, Bas_dicts.class);

            if ("modify".equals(operationType) || "view".equals(operationType)) {// 修改；查看

                sql = "select t.*,t2.ordername as chgitemname  from bas_beds t,bas_orderitem t2 where t.hosnum=? and t.wardno=? and t.bedno=? and t2.ordercode=t.chgitem(+) and t2.hosnum = ?";
                String[] codes = operationId.split(";");
                if (codes != null && codes.length == 3) {
                    deptList = db.find(dept_sql, new Object[]{codes[0],
                            codes[1]}, Bas_dept.class);
                    bedList = db.find(sql, new Object[]{codes[0], codes[1],
                            codes[2], codes[0]}, Bas_beds.class);
                }
            } else {
                deptList = db.find(dept_sql, new Object[]{defaultHos,
                        defaultDept}, Bas_dept.class);
            }
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }

        modelMap.put("deptList", deptList);
        modelMap.put("sexList", bds);

        modelMap.put("operationType", operationType);
        modelMap.put("save_num", save_num);
        if ("add".equals(operationType)) {// 新增
            Bas_beds bed = new Bas_beds();
            bed.setHosnum(defaultHos);
            bed.setWardno(defaultDept);
            modelMap.put("bed", bed);
        } else {// 修改；查看
            Bas_beds bed = (Bas_beds) ListUtil.distillFirstRow(bedList);
            modelMap.put("bed", bed);
        }

        return new ModelAndView("dept/dept_bed_add", modelMap);
    }

    /**
     * 新增或者修床位信息
     *
     * @param request
     * @param response
     * @param bed
     * @throws Exception
     */
    @RequestMapping(value = "/dept_bed_added", method = RequestMethod.POST)
    public void deptBedAdded(HttpServletRequest request,
                             HttpServletResponse response, Bas_beds bed) throws Exception {
        String operationType = request.getParameter("operationType");// 操作类型：add（新增）,modify（修改）
        String old_bedno = request.getParameter("old_bedno");
        DBOperator db = null;
        try {
            db = new DBOperator();
            if ("add".equals(operationType)) {
                db.excute("insert into bas_beds(HOSNUM,WARDNO,DEPTCODE,ROOMNO,BEDNO,FORSEX,CHGITEM) values(?,?,?,?,?,?,?)",
                        new Object[]{bed.getHosnum(),
                                bed.getWardno(), bed.getDeptcode(),
                                bed.getRoomno(), URLDecoder.decode(bed.getBedno(), "utf-8"),
                                bed.getForsex(), bed.getChgitem()});
            } else if ("modify".equals(operationType)) {
                db.excute("update bas_beds set HOSNUM=?,WARDNO=?,DEPTCODE=?,ROOMNO=?,BEDNO=?,FORSEX=?,CHGITEM=? " +
                                "where hosnum=? and wardno=? and bedno=?",
                        new Object[]{bed.getHosnum(),
                                bed.getWardno(), bed.getDeptcode(),
                                bed.getRoomno(), bed.getBedno(),
                                bed.getForsex(), bed.getChgitem(),
                                bed.getHosnum(), bed.getWardno(), old_bedno});
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
     * 床位信息是否重复验证
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/dept_bed_add_check", method = RequestMethod.POST)
    public void deptBedAddCheck(HttpServletRequest request,
                                HttpServletResponse response) throws Exception {
        String hosnum = request.getParameter("hosnum");
        String wardno = request.getParameter("wardno");
        String bedno = URLDecoder.decode(request.getParameter("bedno"), "utf-8");

        DBOperator db = null;
        List bedList = null;
        try {
            db = new DBOperator();
            String sql = "select rowid from bas_beds where hosnum=? and wardno=? and bedno=?";
            bedList = db.find(sql, new Object[]{hosnum, wardno, bedno});
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = null;
        pw = response.getWriter();
        if (ListUtil.listIsNotEmpty(bedList)) {
            pw.print("Y");
        } else {
            pw.print("N");
        }
    }

    /**
     * 删除床位信息
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/dept_bed_remove", method = RequestMethod.GET)
    public void deptBedDelete(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        String checkIds = StrUtil.strNullToEmpty(request.getParameter("checkIds"));
        checkIds = URLDecoder.decode(checkIds, "utf-8");
        String[] Ids = checkIds.split(",");
        DBOperator db = null;
        try {
            db = new DBOperator();
            for (int i = 0; i < Ids.length; i++) {
                String[] id = Ids[i].split(";");
                if (id.length == 3) {
                    String sql = "delete from bas_beds where hosnum=? and wardno=? and bedno=?";
                    db.excute(sql, new Object[]{id[0], id[1], id[2]});
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
     * ajax方式加载床位表格显示数据
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/dept_bed_list_load", method = RequestMethod.GET)
    public void loadDeptBedsTable(HttpServletRequest request,
                                  HttpServletResponse response) throws Exception {
        String hosnum = request.getParameter("hosnum");
        String wardno = request.getParameter("wardno");

        // 数据库查询
        List bedsList = new ArrayList();
        if (StrUtil.strIsNotEmpty(hosnum) && StrUtil.strIsNotEmpty(wardno)) {
            DBOperator db = null;
            try {
                db = new DBOperator();
                String sql = "select c.*, d.deptname from (select a.hosnum,a.wardno,a.deptcode, a.roomno, a.bedno,f.ordername as chgitem, b.contents " +
                        "from BAS_Beds a, bas_dicts b,bas_orderitem f where a.hosnum = ? and a.wardno = ? and a.forsex = b.nevalue and b.nekey='49' and a.chgitem=f.ordercode and f.hosnum = ?) c " +
                        "left join bas_dept d on c.deptcode = d.deptcode and d.isdeleted='N'";
                bedsList = db.find(sql, new Object[]{hosnum, wardno, hosnum});
            } catch (Exception e) {
                db.rollback();
                e.printStackTrace();
            } finally {
                db.freeCon();
            }
        }

        // 数据填充到模版
        // if(ListUtil.listIsNotEmpty(scopeList)){
        response.setContentType("text/xml;charset=utf-8");
        String vmpagckage = "com/cpinfo/his/template/dept/";
        String vmname = "dept_bed.vm";
        PrintWriter pw = null;
        pw = response.getWriter();
        String vm = VelocityUtils.generateGridVm(vmpagckage, vmname, "beds",
                bedsList);
        pw.print(vm);
        pw.flush();
        pw.close();
        // }
    }

    /**
     * ajax方式加载床位表格显示数据
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/dept_bed_list")
    public ModelAndView deptBedList(HttpServletRequest request,
                                    HttpServletResponse response) throws Exception {
        String hosnum = request.getParameter("hosnum");
        String wardno = request.getParameter("wardno");

        ModelMap model = new ModelMap();
        model.put("hosnum", hosnum);
        model.put("wardno", wardno);
        return new ModelAndView("dept/dept_bed_list", model);
    }

    /**
     * ajax方式加载床位表格显示数据
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/bedItemLoad")
    public void bedItemLoad(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("text/xml;charset=utf-8");
        Bas_hospitals hospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        DBOperator db = null;
        PrintWriter pw = response.getWriter();
        try {
            db = new DBOperator();
            String sql = "select  b.ordercode as id,b.ordername as value from bas_orderitem b where b.parentid = (select max(a.ordercode) from bas_orderitem a where a.ordername = '床位医嘱' and a.hosnum = ?) and b.hosnum = ?";
            List orderitems = db.find(sql, new Object[]{hospitals.getHosnum(), hospitals.getHosnum()});
            pw.print(JSONArray.fromObject(orderitems).toString());
            pw.flush();
            pw.close();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }
    }

}
