package com.xiaozhao.servicenetwork.framework.exception;


public class DBException extends SystemException {

    private static final long serialVersionUID = -3589790516990181160L;

    private static final String MESSAGE_CODE = "F_E_002";

    public DBException() {
        super(MESSAGE_CODE);
    }

    public DBException(Throwable e) {
        super(MESSAGE_CODE, e);
    }
}
