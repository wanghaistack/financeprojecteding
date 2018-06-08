package com.atguigu.atcrowdfunding.portal.web.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.atguigu.atcrowdfunding.util.Const;
@WebListener
public class StartUpSystemListener implements ServletContextListener {
	public StartUpSystemListener() {
		System.out.println("监听器正在监听...........");
	}
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		//获取上下文application域
		ServletContext application = sce.getServletContext();
		//获取上下文路径
		String contextPath = application.getContextPath();
		//把上下文路径设置到application域中
		application.setAttribute(Const.APP_PATH, contextPath);
		
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		
	}

}
