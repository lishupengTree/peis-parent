package com.lsp.his.filter;

import com.lsp.his.tables.Bas_user;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/8 20:28
 */

public class SystemFilter implements Filter {
    static String[] EXCEPTURL = new String[]{"login.htm", "checkUser.htm", "getDepts.htm", "getShaynes", "number/skin_testshow.htm", "getAllSkintest", "checkSkin", "number/getPost", "TMPrint/show.htm", "TMPrint/getprintmsg.htm", "TMPrint/getprintmsg.htm", "scan/forwardConfiguration.htm", "scan/sendLisData.htm", "TMPrint/getreport.htm", "CheckSearch/getCheckReuslt.htm", "CheckSearch/CheckSearchOpenJyBC.htm", "CheckSearch/findSample.htm", "CheckSearch/loadNRGridBC.htm", "yyt/yytindex.htm", "yyt/yytmian.htm", "yyt/setsession.htm", "zhyl/zhylIndex.htm", "zhyl/zhylmain.htm", "zhyl/setsession.htm", "/patSigns/patSigns_1.htm", "autoComeIN.htm", "/fescope/orders.htm", "/fescope/order.htm", "/fescope/cancelOrder.htm", "/fescope/queryStatus.htm", "/fescope/checkReport.htm"};  //

    public void init(FilterConfig arg0) throws ServletException {

    }

    public void destroy() {

    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(true);
        String menuid = request.getParameter("menuid");
        if (menuid != null && !menuid.equals("")) {
            session.setAttribute("systemid", menuid.substring(0, 2));
            session.setAttribute("menuid", menuid);
        }

        String a = req.getRequestURL().toString();

        if (checkUrl(req)) {
            Bas_user user = (Bas_user) session.getAttribute("login_user");
            if (user == null) {
                String basePath = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath() + "/login.htm";
                //resp.sendRedirect(basePath);
                if (a.contains("selfHelpPrint") || a.contains("MyselfPrint") || a.contains("TMPrint") || a.contains("CheckOperate") || a.contains("CheckSearch")) {
                    chain.doFilter(request, response);
                } else {

                    resp.sendRedirect(basePath);
                }
            } else {
                chain.doFilter(request, response);
            }
        } else {
            chain.doFilter(request, response);
        }
        System.out.println("------->url" + req.getRequestURI());

    }

    boolean checkUrl(HttpServletRequest req) {
        for (String obj : EXCEPTURL) {
            if (req.getRequestURI().indexOf(obj) > 0) {
                return false;
            }
        }
        return true;
    }

}

