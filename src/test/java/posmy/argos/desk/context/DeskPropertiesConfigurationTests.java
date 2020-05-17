package posmy.argos.desk.context;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Rashidi Zin
 */
@SpringBootTest(properties = "argos.desk.period.max-hour=2", classes = DeskPropertiesConfiguration.class)
class DeskPropertiesConfigurationTests {

    @Autowired
    private DeskProperties properties;

    @Test
    void maxHour() {
        assertThat(properties.getPeriod().getMaxHour()).isEqualTo(2);
    }

}
