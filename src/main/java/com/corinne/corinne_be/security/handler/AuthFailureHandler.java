package com.corinne.corinne_be.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthFailureHandler implements AuthenticationFailureHandler {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
//        String username = request.getParameter("username"); // request에서 getParameter를 사용하여 "username"에 대한 정보를 가져올 수 있다.
//        response.sendRedirect("api/login?Error");
        String errormessage = "아이디와 비밀번호를 확인해주세요.";
        if (exception instanceof DisabledException) {
            errormessage = "휴면계정입니다.";
        } else if (exception instanceof CredentialsExpiredException) {
            errormessage = "비밀번호가 만료되었습니다.";
        } else if (exception instanceof BadCredentialsException) {
            errormessage = "비밀번호가 일치하지 않습니다.";
        }
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        Map<String, String> data = new HashMap<>();
        System.out.println(errormessage);
        data.put(
                "exception",
                errormessage);

        String msg = new String (objectMapper.writeValueAsString(data).getBytes("UTF-8"), "ISO-8859-1");
        response.getOutputStream()
                .println(msg);
    }
}
