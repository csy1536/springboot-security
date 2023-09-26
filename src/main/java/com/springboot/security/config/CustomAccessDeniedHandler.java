package com.springboot.security.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author user
 * AccessDeniedHandler는 접근 권한이 없는 리소스에 접근한 경우 발생한 예외 
 *
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
	
	private final Logger logger = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {
		
		logger.info("[handle] 접근 막혔을 경우 경로 리다이렉트");
		//response.sendRedirect("/sign-api/exception");
		response.sendError(HttpServletResponse.SC_FORBIDDEN);
		
	}

}
