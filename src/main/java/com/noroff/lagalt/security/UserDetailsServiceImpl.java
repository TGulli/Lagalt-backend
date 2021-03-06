package com.noroff.lagalt.security;

import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    /**
     * Service for handling UserDetails objects.
     */

    @Autowired
    UserRepository userRepository;

    @Autowired
    LoginAttemptService loginAttemptService;

    @Autowired
    HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //If a user is temporarily banned, give a proper warning.
        String ip = getClientIP();
        if (loginAttemptService.isBlocked(ip)){
            throw new RuntimeException("Brukeren er blokkert, vent 1 minutt før du prøver igjen");
        }

        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bruker ikke funnet med brukernavn: " + username));
        return UserDetailsImpl.build(user);

    }

    private String getClientIP(){
        //Extract IP address from HTTP request header field
        String xfHeader = request.getHeader("X-Forwarded-For");
        if(xfHeader == null){
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
