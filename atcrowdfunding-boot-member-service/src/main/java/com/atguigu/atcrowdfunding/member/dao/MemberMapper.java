package com.atguigu.atcrowdfunding.member.dao;

import com.atguigu.atcrowdfunding.bean.Cert;
import com.atguigu.atcrowdfunding.bean.Member;
import com.atguigu.atcrowdfunding.bean.MemberCert;
import com.atguigu.atcrowdfunding.bean.Ticket;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface MemberMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Member record);

    Member selectByPrimaryKey(Integer id);

    List<Member> selectAll();

    int updateByPrimaryKey(Member record);

	Member queryMemberByLoginacct(String loginacct);

	Ticket queryTicketByMemberId(Integer id);

	void saveTicket(Ticket ticket);

	int updateMember(Member loginMember);

	int updateTicket(Ticket ticket);

	int updateLoginMember(Member loginMember);

	List<Cert> queryCertByAccttype(Member loginMember);

	void saveMemberCertList(List<MemberCert> certFiles);

	Member queryMemberById(Integer memberid);

	int updateMemberByEmail(Member member);

	int updateTicketByAuthcodeAndPstep(Ticket ticket);

	int updateMemberByAuthstatus(Member member);

	int querypageCount();

	Member queryMemberByPiid(String piid);

	List<Map<String, Object>> queryCertByMemberId(Integer memberid);



}