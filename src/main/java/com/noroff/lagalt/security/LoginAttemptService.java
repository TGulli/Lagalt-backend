package com.noroff.lagalt.security;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {

    /**
     * Service to manage the amount of attempts a user has to login
     * in to a given username
     */

    // Five attempts set as a maximum
    private final int maxAttempts = 5;
    private LoadingCache<String, Integer> attemptsCache;

    public LoginAttemptService(){
        super();
        attemptsCache = CacheBuilder.newBuilder()
                // The attemps are reset after 1 minute, giving the user five more attempts.
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .build(new CacheLoader<String, Integer>() {
                    public Integer load(String key) {
                        return 0;
                    }
                });
    }

    public void loginSucceeded(String key){
        attemptsCache.invalidate(key);
    }

    public void loginFailed(String key){
        int attempts = 0;
        try{
            attempts = attemptsCache.get(key);
        }catch (ExecutionException e){
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(key, attempts);
    }

    public boolean isBlocked(String key){
        try{
            return attemptsCache.get(key) >= maxAttempts;
        } catch (ExecutionException e){
            return false;
        }
    }
}


