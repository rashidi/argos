package posmy.argos.desk.context;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Rashidi Zin
 */
@ConstructorBinding
@ConfigurationProperties("argos.desk")
@AllArgsConstructor
public class DeskProperties {

    @Getter
    private final Duration maxDuration;

    @Getter
    private final Long schedulerDelay;

}
