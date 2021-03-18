package com.noroff.lagalt.utility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noroff.lagalt.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FacebookTokenVerifier {
    public static User verify(String accessToken) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://graph.facebook.com/me?fields=id,name,email,picture&access_token=" + accessToken))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200){
            System.out.println(response.statusCode());
            System.out.println(response.body());
            return null;
        }
        User facebookUser = new User();

        // TODO add more data to user?
        JsonNode userData = new ObjectMapper().readTree(response.body());
        facebookUser.setName(userData.get("name").toString().replace("\"", ""));
        facebookUser.setSecret(userData.get("id").toString().replace("\"", "")); // id from facebook

        return facebookUser;
    }

}

