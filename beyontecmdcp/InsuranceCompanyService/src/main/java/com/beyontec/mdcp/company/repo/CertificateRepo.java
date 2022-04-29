package com.beyontec.mdcp.company.repo;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.company.model.BranchMaster;
import com.beyontec.mdcp.company.model.CertificateDetails;
import com.beyontec.mdcp.company.model.InsuranceCompany;



@Repository
public interface CertificateRepo extends JpaRepository<CertificateDetails, Integer> {

	List<CertificateDetails> findAllByInsuranceCompany(InsuranceCompany insuranceCompany, Pageable pageable);

	List<CertificateDetails> findAllByInsuranceCompanyAndCommencingDateBetween(InsuranceCompany insuranceCompany,
			Date date1, Date date2, Pageable pageable);

	@Query("select count(certificateDetails.insuranceCompany) from CertificateDetails certificateDetails where certificateDetails.insuranceCompany=:insuranceCompany")
	public Long countCompaniesByCompanyName(@Param("insuranceCompany") InsuranceCompany insuranceCompany);

//	List<CertificateDetails> findAllByInsuranceCompanyAndIssuedBy(InsuranceCompany insuranceCompany, Integer issuedBy,
//			Pageable pageable);
	
	public CertificateDetails findByCertificateSerialNumber(String serialNo);

	public List<CertificateDetails> findAllByInsuranceCompanyAndStatus(InsuranceCompany insuranceCompany, Integer status);

	@Query("select count(c) from CertificateDetails c where c.insuranceCompany=:insuranceCompany")
	public Integer countByCompany(@Param("insuranceCompany") InsuranceCompany company);

	List<CertificateDetails> findAllByInsuranceCompanyAndBranchAndUploadedByInAndCreatedDateBetweenOrderByCreatedDateDesc(InsuranceCompany insuranceCompany,
			BranchMaster branch, List<Integer> userList,LocalDateTime startDate, LocalDateTime endDate, Pageable pagable);

	List<CertificateDetails> findAllByInsuranceCompanyAndBranchAndUploadedByInAndCreatedDateBetweenOrderByCreatedDateDesc(InsuranceCompany insuranceCompany,
			BranchMaster branch, List<Integer> userList,LocalDateTime startDate, LocalDateTime endDate);

	List<CertificateDetails> findAllByInsuranceCompanyAndUploadedBy(InsuranceCompany insuranceCompany, Integer uploadedBy,
			Pageable pageable);

	List<CertificateDetails> findAllByInsuranceCompanyAndUploadedBy(InsuranceCompany insuranceCompany, Integer uploadedBy);


	@Query("Select certificate from CertificateDetails certificate where (certificate.certificateSerialNumber like %:certificateSerialNumber% OR certificate.insured like %:insured% OR certificate.registartionNumber like %:registartionNumber% OR certificate.policyNumber like %:policyNumber% OR certificate.status=:status) AND certificate.uploadedBy IN (:uploadedBy) AND certificate.insuranceCompany=:insuranceCompany AND certificate.branch=:branch")
	public Page<CertificateDetails> findByLikeSearchBranch(String certificateSerialNumber, String insured, String registartionNumber, String policyNumber, Integer status, List<Integer> uploadedBy, InsuranceCompany insuranceCompany, BranchMaster branch, Pageable pageable);
	
	@Query("Select certificate from CertificateDetails certificate where (certificate.certificateSerialNumber like %:certificateSerialNumber% OR certificate.insured like %:insured% OR certificate.registartionNumber like %:registartionNumber% OR certificate.policyNumber like %:policyNumber% OR certificate.status=:status) AND certificate.uploadedBy IN (:uploadedBy) AND certificate.insuranceCompany=:insuranceCompany AND certificate.branch=:branch")
	public List<CertificateDetails> findByLikeSearchBranch1(String certificateSerialNumber, String insured, String registartionNumber, String policyNumber, Integer status, List<Integer> uploadedBy, InsuranceCompany insuranceCompany, BranchMaster branch);
	
