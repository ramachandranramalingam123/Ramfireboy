package com.beyontec.mdcp.company.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.beyontec.mdcp.company.model.User;


@Repository
public interface UserRepo extends JpaRepository<User, Integer> {

	public Page<User> findByUserAccount(String userType, Pageable pageable);
	
	public List<User> findByUserAccount(String userType);

	public User findByUserId(Integer userId);
	
	@Query("SELECT u.userId from User u WHERE u.companyId= :companyId AND u.userTypeId= :userTypeId")
	public List<Integer> findByUserIdAndCompany(@Param(value = "companyId")Integer companyId,@Param(value = "userTypeId")Integer userTypeId );

	public User findByUserName(String userName);
	
	public List<User> findByRoleId(Integer roleId);

}
