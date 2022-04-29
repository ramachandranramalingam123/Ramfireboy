package com.beyontec.mdcp.company.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "TOKEN_DETAIL")
@Data
public class TokenDetail implements Serializable {

	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "TOKEN_ID")
	private Integer tokenId;

	@Column(name = "USER_ID")
	private Integer userId;

	@Column(name = "TOKEN")
	private String token;
	
	@Column(name = "REFRESH_TOKEN")
	private String refreshToken;

	@Column(name = "IS_REMEMBER")
	private Integer isRemember;

	@Column(name = "DEVICE_TYPE")
	private String devicetype;

	@Column(name = "CLIENT_IP")
	private String clientIp;

	@Column(name = "TOKEN_EXPIRY_TIME")
	private LocalDateTime tokenExpiryTime;
	
	@Column(name = "CREATED_BY")
	private String creadtedBy;
	
	@Column(name = "CREATED_DATE")
	private LocalDateTime creadtedDate;

	
	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@Column(name = "UPDATED_DATE")
	private LocalDateTime updatedDate;
	

}
