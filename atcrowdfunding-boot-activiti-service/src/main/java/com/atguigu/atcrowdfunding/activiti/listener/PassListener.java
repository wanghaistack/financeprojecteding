package com.atguigu.atcrowdfunding.activiti.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.context.ApplicationContext;

import com.atguigu.atcrowdfunding.bean.Member;
import com.atguigu.atcrowdfunding.service.MemberService;
import com.atguigu.atcrowdfunding.util.ApplicationContextAutoWire;

public class PassListener implements ExecutionListener{

	@Override
	public void notify(DelegateExecution execution) throws Exception {
		System.out.println("流程审批已通过");
		Integer memberid=(Integer) execution.getVariable("memberid");
		ApplicationContext applicationContext = ApplicationContextAutoWire.applicationContext;
		MemberService memberService = applicationContext.getBean(MemberService.class);
		Member member=memberService.queryMemberByMemberid(memberid);
		member.setAuthstatus("2");
		memberService.updateStatusByMemberid(member);
	}

}
