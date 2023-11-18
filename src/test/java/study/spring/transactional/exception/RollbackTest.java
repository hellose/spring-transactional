package study.spring.transactional.exception;


import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

/*
Creating new transaction with name -> ClassName.MethodName
 */
@SpringBootTest
public class RollbackTest {

    @Autowired
    RollbackService service;

    @TestConfiguration
    static class RollbackTestConfiguration {
        @Bean
        RollbackService rollbackService() {
            return new RollbackService();
        }
    }

    static class MyException extends Exception {

    }

    @Slf4j
    static class RollbackService {
        // 런타임 예외 발생: 롤백
        @Transactional
        public void runtimeException() {
            log.info("call runtimeException");
            throw new RuntimeException();
        }

        // 체크 예외 발생: 커밋
        @Transactional
        public void checkedException() throws MyException {
            log.info("call checkedException");
            throw new MyException();
        }

        // 체크 예외 rollbackFor 지정: 롤백
        @Transactional(rollbackFor = MyException.class) // MyException 포함 모든 하위 Exception 발생한 경우 rollback
        public void rollbackForCheckedException() throws MyException {
            log.info("call rollbackFor");
            throw new MyException();
        }

    }

    // Rolling back JPA transaction on EntityManager
    @Test
    void runtimeException() {
        Assertions
                .assertThatThrownBy(() -> service.runtimeException())
                .isInstanceOf(RuntimeException.class);
    }

    // Committing JPA transaction on EntityManager
    @Test
    void checkedException() {
        Assertions
                .assertThatThrownBy(() -> service.checkedException())
                .isInstanceOf(MyException.class);
    }

    // Rolling back JPA transaction on EntityManager
    @Test
    void rollbackForCheckedException() {
        Assertions
                .assertThatThrownBy(() -> service.rollbackForCheckedException())
                .isInstanceOf(MyException.class);
    }

}