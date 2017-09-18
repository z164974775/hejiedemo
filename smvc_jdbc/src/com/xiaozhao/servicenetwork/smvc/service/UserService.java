package com.xiaozhao.servicenetwork.smvc.service;

import java.util.List;
import com.xiaozhao.servicenetwork.framework.entity.UserAdmin;


public interface UserService {
	/**
	 * 
	 * @param sql 查询语句
	 * @param list 代参查询,无参则传入null
	 * @param objClass 需要转换的对象class
	 * @return List返回相应对象集合
	 */
	public List<UserAdmin> loginUser(String loginname,String loginpwd);
}
	
