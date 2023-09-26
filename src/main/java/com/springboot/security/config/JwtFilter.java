package com.springboot.security.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.springboot.security.JwtProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtFilter extends OncePerRequestFilter {
	
	private final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
	private final JwtProvider jwtProvider;
	
	public JwtFilter(JwtProvider jwtProvider) {
		this.jwtProvider = jwtProvider;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = jwtProvider.resolveToken(request);
		logger.info("[doFilterInternal] token 값 추출 ::: {}", token);
		
		logger.info("[doFilterInternal] token 유효성 체크 시작 ");
		if(token != null &&  jwtProvider.validateToken(token)) {
			Authentication authentication = jwtProvider.getAuthentication(token);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			logger.info("[doFilterInternal] token 유효성 체크 완료 ");
		}
		
		filterChain.doFilter(request, response);
		
	}
	

}
