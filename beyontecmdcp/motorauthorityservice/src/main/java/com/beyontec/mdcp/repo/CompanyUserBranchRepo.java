package com.beyontec.mdcp.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.model.BranchMaster;
import com.beyontec.mdcp.model.CompanyBranch;
import com.beyontec.mdcp.model.CompanyUserBranch;
import com.beyontec.mdcp.model.User;

@Repository
public interface CompanyUserBranchRepo extends JpaRepository<CompanyUserBranch, Integer>{

	public List<CompanyUserBranch> findByBranch(BranchMaster branch);

	public Page<CompanyUserBranch> findByUser(User user, Pageable pagable);

	public List<CompanyUserBranch> findByUser(User user);
	
	@Query("select count(c) from CompanyUserBranch c where c.user=:user")
	public Long countByBranchCount(@Param("user") User user);

	public List<CompanyUserBranch> findByCompanyBranchIn(List<CompanyBranch> companyBranch);

	public Page<CompanyUserBranch> findByCompanyBranchIn(List<CompanyBranch> companyBranch, Pageable pagable);
}
