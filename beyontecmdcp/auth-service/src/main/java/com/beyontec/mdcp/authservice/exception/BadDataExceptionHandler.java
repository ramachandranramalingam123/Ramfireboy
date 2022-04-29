package com.beyontec.mdcp.authservice.exception;

import org.springframework.http.HttpStatus;

import com.beyontec.mdcp.authservice.response.Response;

public class BadDataExceptionHandler extends RuntimeException {

	private static final long serialVersionUID = 1795793256923097719L;

	public BadDataExceptionHandler(String message) {
		super(message);
	}
	

}
