package com.atguigu.atcrowdfunding.member.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atguigu.atcrowdfunding.bean.Cert;
import com.atguigu.atcrowdfunding.bean.Member;
import com.atguigu.atcrowdfunding.bean.MemberCert;
import com.atguigu.atcrowdfunding.bean.Ticket;
import com.atguigu.atcrowdfunding.member.dao.MemberMapper;
import com.atguigu.atcrowdfunding.member.service.ActivitiService;
import com.atguigu.atcrowdfunding.member.service.MemberService;
import com.atguigu.atcrowdfunding.util.Const;
@Service
@Transactional
public class MemberServiceImpl implements MemberService {
	@Autowired
	private MemberMapper memberMapper;
	@Autowired
	private ActivitiService activitiService;
	
	@Override
	public Member queryMember(Integer id) {
		
		Member member=memberMapper.selectByPrimaryKey(id);
		return member;
	}

	@Override
	public Member queryMemberByLoginacct(String loginacct) {
		
		return memberMapper.queryMemberByLoginacct(loginacct);
	}

	@Override
	public Ticket queryTicketByMemberId(Integer id) {
		
		return memberMapper.queryTicketByMemberId(id);
	}

	@Override
	public void saveTicket(Ticket ticket) {
		memberMapper.saveTicket(ticket);
		
	}

	@Override
	public void updateAccttype(Member loginMember) {
		//更新会员类型
		int count=memberMapper.updateMember(loginMember);
		//更新流程进度,先查询流程表t_ticket
		Ticket ticket=memberMapper.queryTicketByMemberId(loginMember.getId());
		//设置步骤
		ticket.setPstep("baseinfo");
		//更新数据库
		 int count1=memberMapper.updateTicket(ticket);
		//更新流程进度
		Map<String, Object> variables=new HashMap<String, Object>();
		variables.put("loginacct",loginMember.getLoginacct());
		variables.put("piid", ticket.getPiid());
		activitiService.nextPstep(variables);
	}

	@Override
	public void updateLoginMember(Member loginMember) {
		//更新数据库会员信息
		int count=memberMapper.updateLoginMember(loginMember);
		//根据memberid查询t_ticket表中的数据，设置pstep
		Ticket ticket = memberMapper.queryTicketByMemberId(loginMember.getId());
		ticket.setPstep("certupload");
		String piid = ticket.getPiid();
		 count=memberMapper.updateTicket(ticket);
		//流程任务设置下一步
		Map<String, Object> variables=new HashMap<String,Object>();
		variables.put(Const.LOGINACCT, loginMember.getLoginacct());
		variables.put(Const.FLAG, true);
		variables.put(Const.PIID, piid);
		activitiService.nextPstep(variables);
	}

	@Override
	public List<Cert> queryCertByAccttype(Member loginMember) {
		
		return memberMapper.queryCertByAccttype(loginMember);
	}

	@Override
	public void saveMemberCertList(List<MemberCert> certFiles) {
		//把上传的资质集合保存到数据库中
		memberMapper.saveMemberCertList(certFiles);
		//从集合中单取一个membercert对象
		MemberCert memberCert=certFiles.get(0);
		Member member=memberMapper.queryMemberById(memberCert.getMemberid());
		//查询t_Ticket表以便更新流程步骤
		Ticket ticket = memberMapper.queryTicketByMemberId(memberCert.getMemberid());
		ticket.setPstep("checkemail");
		 int count=memberMapper.updateTicket(ticket);
		 Map<String, Object>variables=new HashMap<String,Object>();
		 variables.put(Const.FLAG, true);
		 variables.put(Const.PIID, ticket.getPiid());
		 variables.put(Const.LOGINACCT,member.getLoginacct() );
		 activitiService.nextPstep(variables);
		
		
	}

	@Override
	public int updateMemberByEmail(Member member) {
		//1.更新member数据库
		int count=0;
		 count =memberMapper.updateMemberByEmail(member);
		if (count==1) {
		//2.若用户更新成功，则根据memberid获取流程步骤
		//连续循环4次随机数，获取随机验证码
			StringBuilder authcode=new StringBuilder();
			for (int i = 1; i <= 4; i++) {
				authcode.append(new Random().nextInt(10));
			}
			Ticket ticket = memberMapper.queryTicketByMemberId(member.getId());
			ticket.setPstep("checkauthcode");
			ticket.setAuthcode(authcode.toString());
			count=memberMapper.updateTicketByAuthcodeAndPstep(ticket);
			Map<String, Object> variables=new HashMap<String,Object>();
			variables.put(Const.PIID, ticket.getPiid());
			variables.put(Const.FLAG, true);
			variables.put(Const.EMAIL, member.getEmail());
			variables.put(Const.AUTHCODE,authcode.toString());
			variables.put(Const.LOGINACCT, member.getLoginacct());
			activitiService.nextPstep(variables);
		}
	
		return count;
	
	}

