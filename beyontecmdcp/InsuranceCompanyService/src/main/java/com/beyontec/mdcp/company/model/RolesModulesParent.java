package com.beyontec.mdcp.company.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "ROLES_MODULES_PARENT")
@Data
public class RolesModulesParent {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "MODULE_PARENT_ID")
	private Integer moduleParentId;

	@Column(name = "MODULE_PARENT")
	private String moduleParent;

}
