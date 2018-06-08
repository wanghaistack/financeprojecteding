package com.atguigu.atcrowdfunding.util;

import java.util.List;

public class Page<T> {
	//页码
	private Integer pageno;
	//每页显示的条数
	private Integer pagesize;
	//总页码
	private Integer totalno;
	//总共有多少条数据
	private Integer totalsize;
	//总数据集合
	private List<T> datas;
	
	
	public Page(Integer pageno, Integer pagesize) {
		if (pagesize<=0) {
			this.pagesize=10;
		}else {
			this.pagesize=pagesize;
		}
		if (pageno<=0) {
			this.pageno=1;
		}else {
			this.pageno = pageno;
		}
		
	
	}
	public Integer getPageno() {
		return pageno;
	}
	public void setPageno(Integer pageno) {
		this.pageno = pageno;
	}
	public Integer getPagesize() {
		return pagesize;
	}
	public void setPagesize(Integer pagesize) {
		this.pagesize = pagesize;
	}
	public Integer getTotalno() {
		return totalno;
	}
	public void setTotalno(Integer totalno) {
		this.totalno = totalno;
	}
	public Integer getTotalsize() {
		return totalsize;
	}
	public void setTotalsize(Integer totalsize) {
		this.totalsize = totalsize;
		this.totalno=(totalsize%pagesize==0)?(totalsize/pagesize):(totalsize/pagesize+1);
	}
	public List<T> getDatas() {
		return datas;
	}
	public void setDatas(List<T> datas) {
		this.datas = datas;
	}
	public Integer getIndexStart() {
		return (pageno-1)*pagesize;
	}
	
	
	
}
