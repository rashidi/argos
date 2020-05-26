package posmy.argos.containers.mongodb;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * @author Rashidi Zin
 */
@Testcontainers
public class MongoDBTestContainerSetup {
    
    @Container
    private static final MongoDBContainer container = new MongoDBContainer();
    
    @DynamicPropertySource 
    static void setupMongoDB(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.host", () -> container.getContainerIpAddress());
        registry.add("spring.data.mongodb.port", () -> container.getMappedPort(27017));
    }

}