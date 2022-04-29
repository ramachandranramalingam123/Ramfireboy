package com.beyontec.mdcp.notification.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_EMPTY)
public class Response<T> {

	public Response(Integer status, String message) {
		super();
		this.status = status;
		this.message = message;
	}

	public Response(Integer status, T data, String message) {
		super();
		this.status = status;
		this.data = data;
		this.message = message;
	}

	public Response(T data) {
		super();
		this.data = data;
	}

	public Response() {
	}

	Integer status;
	T data;
	String message;

	public Integer getStatus() {
		return status;
	}

	public T getData() {
		return data;
	}

	public String getMessage() {
		return message;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setData(T data) {
		this.data = data;
	}


	public void setMessage(String message) {
		this.message = message;
	}

}
