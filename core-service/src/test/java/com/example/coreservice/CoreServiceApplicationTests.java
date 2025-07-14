package com.example.coreservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(classes = CoreServiceApplication.class)
class CoreServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
