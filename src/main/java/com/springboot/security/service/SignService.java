package com.springboot.security.service;

import com.springboot.security.entity.User;

public interface SignService {
	
	 String signIn(String name, String password) throws Exception;
	 
	 User signUp(String id, String name, String password, String role) throws Exception;

}
