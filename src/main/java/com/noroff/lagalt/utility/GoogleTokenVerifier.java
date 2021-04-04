package com.noroff.lagalt.utility;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.noroff.lagalt.user.model.LoginMethod;
import com.noroff.lagalt.user.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

public class GoogleTokenVerifier {
    /**
     * Class used to verify tokens given by google
     */

    private static final String clientId = "119104222557-up2cfjpdaijqfnchovd4t33blblu11nv.apps.googleusercontent.com";

    public static User verifiyGoogleToken(String idTokenString) throws IOException, GeneralSecurityException {

        // Verifies the actual token as a legitimate one
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();
        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            // If the token is valid, extract payload
            Payload payload = idToken.getPayload();

            // Create a user in our own system.
            User gUser = new User();
            gUser.setUsername((String) payload.get("name"));
            gUser.setEmail(payload.getEmail());
            gUser.setName((String) payload.get("given_name") + " " + (String) payload.get("family_name"));
            gUser.setHidden(false);
            gUser.setLocale((String) payload.get("locale"));
            gUser.setBio("");
            gUser.setLoginMethod(LoginMethod.google);

            return gUser;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Feil ved å verifisere akksesstoken Google login.");
        }
    }

}
