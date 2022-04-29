package com.beyontec.mdcp.authservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "ROLES_MASTER")
@Data
public class RolesMaster {
	
	@Id
	@Column(name = "ROLES_MASTER_ID")
	private Integer masterId;

	@Column(name = "ROLE")
	private String role;

	@Column(name = "ROLE_ID")
	private String roleId;
	
	@Column(name = "USER_PORTAL")
	private String userPortal;

}
