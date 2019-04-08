package com.cloud.disk.core;

public class ResponseBean<T>{
    private boolean success;
    private T data;
    private String errCode;
    private String errMsg;

    public ResponseBean(){}

    public ResponseBean(boolean success, T data) {
        super();
        this.success = success;
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseBean{" +
                "success=" + success +
                ", data=" + data +
                ", errCode='" + errCode + '\'' +
                ", errMsg='" + errMsg + '\'' +
                '}';
    }

    public ResponseBean(boolean success, T data, String errCode, String errMsg) {
        super();
        this.success = success;
        this.data = data;
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public ResponseBean(boolean success, String errCode, String errMsg) {
        this.success = success;
        this.errCode = errCode;
        this.errMsg = errMsg;
    }
    public ResponseBean(boolean success,UnicomResponseEnums enums){
        this.success=success;
        this.errCode=enums.getCode();
        this.errMsg=enums.getMsg();
    }
    public ResponseBean(boolean success,T data,UnicomResponseEnums enums){
        this.success=success;
        this.data=data;
        this.errCode=enums.getCode();
        this.errMsg=enums.getMsg();
    }
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }
    public String getErrCode() {
        return errCode;
    }
    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }
    public String getErrMsg() {
        return errMsg;
    }
    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public static ResponseBean<UnicomResponseEnums>  success()
    {
        return new ResponseBean(true,UnicomResponseEnums.SUCCESS_OPTION);
    }
    public static ResponseBean<UnicomResponseEnums>  success(String errorMsg)
    {
        return new ResponseBean(true,"1",errorMsg);
    }
    public static ResponseBean<UnicomResponseEnums>  success(String errorCode,String errorMsg)
    {
        return new ResponseBean(true,errorCode,errorMsg);
    }
    public static ResponseBean<UnicomResponseEnums>  error()
    {
        return new ResponseBean(true,UnicomResponseEnums.SYSTEM_ERROR);
    }
    public static ResponseBean<UnicomResponseEnums>error(String errMsg){
        return new ResponseBean(false,"-1",errMsg);
    }
}