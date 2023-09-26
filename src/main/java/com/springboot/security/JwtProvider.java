package com.springboot.security;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.springboot.security.service.UserDetailService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {
	
	private final Logger logger = LoggerFactory.getLogger(JwtProvider.class);
	private final UserDetailService userDetailService;
	public final String AUTHORIZATION_HEADER = "Authorization";
	
	@Value("${springboot.jwt.secret}")
	private String secretKey;
	
	private long expiredMs = 1000 * 60 * 60L; //1시간
	
	/**
	 * @PostConstruct는 해당 객체가 빈 객체로 주입이 되면 맨처음 실행되는 메서드를 가리킨다.
	 */
	@PostConstruct
	protected void init() {
		logger.info("[init] secretKey 초기화 시작, secretKey ::: {}", secretKey);
		secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
		logger.info("[init] secretKey 초기화 완료, secretKey ::: {}", secretKey);
	}
	
	/**
	 * 토큰 생성
	 * @param userName
	 * @return
	 * @throws Exception
	 */
	public String createToken(String userName) throws Exception {
		Claims claims = Jwts.claims().setSubject(userName);
		claims.put("userName", userName);
		
		Date now = new Date();
		
		String token =  Jwts.builder()
						.setClaims(claims)
						.setIssuedAt(now)
						.setExpiration(new Date(now.getTime() + expiredMs))
						.signWith(SignatureAlgorithm.HS256, secretKey)
						.compact();
		
		logger.info("[createToken] 토큰 생성 완료 : {}", token);
		
		return token;
	}

	
	/**
	 * 필터에서 인증이 성공했을때 secutityContextHolder에 저장할 Authentication 생성하는 역할
	 * @param token
	 * @return	 * 
	 */
	public Authentication getAuthentication(String token) {
		logger.info("[getAuthentication] 토큰 인증 정보 조회 시작");
		UserDetails userDetails =  userDetailService.loadUserByUsername(getUsername(token));
		logger.info("[getAuthentication] 토큰 인증 정보 조회 완료, UserDetails username ::: {}", userDetails.getUsername());
		
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}
	
	/**
	 * 토큰 내에 username 추출
	 * @param token
	 * @return
	 */
	public String getUsername(String token) {
		logger.info("[getUsername] 토큰 기반 회원 구별 정보 추출");
		String info = Jwts.parserBuilder()
						.setSigningKey(secretKey)
						.build()
						.parseClaimsJws(token)
						.getBody()
						.getSubject();
		logger.info("[getUsername] 토큰 기반 회원 구별 정보 추출 완료 , info ::: {}", info);
		return info;
	}
	
	/**
	 * httpservletRequest의 헤더에서 토큰 추출
	 * @param request
	 * @return
	 */
	public String resolveToken(HttpServletRequest request) {
		String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
		
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
		
		return null;
	}
	
	/**
	 * token 유효 체크
	 * @param token
	 * @return
	 */
	public boolean validateToken(String token) {
		logger.info("[validateToken] 토큰 유효 기간 체크 시작");
		try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {            
            logger.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {            
            logger.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {            
            logger.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {            
            logger.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
	}

}
