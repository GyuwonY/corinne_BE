package com.corinne.corinne_be.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.corinne.corinne_be.security.UserDetailsImpl;
import java.util.Date;

public final class JwtTokenUtils {

    private static final int SEC = 1;
    private static final int MINUTE = 60 * SEC;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    private static final int JWT_TOKEN_VALID_SEC = 3 * DAY;

    private static final int JWT_TOKEN_VALID_MILLI_SEC = JWT_TOKEN_VALID_SEC * 1000;

    public static final String CLAIM_EXPIRED_DATE = "EXPIRED_DATE";
    public static final String CLAIM_USER_ID = "USER_ID";
    public static final String JWT_SECRET = "jwt_secret_!@#$%";

    public static String generateJwtToken(UserDetailsImpl userDetails) {
        String token = null;
        try {
            token = JWT.create()
                    .withIssuer("sparta")
                    .withClaim(CLAIM_USER_ID, userDetails.getUser().getUserId())
                    // 토큰 만료 일시 = 현재 시간 + 토큰 유효기간)
                    .withClaim(CLAIM_EXPIRED_DATE, new Date(System.currentTimeMillis() + JWT_TOKEN_VALID_MILLI_SEC))
                    .sign(generateAlgorithm());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return token;
    }

    private static Algorithm generateAlgorithm() {
        return Algorithm.HMAC256(JWT_SECRET);
    }
}
