package com.beyontec.mdcp.model;

import java.time.LocalDate;

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
@Table(name="CERT_SERIAL_NUM")
@Data
public class CertificateSerialNum {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="SERIAL_NUM_ID")
	private Integer serialNumId;
	
	@Column(name="SERIAL_NUM")
	private String serialNum;
	
	@Column(name="SERIAL_NUM_ORDER")
	private Integer serialNumOrder;
	
	@Column(name="ISSUED_STATUS")
	private String IssuedStatus;
	
	@Column(name="ALLOCATED_DATE")
	private LocalDate allocatedDate;
	
	@OneToOne
	@JoinColumn(name = "COMPANY_ID", nullable = false)
	private InsuranceCompany company;
	
	
	

}
