package com.beyontec.mdcp.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="INTL_CERT_ALCTN")
@Data
public class InternalCertificate {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ALLOCATED_ID")
	private Integer allocationId;
	
	@Column(name = "ALLOCATED_CERTIFICATES",  columnDefinition = "int default 0")
	private Integer allocatedCertificates;
	
	@Column(name="ALLOCATED_BY")
	private Integer allocatedBy;
	
	@Column(name="ALLOCATED_DATE")
	private LocalDateTime allocatedDate;
	
	@ManyToOne
	@JoinColumn(name = "COMPANY_ID", nullable = false)
	private InsuranceCompany company;
	
	@ManyToOne
	@JoinColumn(name = "USER_TYPE_ID", nullable = false)
	private CompanyUserType companyUserType;


}
