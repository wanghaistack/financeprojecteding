package com.atguigu.atcrowdfunding.member.service;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("atcrowdfunding-activiti-service")
public interface ActivitiService {
	@RequestMapping("/nextPstep")
	public void nextPstep(@RequestBody Map<String, Object> variables);
/*	@RequestMapping("/taskComplet")
	public void taskComplet(@RequestBody Map<String, Object> variables);*/
	@RequestMapping("/nextPstepEnd")
	public void nextPstepEnd(@RequestBody Map<String, Object> variables);
	
}
