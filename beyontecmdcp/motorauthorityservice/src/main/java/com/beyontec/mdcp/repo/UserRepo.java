package com.beyontec.mdcp.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.model.User;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {


	public Page<User> findByUserAccount(String userType, Pageable pageable);
	
	public List<User> findByUserAccount(String userType);

	public User findByUserName(String stringCellValue);
	
	@Query("Select user from User user where user.userAccount =:userAccount order by user.creadtedDate desc")
	public Page<User> findAllByUserAccountByOrderByCreadtedDateDesc(String userAccount, Pageable pageable);

	@Query("Select user from User user where user.userAccount =:userAccount")
	public List<User> findByUser(String userAccount);

	public User findByUserId(Integer userId);

	public List<User> findByMailStatus(String mailStatus);
	
	@Query("select u.userName from User u")
	public List<String> getAllUserNames();
	
	@Query("Select user from User user where user.userAccount =:userAccount")
	public List<User> findAllByUserAccountAndstatus(String userAccount);

	@Query("Select user from User user where user.userAccount =:userAccount and user.status=:status and user.companyId=:companyId order by user.creadtedDate desc")
	public Page<User> findAllByUserAccountAndStatusAndCompanyId(String userAccount, String status, Integer companyId,Pageable pageable);

	@Query("Select user from User user where user.userAccount =:userAccount and user.status=:status and user.companyId=:companyId order by user.creadtedDate desc")
	public List<User> findcountByUserAccountAndStatusAndCompanyId(String userAccount, String status, Integer companyId);

	public List<User> findByRoleIdAndCompanyId(Integer roleId, Integer companyId);
}
