package com.beyontec.mdcp.company.model;

import java.time.LocalDateTime;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


import lombok.Data;

@Entity
@Table(name="CERTIFICATE_ALLOCATION")
@Data
public class CertificateAllocation {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="ALLOCATION_ID")
	private Integer allocationId;
	
	@Column(name="REQUESTED_BY")
	private Integer requestedBy;
	
	@Column(name="REQUESTED_DATE")
	private LocalDateTime requestedDate;
	
	@Column(name="ALLOCATED_BY")
	private Integer allocatedBy;
	
	@Column(name="ALLOCATED_DATE")
	private LocalDateTime allocatedDate;
	
	@Column(name="APPROVED_BY")
    private Integer approvedBy;
	
	@Column(name="APPROVED_DATE")
	private LocalDateTime approvedDate;
	
	@Column(name="ALLCATED_CERTIFICATES", columnDefinition = "int default 0")
	private Long allocatedCertificates;
	
	@Column(name="REQUESTED_CERTIFICATES", columnDefinition = "int default 0")
	private Long requestedCertificates;
	
	@Column(name="SHOW_AUTHORITY_NOTIFY")
	private Integer showAuthorityNonify;
	
	@Column(name="SHOW_COMPANY_NOTIFY")
	private Integer showCompanyNonify;
	
	@Column(name="IS_REJECTED", columnDefinition = "int default 0")
	private Integer Isrejected;
	
	@Column(name="REJECTED_AT")
	private LocalDateTime rejectedAt;
	
	@Column(name="REJECTED_BY")
	private Integer rejectedBy;
	
	@Column(name="CU_IS_REJECTED", columnDefinition = "int default 0")
	private Integer cuIsrejected;
	
	@Column(name="CU_REJECTED_AT")
	private LocalDateTime cuRejectedAt;
	
	@Column(name="CU_REJECTED_BY")
	private Integer cuRejectedBy;
	
	@ManyToOne
	@JoinColumn(name = "COMPANY_ID", nullable = false)
	private InsuranceCompany company;
	
	@Column(name="CERT_NUM_STATUS", columnDefinition = "int default 0")
	private Integer certificateNumStatus;
	
	@Column(name = "PAYMENT_FILE_NAME")
	private String fileName;
	
	@Column(name = "PAYMENT_FILE_TYPE")
	private String paymentFileType;
	
	@Column(name = "PAYMENT_DESCRIPTION")
	private String paymentDescription;
	
	
	

}
