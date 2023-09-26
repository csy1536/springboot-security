package com.springboot.security.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductsController {
	
	@GetMapping
	public ResponseEntity<String> getProduct() {
		return ResponseEntity.ok().body("get success");
	}
	
	@PostMapping
	public ResponseEntity<String> insertProduct() {
		return ResponseEntity.ok().body("post success");
	}

}
