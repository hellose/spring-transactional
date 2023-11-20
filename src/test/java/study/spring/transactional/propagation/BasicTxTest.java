package study.spring.transactional.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.Platform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
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

}
