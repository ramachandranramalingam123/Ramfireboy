package com.beyontec.mdcp.authservice.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "COMPANY_DTLS")
@Data
public class InsuranceCompany {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "COMPANY_ID")
	private Integer companyId;

	@Column(name = "COMPANY_NAME")
	private String companyName;

	@Column(name = "COMPANY_CODE")
	private String companyCode;

	@Column(name = "ADDRESS_1")
	private String address1;

	@Column(name = "ADDRESS_2")
	private String address2;

	@Column(name = "CITY")
	private String city;

	@Column(name = "STATE")
	private String state;

	@Column(name = "COUNTRY")
	private String country;

	@Column(name = "ZIPCODE")
	private String zipCode;

	@Column(name = "CONTACT_NUMBER")
	private String contactNo;

	@Column(name = "MAIL_ID")
	private String mailId;

	@Column(name = "AUTO_APPROVAL_TIME",  columnDefinition = "int default 0")
	private Long autoApprovalTime;

	@Column(name = "AUTO_APPROVAL_LIMIT",  columnDefinition = "int default 0")
	private Integer autoApprovalLimit;

	@Column(name = "CREATED_DATE")
	private LocalDateTime createdDate;

	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Column(name = "UPDATED_DATE")
	private LocalDateTime updatedDate;
	
	@Column(name = "COMPANY_LOGO")
	@Lob
	private byte[] companyLogo;
	
	@Column(name = "STATUS")
	private String status;
	

}
