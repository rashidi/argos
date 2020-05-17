package posmy.argos.desk.context;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

/**
 * @author Rashidi Zin
 */
@ConstructorBinding
@ConfigurationProperties("argos.desk")
@Value
public class DeskProperties {

    Period period;

    public static class Period {

        private final Integer maxHour;

        public Period(Integer maxHour) {
            this.maxHour = maxHour;
        }

        public Integer getMaxHour() {
            return maxHour;
        }

    }

}
