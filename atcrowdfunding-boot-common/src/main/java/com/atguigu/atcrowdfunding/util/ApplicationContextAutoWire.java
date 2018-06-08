package com.atguigu.atcrowdfunding.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
@Component
//IOC容器接口注入
public class ApplicationContextAutoWire implements ApplicationContextAware{
	public static ApplicationContext applicationContext;
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
			ApplicationContextAutoWire.applicationContext=applicationContext;
	}


}
