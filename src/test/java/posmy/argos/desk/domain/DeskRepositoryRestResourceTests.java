package posmy.argos.desk.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import posmy.argos.desk.history.domain.DeskOccupiedHistory;
import posmy.argos.desk.history.domain.DeskOccupiedHistoryRepository;
import posmy.argos.security.azure.WithAzureADUser;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static posmy.argos.desk.domain.DeskArea.DATA_AND_TECHNOLOGY;
import static posmy.argos.desk.domain.DeskStatus.OCCUPIED;
import static posmy.argos.desk.domain.DeskStatus.VACANT;

/**
 * @author Rashidi Zin
 */
@SpringBootTest
@WithAzureADUser
class DeskRepositoryRestResourceTests {

    @Autowired
    private WebApplicationContext ctx;

    private MockMvc mvc;

    @Autowired
    private DeskRepository repository;

    @Autowired
    private DeskOccupiedHistoryRepository historyRepository;

    @BeforeEach
    void setup() {
        mvc = webAppContextSetup(ctx)
                .apply(springSecurity())
                .build();
    }

    @Test
    void browse() throws Exception {
        mvc.perform(
                get("/desks")
        )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("History record will be created when a Desk is OCCUPIED")
    void occupy() throws Exception {
        var location = new DeskLocation().row("D").column(12);

        var desk = repository.save(
                new Desk().location(location).area(DATA_AND_TECHNOLOGY).status(VACANT)
        );

        var content = new ObjectMapper().writeValueAsString(desk.status(OCCUPIED));

        mvc.perform(
                put("/desks/{id}", desk.id())
                        .accept(HAL_JSON)
                        .content(content)
                        .contentType(APPLICATION_JSON)
        )
                .andExpect(status().isOk());

        var history = historyRepository.findOne(Example.of(new DeskOccupiedHistory().location(location)));

        assertThat(history)
                .isNotEmpty();
    }

    @Test
    @DisplayName("History end time will be updated to earlier if when user manually check out")
    void manualCheckout() throws Exception {
        var location = new DeskLocation().row("D").column(12);

        var desk = repository.save(
                new Desk().location(location).area(DATA_AND_TECHNOLOGY).status(OCCUPIED)
        );

        var history = historyRepository.save(
                new DeskOccupiedHistory().location(location).since(now()).end(now().plus(9, HOURS))
        );

        var content = new ObjectMapper().writeValueAsString(desk.status(VACANT));

        mvc.perform(
                put("/desks/{id}", desk.id())
                        .accept(HAL_JSON)
                        .content(content)
                        .contentType(APPLICATION_JSON)
        );

        var updatedHistory = historyRepository.findOne(Example.of(new DeskOccupiedHistory().location(location)));

        assertThat(updatedHistory)
                .get()
                .satisfies(persisted ->
                        assertThat(persisted.end()).isBefore(history.end())
                );
    }
}
