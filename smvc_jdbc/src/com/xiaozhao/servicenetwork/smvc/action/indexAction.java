package com.xiaozhao.servicenetwork.smvc.action;

import java.io.IOException;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.xiaozhao.servicenetwork.framework.base.BaseAction;
import com.xiaozhao.servicenetwork.framework.constant.AttributeName;
import com.xiaozhao.servicenetwork.framework.logger.Slf4jUtil;
import com.xiaozhao.servicenetwork.framework.util.MD5Encryption;
import com.xiaozhao.servicenetwork.smvc.service.UserService;
@Controller
public class indexAction extends BaseAction{
	private Slf4jUtil logger = new Slf4jUtil(indexAction.class);
	@Resource
	private UserService userService;
	
	
	/**
	 * 初始化方法<br>
	 * 方 法 名：indexInit <br>
	 * 创 建 人：he <br>
	 * 创建时间：2017年7月18日 下午5:21:48 <br>
	 * 修 改 人： <br>
	 * 修改日期： <br>
	 * @param respo
	 * @throws SQLException
	 * @throws IOException void
	 */
	@RequestMapping(value="/indexSel")//指定一个url地址
	public void indexInit(HttpServletResponse respo) throws SQLException, IOException{
		
	}
	
	/**
	 * 验证码
	 * @param respo
	 */
	@RequestMapping(value="/YzmTest")
	public void YzmTest(HttpServletResponse respo,HttpServletRequest request){
		super.createRandomCodeImage(respo,request);//传入Response写回验证码图片到页面
	}
	
	@RequestMapping(value="/Login")
	public void LoginInit(HttpServletResponse respo,HttpServletRequest request,String loginname,String loginpwd,String loginYzm){
		//判断验证码是否正确
		if(super.checkRandomCode(loginYzm)){
			//判定长度
			if(userService.loginUser(loginname,MD5Encryption.MD5(loginpwd)).size()>0){
				//登录成功
				request.getSession().setAttribute(AttributeName.USER_LOGIN,loginname);
				getPrintWriter().write("true");
			}else{
				getPrintWriter().write("用户名或密码错误!");	
			}
		}else{
			getPrintWriter().write("验证码错误!");
		}
	}
	/**
	 * 跳转到首页
	 * @throws IOException 
	 */
	@RequestMapping(value="/welcom")
	public ModelAndView LoginWelCom(HttpServletRequest request){
		Object login = request.getSession().getAttribute(AttributeName.USER_LOGIN);
		if(login!=null){
			logger.log.debug(login.toString());
			return	new ModelAndView("welcom.jsp").addObject("loginName", login.toString());
		}
		return new ModelAndView("index.jsp");
	}
	
}
