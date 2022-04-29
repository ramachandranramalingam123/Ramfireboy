package com.beyontec.mdcp.authservice.security.userdetails;


import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.beyontec.mdcp.authservice.exception.UserAccountLockedException;
import com.beyontec.mdcp.authservice.model.User;
import com.beyontec.mdcp.authservice.repository.UserRepository;
import com.beyontec.mdcp.authservice.security.LoginAttemptService;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (loginAttemptService.isBlocked(username))
            throw new UserAccountLockedException("Account is locked");
        Optional<User> user = userRepository.findByUserName(username);
        return user.map(UserDetailsImpl::new).orElseGet(this::guestUser);

    }

    private UserDetailsImpl guestUser() {
        User guestUser = new User();
        guestUser.setUserName("GUEST");
        guestUser.setPassword("GUEST");
        return new UserDetailsImpl(guestUser, new HashSet<GrantedAuthority>(Collections.singletonList(new SimpleGrantedAuthority("GUEST"))));
    }
}
