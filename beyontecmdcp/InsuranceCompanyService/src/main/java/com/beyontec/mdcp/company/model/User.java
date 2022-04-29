package com.beyontec.mdcp.company.model;

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
@Table(name="USER")
@Data
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="USER_ID")
	private Integer userId;
	
	@Column(name="FIRST_NAME")
	private String firstName;
	
	@Column(name="MIDDLE_NAME")
	private String middleName;
	
	@Column(name="LAST_NAME")
	private String lastName;
	
	@Column(name="MOBILE_NUM")
	private String mobileNumber;
	
	@Column(name="USER_NAME")
	private String userName;
	
	@Column(name="PASSWORD")
	private String password;
	
	@Column(name="IS_PASSWORD_UPDATED")
	private String isPasswordUpdated;
	
	@Column(name="EMAIL")
	private String email;
	
	@Column(name="DESIGNATION")
	private String desingnation;
	
	@Column(name="INSURE_COMPANY")
	private String insureCompany;
	
	@Column(name="ROLE_ID")
	private Integer roleId;
	
	@Column(name="STATUS")
	private String status;
	
	@Column(name = "LAST_LOGGED_IN")
	private LocalDateTime lastLoggedIn;
	
	@Column(name = "COMPANY_ID")
	private Integer companyId;
	
	@Column(name="USER_ACCOUNT")
	private String userAccount;
	
	@Column(name="MAIL_SATUS")
	private String mailStatus;
	

	@Column(name = "USER_TYPE_ID")
	private Integer userTypeId;
	
	@Column(name = "CREATED_BY")
	private Integer creadtedBy;
	
	@Column(name = "CREATED_DATE")
	private LocalDateTime creadtedDate;

	
	@Column(name = "UPDATED_BY")
	private Integer updatedBy;

	@Column(name = "UPDATED_DATE")
	private LocalDateTime updatedDate;
	
	@Column(name = "SIGNATURE")
	@Lob
	private byte[] signature;
	
	@Column(name = "PHOTO")
	@Lob
	private byte[] photo;
	
	@Column(name="BRANCH_USER_TYPE")
	private String branchUserType;


}
