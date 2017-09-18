package com.xiaozhao.servicenetwork.smvc.service.impl;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.xiaozhao.servicenetwork.framework.entity.UserAdmin;
import com.xiaozhao.servicenetwork.smvc.dao.UserDao;
import com.xiaozhao.servicenetwork.smvc.service.UserService;

@Service("userServiceImpl")
@Scope("prototype")
@SuppressWarnings("unchecked")
public class UserServiceImpl implements UserService{
	@Autowired
    @Qualifier("userDao")
	private UserDao userDao = null;
	
	
	@Override
	public List<UserAdmin> loginUser(String loginname,String loginpwd) {
		return userDao.loginUser(loginname, loginpwd);
	}


}
