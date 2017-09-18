package com.xiaozhao.servicenetwork.framework.entity;

import java.io.Serializable;
import java.util.Date;

import com.xiaozhao.servicenetwork.framework.conn.FieldTag;

/**
 * 注意:类名和字段名必须与数据库保持一致 (大小写可以随意 不用保持一致)
 * [概 要] <测试实体类><br/>
 * [环 境] J2EE 1.7
 * 创建时间：2017年6月9日 上午9:27:05 <br>
 * @author he
 * @version 1.0
 */
public class UserAdmin implements Serializable{
	private static final long serialVersionUID = 1L;
	@FieldTag(pk = true,rdUUID = true) //标注该数据库字段为主键 且自动生成32位UUID
	private String userId;//用户ID
	private String name;//用户名称
	private String pwd;//用户密码
	private Date createTime;//创建时间
	
	
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
}
