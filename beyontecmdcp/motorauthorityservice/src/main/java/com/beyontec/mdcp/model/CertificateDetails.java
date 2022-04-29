package com.beyontec.mdcp.model;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;




import lombok.Data;

@Entity
@Table(name = "CERTIFICATE_DTLS")
@Data
public class CertificateDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "CERTIFICATE_ID")
	private Integer certificateId;
	
	@Column(name = "CERTIFICATE_SER_NO")
	private String certificateSerialNumber;

	@Column(name = "INSURED")
	private String insured;

	@Column(name = "POLICY_NUMBER")
	private String policyNumber;

	@Column(name = "STATUS")
	private Integer status;

	@Column(name = "COMMENCING_DATE")
	private Date commencingDate;

	@Column(name = "EXPIERY_DATE")
	private Date expiryDate;

	@Column(name = "REGISTRATION_NUMBER")
	private String registartionNumber;
	
	@Column(name = "CHASSIS_NUMBER")
	private String chassisNumber;

	@Column(name = "VECHICLE_TYPE")
	private String vechicleType;
	
	@Column(name = "LICENSED")
	private String licensed;

	@Column(name = "ISSUED_BY")
	private String issuedBy;
	
	@Column(name = "UPLOADED_BY")
	private Integer uploadedBy;
	
	@Column(name = "APPROVED_BY")
	private String approvedBy;

	@Column(name = "REVOKE_REASON")
	private String revokeReason;
	
	@Column(name = "CREATED_DATE")
	private LocalDateTime createdDate;

	@Column(name = "CREATED_BY")
	private Integer createdBy;
	
	@Column(name = "UPDATED_BY")
	private Integer updatedBy;

	@Column(name = "UPDATED_DATE")
	private LocalDateTime updatedDate;

	@Column(name = "CERTIFICATE_USAGE")
	private String usage;
	

	@ManyToOne
	@JoinColumn(name = "COMPANY_ID", nullable = false)
	private InsuranceCompany insuranceCompany;
	
	@Column(name = "QR_CODE")
	@Lob
	private byte[] qrCode;

	@Column(name = "PRIMARY_EMAIL")
	private String primaryEmail;
	
	@Column(name = "SECONDARY_EMAIL")
	private String secondaryEmail;

}