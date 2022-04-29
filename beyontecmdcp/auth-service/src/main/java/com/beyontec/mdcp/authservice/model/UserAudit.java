package com.beyontec.mdcp.authservice.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="USER_AUDIT")
public class UserAudit implements Serializable {

	private static final long serialVersionUID = 7139655001268948165L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "USER_AUDIT_ID")
	private Integer userAuditId;

	@Column(name="USER_ID")
	private Integer userId;

	@Column(name="IP_ADDRESS")
	private String ipAddress;

	@Column(name="CREATED_BY")
	private String createdBy;  
	
	@Column(name="CREATED_DATE")
	private LocalDateTime createdDate;  
	
	@Column(name="UPDATED_BY")
	private String updatedBy;

	@Column(name="UPDATED_DATE")
	private LocalDateTime updatedDate; 
}
