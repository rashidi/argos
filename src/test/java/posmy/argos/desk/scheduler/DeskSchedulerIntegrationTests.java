package posmy.argos.desk.scheduler;

import posmy.argos.desk.context.DeskProperties;
import posmy.argos.desk.domain.Desk;
import posmy.argos.desk.domain.DeskRepository;
import posmy.argos.desk.history.domain.DeskOccupiedHistory;
import posmy.argos.desk.history.domain.DeskOccupiedHistoryRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static posmy.argos.desk.domain.DeskStatus.OCCUPIED;
import static posmy.argos.desk.domain.DeskStatus.VACANT;
import static posmy.argos.desk.helper.DeskTestHelper.create;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.awaitility.Awaitility.await;

/**
 * @author Rashidi Zin
 */
@SpringBootTest
@Testcontainers
@TestPropertySource(properties = { "argos.desk.max-duration=1M", "argos.desk.scheduler-delay=10000" })
class DeskSchedulerIntegrationTests {

    @Container
    private static final MongoDBContainer container = new MongoDBContainer();

    @Autowired
    private DeskRepository repository;

    @Autowired
    private DeskOccupiedHistoryRepository historyRepository;

    @Autowired
    private DeskProperties properties;

    @DynamicPropertySource 
    static void setupMongoDB(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.host", () -> container.getContainerIpAddress());
        registry.add("spring.data.mongodb.port", () -> container.getMappedPort(27017));
    }
    
    @Test
    @DisplayName("Inactive OCCUPIED Desks will be mark as VACANT by scheduler")
    void updateInactiveOccupiedDesks() {
        var desk = repository.insert(create().status(OCCUPIED));

        historyRepository.insert(
            new DeskOccupiedHistory().location(desk.location()).since(now().minus(1, MINUTES)).end(now())
        );

        await()
            .atMost(properties.getSchedulerDelay() * 6, MILLISECONDS) // Yeah I can actual figure. But I have to make sure the properties is assigned too.
            .until(() -> 
                repository.findOne(Example.of(new Desk().id(desk.id()))).get().status() == VACANT
            );
    }

}