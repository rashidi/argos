package posmy.argos.desk.validator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.rest.core.ValidationErrors;
import posmy.argos.desk.domain.Desk;
import posmy.argos.desk.domain.DeskLocation;
import posmy.argos.desk.domain.DeskRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static posmy.argos.desk.domain.DeskArea.DATA_AND_TECHNOLOGY;

/**
 * @author Rashidi Zin
 */
@ExtendWith(MockitoExtension.class)
class BeforeCreateDeskValidatorTests {

    private final DeskRepository repository = mock(DeskRepository.class);

    private final BeforeCreateDeskValidator validator = new BeforeCreateDeskValidator(repository);

    @Captor
    private ArgumentCaptor<Example<Desk>> findCriteriaCaptor;

    @Captor
    private ArgumentCaptor<String> errorMessageCaptor;

    @Test
    @DisplayName("Only Desk class is supported")
    void supports() {
        assertThat(validator.supports(Desk.class)).isTrue();

        assertThat(validator.supports(String.class)).isFalse();
    }

    @Test
    @DisplayName("Providing an existing location will cause an error")
    void validateWithExistingLocation() {
        var location = new DeskLocation().row("D").column(12);
        var desk = new Desk().location(location).area(DATA_AND_TECHNOLOGY);

        doReturn(true).when(repository).exists(any());

        ValidationErrors errors = mock(ValidationErrors.class);

        validator.validate(desk, errors);

        verify(repository).exists(findCriteriaCaptor.capture());

        var locationCriteria = findCriteriaCaptor.getValue().getProbe().location();

        assertThat(locationCriteria).isEqualTo(location);

        verify(errors).rejectValue(anyString(), anyString(), errorMessageCaptor.capture());

        var errorMessage = errorMessageCaptor.getValue();

        assertThat(errorMessage).isEqualTo("desk.location already exist");
    }

    @Test
    @DisplayName("No errors should be assigned for new location")
    void validateWithNewLocation() {
        var location = new DeskLocation().row("D").column(12);
        var desk = new Desk().location(location).area(DATA_AND_TECHNOLOGY);

        doReturn(false).when(repository).exists(any());

        ValidationErrors errors = mock(ValidationErrors.class);

        validator.validate(desk, errors);

        verify(repository).exists(findCriteriaCaptor.capture());

        verify(errors, never()).rejectValue(anyString(), anyString(), anyString());
    }
}
