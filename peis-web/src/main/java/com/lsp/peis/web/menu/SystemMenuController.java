package com.lsp.peis.web.menu;

import com.lsp.peis.db.DBOperator;
import com.lsp.peis.tables.Bas_system_menu;
import net.sf.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/7 13:22
 */
@Controller
@RequestMapping("/menu")
public class SystemMenuController {


    @RequestMapping("/system_menu")
    public String show() {
        return "menu/system_menu";
    }


    /***
     * 加载树菜单
     * @param request
     * @param response
     * @param modelMap
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/system_menu_tree", method = RequestMethod.GET)
    public void loadTreeStr(HttpServletRequest request,
                            HttpServletResponse response, ModelMap modelMap) throws Exception {
        String sqlStr = "select t.id,t.pid,t.name,t.index_no,t.menu_type from bas_system_menu t order by t.index_no";
        List<Bas_system_menu> bsms = null;
        DBOperator db = null;
        try {
            db = new DBOperator();
            bsms = db.find(sqlStr, Bas_system_menu.class);
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            if (db != null) {
                db.freeCon();
            }
        }
        // 传到前台
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(JSONArray.fromObject(bsms).toString());
        pw.flush();
        pw.close();

    }

}
