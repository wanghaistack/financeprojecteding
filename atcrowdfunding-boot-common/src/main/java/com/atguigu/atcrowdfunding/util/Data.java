package com.atguigu.atcrowdfunding.util;

import java.util.List;

import com.atguigu.atcrowdfunding.bean.Member;
import com.atguigu.atcrowdfunding.bean.MemberCert;

public class Data {
	private List<Integer> ids;
	private List<Member> datas;
	private List<MemberCert> certFiles;
	

	public List<MemberCert> getCertFiles() {
		return certFiles;
	}

	public void setCertFiles(List<MemberCert> certFiles) {
		this.certFiles = certFiles;
	}

	public List<Integer> getIds() {
		return ids;
	}

	public void setIds(List<Integer> ids) {
		this.ids = ids;
	}

	public List<Member> getDatas() {
		return datas;
	}

	public void setDatas(List<Member> datas) {
		this.datas = datas;
	}

}
