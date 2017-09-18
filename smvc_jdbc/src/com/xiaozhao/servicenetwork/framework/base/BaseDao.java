package com.xiaozhao.servicenetwork.framework.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

import com.xiaozhao.servicenetwork.framework.conn.FieldTag;
import com.xiaozhao.servicenetwork.framework.entity.UserAdmin;
import com.xiaozhao.servicenetwork.framework.logger.Slf4jUtil;

import oracle.sql.BLOB;

/**
 * [概 要] <Dao数据访问处理类><br/>
 * [环 境] J2EE 1.7
 * 创建时间：2017年6月14日 上午10:47:23 <br>
 * @author he
 * @version 1.0
 */
@Repository("baseDao")
@Scope("singleton")
@SuppressWarnings({ "rawtypes", "unused", "deprecation" })
public class BaseDao {
	//定义logger日志
	private Slf4jUtil logger = new Slf4jUtil(BaseDao.class);
	//加载spring配置
	BeanFactory beanfactory = new XmlBeanFactory(new ClassPathResource(
            "applicationContext.xml"));
	//获取事物管理的datasource
	private DataSource datasource = beanfactory.getBean("dataSource", DataSource.class);
	PreparedStatement pstmt = null;
	Connection con =null;
	/**
	 * 获取数据库连接
	 * @return Connection     
	 */
	public Connection getCon(){
		try {
			
			if(con==null||con.isClosed()){
				con = datasource.getConnection();
			}
		} catch (SQLException e) {
			logger.error(e);
			e.printStackTrace();
		}
		return con;
	}
	
