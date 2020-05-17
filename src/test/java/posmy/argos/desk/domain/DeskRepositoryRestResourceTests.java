package posmy.argos.desk.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import posmy.argos.desk.history.domain.DeskOccupiedHistory;
import posmy.argos.desk.history.domain.DeskOccupiedHistoryRepository;
import posmy.argos.desk.junit.arguments.InvalidLocationsArgumentProvider;
import posmy.argos.security.azure.WithAzureADUser;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.hateoas.MediaTypes.HAL_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;
import static posmy.argos.desk.domain.DeskArea.DATA_AND_TECHNOLOGY;
import static posmy.argos.desk.domain.DeskStatus.OCCUPIED;
import static posmy.argos.desk.domain.DeskStatus.VACANT;
import static posmy.argos.desk.helper.DeskTestHelper.create;

/**
 * @author Rashidi Zin
 */
@SpringBootTest(properties = "argos.desk.period.max-hour=2")
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
    @DisplayName("Desk occupant will be updated and history record will be created when a Desk is OCCUPIED")
    void occupy() throws Exception {
        var desk = repository.save(create().status(VACANT));

        var content = new ObjectMapper().writeValueAsString(desk.status(OCCUPIED));

        mvc.perform(
                put("/desks/{id}", desk.id())
                        .accept(HAL_JSON)
                        .content(content)
                        .contentType(APPLICATION_JSON)
        )
                .andExpect(status().isOk());

        var persisted = repository.findById(desk.id());

        assertThat(persisted).isNotEmpty().get().extracting(Desk::occupant).isEqualTo("rashidi");

        var history = historyRepository.findOne(Example.of(new DeskOccupiedHistory().location(desk.location())));

        assertThat(history)
                .isNotEmpty();
    }

    @Test
    @DisplayName("Desk occupant will be removed and history end time will be updated to earlier when user manually check out")
    void manualCheckout() throws Exception {
        var desk = repository.save(create().status(OCCUPIED));
        var location = desk.location();

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

        assertThat(repository.findById(desk.id()))
                .isNotEmpty()
                .get()
                .extracting(Desk::occupant)
                .isNull();
    }

    @Test
    @DisplayName("Upon creation, Desk status will be VACANT")
    void assertInitialStatus() throws Exception {
        var location = new DeskLocation().row("D").column(12);
        var desk = new Desk().location(location).area(DATA_AND_TECHNOLOGY);

        var content = new ObjectMapper().writeValueAsString(desk);

        mvc.perform(
                post("/desks")
                        .accept(HAL_JSON)
                        .content(content)
                        .contentType(APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is(VACANT.name())));
    }

    @Test
    @DisplayName("Only one location can exist per entry")
    void createWithExistingLocation() throws Exception {
        var location = new DeskLocation().row("D").column(12);

        repository.save(new Desk().location(location).area(DATA_AND_TECHNOLOGY));

        var content = new ObjectMapper().writeValueAsString(
                new Desk().location(location).area(DATA_AND_TECHNOLOGY)
        );

        mvc.perform(
                post("/desks")
                        .accept(HAL_JSON)
                        .content(content)
                        .contentType(APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.[0].message", is("desk.location already exist")));
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidLocationsArgumentProvider.class)
    void createWithInvalidLocation(Desk desk) throws Exception {
        var content = new ObjectMapper().writeValueAsString(desk);

        mvc.perform(
                post("/desks")
                .accept(HAL_JSON)
                .content(content)
                .contentType(APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.[0].message", is("desk.location is required")));
    }

    @Test
    @DisplayName("area cannot be null")
    void createWithMissingArea() throws Exception {
        var content = new ObjectMapper().writeValueAsString(
                new Desk()
                        .location(new DeskLocation().row("D").column(12))
        );

        mvc.perform(
                post("/desks")
                        .accept(HAL_JSON)
                        .content(content)
                        .contentType(APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.[0].message", is("desk.area is required")));
    }

    @Test
    @DisplayName("OCCUPIED Desk cannot be occupied again")
    void occupyOccupiedDesk() throws Exception {
        var persisted = repository.save(create().status(OCCUPIED));

        var content = new ObjectMapper().writeValueAsBytes(persisted);

        mvc.perform(
                put("/desks/{id}", persisted.id())
                        .accept(HAL_JSON)
                        .content(content)
                        .contentType(APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.[0].message", is("desk.location is not available")));
    }

    @Test
    @DisplayName("Active occupant is not allowed to occupy another Desk")
    void occupyDeskByActiveOccupant() throws Exception {

        repository.save(create().status(OCCUPIED).occupant("rashidi"));

        var vacantDesk = repository.save(create().location(new DeskLocation().row("UB").column(40)).status(VACANT));

        var content = new ObjectMapper().writeValueAsBytes(vacantDesk.status(OCCUPIED));

        mvc.perform(
                put("/desks/{id}", vacantDesk.id())
                        .accept(HAL_JSON)
                        .content(content)
                        .contentType(APPLICATION_JSON)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.[0].message", is("desk.occupant is active")));
    }

    @AfterEach
    void cleanup() {
        repository.deleteAll();
    }

}
