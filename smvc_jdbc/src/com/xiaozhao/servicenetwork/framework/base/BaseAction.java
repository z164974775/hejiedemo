package com.xiaozhao.servicenetwork.framework.base;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.alibaba.fastjson.JSON;
import com.xiaozhao.servicenetwork.framework.constant.AttributeName;
import com.xiaozhao.servicenetwork.framework.constant.IdentifyingCode;
import com.xiaozhao.servicenetwork.framework.entity.UserAdmin;
import com.xiaozhao.servicenetwork.framework.logger.Slf4jUtil;
import com.xiaozhao.servicenetwork.framework.util.MD5Encryption;
public class BaseAction {
	//logger日志
	private Slf4jUtil logger = new Slf4jUtil(BaseAction.class);
    //定义map对象
    protected Map<String, Object> resultMap = new HashMap<String, Object>();
    //注解request对象
    @Autowired
    protected  HttpServletRequest request;
    //注解session对象
    @Autowired  
    protected  HttpSession session;
    //注解context对象
    @Autowired  
    protected  ServletContext context;
    //定义response对象
    protected HttpServletResponse response;
    /**
     * 禁止实例化构造函数: <br>
     *  <br>
     */
	protected BaseAction() {
	    
	}	
	/**
	 * 获取response对象<br>
	 * 方 法 名：setReqAndRes <br>
	 * 创 建 人：he <br>
	 * 创建时间：2017年4月13日 下午2:48:24 <br>
	 * 修 改 人： <br>
	 * 修改日期： <br>
	 * @param response void
	 */
	@ModelAttribute
    public void setResponse(HttpServletResponse response){
	    this.response = response;
    }
	
	/**
	 * 获取HttpSession<br>
	 * 方 法 名：getSession <br>
	 * 创 建 人：he <br>
	 * 创建时间：2017年3月9日 上午11:20:05 <br>
	 * 修 改 人： <br>
	 * 修改日期： <br>
	 * @return HttpSession
	 */
	protected HttpSession getSession() {
		return session;
	}
	/**
	 * 获取HttpServletRequest<br>
	 * 方 法 名：getRequest <br>
	 * 创 建 人：he <br>
	 * 创建时间：2017年3月9日 上午11:20:19 <br>
	 * 修 改 人： <br>
	 * 修改日期： <br>
	 * @return HttpServletRequest
	 */
	protected HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * 获取HttpServletResponse<br>
	 * 方 法 名：getResponse <br>
	 * 创 建 人：he <br>
	 * 创建时间：2017年3月9日 上午11:20:51 <br>
	 * 修 改 人： <br>
	 * 修改日期： <br>
	 * @return HttpServletResponse
	 */
	protected HttpServletResponse getResponse() {
		return response;
	}
	/**
	 * 返回PrintWriter对象<br>
	 * 方 法 名：getPrintWriter <br>
	 * 创 建 人：he <br>
	 * 创建时间：2017年7月18日 下午5:23:15 <br>
	 * 修 改 人： <br>
	 * 修改日期： <br>
	 * @return PrintWriter
	 */
	protected PrintWriter getPrintWriter(){
		try {
			return getResponse().getWriter();
		} catch (Exception e) {}
		return null;
	}

	/**
	 * 获取ServletContext<br>
	 * 方 法 名：getApplication <br>
	 * 创 建 人：he <br>
	 * 创建时间：2017年3月9日 上午11:21:12 <br>
	 * 修 改 人： <br>
	 * 修改日期： <br>
	 * @return ServletContext
	 */
	protected ServletContext getApplication() {
		return context;
	}	

