package com.beyontec.mdcp.authservice.security;



import com.beyontec.mdcp.authservice.model.User;
import com.beyontec.mdcp.authservice.repository.UserRepository;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {

    private final int MAX_ATTEMPT = 3;
    private LoadingCache<String, Integer> attemptsCache;

    @Autowired
    private UserRepository userRepository;

    public LoginAttemptService() {
        attemptsCache = CacheBuilder
                .newBuilder()
                .expireAfterWrite(1, TimeUnit.DAYS)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(final String key) {
                        return 0;
                    }
                });
    }

    public void loginSucceeded(final String key) {
        attemptsCache.invalidate(key);
    }

    public void loginFailed(String key) {
        int attempts = 0;
        try {
            attempts = attemptsCache.get(key);
        } catch (final ExecutionException e) {
            attempts = 0;
        }
        if(++attempts >= MAX_ATTEMPT)
            userRepository.blockUser(key);
        attemptsCache.put(key, attempts);
    }

    public boolean isBlocked(final String key) {
        Optional<User> optionalUser = userRepository.findByUserName(key);
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            return "AL".equals(user.getStatus());
            /*try {
                 attemptsCache.get(key) >= MAX_ATTEMPT;
            } catch (final ExecutionException e) {
                return false;
            }*/
        }
        return false;
    }
}
