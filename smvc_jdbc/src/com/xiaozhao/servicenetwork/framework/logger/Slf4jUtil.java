package com.xiaozhao.servicenetwork.framework.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * [概 要] <Slf4j logger工具类><br/>
 * [环 境] J2EE 1.7
 * 创建时间：2017年5月9日 下午3:59:03 <br>
 * @author he
 * @version 1.0
 */
public class Slf4jUtil {
	//定义logger对象
	public Logger log;
	//构造
	public Slf4jUtil(Class<?> t){
		log = LoggerFactory.getLogger(t);
	}
	//抛出异常信息
	public void error(Throwable e){
		StringBuffer buffer = new StringBuffer();
		buffer.append(e);
		StackTraceElement [] messages=e.getStackTrace();
		for(int i=0;i<messages.length;i++){
		    buffer.append("\n\tat "+messages[i].toString());
		}
		log.error(buffer.toString());
	}
	//抛出自定义信息和异常信息
	public void error(String message,Throwable e){
		StringBuffer buffer = new StringBuffer();
		buffer.append(message);
		buffer.append("\n\t"+e);
		StackTraceElement [] messages=e.getStackTrace();
		for(int i=0;i<messages.length;i++){
		    buffer.append("\n\tat "+messages[i].toString());
		}
		log.error(buffer.toString());
	}
	
	
}
