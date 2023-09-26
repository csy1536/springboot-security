package com.springboot.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.springboot.security.JwtProvider;

import lombok.RequiredArgsConstructor;

/**
 * @author user
 * 모든 Http 에 인증이 필요하게 됨.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final JwtProvider jwtProvider;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	
	@Bean 
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity)throws Exception{ 
		httpSecurity .httpBasic().disable() //ui가 아닌 토큰 방식 인증으로 disable // token을 사용하는 방식이기 때문에 csrf를 disable합니다. 
		.csrf().disable()
		.cors().and()
	
	// enable h2-console 
		.headers() .frameOptions() .sameOrigin()
	
	// 세션을 사용하지 않기 때문에 STATELESS로 설정 
		.and() 
		.sessionManagement()
		.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	
		.and() 
		.authorizeHttpRequests() // HttpServletRequest를 사용하는 요청들에 대한 접근제한을 설정
		.requestMatchers("/sign-api/sign-in").permitAll() // 로그인 api
		//.requestMatchers("/sign-api/exception").permitAll()
		.requestMatchers(HttpMethod.POST, "/product").hasRole("ADMIN")
		//.anyRequest().hasRole("ADMIN")	
		.anyRequest().authenticated() // 그 외 인증 없이 접근X
		
		.and()
		.exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler()) // 권한 확인 중 예외

		.and()
		.exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
		
		.and()
		.addFilterBefore(new JwtFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
		;
	
	return httpSecurity.build();
	
	}
	

}
