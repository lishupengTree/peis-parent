package com.lsp.his.web.user;

import com.lsp.his.db.DBOperator;
import com.lsp.his.tables.Bas_dicts;
import com.lsp.his.tables.Bas_role;
import com.lsp.his.utils.ListUtil;
import com.lsp.his.utils.StrUtil;
import com.lsp.his.utils.UuidUtil;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/8 22:40
 */


@Controller
@RequestMapping("/user/role")
public class BasRoleAction {

    /**
     * 转到角色列表页面
     *
     *
     * @throws Exception
     */
    @RequestMapping(value = "/role_list")
    public ModelAndView showList() {
        ModelMap model = new ModelMap();
        return new ModelAndView("user/role/role_list", model);
    }


    /**
     * ajax方式加载科室服务范围树
     *
     * @param request
     * @param response
     * @param modelMap
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/menu_tree", method = RequestMethod.GET)
    public void loadDeptScopeTree(HttpServletRequest request,
                                  HttpServletResponse response, ModelMap modelMap) throws Exception {
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
        try {
            db = new DBOperator();
            String sql = "select id,pid,name from bas_system_menu t order by id,index_no";
            scopeList = db.find(sql);
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }

        if (ListUtil.listIsNotEmpty(scopeList)) {// 存在菜单
            for (Iterator scopeIterator = scopeList.iterator(); scopeIterator
                    .hasNext(); ) {
                Map scopeMap = (Map) scopeIterator.next();
                String temp = "{id:\"" + scopeMap.get("id") + "\","
                        + "pId:\"" + scopeMap.get("pid") + "\","
                        + "name:\"" + scopeMap.get("name") + "\","
                        + "checked:" + StrUtil.strIsInArrary((String) scopeMap.get("id"), checkedList) + ","
                        + "open:" + "root".equals(scopeMap.get("id")) + "}";// 子节点
                lstTree.add(temp);
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
     * ajax方式加载用户列表显示数据
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/role_list_load", method = RequestMethod.GET)
    public void loadList(HttpServletRequest request,
                         HttpServletResponse response) throws Exception {

        // 数据库查询
        DBOperator db = null;
        List roleList = null;
        try {
            db = new DBOperator();
            roleList = db.find("select id,code,name,index_no," +
                    "(select contents from bas_dicts b where b.nekey=60 and b.nevalue=a.lvl) lvl" +
                    " from bas_role a where a.sts='Y' order by index_no");
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }

        // 数据填充到模版
        response.setContentType("text/xml;charset=utf-8");
        String vmpagckage = "com/lsp/his/template/user/";
        String vmname = "role_list.vm";
        PrintWriter pw = null;
        pw = response.getWriter();
        String vm = VelocityUtils.generateGridVm(vmpagckage, vmname, "roleList", roleList);
        pw.print(vm);
        pw.flush();
        pw.close();
    }

    /**
     * 转到角色新增页面
     *
     * @param request
     * @param response
     * @param modelMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/role_save")
    public ModelAndView doSave(HttpServletRequest request,
                               HttpServletResponse response, ModelMap modelMap) throws Exception {
        // 新增需要传入默认编码；修改和查看需要传入ID
        String operationType = request.getParameter("operationType");// 操作类型：add（新增）,modify（修改）,view（查看）
        String operationId = request.getParameter("operationId");// 操作行的ID
        String save_num = StrUtil.strNullToDef(request.getParameter("save_num"), "0");//保存次数

        DBOperator db = null;
        List<Bas_role> roleList = null;
        List<Map> menuList = null;
        List<Bas_dicts> roleLvlList = null;
        try {
            db = new DBOperator();
            if ("modify".equals(operationType) || "view".equals(operationType)) {// 修改；查看
                if (StrUtil.strIsNotEmpty(operationId)) {
                    roleList = db.find("select * from bas_role t where id=?", new Object[]{operationId}, Bas_role.class);
                    menuList = db.find("select menu_id from bas_user_role_right t where t.user_role_id=?", new Object[]{operationId});
                }
            }
            String sql = "select * from bas_dicts t where t.hosnum = '0000' and t.nekey = 60 and t.nevalue != '!' order by t.nevalue asc";
            roleLvlList = db.find(sql, Bas_dicts.class);
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }

        modelMap.put("roleLvlList", roleLvlList);
        modelMap.put("operationType", operationType);
        modelMap.put("save_num", save_num);
        if ("modify".equals(operationType) || "view".equals(operationType)) {// 修改；查看
            if (ListUtil.listIsNotEmpty(menuList)) {
                StringBuffer checkedScope = new StringBuffer();
                for (Iterator<Map> iterator = menuList.iterator(); iterator.hasNext(); ) {
                    Map scopeMap = iterator.next();
                    checkedScope.append(scopeMap.get("menu_id") + ";");
                }
                modelMap.put("checkedScope", checkedScope.toString());
            }
            Bas_role role = (Bas_role) ListUtil.distillFirstRow(roleList);
            modelMap.put("user", role);
        }

        return new ModelAndView("user/role/role_save", modelMap);
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
    @RequestMapping(value = "/role_saved", method = RequestMethod.POST)
    public void doSaved(HttpServletRequest request,
                        HttpServletResponse response, Bas_role role)
            throws Exception {
        String operationType = request.getParameter("operationType");// 操作类型：add（新增）,modify（修改）
        String addscopes = request.getParameter("addscopes");// 新增的范围
        String removescopes = request.getParameter("removescopes");// 删除的范围

        DBOperator db = null;
        try {
            db = new DBOperator();
            if ("add".equals(operationType)) {
                role.setId(UuidUtil.getUUID());//主键
                db.excute("insert into bas_role(ID,CODE,NAME,REMARK,INDEX_NO,LVL) values(?,?,?,?,?,?)",
                        new Object[]{role.getId(), role.getCode(), role.getName(), role.getRemark(), role.getIndex_no(), role.getLvl()});
            } else if ("modify".equals(operationType)) {
                db.excute("update bas_role set CODE=?,NAME=?,REMARK=?,INDEX_NO=?,LVL=? where ID=?",
                        new Object[]{role.getCode(), role.getName(), role.getRemark(),
                                role.getIndex_no(), role.getLvl(), role.getId()});
            }
            if (StrUtil.strIsNotEmpty(removescopes)) {// 批量删除菜单范围
                String[] removeList = removescopes.split(";");
                for (int i = 0; i < removeList.length; i++) {
                    db.excute("delete from bas_user_role_right where user_role_id=? and menu_id=?",
                            new Object[]{role.getId(), removeList[i]});
                }
            }

            if (StrUtil.strIsNotEmpty(addscopes)) {// 批量新增菜单范围
                String[] addList = addscopes.split(";");
                for (int i = 0; i < addList.length; i++) {
                    db.excute("insert into bas_user_role_right(user_role_id,menu_id) values(?,?)",
                            new Object[]{role.getId(), addList[i]});
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
            pw.print(role.getId());
        } catch (Exception e) {
            e.printStackTrace();
            pw.print("fail");
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
    @RequestMapping(value = "/role_save_check", method = RequestMethod.POST)
    public void doSaveCheck(HttpServletRequest request,
                            HttpServletResponse response) throws Exception {
        String code = request.getParameter("code");
        String name = request.getParameter("name");

        List roleList = null;
        DBOperator db = null;
        try {
            db = new DBOperator();
            if (StrUtil.strIsNotEmpty(code) && StrUtil.strIsNotEmpty(name)) {
                String sql = "select code,name from bas_role where (code=? or name=?)";
                roleList = db.find(sql, new Object[]{code, name});
            } else if (StrUtil.strIsNotEmpty(code)) {
                String sql = "select code,name from bas_role where code=?";
                roleList = db.find(sql, new Object[]{code});
            } else if (StrUtil.strIsNotEmpty(name)) {
                String sql = "select code,name from bas_role where name=?";
                roleList = db.find(sql, new Object[]{name});
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
        if (ListUtil.listIsNotEmpty(roleList)) {
            String reVal = "";
            int codeNum = 0;
            int nameNum = 0;
            for (int i = 0; i < roleList.size(); i++) {
                Map map = (Map) roleList.get(i);
                if (StrUtil.strIsNotEmpty(code) && code.equals((String) map.get("code"))) {
                    codeNum++;
                }
                if (StrUtil.strIsNotEmpty(name) && name.equals((String) map.get("name"))) {
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
     * 删除角色表
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/role_remove", method = RequestMethod.POST)
    public void doDelete(HttpServletRequest request,
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
                db.excute("update bas_role a set a.sts='N' where a.id=?", new Object[]{Ids[i]});
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
}


