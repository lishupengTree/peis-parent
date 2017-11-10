package com.lsp.his.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ZTreeNode implements Serializable{

    private static final long serialVersionUID = -607798081136377227L;
    private String id;
	
	private String pid;
	
	private String icon;
	
	private String name;
	
	private boolean open;
	
	private String click;
	
	private boolean showRightMenu;
	
	private String draggable = "Y";
	
	private boolean isParent;
	
	private String menuType;
	
	private String rightHtml;
	
	private Map<String, Object> prototype = new HashMap<String, Object>();

	public ZTreeNode() {
	}

	public ZTreeNode(String id, String pid, String icon, String name,String draggable,boolean showRightMenu,
			boolean open,boolean isParent, String click,String menuType,String rightHtml) {
		this.id = id;
		this.pid = pid;
		this.icon = icon;
		this.name = name;
		this.open = open;
		this.click = click;
		this.draggable = draggable;
		this.isParent = isParent;
		this.showRightMenu = showRightMenu;
		this.menuType = menuType;
		this.rightHtml = rightHtml;
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

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public String getClick() {
		return click;
	}

	public void setClick(String click) {
		this.click = click;
	}

	public Map<String, Object> getPrototype() {
		return prototype;
	}

	public void setPrototype(Map<String, Object> prototype) {
		this.prototype = prototype;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("");
		
		sb.append("{");
		sb.append("\"id\":\"");
		sb.append(this.id);
		sb.append("\",\"pid\":\"");
		sb.append(this.pid);
		sb.append("\",\"name\":\"");
		sb.append(this.name);
		sb.append("\",\"rightHtml\":\"");
		sb.append(this.rightHtml);
		sb.append("\",\"menuType\":\"");
		sb.append(this.menuType);
		sb.append("\",\"showRightMenu\":");
		sb.append(this.showRightMenu);
		if(this.isParent){
			sb.append(",\"isParent\":");
			sb.append(this.isParent);
		}
		sb.append(",\"draggable\":\"");
		sb.append(this.draggable);
		if(this.icon!=null){
			sb.append("\",\"icon\":\"");
			sb.append(this.icon);
		}
		sb.append("\",\"open\":");
		sb.append(this.open);
		if(this.click!=null){
			sb.append(",\"click\":\"");
			sb.append(this.click);
			sb.append("\"");
		}
		if(this.prototype.size()>0){
			for(String key : this.prototype.keySet()){
				sb.append(",\"");
				sb.append(key);
				sb.append("\":\"");
				sb.append(this.prototype.get(key));
				sb.append("\"");
			}
		}
		sb.append("}");
		
		return sb.toString();
	}
	
	public static void main(String[] args) {
		ZTreeNode zTreeNode = new ZTreeNode("id", "pid", "icon", "name","draggable",false, true,false, "doClick('true')","1","");
		Map<String, Object> prototype = zTreeNode.getPrototype();
		prototype.put("test1", "testone");
		prototype.put("test2", "testtwo");
		System.out.println(zTreeNode);
	}

	public String getDraggable() {
		return draggable;
	}

	public void setDraggable(String draggable) {
		this.draggable = draggable;
	}

	public boolean isShowRightMenu() {
		return showRightMenu;
	}

	public void setShowRightMenu(boolean showRightMenu) {
		this.showRightMenu = showRightMenu;
	}

	public boolean isParent() {
		return isParent;
	}

	public void setIsParent(boolean isParent) {
		this.isParent = isParent;
	}

	public String getMenuType() {
		return menuType;
	}

	public void setMenuType(String menuType) {
		this.menuType = menuType;
	}

	public String getRightHtml() {
		return rightHtml;
	}

	public void setRightHtml(String rightHtml) {
		this.rightHtml = rightHtml;
	}
	
	
}
