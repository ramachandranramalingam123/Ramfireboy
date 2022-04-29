package com.beyontec.mdcp.authservice.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.beyontec.mdcp.authservice.model.UserAudit;



public interface UserAuditRepo extends JpaRepository<UserAudit, Integer> {

	@Query("Select count(u.userAuditId) from UserAudit u where u.userId=:userId")
	public int getUserAuditCount(@Param(value = "userId") Integer userId);

	@Transactional
    @Modifying
	@Query("Delete from UserAudit u where u.userId=:userId")
	public void deleteUserAudit(@Param(value = "userId") Integer userId);
}
