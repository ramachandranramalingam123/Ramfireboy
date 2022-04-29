package com.beyontec.mdcp.company.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "COMPANY_USER_TYPE")
@Data
public class CompanyUserType {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "USER_TYPE_ID", updatable = false, insertable = false)
	private Integer userTypeId;
	

	@Column(name = "USER_TYPE", insertable = false, updatable = false)
	private String userType;
	
	@Column(name = "IS_OFFLINE", insertable = false, updatable = false,  columnDefinition = "int default 0")
	private Integer isOffline;

}
