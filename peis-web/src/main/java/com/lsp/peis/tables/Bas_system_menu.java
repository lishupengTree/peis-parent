package com.lsp.peis.tables;

import java.io.Serializable;

public class Bas_system_menu implements Serializable{
	private String id;
	private String pid;
	private String name;
	private String url;
	private String image;
	private String menu_type;
	private String open_type;
	private Double index_no;
	private String hotkeys;
	private String default_open;
	public String getHotkeys() {
		return hotkeys;
	}
	public void setHotkeys(String hotkeys) {
		this.hotkeys = hotkeys;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getMenu_type() {
		return menu_type;
	}
	public void setMenu_type(String menu_type) {
		this.menu_type = menu_type;
	}
	public String getOpen_type() {
		return open_type;
	}
	public void setOpen_type(String open_type) {
		this.open_type = open_type;
	}
	public Double getIndex_no() {
		return index_no;
	}
	public void setIndex_no(Double index_no) {
		this.index_no = index_no;
	}
	public String getDefault_open() {
		return default_open;
	}
	public void setDefault_open(String default_open) {
		this.default_open = default_open;
	}
}
