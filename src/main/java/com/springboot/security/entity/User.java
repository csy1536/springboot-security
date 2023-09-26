package com.springboot.security.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table
public class User implements UserDetails{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(nullable = false, unique = true)
	private String uid;
	
	@JsonProperty(access = Access.WRITE_ONLY) // 쓰려는 경우에만 접근 허용, 응답결과 생성시에는 제외
	@Column(nullable = false)
	private String password;
	
	@Column(nullable = false)
	private String name;
	
	@ElementCollection(fetch = FetchType.EAGER) // 컬렉션은 테이블에 넣을수 없기에 별도의 테이블 생성하여 컬렉션 관리
	@Builder.Default // 특정필드의 값으로 초기화 할떄 쓰임
	private List<String> role = new ArrayList<>();

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return role.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	}

	@JsonProperty(access = Access.WRITE_ONLY)
	@Override
	public String getUsername() {
		return this.uid;
	}

	@JsonProperty(access = Access.WRITE_ONLY)
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@JsonProperty(access = Access.WRITE_ONLY)
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@JsonProperty(access = Access.WRITE_ONLY)
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@JsonProperty(access = Access.WRITE_ONLY)
	@Override
	public boolean isEnabled() {
		return true;
	}

}
