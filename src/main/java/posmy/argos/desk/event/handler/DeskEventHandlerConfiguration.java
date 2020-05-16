package posmy.argos.desk.event.handler;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import posmy.argos.desk.history.domain.DeskOccupiedHistoryRepository;

/**
 * @author Rashidi Zin
 */
@Configuration
public class DeskEventHandlerConfiguration {

    @Bean("deskBeforeCreateEventHandler")
    public DeskBeforeCreateEventHandler beforeCreate() {
        return new DeskBeforeCreateEventHandler();
    }

    @Bean("deskBeforeSaveEventHandler")
    public DeskBeforeSaveEventHandler beforeSave() {
        return new DeskBeforeSaveEventHandler();
    }

    @Bean("deskAfterSaveEventHandler")
    public DeskAfterSaveEventHandler afterSave(DeskOccupiedHistoryRepository historyRepository) {
        return new DeskAfterSaveEventHandler(historyRepository);
    }

}
