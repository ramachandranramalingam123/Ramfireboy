package com.beyontec.mdcp.authservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beyontec.mdcp.authservice.model.UserAudit;
import com.beyontec.mdcp.authservice.repository.UserAuditRepo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserAuditService {

	@Autowired UserAuditRepo userAuditRepo;

	public int getUserAuditCount(Integer userId) {
		return userAuditRepo.getUserAuditCount(userId);
	}

	public void deleteUserAudit(Integer userId) {
		userAuditRepo.deleteUserAudit(userId);
	}

	public void saveUserAudit(UserAudit userAudit) {
		userAuditRepo.save(userAudit);
	}
}
