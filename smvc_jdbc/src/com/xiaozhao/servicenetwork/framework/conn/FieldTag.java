package com.xiaozhao.servicenetwork.framework.conn;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * FieldTag概要说明：自定义标签
 * @author pengqunfei
 */
@Target(value = { ElementType.FIELD })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface FieldTag {
	/**
	 * cName方法慨述:属性的中文名
	 * @return String
	 */
	String cName() default "";

	/**
	 * pk方法慨述:是否主键， 默认false。false不为主键，true主键
	 * @return boolean
	 */
	boolean pk() default false;
	/**
	 * rd方法慨述:是否自动生成UUID， 默认false。false不生成，true自动生成
	 * @return boolean
	 */
	boolean rdUUID() default false;

}
