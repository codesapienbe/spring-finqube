package com.finqube.iso20022.admin;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test for the Spring Finqube Admin Application.
 *
 * <p>This test verifies that the application context can be loaded
 * successfully and all required beans are available.</p>
 *
 * @author Spring Finqube Team
 * @version 0.1.0
 * @since 0.1.0
 */
@SpringBootTest
@ActiveProfiles("test")
class SpringFinqubeAdminApplicationTest {

    /**
     * Tests that the application context loads successfully.
     */
    @Test
    void contextLoads() {
        // This test will pass if the Spring application context loads successfully
        // It verifies that all required beans can be created and dependencies resolved
    }
}
