package com.lsp.his.web.login;

import com.lsp.his.db.DBOperator;
import com.lsp.his.tables.Bas_dept;
import com.lsp.his.tables.Bas_hospitals;
import com.lsp.his.tables.Bas_system_menu;
import com.lsp.his.tables.Bas_user;
import com.lsp.his.utils.ParamBuffer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/8 21:50
 */

@Controller
@RequestMapping("/")
public class PlatformController {



    @SuppressWarnings("unchecked")
    @RequestMapping("/platform")
    public ModelAndView platform(HttpServletRequest request, HttpServletResponse response) {
        Map map = new HashMap();
        DBOperator db = null;
        Bas_user user = (Bas_user) request.getSession().getAttribute("login_user");
        Bas_dept dept = (Bas_dept) request.getSession().getAttribute("login_dept");
        Bas_hospitals hospital = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        try {
            db = new DBOperator();

            //查询用户有限权的子系统ID
            //条件1：用户直接关联菜单
            //条件2：用户拥有角色关联的菜单
            List<Map> menu_ids = null;
            if (dept.getDeptclass().equals("科室")) {
                menu_ids = db.find("select a.id from bas_system_menu a ,bas_user_role_right b where a.pid = 'root' and a.id = b.menu_id  and ((b.user_role_id=? and b.hosnum=? and b.nodecode=? and b.dept_id=? ) or (b.user_role_id in (select c.role_id from bas_user_dept_role_relation c where c.user_id=? and c.hosnum=? and c.nodecode=? and c.office_id=? and c.role_id is not null))) ", new Object[]{user.getId(), hospital.getHosnum(), hospital.getNodecode(), dept.getDeptcode(), user.getId(), hospital.getHosnum(), hospital.getNodecode(), dept.getDeptcode()});
            } else {
                menu_ids = db.find("select a.id from bas_system_menu a ,bas_user_role_right b where a.pid = 'root' and a.id = b.menu_id  and ((b.user_role_id=? and b.hosnum=? and b.nodecode=? and b.dept_id=? ) or (b.user_role_id in (select c.role_id from bas_user_dept_role_relation c where c.user_id=? and c.hosnum=? and c.nodecode=? and c.shayne_id=? and c.role_id is not null))) ", new Object[]{user.getId(), hospital.getHosnum(), hospital.getNodecode(), dept.getDeptcode(), user.getId(), hospital.getHosnum(), hospital.getNodecode(), dept.getDeptcode()});
            }
            db.commit();
            Map my_menu_ids = new HashMap();
            String ids = "00";
            my_menu_ids.put("00", true);
            for (Map obj : menu_ids) {
                my_menu_ids.put(obj.get("id"), true);
                if (ids.equals("")) {
                    ids = (String) obj.get("id");
                } else {
                    ids = ids + "','" + (String) obj.get("id");
                }
            }
            map.put("my_menu_ids", my_menu_ids);
            List<Bas_system_menu> system_menus = db.find("select * from bas_system_menu t where t.pid = 'root' and t.id in ('" + ids + "') order by t.index_no", Bas_system_menu.class);
            map.put("system_menus", system_menus);

        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }
        return new ModelAndView("login/platform", map);
    }

    @RequestMapping(value = "/getNowServerTime")
    public void getNowServerTime(HttpServletRequest request, HttpServletResponse response) throws Exception {
        PrintWriter pw = response.getWriter();
        Date d = new Date();
        DateFormat dft = new SimpleDateFormat("HHmm");
        String hour = dft.format(d);
        String flag = "false";
        int minite = Integer.parseInt(hour);
        Bas_hospitals basHospital = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String timeSet = ParamBuffer.getParamValue(request, basHospital.getHosnum(), basHospital.getNodecode(), "门诊挂号", "报表查询不开放时间");
        if ("".equals(timeSet) || "N".equals(timeSet)) {
            flag = "false";
        } else {
            int start = Integer.parseInt(timeSet.split("~")[0].split(":")[0] + timeSet.split("~")[0].split(":")[1]);
            String endstr = timeSet.split("~")[1];
            int end = Integer.parseInt(endstr.split(":")[0] + endstr.split(":")[1]);
            if (start <= minite && minite < end) {
                flag = endstr;
            }
        }
        pw.print(flag);
        pw.flush();
        pw.close();
    }
}
