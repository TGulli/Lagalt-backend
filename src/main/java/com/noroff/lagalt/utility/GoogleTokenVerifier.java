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

    private static final String clientId = "119104222557-up2cfjpdaijqfnchovd4t33blblu11nv.apps.googleusercontent.com";

    public static User verifiyGoogleToken(String idTokenString) throws IOException, GeneralSecurityException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            Payload payload = idToken.getPayload();

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
