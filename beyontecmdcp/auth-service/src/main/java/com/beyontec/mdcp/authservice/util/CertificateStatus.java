package com.beyontec.mdcp.authservice.util;

public enum CertificateStatus {
	
	revoked (0),
	issued (1);
	
	private final int status;

    private CertificateStatus(int status) {
        this.status = status;
    }

}
