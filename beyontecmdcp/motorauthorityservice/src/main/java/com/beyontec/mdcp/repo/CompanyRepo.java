package com.beyontec.mdcp.repo;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.model.InsuranceCompany;

@Repository
public interface CompanyRepo extends JpaRepository<InsuranceCompany, String> {

	public InsuranceCompany findByCompanyId(Integer companyId);

	public InsuranceCompany findByCompanyName(String companyName);
	
	@Query("Select company from InsuranceCompany company where company.companyName like %:companyName%")
    public List<InsuranceCompany> findByCompanyNameLike(String companyName);
	
	@Query("Select company from InsuranceCompany company where company.companyName like %:companyName%")
	public Page<InsuranceCompany> findByCompanyNameContaining(String companyName, Pageable pageable);
	
	public Page<InsuranceCompany> findAll(Pageable pageable);
	
	@Query("select company.companyName from InsuranceCompany company")
	public Set<String> getAllCompanyNames();
	
	public Page<InsuranceCompany> findAllByStatusOrderByCreatedDateDesc(String status,Pageable pageable);
	
	public InsuranceCompany findByCompanyCode(String companyCode);
	
	public List<InsuranceCompany> findAllByStatus(String status);
	
	public List<InsuranceCompany> findAllByStatusOrderByCreatedDateDesc(String status);
	
}
