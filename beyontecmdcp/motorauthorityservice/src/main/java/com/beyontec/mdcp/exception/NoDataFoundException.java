package com.beyontec.mdcp.exception;
/**
 * A class that user define exception class for handle runtime exception 
 */
public class NoDataFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NoDataFoundException(String message) {
		super(message);
	}
}
