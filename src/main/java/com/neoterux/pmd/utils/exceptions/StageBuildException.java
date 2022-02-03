package com.neoterux.pmd.utils.exceptions;

public class StageBuildException extends RuntimeException {
    public StageBuildException(String msg) {
        super(msg);
    }
    public StageBuildException(String msg, Throwable cause){
        super(msg, cause);
    }
}
