package com.lsp.his.tables;

import java.io.Serializable;

public class Bas_worddict implements Serializable{
    private static final long serialVersionUID = 1303439439732730257L;
    private Integer sn;
	private String cnchar;
	private String pychar;
	private String wbchar;
	public Integer getSn() {
		return sn;
	}
	public void setSn(Integer sn) {
		this.sn = sn;
	}
	public String getCnchar() {
		return cnchar;
	}
	public void setCnchar(String cnchar) {
		this.cnchar = cnchar;
	}
	public String getPychar() {
		return pychar;
	}
	public void setPychar(String pychar) {
		this.pychar = pychar;
	}
	public String getWbchar() {
		return wbchar;
	}
	public void setWbchar(String wbchar) {
		this.wbchar = wbchar;
	}
}
