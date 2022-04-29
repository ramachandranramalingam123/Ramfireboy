package com.beyontec.mdcp.authservice.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.authservice.model.TokenDetail;



@Repository
public interface TokenRepo extends JpaRepository<TokenDetail, Integer> {

	TokenDetail findByToken(String header);

	@Transactional
	@Modifying
	@Query("delete from TokenDetail td where td.userId=:userId")
	void deleteUserToken(int userId);

	@Query(" from TokenDetail td where td.userId=:userId ORDER BY td.tokenExpiryTime DESC")
	List<TokenDetail> getUserTokenDetails(int userId);
	
	
	
	
	
	

}
