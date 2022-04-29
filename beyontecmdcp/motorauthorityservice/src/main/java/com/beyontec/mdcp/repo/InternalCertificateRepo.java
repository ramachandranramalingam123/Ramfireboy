package com.beyontec.mdcp.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.model.InsuranceCompany;
import com.beyontec.mdcp.model.InternalCertificate;



@Repository
public interface InternalCertificateRepo extends JpaRepository<InternalCertificate, Integer>{
	
	
	
	@Query("SELECT SUM(ic.allocatedCertificates) from InternalCertificate ic ")	
	public Long getTotalSumOfAllocatedCertificateByAll();
	

}
