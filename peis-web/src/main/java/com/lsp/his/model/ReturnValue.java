package com.lsp.his.model;

import java.io.Serializable;

public class ReturnValue implements Serializable{
    private static final long serialVersionUID = -2381367694999299725L;
    private boolean status;  //状态
    private String value; //返回值
    private String message; //消息
    private String lsheetids;  //检验id
    private String esheetids;  //检查id

    private String patientinfo; //导诊单  病人信息
    private String dzdlist;    //导诊单医嘱信息
    private String agetype;

    public String getAgetype() {
		return agetype;
	}
	public void setAgetype(String agetype) {
		this.agetype = agetype;
	}
	
	public String getPatientinfo() {
		return patientinfo;
	}
	public void setPatientinfo(String patientinfo) {
		this.patientinfo = patientinfo;
	}
	public String getDzdlist() {
		return dzdlist;
	}
	public void setDzdlist(String dzdlist) {
		this.dzdlist = dzdlist;
	}
	public String getLsheetids() {
		return lsheetids;
	}
	public void setLsheetids(String lsheetids) {
		this.lsheetids = lsheetids;
	}
	public String getEsheetids() {
		return esheetids;
	}
	public void setEsheetids(String esheetids) {
		this.esheetids = esheetids;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
