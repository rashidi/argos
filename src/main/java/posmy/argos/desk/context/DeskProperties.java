package posmy.argos.desk.context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

/**
 * @author Rashidi Zin
 */
@ConstructorBinding
@ConfigurationProperties("argos.desk")
@AllArgsConstructor
@Getter
public class DeskProperties {

    Period period;

    @AllArgsConstructor
    @Getter
    public static class Period {

        Integer maxHour;

    }

}
