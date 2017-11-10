package com.lsp.his.tables.pexam;

import java.util.Date;

public class Pexam_deptsum {
	private String hosnum;
	private String examid;
	private String excdept;
	private String deptsum;
	private String doctorid;
	private Date examdate;


    public String getHosnum() {
        return hosnum;
    }

    public void setHosnum(String hosnum) {
        this.hosnum = hosnum;
    }

    public String getExamid() {
        return examid;
    }

    public void setExamid(String examid) {
        this.examid = examid;
    }

    public String getExcdept() {
        return excdept;
    }

    public void setExcdept(String excdept) {
        this.excdept = excdept;
    }

    public String getDeptsum() {
        return deptsum;
    }

    public void setDeptsum(String deptsum) {
        this.deptsum = deptsum;
    }

    public String getDoctorid() {
        return doctorid;
    }

    public void setDoctorid(String doctorid) {
        this.doctorid = doctorid;
    }

    public Date getExamdate() {
        return examdate;
    }

    public void setExamdate(Date examdate) {
        this.examdate = examdate;
    }
}
