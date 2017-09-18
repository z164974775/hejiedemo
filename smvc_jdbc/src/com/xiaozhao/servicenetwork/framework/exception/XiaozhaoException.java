package com.xiaozhao.servicenetwork.framework.exception;

public interface XiaozhaoException {

    public abstract String getMessage();

    public abstract String getMessageCode();

    public abstract Object[] getReplacePattern();

    public abstract Throwable getCause();

    public abstract String toString();
    
    public abstract String getDefinedMessage();
}
