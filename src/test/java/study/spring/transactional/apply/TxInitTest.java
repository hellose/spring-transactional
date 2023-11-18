package study.spring.transactional.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import study.spring.transactional.Util;

import javax.annotation.PostConstruct;

@SpringBootTest
public class TxInitTest {

    @Autowired
    Hello hello;

    @TestConfiguration
    static class TxInitTestConfig {

        @Bean
        Hello hello() {
            return new Hello();
        }
    }

    @Slf4j
    static class Hello {
        @PostConstruct
        @Transactional
        public void initV1() {
            log.debug("initV1");
            Util.isTransactionActive();
        }

        @EventListener(ApplicationReadyEvent.class)
        @Transactional
        public void initV2() {
            log.debug("initV2");
            Util.isTransactionActive();
        }
    }

    @Test
    void startApplication1() {
        // 스프링 내부에서 모든 Bean 생성 -> 트랜잭션 AOP가 적용
    }

    @Test
    void startApplication2() {
    }
}