package com.atguigu.atcrowdfunding.controller;

import java.util.HashMap;
import java.util.Map;

import com.atguigu.atcrowdfunding.util.Page;

public class BaseController {
	/*
	 * start()方法每调用一次，创建一个map对象，多个线程指向同一个对象，造成多线程并发问题
	 * 用ThreadLocal绑定，确保一个线程对应一个对象
	 */
	private ThreadLocal<Map<String, Object>>threadLocal=new ThreadLocal<Map<String, Object>>();
	Map<String,Object>map =null;
	
	public void start() {
		map =new HashMap<String ,Object>();
		threadLocal.set(map);
	}
	public void message(String message) {
		threadLocal.get().put("message", message);
	}
	public void success(Boolean flag) {
		threadLocal.get().put("success", flag);
	}
	public Map<String, Object> end() {
		return threadLocal.get();
	}
	public void page(Page page) {
		//result.setPage(page);
		threadLocal.get().put("page", page);
	}
	public void data(Object data) {
		threadLocal.get().put("data", data);
	}
}
