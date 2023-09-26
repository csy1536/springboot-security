package com.springboot.security.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.springboot.security.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{

	User getByUid(String uid);

	Optional<User> findByUid(String name);
}
