package vn.bighousevn.jobhunter.domain.response;

public class RestResponse<T> {

    private int statusCode;
    private String error;
    private Object message;
    private T data;

    public String getError() {
        return error;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Object getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

}
