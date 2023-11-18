package study.spring.transactional.issue;

import lombok.RequiredArgsConstructor;
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
public class SolveProblemTest {
    @Autowired
    ExternalService externalService;
    @Autowired
    InternalService internalService;

    @TestConfiguration
    static class SolveProblemTestConfig {
        @Bean
        public ExternalService externalService(InternalService internalService) {
            return new ExternalService(internalService);
        }

        @Bean
        public InternalService internalService() {
            return new InternalService();
        }

    }

    @Slf4j
    @RequiredArgsConstructor
    static class ExternalService {

        private final InternalService internalService;

        public void external() {
            log.info("call external");
            Util.isTransactionActive();
            internalService.internal();
        }
    }

    static class InternalService {
        @Transactional
        public void internal() {
            log.info("call internal");
            Util.isTransactionActive();
        }
    }

    @Test
    void externalCall() throws Exception {
        externalService.external();
    }

    @Test
    void internalCall() {
        internalService.internal();
    }


}