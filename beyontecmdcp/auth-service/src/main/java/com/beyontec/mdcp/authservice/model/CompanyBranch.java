package com.beyontec.mdcp.authservice.model;

import java.time.LocalDateTime;

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
@Table(name = "COMPANY_BRANCH")
@Data
public class CompanyBranch {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "COMPANY_BRANCH_ID")
	private Integer companyBranchId;

	@Column(name = "CREATED_DATE")
	private LocalDateTime createdDate;
	
	@ManyToOne
	@JoinColumn(name = "COMPANY_ID", nullable = false)
	private InsuranceCompany company;
	
	@ManyToOne
	@JoinColumn(name = "BRANCH_ID", nullable = false)
	private BranchMaster branch;

	
	@Column(name = "CREATED_BY")
	private Integer createdBy;

	@Column(name = "UPDATED_BY")
	private Integer updatedBy;

	@Column(name = "UPDATED_DATE")
	private LocalDateTime updatedDate;

}
