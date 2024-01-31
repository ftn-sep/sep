package org.psp.service;

import org.sep.exceptions.WrongApiKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public static void checkIfApiKeysMatches(String apiKey, String sellersApiKey, PasswordEncoder encoder) {
        if (!encoder.matches(apiKey, sellersApiKey))
            throw new WrongApiKeyException(apiKey);
    }
}
