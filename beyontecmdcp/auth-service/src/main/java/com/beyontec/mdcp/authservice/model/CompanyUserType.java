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
@Table(name = "COMPANY_USER_TYPE")
@Data
public class CompanyUserType {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "USER_TYPE_ID")
	private Integer userTypeId;
	
	@Column(name = "USER_TYPE")
	private String userType;;

}
