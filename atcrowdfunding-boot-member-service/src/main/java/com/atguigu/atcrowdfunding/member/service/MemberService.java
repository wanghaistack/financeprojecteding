package com.atguigu.atcrowdfunding.member.service;

import java.util.List;
import java.util.Map;

import com.atguigu.atcrowdfunding.bean.Cert;
import com.atguigu.atcrowdfunding.bean.Member;
import com.atguigu.atcrowdfunding.bean.MemberCert;
import com.atguigu.atcrowdfunding.bean.Ticket;

public interface MemberService {

	Member queryMember(Integer id);

	Member queryMemberByLoginacct(String loginacct);

	Ticket queryTicketByMemberId(Integer id);

	void saveTicket(Ticket ticket);

	void updateAccttype(Member loginMember);

	void updateLoginMember(Member loginMember);

	List<Cert> queryCertByAccttype(Member loginMember);

	void saveMemberCertList(List<MemberCert> certFiles);

	int updateMemberByEmail(Member member);

	/*int checkAuthcode(Map<String, Object> paramMap);*/

	//int checkAuthcode(String authcode, Integer id);

	int checkAuthcode(Map<String, Object> paramMap);

/*	List<Map<String, Object>> queryPage(Map<String, Object> paramMap);

	int querypageCount();*/

	Member queryMemberByPiid(String piid);

	List<Map<String, Object>> queryCertByMemberId(Integer memberid);

	void updateMemberStatus(Map<String, Object> paramMap);

	int updateStatusByMemberid(Member member);

}
