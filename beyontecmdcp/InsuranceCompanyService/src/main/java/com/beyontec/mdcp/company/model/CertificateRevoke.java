package com.beyontec.mdcp.company.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="CERTIFICATE_REVOKE")
@Data
public class CertificateRevoke {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "REVOKE_ID")
	private Integer revokeId;
	
	@Column(name = "REVOKED_BY")
	private Integer revokedBy;
	
	@Column(name = "REASON")
	private String reason;
	
	@Column(name = "REVOKED_AT")
	private Date revokedAt;
	
	@OneToOne
	@JoinColumn(name = "CERTIFICATE_ID", nullable = false)
	private CertificateDetails certificateDetails;

}
