package com.lsp.his.web.maintenance;

import com.lsp.his.db.DBOperator;
import com.lsp.his.tables.Bas_dicts;
import com.lsp.his.tables.Bas_hospitals;
import com.lsp.his.utils.StrUtil;
import com.lsp.his.utils.VelocityUtils;
import com.lsp.his.utils.WordUtil;
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

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/8 23:31
 */

@Controller
@RequestMapping("/maintenance/chg_manage")
public class BasHospitalsController {

    @RequestMapping("/bas_hospitals")
    public String show() {
        return "maintenance/chg_manage/bas_hospital";
    }


    /**
     * 1.加载医院树
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getBasHospitalsTree", method = RequestMethod.GET)
    public void getLabItemsTree(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Bas_hospitals basHos = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        DBOperator db = new DBOperator();
        List<Bas_hospitals> hosList = null;
        try {
            String sql = "select t.hosnum,t.nodecode,t.hosname,t.supunit from Bas_hospitals t where t.nodetype='医院' order by t.hosnum";
            hosList = db.find(sql, Bas_hospitals.class);
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }
        List<String> lstTree = new ArrayList<String>();
        String temp = "";
        for (Bas_hospitals h : hosList) {
            temp = "{id:\"" + h.getHosnum() + "\"," +
                    "pid:\"" + h.getSupunit() + "\"," +
                    "name:\"" + h.getHosname() + "\"}";
            lstTree.add(temp);
        }

        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(JSONArray.fromObject(lstTree).toString());
        pw.flush();
        pw.close();
    }

    /**
     * 2.通过supunit 加载GRID
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/loadBasHospitals", method = RequestMethod.GET)
    public void loadBasHospitals(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String supunit = request.getParameter("supunit");
        DBOperator db = new DBOperator();
        List<Bas_hospitals> hosList = null;
        try {
            String sql = "select t.hosnum,t.nodecode,t.nodetype,t.hosname,t.tel,t.address from Bas_hospitals t where supunit=? and nodetype = '医院' order by t.hosnum";
            hosList = db.find(sql, new Object[]{supunit}, Bas_hospitals.class);
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }

        String vmpagckage = "com/lsp/his/template/maintenance/chgManage/";
        String vmname = "basHospitals.vm";
        String vm = VelocityUtils.generateGridVm(vmpagckage, vmname, "hs", hosList);
        response.setContentType("text/xml;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(vm);
        pw.flush();
        pw.close();
    }

    /**
     * 3.删除卫生站
     */
    @RequestMapping(value = "/delBasHospitals", method = RequestMethod.GET)
    public void delBasHospitals(HttpServletRequest request, HttpServletResponse response) throws Exception {

        String hosnumNodeCode = request.getParameter("hosnumNodeCode");
        String[] hAndN = hosnumNodeCode.split(";");

        String msg = "";
        boolean delFlag = true;
        DBOperator db = new DBOperator();
        try {
            //如果hosnum 与 nodecode 相等 查询有无下级在删除
            if (hAndN[0].equals(hAndN[1])) {
                List tList = db.find("select hosnum from Bas_hospitals where supunit = ?", new Object[]{hAndN[0]});
                if (tList.size() > 0) {
                    msg = "hasChildren";
                    delFlag = false;
                }
            }
            if (delFlag) {
                String sql = "delete from bas_hospitals where hosnum=? and nodecode=?";
                int num = db.excute(sql, new Object[]{hAndN[0], hAndN[1]});
                msg = "success";
            }
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
            msg = "unkown";
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
     * 4.查看或添加新的卫生站
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/basHospitalsDetail", method = RequestMethod.GET)
    public ModelAndView basHopitalsDetail(HttpServletRequest request, HttpServletResponse response
            , ModelMap map) throws Exception {

        int showtype = Integer.parseInt(request.getParameter("showtype").toString());//0:查看； 1添加
        String parentname = URLDecoder.decode(request.getParameter("parentname"), "utf-8");

        map.put("parentname", parentname);
        if (0 == showtype) {  //查看，或更新
            String[] hAndN = request.getParameter("hosnumNodeCode").split(";");
            String sql = "select * from bas_hospitals where hosnum = ? and nodecode =?";
            List<Bas_hospitals> bhs = null;
            Bas_hospitals bh = null;
            DBOperator db = new DBOperator();
            try {
                bhs = db.find(sql, new Object[]{hAndN[0], hAndN[1]}, Bas_hospitals.class);
                bh = bhs.get(0);
                map.put("bh", bh);
                //查找行政区划
                List<Bas_dicts> bds = db.find("select t.contents from bas_dicts t where t.nevalue = ? and t.nekey = '1'"
                        , bh.getDistcode(), Bas_dicts.class);
                if (bds.size() > 0) {
                    map.put("distname", bds.get(0).getContents());
                }
                db.commit();
            } catch (Exception e) {
                e.printStackTrace();
                db.rollback();
            } finally {
                db.freeCon();
            }
        } else if (1 == showtype) {
            Bas_hospitals bh = new Bas_hospitals();
            bh.setNodetype("医院");
            map.put("bh", bh);
        }

        return new ModelAndView("maintenance/chg_manage/bas_hospital_detail", map);
    }

    /**
     * 4.查看本院的卫生站
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/selfBasHospitalsDetail", method = RequestMethod.GET)
    public ModelAndView selfBasHopitalsDetail(HttpServletRequest request, HttpServletResponse response
            , ModelMap map) throws Exception {
        Bas_hospitals bh = (Bas_hospitals) request.getSession().getAttribute("login_hospital");
        map.put("parentname", bh.getHosname());
        map.put("bh", bh);

        DBOperator db = new DBOperator();
        try {
            //查找行政区划
            List<Bas_dicts> bds = db.find("select t.contents from bas_dicts t where t.nevalue = ? and t.nekey = '1'", bh.getDistcode(), Bas_dicts.class);
            if (bds.size() > 0) {
                map.put("distname", bds.get(0).getContents());
            }
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
        } finally {
            db.freeCon();
        }

        return new ModelAndView("maintenance/chg_manage/self_bas_hospital_detail", map);
    }

    /**
     * 5.保存
     */
    @RequestMapping(value = "/saveBasHospitals", method = RequestMethod.POST)
    public void saveBasHospitals(HttpServletRequest request, HttpServletResponse response
            , Bas_hospitals bh) throws Exception {
        int showtype = Integer.parseInt(request.getParameter("showtype").toString());//0:查看； 1添加

        String msg = "";

        if (StrUtil.strIsNotEmpty(bh.getHosname())) {
            bh.setInputcwb(WordUtil.trans2WbCode(bh.getHosname()));
            bh.setInputcpy(WordUtil.trans2PyCode(bh.getHosname()));
        }
        String oldhosnum = request.getParameter("oldhosnum");
        String oldnodecode = request.getParameter("oldnodecode");
        msg = checkBh(bh, showtype, oldhosnum, oldnodecode);
        if ("ok".equals(msg)) {
            DBOperator dbe = new DBOperator();
            try {
                if (1 == showtype) { //新增
                    dbe.excute("insert into bas_hospitals(" +
                                    "HOSNUM,NODECODE, HOSNAME, DISTCODE," +
                                    "SUPUNIT, NODETYPE,HOSDEGREE, ORGTYPE," +
                                    "EMPNUMBER,BEDS,DOCTORS,NURSES," +
                                    "ADDRESS,TEL,INTRODUCTION,INPUTCPY," +
                                    "INPUTCWB,DEGREELEVEL, HOSDNAME,DEGREELNAME, SHORTNAME,ycentercode,ncentercode,sn)values(" +
                                    "?,?,?,?," + "?,?,?,?," + "?,?,?,?," + "?,?,?,?," + "?,?,?,?,?,?,?,?)",
                            new Object[]{bh.getHosnum().trim(), bh.getNodecode().trim(), bh.getHosname().trim(), bh.getDistcode(),
                                    bh.getSupunit(), bh.getNodetype(), bh.getHosdegree(), bh.getOrgtype(),
                                    bh.getEmpnumber(), bh.getBeds(), bh.getDoctors(), bh.getNurses(),
                                    bh.getAddress(), bh.getTel(), bh.getIntroduction(), bh.getInputcpy(),
                                    bh.getInputcwb(), bh.getDegreelevel(), bh.getHosdname(), bh.getDegreelname(), bh.getShortname(), bh.getYcentercode(), bh.getNcentercode(), bh.getSn()});
                    msg = "success";
                } else if (0 == showtype) {//修改
                    dbe.excute("update bas_hospitals set " +
                                    "HOSNUM = ?,NODECODE = ?, HOSNAME = ?,DISTCODE = ?," +
                                    "NODETYPE = ?, HOSDEGREE = ?, ORGTYPE = ?,  EMPNUMBER = ?," +
                                    "BEDS = ?, DOCTORS = ?,  NURSES = ?,ADDRESS = ?," +
                                    "TEL = ?,INTRODUCTION = ?,  INPUTCPY = ?, INPUTCWB = ?," +
                                    "DEGREELEVEL = ?, HOSDNAME = ?, DEGREELNAME = ?, SHORTNAME=?,ycentercode=?,ncentercode=?,sn=? where HOSNUM=? and NODECODE=?",
                            new Object[]{bh.getHosnum().trim(), bh.getNodecode().trim(), bh.getHosname().trim(), bh.getDistcode(),
                                    bh.getNodetype(), bh.getHosdegree(), bh.getOrgtype(), bh.getEmpnumber(),
                                    bh.getBeds(), bh.getDoctors(), bh.getNurses(), bh.getAddress(),
                                    bh.getTel(), bh.getIntroduction(), bh.getInputcpy(), bh.getInputcwb(),
                                    bh.getDegreelevel(), bh.getHosdname(), bh.getDegreelname(), bh.getShortname(), bh.getYcentercode(), bh.getNcentercode(), bh.getSn(), oldhosnum, oldnodecode});
                    msg = "success";
                }
                dbe.commit();
            } catch (Exception e) {
                e.printStackTrace();
                dbe.rollback();
                msg = "fail.";
            } finally {
                dbe.freeCon();
            }
        }
        response.setContentType("text/html;charset=utf-8");
        PrintWriter pw = response.getWriter();
        pw.print(msg);
        pw.flush();
        pw.close();
    }

    // 验证数据
    private String checkBh(Bas_hospitals bh, int showtype, String oldhosnum, String oldnodecode) throws Exception {
        String msg = "";
        DBOperator dbe = new DBOperator();
        try {
            if (1 == showtype) { //添加
                if ("医院".equals(bh.getNodetype())) {
                    //医院只要判断 hosnum 是否已经存在就可以
                    List l = dbe.find("select t.hosnum from bas_hospitals t where t.hosnum = ?", new Object[]{bh.getHosnum()});
                    if (l.size() > 0) {
                        msg = "hosnumOrNodecodeIsExist";
                    } else {
                        msg = "ok";
                    }
                } else if ("院区".equals(bh.getNodetype())) {
                    //院区 就判断nodecode 是否已经存在
                    List l = dbe.find("select t.hosnum from bas_hospitals t where t.nodecode = ?", new Object[]{bh.getNodecode()});
                    if (l.size() > 0) {
                        msg = "hosnumOrNodecodeIsExist";
                    } else {
                        msg = "ok";
                    }
                }
            } else if (0 == showtype) { // 保存
                if ("医院".equals(bh.getNodetype())) {
                    //医院只要判断 hosnum 是否已经存在  并且不包含原始的值
                    if (bh.getHosnum().equals(oldhosnum)) {
                        msg = "ok";
                    } else {
                        List l = dbe.find("select t.hosnum from bas_hospitals t where t.hosnum = ?", new Object[]{bh.getHosnum()});
                        if (l.size() > 0) {
                            msg = "hosnumOrNodecodeIsExist";
                        } else {
                            msg = "ok";
                        }
                    }
                } else if ("院区".equals(bh.getNodetype())) {
                    if (bh.getNodecode().equals(oldnodecode)) {
                        msg = "ok";
                    } else {
                        List l = dbe.find("select t.hosnum from bas_hospitals t where t.hosnum = ? and t.nodecode =?", new Object[]{bh.getHosnum(), bh.getNodecode()});
                        if (l.size() > 0) {
                            msg = "hosnumOrNodecodeIsExist";
                        } else {
                            msg = "ok";
                        }
                    }
                }
            }
            dbe.commit();
        } catch (Exception e) {
            e.printStackTrace();
            dbe.rollback();
            msg = "unkown";
        } finally {
            dbe.freeCon();
        }

        return msg;
    }


}
