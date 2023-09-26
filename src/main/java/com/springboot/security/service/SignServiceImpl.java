package com.springboot.security.service;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.springboot.security.JwtProvider;
import com.springboot.security.entity.User;
import com.springboot.security.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SignServiceImpl implements SignService {
	
	private static final Logger logger = LoggerFactory.getLogger(SignServiceImpl.class);

	@Autowired
	public PasswordEncoder passwordEncoder;
	
	@Autowired
	public UserRepository userRepository;
	
	@Autowired
	public JwtProvider jwtProvider;
	
	/**
	 * 로그인 후 토큰 전달
	 * @throws Exception 
	 */
	@Override
	public String signIn(String name, String password) throws Exception {
		User user = userRepository.getByUid(name);	
		
		if(user == null) {
			logger.error("[ {} ] 사용자는 존재하지 않습니다.", name);
			throw new UsernameNotFoundException("사용자가 존재하지 않습니다.");
		}
		
		logger.info("[signIn] 로그인 id ::: {}", user.getUid());
		
		if(!passwordEncoder.matches(password, user.getPassword())) {
			throw new RuntimeException("Failed Login");
		}
		
		return jwtProvider.createToken(name);
	}

	/**
	 * 회원가입
	 */
	@Override
	public User signUp(String id, String name, String password, String role) throws Exception {
		logger.info("[signUp] 회원가입 정보 전달");
		
		User user;
		if(role.equalsIgnoreCase("admin")) {
			user = User.builder()
					.uid(id)
					.name(name)
					.password(passwordEncoder.encode(password))
					.role(Collections.singletonList("ROLE_ADMIN"))
					.build();
		} else {
			user = User.builder()
					.uid(id)
					.name(name)
					.password(passwordEncoder.encode(password))
					.role(Collections.singletonList("ROLE_USER"))
					.build();
		}
		
		User saveUser = userRepository.save(user);
		
		return saveUser;
	}
	
}
