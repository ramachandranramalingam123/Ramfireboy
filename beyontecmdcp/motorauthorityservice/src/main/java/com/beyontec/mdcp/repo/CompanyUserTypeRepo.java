package com.beyontec.mdcp.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.model.CompanyUserType;


@Repository
public interface CompanyUserTypeRepo extends JpaRepository<CompanyUserType, Integer>{

	public CompanyUserType findByUserTypeId(Integer userTypeId);

	public CompanyUserType findByUserType(String userTypeMap);

}
