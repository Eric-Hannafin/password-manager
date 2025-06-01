package passwordmanager.security;

import javax.crypto.SecretKey;

public record UserSession(String username, SecretKey userKey) {

}