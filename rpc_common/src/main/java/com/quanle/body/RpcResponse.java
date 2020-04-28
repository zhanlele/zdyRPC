package com.quanle.body;

/**
 * @author quanle
 * @date 2020/4/28 11:44 PM
 */
public class RpcResponse {
    /**
     * 请求对象的ID
     */
    private String requestId;
    /**
     * 类名
     */
    private int code;
    /**
     * 报错信息
     */
    private Exception exception;
    /**
     * 返回值
     */
    private Object result;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
