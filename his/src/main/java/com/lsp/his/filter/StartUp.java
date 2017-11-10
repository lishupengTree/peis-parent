package com.lsp.his.filter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/8 20:32
 */
public class StartUp implements ServletContextListener {

    public void contextDestroyed(ServletContextEvent arg0){
    }

    public void contextInitialized(ServletContextEvent arg0) {
        System.out.println("系统初始化===");
    }
}