	@Query("Select certificate from CertificateDetails certificate where (certificate.certificateSerialNumber like %:certificateSerialNumber% OR certificate.insured like %:insured% OR certificate.registartionNumber like %:registartionNumber% OR certificate.policyNumber like %:policyNumber% OR certificate.status=:status) AND certificate.uploadedBy IN (:uploadedBy) AND certificate.insuranceCompany=:insuranceCompany ")
	public Page<CertificateDetails> findByLikeSearch(String certificateSerialNumber, String insured, String registartionNumber, String policyNumber, Integer status, List<Integer> uploadedBy, InsuranceCompany insuranceCompany, Pageable pageable);
	
	@Query("Select certificate from CertificateDetails certificate where (certificate.certificateSerialNumber like %:certificateSerialNumber% OR certificate.insured like %:insured% OR certificate.registartionNumber like %:registartionNumber% OR certificate.policyNumber like %:policyNumber% OR certificate.status=:status) AND certificate.uploadedBy IN (:uploadedBy) AND certificate.insuranceCompany=:insuranceCompany ")
	public List<CertificateDetails> findByLikeSearch1(String certificateSerialNumber, String insured, String registartionNumber, String policyNumber, Integer status, List<Integer> uploadedBy, InsuranceCompany insuranceCompany);
	
	@Query("Select certificate from CertificateDetails certificate where (certificate.certificateSerialNumber like %:certificateSerialNumber% OR certificate.insured like %:insured%) AND certificate.uploadedBy IN (:uploadedBy) AND certificate.insuranceCompany=:insuranceCompany AND certificate.branch=:branch")
	public List<CertificateDetails> findByLikeSearch(String certificateSerialNumber, String insured, List<Integer> uploadedBy,
			InsuranceCompany insuranceCompany, BranchMaster branch);

	//@Query("Select certificate from CertificateDetails certificate where (certificate.issuedBy like %:issuedBy%) AND certificate.insuranceCompany=:insuranceCompany AND certificate.status=:status  AND certificate.uploadedBy=:uploadedBy AND certificate.expiryDate BETWEEN :startDate AND :endDate")
	//public Page<CertificateDetails> findByLikeCertificateSearchReport(InsuranceCompany insuranceCompany, Integer status, String issuedBy, Integer uploadedBy, Date startDate, Date endDate, Pageable pageable);
	
	public CertificateDetails findByRegistartionNumber(String vehicleNo);
	
	List<CertificateDetails> findAllByInsuranceCompanyAndUploadedByInAndCreatedDateBetweenOrderByCreatedDateDesc(InsuranceCompany insuranceCompany,
			List<Integer> userList,LocalDateTime startDate, LocalDateTime endDate);
	
	List<CertificateDetails> findAllByInsuranceCompanyAndUploadedByIn(InsuranceCompany insuranceCompany,
			List<Integer> userList);

	List<CertificateDetails> findAllByInsuranceCompany(InsuranceCompany companyData);
	@Query("Select certificate from CertificateDetails certificate where (certificate.issuedBy like %:issuedBy%) AND certificate.status=:status  AND certificate.uploadedBy=:uploadedBy AND certificate.expiryDate=:startDate")
	List<CertificateDetails> findByLikeCertificateSearchReport(Integer status, String issuedBy, Integer uploadedBy, Date startDate);

	List<CertificateDetails> findAllByInsuranceCompanyAndUploadedByInAndCreatedDateBetweenOrderByCreatedDateDesc(
			InsuranceCompany insuranceCompany, List<Integer> userList, LocalDateTime convertToLocalDateTimeViaInstant,
			LocalDateTime now, Pageable pagable);
	

	public CertificateDetails findByPolicyNumber(String policyNumber);

	public	boolean existsByInsuranceCompany(InsuranceCompany insuranceCompany);
	
	public List<CertificateDetails> findByMailStatus(String mailStatus);

	public List<CertificateDetails> findAllByChassisNumber(String chassisNo);
	
	public CertificateDetails findByChassisNumber(String chassisNo);
	
}

