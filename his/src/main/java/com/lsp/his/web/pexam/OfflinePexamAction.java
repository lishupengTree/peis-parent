package com.lsp.his.web.pexam;

import com.lsp.his.db.DBOperator;
import com.lsp.his.utils.XmlUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author lishupeng
 * @Description
 * @Date 2017/11/9 0:10
 */

@Controller
@RequestMapping("/offlinePexam")
public class OfflinePexamAction {

    @RequestMapping("/show")
    public String show() {
        return "login/login";
    }

    public void offlinePexam(String file, Object[] str) throws Exception {
        XmlUtil xUtil = new XmlUtil();
        String xmlStr = xUtil.xmlToLiu(file);
        List<Map<String, String>> list = xUtil.xml2List(xmlStr);
        List<Map> temp = new ArrayList<Map>();
        Map<String, Map> offcolumnMap = new HashMap<String, Map>();
        List<Object[]> pi = new ArrayList<Object[]>();
        List<Object[]> pis = new ArrayList<Object[]>();
        Date datetime = new Date();
        Date pdate = null;
        Date hdate = null;
        String insertSql = "";
        String sql = "";
        String findsql = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String examid = formatter.format(datetime);
        DBOperator db = new DBOperator();
        try {
            //插入体检预约表
//	    	 insertSql="insert into pexam_main(" +
//	     			"hosnum," 		+
//	     			"examid," 		+
//	     			"unitcode," 	+
//	     			"unitname," 	+
//	     			"examname," 	+//5
//	     			"examtype," 	+
//	     			"salesman," 	+
//	     			"bookdate," 	+
//	     			"excdate," 		+
//	     			"canceldate," 	+//10
//	     			"unitprice," 	+
//	     			"examqty," 		+
//	     			"totalamt," 	+
//	     			"discount," 	+
//	     			"discamt," 		+//15
//	     			"addamt," 		+
//	     			"accdate," 		+
//	     			"acctype," 		+
//	     			"sheetno," 		+
//	     			"invoice," 		+//20
//	     			"comments" 		+
//	     			") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//	    	 db.excute(insertSql, new Object[]{"1005",examid,null,examid,"农民体检-"+examid,
//	    			 "职工体检",null,new Date(),null,null,
//	    			 null,null,null,null,null,
//	    			 null,null,null,null,null,
//	    			 null});
            //找到检查结果对应信息
            String itemid = "";
            for (int i = 2; i < str.length; i++) {
                if (i == str.length - 1) {
                    itemid = itemid + str[i].toString();
                } else {
                    itemid += str[i].toString() + ",";
                }
            }
            System.out.println("itemid========>" + itemid);
            sql = "select s.offcolumn,decode(s.detailid,null,d.itemcode,s.detailid) as detailid,d.itemcode,d.itemname,d.itemclass from pexam_items_group i left join pexam_items_groupdetails g on i.groupid=g.groupid left join pexam_items_def d on g.itemcode=d.itemcode left join pexam_items_details s on s.offcolumn is not null and d.itemcode=s.parentid where i.groupid in (?)".replace("?", itemid);
            temp = db.find(sql);
            for (int i = 0; i < temp.size(); i++) {
                offcolumnMap.put((String) temp.get(i).get("offcolumn"), temp.get(i));
            }
            for (int i = 0; i < list.size(); i++) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd hh:ss:mm");
                try {
                    pdate = df.parse(list.get(i).get("pbirthdate"));
                    hdate = dfs.parse(list.get(i).get("hcudate"));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
                findsql = "select * from pexam_mans where hosnum=? and pexamid=?";
                List li = db.find(findsql, new Object[]{str[0].toString(), str[1].toString() + "" + i});
                if (!li.isEmpty()) {
                    findsql = "delete from pexam_mans where hosnum=? and pexamid=?";
                    db.excute(findsql, new Object[]{str[0].toString(), str[1].toString() + "" + i});
                }
                pi.add(new Object[]{str[0].toString(), str[1].toString(), null, str[1].toString() + "" + i, "身份证",
                        list.get(i).get("cardno"), list.get(i).get("idno"), list.get(i).get("pname"), list.get(i).get("psex"), pdate,
                        null, null, null, null, null,
                        null, null, hdate, null, null,
                        null, null, null, null, null,
                        null, null, null, null, null,
                        null, null, null, null, null,
                });

                System.out.println("pexamid==========>" + str[1].toString() + "" + i);
                Iterator<String> keys = list.get(i).keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    if (offcolumnMap.get(key) != null && list.get(i).get(key) != null) {
                        pis.add(new Object[]{str[0].toString(), str[1].toString(), offcolumnMap.get(key).get("itemcode"), offcolumnMap.get(key).get("detailid"), null,
                                null, null, null, null, null,
                                list.get(i).get(key), str[1].toString() + "" + i, offcolumnMap.get(key).get("itemname"), offcolumnMap.get(key).get("itemcode"), null,
                                offcolumnMap.get(key).get("itemclass")});
                    }
                }
            }
            //插入体检预约人员表
            insertSql = "insert into pexam_mans(" +
                    "hosnum," +
                    "examid," +
                    "sn," +
                    "pexamid," +
                    "idtype," +//5
                    "idnum," +
                    "inscardno," +
                    "patname," +
                    "sex," +
                    "dateofbirth," +//10
                    "professional," +
                    "maritalstatus," +
                    "genexamdoctor," +
                    "doctorname," +
                    "examresult," +//15
                    "examsuggest," +
                    "genexamdate," +
                    "bdate," +
                    "edate," +
                    "invoiceid," +//20
                    "comments," +
                    "culturaldegree," +
                    "province," +
                    "city," +
                    "county," +//25
                    "township," +
                    "village," +
                    "laddress," +
                    "censustype," +
                    "province_c," +//30
                    "city_c," +
                    "county_c," +
                    "township_c," +
                    "village_c," +
                    "laddress_c" +//35
                    ") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            Object[][] param = new Object[pi.size()][2];
            Object[][] params = new Object[pis.size()][2];
            for (int i = 0; i < pi.size(); i++) {
                param[i] = pi.get(i);
            }
            db.excuteBatch(insertSql, param);
            //插入体检结果表
            insertSql = "insert into pexam_results(" +
                    "hosnum," +
                    "examid," +
                    "detailid," +
                    "rsheetno," +
                    "sn," +//5
                    "excdept," +
                    "doctorid," +
                    "examdate," +
                    "stringvalue," +
                    "numvalue," +//10
                    "result," +
                    "pexamid," +
                    "itemname," +
                    "itemcode," +
                    "unnormal," +//15
                    "examtype" +
                    ") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            for (int i = 0; i < pis.size(); i++) {
                params[i] = pis.get(i);
            }
            db.excuteBatch(insertSql, params);
            db.commit();
        } catch (Exception e) {
            db.rollback();
            e.printStackTrace();
        } finally {
            db.freeCon();
        }
    }
//	public static void main(String[] args) throws Exception{
//		OfflinePexamAction op=new OfflinePexamAction();
//		op.offlinePexam("E:\\33333.xml");
//	}
}
