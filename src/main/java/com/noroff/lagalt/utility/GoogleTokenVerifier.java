package com.noroff.lagalt.utility;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.noroff.lagalt.model.User;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class GoogleTokenVerifier {

    private static final String clientId = "119104222557-up2cfjpdaijqfnchovd4t33blblu11nv.apps.googleusercontent.com";

    public static User verifiyGoogleToken(String idTokenString) throws IOException, GeneralSecurityException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {

            Payload payload = idToken.getPayload();

            System.out.println(payload);
            // Print user identifier
            String userId = payload.getSubject();
            System.out.println("User ID: " + userId);

            // Get profile information from payload
            String email = payload.getEmail();
            boolean emailVerified = payload.getEmailVerified();
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");

            // Use or store profile information
            System.out.println("Email: " + email);
            System.out.println("emailVerified: " + emailVerified);
            System.out.println("name: " + name);
            System.out.println("pictureUrl: " + pictureUrl);
            System.out.println("locale: " + locale);
            System.out.println("familyName: " + familyName);
            System.out.println("givenName: " + givenName);

            User gUser = new User();
            gUser.setUsername(name);
            gUser.setSecret(userId);
            gUser.setHidden(false);


            return gUser;
        } else {
            System.out.println("Invalid ID token.");
            return null;
        }
    }

}
