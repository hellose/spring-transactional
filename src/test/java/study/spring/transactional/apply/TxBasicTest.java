package study.spring.transactional.apply;

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
 * TransactionSynchronizationManager.isActualTransactionActive()
 * 현재 쓰레드에 트랜잭션이 적용되어 있는지 확인할 수 있는 기능
 */
@Slf4j
@SpringBootTest
public class TxBasicTest {

	@Autowired
	BasicService service;

	@TestConfiguration
	static class TransactionApplyConfig {
		@Bean
		BasicService basicService() {
			return new BasicService();
		}

	}

	@Slf4j
	static class BasicService {

		@Transactional
		public void tx() {
			log.info("call tx");
			Util.isTransactionActive();
		}

		public void nonTx() {
			log.info("call nonTx");
			Util.isTransactionActive();
		}
	}

	@Test
	void proxyCheck() {
		log.info("aop class={}", service.getClass());
		assertThat(AopUtils.isAopProxy(service)).isTrue();
	}

	@Test
	void txTest() {
		service.tx();
		service.nonTx();

	}
}
