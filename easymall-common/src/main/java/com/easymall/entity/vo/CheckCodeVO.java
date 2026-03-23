package com.easymall.entity.vo;

public class CheckCodeVO {
    private String checkCodeKey;
    private String checkCode;

    public CheckCodeVO(String checkCodeKey, String checkCode){
        this.checkCodeKey = checkCodeKey;
        this.checkCode = checkCode;
    }

    public String getCheckCodeKey() {
        return checkCodeKey;
    }

    public void setCheckCodeKey(String checkCodeKey) {
        this.checkCodeKey = checkCodeKey;
    }

    public String getCheckCode() {
        return checkCode;
    }

    public void setCheckCode(String checkCode) {
        this.checkCode = checkCode;
    }
}
