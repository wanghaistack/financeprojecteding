package com.atguigu.atcrowdfunding.bean;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

public class MemberCert implements Serializable {
    private Integer id;

    private Integer memberid;

    private Integer certid;

    private String iconpath;
    
    private MultipartFile file;
    

    public MultipartFile getFile() {
		return file;
	}

	public void setFile(MultipartFile file) {
		this.file = file;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMemberid() {
        return memberid;
    }

    public void setMemberid(Integer memberid) {
        this.memberid = memberid;
    }

    public Integer getCertid() {
        return certid;
    }

    public void setCertid(Integer certid) {
        this.certid = certid;
    }

    public String getIconpath() {
        return iconpath;
    }

    public void setIconpath(String iconpath) {
        this.iconpath = iconpath == null ? null : iconpath.trim();
    }
}