package com.lsp.his.utils;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.NumberTool;

import java.io.StringWriter;
import java.util.*;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/8 22:15
 */
public class VelocityUtils {
    /**
     * 生成vm
     *
     * @param vmpackage vm所在的包名
     * @param vmname
     * @param listname
     * @param objects
     * @return
     * @throws Exception
     */
    public static String generateVm(String vmpackage, String vmname, String listname, List<? extends Object> objects) throws Exception {
        if (objects.size() > 0) {
            Properties p = new Properties();
            String classpath = VelocityUtils.class.getResource("/").getPath();//取得src的路径
            p.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, classpath + vmpackage);
            //设置velocity的编码
            p.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
            p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
            p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
            //初始化并取得Velocity引擎
            VelocityEngine ve = new VelocityEngine();
            ve.init(p);
            //取得velocity的模版
            Template t = null;
            t = ve.getTemplate(vmname);
            //取得velocity的上下文context
            VelocityContext context = new VelocityContext();
            context.put("number", new NumberTool());
            context.put("date", new DateTool());
            context.put(listname, objects);
            //输出流
            StringWriter writer = new StringWriter();
            //转换输出
            t.merge(context, writer);
            return writer.toString();
        } else {
            return null;
        }
    }


    /**
     * 生成vm
     *
     * @param vmpackage vm所在的包名
     * @param vmname
     * @param listname
     * @param objects
     * @return
     * @throws Exception
     */
    public static String generateVm(int totalCount, int posStart, String vmpackage, String vmname, String listname, List<? extends Object> objects, String listname2, List<? extends Object> objects2) throws Exception {
        if (objects.size() > 0 && objects2.size() > 0) {
            Properties p = new Properties();
            String classpath = VelocityUtils.class.getResource("/").getPath();//取得src的路径
            p.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, classpath + vmpackage);
            //设置velocity的编码
            p.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
            p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
            p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
            //初始化并取得Velocity引擎
            VelocityEngine ve = new VelocityEngine();
            ve.init(p);
            //取得velocity的模版
            Template t = null;
            t = ve.getTemplate(vmname);
            //取得velocity的上下文context
            VelocityContext context = new VelocityContext();
            context.put("totalCount", totalCount);
            context.put("posStart", posStart);
            context.put(listname, objects);
            context.put(listname2, objects2);
            //输出流
            StringWriter writer = new StringWriter();
            //转换输出
            //System.out.print(writer);
            t.merge(context, writer);
            return writer.toString();
        } else {

            String vmstring = "<?xml version='1.0' encoding='UTF-8'?><rows></rows>";
            return vmstring;
        }
    }

    /**
     * 生成vm,用于分页
     *
     * @param vmpackage  vm所在的包名
     * @param totalCount 总得数量
     * @param posStart   起始位置
     * @param objects
     * @return
     * @throws Exception
     */
    public static String generateVm(int totalCount, int posStart, String vmpackage, String vmname, String listname, List<? extends Object> objects) throws Exception {
        if (objects.size() > 0) {
            Properties p = new Properties();
            String classpath = VelocityUtils.class.getResource("/").getPath();//取得src的路径
            p.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, classpath + vmpackage);
            //设置velocity的编码
            p.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
            p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
            p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
            //初始化并取得Velocity引擎
            VelocityEngine ve = new VelocityEngine();
            ve.init(p);
            //取得velocity的模版
            Template t = null;
            t = ve.getTemplate(vmname);
            //取得velocity的上下文context
            VelocityContext context = new VelocityContext();
            context.put("totalCount", totalCount);
            context.put("posStart", posStart);
            context.put("date", new DateTool());
            context.put("number", new NumberTool());
            context.put(listname, objects);
            //输出流
            StringWriter writer = new StringWriter();
            //转换输出
            //System.out.print(writer);
            t.merge(context, writer);
            return writer.toString();
        } else {
            String vmstring = "<?xml version='1.0' encoding='UTF-8'?><rows></rows>";
            return vmstring;
        }
    }

    /**
     * 生成dhtmlxGrid的模板
     *
     * @param
     * @return
     * @throws Exception
     */
    public static String generateGridVm(String vmpackage, String vmname, String listname, List<? extends Object> objects) throws Exception {
        String vmstring = generateVm(vmpackage, vmname, listname, objects);
        if (vmstring == null) {
            vmstring = "<?xml version='1.0' encoding='UTF-8'?><rows></rows>";
        }
        return vmstring;
    }


    /**
     * 生成vm,用于显示图表的数据
     *
     * @param vmpackage vm所在的包名
     * @param vmname
     * @param object    图表对象  charData
     * @throws Exception
     */
    public static String generateVm(String vmpackage, String vmname, String objectName, Object object) throws Exception {
        if (object != null) {
            Properties p = new Properties();
            String classpath = VelocityUtils.class.getResource("/").getPath();//取得src的路径
            p.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, classpath + vmpackage);
            //设置velocity的编码
            p.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
            // p.setProperty(VelocityEngine.s, value), value)
            p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
            p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");

            //初始化并取得Velocity引擎
            VelocityEngine ve = new VelocityEngine();
            ve.init(p);
            //取得velocity的模版
            Template t = null;
            t = ve.getTemplate(vmname);
            //取得velocity的上下文context
            VelocityContext context = new VelocityContext();
            context.put("number", new NumberTool());
            context.put("date", new DateTool());
            context.put(objectName, object);
            //输出流
            StringWriter writer = new StringWriter();
            //转换输出
            t.merge(context, writer);
            return writer.toString();
        } else {
            return null;
        }
    }

    /**
     * 传入一个List,一个Object
     *
     * @param vmpackage
     * @param vmname
     * @param listname
     * @param objects
     * @param objectname
     * @param object
     * @return
     * @throws Exception
     */
    public static String generateVm(String vmpackage, String vmname, String listname, List<? extends Object> objects, String objectname, Object object) throws Exception {
        Properties p = new Properties();
        String classpath = VelocityUtils.class.getResource("/").getPath();//取得src的路径
        p.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, classpath + vmpackage);
        //设置velocity的编码
        p.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
        p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        //初始化并取得Velocity引擎
        VelocityEngine ve = new VelocityEngine();
        ve.init(p);
        //取得velocity的模版
        Template t = null;
        t = ve.getTemplate(vmname);
        //取得velocity的上下文context
        VelocityContext context = new VelocityContext();
        context.put("date", new DateTool());
        context.put("number", new NumberTool());
        context.put(listname, objects);
        context.put(objectname, object);
        //输出流
        StringWriter writer = new StringWriter();
        //转换输出
        //System.out.print(writer);
        t.merge(context, writer);
        return writer.toString();
    }

    public static void main(String[] args) {
        System.out.println(VelocityUtils.class.getResource("/").getPath());
        String vmpagckage = "com/cpinfo/his/template/chgmatch/";
        String vmname = "chgmatch.vm";
        List<Map> list = new ArrayList<Map>();
        try {
            String vm = VelocityUtils.generateGridVm(vmpagckage, vmname, "list", list);
            System.out.println(vm);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


}
