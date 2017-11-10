package com.lsp.his.utils;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;



/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/9 0:11
 */
public class XmlUtil {


    /**
     * 取得xml文件的根节点名称，即消息名称。
     *
     * @param xmlStr xml内容
     * @return String 返回名称
     */
    public static String getRootName(String xmlStr) throws Exception {
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new StringReader(xmlStr));
        Element root = doc.getRootElement();
        return root.getName();
    }


    /**
     * 把xml文件转换为map形式，其中key为有值的节点名称，并以其所有的祖先节点为前缀，用
     * "."相连接。如：SubscribeServiceReq.Send_Address.Address_Info.DeviceType
     *
     * @param xmlStr xml内容
     * @return Map 转换为map返回
     */
    public static Map<String, String> xml2Map(String xmlStr) throws JDOMException, IOException {
        Map<String, String> rtnMap = new HashMap<String, String>();
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new StringReader(xmlStr));
        // 得到根节点
        Element root = doc.getRootElement();
        String rootName = root.getName();
        rtnMap.put("root.name", rootName);

        // 调用递归函数，得到所有最底层元素的名称和值，加入map中
        convert(root, rtnMap, rootName);
        printMap(rtnMap);
        return rtnMap;
    }

    /**
     * 递归函数，找出最下层的节点并加入到map中，由xml2Map方法调用。
     *
     * @param e        xml节点，包括根节点
     * @param map      目标map
     * @param lastname 从根节点到上一级节点名称连接的字串
     */
    public static void convert(Element e, Map<String, String> map, String lastname) {
        if (e.getAttributes().size() > 0) {
            Iterator it_attr = e.getAttributes().iterator();
            while (it_attr.hasNext()) {
                Attribute attribute = (Attribute) it_attr.next();
                String attrname = attribute.getName();
                String attrvalue = e.getAttributeValue(attrname);
                //  map.put(lastname + "." + attrname, attrvalue);
                map.put(attrname, attrvalue);
            }
        }
        List children = e.getChildren();
        Iterator it = children.iterator();
        while (it.hasNext()) {
            Element child = (Element) it.next();
            // String name = lastname + "." + child.getName();
            String name = child.getName();
            // 如果有子节点，则递归调用
            if (child.getChildren().size() > 0) {
                convert(child, map, name);
            } else {
                // 如果没有子节点，则把值加入map
                map.put(name, child.getText());
                // 如果该节点有属性，则把所有的属性值也加入map
                if (child.getAttributes().size() > 0) {
                    Iterator attr = child.getAttributes().iterator();
                    while (attr.hasNext()) {
                        Attribute attribute = (Attribute) attr.next();
                        String attrname = attribute.getName();
                        String attrvalue = child.getAttributeValue(attrname);
                        // map.put(name + "." + attrname, attrvalue);
                        map.put(attrname, attrvalue);
                    }
                }
            }
        }
    }

    /**
     * 把xml文件转换为list形式，其中每个元素是一个map，map中的key为有值的节点名称，并以其所有的祖先节点为前缀，用
     * "."相连接。如：SubscribeServiceReq.Send_Address.Address_Info.DeviceType
     *
     * @param xmlStr xml内容
     * @return Map 转换为map返回
     */
    public static List<Map<String, String>> xml2List(String xmlStr) throws JDOMException, IOException {
        List<Map<String, String>> rtnList = new ArrayList<Map<String, String>>();
        Map<String, String> rtnMap = new HashMap<String, String>();
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new StringReader(xmlStr));
        // 得到根节点
        Element root = doc.getRootElement();
        String rootName = root.getName();
        rtnMap.put("root.name", rootName);

        // 调用递归函数，得到所有最底层元素的名称和值，加入map中
        convert2List(root, rtnMap, rootName, rtnList);
        if (rtnList.size() == 0)
            rtnList.add(rtnMap);
        return rtnList;
    }

    /**
     * 递归函数，找出最下层的节点并加入到map中，如果有相同的节点，则加入list中，
     * 由xml2List方法调用。
     *
     * @param e        xml节点，包括根节点
     * @param map      目标map
     * @param lastname 从根节点到上一级节点名称连接的字串
     * @param list     相同节点生成map放入list中
     */
    public static void convert2List(Element e, Map<String, String> map, String lastname, List<Map<String, String>> list) {
        if (e.getAttributes().size() > 0) {
            Iterator it_attr = e.getAttributes().iterator();
            while (it_attr.hasNext()) {
                Attribute attribute = (Attribute) it_attr.next();
                String attrname = attribute.getName();
                String attrvalue = e.getAttributeValue(attrname);
                map.put(lastname + "." + attrname, attrvalue);
            }
        }
        List children = e.getChildren();
        Iterator it = children.iterator();
        while (it.hasNext()) {
            Element child = (Element) it.next();
            String name = lastname + "." + child.getName();
            // 如果有子节点，则递归调用
            if (child.getChildren().size() > 0) {
                convert(child, map, name);
            } else {
                // 如果没有子节点，则把值加入map
                map.put(name, child.getText());
                // 如果该节点有属性，则把所有的属性值也加入map
                if (child.getAttributes().size() > 0) {
                    Iterator attr = child.getAttributes().iterator();
                    while (attr.hasNext()) {
                        Attribute attribute = (Attribute) attr.next();
                        String attrname = attribute.getName();
                        String attrvalue = child.getAttributeValue(attrname);
                        map.put(name + "." + attrname, attrvalue);
                    }
                }
            }
            //如果有相同节点，则加入list中，不考虑子节点中又有相同节点的情况
            if (e.getChildren(child.getName()).size() > 1) {
                Map<String, String> aMap = new HashMap<String, String>();
                aMap.putAll(map);
                list.add(aMap);
                map = new HashMap<String, String>();
                map.put("root.name", aMap.get("root.name"));
            }

        }
    }


    /**
     * 打印map 的所有key和value
     *
     * @param map
     */
    public static void printMap(Map<String, String> map) {
        Iterator<String> keys = map.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            System.out.println(key + ":" + map.get(key));
        }
    }


    public String xmlToLiu(String path) throws Exception {
        File file = new File(path);
        System.out.println(file.getPath());
        if (!file.exists() || file.isDirectory()) {
            throw new FileNotFoundException();
        }
        // 以"GB2312"编码，解决中文乱码问题
        InputStreamReader read = new InputStreamReader(
                new FileInputStream(file), "GB2312");
        BufferedReader br = new BufferedReader(read);
        String temp = null;
        StringBuffer sb = new StringBuffer();
        temp = br.readLine();
        while (temp != null) {
            sb.append(temp + "\n");
            temp = br.readLine();
        }
        br.close();
        read.close();
        return sb.toString();
    }


    public static void main(String[] args) throws Exception {

        XmlUtil xUtil = new XmlUtil();
        String xmlStr = xUtil.xmlToLiu("E:\\33333.xml");
        xUtil.xml2List(xmlStr);


    }

    public static List<Map<String, String>> xml2ListNew(String xmlStr) throws JDOMException, IOException {
        List<Map<String, String>> rtnList = new ArrayList<Map<String, String>>();
        Map<String, String> rtnMap = new HashMap<String, String>();
        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new StringReader(xmlStr));
        // 得到根节点
        Element root = doc.getRootElement();
        List children = root.getChildren();
        Iterator it = children.iterator();
        while (it.hasNext()) {
            Element e = (Element) it.next();
            Iterator it_attr = e.getAttributes().iterator();
            rtnMap = new HashMap<String, String>();
            while (it_attr.hasNext()) {
                Attribute att = (Attribute) it_attr.next();
//			  System.out.println("city="+att.getName()+","+att.getValue());
                rtnMap.put(att.getName(), att.getValue());
            }
            rtnList.add(rtnMap);
//		  System.out.println("rtnList000="+rtnList);
            List cc = e.getChildren();
            Iterator its = cc.iterator();
            while (its.hasNext()) {
                Element es = (Element) its.next();
                Iterator it_attrs = es.getAttributes().iterator();
                rtnMap = new HashMap<String, String>();
                while (it_attrs.hasNext()) {
                    Attribute attss = (Attribute) it_attrs.next();
//				  System.out.println("county="+attss.getName()+","+attss.getValue());
                    rtnMap.put(attss.getName(), attss.getValue());
                }
                rtnList.add(rtnMap);
                List ccc = es.getChildren();
                Iterator itss = ccc.iterator();
                while (itss.hasNext()) {
                    Element ess = (Element) itss.next();
                    Iterator it_attrss = ess.getAttributes().iterator();
                    rtnMap = new HashMap<String, String>();
                    while (it_attrss.hasNext()) {
                        Attribute attsss = (Attribute) it_attrss.next();
//					  System.out.println("county="+attss.getName()+","+attss.getValue());
                        rtnMap.put(attsss.getName(), attsss.getValue());
                    }
                    rtnList.add(rtnMap);
                }
            }
        }
        return rtnList;
    }


}
