package kr.movements.smv2.dto;

import kr.movements.smv2.common.exception.BootException;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

public class Payload<T> {
    public String timestamp;
    public String path;
    public int status;
    public String errorMsg;
    public T data;

    public Payload(HttpStatus status, String path, HttpServletResponse response) {
        this.timestamp = LocalDateTime.now().toString();
        this.status = status.value();
        this.path = path;
        response.setStatus(this.status);
    }

    public Payload(HttpStatus status, String path, T data, HttpServletResponse response) {
        this(status, path, response);
        this.data = data;
    }

    public Payload(HttpStatus status, String path, String errorMsg, HttpServletResponse response) {
        this(status, path, response);
        this.errorMsg = errorMsg;
    }

    public Payload(String path, BootException be, HttpServletResponse response) {
        this(be.getStatus(), path, response);
        this.errorMsg = be.getMessage();
    }

    public Payload(HttpStatus status, String path, BootException be, HttpServletResponse response) {
        this(status, be.getMessage(), path, response);
        this.errorMsg = be.getMessage();
    }

    public Payload(HttpStatus status, String path, Exception e, HttpServletResponse response) {
        this(status, path, response);
        this.errorMsg = e.getMessage();
    }

    public String getTimestamp() {
        return this.timestamp;
    }

    public int getStatus() {
        return this.status;
    }

    public String getError() {
        return this.errorMsg;
    }

    public String getPath() {
        return this.path;
    }

    public T getData() {
        return this.data;
    }
}
