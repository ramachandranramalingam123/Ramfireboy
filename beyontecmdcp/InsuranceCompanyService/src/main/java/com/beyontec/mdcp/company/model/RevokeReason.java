package com.beyontec.mdcp.company.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="REVOKE_REASON")
@Data
public class RevokeReason {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "REASON_ID")
	private Integer reasonId;
	
	@Column(name = "REASON")
	private String reason;
	
	

}
