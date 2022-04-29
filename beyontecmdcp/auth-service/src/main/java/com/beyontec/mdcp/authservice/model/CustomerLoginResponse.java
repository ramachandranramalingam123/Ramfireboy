package com.beyontec.mdcp.authservice.model;

import java.util.List;

import com.beyontec.mdcp.authservice.dto.UserInformation;

import lombok.Data;

@Data
public class CustomerLoginResponse {

	private UserInformation userInformation;

	private List<PolicyDetails> policyDetails;

}
