package com.lsp.his.web.user;

import com.lsp.his.db.DBOperator;
import com.lsp.his.tables.Bas_hospitals;
import com.lsp.his.tables.Bas_user;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/8 23:00
 */

@Controller
@RequestMapping(value = "/personalInformation")
public class PersonalInformationController {

    /**
     * 个人信息设置
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/show")
    public ModelAndView incomeCollect(HttpServletRequest request, HttpServletResponse response,
                                      ModelMap modelMap) throws Exception {
        Calendar today = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        modelMap.put("reportdate", sdf.format(today.getTime()));

        Bas_user basUser = (Bas_user) request.getSession().getAttribute("login_user");
        String sql = "select * from Bas_user where id = ? and hosnum=?";
        DBOperator db = new DBOperator();
        try {
            List<Bas_user> bs = db.find(sql, new Object[]{basUser.getId(), basUser.getHosnum()});
            if (bs.size() > 0)
                modelMap.put("user", bs.get(0));
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }

        return new ModelAndView("/maintenance/personalInformation", modelMap);
    }


    @RequestMapping(value = "/saveInfo", method = RequestMethod.POST)
    public void saveInfo(HttpServletRequest request, HttpServletResponse response,
                         Bas_user bu) throws Exception {
        Bas_hospitals basHos = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String birthdate_str = request.getParameter("birthdate_str");
        String msg = "";
        String sql = "update bas_user set name = ?,idcard=?,job_no=?,sex=?," +
                "birthdate= to_date(?,'yyyy-MM-dd'),index_no=?,phone=?,mobile=?," +
                "short_mobile=?,input_custom=?,email=?,remark=?," +
                "post=?,post_code=? where id=? and hosnum = ?";
        DBOperator db = new DBOperator();
        try {
            db.excute(sql, new Object[]{bu.getName(), bu.getIdcard(), bu.getJob_no(), bu.getSex(),
                    birthdate_str, bu.getIndex_no(), bu.getPhone(), bu.getMobile(),
                    bu.getShort_mobile(), bu.getInput_custom(), bu.getEmail(), bu.getRemark(),
                    bu.getPost(), bu.getPost_code(), bu.getId(), basHos.getHosnum()});
            db.commit();
            msg = "success";
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(msg);
        pw.flush();
        pw.close();
    }


    @RequestMapping(value = "/newPassword", method = RequestMethod.GET)
    public void newPassword(HttpServletRequest request, HttpServletResponse response
    ) throws Exception {
        Bas_hospitals basHos = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String password = request.getParameter("password");
        String id = request.getParameter("id");
        String msg = "";
        String sql = "update bas_user set password=? where id=? and hosnum=?";
        DBOperator db = new DBOperator();
        try {
            db.excute(sql, new Object[]{password, id, basHos.getHosnum()});
            db.commit();
            msg = "success";
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(msg);
        pw.flush();
        pw.close();
    }

    /**
     * 更新用户健康档案信息
     *
     * @param request
     * @param response
     */
    @RequestMapping(value = "updateEHRUserInfo", method = RequestMethod.GET)
    public void updateEHRUserInfo(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=utf-8");
        Bas_hospitals basHos = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String id = request.getParameter("id");
        String roleId = request.getParameter("roleId");
        DBOperator db = null;
        PrintWriter pw = null;
        try {
            db = new DBOperator();
            pw = response.getWriter();

            String sql = "update bas_user set ehruser_key=?,ehrpassword=?, ehrrole=? where id=? and hosnum=?";
            db.excute(sql, new Object[]{username, password, roleId, id, basHos.getHosnum()});
            db.commit();
            pw.print("success");
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

