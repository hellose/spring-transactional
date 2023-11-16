package study.spring.transactional;

import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Util {

	public static void isTransactionActive() {
		boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
		log.info("active: {}", txActive);
	}

	public static void isTransactionReadOnly() {
		boolean txReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
		log.info("readOnly: {}", txReadOnly);
	}

}
