package study.spring.transactional.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;
import study.spring.transactional.Util;

@Slf4j
@SpringBootTest
public class TxJavaAccessModifierTest {

    @Autowired
    TestService service;

    @TestConfiguration
    static class TxJavaAccessModifierConfig {
        @Bean
        TestService testService() {
            return new TestService();
        }
    }

    @Slf4j
    static class TestService {
        @Transactional
        private void privateMethod() {
            Util.isTransactionActive();
        }

        @Transactional
        void method() {
            Util.isTransactionActive();
        }

        @Transactional
        public void publicMethod() {
            Util.isTransactionActive();
        }

    }

    @Test
    void privateMethod() {
        service.privateMethod();
    }

    @Test
    void method() {
        service.method();
    }

    // public 메서드만 @Transactional이 적용된다.
    @Test
    void publicMethod() {
        service.publicMethod();
    }

}