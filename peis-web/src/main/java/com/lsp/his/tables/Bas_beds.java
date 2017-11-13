package com.lsp.his.tables;

import java.io.Serializable;

public class Bas_beds implements Serializable{
	private String hosnum;
	private String wardno;
	private String deptcode;
	private String roomno;
	private String bedno;
	private String forsex;
	private String chgitem;
	private String chgitemname;
	public String getHosnum() {
		return hosnum;
	}
	public void setHosnum(String hosnum) {
		this.hosnum = hosnum;
	}
	public String getWardno() {
		return wardno;
	}
	public void setWardno(String wardno) {
		this.wardno = wardno;
	}
	public String getDeptcode() {
		return deptcode;
	}
	public void setDeptcode(String deptcode) {
		this.deptcode = deptcode;
	}
	public String getRoomno() {
		return roomno;
	}
	public void setRoomno(String roomno) {
		this.roomno = roomno;
	}
	public String getBedno() {
		return bedno;
	}
	public void setBedno(String bedno) {
		this.bedno = bedno;
	}
	public String getForsex() {
		return forsex;
	}
	public void setForsex(String forsex) {
		this.forsex = forsex;
	}
	public String getChgitem() {
		return chgitem;
	}
	public void setChgitem(String chgitem) {
		this.chgitem = chgitem;
	}
	public String getChgitemname() {
		return chgitemname;
	}
	public void setChgitemname(String chgitemname) {
		this.chgitemname = chgitemname;
	}
	
}
