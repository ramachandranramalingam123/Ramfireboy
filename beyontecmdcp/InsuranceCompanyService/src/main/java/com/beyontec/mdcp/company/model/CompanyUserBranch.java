package com.beyontec.mdcp.company.model;

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
@Table(name = "COMPANY_USER_BRANCH")
@Data
public class CompanyUserBranch {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "COMPANY_USER_BRANCH_ID")
	private Integer companyUserBranchId;
	
	@ManyToOne
	@JoinColumn(name = "USER_ID", nullable = false)
	private User user;

	@ManyToOne
	@JoinColumn(name = "BRANCH_ID", nullable = false)
	private BranchMaster branch;
	
	@Column(name = "PRIMARY_BRANCH")
	private String primaryBranch;
	
	@ManyToOne
	@JoinColumn(name = "COMPANY_BRANCH_ID", nullable = false)
	private CompanyBranch companyBranch;

	@Column(name = "CREATED_DATE")
	private LocalDateTime createdDate;

	@Column(name = "CREATED_BY")
	private Integer createdBy;

	@Column(name = "UPDATED_BY")
	private Integer updatedBy;

	@Column(name = "UPDATED_DATE")
	private LocalDateTime updatedDate;

}
