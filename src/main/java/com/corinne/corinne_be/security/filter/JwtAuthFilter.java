package com.corinne.corinne_be.security.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.corinne.corinne_be.exception.CustomException;
import com.corinne.corinne_be.exception.ErrorCode;
import com.corinne.corinne_be.exception.Exception;
import com.corinne.corinne_be.security.jwt.HeaderTokenExtractor;
import com.corinne.corinne_be.security.jwt.JwtDecoder;
import com.corinne.corinne_be.security.jwt.JwtPreProcessingToken;
import com.corinne.corinne_be.security.jwt.JwtTokenUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Token 을 내려주는 Filter 가 아닌  client 에서 받아지는 Token 을 서버 사이드에서 검증하는 클레스 SecurityContextHolder 보관소에 해당
 * Token 값의 인증 상태를 보관 하고 필요할때 마다 인증 확인 후 권한 상태 확인 하는 기능
 */
public class JwtAuthFilter extends AbstractAuthenticationProcessingFilter {

    private final HeaderTokenExtractor extractor;
    private final JwtDecoder jwtDecoder;

    public JwtAuthFilter(
            RequestMatcher requiresAuthenticationRequestMatcher,
            HeaderTokenExtractor extractor,
            JwtDecoder jwtDecoder
    ) {
        super(requiresAuthenticationRequestMatcher);
        this.jwtDecoder = jwtDecoder;
        this.extractor = extractor;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws AuthenticationException, IOException {

        // JWT 값을 담아주는 변수 TokenPayload
        String tokenPayload = request.getHeader("Authorization");
        if (tokenPayload == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return null;
        }

        String nowToken = extractor.extract(tokenPayload, request);
        JwtPreProcessingToken jwtToken = new JwtPreProcessingToken(
                extractor.extract(tokenPayload, request));

        DecodedJWT decodedJWT = jwtDecoder.isValidToken(nowToken)
                .orElseThrow(() -> new CustomException(ErrorCode.NON_AVAILABLE_TOKEN));

        Date expiredDate = decodedJWT
                .getClaim(JwtTokenUtils.CLAIM_EXPIRED_DATE)
                .asDate();

        if(expiredDate.before(new Date())){
            putErrorMessage(response, "만료된 토큰입니다.");
            return null;

        }

        return super
                .getAuthenticationManager()
                .authenticate(jwtToken);
    }

    private void putErrorMessage(HttpServletResponse response, String errorMessage) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8"); // HelloData 객체
        Exception exception = new Exception();
        exception.setHttpStatus(HttpStatus.BAD_REQUEST);
        exception.setErrorMessage(errorMessage);
        String result = mapper.writeValueAsString(exception);
        response.getWriter().print(result);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult
    ) throws IOException, ServletException {
        /*
         *  SecurityContext 사용자 Token 저장소를 생성합니다.
         *  SecurityContext 에 사용자의 인증된 Token 값을 저장합니다.
         */
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);

        // FilterChain chain 해당 필터가 실행 후 다른 필터도 실행할 수 있도록 연결실켜주는 메서드
        chain.doFilter(
                request,
                response
        );
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed
    ) throws IOException, ServletException {
        /*
         *	로그인을 한 상태에서 Token값을 주고받는 상황에서 잘못된 Token값이라면
         *	인증이 성공하지 못한 단계 이기 때문에 잘못된 Token값을 제거합니다.
         *	모든 인증받은 Context 값이 삭제 됩니다.
         */
        SecurityContextHolder.clearContext();

        super.unsuccessfulAuthentication(
                request,
                response,
                failed
        );
    }
}
