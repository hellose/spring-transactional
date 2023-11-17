package study.spring.transactional.issue;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import study.spring.transactional.Util;

/*
 * CallNoProxyBean1Test.png 참고
 */
@Slf4j
@SpringBootTest
public class CallNoProxyBean1Test {

	@Autowired
	CallService service;

	@TestConfiguration
	static class CallNoProxyBean1Config {
		@Bean
		public CallService callService() {
			return new CallService();
		}
	}

	@Slf4j
	static class CallService {

		public void external() {
			log.info("call external");
			Util.isTransactionActive();
			/*
			 * transaction이 필요하지 않은 부분...
			 */

			// transaction이 필요한 경우
			internal();
		}

		@Transactional
		public void internal() {
			log.info("call internal");
			Util.isTransactionActive();
		}

	}

	@Test
	void beanIsProxyObject() {
		log.info("callService class: {}", service.getClass());
	}

	@Test
	void internalCall() {
		service.internal();
	}

	@Test
	void externalCall() throws Exception {
		service.external();
	}

	@Test
	void proxyCheck() {
		log.info("aop class={}", service.getClass());
		assertThat(AopUtils.isAopProxy(service)).isTrue();
	}

}
