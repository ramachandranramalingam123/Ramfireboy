package com.beyontec.mdcp.company.exception;

public class BadDataExceptionHandler extends RuntimeException {

	private static final long serialVersionUID = 1795793256923097719L;

	public BadDataExceptionHandler(String message) {
		super(message);
	}

}
