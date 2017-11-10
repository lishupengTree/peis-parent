package com.lsp.his.web.maintenance;

import com.lsp.his.db.DBOperator;
import com.lsp.his.tables.Bas_hospitals;
import com.lsp.his.tables.Bas_parms;
import com.lsp.his.tables.Bas_user;
import com.lsp.his.utils.UUIDGenerator;
import com.lsp.his.utils.VelocityUtils;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/9 13:01
 */

@Controller
@RequestMapping("/maintenance/chg_manage")
public class BasParmsController {
    private static final String ALL = "1";
    private static final String WITHTYPE = "2";

    @RequestMapping("/bas_parms")
    public String show() {
        return "maintenance/chg_manage/6_bas_parms";
    }

    @RequestMapping("/bas_parms_only")
    public String showOnly(HttpServletRequest request, HttpServletResponse response, String string) throws Exception {
        //System.out.println("maintenance/chg_manage/bas_parms_only");
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();
        Bas_user user = (Bas_user) request.getSession().getAttribute("login_user");
        String name = user.getName();
        int m = 0;
        String result = "";
        DBOperator db = new DBOperator();
        List<Map> user_list = new ArrayList<Map>();
        List<Map> oper_list = new ArrayList<Map>();
        try {
            String user_sql = "select b.id from bas_user b where b.name = '" + name + "'";
            String oper_sql = "select c.operator from chg_invoice c where c.hosnum='" + hosnum + "' group by c.operator";

            user_list = db.find(user_sql);
            JSONArray arr1 = JSONArray.fromObject(user_list);
            oper_list = db.find(oper_sql);
            JSONArray arr = JSONArray.fromObject(oper_list);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.freeCon();
        }
        //System.out.println();
        for (int i = 0; i < oper_list.size(); i++) {
            if (!user_list.get(0).get("id").toString().equals(oper_list.get(i).get("operator").toString())) {
                m += 0;
            } else {
                m += 1;
            }
        }
        if (m != 1) {
            result = "maintenance/chg_manage/bas_parms_only";
        } else {
            result = "/maintenance/chg_manage/block";
        }
        return result;
    }

    /***
     * 院级系统管理中得加载树*/
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/load_parms_tree", method = RequestMethod.GET)
    public void loadParmsTree(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Bas_hospitals basHos = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHos.getHosnum();
        String nodecode = basHos.getNodecode();
        List<String> lstTree = new ArrayList<String>(); //用于构建树
        DBOperator dbo = new DBOperator();
        try {
            List<Bas_parms> bps = null;
            //String sql = "select t.sysname from BAS_PARMS t group by t.sysname";    //参数分类
            String sql = "select t.scope from bas_parms t where t.hosnum=? and nodecode=? group by t.scope ";  //作用范围
            bps = dbo.find(sql, new Object[]{hosnum, nodecode}, Bas_parms.class);
            dbo.commit();
            //   手动构建树
            int i = 1;
            String temp = "";
            String strParent = "{id:1, pId:0, name:'系统参数', open:true}";
            lstTree.add(strParent);
            for (Bas_parms bp : bps) {
                String scope = bp.getScope();
                temp = "{id:" + (++i) + ",pId:" + 1 + ",name:\"" + scope + "\"}";
                lstTree.add(temp);
            }
            dbo.commit();
        } catch (Exception e) {
            e.printStackTrace();
            dbo.rollback();
        } finally {
            dbo.freeCon();
        }

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(JSONArray.fromObject(lstTree).toString());
        pw.flush();
        pw.close();
    }

