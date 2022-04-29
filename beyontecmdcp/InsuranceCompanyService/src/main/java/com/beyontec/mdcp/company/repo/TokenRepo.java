package com.beyontec.mdcp.company.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.company.model.TokenDetail;

@Repository
public interface TokenRepo extends JpaRepository<TokenDetail, Integer> {

	TokenDetail findByToken(String header);

}
