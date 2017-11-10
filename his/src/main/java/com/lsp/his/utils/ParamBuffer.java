package com.lsp.his.utils;

import com.lsp.his.db.DBOperator;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 参数设置缓冲区，放入http session中
 *
 * @author lishupeng
 * @Description
 * @Date 2017/11/8 21:53
 */
public class ParamBuffer {
    //频次代码和中文名称MAP
    private static List<Map> freqList = new ArrayList<Map>();

    /**
     * 创建缓冲区
     *
     * @param request
     * @param hosnum
     * @param nodecode
     * @param sysname
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static void create(HttpServletRequest request, String hosnum, String nodecode, String sysname) throws Exception {
        DBOperator db = new DBOperator();
        try {
            //String sql = "select t.parmname,t.parmvalue from bas_parms t where t.hosnum = ? and t.nodecode = ? and t.sysname = ? ";
            String sql = "select t.parmname,t.parmvalue from bas_parms t where t.hosnum = ? and t.sysname = ? ";
            //List<Map<String, String>> basParms = db.find(sql, new Object[]{hosnum, nodecode, sysname});
            List<Map<String, String>> basParms = db.find(sql, new Object[]{hosnum, sysname});
            Map<String, String> bufferMap = new HashMap<String, String>();
            for (Map<String, String> basParm : basParms) {
                bufferMap.put(basParm.get("parmname"), basParm.get("parmvalue"));
            }

            //request.getSession().setAttribute(hosnum + nodecode + sysname, bufferMap);
            request.getSession().setAttribute(hosnum + sysname, bufferMap);
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            throw new Exception("参数设置缓冲区创建失败,sysname:" + sysname);
        } finally {
            db.freeCon();
        }

    }

    /**
     * 创建区域参数缓冲区
     *
     * @param request
     * @param sysname
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static void create(HttpServletRequest request, String sysname) throws Exception {
        DBOperator db = new DBOperator();
        try {
            //String sql = "select t.parmname,t.parmvalue from bas_parms t where t.hosnum = ? and t.nodecode = ? and t.sysname = ? ";
            String sql = "select t.parmname,t.parmvalue from bas_parms t where t.sysname = ? ";
            //List<Map<String, String>> basParms = db.find(sql, new Object[]{hosnum, nodecode, sysname});
            List<Map<String, String>> basParms = db.find(sql, new Object[]{sysname});
            Map<String, String> bufferMap = new HashMap<String, String>();
            for (Map<String, String> basParm : basParms) {
                bufferMap.put(basParm.get("parmname"), basParm.get("parmvalue"));
            }
            //request.getSession().setAttribute(hosnum + nodecode + sysname, bufferMap);
            request.getSession().setAttribute(sysname, bufferMap);
            db.commit();
        } catch (Exception e) {
            e.printStackTrace();
            db.rollback();
            throw new Exception("参数设置缓冲区创建失败,sysname:" + sysname);
        } finally {
            db.freeCon();
        }

    }

    /**
     * 清除缓冲区
     *
     * @param request
     * @param hosnum
     * @param nodecode
     * @param sysname
     * @throws Exception
     */
    public static void clear(HttpServletRequest request, String hosnum, String nodecode, String sysname) throws Exception {
        request.getSession().removeAttribute(hosnum + nodecode + sysname);
    }

    /**
     * 设置参数
     *
     * @param request
     * @param hosnum
     * @param nodecode
     * @param sysname
     * @param parmname
     * @param parmvalue
     */
    public static void setParamValue(HttpServletRequest request, String hosnum, String nodecode, String sysname, String parmname, String parmvalue) {
        @SuppressWarnings("unchecked")
        //Map<String, String> bufferMap = (Map<String, String>) request.getSession().getAttribute(hosnum + nodecode + sysname);
                Map<String, String> bufferMap = (Map<String, String>) request.getSession().getAttribute(hosnum + sysname);
        bufferMap.put(parmname, parmvalue);
        //request.getSession().setAttribute(hosnum + nodecode + sysname, bufferMap);
        request.getSession().setAttribute(hosnum + sysname, bufferMap);
    }

    /**
     * 得到参数
     *
     * @param request
     * @param hosnum
     * @param nodecode
     * @param sysname
     * @param parmname
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String getParamValue(HttpServletRequest request, String hosnum, String nodecode, String sysname, String parmname) {

        //Map<String, String> bufferMap = (Map<String, String>) request.getSession().getAttribute(hosnum + nodecode + sysname);
        Map<String, String> bufferMap = (Map<String, String>) request.getSession().getAttribute(hosnum + sysname);
        if (bufferMap == null || bufferMap.isEmpty()) {
            try {
                create(request, hosnum, nodecode, sysname);
                //bufferMap = (Map<String, String>) request.getSession().getAttribute(hosnum + nodecode + sysname);
                bufferMap = (Map<String, String>) request.getSession().getAttribute(hosnum + sysname);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return bufferMap.get(parmname);
    }

    /**
     * 得到区域参数
     *
     * @param request
     * @param sysname
     * @param parmname
     * @return
     */
    @SuppressWarnings("unchecked")
    public static String getParamValue(HttpServletRequest request, String sysname, String parmname) {

        //Map<String, String> bufferMap = (Map<String, String>) request.getSession().getAttribute(hosnum + nodecode + sysname);
        Map<String, String> bufferMap = (Map<String, String>) request.getSession().getAttribute(sysname);
        if (bufferMap == null || bufferMap.isEmpty()) {
            try {
                create(request, sysname);
                //bufferMap = (Map<String, String>) request.getSession().getAttribute(hosnum + nodecode + sysname);
                bufferMap = (Map<String, String>) request.getSession().getAttribute(sysname);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return bufferMap.get(parmname);
    }

    public static List<Map> getDeptScope(HttpServletRequest request, String hosNum, String nodeCode, String deptCode, String scopeType) throws Exception {
        List<Map> deptList = (List<Map>) request.getSession().getAttribute(hosNum + nodeCode + deptCode + scopeType);
        if (deptList == null || deptList.isEmpty()) {
            DBOperator db = new DBOperator();
            try {
                String sql = "select b.wmflag,b.cnmflag,b.herbsflag,b.materialflag,b.deptcode,b.deptname from bas_dept_scope a join bas_dept b on a.hosnum=? and a.scopetype=? and a.targetcode=? and b.hosnum=? and b.nodecode=? and a.deptcode=b.deptcode";
                deptList = db.find(sql, new Object[]{hosNum, scopeType, deptCode, hosNum, nodeCode});
                request.getSession().setAttribute(hosNum + nodeCode + deptCode + scopeType, deptList);
                db.commit();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.freeCon();
            }
        }
        return deptList;
    }

    public static List<Map> getFreqList() {
        if (freqList.size() == 0 || freqList == null) {
            DBOperator db = null;
            try {
                db = new DBOperator();
                String sql = "select f.fqcode,f.fqname from bas_frequency f";
                freqList = db.find(sql);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.freeCon();
            }
        }
        return freqList;
    }

    public static void setFreqList(List<Map> freqList) {
        ParamBuffer.freqList = freqList;
    }
}

