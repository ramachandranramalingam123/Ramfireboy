package com.beyontec.mdcp.authservice.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="BRANCH_MASTER")
@Data
public class BranchMaster {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "BRANCH_ID")
	private Integer branchId;
	
	@Column(name = "BRANCH_NAME")
    private String branchName;
    
    @Column(name = "STATUS")
    private Integer status;
    
    @Column(name = "CREATED_DATE")
	private LocalDateTime createdDate;

	@Column(name = "CREATED_BY")
	private Integer createdBy;

	@Column(name = "UPDATED_BY")
	private Integer updatedBy;

	@Column(name = "UPDATED_DATE")
	private LocalDateTime updatedDate;
	

}
