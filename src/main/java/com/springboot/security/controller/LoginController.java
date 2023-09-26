package com.springboot.security.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.security.entity.User;
import com.springboot.security.service.SignService;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/sign-api")
@Slf4j
public class LoginController {
	
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	private SignService signService;

	@PostMapping("/sign-up")
	public ResponseEntity<String> join(
			@RequestParam(value="Id", required = true) String id,
			@RequestParam(value="Password", required = true) String password,
			@RequestParam(value="Name", required = true) String name,
			@RequestParam(value="Role", required = true) String role) throws Exception {
		
		logger.info("[signUp] 회원가입 수행 시작 ::: id:{}, password:{}, name:{}, role:{}", id, password, name, role);
		User user = signService.signUp(id, name, password, role);		
		logger.info("[signUp] 회원가입 완료 ::: id:{}", id);
		
		return ResponseEntity.ok().body(user.toString());
	}
	
	@PostMapping("/sign-in")
	public ResponseEntity<String> login(
			@RequestParam(value="Id", required = true) String id,
			@RequestParam(value="Password", required = true) String password) throws Exception{
		
		logger.info("[login] 로그인 시도 ::: id:{} / password:{}", id, password);
		String token = signService.signIn(id, password);
		
		if(StringUtils.isBlank(token))
			throw new Exception("토큰이 발행되지 않았습니다.");
		
		return ResponseEntity.ok().body(token);
	}
	
	@GetMapping("/exception")
	public String acessException() {
		//throw new RuntimeException("접근이 금지되었습니다.");
		return "exception";
	}
	
}
