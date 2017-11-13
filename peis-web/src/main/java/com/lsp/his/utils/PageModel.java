package com.lsp.his.utils;

import java.util.List;
import java.util.Map;

public class PageModel<T> {
	private List<T> list;
	private int currentPage;
	private int pageSize;
	private int totalRecord;
	
	

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}

	public int getTotalpage(){
		return(this.getTotalRecord()+this.getPageSize()-1)/this.pageSize;
	}
	public int getPageFirst(){
		return 1;
		
	}
	public int getPageLast(){
		return this.getTotalpage();
	}
	public int getPageUp(){
		if(this.getCurrentPage()<=1){
			return 1;
		}
		return this.getCurrentPage()-1;
	}
	public int getPageDown(){
		if(this.getCurrentPage()>=this.getTotalpage()){
			return this.getTotalpage();
		}
		return this.getCurrentPage()+1;
	}
	
}