    /**
     * response写回字符串<br>
     * 方 法 名：strWrite <br>
     * 创 建 人：he <br>
     * 创建时间：2017年3月9日 上午11:21:34 <br>
     * 修 改 人： <br>
     * 修改日期： <br>
     * @param content void
     */
    protected void strWrite(String content) {
        PrintWriter pw = null;
        try {
            HttpServletResponse response = this.getResponse();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html");
            pw = response.getWriter();
            pw.write(content);
            pw.flush();
            pw = null;
        } catch (IOException e) {
            logger.error("", e);
        } finally {
            if (null != pw) {
                pw = null;
            }
        }
    }
    /**
     * 对象转换为json字符串写回<br>
     * 方 法 名：JsonWrite <br>
     * 创 建 人：he <br>
     * 创建时间：2017年3月9日 上午11:09:33 <br>
     * 修 改 人： <br>
     * 修改日期： <br>
     * @param Str void
     */
    protected void jsonWrite(Object obj){
        //将对象转换为json字符串
        String jsonStr = JSON.toJSONString(obj);
        //得到response
        HttpServletResponse response = getResponse();
        PrintWriter out = null;
        try {
            response.setContentType("text/html;charset=UTF-8");
            out = response.getWriter(); 
            //写回json字符串
            out.write(jsonStr);
            out.flush();
            out.close();
            out = null;
        } catch (Exception exception) {
            logger.error("", exception);
        } finally {
            if (null != out) {
                out.close();
                out = null;
            }
        }
    }
    /**
     * 获取登录用户<br>
     * 方 法 名：getLoginUser <br>
     * 创 建 人：he <br>
     * 创建时间：2017年3月17日 下午1:59:46 <br>
     * 修 改 人： <br>
     * 修改日期： <br>
     * @return User
     */
    protected UserAdmin getLoginUser(){
    	UserAdmin loginUser = (UserAdmin)getSession().getAttribute(AttributeName.USER_LOGIN);
        return loginUser;
    }
    /**
     * 返回成功结果<br>
     * 方 法 名：resultSuccess <br>
     * 创 建 人：he <br>
     * 创建时间：2017年3月21日 上午9:17:23 <br>
     * 修 改 人： <br>
     * 修改日期： <br>
     * @return Map<String,Object>
     */
    protected Map<String, Object> resultSuccess(){
        resultMap.put("success", true);//保存成功状态
        return resultMap;
    }
    /**
     * 自定义返回结果提示<br>
     * 方 法 名：resultMessage <br>
     * 创 建 人：he <br>
     * 创建时间：2017年3月21日 上午9:19:20 <br>
     * 修 改 人： <br>
     * 修改日期： <br>
     * @param map
     * @param message
     * @return Map<String,Object>
     */
    protected Map<String, Object> resultMessage(String message){
        resultMap.put("message", message);
        return resultMap;
    }
    /**
     * 系统异常结果<br>
     * 方 法 名：resultError <br>
     * 创 建 人：he <br>
     * 创建时间：2017年3月21日 上午9:19:51 <br>
     * 修 改 人： <br>
     * 修改日期： <br>
     * @param map
     * @return Map<String,Object>
     */
    protected Map<String, Object> resultError(){
        resultMap.put("success", false);//保存失败状态
        resultMap.put("message", "系统异常,请联系管理员.");//保存异常信息
        return resultMap;
    }
    /**
     * 操作成功写回map结果<br>
     * 方 法 名：jsonWriteSucess <br>
     * 创 建 人：he <br>
     * 创建时间：2017年3月21日 上午9:30:11 <br>
     * 修 改 人： <br>
     * 修改日期： <br> void
     */
    protected void jsonWriteSucess(){
        jsonWrite(resultMap);
    }
    
    /**
	 * 写回验证码图片到response
	 * Creator:hejie
	 * Datetime:2015-6-8
	 * Remarks:
	 * Return:
	 */
	public void createRandomCodeImage(HttpServletResponse response,HttpServletRequest request){
		String stryzm = null;//验证码
		//设置不缓存图片  
        response.setHeader("Pragma", "No-cache");  
        response.setHeader("Cache-Control", "No-cache");  
        response.setDateHeader("Expires", 0);
        //设置为二进制流类型
        response.setContentType("multipart/form-data");
      //指定生成的相应图片   
        IdentifyingCode idCode = new IdentifyingCode();  
        BufferedImage image =new BufferedImage(idCode.getWidth() , idCode.getHeight() , BufferedImage.TYPE_INT_BGR) ;  
        Graphics2D g = image.createGraphics() ;  
        //定义字体样式  
        Font myFont = new Font("微乳雅黑", Font.BOLD ,18);  
        //设置字体  
        g.setFont(myFont) ;  
        g.setColor(idCode.getRandomColor(200 , 250));  
        //绘制背景  
        g.fillRect(0, 0, idCode.getWidth() , idCode.getHeight());  
        g.setColor(idCode.getRandomColor(190, 250));  
        idCode.drawRandomLines(g, 160) ;  
        stryzm = idCode.drawRandomString(4, g);//获得产生验证码
        request.getSession().setAttribute(AttributeName.STR_YZM,MD5Encryption.MD5(stryzm));//将加密后验证码存入session 键为stryzm
        g.dispose();             
        try {
        	ImageIO.write(image, "JPEG",response.getOutputStream());//写入到response输出流      		
		} catch (IOException e) {
			logger.error(e);
			e.printStackTrace();
		}
        
	}
	/**
	 * 验证码对比<br>
	 * 方 法 名：checkRandomCode <br>
	 * 创 建 人：he <br>
	 * 创建时间：2017年7月19日 下午2:16:30 <br>
	 * 修 改 人： <br>
	 * 修改日期： <br>
	 * @param stryzm
	 * @param request
	 * @return boolean
	 */
    public  boolean checkRandomCode(String stryzm){
    	boolean boo = false;
    	if(!"".equals(stryzm)&&stryzm!=null){
    		String yzm = request.getSession().getAttribute(AttributeName.STR_YZM).toString();//取得session中加密的验证码
			stryzm = MD5Encryption.MD5(stryzm);//加密用户输入的验证码
			if(yzm.equals(stryzm)){//对比
				boo = true;
			}
    	}
    	return boo;
    }    
    
}
