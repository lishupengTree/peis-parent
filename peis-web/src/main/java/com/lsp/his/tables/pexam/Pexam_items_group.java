package com.lsp.his.tables.pexam;

public class Pexam_items_group {
	private String hosnum;
	private String groupid;
	private String groupname;
	private Double cost;
	private String comments;
	private long sn;
	private String stopflag;
	private String farmitem;

    private String workday; //取报告工作日

    private String yhbl; //体检优惠比例

    private String sprice; //售价
	
	public String getSprice() {
		return sprice;
	}
	public void setSprice(String sprice) {
		this.sprice = sprice;
	}
	public String getYhbl() {
		return yhbl;
	}
	public void setYhbl(String yhbl) {
		this.yhbl = yhbl;
	}
	public String getHosnum() {
		return hosnum;
	}
	public void setHosnum(String hosnum) {
		this.hosnum = hosnum;
	}
	public String getGroupid() {
		return groupid;
	}
	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}
	public String getGroupname() {
		return groupname;
	}
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}
	public Double getCost() {
		return cost;
	}
	public void setCost(Double cost) {
		this.cost = cost;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public long getSn() {
		return sn;
	}
	public void setSn(long sn) {
		this.sn = sn;
	}
	public String getStopflag() {
		return stopflag;
	}
	public void setStopflag(String stopflag) {
		this.stopflag = stopflag;
	}
	public String getFarmitem() {
		return farmitem;
	}
	public void setFarmitem(String farmitem) {
		this.farmitem = farmitem;
	}
	public String getWorkday() {
		return workday;
	}
	public void setWorkday(String workday) {
		this.workday = workday;
	}
	
}
