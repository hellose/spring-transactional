package study.spring.transactional.propagation;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import javax.sql.DataSource;

@Slf4j
@SpringBootTest
public class BasicTxTest {

    @Autowired
    PlatformTransactionManager manager;

    @TestConfiguration
    static class Config {
        @Bean
        public PlatformTransactionManager manager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }
    }

    @Test
    void commit() {
        log.info("트랜잭션 시작");
        TransactionStatus status = manager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션 커밋 시작");
        manager.commit(status);
        log.info("트랜잭션 커밋 완료");
    }

    @Test
    void rollback() {
        log.info("트랜잭션 시작");
        TransactionStatus status = manager.getTransaction(new DefaultTransactionAttribute());

        log.info("트랜잭션 롤백 시작");
        manager.rollback(status);
        log.info("트랜잭션 롤백 완료");
    }

    @Test
    void commit_commit() {
        log.info("트랜잭션1 시작");
        // Acquired Connection [HikariProxyConnection@1827192676 wrapping conn0
        TransactionStatus status1 = manager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션1 커밋");
        manager.commit(status1);

        /*
         * 내부 관리를 위해 물리 Connection을 wrapping한 HikariProxyConnection객체를 생성해서 획득함
         * HikariProxyConnection@번호 -> 번호가 다르다면 반납을 하고 새로 획득한 것으로 이해하면 된다.
         */

        log.info("트랜잭션2 시작");
        // Acquired Connection [HikariProxyConnection@560990653 wrapping conn0
        TransactionStatus status2 = manager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션2 커밋");
        manager.commit(status2);
    }

    @Test
    void commit_rollback() {
        log.info("트랜잭션1 시작");
        TransactionStatus status1 = manager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션1 커밋");
        manager.commit(status1);

        log.info("트랜잭션2 시작");
        TransactionStatus status2 = manager.getTransaction(new DefaultTransactionAttribute());
        log.info("트랜잭션2 롤백");
        manager.rollback(status2);
    }

    /*
        트랜잭션 전파: REQUIRED (default)
     */
    @Test
    void innerCommit_outerCommit() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = manager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction(): {}", outer.isNewTransaction());

        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = manager.getTransaction(new DefaultTransactionAttribute());
        log.info("inner.isNewTransaction(): {}", inner.isNewTransaction());

        // 내부 트랜잭션 커밋
        log.info("내부 트랜잭션 커밋");
        manager.commit(inner);
        log.info("inner.isCompleted: {}", inner.isCompleted());
        log.info("outer.isCompleted: {}", outer.isCompleted());

        // 외부 트랜잭션 커밋
        log.info("외부 트랜잭션 커밋");
        manager.commit(outer);
        log.info("inner.isCompleted: {}", inner.isCompleted());
        log.info("outer.isCompleted: {}", outer.isCompleted());
    }

    @Test
    void innerCommit_outerRollback() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = manager.getTransaction(new DefaultTransactionAttribute());

        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = manager.getTransaction(new DefaultTransactionAttribute());

        // 내부 트랜잭션 커밋
        log.info("내부 트랜잭션 커밋");
        manager.commit(inner);

        // 외부 트랜잭션 롤백
        log.info("외부 트랜잭션 롤백");
        manager.rollback(outer);
    }

    @Test
    void innerRollback_outerCommit() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = manager.getTransaction(new DefaultTransactionAttribute());

        log.info("내부 트랜잭션 시작");
        TransactionStatus inner = manager.getTransaction(new DefaultTransactionAttribute());

        // 내부 트랜잭션 롤백
        log.info("내부 트랜잭션 롤백");
        // 현재 물리 트랜잭션에 rollback-only 마킹 -> 트랜잭션 동기화 매니저에 rollbackOnly=true 라는 표시를 해둠
        manager.rollback(inner);

        // 외부 트랜잭션 커밋
        log.info("외부 트랜잭션 커밋");
        // 트랜잭션 동기화 매니저에 rollback-only 표시가 있는지 확인 -> 존재 -> 물리 트랜잭션 롤백 -> 트랜잭션 매니저가 UnexpectedRollbackException을 던짐
        Assertions.assertThatThrownBy(() -> manager.commit(outer)).isInstanceOf(UnexpectedRollbackException.class);
    }

    /*
        트랜잭션 전파: REQUIRED_NEW
     */
    @Test
    void required_new_innerRollback_outerCommit() {
        log.info("외부 트랜잭션 시작");
        TransactionStatus outer = manager.getTransaction(new DefaultTransactionAttribute());
        log.info("outer.isNewTransaction(): {}", outer.isNewTransaction()); // true

        log.info("내부 트랜잭션 시작");
        DefaultTransactionAttribute definition = new DefaultTransactionAttribute();
        // REQUIRED_NEW 설정 -> 새 물리 트랜잭션 생성
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus inner = manager.getTransaction(definition);
        log.info("inner.isNewTransaction(): {}", inner.isNewTransaction()); // true

        log.info("내부 트랜잭션 롤백");
        manager.rollback(inner);

        log.info("외부 트랜잭션 커밋");
        manager.commit(outer);
    }


}
