package com.noroff.lagalt.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.noroff.lagalt.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/*
    This class is our extension of the default UserDetails Spring Security has.
    It is extended to provide a way of accessing all the User entity fields as well as converting our
    roles into GrantedAuthority.
 */

public class JwtUserDetails implements UserDetails {
    // Fields
    private Long id;

    private String username;

    private String email;

    // We dont want the password to be shown, so we simply ignore it.
    @JsonIgnore
    private String password;

    // Constructor
    public JwtUserDetails(Long id, String username, String email,
                           String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Build method to create a new UserDetailsImpl, this method converts our Role into GrantedAuthority
    public static JwtUserDetails build(User user) {
        /*
         Here we use StreamAPI to create a list of GrantedAuthority from or role names.
         SimpleGrantedAuthority has a constructor that can take an argument that represents the role.
        */

        return new JwtUserDetails(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getSecret());
    }

    // Extensions

    public String getEmail(){
        return email;
    }

    public Long getId(){
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    /*
     The following override methods have their default configurations.
     We could implement logic in our application to handle the expiration of account and credentials
     and we could make a way to lock our users out.
     These are out of the scope of our project, but have merit in real world applications.
    */

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
        JwtUserDetails user = (JwtUserDetails) o;
        return Objects.equals(id, user.id);
    }
}

