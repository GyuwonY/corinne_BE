package com.corinne.corinne_be.config;


import com.corinne.corinne_be.security.filter.FilterSkipMatcher;
import com.corinne.corinne_be.security.filter.FormLoginFilter;
import com.corinne.corinne_be.security.filter.JwtAuthFilter;
import com.corinne.corinne_be.security.handler.AuthFailureHandler;
import com.corinne.corinne_be.security.handler.CustomLogoutSuccessHandler;
import com.corinne.corinne_be.security.handler.FormLoginSuccessHandler;
import com.corinne.corinne_be.security.jwt.HeaderTokenExtractor;
import com.corinne.corinne_be.security.provider.FormLoginAuthProvider;
import com.corinne.corinne_be.security.provider.JWTAuthProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity // 스프링 Security 지원을 가능하게 함
@EnableGlobalMethodSecurity(securedEnabled = true) // @Secured 어노테이션 활성화
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JWTAuthProvider jwtAuthProvider;
    private final HeaderTokenExtractor headerTokenExtractor;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;

    public WebSecurityConfig(
            JWTAuthProvider jwtAuthProvider,
            HeaderTokenExtractor headerTokenExtractor,
            CustomLogoutSuccessHandler customLogoutSuccessHandler
    ) {
        this.jwtAuthProvider = jwtAuthProvider;
        this.headerTokenExtractor = headerTokenExtractor;
        this.customLogoutSuccessHandler = customLogoutSuccessHandler;
    }

    @Bean
    public BCryptPasswordEncoder encodePassword() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        auth
                .authenticationProvider(formLoginAuthProvider())
                .authenticationProvider(jwtAuthProvider);
    }

    @Override
    public void configure(WebSecurity web) {
        // h2-console 사용에 대한 허용 (CSRF, FrameOptions 무시)
        web
                .ignoring()
                .antMatchers("/h2-console/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.headers().frameOptions().disable()
                .and()
                .cors();

        /*
         * 1.
         * UsernamePasswordAuthenticationFilter 이전에 FormLoginFilter, JwtFilter 를 등록합니다.
         * FormLoginFilter : 로그인 인증을 실시합니다.
         * JwtFilter       : 서버에 접근시 JWT 확인 후 인증을 실시합니다.
         */
        http
                .addFilterBefore(formLoginFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);

        http.authorizeRequests()
                .anyRequest()
                .permitAll()
                .and()
                .formLogin().usernameParameter("userEmail")  // 아이디 파라미터명 설정
                .and()// [로그아웃 기능]
                .logout()
                // 로그아웃 요청 처리 URL
                .logoutUrl("/user/logout")
                .logoutSuccessHandler(customLogoutSuccessHandler)
                .permitAll()
                .and()
                .exceptionHandling()
                // "접근 불가" 페이지 URL 설정
                .accessDeniedPage("/forbidden.html");
    }

    @Bean
    public FormLoginFilter formLoginFilter() throws Exception {
        FormLoginFilter formLoginFilter = new FormLoginFilter(authenticationManager());
        formLoginFilter.setFilterProcessesUrl("/user/login");
        formLoginFilter.setAuthenticationSuccessHandler(formLoginSuccessHandler());
        formLoginFilter.setAuthenticationFailureHandler(authFailureHandler());
        formLoginFilter.afterPropertiesSet();
        return formLoginFilter;
    }

    @Bean
    public FormLoginSuccessHandler formLoginSuccessHandler() {
        return new FormLoginSuccessHandler();
    }

    @Bean
    public FormLoginAuthProvider formLoginAuthProvider() {
        return new FormLoginAuthProvider(encodePassword());
    }

    @Bean
    public AuthFailureHandler authFailureHandler() {
        return new AuthFailureHandler();
    }

    private JwtAuthFilter jwtFilter() throws Exception {
        List<String> skipPathList = new ArrayList<>();

        // Static 정보 접근 허용
        skipPathList.add("GET,/images/**");
        skipPathList.add("GET,/css/**");

        // h2-console 허용
        skipPathList.add("GET,/h2-console/**");
        skipPathList.add("POST,/h2-console/**");
        skipPathList.add("GET,/stomp");
        skipPathList.add("GET,/stomp/**");
        skipPathList.add("GET,/stomp/**/**");

//        skipPathList.add("POST,/api/board/photo");
//        skipPathList.add("POST,/api/board/regist");

        // 회원 관리 API 허용
//        skipPathList.add("GET,/user/**");
        skipPathList.add("POST,/user/signup");
        skipPathList.add("POST,/user/login");
        skipPathList.add("GET,/api/board/**/**");

        skipPathList.add("GET,/api/price/rank");
        skipPathList.add("GET,/api/rank/top3");

        skipPathList.add("GET,/basic.js");

        skipPathList.add("GET,/favicon.ico");

        FilterSkipMatcher matcher = new FilterSkipMatcher(
                skipPathList,
                "/**"
        );

        JwtAuthFilter filter = new JwtAuthFilter(
                matcher,
                headerTokenExtractor
        );
        filter.setAuthenticationManager(super.authenticationManagerBean());

        return filter;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


}