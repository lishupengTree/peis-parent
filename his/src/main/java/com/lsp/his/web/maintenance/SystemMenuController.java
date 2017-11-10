package com.lsp.his.web.maintenance;

import com.lsp.his.db.DBOperator;
import com.lsp.his.tables.Bas_system_menu;
import com.lsp.his.utils.VelocityUtils;
import net.sf.json.JSONArray;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.*;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/8 22:12
 */


@Controller
@RequestMapping("/maintenance")
public class SystemMenuController {

    /**
     * 菜单页面  2017年11月8日19:48:43 lsp
     *
     * @return
     */
    @RequestMapping("/system_menu")
    public String show() {
        return "maintenance/system_menu";
    }

    // 菜单维护的详细信息
    @RequestMapping("/system_menu_info")
    public ModelAndView showInfo(HttpServletRequest request,
                                 HttpServletResponse response, ModelMap modelMap) throws Exception {
        String name = "";
        if (null != request.getParameter("name")) {
            name = URLDecoder.decode(request.getParameter("name"), "utf-8");
        }
        String menu_type = "";
        if (null != request.getParameter("menu_type")) {
            menu_type = URLDecoder.decode(request.getParameter("menu_type"),
                    "utf-8");
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("menu_type", menu_type);
        map.put("name", name);

        int showtype = Integer.parseInt(request.getParameter("showtype")
                .toString());

        String id = "";
        if (showtype == 0) { // 是查看操作
            id = request.getParameter("id");
        } else if (showtype == 1) { // 是插入操作
            id = CreateTheChildId(request.getParameter("pid")); // 生成ID，通过父ID
        }
        map.put("id", id);
        return new ModelAndView("maintenance/system_menu_windows/info", map);
    }

    /***
     * 加载树菜单
     *
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

    /***
     * 加载树通过ID
     *
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/load_menu", method = RequestMethod.GET)
    public void loadMenu(HttpServletRequest request,
                         HttpServletResponse response, ModelMap modelMap) throws Exception {

        String nodeId = request.getParameter("nodeid").toString();
        String sql = "select t.* from bas_system_menu t where pid=? order by t.index_no";
        List<Bas_system_menu> bsms = new ArrayList<Bas_system_menu>();
        DBOperator db = null;
        try {
            db = new DBOperator();
            bsms = db.find(sql, new Object[]{nodeId}, Bas_system_menu.class);
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }
        String vmpagckage = "com/lsp/his/template/maintenance/";
        String vmname = "systemMenu.vm";
        String vm = VelocityUtils
                .generateGridVm(vmpagckage, vmname, "bs", bsms);
        response.setContentType("text/xml;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(vm);
        pw.flush();
        pw.close();
    }

    /***
     * 删除
     *
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/del_system_menu", method = RequestMethod.GET)
    public void delSystemMenu(HttpServletRequest request,
                              HttpServletResponse response, ModelMap modelMap) throws Exception {

        String id = request.getParameter("id");
        String msg = "";
        DBOperator dbe = new DBOperator();
        try {
            String sqlFindNodes = "select a.id from bas_system_menu a where pid = ?";
            int count = dbe.excute(sqlFindNodes, new Object[]{id});
            if (count == 0) {
                String sql = "delete from bas_system_menu where id = ?";
                dbe.excute(sql, new Object[]{id});
            } else { // 存在子节点不能删除
                msg = "hasChildren";
            }
            dbe.commit();
        } catch (Exception e) {
            e.printStackTrace();
            dbe.rollback();
        } finally {
            dbe.freeCon();
        }
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(msg);
        pw.flush();
        pw.close();
    }

    /***
     * 保存或新增
     *
     * @throws Exception
     */
    @RequestMapping(value = "/add_system_menu", method = RequestMethod.GET)
    public void addSystemMenu(HttpServletRequest request,
                              HttpServletResponse response, ModelMap modelMap) throws Exception {

        String showtype = request.getParameter("showtype").toString();

        String id = request.getParameter("id").toString();
        String pid = request.getParameter("pid").toString();
        String name = URLDecoder.decode(request.getParameter("name"), "utf-8");
        String url = request.getParameter("url").toString();
        String image = request.getParameter("image").toString();
        String menu_type = URLDecoder.decode(request.getParameter("menu_type"),
                "utf-8");
        String open_type = request.getParameter("open_type").toString();
        String index_no = request.getParameter("index_no").toString();
        String hotkeys = request.getParameter("hotkeys").toString();
        String default_open = request.getParameter("default_open").toString();
        String msg = "";
        DBOperator dbe = new DBOperator();
        try {
            if ("1".equals(showtype)) { // 新增
                String sqlGetCount = "select count(*) from bas_system_menu where id=?";
                List<Map<String, BigDecimal>> totalCountList = dbe.find(
                        sqlGetCount, new Object[]{id});
                int totalCount = totalCountList.get(0).get("count(*)")
                        .intValue();
                if (totalCount > 0) {
                    msg = "ID已经存在!";
                } else {
                    String sql = "insert into bas_system_menu(id,pid,name,url,image,menu_type,open_type,index_no,hotkeys,DEFAULT_OPEN) values(?,?,?,?,?,?,?,?,?,?)";
                    dbe.excute(sql, new Object[]{id, pid, name, url, image,
                            menu_type, open_type, index_no, hotkeys,
                            default_open});
                    msg = "添加成功!";
                }
            } else if ("0".equals(showtype)) {// 修改
                String sql = "update bas_system_menu set name =?,url=?,image=?,menu_type=?,open_type=?,index_no=?,hotkeys=?,default_open=? where id =?";
                dbe.excute(sql, new Object[]{name, url, image, menu_type,
                        open_type, index_no, hotkeys, default_open, id});
                msg = "更新成功!";
            }
            dbe.commit();
        } catch (Exception e) {
            e.printStackTrace();
            dbe.rollback();
        } finally {
            dbe.freeCon();
        }

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(msg);
        pw.flush();
        pw.close();

    }

    /***
     * 模糊搜索
     *
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/system_menu_dim_search", method = RequestMethod.GET)
    public void dimSearch(HttpServletRequest request,
                          HttpServletResponse response, ModelMap modelMap) throws Exception {
        String searchContent = URLDecoder.decode(request
                .getParameter("searchContent"), "utf-8");

        String sql = "select t.*　from bas_system_menu t where name like ? or url like ? or image like ?";
        // String msg="";
        List<Bas_system_menu> bsms = new ArrayList<Bas_system_menu>();
        DBOperator db = new DBOperator();
        try {
            bsms = db.find(sql, new Object[]{"%" + searchContent + "%",
                            "%" + searchContent + "%", "%" + searchContent + "%"},
                    Bas_system_menu.class);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.freeCon();
        }

        String vmpagckage = "com/cpinfo/his/template/maintenance/";
        String vmname = "systemMenu.vm";
        String vm = VelocityUtils.generateGridVm(vmpagckage, vmname, "bs", bsms);

        response.setContentType("text/xml;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(vm);
        pw.flush();
        pw.close();

    }

    /***
     * 通过PID 创建ID
     *
     * @throws Exception
     */
    String CreateTheChildId(String pid) throws Exception {

        String cId = "";
        String tPid = pid;
        String sql = "select t.id from bas_system_menu t where t.pid = ? order by t.id DESC";

        List<Bas_system_menu> bsms = new ArrayList<Bas_system_menu>();
        DBOperator db = new DBOperator();
        try {
            bsms = db.find(sql, new Object[]{pid}, Bas_system_menu.class);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.freeCon();
        }

        if ("root".equals(pid))
            tPid = "";
        if (bsms.size() > 0) {
            cId = bsms.get(0).getId();
            cId = cId.substring(tPid.length(), cId.length());
            int cIdInt = Integer.parseInt(cId) + 1;
            if (cIdInt < 10) {
                cId = tPid + "0" + cIdInt;
            } else {
                cId = tPid + cIdInt;
            }
        } else {
            cId = tPid + "01";
        }
        return cId;
    }

    /***
     * 图片上传
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/system_menu_img_upload", method = RequestMethod.POST)
    public void uploadImg(HttpServletRequest request,
                          HttpServletResponse response) throws Exception {

        String imgPath = request.getSession().getServletContext().getRealPath(
                "/");
        imgPath += "img/upload/"; // 图片文件路径
        String msg = "success";
        String returnPath = "img/upload/"; // 用来返回的地址
        File imgFile = null;
        imgFile = new File(imgPath);// upload 文件夹是否存在
        if (!imgFile.exists()) {
            imgFile.mkdirs();
        }

        DiskFileItemFactory fac = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(fac);
        upload.setHeaderEncoding("utf-8");
        List fileList = null;
        fileList = upload.parseRequest(request);

        Iterator<FileItem> it = fileList.iterator();
        while (it.hasNext()) {
            FileItem item = it.next();
            if (null != item && null != item.getName()) {
                String imgName = item.getName(); // 原始的文件名
                imgFile = new File(imgPath + imgName);
                if (!imgFile.exists()) { // 文件不存在
                    item.write(imgFile);
                    returnPath += imgName;
                } else { // 文件存在
                    String fName = imgName.substring(0, imgName
                            .lastIndexOf('.'));// 获取名称
                    String xName = imgName.substring(imgName.lastIndexOf('.')); // 获取后缀
                    String newImgName = fName + "_0" + xName;
                    imgFile = new File(imgPath + newImgName);
                    int num = 1;
                    while (imgFile.exists()) {
                        newImgName = fName + "_" + num + xName;
                        imgFile = new File(imgPath + newImgName);
                        num++;
                    }
                    item.write(imgFile);
                    returnPath += newImgName;
                }
            }
        }
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(returnPath);
        pw.flush();
        pw.close();
    }
}
