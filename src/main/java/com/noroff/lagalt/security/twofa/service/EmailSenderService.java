package com.noroff.lagalt.security.twofa.service;

import com.noroff.lagalt.user.model.User;
import com.noroff.lagalt.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.mail.SendFailedException;
import java.util.Objects;
import java.util.Optional;

@Service
public class EmailSenderService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserRepository userRepository;

    @Async
    public void sendEmail(SimpleMailMessage email) {
        try{
            javaMailSender.send(email);
        } catch (MailException e){

            try {
                Optional<User> deleteMe = userRepository.findByEmail(email.getTo()[0]);
                deleteMe.ifPresent(user -> userRepository.delete(user));
            }
            catch (NullPointerException nullPointerException){
                System.out.println(nullPointerException);
            }


            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Could not send email for validation, because the email is not a valid RFC-5321 address.");
        }
    }


}