    /***
     * 系统管理中通过作用范围(scope) 加载数据 1:默认加载所有；2：按scope加载*/
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/load_parms", method = RequestMethod.GET)
    public void loadParmsGrid(HttpServletRequest request,
                              HttpServletResponse response) throws Exception {
        Bas_hospitals basHos = (Bas_hospitals) request.getSession().getAttribute("login_hospital");

        String hosnum = basHos.getHosnum();
        String nodecode = basHos.getNodecode();
        String type = request.getParameter("type");  //1:默认加载所有；2：按sysname加载
        List<Bas_parms> parms = null;
        DBOperator db = new DBOperator();
        try {
            if (ALL.equals(type)) { //加载所有
                parms = db.find("select t.* from BAS_PARMS t where t.hosnum=? and t.nodecode = ?", new Object[]{hosnum, nodecode}, Bas_parms.class);
            } else if (WITHTYPE.equals(type)) { //按sysname加载
                String scope = URLDecoder.decode(request.getParameter("scope"), "utf-8");
                parms = db.find("select * from BAS_PARMS where scope=? and hosnum=? and nodecode =?", new Object[]{scope, hosnum, nodecode}, Bas_parms.class);
            }
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }

        String vmpagckage = "com/cpinfo/his/template/maintenance/";
        String vmname = "grid_bas_parm.vm";
        String vm = VelocityUtils.generateGridVm(vmpagckage, vmname, "grids", parms);
        response.setContentType("text/xml;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(vm);
        pw.flush();
        pw.close();
    }

    /***
     * Only  树*/
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/loadOnlyParmsTree", method = RequestMethod.GET)
    public void loadOnlyParmsTree(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Bas_hospitals basHos = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHos.getHosnum();
        String nodecode = basHos.getNodecode();
        String thisScope = URLDecoder.decode(request.getParameter("thisScope"), "utf-8");


        List<String> lstTree = new ArrayList<String>(); //用于构建树
        DBOperator dbo = new DBOperator();
        try {
            List<Bas_parms> bps = null;
            String sql = "select t.sysname from BAS_PARMS t where t.hosnum=? and t.nodecode =? and t.scope =? group by t.sysname ";
            bps = dbo.find(sql, new Object[]{hosnum, nodecode, thisScope}, Bas_parms.class);
            dbo.commit();
            //   手动构建树
            int i = 1;
            String temp = "";
            for (Bas_parms bp : bps) {
                String sysname = bp.getSysname();
                temp = "{id:" + (++i) + ",pId:" + 1 + ",name:\"" + sysname + "\"}";
                lstTree.add(temp);
            }
            dbo.commit();
        } catch (Exception e) {
            e.printStackTrace();
            dbo.rollback();
        } finally {
            dbo.freeCon();
        }

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(JSONArray.fromObject(lstTree).toString());
        pw.flush();
        pw.close();

    }

    /***
     * Only  GRID*/
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/loadOnlyGrid", method = RequestMethod.GET)
    public void loadOnlyGrid(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Bas_hospitals basHos = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHos.getHosnum();
        String nodecode = basHos.getNodecode();
        String type = request.getParameter("type");  //1:默认加载所有；2：按sysname加载
        String scope = URLDecoder.decode(request.getParameter("scope"), "utf-8");
        List<Bas_parms> parms = null;
        DBOperator db = new DBOperator();
        try {
            if (ALL.equals(type)) { //按scope加载所有
                parms = db.find("select t.* from BAS_PARMS t where t.hosnum=? and t.nodecode=? and t.scope=?", new Object[]{hosnum, nodecode, scope}, Bas_parms.class);
            } else if (WITHTYPE.equals(type)) { //按sysname加载
                String sysname = URLDecoder.decode(request.getParameter("sysname"), "utf-8");
                parms = db.find("select * from BAS_PARMS where hosnum=? and nodecode = ? and scope=? and sysname=?", new Object[]{hosnum, nodecode, scope, sysname}, Bas_parms.class);
            }
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }

        String vmpagckage = "com/cpinfo/his/template/maintenance/";
        String vmname = "grid_bas_parm_only.vm";
        String vm = VelocityUtils.generateGridVm(vmpagckage, vmname, "grids", parms);
        response.setContentType("text/xml;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(vm);
        pw.flush();
        pw.close();
    }

    /***
     * 详细信息页面   type：0 是查看； type：1 新增 */
    @RequestMapping(value = "/bas_parms_detail", method = RequestMethod.GET)
    public ModelAndView basParmsDetail(HttpServletRequest request,
                                       HttpServletResponse response, ModelMap modelMap) throws Exception {
        Bas_hospitals basHos = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHos.getHosnum();
        String nodecode = basHos.getNodecode();
        String showtype = request.getParameter("showtype");  //0: 查看，1：新增

        String permission = request.getParameter("permission");  //1: 系统权限,only:子权限,只读


        if ("1".equals(showtype)) {//1：新增
            //Do nothing.
            //Set default parameter.
            Bas_parms bp = new Bas_parms();
            bp.setCanedit(1);
            modelMap.put("b", bp);
        } else if ("0".equals(showtype)) {//0:查看
            String parmid = request.getParameter("parmid");
            DBOperator db = new DBOperator();
            try {
                List ps = db.find("select t.* from Bas_parms t where t.parmid =? and t.hosnum=? and t.nodecode = ?", new Object[]{parmid, hosnum, nodecode}, Bas_parms.class);
                db.commit();
                if (ps.size() > 0) {
                    modelMap.put("b", ps.get(0));
                }

                if ("only".equals(permission)) {
                    // 不是院级系统管理 跳到另外一张页面, 是子功能权限
                    return new ModelAndView("maintenance/chg_manage/bas_parms_detail_only", modelMap);
                }
            } catch (Exception e) {
                e.printStackTrace();
                db.rollback();
            } finally {
                db.freeCon();
            }
        }

        return new ModelAndView("maintenance/chg_manage/6_bas_parms_detail", modelMap);
    }

    /***
     * 获取系统分类*/
    @RequestMapping(value = "/get_bas_sysname", method = RequestMethod.GET)
    public void getBasSysname(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Bas_hospitals basHos = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHos.getHosnum();

        List sysnames = null;
        DBOperator db = new DBOperator();
        try {
            sysnames = db.find("select t.sysname from bas_parms t where t.hosnum=? group by t.sysname", hosnum);
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(JSONArray.fromObject(sysnames).toString());
        pw.flush();
        pw.close();
    }

    /***
     * 获取作用范围*/
    @RequestMapping(value = "/get_parms_scope", method = RequestMethod.GET)
    public void getParmsScope(HttpServletRequest request, HttpServletResponse response) throws Exception {
        List scopes = null;
        DBOperator db = new DBOperator();
        try {
            scopes = db.find("select t.name as scope from bas_system_menu t where pid='root'");
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(JSONArray.fromObject(scopes).toString());
        pw.flush();
        pw.close();
    }

    /***
     * 验证要保存的数据*/
    @RequestMapping(value = "/check_bas_parms", method = RequestMethod.GET)
    public void checkBasParms(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String showtype = request.getParameter("showtype");
        String parmname = URLDecoder.decode(request.getParameter("parmname"), "utf-8");
        String msg = "";
        DBOperator db = new DBOperator();
        Bas_hospitals hospital = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        try {
            List list = null;
            if ("0".equals(showtype)) {  //更新时候的验证去除本身的重复
                String parmid = request.getParameter("parmid");
                list = db.find("select t.parmid from bas_parms t where t.parmname = ? and parmid != ? and hosnum = ?", new Object[]{parmname, parmid, hospital.getHosnum()});
            } else {
                list = db.find("select t.parmid from bas_parms t where t.parmname = ? and hosnum=?", new Object[]{parmname, hospital.getHosnum()});
            }
            db.commit();
            if (list.size() > 0) {
                msg = "exist";
            } else {
                msg = "success";
            }
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            msg = "unknow";
        } finally {
            db.freeCon();
        }

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(msg);
        pw.flush();
        pw.close();
    }

    /***
     * 保存的数据*/
    @RequestMapping(value = "/save_bas_parms", method = RequestMethod.POST)
    public void saveBasParms(HttpServletRequest request, HttpServletResponse response, Bas_parms b) throws Exception {

        Bas_hospitals basHos = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHos.getHosnum();   //这里咯这个不知道啥东东了
        String nodecode = basHos.getNodecode();
        String showtype = request.getParameter("showtype");
        String msg = "";
        DBOperator db = new DBOperator();
        try {
            if ("0".equals(showtype)) {  //0:更新
                String sql = "update Bas_parms set hosnum = ?,scope = ?," +
                        "parmname = ?,parmvalue = ?,canedit = ?,comments = ?," +
                        "descriptions = ?,sysname = ?,defaultparms=? where parmid = ? ";
                db.excute(sql, new Object[]{hosnum, b.getScope(), b.getParmname(),
                        b.getParmvalue(), b.getCanedit(), b.getComments(), b.getDescriptions(), b.getSysname(),
                        b.getDefaultparms(), b.getParmid()});
                msg = "success";
            } else if ("1".equals(showtype)) { //1:插入
                String sql = "insert into Bas_parms(parmid,hosnum,scope,parmname," +
                        "parmvalue,canedit,comments,descriptions,sysname,defaultparms,nodecode)values(?,?,?,?,?,?,?,?,?,?,?)";
                db.excute(sql, new Object[]{new UUIDGenerator().generate(), hosnum, b.getScope(), b.getParmname(),
                        b.getParmvalue(), b.getCanedit(), b.getComments(), b.getDescriptions(), b.getSysname(), b.getDefaultparms(), nodecode});
                msg = "success";
            }
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            msg = "unknow";
        } finally {
            db.freeCon();
        }
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(msg);
        pw.flush();
        pw.close();
    }

    //del_bas_parms

    /***
     * 删除数据*/
    @RequestMapping(value = "/del_bas_parms", method = RequestMethod.GET)
    public void delBasParms(HttpServletRequest request, HttpServletResponse response, Bas_parms b) throws Exception {
        Bas_hospitals basHospitals = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String hosnum = basHospitals.getHosnum();

        String parmid = request.getParameter("parmid");
        String msg = "";
        DBOperator dbe = new DBOperator();
        try {

            String sql = "delete from Bas_parms where parmid = ? and hosnum=? ";
            dbe.excute(sql, new Object[]{parmid, hosnum});
            dbe.commit();
            msg = "success";

        } catch (Exception e) {
            e.printStackTrace();
            dbe.rollback();
            msg = "unknow";
        } finally {
            dbe.freeCon();
        }
        PrintWriter pw = response.getWriter();
        pw.print(msg);
        pw.flush();
        pw.close();

    }
}
