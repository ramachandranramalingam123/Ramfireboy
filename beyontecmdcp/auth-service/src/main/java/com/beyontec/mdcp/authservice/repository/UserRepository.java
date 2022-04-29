package com.beyontec.mdcp.authservice.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import javax.transaction.Transactional;

import com.beyontec.mdcp.authservice.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Integer>{
	
    @Transactional
    @Modifying
    @Query("update User u set u.status = 'AL' where u.userName = :userName")
    void blockUser(@Param("userName") String userName);
    
    
   public Optional<User> findByUserName(String userName);

   @Query("Select u from User u where u.userName= :userName")
   public User getUserByUserName(@Param("userName") String userName);


  public User findByUserId(Integer issuedBy);


  

}
