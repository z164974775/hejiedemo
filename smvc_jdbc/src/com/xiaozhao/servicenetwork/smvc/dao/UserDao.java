package com.xiaozhao.servicenetwork.smvc.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.xiaozhao.servicenetwork.framework.base.BaseDao;
import com.xiaozhao.servicenetwork.framework.entity.UserAdmin;

@SuppressWarnings("rawtypes")
@Repository("userDao")
@Scope("singleton")
public class UserDao extends BaseDao{
	/**
	 * @param sql 查询语句
	 * @param list 代参查询,无参则传入null
	 * @param objClass 需要转换的对象class
	 * @return List返回相应对象集合
	 */
	public List loginUser(String loginname,String loginpwd){
		String sql = "SELECT * FROM USERADMIN WHERE USERID=? AND PWD=?";
		List<String> list = new ArrayList<String>();
		list.add(loginname);
		list.add(loginpwd);
		//返回查询结果集合
		return super.selObj(sql, list,UserAdmin.class);
	}
	/**
	 * 添加<br>
	 * 方 法 名：addUser <br>
	 * 创 建 人：he <br>
	 * 创建时间：2017年6月8日 下午2:10:09 <br>
	 * 修 改 人： <br>
	 * 修改日期： <br>
	 * @param param void
	 */
	public void addUser(UserAdmin param){
		super.insert(param);
	}
	/**
	 * 修改用户<br>
	 * 方 法 名：updateUser <br>
	 * 创 建 人：he <br>
	 * 创建时间：2017年7月18日 下午4:59:55 <br>
	 * 修 改 人： <br>
	 * 修改日期： <br>
	 * @param param void
	 */
	public void updateUser(UserAdmin param){
		super.update(param);
	}
	
}
