package com.beyontec.mdcp.company.response;



import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * A Response class to return the Status, Data, Error code, Error message on API
 * Operations. This response class is mainly used to track the error based on
 * their error code and error message.
 * 
 * @param <T> Response Object
 */

@JsonInclude(value = Include.NON_EMPTY)
public class Response<T> {

	Integer status;
	T data;
	String message;

	/**
	 * A Response Function to get HttpResponse and HttpMessage informations.
	 * 
	 * @param status  code to find the error
	 * @param message description about the error.
	 */

	public Response(Integer status, String message) {
		super();
		this.status = status;
		this.message = message;
	}

	/**
	 * A Response method to get status, data and message informations Data is JSON
	 * informations with key and value pairs.
	 * 
	 * @param status  status code to find the error
	 * @param data    JSON keys and value pairs.
	 * @param message message description about the error.
	 */

	public Response(Integer status, T data, String message) {
		super();
		this.status = status;
		this.data = data;
		this.message = message;
	}

	/**
	 * A Response method to send a JSON format data information to browser.
	 * 
	 * @param data contains JSON information
	 */

	public Response(T data) {
		super();
		this.data = data;
	}

	public Response() {
	}

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
