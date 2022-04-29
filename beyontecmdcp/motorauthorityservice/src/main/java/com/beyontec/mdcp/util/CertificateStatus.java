package com.beyontec.mdcp.util;

public enum CertificateStatus {
	
	revoked (0),
	issued (1);
	
	private final int status;
	
	
	public int getStatus() {
		
		return this.status;
	}

    private CertificateStatus(int status) {
        this.status = status;
    }

}
