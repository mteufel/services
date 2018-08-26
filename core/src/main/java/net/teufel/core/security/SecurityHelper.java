package net.teufel.core.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.teufel.core.model.BasicLogin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Dependent
public class SecurityHelper {

    private static final Logger logger = LoggerFactory.getLogger(SecurityHelper.class);

    public static final String KEY_USER = "_u";
    public static final String KEY_PASS = "_p";
    public static final String KEY_ORIGIN = "_o";
    public static final String KEY_DB = "_d";

    private final TokenInfoProvider tokenInfoProvider;

    private final static String tokenKey = "The quick brown fox jumps over the lazy dog";

    @Inject
    public SecurityHelper(final TokenInfoProvider tokenInfoProvider) {
        this.tokenInfoProvider = tokenInfoProvider;
    }

    public boolean isValidToken(String token) {
        try {
            Claims claims = getTokenClaims(token);
            if (claims != null && claims.get(KEY_ORIGIN) != null) {
                return tokenInfoProvider.isOriginAccepts(claims.get(KEY_ORIGIN, String.class));
            }
        } catch (Exception e) {
            logger.warn("Token validation error: " + e.toString());
        }
        return false;
    }

    public <T extends BasicLogin> T getLoginFromRequest(HttpServletRequest request, Class<T> loginClass) {
        try {
            String authorizationHeader = request.getHeader("Authorization");
            String token = authorizationHeader.substring("Bearer".length()).trim();
            return getLoginFromToken(token, loginClass);

        } catch (Exception e) {
            logger.error("Error:" + e.getMessage());
        }
        return null;
    }

    public <T extends BasicLogin> T getLoginFromToken(String token, Class<T> loginClass) {
        try {
            //logger.info("token=" + token);

            Claims claims = getTokenClaims(token);
            String user = (String) claims.get(KEY_USER);
            String password = (String) claims.get(KEY_PASS);
            String db = (String) claims.get(KEY_DB);
            String tokenPayloadKey = getTokenPayloadKey(claims);

            T login = loginClass.newInstance();
            login.setUser(aesDecrypt(user, tokenPayloadKey));
            if (password != null) {
                login.setPassword(aesDecrypt(password, tokenPayloadKey));
            }
            if (db != null) {
                login.setDb(aesDecrypt(db, tokenPayloadKey));
            }
            login.setToken(token);

            return login;

        } catch (Exception e) {
            logger.error("Error:" + e.getMessage());
        }
        return null;
    }

    public String getUsernameFromToken(String token) {
        try {
            //logger.info("token=" + token);

            Claims claims = getTokenClaims(token);
            String user = (String) claims.get(KEY_USER);
            return aesDecrypt(user, getTokenPayloadKey(claims));

        } catch (Exception e) {
            logger.error("Error:" + e.getMessage());
        }
        return null;
    }

    private Claims getTokenClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getTokenKey())
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getTokenKey() {
        return new SecretKeySpec(tokenKey.getBytes(), 0, tokenKey.getBytes().length, "DES");
    }

    private String getTokenPayloadKey(Claims claims) {
        return getTokenPayloadKey(claims.get(KEY_ORIGIN, String.class));
    }

    private String getTokenPayloadKey(String origin) {
        if (origin == null) {
            throw new SecurityException("Failed to get token payload key: origin not found!");
        }
        String tokenPayloadKey = tokenInfoProvider.getTokenPayloadKey(origin);
        if (tokenPayloadKey == null) {
            throw new SecurityException("Failed to get token payload key: key found!");
        }
        return tokenPayloadKey;
    }

    public String aesEncrypt(String data, String key) throws Exception {
        Crypt crpt = new Crypt(key);
        return crpt.encrypt(data);
    }

    public String aesDecrypt(String data, String key) throws Exception {
        Crypt crpt = new Crypt(key);
        return crpt.decrypt(data);
    }

    public String issueToken(String login) throws Exception {
        return issueToken(login, null, null);
    }

    public String issueToken(BasicLogin login, String issuer) throws Exception {
        return issueToken(login.getUser(), login.getPassword(), issuer);
    }

    public String issueToken(String login, String issuer) throws Exception {
        return issueToken(login, null, issuer);
    }

    public String issueToken(String login, String password, String issuer) throws Exception {
        return issueToken(login, password, issuer, null);
    }

    public String issueToken(String login, String password, String issuer, Map<String, String> extraClaims) throws Exception {
        Key tokenKey = getTokenKey();
        String tokenPayloadKey = getTokenPayloadKey(tokenInfoProvider.getOrigin());
        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(login)
                .setIssuedAt(new Date())
                .setExpiration(tokenInfoProvider.getExpiration())
                .claim(KEY_ORIGIN, tokenInfoProvider.getOrigin())
                .claim("_u", aesEncrypt(login, tokenPayloadKey));
        if (password != null) {
            jwtBuilder.claim(KEY_PASS, aesEncrypt(password, tokenPayloadKey));
        }
        if (extraClaims != null) {
            for (Map.Entry<String, String> c : extraClaims.entrySet()) {
                jwtBuilder.claim(c.getKey(), aesEncrypt(c.getValue(), tokenPayloadKey));
            }
        }
        if (issuer != null) {
            jwtBuilder.setIssuer(issuer);
        }
        return jwtBuilder
                .signWith(SignatureAlgorithm.HS512, tokenKey)
                .compact();
    }

}
