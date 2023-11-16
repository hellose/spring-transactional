package study.spring.transactional.apply;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import study.spring.transactional.Util;

/*
 * 클래스에 선언된 경우 모든 메서드 레벨에 적용되며, 메서드에도 적용된 경우 메서드에 해닿하는 것이 우선순위가 높다.
 */
@Slf4j
@SpringBootTest
public class TxPrecedenceTest {

	@Autowired
	PrecedenceService service;

	@TestConfiguration
	static class TxPrecedenceTestConfig {
		@Bean
		public PrecedenceService precedenceService() {
			return new PrecedenceService();
		}
	}

	@Slf4j
	@Transactional(readOnly = true)
	static class PrecedenceService {

		@Transactional(readOnly = false)
		public void write() {
			log.info("call write");
			Util.isTransactionActive();
		}

		public void read() {
			log.info("call read");
			Util.isTransactionActive();
		}
	}

	@Test
	void precedenceTest() {
		service.write();
		service.read();
	}
}
