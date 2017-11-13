package com.lsp.his.tables.pexam;

import java.util.Date;

/*
 *  科室小结视图的实现类
 * */
public class Pexam_result_view {
	private String hosum;
	private String examid;
	private String doctorid;
	private Date examdate;
	private String pexamid;
	private String deptsum;
	private String deptname;
	private String excdept;
		public String getHosum() {
			return hosum;
		}
		public void setHosum(String hosum) {
			this.hosum = hosum;
		}
		public String getExamid() {
			return examid;
		}
		public void setExamid(String examid) {
			this.examid = examid;
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
		public String getPexamid() {
			return pexamid;
		}
		public void setPexamid(String pexamid) {
			this.pexamid = pexamid;
		}
		public String getDeptsum() {
			return deptsum;
		}
		public void setDeptsum(String deptsum) {
			this.deptsum = deptsum;
		}
		public String getDeptname() {
			return deptname;
		}
		public void setDeptname(String deptname) {
			this.deptname = deptname;
		}
		public String getExcdept() {
			return excdept;
		}
		public void setExcdept(String excdept) {
			this.excdept = excdept;
		}

}
