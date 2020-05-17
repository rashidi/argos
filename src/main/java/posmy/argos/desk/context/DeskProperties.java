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

    @Value
    public static class Period {

        Integer maxHour;

    }

}
