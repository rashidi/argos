package posmy.argos.desk.context;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.core.env.StandardEnvironment;

import java.time.Duration;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Rashidi Zin
 */
@SpringBootTest(classes = DeskPropertiesConfiguration.class)
class DeskPropertiesConfigurationTests {

    @Autowired
    private StandardEnvironment env;

    private final static String MAX_DURATION_PROPERTY = "argos.desk.max-duration";

    @ParameterizedTest
    @MethodSource("maxDurationValues")
    void maxDuration(String value, long expectedSeconds) {
        TestPropertyValues.of(String.format("%s=%s", MAX_DURATION_PROPERTY, value)).applyTo(env);

        assertThat(env.getProperty(MAX_DURATION_PROPERTY, Duration.class))
                .isNotNull()
                .extracting(Duration::getSeconds)
                .isEqualTo(expectedSeconds);
    }

    private static Stream<Arguments> maxDurationValues() {
        return Stream.of(
                Arguments.of("30M", MINUTES.toSeconds(30)), // Assign by minutes
                Arguments.of("9H", HOURS.toSeconds(9)), // Assign by hours
                Arguments.of("7D", DAYS.toSeconds(7)) // Assign by days
        );
    }
}
