package com.beyontec.mdcp.exception;

/**
 * A class that user define exception class for handle runtime exception 
 */
public class InvalidPasswordSessionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidPasswordSessionException(String message) {
		super(message);
	}
}
