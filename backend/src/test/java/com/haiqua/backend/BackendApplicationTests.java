package com.haiqua.backend;

import com.haiqua.backend.config.TestMailConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestMailConfig.class)
class BackendApplicationTests {

    @Test
    void contextLoads() {
    }
}