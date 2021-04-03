package com.noroff.lagalt.security;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {
    private final int maxAttempts = 1000000;
    private LoadingCache<String, Integer> attemptsCache;

    public LoginAttemptService(){
        super();
        attemptsCache = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.SECONDS)
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


