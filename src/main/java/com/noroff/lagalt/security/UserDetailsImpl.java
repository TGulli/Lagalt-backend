package com.noroff.lagalt.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;


public class UserDetailsImpl implements UserDetails {
    /**
     * UserDetails implementation for internally handling users in the authentication process.
     * Essentially just a built upon version of the default UserDetails
     */

    // Fields
    private Long id;

    private String username;

    private String email;

    @JsonIgnore
    private String password;


    // Constructor
    public UserDetailsImpl(Long id, String username, String email,
                           String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Bærre ein vanlig builder, mulighet for å legge te roller eittehvert.
    public static UserDetailsImpl build(User user) {

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getSecret());
    }

    public String getEmail(){
        return email;
    }

    public Long getId(){
        return id;
    }

    // Må overrides for å bruke UserDetails...
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() { return this.password; }

    @Override
    public String getUsername() {
        return this.username;
    }

    // Our accounts will never expire
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Our accounts will never be locked
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // Our credentials never expire
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // All accounts are enabled by default
    @Override
    public boolean isEnabled() {
        return true;
    }

    // Two UserDetailImpl are equal when their Ids are equal
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }
}