	/*@Override
	public int checkAuthcode(Map<String, Object> paramMap) {
		int count=0;
		//获取参数中的验证码信息
		String authcode = (String) paramMap.get("authcode");
		//获取参数中的会员信息
		Object object= paramMap.get("loginMember");
		Member member=(Member) object;
		//根据会员memberid查询ticket流程表
		 //Member member=new Member();
		Ticket ticket = memberMapper.queryTicketByMemberId(member.getId());
		if (authcode.equals(ticket.getAuthcode())) {
			member.setAuthstatus("1");
			count=memberMapper.updateMemberByAuthstatus(member);
			Map<String, Object> variables=new HashMap<String ,Object>();
			variables.put(Const.LOGINACCT,member.getLoginacct());
			variables.put(Const.PIID, ticket.getPiid());
			activitiService.nextPstep(variables);
			return count;
			
		}
		
		return count;
	}*/


	@Override
	public int checkAuthcode(Map<String, Object> paramMap) {
		int count=0;
		//获取参数中的验证码信息
		
		//获取参数中的会员信息
		//根据会员memberid查询ticket流程表
		 //Member member=new Member();
		String authcode=(String) paramMap.get("authcode");
		Integer memberid=(Integer) paramMap.get("memberid");
		Ticket ticket = memberMapper.queryTicketByMemberId(memberid);
		Member member=memberMapper.queryMemberById(memberid);
		if (authcode.equals(ticket.getAuthcode())) {
			member.setAuthstatus("1");
			int count1=memberMapper.updateMemberByAuthstatus(member);
			Map<String, Object> variables=new HashMap<String ,Object>();
			variables.put(Const.LOGINACCT,member.getLoginacct());
			variables.put(Const.PIID, ticket.getPiid());
			activitiService.nextPstep(variables);
			count=1;
		}
			return count;
		
		

	}

/*	@Override
	public List<Map<String, Object>> queryPage(Map<String, Object> paramMap) {
		Integer startIndex=(Integer) paramMap.get("startIndex");
		Integer pagesize=(Integer) paramMap.get("pagesize");
		
		return null;
	}

	@Override
	public int querypageCount() {
		
		return memberMapper.querypageCount();
	}*/

	@Override
	public Member queryMemberByPiid(String piid) {

		return memberMapper.queryMemberByPiid(piid);
	}

	@Override
	public List<Map<String, Object>> queryCertByMemberId(Integer memberid) {
		// TODO Auto-generated method stub
		return memberMapper.queryCertByMemberId(memberid);
	}
	//第一种任务审核完成流程
	/*@Override
	public int updateMemberStatus(Map<String, Object> paramMap) {
		int count=0;
		int memberid=(int) paramMap.get("memberid");
		String taskid= (String) paramMap.get("taskid");
		Ticket ticket = memberMapper.queryTicketByMemberId(memberid);
		Member member=memberMapper.queryMemberById(memberid);
		member.setAuthstatus("2");
		count=memberMapper.updateMemberByAuthstatus(member);
		if (count==1) {
			Map<String, Object> variables=new HashMap<String,Object>();
			variables.put(Const.FLAG,true);
			variables.put("piid",ticket.getPiid());
			activitiService.nextPstepEnd(variables);
			return count;
		}
		
		return 0;
	}*/
	@Override
	public void updateMemberStatus(Map<String, Object> paramMap) {
		int memberid=(int) paramMap.get("memberid");
		String taskid= (String) paramMap.get("taskid");
		Ticket ticket = memberMapper.queryTicketByMemberId(memberid);
		/*Member member=memberMapper.queryMemberById(memberid);*/
		/*member.setAuthstatus("2");
		int count=memberMapper.updateMemberByAuthstatus(member);*/
			Map<String, Object> variables=new HashMap<String,Object>();
			variables.put(Const.FLAG,true);
			variables.put("memberid", memberid);
			variables.put("piid",ticket.getPiid());
			activitiService.nextPstepEnd(variables);
	}


	@Override
	public int updateStatusByMemberid(Member member) {
		// TODO Auto-generated method stub
		return memberMapper.updateMemberByAuthstatus(member);
	}

/*	@Override
	public List<Map<String, Object>> queryPage(Map<String, Object> paramMap) {
		// TODO Auto-generated method stub
		return null;
	}*/

	

}