	/**
     * 执行SQL语句，可以进行增、删、改的操作，不能执行查询
     * @param sql  预编译的 SQL 语句
     * @param param  预编译的 SQL 语句中的‘？’参数的字符串数组
     * @return 影响的条数
	 * @throws SQLException 
     */
	public int executeSQL(String preparedSql,List paramlist) {
    	Connection   con = null;
        int               num   = 0;
        StringBuffer strbuffer = new StringBuffer();
        /*  处理SQL,执行SQL  */
        try {
            con  = getCon();//获得连接
            pstmt = con.prepareStatement(preparedSql);    // 得到PreparedStatement对象
            if( paramlist != null ) {
                for( int i = 0; i < paramlist.size(); i++ ) {
                    pstmt.setString(i+1,paramlist.get(i).toString());         // 为预编译sql设置参数
                    strbuffer.append(paramlist.get(i).toString()).append(",");
                }
            }
            num = pstmt.executeUpdate();                   // 执行SQL语句
            logger.log.debug("执行的SQL:"+preparedSql+"\n\t参数:"+strbuffer.toString());
        } catch (SQLException e) {
        	logger.error(e);
            e.printStackTrace();                            // 处理SQLException异常
        }
        return num;
    }
	/**
	 * 新增<br>
	 * 方 法 名：insert <br>
	 * 创 建 人：he <br>
	 * 创建时间：2017年6月8日 下午3:56:52 <br>
	 * 修 改 人： <br>
	 * 修改日期： <br>
	 * @param obj void
	 * @throws SQLException 
	 */
	public void insert(Object obj){
		/*  处理SQL,执行SQL  */
        try {
        	// 操作SQL
    		StringBuffer addSql = new StringBuffer();
    		StringBuffer valueSql = new StringBuffer();			
    		addSql.append("insert into ");
    		valueSql.append("values(");
    		String tabName = obj.getClass().getSimpleName();
    		addSql.append(tabName + "(");
    		Field[] fields = obj.getClass().getDeclaredFields();
    		// 添加数据字段
    		List<String> fieldTempList = new ArrayList<String>();
    		// 添加字段对应的值
    		List<String> valueTempList = new ArrayList<String>();
    		// 主键字段
    		List<String> pkFileList = new ArrayList<String>();
    		List<String> pkValueList = new ArrayList<String>();
    		// 保存数据类型为blob类型的字段
    		List<String> blobFiledList = new ArrayList<String>();
    		List<Blob> blobValueList = new ArrayList<Blob>();
    		// 保存时间类型字段
    		List<String> dateFiledList = new ArrayList<String>();
    		List<Date> dateValueList = new ArrayList<Date>();
    		for (Field field : fields) {
	    			//判断是否是静态变量
					boolean isStatic = Modifier.isStatic(field.getModifiers());
			        if(isStatic) {
			            continue;
			        }
			        //设置可操作私有变量
			        field.setAccessible(true);
			        if(isRandomUUID(field)){
			        	//需要自动生成UUID
			        	UUID uuid=UUID.randomUUID();
			        	//将uuid转为字符串
			            String str = uuid.toString(); 
			            //去除-并转换为大写
			            String uuidStr=str.replace("-", "").toUpperCase();
			            //保存字段
			            fieldTempList.add(field.getName());
			            valueTempList.add(uuidStr);
			            //执行下一次循环
			            continue;
			        }
    				// 得到该字段数据
    				Object fieldValueObj = field.get(obj);
    				// 得到该字段数据库对应字段
    				String fieldDB = field.getName();
    				// 得到数据类型
    				String dataType = field.getType().getName();
    				//判断是否是大数据类型
    				if (dataType.equals("java.sql.Blob")) {
    					if (null != fieldValueObj) {
    						blobFiledList.add(fieldDB);
    						blobValueList.add((Blob) fieldValueObj);
    					}
    				} else if (dataType.equals("java.util.Date")) {//判断时间类型
    					if (null != fieldValueObj) {
    						dateFiledList.add(fieldDB);
    						dateValueList.add((Date) fieldValueObj);
    					}
    				} else {
    					//默认是字符串类型
    					if (null != fieldValueObj) {
    						valueTempList.add(fieldValueObj.toString());
    					} else {
    						valueTempList.add("");
    					}
    					fieldTempList.add(fieldDB);
    				}		
    			
    		}
    		//得到连接信息
    		DatabaseMetaData datame = getCon().getMetaData();
    		//得到数据库类型名称
    		String dbname = datame.getDatabaseProductName().toUpperCase();
    		// 普通类型SQL
    		for (int i = 0; i < fieldTempList.size(); i++) {
    			addSql.append(fieldTempList.get(i));
    			valueSql.append("?");
    			addSql.append(",");
    			valueSql.append(",");
    		}

    		// Date类型SQL
    		if (null != dateFiledList && dateFiledList.size() > 0) {
    			for (int i = 0; i < dateFiledList.size(); i++) {
    				addSql.append(dateFiledList.get(i));
    				if (dbname.equals("ORACLE")) {
    					valueSql.append("?");
    				} else {
    					valueSql.append("?");
    				}
    				addSql.append(",");
    				valueSql.append(",");
    			}
    		}
    		// 大数据类型SQL
    		for (int i = 0; i < blobFiledList.size(); i++) {
    			addSql.append(blobFiledList.get(i));
    			// 如果是mysql就直接赋值，如果是oracle那么使用oracle的empty_blob()函数添加空值
    			if (dbname.equals("MYSQL") || dbname.equals("MICROSOFT SQL SERVER")) {
    				valueSql.append("?");
    			} else if (dbname.equals("ORACLE")) {
    				valueSql.append("empty_blob()");
    			}
    			addSql.append(",");
    			valueSql.append(",");
    		}
    		// 去掉循环最后一个逗号
    		addSql = addSql.deleteCharAt(addSql.length() - 1);
    		valueSql = valueSql.deleteCharAt(valueSql.length() - 1);
    		addSql.append(") ");
    		valueSql.append(")");
    		addSql.append(valueSql.toString());
    		// 预编译sql
    		pstmt = getCon().prepareStatement(addSql.toString().toUpperCase());
    		// 普通参数赋值
    		setParam(pstmt, valueTempList, 1);
    		// Date类型
    		setDateParam(pstmt, dateValueList, valueTempList.size() + 1);
    		// 大数据类型赋值
    		setBlobParam(pstmt, blobValueList, dateValueList.size() + valueTempList.size() + 1);
    		// 执行新增sql语句
    		pstmt.executeUpdate();
    		//将执行sql语句存入logger日志
    		logger.log.debug(addSql.toString().toUpperCase());
    		if (datame.getDatabaseProductName().equals("Oracle")) {
    			// 执行完毕后，如果是oracle，需要对oracle的大数据类型进行赋值
    			if (null != blobFiledList && blobFiledList.size() > 0) {
    				StringBuffer blobSql = new StringBuffer("select ");
    				for (int i = 0; i < blobFiledList.size(); i++) {
    					blobSql.append(blobFiledList.get(i));
    					if (i + 1 != blobFiledList.size()) {
    						blobSql.append(",");
    					}
    				}
    				blobSql.append(" from ");
    				blobSql.append(tabName);
    				if (null != pkFileList && pkFileList.size() > 0) {
    					blobSql.append(" where 1 = 1 and ");
    				}
    				for (int i = 0; i < pkFileList.size(); i++) {
    					blobSql.append(pkFileList.get(i) + " = ? ");
    					if (i + 1 != pkFileList.size()) {
    						blobSql.append(" and ");
    					}
    				}
    				blobSql.append(" for update");
    				pstmt = getCon().prepareStatement(blobSql.toString().toUpperCase());
    				setParam(pstmt, pkValueList, 1);
    				ResultSet rs = pstmt.executeQuery();
    				if (rs.next()) {
    					//遍历blob类型值
    					for (int i = 0; i < blobValueList.size(); i++) {
    						//得到遍历值
    						Blob blobObj = blobValueList.get(i);
    						oracle.sql.BLOB blob = (BLOB) rs.getBlob(i + 1);   	
    						//得到输出流
    						OutputStream outStream = blob.getBinaryOutputStream();
    						//得到输入流
    						InputStream fin = blobObj.getBinaryStream();
    						byte[] by = new byte[blob.getBufferSize()];
    						int len = 0;
    						while ((len = fin.read(by)) != -1) {
    							outStream.write(by, 0, len);
    						}
    						//刷新关闭流
    						fin.close();
    						outStream.flush();
    						outStream.close();
    					}
    				}
    			}
    		}
        } catch (Exception e) {
        	logger.error(e);
            e.printStackTrace();                            // 抛出SQLException异常
        }
	}
	/**
	 * 自动匹配不为空的值得字段进行查询(字符串,数字,日期(yyyy-MM-dd)等类型)<br>
	 * 方 法 名：select <br>
	 * 创 建 人：he <br>
	 * 创建时间：2017年7月19日 下午2:33:27 <br>
	 * 修 改 人： <br>
	 * 修改日期： <br>
	 * @param obj
	 * @return List
	 */
	public List select(Object obj){
		StringBuffer sql = new StringBuffer();
		String tabName = obj.getClass().getSimpleName();
		sql.append("SELECT * FROM ").append(tabName).append(" WHERE 1 = 1 ");
		List<Object> params = new ArrayList<Object>();
		//得到所有字段
		Field[] fields = obj.getClass().getDeclaredFields();
		try {
			for (Field field : fields) {
				//判断是否是静态变量
				boolean isStatic = Modifier.isStatic(field.getModifiers());
		        if(isStatic) {
		            continue;
		        }
		        //设置可操作私有变量
		        field.setAccessible(true);
		        //获取字段值
		        Object value = field.get(obj);
		        //获取字段名称
		        String fieldName = field.getName();
		        if(value!=null&&!"".equals(value)){
		        	//时间类型
		        	if(field.getType().getName().equals("java.util.Date")){
		        		//得到连接信息
		        		DatabaseMetaData datame = getCon().getMetaData();
		        		//得到数据库类型名称
		        		String dbname = datame.getDatabaseProductName().toUpperCase();
		        		if(dbname.equals("ORACLE")){
			        		//ORACLE数据库
		        			sql.append(" AND ").append("to_char("+fieldName+", 'yyyy-mm-dd') = ?");
		        		}else{
		        			//mysql数据库或其他
			        		sql.append(" AND ").append("DATE_FORMAT("+fieldName+",'%Y-%m-%d') "+" = DATE_FORMAT(?,'%Y-%m-%d')");
		        		}
		        		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		        		String dataStr = sdf.format(value);
		        		params.add(dataStr);
		        	}else{
		        		//普通数据类型
			        	sql.append(" AND ").append(fieldName+" = ?");	
			        	params.add(value);
		        	}
		        }
			}
			ResultSet rs = null;
	    	PreparedStatement pstmt = null;
	    	Connection con = getCon();
	    	pstmt = con.prepareStatement(sql.toString());
	    	for (int i = 0; i < params.size(); i++) {
	    		pstmt.setString(i+1, params.get(i).toString());
			}
	    	rs = pstmt.executeQuery();
	    	List resultList = TransformationObj(rs, obj.getClass());
	    	return resultList;
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 删除对象(List<Object> OR Object 需要主键标识)<br>
	 * 方 法 名：select <br>
	 * 创 建 人：he <br>
	 * 创建时间：2017年7月19日 下午2:33:27 <br>
	 * 修 改 人： <br>
	 * 修改日期： <br>
	 * @param obj
	 * @return List
	 */
	@SuppressWarnings("unchecked")
	public void delete(Object obj){
		try {
			List<Object> list = new ArrayList<Object>();
			//判断集合
			if(obj.getClass().getName().equals("java.util.List")||obj.getClass().getName().equals("java.util.ArrayList")){
				 //集合
				 list = (List<Object>)obj;
			}else{
				//单个对象
				list.add(obj);
			}
			//定义sql字符串
			StringBuffer sql = new StringBuffer();
			//定义参数集合
			List<Object> paramList = new ArrayList<Object>();
			//得到表名
			String tabName = list.get(0).getClass().getSimpleName();
			sql.append("DELETE FROM ").append(tabName).append(" WHERE 1 = 1 ");
			boolean boo = false;
			for (Object object : list) {
				Field[] fields = object.getClass().getDeclaredFields();
				for (Field field : fields) {
					//判断是否是静态变量
					boolean isStatic = Modifier.isStatic(field.getModifiers());
			        if(isStatic) {
			            continue;
			        }
			        //设置可操作私有变量
			        field.setAccessible(true); 
			        if(isPK(field)){
			        	if(!boo){
			        		sql.append(" AND "+field.getName()+" in (?");
			        	}
			        	if(boo){
			        		sql.append(",?");
			        	}
			        	paramList.add(field.get(object));			        	
			        	boo = true;
			        	break;
			        }
				}
			}
			if(!boo){
				logger.log.debug("未找到删除对象的主键标识");
			}
			sql.append(")");
			PreparedStatement pstmt = null;
	    	Connection con = getCon();
	    	pstmt = con.prepareStatement(sql.toString());
	    	for (int i = 0; i < paramList.size(); i++) {
	    		pstmt.setString(i+1, paramList.get(i).toString());
			}
	    	pstmt.executeUpdate();
	    	logger.log.debug(sql.toString());
		} catch (Exception e) {
			logger.error(e);
		}
		
		
	}
	/**
	 * 执行修改(默认修改obj不为空的值)<br>
	 * 方 法 名：update <br>
	 * 创 建 人：he <br>
	 * 创建时间：2017年6月8日 下午4:24:05 <br>
	 * 修 改 人： <br>
	 * 修改日期： <br>
	 * @param obj
	 * @param param void
	 * @throws SQLException 
	 */
	public void update(Object obj){
		String paramKey = "";
		String paramValue = "";
		StringBuffer updateSql = new StringBuffer("UPDATE ");
		updateSql.append(obj.getClass().getSimpleName()+" SET ");
		// 修改数据库字段
		List<String> fieldTempList = new ArrayList<String>();
		List<String> valueTempList = new ArrayList<String>();
		// 保存数据类型为blob类型的字段
		List<String> blobFiledList = new ArrayList<String>();
		List<Blob> blobValueList = new ArrayList<Blob>();
		// 保存时间类型字段
		List<String> dateFiledList = new ArrayList<String>();
		List<Date> dateValueList = new ArrayList<Date>();
		try {
			//获取所有字段
			Field[] fields = obj.getClass().getDeclaredFields();
			for (Field field : fields) {
				//判断是否是静态变量
				boolean isStatic = Modifier.isStatic(field.getModifiers());
		        if(isStatic) {
		            continue;
		        }
		        //设置可操作私有变量
		        field.setAccessible(true); 
		        if(isPK(field)){
		        	//该字段是主键
		        	paramKey = field.getName(); //得到字段名
		        	paramValue = field.get(obj).toString();
		        	continue;
		        }
				String dataType = field.getType().getName();
				String fieldDB = field.getName();
				Object fieldValueObj = field.get(obj);
				if(fieldValueObj!=null){
					if (dataType.equals("java.sql.Blob")) {
						// 大数据类型字段单独存储处理
						blobFiledList.add(fieldDB);
						blobValueList.add((Blob) fieldValueObj);
					} else if (dataType.equals("java.util.Date")) {
						if (null != fieldValueObj) {
							dateFiledList.add(fieldDB);
							dateValueList.add((Date) fieldValueObj);
						}
					} else {
						valueTempList.add(fieldValueObj.toString());
						fieldTempList.add(fieldDB);
					}
				}
			}
			// 普通类型组装sql语句
			for (int i = 0; i < fieldTempList.size(); i++) {
				updateSql.append(fieldTempList.get(i) + "=?");
				updateSql.append(",");
			}
			// Date类型组装sql语句
			for (int i = 0; i < dateFiledList.size(); i++) {
				updateSql.append(dateFiledList.get(i) + "=?");
				updateSql.append(",");
			}
			//获取连接信息
			DatabaseMetaData datame = getCon().getMetaData();
			//获取数据库名称
			String dbName = datame.getDatabaseProductName().toUpperCase();
			// 组装大数据类型sql
			for (int i = 0; i < blobFiledList.size(); i++) {
				updateSql.append(blobFiledList.get(i) + "=");
				// 如果是mysql就直接赋值，如果是oracle那么使用oracle的empty_blob()函数添加空值
				if (dbName.equals("MYSQL")) {
					updateSql.append("?");
				} else if (dbName.equals("ORACLE")) {
					updateSql.append("empty_blob()");
				}
				updateSql.append(",");
			}
			// 去掉循环最后一个逗号
			updateSql = updateSql.deleteCharAt(updateSql.length() - 1);
			if(paramKey!=null){
				updateSql.append(" WHERE ");
				updateSql.append(paramKey+"=?");
			}
			//获取数据库连接
			Connection con  = getCon();
            pstmt = con.prepareStatement(updateSql.toString().toUpperCase());    // 得到PreparedStatement对象
            
            if( valueTempList != null ) {
            	//遍历所有字符串值
                for( int i = 0; i < valueTempList.size(); i++ ) {
                    pstmt.setString(i+1,valueTempList.get(i).toString());         // 为预编译sql设置参数
                }
            }
            if( dateValueList != null ) {
            	//遍历所有date类型值
                for( int i = 0; i < dateValueList.size(); i++ ) {
                    pstmt.setTimestamp(valueTempList.size()+i+1, new java.sql.Timestamp(dateValueList.get(i).getTime()));
                }
            }
            if( blobValueList != null ) {
            	//遍历所有blob类型值
                for( int i = 0; i < blobValueList.size(); i++ ) {
                    pstmt.setBlob(valueTempList.size()+dateValueList.size()+i+1,blobValueList.get(i));       // 为预编译sql设置参数
                }
            }
            if(paramKey!=null){
            	//设置主键标识值
            	pstmt.setString(valueTempList.size()+dateValueList.size()+blobValueList.size()+1, paramValue);
            }
            //执行语句
            pstmt.executeUpdate();   
            //保存修改语句
            logger.log.debug(updateSql.toString().toUpperCase());
		} catch (Exception e) {
			logger.error(e);
            e.printStackTrace();                            // 处理SQLException异常
        }
	}
	
    
    
    /**
     * 释放资源
     * @param conn 数据库连接
     * @param pstmt PreparedStatement对象
     * @param rs 结果集
     * 
     * PreparedStatement 预编译
     * ResultSet 结果集
     */
    public void closeAll( Connection con, PreparedStatement pstmt, ResultSet rs ) 
    {
        /*  如果rs不空，关闭rs  */
        if(rs != null){
            try { rs.close();} catch (SQLException e) {e.printStackTrace();}
        }
        /*  如果pstmt不空，关闭pstmt  */
        if(pstmt != null){
            try { pstmt.close();} catch (SQLException e) {e.printStackTrace();}
        }
        /*  如果conn不空，关闭conn  */
        if(con != null){
            try { con.close();} catch (SQLException e) {e.printStackTrace();}
        }
    }
    
    /**
     * sql查询
     * @param sql 数据查询语句
     * @param List 代参查询,无参则传入null
     * 
     * PreparedStatement 预编译
     * ResultSet 结果集
     * @throws SQLException 
     */
	public ResultSet selObj(String sql,List list){
    	ResultSet rs = null;
    	PreparedStatement pstmt = null;
    	Connection con = getCon();
    	if(sql!=null&&!"".equals(sql)){//判定传入sql是否为空
    		try {
				pstmt = con.prepareStatement(sql.toUpperCase());
				StringBuffer paramStr = new StringBuffer();
				if(list!=null){
					for (int i = 0; i < list.size(); i++) {
						pstmt.setString(i+1,list.get(i).toString());//赋值参数
						paramStr.append(list.get(i).toString()+",");
					}
				}
				rs = pstmt.executeQuery();//执行查询获取返回值
				logger.log.debug("执行SQL:"+sql.toUpperCase()+"\n\t参数:"+paramStr);
			} catch (Exception e) {
				closeAll(con,pstmt,rs);//释放资源
			}
    	}
    	return rs;
    }
    /**
     * 执行查询并将结果转换为对象<br>
     * 方 法 名：selObj <br>
     * 创 建 人：he <br>
     * 创建时间：2017年6月8日 下午5:25:04 <br>
     * 修 改 人： <br>
     * 修改日期： <br>
     * @param sql
     * @param paramList
     * @param objClass
     * @return List<?>
     */
	public List selObj(String sql,List paramList,Class<?> objClass){
    	ResultSet rs = selObj(sql,paramList);
    	return TransformationObj(rs,objClass);
    }
    
    /**
     * 分页查询  <br>
     * 方 法 名：queryPageRelative <br>
     * 创 建 人：he <br>
     * 创建时间：2017年6月14日 上午10:25:50 <br>
     * 修 改 人： <br>
     * 修改日期： <br>
     * @param sql
     * @param param
     * @param page
     * @param limit
     * @return ResultSet
     */
	private ResultSet queryPageRelative(String sql, List param, 
            int page,int limit){ 
    	ResultSet rs = null;
    	try {
    		StringBuffer finalSql = new StringBuffer(); 
    		// 组装该sql的分页语句
    		DatabaseMetaData dbMa = getCon().getMetaData();
			// 数据库名字
			String dbName = dbMa.getDatabaseProductName().toUpperCase();	
			//判断数据库
			if (dbName.equals("MYSQL")) {
				finalSql.append(sql);
				finalSql.append(" limit ");
				finalSql.append(getStartIndex(page,limit));
				finalSql.append(",");
				finalSql.append(getEndIndex(page, limit, "mysql"));
			} else if (dbName.equals("ORACLE")) {
				int startIndex = 0;
				int endIndex = 0;
				startIndex = getStartIndex(page,limit);
				endIndex = getEndIndex(page, limit, "oracle");
				finalSql.append("select * from");
				finalSql.append(" (SELECT a.*, ROWNUM rn FROM (");
				finalSql.append(sql);
				finalSql.append(") a ) b");
				finalSql.append(" WHERE rn >=");
				finalSql.append(startIndex);
				finalSql.append(" and rn <=");
				finalSql.append(endIndex);
			}
			//保存sql日志
			logger.log.debug(finalSql.toString());
			return selObj(finalSql.toString(), param);
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
        return rs;  
    }
	/**
	 * 分页查询数据<br>
	 * 方 法 名：queryPage <br>
	 * 创 建 人：he <br>
	 * 创建时间：2017年6月14日 上午10:26:07 <br>
	 * 修 改 人： <br>
	 * 修改日期： <br>
	 * @param sql	查询语句
	 * @param param	参数
	 * @param page	当前页码
	 * @param limit 每页显示条数
	 * @param objClass 结果转换类型
	 * @return List 返回结果
	 */
	public List queryPage(String sql, List param, int page,int limit,Class<?> objClass){
		ResultSet rs = queryPageRelative(sql, param, page, limit);
		//将分页查询数据转换为指定对象
		return TransformationObj(rs, objClass);
	}
	
    /**
     * 分页总数<br>
     * 方 法 名：queryPage <br>
     * 创 建 人：he <br>
     * 创建时间：2017年6月8日 下午2:53:53 <br>
     * 修 改 人： <br>
     * 修改日期： <br>
     * @param sql
     * @param page
     * @param limit
     * @return Object[] 0数据 1数据总条数
     */
    public int queryPageCount(String sql,List param){
    	ResultSet rs = selObj(sql,param);
    	try {
    		//返回查询结果
			return Integer.parseInt((String) rs.getObject(1));
		} catch (SQLException e) {
			logger.error(e);
		}
    	return 0;
    }
    
    
    /**
     * 将ResultSet对象转换为List集合<Object>
     * @author HJ
     * @param rs结果集
     * @param objClass转换对象的class
     */
        public List TransformationObj(ResultSet rs,
                Class<?> objClass)
        {
            /**存储转化后的实体类*/
            List<Object> listObjs = new ArrayList<Object>();
            /**resultSet数据表中的字段名称*/
            String[] columnNames = null;
            /**resultSet数据表中对应字段的数据类型*/
            String[] columnTypes = null;
            try
            {
                if (rs == null)
                {
                    return listObjs;
                } else
                {
                	//得到查询出的数据
                    ResultSetMetaData metaResult = rs.getMetaData();
                    int length = metaResult.getColumnCount();
                    columnNames = new String[length];
                    columnTypes = new String[length];
                    for (int i = 0; i < columnNames.length; i++)
                    {
                    	String columnName = metaResult.getColumnName(i + 1);
                        columnNames[i] = columnName.toLowerCase();//将数据库字段全部转换为小写
                        columnTypes[i] = metaResult.getColumnClassName(i + 1);
                    }
                    //获取该类所有方法
                    Method[] methods = objClass.getDeclaredMethods();
                    //存入该类所有的方法
                    Map<String, Object> methodMap = new HashMap<String, Object>();
                    for (Method method : methods) {
                    	//将所有方法存入map集合
                    	methodMap.put(method.getName().toUpperCase(), method);
					}
                    while (rs.next())
                    {
                        try
                        {
                            /*实例化实体类*/
                            Object obj = objClass.newInstance();
                            /*根据字段名调用实体类中的set方法*/
                            for (int j = 0; j < columnNames.length; j++)
                            {
                            	try {
                            		//获取值
                            		Object objValue = rs.getObject(columnNames[j]);
                            		//执行赋值操作
                            		String methodName = "set"+columnNames[j];
                            		//获取匹配的method对象
                            		Method method = (Method)methodMap.get(methodName.toUpperCase());
                            		//执行该方法
                            		method.invoke(obj,objValue);//反射到对应实体类方法并赋值
								} catch (Exception e) {}                    
                            }                          
                            listObjs.add(obj);
                        } catch (InstantiationException e)
                        {
                            e.printStackTrace();
                        } catch (IllegalAccessException e)
                        {
                            e.printStackTrace();
                        } catch (SecurityException e)
                        {
                            e.printStackTrace();
                        } catch (IllegalArgumentException e)
                        {
                            e.printStackTrace();
                        } 
                    }
                }
    
            } catch (SQLException e)
            {
                e.printStackTrace();
            }
            return listObjs;
        }
    

        /**
         * 将首字母变为大写
         * @param str
         * @return
         */
        public String upInitial(String str){
            char[] chars = str.toCharArray();
            chars[0] = Character.toUpperCase(chars[0]);
            return new String(chars);
        }
        
        /**
         * 获取分页起始位置<br>
         * 方 法 名：getStartIndex <br>
         * 创 建 人：he <br>
         * 创建时间：2017年6月9日 上午9:11:31 <br>
         * 修 改 人： <br>
         * 修改日期： <br>
         * @param pageNo
         * @param pageSize
         * @return Integer
         */
        public Integer getStartIndex(Integer pageNo,Integer pageSize) {
            if (null == pageNo || null == pageSize) {
                return null;
            }
            return (pageNo - 1) * pageSize;
        }
        /**
         * <br>
         * 方 法 名：getEndIndex <br>
         * 创 建 人：he <br>
         * 创建时间：2017年6月9日 上午9:11:42 <br>
         * 修 改 人： <br>
         * 修改日期： <br>
         * @param pageNo
         * @param pageSize
         * @param dbname
         * @return Integer
         */
        public Integer getEndIndex(Integer pageNo,Integer pageSize,String dbname) {
        	if(dbname.equals("oracle")){
              if (null == pageNo || null == pageSize) {
                  return null;
              }
              return pageNo * pageSize;
        	}else{
        		return pageSize;
        	}  
        }
        /**
         * 设置一般字符串参数<br>
         * 方 法 名：setParam <br>
         * 创 建 人：he <br>
         * 创建时间：2017年6月9日 上午9:14:26 <br>
         * 修 改 人： <br>
         * 修改日期： <br>
         * @param pstmt
         * @param valueList
         * @param startNum
         * @throws NumberFormatException
         * @throws SQLException void
         */
    	private void setParam(PreparedStatement pstmt, List<String> valueList, int startNum){
    		try {
    			for (int i = 0; i < valueList.size(); i++) {
        			// 得到该赋值字段的值
        			String fieldValue = valueList.get(i);
        			pstmt.setObject(startNum, fieldValue);
        			startNum++;
        		}
			} catch (NumberFormatException ne) {
				logger.error(ne);
				ne.printStackTrace();
			} catch (SQLException se) {
				logger.error(se);
				se.printStackTrace();
			}
    	}
    	/**
    	 * 设置时间类型数据<br>
    	 * 方 法 名：setDateParam <br>
    	 * 创 建 人：he <br>
    	 * 创建时间：2017年6月9日 上午9:15:08 <br>
    	 * 修 改 人： <br>
    	 * 修改日期： <br>
    	 * @param pstmt
    	 * @param dateValueList
    	 * @param startNum
    	 * @throws SQLException
    	 * @throws IOException void
    	 */
    	private void setDateParam(PreparedStatement pstmt, List<Date> dateValueList, int startNum){
    		try {
    			for (Date d : dateValueList) {
    				pstmt.setTimestamp(startNum, new java.sql.Timestamp(d.getTime()));
    				startNum++;
    			}
			} catch (SQLException se) {
				logger.error(se);
				se.printStackTrace();
			}
    	}
    	/**
    	 * 设置blob类型数据<br>
    	 * 方 法 名：setBlobParam <br>
    	 * 创 建 人：he <br>
    	 * 创建时间：2017年6月9日 上午9:15:21 <br>
    	 * 修 改 人： <br>
    	 * 修改日期： <br>
    	 * @param pstmt
    	 * @param blobValueList
    	 * @param startNum
    	 * @throws SQLException
    	 * @throws IOException void
    	 */
    	private void setBlobParam(PreparedStatement pstmt, List<Blob> blobValueList, int startNum) {
    		try {
    			// 为mysql时赋值
    			DatabaseMetaData datame = getCon().getMetaData();
    			//获取数据库名称
    			String dbName = datame.getDatabaseProductName().toUpperCase();
    			//判定数据库类型
    			if (dbName.equals("MYSQL") || dbName.equals("MICROSOFT SQL SERVER")) {
    				for (Blob b : blobValueList) {
    					InputStream fin = b.getBinaryStream();
    					byte[] bys = new byte[fin.available()];
    					fin.read(bys);
    					pstmt.setBytes(startNum, bys);
    					startNum++;
    				}
    			}
			} catch (SQLException se) {
				logger.error(se);
				se.printStackTrace();
			} catch (IOException ie) {
				logger.error(ie);
				ie.printStackTrace();
			}
		}
    	/**
    	 * 判定字段是否是主键<br>
    	 * 方 法 名：getIsDB <br>
    	 * 创 建 人：he <br>
    	 * 创建时间：2017年7月18日 下午2:49:00 <br>
    	 * 修 改 人： <br>
    	 * 修改日期： <br>
    	 * @param field
    	 * @return boolean
    	 */
    	private boolean isPK(Field field) {
    		boolean retValue = false;
    		if (field.getAnnotation(FieldTag.class) != null) {
    			retValue = field.getAnnotation(FieldTag.class).pk();
    		}
    		return retValue;
    	}
    	/**
    	 * 判断是否自动生成UUID<br>
    	 * 方 法 名：isRandomUUID <br>
    	 * 创 建 人：he <br>
    	 * 创建时间：2017年7月18日 下午3:11:39 <br>
    	 * 修 改 人： <br>
    	 * 修改日期： <br>
    	 * @param field
    	 * @return boolean
    	 */
    	private boolean isRandomUUID(Field field){
    		boolean retValue = false;
    		if(field.getAnnotation(FieldTag.class) != null){
    			retValue = field.getAnnotation(FieldTag.class).rdUUID();
    		}
    		return retValue;
    	}
        
}
