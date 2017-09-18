package com.xiaozhao.servicenetwork.framework.exception;


public class UnexpectedException extends SystemException {

    private static final long serialVersionUID = -4294304120127132394L;

    private static final String MESSAGE_CODE = "F_E_003";

    public UnexpectedException() {
        super(MESSAGE_CODE);
    }

    public UnexpectedException(String msg) {
        super(msg);
    }

    public UnexpectedException(Throwable e) {
        super(MESSAGE_CODE, e);
    }
}
