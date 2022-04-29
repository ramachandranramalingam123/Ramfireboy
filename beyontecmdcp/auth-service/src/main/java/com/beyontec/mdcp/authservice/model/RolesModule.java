package com.beyontec.mdcp.authservice.model;

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
@Table(name = "ROLES_MODULE")
@Data
public class RolesModule {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "MODULE_ID")
	private Integer moduleId;

	@Column(name = "PORTAL")
	private String portal;
	
	@Column(name = "MODULE_LABEL")
	private String moduleLabel;
	
	@OneToOne
	@JoinColumn(name = "MODULE_PARENT_ID", nullable = false)
	private RolesModulesParent moduleParent;
	
}
