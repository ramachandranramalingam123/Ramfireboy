package com.beyontec.mdcp.exception;

public class InvalidURLExpception extends RuntimeException {

    public InvalidURLExpception() {
        super();
    }

    public InvalidURLExpception(String message) {
        super(message);
    }
}
