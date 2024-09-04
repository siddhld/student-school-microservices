package com.jwt.springsecurity;

import com.jwt.springsecurity.service.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringSecurityApplicationTests {

	@Autowired
	private RedisService redisService;
	@Value("${jwt.access.token.expiry.second}")
	private long jwtAccessTokenExpiryInSecond;
	@Test
	void contextLoads() {
		System.err.println(jwtAccessTokenExpiryInSecond);
		redisService.set("kala","kalusingh@gmail.com", jwtAccessTokenExpiryInSecond);
		String email = redisService.get("kala", String.class);
		System.err.println(email);
	}

}
