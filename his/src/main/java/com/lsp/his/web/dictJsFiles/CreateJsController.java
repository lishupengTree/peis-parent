package com.lsp.his.web.dictJsFiles;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lsp.his.db.DBOperator;
import com.lsp.his.tables.Bas_dicts;
import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/jsfile")
public class CreateJsController {

    @RequestMapping(params = "method=dict")
    void createDicts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        String nekey = request.getParameter("nekey");
        PrintWriter pw = response.getWriter();
        if (nekey == null && nekey.equals("")) {
            pw.print("var dict" + nekey + " = [];");
        } else {
            List dicts = (List) request.getSession().getAttribute("nekey_" + nekey);
            if (dicts == null || dicts.isEmpty()) {
                DBOperator db = null;
                try {
                    db = new DBOperator();
                    dicts = db.find("select * from bas_dicts t where t.hosnum = ? and t.nekey = ? and (t.isdeleted is null or lower(t.isdeleted)='n') and t.nevalue != '!' order by t.nevalue", new Object[]{"0000", Integer.parseInt(nekey)}, Bas_dicts.class);
                    request.getSession().setAttribute("nekey_" + nekey, dicts);
                    JSONArray jsons = JSONArray.fromObject(dicts);
                    pw.print("var dicts" + nekey + " = " + jsons.toString() + ";");
                } catch (Exception e) {
                    db.rollback();
                    e.printStackTrace();
                    pw.print("fail");
                } finally {
                    db.freeCon();
                }
            } else {
                JSONArray jsons = JSONArray.fromObject(dicts);
                pw.print("var dicts" + nekey + " = " + jsons.toString() + ";");
            }
        }
    }

//    @RequestMapping(params = "method=basFrequency")
//    void createBasFrequencys(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        response.setContentType("text/html;charset=utf-8");
//        PrintWriter pw = response.getWriter();
//        List<Bas_frequency> basFrequencys = (List<Bas_frequency>) request.getSession().getAttribute("frequency_0000");
//        if (basFrequencys == null || basFrequencys.isEmpty()) {
//            DBOperator db = null;
//            try {
//                db = new DBOperator();
//                basFrequencys = db.find("select * from bas_frequency t where t.hosnum = ? order by to_number(t.comments)", new Object[]{"0000"}, Bas_frequency.class);
//                request.getSession().setAttribute("frequency_0000", basFrequencys);
//                JSONArray jsons = JSONArray.fromObject(basFrequencys);
//                pw.print("var frequencys= " + jsons.toString() + ";");
//            } catch (Exception e) {
//                db.rollback();
//                e.printStackTrace();
//                pw.print("fail");
//            } finally {
//                db.freeCon();
//            }
//        } else {
//            JSONArray jsons = JSONArray.fromObject(basFrequencys);
//            pw.print("var frequencys = " + jsons.toString() + ";");
//        }
//
//    }
}
