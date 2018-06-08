package com.atguigu.atcrowdfunding.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atguigu.atcrowdfunding.bean.Cert;
import com.atguigu.atcrowdfunding.bean.Member;
import com.atguigu.atcrowdfunding.bean.MemberCert;
import com.atguigu.atcrowdfunding.bean.Ticket;
import com.atguigu.atcrowdfunding.member.service.MemberService;

@RestController
public class MemberController {
	@Autowired
	private MemberService memberService;
	//根据memberid查询member对象
	@RequestMapping("/queryMemberByMemberid/{memberid}")
	public Member queryMemberByMemberid(@PathVariable("memberid")Integer memberid) {
		Member member= memberService.queryMember(memberid);
		return member;
	}
	//根据member设置的status为2,更新数据库
	@RequestMapping("/updateStatusByMemberid")
	public int updateStatusByMemberid(@RequestBody Member member) {
		return memberService.updateStatusByMemberid(member);
		
	}
	@RequestMapping("/updateMemberStatus")
	public void updateMemberStatus(@RequestBody Map<String, Object> paramMap) {
		memberService.updateMemberStatus(paramMap);
	}
	
	/*@RequestMapping("/updateMemberStatus")
	public int updateMemberStatus(@RequestBody Map<String, Object> paramMap) {
		return memberService.updateMemberStatus(paramMap);
	}*/
	
	@RequestMapping("/queryMemberByMemberId/{memberid}")
	public Member queryMemberByMemberId(@PathVariable("memberid")Integer memberid) {
		return memberService.queryMember(memberid);
	}
	@RequestMapping("/queryCertByMemberId/{memberid}")
	public List<Map<String, Object>> queryCertByMemberId(@PathVariable("memberid")Integer memberid) {
		return memberService.queryCertByMemberId(memberid);
	}
	/*@RequestMapping("/checkAuthcode")
	public int checkAuthcode(@RequestBody Map<String, Object> paramMap) {
		return memberService.checkAuthcode(paramMap);
	}*/
	/*@RequestMapping("/checkAuthcode/{authcode}/{id}")
	public int checkAuthcode(@PathVariable("authcode") String authcode, @PathVariable("id")Integer id) {
		return memberService.checkAuthcode(authcode,id);
	}*/
	@RequestMapping("/queryMember/{piid}")
	public Member queryMember(@PathVariable("piid")String piid) {
		return memberService.queryMemberByPiid(piid);
	}
	/*@RequestMapping("/queryTicketByMemberId/{id}")
	public Ticket queryTicketBymemberid(@PathVariable("id") Integer id) {
		return memberService.queryTicketByMemberId(id);
	}*/
	/*@RequestMapping("/queryPage")
	public List<Map<String, Object>> queryPage(@RequestBody Map<String, Object> paramMap){
		return memberService.queryPage(paramMap);
	}*/
	/*@RequestMapping("/queryPageCount")
	public int queryPageCount() {
		return memberService.querypageCount();
	}*/
	
	
	@RequestMapping("/checkAuthcode")
	public int checkAuthcode(@RequestBody Map<String, Object> paramMap) {
		return memberService.checkAuthcode(paramMap);
	}
	
	@RequestMapping("/updateMemberByEmail")
	public int updateMemberByEmail(@RequestBody Member member) {
		return memberService.updateMemberByEmail(member);
	}
	
	@RequestMapping("/saveMemberCertList")
	public void saveMemberCertList(@RequestBody List<MemberCert> certFiles) {
		memberService.saveMemberCertList(certFiles);
	}
	
	@RequestMapping("/queryCertByAccttype")
	public List<Cert> queryCertByAccttype(@RequestBody Member loginMember){
		//根据用户类型查询资质
		return memberService.queryCertByAccttype(loginMember);
		
	}
	@RequestMapping("/updateBaseInfo")
	public void updateBaseInfo(@RequestBody Member loginMember) {
		//更新用户类型
		memberService.updateLoginMember(loginMember);
		
	}
	
	@RequestMapping("/updateAccttype")
	public void updateAccttype(@RequestBody Member loginMember) {
		//更新用户类型
	 memberService.updateAccttype(loginMember);
		 //查询流程单的步骤
		/*Ticket ticket = memberService.queryTicketByMemberId(loginMember.getId());
		 //设置流程步骤
		ticket.setPstep("baseinfo");
		//数据库更新流程审批单的流程步骤
		memberService.updateTicket(ticket);*/
	}
	
	@RequestMapping("/saveTicket")
	public void saveTicket(@RequestBody Ticket ticket) {
		memberService.saveTicket(ticket);
	}
	
	@RequestMapping("/queryTicketByMemberId/{id}")
	public Ticket queryTicketByMemberId(@PathVariable("id")Integer id) {
		Ticket ticket=memberService.queryTicketByMemberId(id);
		return ticket;
		
	}
	
	
	@RequestMapping("/member/{id}")
	public Member login(@PathVariable("id") Integer id) {
		Member member = memberService.queryMember(id);
		return member;
	}

	@RequestMapping("/query/{loginacct}")
	public Member dologin(@PathVariable("loginacct") String loginacct) {
		Member member = memberService.queryMemberByLoginacct(loginacct);
		return member;

	}

	
}
