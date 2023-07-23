package com.meerkat.mytidbapp.util;

/**
 * 公共返回包装类
 *
 */
public class Result<T> {

    /**
     * 返回码
     */
    private Integer code;

    /**
     * 返回信息
     */
    private String msg;

    /**
     * 结果是否成功
     */
    private Boolean success;

    /**
     * 返回数据
     */
    private T data;

    public static Result<String> ok() {
        Result<String> result = new Result<>();
        result.setCode(ResultCode.S200.getCode());
        result.setMsg("success");
        result.setSuccess(true);
        return result;
    }

    public static <T> Result<T> ok(T data, String msg) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.S200.getCode());
        result.setMsg(msg);
        result.setData(data);
        result.setSuccess(true);
        return result;
    }

    public static <T> Result<T> okWithMsg(String msg) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.S200.getCode());
        result.setMsg(msg);
        result.setSuccess(true);
        return result;
    }

    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.S200.getCode());
        result.setMsg("success");
        result.setData(data);
        result.setSuccess(true);
        return result;
    }

    public static <T> Result<T> ok(String msg) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.S200.getCode());
        result.setMsg(msg);
        result.setSuccess(true);
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.E500.getCode());
        result.setMsg(msg);
        result.setSuccess(false);
        return result;
    }

    public static <T> Result<T> error401(String msg) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.E401.getCode());
        result.setMsg(msg);
        result.setSuccess(false);
        return result;
    }

    public static <T> Result<T> error403(String msg) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.E403.getCode());
        result.setMsg(msg);
        result.setSuccess(false);
        return result;
    }

    public static <T> Result<T> error403() {
        return error403("Forbidden");
    }

    public static <T> Result<T> error405(String msg) {
        Result<T> result = new Result<>();
        result.setCode(ResultCode.E405.getCode());
        result.setMsg(msg);
        result.setSuccess(false);
        return result;
    }

    public static <T> Result<T> error405() {
        return error405("请求不允许");
    }

    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMsg(msg);
        result.setSuccess(false);
        return result;
    }

    public static <T> Result<T> error(Integer code, String msg, T t) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setData(t);
        result.setMsg(msg);
        result.setSuccess(false);
        return result;
    }

    public static <T> Result<T> errorWithMsg(String msg) {
        Result<T> result = new Result<>();
        result.setCode(999);
        result.setMsg(msg);
        result.setSuccess(false);
        return result;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public enum ResultCode {

        S200(200),
        E400(400),
        E401(401),
        E403(403),
        E405(405),
        E500(500),
        E999(999);

        private final Integer code;

        ResultCode(Integer code) {
            this.code = code;
        }

        public Integer getCode() {
            return code;
        }
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", success=" + success +
                ", data=" + data +
                '}';
    }
}
