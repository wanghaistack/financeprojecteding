package com.atguigu.atcrowdfunding.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.atguigu.atcrowdfunding.bean.Member;
import com.atguigu.atcrowdfunding.bean.Ticket;

@FeignClient("atcrowdfunding-member-service")
public interface MemberService {
	@RequestMapping("/queryMember/{piid}")
	public Member queryMember(@PathVariable("piid")String piid);
	@RequestMapping("/queryTicketByMemberId/{id}")
	public Ticket queryTicketByMemberId(@PathVariable("id")Integer id);
	@RequestMapping("/queryMemberByMemberid/{memberid}")
	public Member queryMemberByMemberid(@PathVariable("memberid")Integer memberid);
	@RequestMapping("/updateStatusByMemberid")
	public int updateStatusByMemberid(@RequestBody Member member);
	

}
