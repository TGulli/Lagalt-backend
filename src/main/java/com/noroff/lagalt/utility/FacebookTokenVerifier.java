package com.noroff.lagalt.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noroff.lagalt.user.model.LoginMethod;
import com.noroff.lagalt.user.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FacebookTokenVerifier {

    /**
     * Method to verify access tokens returned by the facebook login
     */

    public static User verify(String accessToken) {

        try{

            // Validates the request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://graph.facebook.com/me?fields=id,name,email,picture,locale&access_token=" + accessToken))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode userData = new ObjectMapper().readTree(response.body());
            User facebookUser = new User();

            // Generates a new user
            facebookUser.setName(userData.get("name").toString().replace("\"", ""));
            facebookUser.setEmail(userData.get("email").toString().replace("\"", ""));
            facebookUser.setUsername(facebookUser.getEmail());
            facebookUser.setLoginMethod(LoginMethod.facebook);
            facebookUser.setHidden(false);

            return facebookUser;
        } catch (IOException | InterruptedException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Feil ved å verifisere akksesstoken Facebook login.");
        }
    }

}

