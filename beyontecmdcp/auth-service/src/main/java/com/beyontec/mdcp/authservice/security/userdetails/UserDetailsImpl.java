package com.beyontec.mdcp.authservice.security.userdetails;


import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.beyontec.mdcp.authservice.model.User;

import java.util.Collection;
import java.util.HashSet;

@NoArgsConstructor
@Component
public class UserDetailsImpl implements UserDetails {

    private User user;
    private Collection<GrantedAuthority> authorities = new HashSet<>();


    public UserDetailsImpl(User user) {
        this(user, new HashSet<>());
    }

    public UserDetailsImpl(User user, HashSet<GrantedAuthority> authorities) {
        this.user = user;
        this.authorities.addAll(authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}


