package posmy.argos.desk.context;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.time.Duration;

/**
 * @author Rashidi Zin
 */
@ConstructorBinding
@ConfigurationProperties("argos.desk")
@AllArgsConstructor
public class DeskProperties {

    @Getter
    private final Duration maxDuration;

}
