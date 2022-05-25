package com.corinne.corinne_be.security.handler;

import com.corinne.corinne_be.security.UserDetailsImpl;
import com.corinne.corinne_be.security.jwt.JwtTokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
public class FormLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    public static final String AUTH_HEADER = "Authorization";
    public static final String TOKEN_TYPE = "BEARER";
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
                                        final Authentication authentication) {
        final UserDetailsImpl userDetails = ((UserDetailsImpl) authentication.getPrincipal());
        // Token 생성
        final String token = JwtTokenUtils.generateJwtToken(userDetails);
        try {
            response.addHeader(AUTH_HEADER, TOKEN_TYPE + " " + token);
            String data =Long.toString(userDetails.getUser().getUserId());
            String msg = new String (objectMapper.writeValueAsString(data).getBytes("UTF-8"), "ISO-8859-1");
            response.getOutputStream()
                    .println(msg);
            response.setStatus(HttpServletResponse.SC_OK);
        }catch (Exception e){

        }

    }

}
