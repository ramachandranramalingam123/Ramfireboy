package com.beyontec.mdcp.company.exception;

public class InvalidURLExpception extends RuntimeException {

    public InvalidURLExpception() {
        super();
    }

    public InvalidURLExpception(String message) {
        super(message);
    }
}
