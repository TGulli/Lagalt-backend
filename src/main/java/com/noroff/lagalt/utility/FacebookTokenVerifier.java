package com.noroff.lagalt.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.VerifyException;
import com.noroff.lagalt.model.LoginMethod;
import com.noroff.lagalt.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FacebookTokenVerifier {
    public static User verify(String accessToken) {

        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://graph.facebook.com/me?fields=id,name,email,picture,locale&access_token=" + accessToken))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            // TODO add more data to user?
            JsonNode userData = new ObjectMapper().readTree(response.body());
            User facebookUser = new User();

            facebookUser.setUsername(userData.get("name").toString().replace("\"", ""));
            facebookUser.setEmail(userData.get("email").toString().replace("\"", ""));
            facebookUser.setLoginMethod(LoginMethod.facebook);
            facebookUser.setHidden(false);

            return facebookUser;
        } catch (IOException | InterruptedException e){
            throw new VerifyException("Could not verify the accesstoken.");
        }
    }

}

