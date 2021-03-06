package com.noroff.lagalt.security.twofa.repository;

import com.noroff.lagalt.security.twofa.model.ConfirmationToken;
import com.noroff.lagalt.user.model.User;
import org.springframework.data.repository.CrudRepository;

public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken, Long> {
    ConfirmationToken findByConfirmationToken(String confirmationToken);
    ConfirmationToken findByUser_Id(Long userId);
}
