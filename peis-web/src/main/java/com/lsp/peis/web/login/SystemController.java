package com.lsp.peis.web.login;

import com.lsp.peis.db.DBOperator;
import com.lsp.peis.tables.Bas_dept;
import com.lsp.peis.tables.Bas_hospitals;
import com.lsp.peis.tables.Bas_system_menu;
import com.lsp.peis.tables.Bas_user;
import com.lsp.peis.utils.StrUtil;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/6 17:17
 */

@Controller
@RequestMapping("/")
public class SystemController {

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/system", method = RequestMethod.GET)
    public ModelAndView system(HttpServletRequest request, HttpServletResponse response) {
        String pid = request.getParameter("id");
        String menuid = null;
        DBOperator db = null;
        String url = null;
        try {
            db = new DBOperator();
            if (request.getSession().getAttribute(pid) == null) {
                Bas_user user = (Bas_user) request.getSession().getAttribute("login_user");
                Bas_dept dept = (Bas_dept) request.getSession().getAttribute("login_dept");
                Bas_hospitals hospital = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
                Bas_system_menu menu = (Bas_system_menu) db.findOne("select * from bas_system_menu t where t.id = ?", new Object[]{pid}, Bas_system_menu.class);
                List<Bas_system_menu> menus = null;
                if (dept.getDeptclass().equals("科室")) {
                    menus = db.find("select distinct a.* from ( SELECT * FROM bas_system_menu WHERE FIND_IN_SET(id, getChildList(?)) ) a,bas_user_role_right b where b.menu_id = a.id and a.default_open = 'Y' and ((b.user_role_id=? and b.hosnum=? and b.nodecode=? and b.dept_id=? ) or (b.user_role_id in (select c.role_id from bas_user_dept_role_relation c where c.user_id=? and c.hosnum=? and c.nodecode=? and c.office_id=? and c.role_id is not null))) order by a.index_no desc", new Object[]{pid, user.getId(), hospital.getHosnum(), hospital.getNodecode(), dept.getDeptcode(), user.getId(), hospital.getHosnum(), hospital.getNodecode(), dept.getDeptcode()}, Bas_system_menu.class);
                } else {
                    menus = db.find("select distinct a.* from ( SELECT * FROM bas_system_menu WHERE FIND_IN_SET(id, getChildList(?)) ) a,bas_user_role_right b where b.menu_id = a.id and a.default_open = 'Y' and ((b.user_role_id=? and b.hosnum=? and b.nodecode=? and b.dept_id=? ) or (b.user_role_id in (select c.role_id from bas_user_dept_role_relation c where c.user_id=? and c.hosnum=? and c.nodecode=? and c.shayne_id=? and c.role_id is not null))) order by a.index_no desc", new Object[]{pid, user.getId(), hospital.getHosnum(), hospital.getNodecode(), dept.getDeptcode(), user.getId(), hospital.getHosnum(), hospital.getNodecode(), dept.getDeptcode()}, Bas_system_menu.class);
                }

                Bas_system_menu other = new Bas_system_menu();
                other.setId(pid);
                other.setName("所有功能");
                other.setUrl("others.htm");
                other.setHotkeys("F9");
                menus.add(0, other);
                Map login_menus = (Map) request.getSession().getAttribute("login_menus");

                if (login_menus == null) {
                    login_menus = new HashMap();
                }
                login_menus.put(pid, menu);
                login_menus.put(pid + "s", menus);
                request.getSession().setAttribute("login_menus", login_menus);
                if (menus != null && menus.size() > 0) {
                    menuid = menus.get(menus.size() - 1).getId();
                    url = menus.get(menus.size() - 1).getUrl();
                    if (url.indexOf("?") > -1) {
                        url = url + "&menuid=" + menuid;
                    } else {
                        url = url + "?menuid=" + menuid;
                    }
                } else {

                }
            } else {
                List<Bas_system_menu> menus = (List) request.getSession().getAttribute(pid);
                if (menus != null && menus.size() > 0) {
                    menuid = menus.get(menus.size() - 1).getId();
                    url = menus.get(menus.size() - 1).getUrl();
                    if (url.indexOf("?") > -1) {
                        url = url + "&menuid=" + menuid;
                    } else {
                        url = url + "?menuid=" + menuid;
                    }
                } else {

                }
            }
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }
        return new ModelAndView(new RedirectView(url));
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/others", method = RequestMethod.GET)
    public ModelAndView module(HttpServletRequest request, HttpServletResponse response) {
        DBOperator db = null;
        Map map = new HashMap();
        String menuid = request.getParameter("menuid");
        String others = (String) request.getSession().getAttribute(menuid + "t");


        //if(others==null){
        try {
            db = new DBOperator();
            Bas_user user = (Bas_user) request.getSession().getAttribute("login_user");
            Bas_dept dept = (Bas_dept) request.getSession().getAttribute("login_dept");
            Bas_hospitals hospital = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
            List result = null;
            // 获取登录用户地址编码
            if (("19").equals(menuid)) {//优质服务 菜单MENUID
                @SuppressWarnings("unused")
                String codelength = "9";
                if (hospital != null) {
                    String code = StrUtil.strNullToEmpty(hospital.getDistcode());
                    if (!("").equals(code) && code != null) {
                        if (code.length() == 6) {//县级
                            result = db.find("select a.id,a.pid as pId,a.name,a.url as model,a.open_type as type,'true' as open from ( SELECT * FROM bas_system_menu WHERE FIND_IN_SET(id, getChildList(?))  and pid!='ROOT' ) a,bas_user_role_right b where b.menu_id = a.id  and ((b.user_role_id=? and b.hosnum=? and b.nodecode=? and b.dept_id=? ) or (b.user_role_id in (select c.role_id from bas_user_dept_role_relation c where c.user_id=? and c.hosnum=? and c.nodecode=? and c.office_id=? and c.role_id is not null))) and id in ('1901','1902','190206','190207') order by a.index_no", new Object[]{menuid, user.getId(), hospital.getHosnum(), hospital.getNodecode(), dept.getDeptcode(), user.getId(), hospital.getHosnum(), hospital.getNodecode(), dept.getDeptcode()});
                        } else if (code.length() == 9) {//乡级
                            result = db.find("select a.id,a.pid as pId,a.name,a.url as model,a.open_type as type,'true' as open from ( SELECT * FROM bas_system_menu WHERE FIND_IN_SET(id, getChildList(?)) and pid!='ROOT' ) a,bas_user_role_right b where b.menu_id = a.id  and ((b.user_role_id=? and b.hosnum=? and b.nodecode=? and b.dept_id=? ) or (b.user_role_id in (select c.role_id from bas_user_dept_role_relation c where c.user_id=? and c.hosnum=? and c.nodecode=? and c.office_id=? and c.role_id is not null))) and id in ('1901','1902','190201','190202','190203','190204','190205') order by a.index_no", new Object[]{menuid, user.getId(), hospital.getHosnum(), hospital.getNodecode(), dept.getDeptcode(), user.getId(), hospital.getHosnum(), hospital.getNodecode(), dept.getDeptcode()});
                        } else if (code.length() == 12) {//村级
                            result = db.find("select a.id,a.pid as pId,a.name,a.url as model,a.open_type as type,'true' as open from ( SELECT * FROM bas_system_menu WHERE FIND_IN_SET(id, getChildList(?)) and pid!='ROOT' ) a,bas_user_role_right b where b.menu_id = a.id  and ((b.user_role_id=? and b.hosnum=? and b.nodecode=? and b.dept_id=? ) or (b.user_role_id in (select c.role_id from bas_user_dept_role_relation c where c.user_id=? and c.hosnum=? and c.nodecode=? and c.office_id=? and c.role_id is not null))) and id in ('1901','1902','190208','190209','190210') order by a.index_no", new Object[]{menuid, user.getId(), hospital.getHosnum(), hospital.getNodecode(), dept.getDeptcode(), user.getId(), hospital.getHosnum(), hospital.getNodecode(), dept.getDeptcode()});
                        } else {//默认乡级
                            result = db.find("select a.id,a.pid as pId,a.name,a.url as model,a.open_type as type,'true' as open from (SELECT * FROM bas_system_menu WHERE FIND_IN_SET(id, getChildList(?)) and pid!='ROOT' ) a,bas_user_role_right b where b.menu_id = a.id  and ((b.user_role_id=? and b.hosnum=? and b.nodecode=? and b.dept_id=? ) or (b.user_role_id in (select c.role_id from bas_user_dept_role_relation c where c.user_id=? and c.hosnum=? and c.nodecode=? and c.office_id=? and c.role_id is not null))) and id in ('1901','1902','190201','190202','190203','190204','190205') order by a.index_no", new Object[]{menuid, user.getId(), hospital.getHosnum(), hospital.getNodecode(), dept.getDeptcode(), user.getId(), hospital.getHosnum(), hospital.getNodecode(), dept.getDeptcode()});
                        }
                    }
                }
            } else {

                if (dept.getDeptclass().equals("科室")) {
                    result = db.find("select distinct a.id,a.pid as pId,a.name,a.url as model,a.open_type as type,'true' as open,a.index_no from ( SELECT * FROM bas_system_menu WHERE FIND_IN_SET(id, getChildList(?)) and pid!='ROOT' ) a,bas_user_role_right b where b.menu_id = a.id  and ((b.user_role_id=? and b.hosnum=? and b.nodecode=? and b.dept_id=? ) or (b.user_role_id in (select c.role_id from bas_user_dept_role_relation c where c.user_id=? and c.hosnum=? and c.nodecode=? and c.office_id=? and c.role_id is not null))) order by index_no asc", new Object[]{menuid, user.getId(), hospital.getHosnum(), hospital.getNodecode(), dept.getDeptcode(), user.getId(), hospital.getHosnum(), hospital.getNodecode(), dept.getDeptcode()});
                } else {
                    result = db.find("select distinct a.id,a.pid as pId,a.name,a.url as model,a.open_type as type,'true' as open,a.index_no from ( SELECT * FROM bas_system_menu WHERE FIND_IN_SET(id, getChildList(?)) and pid!='ROOT' ) a,bas_user_role_right b where b.menu_id = a.id  and ((b.user_role_id=? and b.hosnum=? and b.nodecode=? and b.dept_id=? ) or (b.user_role_id in (select c.role_id from bas_user_dept_role_relation c where c.user_id=? and c.hosnum=? and c.nodecode=? and c.shayne_id=? and c.role_id is not null))) order by index_no asc", new Object[]{menuid, user.getId(), hospital.getHosnum(), hospital.getNodecode(), dept.getDeptcode(), user.getId(), hospital.getHosnum(), hospital.getNodecode(), dept.getDeptcode()});
                }
            }
            Map menu = new HashMap();
            menu.put("id", menuid);
            menu.put("pid", "0");
            menu.put("name", "功能列表");
            menu.put("open", true);
            result.add(0, menu);
            others = (JSONArray.fromObject(result)).toString();
            request.getSession().setAttribute(menuid + "t", others);
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }
        //}
        map.put("others", others);
        map.put("validto_warning", (String) request.getParameter("validto_warning"));
        return new ModelAndView("login/others", map);
    }
}
