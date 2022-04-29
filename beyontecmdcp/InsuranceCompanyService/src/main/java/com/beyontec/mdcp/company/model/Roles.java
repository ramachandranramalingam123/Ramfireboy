package com.beyontec.mdcp.company.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "ROLES")
@Data
public class Roles {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ID")
	private Integer id;

	@OneToOne
	@JoinColumn(name = "MODULE_ID", nullable = false)
	private RolesModule rolesModule;
	
	@OneToOne
	@JoinColumn(name = "ROLES_MASTER_ID", nullable = false)
	private RolesMaster rolesMaster;

	@Column(name = "CAN_ACCESS")
	private boolean canAccess;

}
