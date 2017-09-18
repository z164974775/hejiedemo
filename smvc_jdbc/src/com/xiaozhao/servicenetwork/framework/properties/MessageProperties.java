package com.xiaozhao.servicenetwork.framework.properties;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
/**
 * @author hejie
 */
public final class MessageProperties {

    private static MessageProperties properties;

    private Properties message;

    private MessageProperties() {
        message = new Properties();
        initialize();
    }
    /**
     * 初始化加载配置文件
     */
    public void initialize() {
        InputStream stream = null;
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            //加载message配置文件
            stream = loader.getResource("config/messages.properties").openStream();
            message.load(stream);
        } catch (IOException e) {
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                    stream = null;
                } catch (Exception e) {
                }
            }
        }
    }
    
    public synchronized static MessageProperties getInstance() {
        if (null == properties) {
            properties = new MessageProperties();
        }
        return properties;
    }
    //根据key获取内容
    public String getMessageValue(String key) {
        return getMessageValue(key, null);
    }
    //获取配置内容组装
    public String getMessageValue(String key, Object[] args) {
        String msg = (String) message.get(key);
        if (StringUtils.isEmpty(msg)) {
            msg = "";
        } else {
            msg = MessageFormat.format(msg, args);
        }
        return msg;
    }
}