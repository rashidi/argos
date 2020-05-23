package posmy.argos.desk.scheduler;

import posmy.argos.desk.domain.DeskRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Rashidi Zin
 */
@Configuration
@EnableScheduling
public class SchedulerConfiguration {
    
    @Bean("updateDeskStatusScheduler")
    public UpdateDeskStatusScheduler updateStatus(DeskRepository repository, DeskSchedulerService service) {
        return new UpdateDeskStatusScheduler(repository, service);
    }

}