package com.lsp.peis.filter;

import com.lsp.peis.tables.Bas_user;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/6 2:13
 */
public class SystemFilter implements Filter {
    static String[] EXCEPTURL = new String[]{"login.htm", "checkUser.htm", "getDepts.htm", "getShaynes", "number/skin_testshow.htm", "getAllSkintest", "checkSkin", "number/getPost", "TMPrint/show.htm", "TMPrint/getprintmsg.htm", "TMPrint/getprintmsg.htm", "scan/forwardConfiguration.htm", "scan/sendLisData.htm", "TMPrint/getreport.htm", "CheckSearch/getCheckReuslt.htm", "CheckSearch/CheckSearchOpenJyBC.htm", "CheckSearch/findSample.htm", "CheckSearch/loadNRGridBC.htm", "yyt/yytindex.htm", "yyt/yytmian.htm", "yyt/setsession.htm", "zhyl/zhylIndex.htm", "zhyl/zhylmain.htm", "zhyl/setsession.htm", "/patSigns/patSigns_1.htm", "autoComeIN.htm", "/fescope/orders.htm", "/fescope/order.htm", "/fescope/cancelOrder.htm", "/fescope/queryStatus.htm", "/fescope/checkReport.htm"};  //

    public void init(FilterConfig arg0) throws ServletException {
        //Timer timer = new Timer();
        //timer.schedule(new DbTask(),0, 120*1000);
/*		Map map = new HashMap();
        map.put("柯斌","03");
		map.put("华闻闻","05");
		map.put("王晓璐","06");
		map.put("钟海坪","07");
		map.put("钱林君","08");
		map.put("吴洲红","09");
		map.put("孙其俊","12");*/

        // 一天的毫秒数
        long daySpan = 24 * 60 * 60 * 1000;

        // 规定的每天时间15:33:30运行
        //final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd '01:00:00'");
        // 首次运行时间
        Date startTime;
        try {
            //startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sdf.format(new Date()));

            // 如果今天的已经过了 首次运行时间就改为明天
            // if(System.currentTimeMillis() > startTime.getTime())
            //  startTime = new Date(startTime.getTime() + daySpan);


            // Timer t = new Timer();

            // TimerTask task = new TimerTask(){
            // @Override
            // public void run() {
            //  // 要执行的代码
            //InpChargeAction.advanceCharge();
            // }
            // };

            // 以每24小时执行一次
            // t.scheduleAtFixedRate(task, startTime, daySpan);


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


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
