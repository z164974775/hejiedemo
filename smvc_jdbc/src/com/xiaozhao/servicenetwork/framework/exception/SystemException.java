package com.xiaozhao.servicenetwork.framework.exception;

import com.xiaozhao.servicenetwork.framework.properties.MessageProperties;

public class SystemException extends RuntimeException implements XiaozhaoException {

    private static final long serialVersionUID = -7072006125908746827L;

    protected String messageCode = null;

    protected Throwable cause = null;

    protected Object[] pattern = null;

    protected SystemException(String definedMessage) {
        super(definedMessage);
    }

    protected SystemException(String messageCode, Object[] pattern) {
        this(messageCode, null, pattern);
    }

    protected SystemException(String messageCode, Throwable t) {
        this(messageCode, t, null);
    }

    protected SystemException(String messageCode, Throwable t, Object[] pattern) {
        super(t);
        this.messageCode = messageCode;
        this.cause = t;
        this.pattern = pattern;
    }

    public String getMessage() {
        return MessageProperties.getInstance().getMessageValue(messageCode, pattern);
    }

    public String getMessageCode() {
        return messageCode;
    }

    public Object[] getReplacePattern() {
        return pattern;
    }

    public Throwable getCause() {
        return cause;
    }

    public String toString() {
        return getMessage();
    }

    public String getDefinedMessage() {
        return super.getMessage();
    }
}
