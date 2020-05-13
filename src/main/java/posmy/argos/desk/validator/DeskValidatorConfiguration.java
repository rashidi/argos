package posmy.argos.desk.validator;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import posmy.argos.desk.domain.DeskRepository;

/**
 * @author Rashidi Zin
 */
@Configuration
@AllArgsConstructor
public class DeskValidatorConfiguration {

    private final DeskRepository repository;

    @Bean("deskRepositoryRestConfiguration")
    public RepositoryRestConfigurer repositoryRestConfiguration() {

        return new RepositoryRestConfigurer() {

            @Override
            public void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener listener) {
                listener.addValidator("beforeCreate", new BeforeCreateDeskValidator(repository));
            }

        };

    }

}
