package com.example.chatapi.model.res;

public enum ResultEnum {
    SUCCESS(200, "成功"),
    ERROR(500, "失败"),
    PARAM_ERROR(400, "参数错误"),
    NOT_FOUND(404, "未找到"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    ;

    private Integer code;
    private String msg;

    private Object data;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public ResultEnum setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public ResultEnum setCode(Integer code) {
        this.code = code;
        return this;
    }

    public ResultEnum setData(Object data) {
        this.data = data;
        return this;
    }

    public Result getResult() {
        Result result = new Result();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }
}
